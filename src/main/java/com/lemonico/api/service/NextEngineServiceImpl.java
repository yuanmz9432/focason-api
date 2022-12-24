package com.lemonico.api.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.API;
import com.lemonico.api.bean.ClientInfo;
import com.lemonico.api.utils.SLHttpClient;
import com.lemonico.common.bean.*;
import com.lemonico.core.enums.ProductType;
import com.lemonico.core.enums.SwitchEnum;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Next-Engine APIサービス実現クラス
 */
@Service
public class NextEngineServiceImpl extends APIService implements APIInterface
{
    private final static Logger logger = LoggerFactory.getLogger(NextEngineServiceImpl.class);
    /**
     * ベースURI
     */
    private static final String BASE_URI = "https://api.next-engine.org";
    /**
     * 受注検索URI
     */
    private static final String SEARCH_ORDERS_URI = BASE_URI + "/api_v1_receiveorder_base/search";
    /**
     * 受注伝票更新URI
     */
    private static final String UPDATE_ORDERS_URI = BASE_URI + "/api_v1_receiveorder_base/update";
    /**
     * 受注詳細取得URI
     */
    private static final String SEARCH_ORDER_DETAIL_URI = BASE_URI + "/api_v1_receiveorder_row/search";
    /**
     * 店舗マスタ取得URI
     */
    private static final String SEARCH_SHOP_MASTER_URI = BASE_URI + "/api_v1_master_shop/search";
    /**
     * 受注詳細取得フィールド
     */
    private final static String SEARCH_RECEIVE_ORDER_DETAIL_FIELDS = "receive_order_row_goods_option" +
        ",receive_order_row_goods_id" +
        ",receive_order_row_goods_name" +
        ",receive_order_row_unit_price" +
        ",receive_order_row_quantity" +
        ",receive_order_row_sub_total_price";
    /**
     * 店舗マスタ取得フィールド
     */
    private final static String SEARCH_SHOP_MASTER_FIELDS = "shop_id" +
        ",shop_tax_name";
    /**
     * 受注検索フィールド
     */
    private final static String SEARCH_RECEIVE_ORDER_FIELDS = "receive_order_id" +
        ",receive_order_shop_cut_form_id" +
        ",receive_order_delivery_name" +
        ",receive_order_hope_delivery_time_slot_name" +
        ",receive_order_consignee_zip_code" +
        ",receive_order_consignee_address1" +
        ",receive_order_consignee_address2" +
        ",receive_order_consignee_name" +
        ",receive_order_consignee_kana" +
        ",receive_order_consignee_tel" +
        ",receive_order_hope_delivery_date" +
        ",receive_order_payment_method_name" +
        ",receive_order_shop_id" +
        ",receive_order_date" +
        ",receive_order_note" +
        ",receive_order_goods_amount" +
        ",receive_order_tax_amount" +
        ",receive_order_charge_amount" +
        ",receive_order_other_amount" +
        ",receive_order_delivery_fee_amount" +
        ",receive_order_total_amount" +
        ",receive_order_deposit_type_name" +
        ",receive_order_payment_method_id" +
        ",receive_order_purchaser_name" +
        ",receive_order_purchaser_kana" +
        ",receive_order_purchaser_zip_code" +
        ",receive_order_purchaser_address1" +
        ",receive_order_purchaser_address2" +
        ",receive_order_purchaser_tel" +
        ",receive_order_purchaser_mail_address" +
        ",receive_order_cancel_type_id";

    /**
     * 受注連携処理
     *
     * <ul>
     * <li>
     * 受注自動連携フラグが【ON】に設定している店舗を取得する
     * </li>
     * <li>
     * 店舗毎のループ処理
     * </li>
     * <ul>
     * <li>
     * 店舗設定によって、受注情報を取得する
     * </li>
     * <li>
     * 新規受注（落ち込んだ受注、キャンセルされた受注を除く）リストを洗い出す。
     * </li>
     * </ul>
     * <li>
     * 新規受注毎のループ処理
     * </li>
     * <ul>
     * <li>
     * 受注データをTc200_orderテーブルに挿入する
     * </li>
     * <li>
     * 受注詳細データをTc201_order_detailテーブルに挿入する
     * </li>
     * </ul>
     *
     * @return {@link ResponseEntity<Void>} 返却値なし
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> executeFetchOrderProcess() {
        // **********************************************************
        // ***************** 利用店舗情報取得及び初期化 ******************
        // **********************************************************
        initialize();
        List<Tc203_order_client> clients = getFetchOrderClients(API.NEXTENGINE);
        if (clients == null || clients.isEmpty()) {
            logger.info("Next-Engine 受注連携 利用店舗取得処理 成功 利用店舗：0件");
            return ResponseEntity.ok().build();
        } else {
            logger.info("Next-Engine 受注連携 利用店舗取得処理 成功 利用店舗：{}件", clients.size());
        }
        for (Tc203_order_client client : clients) {
            logger.info("============ Next-Engine 受注連携 店舗【{}】の受注連携処理 開始 ==========", client.getClient_id());
            // **********************************************************
            // ********************* ①店舗情報初期化 **********************
            // **********************************************************
            ClientInfo clientInfo = clientInitializer(client);
            final String clientId = clientInfo.getClientId();
            // **********************************************************
            // ********************* ②各APIの事前処理 *********************
            // **********************************************************
            try {
                clientInfo = (ClientInfo) preProcess(clientInfo);
            } catch (BaseException baseException) {
                logger.error("Next-Engine 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("Next-Engine 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("Next-Engine 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Next-Engine 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // ***************** ③ECサイトから受注情報取得 *****************
            // **********************************************************
            JSONArray response;
            try {
                response = fetchOrders(client);
            } catch (BaseException baseException) {
                logger.error("Next-Engine 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("Next-Engine 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("Next-Engine 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Next-Engine 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // *************** ④全件受注から新規受注を洗い出す ***************
            // **********************************************************
            List<JSONObject> newOrders;
            try {
                newOrders = filterNewOrders(response, clientInfo);
            } catch (Exception exception) {
                logger.error("Next-Engine 受注連携 店舗【{}】の新規受注を洗い出す処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Next-Engine 受注連携 店舗【{}】の新規受注を洗い出す処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // ******************** ⑤新規受注挿入処理 *********************
            // **********************************************************
            try {
                insertOrder(newOrders, clientInfo);
            } catch (Exception exception) {
                logger.error("Next-Engine 受注連携 店舗【{}】の新規受注挿入処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Next-Engine 受注連携 店舗【{}】の新規受注挿入処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }

            logger.info("============ Next-Engine 受注連携 店舗【{}】の受注連携処理 終了 ==========", clientId);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 受注情報取得する前の事前処理
     *
     * @param objects 店舗情報
     * @return 店舗情報
     */
    @Override
    public Object preProcess(Object... objects) {
        logger.info("------------------------------ 事前処理 ------------------------------");

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        ClientInfo clientInfo = (ClientInfo) Arrays.asList(objects).get(0);
        final String clientId = clientInfo.getClientId();

        logger.info("Next-Engine 受注連携 店舗【{}】の事前処理 成功", clientId);
        return clientInfo;
    }

