package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.service.ApiErrorService;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.enums.SettingEnum;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderService;
import io.jsonwebtoken.lang.Collections;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class APICommonUtils
{

    private final static Logger logger = LoggerFactory.getLogger(APICommonUtils.class);
    // 配送情報マスタ
    private List<Ms006_delivery_time> ms006DeliveryTimes;
    // 支払情報マスタ
    private Map<String, Integer> ms014PaymentMap;
    @Resource
    private DeliveryDao deliveryDao;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderHistoryDao orderHistoryDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
    @Resource
    private OrderCancelDao orderCancelDao;
    @Resource
    private OrderErrorDao orderErrorDao;
    @Resource
    private ClientDao clientDao;
    @Resource
    private OrderDao orderDao;
    @Resource
    private SponsorDao sponsorDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private ApiErrorService apiErrorService;


    /**
     * API共通パーツ初期化
     */
    public synchronized void initialize() {
        if (Collections.isEmpty(this.ms006DeliveryTimes)) {
            try {
                this.ms006DeliveryTimes = deliveryDao.getDeliveryTimeAllList();
            } catch (Exception e) {
                logger.error("配送時間帯マスタ初期化失敗");
                logger.error(BaseException.print(e));
            }
        }
        if (Collections.isEmpty(this.ms014PaymentMap)) {
            List<Ms014_payment> ms014PaymentList = null;
            try {
                ms014PaymentList = deliveryDao.getDeliveryPaymentAllList();
            } catch (Exception e) {
                logger.error("支払方法マスタ初期化失敗");
                logger.error(BaseException.print(e));
            }
            if (!Collections.isEmpty(ms014PaymentList)) {
                this.ms014PaymentMap = ms014PaymentList.stream()
                    .collect(Collectors.toMap(Ms014_payment::getPayment_name, Ms014_payment::getPayment_id));
            }
        }
    }

    /**
     * 配送情報を取得
     *
     * @param deliveryMethod 配送方法（BASE取得）
     * @param defaultDeliveryMethod ディフォルト配送方法
     * @param ms007SettingMap ms007_settingレコード
     * @return 配送情報
     */
    public Ms004_delivery getDeliveryMethod(String deliveryMethod, String defaultDeliveryMethod,
        Map<String, String> ms007SettingMap) {
        // 初期化
        Ms004_delivery ms004Delivery = null;
        if (!Collections.isEmpty(ms007SettingMap)) {
            // 配送連携設定(ms700)から支払方法を取得した場合、処理
            if (ms007SettingMap.containsKey(deliveryMethod)) {
                ms004Delivery = deliveryDao.getDeliveryById(ms007SettingMap.get(deliveryMethod));
            }
        }
        if (Objects.isNull(ms004Delivery)) {
            // 配送マスタ(ms007_setting)から取得できない場合、店舗側のディフォル値を取得
            ms004Delivery = deliveryDao.getDeliveryById(defaultDeliveryMethod);
        }
        return ms004Delivery;
    }

    /**
     * 受注番号（SunLogi採番）を取得
     *
     * @param num 枝番
     * @param identification 識別キー
     * @return 受注番号
     */
    public static String getOrderNo(int num, String identification) {
        // オーダーコード（ディフォルトは【SL000】）
        String prefix = "SL000";
        if (!StringTools.isNullOrEmpty(identification)) {
            prefix = identification;
        }
        // 受注番号（SunLogi採番）
        return prefix + "-" + new SimpleDateFormat("yyyyMMddHHmmss-").format(new Date())
            + String.format("%05d", num);
    }

    /**
     * 配送時間帯を取得
     *
     * @param deliveryName 配送方法名称
     * @param deliverTimeName 配送時間帯名称
     * @param apiName API種別名称
     * @param ms007SettingMap 店舗側設定マスタ
     * @return 配送時間帯（SunLogi定義）
     */
    public String getDeliveryTimeSlot(String deliveryName, String deliverTimeName, String apiName,
        Map<String, String> ms007SettingMap) {
        if (StringTools.isNullOrEmpty(deliverTimeName)) {
            return null;
        }
        // 初期化
        String ms006DeliveryTimeId = "";

        // 删除配送时间带前后的全角和半角空格
        deliverTimeName = deliverTimeName.replaceAll("^　|　$", "").trim();
        // 時間文字を変換
        String ms006DeliveryTimeName = CommonUtils.timeToString(deliverTimeName);

        // logger.error("1回目:配送業者【{}】配送時間帯変換前【{}】変換後【{}】",deliveryName,deliverTimeName,ms006DeliveryTimeName);
        // 店舗側で設定している配送時間帯(ms007)を検索
        if (!Collections.isEmpty(ms007SettingMap)) {
            // 配送連携設定(ms700)から配送時間帯を取得した場合、処理
            if (ms007SettingMap.containsKey(deliverTimeName)) {
                ms006DeliveryTimeName = ms007SettingMap.get(deliverTimeName);
            }
        }
        // logger.error("2回目:配送業者【{}】配送時間帯変換前【{}】変換後【{}】",deliveryName,deliverTimeName,ms006DeliveryTimeName);
        // 店舗側で設定している配送時間帯(ms007)を検索できない場合、 配送時間帯マスタ(ms006)から検索
        if (StringTools.isNullOrEmpty(ms006DeliveryTimeName)) {
            ms006DeliveryTimeName = deliverTimeName;
        }
        // 配送時間帯設定(ms006)から配送時間帯を取得した場合、処理
        for (Ms006_delivery_time ms006 : this.ms006DeliveryTimes) {
            if (ms006.getDelivery_nm().equals(deliveryName) &&
                ms006.getDelivery_time_name().equals(ms006DeliveryTimeName)) {
                ms006DeliveryTimeId = ms006.getDelivery_time_id().toString();
                break;
            }
        }
        // logger.error("3回目:配送業者【{}】配送時間帯変換前【{}】変換後【{}】",deliveryName,deliverTimeName,ms006DeliveryTimeName);
        // 配送時間帯マスタ(ms006)から検索できない場合、新規登録
        if (StringTools.isNullOrEmpty(ms006DeliveryTimeId)) {
            // 支払方法を追加
            Ms006_delivery_time ms006 = new Ms006_delivery_time();
            ms006.setKubu(99999);
            ms006.setDelivery_nm(deliveryName);
            ms006.setDelivery_time_name(ms006DeliveryTimeName);
            ms006.setInfo(apiName + "受注連携");
            ms006.setIns_usr(apiName);
            ms006.setDelivery_time_csv(ms006DeliveryTimeName);
            // 新期登録
            try {
                deliveryDao.insertMs006DeliveryTime(ms006);
            } catch (Exception e) {
                logger.error("Ms006配送時間帯テーブルに挿入する際に異常が発生しました。");
                logger.error(BaseException.print(e));
                return null;
            }
            List<Ms006_delivery_time> ms006DeliveryTimes;
            try {
                ms006DeliveryTimes = deliveryDao.getDeliveryTimeName(ms006DeliveryTimeName, deliveryName);
            } catch (Exception e) {
                logger.error("Ms006配送時間帯テーブルからレコードを取得する際に異常が発生しました。");
                logger.error(BaseException.print(e));
                return null;
            }
            if (Collections.isEmpty(ms006DeliveryTimes)) {
                logger.error("配送時間帯マスタ情報取得失敗しました。");
                return null;
            }
            ms006DeliveryTimeId = ms006DeliveryTimes.get(0).getDelivery_time_id().toString();
            this.ms006DeliveryTimes.add(ms006DeliveryTimes.get(0));
        }
        return ms006DeliveryTimeId;
    }

    /**
     * 出庫依頼
     *
     * @param purchaseOrderNo 受注番号
     * @param clientId 店舗ID
     */
    public void processShipment(String purchaseOrderNo, String clientId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        array.add(purchaseOrderNo);
        jsonObject.put("list", array);
        try {
            orderService.createShipments(jsonObject, clientId, null);
        } catch (Exception e) {
            logger.error("受注ID【{}】出庫依頼失敗しました。", purchaseOrderNo);
            logger.error(BaseException.print(e));
        }
    }

    /**
     * 受注履歴IDを記録
     *
     * @param clientId 店舗ID
     * @param apiName API名称
     * @param historyId 履歴ID
     * @param successCnt 成功件数
     * @param failureCnt 失敗件数
     */
    public void processOrderHistory(String clientId, String apiName, Integer historyId, Integer successCnt,
        Integer failureCnt) {
        // 受注履歴Beanに情報を格納
        Tc202_order_history order_history = new Tc202_order_history();
        // 受注履歴Beanに情報を格納
        // 受注履歴ID
        order_history.setHistory_id(historyId);
        // 記録時間
        Date today = DateUtils.getDate();
        order_history.setImport_datetime(today);
        // 店舗ID
        order_history.setClient_id(clientId);
        // 取込件数(成功件数 + 失敗件数)
        order_history.setTotal_cnt(successCnt + failureCnt);
        // 成功件数
        order_history.setSuccess_cnt(successCnt);
        // 失敗件数
        order_history.setFailure_cnt(failureCnt);
        // 備考(店舗テンプレート名を記録)
        order_history.setBiko01(apiName + "受注API連携");
        try {
            orderHistoryDao.insertOrderHistory(order_history);
        } catch (Exception e) {
            logger.error("店舗ID【{}】成功件数【{}】失敗件数【{}】受注履歴の登録失敗", clientId, successCnt, failureCnt);
            logger.error(BaseException.print(e));
        }
    }

    /**
     * 支払方法を取得
     *
     * @param paymentName 支払方法名称
     * @param templateName API種別名称
     * @param ms007SettingMap 店舗側設定リスト
     * @return 支払方法
     */
    public String getPaymentMethod(String paymentName, String templateName, Map<String, String> ms007SettingMap) {
        // 初期化
        String paymentMethod = "";
        // 支払方法が空の場合、処理スキップ
        if (StringTools.isNullOrEmpty(paymentName)) {
            logger.info("支払方法なし　処理スキップ");
            return paymentMethod;
        }
        if (ms007SettingMap.size() > 0) {
            // 配送連携設定(ms700)から支払方法を取得した場合、処理
            if (ms007SettingMap.containsKey(paymentName)) {
                paymentMethod = ms007SettingMap.get(paymentName);
            }
        }

        // 配送連携設定(ms700)から支払方法IDを取得できない場合、支払方法マスタから検索
        if (StringTools.isNullOrEmpty(paymentMethod)) {
            if (this.ms014PaymentMap.size() > 0 && this.ms014PaymentMap.containsKey(paymentName)) {
                // 支払方法設定(ms014)から支払方法を取得した場合、処理続く
                paymentMethod = this.ms014PaymentMap.get(paymentName).toString();
            } else {
                // 支払方法を追加
                Ms014_payment ms014 = new Ms014_payment();
                ms014.setKubu(99999);
                ms014.setPayment_name(paymentName);
                ms014.setInfo(templateName + "受注自動連携");
                ms014.setIns_usr(templateName);
                ms014.setPayment_csv(paymentName);
                try {
                    // 支払方法を追加
                    deliveryDao.insertMs014Payment(ms014);
                    // 新規追加の支払方法をms014PaymentMapに追加
                    List<Ms014_payment> ms014PaymentList;
                    ms014PaymentList = deliveryDao.getDeliveryPaymentAllList();
                    if (!Collections.isEmpty(ms014PaymentList)) {
                        this.ms014PaymentMap = ms014PaymentList.stream()
                            .collect(Collectors.toMap(Ms014_payment::getPayment_name, Ms014_payment::getPayment_id));
                        // 支払方法設定(ms014)から支払方法を取得した場合、処理続く
                        if (this.ms014PaymentMap.containsKey(paymentName)) {
                            paymentMethod = this.ms014PaymentMap.get(paymentName).toString();
                        }
                    }
                } catch (Exception e) {
                    logger.error("支払方法【{}】支払方法の登録失敗", paymentName);
                    logger.error(BaseException.print(e));
                }
            }
        }
        return paymentMethod;
    }

    /**
     * 商品新期登録
     *
     * @param clientId 店舗ID
     * @param name 商品名
     * @param code 商品コード
     * @param price 商品単価
     * @param isTax 税区分
     * @return Mc100商品レコード
     */
    protected Mc100_product createProduct(String clientId, String name, String code, String price, Integer isTax) {
        Mc100_product mc100Product = null;
        // 初期化
        JSONObject object = new JSONObject();
        JSONObject items = new JSONObject();
        JSONArray array = new JSONArray();

        // 商品マスタにセットする
        object.put("client_id", clientId);
        items.put("name", name);
        items.put("code", code);
        items.put("bundled_flg", 0);
        items.put("is_reduced_tax", isTax);
        items.put("price", price);
        items.put("tags", new JSONArray());
        items.put("img", new JSONArray());
        array.add(items);
        object.put("items", array);
        try {
            // 商品IDを取得(最大数)
            String productId = productService.createProductId(clientId);
            // 商品新規登録
            productService.insertProductMain(object, null);
            // 登録の商品IDを取得
            mc100Product = productDao.getProductById(productId, clientId);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
        }
        return mc100Product;
    }

    /**
     * 最大受注取込履歴ID
     *
     * @return 最大読み込むID
     */
    public Integer getMaxHistoryId() {
        String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
        int lastHistoryId = 0;
        if (!StringTools.isNullOrEmpty(lastOrderHistoryNo)) {
            lastHistoryId = Integer.parseInt(lastOrderHistoryNo);
        }
        return lastHistoryId + 1;
    }

    /**
     * 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
     *
     * @param data Tc203クライアント情報
     * @return クライアント情報
     */
    public InitClientInfoBean initClientCommon(Tc203_order_client data) {
        // 初期化
        InitClientInfoBean init = new InitClientInfoBean();
        // 店舗ID
        String clientId = data.getClient_id();
        if (!StringTools.isNullOrEmpty(clientId)) {
            // 店舗情報取得
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);

            // Ms007_settingの情報を取得 1:配送方法 2:配送时间带 3:支付方法
            List<Ms007_setting> ms007SettingList = deliveryDao.getConvertedDataAll(clientId, null);
            Map<String, String> ms007SettingTimeMap = new HashMap<>();
            Map<String, String> ms007SettingPaymentMap = new HashMap<>();
            Map<String, String> ms007SettingDeliveryMethodMap = new HashMap<>();
            // Ms007_settingテーブルのmapping_valueとconverted_idをマップにまとめる
            for (Ms007_setting ms007 : ms007SettingList) {
                Integer kubun = ms007.getKubun();
                if (StringTools.isNullOrEmpty(kubun)) {
                    continue;
                }
                switch (SettingEnum.get(kubun)) {
                    case PAYMENT_METHOD:
                        // 支付方法
                        ms007SettingPaymentMap.put(ms007.getMapping_value(), ms007.getConverted_id());
                        break;
                    case DELIVERY_TIME_ZONE:
                        // 配送时间带
                        ms007SettingTimeMap.put(ms007.getMapping_value(), ms007.getConverted_value());
                        break;
                    case DELIVERY_METHOD:
                        // 配送方法
                        ms007SettingDeliveryMethodMap.put(ms007.getMapping_value(), ms007.getConverted_id());
                        break;
                    default:
                        break;
                }
            }

            // 店舗にひもｄId
            List<String> warehouseIdListByClientId = orderDao.getWarehouseIdListByClientId(clientId);
            String warehouseCd = warehouseIdListByClientId.get(0);
            // 注文者ID(依頼主ID)
            List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(clientId, false, data.getSponsor_id());
            if (sponsorList.size() <= 0) {
                sponsorList = sponsorDao.getSponsorList(clientId, true, null);
            }
            Ms012_sponsor_master ms012sponsor = sponsorList.get(0);

            // 受注取込履歴ID取得(最大値)
            Integer historyId = getMaxHistoryId();

            // 必要情報をinitClientInfoBeanにセットする
            init.setClientId(clientId);
            init.setApiName(data.getApi_name());
            init.setHistoryId(historyId);
            init.setWarehouseCd(warehouseCd);
            init.setDefaultDeliveryMethod(clientInfo.getDelivery_method());
            init.setShipmentStatus(data.getShipment_status());
            init.setIdentification(data.getIdentification());
            init.setSponsorId(data.getSponsor_id());
            init.setApiId(data.getId());
            init.setMs007SettingTimeMap(ms007SettingTimeMap);
            init.setMs007SettingPaymentMap(ms007SettingPaymentMap);
            init.setMs007SettingDeliveryMethodMap(ms007SettingDeliveryMethodMap);
            init.setMs012sponsor(ms012sponsor);
            init.setTc203order(data);
        }
        return init;
    }

    /**
     * Mc100_productを取得する
     *
     * @param clientId 店舗ID
     * @param code 商品コード
     * @param options 商品オプション
     * @return Mc100商品レコード
     */
    public Mc100_product fetchMc100Product(String clientId, String code, String options) {
        Mc100_product mc100Product = null;
        List<Mc110_product_options> mc110ProductOptions = null;
        if (StringTools.isNullOrEmpty(code)) {
            logger.error("店舗ID【{}】 fetchMc100Productメソッドの引数で商品コードが空です。", clientId);
            return null;
        }
        String mc110Code = null;
        // Mc110Productレコード取得（検索条件：商品コード、店舗ID）
        try {
            mc110ProductOptions = productDao.getMc110ProductByCode(code, clientId);
        } catch (Exception e) {
            logger.error("店舗ID【{}】 mc110ProductOptionsレコード取得する際に、異常が発生しました。", clientId);
            logger.error(BaseException.print(e));
        }
        // 取得したmc110ProductOptionsの件数を判断して、複数件があった場合、【オプションoptions】でマッピングする
        if (!Collections.isEmpty(mc110ProductOptions)) {

            boolean hasOptions = false;


            // 商品コード＋オプション値 を Mapキーとして登録
            Map<String, List<Mc110_product_options>> collect =
                mc110ProductOptions.stream()
                    .collect(Collectors.groupingBy(
                        x -> x.getCode() + (StringTools.isNullOrEmpty(x.getOptions()) ? "" : x.getOptions())));

            // 商品コード＋オプション値が異なる場合、店舗側のオプション値と比較
            if (collect.size() > 1) {
                if (!StringTools.isNullOrEmpty(options)) {
                    //
                    for (Mc110_product_options mc110 : mc110ProductOptions) {
                        // 商品対応表の商品コードが異なる場合、オプション値の比較必要
                        if (options.equals(mc110.getOptions())) {
                            mc110Code = mc110.getCode();
                            hasOptions = true;
                            break;
                        }
                    }
                }
            }

            // 如果 以商品code+option 为key值 map的size为1 或者 mc110集合为 1 的时候 默认取第一个值
            if (collect.size() == 1 || mc110ProductOptions.size() == 1) {
                // 初期商品コードを取得
                mc110Code = mc110ProductOptions.get(0).getCode();
                hasOptions = true;
            }

            if (!hasOptions) {
                logger.error("店舗ID【{}】商品コード【{}】原因：商品対応表の登録不正", clientId, code);
                return null;
            }
        }

        // mc110Codeが見つからない場合、商品コードをmc110Codeに設定する
        if (!StringTools.isNullOrEmpty(mc110Code)) {
            code = mc110Code;
        }
        List<Mc100_product> mc100ProductList;
        try {
            // 商品コードで商品マスタから商品情報取得
            mc100ProductList = productDao.getProductInfoListByCode(code, clientId);
            if (!Collections.isEmpty(mc100ProductList)) {
                mc100Product = mc100ProductList.get(0);
            }
        } catch (Exception e) {
            logger.warn("店舗ID【{}】商品CD【{}】原因：商品重複登録", clientId, code);
            logger.error(BaseException.print(e));
            return null;
        }
        if (!Objects.isNull(mc100Product)) {
            return mc100Product;
        }
        return null;
    }

    /**
     * Mc100商品レコードを挿入する
     *
     * @param insProductBean 商品項目
     * @param apiName API名称
     * @return Mc100商品登録された商品レコード
     */
    public Mc100_product insertMc100Product(ProductBean insProductBean, String apiName) {
        // 初期化
        JSONObject productObj = new JSONObject();
        // 商品ID
        String productId;
        Mc100_product mc100Product = null;
        // クライアントID
        String clientId = insProductBean.getClientId();
        // 商品コード
        String code = insProductBean.getCode();
        // 商品IDを取得(最大数)
        productId = productService.createProductId(clientId);
        productObj.put("client_id", clientId);
        // 初期化
        JSONObject item = new JSONObject();
        JSONArray items = new JSONArray();
        // 商品マスタにセットする
        item.put("name", insProductBean.getName());
        item.put("code", code);
        item.put("bundled_flg", 0);
        item.put("barcode", insProductBean.getBarcode());
        item.put("is_reduced_tax", insProductBean.getIsReducedTax());
        item.put("price", insProductBean.getPrice());
        item.put("bikou", apiName + "自動連携");
        item.put("tags", new JSONArray());
        item.put("img", new JSONArray());
        // 因为该商品之前不存在 所以为仮登録 需要 kubun = 9 show_flg = 1
        item.put("kubun", 9);
        item.put("show_flg", 0);
        items.add(item);
        if (Collections.isEmpty(items)) {
            logger.error("{}店舗ID【{}】　登録するつもり商品が存在してない。", apiName, clientId);
            return null;
        }
        productObj.put("items", items);
        try {
            // 商品新規登録
            productService.insertProductMain(productObj, null);
        } catch (Exception e) {
            logger.error("{}店舗ID【{}】　商品新規登録する際に、異常が発生しました。", apiName, clientId);
            logger.error(BaseException.print(e));
        }
        try {
            mc100Product = productDao.getProductById(productId, clientId);
        } catch (Exception e) {
            logger.error("{}店舗ID【{}】　新規登録した商品を取得する際に、異常が発生しました。", apiName, clientId);
            logger.error(BaseException.print(e));
        }
        return mc100Product;
    }

    /**
     * 受注自動連携失敗した場合、異常メッセージを記録する
     *
     * @param clientId 店舗ID
     * @param historyId 履歴ID
     * @param outerOrderNo 外部連携番号
     * @param message メッセージ
     * @param apiName API名
     */
    public void insertTc207OrderError(String clientId, Integer historyId, String outerOrderNo, String message,
        String apiName) {
        Tc207_order_error orderError = new Tc207_order_error();
        Integer cnt = orderErrorDao.getOrderError(clientId, outerOrderNo);
        // 過去受注エラーメッセージがある場合、スキップ
        orderError.setHistory_id(historyId);
        orderError.setClient_id(clientId);
        orderError.setError_msg(message);
        orderError.setOuter_order_no(outerOrderNo);
        orderError.setBiko01(apiName + "受注自動連携");
        orderError.setStatus(0);// 処理状況(0:未確認 1:確認済)
        if (cnt == 0) {
            orderError.setIns_usr(apiName);
            orderError.setIns_date(new Timestamp(System.currentTimeMillis()));
            // 受注エラーメッセージ
            orderErrorDao.insertOrderError(orderError);
        } else {
            orderError.setUpd_usr(apiName);
            orderError.setUpd_date(new Timestamp(System.currentTimeMillis()));
            orderErrorDao.updateOrderError(orderError);
        }
    }

    /**
     * 受注キャンセルされた場合、受注データを記録する
     *
     * @param clientId 店铺Id
     * @param outerOrderNo 受注番号（BASE採番）
     * @param apiName API名称
     */
    public void insertTc208OrderCancel(String clientId, String outerOrderNo, String apiName) {
        Tc208_order_cancel orderCancel = new Tc208_order_cancel();
        Tw200_shipment tw200Ships;
        try {
            tw200Ships = orderCancelDao.getShipmentList(clientId, outerOrderNo);
        } catch (Exception e) {
            logger.error("{}店舗【{}】の出庫情報を取得する際に、異常が発生しました。受注番号【{}】", apiName, clientId, outerOrderNo);
            logger.error(BaseException.print(e));
            return;
        }
        // 初期化
        String shipmentPlanId = apiName + "ORDER-CANCEL";
        String user = apiName + "受注自動連携";
        // 処理状況(0:未確認 1:確認済)
        int status = 1;
        if (!Objects.isNull(tw200Ships)) {
            shipmentPlanId = tw200Ships.getShipment_plan_id();
            status = 0;
        }
        orderCancel.setClient_id(clientId);
        orderCancel.setOuter_order_no(outerOrderNo);
        orderCancel.setShipment_plan_id(shipmentPlanId);
        orderCancel.setStatus(status);
        orderCancel.setBikou1(apiName);
        // 取込前の受注キャンセルも記録(cancel)
        int count;
        try {
            count = orderCancelDao.getOrderCancel(clientId, outerOrderNo, shipmentPlanId);
        } catch (Exception e) {
            logger.error("{}店舗【{}】のキャンセル情報を取得する際に、異常が発生しました。受注番号【{}】", apiName, clientId, outerOrderNo);
            logger.error(BaseException.print(e));
            return;
        }
        if (count == 0) {
            orderCancel.setIns_usr(user);
            orderCancel.setIns_date(new Timestamp(System.currentTimeMillis()));
            // 受注キャンセルを記録
            try {
                orderCancelDao.insertOrderCancel(orderCancel);
            } catch (Exception e) {
                logger.error("{}店舗【{}】のキャンセル情報を挿入する際に、異常が発生しました。受注番号【{}】", apiName, clientId, outerOrderNo);
                logger.error(BaseException.print(e));
            }
        } else {
            orderCancel.setUpd_usr(user);
            orderCancel.setUpd_date(new Timestamp(System.currentTimeMillis()));
            // 受注キャンセルを更新
            try {
                orderCancelDao.updateOrderCancel(orderCancel);
            } catch (Exception e) {
                logger.error("{}店舗【{}】のキャンセル情報を更新する際に、異常が発生しました。受注番号【{}】", apiName, clientId, outerOrderNo);
                logger.error(BaseException.print(e));
            }
        }
    }

    /**
     * Mc106_renkei_productを挿入する
     *
     * @param clientId 店舗ID
     * @param apiId API番号
     * @param renkeiProductId 連携商品ID
     * @param mc100Product Mc100商品レコード
     * @param apiName API名称
     */
    public void insertMc106Product(String clientId, Integer apiId, String renkeiProductId, Mc100_product mc100Product,
        String apiName) {
        Mc106_produce_renkei mc106Product = new Mc106_produce_renkei();
        // 連続API番号
        mc106Product.setApi_id(apiId);
        // 店舗ID
        mc106Product.setClient_id(clientId);
        // 商品ID
        mc106Product.setProduct_id(mc100Product.getProduct_id());
        // 商品ID(外部SKU商品コード)
        mc106Product.setRenkei_product_id(renkeiProductId);
        // 検証ID(外部在庫ロケーションID) TODO デフォルト値(default)として設定
        mc106Product.setVariant_id(mc100Product.getCode());
        // 外部連携商品情報登録
        int rows = productDao.getRenkeiProduct(mc106Product);
        if (rows == 0) {
            mc106Product.setIns_usr(apiName + "自動連携");
            mc106Product.setIns_date(new Timestamp(System.currentTimeMillis()));
            productDao.insertRenkeiProduct(mc106Product);
        }
    }

    /**
     * 受注情報削除
     *
     * @param clientId 店舗ID
     * @param purchaseOrderNo 受注番号（外部EC採番）
     */
    public void deleteTc200Order(String clientId, String purchaseOrderNo) {
        orderDao.orderDelete(clientId, purchaseOrderNo);
    }

    /**
     * 受注情報削除
     *
     * @param purchaseOrderNo 受注番号（SunLOGI採番）
     */
    public void deleteTc201OrderDetails(String purchaseOrderNo) {
        orderDetailDao.orderDetailDelete(purchaseOrderNo);
    }

    /**
     * API実行エラー回数記録
     *
     * @param errOrderClients 異常発生店舗リスト
     */
    public void insertApiErrorCount(List<Tc203_order_client> errOrderClients) {
        if (!Collections.isEmpty(errOrderClients))
            apiErrorService.insertApiErrorCount(errOrderClients);
    }

    /**
     * 都道府県と市区町村分離処理
     *
     * @param address 都道府県と市区町村文字列
     * @return 住所マップ
     */
    public Map<String, String> addressSplice(String address) {
        // 住所マップ初期化
        Map<String, String> addressMap = new HashMap<>();

        // 都道府県が【神奈川県、鹿児島県、和歌山県】の場合、下記処理を行う
        if ("神奈川県".equals(address.substring(0, 4)) || "鹿児島県".equals(address.substring(0, 4))
            || "和歌山県".equals(address.substring(0, 4))) {
            // 注文者住所都道府県
            addressMap.put("todoufuken", address.substring(0, 4));
            // 注文者住所市区町村
            addressMap.put("address1", address.substring(4));
        } else {
            // 注文者住所都道府県
            addressMap.put("todoufuken", address.substring(0, 3));
            // 注文者住所市区町村
            addressMap.put("address1", address.substring(3));
        }

        return addressMap;
    }
}
