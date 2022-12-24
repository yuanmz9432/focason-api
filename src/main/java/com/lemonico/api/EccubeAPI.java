package com.lemonico.api;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.*;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.OrderApiService;
import com.lemonico.store.service.OrderService;
import com.lemonico.store.service.ShipmentsService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Eccube店舗から情報取得(JSON形式)
 *
 * @className: EccubeUtil
 * @date 2020/9/9 9:27
 **/
@Component
@EnableScheduling
public class EccubeAPI
{

    private final static Logger logger = LoggerFactory.getLogger(EccubeAPI.class);

    @Resource
    private OrderApiService apiService;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductService productService;
    @Resource
    private ProductSettingService productSettingService;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderHistoryDao orderHistoryDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private SponsorDao sponsorDao;
    @Resource
    private DeliveryDao deliveryDao;
    @Resource
    private OrderService orderService;
    @Resource
    private ShipmentsService shipmentsService;
    @Resource
    private APICommonUtils apiCommonUtils;
    @Resource
    private MailTools mailTools;
    @Resource
    private ClientDao clientDao;

    /**
     * Eccube受注自動取込 (10分ごと自動起動 6,16,26,36,46,56)
     *
     * @author HZM
     * @date 2020/9/9
     */
    // @Scheduled(cron = "0 6/10 * * * ?")
    public void fetchEccubeOrders() {
        logger.info("Eccube受注連携 開始");
        // Eccube店舗連携情報取得
        List<Tc203_order_client> allData = apiService.getAllDataOrder(API.ECCUBE.getName());
        // Eccubeに関するAPI情報が存在しない場合、処理中止
        if (io.jsonwebtoken.lang.Collections.isEmpty(allData)) {
            logger.info("Eccube受注連携 店舗情報：0件");
            logger.info("Eccube受注連携 終了");
            return;
        }

        Hashtable<String, List<String>> errHashtable = new Hashtable<>();

        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        apiCommonUtils.initialize();
        for (Tc203_order_client allDatum : allData) {

            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();

            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(allDatum);
            // 受注取込履歴ID取得
            Integer historyId = apiCommonUtils.getMaxHistoryId();
            // 受注取得URLの初期化
            StringBuilder url = new StringBuilder();
            // 店舗情報取得
            String clientId = allDatum.getClient_id();
            // アプリ情報取得
            String apiUrl = allDatum.getClient_url();
            Integer apiId = allDatum.getId();
            String user = allDatum.getApi_key();
            String passwd = allDatum.getPassword();
            String sponsorId = allDatum.getSponsor_id();
            // 処理条件1 (入金待ちの状態)
            String option1 = setOrderOptions(allDatum.getBikou1(), "1");
            // 処理条件2 (取込対象の状態 入金済み ※通常設定なし)
            String option2 = setOrderOptions(allDatum.getBikou2(), "2");
            // 処理条件3 (注文取消しの状態)
            String option3 = setOrderOptions(allDatum.getBikou3(), "3");
            // 受注管理
            JSONArray orders = null;

            // 店舗の設定情報取得(明細書金額印字・明細同梱設定)
            Mc105_product_setting productSetting = productSettingService.getProductSetting(clientId, null);
            try {
                // 受注情報取得
                url.append("https://").append(apiUrl).append("getOrder.php")
                    .append("?user=").append(URLEncoder.encode(user, StandardCharsets.UTF_8.name()))
                    .append("&passwd=").append(URLEncoder.encode(passwd, StandardCharsets.UTF_8.name()));

                logger.info("eccube受注連携 店舗ID:" + clientId);

                // Eccubeから受注情報として取込
                JSONObject jsonObject = HttpUtils.sendHttpsGet(url.toString(), null, null, errList);
                // Eccubeから受注情報を取得できない、処理せず
                if (jsonObject != null) {
                    String error = jsonObject.getString("error");
                    if (!Strings.isNullOrEmpty(error)) {
                        logger.error("eccube受注連携NG 店舗ID:" + clientId + " API名:" + allDatum.getApi_name() + " 原因："
                            + error);
                        errList.add(error);
                        errOrderClients.add(allDatum);
                        continue;
                    }
                    orders = jsonObject.getJSONArray("orders");
                }
            } catch (Exception e) {
                logger.error("eccube受注連携NG 店舗ID:" + clientId + " API名:" + allDatum.getApi_name());
                logger.error(BaseException.print(e));
                errList.add(BaseException.print(e));
                errOrderClients.add(allDatum);
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
            if (Objects.equal(orders, null)) {
                logger.warn("eccube受注連携NG 店舗ID:" + clientId + " API名:" + allDatum.getApi_name());
            } else {
                // 受注件数取得
                total = orders.size();

                // 受注処理
                for (int j = 0; j < orders.size(); j++) {
                    // エラーメッセージ
                    String msg = "";
                    // 初期化
                    Tc200_order tc200Order = new Tc200_order();
                    // 自動出庫フラグ
                    Integer status = allDatum.getShipment_status();
                    // 受注番号「店舗API識別番号(5文字)-YYYYMMDDHHMM-00001」
                    String orderNo = APICommonUtils.getOrderNo(subno, allDatum.getIdentification());
                    tc200Order.setPurchase_order_no(orderNo);
                    // 倉庫ID
                    List<String> warehouseIdListByClientId = orderDao.getWarehouseIdListByClientId(clientId);
                    tc200Order.setWarehouse_cd(warehouseIdListByClientId.get(0));
                    // 店舗ID
                    tc200Order.setClient_id(clientId);

                    // 外部受注番号
                    JSONObject orderT = orders.getJSONObject(j);
                    String outerOrderNo = orderT.getString("id");
                    // 決済方法
                    String payment_name = orderT.getString("payment_method");
                    // 注文状況 ※検索条件にはcanceledを含む必要
                    String state = orderT.getString("state");
                    // order_type 0:入金待ち 1:入金済み
                    int orderType = 1;
                    // 処理１入金待ち
                    if (!StringTools.isNullOrEmpty(option1) && option1.contains(state)
                        && !"代金引換".contains(payment_name)) {
                        orderType = 0;
                    }
                    // 処理2 入金済み
                    if (!StringTools.isNullOrEmpty(option2) && option2.contains(state)) {
                        List<Tc200_order> tc200 = orderDao.getOrderType(outerOrderNo, clientId);
                        if (tc200 != null && tc200.size() > 0) {
                            Integer type = tc200.get(0).getOrder_type();
                            if (type != null && type == 0) {
                                orderService.upOrderStatus(clientId, outerOrderNo, warehouseIdListByClientId.get(0),
                                    tc200.get(0).getShipment_plan_id(), null);
                                logger.info("eccube受注連携 店舗ID:" + clientId + " 受注ID:" + outerOrderNo
                                    + " 入金待ちを入金済みに変更");
                                continue;
                            }
                        }
                    }
                    tc200Order.setOrder_type(orderType);

                    // キャンセル場合、受注キャンセルを記録処理 ※検索条件にはcanceledを含む必要 TODO 改善必要
                    if (!StringTools.isNullOrEmpty(option3) && option3.contains(state)) {
                        // キャンセル以外場合、処理 ※検索条件にはcanceledを含む必要
                        logger.warn(
                            "eccube受注連携NG" + " 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + " 原因:取込前の受注キャンセル");
                        total--;
                        apiCommonUtils.insertTc208OrderCancel(clientId, outerOrderNo, API.ECCUBE.getName());
                        continue;
                    }
                    // 外部受注番号がない場合、新規受注として取込しない
                    Integer outerOrderCnt = orderDao.getOuterOrderNo(outerOrderNo, clientId);
                    // 過去受注に関して、処理をスキップ
                    if (!(outerOrderCnt == 0)) {
                        logger.warn("eccube受注連携 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + " 原因:過去受注取込済");
                        total--;
                        continue;
                    }
                    tc200Order.setOuter_order_no(outerOrderNo);
                    // 受注取込履歴ID
                    tc200Order.setHistory_id(Integer.toString(historyId));
                    // 外部注文ステータス(0:出庫未依頼 1:出庫依頼済)
                    tc200Order.setOuter_order_status(0);
                    // 注文日時
                    String orderTime = orderT.getString("completed_at");
                    tc200Order.setOrder_datetime(DateUtils.getNowTime(orderTime));

                    // 支払方法(連携設定ms007表に参考し、変換されたIDを取得)
                    if (!StringTools.isNullOrEmpty(payment_name)) {
                        String convertedId = deliveryDao.getConverted_id(clientId, 3, payment_name);
                        if (!StringTools.isNullOrEmpty(convertedId)) {
                            tc200Order.setPayment_method(convertedId);
                        }
                        // 元々の支払方法を備考10も記載する
                        tc200Order.setBikou10(payment_name);
                    }

                    // 代引き料金 TODO
                    if ("代金引換".equals(payment_name)) {
                        tc200Order.setCash_on_delivery_fee(
                            Double.valueOf(orderT.getString("total")).intValue());
                    }
                    // 商品税抜金額
                    tc200Order.setProduct_price_excluding_tax(
                        Double.valueOf(orderT.getString("subtotal")).intValue());
                    // 消費税合計
                    tc200Order.setTax_total(Double.valueOf(orderT.getString("tax")).intValue());
                    // その他金額 TODO discount_with_point 割引(ポイント含む)
                    // TODO misc_fee その他
                    tc200Order.setOther_fee(Double.valueOf(orderT.getString("discount")).intValue());
                    // 送料合計
                    tc200Order.setDelivery_total(
                        Double.valueOf(orderT.getString("delivery_fee_total")).intValue());
                    // 手数料
                    tc200Order.setHandling_charge(Double.valueOf(orderT.getString("charge")).intValue());
                    // 合計請求金額
                    tc200Order.setBilling_total(Double.valueOf(orderT.getString("total")).intValue());
                    // ギフト(0:無 1:注文単位)
                    tc200Order.setGift_wish("0");

                    // 店舗側のメモ取得(※備考欄に記載する) 備考欄(通信欄)
                    String remark = orderT.getString("note");
                    if (!Strings.isNullOrEmpty(remark)) {
                        // 備考フラグ1を設定する
                        tc200Order.setBikou_flg(1);
                        tc200Order.setMemo(remark);
                    }
                    /* 注文者情報 */
                    // 注文者郵便番号1
                    String billingZip = orderT.getString("postal_code");
                    List<String> zipList = CommonUtils.checkZipToList(billingZip);
                    // 配送先郵便番号
                    if (!StringTools.isNullOrEmpty(zipList) && zipList.size() > 0) {
                        // 注文者郵便番号1
                        tc200Order.setOrder_zip_code1(zipList.get(0));
                        // 注文者郵便番号2
                        tc200Order.setOrder_zip_code2(zipList.get(1));
                    }
                    // 注文者住所都道府県
                    tc200Order.setOrder_todoufuken(orderT.getString("prefecture_name"));
                    // 注文者住所郡市区
                    tc200Order.setOrder_address1(orderT.getString("addr01"));
                    // 注文者詳細住所
                    tc200Order.setOrder_address2(orderT.getString("addr02"));
                    // 注文者姓
                    tc200Order.setOrder_family_name(orderT.getString("name01"));
                    // 注文者名
                    tc200Order.setOrder_first_name(orderT.getString("name02"));
                    // 注文者姓カナ
                    tc200Order.setOrder_family_kana(orderT.getString("kana01"));
                    // 注文者名カナ
                    tc200Order.setOrder_first_kana(orderT.getString("kana02"));
                    // 注文者電話番号1
                    String phone = orderT.getString("phone_number");
                    if (!StringTools.isNullOrEmpty(phone)) {
                        List<String> telList = CommonUtils.checkPhoneToList(phone);
                        if (telList != null && telList.size() > 0) {
                            tc200Order.setOrder_phone_number1(telList.get(0));
                            tc200Order.setOrder_phone_number2(telList.get(1));
                            tc200Order.setOrder_phone_number3(telList.get(2));
                        }
                    }
                    // 注文者メールアドレス
                    tc200Order.setOrder_mail(orderT.getString("email"));

                    /* 配送先情報 */

                    // 配送先郵便番号1 **必須項目
                    String shippingZip = orderT.getString("post");
                    zipList = CommonUtils.checkZipToList(shippingZip);
                    // 配送先郵便番号
                    if (!io.jsonwebtoken.lang.Collections.isEmpty(zipList)) {
                        // 配送先郵便番号1 **必須項目
                        tc200Order.setReceiver_zip_code1(zipList.get(0));
                        // 配送先郵便番号2
                        tc200Order.setReceiver_zip_code2(zipList.get(1));
                    } else {
                        logger.warn("eccube受注(" + orderNo + "):eccubeデータ不正(郵便)");
                        tc200Order.setReceiver_zip_code1("000");
                        tc200Order.setReceiver_zip_code2("0000");
                    }

                    // 配送先住所都道府県 **必須項目
                    tc200Order.setReceiver_todoufuken(orderT.getString("prefecture_name"));
                    // 配送先住所郡市区
                    tc200Order.setReceiver_address1(orderT.getString("addr1"));
                    // 配送先詳細住所
                    tc200Order.setReceiver_address2(orderT.getString("addr2"));
                    // 配送先姓 **必須項目
                    tc200Order.setReceiver_family_name(orderT.getString("name1"));
                    // 配送先名
                    tc200Order.setReceiver_first_name(orderT.getString("name2"));
                    // 配送先姓カナ
                    tc200Order.setReceiver_family_kana(orderT.getString("kana1"));
                    // 配送先名カナ
                    tc200Order.setReceiver_first_kana(orderT.getString("kana2"));
                    // 配送先電話番号1
                    String phone2 = orderT.getString("phone_nu");
                    if (!StringTools.isNullOrEmpty(phone2)) {
                        List<String> telList = CommonUtils.checkPhoneToList(phone2);
                        if (telList != null && telList.size() > 0) {
                            tc200Order.setReceiver_phone_number1(telList.get(0));
                            tc200Order.setReceiver_phone_number2(telList.get(1));
                            tc200Order.setReceiver_phone_number3(telList.get(2));
                        }
                    }
                    // 取込日時
                    tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
                    // 配送情報取得 TODO(配送連携設定管理表「mc007_setting」から取得)
                    String title = orderT.getString("delivery_name");
                    // 配送方法取得
                    Ms004_delivery ms004Delivery = apiCommonUtils.getDeliveryMethod(title,
                        initClientInfo.getDefaultDeliveryMethod(), initClientInfo.getMs007SettingDeliveryMethodMap());
                    String deliveryNm = "";
                    if (!java.util.Objects.isNull(ms004Delivery)) {
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
                    // @Add wang 2021/3/30 start
                    // 元の配送方法を備考9に保管する @Add wang 2021/3/30
                    tc200Order.setBikou9(title);
                    // @Add wang 2021/3/30 end
                    // 配送時間帯 (配送連携設定管理表「mc007_setting」から取得)
                    String deliveryTimeId = "";
                    if (!StringTools.isNullOrEmpty(deliveryNm)) {
                        deliveryTimeId =
                            apiCommonUtils.getDeliveryTimeSlot(deliveryNm, orderT.getString("delivery_time"),
                                API.ECCUBE.getName(), initClientInfo.getMs007SettingTimeMap());
                    }

                    tc200Order.setDelivery_time_slot(deliveryTimeId);
                    // 希望届け日 TODO
                    String deliveryDate = orderT.getString("delivery_date");
                    tc200Order.setDelivery_date(DateUtils.stringToDate(deliveryDate));

                    // 注文者会社名
                    tc200Order.setOrder_company(orderT.getString("company_name"));
                    // 配送先会社名
                    tc200Order.setReceiver_company(orderT.getString("company_name2"));
                    // ディフォルトは依頼主
                    tc200Order.setOrder_flag(0);
                    // 注文者ID(依頼主ID 及び明細書メッセージ)
                    List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(clientId, false, sponsorId);
                    if (io.jsonwebtoken.lang.Collections.isEmpty(sponsorList)) {
                        sponsorList = sponsorDao.getSponsorList(clientId, true, null);
                    }
                    Ms012_sponsor_master sponsorDefaultInfo = sponsorList.get(0);
                    if (sponsorDefaultInfo != null) {
                        tc200Order.setSponsor_id(sponsorDefaultInfo.getSponsor_id());
                        tc200Order.setDetail_message(sponsorDefaultInfo.getDetail_message());
                        // 明細同梱設定(1:同梱する 0:同梱しない)
                        if ("1".equals(String.valueOf(sponsorDefaultInfo.getDelivery_note_type()))) {
                            tc200Order.setDetail_bundled("同梱する");
                        } else {
                            tc200Order.setDetail_bundled("同梱しない");
                        }
                        // 明細書金額印字
                        if ("1".equals(String.valueOf(sponsorDefaultInfo.getPrice_on_delivery_note()))) {
                            tc200Order.setDetail_price_print("1");
                        }
                    }
                    // 削除Flg(0削除しない)
                    tc200Order.setDel_flg(0);
                    // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
                    tc200Order.setForm(2);
                    // NTMの場合、formを法人にする
                    if (!Strings.isNullOrEmpty(orderT.getString("form"))) {
                        tc200Order.setForm(1);
                    }

                    boolean isNtmFlg = apiService.hasNtmFunction(warehouseIdListByClientId.get(0), clientId, "4");
                    if (isNtmFlg) {
                        String checkMsg = "";
                        String checkPhoneMsg = apiService.checkPhoneNumber(tc200Order);
                        String checkYubinMsg = apiService.checkYubinLegal(tc200Order);
                        if (!Strings.isNullOrEmpty(checkPhoneMsg)) {
                            checkMsg += checkPhoneMsg;
                            if (!Strings.isNullOrEmpty(checkYubinMsg)) {
                                checkMsg += "、";
                                checkMsg += checkYubinMsg;
                            }
                            checkMsg += " 検証に失敗しました。";
                        } else if (!Strings.isNullOrEmpty(checkYubinMsg)) {
                            checkMsg += checkYubinMsg;
                            checkMsg += " 検証に失敗しました。";
                        }

                        if (!Strings.isNullOrEmpty(checkMsg)) {
                            tc200Order.setBikou7(checkMsg);
                        }
                    }

                    try {
                        // 受注管理登録
                        if (StringTools.isNullOrEmpty(msg)) {
                            orderDao.insertOrder(tc200Order);
                            subno++;
                            successCnt++;
                        } else {
                            logger.warn(
                                "eccube受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc200)");
                        }
                    } catch (Exception e) {
                        subno++;
                        failureCnt++;
                        msg = "eccube受注連携のバッチ処理により、受注管理の登録が失敗しました。" + "詳細はシステム担当者にお問い合わせください。";
                        apiCommonUtils.insertTc207OrderError(clientId, historyId, outerOrderNo, msg,
                            API.ECCUBE.getName());
                        logger.error("eccube受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc200)");
                        logger.error(BaseException.print(e));
                        errList.add(" 受注ID:" + outerOrderNo + "eccube受注連携のバッチ処理により、受注管理の登録が失敗しました。");
                        errOrderClients.add(allDatum);
                        continue;
                    }

                    try {
                        if (StringTools.isNullOrEmpty(msg)) {
                            // 受注明細登録
                            insertOrderDetail(orderNo, clientId, apiId, orderT, productSetting);
                            logger.info("eccube受注連携OK 店舗ID:" + clientId + " 受注ID:" + outerOrderNo);
                        } else {
                            logger.warn(
                                "eccube受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc201)");
                        }
                    } catch (Exception e) {
                        subno++;
                        failureCnt++;

                        msg = "eccube受注の連携処理により、受注明細(tc201)の登録が失敗しました。" + "詳細はシステム担当者にお問い合わせください。";
                        apiCommonUtils.insertTc207OrderError(clientId, historyId, outerOrderNo, msg,
                            API.ECCUBE.getName());
                        logger.error("eccube受注連携NG 店舗ID:" + clientId + " 受注ID:" + outerOrderNo + "(tc201)");
                        logger.error(BaseException.print(e));
                        errList.add(" 受注ID:" + outerOrderNo + "受注明細(tc201)の登録が失敗しました。");
                        errOrderClients.add(allDatum);
                        continue;
                    }
                    // 自動出庫(1:自動出庫 0:出庫しない)
                    if (status == 1) {
                        apiCommonUtils.processShipment(orderNo, clientId);
                    }
                }

                // 取り込まれていない場合、取込履歴を記録しない
                if (successCnt > 0) {
                    logger.info("eccube受注連携 店舗ID:" + clientId + " 成功件数:" + successCnt + " 失敗件数:" + failureCnt);
                    // 生成した受注履歴beanに情報を格納
                    Tc202_order_history order_history = new Tc202_order_history();
                    order_history.setHistory_id(historyId);
                    Date nowTime = DateUtils.getDate();
                    order_history.setImport_datetime(nowTime);
                    order_history.setClient_id(clientId);
                    order_history.setTotal_cnt(total);// 取込件数
                    order_history.setSuccess_cnt(successCnt);// 成功件数
                    order_history.setFailure_cnt(failureCnt);// 失敗件数
                    order_history.setBiko01("eccube受注連携");
                    orderHistoryDao.insertOrderHistory(order_history);
                }
            }

            if (!errList.isEmpty()) {
                String key = clientId + "_APi名：" + allDatum.getApi_name();
                errHashtable.put(key, errList);
            }
        }
        logger.info("eccube受注連携 終了");
        // mailTools.sendErrorMessage(errHashtable, API.ECCUBE.getName());

        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * 送り状番号の自動連携 (毎日18時)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // @Scheduled(cron = "0 5/15 * * * ?")
    public void sendEccubeTrackingNm() {
        logger.info("eccube伝票連携 開始");
        // Eccube情報取得(送状連携設定有効のみ)
        List<Tc203_order_client> allData = apiService.getAllDataDelivery(API.ECCUBE.getName());
        // Eccubeに関するAPI情報が存在しない場合、処理中止
        if (io.jsonwebtoken.lang.Collections.isEmpty(allData)) {
            logger.info("eccube伝票連携 店舗情報：0件");
            logger.info("eccube伝票連携 終了");
            return;
        }
        int successCnt = 0;
        int errorCnt = 0;
        for (Tc203_order_client allDatum : allData) {
            // 店舗API情報
            String clientId = allDatum.getClient_id();
            String apiUrl = allDatum.getClient_url();
            String user = allDatum.getApi_key();
            String passwd = allDatum.getPassword();

            // 出庫情報取得(条件:出荷完了 伝票番号有り 在庫未連携)
            List<Tw200_shipment> getShipmentEccubeList = shipmentsService.getUntrackedShipments(clientId,
                "eccube");
            StringBuilder url1 = new StringBuilder();
            StringBuilder param = new StringBuilder();

            param.append("{\"user\":\"").append(user).append("\",\"passwd\":\"").append(passwd)
                .append("\",\"orders\":[");
            url1.append("https://").append(apiUrl).append("sendTrackNm.php");
            // 店舗設定の全て情報
            for (int j = 0; j < getShipmentEccubeList.size(); j++) {
                // 初期化
                String order_no = getShipmentEccubeList.get(j).getOrder_no();
                String tracking_nms = getShipmentEccubeList.get(j).getDelivery_tracking_nm();
                String delivery_nm = getShipmentEccubeList.get(j).getDelivery_method_name();
                if (j > 0) {
                    param.append(",");
                }
                param.append("{\"order_id\":\"").append(order_no)
                    .append("\",\"delivery_name\":\"").append(delivery_nm)
                    .append("\",\"tracking_number\":\"").append(tracking_nms).append("\"}");
            }
            param.append("]}");
            try {
                String res = HttpUtils.sendJsonPost(url1.toString(), param, "");
                // 更新が成功した場合、API連携を連携済に変更
                if (!StringTools.isNullOrEmpty(res) && "success".equals(res.trim())) {
                    for (Tw200_shipment tw200_shipment : getShipmentEccubeList) {
                        shipmentsService.setShipmentFinishFlg(allDatum.getClient_id(),
                            tw200_shipment.getWarehouse_cd(),
                            tw200_shipment.getShipment_plan_id());
                        logger.info("eccube伝票連携OK" + " 店舗ID:" + clientId + " 受注ID:"
                            + tw200_shipment.getOrder_no() + " 伝票ID:"
                            + tw200_shipment.getDelivery_tracking_nm());
                        successCnt++;
                    }
                } else {
                    logger.warn("eccube伝票連携NG 店舗ID:" + clientId + " API:" + apiUrl + "(返却値無)");
                }
            } catch (Exception e) {
                errorCnt++;
                logger.error("eccube伝票連携NG 店舗ID:" + clientId + " API:" + apiUrl + "");
                logger.error(BaseException.print(e));
            }
        }
        logger.info("Eccube伝票連携 終了--連携件数(OK:" + successCnt + " NG:" + errorCnt + ")");
    }

    /**
     * 在庫数自動連携 (毎日23時に自動実行)
     *
     * @author HZM
     * @date 2020/12/25
     */
    // @Scheduled(cron = "0 0 23 * * ?")
    public void setEccubeProductCnt() {
        logger.info("eccube在庫連携 開始");
        List<Tc203_order_client> allData = apiService.getAllDataStock(API.ECCUBE.getName());
        // Eccubeに関するAPI情報が存在しない場合、処理中止
        if (io.jsonwebtoken.lang.Collections.isEmpty(allData)) {
            logger.info("eccube在庫連携 店舗情報：0件");
            logger.info("eccube在庫連携 終了");
            return;
        }
        int successCnt = 0;
        for (Tc203_order_client allDatum : allData) {
            // API連携情報
            String clientId = allDatum.getClient_id();
            String apiUrl = allDatum.getClient_url();
            Integer apiId = allDatum.getId();
            String user = allDatum.getApi_key();
            String passwd = allDatum.getPassword();
            // API連携取得
            List<Mc106_produce_renkei> allProductData = productService.getAllProductDataById(clientId, apiId);

            StringBuilder url = new StringBuilder();
            StringBuilder param = new StringBuilder();

            if (allProductData != null && allProductData.size() > 0) {
                // アクセスURL
                url.append("https://").append(apiUrl).append("setProductCnt.php");

                // パラメータ
                param.append("{\"user\":\"").append(user).append("\",\"passwd\":\"").append(passwd)
                    .append("\",\"stock_items\":[");
                try {
                    int flag = 0;
                    for (Mc106_produce_renkei allProductDatum : allProductData) {
                        // 在庫数
                        String product_id = allProductDatum.getProduct_id();
                        // 商品ID(eccube商品ID)
                        String renkeiPid = allProductDatum.getRenkei_product_id();
                        // 商品CD(eccube商品CD)
                        String variantId = allProductDatum.getVariant_id();
                        // WMS実在庫数ー予備在庫数ー依頼中数ー個人宅出荷予想数
                        Integer productCnt = productService.getNtmProductCnt(clientId, product_id);
                        // 有効在庫数が0以下場合、0とする
                        // TODO 处理逻辑错误，需要再确认
                        if (productCnt != null && productCnt > 0) {
                            // 配送可在庫数を取得 (※available_cntのみ取得可、他の項目を取得できない ※要注意)
                            if (flag > 0) {
                                param.append(",");
                            }
                            param.append("{\"product_id\":\"").append(product_id).append("\",\"product_code\":\"")
                                .append(variantId).append("\",\"renkei_product_id\":\"").append(renkeiPid)
                                .append("\",\"stock\":").append(productCnt).append("}");
                            flag++;
                        }
                    }
                    param.append("]}");
                    // リクエスト
                    String res = HttpUtils.sendJsonPost(url.toString(), param, "");
                    if (!StringTools.isNullOrEmpty(res)) {
                        JSONObject obj = JSON.parseObject(res);
                        String error = obj.getString("error");
                        if (!StringTools.isNullOrEmpty(error)) {
                            logger.error("eccube在庫連携NG 店舗ID:" + clientId + " 原因:" + error);
                            continue;
                        }
                        // 在庫連携が正常の場合、送り状連携状況を「1:連携済」に変更
                        JSONArray success = obj.getJSONArray("success");
                        // 返却値がNull場合、処理スキップ
                        if (success != null && success.size() > 0) {
                            for (int m = 0; m < success.size(); m++) {
                                String sku = success.getJSONObject(m).getString("product_code");
                                String stock = success.getJSONObject(m).getString("stock");
                                String productId = success.getJSONObject(m).getString("product_id");
                                // 連携した在庫数を在庫TBLの店舗数を反映
                                productDao.updateStockStroeCnt(clientId, productId, Integer.valueOf(stock),
                                    "eccube", DateUtils.getDate());
                                logger.info("eccube在庫連携OK 店舗ID:" + clientId + " 商品CD:" + sku + " 在庫数:" + stock);
                                // 総計件数
                                successCnt++;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("eccube在庫連携NG 店舗ID:" + clientId);
                    logger.error(BaseException.print(e));
                }
            }
        }
        logger.info("eccube在庫連携 終了--処理件数:" + successCnt);
    }

    /**
     * 受注明細情報を登録
     *
     * @param orderNo 受注番号
     * @param clientId 店舗ID
     * @param apiId API番号
     * @param order 受注情報
     * @param productSetting 商品設定マスタ
     * @author wang
     * @date 2021/1/16
     */
    private void insertOrderDetail(String orderNo, String clientId, Integer apiId, JSONObject order,
        Mc105_product_setting productSetting) {
        /* 受注明細 登録 */
        Tc201_order_detail tc201OrderDetail;
        try {
            // tc201受注明細
            JSONArray orderItems = order.getJSONArray("products");
            int subNo;
            // 連続API管理ID
            // Integer apiId = allData.get(i).getId();
            for (int m = 0; m < orderItems.size(); m++) {
                tc201OrderDetail = new Tc201_order_detail();
                // 受注明細番号
                subNo = m + 1;
                // JSONから商品情報取得
                JSONObject product = orderItems.getJSONObject(m);
                // 連携商品ID
                String renkeiProductId = product.getString("product_id");
                // 商品コード(SKUコード)
                String code = product.getString("product_code");
                // 商品名
                String name = product.getString("product_name");
                // 商品単価
                String price = product.getString("price");
                // 税区分(1:税抜 0:税込)
                int taxFlag = 0;
                if ("1".equals(product.getString("tax_flag"))) {
                    taxFlag = 1;
                }
                // 商品オプション TODO 固定的に空に設定するのが問題ないでしょうか。
                String options = "";
                // 軽減税率フラグ
                int isReducedTax = 0;// TODO 固定に0に設定するのが問題ないですか。
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
                if (java.util.Objects.isNull(mc100Product)) {
                    // 商品新規登録
                    mc100Product = apiCommonUtils.insertMc100Product(productBean, API.ECCUBE.getName());
                    // 商品之前不存在 设定为仮登録
                    tc201OrderDetail.setProduct_kubun(9);
                }
                // 外部商品連携管理TBL
                if (!java.util.Objects.isNull(mc100Product)) {
                    apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                        API.ECCUBE.getName());
                }
                if (java.util.Objects.nonNull(mc100Product)) {
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
                    logger.error("ECCUBE受注連携NG 店舗ID:{} 受注ID:{} 原因:商品IDの取得失敗", clientId,
                        tc201OrderDetail.getOrder_detail_no());
                    throw new BaseException(ErrorCode.E_11005);
                }
                // 商品単価(販売価格) ※注意list_price通常価格 sales_price販売価格 商品価格price
                tc201OrderDetail.setUnit_price(Double.valueOf(price).intValue());
                // 商品数量
                tc201OrderDetail.setNumber(Integer.valueOf(product.getString("quantity")));
                // 商品小計(販売価格*商品数量)
                if (!StringTools.isNullOrEmpty(tc201OrderDetail.getNumber())
                    && !StringTools.isNullOrEmpty(tc201OrderDetail.getUnit_price())) {
                    int total_price;
                    int unitPrice = tc201OrderDetail.getUnit_price();
                    int number = tc201OrderDetail.getNumber();
                    int accordion = productSetting.getAccordion();
                    if (taxFlag == 1) {
                        // 税抜の場合、税率10%として計算 (税込= 単価(税抜)-単価(税抜)*税率)
                        total_price = CommonUtils.getTaxIncluded(unitPrice, 10, accordion) * number;
                    } else {
                        total_price = unitPrice * number;
                    }
                    tc201OrderDetail.setProduct_total_price(total_price);
                }
                // 税率(10%固定)
                tc201OrderDetail.setIs_reduced_tax(isReducedTax);
                tc201OrderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
                tc201OrderDetail.setPurchase_order_no(orderNo);
                tc201OrderDetail.setDel_flg(0);
                tc201OrderDetail.setTax_flag(taxFlag);
                try {
                    // 受注明細登録
                    orderDetailDao.insertOrderDetail(tc201OrderDetail);
                } catch (Exception e) {
                    logger.error(BaseException.print(e));
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
        }
    }

    /**
     * 検索オプションを設定
     *
     * @param values 変更対象値
     * @param type 変更タイプ
     * @return 検索条件
     * @author wang
     * @date 2021/3/24
     */
    private String setOrderOptions(String values, String type) {
        // 戻り値
        String option = "";
        // オプションにより、値をセット
        switch (type) {
            case "1":// 入金待ち
                // 指定ない場合、ディフォル値(新規受付)
                if (!StringTools.isNullOrEmpty(values)) {
                    option = values;
                } else {
                    option = "新規受付";
                }
                break;
            case "2":// 入金済み (※通常出荷依頼)
                // 指定ない場合、ディフォル値
                if (!StringTools.isNullOrEmpty(values)) {
                    option = values;
                } else {
                    option = "入金済み";
                }
                break;
            case "3":// キャンセルの場合
                // 指定ない場合、ディフォル値(注文取消し)
                if (!StringTools.isNullOrEmpty(values)) {
                    option = values;
                } else {
                    option = "注文取消し";
                }
                break;
        }
        return option;
    }

    /**
     * 商品自動連携 (1時間ごと自動起動)
     *
     * @author HZM
     * @date 2021/10/15
     */
    // @Scheduled(cron = "30 10 * * * ?")
    public void setEccubeProduct() {
        logger.info("eccube商品連携 開始");
        List<Tc203_order_client> allApiData = apiService.getAllData("eccube");
        int cnts = 0;
        if (allApiData != null && allApiData.size() > 0) {
            for (int j = 0; j < allApiData.size(); j++) {
                // API連携情報
                ArrayList<String> errList = new ArrayList<>();
                String clientId = allApiData.get(j).getClient_id();
                String apiUrl = allApiData.get(j).getClient_url();
                Integer apiId = allApiData.get(j).getId();
                String user = allApiData.get(j).getApi_key();
                String passwd = allApiData.get(j).getPassword();
                String bikou3 = allApiData.get(j).getBikou3();
                if (StringTools.isNullOrEmpty(bikou3) || !"商品連携".equals(bikou3)) {
                    continue;
                }
                // API連携取得
                StringBuilder url = new StringBuilder();

                try {
                    // アクセスURL
                    url.append("https://").append(apiUrl).append("getProduct.php")
                        .append("?user=").append(user).append("&passwd=").append(passwd);
                    // 商品詳細を取得
                    JSONObject jsonObject = HttpUtils.sendHttpsGet(url.toString(), null, null, errList);
                    if (StringTools.isNullOrEmpty(jsonObject)) {
                        logger.warn("eccube商品連携 URL:" + url.toString());
                        continue;
                    } else {
                        JSONArray products = jsonObject.getJSONArray("products");
                        for (int i = 0; i < products.size(); i++) {
                            // 商品名
                            String name = products.getJSONObject(i).getString("name");
                            // 連携商品ID
                            String renkei_product_id = products.getJSONObject(i).getString("id");
                            // 商品コード
                            String code = products.getJSONObject(i).getString("product_code");
                            // 商品単価
                            String price = products.getJSONObject(i).getString("price");
                            // 同梱物
                            String bundledFlg = "0";
                            // 商品ステータス（1:公開 2:非公開）
                            Integer eccube_show_flg = products.getJSONObject(i).getInteger("status");
                            List<String> inList = insertProductDate(apiId, clientId, renkei_product_id, code, name,
                                price, bundledFlg, eccube_show_flg);
                            if (!StringTools.isNullOrEmpty(inList) && inList.size() > 0) {
                                logger.info("eccube商品連携(新規:1更新:0)[" + inList.get(4) + "]店舗ID:" + clientId + " 商品ID:"
                                    + inList.get(0) + " 商品CD:" + code + " 外部ID:" + renkei_product_id + "(" + code
                                    + ")");
                                cnts++;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("eccube商品連携NG 店舗ID:" + clientId);
                    logger.error(BaseException.print(e));
                    continue;
                }
            }
        }
        logger.info("eccube商品連携 終了--処理件数:" + cnts);
    }

    /**
     * @description: ECCUBE商品情報をサンロジに登録
     * @Param: api_id アプリID
     * @param: client_id 店舗ID
     * @Param: products 商品情報
     * @param: request HttpServletRequest
     * @return: List<String>
     * @author: hzm
     * @date: 2021/1/16
     */
    private List<String> insertProductDate(Integer apiId, String clientId, String renkeiPid, String code, String name,
        String price, String bundledFlg, Integer eccube_show_flg) {
        // 初期化
        List<String> list = new ArrayList<>();
        String tmpPid = ""; // 商品ID
        String tmpCode = code; // 商品コード
        String tmpName = name; // 商品名
        String tmpPrice = Double.valueOf(price).intValue() + ""; // 商品価格
        String tmpRekid = renkeiPid; // 外部連続番号
        String tmpSetid = ""; // セット商品ID
        String tmpUp = "0"; // 更新有(1:無 0:有)
        String tmpBunflg = "0";// 同梱物(0:無 1:有)
        Integer tmpShowFlg = eccube_show_flg;// ステータス(1:公開 2:非公開)

        // 同梱物
        if (!StringTools.isNullOrEmpty(bundledFlg) && "1".equals(bundledFlg)) {
            tmpBunflg = bundledFlg;
        }
        // 初期化
        Mc100_product mc100Product = null;
        try {
            // 商品コードで商品マスタから商品情報取得
            mc100Product = productDao.getProductInfoByCode(tmpCode, clientId);
        } catch (Exception e) {
            logger.warn("商品コード:" + tmpCode + "重複登録");
            logger.error(BaseException.print(e));
            return list;
        }

        // 初期化
        JSONObject object = new JSONObject();
        JSONObject items = new JSONObject();
        JSONArray array = new JSONArray();
        // 商品IDを取得(最大数)
        tmpPid = productService.createProductId(clientId);
        // 商品マスタにセットする
        object.put("client_id", clientId);
        items.put("name", tmpName);
        items.put("code", tmpCode);
        items.put("is_reduced_tax", 0);
        items.put("price", tmpPrice);
        items.put("bundled_flg", tmpBunflg);
        items.put("tags", new JSONArray());
        items.put("img", new JSONArray());
        items.put("eccube_show_flg", tmpShowFlg);
        items.put("kubun", 0);
        array.add(items);
        object.put("items", array);
        // 商品登録されていない場合、商品マスタに新規登録
        if (StringTools.isNullOrEmpty(mc100Product)) {
            // 商品新規登録
            productService.insertProductMain(object, null);
        } else {
            // 商品更新
            String product_id = mc100Product.getProduct_id();
            productService.updateProductMain(clientId, null, null, product_id, object, null);
        }
        // 商品名か商品コードにより、既存情報をセットする
        if (!StringTools.isNullOrEmpty(mc100Product)) {
            tmpPid = mc100Product.getProduct_id();
            tmpName = mc100Product.getName();
            tmpCode = mc100Product.getCode();
            // セット商品なしの場合、空値
            tmpSetid = String.valueOf(mc100Product.getSet_sub_id());
            if (StringTools.isNullOrEmpty(tmpSetid)) {
                tmpSetid = "";
            }
        }
        // 外部商品連携管理TBL
        Mc106_produce_renkei mc106Product = new Mc106_produce_renkei();
        // 連続APIID
        mc106Product.setApi_id(apiId);
        // 店舗ID
        mc106Product.setClient_id(clientId);
        // 商品ID
        mc106Product.setProduct_id(tmpPid);
        // 商品ID(外部SKU商品コード)
        mc106Product.setRenkei_product_id(tmpRekid);
        // 検証ID(外部在庫ロケーションID) TODO デフォルト値(default)として設定
        mc106Product.setVariant_id(tmpCode);

        // 外部連携商品情報登録
        Integer rows = productDao.getRenkeiProduct(mc106Product);
        if (rows == 0) {
            mc106Product.setIns_usr("eccube自動");
            mc106Product.setIns_date(new Timestamp(System.currentTimeMillis()));
            productDao.insertRenkeiProduct(mc106Product);
            tmpUp = "1"; // 追加更新あり
        } else {
            tmpUp = "0";// 既存
        }
        list.add(0, tmpPid);
        list.add(1, tmpName);
        list.add(2, tmpCode);
        list.add(3, tmpSetid);
        list.add(4, tmpUp);
        return list;
    }
}
