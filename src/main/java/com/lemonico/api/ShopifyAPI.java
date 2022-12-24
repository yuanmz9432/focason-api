package com.lemonico.api;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.HttpUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * ShopifyのAPI連携機能
 *
 * @author YuanMingZe
 * @className ShopifyAPI
 * @date 2021/06/30
 **/
@Component
@EnableScheduling
public class ShopifyAPI
{

    private final static Logger logger = LoggerFactory.getLogger(ShopifyAPI.class);
    // Shopify 的配送方法对应的代号
    private static final HashMap<String, String> prefectureMap = new HashMap<>();

    static {
        prefectureMap.put("Hokkaidō", "北海道");
        prefectureMap.put("Aomori", "青森県");
        prefectureMap.put("Iwate", "岩手県");
        prefectureMap.put("Miyagi", "宮城県");
        prefectureMap.put("Akita", "秋田県");
        prefectureMap.put("Yamagata", "山形県");
        prefectureMap.put("Fukushima", "福島県");
        prefectureMap.put("Ibaraki", "茨城県");
        prefectureMap.put("Tochigi", "栃木県");
        prefectureMap.put("Gunma", "群馬県");
        prefectureMap.put("Saitama", "埼玉県");
        prefectureMap.put("Chiba", "千葉県");
        prefectureMap.put("Tokyo", "東京都");
        prefectureMap.put("Tōkyō", "東京都");
        prefectureMap.put("Kanagawa", "神奈川県");
        prefectureMap.put("Niigata", "新潟県");
        prefectureMap.put("Toyama", "富山県");
        prefectureMap.put("Ishikawa", "石川県");
        prefectureMap.put("Fukui", "福井県");
        prefectureMap.put("Yamanashi", "山梨県");
        prefectureMap.put("Nagano", "長野県");
        prefectureMap.put("Gifu", "岐阜県");
        prefectureMap.put("Shizuoka", "静岡県");
        prefectureMap.put("Aichi", "愛知県");
        prefectureMap.put("Mie", "三重県");
        prefectureMap.put("Shiga", "滋賀県");
        prefectureMap.put("Kyōto", "京都府");
        prefectureMap.put("Ōsaka", "大阪府");
        prefectureMap.put("Hyōgo", "兵庫県");
        prefectureMap.put("Nara", "奈良県");
        prefectureMap.put("Wakayama", "和歌山県");
        prefectureMap.put("Tottori", "鳥取県");
        prefectureMap.put("Shimane", "島根県");
        prefectureMap.put("Okayama", "岡山県");
        prefectureMap.put("Hiroshima", "広島県");
        prefectureMap.put("Yamaguchi", "山口県");
        prefectureMap.put("Tokushima", "徳島県");
        prefectureMap.put("Kagawa", "香川県");
        prefectureMap.put("Ehime", "愛媛県");
        prefectureMap.put("Kōchi", "高知県");
        prefectureMap.put("Fukuoka", "福岡県");
        prefectureMap.put("Saga", "佐賀県");
        prefectureMap.put("Nagasaki", "長崎県");
        prefectureMap.put("Kumamoto", "熊本県");
        prefectureMap.put("Ōita", "大分県");
        prefectureMap.put("Miyazaki", "宮崎県");
        prefectureMap.put("Kagoshima", "鹿児島県");
        prefectureMap.put("Okinawa", "沖縄県");
    }

    // APIバージョン
    private final String VERSION = "2022-01";
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
    private ShipmentsService shipmentsService;
    @Resource
    private APICommonUtils apiCommonUtils;

