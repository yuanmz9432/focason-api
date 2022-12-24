package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.dao.ProductRenkeiDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.OrderService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * BASEのAPI連携機能
 *
 * @author GaoJiaBao
 * @className BaseAPI
 * @date 2020/12/14
 **/
@Component
@EnableScheduling
public class BaseAPI
{

    private final static Logger logger = LoggerFactory.getLogger(BaseAPI.class);
    // トークン名
    private final static String TOKEN_NAME = "Authorization";
    // BASEの配送方法対応番号
    private static final HashMap<String, String> deliveryMethodMap = new HashMap<>();

    static {
        deliveryMethodMap.put("creditcard", "クレジットカード決済");
        deliveryMethodMap.put("bt", "銀行振込(ショップ口座)");
        deliveryMethodMap.put("cod", "代金引換");
        deliveryMethodMap.put("cvs", "コンビニ決済");
        deliveryMethodMap.put("base_bt", "銀行振込(BASE口座)");
        deliveryMethodMap.put("atobarai", "後払い決済");
        deliveryMethodMap.put("carrier_01", "キャリア決済ドコモ");
        deliveryMethodMap.put("carrier_02", "キャリア決済au");
        deliveryMethodMap.put("carrier_03", "キャリア決済ソフトバンク");
        deliveryMethodMap.put("paypal", "PayPal決済");
        deliveryMethodMap.put("coin", "コイン決済");
        deliveryMethodMap.put("amazon_pay", "Amazon Pay");
    }

    @Resource
    private OrderApiService apiService;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private OrderApiDao orderApiDao;
    @Resource
    private ProductRenkeiDao productRenkeiDao;
    @Resource
    private StockDao stockDao;
    @Resource
    private ShipmentsService shipmentsService;
    @Resource
    private APICommonUtils apiCommonUtils;
    // @Resource
    // private MailTools mailTools;
    @Resource
    private DeliveryDao deliveryDao;
    @Value("${baseUrl}")
    private String baseUrl;
    @Resource
    private ProductDao productDao;
    @Resource
    private ShipmentDetailDao shipmentDetailDao;
    @Resource
    private OrderService orderService;

    /**
     * 获取mc106数据和tw300在库数据
     *
     * @param apiService apiService
     * @param productRenkeiDao productRenkeiDao
     * @param stockDao stockDao
     * @param template 模板
     * @return HashMap<String, Object> 在庫マップ
     * @author GaoJiaBao
     * @date 2021/1/12 16:43
     */
    public static HashMap<String, Object> getStockMap(OrderApiService apiService, ProductRenkeiDao productRenkeiDao,
        StockDao stockDao, String template) {
        // a获取所有和template连携的信息
        List<Tc203_order_client> allData = apiService.getAllData(template);
        // a获取到属于base的 tc203的 id 集合
        List<Integer> idList = allData.stream().map(Tc203_order_client::getId).collect(Collectors.toList());
        if (idList.size() != 0) {
            // a根据api_id 获取到mc106的集合
            List<Mc106_produce_renkei> produceRenkeis = productRenkeiDao.getDataByApiId(idList);
            // a生成 以clientId - productId 为key值， mc106对象为value的 map
            HashMap<String, Mc106_produce_renkei> renkeiMap = (HashMap<String, Mc106_produce_renkei>) produceRenkeis
                .stream().filter(x -> !StringTools.isNullOrEmpty(x.getRenkei_product_id()))
                .collect(Collectors.toMap(renkei -> renkei.getClient_id() + "-" + renkei.getProduct_id(),
                    mc106ProduceRenkei -> mc106ProduceRenkei));
            // a从mc106表里面取到 sunlogi系统的商品id集合
            List<Tw300_stock> stocks = stockDao.getAllAvailableCnt();
            // a以clientId - productId 为key值， 理论在库数为value
            HashMap<String, Integer> stockMap = (HashMap<String, Integer>) stocks.stream()
                .filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
                .collect(Collectors.toMap(stock -> stock.getClient_id() + "-" + stock.getProduct_id(),
                    Tw300_stock::getAvailable_cnt));
            HashMap<String, Object> map = new HashMap<>();
            map.put("renkei", renkeiMap);
            map.put("stock", stockMap);
            return map;
        } else {
            return null;
        }
    }

