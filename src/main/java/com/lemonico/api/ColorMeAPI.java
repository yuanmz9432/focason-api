package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.core.enums.TaxType;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.OrderService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * カラーミーAPI連携
 * <p>
 * APIドキュメント：https://developer.shop-pro.jp/docs/colorme-api
 *
 * <p>
 * ①受注自動取込む
 * <p>
 * ②入金ステータス連携
 * <p>
 * ③伝票番号自動連携
 * <p>
 * ④在庫連携（開発中）
 */
@Component
@EnableScheduling
public class ColorMeAPI
{

    private final static Logger logger = LoggerFactory.getLogger(ColorMeAPI.class);
    // 受注データのリスト取得URL
    private final static String SALES_URL = "https://api.shop-pro.jp/v1/sales";
    // 配送方法のリスト取得URL
    private final static String DELIVERIES_URL = "https://api.shop-pro.jp/v1/deliveries";
    // 支払方法のリスト取得URL
    private final static String PAYMENTS_URL = "https://api.shop-pro.jp/v1/payments";
    // ショップ情報の取得URL
    private final static String SHOP_URL = "https://api.shop-pro.jp/v1/shop";
    // AUTHORIZATION
    private final static String AUTHORIZATION = "Authorization";
    // 受注データ取得最大件数
    private final static int LIMIT = 100;
    // 処理失敗
    private final static String NG = "NG";
    // テンプレートID（ms_013_api_templateテーブルに参照）
    private final static String COLOR_ME_TEMPLATE_ID = "カラーミーショップAPI連携";
    @Resource
    private OrderApiService apiService;
    @Resource
    private OrderApiDao orderApiDao;
    @Resource
    private OrderHistoryDao orderHistoryDao;
    @Resource
    private OrderCancelDao orderCancelDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private OrderDao orderDao;
    @Resource
    private APICommonUtils apiCommonUtils;
    @Resource
    private ShipmentsService shipmentsService;
    @Resource
    private OrderService orderService;