    /**
     * Shopify受注自動取込(10分ごと自動起動 1,11,21,31,41,51)
     *
     * @date 2020/9/9
     */
    // @Scheduled(cron = "0 1/10 * * * ?")
    public void fetchShopifyOrders() {
        logger.info("Shopify受注連携 開始");
        // shopify連携情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.SHOPIFY.getName());
        // Shopifyに関するAPI情報が存在しない場合、処理中止
        if (Collections.isEmpty(allData)) {
            logger.info("Shopify受注連携 店舗情報：0件");
            logger.info("Shopify受注連携 終了");
            return;
        }
        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        // 初期化
        apiCommonUtils.initialize();
        // 各店舗の受注連携を開始
        for (Tc203_order_client data : allData) {
            // 保存错误数据
            ArrayList<String> errList = new ArrayList<>();

            // 初期化
            JSONArray orders;
            String link;
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(data);

            // Shopify連携APIのURL
            StringBuilder urlKey = new StringBuilder();
            urlKey.append("https://");
            urlKey.append(data.getClient_url());
            urlKey.append("/admin/api/" + VERSION);
            urlKey.append("/orders.json?limit=250&status=any");

            // 控制循环遍历的flg
            boolean finishFlg = true;
            // ループ回数
            int count = 1;
            String param = null;
            while (finishFlg) {
                String url = count > 1 ? urlKey + "&" + param : urlKey.toString();
                // 请求Shopify Api接口 获取数据
                JSONObject orderJson =
                    HttpUtils.sendHttpsGet(url, "X-Shopify-Access-Token", data.getPassword(), errList);
                if (orderJson == null) {
                    break;
                }
                // 添加 , 为了便于分割
                link = orderJson.getString("link");
                String pageInfo;
                boolean stopFlg = false;
                if (!Strings.isNullOrEmpty(link)) {
                    if (link.contains(",")) {
                        link = link.split(",")[1];
                    }
                    String[] splitLink = link.split(";");
                    if (splitLink[1].contains("next")) {
                        pageInfo = splitLink[0];
                        // <>を外す
                        String newUrl = pageInfo.substring(1, pageInfo.length() - 1);
                        System.out.println(newUrl);
                        param = newUrl.split("&")[1];
                        stopFlg = true;
                    }
                }
                if (!stopFlg) {
                    finishFlg = false;
                }
                // 受注データを処理
                orders = orderJson.getJSONArray("orders");
                dataProcessing(orders, initClientInfo, errList, errOrderClients, data);
                count++;
            }
        }
        logger.info("shopify受注連携 終了");
        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 受注データの処理を実施
     *
     * @param orders : 受注情報
     * @param initClientInfo : 店舗情報
     * @date 2021/6/17 16:02
     */
    public void dataProcessing(JSONArray orders, InitClientInfoBean initClientInfo, List<String> errList,
        List<Tc203_order_client> errOrderClients, Tc203_order_client data) {
        // Shopify連携API情報
        String apiNm = initClientInfo.getApiName();
        String clientId = initClientInfo.getClientId();
        Integer historyId = initClientInfo.getHistoryId();
        // 受注件数を取得しない場合、処理スキップ
        if (orders == null || orders.size() == 0) {
            logger.info("Shopify受注連携OK 店舗ID:{} API名:{} 受注件数：0件", clientId, apiNm);
        } else {
            // 取込件数
            int total = orders.size();
            // 成功件数
            int successCnt = 0;
            // 失敗件数
            int failureCnt = 0;
            logger.info("Shopify受注連携OK 店舗ID:{} API名:{} 受注件数：{}", clientId, apiNm, total);
            // 受注番号の枝番
            int subno = 1;
            for (int j = 0; j < orders.size(); j++) {
                // 受注情報取得
                JSONObject order = orders.getJSONObject(j);
                // 識別番号(Shopify店舗の注文番号(#無くす)+Shopify内部管理ID)
                String spOrderId = order.getString("name").replace("#", "") + "-" + order.getString("id");
                // orderNoList.add(spOrderId);
                Integer outerOrderNo = orderDao.getOuterOrderNo(spOrderId, clientId);
                // キャンセル発生するかを確認
                // JSONArray refunds = order.getJSONArray("refunds");

                // 過去受注番号に対してのキャンセルが発生した場合
                if ("refunded".equals(order.getString("financial_status"))) {
                    total--;
                    logger.warn("Shopify受注連携NG 店舗ID:{} 受注ID:{} 原因:受注キャンセル", clientId, spOrderId);
                    // 過去受注が受注キャンセルがある場合、記録
                    apiCommonUtils.insertTc208OrderCancel(clientId, spOrderId, API.SHOPIFY.getName());
                }

                // Shopify注文番号がない場合、処理
                if (outerOrderNo == 0) {
                    // 決済状況確認(pending 決済未払 ※代引引換 一時対応)
                    // "gateway": "Cash on Delivery (COD)",
                    if ("pending".equals(order.getString("financial_status"))) {
                        if (!"Cash on Delivery (COD)".equals(order.getString("gateway"))) {
                            logger.warn("Shopify受注連携NG 店舗ID:{} 受注ID:{} 原因:決済未支払", clientId, spOrderId);
                            // 受注連携が失敗する場合、失敗内容を記録
                            // String msg = "支払ステータスが未支払のため、受注取込の対象外となり、Shopify店舗側でご確認ください。";
                            // apiCommonUtils.insertTc207OrderError(clientId, historyId, spOrderId, msg,
                            // API.SHOPIFY.getName());
                            // failureCnt++;// 失敗
                            total--;
                            continue;
                        }
                    }
                    if ("refunded".equals(order.getString("financial_status"))) {
                        logger.warn("Shopify受注連携NG 店舗ID:{} 受注ID:{} 原因:取込前の受注キャンセル", clientId, spOrderId);
                        total--;
                        // 取込前の受注キャンセルを記録しない
                        apiCommonUtils.insertTc208OrderCancel(clientId, spOrderId, API.SHOPIFY.getName());
                        continue;
                    }
                    // **********************************************************
                    // ********************* 共通パラメータ整理 *********************
                    // **********************************************************
                    try {
                        // 受注子番号をセットする ※重要
                        initClientInfo.setSubNo(subno);
                        // 受注出庫依頼
                        List<String> res = setTc200Json(order, initClientInfo);
                        // 受注明細sub番号利用
                        subno++;
                        // 成功かどうか
                        if (!Collections.isEmpty(res)) {
                            failureCnt++;// 失敗
                            logger.warn("Shopify受注連携NG 店舗ID:{} 受注ID:{} 原因:受注データ不正", clientId, spOrderId);
                            // 受注連携が失敗する場合、失敗内容を記録
                            apiCommonUtils.insertTc207OrderError(clientId, historyId, spOrderId, res.get(0),
                                API.SHOPIFY.getName());
                        } else {
                            successCnt++;// 成功
                            logger.info("Shopify受注連携OK 店舗ID:{} 受注ID:{}", clientId, spOrderId);
                        }
                    } catch (Exception e) {
                        subno++;
                        failureCnt++;
                        // 受注連携が失敗する場合、失敗内容を記録
                        String msg = "Shopify受注連携のバッチ処理が失敗しましたので、システム担当者にお問い合わせください。";
                        apiCommonUtils.insertTc207OrderError(clientId, historyId, spOrderId, msg,
                            API.SHOPIFY.getName());
                        logger.error(BaseException.print(e));
                        errList.add("Shopify受注連携のバッチ処理が失敗しましたので、システム担当者にお問い合わせください。受注ID" + spOrderId);
                        errOrderClients.add(data);
                    }
                }
            }
            // 取り込まれていない場合、取込履歴を記録しない
            if (successCnt > 0) {
                apiCommonUtils.processOrderHistory(clientId, API.SHOPIFY.getName(), historyId, successCnt, failureCnt);
            }
            logger.info("Shopify受注連携 店舗ID:{} 取込件数:{} 成功件数:{} 失败件数:{}", clientId, total, successCnt, failureCnt);
        }
    }

    /**
     * 送り状番号の自動連携 (15分ごと自動起動 00,15,30,45)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // //@Scheduled(cron = "0 0/15 * * * ?")
    public void sendShopifyTrackingNm() {
        logger.info("shopify伝票連携 開始");
        // shopify情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> allData = apiService.getAllDataDelivery("shopify");
        int cnts = 0;
        int errs = 0;
        // 情報取得できない場合、処理スキップ
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client allDatum : allData) {
                // 店舗API情報
                String clientId = allDatum.getClient_id();
                String apiUrl = allDatum.getClient_url();
                String apiPws = allDatum.getPassword();
                // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
                List<Tw200_shipment> shopifyList = shipmentsService.getUntrackedShipments(clientId, "shopify");

                if (shopifyList != null && shopifyList.size() > 0) {

                    // 店舗設定の全て情報
                    for (Tw200_shipment tw200_shipment : shopifyList) {
                        // 初期化
                        StringBuilder url1 = new StringBuilder();
                        StringBuilder url2 = new StringBuilder();
                        String sendUrl = "";
                        JSONArray locations;

                        try {
                            String order_no = tw200_shipment.getOrder_no();
                            if (order_no.contains("-")) {
                                String[] arr = order_no.split("-");
                                order_no = arr[1];
                            }
                            // Shopify側ローテーション情報取得
                            url1.append("https://");
                            url1.append(apiUrl);
                            url1.append("/admin/api/" + VERSION);
                            url1.append("/locations.json");

                            JSONObject jsonObject =
                                HttpUtils.sendHttpsGet(url1.toString(), "X-Shopify-Access-Token", apiPws, null);
                            locations = jsonObject.getJSONArray("locations");

                            // ロケ値を取得できない場合、処理せず
                            if (!StringTools.isNullOrEmpty(locations) && locations.size() > 0) {
                                String tracking_nms = tw200_shipment.getDelivery_tracking_nm();
                                // 伝票番号を送信(POST)
                                url2.append("https://").append(apiUrl).append("/admin/api/").append(VERSION)
                                    .append("/orders/")
                                    .append(order_no).append("/fulfillments.json");
                                switch (tw200_shipment.getDelivery_nm()) {
                                    case "ヤマト運輸":
                                        sendUrl = "https://jizen.kuronekoyamato.co.jp/jizen/servlet/crjz.b.NQ0010?id=";
                                        break;
                                    case "佐川急便":
                                        sendUrl = "https://k2k.sagawa-exp.co.jp/p/web/okurijosearch.do?okurijoNo=";
                                        break;
                                    case "日本郵便":
                                        sendUrl =
                                            "https://trackings.post.japanpost.jp/services/srv/search/direct?org.apache.struts.taglib.html.TOKEN=&searchKind=S002&locale=ja&SVID=&reqCodeNo1=";
                                        break;
                                    case "福山通運":
                                        sendUrl = "https://corp.fukutsu.co.jp/situation/tracking_no_hunt";
                                        break;
                                    case "西濃運輸":
                                        sendUrl = "https://track.seino.co.jp/cgi-bin/gnpquery.pgm?GNPNO1=";
                                        break;
                                    default:
                                        break;
                                }
                                for (int k = 0; k < locations.size(); k++) {
                                    StringBuilder param = new StringBuilder();
                                    String location_id = locations.getJSONObject(k).getString("id");
                                    param.append("{\"fulfillment\":{\"location_id\":").append(location_id);
                                    if (tracking_nms.contains(",")) {
                                        param.append(",\"tracking_numbers\":[");
                                        String[] tracking_nm = tracking_nms.split(",");
                                        StringBuilder sendUrls = new StringBuilder();
                                        for (int m = 0; m < tracking_nm.length; m++) {
                                            param.append("\"").append(tracking_nm[m]).append("\"");
                                            sendUrls.append(sendUrl);
                                            if (!tw200_shipment.getDelivery_nm().equals("福山通運")) {
                                                sendUrls.append(tracking_nm[m]);
                                            }
                                            if (m != tracking_nm.length - 1) {
                                                param.append(",");
                                                sendUrls.append("\",\"");
                                            }
                                        }
                                        param.append("],\"tracking_urls\":[\"").append(sendUrls);
                                    } else {
                                        param.append(",\"tracking_number\":\"").append(tracking_nms);
                                        param.append("\",\"tracking_urls\":[\"").append(sendUrl);
                                        if (!tw200_shipment.getDelivery_nm().equals("福山通運")) {
                                            param.append(tracking_nms);
                                        }
                                    }
                                    param.append("\"],\"notify_customer\":true}}");
                                    // 伝票番号を送信
                                    String res = HttpUtils.sendJsonPost(url2.toString(), param,
                                        "X-Shopify-Access-Token", apiPws);
                                    int success_flg = res.indexOf("success");
                                    // 更新が成功した場合、API連携を連携済に変更
                                    if (success_flg != -1) {
                                        shipmentsService.setShipmentFinishFlg(allDatum.getClient_id(),
                                            tw200_shipment.getWarehouse_cd(),
                                            tw200_shipment.getShipment_plan_id());
                                        cnts++;
                                        logger.info("shopify伝票連携OK 店舗ID:{} 受注ID:{} ロケID:{} 伝票ID:{}", clientId,
                                            tw200_shipment.getOrder_no(), location_id, tracking_nms);
                                    } else {
                                        logger.warn("shopify伝票連携OK 店舗ID:{} 受注ID:{} ロケID:{} 伝票ID:{} 原因:{}", clientId,
                                            tw200_shipment.getOrder_no(), location_id, tracking_nms, res);
                                        // TODO エラー記録実装必要
                                        errs++;
                                    }
                                }
                            } else {
                                logger.warn("shopify伝票連携NG 店舗ID:{} 受注ID:{} 原因:Shopifyからロケ取得失敗", clientId, order_no);
                                // TODO エラー記録実装必要
                                errs++;
                            }
                        } catch (Exception e) {
                            logger.error("shopify伝票連携NG 店舗ID:{} API:{}", clientId, apiUrl);
                            logger.error(BaseException.print(e));
                        }
                    }
                }
            }
        }
        logger.info("shopify伝票連携 終了--連携件数(OK:" + cnts + " NG:" + errs + ")");
    }

    /**
     * 在庫数自動連携 (20分ごと自動起動 0,20,40)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // @Scheduled(cron = "30 0/20 * * * ?")
    public void setShopifyProductCnt() {
        logger.info("shopify在庫連携 開始");
        // shopify情報取得
        List<Mc106_produce_renkei> allData = productService.getAllProductData("shopify");
        int cuts = 0;
        int errs = 0;
        if (allData != null && allData.size() > 0) {
            for (Mc106_produce_renkei allDatum : allData) {
                // 初期化
                JSONArray products;
                StringBuilder url1 = new StringBuilder();
                StringBuilder url2 = new StringBuilder();
                StringBuilder url3 = new StringBuilder();
                StringBuilder param = new StringBuilder();

                // 認証トークン(Shopify場合、空)
                String invItemId = "";
                String locationId;
                // 店舗ID
                String clientId = allDatum.getClient_id();
                // 商品ID(サンロジ)
                String productId = allDatum.getProduct_id();
                // 商品ID(Shopify商品ID)
                String renkeiPid = allDatum.getRenkei_product_id();
                // 認証ID(Shopify認証ID)
                String variantId = allDatum.getVariant_id();
                // ShopifyAPI管理ID
                Integer apiId = allDatum.getApi_id();
                // 在庫情報取得
                Tw300_stock productCnt = productService.getProductCnt(clientId, productId);
                // 在庫情報がある場合、処理
                if (!StringTools.isNullOrEmpty(productCnt)) {
                    // 外部API連携(在庫連携)(0:連携しない 1:自動連携)
                    Tc203_order_client apiData = productService.getApiDataStock(clientId, apiId);
                    if (!StringTools.isNullOrEmpty(apiData)) {
                        String apiUrl = apiData.getClient_url();
                        String apiPws = apiData.getPassword();
                        // Shopify商品取得データを生成
                        url1.append("https://");
                        url1.append(apiUrl);
                        url1.append("/admin/api/");
                        url1.append(VERSION);
                        url1.append("/products.json");
                        url1.append("?fields=variants&ids=");
                        url1.append(renkeiPid);
                        // Shopify商品取得を実行
                        JSONObject jsonObject =
                            HttpUtils.sendHttpsGet(url1.toString(), "X-Shopify-Access-Token", apiPws, null);
                        // Shopify商品が取得できない場合、処理スキップ
                        if (!StringTools.isNullOrEmpty(jsonObject)) {
                            products = jsonObject.getJSONArray("products").getJSONObject(0).getJSONArray("variants");
                        } else {
                            logger.warn("shopify在庫連携NG 店舗ID:{} 原因:Shopifyから商品情報取得失敗 URL:{}", clientId, url1);
                            errs++;
                            continue;
                        }

                        // 商品情報取得できない場合、処理スキップ
                        if (!StringTools.isNullOrEmpty(products) && products.size() > 0) {
                            // Shopify店舗の商品管理コードと比較する、一致する商品に対して、在庫数を更新
                            for (int j = 0; j < products.size(); j++) {
                                String shopifyPid = products.getJSONObject(j).getString("id");
                                // null場合も考慮(google関数利用)
                                if (Objects.equal(shopifyPid, variantId)) {
                                    invItemId = products.getJSONObject(j).getString("inventory_item_id");
                                    break;
                                }
                            }
                        }
                        // invItemIdがない場合、スキップ
                        if (StringTools.isNullOrEmpty(invItemId)) {
                            logger.warn("shopify在庫連携NG 店舗ID:{} 外部連携ID:{} 原因:Shopify側管理商品IDの取得失敗", clientId, renkeiPid);
                            continue;
                        }
                        // Shopifyロケ取得データを生成
                        url2.append("https://").append(apiUrl)
                            .append("/admin/api/").append(VERSION).append("/inventory_levels.json")
                            .append("?inventory_item_ids=").append(invItemId);
                        // Shopifyロケ取得を実行
                        JSONObject jsonObject2 =
                            HttpUtils.sendHttpsGet(url2.toString(), "X-Shopify-Access-Token", apiPws, null);
                        // ShopifyロケーションIDが取得できない場合、処理スキップ
                        if (!StringTools.isNullOrEmpty(jsonObject2)) {
                            locationId = jsonObject2.getJSONArray("inventory_levels").getJSONObject(0)
                                .getString("location_id");
                        } else {
                            logger.warn("shopify在庫連携NG 店舗ID:{} URL:{} 原因:Shopifyからロケ取得失敗", clientId, url2);
                            errs++;
                            continue;
                        }

                        if (!StringTools.isNullOrEmpty(locationId)) {
                            // 有効在庫数0以下は全て0とする
                            int nums = (productCnt.getAvailable_cnt() > 0) ? productCnt.getAvailable_cnt() : 0;

                            // Shopify在庫連携データ生成
                            url3.append("https://").append(apiUrl)
                                .append("/admin/api/").append(VERSION).append("/inventory_levels/set.json");
                            param.append("{\"location_id\":").append(locationId).append(",\"inventory_item_id\":")
                                .append(invItemId).append(",\"available\":").append(nums).append("}");

                            // Shopify在庫連携を実行
                            String res = null;
                            try {
                                res = HttpUtils.sendJsonPost(url3.toString(), param, "X-Shopify-Access-Token", apiPws);
                            } catch (Exception e) {
                                logger.error("shopify在庫連携NG 店舗ID:{} 商品ID:{} 在庫数:{}" + clientId, productId, nums);
                                logger.error(BaseException.print(e));
                            }
                            if (!StringTools.isNullOrEmpty(res)) {
                                logger.info("shopify在庫連携OK 店舗ID:{} 商品ID:{} 在庫数:{}" + clientId, productId, nums);
                                // 連携した在庫数を在庫TBLの店舗数を反映
                                productDao.updateStockStroeCnt(clientId, productId, productCnt.getAvailable_cnt(),
                                    "shopify", DateUtils.getDate());
                                cuts++;
                            } else {
                                logger.warn("shopify在庫連携NG 店舗ID:{} 商品ID:{} 在庫数:{}" + clientId, productId, nums);
                                // TODO エラー記録実装必要
                                errs++;
                            }
                        }
                    }
                }
            }
        }
        logger.info("shopify在庫連携 終了--連携件数:OK(" + cuts + ")NG(" + errs + ")");
    }

    /**
     * Shopify店舗の商品情報取得 (毎日朝5時)
     *
     * @author HZM
     * @date 2021/1/12
     */
    // @Scheduled(cron = "0 0 5 * * ?")
    public void getAllProduct() {
        logger.info("shopify商品連携 開始");
        // shopifyAPI連携情報取得
        List<Tc203_order_client> allData = apiService.getAllData("shopify");
        int cuts = 0;
        for (Tc203_order_client allDatum : allData) {
            StringBuilder url = new StringBuilder();
            Integer apiId = allDatum.getId();
            String clientId = allDatum.getClient_id();
            url.append("https://").append(allDatum.getClient_url()).append("/admin/api/").append(VERSION)
                .append("/products.json?limit=250");

            String link;
            // 控制循环遍历的flg
            boolean finishFlg = true;
            while (finishFlg) {
                JSONObject jsonObject =
                    HttpUtils.sendHttpsGet(url.toString(), "X-Shopify-Access-Token", allDatum.getPassword(), null);
                if (StringTools.isNullOrEmpty(jsonObject)) {
                    logger.warn("shopify商品連携 URL:" + url);
                    break;
                }
                // 添加 , 为了便于分割
                link = jsonObject.getString("link") + ",";
                // 次回リクエストURLを解析（Shopifyから返すレスポンスのHeadersのLinkを判断）
                // noinspection UnstableApiUsage
                List<String> linkList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(link);
                boolean stopFlg = false;
                for (String param : linkList) {
                    String[] split = param.split(";");
                    String ref = split[1];
                    // 如果含有next 则表明还有下一页
                    if (ref.contains("next")) {
                        stopFlg = true;
                        String unprocessedUrl = split[0];
                        // 去除前后的 < >
                        String newUrl = unprocessedUrl.substring(1, unprocessedUrl.length() - 1);
                        String[] splitUrl = newUrl.split("&");
                        // 将pageInfo 拼接到基础的url上面
                        url.append("&").append(splitUrl[1]);
                    }
                }
                if (!stopFlg) {
                    finishFlg = false;
                }
                // 商品詳細を取得
                JSONArray products = jsonObject.getJSONArray("products");
                logger.info("Shopify商品連携 登録件数(" + products.size() + ") URL:" + url);
                for (int j = 0; j < products.size(); j++) {
                    String name = products.getJSONObject(j).getString("title");
                    String renkeiProductId = products.getJSONObject(j).getString("id");
                    JSONArray variants = products.getJSONObject(j).getJSONArray("variants");
                    for (int m = 0; m < variants.size(); m++) {
                        String sub_title = variants.getJSONObject(m).getString("title");
                        String code = variants.getJSONObject(m).getString("sku");
                        String product_name;
                        if ("Default Title".equals(sub_title)) {
                            product_name = name;
                        } else {
                            product_name = name + " " + sub_title;
                        }
                        String variant_id = variants.getJSONObject(m).getString("id");
                        String price = variants.getJSONObject(m).getString("price");
                        // TODO 商品オプション
                        String options = "";
                        // Shopify商品情報をサンロジに登録
                        // **********************************************************
                        // ********************* 共通パラメータ整理 *********************
                        // **********************************************************
                        ProductBean productBean = new ProductBean();
                        productBean.setClientId(clientId);
                        productBean.setApiId(apiId);
                        productBean.setCode(code);
                        productBean.setName(product_name);
                        productBean.setPrice(price);
                        productBean.setVariantId(variant_id);
                        productBean.setIsReducedTax(0);
                        productBean.setRenkeiPid(renkeiProductId);
                        // Mc100_productテーブルの既存商品をマッピング
                        Mc100_product mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
                        // 商品登録されていない場合、商品マスタに仮商品として新規登録
                        if (java.util.Objects.isNull(mc100Product)) {
                            // 商品新規登録 TODO 暫定対策：新規商品の処理を外す
                            // mc100Product = apiCommonUtils.insertMc100Product(productBean, API.SHOPIFY.getName());
                        }
                        if (!java.util.Objects.isNull(mc100Product)) {
                            apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                                API.SHOPIFY.getName());
                        }
                    }
                }
            }
        }
        logger.info("shopify商品連携 終了--登録件数:" + cuts);
    }

