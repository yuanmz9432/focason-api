package com.lemonico.api.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.API;
import com.lemonico.api.bean.ClientInfo;
import com.lemonico.api.utils.SLHttpClient;
import com.lemonico.common.bean.*;
import com.lemonico.core.enums.ProductType;
import com.lemonico.core.enums.SwitchEnum;
import com.lemonico.core.enums.TaxType;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * カラーミーAPIサービス実現クラス
 */
@Service
public class ColorMeServiceImpl extends APIService implements APIInterface
{
    private final static Logger logger = LoggerFactory.getLogger(ColorMeServiceImpl.class);
    /**
     * 受注データのリスト取得URL
     */
    private final static String SALES_URL = "https://api.shop-pro.jp/v1/sales";
    /**
     * 配送方法のリスト取得URL
     */
    private final static String DELIVERIES_URL = "https://api.shop-pro.jp/v1/deliveries";
    /**
     * 支払方法のリスト取得URL
     */
    private final static String PAYMENTS_URL = "https://api.shop-pro.jp/v1/payments";
    /**
     * 店舗設定取得URL
     */
    private final static String SHOP_URL = "https://api.shop-pro.jp/v1/shop";

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
        List<Tc203_order_client> clients = getFetchOrderClients(API.COLORME);
        if (clients == null || clients.isEmpty()) {
            logger.info("ColorMe 受注連携 利用店舗取得処理 成功 利用店舗：0件");
            return ResponseEntity.ok().build();
        } else {
            logger.info("ColorMe 受注連携 利用店舗取得処理 成功 利用店舗：{}件", clients.size());
        }
        for (Tc203_order_client client : clients) {
            logger.info("============ ColorMe 受注連携 店舗【{}】の受注連携処理 開始 ==========", client.getClient_id());
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
                logger.error("ColorMe 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("ColorMe 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("ColorMe 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("ColorMe 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // ***************** ③ECサイトから受注情報取得 *****************
            // **********************************************************
            JSONArray response;
            try {
                response = fetchOrders(client);
            } catch (BaseException baseException) {
                logger.error("ColorMe 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("ColorMe 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("ColorMe 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("ColorMe 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // *************** ④全件受注から新規受注を洗い出す ***************
            // **********************************************************
            List<JSONObject> newOrders;
            try {
                newOrders = filterNewOrders(response, clientInfo);
            } catch (Exception exception) {
                logger.error("ColorMe 受注連携 店舗【{}】の新規受注を洗い出す処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("ColorMe 受注連携 店舗【{}】の新規受注を洗い出す処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }
            // **********************************************************
            // ******************** ⑤新規受注挿入処理 *********************
            // **********************************************************
            try {
                insertOrder(newOrders, clientInfo);
            } catch (Exception exception) {
                logger.error("ColorMe 受注連携 店舗【{}】の新規受注挿入処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("ColorMe 受注連携 店舗【{}】の新規受注挿入処理に失敗したので、次の店舗に処理続く", clientId);
                continue;
            }

            logger.info("============ ColorMe 受注連携 店舗【{}】の受注連携処理 終了 ==========", clientId);
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
        final String accessToken = clientInfo.getTc203Order().getAccess_token();
        String responseStr; // 取得した受注データを格納する変数
        JSONObject response; // 取得した受注データを格納する変数
        // リクエストするヘッダー作成
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(AUTHORIZATION, accessToken);
        // **********************************************************
        // ********************** 配送情報取得 ************************
        // **********************************************************
        // ※ ColorMeで配送情報：一つ受注データに対して、複数配送情報を定義できる
        responseStr = SLHttpClient.get(DELIVERIES_URL, headerMap);
        response = JSONObject.parseObject(responseStr);
        if (StringTools.isNullOrEmpty(response)) {
            throw new BaseException(ErrorCode.E_CM001);
        }
        clientInfo.setDeliveries(response.getJSONArray("deliveries"));
        logger.info("ColorMe 受注連携 店舗【{}】の事前処理 配送情報取得 成功", clientId);
        // **********************************************************
        // ********************** 支払方法取得 ************************
        // **********************************************************
        responseStr = SLHttpClient.get(PAYMENTS_URL, headerMap);
        response = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
        if (StringTools.isNullOrEmpty(response)) {
            throw new BaseException(ErrorCode.E_CM002);
        }
        clientInfo.setPayments(response.getJSONArray("payments"));
        logger.info("ColorMe 受注連携 店舗【{}】の事前処理 支払方法取得 成功", clientId);
        // **********************************************************
        // *********************** 店舗設定取得 ***********************
        // **********************************************************
        responseStr = SLHttpClient.get(SHOP_URL, headerMap);
        response = JSONObject.parseObject(responseStr);
        if (StringTools.isNullOrEmpty(response)) {
            throw new BaseException(ErrorCode.E_CM006);
        }
        clientInfo.setShop(response.getJSONObject("shop"));
        logger.info("ColorMe 受注連携 店舗【{}】の事前処理 店舗設定取得 成功", clientId);

        logger.info("ColorMe 受注連携 店舗【{}】の事前処理 成功", clientInfo.getClientId());
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
        int eachCount;
        // 全部取得できた件数
        int totalCount = 0;
        // オフセット（最初が0にする）
        int offset = 0;
        JSONArray totalOrders = new JSONArray();
        final String clientId = client.getClient_id();
        final String accessToken = client.getAccess_token();
        // **********************************************************
        // *********************** 受注取得 **************************
        // **********************************************************
        do {
            // ①リクエストするURL作成
            StringBuilder url = new StringBuilder();
            url.append(SALES_URL).append("?limit=").append(LIMIT).append("&offset=").append(offset);
            // ディフォルト：360分前
            int timeUnit = StringTools.isNullOrEmpty(client.getBikou1()) ? 360 : Integer.parseInt(client.getBikou1());
            // 受注取得開始時点
            final String after = LocalDateTime.now().minusMinutes(timeUnit).toString();
            // 検索範囲：受注開始時間
            url.append("&after=").append(after);
            logger.info("ColorMe 受注連携 店舗【{}】の受注情報取得条件： {}以降", clientId, after);

            // ②リクエストするヘッダー作成
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put(AUTHORIZATION, accessToken);

            // ③リクエスト出す
            String responseStr = SLHttpClient.get(url.toString(), headerMap);
            JSONObject response = JSONObject.parseObject(responseStr);
            if (StringTools.isNullOrEmpty(response)) {
                throw new BaseException(ErrorCode.E_CM003);
            }

            // ④取得データをtotalOrdersに格納
            JSONArray orders = response.getJSONArray("sales");
            totalOrders.addAll(orders);

            // ⑤取得データのサイズ
            eachCount = orders.size();
            logger.info("ColorMe 受注連携 店舗【{}】の受注情報取得 成功 件数：{}件", clientId, eachCount);
            offset += LIMIT;
            totalCount += eachCount;
        } while (eachCount == LIMIT); // 取得件数が取得最大件数に一致していたら次を読み込む

        logger.info("ColorMe 受注連携 店舗【{}】の受注情報取得総件数：{}件", clientId, totalCount);
        return totalOrders;
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
            // 外部受注番号（ColorMeの受注番号）
            String orderId = order.getString("id");
            if (order.getBoolean("canceled")) {
                // キャンセル済み受注はtc_208テーブルに挿入して、スキップ
                insertTc208OrderCancel(clientId, orderId, API.COLORME);
            } else {
                // キャンセルされていない受注情報を格納
                filteredOrders.add(order);
            }
        }
        logger.info("ColorMe 受注連携 店舗【{}】のキャンセル済み受注を外す処理 成功 残り件数：{}件", clientInfo.getClientId(), filteredOrders.size());
        if (filteredOrders.isEmpty())
            return filteredOrders;
        // **********************************************************
        // ******************* 落込んだ受注を外す **********************
        // **********************************************************
        List<String> idList = filteredOrders.stream().map(i -> i.getString("id")).collect(Collectors.toList());
        List<String> existedIdList = orderDao.getExistOrderId(idList);
        filteredOrders.removeIf(i -> existedIdList.contains(i.getString("id")));
        logger.info("ColorMe 受注連携 店舗【{}】の落込んだ受注を外す処理 成功 残り件数：{}件", clientInfo.getClientId(), filteredOrders.size());
        List<String> newIds = filteredOrders.stream().map(item -> item.getString("id")).collect(Collectors.toList());
        if (newIds.isEmpty()) {
            logger.info("ColorMe 受注連携 店舗【{}】の新規受注：0件", clientInfo.getClientId());
        } else {
            logger.info("ColorMe 受注連携 店舗【{}】の新規受注：{}件 【{}】", clientInfo.getClientId(), filteredOrders.size(),
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
            logger.info("ColorMe 受注連携 店舗【{}】の新規受注が0件のため、次の店舗に処理続く", clientInfo.getClientId());
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
                failureCount++;
            } finally {
                if (!StringTools.isNullOrEmpty(errorMessage)) {
                    // 手動的にロールバックする
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                    logger.error("ColorMe 受注連携 店舗【{}】の受注データ【{}】で挿入処理が失敗しました。異常メッセージ：{}", clientId, purchaseOrderNo,
                        errorMessage);
                    // 受注自動連携に失敗し、エラーメッセージがある場合、Tc207OrderErrorテーブルに記録する
                    insertTc207OrderError(clientInfo.getClientId(), clientInfo.getHistoryId(), newOrder.getString("id"),
                        errorMessage, API.COLORME);
                } else {
                    // 手動的にコミットする
                    TransactionAspectSupport.currentTransactionStatus().releaseSavepoint(savePoint);
                    logger.info("ColorMe 受注連携 店舗【{}】の受注【{}】 出荷依頼する", clientId, purchaseOrderNo);
                    // TODO 後日で出荷依頼処理を追加する
                }
            }
        }
        // **********************************************************
        // ****************** 受注履歴テーブルに記録 ********************
        // **********************************************************
        if (successCount > 0) {
            try {
                insertTc202OrderHistory(clientId, API.COLORME, clientInfo.getHistoryId(), successCount, failureCount);
            } catch (Exception exception) {
                logger.error("ColorMe 受注連携 店舗【{}】の受注連携履歴情報の挿入に失敗したので、次の店舗に処理続く", clientId);
                exception.printStackTrace();
            }
        }
    }

    /**
     * 受注情報をTc200Orderテーブルに登録
     *
     * @param jsonObject 受注情報
     * @param clientInfo 店舗情報
     * @param purchaseOrderNo 受注番号
     */
    @Override
    public void insertTc200Order(@NotNull JSONObject jsonObject, @NotNull ClientInfo clientInfo,
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
        // 受注番号（ColorMe採番）
        final Integer orderId = jsonObject.getInteger("id");
        // 配送情報取得
        final JSONArray saleDeliveries = jsonObject.getJSONArray("sale_deliveries");
        if (StringTools.isNullOrEmpty(saleDeliveries)) {
            throw new BaseException(ErrorCode.E_CM004);
        }
        JSONObject saleDelivery = saleDeliveries.getJSONObject(0);
        // 配送情報取得
        Tc200_order tc200Order = new Tc200_order();
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 初期化 成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 基本情報設定 ***********************
        // **********************************************************
        // 受注番号
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 外部受注番号
        tc200Order.setOuter_order_no(String.valueOf(orderId));
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
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 基本情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 配送情報設定 ***********************
        // **********************************************************
        setTc200OrderDelivery(saleDelivery, clientInfo, tc200Order);
        // **********************************************************
        // *********************** 支払方法設定 ***********************
        // **********************************************************
        final String paymentId = setTc200OrderPayment(jsonObject.getInteger("payment_id"), clientInfo, tc200Order);
        tc200Order.setPayment_method(paymentId);
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
        // 外部注文ステータス 0:新規受付 1:出庫済み
        tc200Order.setOuter_order_status(0);
        // 注文日時： 取得した日付の単位は秒なので、timestampを生成する場合、1000を乗算にする
        final Integer orderDatetime = jsonObject.getInteger("make_date");
        if (orderDatetime > 0) {
            Timestamp timestamp = new Timestamp(Long.valueOf(orderDatetime) * 1000);
            tc200Order.setOrder_datetime(timestamp);
        }
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 依頼主情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 備考&メモ設定情報 *********************
        // **********************************************************
        // 備考
        final String memoByColorMe = saleDelivery.getString("memo");
        // フリー項目1の入力内容
        final String answerFreeForm1 = saleDelivery.getString("answer_free_form1");
        // フリー項目2の入力内容
        final String answerFreeForm2 = saleDelivery.getString("answer_free_form2");
        // フリー項目3の入力内容
        final String answerFreeForm3 = saleDelivery.getString("answer_free_form3");
        final String memo = (memoByColorMe == null ? "" : memoByColorMe) +
            (answerFreeForm1 == null ? "" : answerFreeForm1) +
            (answerFreeForm2 == null ? "" : answerFreeForm2) +
            (answerFreeForm3 == null ? "" : answerFreeForm3);
        tc200Order.setMemo(memo);
        // 備考フラグ
        tc200Order.setBikou_flg(StringTools.isNullOrEmpty(memo) ? 0 : 1);
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 備考&メモ設定情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************** 商品金額計算 ************************
        // **********************************************************
        // 商品金額(税込)
        final Integer productPriceExcludingTax = jsonObject.getInteger("product_total_price");
        tc200Order.setProduct_price_excluding_tax(productPriceExcludingTax);
        // 消費税合計
        final Integer taxTotal = jsonObject.getInteger("tax");
        tc200Order.setTax_total(taxTotal);
        // 手数料(税込)
        final Integer handlingCharge = jsonObject.getInteger("fee");
        tc200Order.setHandling_charge(handlingCharge);
        // その他金額(割引)point_discount + gmo_point_discount + other_discount
        final Integer pointDiscount = jsonObject.getInteger("point_discount");
        final Integer gmoPointDiscount = jsonObject.getInteger("gmo_point_discount");
        final Integer otherDiscount = jsonObject.getInteger("other_discount");
        final Integer otherFee = pointDiscount + gmoPointDiscount + otherDiscount;
        tc200Order.setOther_fee(otherFee);
        // 送料(税込)
        final Integer deliveryTotal = jsonObject.getInteger("delivery_total_charge");
        tc200Order.setDelivery_total(deliveryTotal);
        // 合計金額(税込)
        final Integer billingTotal = jsonObject.getInteger("total_price");
        tc200Order.setBilling_total(billingTotal);
        // 注文種別 0:入金待ち 1:入金済み
        int orderType = jsonObject.getBoolean("paid") ? 1 : 0;
        // 代引引換の場合、入金待ちを入金済みとする
        if ("2".equals(paymentId)) {
            tc200Order.setCash_on_delivery_fee(billingTotal);
            orderType = 1;
        }
        // 注文種別 0:入金待ち 1:入金済み
        tc200Order.setOrder_type(orderType);
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 商品金額計算 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************** 注文者情報設定 ***********************
        // **********************************************************
        // 受注顧客
        JSONObject customer = jsonObject.getJSONObject("customer");
        // 注文者姓
        final String orderFamilyName = customer.getString("name");
        tc200Order.setOrder_family_name(orderFamilyName);
        // 注文者姓カナ
        final String orderFamilyKana = customer.getString("furigana");
        tc200Order.setOrder_family_kana(orderFamilyKana);
        // 注文者郵便番号
        final String orderZipCode = customer.getString("postal");
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
        // 注文者住所都道府県
        final String orderPref = customer.getString("pref_name");
        tc200Order.setOrder_todoufuken(orderPref);
        // 注文者住所郡市区
        final String orderAddress1 = customer.getString("address1");
        tc200Order.setOrder_address1(orderAddress1);
        // 注文者詳細住所
        final String orderAddress2 = customer.getString("address2");
        tc200Order.setOrder_address2(orderAddress2);
        // 注文者部署
        final String orderDivision = customer.getString("busho");
        tc200Order.setOrder_division(orderDivision);
        // 注文者電話番号
        List<String> list = CommonUtils.checkPhoneToList(customer.getString("tel"));
        if (!StringTools.isNullOrEmpty(list)) {
            // 注文者電話番号1
            tc200Order.setOrder_phone_number1(list.get(0));
            // 注文者電話番号2
            tc200Order.setOrder_phone_number2(list.get(1));
            // 注文者電話番号3
            tc200Order.setOrder_phone_number3(list.get(2));
        }
        // 注文者メールアドレス
        tc200Order.setOrder_mail(customer.getString("mail"));
        // 注文者性別 確認必要：1:男性 0:女性
        final String sex = customer.getString("sex");
        tc200Order.setOrder_gender("male".equals(sex) ? 1 : 0);
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 注文者情報設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ******************** 受注管理表に書き込み ********************
        // **********************************************************
        try {
            orderDao.insertOrder(tc200Order);
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.E_CM005);
        }
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 受注管理表に書き込み【{}】 成功", clientInfo.getClientId(), purchaseOrderNo);
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
        // 配送情報取得
        JSONArray saleDeliveries = jsonObject.getJSONArray("sale_deliveries");
        if (StringTools.isNullOrEmpty(saleDeliveries)) {
            throw new BaseException(ErrorCode.E_CM004);
        }
        JSONObject saleDelivery = saleDeliveries.getJSONObject(0);
        JSONArray details = jsonObject.getJSONArray("details");
        for (int idx = 0; idx < details.size(); idx++) {
            JSONObject detail = details.getJSONObject(idx);
            // **********************************************************
            // ********************* 対象商品マッピング ********************
            // **********************************************************
            // ColorMe側の商品オプション１
            final String option1Value = detail.getString("option1_value");
            // ColorMe側の商品オプション２
            final String option2Value = detail.getString("option2_value");
            // 商品IDで商品mc110_product_optionsに対象商品があるがどうかをチェックする
            StringBuilder options = new StringBuilder();
            if (!StringTools.isNullOrEmpty(option1Value)) {
                options.append(option1Value);
                if (!StringTools.isNullOrEmpty(option2Value)) {
                    options.append(",");
                    options.append(option2Value);
                }
            }
            // 商品コード product_model_number > product_id
            String code;
            if (!StringTools.isNullOrEmpty(detail.getString("product_model_number"))) {
                code = detail.getString("product_model_number");
            } else {
                code = detail.getString("product_id");
            }
            // Mc100_productテーブルの既存商品をマッピング
            Mc100_product mc100Product = fetchMc100Product(clientId, code, options.toString());
            // **********************************************************
            // ************************ 仮商品登録 ************************
            // **********************************************************
            // 商品名称
            final String name = detail.getString("product_name");
            // 商品コード
            final String renkeiPid = detail.getString("product_id");
            // 税抜き単価
            final Integer price = detail.getInteger("price");
            // 税込み単価
            final Integer priceWithTax = detail.getInteger("price_with_tax");
            final int isReducedTax = 0;
            ProductBean productBean = new ProductBean();
            productBean.setClientId(clientId);
            productBean.setCode(code);
            productBean.setName(name);
            productBean.setApiId(apiId);
            productBean.setPrice(String.valueOf(priceWithTax));
            productBean.setIsReducedTax(isReducedTax);
            productBean.setRenkeiPid(renkeiPid);
            productBean.setOptions(options.toString());
            // 商品登録されていない場合、商品マスタに仮商品として新規登録
            if (StringTools.isNullOrEmpty(mc100Product)) {
                logger.info("ColorMe 受注連携 店舗【{}】の受注詳細情報挿入処理 商品マッピング失敗", clientInfo.getClientId());
                // 仮商品として新規登録
                mc100Product = insertMc100Product(productBean, API.COLORME.getName());
                // 商品連携テーブルに挿入する
                insertMc106Product(clientId, apiId, renkeiPid, mc100Product, API.COLORME.getName());
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
            logger.info("ColorMe 受注連携 店舗【{}】の受注詳細情報挿入処理 基本情報設定 成功", clientInfo.getClientId());
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
            // 店舗消費税設定によって、内税⇒税込み、外税⇒税抜き
            tc201OrderDetail.setTax_flag(TaxType.EXCLUDED.getValue());
            if (TaxType.INCLUDED.getLabel().equals(clientInfo.getShop().getString("tax_type"))) {
                tc201OrderDetail.setTax_flag(TaxType.INCLUDED.getValue());
            }
            // 個数
            tc201OrderDetail.setNumber(detail.getInteger("product_num"));
            // 商品計
            tc201OrderDetail.setProduct_total_price(detail.getInteger("subtotal_price"));
            // ラッピングの表示名
            final String wrappingName1 = saleDelivery.getString("wrapping_name");
            tc201OrderDetail.setWrapping_name1(wrappingName1);
            // ラッピングの料金
            final Integer wrappingPrice1 = saleDelivery.getInteger("wrapping_charge");
            tc201OrderDetail.setWrapping_price1(wrappingPrice1);
            logger.info("ColorMe 受注連携 店舗【{}】の受注詳細情報挿入処理 商品情報設定 成功", clientInfo.getClientId());
            // **********************************************************
            // ******************** 受注明細レコード挿入 ********************
            // **********************************************************
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
            } catch (Exception exception) {
                throw new BaseException(ErrorCode.E_CM007);
            }
            logger.info("ColorMe 受注連携 店舗【{}】の受注詳細情報挿入処理 受注明細管理表に書き込み【{}】 成功", clientInfo.getClientId(), orderDetailNo);
        }
    }

    /**
     * 配送方法を設定する
     *
     * @param saleDelivery 配送情報
     * @param clientInfo クライアント共通パラメータ
     * @param tc200Order 受注管理レコード
     */
    private void setTc200OrderDelivery(@NotNull JSONObject saleDelivery, @NotNull ClientInfo clientInfo,
        @NotNull Tc200_order tc200Order) {
        // **********************************************************
        // ********************* 配送方法設定 *************************
        // **********************************************************
        // お届け先ID
        String saleDeliveriesId = saleDelivery.getString("id");
        tc200Order.setRelated_order_no(saleDeliveriesId);
        // ColorMeの使用された配送方法ID
        final Integer deliveryId = saleDelivery.getInteger("delivery_id");
        // ColorMeの配達希望時間帯
        final String preferredPeriod = saleDelivery.getString("preferred_period");
        // 配送方法名
        String deliveryName = "";
        // APIの結果から配送名取得
        for (int i = 0; i < clientInfo.getDeliveries().size(); i++) {
            JSONObject delivery = clientInfo.getDeliveries().getJSONObject(i);
            if (deliveryId.equals(delivery.getInteger("id"))) {
                deliveryName = delivery.getString("name");
                // 元の配送方法を備考9に保存する
                tc200Order.setBikou9(deliveryName);
            }
        }
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
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 配送方法設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 配送時間帯設定 ***********************
        // **********************************************************
        final String deliveryTimeSlot = getDeliveryTimeSlot(deliveryName, preferredPeriod, API.COLORME.getName(),
            clientInfo.getMs007SettingTimeMap());
        tc200Order.setDelivery_time_slot(deliveryTimeSlot);
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 配送時間帯設定 成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 配送先情報設定 ***********************
        // **********************************************************
        // 配送先郵便番号
        final String receiverZipCode = saleDelivery.getString("postal");
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
        // 配送先住所都道府県
        final String receiverPref = saleDelivery.getString("pref_name");
        tc200Order.setReceiver_todoufuken(receiverPref);
        // 配送先住所郡市区
        final String receiverAddress1 = saleDelivery.getString("address1");
        tc200Order.setReceiver_address1(receiverAddress1);
        // 配送先詳細住所
        final String receiverAddress2 = saleDelivery.getString("address2");
        tc200Order.setReceiver_address2(receiverAddress2);
        // 配送先姓
        final String receiverFamilyName = saleDelivery.getString("name");
        tc200Order.setReceiver_family_name(receiverFamilyName);
        // 配送先姓カナ
        final String receiverFamilyKana = saleDelivery.getString("furigana");
        tc200Order.setReceiver_family_kana(receiverFamilyKana);
        // 配送先電話番号
        final String receiverPhoneNumber = saleDelivery.getString("tel");
        List<String> list = CommonUtils.checkPhoneToList(receiverPhoneNumber);
        if (!StringTools.isNullOrEmpty(list)) {
            // 注文者電話番号1
            tc200Order.setReceiver_phone_number1(list.get(0));
            // 注文者電話番号2
            tc200Order.setReceiver_phone_number2(list.get(1));
            // 注文者電話番号3
            tc200Order.setReceiver_phone_number3(list.get(2));
        }
        // 配達希望日
        final String deliveryDate = saleDelivery.getString("preferred_date");
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));
        }
        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 配送先情報設定 成功", clientInfo.getClientId());
    }

    /**
     * 支払方法を設定する
     *
     * @param paymentId 支払方法ID
     * @param clientInfo 店舗情報
     * @param tc200Order 受注管理レコード
     */
    private String setTc200OrderPayment(Integer paymentId, ClientInfo clientInfo, Tc200_order tc200Order) {
        // 決済方法名
        String paymentName = null;
        // 支払方法
        String paymentMethod;
        // APIの結果から決済名取得
        for (int i = 0; i < clientInfo.getPayments().size(); i++) {
            JSONObject payment = clientInfo.getPayments().getJSONObject(i);
            if (paymentId.equals(payment.getInteger("id"))) {
                paymentName = payment.getString("name");
                // 元の支払方法を備考10に保存する
                tc200Order.setBikou10(paymentName);
                break;
            }
        }
        paymentMethod = getPaymentMethod(paymentName, API.COLORME.getName(), clientInfo.getMs007SettingPaymentMap());

        logger.info("ColorMe 受注連携 店舗【{}】の受注情報挿入処理 支払方法設定 成功", clientInfo.getClientId());
        return paymentMethod;
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
     * 送り状番号自動連携
     *
     * @return {@link ResponseEntity<Void>} 返却値なし
     */
    @Override
    public ResponseEntity<Void> executeSendTrackingNoProcess() {

        return null;
    }
}
