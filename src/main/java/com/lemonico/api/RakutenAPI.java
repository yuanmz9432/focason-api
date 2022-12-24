package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.utils.Rakuten.*;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.dao.ProductRenkeiDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderApiService;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.xml.rpc.ServiceException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


/**
 * RakutenのAPI連携機能
 *
 * @author YuanMingZe
 * @className RakutenAPI
 * @date 2021/07/15
 **/
@Component
@EnableScheduling
public class RakutenAPI
{

    private final static Logger logger = LoggerFactory.getLogger(RakutenAPI.class);
    // 楽天API：受注検索URL
    static final String SEARCH_ORDER_URL = "https://api.rms.rakuten.co.jp/es/2.0/order/searchOrder/";
    // 楽天API：受注取得URL
    static final String GET_ORDER_URL = "https://api.rms.rakuten.co.jp/es/2.0/order/getOrder/";
    // 楽天API：出荷通知URL 送り状番号連携
    static final String UPDATE_ORDER_SHIPPING_ASYNC_URL =
        "https://api.rms.rakuten.co.jp/es/2.0/order/updateOrderShippingAsync/";
    // 楽天の検索範囲指定：時間形式
    static String TIME_FORMAT_STR = "yyyy'-'MM'-'dd'T'HH':'mm':'ss+0900";
    // MAP検索キー（renkei）
    static String MAPKEY_RENKEI = "renkei";
    // MAP検索キー（stock）
    static String MAPKEY_STOCK = "stock";
    // 除外処理（E999999）
    static String ERR_CATCH_KEY = "E999999";
    // 除外処理（E999999）
    final static Integer VERSION = 5;

    @Resource
    private OrderApiService orderApiService;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderHistoryDao orderHistoryDao;
    @Resource
    private DeliveryDao deliveryDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private ProductRenkeiDao productRenkeiDao;
    @Resource
    private OrderApiService apiService;
    @Resource
    private StockDao stockDao;
    @Resource
    private APICommonUtils apiCommonUtils;
    @Resource
    private MailTools mailTools;