    /**
     * 受注管理TBLの新規登録
     *
     * @param order 受注データ
     * @param init 店舗情報
     * @return List<String> 異常情報
     */
    private List<String> setTc200Json(JSONObject order, InitClientInfoBean init) {
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
        // 依頼マスタ
        Ms012_sponsor_master ms012spons = init.getMs012sponsor();
        // 自動出庫(1:自動出庫 0:出庫しない) TODO
        Integer shipmentStatus = init.getShipmentStatus();
        List<String> errList = new ArrayList<>();

        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMM-00001」
        String orderNo = CommonUtils.getOrderNo(subNo, identification);
        tc200Order.setPurchase_order_no(orderNo);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(order.getString("name").substring(1) + "-" + order.getString("id"));
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 个口数(Default値:1)
        tc200Order.setBoxes(1);
        // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済)
        tc200Order.setOuter_order_status(0);
        // 必要字段写入（receiver_zip_code1）（Import_datetime）(Receiver_address1)(Receiver_todoufuken)(Receiver_family_name)
        try {
            // 受注時間
            tc200Order.setOrder_datetime(CommonUtils.dealDateFormat(order.getString("created_at")));
            // 作成時間
            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
            // 配送者情報を取得 TODO
            JSONObject shippingObject = order.getJSONObject("shipping_address");
            if (StringTools.isNullOrEmpty(shippingObject)) {
                shippingObject = order.getJSONObject("billing_address");
            }
            if (!StringTools.isNullOrEmpty(shippingObject)) {

                List<String> zipList = CommonUtils.checkZipToList(shippingObject.getString("zip"));
                // 配送先郵便番号
                if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                    tc200Order.setReceiver_zip_code1(zipList.get(0));
                    tc200Order.setReceiver_zip_code2(zipList.get(1));
                } else {
                    // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                    logger.warn("shopify受注(" + orderNo + "):shopifyデータ不正(郵便)");
                    tc200Order.setReceiver_zip_code1("000");
                    tc200Order.setReceiver_zip_code2("0000");
                }
                // 配送先都道府県
                String province = shippingObject.getString("province");
                if (!Strings.isNullOrEmpty(prefectureMap.get(province))) {
                    tc200Order.setReceiver_todoufuken(prefectureMap.get(province));
                } else {
                    tc200Order.setReceiver_todoufuken(province);
                }
                // 配送住所(市区町+住所1)
                if (!StringTools.isNullOrEmpty(shippingObject.getString("city"))
                    && !StringTools.isNullOrEmpty(shippingObject.getString("address1"))) {
                    String address = shippingObject.getString("city") + shippingObject.getString("address1");
                    tc200Order.setReceiver_address1(address);
                } else {
                    errList.add("配送先の住所が取得できないため、Shopify店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送住所(住所2)
                if (!StringTools.isNullOrEmpty(shippingObject.getString("address2"))) {
                    tc200Order.setReceiver_address2(shippingObject.getString("address2"));
                }
                // 配送先名前
                if (!StringTools.isNullOrEmpty(shippingObject.getString("last_name"))) {
                    tc200Order.setReceiver_family_name(shippingObject.getString("last_name"));
                    tc200Order.setReceiver_first_name(shippingObject.getString("first_name"));
                } else {
                    errList.add("配送先の名前が取得できないため、Shopify店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送先会社名称
                if (!StringTools.isNullOrEmpty(shippingObject.getString("company"))) {
                    tc200Order.setReceiver_company(shippingObject.getString("company"));
                }
                // 配送先電話番号
                String phone = shippingObject.getString("phone");
                if (!StringTools.isNullOrEmpty(phone)) {
                    List<String> telList = CommonUtils.checkPhoneToList(phone);
                    if (telList != null && telList.size() > 0) {
                        tc200Order.setReceiver_phone_number1(telList.get(0));
                        tc200Order.setReceiver_phone_number2(telList.get(1));
                        tc200Order.setReceiver_phone_number3(telList.get(2));
                    }
                }
            } else {
                errList.add("受注配送情報がないので、注文情報をご確認ください。");
                return errList;
            }
        } catch (Exception e) {
            errList.add("受注データ不正(配送先の郵便番号、住所、名前)が発生したので、注文情報をご確認ください。");
            return errList;
        }
        // 支払方法
        String payment = order.getString("gateway");
        // 支付方法（SunLogi定義）
        String paymentMethod = apiCommonUtils.getPaymentMethod(payment, API.SHOPIFY.getName(), ms007SettingPaymentMap);
        tc200Order.setPayment_method(paymentMethod);
        // 元々の支払方法を備考10も記載する
        tc200Order.setBikou10(payment);

        // 代引引換の一時対応(wang 2020119 ※配送連携設定の決済方法を追加必要)
        if ("Cash on Delivery (COD)".equals(payment) || "代金引換".equals(paymentMethod)) {
            tc200Order.setCash_on_delivery_fee(Integer.parseInt(order.getString("total_price")));
        }
        // 合計請求金額
        tc200Order.setBilling_total(Integer.parseInt(order.getString("total_price")));

        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
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

        // 配送情報 TODO
        JSONArray jsonArray = order.getJSONArray("shipping_lines");
        String shippingMethod = "";
        if (!jsonArray.isEmpty()) {
            shippingMethod = jsonArray.getJSONObject(0).getString("title");
        }

        // 元の配送方法を備考9に保管する @Add wang 2021/3/30
        tc200Order.setBikou9(shippingMethod);
        // 配送方法
        Ms004_delivery ms004Delivery =
            apiCommonUtils.getDeliveryMethod(shippingMethod, defaultDeliveryMethod, ms007SettingDeliveryMethodMap);
        if (!java.util.Objects.isNull(ms004Delivery)) {
            // 配送会社
            tc200Order.setDelivery_company(ms004Delivery.getDelivery_cd());
            // 配送方法
            tc200Order.setDelivery_method(ms004Delivery.getDelivery_method());
            // 配送master登録した配送会社名を取得
            shippingMethod = ms004Delivery.getDelivery_nm();
        }

        // 配達希望時間帯 TODO Shopify側が未指定
        // String deliveryTimeZone = "指定なし";
        // 配送時間帯 (配送連携設定管理表「mc007_setting」から取得)
        JSONArray note_attributes = order.getJSONArray("note_attributes");
        for (int k = 0; k < note_attributes.size(); k++) {
            JSONObject note_attribute = note_attributes.getJSONObject(k);
            if ("配送時間帯".equals(note_attribute.getString("name"))) {
                String deliveryTimeId = "";
                if (!StringTools.isNullOrEmpty(shippingMethod)) {
                    deliveryTimeId = apiCommonUtils.getDeliveryTimeSlot(shippingMethod,
                        note_attribute.getString("value"), API.SHOPIFY.getName(), ms007SettingTimeMap);
                }
                tc200Order.setDelivery_time_slot(deliveryTimeId);
            }
            if ("配送希望日".equals(note_attribute.getString("name"))) {
                // 希望届け日 TODO
                String deliveryDate = note_attribute.getString("value");
                tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));
            }
        }

        // 注文者情報を取得 TODO
        JSONObject billingObject = order.getJSONObject("billing_address");
        if (!StringTools.isNullOrEmpty(billingObject)) {
            List<String> zipList = CommonUtils.checkZipToList(billingObject.getString("zip"));
            // 注文者郵便番号
            if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                tc200Order.setOrder_zip_code1(zipList.get(0));
                tc200Order.setOrder_zip_code2(zipList.get(1));
            } else {
                // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                logger.warn("shopify受注(" + orderNo + "):shopifyデータ不正(郵便)");
                tc200Order.setOrder_zip_code1("000");
                tc200Order.setOrder_zip_code2("0000");
            }
            // 注文者都道府県
            String province = billingObject.getString("province");
            if (!Strings.isNullOrEmpty(prefectureMap.get(province))) {
                tc200Order.setOrder_todoufuken(prefectureMap.get(province));
            } else {
                tc200Order.setOrder_todoufuken(province);
            }
            // 注文者住所(市区町+住所1)
            if (!StringTools.isNullOrEmpty(billingObject.getString("city"))
                && !StringTools.isNullOrEmpty(billingObject.getString("address1"))) {
                String address = billingObject.getString("city") + billingObject.getString("address1");
                tc200Order.setOrder_address1(address);
            }
            // 注文者住所(住所2)
            if (!StringTools.isNullOrEmpty(billingObject.getString("address2"))) {
                tc200Order.setOrder_address2(billingObject.getString("address2"));
            }
            // 注文者名前
            if (!StringTools.isNullOrEmpty(billingObject.getString("last_name"))) {
                tc200Order.setOrder_family_name(billingObject.getString("last_name"));
                tc200Order.setOrder_first_name(billingObject.getString("first_name"));
            }
            // 注文者会社名称
            if (!StringTools.isNullOrEmpty(billingObject.getString("company"))) {
                tc200Order.setOrder_company(billingObject.getString("company"));
            }
            // 注文者電話番号
            String phone = billingObject.getString("phone");
            if (!StringTools.isNullOrEmpty(phone)) {
                List<String> telList = CommonUtils.checkPhoneToList(phone);
                if (telList != null && telList.size() > 0) {
                    tc200Order.setOrder_phone_number1(telList.get(0));
                    tc200Order.setOrder_phone_number2(telList.get(1));
                    tc200Order.setOrder_phone_number3(telList.get(2));
                }
            }
        }
        // 税率計算
        String taxStr = order.getString("tax_lines");
        double rate = 0.1;
        if (!StringTools.isNullOrEmpty(taxStr)) {
            JSONArray obj = JSONArray.parseArray(taxStr);
            // 税率の取得
            if (!StringTools.isNullOrEmpty(obj) && obj.size() > 0) {
                JSONObject jsonTax = obj.getJSONObject(0);
                // 税率
                String rateStr = jsonTax.getString("rate");
                if (!StringTools.isNullOrEmpty(rateStr) && "0.08".equals(rateStr)) {
                    rate = 0.08;
                }
            }
        }
        // 送料合計 taxes_included設定されている場合true、total_shipping_price_set税金が含まれます。
        int deliveryTotal;
        JSONObject deliveryObject = order.getJSONObject("total_shipping_price_set");
        JSONObject jsonObject = deliveryObject.getJSONObject("shop_money");
        // 消費税合計(税抜)
        int total;
        // 割引金額
        int otherFee;
        if ("false".equals(order.getString("taxes_included"))) {
            // 税率
            BigDecimal bigTax = BigDecimal.valueOf((1.0 + rate));
            // 商品金額(税抜の商品金額*(1+税率)
            BigDecimal bigTotal = new BigDecimal(order.getInteger("total_line_items_price"));
            total = bigTotal.multiply(bigTax).intValue();
            // 割引金額
            BigDecimal bigOtherFree = new BigDecimal(order.getBigInteger("total_discounts"));
            otherFee = bigOtherFree.multiply(bigTax).intValue();
            // 送料 ※ 税抜の送料*(1+税率)
            BigDecimal bigDeliveryTotal = new BigDecimal(jsonObject.getBigInteger("amount"));
            deliveryTotal = bigDeliveryTotal.multiply(bigTax).intValue();
        } else {
            total = order.getInteger("total_line_items_price");
            otherFee = order.getInteger("total_discounts");
            deliveryTotal = jsonObject.getInteger("amount");
        }
        // 送料合計(税込)
        tc200Order.setDelivery_total(deliveryTotal);
        // 割引金額 wang @Add 20210804
        tc200Order.setOther_fee(otherFee);
        // 商品金額(税込)
        tc200Order.setProduct_price_excluding_tax(total);
        // 消費税
        tc200Order.setTax_total(order.getInteger("total_tax"));
        // 店舗側のメモ取得(※備考欄に記載する)
        if (!Strings.isNullOrEmpty(order.getString("note"))) {
            // 備考フラグ1を設定する
            tc200Order.setBikou_flg(1);
            // 注文者の備考情報設定
            tc200Order.setMemo(order.getString("note"));
        }
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);

