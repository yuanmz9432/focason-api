package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.ProductRenkeiDao;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.OrderService;
import com.lemonico.store.service.ShipmentsService;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Yahoo店舗から情報取得(JSON形式)
 *
 * @author HZM
 * @className: YahooAPI
 * @date 2021/6/1 9:27
 **/
@Component
public class YahooAPI
{

    private final static Logger logger = LoggerFactory.getLogger(YahooAPI.class);
    private final static String TOKEN_NAME = "Authorization";

    @Resource
    private OrderApiDao orderApiDao;
    @Resource
    private OrderApiService apiService;
    @Resource
    private ClientDao clientDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
    @Resource
    private ProductSettingService productSettingService;
    @Resource
    private SponsorDao sponsorDao;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderHistoryDao orderHistoryDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private ProductRenkeiDao productRenkeiDao;
    @Resource
    private OrderService orderService;
    @Resource
    private APICommonUtils apiCommonUtils;
    @Resource
    private ShipmentsService shipmentsService;

    /**
     * JSONをXMLへ変換する
     *
     * @param jsonObj 変換対象JSON
     * @return XML文字列
     */
    private static String jsonToXml(JSONObject jsonObj) {
        StringBuilder buff = new StringBuilder();
        JSONObject tempObj;
        JSONArray tempArr;
        for (String temp : jsonObj.keySet()) {
            buff.append("<").append(temp.trim()).append(">");
            jsonObj.get(temp);
            if (jsonObj.get(temp) instanceof JSONObject) {
                tempObj = (JSONObject) jsonObj.get(temp);
                buff.append(jsonToXml(tempObj));
            } else if (jsonObj.get(temp) instanceof JSONArray) {
                tempArr = (JSONArray) jsonObj.get(temp);
                if (tempArr.size() > 0) {
                    for (int i = 0; i < tempArr.size(); i++) {
                        tempObj = (JSONObject) tempArr.get(0);
                        buff.append(jsonToXml(tempObj));
                    }
                }
            } else {
                String tempStr = jsonObj.get(temp).toString();
                buff.append(tempStr.trim());
            }
            buff.append("</").append(temp.trim()).append(">");
        }
        return buff.toString();
    }

