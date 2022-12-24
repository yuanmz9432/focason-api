package com.lemonico.api;

import static java.lang.Math.abs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.dao.ProductRenkeiDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.OrderService;
import com.lemonico.store.service.ShipmentsService;
import io.jsonwebtoken.lang.Collections;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * MakeShopのAPI連携機能
 *
 * @author YuanMingZe
 * @className MakeShopAPI
 * @date 2021/06/30
 **/
@Component
@EnableScheduling
public class MakeShopAPI
{

    private final static Logger logger = LoggerFactory.getLogger(MakeShopAPI.class);
    // MakeShopの検索範囲指定：時間形式
    private final static DateTimeFormatter TIME_FORMAT_STR = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    // 入金完了フラグ
    private final static Integer PAID = 1;
    // MakeShopの配送業者コード
    private final static HashMap<String, String> deliveryCarriersMap = new HashMap<>();
    // オプション一括登録のCSVヘーダ
    private final static String OPTION_CSV_HEADER =
        "商品特定コード指定,オプション特定コード指定,システム商品コード,独自商品コード,商品名,商品カテゴリーパス,オプション独自コード,オプション1項目,オプション2項目,販売価格,数量,JANコード";
    // 商品一括登録のCSVヘーダ
    private final static String NORMAL_CSV_HEADER =
        "商品特定コード指定,更新時間フラグ,システム商品コード,独自商品コード,カテゴリー識別コード,カテゴリーパス,商品名,重量,販売価格,定価,ポイント,仕入価格,製造元,原産地,原産地表示フラグ,数量,数量表示フラグ,最小注文限度数,最大注文限度数,陳列位置,送料個別設定,掲載開始日指定フラグ,掲載開始日,掲載終了日指定フラグ,掲載終了日,掲載期間外表示可否,割引使用フラグ,割引率,割引期間,商品グループ,商品検索語,商品別特殊表示,オプション１名称,オプション２名称,オプショングループ,拡大画像名,普通画像名,縮小画像名,追加商品画像1,画像説明文1,追加商品画像2,画像説明文2,追加商品画像3,画像説明文3,レイアウト指定,PC用メイン商品説明文,JANコード,商品表示可否,商品状態フラグ,データ用商品名,データ用詳細内容,オプションの表示形式,ISBNコード,ブランド名,MPN(メーカー型番),Googleショッピングカテゴリー,性別,年齢層,色,サイズ,素材,柄,商品グループID,アドワーズ用グループ,アドワーズ用ラベル,商品ページURL,商品カテゴリー用商品説明文,商品カテゴリー用商品説明文表示可否,備考欄表示テキスト指定,スマホ商品説明1,スマホ商品説明2,再入荷お知らせ,決済グループ,PC用追加説明文,名入れグループ,消費税率,軽減税率対象,ブラウザータイトル,メタタグ：Description";

    static {
        deliveryCarriersMap.put("ゆうパック", "001");
        deliveryCarriersMap.put("クロネコヤマト", "002");
        deliveryCarriersMap.put("佐川急便", "003");
        deliveryCarriersMap.put("国際スピード郵便(USPS)", "004");
        deliveryCarriersMap.put("西濃運輸", "006");
        deliveryCarriersMap.put("福山通運", "007");
        deliveryCarriersMap.put("EMS国際スピード郵便", "008");
        deliveryCarriersMap.put("ゆうパック代金引換小包", "009");
        deliveryCarriersMap.put("ゆうパック書留", "010");
        deliveryCarriersMap.put("レターパック500", "011");
        deliveryCarriersMap.put("翌朝10時郵便", "012");
        deliveryCarriersMap.put("名鉄運輸", "013");
        deliveryCarriersMap.put("OCS 国際エクスプレス", "014");
        deliveryCarriersMap.put("普通郵便", "015");
        deliveryCarriersMap.put("普通郵便代引き", "016");
        deliveryCarriersMap.put("トナミ運輸", "017");
        deliveryCarriersMap.put("トールエクスプレスジャパン", "018");
        deliveryCarriersMap.put("エコ配", "019");
        deliveryCarriersMap.put("レターパック350", "020");
        deliveryCarriersMap.put("セイノースーパーエクスプレス", "021");
        deliveryCarriersMap.put("新潟運輸", "022");
        deliveryCarriersMap.put("DHL", "023");
        deliveryCarriersMap.put("第一貨物", "024");
        deliveryCarriersMap.put("ゆうパケット", "025");
        deliveryCarriersMap.put("ポスパケット", "026");
        deliveryCarriersMap.put("クリックポスト", "027");
        deliveryCarriersMap.put("スマートレター", "028");
        deliveryCarriersMap.put("宅急便コンパクト", "029");
        deliveryCarriersMap.put("ネコポス", "030");
        deliveryCarriersMap.put("クロネコDM便", "031");
        deliveryCarriersMap.put("DM便", "031");
    }

    @Resource
    private ProductRenkeiDao productRenkeiDao;
    @Resource
    private OrderApiService apiService;
    @Resource
    private StockDao stockDao;
    @Resource
    private ProductDao productDao;
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
    private OrderService orderService;