        try {
            // 受注管理表登録
            if (Collections.isEmpty(errList)) {
                orderDao.insertOrder(tc200Order);
            } else {
                logger.warn("Shopify受注連携NG 管理TBLの登録失敗 受注ID(" + orderNo + ")");
            }
        } catch (Exception e) {
            logger.error("shopify受注管理TBLの登録失敗 受注ID(" + orderNo + ")");
            logger.error(BaseException.print(e));
            errList.add("何か原因により、受注管理の取込が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }

        try {
            if (Collections.isEmpty(errList)) {
                // 受注明細登録
                getTc201DetailData(order, clientId, orderNo, apiId);
            } else {
                logger.warn("shopify受注明細TBLの登録失敗 受注ID(" + orderNo + ")");
            }
        } catch (Exception e) {
            logger.error("shopify受注明細TBLの登録失敗 受注ID(" + orderNo + ")");
            logger.error(BaseException.print(e));
            errList.add("何か原因により、受注明細の取込が失敗したので、システム担当者にお問い合わせください。");
            return errList;
        }
        // 出庫依頼処理(0:出庫しない 1:自動出庫)
        if (1 == shipmentStatus) {
            apiCommonUtils.processShipment(orderNo, clientId);
        }
        return errList;
    }

    /**
     * 受注明細の新規登録
     *
     * @param order 受注データ
     * @param clientId 店舗ID
     * @param orderNo 受注番号
     * @param apiId API番号
     */
    private void getTc201DetailData(JSONObject order, String clientId, String orderNo, Integer apiId) {
        // 初期化
        Tc201_order_detail tc201OrderDetail;
        JSONArray jsonArray = order.getJSONArray("line_items");
        Integer subNo = 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            tc201OrderDetail = new Tc201_order_detail();
            // 商品情報
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // SHOPIFYの商品ID
            String renkeiProductId = jsonObject.getString("product_id");
            // 商品コード(SKUコード)
            String code = jsonObject.getString("sku");
            // 商品バリエーションID(※在庫数連携のため、利用)
            String variantId = jsonObject.getString("variant_id");
            // 商品名
            String name = jsonObject.getString("name");
            // 商品単価
            Integer price = jsonObject.getInteger("price");
            // 商品合計()
            Integer unit_price = jsonObject.getInteger("price");
            // 商品数量
            Integer amount = jsonObject.getInteger("quantity");
            // 商品オプション
            String options = "";
            // 税区分(0:税込 1:税抜) @Add wang 20210803
            int taxFlag = 0;
            // 軽減税率の取得(0:税率10% 1:税率8%) @Add wang 20210212
            int isReducedTax = 0;
            String taxStr = jsonObject.getString("tax_lines");
            String taxObj;
            double rate;
            if (!StringTools.isNullOrEmpty(taxStr)) {
                taxObj = taxStr.replace("[", "").replace("]", "");
                // 税率の取得
                if (!StringTools.isNullOrEmpty(taxObj)) {
                    JSONObject jsonTax = JSON.parseObject(taxObj);
                    // 税率
                    rate = jsonTax.getDouble("rate");
                    if (!StringTools.isNullOrEmpty(rate) && "0.08".equals(String.valueOf(rate))) {
                        isReducedTax = 1;
                        rate = 0.08;
                    }
                    // 税抜の場合
                    // 消費税合計(税抜)
                    if ("false".equals(order.getString("taxes_included"))) {
                        // 税率
                        BigDecimal bigTax = BigDecimal.valueOf((1.0 + rate));
                        // 商品金額(税抜の商品金額*(1+税率)
                        BigDecimal bigPrice = new BigDecimal(jsonObject.getInteger("price"));
                        unit_price = bigPrice.multiply(bigTax).intValue();
                        taxFlag = 1;
                    }
                }
            }
            // 税率
            tc201OrderDetail.setIs_reduced_tax(isReducedTax);
            // 税区分(0:税込 1:税抜) @Add wang 20210803
            tc201OrderDetail.setTax_flag(taxFlag);
            // **********************************************************
            // ********************* 共通パラメータ整理 *********************
            // **********************************************************
            ProductBean productBean = new ProductBean();
            productBean.setClientId(clientId);
            productBean.setApiId(apiId);
            productBean.setCode(code);
            productBean.setName(name);
            productBean.setPrice(String.valueOf(price));
            productBean.setVariantId(variantId);
            productBean.setIsReducedTax(isReducedTax);
            productBean.setRenkeiPid(renkeiProductId);
            // Mc100_productテーブルの既存商品をマッピング
            Mc100_product mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
            // 商品登録されていない場合、商品マスタに仮商品として新規登録
            if (java.util.Objects.isNull(mc100Product)) {
                // 商品新規登録
                mc100Product = apiCommonUtils.insertMc100Product(productBean, API.SHOPIFY.getName());
                // 商品之前不存在 设定为仮登録
                tc201OrderDetail.setProduct_kubun(9);
            }
            if (!java.util.Objects.isNull(mc100Product)) {
                apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                    API.SHOPIFY.getName());
            }
            if (java.util.Objects.nonNull(mc100Product)) {
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
                logger.error("Shopify受注連携NG 店舗ID【{}】受注番号【{}】原因:商品IDの取得失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                throw new BaseException(ErrorCode.E_11005);
            }
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // 商品名 (受注の商品名をそのままに保管)
            tc201OrderDetail.setProduct_name(name);
            // 単価
            tc201OrderDetail.setUnit_price(price);
            // 個数
            tc201OrderDetail.setNumber(amount);
            // 商品計(税込)
            int total_price = unit_price * tc201OrderDetail.getNumber();
            tc201OrderDetail.setProduct_total_price(total_price);
            // 受注明細番号
            tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
            // 受注番号
            tc201OrderDetail.setPurchase_order_no(orderNo);
            // 削除フラグ
            tc201OrderDetail.setDel_flg(0);
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
                subNo++;
            } catch (Exception e) {
                subNo++;
                logger.error("Shopify受注連携NG 店舗ID【{}】受注番号【{}】原因:受注明細の登録失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
            }
        }
    }