    /**
     * 受注情報取得
     *
     * @param client 店舗受注API連携管理表
     * @return 受注情報
     */
    @Override
    public JSONArray fetchOrders(@NotNull Tc203_order_client client) {
        logger.info("---------------------------- 受注情報取得 ----------------------------");

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 毎回取得できた件数
        int eachCount = 0;
        // 全部取得できた件数
        int totalCount = 0;
        // オフセット（最初が0にする）
        int offset = 0;
        JSONArray totalOrders = new JSONArray();
        final String clientId = client.getClient_id();
        final String accessToken = client.getAccess_token();
        final String refreshToken = client.getRefresh_token();
        // **********************************************************
        // *********************** 受注取得 **************************
        // **********************************************************
        do {
            // ①リクエストするURI作成
            String searchUri = SEARCH_ORDERS_URI + "?offset=" + offset;
            // ②リクエストするヘッダー作成
            HashMap<String, String> headerMap = new HashMap<>();

            // ③リクエストするボディ作成
            HashMap<String, String> bodyMap = new HashMap<>();
            bodyMap.put("access_token", accessToken);
            bodyMap.put("refresh_token", refreshToken);
            bodyMap.put("fields", SEARCH_RECEIVE_ORDER_FIELDS);
            bodyMap.put("receive_order_order_status_id-eq", "20");
            HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);

            // ④リクエスト出す
            String responseStr = SLHttpClient.post(searchUri, headerMap, httpEntity);
            JSONObject response = JSONObject.parseObject(responseStr);
            if (StringTools.isNullOrEmpty(response)) {
                throw new BaseException(ErrorCode.E_NE001);
            }

            JSONArray orders = new JSONArray();
            // ⑤取得データをtotalOrdersに格納
            if ("success".equals(response.getString("result"))) {
                updateToken(response, client.getId(), clientId);
                if (response.getInteger("count") == 0) {
                    logger.info("店舗【{}】API【{}】の受注データが0件", clientId, client.getApi_name());
                    continue;
                }
                orders = response.getJSONArray("data");
            }
            if ("error".equals(response.getString("result"))) {
                logger.error(response.getString("message"));
                throw new BaseException(ErrorCode.E_NE001);
            }
            totalOrders.addAll(orders);

            // ⑤④取得データのサイズ
            eachCount = orders.size();
            logger.info("Next-Engine 受注連携 店舗【{}】の受注情報取得 成功 件数：{}件", clientId, eachCount);
            offset += LIMIT;
            totalCount += eachCount;
        } while (eachCount == LIMIT); // 取得件数が取得最大件数に一致していたら次を読み込む

        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報取得総件数：{}件", clientId, totalCount);
        return totalOrders;
    }

    /**
     * 受注詳細情報取得
     *
     * @param client 店舗受注API連携管理表
     * @return 受注情報
     */
    public JSONArray fetchOrderDetail(Tc203_order_client client, final String receiveOrderId) {
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~ 受注詳細情報取得 ~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        final String clientId = client.getClient_id();
        final String accessToken = client.getAccess_token();
        final String refreshToken = client.getRefresh_token();
        // **********************************************************
        // ********************** 受注詳細取得 ************************
        // **********************************************************
        // ①リクエストするヘッダー作成
        HashMap<String, String> headerMap = new HashMap<>();

        // ②リクエストするボディ作成
        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("access_token", accessToken);
        bodyMap.put("refresh_token", refreshToken);
        bodyMap.put("fields", SEARCH_RECEIVE_ORDER_DETAIL_FIELDS);
        bodyMap.put("receive_order_row_receive_order_id-eq", receiveOrderId);
        HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);

        // ③リクエスト出す
        String responseStr = SLHttpClient.post(SEARCH_ORDER_DETAIL_URI, headerMap, httpEntity);
        JSONObject response = JSONObject.parseObject(responseStr);
        if (StringTools.isNullOrEmpty(response)) {
            throw new BaseException(ErrorCode.E_NE002);
        }

        JSONArray orderDetails = new JSONArray();
        // ③取得データをtotalOrdersに格納
        if ("success".equals(response.getString("result"))) {
            if (response.getInteger("count") == 0) {
                logger.info("店舗【{}】API【{}】の受注データが0件", clientId, client.getApi_name());
            }
            orderDetails = response.getJSONArray("data");
        }

        logger.info("Next-Engine 受注連携 店舗【{}】の受注詳細情報取得 成功 詳細情報件数：{}件", clientId, orderDetails.size());
        return orderDetails;
    }

    /**
     * トークン情報を更新
     *
     * @param response 受注取得のレスポンス情報
     * @param id 店舗ID（自動採番）
     * @param clientId 店舗ID
     */
    private void updateToken(JSONObject response, Integer id, String clientId) {
        // 新発行アクセストークン
        String newAccessToken = response.getString("access_token");
        // 新発行リフレッシュトークン
        String newRefreshToken = response.getString("refresh_token");
        orderApiDao.updateToken(newAccessToken, newRefreshToken, id, clientId);
    }

    /**
     * 取得した受注情報から、キャンセル済み受注と既存受注情報を排除
     *
     * @param jsonArray 受注情報
     * @param clientInfo 店舗情報
     * @return 加工済み受注情報
     */
    @Override
    public List<JSONObject> filterNewOrders(@NotNull JSONArray jsonArray, @NotNull ClientInfo clientInfo) {
        logger.info("--------------------------- 新規受注洗い出す --------------------------");

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        List<JSONObject> originOrders = jsonArray.toJavaList(JSONObject.class);
        List<JSONObject> filteredOrders = new ArrayList<>();
        final String clientId = clientInfo.getClientId();
        // **********************************************************
        // ***************** キャンセル済み受注を外す *******************
        // **********************************************************
        for (JSONObject order : originOrders) {
            // 外部受注番号（Next-Engineの受注番号）
            String orderId = order.getString("receive_order_id");
            if (!"0".equals(order.getString("receive_order_cancel_type_id"))) {
                // キャンセル済み受注はtc_208テーブルに挿入して、スキップ
                insertTc208OrderCancel(clientId, orderId, API.NEXTENGINE);
            } else {
                // キャンセルされていない受注情報を格納
                filteredOrders.add(order);
            }
        }
        logger.info("Next-Engine 受注連携 店舗【{}】のキャンセル済み受注を外す処理 成功 残り件数：{}件", clientInfo.getClientId(),
            filteredOrders.size());
        if (filteredOrders.isEmpty())
            return filteredOrders;
        // **********************************************************
        // ******************* 落込んだ受注を外す **********************
        // **********************************************************
        List<String> idList = filteredOrders.stream()
            .map(i -> i.getString("receive_order_id") + "-" + i.getString("receive_order_shop_cut_form_id"))
            .collect(Collectors.toList());
        List<String> existedIdList = orderDao.getExistOrderId(idList);
        filteredOrders.removeIf(i -> existedIdList
            .contains(i.getString("receive_order_id") + "-" + i.getString("receive_order_shop_cut_form_id")));
        logger.info("Next-Engine 受注連携 店舗【{}】の落込んだ受注を外す処理 成功 残り件数：{}件", clientInfo.getClientId(), filteredOrders.size());
        List<String> newIds = filteredOrders.stream()
            .map(i -> i.getString("receive_order_id") + "-" + i.getString("receive_order_shop_cut_form_id"))
            .collect(Collectors.toList());
        if (newIds.isEmpty()) {
            logger.info("Next-Engine 受注連携 店舗【{}】の新規受注：0件", clientInfo.getClientId());
        } else {
            logger.info("Next-Engine 受注連携 店舗【{}】の新規受注：{}件 【{}】", clientInfo.getClientId(), filteredOrders.size(),
                newIds.toString());
        }

        return filteredOrders;
    }

    /**
     * 新規受注挿入
     *
     * @param newOrders 新規受注情報
     * @param clientInfo 店舗情報
     */
    @Override
    public void insertOrder(@NotNull List<JSONObject> newOrders, @NotNull ClientInfo clientInfo) {
        logger.info("---------------------------- 新規受注挿入 ----------------------------");

        if (newOrders == null || newOrders.isEmpty()) {
            logger.info("Next-Engine 受注連携 店舗【{}】の新規受注が0件のため、次の店舗に処理続く", clientInfo.getClientId());
            return;
        }
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        int successCount = 0; // 成功件数
        int failureCount = 0; // 失敗件数
        final String clientId = clientInfo.getClientId();
        // **********************************************************
        // ******************* 新規受注をループ処理 *********************
        // **********************************************************
        for (int index = 0; index < newOrders.size(); index++) {
            // トランザクションポイント作成
            Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

            JSONObject newOrder = newOrders.get(index);
            String errorMessage = null;
            // 受注番号(SunLogiの受注番号)取得
            int orderSubNo = index + 1;
            String identification = clientInfo.getIdentification();
            String purchaseOrderNo = getOrderNo(orderSubNo, identification);
            String orderNo =
                newOrder.getString("receive_order_id") + "-" + newOrder.getString("receive_order_shop_cut_form_id");
            try {
                // 受注情報をテーブルに登録
                insertTc200Order(newOrder, clientInfo, purchaseOrderNo);
                // 受注明細情報をテーブルに登録
                insertTc201OrderDetail(newOrder, clientInfo, purchaseOrderNo);
                successCount++;
            } catch (BaseException baseException) {
                errorMessage = baseException.getCode().getDetail();
                failureCount++;
            } catch (Exception exception) {
                errorMessage = exception.getMessage();
                exception.printStackTrace();
                failureCount++;
            } finally {
                if (!StringTools.isNullOrEmpty(errorMessage)) {
                    // 手動的にロールバックする
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                    logger.error("Next-Engine 受注連携 店舗【{}】の受注データ【{}】で挿入処理が失敗しました。異常メッセージ：{}", clientId, purchaseOrderNo,
                        errorMessage);
                    // トランザクションポイント作成
                    savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                    // 受注自動連携に失敗し、エラーメッセージがある場合、Tc207OrderErrorテーブルに記録する
                    insertTc207OrderError(clientInfo.getClientId(), clientInfo.getHistoryId(), orderNo, errorMessage,
                        API.NEXTENGINE);
                } else {
                    logger.info("Next-Engine 受注連携 店舗【{}】の受注【{}】 出荷依頼する", clientId, purchaseOrderNo);
                    // 出荷依頼
                    if (clientInfo.getShipmentStatus() == SwitchEnum.ON.getCode()) {
                        shipmentProcess(purchaseOrderNo, clientId);
                    }
                }
                // 手動的にコミットする
                TransactionAspectSupport.currentTransactionStatus().releaseSavepoint(savePoint);
            }
        }
        // **********************************************************
        // ****************** 受注履歴テーブルを更新 ********************
        // **********************************************************
        try {
            updateTc202OrderHistory(clientId, API.NEXTENGINE, clientInfo.getHistoryId(), successCount, failureCount);
        } catch (Exception exception) {
            logger.error("Next-Engine 受注連携 店舗【{}】の受注連携履歴情報の挿入に失敗したので、次の店舗に処理続く", clientId);
            exception.printStackTrace();
        }
    }

    /**
     * 受注情報をTc200Orderテーブルに登録
     *
     * @param order 受注情報
     * @param clientInfo 店舗情報
     * @param purchaseOrderNo 受注番号
     */
    @Override
    public void insertTc200Order(@NotNull JSONObject order, @NotNull ClientInfo clientInfo,
        @NotBlank final String purchaseOrderNo) {
        logger.info("*************************** 受注【{}】 ***************************", purchaseOrderNo);

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 共通カラムの値を取得
        // 店舗ID
        final String clientId = clientInfo.getClientId();
        // 倉庫コード
        final String warehouseCd = clientInfo.getWarehouseCd();
        // ディフォルト配送方法
        final String deliveryMethod = clientInfo.getDefaultDeliveryMethod();
        // 履歴ID
        final Integer historyId = clientInfo.getHistoryId();
        // 受注番号（Next-Engine採番：伝票番号-受注番号）
        final String orderId =
            order.getString("receive_order_id") + "-" + order.getString("receive_order_shop_cut_form_id");
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 初期化 成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 基本情報設定 ***********************
        // **********************************************************
        Tc200_order tc200Order = new Tc200_order();
        // 受注番号
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 外部受注番号
        tc200Order.setOuter_order_no(orderId);
        // 配送方法をディフォルト値に設定する
        tc200Order.setDelivery_method(deliveryMethod);
        // 取込日時
        tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        // 个口数
        tc200Order.setBoxes(1);
        // 作成者、作成日、更新者、更新日を設定する
        tc200Order.setIns_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setIns_usr(clientId);
        tc200Order.setUpd_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setUpd_usr(clientId);
        tc200Order.setDel_flg(SwitchEnum.OFF.getCode());
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 基本情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 配送情報設定 ***********************
        // **********************************************************
        setTc200OrderDelivery(order, clientInfo, tc200Order);
        // **********************************************************
        // *********************** 支払方法設定 ***********************
        // **********************************************************
        // 支払方法名称
        String payment = order.getString("receive_order_payment_method_name");
        tc200Order.setBikou10(payment);
        // 支付方法（SunLogi定義）
        String paymentMethod =
            getPaymentMethod(payment, API.NEXTENGINE.getName(), clientInfo.getMs007SettingPaymentMap());
        tc200Order.setPayment_method(paymentMethod);
        // **********************************************************
        // *********************** 依頼主情報 *************************
        // **********************************************************
        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
        Ms012_sponsor_master ms012Sponsor = clientInfo.getMs012sponsor();
        if (!StringTools.isNullOrEmpty(ms012Sponsor)) {
            // 明細書金額印字
            tc200Order.setDetail_price_print(String.valueOf(ms012Sponsor.getPrice_on_delivery_note()));
            // 明細同梱設定(1:同梱する 0:同梱しない)
            if (0 == ms012Sponsor.getDelivery_note_type()) {
                tc200Order.setDetail_bundled("同梱しない");
            } else {
                tc200Order.setDetail_bundled("同梱する");
            }
            // 依頼主
            tc200Order.setSponsor_id(ms012Sponsor.getSponsor_id());
            // 依頼主情報
            tc200Order.setDetail_message(ms012Sponsor.getDetail_message());
            // 注文者を依頼主にするかどうか(0:依頼マスタ 1:注文者)
            tc200Order.setOrder_flag(0);
        } else {
            // 注文者を依頼主にするかどうか(0:依頼マスタ 1:注文者)
            tc200Order.setOrder_flag(1);
        }
        // 店舗【SU006】に対して、複数依頼主を分ける処理
        // 対応店舗ID
        final String receiveOrderShopId = order.getString("receive_order_shop_id");
        if ("SU006".equals(clientId)) {
            if ("1".equals(receiveOrderShopId)) {
                tc200Order.setSponsor_id("1000011"); // moppy＝M0001000011
            } else if ("2".equals(receiveOrderShopId)) {
                tc200Order.setSponsor_id("1000021"); // moppy business=M0001000021
            }
        }
        // 外部注文ステータス 0:新規受付 1:出庫済み
        tc200Order.setOuter_order_status(0);
        // 注文日時
        final String orderDatetime = order.getString("receive_order_date");
        if (!StringTools.isNullOrEmpty(orderDatetime)) {
            Date date = DateUtils.stringToDate(orderDatetime);
            tc200Order.setOrder_datetime(date);
        }
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 依頼主情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 備考&メモ設定情報 *********************
        // **********************************************************
        // 備考
        final String memo = order.getString("receive_order_note");
        if (!StringTools.isNullOrEmpty(memo)) {
            tc200Order.setMemo(memo);
            tc200Order.setBikou_flg(SwitchEnum.ON.getCode());
        } else {
            tc200Order.setBikou_flg(SwitchEnum.OFF.getCode());
        }
        // 備考フラグ
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 備考&メモ設定情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************** 商品金額計算 ************************
        // **********************************************************
        // 商品金額(税込)
        final int productPriceExcludingTax = order.getDouble("receive_order_goods_amount").intValue();
        tc200Order.setProduct_price_excluding_tax(productPriceExcludingTax);
        // 消費税合計
        final int taxTotal = order.getDouble("receive_order_tax_amount").intValue();
        tc200Order.setTax_total(taxTotal);
        // 手数料(税込)
        final int handlingCharge = order.getDouble("receive_order_charge_amount").intValue();
        tc200Order.setHandling_charge(handlingCharge);
        // その他金額(割引)
        final int otherFee = order.getDouble("receive_order_other_amount").intValue();
        tc200Order.setOther_fee(otherFee);
        // 送料(税込)
        final int deliveryTotal = order.getDouble("receive_order_delivery_fee_amount").intValue();
        tc200Order.setDelivery_total(deliveryTotal);
        // 合計金額(税込)
        final int billingTotal = order.getDouble("receive_order_total_amount").intValue();
        tc200Order.setBilling_total(billingTotal);
        // 注文種別 0:入金待ち 1:入金済み
        final String deposit = order.getString("receive_order_deposit_type_name");
        int orderType = "入金済み".equals(deposit) ? 1 : 0;
        // 代引引換の場合、入金待ちを入金済みとする
        String paymentId = order.getString("receive_order_payment_method_id");
        if ("1".equals(paymentId) && !StringTools.isNullOrEmpty(billingTotal)) {
            tc200Order.setCash_on_delivery_fee(billingTotal);
            orderType = 1;
        } else {
            tc200Order.setCash_on_delivery_fee(0);
        }
        // 注文種別 0:入金待ち 1:入金済み
        tc200Order.setOrder_type(orderType);
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 商品金額計算 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************** 注文者情報設定 ***********************
        // **********************************************************
        // 注文者姓
        final String orderFamilyName = order.getString("receive_order_purchaser_name");
        tc200Order.setOrder_family_name(orderFamilyName);
        // 注文者姓カナ
        final String orderFamilyKana = order.getString("receive_order_purchaser_kana");
        tc200Order.setOrder_family_kana(orderFamilyKana);
        // 注文者郵便番号
        final String orderZipCode = order.getString("receive_order_purchaser_zip_code");
        tc200Order.setOrder_zip_code1("000");
        tc200Order.setOrder_zip_code2("0000");
        if (!StringTools.isNullOrEmpty(orderZipCode)) {
            String formatZip = CommonUtils.formatZip(orderZipCode);
            boolean isContained = formatZip.contains("-");
            if (isContained) {
                String[] splitString = formatZip.split("-");
                // 配送先郵便番号1
                tc200Order.setOrder_zip_code1(splitString[0].trim());
                // 配送先郵便番号2
                tc200Order.setOrder_zip_code2(splitString[1].trim());
            }
        }
        Map<String, String> splicedAddress = spliceAddress(order.getString("receive_order_purchaser_address1"));

        // 注文者住所都道府県
        final String orderPref = splicedAddress.get("prefectures");
        tc200Order.setOrder_todoufuken(orderPref);
        // 注文者住所郡市区
        final String orderAddress1 = splicedAddress.get("municipality");
        tc200Order.setOrder_address1(orderAddress1);
        // 注文者詳細住所
        final String orderAddress2 = order.getString("receive_order_purchaser_address2");
        tc200Order.setOrder_address2(orderAddress2);
        // 注文者電話番号
        List<String> list = CommonUtils.checkPhoneToList(order.getString("receive_order_purchaser_tel"));
        if (!StringTools.isNullOrEmpty(list) && list.size() > 0) {
            // 注文者電話番号1
            tc200Order.setOrder_phone_number1(list.get(0));
            // 注文者電話番号2
            tc200Order.setOrder_phone_number2(list.get(1));
            // 注文者電話番号3
            tc200Order.setOrder_phone_number3(list.get(2));
        }
        // 注文者メールアドレス
        tc200Order.setOrder_mail(order.getString("receive_order_purchaser_mail_address"));
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 注文者情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ******************** 受注管理表に書き込み ********************
        // **********************************************************
        try {
            orderDao.insertOrder(tc200Order);
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.E_CM005);
        }
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 受注管理表に書き込み【{}】 成功", clientInfo.getClientId(), purchaseOrderNo);
    }

    /**
     * 受注情報をTc201OrderDetailテーブルに登録
     *
     * @param jsonObject 受注情報
     * @param clientInfo 店舗情報
     * @param purchaseOrderNo 受注番号
     */
    @Override
    public void insertTc201OrderDetail(@NotNull JSONObject jsonObject, @NotNull ClientInfo clientInfo,
        @NotBlank final String purchaseOrderNo) {
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 店舗ID
        final String clientId = clientInfo.getClientId();
        // API番号
        final Integer apiId = clientInfo.getApiId();
        // アクセストークン
        final String accessToken = clientInfo.getTc203Order().getAccess_token();
        // リフレッシュトークン
        final String refreshToken = clientInfo.getTc203Order().getRefresh_token();
        // **********************************************************
        // ********************** 受注詳細情報取得 *********************
        // **********************************************************
        JSONArray orderDetails = fetchOrderDetail(clientInfo.getTc203Order(), jsonObject.getString("receive_order_id"));
        for (int idx = 0; idx < orderDetails.size(); idx++) {
            JSONObject detail = orderDetails.getJSONObject(idx);
            // **********************************************************
            // ********************** 店舗情報取得 ************************
            // **********************************************************
            // ①リクエストするヘッダー作成
            HashMap<String, String> headerMap = new HashMap<>();

            // ②リクエストするボディ作成
            HashMap<String, String> bodyMap = new HashMap<>();
            bodyMap.put("access_token", accessToken);
            bodyMap.put("refresh_token", refreshToken);
            bodyMap.put("fields", SEARCH_SHOP_MASTER_FIELDS);
            bodyMap.put("shop_id-eq", jsonObject.getString("receive_order_shop_id"));
            HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);

            // ③リクエスト出す
            String responseStr = SLHttpClient.post(SEARCH_SHOP_MASTER_URI, headerMap, httpEntity);
            JSONObject shop = JSONObject.parseObject(responseStr);
            String shopTaxName;
            if (!StringTools.isNullOrEmpty(shop) && shop.getInteger("count") > 0) {
                JSONArray jsonData = shop.getJSONArray("data");
                shopTaxName = jsonData.getJSONObject(0).getString("shop_tax_name");
            } else {
                throw new BaseException(ErrorCode.E_NE003);
            }
            // **********************************************************
            // ********************* 対象商品マッピング ********************
            // **********************************************************
            // 商品オプション
            final String options = detail.getString("receive_order_row_goods_option");
            // 商品コード product_model_number > product_id
            final String code = detail.getString("receive_order_row_goods_id");
            // Mc100_productテーブルの既存商品をマッピング
            Mc100_product mc100Product = fetchMc100Product(clientId, code, options);
            // **********************************************************
            // ************************ 仮商品登録 ************************
            // **********************************************************
            // 商品名称
            final String name = detail.getString("receive_order_row_goods_name");
            if ("眉毛専用ハイライトシフォン".equals(name)) {
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            // 商品コード
            final String renkeiPid = detail.getString("receive_order_row_goods_id");
            // 税抜き単価
            final int price = detail.getDouble("receive_order_row_unit_price").intValue();
            // 税区分(0：税込 1:税抜)
            final int isReducedTax = "税抜".equals(shopTaxName) ? 1 : 0;
            ProductBean productBean = new ProductBean();
            productBean.setClientId(clientId);
            productBean.setCode(code);
            productBean.setName(name);
            productBean.setApiId(apiId);
            productBean.setPrice(String.valueOf(price));
            productBean.setIsReducedTax(isReducedTax);
            productBean.setRenkeiPid(renkeiPid);
            productBean.setOptions(options);
            // 商品登録されていない場合、商品マスタに仮商品として新規登録
            if (StringTools.isNullOrEmpty(mc100Product)) {
                logger.info("Next-Engine 受注連携 店舗【{}】の受注詳細情報挿入処理 商品マッピング失敗", clientInfo.getClientId());
                // 仮商品として新規登録
                mc100Product = insertMc100Product(productBean, API.NEXTENGINE.getName());
                // 商品連携テーブルに挿入する
                insertMc106Product(clientId, apiId, renkeiPid, mc100Product, API.NEXTENGINE.getName());
            }
            // **********************************************************
            // *********************** 基本情報設定 ***********************
            // **********************************************************
            // Tc201_order_detailレコード
            Tc201_order_detail tc201OrderDetail = new Tc201_order_detail();
            final String orderDetailNo = purchaseOrderNo + "-" + String.format("%03d", idx + 1);
            // 受注明細番号（SunLogi採番）
            tc201OrderDetail.setOrder_detail_no(orderDetailNo);
            // 受注番号
            tc201OrderDetail.setPurchase_order_no(purchaseOrderNo);
            // 作成者、作成日、更新者、更新日、削除フラグを設定する
            tc201OrderDetail.setDel_flg(SwitchEnum.OFF.getCode());
            tc201OrderDetail.setIns_date(new Timestamp(System.currentTimeMillis()));
            tc201OrderDetail.setIns_usr(clientId);
            tc201OrderDetail.setUpd_date(new Timestamp(System.currentTimeMillis()));
            tc201OrderDetail.setUpd_usr(clientId);
            logger.info("Next-Engine 受注連携 店舗【{}】の受注詳細情報挿入処理 基本情報設定 成功", clientInfo.getClientId());
            // **********************************************************
            // *********************** 商品情報設定 ***********************
            // **********************************************************
            // 商品ID
            tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
            // セット商品ID
            if (mc100Product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
            }
            // 同梱物(0:非同梱物 1:同梱物)
            if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg()) && mc100Product.getBundled_flg() == 1) {
                tc201OrderDetail.setBundled_flg(mc100Product.getBundled_flg());
            } else {
                tc201OrderDetail.setBundled_flg(0);
            }
            // 仮登録 默认为0
            int kubun = ProductType.NORMAL.getValue();
            Integer productKubun = mc100Product.getKubun();
            if (!StringTools.isNullOrEmpty(productKubun) && ProductType.ASSUMED.getValue() == productKubun) {
                kubun = productKubun;
            }
            tc201OrderDetail.setIs_reduced_tax(isReducedTax);
            tc201OrderDetail.setProduct_kubun(kubun);
            // 商品ID
            tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
            // 商品名
            tc201OrderDetail.setProduct_name(name);
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // セット商品ID
            if (mc100Product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
            }
            // 同梱物(0:なし 1:同梱物)
            if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg()) && mc100Product.getBundled_flg() == 1) {
                tc201OrderDetail.setBundled_flg(mc100Product.getBundled_flg());
            } else {
                tc201OrderDetail.setBundled_flg(0);
            }
            tc201OrderDetail.setUnit_price(price);
            // 個数
            tc201OrderDetail.setNumber(detail.getInteger("receive_order_row_quantity"));
            // 商品計（単価*(掛率/10)*受注数）
            tc201OrderDetail.setProduct_total_price(detail.getDouble("receive_order_row_sub_total_price").intValue());
            logger.info("Next-Engine 受注連携 店舗【{}】の受注詳細情報挿入処理 商品情報設定 成功", clientInfo.getClientId());
            // **********************************************************
            // ******************** 受注明細レコード挿入 ********************
            // **********************************************************
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
            } catch (Exception exception) {
                throw new BaseException(ErrorCode.E_NE004);
            }
            logger.info("Next-Engine 受注連携 店舗【{}】の受注詳細情報挿入処理 受注明細管理表に書き込み【{}】 成功", clientInfo.getClientId(),
                orderDetailNo);
        }
    }

    /**
     * 配送方法を設定する
     *
     * @param order 受注情報
     * @param clientInfo クライアント共通パラメータ
     * @param tc200Order 受注管理レコード
     */
    private void setTc200OrderDelivery(@NotNull JSONObject order, @NotNull ClientInfo clientInfo,
        @NotNull Tc200_order tc200Order) {
        // **********************************************************
        // ********************* 配送方法設定 *************************
        // **********************************************************
        // 配送方法名
        String deliveryName = order.getString("receive_order_delivery_name");
        tc200Order.setBikou9(deliveryName);
        // 配送情報取得
        Ms004_delivery ms004Delivery = getDeliveryMethod(deliveryName, clientInfo.getDefaultDeliveryMethod(),
            clientInfo.getMs007SettingDeliveryMethodMap());
        if (!StringTools.isNullOrEmpty(ms004Delivery)) {
            // 配送会社指定
            tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
            // 配送方法
            tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
            // 配送方法名称を特殊指定名称に変更する
            deliveryName = ms004Delivery.getDelivery_nm();
        }
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 配送方法設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 配送時間帯設定 ***********************
        // **********************************************************
        String preferredPeriod = order.getString("receive_order_hope_delivery_time_slot_name");
        final String deliveryTimeSlot = getDeliveryTimeSlot(deliveryName, preferredPeriod, API.NEXTENGINE.getName(),
            clientInfo.getMs007SettingTimeMap());
        tc200Order.setDelivery_time_slot(deliveryTimeSlot);
        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 配送時間帯設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 配送先情報設定 ***********************
        // **********************************************************
        // 配送先郵便番号
        final String receiverZipCode = order.getString("receive_order_consignee_zip_code");
        tc200Order.setReceiver_zip_code1("000");
        tc200Order.setReceiver_zip_code2("0000");
        if (!StringTools.isNullOrEmpty(receiverZipCode)) {
            String formatZip = CommonUtils.formatZip(receiverZipCode);
            final boolean isContained = formatZip.contains("-");
            if (isContained) {
                String[] splitString = formatZip.split("-");
                // 配送先郵便番号1
                tc200Order.setReceiver_zip_code1(splitString[0].trim());
                // 配送先郵便番号2
                tc200Order.setReceiver_zip_code2(splitString[1].trim());
            }
        }
        // 配送先都道府县分割处理
        Map<String, String> splicedAddress = spliceAddress(order.getString("receive_order_consignee_address1"));
        // 配送先住所都道府県
        final String receiverPref = splicedAddress.get("prefectures");
        tc200Order.setReceiver_todoufuken(receiverPref);
        // 配送先住所郡市区
        final String receiverAddress1 = splicedAddress.get("municipality");
        tc200Order.setReceiver_address1(receiverAddress1);
        // 配送先詳細住所
        final String receiverAddress2 = order.getString("receive_order_consignee_address2");
        if (!StringTools.isNullOrEmpty(receiverAddress2)) {
            tc200Order.setReceiver_address2(receiverAddress2);
        }
        // 配送先姓
        final String receiverFamilyName = order.getString("receive_order_consignee_name");
        tc200Order.setReceiver_family_name(receiverFamilyName);
        // 配送先姓カナ
        final String receiverFamilyKana = order.getString("receive_order_consignee_kana");
        tc200Order.setReceiver_family_kana(receiverFamilyKana);
        // 配送先電話番号
        final String receiverPhoneNumber = order.getString("receive_order_consignee_tel");
        List<String> list = CommonUtils.checkPhoneToList(receiverPhoneNumber);
        if (!StringTools.isNullOrEmpty(list) && list.size() > 0) {
            // 配送先電話番号1
            tc200Order.setReceiver_phone_number1(list.get(0));
            // 配送先電話番号2
            tc200Order.setReceiver_phone_number2(list.get(1));
            // 配送先電話番号3
            tc200Order.setReceiver_phone_number3(list.get(2));
        } else {
            throw new BaseException(ErrorCode.E_NE005);
        }
        // 配達希望日
        final String deliveryDate = order.getString("receive_order_hope_delivery_date");
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));
        }

        logger.info("Next-Engine 受注連携 店舗【{}】の受注情報挿入処理 配送先情報設定 成功", clientInfo.getClientId());
    }

    /**
     * 都道府県と市区町村分離処理
     *
     * @param address 都道府県と市区町村文字列
     * @return 住所マップ
     */
    public Map<String, String> spliceAddress(String address) {
        // 住所マップ初期化
        Map<String, String> addressMap = new HashMap<>();

        // 都道府県が【神奈川県、鹿児島県、和歌山県】の場合、下記処理を行う
        if ("神奈川県".equals(address.substring(0, 4)) || "鹿児島県".equals(address.substring(0, 4))
            || "和歌山県".equals(address.substring(0, 4))) {
            // 注文者住所都道府県
            addressMap.put("prefectures", address.substring(0, 4));
            // 注文者住所市区町村
            addressMap.put("municipality", address.substring(4));
        } else {
            // 注文者住所都道府県
            addressMap.put("prefectures", address.substring(0, 3));
            // 注文者住所市区町村
            addressMap.put("municipality", address.substring(3));
        }

        return addressMap;
    }

    /**
     * 支払ステータス自動連携
     *
     * @return {@link ResponseEntity<Void>} 返却値なし
     */
    @Override
    public ResponseEntity<Void> executeFetchPaymentStatusProcess() {

        return null;
    }

    /**
     * 伝票番号自動連携
     *
     * @return {@link ResponseEntity<Void>} 返却値なし
     */
    @Override
    public ResponseEntity<Void> executeSendTrackingNoProcess() {
        // **********************************************************
        // ***************** ①利用店舗情報取得及び初期化 *****************
        // **********************************************************
        List<Tc203_order_client> clients = getSendTrackingNoClients(API.NEXTENGINE);
        if (clients == null || clients.isEmpty()) {
            logger.info("Next-Engine 伝票番号連携 利用店舗取得処理 成功 利用店舗：0件");
            return null;
        } else {
            logger.info("Next-Engine 伝票番号連携 利用店舗取得処理 成功 利用店舗：{}件", clients.size());
        }
        for (Tc203_order_client client : clients) {
            logger.info("============ Next-Engine 伝票番号連携 店舗【{}】の伝票番号連携処理 開始 ==========", client.getClient_id());
            // **********************************************************
            // ************************ ②初期化 **************************
            // **********************************************************
            final String clientId = client.getClient_id();
            logger.info("Next-Engine 伝票番号連携 店舗【{}】の初期化処理 成功", clientId);
            // **********************************************************
            // ********************** ③出庫情報取得 ***********************
            // **********************************************************
            // ※ 条件:出荷完了 伝票番号有り 在庫未連携
            List<Tw200_shipment> untrackedShipments = getUntrackedShipments(clientId, API.NEXTENGINE);
            if (untrackedShipments == null || untrackedShipments.isEmpty()) {
                logger.info("Next-Engine 伝票番号連携 店舗【{}】の出庫情報取得処理 成功 連携件数: 0件", clientId);
                continue;
            }
            logger.info("Next-Engine 伝票番号連携 店舗【{}】の出庫情報取得処理 成功 連携件数: {}件", clientId, untrackedShipments.size());
            // **********************************************************
            // ******************** ④Next-Engineへ連携 ******************
            // **********************************************************
            HashMap<String, String> bodyMap = new HashMap<>();
            bodyMap.put("access_token", client.getAccess_token());
            bodyMap.put("refresh_token", client.getRefresh_token());
            for (Tw200_shipment shipment : untrackedShipments) {
                String trackingNo = shipment.getDelivery_tracking_nm();
                final String orderNo = shipment.getOrder_no().split("-")[0];
                // 最後の更新日を検索する
                bodyMap.put("receive_order_id", orderNo);
                bodyMap.put("receive_order_last_modified_date", checkLastDate(client, orderNo));
                if (trackingNo.contains(",")) {
                    trackingNo = trackingNo.split(",")[0];
                }
                final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<root>\n" +
                    "<receiveorder_base>\n" +
                    "<receive_order_delivery_cut_form_id>" + trackingNo + "</receive_order_delivery_cut_form_id>\n" +
                    "</receiveorder_base>\n" +
                    "</root>";
                bodyMap.put("data", xml);
                HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);
                SLHttpClient.post(UPDATE_ORDERS_URI, new HashMap<>(), httpEntity);
                logger.info("Next-Engine 伝票番号連携 店舗【{}】の受注情報更新処理 成功", clientId);
                // **********************************************************
                // ******************** ⑤連携された出荷依頼更新 *****************
                // **********************************************************
                updateFinishedShipment(clientId, shipment.getWarehouse_cd(), shipment.getShipment_plan_id());
                logger.info("Next-Engine 伝票番号連携 店舗【{}】の連携された出荷依頼更新処理 成功 出荷依頼ID: {}", clientId,
                    shipment.getShipment_plan_id());
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 最後の更新日を検索する
     *
     * @param client 店舗ID
     * @param orderNo 受注番号
     * @return 最後の更新日
     */
    private String checkLastDate(Tc203_order_client client, String orderNo) {

        HashMap<String, String> headerMap = new HashMap<>();

        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("access_token", client.getAccess_token());
        bodyMap.put("refresh_token", client.getRefresh_token());
        bodyMap.put("fields", "receive_order_last_modified_date");
        bodyMap.put("receive_order_id-eq", orderNo);

        HttpEntity httpEntity = SLHttpClient.createUrlEncodedFormEntity(bodyMap);

        final String responseStr = SLHttpClient.post(SEARCH_ORDERS_URI, headerMap, httpEntity);
        final JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray orderData;
        if (!StringTools.isNullOrEmpty(jsonObject) && jsonObject.getInteger("count") > 0) {
            orderData = jsonObject.getJSONArray("data");
        } else {
            return null;
        }

        return orderData.getJSONObject(0).getString("receive_order_last_modified_date");
    }
}