    /**
     * BASE受注自動取込 (10分ごと自動起動 2, 12, 22, 32, 42, 52)
     *
     * @author YuanMingZe
     * @date 2021/06/30
     */
    // @Scheduled(cron = "0 2/10 * * * ?")
    public void fetchBaseOrders() {
        logger.info("BASE受注連携 開始");
        // Baseに関する情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.BASE.getName());
        // BASEに関するAPI情報が存在しない場合、処理中止
        if (Collections.isEmpty(allData)) {
            logger.info("Base受注連携 店舗情報：0件");
            logger.info("Base受注連携 終了");
            return;
        }
        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        Hashtable<String, List<String>> errHashtable = new Hashtable<>();
        apiCommonUtils.initialize();
        for (Tc203_order_client data : allData) {

            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();

            // Baseのトークンをリフレッシュ
            boolean isSucceed = refreshToken(data);
            if (!isSucceed) {
                logger.warn("Base受注連携 店舗ID：{} 原因：アクセストークン更新失敗", data.getClient_id());
                errList.add("アクセストークン更新失敗");
                errOrderClients.add(data);
                continue;
            }
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(data);
            JSONArray orders;
            String clientId = data.getClient_id();
            // 受注取得の時間帯を取得
            String url = data.getClient_url() + "/1/orders?limit=" + 100;
            // ディフォルト取得時間を30分にする
            String bikou1 = data.getBikou1();
            int timeUnit = 30;
            if (!Objects.isNull(bikou1)) {
                timeUnit = Integer.parseInt(bikou1);
            }
            // 検索範囲：受注時間を設定
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.MINUTE, timeUnit * -1);
            Date time = startTime.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 検索範囲：受注開始時間
            String startOrdered = simpleDateFormat.format(time);
            // 検索範囲：受注終了時間 ※API起動時間
            String endOrdered = simpleDateFormat.format(new Date());
            try {
                url += "&start_ordered=" + URLEncoder.encode(startOrdered, "utf-8") +
                    "&end_ordered=" + URLEncoder.encode(endOrdered, "utf-8");
            } catch (Exception e) {
                logger.error("Base受注連携NG 店铺ID:{} 原因:受注時間帯の設定失敗", data.getClient_id());
                logger.error(BaseException.print(e));
                errList.add("受注時間帯の設定失敗");
                continue;
            }
            String responseStr = HttpClientUtils.sendGet(url, TOKEN_NAME, data.getAccess_token(), errList);
            JSONObject responseObj = JSONObject.parseObject(responseStr);
            if (!Objects.isNull(responseObj)) {
                orders = responseObj.getJSONArray("orders");
            } else {
                continue;
            }
            // 取込件数
            int total = orders.size();
            // 成功件数
            int successCnt = 0;
            // 失敗件数
            int failureCnt = 0;
            // 受注番号の枝番
            int subno = 1;
            // dispatch_status - 注文ステータス。
            // ordered:発送待ち、cancelled:キャンセル、dispatched:発送済み、unpaid:入金待ち、shipping:配送中
            List<String> orderStatus = Arrays.asList("ordered", "cancelled", "unpaid");

            boolean errFlg = false;

            for (int i = 0; i < orders.size(); i++) {
                JSONObject json = orders.getJSONObject(i);
                // 受注ステータス
                String dispatchStatus = json.getString("dispatch_status");
                // 注文情報を識別するユニークなキー
                String uniqueKey = json.getString("unique_key");

                // dispatch_status - 注文ステータス。ordered:発送待ち、cancelled:キャンセル、dispatched:発送済み、unpaid:入金待ち、shipping:配送中
                if (!orderStatus.contains(dispatchStatus)) {
                    failureCnt++;
                    logger.warn("Base受注連携NG 店舗ID:{} 受注番号:{} 原因：受注取込の対象外{}", data.getClient_id(), uniqueKey,
                        dispatchStatus);
                    continue;
                }
                // キャンセルされた受注情報を保存する
                if ("cancelled".equals(dispatchStatus)) {
                    logger.info("Base受注連携NG 店舗ID:{} 受注番号:{} 原因：受注キャンセル", data.getClient_id(), uniqueKey);
                    apiCommonUtils.insertTc208OrderCancel(clientId, uniqueKey, API.BASE.getName());
                    continue;
                }

                // 注文区分(0:入金待ち 1:入金済み)
                int orderType = 1;
                // 入金待ちの受注情報を保存する
                if ("unpaid".equals(dispatchStatus)) {
                    orderType = 0;
                }
                // 受注明細を取得
                String detailUrl = data.getClient_url() + "/1/orders/detail/" + uniqueKey;

                try {
                    // 注文明細を取得
                    responseStr = HttpClientUtils.sendGet(detailUrl, TOKEN_NAME, data.getAccess_token(), errList);
                } catch (Exception e) {
                    logger.info("Base受注連携の取得は失敗しました。");
                    logger.error(BaseException.print(e));
                    continue;
                }
                JSONObject detailResponseObj = JSONObject.parseObject(responseStr);
                // 注文ない場合
                JSONObject order = null;
                if (!StringTools.isNullOrEmpty(detailResponseObj)
                    && !StringTools.isNullOrEmpty(detailResponseObj.getJSONObject("order"))) {
                    order = detailResponseObj.getJSONObject("order");
                }

                // 受注情報を取得しない場合、処理スキップ
                if (StringTools.isNullOrEmpty(order)) {
                    logger.warn("Base受注連携NG　店舗ID【{}】 受注番号【{}】原因：受注取得失敗", clientId, uniqueKey);
                    failureCnt++;
                    continue;
                }
                // 受注データが存在したかどうかを判断
                int outerOrderNo = orderDao.getOuterOrderNo(uniqueKey, clientId);
                if (outerOrderNo == 0) {
                    // **********************************************************
                    // ********************* 共通パラメータ整理 *********************
                    // **********************************************************
                    try {
                        // 受注子番号をセットする ※重要
                        initClientInfo.setSubNo(subno);
                        // 入金済みかどうかの確認
                        order.fluentPut("com_orderType", orderType);
                        // 受注明細を取得
                        setTc200Json(order, initClientInfo);
                        subno++;
                        successCnt++;
                        logger.info("Base受注連携OK 店舗ID:{} 受注番号:{}", clientId, uniqueKey);
                    } catch (Exception e) {
                        failureCnt++;
                        logger.error("Base受注連携NG 店舗ID:{} 受注番号:{} 原因:受注TBLの書込み失敗", clientId, uniqueKey);
                        logger.error(BaseException.print(e));
                        errList.add(" 受注番号: " + uniqueKey + " 原因:受注TBLの書込み失敗");
                        errFlg = true;
                    }
                }
                if (outerOrderNo > 0) {
                    logger.warn("Base受注連携NG 店舗ID:{} 受注番号:{} 原因:過去受注取込済", clientId, uniqueKey);
                    total--;
                }
            }

            if (errFlg) {
                errOrderClients.add(data);
            }
            // 受注履歴を記録
            if (successCnt > 0) {
                apiCommonUtils.processOrderHistory(clientId, API.BASE.getName(), initClientInfo.getHistoryId(),
                    successCnt, failureCnt);
            }
            logger.info("Base受注連携 店舗ID:{} 取込件数:{} 成功件数:{} 失败件数:{}", clientId, total, successCnt, failureCnt);

            if (!errList.isEmpty()) {
                String key = clientId + "_API名：" + data.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        logger.info("Base受注連携 終了");
        // mailTools.sendErrorMessage(errHashtable, API.BASE.getName());

        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 出庫ステータス自動連携 (10分ごと自動起動 3, 18, 33, 48)
     *
     * @author YuanMingZe
     * @date 2021/07/02
     */
    // @Scheduled(cron = "0 0/30 * * * ?")
    public void updateShipmentStatus() {
        logger.info("Base受注連携(入金待ち) 開始");
        // MakeShopに関する情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.BASE.getName());
        // MakeShopに関する情報を全て処理
        for (Tc203_order_client data : allData) {
            // 初期化
            StringBuilder url = new StringBuilder();
            List<Tw200_shipment> tw200Shipments;
            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();
            // 店舗ID
            String clientId = data.getClient_id();
            String apiUrl = data.getClient_url();
            String apiToken = data.getAccess_token();
            // 受注データ取得 取得条件：該当店舗、受注データの支払ステータスが未入金、del_flgが0の場合
            try {
                tw200Shipments = orderDao.getOrderByStatus(clientId);
                for (Tw200_shipment tw200Shipment : tw200Shipments) {
                    // 倉庫コード
                    String warehouseCd = tw200Shipment.getWarehouse_cd();
                    // 出庫ID
                    String shipmentPlanId = tw200Shipment.getShipment_plan_id();
                    // 受注番号
                    String orderNum = tw200Shipment.getOrder_no();
                    // URLを組み立てる
                    String detailUrl = data.getClient_url() + "/1/orders/detail/" + orderNum;

                    String responseStr;
                    try {
                        // 注文明細を取得
                        responseStr = HttpClientUtils.sendGet(detailUrl, TOKEN_NAME, apiToken, errList);
                    } catch (Exception e) {
                        logger.info("Base受注連携(入金待ち)の取得は失敗しました。");
                        logger.error(BaseException.print(e));
                        continue;
                    }
                    JSONObject resultObj = JSONObject.parseObject(responseStr);
                    // 注文ない場合
                    JSONObject order = null;
                    if (!StringTools.isNullOrEmpty(resultObj)
                        && !StringTools.isNullOrEmpty(resultObj.getJSONObject("order"))) {
                        order = resultObj.getJSONObject("order");
                    }

                    // 受注件数を取得しない場合、処理スキップ
                    if (order == null) {
                        logger.warn("Base受注連携(入金待ち)NG　店舗ID【{}】 受注番号【{}】原因：受注キャンセル", clientId, orderNum);
                        continue;
                    }
                    // 受注ステータス
                    String dispatchStatus = order.getString("dispatch_status");
                    // 入金待ちの場合、処理スキップ
                    if ("unpaid".equals(dispatchStatus)) {
                        logger.info("Base受注連携(入金待ち)　店舗ID【{}】 受注番号【{}】原因：入金待ちのまま", clientId, orderNum);
                    }

                    // キャンセルされた受注情報を保存する
                    if ("cancelled".equals(dispatchStatus)) {
                        logger.info("Base受注連携(入金待ち) 店舗ID:{} 受注番号:{} 原因：受注キャンセル", clientId, orderNum);
                        apiCommonUtils.insertTc208OrderCancel(clientId, orderNum, API.BASE.getName());
                        continue;
                    }
                    // 入金待ちを発送待ちとなる場合、出庫依頼ステータスを変更
                    if ("ordered".equals(dispatchStatus)) {
                        orderService.upOrderStatus(clientId, orderNum, warehouseCd, shipmentPlanId, null);
                        logger.info("Base受注連携 店舗ID:" + clientId + " 受注ID:" + orderNum
                            + " 入金待ちを入金済みに変更");
                    }
                }
            } catch (Exception e) {
                logger.error("Base受注連携(入金待ち)の更新処理が失敗しました。");
                logger.error(BaseException.print(e));
            }
        }
        logger.info("Base受注連携(入金待ち) 終了");
    }

    /**
     * BASE送り状番号自動連携 (一時間ごと 1,16,31,46)
     *
     * @author YuanMingZe
     * @date 2021/07/21
     */
    // @Scheduled(cron = "0 1/15 * * * ?")
    public void processTrackingNo() {
        logger.info("Base伝票連携 開始");
        // BASEのAPI情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> clients = apiService.getAllDataDelivery(API.BASE.getName());
        if (Collections.isEmpty(clients)) {
            logger.info("Base伝票連携 店舗情報：0件");
            logger.info("Base伝票連携 終了");
        }

        // 查询出所有的配送方法
        List<Ms004_delivery> deliveryList = deliveryDao.getDeliveryAll();


        Map<String, Ms004_delivery> deliveryMap = new HashMap<>();
        if (!Collections.isEmpty(deliveryList)) {
            // 将 配送Id作为key 对象作为value
            deliveryMap = deliveryList.stream().collect(Collectors.toMap(Ms004_delivery::getDelivery_cd, o -> o));
        }
        for (Tc203_order_client client : clients) {
            // Baseのトークンをリフレッシュ
            boolean isSucceed = refreshToken(client);
            if (!isSucceed) {
                continue;
            }
            String clientId = client.getClient_id();

            // 获取到该店铺设置别名的 所有配送方法的信息
            List<Ms007_setting> ms007Settings = deliveryDao.getConvertedDataAll(clientId, 1);

            // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
            List<Tw200_shipment> tw200Shipments = shipmentsService.getUntrackedShipments(clientId, API.BASE.getName());
            if (Collections.isEmpty(tw200Shipments)) {
                logger.info("Base伝票連携 店舗ID:{} 伝票件数:0件", clientId);
                continue;
            }
            Map<String, Integer> deliveryCompaniesMap = new HashMap<>();
            try {
                deliveryCompaniesMap = getDeliveryCompanies(client);
            } catch (Exception e) {
                logger.error("Base配送業者情報の一覧を取得失败, 店铺Id={}", client.getClient_id());
                continue;
            }

            for (Tw200_shipment shipment : tw200Shipments) {
                // 伝票番号
                String deliveryTrackingNm = shipment.getDelivery_tracking_nm();
                // 受注番号（BASE採番）
                String uniqueKey = shipment.getOuter_order_no();
                // 連携用URL
                String url = client.getClient_url() + "/1/orders/detail/" + uniqueKey;
                // 商品IDをAPIによる取得する
                String responseStr = HttpClientUtils.sendGet(url, TOKEN_NAME, client.getAccess_token(), null);
                JSONObject responseObj = JSONObject.parseObject(responseStr);
                if (Objects.isNull(responseObj)) {
                    logger.info("Base伝票連携NG 店舗ID:{} 受注番号:{} 原因：BASEから受注明細の取得失敗", clientId, uniqueKey);
                    continue;
                }
                // 受注商品詳細を取得
                JSONObject order = responseObj.getJSONObject("order");
                JSONArray orderItems = order.getJSONArray("order_items");
                if (Collections.isEmpty(orderItems)) {
                    logger.info("Base伝票連携NG 店舗ID:{} 受注番号:{} 原因：注文商品明細の解析失敗", clientId, uniqueKey);
                    continue;
                }

                String deliveryCarrier = shipment.getDelivery_carrier();
                // 获取到配送方法详细信息
                Ms004_delivery delivery = null;
                if (deliveryMap.containsKey(deliveryCarrier)) {
                    delivery = deliveryMap.get(deliveryCarrier);
                }

                List<Ms007_setting> ms007SettingList = new ArrayList<>();
                if (!Collections.isEmpty(ms007Settings) && !StringTools.isNullOrEmpty(deliveryCarrier)) {
                    // 获取到 该配送方法 存在别的所有数据
                    ms007SettingList = ms007Settings.stream()
                        .filter(x -> deliveryCarrier.equals(x.getConverted_id())).collect(Collectors.toList());
                }

                // 受注商品詳細毎で伝票連携リクエストを出す
                for (int index = 0; index < orderItems.size(); index++) {
                    JSONObject orderItem = orderItems.getJSONObject(index);
                    url = client.getClient_url() + "/1/orders/edit_status";
                    HashMap<String, String> map = new HashMap<>();
                    map.put("order_item_id", orderItem.getString("order_item_id"));
                    map.put("status", "dispatched");
                    map.put("tracking_number", deliveryTrackingNm);
                    if (!StringTools.isNullOrEmpty(delivery) && !StringTools.isNullOrEmpty(deliveryCompaniesMap)) {
                        String deliveryNm = delivery.getDelivery_nm();
                        if (deliveryCompaniesMap.containsKey(deliveryNm)) {
                            // 判断 ms004_delivery 表中的 配送方法可以和BASE设定的配送放匹配上
                            Integer deliveryId = deliveryCompaniesMap.get(deliveryNm);
                            map.put("delivery_company_id", String.valueOf(deliveryId));
                        } else {
                            if (!Collections.isEmpty(ms007SettingList)) {
                                // 若004 表未匹配上 需要判断ms007_setting 表中是否存在
                                for (Ms007_setting setting : ms007SettingList) {
                                    String mappingValue = setting.getMapping_value();
                                    if (deliveryCompaniesMap.containsKey(mappingValue)) {
                                        map.put("delivery_company_id",
                                            String.valueOf(deliveryCompaniesMap.get(mappingValue)));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    HttpEntity entity = HttpClientUtils.createHttpEntity(map);
                    responseStr = HttpClientUtils.sendPost(url, entity, TOKEN_NAME, client.getAccess_token(), null);
                    responseObj = JSONObject.parseObject(responseStr);
                    // レスポンスがNULLではない、またerror情報がなければ、下記処理を続く。
                    if (!Objects.isNull(responseObj)) {
                        if (StringTools.isNullOrEmpty(responseObj.getString("error"))) {
                            try {
                                orderDao.updateFinishFlag(shipment.getShipment_plan_id());
                            } catch (Exception e) {
                                logger.error("Base伝票連携NG 店舗ID:{} 伝票連携NG 出庫番号:{} 原因：連携フラグの更新失敗", clientId,
                                    shipment.getShipment_plan_id());
                                logger.error(BaseException.print(e));
                                break;
                            }
                            logger.info("Base伝票連携OK 店舗ID:{} 出庫ID:{}", clientId, shipment.getShipment_plan_id());
                        } else {
                            logger.error("Base伝票連携NG 店舗ID:{} 伝票連携NG 出庫ID:{} 原因：{}", clientId,
                                shipment.getShipment_plan_id(), responseObj.getString("error_description"));
                            break;
                        }
                    } else {
                        logger.error("Base伝票連携NG 店舗ID:{} 原因：HTTP通信失敗", clientId);
                        break;
                    }
                }
            }
        }
        logger.info("Base伝票連携 終了");
    }

    /**
     * @param orderClient : api设定信息
     * @description: 配送業者情報の一覧を取得失败,
     * @return: java.util.Map<java.lang.Integer, java.lang.String>
     * @author: GaoJiaBao
     * @date: 2021/9/8 15:36
     */
    public Map<String, Integer> getDeliveryCompanies(Tc203_order_client orderClient) {
        Map<String, Integer> resultMap = new HashMap<>();
        String url = orderClient.getClient_url() + "/1/delivery_companies";
        String responseStr = HttpClientUtils.sendGet(url, TOKEN_NAME, orderClient.getAccess_token(), null);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        if (Objects.isNull(responseObj)) {
            logger.info("Base配送業者情報の一覧を取得失败, 店铺Id={}", orderClient.getClient_id());
            return null;
        }

        JSONArray deliveryCompanies = responseObj.getJSONArray("delivery_companies");
        for (int i = 0; i < deliveryCompanies.size(); i++) {
            JSONObject json = deliveryCompanies.getJSONObject(i);
            Integer deliveryCompanyId = json.getInteger("delivery_company_id");
            String name = json.getString("name");
            resultMap.put(name, deliveryCompanyId);
        }
        return resultMap;
    }

    /**
     * BASE在庫自動連携 (20分ごと自動起動 1, 21, 41)
     *
     * @author GaoJiaBao
     * @date 2021/1/12
     */
    // @Scheduled(cron = "30 1/20 * * * ?")
    public void updateStock() {
        logger.info("Base在庫連携 開始");
        HashMap<String, Object> hashMap = getStockMap(apiService, productRenkeiDao, stockDao, API.BASE.getName());
        if (StringTools.isNullOrEmpty(hashMap)) {
            logger.info("Base在庫連携 在庫情報:0件");
            logger.info("Base在庫連携 終了");
            return;
        }
        HashMap<String, Mc106_produce_renkei> renkeiMap = (HashMap<String, Mc106_produce_renkei>) hashMap.get("renkei");
        HashMap<String, Integer> stockMap = (HashMap<String, Integer>) hashMap.get("stock");
        // 遍历mc106 map
        for (Map.Entry<String, Mc106_produce_renkei> map : renkeiMap.entrySet()) {
            String key = map.getKey();
            Mc106_produce_renkei produceRenkei = map.getValue();
            // 理论在库数
            Integer available_cnt = stockMap.get(key);
            String client_url = produceRenkei.getClient_url();
            String url = client_url + "/1/items/edit_stock";
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("item_id", produceRenkei.getRenkei_product_id());
            paramMap.put("stock", String.valueOf(available_cnt));
            try {
                HttpUtils.sendHttpsPost(url, paramMap, TOKEN_NAME, produceRenkei.getAccess_token());
                // TODO 在庫数更新成功したら、店舗在庫数を更新する必要 ※実装必要
                // String res = HttpUtils.sendJsonPost(url3.toString(), param, token);
                // if (!StringTools.isNullOrEmpty(res)) {
                // logger.info("Base在庫連携OK 店舗ID:{} 商品ID:{} 在庫数:{}" + clientId , productId , nums);
                // // 連携した在庫数を在庫TBLの店舗数を反映
                // productDao.updateStockStroeCnt(clientId, productId, productCnt.getAvailable_cnt(),
                // "shopify", CommonUtil.getDate());
                // cuts++;
                // } else {
                // logger.warn("shopify在庫連携OK 店舗ID:{} 商品ID:{} 在庫数:{}" + clientId , productId , nums);;
                // errs++;
                // }
                logger.error("Base在庫連携OK 店舗ID:{} 商品ID:{} 原因:在庫数の更新失敗", produceRenkei.getClient_id(), key);
            } catch (Exception e) {
                logger.error("Base在庫連携NG 店舗ID:{} 商品ID:{} 原因:在庫数の更新失敗", produceRenkei.getClient_id(), key);
                logger.error(BaseException.print(e));
            }
        }
        logger.info("Base在庫連携 終了");
    }

    /**
     * トークンをリフレッシュ
     *
     * @param data api連携情報
     * @author GaoJiaBao
     * @date 2020/12/17
     */
    private boolean refreshToken(Tc203_order_client data) {
        String url = data.getClient_url() + "/1/oauth/token";
        HashMap<String, String> map = new HashMap<>();
        map.put("grant_type", "refresh_token");
        map.put("client_id", data.getApi_key());
        map.put("client_secret", data.getPassword());
        map.put("refresh_token", data.getRefresh_token());
        map.put("redirect_uri", baseUrl);
        HttpEntity entity = HttpClientUtils.createHttpEntity(map);
        String responseStr = HttpClientUtils.sendPost(url, entity, TOKEN_NAME, data.getAccess_token(), null);
        JSONObject responseObj = JSONObject.parseObject(responseStr);
        if (Objects.isNull(responseObj)) {
            logger.info("BaseAPI連携 店舗ID:{} 原因:認証アクセスへの接続失敗", data.getClient_id());
            return false;
        }
        String access_token = responseObj.getString("token_type") + " " + responseObj.getString("access_token");
        String refresh_token = responseObj.getString("refresh_token");
        try {
            orderApiDao.updateToken(access_token, refresh_token, data.getId(), data.getClient_id());
        } catch (Exception e) {
            logger.error("BaseAPI連携 店舗ID:{} 原因:認証トークンの更新失敗", data.getClient_id());
            logger.error(BaseException.print(e));
            return false;
        }
        return true;
    }

    /**
     * 受注データをTc200_orderテーブルに挿入する
     *
     * @param order 受注データ
     * @param init 店舗情報
     * @author GaoJiaBao
     * @date 2020/12/16
     */
    private void setTc200Json(JSONObject order, InitClientInfoBean init) {
        // 初期化
        Tc200_order tc200Order = new Tc200_order();
        // 店舗ID
        String clientId = init.getClientId();
        // API番号
        Integer apiId = init.getApiId();
        // 倉庫番号
        String warehouseCd = init.getWarehouseCd();
        // 受注番号枝番
        Integer subNo = init.getSubNo();
        // 識別番号
        String identification = init.getIdentification();
        // 履歴番号
        Integer historyId = init.getHistoryId();
        // ディフォルト配送方法
        String defaultDeliveryMethod = init.getDefaultDeliveryMethod();
        // 配送情報マスタ
        Map<String, String> ms007SettingTimeMap = init.getMs007SettingTimeMap();
        Map<String, String> ms007SettingPaymentMap = init.getMs007SettingPaymentMap();
        Map<String, String> ms007SettingDeliveryMethodMap = init.getMs007SettingDeliveryMethodMap();
        // 自動出庫(1:自動出庫 0:出庫しない)
        Integer shipmentStatus = init.getShipmentStatus();
        // 受注番号（SunLogi採番 BS001-YYYYMMDDHHMM-00001）
        String purchaseOrderNo = CommonUtils.getOrderNo(subNo, identification);
        // 受注番号（BASE採番）
        String outerOrderNo = order.getString("unique_key");
        // 注文種別 (0:入金待ち 1:入金済み)
        Integer orderType = order.getInteger("com_orderType");
        // **********************************************************
        // ********************* 受注明細情報 *********************
        // **********************************************************
        // 受注明細情報を保存する
        int product_price_excluding_tax;
        try {
            product_price_excluding_tax = getTc201DetailData(order, clientId, purchaseOrderNo, apiId);
        } catch (Exception e) {
            logger.error("Base受注連携NG 店舗ID:{} 受注ID:{} 原因:受注明細TBLの登録失敗", clientId, purchaseOrderNo);
            return;
        }
        // 商品合計金額
        tc200Order.setProduct_price_excluding_tax(product_price_excluding_tax);
        // 受注番号
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(outerOrderNo);
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 注文種別(0:入金待ち 1:入金済み) BASE受注連携
        tc200Order.setOrder_type(orderType);
        // 外部注文ステータス
        tc200Order.setOuter_order_status(0);
        // 个口数
        tc200Order.setBoxes(1);
        // 必要字段写入: 配送先郵便番号1, 取込日時, 配送先住所郡市区, 配送先住所都道府県, 配送先姓
        // 注文日時 *****************
        long orderDatetime = order.getTimestamp("ordered").getTime();
        if (orderDatetime > 0) {
            Timestamp timestamp = new Timestamp(orderDatetime * 1000);
            tc200Order.setOrder_datetime(timestamp);
        }
        // 取込日時
        tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
        // 配達希望日
        String deliveryDate = order.getString("delivery_date");
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            Date date = DateUtils.stringToDate(deliveryDate);
            tc200Order.setDelivery_date(date);
        }
        // 合計請求金額
        String total = order.getString("total");
        if (!StringTools.isNullOrEmpty(total)) {
            tc200Order.setBilling_total(Integer.valueOf(total));
        }

        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
        Ms012_sponsor_master ms012spons = init.getMs012sponsor();
        // 注文者ID(依頼主ID 及び明細書メッセージ)
        String detailDandled = "同梱しない";
        String priceOn = "0";
        // 是否以注文者为依頼主(0：依頼主 1:注文者)
        int orderFlag = 0;
        if (!StringTools.isNullOrEmpty(ms012spons)) {
            tc200Order.setSponsor_id(ms012spons.getSponsor_id());
            tc200Order.setDetail_message(ms012spons.getDetail_message());
            // 明細同梱設定(1:同梱する 0:同梱しない)
            if ("1".equals(String.valueOf(ms012spons.getDelivery_note_type()))) {
                detailDandled = "同梱する";
            }
            if ("1".equals(String.valueOf(ms012spons.getPrice_on_delivery_note()))) {
                priceOn = "1";
            }
        } else {
            // 是否以注文者为依頼主(0:依頼マスタ 1:注文者)
            orderFlag = 1;
        }
        // 是否以注文者为依頼主
        tc200Order.setDetail_bundled(detailDandled);
        tc200Order.setOrder_flag(orderFlag);
        // 明細書金額印字
        tc200Order.setDetail_price_print(priceOn);

        // 削除フラグ
        tc200Order.setDel_flg(0);
        // 注文者郵便番号
        tc200Order.setOrder_zip_code1("000");
        tc200Order.setOrder_zip_code2("0000");
        List<String> zipList = CommonUtils.checkZipToList(order.getString("zip_code"));
        if (!Collections.isEmpty(zipList)) {
            tc200Order.setOrder_zip_code1(zipList.get(0));
            tc200Order.setOrder_zip_code2(zipList.get(1));
        }
        // 注文者住所都道府県
        tc200Order.setOrder_todoufuken(order.getString("prefecture"));
        // 注文者住所郡市区
        tc200Order.setOrder_address1(order.getString("address"));
        // 注文者詳細住所
        tc200Order.setOrder_address2(order.getString("address2"));
        // 注文者姓
        tc200Order.setOrder_family_name(order.getString("last_name"));
        // 注文者名
        tc200Order.setOrder_first_name(order.getString("first_name"));
        // 注文者電話番号
        List<String> orderTelList = CommonUtils.checkPhoneToList(order.getString("tel"));
        if (!Collections.isEmpty(orderTelList)) {
            // 注文者電話番号1
            tc200Order.setOrder_phone_number1(orderTelList.get(0));
            // 注文者電話番号2
            tc200Order.setOrder_phone_number2(orderTelList.get(1));
            // 注文者電話番号3
            tc200Order.setOrder_phone_number3(orderTelList.get(2));
        }
        // 注文者メールアドレス
        tc200Order.setOrder_mail(order.getString("mail_address"));
        // 配送方法名（BASE定義）
        String shippingMethod = order.getString("shipping_method");
        tc200Order.setBikou9(shippingMethod);
        // 先判断该配送方法的代号是否存在
        if (deliveryMethodMap.containsKey(shippingMethod)) {
            shippingMethod = deliveryMethodMap.get(shippingMethod);
        }
        // 配送方法
        Ms004_delivery ms004Delivery =
            apiCommonUtils.getDeliveryMethod(shippingMethod, defaultDeliveryMethod, ms007SettingDeliveryMethodMap);
        if (!Objects.isNull(ms004Delivery)) {
            // 配送会社
            tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
            // 配送方法
            tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
            // 配送業者
            shippingMethod = ms004Delivery.getDelivery_nm();
        }

        // 配達希望時間帯（BASE定義）
        String deliveryTimeZone = order.getString("delivery_time_zone");
        if (!StringTools.isNullOrEmpty(deliveryTimeZone)) {
            // 配達希望時間帯（SunLogi定義）
            String deliveryTimeSlot = apiCommonUtils.getDeliveryTimeSlot(shippingMethod, deliveryTimeZone,
                API.BASE.getName(), ms007SettingTimeMap);
            tc200Order.setDelivery_time_slot(deliveryTimeSlot);
        }
        // 支付方法（BASE定義）
        String payment = order.getString("payment");
        tc200Order.setBikou10(payment);
        // 支付方法（SunLogi定義）
        String paymentMethod = apiCommonUtils.getPaymentMethod(payment, API.BASE.getName(), ms007SettingPaymentMap);
        tc200Order.setPayment_method(paymentMethod);
        // 送料合計
        tc200Order.setDelivery_total(order.getInteger("shipping_fee"));
        // 調整金額
        Integer adjusted_amount = order.getJSONObject("order_amount_adjustment").getInteger("adjusted_amount");
        // 代引き手数料
        Integer cod_fee = order.getInteger("cod_fee");
        // 手数料 ( 代引料手数料+調整金額 )
        tc200Order.setHandling_charge(adjusted_amount + cod_fee);
        // 代引引換総額(合計金額)
        if ("cod".equals(payment) && !StringTools.isNullOrEmpty(total)) {
            tc200Order.setCash_on_delivery_fee(Integer.valueOf(total));
        } else {
            tc200Order.setCash_on_delivery_fee(0);
        }

        // 備考
        String remark = order.getString("remark");
        if (!Objects.isNull(remark)) {
            tc200Order.setMemo(remark);
        }

        // 配送先情報設定
        JSONObject order_receiver = order.getJSONObject("order_receiver");
        String toJSONString = order_receiver.toJSONString();
        if ("{}".equals(toJSONString)) {
            // 配送先と注文先同じ場合
            tc200Order.setReceiver_zip_code1(tc200Order.getOrder_zip_code1());
            tc200Order.setReceiver_zip_code2(tc200Order.getOrder_zip_code2());
            tc200Order.setReceiver_address1(tc200Order.getOrder_address1());
            tc200Order.setReceiver_address2(tc200Order.getOrder_address2());
            tc200Order.setReceiver_todoufuken(tc200Order.getOrder_todoufuken());
            tc200Order.setReceiver_family_name(tc200Order.getOrder_family_name());
            tc200Order.setReceiver_first_name(tc200Order.getOrder_first_name());
            tc200Order.setReceiver_phone_number1(tc200Order.getOrder_phone_number1());
            tc200Order.setReceiver_phone_number2(tc200Order.getOrder_phone_number2());
            tc200Order.setReceiver_phone_number3(tc200Order.getOrder_phone_number3());
            tc200Order.setReceiver_mail(tc200Order.getOrder_mail());
        } else {
            // 配送先郵便番号
            tc200Order.setReceiver_zip_code1("000");
            tc200Order.setReceiver_zip_code2("0000");
            List<String> receiverZipList = CommonUtils.checkZipToList(order_receiver.getString("zip_code"));
            if (!Collections.isEmpty(receiverZipList)) {
                // 配送先郵便番号1
                tc200Order.setReceiver_zip_code1(receiverZipList.get(0));
                // 配送先郵便番号2
                tc200Order.setReceiver_zip_code2(receiverZipList.get(1));
            }
            // 配送先住所郡市区
            tc200Order.setReceiver_address1(order_receiver.getString("address"));
            // 配送先詳細住所
            tc200Order.setReceiver_address2(order_receiver.getString("address2"));
            // 配送先住所都道府県
            tc200Order.setReceiver_todoufuken(order_receiver.getString("prefecture"));
            // 配送先姓
            tc200Order.setReceiver_family_name(order_receiver.getString("last_name"));
            // 配送先名
            tc200Order.setReceiver_first_name(order_receiver.getString("first_name"));
            // 配送先電話番号
            List<String> receiverTelList = CommonUtils.checkPhoneToList(order_receiver.getString("tel"));
            if (!Collections.isEmpty(receiverTelList)) {
                // 配送先電話番号1
                tc200Order.setReceiver_phone_number1(receiverTelList.get(0));
                // 配送先電話番号2
                tc200Order.setReceiver_phone_number2(receiverTelList.get(1));
                // 配送先電話番号3
                tc200Order.setReceiver_phone_number3(receiverTelList.get(2));
            }
        }
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        try {
            orderDao.insertOrder(tc200Order);
        } catch (Exception e) {
            logger.error("Base受注連携NG 店舗ID:{} 受注ID:{} 原因:受注管理TBLの登録失敗", clientId, outerOrderNo);
            logger.error(BaseException.print(e));
            // 受注管理レコード削除
            orderDao.orderDelete(clientId, purchaseOrderNo);
            // 受注明細レコード削除
            orderDetailDao.orderDetailDelete(purchaseOrderNo);
            return;
        }
        // 出庫依頼（1:依頼する 0:依頼しない）
        if (shipmentStatus == 1) {
            apiCommonUtils.processShipment(purchaseOrderNo, clientId);
        }
    }

    /**
     * 受注明細情報を保存する
     *
     * @param order 受注信息
     * @param clientId 店铺信息
     * @param orderNo 受注番号
     * @param apiId API番号
     * @return int
     * @author GaoJiaBao
     * @date 2020/12/16
     */
    private int getTc201DetailData(JSONObject order, String clientId, String orderNo, Integer apiId) {
        int product_price_excluding_tax = 0;
        Tc201_order_detail tc201OrderDetail;
        JSONArray jsonArray = order.getJSONArray("order_items");
        Integer subNo = 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            tc201OrderDetail = new Tc201_order_detail();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 商品合計金額を取得
            product_price_excluding_tax += jsonObject.getInteger("item_total");
            // 根据code查询该商品以前是否存在
            // 如果同一种商品 商品颜色不同 可能得根据variation_id 来区别
            // 商品コード
            String code = jsonObject.getString("item_id");
            // item_identifier - 商品コード
            if (!StringTools.isNullOrEmpty(jsonObject.getString("item_identifier"))) {
                code = jsonObject.getString("item_identifier");
            }
            // variation_identifier オプションコード
            if (!StringTools.isNullOrEmpty(jsonObject.getString("variation_identifier"))) {
                code = jsonObject.getString("variation_identifier");
            }
            // 商品オプション値
            String options = "";
            if (!StringTools.isNullOrEmpty(jsonObject.getString("variation"))) {
                options = jsonObject.getString("variation");
            }
            // 商品名称
            String name = jsonObject.getString("title");
            // 商品単価
            Integer price = jsonObject.getInteger("price");
            // 商品数量
            Integer amount = jsonObject.getInteger("amount");
            // 税区分(1:標準税率、2:軽減税率)
            String itemTaxType = jsonObject.getString("item_tax_type");
            // 税区分(0：税込 1:税抜)
            int isReducedTax = 0;
            if ("2".equals(itemTaxType)) {
                isReducedTax = 1;
            }
            // EC店舗側の商品ID
            String renkeiProductId = jsonObject.getString("item_id");
            // **********************************************************
            // ********************* 共通パラメータ整理 *********************
            // **********************************************************
            Mc100_product mc100Product;
            ProductBean productBean = new ProductBean();
            productBean.setClientId(clientId);
            productBean.setApiId(apiId);
            productBean.setCode(code);
            productBean.setName(name);
            productBean.setPrice(String.valueOf(price));
            productBean.setIsReducedTax(isReducedTax);
            productBean.setRenkeiPid(renkeiProductId);
            // Mc100_productテーブルの既存商品をマッピング
            mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
            // 商品登録されていない場合、商品マスタに仮商品として新規登録
            if (Objects.isNull(mc100Product)) {
                // 商品新規登録
                mc100Product = apiCommonUtils.insertMc100Product(productBean, API.BASE.getName());
                // 商品之前不存在 设定为仮登録
                tc201OrderDetail.setProduct_kubun(9);
            }
            // 外部商品連携管理TBL
            if (!Objects.isNull(mc100Product)) {
                apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product, API.BASE.getName());
            }
            if (Objects.nonNull(mc100Product)) {
                // 商品ID
                tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
                // 商品コード
                code = mc100Product.getCode();
                // セットID
                if (mc100Product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                    tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
                }
                // 同梱物(0:なし 1:同梱物)
                if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg()) && mc100Product.getBundled_flg() == 1) {
                    tc201OrderDetail.setBundled_flg(mc100Product.getBundled_flg());
                } else {
                    tc201OrderDetail.setBundled_flg(0);
                }
                // 仮登録 默认为0
                int kubun = 0;
                Integer productKubun = mc100Product.getKubun();
                if (!StringTools.isNullOrEmpty(productKubun)) {
                    kubun = productKubun;
                }
                tc201OrderDetail.setProduct_kubun(kubun);
            } else {
                logger.error("Base受注連携NG 店舗ID:{} 受注ID:{} 原因:商品IDの取得失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                throw new BaseException(ErrorCode.E_11005);
            }
            // 商品オプション値を保管
            tc201OrderDetail.setProduct_option(options);
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // 商品名 (受注の商品名をそのままに保管)
            tc201OrderDetail.setProduct_name(name);
            // 単価
            tc201OrderDetail.setUnit_price(price);
            // 個数
            tc201OrderDetail.setNumber(amount);
            // 商品計
            int total_price = tc201OrderDetail.getUnit_price() * tc201OrderDetail.getNumber();
            tc201OrderDetail.setProduct_total_price(total_price);
            // 受注明細番号
            tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
            tc201OrderDetail.setPurchase_order_no(orderNo);
            // 軽減税率適用商品
            tc201OrderDetail.setIs_reduced_tax(isReducedTax);
            // 削除フラグ
            tc201OrderDetail.setDel_flg(0);
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
                subNo++;
            } catch (Exception e) {
                subNo++;
                logger.error("Base受注連携NG 店舗ID:{} 受注ID:{} 原因:受注明細TBLの登録失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
            }
        }
        return product_price_excluding_tax;
    }
}