    /**
     * MakeShop受注自動取込 (10分ごと自動起動 0, 10, 20, 30, 40, 50)
     *
     * @author YuanMingZe
     * @date 2021/06/30
     */
    // @Scheduled(cron = "0 4/10 * * * ?")
    public void fetchMakeShopOrders() {
        logger.info("MakeShop受注連携 開始");
        // MakeShopに関する情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.MAKESHOP.getName());
        // MakeShopに関するAPI情報が存在しない場合、処理中止
        if (Collections.isEmpty(allData)) {
            logger.info("MakeShop受注連携 店舗情報：0件");
            logger.info("MakeShop受注連携 終了");
            return;
        }

        Hashtable<String, List<String>> errHashtable = new Hashtable<>();

        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        apiCommonUtils.initialize();
        // MakeShopに関する情報を全て処理
        for (Tc203_order_client data : allData) {

            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();

            // MakeShop連携API情報
            String apiName = data.getApi_name();
            // 現時点
            LocalDateTime now = LocalDateTime.now();
            // 検索条件：時間単位(Default値:60 単位：分) ※備考1の値を取得
            int timeUnit = 60;
            if (!StringTools.isNullOrEmpty(data.getBikou3())) {
                timeUnit = Integer.parseInt(data.getBikou3());
            }
            // 検索条件：開始時間
            String start = now.minusMinutes(timeUnit).format(TIME_FORMAT_STR);
            // 検索条件：終了時間(現在時刻より2分前)
            String end = now.minusMinutes(2).format(TIME_FORMAT_STR);

            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(data);

            // 店舗ID
            String clientId = data.getClient_id();
            // 取込履歴ID
            Integer historyId = initClientInfo.getHistoryId();
            // 受注データリスト
            JSONArray orders = new JSONArray();
            // 受注データ取得URL
            StringBuilder url = new StringBuilder();

            try {
                // https://www.makeshop.jp/api/orderinfo/index.html?
                // cmd=status&shopid=test&token=61efa02bde9d3eb5714560b233596e42
                // &service=testsystem&ordernum=PB13818933178731403
                // &status=0&result=test&deliveryid=0
                // 1 cmd ○ API動作指定(get：注文データ取得) 2 ordernum 注文番号を26文字以内の半角英数で指定します。 3 start
                // 注文日時yyyymmddhhmmss 4 end 注文日時yyyymmddhhmmss 5 canceled キャンセル注文（0：取得しない
                // 1：取得する）※指定なし:0。
                // MakeShop連携APIのURL
                String cmd = "get";
                String apiUrl = data.getClient_url();
                String shopId = data.getBikou2();
                String token = data.getApi_key();
                url.append("https://").append(apiUrl)
                    .append("/api/orderinfo/index.html?")
                    .append("cmd=").append(cmd)
                    .append("&shopid=").append(shopId)
                    .append("&token=").append(token)
                    .append("&start=").append(start)
                    .append("&end=").append(end)
                    .append("&service=sunlogi");
                // 受注取得APIのレスポンス
                String responseStr;
                try {
                    responseStr = HttpClientUtils.sendGet(url.toString(), null, null, errList);
                } catch (Exception e) {
                    logger.error("MakeShop受注連携NG 店舗ID:{} URL:{} 原因:MakeShop受注APIの接続失敗", clientId, url);
                    logger.error(BaseException.print(e));
                    errList.add("MakeShop受注APIの接続失敗");
                    errOrderClients.add(data);
                    continue;
                }
                JSONObject resultObj = JSONObject.parseObject(responseStr);
                // 注文がある場合
                if (!StringTools.isNullOrEmpty(resultObj)) {
                    JSONObject ordersObj = resultObj.getJSONObject("orders");
                    // ordersの配下のorder類型を判断する。
                    if (!StringTools.isNullOrEmpty(ordersObj)) {
                        if (ordersObj.get("order") instanceof JSONObject) {
                            orders.add(ordersObj.get("order"));
                        } else {
                            orders = (JSONArray) ordersObj.get("order");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("MakeShop受注連携NG 店舗ID:{} URL:{} 原因:MakeShop受注APIの接続失敗", clientId, url);
                logger.error(BaseException.print(e));
                errList.add("MakeShop受注APIの接続失敗");
                errOrderClients.add(data);
                continue;
            }
            // 受注件数を取得しない場合、処理スキップ
            if (Collections.isEmpty(orders)) {
                logger.info("MakeShop受注連携OK  店舗ID:{} API名:{} 受注:0件", clientId, apiName);
            } else {
                // 取込件数
                int total = orders.size();
                logger.info("MakeShop受注連携OK  店舗ID:{} API名:{} 受注:{}件", clientId, apiName, total);
                // 成功件数
                int successCnt = 0;
                // 失敗件数
                int failureCnt = 0;
                // 受注番号の枝番
                int subNo = 1;
                for (int j = 0; j < orders.size(); j++) {
                    // 最大100件まで出力されます。
                    // 対象データが100件以上ある場合は、条件を指定することで、出力件数を調整してください。
                    // 受注情報取得
                    JSONObject order = orders.getJSONObject(j);
                    // MakeShopの注文番号
                    String orderNo = order.getString("ordernum");
                    // 注文の判別に利用 0：キャンセル 1：通常 99：仮注文
                    String status = order.getString("status");
                    // 決済状況確認(※入金待ち)
                    String type = order.getJSONObject("paymethod").getString("type");
                    // 入金状況(0：未入金1：入金完了)
                    // ※決済方法と入金完了とするステータスについては入金完了とする決済別ステータス一覧を参照)
                    Integer payment_status = order.getInteger("payment_status");
                    // order_type 0:入金待ち 1:入金済み
                    int orderType = 1;
                    // エラーメッセージ
                    String msg;
                    // SunLogiの注文番号
                    Integer outerOrderNo = orderDao.getOuterOrderNo(orderNo, clientId);
                    // 注文番号がない場合、処理
                    if (outerOrderNo == 0) {
                        if ("0".equals(status)) {
                            // 取込前の受注キャンセルを記録しない
                            logger.warn("MakeShop受注連携NG 店舗ID:{} 受注ID:{} 原因:受注キャンセル", clientId, orderNo);
                            continue;
                        }
                        // 処理１入金待ち
                        // order_type 0:入金待ち 1:入金済み
                        if (!StringTools.isNullOrEmpty(type) && payment_status == 0) {
                            switch (type) {
                                case "R": // 代金引換
                                case "N": // NP後払い
                                case "A": // 後払い.COM
                                case "M": // 後払い決済（Paid）
                                case "B1": // GMO後払い
                                    orderType = 1;
                                    break;
                                default:
                                    // B:銀行振り P:ゆうちょ銀行 Z:コンビニ決済 M1:銀行振込 D1:Amazon Pay等考慮
                                    orderType = 0;
                                    break;
                            }
                        }
                        // **********************************************************
                        // ********************* 共通パラメータ整理 *********************
                        // **********************************************************

                        try {
                            initClientInfo.setSubNo(subNo);
                            order.fluentPut("com_orderType", orderType);
                            // 受注書き込む
                            List<String> errorList = setTc200Json(order, initClientInfo);
                            subNo++;
                            // 成功かどうか
                            if (!Collections.isEmpty(errorList)) {
                                failureCnt++;// 失敗
                                logger.warn("MakeShop受注連携NG 店舗ID:{} 受注ID:{} 原因:受注データ不正", clientId, orderNo);
                                // 受注連携が失敗する場合、失敗内容を記録
                                apiCommonUtils.insertTc207OrderError(clientId, historyId, orderNo, errorList.get(0),
                                    API.ECFORCE.getName());
                            } else {
                                successCnt++;// 成功
                                logger.info("MakeShop受注連携OK 店舗ID:{} 受注ID:{} ", clientId, orderNo);
                            }
                        } catch (Exception e) {
                            failureCnt++;
                            // 受注連携が失敗する場合、失敗内容を記録
                            msg = "MakeShop受注連携のバッチ処理が失敗しましたので、システム担当者にお問い合わせください。";
                            apiCommonUtils.insertTc207OrderError(clientId, historyId, orderNo, msg,
                                API.ECFORCE.getName());
                            logger.error(BaseException.print(e));
                            errList.add("MakeShop受注連携のバッチ処理が失敗しましたので。注文No=" + orderNo);
                            errOrderClients.add(data);
                        }
                    } else {
                        logger.warn("MakeShop受注連携NG  店舗ID:{} 受注ID:{} 原因:過去受注取込済", clientId, orderNo);
                        total--;// 成功
                        // 過去受注番号に対してのキャンセルが発生した場合
                        if ("0".equals(status)) {
                            // 過去受注が受注キャンセルがある場合、記録
                            apiCommonUtils.insertTc208OrderCancel(clientId, orderNo, API.MAKESHOP.getName());
                            logger.warn("MakeShop受注連携NG  店舗ID:{} 受注ID:{} 原因:取込後の受注キャンセル", clientId, orderNo);
                        }
                    }
                }
                // 取り込まれていない場合、取込履歴を記録しない
                if (successCnt > 0) {
                    apiCommonUtils.processOrderHistory(clientId, API.MAKESHOP.getName(), historyId, successCnt,
                        failureCnt);
                }
            }
            if (!errList.isEmpty()) {
                String key = clientId + "_API名：" + data.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        logger.info("MakeShop受注連携 終了");
        // mailTools.sendErrorMessage(errHashtable, API.MAKESHOP.getName());
        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 送り状番号自動連携 (15分ごと自動起動 3, 18, 33, 48)
     *
     * @author YuanMingZe
     * @date 2021/07/02
     */
    // @Scheduled(cron = "0 3/15 * * * ?")
    public void processTrackingNo() {
        logger.info("MakeShop伝票自動連携 開始");
        // MakeShop情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> allData = apiService.getAllDataDelivery(API.MAKESHOP.getName());
        int counts = 0;
        int errs = 0;
        // 情報取得できない場合、処理スキップ
        if (Collections.isEmpty(allData)) {
            logger.info("MakeShopに関するAPI情報が存在しないので、【設定】→【連携設定】→【API連携設定】画面で新規登録してください。");
            logger.info("MakeShop伝票自動連携 終了");
            return;
        }
        for (Tc203_order_client data : allData) {
            // 店舗API情報
            String clientId = data.getClient_id();
            // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
            List<Tw200_shipment> makeShopList =
                shipmentsService.getUntrackedShipments(clientId, API.MAKESHOP.getName());
            // 出庫情報が存在しない場合、処理をスキップ
            if (Collections.isEmpty(makeShopList)) {
                logger.info("店舗ID:" + clientId + " の出庫情報が存在しないので、次の店舗に処理続きます。");
                continue;
            }
            // 店舗設定の全て情報
            for (Tw200_shipment shipment : makeShopList) {
                // 初期化
                StringBuilder url = new StringBuilder();
                try {
                    // 1 cmd ○ deliver：配送状態変更 // 2 ordernum ○ 注文番号 // 3 deliveryid ○
                    // 配送先が1つの場合は0。複数ある場合は1以降の連番を半角数字。 // 4 status ○ 配送ステータスを変更します。 // 1：配送指示済 //
                    // 2：配送準備中 // 3：配送完了 // 9：返送 // 5 carrier 配送伝票業者を配送業者コード // 6 deliverynum 配送伝票番号
                    // null入力で値を削除 // 7 send_mail ○ 固定値で1を指定します。 // 8 result キャンセル理由 ＋（API）[改行] ＋
                    // 既存のメモ欄 EUC-JPでURLエンコード
                    // 受注番号
                    String orderNo = shipment.getOrder_no();
                    // 配送伝票業者
                    String carrier = getDeliveryCarrier(shipment.getDelivery_carrier());
                    // 配送伝票番号
                    String trackingNms = shipment.getDelivery_tracking_nm();
                    if (!StringTools.isNullOrEmpty(trackingNms)) {
                        trackingNms = trackingNms.split(",")[0];
                    }
                    // 倉庫コード
                    String warehouseCd = shipment.getWarehouse_cd();
                    // 配送プランID
                    String shipmentPlanId = shipment.getShipment_plan_id();
                    // MakeShopのURLを組み立てる
                    String cmd = "deliver";
                    String apiUrl = data.getClient_url();
                    String shopid = data.getBikou2();
                    String token = data.getApi_key();
                    // 配送先1つのみ固定値0を指定
                    // 1：配送指示済 2：配送準備中3：配送完了 9：返送
                    // 配送先1つのみ固定値0を指定
                    url.append("https://").append(apiUrl)
                        .append("/api/orderinfo/index.html?")
                        .append("cmd=").append(cmd)
                        .append("&shopid=").append(shopid)
                        .append("&token=").append(token)
                        .append("&service=sunlogi")
                        .append("&ordernum=").append(orderNo)
                        .append("&deliveryid=0") // 配送先1つのみ固定値0を指定
                        .append("&status=3")
                        .append("&carrier=").append(carrier)
                        .append("&deliverynum=").append(trackingNms)
                        .append("&send_mail=1"); // 固定値1を指定
                    String responseStr = HttpClientUtils.sendGet(url.toString(), null, null, null);
                    JSONObject jsonObject = JSONObject.parseObject(responseStr);
                    if (Objects.isNull(jsonObject)) {
                        continue;
                    }
                    JSONObject response = jsonObject.getJSONObject("response");
                    // ロケ値を取得できない場合、処理せず
                    if (!StringTools.isNullOrEmpty(response)) {
                        // 注文番号 半角数値26桁
                        String ordernum = response.getString("ordernum");
                        // code レスポンスコード 半角数値3ケタ
                        // message エラーメッセージ
                        String message = response.getString("message");
                        // 更新が成功した場合、API連携を連携済に変更
                        if (!StringTools.isNullOrEmpty(message) && "OK".equals(message)) {
                            shipmentsService.setShipmentFinishFlg(clientId, warehouseCd, shipmentPlanId);
                            counts++;
                            logger.info("MakeShop伝票連携OK" + " 店舗ID:" + clientId + " 受注ID:" + ordernum + " 伝票ID:"
                                + trackingNms);
                        } else {
                            logger.warn("MakeShop伝票連携NG" + " 店舗ID:" + clientId + " 受注ID:" + ordernum + " 伝票ID:"
                                + trackingNms + " 原因:" + message);
                            errs++;
                        }
                    }
                } catch (Exception e) {
                    logger.error("MakeShop伝票連携NG 店舗ID:" + clientId + " API:" + url + "");
                    logger.error(BaseException.print(e));
                }
            }
        }
        logger.info("MakeShop伝票自動連携件数(OK:" + counts + " NG:" + errs + ")");
        logger.info("MakeShop伝票自動連携 終了");
    }

    /**
     * 出庫ステータス自動連携 (10分ごと自動起動 3, 18, 33, 48)
     *
     * @author YuanMingZe
     * @date 2021/07/02
     */
    // @Scheduled(cron = "0 3/15 * * * ?")
    public void updateShipmentStatus() {
        logger.info("MakeShop出庫ステータス連携 開始");
        // MakeShopに関する情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.MAKESHOP.getName());
        // MakeShopに関する情報を全て処理
        for (Tc203_order_client data : allData) {
            // 初期化
            StringBuilder url = new StringBuilder();
            List<Tw200_shipment> tw200Shipments;
            // 店舗ID
            String clientId = data.getClient_id();
            // 受注データ取得 取得条件：該当店舗、受注データの支払ステータスが未入金、del_flgが0の場合
            try {
                tw200Shipments = orderDao.getOrderByStatus(clientId);
                for (Tw200_shipment tw200Shipment : tw200Shipments) {
                    // 倉庫コード
                    String warehouseCd = tw200Shipment.getWarehouse_cd();
                    // 出庫ID
                    String shipmentPlanId = tw200Shipment.getShipment_plan_id();
                    // URLを組み立てる
                    String cmd = "get";
                    String apiUrl = data.getClient_url();
                    String shopId = data.getBikou2();
                    String token = data.getApi_key();
                    String orderNum = tw200Shipment.getOrder_no();
                    url.append("https://").append(apiUrl)
                        .append("/api/orderinfo/index.html?")
                        .append("cmd=").append(cmd)
                        .append("&shopid=").append(shopId)
                        .append("&token=").append(token)
                        .append("&ordernum=").append(orderNum)
                        .append("&service=sunlogi");
                    // GetでMakeShopから注文情報を取得
                    String responseStr;
                    try {
                        responseStr = HttpClientUtils.sendGet(url.toString(), null, null, null);
                    } catch (Exception e) {
                        logger.info("MakeShopの受注データの取得は失敗しました。");
                        logger.error(BaseException.print(e));
                        continue;
                    }
                    JSONObject resultObj = JSONObject.parseObject(responseStr);
                    // 注文ない場合
                    JSONObject order = null;
                    if (!StringTools.isNullOrEmpty(resultObj)
                        && !StringTools.isNullOrEmpty(resultObj.getJSONObject("orders"))) {
                        order = resultObj.getJSONObject("orders").getJSONObject("order");
                        logger.info("MakeShop受注データ取得成功　店舗ID【{}】 受注番号【{}】", clientId, orderNum);
                    }

                    // 受注件数を取得しない場合、処理スキップ
                    if (order == null) {
                        logger.info("MakeShop受注データ取得失敗　店舗ID【{}】 受注番号【{}】", clientId, orderNum);
                        continue;
                    } else {
                        // MakeShopの支払ステータス
                        Integer paymentStatus = order.getInteger("payment_status");
                        // MakeShopの支払ステータスが入金済みで、かつSunLogiの出庫ステータスと違う場合、更新処理を行う
                        if (PAID.equals(paymentStatus)) {
                            try {
                                orderService.upOrderStatus(clientId, orderNum, warehouseCd, shipmentPlanId, null);
                                logger.info("MakeShop受注連携 店舗ID:" + clientId + " 受注ID:" + orderNum
                                    + " 入金待ちを入金済みに変更");
                            } catch (Exception e) {
                                logger.error(BaseException.print(e));
                                logger.error("店舗ID【{}】出庫依頼番号【{}】の出庫ステータス更新失敗しました。", clientId, shipmentPlanId);
                            }
                        }
                        logger.info("MakeShopの出庫管理情報更新OK 店舗ID【{}】 受注番号【{}】 受注ステータス【{}】", clientId, orderNum,
                            paymentStatus);
                    }
                    // リクエストは1秒間に1回までです。 これを超えると負荷の高いアクセスとみなされアクセス遮断の対象となります。
                    // 1秒間に1回はAPI全体での制限で、注文APIと会員APIで同時にアクセスすると秒間2回のリクエストカウントとなり、アクセス遮断の対象となります。
                    // 1000ミリ秒を設定する
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                logger.error("MakeShopの出庫ステータス更新処理が失敗しました。");
                logger.error(BaseException.print(e));
            }
        }
        logger.info("MakeShop出庫ステータス連携 終了");
    }

    /**
     * 在庫数自動連携 (10分ごと自動起動 0,10,20,30,40,50)
     *
     * @author YuanMingZe
     * @date 2020/12/25
     */
    // @Scheduled(cron = "30 3/20 * * * ?")
    public void updateStock() {
        logger.info("MakeShop在庫連携 開始");
        // MakeShopに関する情報(全部)取得
        List<Tc203_order_client> allData = apiService.getAllDataStock(API.MAKESHOP.getName());
        for (Tc203_order_client data : allData) {
            // 店舗ID(SunLogi)
            String clientId = data.getClient_id();
            // API番号
            Integer apiId = data.getId();
            // Mc106_produce_renkeiテーブルから、商品データ取得
            List<Mc106_produce_renkei> renkeiProducts;
            try {
                renkeiProducts = productRenkeiDao.getDataByClientIdAndApiId(clientId, apiId);
            } catch (Exception e) {
                logger.error("店舗ID【{}】の連携商品データ取得失敗しました。", clientId);
                logger.error(BaseException.print(e));
                continue;
            }
            if (Collections.isEmpty(renkeiProducts)) {
                logger.info("店舗ID【{}】の連携商品データなし、処理スキップ", clientId);
                continue;
            }
            List<String> renkeiProductIds =
                renkeiProducts.stream().map(Mc106_produce_renkei::getProduct_id).collect(Collectors.toList());
            // 倉庫情報取得
            List<String> warehouseCds = orderDao.getWarehouseIdListByClientId(clientId);
            String warehouseCd = warehouseCds.get(0);
            // 連携商品の在庫情報を取得する
            List<Tw300_stock> stocks;
            try {
                stocks = stockDao.getStocksByProductIds(clientId, warehouseCd, renkeiProductIds);
            } catch (Exception e) {
                logger.error(BaseException.print(e));
                logger.error("店舗ID【{}】の連携商品の在庫情報が取得失敗しました。", clientId);
                continue;
            }
            if (Collections.isEmpty(stocks)) {
                logger.error("店舗ID【{}】 の連携商品の在庫情報なし、処理スキップ", clientId);
                continue;
            }
            // オプション独自コードが設定されている商品
            List<String[]> optionDataList = new ArrayList<>();
            // システムコードや商品独自コードが設定されている商品
            List<String[]> normalDataList = new ArrayList<>();
            for (Tw300_stock stock : stocks) {
                for (Mc106_produce_renkei renkeiProduct : renkeiProducts) {
                    if (renkeiProduct.getProduct_id().equals(stock.getProduct_id())) {
                        // 商品オプションコード指定した場合、オプション一括登録のAPIを使う
                        if ("2".equals(renkeiProduct.getInventory_type())) {
                            String[] item = new String[12];
                            // 商品特定コード指定(0 = システム商品コード、1 = 独自商品コード)
                            item[0] = "0";
                            // オプション特定コード指定(0 = オプション独自コード、1 = オプション1項目＋オプション2項目)
                            item[1] = "0";
                            // システム商品コード
                            item[2] = renkeiProduct.getRenkei_product_id();
                            // オプション独自コード
                            item[6] = renkeiProduct.getVariant_id();
                            // オプション1項目
                            item[7] = "";
                            // オプション2項目
                            item[8] = "";
                            if (!StringTools.isNullOrEmpty(renkeiProduct.getBiko())) {
                                String[] options = renkeiProduct.getBiko().split(",");
                                if (options.length == 0) {
                                    // オプション1項目
                                    String[] option = renkeiProduct.getBiko().split(":");
                                    if (option.length == 2) {
                                        item[7] = option[1].trim();
                                    }
                                } else {
                                    // オプション1項目
                                    if (!StringTools.isNullOrEmpty(options[0])) {
                                        String[] option = options[0].split(":");
                                        if (option.length == 2) {
                                            item[7] = option[1].trim();
                                        }
                                    }
                                    // オプション2項目
                                    if (!StringTools.isNullOrEmpty(options[1])) {
                                        String[] option = options[1].split(":");
                                        if (option.length == 2) {
                                            item[8] = option[1].trim();
                                        }
                                    }

                                }
                            }
                            // 数量
                            item[10] = String.valueOf(stock.getAvailable_cnt());
                            optionDataList.add(item);
                        } else {
                            // 商品一括登録のAPIを使う
                            String[] item = new String[79];
                            // 商品特定コード指定
                            item[0] = renkeiProduct.getInventory_type();
                            // システム商品コード
                            item[2] = renkeiProduct.getRenkei_product_id();
                            // 独自商品コード
                            item[3] = renkeiProduct.getVariant_id();
                            // 数量
                            item[15] = String.valueOf(stock.getAvailable_cnt());
                            normalDataList.add(item);
                        }
                        break;
                    }
                }
            }
            // オプション商品一括登録ファイル名
            String optionCsvFileName =
                "MakeShop_Option_Stock_Update_" + clientId + "_" + LocalDateTime.now().format(TIME_FORMAT_STR) + ".csv";
            // 商品一括登録ファイル名
            String normalCsvFileName =
                "MakeShop_Normal_Stock_Update_" + clientId + "_" + LocalDateTime.now().format(TIME_FORMAT_STR) + ".csv";
            // 共通パラメータ
            JSONObject commonParams = new JSONObject();
            commonParams.put("id", data.getBikou2());
            commonParams.put("pw", data.getPassword());
            commonParams.put("clientId", clientId);
            // CSVファイルをMakeShopに発送する
            if (!Collections.isEmpty(optionDataList)) {
                processCsvFile(commonParams, optionDataList, optionCsvFileName, OPTION_CSV_HEADER, "2");
            }
            if (!Collections.isEmpty(normalDataList)) {
                processCsvFile(commonParams, normalDataList, normalCsvFileName, NORMAL_CSV_HEADER, "1");
            }
        }
        logger.info("MakeShop在庫連携 終了");
    }

    /**
     * CSVファイルに対する処理を行う
     *
     * @param commonParams 共通パラメータ
     * @param dataList CSVデータ
     * @param fileName ファイル名
     * @param header CSVヘッダー
     * @param dest 分類
     * @author YuanMingZe
     * @date 2021/07/07
     */
    private void processCsvFile(JSONObject commonParams, List<String[]> dataList, String fileName, String header,
        String dest) {
        // アクセストークンを取得
        String AUTH_URL = "https://www.makeshop.jp/api/webftp/index.html";
        HashMap<String, String> authBody = new HashMap<>();
        authBody.put("id", commonParams.getString("id"));
        authBody.put("pw", commonParams.getString("pw"));
        String apiResponse;
        try {
            apiResponse = getAccessUrl(AUTH_URL, authBody);
        } catch (Exception e) {
            logger.error("アクセストークンを取得する時、異常が発生しました。");
            logger.error(BaseException.print(e));
            return;
        }
        logger.info("apiResponse :{}", apiResponse);
        if (StringTools.isNullOrEmpty(apiResponse) || "FAILURE".equals(apiResponse)) {
            return;
        }
        // アクセスURL
        String accessUrl = apiResponse.split("\n")[0];
        // アクセスキー
        String key = apiResponse.split("\n")[1];
        // 更新つもり商品データをCSVファイルに書き込む
        CsvUtils.write(fileName, header, dataList, true);
        File csvFile = new File(fileName);
        // CSVファイルを発送する
        String response = sendCsv(accessUrl, key, dest, csvFile);
        // 生成されたCSVファイルを削除する
        try {
            Files.delete(Paths.get(String.valueOf(csvFile)));
        } catch (Exception e) {
            logger.error("CSVファイル削除失敗しました。");
            logger.error(BaseException.print(e));
        }
        // レスポンスを出力する
        if (StringTools.isNullOrEmpty(response)) {
            logger.error("商品一括更新処理は失敗しました。");
        } else {
            logger.info("店舗ID【{}】の在庫連携処理が完了しました。結果は下記となります。", commonParams.getString("clientId"));
            logger.info(response);
        }
    }

    /**
     * CSVファイルを発送する
     *
     * @param accessUrl アクセスURL
     * @param key キー
     * @param dest ファイル分類
     * @param csvFile CSVファイル
     * @return String レスポンス
     */
    private String sendCsv(String accessUrl, String key, String dest, File csvFile) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("key", key);
        // srcは固定的に1を設定する
        builder.addTextBody("src", "1");
        // 実行ファイルの分類
        // 0：カテゴリー登録/修正
        // 1：商品登録/修正
        // 2：オプション登録/修正
        // 3：会員グループ別価格登録/修正（BtoBオプションのご契約が必要です）
        builder.addTextBody("dest", dest);
        builder.addBinaryBody(
            "upload_file",
            csvFile,
            ContentType.MULTIPART_FORM_DATA,
            csvFile.getName());
        HttpEntity multipart = builder.build();
        return HttpClientUtils.sendPost(accessUrl, multipart, null, null, null);
    }

    /**
     * アクセスURLを取得
     *
     * @param url リクエストURL
     * @param body リクエストBody
     * @return String レスポンス
     */
    private String getAccessUrl(String url, HashMap<String, String> body) {
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        if (body != null) {
            for (Map.Entry<String, String> entry : body.entrySet()) {
                valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
        } catch (Exception e) {
            logger.error("HttpEntityの作成は失敗しました。");
            logger.error(BaseException.print(e));
        }
        return HttpClientUtils.sendPost(url, entity, null, null, null);
    }

    /**
     * 受注管理表の登録
     *
     * @param order 受注データ
     * @param init 店舗情報
     * @return List<String> エラーメッセージリスト
     * @author YuanMingZe
     * @date 2021/06/30
     */
    private List<String> setTc200Json(JSONObject order, InitClientInfoBean init) {

        // 初期化
        Tc200_order tc200Order = new Tc200_order();
        List<String> errList = new ArrayList<>();

        // 受注タイプ
        Integer orderType = order.getInteger("com_orderType");
        // 店舗ID
        String clientId = init.getClientId();
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
        // 自動出庫(1:自動出庫 0:出庫しない)
        Integer shipmentStatus = init.getShipmentStatus();

        // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMMSS-00001」
        String purchaseOrderNo = APICommonUtils.getOrderNo(subNo, identification);
        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 受注タイプ（0:入金待ち 1:入金完了）
        tc200Order.setOrder_type(orderType);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(order.getString("ordernum"));
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 个口数(Default値:1)
        tc200Order.setBoxes(1);
        // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済)
        tc200Order.setOuter_order_status(0);
        // 必要字段写入（receiver_zip_code1）（Import_datetime）(Receiver_address1)(Receiver_todoufuken)(Receiver_family_name)
        try {
            // 受注時間
            tc200Order.setOrder_datetime(CommonUtils.dealDateFormat(order.getString("date")));
            // 作成時間
            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
            // 配送者の住所情報取得
            JSONObject delivery = order.getJSONObject("deliveries").getJSONObject("delivery");
            if (!StringTools.isNullOrEmpty(delivery)) {
                List<String> zipList = CommonUtils.checkZipToList(delivery.getString("zip"));
                // 配送先郵便番号
                if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                    tc200Order.setReceiver_zip_code1(zipList.get(0));
                    tc200Order.setReceiver_zip_code2(zipList.get(1));
                } else {
                    // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
                    logger.warn("MakeShop受注(" + purchaseOrderNo + "):データ不正(配送者郵便)");
                    tc200Order.setReceiver_zip_code1("000");
                    tc200Order.setReceiver_zip_code2("0000");
                }
                // 配送先都道府県
                String area = delivery.getString("area");
                if ("東京(23区内)".equals(area) || "東京(23区外)".equals(area)) {
                    tc200Order.setReceiver_todoufuken("東京都");
                } else {
                    tc200Order.setReceiver_todoufuken(area);
                }
                // 配送住所(市区町+住所1)
                if (!StringTools.isNullOrEmpty(delivery.getString("city"))) {
                    tc200Order.setReceiver_address1(delivery.getString("city"));
                    tc200Order.setReceiver_address2(delivery.getString("street"));
                } else {
                    errList.add("配送先の住所が取得できないため、MakeShop店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送先名前
                if (!StringTools.isNullOrEmpty(delivery.getString("name"))) {
                    tc200Order.setReceiver_family_name(delivery.getString("name"));
                } else {
                    errList.add("配送先の名前が取得できないため、MakeShop店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送先カナ
                if (!StringTools.isNullOrEmpty(delivery.getString("kana"))) {
                    tc200Order.setReceiver_family_kana(delivery.getString("kana"));
                } else {
                    errList.add("配送先のカナが取得できないため、MakeShop店舗の注文情報をご確認ください。");
                    return errList;
                }
                // 配送先電話番号
                String phone = delivery.getString("tel");
                if (!StringTools.isNullOrEmpty(phone)) {
                    List<String> telList = CommonUtils.checkPhoneToList(phone);
                    if (!Collections.isEmpty(telList)) {
                        tc200Order.setReceiver_phone_number1(telList.get(0));
                        tc200Order.setReceiver_phone_number2(telList.get(1));
                        tc200Order.setReceiver_phone_number3(telList.get(2));
                    }
                }
                // 配送先メールアドレス
                tc200Order.setOrder_mail(delivery.getString("email"));

                // 配送方法
                List<String> carriages = getJSONString(delivery.getString("carriage"), "name");
                String deliveryName = "";
                if (!Collections.isEmpty(carriages)) {
                    deliveryName = carriages.get(0);
                }
                // 配送方法取得
                Ms004_delivery ms004Delivery = apiCommonUtils.getDeliveryMethod(deliveryName, defaultDeliveryMethod,
                    ms007SettingDeliveryMethodMap);
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
                // 元の配送方法を備考9に保管する @Add wang 2021/3/30
                tc200Order.setBikou9(deliveryName);

                // 配送予定日(yyyy/mm/dd)
                String deliveryDate = delivery.getString("deliverydate");
                tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));
                // 配送時間帯 (配送連携設定管理表「ms007_setting」から取得)
                String deliveryTime = delivery.getString("deliverytime");
                String deliveryTimeId = apiCommonUtils.getDeliveryTimeSlot(deliveryName, deliveryTime,
                    API.MAKESHOP.getName(), ms007SettingTimeMap);
                tc200Order.setDelivery_time_slot(deliveryTimeId);
                // 発送予定日(yyyy-mm-dd)
                tc200Order.setShipment_plan_date(DateUtils.stringToDate(delivery.getString("scheduled_shipping_date")));
            }
        } catch (Exception e) {
            errList.add("受注データ不正(配送先の郵便番号、住所、名前)が発生したので、注文情報をご確認ください。");
            logger.error(BaseException.print(e));
            return errList;
        }

        // 注文者情報
        JSONObject buyer = order.getJSONObject("buyer");
        List<String> zipList = CommonUtils.checkZipToList(buyer.getString("zip"));
        // 注文者郵便番号
        if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
            tc200Order.setOrder_zip_code1(zipList.get(0));
            tc200Order.setOrder_zip_code2(zipList.get(1));
        } else {
            // 郵便番号がない場合、「000-0000」JSONObject jsonObjectと設定する
            logger.warn("MakeShop受注(" + purchaseOrderNo + "):データ不正(注文者郵便)");
            tc200Order.setOrder_zip_code1("000");
            tc200Order.setOrder_zip_code2("0000");
        }
        // 注文者住所
        tc200Order.setOrder_family_name(buyer.getString("name"));
        tc200Order.setOrder_family_kana(buyer.getString("kana"));
        // 注文者都道府県
        String area = buyer.getString("area");
        if ("東京(23区内)".equals(area) || "東京(23区外)".equals(area)) {
            tc200Order.setOrder_todoufuken("東京都");
        } else {
            tc200Order.setOrder_todoufuken(area);
        }
        tc200Order.setOrder_address1(buyer.getString("city"));
        tc200Order.setOrder_address2(buyer.getString("street"));
        tc200Order.setOrder_mail(buyer.getString("email"));
        // 注文電話番号
        String phone = buyer.getString("tel");
        if (!StringTools.isNullOrEmpty(phone)) {
            List<String> telList = CommonUtils.checkPhoneToList(phone);
            if (telList != null && telList.size() > 0) {
                tc200Order.setOrder_phone_number1(telList.get(0));
                tc200Order.setOrder_phone_number2(telList.get(1));
                tc200Order.setOrder_phone_number3(telList.get(2));
            }
        }
        // 受注管理表 設定値
        // 支払方法
        List<String> type = getJSONString(order.getString("paymethod"), "type");
        List<String> name = getJSONString(order.getString("paymethod"), "content");
        order.getJSONObject("paymethod").getString("content");
        // 支払方法(連携設定ms007表に参考し、変換されたIDを取得)
        String paymentMethod;
        if (!StringTools.isNullOrEmpty(name)) {
            paymentMethod =
                apiCommonUtils.getPaymentMethod(name.get(0), API.MAKESHOP.getName(), ms007SettingPaymentMap);
            tc200Order.setPayment_method(paymentMethod);
            // 元々の支払方法を備考10も記載する
            tc200Order.setBikou10(name.get(0));
        }

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

        // 削除Flg(0削除しない)
        tc200Order.setDel_flg(0);

        // 注文明細
        // 商品合計金額 + 送料 + 決済手数料 - 割引金額 = 合計金額
        JSONObject orderDetail = order.getJSONObject("orderdetail");
        // 商品金額
        JSONArray jsonArray = new JSONArray();
        List<String> commodities = getJSONString(orderDetail.getString("commodities"), "commodity");
        for (String data : commodities) {
            if (data.startsWith("[") && data.endsWith("]")) {
                jsonArray = JSONArray.parseArray(data);
            } else {
                jsonArray.add(JSONObject.parseObject(data));
            }
        }
        // 受注総計金額
        int sumPrice = orderDetail.getInteger("sumprice");
        // 送料
        int carriage = orderDetail.getInteger("carriage");
        // まとめ買い割引額
        int bulk = orderDetail.getInteger("bulk") == null ? 0 : orderDetail.getInteger("bulk");
        // クーポン割引額
        int coupon = orderDetail.getInteger("coupon") == null ? 0 : orderDetail.getInteger("coupon");
        // 利用ポイント
        JSONArray usePoint = orderDetail.getJSONArray("usepoint");
        Integer shopPoint = 0;
        Integer gmoPoint = 0;
        Integer yahooPoint = 0;
        for (int i = 0; i < usePoint.size(); i++) {
            JSONObject item = usePoint.getJSONObject(i);
            if ("shop".equals(item.getString("type"))) {
                shopPoint = item.getInteger("content");
            } else if ("gmo".equals(item.getString("type"))) {
                gmoPoint = item.getInteger("content");
            } else if ("yahoo".equals(item.getString("type"))) {
                yahooPoint = item.getInteger("content");
            }
        }
        // その他金額 = まとめ買い割引額 + クーポン割引額 + 利用ポイント
        int otherFee = new BigDecimal(bulk).add(new BigDecimal(coupon)).add(new BigDecimal(shopPoint))
            .add(new BigDecimal(gmoPoint)).add(new BigDecimal(yahooPoint)).intValue();
        tc200Order.setOther_fee(abs(otherFee));
        // 手数料 (店舗側が設定しない場合、項目なし)
        JSONObject commissionObj = orderDetail.getJSONObject("commission");
        int commission = 0;
        if (!StringTools.isNullOrEmpty(commissionObj)) {
            commission = commissionObj.getInteger("content");
        }
        tc200Order.setHandling_charge(commission);
        // 商品販売価格(税率)
        tc200Order.setProduct_price_excluding_tax(sumPrice);
        // TODO 暫定対策：SU038店舗に対して、m-1の商品のオプション値をメモに記録する
        StringBuilder memo = new StringBuilder();
        if ("SU038".equals(clientId)) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                if ("m-".equals(data.getString("orgcode"))) {
                    memo.append(data.getString("option")).append(",");
                }
            }
        }
        // 代引引換
        if ("R".equals(type.get(0))) {
            tc200Order.setCash_on_delivery_fee(sumPrice);
        }
        // 合計請求金額
        tc200Order.setBilling_total(sumPrice);
        // 送料
        tc200Order.setDelivery_total(carriage);
        // 店舗側のメモ取得(※備考欄に記載する)
        JSONObject notes = order.getJSONObject("notes");
        JSONArray noteArray = new JSONArray();
        if (notes.get("note") instanceof JSONArray) {
            noteArray = (JSONArray) notes.get("note");
        } else {
            JSONObject obj = (JSONObject) notes.get("note");
            noteArray.add(obj);
        }
        for (int index = 0; index < noteArray.size(); index++) {
            JSONObject item = noteArray.getJSONObject(index);
            String title = item.getString("title");
            memo.append(title).append(":");
            String content = item.getString("content");
            if (StringTools.isNullOrEmpty(content)) {
                content = "";
            }
            memo.append(content);
            memo.append(",");
        }
        String memoStr = null;
        if (memo.length() > 0) {
            memoStr = memo.substring(0, memo.toString().length() - 1);
        }
        tc200Order.setMemo(memoStr);
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        // 作成者、作成日、更新者、更新日を設定する
        tc200Order.setIns_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setIns_usr(clientId);
        tc200Order.setUpd_date(new Timestamp(System.currentTimeMillis()));
        tc200Order.setUpd_usr(clientId);

        try {
            // 受注管理表登録
            if (Collections.isEmpty(errList)) {
                orderDao.insertOrder(tc200Order);
            } else {
                logger.warn("MakeShop受注連携 店舗ID:{} 受注ID:{} 原因:受注明細TBLの登録失敗", clientId, purchaseOrderNo);
            }
        } catch (Exception e) {
            logger.error("MakeShop受注連携 店舗ID:{} 受注ID:{} 原因:受注管理TBLの登録失敗", clientId, purchaseOrderNo);
            logger.error(BaseException.print(e));
            errList.add("何か原因により、MakeShop受注連携の取込が失敗しました。システム担当者にお問い合わせください。");
            return errList;
        }

        if (Collections.isEmpty(errList)) {
            // 受注明細登録
            errList = insTc201DetailData(orderDetail, init, purchaseOrderNo);
        } else {
            logger.warn("MakeShop受注連携 店舗ID:{} 受注ID:{} 原因:受注管理TBLの登録失敗", clientId, purchaseOrderNo);
            // 受注管理レコード削除
            orderDao.orderDelete(clientId, purchaseOrderNo);
            // 受注明細レコード削除
            orderDetailDao.orderDetailDelete(purchaseOrderNo);
        }
        // 出庫依頼処理 自動出庫(0:出庫しない 1:自動出庫)
        if (Collections.isEmpty(errList)) {
            if (shipmentStatus == 1) {
                apiCommonUtils.processShipment(purchaseOrderNo, clientId);
            }
        } else {
            logger.warn("MakeShop受注連携 店舗ID:{} 受注ID:{} 原因:受注管理TBLの登録失敗", clientId, purchaseOrderNo);
            // 受注管理レコード削除
            orderDao.orderDelete(clientId, purchaseOrderNo);
            // 受注明細レコード削除
            orderDetailDao.orderDetailDelete(purchaseOrderNo);
        }
        return errList;
    }

    /**
     * 商品情報をサンロジに登録
     *
     * @param insProductBean 商品レコード
     * @param codeCategory 商品コード種別
     * @return List<String> 商品情報リスト
     * @author YuanMingZe
     * @date 2021/06/30
     */
    private Mc100_product insertProductData(ProductBean insProductBean, String codeCategory) {
        // 初期化
        Mc100_product mc100Product;
        String clientId = insProductBean.getClientId();
        String code = insProductBean.getCode();
        String options = insProductBean.getOptions();

        // 商品オプションでmc110_productテーブルに検索して、結果があれば、codeを使って、mc100_productテーブルに検索する。
        // mc110_product_optionsテーブルの対象を取得
        mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);

        // 商品登録されていない場合、商品マスタに仮商品として新規登録
        if (StringTools.isNullOrEmpty(mc100Product)) {
            // 商品新規登録
            mc100Product = apiCommonUtils.insertMc100Product(insProductBean, API.MAKESHOP.getName());
        }
        if (Objects.isNull(mc100Product)) {
            throw new BaseException(ErrorCode.E_11005);
        }
        // 外部商品連携管理TBL
        Mc106_produce_renkei mc106Product = new Mc106_produce_renkei();
        // API番号
        mc106Product.setApi_id(insProductBean.getApiId());
        // 店舗ID
        mc106Product.setClient_id(clientId);
        // 商品ID
        mc106Product.setProduct_id(mc100Product.getProduct_id());
        // 商品ID(外部SKU商品コード)
        mc106Product.setRenkei_product_id(insProductBean.getRenkeiPid());
        // 検証ID(外部在庫ロケーションID) ※デフォルト値(default)として設定
        // MakeShopの場合、variant_idカラムに特定商品コードを設定する
        mc106Product.setVariant_id(code);
        // MakeShopの場合、inventory_typeカラムにどの種類コードが保存されているの区分（（0:システムコード1:独自商品コード2:オプション商品コード））
        mc106Product.setInventory_type(codeCategory);
        // MakeShopの場合、bikoカラムに商品のオプション項目を設定する
        mc106Product.setBiko(insProductBean.getOptions());
        // 外部連携商品情報登録
        Integer rows = productDao.getRenkeiProduct(mc106Product);
        if (rows == 0) {
            mc106Product.setIns_usr("MakeShop自動");
            mc106Product.setIns_date(new Timestamp(System.currentTimeMillis()));
            productDao.insertRenkeiProduct(mc106Product);
        }
        return mc100Product;
    }

    /**
     * 受注明細テーブルの新規登録
     *
     * @param orderDetail 受注明細データ
     * @param client 店舗情報
     * @param purchaseOrderNo 受注番号
     * @return List<String>
     * @author YuanMingZe
     * @date 2021/07/05
     */
    private List<String> insTc201DetailData(JSONObject orderDetail, InitClientInfoBean client, String purchaseOrderNo) {
        // 初期化
        List<String> errList = new ArrayList<>();

        // 店舗ID
        String clientId = client.getClientId();
        // API番号
        Integer apiId = client.getApiId();

        // 商品明細
        JSONObject commodities = orderDetail.getJSONObject("commodities");
        JSONArray commodity = new JSONArray();
        if (commodities.get("commodity") instanceof JSONArray) {
            commodity = (JSONArray) commodities.get("commodity");
        } else {
            JSONObject obj = (JSONObject) commodities.get("commodity");
            commodity.add(obj);
        }

        // 受注子番号
        Integer subNo = 1;
        for (int i = 0; i < commodity.size(); i++) {
            Tc201_order_detail tc201OrderDetail = new Tc201_order_detail();
            // 商品情報
            JSONObject product = commodity.getJSONObject(i);
            // MakesShopのシステム商品IDを連携商品IDとして格納する
            String renkeiProductId = product.getString("brandcode");
            String code = product.getString("brandcode");
            // 商品コードのカテゴリー（0:システムコード1:独自商品コード2:オプション商品コード）
            String codeCategory = "0";
            // 商品コード(オプション商品コード⇒独自商品コード⇒システムコード)
            if (!StringTools.isNullOrEmpty(product.getString("orgoptioncode"))) {
                code = product.getString("orgoptioncode");
                codeCategory = "2";
            } else {
                if (!StringTools.isNullOrEmpty(product.getString("orgcode"))) {
                    code = product.getString("orgcode");
                    codeCategory = "1";
                }
            }
            // 商品JAN
            String jancode = product.getString("jancode");
            // 商品名
            String name = product.getString("name");
            // 商品単価（税抜き）
            Integer price = product.getInteger("price");
            // 商品数量
            Integer nums = product.getInteger("amount");
            // 消費税率
            Integer taxRate = product.getInteger("consumption_tax_rate");
            // 軽減税率の取得(0:税率10% 1:税率8%)
            int isReducedTax = 0;
            if (taxRate == 8) {
                isReducedTax = 1;
            }
            // 項目選択肢の値
            String option = product.getString("option");
            // 全角【：】を半角にする
            if (!StringTools.isNullOrEmpty(option)) {
                option = option.replace("：", ":");
                option = option.replace(" : ", ":");
            }

            // **********************************************************
            // ********************* 共通パラメータ整理 *********************
            // **********************************************************
            ProductBean insProductBean = new ProductBean();
            insProductBean.setClientId(clientId);
            insProductBean.setApiId(apiId);
            insProductBean.setCode(code);
            insProductBean.setName(name);
            insProductBean.setPrice(String.valueOf(price));
            insProductBean.setIsReducedTax(isReducedTax);
            insProductBean.setRenkeiPid(renkeiProductId);
            insProductBean.setOptions(option);
            insProductBean.setBarcode(jancode);

            // 商品コードにより、商品情報を取得
            Mc100_product mc100Product;
            try {
                mc100Product = insertProductData(insProductBean, codeCategory);
            } catch (BaseException e) {
                logger.error("MakeShop受注連携NG 店舗ID:{} 受注ID:　原因:商品マッピング失敗", clientId);
                logger.error(BaseException.print(e));
                errList.add("受注明細の商品取込が失敗しました。");
                return errList;
            }

            tc201OrderDetail.setProduct_kubun(mc100Product.getKubun());
            // 商品ID
            tc201OrderDetail.setProduct_id(mc100Product.getProduct_id());
            // 商品コード
            code = mc100Product.getCode();
            // 商品オプション値を保管
            tc201OrderDetail.setProduct_option(option);
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // 商品名称（受注の商品名をそのままに保管）
            tc201OrderDetail.setProduct_name(name);
            // 商品単価（税抜き）
            tc201OrderDetail.setUnit_price(price);
            // 数量
            tc201OrderDetail.setNumber(nums);
            // 商品総計（税込み）（商品単価 * (1 + 税率) * 数量）
            BigDecimal bigPrice = new BigDecimal(price);
            BigDecimal bigTaxRate = new BigDecimal("1.00").add(new BigDecimal(taxRate).divide(BigDecimal.valueOf(100)));
            BigDecimal bigNum = new BigDecimal(nums);
            // 価格単価を取得できない場合、商品計が0円として計算
            int totalPrice = bigPrice.multiply(bigTaxRate).multiply(bigNum).intValue();
            tc201OrderDetail.setProduct_total_price(totalPrice);
            // 軽減税率(0:税率10% 1:税率8%)
            tc201OrderDetail.setIs_reduced_tax(isReducedTax);
            // 商品単価が税抜きの単価を使っているので、税区分を固定に【1:税抜き】にします。
            tc201OrderDetail.setTax_flag(1);
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
            // 受注明細番号
            tc201OrderDetail.setOrder_detail_no(purchaseOrderNo + "-" + String.format("%03d", subNo));
            // 受注番号
            tc201OrderDetail.setPurchase_order_no(purchaseOrderNo);
            // 商品オプション
            tc201OrderDetail.setProduct_option(option);
            // 削除フラグ
            tc201OrderDetail.setDel_flg(0);
            try {
                orderDetailDao.insertOrderDetail(tc201OrderDetail);
            } catch (Exception e) {
                logger.error("MakeShop受注連携NG 店舗ID:{} 受注ID:　原因:受注明細TBLの登録失敗", clientId);
                logger.error(BaseException.print(e));
                errList.add("何か原因により、受注明細の取込が失敗したので、システム担当者にお問い合わせください。");
                return errList;
            }
            subNo++;
        }
        return errList;
    }

    /**
     * JSON対象取得
     *
     * @param data XMLデータをJSON化にした後の文字列
     * @param key 対象データの名前
     * @return List<String> 文字列のリスト
     * @author YuanMingZe
     * @date 2021/07/05
     */
    private List<String> getJSONString(String data, String key) {
        // 初期化
        JSONArray jsonArray;
        JSONObject jsonData;
        List<String> list = new ArrayList<>();

        if (!StringTools.isNullOrEmpty(data)) {
            // JSONArrayかJSONObjectか判断する
            if (data.startsWith("[") && data.endsWith("]")) {
                jsonArray = JSONArray.parseArray(data);
                // 中身を解析する
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonArrayObject = (JSONObject) jsonArray.get(i);
                    list.add(i, jsonArrayObject.getString(key));
                }
            } else {
                jsonData = JSONObject.parseObject(data);
                list.add(jsonData.getString(key));
            }
        }
        return list;
    }

    /**
     * 配送会社コードを転換する
     *
     * @param deliveryCarrier 配送会社コード
     * @return String 配送会社のコード
     * @author YuanMingZe
     * @date 2021/07/02
     */
    private String getDeliveryCarrier(String deliveryCarrier) {
        // 根据配送Id 获取配送信息
        Ms004_delivery delivery = deliveryDao.getDeliveryById(deliveryCarrier);
        String carriers = null;
        // 配送会社
        String deliveryNm = delivery.getDelivery_nm();
        // 配送方法名
        String deliveryMethodName = delivery.getDelivery_method_name();
        // 判断配送会社名 是否有相对应的 代号
        if (deliveryCarriersMap.containsKey(deliveryNm)) {
            carriers = deliveryCarriersMap.get(deliveryNm);
            return carriers;
        }
        // 判断配送方法名 是否邮箱对应的 代号
        if (deliveryCarriersMap.containsKey(deliveryMethodName)) {
            carriers = deliveryCarriersMap.get(deliveryMethodName);
        }
        return carriers;

        // Ms007_settingテーブルのレコードを取る。区分は固定に1に設定する。区分：1:運輸会社 2:運輸時間帯 3:支払方法
        // List<Ms007_setting> ms007Settings = deliveryDao.getConvertedDataAll(clientId, 1);
        // String carriers = null;
        // // 紐づける配送コードがなければ、nullを返す。
        // for (Ms007_setting setting : ms007Settings) {
        // if (setting.getConverted_id().equals(deliveryCarrier)) {
        // carriers = deliveryCarriersMap.get(setting.getMapping_value());
        // break;
        // }
        // }
    }
}