    /**
     * ColorMe受注データ自動連携 (10分ごと自動起動)
     *
     * @author YuanMingZe
     * @date 2021/06/25 12:31
     */
    // @Scheduled(cron = "0 3/10 * * * ?")
    public void fetchColorMeOrders() {
        logger.info("===========================================");
        logger.info("ColorMe受注連携　開始");
        apiCommonUtils.initialize();
        // **********************************************************
        // ********************** API情報取得 *************************
        // **********************************************************
        List<Tc203_order_client> clients = apiService.getAllDataOrder(API.COLORME.getName());
        if (Collections.isEmpty(clients)) {
            logger.info("ColorMe受注連携 店舗情報：0件");
            logger.info("ColorMe受注連携 終了");
            logger.info("===========================================");
            return;
        }
        logger.info("ColorMe受注連携 店舗情報：{}件", clients.size());
        for (Tc203_order_client client : clients) {
            logger.info("ColorMe受注連携 店舗情報：【{}】 処理開始", client.getClient_id());
            // **********************************************************
            // ******************** 店舗やAPIなどの情報 ********************
            // **********************************************************
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(client);
            // 店舗ID
            String clientId = initClientInfo.getClientId();
            // アクセストークン
            String accessToken = client.getAccess_token();
            // **********************************************************
            // ********** APIによる配送方法、支払方法、店舗設定定義取得 *********
            // **********************************************************
            // 配送方法（ColorMe定義）
            JSONArray deliveries = null;
            // 支払方法（ColorMe定義）
            JSONArray payments = null;
            // 店舗設定（ColorMe定義）
            JSONObject shop = null;
            // レスポンスの文字列
            String responseStr;
            // レスポンス
            JSONObject response;
            try {
                // ColorMeで配送情報：一つ受注データに対して、複数配送情報を定義できる
                responseStr = HttpClientUtils.sendGet(DELIVERIES_URL, AUTHORIZATION, accessToken, null);
                response = JSONObject.parseObject(responseStr);
                if (Objects.isNull(response)) {
                    logger.error("店舗ID【{}】の配送方法定義の取得APIが失敗しました。", clientId);
                    continue;
                }
                deliveries = response.getJSONArray("deliveries");
            } catch (Exception e) {
                logger.error("店舗ID【{}】の配送情報の取得APIが失敗しました。", clientId);
                logger.error(BaseException.print(e));
            }
            try {
                responseStr = HttpClientUtils.sendGet(PAYMENTS_URL, AUTHORIZATION, accessToken, null);
                response = JSONObject.parseObject(responseStr);
                if (Objects.isNull(response)) {
                    logger.error("店舗ID【{}】の支払方法定義の取得APIが失敗しました。", clientId);
                    continue;
                }
                payments = response.getJSONArray("payments");
            } catch (Exception e) {
                logger.error("店舗ID【{}】の支払方法定義の取得APIが失敗しました。", clientId);
                logger.error(BaseException.print(e));
            }
            try {
                responseStr = HttpClientUtils.sendGet(SHOP_URL, AUTHORIZATION, accessToken, null);
                response = JSONObject.parseObject(responseStr);
                if (Objects.isNull(response)) {
                    logger.error("店舗ID【{}】の店舗定義の取得APIが失敗しました。", clientId);
                    continue;
                }
                shop = response.getJSONObject("shop");
            } catch (Exception e) {
                logger.error("店舗ID【{}】の店舗情報の取得APIが失敗しました。", clientId);
                logger.error(BaseException.print(e));
            }
            initClientInfo.setPayments(payments);
            initClientInfo.setDeliveries(deliveries);
            initClientInfo.setShop(shop);
            // **********************************************************
            // ******************* APIによる受注データ取得 ******************
            // **********************************************************
            // 取得開始位置
            int offset = 0;
            // 実行回数（一回最大100件）
            int count = 1;
            // 取得した件数
            int totalSales;
            do {
                // 初期化
                JSONArray orders;
                String url = SALES_URL + "?limit=" + LIMIT + "&offset=" + offset;
                String bikou1 = client.getBikou1();
                if (!StringTools.isNullOrEmpty(bikou1)) {
                    int timeUnit = Integer.parseInt(bikou1);
                    // 検索範囲：受注時間を設定
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.MINUTE, timeUnit * -1);
                    Date time = startTime.getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // 検索範囲：受注開始時間
                    String startOrdered = simpleDateFormat.format(time);
                    url += "&after=" + startOrdered;
                }
                logger.info("ColorMe受注連携 店舗情報：【{}】 受注取得URL:【{}】ヘッダー：【{}】", client.getClient_id(), url,
                    AUTHORIZATION + ":" + accessToken);
                // 店舗の受注情報を取得
                try {
                    responseStr = HttpClientUtils.sendGet(url, AUTHORIZATION, accessToken, null);
                    response = JSONObject.parseObject(responseStr);
                    if (!StringTools.isNullOrEmpty(response)) {
                        // 受注データ
                        orders = response.getJSONArray("sales");
                        // 受注データなしの場合
                        if (Collections.isEmpty(orders)) {
                            logger.info("店舗ID:【{}】の受注データ取得件数は0件です。", clientId);
                            break;
                        }
                    } else {
                        logger.info("店舗ID:【{}】の受注データは存在しないので、次の店舗の受注データを読込んて行く。", clientId);
                        break;
                    }
                } catch (Exception e) {
                    logger.error("店铺ID：【{}】の【{}】API設定はまちがえました。", clientId, client.getApi_name());
                    logger.error(BaseException.print(e));
                    break;
                }
                // 取得した件数
                totalSales = orders.size();
                // 受注データを書込
                processSalesData(orders, initClientInfo);
                logger.info("ColorMe APIの受注自動連携 【{}】回目。取得した件数は【{}】件。取得開始件数【{}】", count++, orders.size(), offset);
                offset += LIMIT;
            } while (totalSales == LIMIT);
            logger.info("ColorMe受注連携 店舗情報：【{}】 処理終了", client.getClient_id());
        }
        logger.info("ColorMe APIによる受注自動連携　終了");
        logger.info("===========================================");
    }

    /**
     * ColorMeの受注データの読み込む処理を行う
     *
     * @param orders 取得した受注データ
     * @param initClientInfoBean クライアント共通パラメータ
     * @author YuanMingZe
     * @date 2021/06/29 13:31
     */
    private void processSalesData(JSONArray orders, InitClientInfoBean initClientInfoBean) {
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 共通カラム値取得
        String clientId = initClientInfoBean.getClientId();
        Integer historyId = initClientInfoBean.getHistoryId();
        // 取得した受注データ総数
        int orderCnt = orders.size();
        // 処理した受注データ総数
        int totalCnt = 0;
        // 成功総数
        int successCnt = 0;
        // 失敗総数
        int failureCnt = 0;
        // 受注番号枝番
        int orderSubNo = 1;
        for (int idx = 0; idx < orderCnt; idx++) {
            JSONObject order = orders.getJSONObject(idx);
            // 外部受注番号（ColorMeの受注番号）
            Integer orderId = order.getInteger("id");
            // **********************************************************
            // ************* キャンセルされた受注データを処理する **************
            // **********************************************************
            // 受注がキャンセルされたかどうか確認、キャンセルされた場合、tc_208テーブルに挿入して、次の受注データを処理する
            boolean isCanceled = order.getBoolean("canceled");
            if (isCanceled) {
                try {
                    insertOrderCancel(clientId, orderId);
                } catch (Exception e) {
                    logger.info("店舗ID【{}】の受注データ：【{}】はキャンセルされたので、tc_208テーブルに挿入する際に、異常が発生しました。", clientId, orderId);
                    logger.info(BaseException.print(e));
                }
                logger.info("店舗ID【{}】の受注データ：【{}】はキャンセルされたので、次の受注データを読込んで続けていく。", clientId, orderId);
                successCnt++;
                totalCnt++;
                continue;
            }
            // **********************************************************
            // ***************** 取込済受注データを処理する ******************
            // **********************************************************
            // 受注番号も存在するかどうかを確認する
            Integer outerOrderNo = orderDao.getOuterOrderNo(String.valueOf(orderId), clientId);
            // 受注番号が存在した場合、次の受注番号を読込む
            if (outerOrderNo > 0) {
                logger.warn("ColorMe受注連携NG 店舗ID【{}】　受注ID：【{}】 原因:過去受注取込済", clientId, orderId);
                successCnt--;
                totalCnt--;
                continue;
            }
            // **********************************************************
            // ****************** 新規受注データを処理する *******************
            // **********************************************************
            // 処理結果を扱うため、resultMapを定義する。resultMapでstatusとmessageというキーを追加する。
            // status OK/NG
            // message エラーメッセージ
            HashMap<String, String> resultMap;
            initClientInfoBean.setSubNo(orderSubNo++);
            try {
                resultMap = processSaleData(order, initClientInfoBean);
            } catch (Exception e) {
                // processSaleDeliveries只抛出预见外异常，并将异常记录到log当中，然后执行下一次循环。其他的业务异常会将异常内容
                logger.error("店舗ID【{}】の受注データ書き込む失敗！受注番号：【{}】", clientId, orderId);
                logger.error(BaseException.print(e));
                continue;
            }
            if (!Collections.isEmpty(resultMap) && NG.equals(resultMap.get("status"))) {
                String message = resultMap.get("message");
                logger.error("店舗ID【{}】の受注データ書き込む失敗！受注番号：【{}】原因：【{}】", clientId, orderId, message);
                failureCnt++;
            }
            if (Collections.isEmpty(resultMap)) {
                logger.info("店舗ID【{}】の受注データ書き込む成功！受注番号：【{}】", clientId, orderId);
                successCnt++;
            }
            totalCnt++;
        }
        // **********************************************************
        // ************* 書き込む結果を履歴テーブルに保存する **************
        // **********************************************************
        if (successCnt <= 0) {
            logger.info("ColorMe受注自動連携成功件数は【{}】件。", successCnt);
        } else {
            // 生成した受注履歴beanに情報を格納
            // 写入履历表
            Tc202_order_history order_history = new Tc202_order_history();
            order_history.setHistory_id(historyId);
            Date nowTime = DateUtils.getDate();
            // 取込時間
            order_history.setImport_datetime(nowTime);
            order_history.setClient_id(clientId);
            // 取込件数
            order_history.setTotal_cnt(totalCnt);
            // 成功件数
            order_history.setSuccess_cnt(successCnt);
            // 失敗件数
            order_history.setFailure_cnt(failureCnt);
            // ms_013_api_templateテーブルに参照
            order_history.setBiko01(COLOR_ME_TEMPLATE_ID);
            try {
                orderHistoryDao.insertOrderHistory(order_history);
            } catch (Exception e) {
                // processSaleDeliveries只抛出预见外异常，并将异常记录到log当中，然后执行下一次循环。其他的业务异常会将异常内容
                logger.error("店舗ID【{}】の受注履歴挿入失敗！履历番号：【{}】", clientId, historyId);
                logger.error(BaseException.print(e));
            }
            logger.info("店舗ID【{}】の受注履歴挿入成功！履历番号：【{}】", clientId, historyId);
        }
    }

    /**
     * ColorMeの受注配送データを処理する
     *
     * @param order 新規受注データ
     * @param initClientInfoBean 共通パラメータ
     * @return Map<String, String> 結果マップ（キー：status:OK/NG,message:異常メッセージ）
     * @author YuanMingZe
     * @date 2021/06/29 13:31
     */
    private HashMap<String, String> processSaleData(JSONObject order, InitClientInfoBean initClientInfoBean) {
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 結果マップ
        HashMap<String, String> resultMap = new HashMap<>();
        // 共通パラメータの値を取得
        if (Objects.isNull(initClientInfoBean)) {
            resultMap.put("status", "NG");
            resultMap.put("message", "共通パラメータ(commonData)が存在しないので、処理終了。");
            return resultMap;
        }
        // 店舗ID
        String clientId = initClientInfoBean.getClientId();
        // 自動出庫依頼ステータス
        Integer shipmentStatus = initClientInfoBean.getShipmentStatus();
        // 受注識別フラグ
        String identification = initClientInfoBean.getIdentification();
        // 受注の配送情報をループして、配送情報毎で受注テーブルにレコードを挿入する
        if (Objects.isNull(order)) {
            resultMap.put("status", "NG");
            resultMap.put("message", "受注データ(order)が存在しないので、処理終了。");
            return resultMap;
        }
        // 受注番号(ColorMe採番)
        Integer orderId = order.getInteger("id");
        // **********************************************************
        // ******************* 受注配送データを読込む *******************
        // **********************************************************
        // 受注配送情報取得
        JSONArray saleDeliveries = order.getJSONArray("sale_deliveries");
        if (Collections.isEmpty(saleDeliveries)) {
            resultMap.put("status", "NG");
            resultMap.put("message", "受注番号【" + orderId + "】の配送情報が存在しないので、処理終了。");
            return resultMap;
        }
        int orderSubNo = initClientInfoBean.getSubNo();
        // 配送情報取得（一番目の配送情報を取る）
        JSONObject saleDelivery = saleDeliveries.getJSONObject(0);
        // 受注番号(店舗template CM001-YYYYMMDDHHMM-00001)(SunLogiの受注番号)
        String purchaseOrderNo = CommonUtils.getOrderNo(orderSubNo, identification);
        // **********************************************************
        // ********************* 受注データを読込む *********************
        // **********************************************************
        try {
            resultMap = setTc200Order(order, saleDelivery, initClientInfoBean, purchaseOrderNo);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            resultMap.put("status", "NG");
            resultMap.put("message", "Tc200_orderテーブルに受注管理レコードを挿入する時、意外の原因で登録失敗しました。");
            return resultMap;
        }
        // エラーがあれば返す
        if (!Objects.isNull(resultMap)) {
            return resultMap;
        }
        // **********************************************************
        // ******************* 受注明細データを読込む *******************
        // **********************************************************
        // 受注明細の配列
        JSONArray details = order.getJSONArray("details");
        // 受注明細番号の枝番
        int orderDetailSubNo = 1;
        if (Collections.isEmpty(details)) {
            logger.info("この配送方法に対する商品が存在しないので、次の処理に続く。");
        }
        for (int idx = 0; idx < details.size(); idx++) {
            try {
                // 受注明細データをTc201_order_detailテーブルに挿入する
                resultMap = setTc201OrderDetail(details.getJSONObject(idx), saleDelivery, initClientInfoBean,
                    purchaseOrderNo, orderDetailSubNo++);
            } catch (Exception e) {
                logger.error(BaseException.print(e));
                assert resultMap != null;
                resultMap.put("status", "NG");
                resultMap.put("message",
                    "ColorMe受注明細挿入失敗 店舗ID【" + clientId + "】 受注ID：【" + orderId + "】 原因：意外の原因で受注明細の登録失敗しました。");
                // 受注管理レコードを削除する
                orderApiDao.deleteTc200OrderByOuterOrderNo(clientId, orderId);
                return resultMap;
            }
        }
        if (!Objects.isNull(resultMap)) {
            return resultMap;
        }
        // 自動出庫依頼(1：出庫する 0:出庫しない)
        if (shipmentStatus == 1) {
            apiCommonUtils.processShipment(purchaseOrderNo, clientId);
        }
        logger.info("ColorMe受注連携OK 店舗ID:【{}】 受注ID【{}】", clientId, orderId);
        return null;
    }

    /**
     * ColorMeの受注管理レコードを保存する
     *
     * @param order 受注データ
     * @param saleDelivery 受注配送データ
     * @param initClientInfoBean 共通パラメータ
     * @param purchaseOrderNo 受注番号（SunLogi採番）
     * @return Map<String, String> 結果マップ
     * @author YuanMingZe
     * @date 2021/06/29 13:31
     */
    private HashMap<String, String> setTc200Order(JSONObject order, JSONObject saleDelivery,
        InitClientInfoBean initClientInfoBean, String purchaseOrderNo) {
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 共通カラムの値を取得
        // 店舗ID
        String clientId = initClientInfoBean.getClientId();
        // 倉庫コード
        String warehouseCd = initClientInfoBean.getWarehouseCd();
        // ディフォルト配送方法
        String deliveryMethod = initClientInfoBean.getDefaultDeliveryMethod();
        // 履歴ID
        Integer historyId = initClientInfoBean.getHistoryId();
        // 配送方法（ColorMe定義）
        JSONArray deliveries = initClientInfoBean.getDeliveries();
        // 支払方法（ColorMe定義）
        JSONArray payments = initClientInfoBean.getPayments();
        // 受注番号（ColorMe採番）
        Integer orderId = order.getInteger("id");
        // 結果マップ
        HashMap<String, String> resultMap = new HashMap<>();
        // 配送情報取得
        Tc200_order tc200Order = new Tc200_order();
        // **********************************************************
        // ***************** Tc200_orderレコードを作成 *****************
        // **********************************************************
        // 受注番号(店舗template bs001-YYYYMMDDHHMM-00001)(SunLogiの受注番号)
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
        // 配送情報設置
        setTc200OrderDelivery(saleDelivery, initClientInfoBean, deliveries, tc200Order, deliveryMethod);
        // 支払方法設置
        String paymentId = setTc200OrderPayment(order.getInteger("payment_id"),
            initClientInfoBean.getMs007SettingPaymentMap(), payments, tc200Order);
        tc200Order.setPayment_method(paymentId);
        // **********************************************************
        // *********************** 依頼主情報 *************************
        // **********************************************************
        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
        Ms012_sponsor_master ms012Sponsor = initClientInfoBean.getMs012sponsor();
        if (!Objects.isNull(ms012Sponsor)) {
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
        Integer orderDatetime = order.getInteger("make_date");
        if (orderDatetime > 0) {
            Timestamp timestamp = new Timestamp(Long.valueOf(orderDatetime) * 1000);
            tc200Order.setOrder_datetime(timestamp);
        }
        // 備考
        String memoByColorMe = saleDelivery.getString("memo");
        // フリー項目1の入力内容
        String answerFreeForm1 = saleDelivery.getString("answer_free_form1");
        // フリー項目2の入力内容
        String answerFreeForm2 = saleDelivery.getString("answer_free_form2");
        // フリー項目3の入力内容
        String answerFreeForm3 = saleDelivery.getString("answer_free_form3");
        String memo = (memoByColorMe == null ? "" : memoByColorMe) +
            (answerFreeForm1 == null ? "" : answerFreeForm1) +
            (answerFreeForm2 == null ? "" : answerFreeForm2) +
            (answerFreeForm3 == null ? "" : answerFreeForm3);
        tc200Order.setMemo(memo);
        // 備考フラグ
        tc200Order.setBikou_flg(StringTools.isNullOrEmpty(memo) ? 0 : 1);
        // **********************************************************
        // ********************** 商品の金額計算 ***********************
        // **********************************************************
        // 商品金額(税込)
        Integer productPriceExcludingTax = order.getInteger("product_total_price");
        tc200Order.setProduct_price_excluding_tax(productPriceExcludingTax);
        // 消費税合計
        Integer taxTotal = order.getInteger("tax");
        tc200Order.setTax_total(taxTotal);
        // 手数料(税込)
        Integer handlingCharge = order.getInteger("fee");
        tc200Order.setHandling_charge(handlingCharge);
        // その他金額(割引)point_discount + gmo_point_discount + other_discount
        Integer pointDiscount = order.getInteger("point_discount");
        Integer gmoPointDiscount = order.getInteger("gmo_point_discount");
        Integer otherDiscount = order.getInteger("other_discount");
        Integer otherFee = pointDiscount + gmoPointDiscount + otherDiscount;
        tc200Order.setOther_fee(otherFee);
        // 送料(税込)
        Integer deliveryTotal = order.getInteger("delivery_total_charge");
        tc200Order.setDelivery_total(deliveryTotal);
        // 合計金額(税込)
        Integer billingTotal = order.getInteger("total_price");
        tc200Order.setBilling_total(billingTotal);
        // 注文種別 0:入金待ち 1:入金済み
        int orderType = order.getBoolean("paid") ? 1 : 0;
        // 代引引換の場合、入金待ちを入金済みとする
        if ("2".equals(paymentId)) {
            tc200Order.setCash_on_delivery_fee(billingTotal);
            orderType = 1;
        }
        // 注文種別 0:入金待ち 1:入金済み
        tc200Order.setOrder_type(orderType);

        // **********************************************************
        // *********************** 注文者情報 *************************
        // **********************************************************
        // 受注顧客
        JSONObject customer = order.getJSONObject("customer");
        // 注文者姓
        String orderFamilyName = customer.getString("name");
        tc200Order.setOrder_family_name(orderFamilyName);
        // 注文者姓カナ
        String orderFamilyKana = customer.getString("furigana");
        tc200Order.setOrder_family_kana(orderFamilyKana);
        // 注文者郵便番号
        String orderZipCode = customer.getString("postal");
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
        String orderPref = customer.getString("pref_name");
        tc200Order.setOrder_todoufuken(orderPref);
        // 注文者住所郡市区
        String orderAddress1 = customer.getString("address1");
        tc200Order.setOrder_address1(orderAddress1);
        // 注文者詳細住所
        String orderAddress2 = customer.getString("address2");
        tc200Order.setOrder_address2(orderAddress2);
        // 注文者部署
        String orderDivision = customer.getString("busho");
        tc200Order.setOrder_division(orderDivision);
        // 注文者電話番号
        List<String> list = CommonUtils.checkPhoneToList(customer.getString("tel"));
        if (list.size() != 0) {
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
        Integer orderGender = "male".equals(customer.getString("sex")) ? 1 : 0;
        tc200Order.setOrder_gender(orderGender);
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        // 个口数
        tc200Order.setBoxes(1);


        // 作成者、作成日、更新者、更新日を設定する
        tc200Order.setIns_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setIns_usr(clientId);
        tc200Order.setUpd_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setUpd_usr(clientId);
        try {
            // 受注管理表に書き込み
            orderDao.insertOrder(tc200Order);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            resultMap.put("status", "NG");
            resultMap.put("message", "ColorMe受注連携NG 店舗ID：【" + clientId + "】 受注ID：【" + orderId + "】 原因：受注管理の登録失敗");
            return resultMap;
        }
        return null;
    }

    /**
     * ColorMeの受注明細レコードを保存する
     *
     * @param detail 商品詳細
     * @param saleDelivery 配送情報
     * @param initClientInfoBean 共通パラメータ
     * @param purchaseOrderNo 注文番号（SunLOGI採番）
     * @param orderDetailSubNo 注文明細枝番
     * @return HashMap<String, String> 結果マップ
     * @author YuanMingZe
     * @date 2021/06/28 12:31
     */
    private HashMap<String, String> setTc201OrderDetail(JSONObject detail, JSONObject saleDelivery,
        InitClientInfoBean initClientInfoBean, String purchaseOrderNo, Integer orderDetailSubNo) {
        // **********************************************************
        // ************************ 初期化 ***************************
        // **********************************************************
        // 受注番号（SunLogi採番）
        // 店舗ID
        String clientId = initClientInfoBean.getClientId();
        // API番号
        Integer apiId = initClientInfoBean.getApiId();
        // 結果マップ
        HashMap<String, String> resultMap = new HashMap<>();
        // **********************************************************
        // ************** Tc201_order_detailレコード作成 **************
        // **********************************************************
        // Tc201_order_detailレコード
        Tc201_order_detail tc201OrderDetail = new Tc201_order_detail();
        String orderDetailNo = purchaseOrderNo + "-" + String.format("%03d", orderDetailSubNo);
        // 受注明細番号（SunLogi採番）
        tc201OrderDetail.setOrder_detail_no(orderDetailNo);
        // 受注番号
        tc201OrderDetail.setPurchase_order_no(purchaseOrderNo);
        // ラッピングの表示名
        String wrappingName1 = saleDelivery.getString("wrapping_name");
        tc201OrderDetail.setWrapping_name1(wrappingName1);
        // ラッピングの料金
        Integer wrappingPrice1 = saleDelivery.getInteger("wrapping_charge");
        tc201OrderDetail.setWrapping_price1(wrappingPrice1);
        // **********************************************************
        // ********************* 対象商品マッピング *********************
        // **********************************************************
        Mc100_product mc100Product;
        // ColorMe側の商品オプション１
        String option1Value = detail.getString("option1_value");
        // ColorMe側の商品オプション２
        String option2Value = detail.getString("option2_value");
        // 商品IDで商品mc110_product_optionsに対象商品があるがどうかをチェックする
        StringBuilder options = new StringBuilder();
        if (!StringTools.isNullOrEmpty(option1Value)) {
            options.append(option1Value);
            if (!StringTools.isNullOrEmpty(option2Value)) {
                options.append(",");
                options.append(option2Value);
            }
        }
        String name = detail.getString("product_name");
        String code;
        if (!StringTools.isNullOrEmpty(detail.getString("product_model_number"))) {
            code = detail.getString("product_model_number");
        } else {
            code = detail.getString("product_id");
        }

        String renkeiPid = detail.getString("product_id");
        // 税抜き
        Integer price = detail.getInteger("price");
        // 税込み
        Integer priceWithTax = detail.getInteger("price_with_tax");
        int isReducedTax = 0;
        tc201OrderDetail.setIs_reduced_tax(isReducedTax);
        try {
            // **********************************************************
            // ********************* 共通パラメータ整理 *********************
            // **********************************************************
            ProductBean productBean = new ProductBean();
            productBean.setClientId(clientId);
            productBean.setCode(code);
            productBean.setName(name);
            productBean.setApiId(apiId);
            productBean.setPrice(String.valueOf(priceWithTax));
            productBean.setIsReducedTax(isReducedTax);
            productBean.setRenkeiPid(renkeiPid);
            productBean.setOptions(options.toString());
            // Mc100_productテーブルの既存商品をマッピング
            mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options.toString());
            // 商品登録されていない場合、商品マスタに仮商品として新規登録
            if (Objects.isNull(mc100Product)) {
                // 商品新規登録
                mc100Product = apiCommonUtils.insertMc100Product(productBean, API.COLORME.getName());
                // 商品之前不存在 设定为仮登録
                tc201OrderDetail.setProduct_kubun(9);
            }
            if (!Objects.isNull(mc100Product)) {
                apiCommonUtils.insertMc106Product(clientId, apiId, renkeiPid, mc100Product, API.COLORME.getName());
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
                throw new BaseException(ErrorCode.E_11005);
            }
        } catch (Exception e) {
            logger.error("Rakuten受注連携NG 店舗ID:{} 受注明細ID:{} 原因:商品登録失敗", clientId, tc201OrderDetail.getOrder_detail_no());
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.E_11005);
        }
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
        // 仮登録 默认为0
        int kubun = 0;
        Integer productKubun = mc100Product.getKubun();
        if (!StringTools.isNullOrEmpty(productKubun)) {
            kubun = productKubun;
        }
        tc201OrderDetail.setProduct_kubun(kubun);
        tc201OrderDetail.setUnit_price(price);
        // 店舗消費税設定によって、内税⇒税込み、外税⇒税抜き
        tc201OrderDetail.setTax_flag(TaxType.EXCLUDED.getValue());
        if (TaxType.INCLUDED.getLabel().equals(initClientInfoBean.getShop().getString("tax_type"))) {
            tc201OrderDetail.setTax_flag(TaxType.INCLUDED.getValue());
        }
        // 個数
        tc201OrderDetail.setNumber(detail.getInteger("product_num"));
        // 商品計
        tc201OrderDetail.setProduct_total_price(detail.getInteger("subtotal_price"));
        // 削除フラグ
        tc201OrderDetail.setDel_flg(0);
        // **********************************************************
        // ******************** 受注明細レコード挿入 ********************
        // **********************************************************
        try {
            orderDetailDao.insertOrderDetail(tc201OrderDetail);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            resultMap.put("status", "NG");
            resultMap.put("message", "ColorMe受注明細挿入 受注明細ID:【" + orderDetailNo + "】原因:受注明細登録失敗しました。");
            return resultMap;
        }
        return null;
    }

    /**
     * キャンセルされた受注データをTc208_order_cancelテーブルに挿入また更新する
     *
     * @param clientId 店舗ID
     * @param outerOrderNo 外部連携番号
     * @author YuanMingZe
     * @date 2021/06/28 12:31
     */
    private void insertOrderCancel(String clientId, Integer outerOrderNo) {
        Tc208_order_cancel orderCancel = new Tc208_order_cancel();
        Tw200_shipment tw200Ships = orderCancelDao.getShipmentList(clientId, String.valueOf(outerOrderNo));
        // 初期化
        String shipmentPlanId = "COLORME-ORDER-CANCEL";
        String insUsr = "cancel";
        // 処理状況(0:未確認 1確認済)
        int status = 1;
        if (tw200Ships != null) {
            shipmentPlanId = tw200Ships.getShipment_plan_id();
            insUsr = "colorme";
            status = 0;
        }
        orderCancel.setClient_id(clientId);
        orderCancel.setOuter_order_no(String.valueOf(outerOrderNo));
        orderCancel.setShipment_plan_id(shipmentPlanId);
        orderCancel.setStatus(status);
        // 7:COLORME
        orderCancel.setBikou1(COLOR_ME_TEMPLATE_ID);
        // 取込前の受注キャンセルも記録(cancel)
        Integer cnt = orderCancelDao.getOrderCancel(clientId, String.valueOf(outerOrderNo), shipmentPlanId);
        if (!StringTools.isNullOrEmpty(cnt) && cnt == 0) {
            orderCancel.setIns_usr(insUsr);
            orderCancel.setIns_date(new Timestamp(System.currentTimeMillis()));
            // 受注キャンセルを記録
            orderCancelDao.insertOrderCancel(orderCancel);
        } else {
            orderCancel.setUpd_usr(insUsr);
            orderCancel.setUpd_date(new Timestamp(System.currentTimeMillis()));
            // 受注キャンセルを更新
            orderCancelDao.updateOrderCancel(orderCancel);
        }

    }

    /**
     * 支払方法を設定する
     *
     * @param paymentId 支払方法ID
     * @param ms007SettingList 店舗側設定マスタ
     * @param payments ColorMeの支払方法定義
     * @param tc200Order 受注管理レコード
     * @author YuanMingZe
     * @date 2021/06/28 15:30
     */
    private String setTc200OrderPayment(
        Integer paymentId, Map<String, String> ms007SettingList, JSONArray payments, Tc200_order tc200Order) {
        // 決済方法名
        String paymentName = null;
        // 支払方法
        String paymentMethod;
        // APIの結果から決済名取得
        for (int i = 0; i < payments.size(); i++) {
            JSONObject payment = payments.getJSONObject(i);
            if (paymentId.equals(payment.getInteger("id"))) {
                paymentName = payment.getString("name");
                // 元の支払方法を備考10に保存する
                tc200Order.setBikou10(paymentName);
                break;
            }
        }
        paymentMethod = apiCommonUtils.getPaymentMethod(paymentName, API.COLORME.getName(), ms007SettingList);
        return paymentMethod;
    }

    /**
     * 配送方法を設定する
     *
     * @param saleDelivery 配送情報
     * @param initClientInfoBean クライアント共通パラメータ
     * @param deliveries 配送マスタ（ColorMeから取得）
     * @param tc200Order 受注管理レコード
     * @param defaultDeliveryMethod ディフォルト配送方法
     * @author YuanMingZe
     * @date 2021/06/28 15:30
     */
    private void setTc200OrderDelivery(JSONObject saleDelivery, InitClientInfoBean initClientInfoBean,
        JSONArray deliveries, Tc200_order tc200Order, String defaultDeliveryMethod) {
        // 送り状番号連携ID
        String related_order_no = saleDelivery.getString("id");
        tc200Order.setRelated_order_no(related_order_no);
        // ColorMeの使用された配送方法ID
        Integer deliveryId = saleDelivery.getInteger("delivery_id");
        // ColorMeの配達希望時間帯
        String preferredPeriod = saleDelivery.getString("preferred_period");
        // 配送方法名
        String deliveryName = "";
        // APIの結果から配送名取得
        for (int i = 0; i < deliveries.size(); i++) {
            JSONObject delivery = deliveries.getJSONObject(i);
            if (deliveryId.equals(delivery.getInteger("id"))) {
                deliveryName = delivery.getString("name");
                // 元の配送方法を備考9に保存する
                tc200Order.setBikou9(deliveryName);
            }
        }
        // 配送情報取得
        Ms004_delivery ms004Delivery = apiCommonUtils.getDeliveryMethod(deliveryName, defaultDeliveryMethod,
            initClientInfoBean.getMs007SettingDeliveryMethodMap());
        if (!Objects.isNull(ms004Delivery)) {
            // 配送会社指定
            tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
            // 配送方法
            tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
            // 配送方法名称を特殊指定名称に変更する
            // deliveryName = ms004Delivery.getDelivery_method_name();
            deliveryName = ms004Delivery.getDelivery_nm();
        }
        // SunLogi配達希望時間帯
        String deliveryTimeSlot = apiCommonUtils.getDeliveryTimeSlot(deliveryName, preferredPeriod,
            API.COLORME.getName(), initClientInfoBean.getMs007SettingTimeMap());
        // 配送先郵便番号
        String receiverZipCode = saleDelivery.getString("postal");
        if (!StringTools.isNullOrEmpty(receiverZipCode)) {
            String formatZip = CommonUtils.formatZip(receiverZipCode);
            boolean isContained = formatZip.contains("-");
            if (isContained) {
                String[] splitString = formatZip.split("-");
                // 配送先郵便番号1
                tc200Order.setReceiver_zip_code1(splitString[0].trim());
                // 配送先郵便番号2
                tc200Order.setReceiver_zip_code2(splitString[1].trim());
            }
        }
        // 配送先住所都道府県
        String receiverPref = saleDelivery.getString("pref_name");
        tc200Order.setReceiver_todoufuken(receiverPref);
        // 配送先住所郡市区
        String receiverAddress1 = saleDelivery.getString("address1");
        tc200Order.setReceiver_address1(receiverAddress1);
        // 配送先詳細住所
        String receiverAddress2 = saleDelivery.getString("address2");
        tc200Order.setReceiver_address2(receiverAddress2);
        // 配送先姓
        String receiverFamilyName = saleDelivery.getString("name");
        tc200Order.setReceiver_family_name(receiverFamilyName);
        // 配送先姓カナ
        String receiverFamilyKana = saleDelivery.getString("furigana");
        tc200Order.setReceiver_family_kana(receiverFamilyKana);
        // 配送先電話番号
        String receiverPhoneNumber = saleDelivery.getString("tel");
        List<String> list = CommonUtils.checkPhoneToList(receiverPhoneNumber);
        if (list.size() != 0) {
            // 注文者電話番号1
            tc200Order.setReceiver_phone_number1(list.get(0));
            // 注文者電話番号2
            tc200Order.setReceiver_phone_number2(list.get(1));
            // 注文者電話番号3
            tc200Order.setReceiver_phone_number3(list.get(2));
        }
        // 配達希望時間帯
        tc200Order.setDelivery_time_slot(deliveryTimeSlot);
        // 配達希望日
        String deliveryDate = saleDelivery.getString("preferred_date");
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            Date date = DateUtils.stringToDate(deliveryDate);
            tc200Order.setDelivery_date(date);
        }
        // 取込日時
        Timestamp import_datetime = new Timestamp(System.currentTimeMillis());
        tc200Order.setImport_datetime(import_datetime);
    }

    /**
     * 支払ステータス自動連携 (30分ごと自動起動 3, 33)
     *
     * @author YuanMingZe
     * @date 2021/07/09
     */
    // @Scheduled(cron = "0 3/30 * * * ?")
    public void fetchPaymentStatus() {
        logger.info("===========================================");
        logger.info("ColorMe API受注取込(入金待ち)自動連携 開始");
        // **********************************************************
        // ********************** API情報取得 *************************
        // **********************************************************
        List<Tc203_order_client> clients = apiService.getAllDataOrder(API.COLORME.getName());
        if (Collections.isEmpty(clients)) {
            logger.info("ColorMe API利用する店舗数：0件");
            logger.info("ColorMe API受注取込(入金待ち)自動連携 終了");
            logger.info("===========================================");
            return;
        }
        logger.info("ColorMe API利用する店舗数：{}件", clients.size());
        // ColorMeに関する情報を全て処理
        for (Tc203_order_client client : clients) {
            // **********************************************************
            // ************************ 初期化 ***************************
            // **********************************************************
            List<Tw200_shipment> tw200Shipments;
            // 店舗ID
            String clientId = client.getClient_id();
            // 受注データ取得 取得条件：該当店舗、受注データの支払ステータスが未入金、del_flgが0の場合
            try {
                tw200Shipments = orderDao.getUnPaidOrder(clientId, API.COLORME.getName());
            } catch (Exception e) {
                logger.info("店舗ID【{}】の出庫依頼請求情報取得失敗しました。次の店舗に処理続く。", clientId);
                continue;
            }
            // **********************************************************
            // ****************** 出庫管理レコード取得 **********************
            // **********************************************************
            // 出庫管理レコードが存在しなければ、次の処理を続く
            if (Collections.isEmpty(tw200Shipments)) {
                logger.info("店舗ID【{}】の出庫依頼請求情報は存在しないので、次の店舗に処理続く。", clientId);
                continue;
            }
            // 外部受注IDをStringにまとめる 例：133890592,133905782
            List<String> orderNosList =
                tw200Shipments.stream().map(Tw200_shipment::getOrder_no).collect(Collectors.toList());
            String orderNosStr = StringUtils.strip(orderNosList.toString(), "[]");
            // **********************************************************
            // *************** APIによる受注データを取得する *****************
            // **********************************************************
            String url = SALES_URL + "?ids=" + orderNosStr;
            url = url.replace(" ", "");
            String accessToken = client.getAccess_token();
            // GetでColorMeから注文情報を取得
            JSONObject response;
            // レスポンスの文字列
            String responseStr;
            // 注文情報
            JSONArray sales;
            try {
                responseStr = HttpClientUtils.sendGet(url, AUTHORIZATION, accessToken, null);
            } catch (Exception e) {
                logger.error("店舗ID:【{}】の受注データ【{}】取得失敗しました。", clientId, orderNosStr);
                logger.error(BaseException.print(e));
                continue;
            }
            if (responseStr != null) {
                responseStr = responseStr.trim();
            }
            response = JSONObject.parseObject(responseStr);
            if (!StringTools.isNullOrEmpty(response)) {
                // 受注データ
                sales = response.getJSONArray("sales");
                // 受注データなしの場合
                if (Collections.isEmpty(sales)) {
                    logger.info("店舗ID:【{}】の受注データ【{}】は存在しないので、次の受注データを取得続く", clientId, orderNosStr);
                    continue;
                }
            } else {
                logger.info("店舗ID:【{}】の受注データは存在しないので、次の受注データを取得続く", clientId);
                continue;
            }
            // **********************************************************
            // ******************* 出庫ステータス更新 ***********************
            // **********************************************************
            TW200: for (Tw200_shipment tw200Shipment : tw200Shipments) {
                // 倉庫コード
                String warehouseCd = tw200Shipment.getWarehouse_cd();
                // 出庫ID
                String shipmentPlanId = tw200Shipment.getShipment_plan_id();
                // 受注番号（ColorMe採番）
                String orderNo = tw200Shipment.getOrder_no();
                for (int idx = 0; idx < sales.size(); idx++) {
                    JSONObject sale = sales.getJSONObject(idx);
                    // 受注番号をマッピング
                    if (!orderNo.equals(String.valueOf(sale.getInteger("id")))) {
                        continue;
                    }
                    // 支払ステータス取得（入金待ち：false 入金済み：true）
                    boolean paid = sale.getBoolean("paid");
                    if (!paid) {
                        continue TW200;
                    }

                    try {
                        orderService.upOrderStatus(clientId, orderNo, warehouseCd, shipmentPlanId, null);
                        logger.info("ColorMe受注連携 店舗ID:" + clientId + " 受注ID:" + orderNo
                            + " 入金待ちを入金済みに変更");
                    } catch (Exception e) {
                        logger.error(BaseException.print(e));
                        logger.error("店舗ID【{}】出庫依頼番号【{}】の出庫ステータス更新失敗しました。", clientId, shipmentPlanId);
                    }
                    continue TW200;
                }
            }
        }
        logger.info("ColorMe APIによる支払ステータス自動連携 終了");
        logger.info("===========================================");
    }

    /**
     * 送り状番号自動連携 (15分ごと自動起動 0, 15, 30, 45)
     *
     * @author YuanMingZe
     * @date 2021/07/29 19:11
     */
    // @Scheduled(cron = "0 2/15 * * * ?")
    public void processTrackingNo() {
        logger.info("===========================================");
        logger.info("ColorMe APIによる伝票番号自動連携 開始");
        // **********************************************************
        // ********************** API情報取得 *************************
        // **********************************************************
        List<Tc203_order_client> clients = apiService.getAllDataDelivery(API.COLORME.getName());
        if (Collections.isEmpty(clients)) {
            logger.info("ColorMe API利用する店舗数：0件");
            logger.info("ColorMe APIによる伝票番号自動連携 終了");
            logger.info("===========================================");
            return;
        }
        logger.info("ColorMe API利用する店舗数：{}件", clients.size());
        for (Tc203_order_client client : clients) {
            // **********************************************************
            // ************************ 初期化 ***************************
            // **********************************************************
            // 店舗ID
            String clientId = client.getClient_id();
            // アクセストークン
            String accessToken = client.getAccess_token();
            // 識別番号
            String identifier = client.getIdentification();
            // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
            List<Tw200_shipment> tw200shipments;
            try {
                tw200shipments = orderDao.getRkShipmentInfoByIdentifier(identifier, clientId);
            } catch (Exception e) {
                logger.error("店舗ID【{}】出庫依頼レコードの取得は失敗しました。", clientId);
                logger.error(BaseException.print(e));
                continue;
            }
            if (Collections.isEmpty(tw200shipments)) {
                logger.info("店舗ID【{}】の条件満たすレコードが存在しない。", clientId);
                continue;
            }
            // **********************************************************
            // ********************* 伝票番号連携 *************************
            // **********************************************************
            for (Tw200_shipment shipment : tw200shipments) {
                // 外部の配送者管理ID
                String slip_id = shipment.getRelated_order_no();
                // 送り状番号
                String trackingNo = shipment.getDelivery_tracking_nm();
                // 出庫ID
                String shipment_plan_id = shipment.getShipment_plan_id();
                // 倉庫CD
                String warehouse_cd = shipment.getWarehouse_cd();
                // 受注データ更新URLを組み立てる http://api.shop-pro.jp/v1/sales/{sale_id}
                String url = SALES_URL + "/" + shipment.getOrder_no();
                StringBuilder param = new StringBuilder();
                param.append("{\"sale\":{\"sale_deliveries\": [{\"id\": ")
                    .append(slip_id)
                    .append(",\"slip_number\": \"")
                    .append(trackingNo)
                    .append("\",\"delivered\": true}]}}");

                // リクエスト
                try {
                    String resStr = HttpUtils.sendJsonPut(url, param, accessToken);
                    JSONObject res = JSONObject.parseObject(resStr);
                    JSONObject sales = res.getJSONObject("sale");
                    JSONArray sale_deliveries = sales.getJSONArray("sale_deliveries");
                    String slip_number = sale_deliveries.getJSONObject(0).getString("slip_number");
                    // カラーミーショップから更新成功の場合、送り状番号と比較、一致する場合、更新成功として更新
                    if (!StringTools.isNullOrEmpty(slip_number) && slip_number.equals(trackingNo)) {
                        // 連携フラグを更新
                        shipmentsService.setShipmentFinishFlg(clientId, warehouse_cd, shipment_plan_id);
                        logger.info("ColorMe伝票連携OK 店舗ID:{} 出庫ID:{} 送り状番号:{}", clientId, shipment_plan_id, trackingNo);
                    } else {
                        logger.warn("ColorMe伝票連携NG 店舗ID:{} 出庫ID:{} 送り状番号:{}", clientId, shipment_plan_id, trackingNo);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("ColorMe APIによる伝票番号自動連携 終了");
        logger.info("===========================================");
    }
}