    /**
     * 楽天店舗受注データ取得(JSON形式) (10分ごと自動起動 5,15,25,35,45,55)
     *
     * @author wang
     * @date 2021/3/24
     */
    // @Scheduled(cron = "0 5/10 * * * ?")
    public void fetchRakutenOrders() {
        // 受注連携開始
        logger.info("Rakuten受注連携 開始");
        // 店舗に関する楽天API連携情報を取得
        List<Tc203_order_client> allData = orderApiService.getAllDataOrder(API.RAKUTEN.getName());
        // 楽天API連携情報がない場合、処理スキップ
        if (allData == null || allData.size() == 0) {
            logger.info("Rakuten受注連携 店舗情報：0件");
            logger.info("Rakuten受注連携 終了");
            return;
        }
        Hashtable<String, List<String>> errHashtable = new Hashtable<>();
        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        apiCommonUtils.initialize();
        for (Tc203_order_client data : allData) {

            // 保存错误数据
            ArrayList<String> errList = new ArrayList<>();

            // 初期化
            JSONObject body = new JSONObject();
            JSONArray orderProgressList;
            JSONObject pageModel = new JSONObject();

            // アプリ情報取得
            String apiKey = data.getApi_key();
            String apiPwd = data.getPassword();
            // 店舗ID
            String clientId = data.getClient_id();

            // 検索条件：注文状況の取得(Default値:300)
            orderProgressList = setOrderOptions(data.getBikou1());
            // 検索条件：受注タイプ(Default値 3注文確定時間) ※備考2から1番目の値を取得
            Integer dateType = setOrderOptions(data.getBikou2(), "type");
            // 検索条件：時間単位(Default値:24時間) ※備考2から2番目の値を取得
            Integer timeUnit = setOrderOptions(data.getBikou2(), "time");
            // 検索条件：開始時間
            String after = new DateTime(System.currentTimeMillis() - 1000L * 3600 * timeUnit)
                .toString(TIME_FORMAT_STR);
            // 検索条件：終了時間(現在時刻より2分前)
            String before = new DateTime(System.currentTimeMillis() - 2 * 60 * 1000).toString(TIME_FORMAT_STR);
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007) TODO
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(data);

            int max = 100;
            for (int p = 1; p < max; p++) {
                /* 検索条件を取得 */
                // 検索条件：取得件数
                pageModel.put("requestRecordsAmount", max);
                // 検索条件：取得Page
                pageModel.put("requestPage", p);
                // 検索条件：受注タイプ(1:新規受注時間 2: 3:注文確定時間)
                body.put("dateType", dateType);
                // 検索条件：開始時間
                body.put("startDatetime", after);
                // 検索条件：終了時間(現在時刻より2分前)
                body.put("endDatetime", before);
                body.put("orderProgressList", orderProgressList);
                body.put("PaginationRequestModel", pageModel);
                // 検索条件をセット
                String bodyStr = body.toString();
                // 受注取込履歴の最新IDを取得
                String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
                Integer historyId = CommonUtils.getMaxHistoryId(lastOrderHistoryNo);
                // 楽天APIの認証トークンを生成
                String authorization = "ESA "
                    + Base64.getEncoder().encodeToString((apiKey + ":" + apiPwd).getBytes());
                HttpEntity entity;
                try {
                    entity = new StringEntity(bodyStr);
                } catch (Exception e) {
                    logger.error("楽天店舗ID【{}】受注取得時、リクエスト作成失敗。", clientId);
                    errList.add("楽天店舗ID【" + clientId + "】受注取得時、リクエスト作成失敗。");
                    errOrderClients.add(data);
                    continue;
                }

                /* 楽天から受注リスト情報を取得(POST) */
                String responseStr = sendPost(SEARCH_ORDER_URL, entity, "Authorization", authorization, errList);
                JSONObject responseData = JSONObject.parseObject(responseStr);
                JSONArray orderNumberList = null;
                // 楽天受注リストを確認
                if (responseData != null && responseData.size() > 0) {
                    orderNumberList = responseData.getJSONArray("orderNumberList");
                }
                // logger.info("Rakuten受注連携 店舗ID：{} API名:{} 条件:{}", clientId, apiNam, bodyStr);
                // 楽天受注がない場合、処理スキップ
                if (orderNumberList == null || orderNumberList.size() < 1) {
                    logger.info("Rakuten受注連携 店舗ID：{} 認証:【{}】【{}】【{}】", clientId, apiKey, apiPwd, authorization);
                    logger.info("Rakuten受注連携 店舗ID：{} 検索結果:{}", clientId, responseData);
                    // 楽天受注データがない場合、処理
                    p = max;
                    continue;
                }

                /* 楽天受注明細を取得 */
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("orderNumberList", orderNumberList);
                jsonObject.put("version", VERSION);
                // 楽天受注明細を取得
                try {
                    entity = new StringEntity(jsonObject.toString());
                } catch (Exception e) {
                    logger.error("楽天店舗ID【{}】受注明細取得時、リクエスト作成失敗。", clientId);
                    errList.add("楽天店舗ID【" + clientId + "】受注明細取得時、リクエスト作成失敗。");
                    continue;
                }
                responseStr = sendPost(GET_ORDER_URL, entity, "Authorization", authorization, null);
                JSONObject res = JSONObject.parseObject(responseStr);
                // 取込件数(初期値:0)
                int size = 0;
                JSONArray orderModelList = null;
                // 楽天受注明細がある場合、処理
                if (res != null && res.size() > 0) {
                    orderModelList = res.getJSONArray("OrderModelList");
                    size = orderModelList.size();
                }
                // 楽天受注データがない場合、処理スキップ
                if (size == 0) {
                    logger.info("Rakuten受注連携 店舗ID:{} 認証情報:【{}】【{}】【{}】", clientId, apiKey, apiPwd, authorization);
                    logger.info("Rakuten受注連携 店舗ID:{} 検索結果:{}", clientId, responseData);
                    // 取得件数が0場合、次の処理が行わない。
                    p = max;
                    continue;
                }
                // 受注件数
                int total = size;
                // 成功件数
                int successCnt = 0;
                // 失敗件数
                int failureCnt = 0;
                // 受注番号の枝番
                int subno = 1;
                // カウンタ数
                int cnt = p;
                // 受注データが100件以下の場合、2回目の受注取得が行わない
                if (total < max) {
                    p = max;
                }
                // 全て受注を処理
                for (int i = 0; i < size; i++) {
                    // 受注明細
                    JSONObject orders = orderModelList.getJSONObject(i);
                    // 楽天認証キー
                    orders.fluentPut("com_authorization", authorization);
                    // 受注番号
                    String orderNumber = orders.getString("orderNumber");
                    Integer outerOrderNo = orderDao.getOuterOrderNo(orderNumber, clientId);
                    // 過去受注番号がある場合、処理スキップ
                    if (outerOrderNo > 0) {
                        logger.warn("Rakuten受注連携NG 店舗ID：{} 受注ID：{} 原因:過去受注取込済", clientId, orderNumber);
                        total--;
                        continue;
                    }
                    // 受注番号がない場合、新規登録
                    if (outerOrderNo == 0) {
                        try {
                            // 受注子番号をセットする ※重要 TODO
                            initClientInfo.setSubNo(subno);
                            // 受注データを書込
                            List<String> orderList = setTc200Json(orders, initClientInfo);
                            // 受注明細の子番号
                            subno++;
                            // 成功かどうか
                            if (orderList.size() > 0) {
                                String msg = orderList.get(0);
                                // 除外処理以外のエラーに対して、処理
                                if (!ERR_CATCH_KEY.equals(msg)) {
                                    failureCnt++;// 失敗
                                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID:{}  原因:受注データ不正", clientId, orderNumber);
                                    // TODO 受注連携が失敗する場合、失敗内容を記録
                                    apiCommonUtils.insertTc207OrderError(clientId, historyId, orderNumber, msg,
                                        API.RAKUTEN.getName());
                                } else {
                                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID:{}  原因:商品取込対象外", clientId, orderNumber);
                                }
                            } else {
                                successCnt++;// 成功
                                logger.info("Rakuten受注連携OK 店舗ID:{} 受注ID:{} ", clientId, orderNumber);
                            }
                        } catch (Exception e) {
                            subno++;
                            failureCnt++;
                            logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID:{} 原因:受注データ不正", clientId, orderNumber);
                            errList.add("Rakuten受注連携NG 店舗ID:" + clientId + " 受注ID:" + orderNumber + " 原因:受注データ不正");
                            logger.error(BaseException.print(e));
                            errOrderClients.add(data);
                        }
                    }
                }
                // 受注履歴を記録
                if (successCnt > 0) {
                    apiCommonUtils.processOrderHistory(clientId, API.RAKUTEN.getName(), historyId, successCnt,
                        failureCnt);
                }
                logger.info("Rakuten受注連携 [{}回目] 店舗ID:{} 取込件数:{} 成功件数:{} 失败件数:{}", cnt, clientId, total, successCnt,
                    failureCnt);
            }

            if (!errList.isEmpty()) {
                String key = clientId + "_API名：" + data.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        // 受注連携終了
        logger.info("Rakuten受注連携 終了");
        // mailTools.sendErrorMessage(errHashtable, API.RAKUTEN.getName());
        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 获取mc106数据和tw300在库数据
     *
     * @param apiService apiService
     * @param productRenkeiDao productRenkeiDao
     * @param stockDao stockDao
     * @param template 模板
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     * @date 2021/1/12 16:43
     */
    public static HashMap<String, Object> getStockMap(OrderApiService apiService, ProductRenkeiDao productRenkeiDao,
        StockDao stockDao, String template, String clientId) {
        // a获取所有和template连携的信息
        List<Tc203_order_client> allData = apiService.getAllData(template);
        // a获取到属于base的 tc203的 id 集合
        List<Integer> idList = allData.stream().map(Tc203_order_client::getId).collect(Collectors.toList());
        if (idList.size() != 0) {
            // a根据api_id 获取到mc106的集合
            List<Mc106_produce_renkei> renkeiProducts = productRenkeiDao.getDataByApiId(idList);
            // a生成 以clientId - productId 为key值， mc106对象为value的 map
            HashMap<String, Mc106_produce_renkei> renkeiMap = (HashMap<String, Mc106_produce_renkei>) renkeiProducts
                .stream().filter(x -> !StringTools.isNullOrEmpty(x.getRenkei_product_id()))
                .collect(Collectors.toMap(renkei -> renkei.getClient_id() + "-" + renkei.getProduct_id(),
                    mc106ProduceRenkei -> mc106ProduceRenkei));
            // a从mc106表里面取到 sunlogi系统的商品id集合
            List<Tw300_stock> stocks = stockDao.getAllAvailableCntNew(clientId);
            // a以clientId - productId 为key值， 理论在库数为value
            HashMap<String, Integer> stockMap = (HashMap<String, Integer>) stocks.stream()
                .filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
                .collect(Collectors.toMap(stock -> stock.getClient_id() + "-" + stock.getProduct_id(),
                    Tw300_stock::getAvailable_cnt));
            HashMap<String, Object> map = new HashMap<>();
            map.put(MAPKEY_RENKEI, renkeiMap);
            map.put(MAPKEY_STOCK, stockMap);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 送り状番号の自動連携 (15分ごと自動起動 04,19,34,49)
     * 
     * @author wang
     * @date 2021/3/25
     */
    // @Scheduled(cron = "0 4/15 * * * ?")
    public void updateTrackingNum() {
        logger.info("Rakuten伝票連携 開始");

        // Rakuten情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> allData = apiService.getAllDataDelivery(API.RAKUTEN.getName());
        // 情報取得できない場合、処理スキップ
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client allDatum : allData) {
                String clientId = allDatum.getClient_id();
                String apiKey = allDatum.getApi_key();
                String apiPwd = allDatum.getPassword();
                String identifier = allDatum.getIdentification();

                // 楽天APIの認証トークンを生成
                String authorization = "ESA " + Base64.getEncoder().encodeToString((apiKey + ":" + apiPwd).getBytes());
                // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
                List<Tw200_shipment> shipments = orderDao.getRkShipmentInfoByIdentifier(identifier, clientId);
                // 初期化
                JSONArray request = new JSONArray();
                JSONObject requestDatas = new JSONObject();
                // 如果有需要返回传票番号的数据才有必要装填参数
                if (shipments != null && shipments.size() > 0) {
                    // サンロジから楽天店舗に紐づく受注番号を整理
                    for (Tw200_shipment shipment : shipments) {
                        request.add(shipment.getOrder_no());
                    }
                    // 処理対象がない場合、処理スキップ
                    if (request.size() > 0) {
                        requestDatas.put("orderNumberList", request);
                        requestDatas.put("version", VERSION);
                        // 楽天店舗から配送情報(既存)を取得
                        HttpEntity entity;
                        try {
                            entity = new StringEntity(requestDatas.toString());
                        } catch (Exception e) {
                            logger.error("楽天店舗ID【{}】送り状番号連携時、リクエスト作成失敗。", clientId);
                            continue;
                        }
                        String responseStr = sendPost(GET_ORDER_URL, entity, "Authorization", authorization, null);
                        JSONObject orderData = JSONObject.parseObject(responseStr);
                        try {
                            // 楽天から受注情報を取得できない、スキップ
                            if (Objects.nonNull(orderData)) {
                                // 楽天店舗への伝票連携を実施
                                updateDeliveryInfo(clientId, orderData.getJSONArray("OrderModelList"), shipments,
                                    authorization);
                            } else {
                                logger.warn("Rakuten伝票連携NG 店舗ID：" + clientId + " 原因:伝票番号の連携失敗");
                            }
                        } catch (Exception e) {
                            logger.warn("Rakuten伝票連携NG 店舗ID：" + clientId + " 原因:伝票番号の連携失敗");
                            logger.error(BaseException.print(e));
                        }
                    }
                }
            }
        }
        logger.info("Rakuten伝票連携 終了");
    }

    /**
     * 在庫の自動連携 (20分ごと自動起動 3,23,43)
     * 
     * @author wang
     * @date 2021/3/25
     */
    @SuppressWarnings("unchecked")
    // @Scheduled(cron = "30 4/20 * * * ?")
    public void updateStock() {
        logger.info("Rakuten在庫連携 開始");
        List<Tc203_order_client> allData = orderApiService.getAllDataStock(API.RAKUTEN.getName());
        // 情報取得できない場合、処理スキップ
        if (allData != null && allData.size() > 0) {
            for (Tc203_order_client data : allData) {
                // 楽天通信情報を確認
                InventoryapiLocator inventoryapiLocator = new InventoryapiLocator();
                InventoryapiPort_PortType inventoryapiPort_portType;
                HashMap<String, Object> hashMap;
                try {
                    inventoryapiPort_portType = inventoryapiLocator.getinventoryapiPort();
                } catch (ServiceException se) {
                    logger.warn("Rakuten在庫連携NG 詳細内容" + se.getLocalizedMessage());
                    continue;
                }
                // 店舗情報
                String clientId = data.getClient_id();
                String apiKey = data.getApi_key();
                String apiPwd = data.getPassword();
                // 楽天認証キー
                String authorization = "ESA " + Base64.getEncoder().encodeToString((apiKey + ":" + apiPwd).getBytes());

                // Auth认证
                ExternalUserAuthModel auth = new ExternalUserAuthModel();
                // 初始化Response
                UpdateResponseExternalModel result;
                // 楽天のリクエスト情報
                auth.setShopUrl(data.getClient_url());
                auth.setAuthKey(authorization);
                auth.setUserName(data.getApi_name());
                // 初期化(楽天リクエスト)
                UpdateRequestExternalModel model = new UpdateRequestExternalModel();
                // 外部連携の商品関連に関する情報を取得
                hashMap = getStockMap(apiService, productRenkeiDao, stockDao, API.RAKUTEN.getName(), clientId);
                if (StringTools.isNullOrEmpty(hashMap)) {
                    logger.warn("Rakuten在庫連携 対象商品：０件");
                    continue;
                }
                // 外部連携の商品情報を取得
                HashMap<String, Mc106_produce_renkei> renkeiMap = (HashMap<String, Mc106_produce_renkei>) hashMap
                    .get(MAPKEY_RENKEI);
                // 商品の在庫数を取得
                HashMap<String, Integer> stockMap = (HashMap<String, Integer>) hashMap.get(MAPKEY_STOCK);
                // 店舗の在庫数(stock_cnt)を更新する為、コレクション
                Map<String, String> itemMap = new HashMap<>();
                // 楽天のリクエスト情報を初期化
                ArrayList<UpdateRequestExternalItem> items = new ArrayList<>();
                for (Entry<String, Mc106_produce_renkei> map : renkeiMap.entrySet()) {
                    String key = map.getKey();
                    Mc106_produce_renkei produceRenkei = map.getValue();
                    // a理论在库数
                    Integer available_cnt = stockMap.get(key);
                    UpdateRequestExternalItem updateRequestExternalItem = new UpdateRequestExternalItem();
                    // 产品管理编号（产品URL）
                    updateRequestExternalItem.setItemUrl(produceRenkei.getRenkei_product_id());
                    updateRequestExternalItem.setInventoryType(2);
                    // 項目 TODO 未実装
                    // 在庫タイプ(0:設定しない※廃止 1:通常在庫 2:項目選択肢別在庫)
                    // String inventoryType = produceRenkei.getInventory_type();
                    // if("3".equals(inventoryType)) {
                    // 1:設定しない※廃止 2:通常在庫 3:項目選択肢別在庫
                    // updateRequestExternalItem.setInventoryType(2);
                    // String choices = produceRenkei.getBiko();
                    // updateRequestExternalItem.setHChoiceName(HChoiceName);
                    // updateRequestExternalItem.setVChoiceName(VChoiceName);
                    // }else {
                    // }
                    // 在庫更新(0:何もしない 1:初期値設定 2:加算 3:減算)
                    updateRequestExternalItem.setInventoryUpdateMode(1);
                    // 配送可在庫数が０以下場合、０にする
                    if (available_cnt != null && available_cnt > 0) {
                        updateRequestExternalItem.setInventory(available_cnt);
                    } else {
                        updateRequestExternalItem.setInventory(0);
                    }
                    // 楽天のリクエスト情報
                    items.add(updateRequestExternalItem);
                    // サンロジの店舗情報を記録するため、情報
                    itemMap.put(produceRenkei.getRenkei_product_id(),
                        produceRenkei.getProduct_id() + ":" + available_cnt);
                }
                try {
                    // 楽天の在庫更新のため、参数設定
                    model.setUpdateRequestExternalItem(items.toArray(new UpdateRequestExternalItem[0]));
                    // 楽天への在庫更新を実施
                    result = inventoryapiPort_portType.updateInventoryExternal(auth, model);
                    // 如果成功修改 则返回的数组为null
                    if ("N00-000".equals(result.getErrCode())) {
                        UpdateResponseExternalItem[] externalItem = result.getUpdateResponseExternalItem();
                        // 乐天的修改库存返回值
                        // 如果成功修改 则返回的数组为null
                        for (UpdateResponseExternalItem updateResponseExternalItem : externalItem) {
                            String productId = null;
                            Integer availableCnt = null;
                            // 商品管理番号（商品URL)
                            String key = updateResponseExternalItem.getItemUrl();
                            String err = updateResponseExternalItem.getItemErrCode();
                            String productInfo = itemMap.get(key);
                            // 商品情報を分割（分割符号:）
                            String[] str = productInfo.split(":");
                            if (str.length > 1) {
                                productId = str[0];
                                if (!Objects.isNull(str[1])) {
                                    availableCnt = Integer.valueOf(str[1]);
                                }
                            }
                            // 楽天の在庫連携が成功した場合、処理
                            if ("N00-000".equals(err)) {
                                logger.info("Rakuten在庫連携OK 店舗ID:" + clientId + " 商品ID:" + productId + " 在庫数:"
                                    + availableCnt);
                                // 連携した在庫数を在庫TBLの店舗数を反映
                                productDao.updateStockStroeCnt(clientId, productId, availableCnt, "Rakuten",
                                    DateUtils.getDate());
                            } else {
                                logger.warn("Rakuten在庫連携NG 店舗ID:" + clientId + " 商品ID:" + productId + " エラーコード:" + err);
                            }
                        }
                    } else {
                        logger.warn("Rakuten在庫連携NG 店舗ID:" + clientId + " 原因：楽天在庫連携");
                        // TODO エラー記録実装必要
                    }

                } catch (RemoteException re) {
                    logger.error("Rakuten在庫連携NG 店舗ID:" + clientId + " 原因：" + re.getMessage());
                    break;
                }
            }
        }
        logger.info("Rakuten在庫連携 終了");
    }

    /**
     * 受注管理表の登録処理
     *
     * @param orders 受注データ
     * @param initClientInfo 店舗情報
     * @return List<String>
     * @date 2020/12/21
     */
    private List<String> setTc200Json(JSONObject orders, InitClientInfoBean initClientInfo) {
        // 初期化
        List<String> errList = new ArrayList<>();
        Tc200_order tc200Order = new Tc200_order();
        /* 共通設定値を取得 */
        String clientId = initClientInfo.getClientId();
        // 取込履歴ID
        Integer historyId = initClientInfo.getHistoryId();
        // 出庫依頼連携(1:連携 0:連携なし）
        Integer status = initClientInfo.getShipmentStatus();
        // 識別番号
        String identification = initClientInfo.getIdentification();
        // 注文子番号
        Integer subNo = initClientInfo.getSubNo();
        // API番号
        Integer apiId = initClientInfo.getApiId();
        // 倉庫管理CD
        String warehouseCd = initClientInfo.getWarehouseCd();
        /* 共通設定値を取得 */

        // 受注番号(店舗template bs001-YYYYMMDDHHMM-00001)
        String purchaseOrderNo = CommonUtils.getOrderNo(subNo, identification);
        // 受注番号
        tc200Order.setPurchase_order_no(purchaseOrderNo);
        // 倉庫管理番号
        tc200Order.setWarehouse_cd(warehouseCd);
        // 顧客管理番号
        tc200Order.setClient_id(clientId);
        // 外部受注番号
        tc200Order.setOuter_order_no(orders.getString("orderNumber"));
        // 受注取込履歴ID
        tc200Order.setHistory_id(String.valueOf(historyId));
        // 个口数
        tc200Order.setBoxes(1);
        // 外部注文ステータス
        tc200Order.setOuter_order_status(0);
        // 必要字段写入（receiver_zip_code1）（Import_datetime）(Receiver_address1)(Receiver_todoufuken)(Receiver_family_name)
        JSONArray PackageModelList = orders.getJSONArray("PackageModelList");
        JSONObject PackageModel = PackageModelList.getJSONObject(0);
        try {
            // 注文日付 order_dateTime
            tc200Order.setOrder_datetime(CommonUtils.dealDateFormat(orders.getString("orderDatetime")));
            // 取込時間
            tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
            JSONObject SenderModel = PackageModel.getJSONObject("SenderModel");
            String msg = "";
            boolean bool = false;
            // 受注管理表の必須項目
            if (!StringTools.isNullOrEmpty(SenderModel)) {
                // 配送先郵便番号※必須項目
                if (!StringTools.isNullOrEmpty(SenderModel.getString("zipCode1"))) {
                    tc200Order.setReceiver_zip_code1(SenderModel.getString("zipCode1"));
                    tc200Order.setReceiver_zip_code2(SenderModel.getString("zipCode2"));
                } else {
                    msg += "郵便番号 ";
                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：{}の不備", clientId, purchaseOrderNo, msg);
                    bool = true;
                }
                // 配送先都道府県※必須項目
                if (!StringTools.isNullOrEmpty(SenderModel.getString("prefecture"))) {
                    tc200Order.setReceiver_todoufuken(SenderModel.getString("prefecture"));
                } else {
                    msg += "都道府県 ";
                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：{}の不備", clientId, purchaseOrderNo, msg);
                    bool = true;
                }
                // 配送先都住所1※必須項目
                if (!StringTools.isNullOrEmpty(SenderModel.getString("city"))) {
                    tc200Order.setReceiver_address1(SenderModel.getString("city"));
                } else {
                    msg += "住所番地 ";
                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：{}の不備", clientId, purchaseOrderNo, msg);
                    bool = true;
                }
                // 配送先名前※必須項目
                if (!StringTools.isNullOrEmpty(SenderModel.getString("familyName"))) {
                    tc200Order.setReceiver_family_name(SenderModel.getString("familyName"));
                } else {
                    msg += "配送先名前 ";
                    logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：{}の不備", clientId, purchaseOrderNo, msg);
                    bool = true;
                }
                if (bool) {
                    errList.add("配送先情報({})が不備のため、受注取込が行いませんでした。EC店舗側でご確認ください。");
                    return errList;
                }
                // 配送先住所
                tc200Order.setReceiver_address2(SenderModel.getString("subAddress"));
                // 配送先名
                tc200Order.setReceiver_first_name(SenderModel.getString("firstName"));
                // 配送先名前カナ1
                tc200Order.setReceiver_family_kana(SenderModel.getString("familyNameKana"));
                // 配送先名前カナ2
                tc200Order.setReceiver_first_kana(SenderModel.getString("firstNameKana"));
                // 配送先電話番号
                tc200Order.setReceiver_phone_number1(SenderModel.getString("phoneNumber1"));
                tc200Order.setReceiver_phone_number2(SenderModel.getString("phoneNumber2"));
                tc200Order.setReceiver_phone_number3(SenderModel.getString("phoneNumber3"));
                // 商品金額 + 送料 + ラッピング料 + 決済手数料 + 注文者負担金 - クーポン利用総額 - ポイント利用額
                tc200Order.setBilling_total(Integer.parseInt(orders.getString("requestPrice")));
            }
        } catch (Exception e) {
            errList.add("配送先情報を取込する際、何か原因によりエラーが発生しました。店舗側で配送先情報などをご確認ください。");
            return errList;
        }
        // 注文者情報 TODO
        JSONObject ordererModel = orders.getJSONObject("OrdererModel");
        if (!StringTools.isNullOrEmpty(ordererModel)) {
            tc200Order.setOrder_zip_code1(ordererModel.getString("zipCode1"));
            tc200Order.setOrder_zip_code2(ordererModel.getString("zipCode2"));
            tc200Order.setOrder_todoufuken(ordererModel.getString("prefecture"));
            tc200Order.setOrder_address1(ordererModel.getString("city"));
            tc200Order.setOrder_address2(ordererModel.getString("subAddress"));
            tc200Order.setOrder_family_name(ordererModel.getString("familyName"));
            tc200Order.setOrder_first_name(ordererModel.getString("firstName"));
            tc200Order.setOrder_family_kana(ordererModel.getString("familyNameKana"));
            tc200Order.setOrder_first_kana(ordererModel.getString("firstNameKana"));
            tc200Order.setOrder_phone_number1(ordererModel.getString("phoneNumber1"));
            tc200Order.setOrder_phone_number2(ordererModel.getString("phoneNumber2"));
            tc200Order.setOrder_phone_number3(ordererModel.getString("phoneNumber3"));
            tc200Order.setOrder_mail(ordererModel.getString("emailAddress"));
        }
        // クーポンの総額
        JSONObject pointModel = orders.getJSONObject("PointModel");
        // その他（other_feeクーポン利用総額 + ポイント利用額
        tc200Order.setOther_fee(orders.getInteger("couponAllTotalPrice") + pointModel.getInteger("usedPoint"));
        // 手数料 (決済手数料 + 注文者負担金 + 代引料)
        int handlingCharge = 0;
        // 楽天側の決済手数料 + 注文者負担金
        handlingCharge = orders.getInteger("paymentCharge") + orders.getInteger("additionalFeeOccurAmountToUser");
        // 楽天側の代引手数料
        Integer deliveryPrice = orders.getInteger("deliveryPrice");
        // 支払方法モデルの取得
        JSONObject SettlementModel = orders.getJSONObject("SettlementModel");
        Integer settlementMethodCode = SettlementModel.getInteger("settlementMethodCode");
        String settlementMethod = SettlementModel.getString("settlementMethod");
        if (settlementMethodCode == 2 || "代金引換".equals(settlementMethod)) {
            tc200Order.setCash_on_delivery_fee(orders.getInteger("requestPrice"));
            handlingCharge = handlingCharge + deliveryPrice;
        }
        // 手数料 (決済手数料 + 注文者負担金 + 代引手数料)
        tc200Order.setHandling_charge(handlingCharge);
        // 商品合計金額
        tc200Order.setProduct_price_excluding_tax(orders.getInteger("goodsPrice"));
        // 消費税合計
        tc200Order.setTax_total(orders.getInteger("goodsTax"));
        // 送料合計 postagePrice (※未確定の場合、-9999になります。)
        Integer postagePrice = orders.getInteger("postagePrice");
        if (postagePrice != null && postagePrice > 0) {
            tc200Order.setDelivery_total(postagePrice);
        }
        // ギフト配送希望フラグ(0: ギフト注文ではない1: ギフト注文である)
        tc200Order.setGift_wish(orders.getString("giftCheckFlag"));

        // 依頼マスタ(依頼主ID 及び明細書メッセージ) TODO
        Ms012_sponsor_master ms012spons = initClientInfo.getMs012sponsor();
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

        // 店舗設定情報を取得 TODO
        Map<String, String> ms007SettingTimeMap = initClientInfo.getMs007SettingTimeMap();
        Map<String, String> ms007SettingPaymentMap = initClientInfo.getMs007SettingPaymentMap();
        Map<String, String> ms007SettingDeliveryMethodMap = initClientInfo.getMs007SettingDeliveryMethodMap();
        // 元々の配送方法の値を取得
        JSONObject deliveryModel = orders.getJSONObject("DeliveryModel");
        String deliveryName = deliveryModel.getString("deliveryName");
        Ms004_delivery ms004Delivery = apiCommonUtils.getDeliveryMethod(deliveryName,
            initClientInfo.getDefaultDeliveryMethod(), ms007SettingDeliveryMethodMap);
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

        // @Add wang 2021/3/30 end
        // お届け日指定
        Date deliveryDate = DateUtils.stringToDate(orders.getString("deliveryDate"));
        tc200Order.setDelivery_date(deliveryDate);

        // お届け日時間帯 (配送連携設定管理表「mc007_setting」から取得)
        String deliveryTimeId = apiCommonUtils.getDeliveryTimeSlot(deliveryName, orders.getString("shippingTerm"),
            API.RAKUTEN.getName(), ms007SettingTimeMap);
        tc200Order.setDelivery_time_slot(deliveryTimeId);

        // 削除Flg(0:無、1:済 )
        tc200Order.setDel_flg(0);
        // 注文者の備考情報設定
        tc200Order.setMemo(orders.getString("remarks"));
        // 定期購入 1: 通常購入 4: 定期購入 5: 頒布会 6: 予約商品
        int orderType = orders.getInteger("orderType");
        if (4 == orderType) {
            // 申込番号
            String reserveNumber = orders.getString("reserveNumber");
            tc200Order.setBuy_id(reserveNumber);
            // お申込届け回数
            int reserveDeliveryCount = orders.getInteger("reserveDeliveryCount");
            tc200Order.setBuy_cnt(reserveDeliveryCount);
            // // 次回のお届け日付を取得するために、RMS APIに再度リクエスト必要
            // //認証キー
            // String authorization = orders.getString("com_authorization");
            // JSONObject jsonObject = new JSONObject();
            // jsonObject.put("orderNumberList", Collections.singletonList(reserveNumber));
            // // 楽天受注明細を取得
            // HttpEntity entity;
            // try {
            // entity = new StringEntity(jsonObject.toString());
            // } catch (Exception e) {
            // logger.error("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：楽天受注明細取得URLの生成失敗", clientId, purchaseOrderNo);
            // errList.add("楽天の受注明細を取得するため、楽天APIのURLを生成する際、エラーが発生しました。楽天APIアクセスキーの有効期限がご確認ください。");
            // return errList;
            // }
            // String responseStr = sendPost(GET_ORDER_URL, entity, "Authorization", authorization);
            // JSONObject res = JSONObject.parseObject(responseStr);
            // if (Objects.isNull(res)) {
            // logger.warn("Rakuten受注連携NG 店舗ID:{} 受注ID：{} 原因：楽天APIの認証エラー", clientId, purchaseOrderNo);
            // errList.add("楽天の受注明細を取得する際、楽天APIへの接続が失敗しました。楽天APIアクセスキーの有効期限等をご確認ください。");
            // return errList;
            // }
            // // お届け予定日取得
            // JSONArray orderModelList = res.getJSONArray("OrderModelList");
            // if (orderModelList != null && orderModelList.size() > 0) {
            // JSONObject orderModel = orderModelList.getJSONObject(0);
            // Date nextDeliveryDate = CommonUtil.stringToDate(orderModel.getString("deliveryDate"));
            // tc200Order.setNext_delivery_date(nextDeliveryDate);
            // }
        }

        // 支払方法の取得
        // JSONObject settlementModel = orders.getJSONObject("SettlementModel");
        // String settlementMethod = settlementModel.getString("settlementMethod");
        // ms007,ms014から支払方法の取得
        String paymentMethod =
            apiCommonUtils.getPaymentMethod(settlementMethod, API.RAKUTEN.getName(), ms007SettingPaymentMap);
        tc200Order.setPayment_method(paymentMethod);
        // 元々の支払方法を備考10も記載する
        tc200Order.setBikou10(ms007SettingPaymentMap.get(settlementMethod));
        // @Add wang 2021/3/30 start
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);

        JSONArray itemModelList = PackageModel.getJSONArray("ItemModelList");
        if (itemModelList != null && itemModelList.size() > 0) {
            try {
                // System.out.println(tc200Order.toString());
                // 受注管理表を書き込み
                orderDao.insertOrder(tc200Order);
            } catch (Exception e) {
                logger.error("Rakuten受注連携NG 店舗ID：{} 受注ID：{} 原因：受注管理TBLの登録失敗", clientId,
                    orders.getString("orderNumber"));
                logger.error(BaseException.print(e));
                errList.add("何か原因により受注管理表の新規登録が失敗しましたので、システム担当者にお問い合わせください。");
                return errList;
            }

            try {
                // 受注詳細を書き込み
                getTc201DetailData(itemModelList, clientId, purchaseOrderNo, apiId);
            } catch (Exception e) {
                logger.error("Rakuten受注連携NG 店舗ID：{} 受注ID：{} 原因：受注明細TBLの登録失敗", clientId,
                    orders.getString("orderNumber"));
                logger.error(BaseException.print(e));
                errList.add("何か原因により受注明細表の新規登録が失敗しましたので、システム担当者にお問い合わせください。");
                // 受注管理レコード削除
                orderDao.orderDelete(clientId, purchaseOrderNo);
                // 受注明細レコード削除
                orderDetailDao.orderDetailDelete(purchaseOrderNo);
                return errList;
            }

            // 自動出庫(1：出庫する 0:出庫しない)
            if (status == 1) {
                apiCommonUtils.processShipment(purchaseOrderNo, clientId);
            }
        } else {
            errList.add(ERR_CATCH_KEY);
            return errList;
        }
        // 戻り値
        return errList;
    }

    /**
     * 受注明細の新規登録
     *
     * @param itemModelList ： 受注情報
     * @param clientId ： 店舗ID
     * @param orderNo ： 受注番号
     * @date 2020/12/21
     */
    private void getTc201DetailData(JSONArray itemModelList, String clientId, String orderNo, Integer apiId) {
        Tc201_order_detail tc201OrderDetail;
        Integer subNo = 1;
        for (int i = 0; i < itemModelList.size(); i++) {
            tc201OrderDetail = new Tc201_order_detail();
            // 商品情報を取得
            JSONObject jsonObject = itemModelList.getJSONObject(i);
            // 在庫タイプ(0:在庫設定なし 1:通常在庫設定 2:項目選択肢在庫設定)
            String type = jsonObject.getString("inventoryType");
            // 項目・選択肢「横軸項目名：横軸選択肢 縦軸項目名：縦軸選択肢」
            String options = jsonObject.getString("selectedChoice");
            // 商品番号管理場合
            String code = jsonObject.getString("manageNumber");
            // 根据code查询该商品以前是否存在 ※店舗側が設定した商品コードを優先
            if (!StringTools.isNullOrEmpty(jsonObject.getString("itemNumber"))) {
                // 項目選択肢別在庫が指定された商品の場合、以下のルールで値が表示されます
                // 楽天の商品番号（店舗様が登録した番号）＋項目選択肢ID（横軸）＋項目選択肢ID（縦軸）
                code = jsonObject.getString("itemNumber");
            }
            // @Add wang 2021/3/30 商品コード横軸選択肢子の対応 Start
            // そ管理番号項目選択肢別在庫が指定された商品の場合、以下のルールで値が表示されます
            // 商品番号（店舗様が登録した番号）＋項目選択肢ID（横軸）＋項目選択肢ID（縦軸）
            // 商品名
            String name = jsonObject.getString("itemName");
            // 単価
            Integer price = jsonObject.getInteger("price");
            // 数量
            Integer units = jsonObject.getInteger("units");
            // 税率
            float taxRate = jsonObject.getFloat("taxRate");
            // 商品毎税込価格
            Integer priceTaxIncl = jsonObject.getInteger("priceTaxIncl");
            // 税込別(0: 税別 1: 税込み)
            if ("0".equals(jsonObject.getString("includeTaxFlag"))) {
                priceTaxIncl = (int) (price * (1 + taxRate));
            }
            // 商品ID(楽天のバリエーションIDとして保管)
            String renkeiPid = jsonObject.getString("itemId");
            // 軽減税率適用商品(0:10% 1:8%) ※楽天の税金(taxRate) version:3しか対応できない
            int isReducedTax = 0;
            if ("0.08".equals(jsonObject.getString("taxRate"))) {
                isReducedTax = 1;
            }
            // @Add wang 2021/3/30 商品コード横軸選択肢子の対応 End
            try {
                // **********************************************************
                // ********************* 共通パラメータ整理 *********************
                // **********************************************************
                ProductBean productBean = new ProductBean();
                productBean.setClientId(clientId);
                productBean.setCode(code);
                productBean.setName(name);
                productBean.setApiId(apiId);
                productBean.setPrice(String.valueOf(priceTaxIncl));
                productBean.setIsReducedTax(isReducedTax);
                productBean.setRenkeiPid(renkeiPid);
                productBean.setOptions(options);
                productBean.setType(type);
                // Mc100_productテーブルの既存商品をマッピング
                Mc100_product mc100Product = apiCommonUtils.fetchMc100Product(clientId, code, options);
                // 商品登録されていない場合、商品マスタに仮商品として新規登録
                if (Objects.isNull(mc100Product)) {
                    // 商品新規登録
                    mc100Product = apiCommonUtils.insertMc100Product(productBean, API.RAKUTEN.getName());
                    // 商品之前不存在 设定为仮登録
                    tc201OrderDetail.setProduct_kubun(9);
                }
                if (!Objects.isNull(mc100Product)) {
                    apiCommonUtils.insertMc106Product(clientId, apiId, renkeiPid, mc100Product, API.RAKUTEN.getName());
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
                logger.error("Rakuten受注連携NG 店舗ID:{} 受注明細ID:{} 原因:商品登録失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.E_11005);
            }
            // 商品名(受注の商品名をそのままに保管)
            tc201OrderDetail.setProduct_name(name);
            // 商品コード
            tc201OrderDetail.setProduct_code(code);
            // 商品オプション値を保管
            tc201OrderDetail.setProduct_option(options);
            // 単価
            tc201OrderDetail.setUnit_price(priceTaxIncl);
            // 個数
            tc201OrderDetail.setNumber(units);
            // 商品計
            int total_price = tc201OrderDetail.getUnit_price() * tc201OrderDetail.getNumber();
            tc201OrderDetail.setProduct_total_price(total_price);
            // 受注明細番号
            tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
            // 受注番号
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
                logger.error("Rakuten受注連携NG 店舗ID:{} 受注明細ID:{} 原因:受注明細TBLの登録失敗", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
            }
        }
    }

    /**
     * 楽天市場の伝票番号を取得
     * 
     * @param clientId 店舗ID
     * @date 2020/12/22
     */
    private void updateDeliveryInfo(String clientId, JSONArray orderData, List<Tw200_shipment> shipments,
        String authorization) {
        // 参数最外层
        JSONArray orderShippingModelList = new JSONArray();
        for (int i = 0; i < orderData.size(); i++) {
            JSONObject order = orderData.getJSONObject(i);
            String orderNumber = order.getString("orderNumber");
            JSONArray packageModelList = order.getJSONArray("PackageModelList");
            JSONObject packageModel = packageModelList.getJSONObject(0);
            JSONArray resShippingModelList = packageModel.getJSONArray("ShippingModelList");
            // 目的地型号列表
            JSONObject orderShippingModel = new JSONObject();
            JSONArray basketIdModelList = new JSONArray();
            // 送付先model
            JSONObject basketIdModel = new JSONObject();
            JSONArray shippingModelList = new JSONArray();

            // サンロジの出荷情報
            for (Tw200_shipment shipment : shipments) {
                // 送り状番号
                String delivery_tracking_nm = shipment.getDelivery_tracking_nm();
                // 受注番号(楽天受注番号)
                String orderNo = shipment.getOrder_no();
                // 配送会社指定
                Ms004_delivery ms400 = deliveryDao.getDeliveryById(shipment.getDelivery_carrier());
                String deliveryMethod = getDeliveryCompanyCode(ms400.getDelivery_nm());
                // 多个传票番号
                if (delivery_tracking_nm.contains(",")) {
                    String[] split = delivery_tracking_nm.split(",");
                    if (orderNo.equals(orderNumber)) {
                        int j = 0;
                        for (String tracking_nm : split) {
                            JSONObject shippingModel = new JSONObject();
                            JSONObject resShippingModel;
                            try {
                                // 如果返回的resShippingModelList数据与要发送的传票番号数量不一值，则会数组下标越界
                                resShippingModel = resShippingModelList.getJSONObject(j);

                                if (!StringTools.isNullOrEmpty(resShippingModel.getString("shippingDetailId"))) {
                                    // 运送详细资料编号
                                    shippingModel.put("shippingDetailId",
                                        resShippingModel.getInteger("shippingDetailId"));
                                }
                            } catch (Exception e) {
                                logger.error("Rakuten伝票連携NG 店舗ID：{} 受注ID{} 原因:楽天店舗[{}]不一致", clientId, orderNo,
                                    tracking_nm);
                                logger.error(BaseException.print(e));
                            }
                            // 发送日指定
                            shippingModel.put("shippingDate", CommonUtils.getNewDate(null));
                            // 传票番号指定（任意）
                            shippingModel.put("shippingNumber", tracking_nm);
                            // 配送会社を楽天の指定番号に変換
                            shippingModel.put("deliveryCompany", deliveryMethod);

                            shippingModelList.add(shippingModel);
                        }
                    }
                } else {
                    // 单个传票番号发货模型 （最大20件）
                    JSONObject shippingModel = new JSONObject();
                    // ·出荷情報の配送会社
                    if (orderNo.equals(orderNumber)) {
                        if (resShippingModelList.size() > 0) {
                            JSONObject resShippingModel = resShippingModelList.getJSONObject(0);
                            // 运送详细资料编号
                            shippingModel.put("shippingDetailId", resShippingModel.getInteger("shippingDetailId"));
                        }
                        // 传票番号指定（任意）
                        shippingModel.put("shippingNumber", delivery_tracking_nm);
                        // 发送日指定（null代表获取当前时间）
                        shippingModel.put("shippingDate", CommonUtils.getNewDate(null));
                        // 配送会社を楽天の指定番号に変換
                        shippingModel.put("deliveryCompany", deliveryMethod);
                        shippingModelList.add(shippingModel);
                    }
                }
            }
            basketIdModel.put("ShippingModelList", shippingModelList);
            basketIdModel.put("basketId", packageModel.getInteger("basketId"));
            basketIdModelList.add(basketIdModel);
            orderShippingModel.put("orderNumber", order.getString("orderNumber"));
            orderShippingModel.put("BasketidModelList", basketIdModelList);
            orderShippingModelList.add(orderShippingModel);
        }
        JSONObject arrays = new JSONObject();
        arrays.put("OrderShippingModelList", orderShippingModelList);
        // 楽天APIへの請求情報を設定
        try {
            // 楽天店舗への配送情報を送信
            HttpEntity entity = new StringEntity(arrays.toString());
            String response = sendPost(UPDATE_ORDER_SHIPPING_ASYNC_URL, entity, "Authorization", authorization, null);
            // 伝票番号へ返却した際、エラー発生した場合、更新をスキップ
            if (!StringTools.isNullOrEmpty(response)) {
                // TODO 更新が成功したメッセージがあるかどうか 確認必要
                for (Tw200_shipment shipment : shipments) {
                    // 修改返回传票的出库顶单的finishFlg
                    orderDao.updateFinishFlag(shipment.getShipment_plan_id());
                    logger.info("Rakuten伝票連携OK 店舗ID：{} 受注ID：{} 伝票番号：{}", clientId, shipment.getOrder_no(),
                        shipment.getDelivery_tracking_nm());
                }
            }
        } catch (Exception e) {
            logger.error("Rakuten伝票連携NG 店舗ID：{} 原因:楽天配送情報の連携失敗" + clientId);
            logger.error(BaseException.print(e));
        }
    }

    /**
     * 配送方法が配送方法CODEを変換
     * 
     * @param value 配送方法
     * @author wang
     * @date 2021/3/24
     */
    private String getDeliveryCompanyCode(String value) {
        String code = "1000";
        if (!StringTools.isNullOrEmpty(value)) {
            code = getDefaultDeliveryCompanyCode(value, "1");
        }
        return code;
    }

    /**
     * 楽天市場の配送方法情報の変換
     * 
     * @param value 変換の対象値
     * @param type 変換タイプ(0 : 配送方法 1 : 配送CODE)
     * @return 変換後の値
     * @author wang
     * @date 2021/3/24
     */
    String getDefaultDeliveryCompanyCode(String value, String type) {

        String str = "";
        // 初期化
        HashMap<String, String> deliveries = new HashMap<>();
        deliveries.put("1000", "その他");
        deliveries.put("1001", "ヤマト運輸");
        deliveries.put("1002", "佐川急便");
        deliveries.put("1003", "日本郵便");
        deliveries.put("1004", "西濃運輸");
        deliveries.put("1005", "セイノースーパーエクスプレス");
        deliveries.put("1006", "福山通運");
        deliveries.put("1007", "名鉄運輸");
        deliveries.put("1008", "トナミ運輸");
        deliveries.put("1009", "第一貨物");
        deliveries.put("1010", "新潟運輸");
        deliveries.put("1011", "中越運送");
        deliveries.put("1012", "岡山県貨物運送");
        deliveries.put("1013", "久留米運送");
        deliveries.put("1014", "山陽自動車運送");
        deliveries.put("1015", "日通トランスポート");
        deliveries.put("1016", "エコ配");
        deliveries.put("1017", "EMS");
        deliveries.put("1018", "DHL");
        deliveries.put("1019", "FedEx");
        deliveries.put("1020", "UPS");
        deliveries.put("1021", "日本通運");
        deliveries.put("1022", "TNT");
        deliveries.put("1023", "OCS");
        deliveries.put("1024", "USPS");
        deliveries.put("1025", "SFエクスプレス");
        deliveries.put("1026", "Aramex");
        deliveries.put("1027", "SGHグローバル・ジャパン");
        deliveries.put("1028", "Rakuten EXPRESS");
        // 楽天市場から値を変換
        for (Entry<String, String> entry : deliveries.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            // 配送方法コードにより、配送方法の値を取得
            if ("0".contentEquals(type) && value.equals(key)) {
                str = val;
            }
            // 配送方法の値により、配送方法のコードを取得を取得
            if ("1".contentEquals(type) && value.equals(val)) {
                str = key;
            }
        }
        // 戻り値
        return str;
    }

    /**
     * 検索オプションを設定
     * 
     * @param values 変更対象値
     * @param option オプション
     * @return Integer
     * @author wang
     * @date 2021/3/24
     */
    private Integer setOrderOptions(String values, String option) {
        // 戻り値
        int resInt = 0;
        String[] arr = {
            "", ""
        };

        String type = "3"; // 受注タイプ (Default値 3注文確定時間)
        String time = "24";// 検索時間単位(Default値 24時間)
        // 設定値がある場合
        if (!StringTools.isNullOrEmpty(values)) {
            arr = values.split(",");
        }
        // オプションにより、値をセット
        switch (option) {
            case "type":// 受注タイプ
                if (!StringTools.isNullOrEmpty(arr[0])
                    && StringTools.isInteger(arr[0])) {
                    if ("1 2 3".contains(arr[0])) {
                        type = arr[0];
                    }
                }
                // 指定ない場合、ディフォル値(3:注文確定時間)
                resInt = Integer.parseInt(type);
                break;
            case "time":// 時間単位
                if (!StringTools.isNullOrEmpty(arr[1])
                    && StringTools.isInteger(arr[1])) {
                    if ((Integer.parseInt(arr[1]) > 24)) {
                        time = arr[1];
                    }
                }
                // 指定ない場合、ディフォル値(24時間)
                resInt = Integer.parseInt(time);
                break;
        }

        // 戻り値
        return resInt;
    }

    private JSONArray setOrderOptions(String values) {
        // 戻り値
        JSONArray options = new JSONArray();
        // 検索条件：
        // 100: 注文確認待ち 200: 楽天処理中 300: 発送待ち 400: 変更確定待ち
        // 500: 発送済 600: 支払手続き中700: 支払手続き済 800: キャンセル確定待ち
        // 900: キャンセル確定
        if (!StringTools.isNullOrEmpty(values)) {
            String[] arr = values.split(",");
            for (String s : arr) {
                if (!StringTools.isNullOrEmpty(s) && StringTools.isInteger(s)) {
                    options.add(s);
                }
            }
        }
        // 設定値がない場合（300: 発送待ち)
        if (options.size() < 1) {
            options.add(300);
        }
        // 戻り値
        return options;
    }


    public String sendPost(String url, HttpEntity entity, String tokenName, String token, List<String> errList) {
        // POSTリクエストを作成
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(url);
        } catch (Exception e) {
            logger.error("HttpGetのインスタンス初期化失敗。");
            logger.error(BaseException.print(e));
            return null;
        }
        // ヘッダーとボディ設定
        if (!Objects.isNull(tokenName) && !Objects.isNull(token)) {
            httpPost.setHeader(tokenName, token);
        }
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        httpPost.setEntity(entity);
        // リクエスト送信
        try {
            return HttpClientUtils.execute(httpPost, errList);
        } catch (Exception e) {
            logger.error("HTTPリクエスト送信失敗, url={}, HTTP情報={}", url, httpPost);
            logger.error(BaseException.print(e));
        }
        return null;
    }

}
