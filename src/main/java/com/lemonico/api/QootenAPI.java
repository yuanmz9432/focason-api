package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.HttpClientUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Qoo10のAPI連携機能
 * 
 * @className QootenAPI
 * @date 2021/07/20
 * @implNote 【注意!!】Qoo10のAPI仕様上、受注連携・送状番号連携ともに注文毎ではなく「注文明細毎」に連携されます
 * @implNote Qoo10側の[packNo]=注文番号 [OrderNo]=注文明細番号
 **/
@Component
@EnableScheduling
public class QootenAPI
{

    private final static Logger logger = LoggerFactory.getLogger(QootenAPI.class);
    // API検索始期（デフォルト:360分）
    private final Integer DEFAULT_TIME_UNIT = 360;
    // API検索日付形式
    private final DateTimeFormatter TIME_FORMAT_STR = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    // API連携共通URL
    private final String API_COMMON_URL = "http://api.qoo10.jp/GMKT.INC.Front.QAPIService/ebayjapan.qapi";
    // 受注取得
    private final String GET_ORDERS = "ShippingBasic.GetShippingInfo_v2";
    // キャンセル受注取得
    private final String GET_CANCEL_ORDERS = "ShippingBasic.GetClaimInfo_V3";
    // 発送予定日更新
    private final String UPDATE_ESTSHIPDATE_URL = API_COMMON_URL + "/ShippingBasic.SetSellerCheckYN_V2";
    // 送状番号更新
    private final String UPDATE_SHIPPING_URL = API_COMMON_URL + "/ShippingBasic.SetSendingInfo";
    // 注文状況（2:入金済み）
    private final String PAID = "2";
    // 日付の種類（2:入金日）
    private final String PAID_DATE = "2";
    // キャンセル状況（3:キャンセル完了）
    private final String CANCELED = "3";
    // 日付の種類（2:キャンセル/払い戻し完了日）
    private final String CANCELED_DATE = "3";
    // APIキーの項目名
    private final String TOKEN_NAME = "GiosisCertificationKey";
    // 発送予定日加算日数（3日）
    private final Integer DAYS_TO_ADD = 3;
    // 配送方法変換表（サンロジ → Qoo10）
    private final static HashMap<String, String> deliveryCarriersMap = new HashMap<>();

    static {
        deliveryCarriersMap.put("ヤマト運輸宅急便", "ヤマト宅急便");
        deliveryCarriersMap.put("ヤマト運輸宅急便コンパクト", "ヤマト宅急便");
        deliveryCarriersMap.put("ヤマト運輸ネコポス", "ネコポス");
        deliveryCarriersMap.put("ヤマト運輸DM便", "クロネコDM便");
        deliveryCarriersMap.put("日本郵便ゆうパック(コンビニ受取)", "コンビニ等受取");
        deliveryCarriersMap.put("日本郵便ゆうパケット", "ゆうパケット");
        deliveryCarriersMap.put("日本郵便クリックポスト", "クリックポスト");
        deliveryCarriersMap.put("日本郵便ゆうパック", "ゆうパック");
        deliveryCarriersMap.put("日本郵便ゆうパック(チルド)", "ゆうパック");
        deliveryCarriersMap.put("日本郵便ゆうパック(冷凍)", "ゆうパック");
        deliveryCarriersMap.put("日本郵便ゆうパック(コンビニ受取)", "ゆうパック");
        deliveryCarriersMap.put("日本郵便ゆうパック(郵便局窓口受取)", "ゆうパック");
        deliveryCarriersMap.put("日本郵便ゆうパック(はこぽす)", "ゆうパック");
        deliveryCarriersMap.put("佐川急便飛脚宅配便", "佐川急便");
        deliveryCarriersMap.put("佐川急便飛脚スーパー便", "佐川急便");
        deliveryCarriersMap.put("佐川急便飛脚即配便", "佐川急便");
        deliveryCarriersMap.put("佐川急便飛脚航空便(翌日中配達)", "佐川急便");
        deliveryCarriersMap.put("佐川急便飛脚航空便(翌日午前中配達)", "佐川急便");
        deliveryCarriersMap.put("佐川急便飛脚ジャストタイム便", "佐川急便");
        deliveryCarriersMap.put("佐川コンビニ受取佐川コンビニ受取", "佐川急便");
        deliveryCarriersMap.put("福山通運宅配便", "福山通運");
        deliveryCarriersMap.put("西濃運輸ミニ", "西濃運輸");
        deliveryCarriersMap.put("西濃運輸一般", "西濃運輸");
        deliveryCarriersMap.put("西濃運輸ビジネス便", "西濃運輸");
        deliveryCarriersMap.put("西濃運輸宅配便", "西濃運輸");
        deliveryCarriersMap.put("西濃運輸通販便", "西濃運輸");
    }

    @Resource
    private OrderApiService orderApiService;
    @Resource
    private ClientDao clientDao;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private DeliveryDao deliveryDao;
    @Resource
    private ShipmentsService shipmentsService;
    @Resource
    private APICommonUtils apiCommonUtils;