    // @Scheduled(cron = "0 0 8,20 * * ?")
    public void fetchShopifyCancelOrders() {
        logger.info("Shopify受注キャンセル 開始");
        // shopify連携情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.SHOPIFY.getName());
        // Shopifyに関するAPI情報が存在しない場合、処理中止
        if (Collections.isEmpty(allData)) {
            logger.info("Shopify受注キャンセル 店舗情報：0件");
            logger.info("Shopify受注キャンセル 終了");
            return;
        }
        // 初期化
        apiCommonUtils.initialize();
        // 各店舗の受注連携を開始
        for (Tc203_order_client data : allData) {

            // 保存错误数据
            ArrayList<String> errList = new ArrayList<>();

            // 初期化
            JSONArray orders;
            String link;
            // Shopify連携APIのURL
            StringBuilder urlKey = new StringBuilder();
            urlKey.append("https://");
            urlKey.append(data.getClient_url());
            urlKey.append("/admin/api/" + VERSION);
            urlKey.append("/orders.json?limit=250");

            // 控制循环遍历的flg
            boolean finishFlg = true;
            // ループ回数
            int count = 1;
            String param = null;
            while (finishFlg) {
                String url = urlKey.toString() + "&status=cancelled";
                // page_infoが存在する場合、statusを渡すことはできません
                if (count > 1 && !StringTools.isNullOrEmpty(param)) {
                    url = urlKey.toString() + "&" + param;
                }
                // 请求Shopify Api接口 获取数据
                JSONObject orderJson =
                    HttpUtils.sendHttpsGet(url, "X-Shopify-Access-Token", data.getPassword(), errList);
                if (orderJson == null) {
                    break;
                }
                // 添加 , 为了便于分割
                link = orderJson.getString("link");
                String pageInfo;
                boolean stopFlg = false;
                if (!Strings.isNullOrEmpty(link)) {
                    if (link.contains(",")) {
                        link = link.split(",")[1];
                    }
                    String[] splitLink = link.split(";");
                    if (splitLink[1].contains("next")) {
                        pageInfo = splitLink[0];
                        // <>を外す
                        String newUrl = pageInfo.substring(1, pageInfo.length() - 1);
                        param = newUrl.split("&")[1];
                        stopFlg = true;
                    }
                }
                if (!stopFlg) {
                    finishFlg = false;
                }
                // 受注データを処理
                orders = orderJson.getJSONArray("orders");
                String clientId = data.getClient_id();
                for (int j = 0; j < orders.size(); j++) {
                    // 受注情報取得
                    JSONObject order = orders.getJSONObject(j);
                    // 識別番号(Shopify店舗の注文番号(#無くす)+Shopify内部管理ID)
                    String spOrderId = order.getString("name").replace("#", "") + "-" + order.getString("id");
                    // キャンセル発生するかを確認
                    // 過去受注番号に対してのキャンセルが発生した場合
                    logger.warn("Shopify受注キャンセル 店舗ID:{} 受注ID:{} 原因:受注キャンセル", clientId, spOrderId);
                    // 過去受注が受注キャンセルがある場合、記録
                    apiCommonUtils.insertTc208OrderCancel(clientId, spOrderId, API.SHOPIFY.getName());
                }
                count++;
            }
        }
        logger.info("shopify受注キャンセル 終了");
    }
}
