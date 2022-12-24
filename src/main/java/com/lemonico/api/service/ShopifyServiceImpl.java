package com.lemonico.api.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.API;
import com.lemonico.api.bean.ClientInfo;
import com.lemonico.api.utils.SLHttpClient;
import com.lemonico.common.bean.Tc203_order_client;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.StringTools;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Shopify APIサービス実現クラス
 */
@Service
public class ShopifyServiceImpl extends APIService implements APIInterface
{
    private final static Logger logger = LoggerFactory.getLogger(ShopifyServiceImpl.class);
    /**
     * 受注データのリスト取得URL
     */
    private final static String SALES_URL = "https://api.shop-pro.jp/v1/sales";
    /**
     * Shopify API利用バージョン
     */
    private final static String VERSION = "2022-01";
    /**
     * 店舗設定取得URL
     */
    private final static String AUTHORIZATION = "X-Shopify-Access-Token";

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
        List<Tc203_order_client> clients = getFetchOrderClients(API.SHOPIFY);
        if (clients == null || clients.isEmpty()) {
            logger.info("Shopify 受注連携 利用店舗：0件");
            return ResponseEntity.ok().build();
        } else {
            logger.info("Shopify 受注連携 利用店舗：{}件", clients.size());
        }
        for (Tc203_order_client client : clients) {
            logger.info("Shopify 受注連携 店舗【{}】の受注連携処理 開始", client.getClient_id());
            // **********************************************************
            // ********************* ①店舗情報初期化 **********************
            // **********************************************************
            ClientInfo clientInfo = clientInitializer(client);
            final String clientId = clientInfo.getClientId();
            // **********************************************************
            // ********************* ②各APIの事前処理 *********************
            // **********************************************************
            try {
                preProcess(clientInfo);
            } catch (BaseException baseException) {
                logger.error("Shopify 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("Shopify 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("Shopify 受注連携 店舗【{}】の事前処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Shopify 受注連携 店舗【{}】の事前処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            }
            // **********************************************************
            // ***************** ③ECサイトから受注情報取得 *****************
            // **********************************************************
            JSONArray response;
            try {
                response = fetchOrders(client);
            } catch (BaseException baseException) {
                logger.error("Shopify 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：{}", clientId,
                    baseException.getCode().getDetail());
                logger.error("Shopify 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            } catch (Exception exception) {
                logger.error("Shopify 受注連携 店舗【{}】の受注情報の取得処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Shopify 受注連携 店舗【{}】の受注情報の取得処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            }
            // **********************************************************
            // *************** ④全件受注から新規受注を洗い出す ***************
            // **********************************************************
            List<JSONObject> newOrders;
            try {
                newOrders = filterNewOrders(response, clientInfo);
            } catch (Exception exception) {
                logger.error("Shopify 受注連携 店舗【{}】の新規受注を洗い出す処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Shopify 受注連携 店舗【{}】の新規受注を洗い出す処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            }
            // **********************************************************
            // ******************** ⑤新規受注挿入処理 *********************
            // **********************************************************
            try {
                insertOrder(newOrders, clientInfo);
            } catch (Exception exception) {
                logger.error("Shopify 受注連携 店舗【{}】の新規受注挿入処理が失敗しました。異常メッセージ：", clientId);
                exception.printStackTrace();
                logger.error("Shopify 受注連携 店舗【{}】の新規受注挿入処理に失敗したので、次の店舗に処理続く。", clientId);
                continue;
            }

            logger.info("Shopify 受注連携 店舗【{}】の受注連携処理 終了", client.getClient_id());
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
        ClientInfo clientInfo = (ClientInfo) Arrays.asList(objects).get(0);
        logger.info("Shopify 受注連携 店舗【{}】の事前処理 開始", clientInfo.getClientId());
        logger.info("Shopify 受注連携 店舗【{}】の事前処理 終了", clientInfo.getClientId());
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
        logger.info("Shopify 受注連携 店舗【{}】の受注情報取得処理 開始", client.getClient_id());

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
            if (!StringTools.isNullOrEmpty(client.getBikou1())) {
                int timeUnit = Integer.parseInt(client.getBikou1());
                // 検索範囲：受注時間を設定
                Calendar startTime = Calendar.getInstance();
                startTime.add(Calendar.MINUTE, timeUnit * -1);
                Date time = startTime.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                // 検索範囲：受注開始時間
                String start = simpleDateFormat.format(time);
                url.append("&after=").append(start);
            }

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
            logger.info("Shopify 受注連携 店舗【{}】の受注情報取得成功。件数：{}件", clientId, eachCount);
            offset += LIMIT;
            totalCount += eachCount;
        } while (eachCount == LIMIT); // 取得件数が取得最大件数に一致していたら次を読み込む

        logger.info("Shopify 受注連携 店舗【{}】の受注情報取得総件数：{}件", clientId, totalCount);
        logger.info("Shopify 受注連携 店舗【{}】の受注情報取得処理 終了", client.getClient_id());
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
        logger.info("Shopify 受注連携 店舗【{}】の新規受注を洗い出す処理 開始", clientInfo.getClientId());

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
        logger.info("Shopify 受注連携 店舗【{}】のキャンセル済み受注を外す処理成功。残り件数：{}件", clientInfo.getClientId(), filteredOrders.size());
        // **********************************************************
        // ******************* 落込んだ受注を外す **********************
        // **********************************************************
        List<String> idList = filteredOrders.stream().map(i -> i.getString("id")).collect(Collectors.toList());
        List<String> existedIdList = orderDao.getExistOrderId(idList);
        filteredOrders.removeIf(i -> existedIdList.contains(i.getString("id")));
        logger.info("Shopify 受注連携 店舗【{}】の落込んだ受注を外す処理成功。残り件数：{}件", clientInfo.getClientId(), filteredOrders.size());

        logger.info("Shopify 受注連携 店舗【{}】の新規受注を洗い出す処理 終了", clientInfo.getClientId());
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
        logger.info("Shopify 受注連携 店舗【{}】の新規受注挿入処理 開始", clientInfo.getClientId());

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
            // Tc200Order,Tc201OrderDetailへの挿入失敗時にロールバックする時点
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
                    logger.error("Shopify 受注連携 店舗【{}】の受注データ【{}】で挿入処理が失敗しました。異常メッセージ：{}", clientId, purchaseOrderNo,
                        errorMessage);
                    // 受注自動連携に失敗し、エラーメッセージがある場合、Tc207OrderErrorテーブルに記録する
                    insertTc207OrderError(clientInfo.getClientId(), clientInfo.getHistoryId(), newOrder.getString("id"),
                        errorMessage, API.COLORME);
                } else {
                    logger.info("Shopify 受注連携 店舗【{}】の出荷依頼処理を行う", clientId);
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
                logger.error("Shopify 受注連携 店舗【{}】の受注連携履歴情報の挿入に失敗したので、次の店舗に処理続く。", clientId);
                exception.printStackTrace();
            }
        }

        logger.info("Shopify 受注連携 店舗【{}】の新規受注挿入処理 終了", clientInfo.getClientId());
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
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 開始", clientInfo.getClientId());

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************

        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 初期化成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 基本情報設定 ***********************
        // **********************************************************

        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 基本情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 配送情報設定 ***********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 配送情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 支払方法設定 ***********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 支払方法設定成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 依頼主情報 *************************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 依頼主情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // ********************* 備考&メモ設定情報 *********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 備考&メモ設定情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // ********************** 商品金額計算 ************************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 注文者情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // ******************** 受注管理表に書き込み ********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 受注管理表に書き込み成功", clientInfo.getClientId());

        logger.info("Shopify 受注連携 店舗【{}】の受注情報挿入処理 終了", clientInfo.getClientId());
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
        logger.info("Shopify 受注連携 店舗【{}】の受注詳細情報挿入処理 開始", clientInfo.getClientId());

        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // **********************************************************
        // *********************** 基本情報設定 ***********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注詳細情報挿入処理 基本情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // *********************** 商品情報設定 ***********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注詳細情報挿入処理 商品情報設定成功", clientInfo.getClientId());
        // **********************************************************
        // ******************** 受注明細レコード挿入 ********************
        // **********************************************************
        logger.info("Shopify 受注連携 店舗【{}】の受注詳細情報挿入処理 成功", clientInfo.getClientId());
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
     * 伝票番号連携
     *
     * @return {@link ResponseEntity<Void>} 返却値なし
     */
    @Override
    public ResponseEntity<Void> executeSendTrackingNoProcess() {
        // **********************************************************
        // ***************** ①利用店舗情報取得及び初期化 *****************
        // **********************************************************
        List<Tc203_order_client> clients = getSendTrackingNoClients(API.SHOPIFY);
        if (clients == null || clients.isEmpty()) {
            logger.info("Shopify 伝票番号連携 利用店舗取得処理 成功 利用店舗：0件");
            return null;
        } else {
            logger.info("Shopify 伝票番号連携 利用店舗取得処理 成功 利用店舗：{}件", clients.size());
        }
        for (Tc203_order_client client : clients) {
            logger.info("============ Shopify 伝票番号連携 店舗【{}】の伝票番号連携処理 開始 ==========", client.getClient_id());
            // **********************************************************
            // ************************ ②初期化 **************************
            // **********************************************************
            final String clientId = client.getClient_id();
            final String endpoint = client.getClient_url();
            final String accessToken = client.getPassword();
            logger.info("Shopify 伝票番号連携 店舗【{}】の初期化処理 成功", clientId);
            // **********************************************************
            // ********************** ③出庫情報取得 ***********************
            // **********************************************************
            // ※ 条件:出荷完了 伝票番号有り 在庫未連携
            List<Tw200_shipment> untrackedShipments = getUntrackedShipments(clientId, API.SHOPIFY);
            if (untrackedShipments == null || untrackedShipments.isEmpty()) {
                logger.info("Shopify 伝票番号連携 店舗【{}】の出庫情報取得処理 成功 連携件数: 0件", clientId);
                continue;
            }
            logger.info("Shopify 伝票番号連携 店舗【{}】の出庫情報取得処理 成功 連携件数: {}件", clientId, untrackedShipments.size());
            // **********************************************************
            // ******************* ④ローテーション情報取得 ******************
            // **********************************************************
            JSONArray locations = getLocations(endpoint, accessToken);
            if (locations == null || locations.isEmpty()) {
                break;
            }
            logger.info("Shopify 伝票番号連携 店舗【{}】のローテーション情報取得処理 成功 ローテーション件数: {}件", clientId, locations.size());
            // **********************************************************
            // ********************** ⑤Shopifyへ連携 *********************
            // **********************************************************
            for (Tw200_shipment shipment : untrackedShipments) {
                // 受注番号（Shopify側採番）
                String orderNo = shipment.getOrder_no();
                // 配送方法
                final String deliveryName = shipment.getDelivery_nm();
                if (orderNo != null && orderNo.contains("-")) {
                    orderNo = orderNo.split("-")[1];
                }
                // 配送先URL
                String deliveryUri = null;
                switch (deliveryName) {
                    case "ヤマト運輸":
                        deliveryUri = "https://jizen.kuronekoyamato.co.jp/jizen/servlet/crjz.b.NQ0010?id=";
                        break;
                    case "佐川急便":
                        deliveryUri = "https://k2k.sagawa-exp.co.jp/p/web/okurijosearch.do?okurijoNo=";
                        break;
                    case "日本郵便":
                        deliveryUri =
                            "https://trackings.post.japanpost.jp/services/srv/search/direct?org.apache.struts.taglib.html.TOKEN=&searchKind=S002&locale=ja&SVID=&reqCodeNo1=";
                        break;
                    case "福山通運":
                        deliveryUri = "https://corp.fukutsu.co.jp/situation/tracking_no_hunt";
                        break;
                    case "西濃運輸":
                        deliveryUri = "https://track.seino.co.jp/cgi-bin/gnpquery.pgm?GNPNO1=";
                        break;
                    default:
                        break;
                }
                // エンドポイント
                StringBuilder createFulfillmentsUri = new StringBuilder();
                createFulfillmentsUri.append("https://").append(endpoint).append("/admin/api/").append(VERSION)
                    .append("/orders/").append(orderNo).append("/fulfillments.json");

                for (int idx = 0; idx < locations.size(); idx++) {
                    String locationId = locations.getJSONObject(idx).getString("id");
                    // **********************************************************
                    // ****************** フルフィルメント情報作成 ******************
                    // **********************************************************
                    JSONObject fulfillment = createFulfillment(shipment, locationId, deliveryUri);
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("fulfillment", fulfillment);
                    StringEntity httpEntity =
                        (StringEntity) SLHttpClient.createStringEntity(requestBody.toJSONString());
                    assert httpEntity != null;
                    httpEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    HashMap<String, String> headerMap = new HashMap<>();
                    headerMap.put(AUTHORIZATION, accessToken);

                    String responseStr = SLHttpClient.post(createFulfillmentsUri.toString(), headerMap, httpEntity);
                    JSONObject response = JSONObject.parseObject(responseStr);
                    if (response == null) {
                        continue;
                    }
                    // レスポンスにerrorがあった場合、次の処理に続く
                    if (response.getString("error") != null && !response.getString("error").isEmpty()) {
                        if ("Location not found.".equals(response.getString("error"))) {
                            logger.info("Shopify 伝票番号連携 店舗【{}】のロケーション情報見つかりませんでした。", clientId);
                        } else {
                            logger.error("Shopify 伝票番号連携 店舗【{}】のフルフィルメント情報作成処理 失敗 出荷依頼ID: {} {}", clientId,
                                shipment.getShipment_plan_id(), response.getString("error"));
                        }
                        continue;
                    }
                    // 出荷済の出荷依頼を連携フラグを更新する
                    if (response.getJSONObject("errors") != null) {
                        JSONObject errors = response.getJSONObject("errors");
                        JSONArray base = errors.getJSONArray("base");
                        String errorMessage = base.get(0).toString();
                        if ("Line items are already fulfilled".equals(errorMessage)) {
                            logger.info("Shopify 伝票番号連携 店舗【{}】のフルフィルメント情報作成処理 失敗 出荷依頼ID: {} 伝票連携済", clientId,
                                shipment.getShipment_plan_id());
                            updateFinishedShipment(clientId, shipment.getWarehouse_cd(),
                                shipment.getShipment_plan_id());
                        } else {
                            logger.error("Shopify 伝票番号連携 店舗【{}】のフルフィルメント情報作成処理 失敗 出荷依頼ID: {} {}", clientId,
                                shipment.getShipment_plan_id(), errorMessage);
                        }
                        continue;
                    }
                    fulfillment = response.getJSONObject("fulfillment");
                    final String fulfillmentId = fulfillment.getString("id");
                    logger.info("Shopify 伝票番号連携 店舗【{}】のフルフィルメント情報作成処理 成功 フルフィルメントID: {}", clientId, fulfillmentId);
                    // **********************************************************
                    // ****************** フルフィルメント完了に更新 *****************
                    // **********************************************************
                    String completeFulfillmentsUri = "https://" + endpoint + "/admin/api/" + VERSION +
                        "/orders/" + orderNo + "/fulfillments/" + fulfillmentId + "/complete.json";
                    SLHttpClient.post(completeFulfillmentsUri, headerMap, null);
                    logger.info("Shopify 伝票番号連携 店舗【{}】のフルフィルメント情報更新処理 成功 フルフィルメントID: {}", clientId, fulfillmentId);
                    // **********************************************************
                    // ******************** 連携された出荷依頼更新 ******************
                    // **********************************************************
                    updateFinishedShipment(clientId, shipment.getWarehouse_cd(), shipment.getShipment_plan_id());
                    logger.info("Shopify 伝票番号連携 店舗【{}】の連携された出荷依頼更新処理 成功 出荷依頼ID: {}", clientId,
                        shipment.getShipment_plan_id());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * フルフィルメント情報作成
     *
     * @param shipment 出荷依頼情報
     * @param locationId ロケーション情報
     * @param deliveryUri 配送URL
     * @return フルフィルメント情報
     */
    private JSONObject createFulfillment(Tw200_shipment shipment, String locationId, String deliveryUri) {
        // 伝票番号
        final String trackingNo = shipment.getDelivery_tracking_nm();
        String deliveryName = shipment.getDelivery_method_name();
        JSONObject fulfillment = new JSONObject();
        fulfillment.put("location_id", locationId);
        StringBuilder trackingUrl = new StringBuilder();
        ArrayList<String> trackingUrlList = new ArrayList<>();
        if (trackingNo.contains(",")) {
            String[] trackingNoArray = trackingNo.split(",");
            fulfillment.put("tracking_numbers", trackingNoArray);
            trackingUrl.append("[");
            for (String noStr : trackingNoArray) {
                trackingUrl.append(deliveryUri);
                if (!"福山通運".equals(deliveryName)) {
                    trackingUrl.append(noStr);
                }
                trackingUrlList.add(trackingUrl.toString());
            }
            fulfillment.put("tracking_urls", trackingUrlList);
        } else {
            fulfillment.put("tracking_number", trackingNo);
            if (!"福山通運".equals(deliveryName)) {
                trackingUrl.append(trackingNo);
                deliveryUri = deliveryUri + trackingNo;
            }
            fulfillment.put("tracking_url", deliveryUri);
        }
        fulfillment.put("notify_customer", "true");
        return fulfillment;
    }

    /**
     * Shopifyのロケーション情報取得
     *
     * @param endpoint エンドポイント
     * @param accessToken アクセストークン
     * @return ロケーション情報リスト
     */
    private JSONArray getLocations(String endpoint, String accessToken) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(AUTHORIZATION, accessToken);
        String responseStr =
            SLHttpClient.get("https://" + endpoint + "/admin/api/" + VERSION + "/locations.json", headerMap);
        JSONObject response = JSONObject.parseObject(responseStr);
        if (response == null) {
            return null;
        }
        return response.getJSONArray("locations");
    }
}
