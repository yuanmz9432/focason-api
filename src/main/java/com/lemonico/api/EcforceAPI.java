package com.lemonico.api;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


/**
 * EcforceのAPI連携機能
 *
 * @className EcforceAPI
 * @date 2020/9/9
 **/
@Component
@EnableScheduling
public class EcforceAPI
{

    private final static Logger logger = LoggerFactory.getLogger(EcforceAPI.class);

    @Resource
    private OrderApiService apiService;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
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
    @Resource
    private MailTools mailTools;

    public static String getBillBarcode(String gmoId, Tc203_order_client orderClient) {
        // 戻り値
        String barcode = "";
        // 認証情報(※現在固定値ですが、DBから取得すべき)
        // TODO 暂时保存
        // String shopcode = "ab044993-00";
        // String password = "a5YAhWTG";
        // String authenid = "0000000001";
        String shopcode = "";
        String password = "";
        String authenid = "";
        if (StringTools.isNullOrEmpty(orderClient) || StringTools.isNullOrEmpty(orderClient.getBikou3())) {
            return barcode;
        }
        String bikou3 = orderClient.getBikou3();
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(bikou3);
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i);
            switch (i) {
                case 0:
                    shopcode = value;
                    break;
                case 1:
                    password = value;
                    break;
                case 2:
                    authenid = value;
                    break;
                default:
                    break;
            }
        }
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<request>\n"
            + "<shopInfo>\n"
            + "<shopCode>" + shopcode + "</shopCode>\n"
            + "<connectPassword>" + password + "</connectPassword>\n"
            + "<authenticationId>" + authenid + "</authenticationId>\n"
            + "</shopInfo>\n"
            + "<transaction>\n"
            + "<gmoTransactionId>" + gmoId + "</gmoTransactionId>\n"
            + "</transaction>\n"
            + "</request>";

        // GMOの請求印字のURL
        String url = "https://shop.gmo-ab.com/auto/getinvoicedata.do";
        // GMOの請求印字の結果
        JSONObject response = doPost(url, xml);
        JSONObject resDatas = response.getJSONObject("response");
        if (resDatas != null) {
            JSONObject resValue = resDatas.getJSONObject("invoiceDataResult");
            // 請求コード
            if (!StringTools.isNullOrEmpty(resValue.getString("votesBarCode"))) {
                barcode = resValue.getString("votesBarCode");
            }
        }
        return barcode;
    }

    /**
     * 時刻文字列(yyyy - mm - dd HH : MM : SS)
     *
     * @return java.lang.String
     * @description
     * @date 2020/9/9
     */
    public static String getNowTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    /**
     * 時刻文字列(yyyy - mm - dd HH : MM : SS)
     *
     * @param time 時間文字列
     * @return java.lang.String
     * @author wang
     * @date 2021/1/16
     */
    public static String stringToTime(String time) throws ParseException {
        String dateString = "";
        if (!StringTools.isNullOrEmpty(time)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = df.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateString = formatter.format(date);
        }
        return dateString;
    }

    /**
     * POSTリクエストを出す
     *
     * @param url URL
     * @param xml ボディ
     * @return JSONObject レスポンス
     */
    public static JSONObject doPost(String url, String xml) {
        CloseableHttpClient httpClient;
        try {
            httpClient = HttpClients.createDefault();
            return sendPost(url, httpClient, xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ecforceリクエストを出す
     *
     * @param url URL
     * @param httpClient HTTPクライアント
     * @param xml ボディ
     * @return JSONObject
     * @author wang
     * @date 2021/1/16
     */
    public static JSONObject sendPost(String url, CloseableHttpClient httpClient, String xml) {
        // 创建http post对象
        HttpPost httpPost = new HttpPost(url);
        // 设置http请求header
        httpPost.addHeader("Content-Type", "application/xml; charset=utf-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(xml, "UTF-8"));
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        // 发送请求，并获取返回值
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            String res = EntityUtils.toString(entity, "UTF-8");
            // XMLをorg.json.JSONObject(JSON)形式
            org.json.JSONObject json = XML.toJSONObject(res);
            return JSONObject.parseObject(json.toString());
        } catch (IOException | JSONException e) {
            logger.error("HttpClient接続失敗 URL={},HTTP情報={}", url, httpClient);
            logger.error(BaseException.print(e));
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Ecforce受注自動取込 (10分ごと自動起動 7, 17, 27, 37, 47, 57)
     *
     * @author HZM
     * @date 2021/1/6
     */
    // @Scheduled(cron = "0 7/10 * * * ?")
    public void fetchEcforceOrders() throws ParseException {
        logger.info("Ecforce受注連携 開始");
        // Ecforce店舗連携情報取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.ECFORCE.getName());
        // Ecforceに関するAPI情報が存在しない場合、処理中止
        if (Collections.isEmpty(allData)) {
            logger.info("Ecforce受注連携 店舗情報：0件");
            logger.info("Ecforce受注連携 終了");
            return;
        }
        Hashtable<String, List<String>> errHashtable = new Hashtable<>();
        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        // API共通パーツ初期化
        apiCommonUtils.initialize();
        for (Tc203_order_client data : allData) {

            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();

            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007) TODO
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(data);

            // 受注取込履歴ID取得
            Integer historyId = initClientInfo.getHistoryId();
            // 受注取得URLの初期化
            StringBuilder url = new StringBuilder();
            // 店舗情報取得
            String clientId = data.getClient_id();
            // アプリ情報取得
            String apiUrl = data.getClient_url();
            String apiKey = data.getApi_key();
            String apiPwd = data.getPassword();

            // 注文状況の検索条件を取得する
            String options1 = strToOptionsState(data.getBikou1());
            // 決済状況の検索条件を取得
            String options2 = strToOptionsPaymentState(data.getBikou2());
            String token = "";
            // 受注管理
            JSONArray orders;
            // 受注明細
            JSONArray orderDetails;
            // 初期化
            HashMap<String, String> map = new HashMap<>();
            try {
                // 認証情報Token取得
                token = getToken(clientId, apiUrl, apiKey, apiPwd);
                // 受注情報取得
                url.append("https://")
                    .append(apiUrl)
                    .append("/api/v1/orders.json")
                    .append("?include=billing_address,shipping_address,order_items")
                    .append("&page=1&per=100");

                // 対応状況(※DBの備考1に設定している値を条件として取得)
                if (!StringTools.isNullOrEmpty(options1)) {
                    url.append(options1);
                }
                // 決済状況(※DBの備考2に設定している値を条件として取得)
                if (!StringTools.isNullOrEmpty(options2)) {
                    url.append(options2);
                }
                // Ecforceから受注情報として取込
                String responseStr = HttpClientUtils.sendGet(url.toString(), "Authorization", token, errList);
                JSONObject responseObj = JSONObject.parseObject(responseStr);
                if (StringTools.isNullOrEmpty(responseObj)) {
                    logger.info("Ecforce受注連携 店舗ID【{}】API検索条件【{}】【{}】", clientId, options1, options2);
                    logger.info("Ecforce受注連携 店舗ID【{}】 受注データが0件。", clientId);
                    errOrderClients.add(data);
                    continue;
                }
                // 受注管理
                orders = responseObj.getJSONArray("data");
                // 受注明細
                orderDetails = responseObj.getJSONArray("included");
            } catch (Exception e) {
                logger.error("Ecforce受注連携NG 店舗ID【{}】 API名【{}】", clientId, data.getApi_name());
                logger.error("Ecforce受注連携NG URL【{}】token【{}】", apiUrl, token);
                logger.error(BaseException.print(e));
                errList.add("Ecforce受注連携获取受注信息失败");
                errOrderClients.add(data);
                continue;
            }
            // 取込件数
            int total;
            // 成功件数
            int successCnt = 0;
            // 失敗件数
            int failureCnt = 0;
            // 受注番号の枝番
            int subno = 1;
            // 受注データ取得できず、処理スキップ
            if (Objects.equal(orders, null) || Objects.equal(orderDetails, null)) {
                logger.warn("Ecforce受注連携NG 店舗ID【{}】 API名【{}】", clientId, data.getApi_name());
            }
            // 受注件数取得
            total = orders.size();
            // 受注明細を整理 Map(key,Object)
            for (int k = 0; k < orderDetails.size(); k++) {
                JSONObject obj = orderDetails.getJSONObject(k);
                map.put(obj.getString("type") + "-" + obj.getString("id"), obj.getString("attributes"));
            }
            // 受注処理
            for (int j = 0; j < orders.size(); j++) {
                // エラーメッセージ
                String msg = "";
                // 初期化
                Tc200_order tc200Order = new Tc200_order();
                Integer status = data.getShipment_status();
                // 受注番号(外部受注番号)
                // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMM-00001」
                String orderNo = APICommonUtils.getOrderNo(subno, data.getIdentification());
                tc200Order.setPurchase_order_no(orderNo);
                // 倉庫ID
                tc200Order.setWarehouse_cd(initClientInfo.getWarehouseCd());
                // 店舗ID
                tc200Order.setClient_id(clientId);

                // 外部受注番号
                JSONObject order = orders.getJSONObject(j);
                JSONObject attr = JSON.parseObject(order.getString("attributes"));
                String outerOrderNo = attr.getString("number") + "-" + attr.getString("id");

                // 決済状況(売上完了completed、仮売上完了authedのみ取込)
                String payment_name = attr.getString("payment_method_name");
                // 注文状況 ※検索条件にはcanceledを含む必要
                String state = attr.getString("state");
                // キャンセル以外場合、処理 ※検索条件にはcanceledを含む必要
                if (!"canceled".equals(state)) {
                    // 外部受注番号がない場合、新規受注として取込しない
                    Integer outerOrderCnt = orderDao.getOuterOrderNo(outerOrderNo, clientId);
                    // 過去受注に関して、処理をスキップ
                    if (outerOrderCnt > 0) {
                        logger.warn("Ecforce受注連携 店舗ID【{}】受注ID【{}】 原因:過去受注取込済", clientId, outerOrderNo);
                        total--;
                    } else {
                        tc200Order.setOuter_order_no(outerOrderNo);
                        // 受注取込履歴ID
                        tc200Order.setHistory_id(Integer.toString(initClientInfo.getHistoryId()));
                        // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済) TODO
                        tc200Order.setOuter_order_status(0);
                        // 注文日時
                        tc200Order.setOrder_datetime(
                            Timestamp.valueOf(stringToTime(attr.getString("completed_at"))));

                        // 支払方法(連携設定ms007表に参考し、変換されたIDを取得)
                        String paymentMethod = "";
                        if (!StringTools.isNullOrEmpty(payment_name)) {
                            // ms007,ms014から支払方法の取得
                            paymentMethod = apiCommonUtils.getPaymentMethod(payment_name, API.ECFORCE.getName(),
                                initClientInfo.getMs007SettingPaymentMap());
                            tc200Order.setPayment_method(paymentMethod);
                            // 元々の支払方法を備考10も記載する
                            tc200Order.setBikou10(payment_name);
                        }

                        // 代引き料金 TODO
                        if ("代金引換".equals(payment_name)) {
                            tc200Order.setCash_on_delivery_fee(attr.getInteger("total"));
                        }
                        // GMO取引の場合、請求コードを記載

                        // GMO後払い:25
                        if (!StringTools.isNullOrEmpty(paymentMethod) && "25".equals(paymentMethod)) {
                            // GMO連携APIにより、請求コードを取得
                            String gmoPaymentId = attr.getString("payment_access_id");
                            String billBarcode = getBillBarcode(gmoPaymentId, data);
                            // 請求コード
                            tc200Order.setBill_barcode(billBarcode);
                            // 取引ID
                            tc200Order.setPayment_id(gmoPaymentId);
                            logger.info("Ecforce受注連携OK 店舗ID:" + clientId
                                + " 支　払:" + payment_name
                                + " 取引ID:" + gmoPaymentId
                                + " 請求CD:" + billBarcode);

                        } else {
                            if (payment_name.contains("後払") && payment_name.contains("GMO")) {
                                // GMO連携APIにより、請求コードを取得
                                String gmoPaymentId = attr.getString("payment_access_id");
                                String billBarcode = getBillBarcode(gmoPaymentId, data);
                                // 請求コード
                                tc200Order.setBill_barcode(billBarcode);
                                // 取引ID
                                tc200Order.setPayment_id(gmoPaymentId);
                                logger.info("Ecforce受注連携OK 店舗ID:" + clientId
                                    + " 支　払:" + payment_name
                                    + " 取引ID:" + gmoPaymentId
                                    + " 請求CD:" + billBarcode);
                            }
                        }

                        // 消費税合計(「手数料」、「送料」の税金を含む)
                        int tax = attr.getInteger("tax");
                        // 消費税合計(8%)
                        int tax8 = attr.getInteger("tax8");
                        // 消費税合計(10% 「手数料」、「送料」の税金を含む)
                        int tax10 = attr.getInteger("tax10");
                        // 商品合計(subtotal8 + subtotal10)
                        int subtotal = attr.getInteger("subtotal");
                        // 商品合計(8％)
                        int subtotal8 = attr.getInteger("subtotal8");
                        // 商品合計(10％)
                        int subtotal10 = attr.getInteger("subtotal10");
                        // 送料 (消費税合計が0以上の場合、税抜(10％)として計算する必要)
                        int delivFee = attr.getInteger("deliv_fee");
                        // 調整金額
                        int adjustment = attr.getInteger("adjustment");
                        // 備考1,2,3,9,10カラムが使われているので、調整金額(adjustment)を備考4に設定する
                        tc200Order.setBikou4(String.valueOf(adjustment));
                        // 手数料 (消費税合計が0以上の場合、税抜(10％)として計算する必要)
                        int charge = attr.getInteger("charge");
                        // その他金額 discount_with_point 割引(ポイント含む 税込)
                        int discountWithPoint = attr.getInteger("discount_with_point");

                        // 消費税合計(「手数料」、「送料」の税金を含む)
                        tc200Order.setTax_total(tax);
                        // 税抜の場合、商品税込(taxが0の場合、税込)
                        if (tax > 0) {
                            // その他金額 discount_with_point 割引(ポイント含む 税込)
                            BigDecimal bigDiscountWithPoint = new BigDecimal(discountWithPoint);
                            if (tax8 > 0) {
                                // 税込8％商品金額
                                BigDecimal bigTax8 = new BigDecimal("1.08");
                                BigDecimal bigTotal8 = new BigDecimal(subtotal8);
                                subtotal8 = bigTotal8.multiply(bigTax8).intValue();
                                // その他金額 discount_with_point 割引(ポイント含む 税込)
                                discountWithPoint = bigDiscountWithPoint.multiply(bigTax8).intValue();
                            }
                            // 税率10%
                            BigDecimal bigTax10 = new BigDecimal("1.1");
                            if (tax10 > 0) {
                                // 税込10％商品金額
                                BigDecimal bigTotal10 = new BigDecimal(subtotal10);
                                subtotal10 = bigTotal10.multiply(bigTax10).intValue();
                                // その他金額 discount_with_point 割引(ポイント含む 税込)
                                discountWithPoint = bigDiscountWithPoint.multiply(bigTax10).intValue();
                            }
                            // 商品金額(計算した税込8％商品金額 + 計算した税込10％商品金額)
                            subtotal = subtotal8 + subtotal10;
                            // 送料(税抜の場合、税込10％として計算する必要)
                            BigDecimal bigDelivFee = new BigDecimal(delivFee);
                            delivFee = bigDelivFee.multiply(bigTax10).intValue();
                            // 手数料(税抜の場合、税込10％として計算する必要)
                            BigDecimal bigCharge = new BigDecimal(charge);
                            charge = bigCharge.multiply(bigTax10).intValue();

                        }

                        // 商品金額(税込)
                        tc200Order.setProduct_price_excluding_tax(subtotal);
                        // その他金額 discount_with_point 割引(ポイント含む 税込)
                        tc200Order.setOther_fee(discountWithPoint);
                        // 送料合計(税込)
                        tc200Order.setDelivery_total(delivFee);
                        // 手数料(税込)
                        tc200Order.setHandling_charge(charge);
                        // 合計請求金額(税込)
                        tc200Order.setBilling_total(attr.getInteger("total"));
                        // ラッピング(null以外 0：無 1:有)
                        if (!StringTools.isNullOrEmpty(attr.getString("wrapping"))) {
                            tc200Order.setGift_wish("1");
                        } else {
                            tc200Order.setGift_wish("0");
                        }

                        // 店舗側のメモ取得(※備考欄に記載する) 備考欄(通信欄)
                        String remark = attr.getString("remark");
                        if (!Strings.isNullOrEmpty(remark)) {
                            // 備考フラグ1を設定する
                            tc200Order.setBikou_flg(1);
                            tc200Order.setMemo(remark);
                        }
                        // 注文者情報
                        JSONObject rels = JSON.parseObject(order.getString("relationships"));
                        JSONObject addr = JSON.parseObject(rels.getString("billing_address"));
                        JSONObject addrData = JSON.parseObject(addr.getString("data"));
                        String mapId = addrData.getString("type") + "-" + addrData.getString("id");
                        JSONObject billing_address = JSON.parseObject(map.get(mapId));

                        if (!billing_address.isEmpty()) {
                            String billingZip = billing_address.getString("zip01")
                                + billing_address.getString("zip02");
                            List<String> zipList = CommonUtils.checkZipToList(billingZip);
                            // 配送先郵便番号
                            if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                                tc200Order.setReceiver_zip_code1(zipList.get(0));
                                tc200Order.setReceiver_zip_code2(zipList.get(1));
                            } else {
                                // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                                logger.warn("Ecforce受注(" + outerOrderNo + "):データ不正(配送者郵便)");
                                tc200Order.setReceiver_zip_code1("000");
                                tc200Order.setReceiver_zip_code2("0000");
                            }

                            // 注文者住所都道府県
                            tc200Order.setOrder_todoufuken(billing_address.getString("prefecture_name"));
                            // 注文者住所郡市区
                            tc200Order.setOrder_address1(billing_address.getString("addr01"));
                            // 注文者詳細住所
                            tc200Order.setOrder_address2(billing_address.getString("addr02"));
                            // 注文者姓
                            tc200Order.setOrder_family_name(billing_address.getString("name01"));
                            // 注文者名
                            tc200Order.setOrder_first_name(billing_address.getString("name02"));
                            // 注文者姓カナ
                            tc200Order.setOrder_family_kana(billing_address.getString("kana01"));
                            // 注文者名カナ
                            tc200Order.setOrder_first_kana(billing_address.getString("kana02"));
                            // 注文者電話番号1
                            tc200Order.setOrder_phone_number1(billing_address.getString("tel01"));
                            // 注文者電話番号1
                            tc200Order.setOrder_phone_number2(billing_address.getString("tel02"));
                            // 注文者電話番号1
                            tc200Order.setOrder_phone_number3(billing_address.getString("tel03"));
                            // 注文者メールアドレス
                            tc200Order.setOrder_mail(attr.getString("email"));
                        }

                        /* 配送先情報 */
                        JSONObject addr2 = JSON.parseObject(rels.getString("shipping_address"));
                        JSONObject data2 = JSON.parseObject(addr2.getString("data"));
                        String mapId2 = data2.getString("type") + "-" + data2.getString("id");
                        JSONObject shipping_address = JSON.parseObject(map.get(mapId2));

                        if (!shipping_address.isEmpty()) {
                            String shippingZip = shipping_address.getString("zip01")
                                + shipping_address.getString("zip02");
                            List<String> zipList = CommonUtils.checkZipToList(shippingZip);
                            // 配送先郵便番号
                            if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                                tc200Order.setReceiver_zip_code1(zipList.get(0));
                                tc200Order.setReceiver_zip_code2(zipList.get(1));
                            } else {
                                // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                                logger.warn("Ecforce受注(" + outerOrderNo + "):データ不正(配送者郵便)");
                                tc200Order.setReceiver_zip_code1("000");
                                tc200Order.setReceiver_zip_code2("0000");
                            }
                            // 配送先住所都道府県 **必須項目
                            tc200Order.setReceiver_todoufuken(shipping_address.getString("prefecture_name"));
                            // 配送先住所郡市区
                            tc200Order.setReceiver_address1(shipping_address.getString("addr01"));
                            // 配送先詳細住所
                            tc200Order.setReceiver_address2(shipping_address.getString("addr02"));
                            // 配送先姓 **必須項目
                            tc200Order.setReceiver_family_name(shipping_address.getString("name01"));
                            // 配送先名
                            tc200Order.setReceiver_first_name(shipping_address.getString("name02"));
                            // 配送先姓カナ
                            tc200Order.setReceiver_family_kana(shipping_address.getString("kana01"));
                            // 配送先名カナ
                            tc200Order.setReceiver_first_kana(shipping_address.getString("kana02"));
                            // 配送先電話番号1
                            tc200Order.setReceiver_phone_number1(shipping_address.getString("tel01"));
                            // 配送先電話番号2
                            tc200Order.setReceiver_phone_number2(shipping_address.getString("tel02"));
                            // 配送先電話番号3
                            tc200Order.setReceiver_phone_number3(shipping_address.getString("tel03"));
                            // 取込日時
                            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
                        } else {
                            /* 異常発生 */
                            msg = "受注データ不正(配送先の郵便番号、住所、名前)が発生したので、注文情報をご確認ください。";
                            apiCommonUtils.insertTc207OrderError(clientId, historyId, outerOrderNo, msg,
                                API.ECFORCE.getName());
                            failureCnt++;
                            errList.add("受注データ不正(配送先の郵便番号、住所、名前)が発生したので、注文情報をご確認ください。");
                            errOrderClients.add(data);
                            continue;
                        }
                        // 配送情報取得 TODO(配送連携設定管理表「mc007_setting」から取得)
                        String title = attr.getString("shipping_carrier_name");
                        // 配送業者ID (Ecforce伝票連携の場合、必須項目 ※重要 TODO)
                        tc200Order.setDelivery_id(attr.getString("shipping_carrier_id"));
                        // 配送方法取得
                        Ms004_delivery ms004Delivery =
                            apiCommonUtils.getDeliveryMethod(title, initClientInfo.getDefaultDeliveryMethod(),
                                initClientInfo.getMs007SettingDeliveryMethodMap());
                        String deliveryNm = "";
                        if (ms004Delivery != null) {
                            // 配送方法ID
                            tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
                            // 配送会社
                            tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
                            // 配送会社名
                            deliveryNm = ms004Delivery.getDelivery_nm();
                        } else {
                            // 配送方法ID
                            tc200Order.setDelivery_company(null);
                            // 配送会社
                            tc200Order.setDelivery_method(null);
                        }
                        // 元の配送方法を備考9に保管する
                        tc200Order.setBikou9(title);
                        // 配送時間帯
                        String deliveryTimeId = "";
                        if (!StringTools.isNullOrEmpty(deliveryNm)) {
                            deliveryTimeId = apiCommonUtils.getDeliveryTimeSlot(deliveryNm,
                                attr.getString("scheduled_delivery_time"), API.ECFORCE.getName(),
                                initClientInfo.getMs007SettingTimeMap());
                        }
                        tc200Order.setDelivery_time_slot(deliveryTimeId);
                        // 配送予定日(yyyy/mm/dd)
                        tc200Order
                            .setDelivery_date(CommonUtils.stringToData(attr.getString("scheduled_to_be_delivered_at")));
                        // 発送予定日(yyyy/mm/dd)
                        tc200Order.setShipment_wish_date(
                            CommonUtils.stringToData(attr.getString("scheduled_to_be_shipped_at")));
                        // 注文者会社名
                        tc200Order.setOrder_company(billing_address.getString("company_name"));
                        // 配送先会社名
                        tc200Order.setReceiver_company(shipping_address.getString("company_name"));
                        // ディフォルトは依頼主
                        tc200Order.setOrder_flag(0);
                        // 注文者ID(依頼主ID 及び明細書メッセージ)
                        Ms012_sponsor_master sponsorDefaultInfo = initClientInfo.getMs012sponsor();
                        if (sponsorDefaultInfo != null) {
                            tc200Order.setSponsor_id(sponsorDefaultInfo.getSponsor_id());
                            tc200Order.setDetail_message(sponsorDefaultInfo.getDetail_message());
                            // 明細書金額印字
                            tc200Order
                                .setDetail_price_print(String.valueOf(sponsorDefaultInfo.getPrice_on_delivery_note()));
                            // 明細同梱設定(1:同梱する 0:同梱しない)
                            if (0 == sponsorDefaultInfo.getDelivery_note_type()) {
                                tc200Order.setDetail_bundled("同梱しない");
                            } else {
                                tc200Order.setDetail_bundled("同梱する");
                            }
                        }
                        // 削除Flg(0削除しない)
                        tc200Order.setDel_flg(0);
                        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
                        tc200Order.setForm(2);
                        try {
                            // 受注管理登録
                            if (StringTools.isNullOrEmpty(msg)) {
                                orderDao.insertOrder(tc200Order);
                                subno++;
                                successCnt++;
                            } else {
                                logger.warn("Ecforce受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc200)");
                            }
                        } catch (Exception e) {
                            subno++;
                            failureCnt++;
                            msg = "Ecforce受注連携のバッチ処理により、受注管理の登録が失敗しました。" + "詳細はシステム担当者にお問い合わせください。";
                            apiCommonUtils.insertTc207OrderError(clientId, historyId, outerOrderNo, msg,
                                API.ECFORCE.getName());
                            logger.error("Ecforce受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc200)");
                            logger.error(BaseException.print(e));
                            errList.add(
                                "Ecforce受注連携のバッチ処理により、受注管理の登録が失敗しました。詳細はシステム担当者にお問い合わせください。" + " 受注ID:" + outerOrderNo);
                            errOrderClients.add(data);
                            continue;
                        }
                        try {
                            if (StringTools.isNullOrEmpty(msg)) {
                                // 受注明細登録
                                insertOrderDetail(orderNo, apiUrl, token, rels, map, initClientInfo);
                                logger.info("Ecforce受注連携OK 店舗ID:" + clientId + " 受注ID:" + outerOrderNo);
                            } else {
                                logger.warn("Ecforce受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc201)");
                            }
                        } catch (Exception e) {
                            subno++;
                            failureCnt++;
                            msg = "Ecforce受注の連携処理により、受注明細(tc201)の登録が失敗しました。" + "詳細はシステム担当者にお問い合わせください。";
                            apiCommonUtils.insertTc207OrderError(clientId, historyId, outerOrderNo, msg,
                                API.ECFORCE.getName());
                            logger.error("Ecforce受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc201)");
                            logger.error(BaseException.print(e));
                            errList.add(
                                "Ecforce受注連携のバッチ処理により、受注管理の登録が失敗しました。詳細はシステム担当者にお問い合わせください。" + " 受注ID:" + outerOrderNo);
                            errOrderClients.add(data);
                            continue;
                        }
                        // 自動出庫(1:自動出庫 0:出庫しない)
                        if (status == 1) {
                            apiCommonUtils.processShipment(orderNo, clientId);
                        }
                    }
                } else {
                    // キャンセル以外場合、処理 ※検索条件にはcanceledを含む必要
                    logger.warn("Ecforce受注連携NG" + " 店舗ID:" + clientId + " 受注ID:" + outerOrderNo
                        + " 原因:取込前の受注キャンセル");
                    total--;
                    // TODO 取込前の受注キャンセルを記録しない
                    apiCommonUtils.insertTc208OrderCancel(clientId, outerOrderNo, API.ECFORCE.getName());
                }
            }
            // 取り込まれていない場合、取込履歴を記録しない
            if (successCnt > 0) {
                apiCommonUtils.processOrderHistory(clientId, API.ECFORCE.getName(), historyId, successCnt, failureCnt);
            }
            logger.info("Ecforce受注連携 店舗I【{}】--連携件数(OK:{} NG:{}", clientId, successCnt, failureCnt);

            if (!errList.isEmpty()) {
                String key = clientId + "_API名：" + data.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        logger.info("Ecforce受注連携 終了 ");
        // mailTools.sendErrorMessage(errHashtable, API.ECFORCE.getName());
        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 受注詳細情報をサンロジに登録
     *
     * @param orderNo 受注番号
     * @param apiUrl URL
     * @param token トークン
     * @param rels 受注情報
     * @param map 受注情報マップ
     * @param initClientInfo クライアント情報
     * @author wang
     * @date 2021/1/16
     */
    private void insertOrderDetail(String orderNo, String apiUrl, String token,
        JSONObject rels, HashMap<String, String> map, InitClientInfoBean initClientInfo) {
        /* 受注明細 登録 */
        Tc201_order_detail tc201OrderDetail;
        // 店舗ID
        String clientId = initClientInfo.getClientId();
        // API番号
        Integer apiId = initClientInfo.getApiId();
        try {
            // tc201受注明細
            JSONArray orderItems = JSON.parseObject(rels.getString("order_items")).getJSONArray("data");
            int subNo;
            // 連続API管理ID
            for (int m = 0; m < orderItems.size(); m++) {
                tc201OrderDetail = new Tc201_order_detail();
                // 受注明細番号
                subNo = m + 1;
                // JSONから商品情報取得
                JSONObject order_item = orderItems.getJSONObject(m);
                String mapId3 = "order_item" + "-" + order_item.getString("id");
                JSONObject products = JSON.parseObject(map.get(mapId3));

                // 同梱物 TODO @Add 20210128 wang
                String bundledFlg = products.getString("product_bundled_item_id");
                // 商品コード(SKUコード)
                String code = products.getString("variant_sku");
                // 商品名
                String name = products.getString("product_name");
                // 商品単価()
                Integer price = products.getInteger("sales_price");
                // 軽減税率フラグ
                int isReducedTax = 0;
                // 税区分(0:税込 1:税抜)
                int taxFlag = 0;
                if (!"0".equals(products.getString("tax_rate"))) {
                    taxFlag = 1;
                    if ("8".equals(products.getString("tax_rate"))) {
                        isReducedTax = 1;
                    }
                }

                // 商品オプション
                String options = "";
                // 連携商品ID // TODO ディフォルト値をdefault値に設定するのが問題ないですか。
                String renkeiProductId = "default";
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
                if (mc100Product != null) {
                    // 商品新規登録
                    mc100Product = apiCommonUtils.insertMc100Product(productBean, API.ECFORCE.getName());
                    // 商品之前不存在 设定为仮登録
                    tc201OrderDetail.setProduct_kubun(9);
                }
                if (mc100Product != null) {
                    apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                        API.ECFORCE.getName());
                }
                if (mc100Product != null) {
                    // 商品ID
                    tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
                    // 商品名
                    tc201OrderDetail.setProduct_name(mc100Product.getName());
                    // 商品コード
                    tc201OrderDetail.setProduct_code(mc100Product.getCode());
                    // セットID
                    if (mc100Product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(mc100Product.getSet_sub_id())) {
                        tc201OrderDetail.setSet_sub_id(mc100Product.getSet_sub_id());
                    }
                    // 同梱物(0:なし 1:同梱物)
                    if (!StringTools.isNullOrEmpty(mc100Product.getBundled_flg())
                        && mc100Product.getBundled_flg() == 1) {
                        tc201OrderDetail.setBundled_flg(mc100Product.getBundled_flg());
                    } else {
                        if (!Strings.isNullOrEmpty(bundledFlg)) {
                            tc201OrderDetail.setBundled_flg(Integer.valueOf(bundledFlg));
                        }
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
                // 商品単価(販売価格) ※注意list_price通常価格 sales_price販売価格 商品価格price
                tc201OrderDetail.setUnit_price(price);
                // 商品数量
                int quantity = products.getInteger("quantity");
                tc201OrderDetail.setNumber(quantity);
                // 商品小計(販売価格*商品数量)
                int total_price = 0;
                BigDecimal bigTax = new BigDecimal("1.1");
                // 税抜の場合、販売価格の税金*商品数量)
                if (taxFlag == 1) {
                    if (isReducedTax == 1) {
                        // 税込8％商品金額
                        bigTax = new BigDecimal("1.08");
                    }
                    BigDecimal bigTotal = new BigDecimal(price * quantity);
                    total_price = bigTotal.multiply(bigTax).intValue();
                } else {
                    total_price = price * quantity;
                }
                // 商品小計(販売価格*商品数量)
                tc201OrderDetail.setProduct_total_price(total_price);
                // 税率
                tc201OrderDetail.setIs_reduced_tax(isReducedTax);
                // 税区分(0:税込 1:税抜)
                tc201OrderDetail.setTax_flag(taxFlag);
                tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
                tc201OrderDetail.setPurchase_order_no(orderNo);
                tc201OrderDetail.setDel_flg(0);
                try {
                    // 受注明細登録
                    orderDetailDao.insertOrderDetail(tc201OrderDetail);
                } catch (Exception e) {
                    logger.error(BaseException.print(e));
                    break;
                }
                // Ecforce倉庫ロケーションコード取得、mc106に更新(Option空値場合全て更新) ※廃止
                // getStockLocationCode(clientId, apiUrl, token, code);
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
        }
    }

    /**
     * 送り状番号の自動連携 (一時間毎)
     *
     * @author HZM
     * @date 2021/1/12
     */
    // @Scheduled(cron = "0 6/15 * * * ?")
    public void sendEcforceTrackingNm() {
        logger.info("Ecforce伝票連携 開始");
        int cnts = 0;
        int errs = 0;
        // Ecforce設定情報取得
        List<Tc203_order_client> allData = apiService.getAllDataDelivery(API.ECFORCE.getName());
        // Ecforce設定情報取得リストから処理開始
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client allDatum : allData) {
                // 店舗ID
                String clientId = allDatum.getClient_id();
                // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
                List<Tw200_shipment> shipEcList =
                    shipmentsService.getUntrackedShipments(clientId, API.ECFORCE.getName());

                if (shipEcList != null && shipEcList.size() > 0) {
                    // API連携情報
                    String apiUrl = allDatum.getClient_url();
                    String apiKey = allDatum.getApi_key();
                    String apiPws = allDatum.getPassword();
                    String apiNam = allDatum.getApi_name();
                    // 認証トークン
                    String token = getToken(clientId, apiUrl, apiKey, apiPws);
                    try {
                        // 初期化
                        StringBuilder param = new StringBuilder();
                        StringBuilder url = new StringBuilder();

                        Map<String, String> idsMap = new HashMap<>();
                        // 現在時刻取得
                        String nowTime = getNowTime();
                        // アクセスURL
                        url.append("https://").append(apiUrl).append("/api/v1/orders/shipping.json");

                        // パラメータ開始
                        param.append("{\"with_sale\":0, \"orders\":[");

                        for (int j = 0; j < shipEcList.size(); j++) {
                            // 配送業者ID
                            String delivery_id = shipEcList.get(j).getDelivery_id();
                            // 配送伝票番号
                            String tracking_nm = shipEcList.get(j).getDelivery_tracking_nm();
                            // 外部受注番号
                            String outer_order_no = shipEcList.get(j).getOuter_order_no();
                            String[] orderNo = outer_order_no.split("-");
                            String shipping_id = orderNo[1];

                            // Map(Key外部受注番号,出庫依頼ID:倉庫CD)にセットする
                            String values = shipEcList.get(j).getWarehouse_cd() + ":"
                                + shipEcList.get(j).getShipment_plan_id();
                            idsMap.put(shipping_id, values);

                            if (j > 0) {
                                param.append(",");
                            }
                            param.append("{\"id\":").append(shipping_id).append(",\"shipping_slip\":\"")
                                .append(tracking_nm).append("\",\"shipping_carrier_id\":").append(delivery_id)
                                .append(",\"shipped_at\":\"").append(nowTime).append("\"}");
                        }
                        // パラメータ終了
                        param.append("]}");

                        // リクエスト
                        String res = HttpUtils.sendJsonPost(String.valueOf(url), param, token);

                        if (!StringTools.isNullOrEmpty(res)) {
                            JSONObject obj = JSON.parseObject(res);

                            // 送り状番号の連携が正常の場合、送り状連携状況を「1:連携済」に変更
                            JSONArray success = obj.getJSONArray("success");
                            for (int m = 0; m < success.size(); m++) {
                                String id = success.getString(m);

                                if (!StringTools.isNullOrEmpty(id)) {
                                    String[] shipmentValues = idsMap.get(id).split(":");
                                    // 送り状連携状況を「1:連携済」に更新
                                    shipmentsService.setShipmentFinishFlg(clientId, shipmentValues[0],
                                        shipmentValues[1]);
                                    logger.info("ecforce伝票連携OK 店舗ID:" + clientId + " 出庫ID:" + shipmentValues[1]
                                        + " 外部受注ID:" + id);
                                    cnts++;
                                }
                            }
                        } else {
                            logger.error("ecforce伝票連携NG 店舗ID:" + clientId + " API名:" + apiNam);
                            int tmpErrs = shipEcList.size();
                            errs = errs + tmpErrs;
                        }
                    } catch (Exception e) {
                        logger.error("ecforce伝票連携NG 店舗ID:" + clientId + " API名:" + apiNam);
                        logger.error(BaseException.print(e));
                        errs++;
                    }
                }

            }
        }
        logger.info("ecforce伝票連携 終了--処理件数(OK:" + cnts + " NG:" + errs + ")");
    }

    /**
     * Ecforce在庫数自動連携 (10分ごと自動起動 5,15,25,35,45,55)
     */
    // @Scheduled(cron = "30 6/20 * * * ?")
    public void setEcProductCnt() {
        logger.info("Ecforce在庫連携 開始");
        List<Tc203_order_client> allApiData = apiService.getAllDataStock(API.ECFORCE.getName());
        int cnts = 0;
        if (allApiData != null && allApiData.size() > 0) {
            for (Tc203_order_client allApiDatum : allApiData) {
                // API連携情報
                String clientId = allApiDatum.getClient_id();
                String apiKey = allApiDatum.getApi_key();
                String apiUrl = allApiDatum.getClient_url();
                String passwd = allApiDatum.getPassword();
                Integer apiId = allApiDatum.getId();
                // 認証トークン
                String token = getToken(clientId, apiUrl, apiKey, passwd);
                // API連携取得
                List<Mc106_produce_renkei> allProductData = productService.getAllProductDataById(clientId, apiId);

                StringBuilder url = new StringBuilder();
                StringBuilder param = new StringBuilder();
                Map<String, String> map = new HashMap<>();

                if (allProductData != null && allProductData.size() > 0) {
                    // アクセスURL
                    url.append("https://").append(apiUrl).append("/api/v1/stock_items.json");

                    // パラメータ
                    param.append("{\"stock_items\":[");
                    try {
                        int flag = 0;
                        for (Mc106_produce_renkei allProductDatum : allProductData) {
                            // 在庫数
                            Integer productCnt;
                            // SKUコード
                            String variant_sku = allProductDatum.getRenkei_product_id();
                            String product_id = allProductDatum.getProduct_id();
                            // 在庫ロケーションコード
                            String stock_location_code = allProductDatum.getVariant_id();
                            // 理論在庫数のみ取得 (※available_cntのみ取得可、他の項目を取得できない ※要注意)
                            Tw300_stock productCnts = productService.getProductCnt(clientId, product_id);
                            // 有効在庫数が0以下場合、0とする
                            if (productCnts != null) {
                                // 配送可在庫数を取得 (※available_cntのみ取得可、他の項目を取得できない ※要注意)
                                productCnt = productCnts.getAvailable_cnt();
                                // 在庫TBLの店舗在庫数を更新
                                map.put(variant_sku, product_id);
                                if (flag > 0) {
                                    param.append(",");
                                }
                                param.append("{\"variant_sku\":\"").append(variant_sku)
                                    .append("\",\"stock_location_code\":\"").append(stock_location_code)
                                    .append("\",\"stock\":").append(productCnt).append("}");
                                flag++;
                            }
                        }
                        param.append("]}");

                        // リクエスト
                        String res = HttpUtils.sendJsonPut(url.toString(), param, token);
                        if (!StringTools.isNullOrEmpty(res)) {
                            JSONObject obj = JSON.parseObject(res);
                            // 在庫連携が正常の場合、送り状連携状況を「1:連携済」に変更
                            JSONArray success = obj.getJSONArray("success");
                            // 返却値がNull場合、処理スキップ
                            if (success != null && success.size() > 0) {
                                for (int m = 0; m < success.size(); m++) {
                                    String sku = success.getJSONObject(m).getString("variant_sku");
                                    String stock = success.getJSONObject(m).getString("stock");
                                    String sCode = success.getJSONObject(m).getString("stock_location_code");

                                    logger.info("ecforce在庫連携OK 店舗ID:" + clientId + " 商品CD:" + sku + " 在庫数:" + stock
                                        + " ロケCD：" + sCode);
                                    // 連携した在庫数を在庫TBLの店舗数を反映
                                    if (!StringTools.isNullOrEmpty(map.get(sku))) {
                                        int nums = 0;
                                        if (!StringTools.isNullOrEmpty(stock)) {
                                            nums = Integer.parseInt(stock);
                                        }
                                        productDao.updateStockStroeCnt(clientId, map.get(sku), nums, "ecforce",
                                            DateUtils.getDate());
                                    }
                                    // 総計件数
                                    cnts++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("ecforce在庫連携NG 店舗ID:" + clientId);
                        logger.error(BaseException.print(e));
                    }
                }
            }
        }
        logger.info("ecforce在庫連携 終了--連携件数:" + cnts);
    }

    /**
     * EcforceGMO連携 (10分ごと自動起動 5,15,25,35,45,55)
     */
    // //@Scheduled(cron = "0 5/10 * * * ?")
    public void setBillBarcode() {
        logger.info("ecforceGMO連携 開始");
        // ecforce店舗連携情報取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.ECFORCE.getName());
        // 連続情報がない場合、処理スキップ
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client allDatum : allData) {
                // 店舗情報取得
                String clientId = allDatum.getClient_id();
                // 店舗に関する請求情報を取得(備考「後払」含む)
                List<Tw200_shipment> date = shipmentsService.getGMOBillBarcode(clientId);
                // 出庫情報ない場合、処理スキップ
                if (date != null && date.size() > 0) {
                    for (Tw200_shipment tw200 : date) {
                        // 請求情報を取得
                        String payment_id = tw200.getPayment_id();
                        String billBarcode = tw200.getBill_barcode();
                        String orderNo = tw200.getOrder_no();
                        String shipmentId = tw200.getShipment_plan_id();

                        // 取引IDがない場合、処理スキップ
                        if (StringTools.isNullOrEmpty(payment_id)) {
                            continue;
                        }
                        // GMO連携APIにより、請求コードを取得
                        String newBillBarcode = getBillBarcode(payment_id, allDatum);
                        // 取得した請求コードと既存と同様の場合、更新しないこと
                        if (!Objects.equal(billBarcode, newBillBarcode)) {
                            shipmentsService.updateGMOBillBarcode(shipmentId, newBillBarcode);
                            logger.info("ecforceGMO連携 店舗ID:" + clientId
                                + " 受注番号:" + orderNo
                                + " 旧請求CD:" + billBarcode
                                + " 新請求CD:" + newBillBarcode);
                        } else {
                            logger.info("ecforceGMO連携 店舗ID:" + clientId
                                + " 受注番号:" + orderNo
                                + " 旧請求CD:" + billBarcode
                                + " 新請求CD: 変更なし");
                        }
                    }
                }
            }
        }
        logger.info("ecforceGMO連携 終了");
    }

    /**
     * Ecforce店舗の認証トークン取得 (每月1日的朝2時)
     *
     * @author HZM
     * @date 2021/1/12
     */
    // @Scheduled(cron = "0 0 2 1 * ?")
    public void setNewToken() {
        logger.info("ecforce新Token発行 開始");
        // ecforceAPI連携情報取得
        List<Tc203_order_client> allApiData = apiService.getAllData(API.ECFORCE.getName());
        if (allApiData != null && allApiData.size() > 0) {
            // @update(postリクエスト方法を改善) wang 20210303 Start
            // 新規トークン発行 */
            // @update(postリクエスト方法を改善) wang 20210303 Start
            for (Tc203_order_client allApiDatum : allApiData) {
                // API連携情報
                String clientId = allApiDatum.getClient_id();
                String apiKey = allApiDatum.getApi_key();
                String apiUrl = allApiDatum.getClient_url();
                String passwd = allApiDatum.getPassword();
                Integer apiId = allApiDatum.getId();
                /* @update(postリクエスト方法を改善) wang 20210303 Start */
                String token = allApiDatum.getToken();

                // 認証トークン発行
                // DBから取得できない場合、Ecforce店舗から取得
                try {
                    // アクセスURL
                    HttpUtils.sendGetEcforce("https://" + apiUrl + "/api/v1/admins/sign_out.json", token);
                    // 認証トークンをDBに空値として更新
                    productDao.setToken(clientId, apiUrl, apiKey, passwd, "");
                    logger.info("ecforce新Token発行OK 店舗ID:" + clientId + " アプリID:" + apiId);
                } catch (Exception e) {
                    logger.error("ecforce新Token発行の処理失敗");
                    logger.error(BaseException.print(e));
                }
            }
        }
        logger.info("ecforce認証Token更新 終了");
    }

    /**
     * Ecforce店舗の認証トークン取得 (每月1日的朝2時5分)
     *
     * @author wang
     * @date 2021/3/4
     */
    // @Scheduled(cron = "0 5 2 1 * ?")
    public void getNewToken() {
        logger.info("Ecforce新規トークン取得 開始");
        // ecforceAPI連携情報取得
        List<Tc203_order_client> allApiData = apiService.getAllData(API.ECFORCE.getName());
        if (allApiData != null && allApiData.size() > 0) {
            for (Tc203_order_client allApiDatum : allApiData) {
                // API連携情報
                String clientId = allApiDatum.getClient_id();
                String apiKey = allApiDatum.getApi_key();
                String apiUrl = allApiDatum.getClient_url();
                String passwd = allApiDatum.getPassword();
                Integer apiId = allApiDatum.getId();
                try {
                    // 最新認証トークンを取得、DBに更新する
                    getToken(clientId, apiUrl, apiKey, passwd);
                    logger.info("ecforce新Token取得OK 店舗ID:" + clientId + " アプリID:" + apiId);
                } catch (Exception e) {
                    logger.error("ecforce新Token取得の失敗");
                }
            }
        }
        logger.info("Ecforce新規トークン取得 終了");
    }

    /**
     * Ecforce店舗の商品情報取得 (毎日4時頃)
     *
     * @author HZM
     * @date 2021/1/12
     */
    // @Scheduled(cron = "0 0 4 * * ?")
    public void getEcProduct() {
        // TODO
        logger.info("Ecforce商品連携 開始");
        // 获取所有和ecforce连携的信息
        List<Tc203_order_client> allApiData = apiService.getAllData(API.ECFORCE.getName());
        int successCnt = 0;
        if (allApiData != null && allApiData.size() > 0) {
            for (Tc203_order_client allApiDatum : allApiData) {
                // API連携情報
                String clientId = allApiDatum.getClient_id();
                String apiKey = allApiDatum.getApi_key();
                String apiUrl = allApiDatum.getClient_url();
                String passwd = allApiDatum.getPassword();
                Integer apiId = allApiDatum.getId();
                // 認証トークン
                String token = getToken(clientId, apiUrl, apiKey, passwd);
                // 商品情報を取得
                for (int j = 1; j < 1000; j++) {
                    String url = "https://" + apiUrl + "/api/v1/products.json?" + "include=variants&page=" + j
                        + "&per=100";
                    String res;
                    try {
                        res = HttpUtils.sendGetEcforce(url, token);
                    } catch (Exception e) {
                        logger.error("Ecforce商品連携失敗。");
                        logger.error(BaseException.print(e));
                        continue;
                    }
                    JSONObject obj;
                    try {
                        obj = JSON.parseObject(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    JSONArray data = obj.getJSONArray("data");
                    JSONArray incs = obj.getJSONArray("included");

                    int cnt = data.size();
                    for (int m = 0; m < cnt; m++) {
                        // 商品名
                        JSONObject attrData = JSON.parseObject(data.getJSONObject(m).getString("attributes"));
                        JSONObject attrIncs = JSON.parseObject(incs.getJSONObject(m).getString("attributes"));
                        // Ecforce店舗の商品情報をサンロジに登録
                        String name = attrData.getString("name");
                        String code = attrIncs.getString("sku");
                        String price = attrIncs.getString("sales_price");
                        String options = ""; // TODO 商品オプションの値を空に設定するのが問題ないでしょうか。
                        // 新規商品として登録する
                        ProductBean productBean = new ProductBean();
                        productBean.setClientId(clientId);
                        productBean.setApiId(apiId);
                        productBean.setCode(code);
                        productBean.setName(name);
                        productBean.setPrice(String.valueOf(price));
                        productBean.setIsReducedTax(0);
                        productBean.setRenkeiPid("default");
                        Mc100_product mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
                        if (mc100Product == null) {
                            apiCommonUtils.insertMc100Product(productBean, API.ECFORCE.getName());
                        }
                        // Ecforce倉庫ロケーションコード取得、mc106に更新(Option空値場合全て更新)
                        getStockLocationCode(clientId, apiUrl, token, code);
                        logger.info("ecforce商品連携OK 店舗ID:" + clientId + " 商品CD:" + code + " 価格:" + price);
                    }
                    // 処理件数
                    successCnt = successCnt + cnt;
                    // 取得件数100以内場合、次の処理を停止
                    if (cnt < 100) {
                        break;
                    }
                }
                // Ecforce倉庫ロケーションコード取得、mc106に更新(Option空値場合全て更新)
                try {
                    getStockLocationCode(clientId, apiUrl, token, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // TODO
        logger.info("Ecforce商品連携 終了--処理件数:" + successCnt);
    }

    /**
     * Ecforce倉庫ロケ取得更新
     *
     * @param clientId 店舗ID
     * @param url URL
     * @param token 認証トークン
     * @param option 在庫数ロケ指定(空値：全部取得)
     * @author wang
     * @date 2021/1/16
     */
    private void getStockLocationCode(String clientId, String url, String token, String option) {
        // Ecforce倉庫ロケーションコード取得、mc106に更新
        StringBuilder getUrl = new StringBuilder();
        getUrl.append("https://").append(url).append("/api/v1/stock_items.json");
        // optionが空の場合、全て在庫数を取得
        if (!StringTools.isNullOrEmpty(option)) {
            getUrl.append("?q[variant_sku_eq]=").append(option);
        }
        String res = "";
        try {
            res = HttpUtils.sendGetEcforce(getUrl.toString(), token);
        } catch (Exception e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        // Retry later
        if (!StringTools.isNullOrEmpty(res) && !("Retry later".equals(res))) {
            JSONObject obj = JSON.parseObject(res);
            // 在庫連携する場合、ロケーションコード必要のため、取得する
            JSONArray data = obj.getJSONArray("data");
            if (data != null && data.size() > 0) {
                for (int j = 0; j < data.size(); j++) {
                    String items = data.getJSONObject(j).getString("attributes");
                    JSONObject attr = JSON.parseObject(items);
                    String variant_sku = attr.getString("variant_sku");
                    String stock_location_code = attr.getString("stock_location_code");
                    productService.setLocationCode(clientId, variant_sku, stock_location_code);
                    logger.info("Ecforce商品情報更新OK 店舗ID【{}】 商品コード【{}】 ロケーションコード【{}】", clientId, variant_sku,
                        stock_location_code);
                }
            }
        } else {
            logger.warn("Ecforceロケ取得失敗 原因:" + res);
        }
    }

    /**
     * 認証トークン取得
     *
     * @param clientUrl 店舗URL
     * @param apiKey APIキー
     * @param passwd パスワード
     * @return String トークン
     * @author wang
     * @date 2021/1/16
     */
    private String getToken(String clientId, String clientUrl, String apiKey, String passwd) {
        // 認証トークン取得
        String token = productDao.getToken(clientId, clientUrl, apiKey, passwd);
        // DBから取得できない場合、Ecforce店舗から取得
        if (StringTools.isNullOrEmpty(token)) {
            try {
                Map<String, String> param = new HashMap<>();
                // パラメータ設定
                param.put("api_admin[email]", apiKey);
                param.put("api_admin[password]", passwd);
                // リクエスト
                JSONObject jsonObject =
                    HttpUtils.sendPostEcforce("https://" + clientUrl + "/api/v1/admins/sign_in.json", param, token);
                token = jsonObject.getString("authentication_token");
                // token取得できない場合、更新が行わない
                if (!StringTools.isNullOrEmpty(token)) {
                    // 取得した情報をDBに更新
                    productDao.setToken(clientId, clientUrl, apiKey, passwd, token);
                } else {
                    // token取得できない場合、ログ記録
                    logger.warn("ecforceトークン取得失敗 店舗ID：" + clientId + " API:" + clientUrl);
                }
            } catch (Exception e) {
                logger.error("ecforceから認証Tokenの取得失敗");
                logger.error(BaseException.print(e));
            }
        }
        return "Token token=\"" + token + "\"";
    }

    /**
     * 検索オプションを設定(注文状況)
     *
     * @param values 検索値
     * @return String 検索条件
     * @author wang
     * @date 2021/8/05
     */
    private String strToOptionsState(String values) {
        String key = "&q[state_in][]=";
        if (StringTools.isNullOrEmpty(values)) {
            // 注文状況の検索条件を取得する
            values = "complete,amazonpay,gmosg,gmopg";
        }
        return strToOptions(key, values);
    }

    /**
     * 検索オプションを設定(決済状況)
     *
     * @param values 検索値
     * @return String 検索条件
     * @author wang
     * @date 2021/8/05
     */
    private String strToOptionsPaymentState(String values) {
        String key = "&q[payment_state_in][]=";
        if (StringTools.isNullOrEmpty(values)) {
            // 決済状況の検索条件を取得
            values = "completed,authed,credit_exam_completed,void,cash_on_delivery_authed";
        }
        return strToOptions(key, values);
    }

    /**
     * 検索オプションを設定
     *
     * @param key 検索キーワード
     * @param values 検索値
     * @return List<String> 検索条件
     * @author wang
     * @date 2021/1/16
     */
    private String strToOptions(String key, String values) {
        // 戻り値
        String options = "";
        if (!StringTools.isNullOrEmpty(values)) {
            if (!values.startsWith(",")) {
                values = "," + values;
            }
            if (!values.endsWith(",")) {
                values = values + ",";
            }
            String[] arr = values.split(",");
            if (arr.length > 0) {
                options = String.join(key, arr);
            }
        }
        return options;
    }
}