    /**
     * Qoo10店舗受注データ取得(JSON形式) (10分ごと自動起動 9,19,29,39,49,59)
     */
    // @Scheduled(cron = "0 9/10 * * * ?")
    public void fetchQoo10Orders() {
        logger.info("Qoo10受注連携 開始");
        // Qoo10利用店舗取得
        List<Tc203_order_client> clients = orderApiService.getAllDataOrder(API.QOOTEN.getName());
        if (CollectionUtils.isEmpty(clients)) {
            logger.info("Qoo10受注連携 Qoo10API店舗情報：0件");
            logger.info("Qoo10受注連携 終了");
            return;
        }
        logger.info("Qoo10受注連携 Qoo10API店舗情報：{}件", clients.size());
        apiCommonUtils.initialize();
        for (Tc203_order_client client : clients) {
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(client);
            // 店舗ID
            String clientId = initClientInfo.getClientId();
            // 販売者認証キー
            String apiKey = client.getApi_key();
            // 現在時刻
            LocalDateTime now = LocalDateTime.now();
            // 検索条件：時間単位(Default値:60 単位：分) ※備考2の値を取得
            int timeUnit = StringTools.isNullOrEmpty(client.getBikou2()) ? DEFAULT_TIME_UNIT
                : Integer.parseInt(client.getBikou2());
            // 検索条件：開始時間
            String start = now.minusMinutes(timeUnit).format(TIME_FORMAT_STR);
            // 検索条件：終了時間(現在時刻より2分前)
            String end = now.minusMinutes(2).format(TIME_FORMAT_STR);
            // 受注キャンセル処理
            processCancelOrder(start, end, client);
            // 受注データ取得URL
            StringBuilder url = new StringBuilder();
            String shippingStat = StringTools.isNullOrEmpty(client.getBikou1()) ? PAID : client.getBikou1();
            String searchCondition = StringTools.isNullOrEmpty(client.getBikou3()) ? PAID_DATE : client.getBikou3();
            url.append(API_COMMON_URL)
                .append("?")
                .append("key=").append(apiKey)
                .append("&v=1.0")
                .append("&returnType=json")
                .append("&method=").append(GET_ORDERS)
                .append("&ShippingStat=").append(shippingStat)
                .append("&search_Sdate=").append(start)
                .append("&search_Edate=").append(end)
                .append("&search_condition=").append(searchCondition);
            String responseStr;
            try {
                responseStr = HttpClientUtils.sendGet(url.toString(), TOKEN_NAME, apiKey, null);
            } catch (Exception e) {
                logger.error("Qoo10の受注連携APIリクエスト送信NG");
                logger.error(BaseException.print(e));
                continue;
            }
            JSONObject responseObj = JSONObject.parseObject(responseStr);
            JSONArray ordersArr = responseObj.getJSONArray("ResultObject");
            if (Collections.isEmpty(ordersArr)) {
                logger.info("Qoo10受注連携 受注データ0件 店舗ID：" + clientId);
                continue;
            }
            // データ整形
            // APIレスポンスの「明細のみ（N）」構造から「受注:明細（1 : N）」の構造に変換する
            JSONArray parents = new JSONArray();
            Set<String> packNos = new HashSet<>();
            // 受注単位で格納（親）
            for (int i = 0; i < ordersArr.size(); i++) {
                String packNo = ordersArr.getJSONObject(i).getString("packNo");
                if (!packNos.contains(packNo)) {
                    packNos.add(packNo);
                    parents.add(ordersArr.getJSONObject(i));
                }
            }
            // 明細単位で格納（子）
            for (int i = 0; i < parents.size(); i++) {
                JSONArray children = new JSONArray();
                for (int j = 0; j < ordersArr.size(); j++) {
                    String parentPackNo = parents.getJSONObject(i).getString("packNo");
                    JSONObject resultObj = ordersArr.getJSONObject(j);//
                    String childPackNo = resultObj.getString("packNo");
                    if (parentPackNo.equals(childPackNo)) {
                        // 同じカート番号の注文データを格納していく
                        children.add(resultObj);
                    }
                }
                parents.getJSONObject(i).fluentPut("details", children);
            }
            // 受注取込件数
            int total = parents.size();
            logger.info("Qoo10受注連携OK  店舗ID:{} 受注:{}件", clientId, total);
            // 成功件数
            int successCnt = 0;
            // 失敗件数
            int failureCnt = 0;
            // 受注番号の枝番
            int subNo = 1;
            // 受注取込履歴の最新IDを取得
            Integer historyId = initClientInfo.getHistoryId();
            // 全て受注を処理
            for (int i = 0; i < parents.size(); i++) {
                JSONObject orderObj = parents.getJSONObject(i);
                String orderNo = orderObj.getString("packNo");
                Integer outerOrderNo = orderDao.getOuterOrderNo(orderNo, clientId);
                if (outerOrderNo > 0) {
                    logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + orderNo + " 原因:過去受注取込済");
                    total--;
                    continue;
                }
                try {
                    initClientInfo.setSubNo(subNo);
                    // 受注データを書込
                    List<String> errorList = setTc200Order(orderObj, initClientInfo);
                    // 受注明細の子番号
                    subNo++;
                    // 成功かどうか
                    if (!Collections.isEmpty(errorList)) {
                        failureCnt++;// 失敗
                        logger.warn("Qoo10受注連携NG" + " 店舗ID:" + clientId + " 受注ID:" + orderNo
                            + " 原因:受注データ不正");
                        // 受注連携が失敗する場合、失敗内容を記録
                        apiCommonUtils.insertTc207OrderError(clientId, historyId, orderNo, errorList.get(0),
                            API.QOOTEN.getName());
                    } else {
                        successCnt++;// 成功
                        logger.info("Qoo10受注連携OK 店舗ID:" + clientId + " 受注ID:" + orderNo);
                        // Qoo10側発送予定日更新
                        updateEstShipDt(orderObj, apiKey, clientId);
                    }
                } catch (Exception e) {
                    subNo++;
                    failureCnt++;
                    logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + orderNo + " 原因:受注データ不正");
                    logger.error(BaseException.print(e));
                }
            }
            // 受注取込履歴登録
            if (successCnt > 0) {
                apiCommonUtils.processOrderHistory(clientId, API.QOOTEN.getName(), historyId, successCnt, failureCnt);
            }
        }
        // 受注連携終了
        logger.info("Qoo10受注連携 終了");
    }

    /**
     * 送り状番号自動連携 (15分ごと自動起動 9, 24, 39, 54)
     *
     * @date 2021/07/30
     * @description 【注意!!】Qoo10の仕様上、注文毎ではなく注文明細毎に連携します
     */
    // TODO:送り状番号連携が失敗したことを知らせる履歴テーブルの作成
    // @Scheduled(cron = "0 9/15 * * * ?")
    public void processTrackingNo() {
        logger.info("Qoo10伝票自動連携 開始");
        // Qoo10情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> clients = orderApiService.getAllDataDelivery(API.QOOTEN.getName());
        int counts = 0;
        int errs = 0;
        // 情報取得できない場合、処理スキップ
        if (Collections.isEmpty(clients)) {
            logger.info("Qoo10に関するAPI情報が存在しないので、【設定】→【連携設定】→【API連携設定】画面で新規登録してください。");
            logger.info("Qoo10伝票自動連携 終了");
            return;
        }
        for (Tc203_order_client client : clients) {
            // 店舗API情報
            String clientId = client.getClient_id();
            // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
            List<Tw200_shipment> shipmentList = shipmentsService.getShipmentListQoo10(clientId,
                API.QOOTEN.getName());
            // 出庫情報が存在しない場合、処理をスキップ
            if (Collections.isEmpty(shipmentList)) {
                logger.info("店舗ID:" + clientId + " の出庫情報が存在しないので、次の店舗に処理続きます。");
                continue;
            }
            // 連携対象の出庫情報全件処理
            for (Tw200_shipment shipment : shipmentList) {
                try {
                    // 更新対象の受注情報を取得
                    String outerOrderNo = shipment.getOrder_no();
                    // Qoo10注文明細毎の注文番号を取得
                    String relatedOrderNos = shipment.getRelated_order_no();
                    if (StringTools.isNullOrEmpty(relatedOrderNos)) {
                        logger.warn("Qoo10伝票連携NG" + " 店舗ID:" + clientId + " 外部受注番号:" + outerOrderNo
                            + " 原因:更新対象の注文番号がQoo10関連注文番号に存在せず更新処理が継続できません。");
                        continue;
                    }
                    // 配送伝票業者
                    String carrier = convertDeliveryCarrier(clientId, shipment.getDelivery_carrier());
                    if (StringTools.isNullOrEmpty(carrier)) {
                        // TODO:配送方法変換に失敗した場合、履歴テーブルへ登録する
                        logger.warn("Qoo10伝票連携NG" + " 店舗ID:" + clientId + " 外部受注番号:" + outerOrderNo
                            + " 配送会社(delivery_carrier):" + shipment.getDelivery_carrier()
                            + " 原因:配送方法の変換ができず更新処理を継続できません。");
                        continue;
                    }
                    // 配送伝票番号
                    String trackingNms = shipment.getDelivery_tracking_nm();
                    HashMap<String, String> paramMap = new HashMap<>();
                    List<String> targetOrderNos = Splitter.on(",").trimResults().omitEmptyStrings()
                        .splitToList(relatedOrderNos);
                    paramMap.put("ShippingCorp", carrier);
                    paramMap.put("TrackingNo", trackingNms);
                    // 更新結果
                    boolean failed = false;
                    // Qoo10は全ての明細行の出荷情報を更新する必要があります。
                    for (String orderNo : targetOrderNos) {
                        paramMap.put("OrderNo", orderNo);
                        HttpEntity entity = HttpClientUtils.createHttpEntity(paramMap);
                        String responseStr;
                        try {
                            responseStr = HttpClientUtils.sendPost(UPDATE_SHIPPING_URL, entity, TOKEN_NAME,
                                client.getApi_key(), null);
                        } catch (Exception e) {
                            logger.error("Qoo10の伝票番号連携NG"
                                + " 受注ID:" + outerOrderNo + " Qoo10注文番号:" + orderNo);
                            logger.error(BaseException.print(e));
                            failed = true;
                            continue;
                        }
                        JSONObject responseObj = JSONObject.parseObject(responseStr);
                        Integer resultCode = responseObj.getInteger("ResultCode");
                        String resultMsg = responseObj.getString("ResultMsg");
                        if (0 != resultCode) {
                            logger.error("Qoo10の伝票番号連携NG"
                                + " 受注ID:" + outerOrderNo + "  Qoo10注文番号:" + orderNo
                                + " エラーコード:" + resultCode + " 詳細:" + resultMsg);
                            failed = true;
                        }
                    }
                    if (failed) {
                        // 1回でも失敗した場合、API連携を連携済に変更しない
                        logger.error("Qoo10伝票連携NG"
                            + " 連携が失敗した注文情報があります。受注IDに紐づくQoo10注文番号を確認してください。"
                            + " 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + " 伝票ID:" + trackingNms);
                        errs++;
                    } else {
                        // 成功
                        // 倉庫コード
                        String warehouseCd = shipment.getWarehouse_cd();
                        // 配送プランID
                        String shipmentPlanId = shipment.getShipment_plan_id();
                        shipmentsService.setShipmentFinishFlg(clientId, warehouseCd, shipmentPlanId);
                        counts++;
                        logger.info("Qoo10伝票連携OK" + " 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + " 伝票ID:"
                            + trackingNms);
                    }
                } catch (Exception e) {
                    logger.error("Qoo10伝票連携NG 店舗ID:" + clientId + " API:" + UPDATE_SHIPPING_URL + "");
                    logger.error(BaseException.print(e));
                }
            }
        }
        logger.info("Qoo10伝票自動連携件数(OK:" + counts + " NG:" + errs + ")");
        logger.info("Qoo10伝票自動連携 終了");
    }

    /**
     * APIから受注キャンセル情報を取得しtc208_order_cancelに登録する
     *
     * @param start 検索期間開始（YYYYMMDD）
     * @param end 検索期間終了（YYYYMMDD）
     * @param client 店舗情報
     */
    private void processCancelOrder(String start, String end, Tc203_order_client client) {
        logger.info("Qoo10受注キャンセル処理 開始");
        StringBuilder url = new StringBuilder();
        url.append(API_COMMON_URL)
            .append("?")
            .append("key=").append(client.getApi_key())
            .append("&v=1.0")
            .append("&returnType=json")
            .append("&method=").append(GET_CANCEL_ORDERS)
            .append("&ClaimStat=").append(CANCELED)
            .append("&search_Sdate=").append(start)
            .append("&search_Edate=").append(end)
            .append("&search_condition=").append(CANCELED_DATE);
        String responseStr;
        try {
            responseStr = HttpClientUtils.sendGet(url.toString(), TOKEN_NAME, client.getApi_key(), null);
        } catch (Exception e) {
            logger.error("Qoo10受注キャンセル取得NG 店舗ID:{}", client.getClient_id());
            return;
        }
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        JSONArray responseArr = responseObj.getJSONArray("ResultObject");
        if (Collections.isEmpty(responseArr)) {
            logger.info("Qoo10受注キャンセル件数：0件");
            logger.info("Qoo10受注キャンセル処理 終了");
            return;
        }
        Set<String> canceledNos = new HashSet<>();
        // 同じカート番号の重複削除
        for (int i = 0; i < responseArr.size(); i++) {
            String number = responseArr.getJSONObject(i).getString("packNo");
            if (canceledNos.contains(number)) {
                continue;
            } else {
                canceledNos.add(number);
            }
        }
        for (String caneledPackNo : canceledNos) {
            apiCommonUtils.insertTc208OrderCancel(client.getClient_id(), caneledPackNo, API.QOOTEN.getName());
        }
        logger.info("Qoo10受注キャンセル処理 終了");
    }

    /**
     * @param order 受注データ
     * @param initClientInfo 共通パラメータ
     * @return List<String> エラーメッセージリスト
     * @description Tc_200_orderテーブルにデータを保存する
     * @author YuanMingZe
     * @date 2021/06/30
     */
    private List<String> setTc200Order(JSONObject order, InitClientInfoBean initClientInfo) {
        Tc200_order tc200Order = new Tc200_order();
        List<String> errList = new ArrayList<>();
        // 店舗ID
        String clientId = initClientInfo.getClientId();
        // 受注番号枝番
        Integer subNo = initClientInfo.getSubNo();
        // 識別番号
        String identification = initClientInfo.getIdentification();
        // 履歴番号
        Integer historyId = initClientInfo.getHistoryId();
        // 配送情報マスタ
        Map<String, String> ms007SettingTimeMap = initClientInfo.getMs007SettingTimeMap();
        Map<String, String> ms007SettingPaymentMap = initClientInfo.getMs007SettingPaymentMap();
        Map<String, String> ms007SettingDeliveryMethodMap = initClientInfo.getMs007SettingDeliveryMethodMap();
        // 依頼主情報
        Ms012_sponsor_master ms012sponsor = initClientInfo.getMs012sponsor();
        // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMMSS-00001」
        String purchaseOrderNo = APICommonUtils.getOrderNo(subNo, identification);
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 倉庫管理番号
        List<String> warehouseCds = orderDao.getWarehouseIdListByClientId(clientId);
        tc200Order.setWarehouse_cd(warehouseCds.get(0));
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(order.getString("packNo"));
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 个口数(Default値:1)
        tc200Order.setBoxes(1);
        // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済)
        tc200Order.setOuter_order_status(0);
        // 注文種別(1:入金済み固定)
        tc200Order.setOrder_type(1);
        try {
            // 注文日時
            tc200Order.setOrder_datetime(CommonUtils.dealDateFormat(order.getString("orderDate")));
            // 作成時間
            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
            // DB登録必須のパラメータが1つでも存在しない場合は処理中断
            Map<String, String> map = new HashMap<String, String>() {
                {
                    put("zipCode", "配送先:郵便番号");
                    put("Addr1", "配送先:都道府県＋市区町村");
                    put("Addr2", "配送先:丁目番地目号以降");
                    put("receiver", "配送先:氏名");
                }
            };
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (StringTools.isNullOrEmpty(order.getString(entry.getKey()))) {
                    logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + purchaseOrderNo + " 原因：" + entry.getValue()
                        + "の不正");
                    errList.add(entry.getValue() + "が存在しません。Qoo10店舗の注文情報をご確認ください。");
                    return errList;
                }
            }
            // 配送先郵便番号
            List<String> splitToList = splitZipCode(order.getString("zipCode"));
            // 配送先郵便番号1
            tc200Order.setReceiver_zip_code1(splitToList.get(0));
            // 配送先郵便番号2
            tc200Order.setReceiver_zip_code2(splitToList.get(1));
            // 配送先都道府県
            String todoufuken = "JP,";
            // 配送先市区町村
            String address1 = order.getString("Addr1");
            // 配送先都道府県と市区町村を分割
            String pattern = "(.{2,3}?[都道府県])(.+)";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(address1);
            if (!m.find()) {
                logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + purchaseOrderNo + " 原因：住所形式の不正");
                // errList.add("配送先住所に都道府県名が無いため登録できませんでした。住所：" + order.getString("Addr1"));
                // return errList;
            } else {
                todoufuken = m.group(1);
                address1 = m.group(2);
            }
            // 配送先都道府県
            tc200Order.setReceiver_todoufuken(todoufuken);
            // 配送先市区町村
            tc200Order.setReceiver_address1(address1);
            // 配送先丁目番地目号以降
            tc200Order.setReceiver_address2(order.getString("Addr2"));
            // 配送先姓
            tc200Order.setReceiver_family_name(order.getString("receiver"));
            // 配送先姓カナ
            tc200Order.setReceiver_family_kana(order.getString("receiver_gata"));
            // 配送先電話番号
            List<String> list = splitPhoneNumber(order.getString("receiverMobile"), order.getString("receiverTel"));
            if (list != null && list.size() > 0) {
                // 配送先電話番号1
                tc200Order.setReceiver_phone_number1(list.get(0));
                // 配送先電話番号2
                tc200Order.setReceiver_phone_number2(list.get(1));
                // 配送先電話番号3
                tc200Order.setReceiver_phone_number3(list.get(2));
            }
            // 配送方法の値を取得
            String deliveryCompany = order.getString("DeliveryCompany");
            tc200Order.setBikou9(deliveryCompany);
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);
            Ms004_delivery ms004Delivery = apiCommonUtils.getDeliveryMethod(deliveryCompany,
                clientInfo.getDelivery_method(), ms007SettingDeliveryMethodMap);
            if (!Objects.isNull(ms004Delivery)) {
                // 配送会社コード
                tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
                // 配送方法
                tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
                // 配送業者
                deliveryCompany = ms004Delivery.getDelivery_nm();
            }
            // 配送希望日・時間
            String hopeDateAndTime = order.getString("hopeDate");
            String hopeDeliveryDate = "";
            String strDeliveryTime = "";
            if (!StringTools.isNullOrEmpty(hopeDateAndTime)) {
                if (hopeDateAndTime.matches("\\d{4}\\-\\d{2}\\-\\d{2}\\s+")) {
                    // YYYY-MM-DD(日付指定のみ)
                    hopeDeliveryDate = hopeDateAndTime;
                } else if (hopeDateAndTime.matches("\\d{2}\\:\\d{2}\\~\\d{2}\\:\\d{2}")) {
                    // HH:MM~HH:MM（時間指定のみ）
                    strDeliveryTime = hopeDateAndTime;
                } else {
                    // YYYY-MM-DD HH:MM~HH:MM（日付＆時間指定）
                    String[] split = hopeDateAndTime.split(" ");
                    hopeDeliveryDate = split[0];
                    strDeliveryTime = split[1];
                }
            }
            tc200Order.setDelivery_date(DateUtils.stringToDate(hopeDeliveryDate));
            // 配送時間帯
            String deliveryTimeId = "";
            if (!StringTools.isNullOrEmpty(strDeliveryTime)) {
                deliveryTimeId = apiCommonUtils.getDeliveryTimeSlot(deliveryCompany, strDeliveryTime,
                    API.QOOTEN.getName(), ms007SettingTimeMap);
            }
            tc200Order.setDelivery_time_slot(deliveryTimeId);
            // 発送予定日(yyyy-mm-dd)
            tc200Order.setShipment_plan_date(DateUtils.stringToDate(order.getString("EstShippingDate")));
            // 削除Flg(0:無、1:済 )
            tc200Order.setDel_flg(0);
            // 配送メッセージ
            tc200Order.setMemo(order.getString("ShippingMsg"));
        } catch (Exception e) {
            errList.add("配送先に関しての情報が不備のため、取込できず、Qoo10店舗の注文情報をご確認ください。");
            logger.error(BaseException.print(e));
            return errList;
        }

        // 注文情報
        JSONArray details = order.getJSONArray("details");
        // 商品価格合計
        int totalProductAmount = 0;
        // 割引価格合計
        int totalDiscountAmount = 0;
        // 注文金額合計
        int totalOrderAmount = 0;
        // オプション価格合計
        int totalOptionAmount = 0;
        for (int i = 0; i < details.size(); i++) {
            // 明細毎の金額項目を取得して注文全体の合計金額として加算していきます
            JSONObject detail = details.getJSONObject(i);
            // 商品単価（割引前）
            int unitPrice = detail.getInteger("orderPrice");
            // 明細毎の注文数量
            int quantity = detail.getInteger("orderQty");
            // 明細毎の商品合計金額
            int product = unitPrice * quantity;
            // 明細毎の割引金額
            int discount = detail.getInteger("discount");
            // 明細毎の合計金額（商品合計金額 + オプション金額 - 割引額）
            int total = detail.getInteger("total");
            // 明細毎のオプション金額（明細毎の合計金額 + 商品割引額 - 商品合計金額）
            int option = total + discount - product;
            // 注文全体の金額
            totalProductAmount += product;
            totalOptionAmount += option;
            totalDiscountAmount += discount;
            totalOrderAmount += total;
        }
        // 商品税抜金額※Qoo10の仕様上税込金額のみ取り扱うため、ここでは税込み金額を登録しています
        tc200Order.setProduct_price_excluding_tax(totalProductAmount);
        // その他金額※合計割引金額を登録しています
        tc200Order.setOther_fee(totalDiscountAmount);
        // 手数料
        tc200Order.setHandling_charge(totalOptionAmount);
        // 送料合計
        int deliveryTotal = order.getInteger("ShippingRate");
        tc200Order.setDelivery_total(deliveryTotal);
        // 合計請求金額
        tc200Order.setBilling_total(totalOrderAmount + deliveryTotal);
        // 注文者情報
        tc200Order.setOrder_family_name(order.getString("buyer"));
        tc200Order.setOrder_family_kana(order.getString("buyer_gata"));
        // 注文者電話番号
        List<String> list = splitPhoneNumber(order.getString("buyerMobile"), order.getString("buyerTel"));
        if (list != null && list.size() > 0) {
            // 注文者電話番号1
            tc200Order.setOrder_phone_number1(list.get(0));
            // 注文者電話番号2
            tc200Order.setOrder_phone_number2(list.get(1));
            // 注文者電話番号3
            tc200Order.setOrder_phone_number3(list.get(2));
        }
        tc200Order.setOrder_mail(order.getString("buyerEmail"));
        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
        if (!StringTools.isNullOrEmpty(ms012sponsor)) {
            tc200Order.setSponsor_id(ms012sponsor.getSponsor_id());
            tc200Order.setDetail_message(ms012sponsor.getDetail_message());
            // 是否以注文者为依頼主(0:依頼マスタ 1:注文者)
            tc200Order.setOrder_flag(0);
        } else {
            // 是否以注文者为依頼主(0:依頼マスタ 1:注文者)
            tc200Order.setOrder_flag(1);
        }
        // 明細書金額印字
        String detailPricePrint = "0";
        if (!Objects.isNull(ms012sponsor.getPrice_on_delivery_note())) {
            detailPricePrint = String.valueOf(ms012sponsor.getPrice_on_delivery_note());
        }
        tc200Order.setDetail_price_print(detailPricePrint);
        // 明細同梱設定(0:同梱しない 1:同梱する)
        if (0 == ms012sponsor.getDelivery_note_type()) {
            tc200Order.setDetail_bundled("同梱しない");
        } else {
            tc200Order.setDetail_bundled("同梱する");
        }
        // 支払方法
        String paymentMethod = order.getString("PaymentMethod");
        if (!StringTools.isNullOrEmpty(paymentMethod)) {
            // 元々の支払方法を備考10に記載する
            tc200Order.setBikou10(paymentMethod);
            paymentMethod = apiCommonUtils.getPaymentMethod(paymentMethod, API.QOOTEN.getName(),
                initClientInfo.getMs007SettingPaymentMap());
            tc200Order.setPayment_method(paymentMethod);

        }
        // Qoo10の配送方法に代引きは存在しないため固定値0を設定
        tc200Order.setCash_on_delivery_fee(0);
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        // 関連注文番号（同じカート番号に紐づく注文番号）※送状番号連携時に使用します
        tc200Order.setRelated_order_no(order.getString("RelatedOrder"));
        // 削除Flg(0削除しない)
        tc200Order.setDel_flg(0);

        try {
            // 受注管理表を書き込み
            orderDao.insertOrder(tc200Order);
        } catch (Exception e) {
            logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + order.getString("orderNumber")
                + " 原因：受注管理の登録失敗");
            logger.error(BaseException.print(e));
            errList.add("受注管理表のDB登録が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }

        try {
            // 受注詳細を書き込み
            errList = setTc201OrderDetail(order.getJSONArray("details"), initClientInfo, purchaseOrderNo);
        } catch (Exception e) {
            logger.warn("Qoo10受注連携NG 店舗ID：" + clientId + " 受注ID：" + order.getString("orderNumber")
                + " 原因：受注明細の登録失敗");
            logger.error(BaseException.print(e));
            errList.add("受注明細管理表のDB登録が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }

        // 自動出庫フラグ(1：出庫する 0:出庫しない)
        if (Collections.isEmpty(errList) && initClientInfo.getShipmentStatus() == 1) {
            apiCommonUtils.processShipment(purchaseOrderNo, clientId);
        }
        // 戻り値
        return errList;
    }

    /**
     * @param orderDetails 受注明細データ
     * @param initClientInfo 共通パラメータ
     * @return List<String>
     * @description 受注明細テーブルにレコードを挿入する
     * @author YuanMingZe
     * @date 2021/07/05
     */
    public List<String> setTc201OrderDetail(JSONArray orderDetails, InitClientInfoBean initClientInfo,
        String purchaseOrderNo) {
        List<String> errList = new ArrayList<>();
        Integer subNo = 1;
        for (int i = 0; i < orderDetails.size(); i++) {
            Tc201_order_detail tc201OrderDetail = new Tc201_order_detail();
            JSONObject detail = orderDetails.getJSONObject(i);
            // API連携
            Integer apiId = initClientInfo.getApiId();
            // 店舗ID
            String clientId = initClientInfo.getClientId();
            // Qoo10システム商品コード
            String renkeiPid = detail.getString("itemCode");
            // オプション
            String options = detail.getString("option");
            // サンロジ商品コード[code]に登録する値の優先順位は以下の通り
            // 1.オプションコード [optionCode]
            // 2.販売商品コード [sellerItemCode]
            // 3.Qoo10システム商品番号 [itemCode]
            String code;
            if (!StringTools.isNullOrEmpty(detail.getString("optionCode"))) {
                code = detail.getString("optionCode");
            } else if (!StringTools.isNullOrEmpty(detail.getString("sellerItemCode"))) {
                code = detail.getString("sellerItemCode");
            } else {
                code = detail.getString("optionCode");
            }
            // 商品名
            String productName = detail.getString("itemTitle");
            // 商品単価
            Integer priceTaxIncl = detail.getInteger("orderPrice");
            // 軽減税率（Qoo10に軽減税率商品であるかどうか判別する機能が無いため固定値"0"を登録）
            int isReducedTax = 0;
            // 商品新規登録
            try {
                ProductBean productBean = new ProductBean();
                productBean.setClientId(clientId);
                productBean.setCode(code);
                productBean.setName(productName);
                productBean.setApiId(apiId);
                productBean.setPrice(String.valueOf(priceTaxIncl));
                productBean.setIsReducedTax(isReducedTax);
                productBean.setRenkeiPid(renkeiPid);
                productBean.setOptions(options);
                // Mc100_productテーブルの既存商品をマッピング
                Mc100_product mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
                // 商品登録されていない場合、商品マスタに仮商品として新規登録
                if (Objects.isNull(mc100Product)) {
                    // 商品新規登録
                    mc100Product = apiCommonUtils.insertMc100Product(productBean, API.QOOTEN.getName());
                    // 商品之前不存在 设定为仮登録
                    tc201OrderDetail.setProduct_kubun(9);
                }

                if (!Objects.isNull(mc100Product)) {
                    apiCommonUtils.insertMc106Product(clientId, apiId, renkeiPid, mc100Product, API.QOOTEN.getName());
                }
                if (!Objects.isNull(mc100Product)) {
                    // 商品ID
                    tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
                    // 商品コード
                    code = mc100Product.getCode();
                    // セット商品ID
                    if (mc100Product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                        tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
                    }
                    // 同梱物(0:なし 1:同梱物)
                    if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg())
                        && mc100Product.getBundled_flg() == 1) {
                        tc201OrderDetail.setBundled_flg(mc100Product.getBundled_flg());
                    } else {
                        tc201OrderDetail.setBundled_flg(0);
                    }
                    // 商品区分(0:本登録 9:仮登録)
                    int kubun = 0;
                    Integer productKubun = mc100Product.getKubun();
                    if (!StringTools.isNullOrEmpty(productKubun)) {
                        kubun = productKubun;
                    }
                    tc201OrderDetail.setProduct_kubun(kubun);
                } else {
                    throw new BaseException(ErrorCode.E_11005);
                }
            } catch (Exception e) {
                logger.error("Qoo10受注連携NG 店舗ID:{} 受注明細ID:{} 原因:商品登録失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.E_11005);
            }
            // 受注明細番号
            tc201OrderDetail.setOrder_detail_no(purchaseOrderNo + "-" + String.format("%03d", subNo));
            // 受注番号
            tc201OrderDetail.setPurchase_order_no(purchaseOrderNo);
            // 商品名
            tc201OrderDetail.setProduct_name(productName);
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // 商品オプション値を保管
            tc201OrderDetail.setProduct_option(options);
            // 単価
            tc201OrderDetail.setUnit_price(priceTaxIncl);
            // 個数
            int quantity = detail.getInteger("orderQty");
            tc201OrderDetail.setNumber(quantity);
            // 商品合計金額
            int productTotalPrice = priceTaxIncl * quantity;
            tc201OrderDetail.setProduct_total_price(productTotalPrice);
            // オプション金額（明細毎の合計金額 + 割引額 - 商品合計金額）
            int optionPrice = detail.getInteger("total") + detail.getInteger("discount") - productTotalPrice;
            tc201OrderDetail.setOption_price(optionPrice);
            // 軽減税率適用商品
            tc201OrderDetail.setIs_reduced_tax(isReducedTax);
            // 税区分(0:税込 1:税抜) ※Qoo10仕様上全て税込みのため固定値0を設定
            tc201OrderDetail.setTax_flag(0);
            // 削除フラグ
            tc201OrderDetail.setDel_flg(0);
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
                subNo++;
            } catch (Exception e) {
                logger.error("Qoo10受注明細テーブルの登録失敗 受注ID(" + purchaseOrderNo + ")");
                logger.error(BaseException.print(e));
                // 受注管理レコード削除
                orderDao.orderDelete(clientId, purchaseOrderNo);
                // 受注明細レコード削除
                orderDetailDao.orderDetailDelete(purchaseOrderNo);
                errList.add("何か原因により、受注明細の取込が失敗したので、システム担当者にお問い合わせください。");
                return errList;
            }
        }
        return errList;
    }

    /**
     * Qoo10側の発送予定日更新 #30753
     *
     * @param order 受注情報
     * @param apiKey アクセストークン
     * @param clientId 店舗ID
     * @date 2021/10/19
     */
    public void updateEstShipDt(JSONObject order, String apiKey, String clientId) {
        // 受注ID
        String packNo = order.getString("packNo");
        // 更新対象の受注明細番号リスト
        List<String> targetOrderNos = Splitter.on(",").trimResults().omitEmptyStrings()
            .splitToList(order.getString("RelatedOrder"));
        // 発送予定日に設定する
        Date paymentDate;
        try {
            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(order.getString("PaymentDate"));
        } catch (ParseException e) {
            logger.error("Qoo10発送予定日更新NG 原因:入金日フォーマットエラー 受注ID:{}", packNo);
            logger.error(BaseException.print(e));
            return;
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(paymentDate);
        calender.add(Calendar.DATE, DAYS_TO_ADD);
        String estShipDt = new SimpleDateFormat("yyyyMMdd").format(calender.getTime());

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("EstShipDt", estShipDt);
        for (String orderNo : targetOrderNos) {
            requestMap.put("OrderNo", orderNo);
            HttpEntity entity = HttpClientUtils.createHttpEntity(requestMap);
            String responseStr;
            try {
                responseStr = HttpClientUtils.sendPost(UPDATE_ESTSHIPDATE_URL, entity, TOKEN_NAME, apiKey, null);
            } catch (Exception e) {
                logger.error("Qoo10発送予定日更新NG 店舗ID:{} 受注ID:{} 原因:HTTP接続エラー ", clientId, orderNo);
                logger.error(BaseException.print(e));
                continue;
            }
            JSONObject response = JSONObject.parseObject(responseStr);
            Integer resultCode = response.getInteger("ResultCode");
            String resultMsg = response.getString("ResultMsg");
            if (0 != resultCode) {
                logger.error("Qoo10発送予定日更新NG 店舗ID:{} カート番号:{} 注文番号:{} エラーコード:{} 詳細:{}",
                    clientId, packNo, orderNo, resultCode, resultMsg);
            } else {
                logger.info("Qoo10発送予定日更新OK 店舗ID:{} カート番号:{} 注文番号:{}", clientId, packNo, orderNo);
            }
        }
    }

    private List<String> splitZipCode(String zipCode) {
        String formatted = CommonUtils.formatZip(zipCode);
        return Splitter.on("-").trimResults().omitEmptyStrings().splitToList(formatted);
    }

    /**
     * 出荷配送方法をQoo10配送マスタの配送方法に変換する
     *
     * @param clientId 店舗ID
     * @param deliveryCarrier 配送会社コード
     * @return String Qoo10配送方法マスタの名称
     * @date 2021/10/12
     */
    private String convertDeliveryCarrier(String clientId, String deliveryCarrier) {
        Ms004_delivery delivery = deliveryDao.getDeliveryById(deliveryCarrier);
        String carrierQoo10 = null;
        // 配送会社 + 配送方法名をキーにしてQoo10配送方法名称に変換します
        String deliveryNmMethod = delivery.getDelivery_nm() + delivery.getDelivery_method_name();
        if (deliveryCarriersMap.containsKey(deliveryNmMethod)) {
            carrierQoo10 = deliveryCarriersMap.get(deliveryNmMethod);
            return carrierQoo10;
        }
        return carrierQoo10;
    }

    /**
     * 電話番号を分割して取得する（携帯番号優先）
     *
     * @param mobile 携帯電話番号
     * @param tel 固定電話番号
     * @return List<String> 分割後の電話番号
     * @date 2021/09/29
     */
    private List<String> splitPhoneNumber(String mobile, String tel) {
        String phoneNumber = mobile;
        if (StringTools.isNullOrEmpty(phoneNumber) || "+81--".equals(phoneNumber)) {
            // 携帯番号未入力でnull,"",+81--が返された場合は固定電話番号を登録する
            phoneNumber = tel;
        }
        return CommonUtils.checkPhoneToList(phoneNumber.replace("+81-", ""));
    }
}