    /**
     * Yahoo受注自動取込 (10分ごと自動起動 6,16,26,36,46,56)
     *
     * @author HZM
     * @date 2020/9/9
     */
    // @Scheduled(cron = "0 8/10 * * * ?")
    public void fetchYahooOrders() {
        logger.info("yahoo受注連携 開始");
        // 获取所有和Yahoo连携的信息
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.YAHOO.getName());
        // Yahooに関するAPI情報が存在しない場合、処理中止
        if (io.jsonwebtoken.lang.Collections.isEmpty(allData)) {
            logger.info("Yahoo受注連携 店舗情報：0件");
            logger.info("Yahoo受注連携 終了");
            return;
        }
        Hashtable<String, List<String>> errHashtable = new Hashtable<>();
        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        apiCommonUtils.initialize();
        for (Tc203_order_client data : allData) {
            if (!"SU003".equals(data.getClient_id())) {
                continue;
            }

            // 保存错误数据
            ArrayList<String> errList = new ArrayList<>();

            // 获取店铺的详细信息
            InitClientInfoBean initClientInfoBean = apiCommonUtils.initClientCommon(data);
            Ms201_client client = clientDao.getClientDetailInfo(data.getClient_id());
            String fileName = data.getBikou1();
            String tokenPasswd = data.getBikou2();
            String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
            String client_id = client.getClient_id();
            String client_url = data.getClient_url();
            String[] urlParts = client_url.split("/");
            String sellerId = urlParts[3];
            Integer historyId = CommonUtils.getMaxHistoryId(lastOrderHistoryNo);
            List<String> warehouseIdListByClientId = orderDao.getWarehouseIdListByClientId(client_id);
            JSONArray orders = new JSONArray();
            /* 设置要获取的订单状态以及固定参数 */
            JSONObject Req = new JSONObject();
            JSONObject Req2 = new JSONObject();
            JSONObject Search = new JSONObject();
            JSONObject Condition = new JSONObject();
            // 设置读取的订单状态
            Req2.put("SellerId", sellerId);
            Search.put("Field", "OrderId,PayStatus,PayMethod");
            // Search.put("", "OrderTime");
            // Condition.put("OrderTimeFrom", "20210601000000");
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String timeTo = today + "235959";
            Condition.put("OrderTimeTo", timeTo);
            // 1 : 予約中 2 : 処理中 3 : 保留 4 : キャンセル 5 : 完了
            Condition.put("OrderStatus", "2");
            // 最大件数
            Search.put("result", 2000);
            Search.put("Condition", Condition);
            Req2.put("Search", Search);
            Req.put("Req", Req2);

            String param = jsonToXml(Req);
            try {
                String token = data.getAccess_token();
                final String ORDER_URL = "https://circus.shopping.yahooapis.jp/ShoppingWebService/V1/orderList";
                JSONObject response =
                    HttpUtils.sendXmlPost(ORDER_URL, param, TOKEN_NAME, token, fileName, tokenPasswd, errList);
                if (Objects.isNull(response)) {
                    logger.error("Yahoo店舗ID【{}】受注情報取得失敗。", client_id);
                    errList.add("受注情報取得失敗。");
                    errOrderClients.add(data);
                    continue;
                }
                // 取込件数
                int total =
                    Integer.parseInt(response.getJSONObject("Result").getJSONObject("Search").getString("TotalCount"));
                int cnt =
                    Integer.parseInt(response.getJSONObject("Result").getJSONObject("Search").getString("TotalCount"));
                // 成功件数
                int successCnt = 0;
                // 失敗件数
                int failureCnt = 0;
                // 受注番号の枝番
                int subno = 1;
                if (total == 1) {
                    JSONObject order =
                        response.getJSONObject("Result").getJSONObject("Search").getJSONObject("OrderInfo");
                    orders.add(order);
                } else {
                    orders = response.getJSONObject("Result").getJSONObject("Search").getJSONArray("OrderInfo");
                }
                for (int i = 0; i < cnt; i++) {
                    JSONObject order = orders.getJSONObject(i);
                    int orderType = 1;
                    String outerOrderNo = order.getString("OrderId");
                    String payStatus = order.getString("PayStatus");// 0:入金待ち1:入金済み
                    String payment_name = order.getString("PayMethod");// payment_d1：商品代引
                    if ("1".equals(payStatus)) {
                        List<Tc200_order> tc200 = orderDao.getOrderType(outerOrderNo, client_id);
                        if (tc200 != null && tc200.size() > 0) {
                            Integer type = tc200.get(0).getOrder_type();
                            if (type != null && type == 0) {
                                orderService.upOrderStatus(client_id, outerOrderNo, warehouseIdListByClientId.get(0),
                                    tc200.get(0).getShipment_plan_id(), null);
                                logger.info("yahoo受注連携 店舗ID:" + client_id + " 受注ID:" + outerOrderNo
                                    + " 入金待ちを入金済みに変更");
                                continue;
                            }
                        }
                        // 代引きで取り込んだ場合「入金待ち」ではなく
                    } else if ("payment_d1".equals(payment_name)) {
                        orderType = 1;
                    } else {
                        orderType = 0;
                    }
                    Integer outerOrderCnt = orderDao.getOuterOrderNo(outerOrderNo, client_id);
                    if (!(outerOrderCnt == 0)) {
                        logger.warn("yahoo受注連携 店舗ID:" + client_id + " 受注ID:" + outerOrderNo + " 原因:過去受注取込済");
                        total--;
                    } else {
                        initClientInfoBean.setSubNo(subno);
                        List<String> res =
                            setTc200Json(order, token, orderType, fileName, tokenPasswd, sellerId, initClientInfoBean);
                        subno++;
                        // 成功かどうか
                        if (res.size() > 0) {
                            failureCnt++;// 失敗
                            logger.warn(
                                "yahoo受注連携NG" + " 店舗ID:" + client_id + " 受注ID:" + outerOrderNo + " 原因:受注データ不正");
                            apiCommonUtils.insertTc207OrderError(client_id, historyId, outerOrderNo, res.get(0),
                                API.YAHOO.getName());
                            errList.add("受注データ不正");
                            errOrderClients.add(data);
                        } else {
                            successCnt++;// 成功
                            logger.info("yahoo受注連携OK 店舗ID:" + client_id + " 受注ID:" + outerOrderNo);
                        }
                    }
                }

                // 取り込まれていない場合、取込履歴を記録しない
                if (successCnt > 0) {
                    logger.info("yahoo受注連携 店舗ID:" + client_id + " 成功件数:" + successCnt + " 失敗件数:" + failureCnt);
                    // 生成した受注履歴beanに情報を格納
                    Tc202_order_history order_history = new Tc202_order_history();
                    order_history.setHistory_id(historyId);
                    Date nowTime = DateUtils.getDate();
                    order_history.setImport_datetime(nowTime);
                    order_history.setClient_id(client_id);
                    order_history.setTotal_cnt(total);// 取込件数
                    order_history.setSuccess_cnt(successCnt);// 成功件数
                    order_history.setFailure_cnt(failureCnt);// 失敗件数
                    order_history.setBiko01("yahoo受注連携");
                    orderHistoryDao.insertOrderHistory(order_history);
                }
            } catch (Exception e) {
                logger.error("店舗：" + client_id + "のYAHOOオーダーAPI：" + data.getApi_name() + "エラーが発生しました。！");
                logger.error(BaseException.print(e));
                errList.add("エラーが発生しました。！");
                errOrderClients.add(data);
            }
            if (!errList.isEmpty()) {
                String key = client_id + "_API名：" + data.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        logger.info("yahoo受注連携 終了");
        // mailTools.sendErrorMessage(errHashtable, API.YAHOO.getName());
        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 伝票番号自動連携のAPI叩く処理
     * Yahooの出荷ステータス変更API、具体的な情報を下記URLのドキュメントを参考してください。
     * https://developer.yahoo.co.jp/webapi/shopping/orderShipStatusChange.html
     *
     * @param apiUrl リクエスト先
     * @param param リクエストボディ
     * @param token アクセストークン
     * @return Yahoo出荷ステータス変更APIのレスポンス
     */
    private static JSONObject sendXmlPost(final String apiUrl, final String param, final String token) {
        URL url;
        HttpsURLConnection con;
        BufferedReader br;
        String line;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(apiUrl);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            con.setRequestProperty(TOKEN_NAME, token);
            con.connect();
            PrintWriter pw = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8)));
            pw.print(param);
            pw.close();
            br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            result =
                new StringBuilder(result.toString().replace("<![CDATA[", "").replace("]]>", "").replace("&", "&amp;"));
            org.json.JSONObject xmlJSONObj = XML.toJSONObject(result.toString());
            result = new StringBuilder(xmlJSONObj.toString(4));
            br.close();
            con.disconnect();
            /*
             * 利用制限があるため、下記対応を追加します。
             * ※短い時間の間に同一URLに大量にアクセスを行った場合、一定時間利用できなくなることもございます。（1クエリー/秒）
             * 1日50,000リクエストを超える場合は、アプリケーションIDを追加してご対応ください。
             */
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            logger.error(
                "Yahoo Shipping Status Update API was occurred an InterruptedException Exception, please check here. {}",
                param);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(
                "Yahoo Shipping Status Update API was occurred an IOException Exception, please check here. {}", param);
        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(
                "Yahoo Shipping Status Update API was occurred an JSONException Exception, please check here. {}",
                param);
        }
        return JSONObject.parseObject(result.toString());
    }

    /**
     * 送り状番号の自動連携 (毎日18時)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // @Scheduled(cron = "0 7/15 * * * ?")
    public void sendYahooTrackingNm() {
        logger.info("yahoo伝票連携 開始");
        // yahoo情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> allData = apiService.getAllDataDelivery(API.YAHOO.getName());
        int cnts = 0;
        int errs = 0;
        String apiUrl = "https://circus.shopping.yahooapis.jp/ShoppingWebService/V1/orderShipStatusChange";
        //
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client allDatum : allData) {
                // 店舗API情報
                String clientId = allDatum.getClient_id();
                String clientUrl = allDatum.getClient_url();
                String[] urlParts = clientUrl.split("/");
                String sellerId = urlParts[3];
                String token = allDatum.getAccess_token();

                // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
                List<Tw200_shipment> getShipmentYahooList =
                    shipmentsService.getUntrackedShipments(clientId, API.YAHOO.getName());
                String sendUrl = "";
                Map<String, Integer> maps = new HashMap<>();
                maps.put("ヤマト運輸", 1001);
                maps.put("佐川急便", 1002);
                maps.put("日本郵便", 1003);
                maps.put("福山通運", 1006);

                // 店舗設定の全て情報
                for (Tw200_shipment tw200_shipment : getShipmentYahooList) {
                    StringBuilder param = new StringBuilder();
                    param.append("<Req><Target><OrderId>");
                    // 初期化
                    String order_no = tw200_shipment.getOrder_no();
                    String tracking_nms = tw200_shipment.getDelivery_tracking_nm();
                    String delivery_nm = tw200_shipment.getDelivery_nm();
                    switch (delivery_nm) {
                        case "ヤマト運輸":
                            sendUrl =
                                "https://jizen.kuronekoyamato.co.jp/jizen/servlet/crjz.b.NQ0010?id=" + tracking_nms;
                            break;
                        case "佐川急便":
                            sendUrl = "https://k2k.sagawa-exp.co.jp/p/web/okurijosearch.do?okurijoNo=" + tracking_nms;
                            break;
                        case "日本郵便":
                            sendUrl =
                                "https://trackings.post.japanpost.jp/services/srv/search/direct?org.apache.struts.taglib.html.TOKEN=&searchKind=S002&locale=ja&SVID=&reqCodeNo1="
                                    + tracking_nms;
                            break;
                        case "福山通運":
                            sendUrl = "https://corp.fukutsu.co.jp/situation/tracking_no_hunt";
                            break;
                    }
                    param.append(order_no)
                        .append(
                            "</OrderId><IsPointFix>true</IsPointFix></Target><Order><Ship><ShipStatus>3</ShipStatus>")
                        .append("<ShipCompanyCode>").append(maps.get(delivery_nm)).append("</ShipCompanyCode>")
                        .append("<ShipInvoiceNumber1>").append(tracking_nms).append("</ShipInvoiceNumber1>")
                        .append("<ShipUrl><![CDATA[").append(sendUrl).append("]]></ShipUrl>")
                        .append("</Ship></Order><SellerId>").append(sellerId).append("</SellerId></Req>");
                    try {
                        JSONObject res = sendXmlPost(apiUrl, param.toString(), token);
                        // 更新が成功した場合、API連携を連携済に変更
                        if (!StringTools.isNullOrEmpty(res) && "OK"
                            .equals(res.getJSONObject("ResultSet").getJSONObject("Result").getString("Status"))) {
                            shipmentsService.setShipmentFinishFlg(allDatum.getClient_id(),
                                tw200_shipment.getWarehouse_cd(),
                                tw200_shipment.getShipment_plan_id());
                            logger.info("yahoo伝票連携OK" + " 店舗ID:" + clientId + " 受注ID:"
                                + tw200_shipment.getOrder_no() + " 伝票ID:"
                                + tw200_shipment.getDelivery_tracking_nm());
                            cnts++;
                        } else {
                            logger.warn("yahoo伝票連携NG 店舗ID:" + clientId + " API:" + apiUrl + "(返却値無)");
                        }
                    } catch (Exception e) {
                        errs++;
                        logger.error("yahoo伝票連携NG 店舗ID:" + clientId + " API:" + apiUrl + "");
                        logger.error(BaseException.print(e));
                    }
                }
            }
        }

        logger.info("yahoo伝票連携 終了--連携件数(OK:" + cnts + " NG:" + errs + ")");
    }

    /**
     * 在庫数自動連携 (10分ごと自動起動 8,18,28,38,48,58)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // @Scheduled(cron = "30 7/20 * * * ?")
    public void setYahooProductCnt() {
        logger.info("yahoo在庫連携 開始");
        String apiUrl = "https://circus.shopping.yahooapis.jp/ShoppingWebService/V1/setStock";
        List<Tc203_order_client> allApiData = apiService.getAllDataStock(API.YAHOO.getName());
        int cnts = 0;
        Map<String, String> error = new HashMap<>();
        error.put("st-02999", "システムエラーが発生しました。");
        error.put("st-02000", "メンテナンス中です。");
        error.put("st-02100", "パラメータ「seller_id」が不正です。");
        error.put("st-02101", "パラメータ「item_code」が不正です。");
        error.put("st-02102", "パラメータ「item_code」に指定した商品コードの数が上限値を超えています。");
        error.put("st-02103", "パラメータ「item_code」に重複する商品コードが含まれています。");
        error.put("st-02104", "パラメータ「quantity」が不正です。");
        error.put("st-02105", "パラメータ「quantity」に指定した在庫数の数が商品コードの数と一致しません。");
        error.put("st-02106", "パラメータ「allow_overdraft」が不正です。");
        error.put("st-02107", "パラメータ「allow_overdraft」に指定した超過購入設定値の数が商品コードの数と一致しません。");
        error.put("ed-00003", "ストアアカウントが指定されていません。");
        error.put("ed-00005", "ストアアカウントの指定が不正です。");
        error.put("ed-00004", "ストアアカウントが存在しません。");
        error.put("ed-00001", "システムエラーが発生しました。");
        error.put("ed-10002", "在庫更新に成功しているが、更新後の在庫情報の取得に失敗した場合リクエストされたいずれかの在庫の更新には成功しているため、207応答になります。");
        if (allApiData != null && allApiData.size() > 0) {
            for (Tc203_order_client allApiDatum : allApiData) {
                // API連携情報
                String clientId = allApiDatum.getClient_id();
                String clientUrl = allApiDatum.getClient_url();
                Integer apiId = allApiDatum.getId();
                String fileName = allApiDatum.getBikou1();
                String tokenPasswd = allApiDatum.getBikou2();
                String token = allApiDatum.getAccess_token();
                String[] urlParts = clientUrl.split("/");
                String sellerId = urlParts[3];
                // API連携取得
                List<Mc106_produce_renkei> allProductData = productService.getAllProductDataById(clientId, apiId);

                String param = "seller_id=" + sellerId;
                StringBuilder itemCode = new StringBuilder("&item_code=");
                StringBuilder quantity = new StringBuilder("&quantity=");

                if (allProductData != null && allProductData.size() > 0) {
                    // パラメータ
                    try {
                        for (Mc106_produce_renkei allProductDatum : allProductData) {
                            // 在庫数
                            Integer productCnt;
                            String product_id = allProductDatum.getProduct_id();
                            // 商品ID(yahoo商品ID)
                            String renkeiPid = allProductDatum.getRenkei_product_id();
                            // 商品CD(yahoo商品CD)
                            String variantId = allProductDatum.getVariant_id();
                            String code =
                                StringTools.isNullOrEmpty(variantId) ? renkeiPid : renkeiPid + ":" + variantId;
                            // 理論在庫数のみ取得 (※available_cntのみ取得可、他の項目を取得できない ※要注意)
                            Tw300_stock productCnts = productService.getProductCnt(clientId, product_id);
                            // 有効在庫数が0以下場合、0とする
                            if (productCnts != null) {
                                // 配送可在庫数を取得 (※available_cntのみ取得可、他の項目を取得できない ※要注意)
                                productCnt = productCnts.getAvailable_cnt();
                                itemCode.append(code).append(",");
                                quantity.append(productCnt).append(",");
                            }
                        }
                        itemCode = new StringBuilder(itemCode.substring(0, itemCode.length() - 1));
                        quantity = new StringBuilder(quantity.substring(0, quantity.length() - 1));
                        if ("&item_code".equals(itemCode.toString())) {
                            continue;
                        }
                        param += itemCode + quantity.toString();
                        // リクエスト
                        JSONObject res =
                            HttpUtils.sendXmlPost(apiUrl, param, TOKEN_NAME, token, fileName, tokenPasswd, null);
                        if (!StringTools.isNullOrEmpty(res)) {
                            JSONArray stocks = new JSONArray();
                            String tmp = res.getJSONObject("ResultSet").getString("Result");
                            if (tmp.charAt(0) == '{') {
                                stocks.add(res.getJSONObject("ResultSet").getJSONObject("Result"));
                            } else {
                                stocks = res.getJSONObject("ResultSet").getJSONArray("Result");
                            }

                            for (int i = 0; i < stocks.size(); i++) {
                                JSONObject stock = stocks.getJSONObject(i);
                                String error_code = stock.getString("ErrorCode");
                                String stockItemCode = stock.getString("ItemCode");
                                String subCode = stock.getString("SubCode");
                                String Quantity = stock.getString("Quantity");
                                Mc106_produce_renkei produceRenkei =
                                    productRenkeiDao.getDataByRenkeiId(stockItemCode, subCode,
                                        clientId);
                                String productId = produceRenkei.getProduct_id();
                                if (!StringTools.isNullOrEmpty(error_code)) {
                                    logger.error("yahoo在庫連携NG 店舗ID:" + clientId + "商品ID:" + productId + " 原因:"
                                        + error.get(error_code));
                                } else {
                                    productDao.updateStockStroeCnt(clientId, productId, Integer.valueOf(Quantity),
                                        API.YAHOO.getName(), DateUtils.getDate());
                                    logger.info(
                                        "yahoo在庫連携OK 店舗ID:" + clientId + " 商品ID:" + productId + " 在庫数:" + Quantity);
                                    // 総計件数
                                    cnts++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("yahoo在庫連携NG 店舗ID:" + clientId);
                        logger.error(BaseException.print(e));
                    }
                }
            }
        }
        logger.info("yahoo在庫連携 終了--処理件数:" + cnts);
    }

    /**
     * token自動更新 (30分ごと自動起動 0,30)
     *
     * @author HZM
     * @date 2021/6/8
     */
    // @Scheduled(cron = "0 0/30 * * * ?")
    private void refreshToken() {
        logger.info("Yahooのtoken自動更新 開始");
        Base64.Encoder encoder = Base64.getEncoder();
        List<Tc203_order_client> allData = apiService.getAllData(API.YAHOO.getName());
        String ApiUrl = "https://auth.login.yahoo.co.jp/yconnect/v2/token";
        // Yahooに関するAPI情報が存在しない場合、処理中止
        if (io.jsonwebtoken.lang.Collections.isEmpty(allData)) {
            logger.info("Yahooのtoken自動更新 店舗情報：0件");
            logger.info("Yahooのtoken自動更新 終了");
            return;
        }
        for (Tc203_order_client data : allData) {
            String client_id = data.getClient_id();
            String refresh_token = data.getRefresh_token();
            logger.info("Yahooのtoken自動更新 店舗ID【{}】refresh_token【{}】", data.getClient_id(), refresh_token);
            if (StringTools.isNullOrEmpty(refresh_token)) {
                continue;
            }
            String APPID = data.getApi_key();
            String SECRET = data.getPassword();
            String basic = APPID + ":" + SECRET;
            byte[] textByte;
            try {
                textByte = basic.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                logger.error("Yahoo トークン更新失敗。");
                logger.error(BaseException.print(e));
                continue;
            }
            String encodedText = encoder.encodeToString(textByte);
            String token = "Basic " + encodedText;
            HashMap<String, String> map = new HashMap<>();
            map.put("refresh_token", refresh_token);
            map.put("grant_type", "refresh_token");
            HttpEntity entity = HttpClientUtils.createHttpEntity(map);
            logger.info("Yahoo　token更新　店舗ID【{}】", data.getClient_id());
            String responseStr = HttpClientUtils.sendPost(ApiUrl, entity, TOKEN_NAME, token, new ArrayList<>());
            JSONObject jsonResult = JSONObject.parseObject(responseStr);
            if (Objects.isNull(jsonResult)) {
                continue;
            }
            String access_token = jsonResult.getString("token_type") + " " + jsonResult.getString("access_token");
            try {
                orderApiDao.updateToken(access_token, refresh_token, data.getId(), client_id);
            } catch (Exception e) {
                logger.error("refresh_token取得失敗");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info("Yahooのtoken自動更新 終了");
    }

    private void insertOrderDetail(String orderNo, String clientId, Integer apiId, JSONObject rels,
        Mc105_product_setting productSetting) {
        {
            /* 受注明細 登録 */
            JSONArray orderItems = new JSONArray();
            Tc201_order_detail tc201OrderDetail;
            try {
                int totalPrice = 0;
                String tmp = rels.getString("Item");
                // tc201受注明細
                if (tmp.charAt(0) == '{') {
                    orderItems.add(rels.getJSONObject("Item"));
                } else {
                    orderItems = rels.getJSONArray("Item");
                }
                int subNo;
                // 連続API管理ID
                // Integer apiId = allData.get(i).getId();
                for (int m = 0; m < orderItems.size(); m++) {
                    tc201OrderDetail = new Tc201_order_detail();
                    // 受注明細番号
                    subNo = m + 1;
                    // JSONから商品情報取得
                    JSONObject products = orderItems.getJSONObject(m);
                    // 連携商品ID
                    String renkeiProductId = products.getString("ItemId");
                    // 商品コード(SKUコード)
                    String code = products.getString("ItemId");
                    if (!StringTools.isNullOrEmpty(products.getString("SubCode"))) {
                        code = products.getString("SubCode");
                    }
                    // 商品名
                    String name = products.getString("Title");
                    // 商品単価
                    String price = products.getString("UnitPrice");
                    // オプション
                    String option = products.getString("ItemOption");
                    StringBuilder options = new StringBuilder();
                    JSONArray orderOptions = new JSONArray();
                    if (!StringTools.isNullOrEmpty(option)) {
                        if (option.charAt(0) == '{') {
                            orderOptions.add(products.getJSONObject("ItemOption"));
                        } else if (option.charAt(0) == '[') {
                            orderOptions = products.getJSONArray("ItemOption");
                        }
                        for (int n = 0; n < orderOptions.size(); n++) {
                            JSONObject op = orderOptions.getJSONObject(n);
                            options.append(op.getString("Name")).append(":").append(op.getString("Value")).append(",");
                        }
                        options = new StringBuilder(options.substring(0, options.length() - 1));
                    }
                    // 消費税率
                    Integer taxRate = products.getInteger("ItemTaxRatio");
                    // 軽減税率の取得(0:税率10% 1:税率8%)
                    int isReducedTax = 0;
                    if (taxRate == 8) {
                        isReducedTax = 1;
                    }
                    // 税区分(1:税抜0:税込) TODO 固定的に0に設定するのが問題ないでしょうか。
                    int tax_flag = 0;
                    // 新規商品登録&外部商品管理ID登録
                    try {
                        // 商品新規登録
                        // **********************************************************
                        // ********************* 共通パラメータ整理 *********************
                        // **********************************************************
                        ProductBean productBean = new ProductBean();
                        productBean.setClientId(clientId);
                        productBean.setApiId(apiId);
                        productBean.setCode(code);
                        productBean.setName(name);
                        productBean.setPrice(String.valueOf(price));
                        productBean.setIsReducedTax(isReducedTax);
                        productBean.setRenkeiPid(renkeiProductId);
                        productBean.setOptions(option);
                        // Mc100_productテーブルの既存商品をマッピング
                        Mc100_product mc100Product =
                            apiCommonUtils.fetchMc100Product(clientId, code, options.toString());
                        // 商品登録されていない場合、商品マスタに仮商品として新規登録
                        if (Objects.isNull(mc100Product)) {
                            // 商品新規登録
                            mc100Product = apiCommonUtils.insertMc100Product(productBean, API.YAHOO.getName());
                            // 商品之前不存在 设定为仮登録
                            tc201OrderDetail.setProduct_kubun(9);
                        }
                        if (!Objects.isNull(mc100Product)) {
                            apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                                API.YAHOO.getName());
                        }
                        if (!Objects.isNull(mc100Product)) {
                            // 商品ID
                            tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
                            // 商品コード
                            code = mc100Product.getCode();
                            // セット商品ID
                            if (mc100Product.getSet_flg() == 1
                                && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                                tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
                            }
                            // 同梱物(0:なし 1:同梱物)
                            if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg())
                                && mc100Product.getBundled_flg() == 1) {
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
                            throw new BaseException(ErrorCode.E_11005);
                        }
                        // 商品名
                        tc201OrderDetail.setProduct_name(name);
                        // 商品コード
                        tc201OrderDetail.setProduct_code(code);
                        // 商品オプション値を保管
                        tc201OrderDetail.setProduct_option(options.toString());
                    } catch (Exception e) {
                        logger.error("yahoo受注連携で商品登録失敗");
                        logger.error(BaseException.print(e));
                    }
                    // 商品単価(販売価格) ※注意list_price通常価格 sales_price販売価格 商品価格price
                    tc201OrderDetail.setUnit_price(Double.valueOf(price).intValue());
                    // 商品数量
                    tc201OrderDetail.setNumber(products.getInteger("Quantity"));
                    // 商品小計(販売価格*商品数量)
                    if (!StringTools.isNullOrEmpty(tc201OrderDetail.getNumber())
                        && !StringTools.isNullOrEmpty(tc201OrderDetail.getUnit_price())) {
                        int total_price;
                        int unitPrice = tc201OrderDetail.getUnit_price();
                        int number = tc201OrderDetail.getNumber();
                        int accordion = productSetting.getAccordion();
                        if (tax_flag == 1) {
                            // 税抜の場合、税率10%として計算 (税込= 単価(税抜)-単価(税抜)*税率)
                            total_price = CommonUtils.getTaxIncluded(unitPrice, 10, accordion) * number;
                        } else {
                            total_price = unitPrice * number;
                        }
                        tc201OrderDetail.setProduct_total_price(total_price);
                        totalPrice += total_price;
                    }
                    tc201OrderDetail.setIs_reduced_tax(isReducedTax);
                    tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
                    tc201OrderDetail.setPurchase_order_no(orderNo);
                    tc201OrderDetail.setDel_flg(0);
                    tc201OrderDetail.setTax_flag(tax_flag);
                    try {
                        // 受注明細登録
                        orderDetailDao.insertOrderDetail(tc201OrderDetail);
                    } catch (Exception e) {
                        System.err.println(BaseException.print(e));
                        logger.error(BaseException.print(e));
                        break;
                    }
                }
                // TODO 一時対策 21/07/15 hzm
                orderDao.setOrderProductTotalPrice(totalPrice, clientId, orderNo);
            } catch (Exception e) {
                logger.error(BaseException.print(e));
            }
        }
    }

    private List<String> setTc200Json(JSONObject order, String token, Integer order_type, String fileName,
        String tokenPasswd, String SellerId, InitClientInfoBean initClientInfoBean) {
        String orderDetailUrl = "https://circus.shopping.yahooapis.jp/ShoppingWebService/V1/orderInfo";
        String orderId = order.getString("OrderId");
        String item =
            "NeedDetailedSlip,OrderTime,BillFirstName,BillFirstNameKana,BillLastName,BillLastNameKana,BillZipCode,BillPrefecture,BillCity,BillAddress1,BillAddress2,BillPhoneNumber,BillMailAddress,ShipMethodName,ShipRequestDate,ShipRequestTime,ShipNotes,ShipCompanyCode,NeedGiftWrap,ShipFirstName,ShipFirstNameKana,ShipLastName,ShipLastNameKana,ShipZipCode,ShipPrefecture,ShipCity,ShipAddress1,ShipAddress2,ShipPhoneNumber,PayCharge,ShipCharge,Discount,UsePoint,TotalMallCouponDiscount,SettleAmount,TotalPrice,LineId,ItemId,Title,SubCode,ProductId,ItemTaxRatio,UnitPrice,Quantity,PayMethod,PayMethodName,ItemOption";
        String param = "<Req><Target><OrderId>";
        param += orderId;
        param += "</OrderId><Field>" + item + "</Field></Target><SellerId>" + SellerId + "</SellerId></Req>";
        JSONObject result =
            HttpUtils.sendXmlPost(orderDetailUrl, param, TOKEN_NAME, token, fileName, tokenPasswd, null);
        JSONObject orderInfo = result.getJSONObject("ResultSet").getJSONObject("Result").getJSONObject("OrderInfo");
        // 初期化
        Tc200_order tc200Order = new Tc200_order();
        List<String> errList = new ArrayList<>();

        tc200Order.setOrder_type(order_type);
        // 店舗ID
        String clientId = initClientInfoBean.getClientId();
        // 倉庫管理番号
        List<String> warehouseIdListByClientId = orderDao.getWarehouseIdListByClientId(clientId);
        tc200Order.setWarehouse_cd(warehouseIdListByClientId.get(0));
        // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMM-00001」
        String orderNo =
            APICommonUtils.getOrderNo(initClientInfoBean.getSubNo(), initClientInfoBean.getIdentification());
        tc200Order.setPurchase_order_no(orderNo);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(orderId);
        // 受注取込履歴ID
        tc200Order.setHistory_id(Integer.toString(initClientInfoBean.getHistoryId()));
        // 个口数(Default値:1)
        tc200Order.setBoxes(1);
        // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済)
        tc200Order.setOuter_order_status(0);
        // 必要字段写入（receiver_zip_code1）（Import_datetime）(Receiver_address1)(Receiver_todoufuken)(Receiver_family_name)
        try {
            // 受注時間
            tc200Order
                .setOrder_datetime(CommonUtils.dealDateFormat(orderInfo.getString("OrderTime").replace("T", " ")));
            // 作成時間
            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));

            /* 配送者の住所情報取得 */
            JSONObject jsonObject = orderInfo.getJSONObject("Ship");
            if (!StringTools.isNullOrEmpty(jsonObject)) {
                // 配送情報取得
                String title = jsonObject.getString("ShipCompanyCode");
                // 配送方法取得
                String deliveryName = null;
                Ms004_delivery ms004Delivery =
                    apiCommonUtils.getDeliveryMethod(title, initClientInfoBean.getDefaultDeliveryMethod(),
                        initClientInfoBean.getMs007SettingDeliveryMethodMap());
                if (!Objects.isNull(ms004Delivery)) {
                    tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
                    tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
                    // 配送master登録した配送会社名を取得
                    deliveryName = ms004Delivery.getDelivery_nm();
                } else {
                    // 店舗の配送情報設定なし、NULLとして設定
                    tc200Order.setDelivery_method(null);
                    tc200Order.setDelivery_company(null);
                }
                tc200Order.setBikou9(deliveryName);
                // 配送時間帯 (配送連携設定管理表「mc007_setting」から取得)
                String deliveryTimeId = "";
                if (!StringTools.isNullOrEmpty(deliveryName)) {
                    deliveryTimeId =
                        apiCommonUtils.getDeliveryTimeSlot(deliveryName, jsonObject.getString("ShipRequestTime"),
                            API.YAHOO.getName(), initClientInfoBean.getMs007SettingTimeMap());
                }
                tc200Order.setDelivery_time_slot(deliveryTimeId);
                // 希望届け日 TODO
                String deliveryDate = jsonObject.getString("ShipRequestDate");
                tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));

                List<String> zipList = CommonUtils.checkZipToList(jsonObject.getString("ShipZipCode"));
                // 配送先郵便番号
                if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                    tc200Order.setReceiver_zip_code1(zipList.get(0));
                    tc200Order.setReceiver_zip_code2(zipList.get(1));
                } else {
                    // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                    logger.warn("Yahoo受注(" + orderNo + "):Yahooデータ不正(郵便)");
                    tc200Order.setReceiver_zip_code1("000");
                    tc200Order.setReceiver_zip_code2("0000");
                }
                // 配送先都道府県
                String province = jsonObject.getString("ShipPrefecture");
                tc200Order.setReceiver_todoufuken(province);
                // 配送住所(市区町+住所1)
                String address = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("ShipCity"))
                    && !StringTools.isNullOrEmpty(jsonObject.getString("ShipAddress1"))) {
                    address = jsonObject.getString("ShipCity") + jsonObject.getString("ShipAddress1");
                }
                if (!StringTools.isNullOrEmpty(address)) {
                    tc200Order.setReceiver_address1(address);
                } else {
                    errList.add("配送先の住所が取得できないため、Yahoo店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送住所2
                tc200Order.setReceiver_address2(jsonObject.getString("ShipAddress2"));
                // 配送者電話番号
                String sphone = jsonObject.getString("ShipPhoneNumber");
                if (!StringTools.isNullOrEmpty(sphone)) {
                    List<String> telList = CommonUtils.checkPhoneToList(sphone);
                    if (telList != null && telList.size() > 0) {
                        tc200Order.setReceiver_phone_number1(telList.get(0));
                        tc200Order.setReceiver_phone_number2(telList.get(1));
                        tc200Order.setReceiver_phone_number3(telList.get(2));
                    }
                }
                // 配送先名前
                if (!StringTools.isNullOrEmpty(jsonObject.getString("ShipLastName"))) {
                    tc200Order.setReceiver_family_name(jsonObject.getString("ShipLastName"));
                } else {
                    errList.add("配送先の名前が取得できないため、Yahoo店舗の注文情報をご確認ください。");
                    return errList;
                }
                tc200Order.setReceiver_family_kana(jsonObject.getString("ShipLastNameKana"));
                // 配送先名
                tc200Order.setReceiver_first_name(jsonObject.getString("ShipFirstName"));
                tc200Order.setReceiver_first_kana(jsonObject.getString("ShipFirstNameKana"));
                // 店舗側のメモ取得(※備考欄に記載する)
                String ShipNotes = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("ShipNotes"))) {
                    // 備考フラグ1を設定する
                    tc200Order.setBikou_flg(1);
                    ShipNotes = jsonObject.getString("ShipNotes");
                }
                String option1Value = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("Option1Value"))) {
                    // 備考フラグ1を設定する
                    tc200Order.setBikou_flg(1);
                    option1Value = "\n" + jsonObject.getString("Option1Value");
                }
                String option2Value = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("Option2Value"))) {
                    // 備考フラグ1を設定する
                    tc200Order.setBikou_flg(1);
                    option2Value = "\n" + jsonObject.getString("Option2Value");
                }
                tc200Order.setMemo(ShipNotes + option1Value + option2Value);
            }
            JSONObject pay = orderInfo.getJSONObject("Pay");
            JSONObject detail = orderInfo.getJSONObject("Detail");
            // 注文者郵便番号1
            String billingZip = pay.getString("BillZipCode");
            List<String> zipList = CommonUtils.checkZipToList(billingZip);
            // 配送先郵便番号
            if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                // 注文者郵便番号1
                tc200Order.setOrder_zip_code1(zipList.get(0));
                // 注文者郵便番号2
                tc200Order.setOrder_zip_code2(zipList.get(1));
            }
            // 注文者住所都道府県
            tc200Order.setOrder_todoufuken(pay.getString("BillPrefecture"));
            // 注文者住所郡市区
            tc200Order.setOrder_address1(pay.getString("BillCity"));
            // 注文者詳細住所
            tc200Order.setOrder_address2(pay.getString("BillAddress1") + pay.getString("BillAddress2"));
            // 注文者姓
            tc200Order.setOrder_family_name(pay.getString("BillLastName"));
            // 注文者名
            tc200Order.setOrder_first_name(pay.getString("BillFirstName"));
            // 注文者姓カナ
            tc200Order.setOrder_family_kana(pay.getString("BillLastNameKana"));
            // 注文者名カナ
            tc200Order.setOrder_first_kana(pay.getString("BillFirstNameKana"));
            // 注文者電話番号1
            String phone = pay.getString("BillPhoneNumber");
            if (!StringTools.isNullOrEmpty(phone)) {
                List<String> telList = CommonUtils.checkPhoneToList(phone);
                if (telList != null && telList.size() > 0) {
                    tc200Order.setOrder_phone_number1(telList.get(0));
                    tc200Order.setOrder_phone_number2(telList.get(1));
                    tc200Order.setOrder_phone_number3(telList.get(2));
                }
            }
            // 注文者メールアドレス
            tc200Order.setOrder_mail(pay.getString("BillMailAddress"));
            // 支払方法
            String payment_name = pay.getString("PayMethodName");
            // 支払方法(連携設定ms007表に参考し、変換されたIDを取得)
            String payment_id = apiCommonUtils.getPaymentMethod(payment_name, API.YAHOO.getName(),
                initClientInfoBean.getMs007SettingPaymentMap());
            tc200Order.setPayment_method(payment_id);
            // 元々の支払方法を備考10も記載する
            tc200Order.setBikou10(payment_name);

            // 割引額（other_fee）TODO
            // 値引き ＋モールクーポン値引き額＋利用ポイント
            Integer Discount = !StringTools.isNullOrEmpty(detail.getString("Discount"))
                ? Integer.parseInt(detail.getString("Discount"))
                : 0;
            Integer TotalMallCouponDiscount = !StringTools.isNullOrEmpty(detail.getString("TotalMallCouponDiscount"))
                ? Integer.parseInt(detail.getString("TotalMallCouponDiscount"))
                : 0;
            Integer UsePoint = !StringTools.isNullOrEmpty(detail.getString("UsePoint"))
                ? Integer.parseInt(detail.getString("UsePoint"))
                : 0;
            Integer other_fee = Discount + TotalMallCouponDiscount + UsePoint;
            tc200Order.setOther_fee(other_fee);
            // 手数料 TODO 手数料＋ギフト包装料 ＋調整額(-マイナス注意)
            Integer PayCharge = !StringTools.isNullOrEmpty(detail.getString("PayCharge"))
                ? Integer.parseInt(detail.getString("PayCharge"))
                : 0;
            Integer GiftWrapCharge = !StringTools.isNullOrEmpty(detail.getString("GiftWrapCharge"))
                ? Integer.parseInt(detail.getString("GiftWrapCharge"))
                : 0;
            Integer Adjustments = !StringTools.isNullOrEmpty(detail.getString("Adjustments"))
                ? Integer.parseInt(detail.getString("Adjustments"))
                : 0;
            Integer handling_charge = PayCharge + GiftWrapCharge + Adjustments;
            tc200Order.setHandling_charge(handling_charge);
            // 送料合計
            tc200Order.setDelivery_total(Integer.parseInt(detail.getString("ShipCharge")));
            // 合計請求金額
            tc200Order.setBilling_total(Integer.parseInt(detail.getString("TotalPrice")));
            // 代引引換の一時対応(wang 2020119 ※配送連携設定の決済方法を追加必要)
            if ("payment_d1".equals(pay.getString("PayMethod")) || "2".equals(payment_id)) {
                tc200Order.setCash_on_delivery_fee(Integer.parseInt(detail.getString("TotalPrice")));
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            errList.add("受注データ不正(配送先の郵便番号、住所、名前)が発生したので、注文情報をご確認ください。");
            return errList;
        }

        // 注文者ID(依頼主ID 及び明細書メッセージ) TODO rakuten 参考
        List<Ms012_sponsor_master> sponsorList =
            sponsorDao.getSponsorList(clientId, false, initClientInfoBean.getSponsorId());
        if (io.jsonwebtoken.lang.Collections.isEmpty(sponsorList)) {
            sponsorList = sponsorDao.getSponsorList(clientId, true, null);
        }
        Ms012_sponsor_master sponsorDefaultInfo = sponsorList.get(0);
        if (sponsorDefaultInfo != null) {
            tc200Order.setSponsor_id(sponsorDefaultInfo.getSponsor_id());
            tc200Order.setDetail_price_print(String.valueOf(sponsorDefaultInfo.getPrice_on_delivery_note()));
            // 明細同梱設定(1:同梱する 0:同梱しない)
            if (0 == sponsorDefaultInfo.getDelivery_note_type()) {
                tc200Order.setDetail_bundled("同梱しない");
            } else {
                tc200Order.setDetail_bundled("同梱する");
            }
        }
        // 是否以注文者为依頼主
        tc200Order.setOrder_flag(0);
        // 削除Flg(0削除しない)
        tc200Order.setDel_flg(0);
        // 消費税合計
        // if (!Strings.isNullOrEmpty(order.getString("taxes_included"))) {
        // tc200Order.setTax_total(0);
        // }
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        try {
            // 受注管理表登録
            orderDao.insertOrder(tc200Order);
        } catch (Exception e) {
            logger.error("Yahoo受注管理TBLの登録失敗 受注ID(" + orderNo + ")");
            logger.error(BaseException.print(e));
            errList.add("何か原因により、受注管理の取込が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }

        try {
            Mc105_product_setting productSetting = productSettingService.getProductSetting(clientId, null);
            // 受注明細登録
            insertOrderDetail(orderNo, clientId, initClientInfoBean.getApiId(), orderInfo, productSetting);
        } catch (Exception e) {
            logger.error("Yahoo受注明細TBLの登録失敗 受注ID(" + orderNo + ")");
            logger.error(BaseException.print(e));
            errList.add("何か原因により、受注明細の取込が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }
        // 出庫依頼処理(0:出庫しない 1:自動出庫)
        if (initClientInfoBean.getShipmentStatus() == 1) {
            apiCommonUtils.processShipment(orderNo, clientId);
        }
        return errList;
    }

}
