package com.lemonico.api;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ProductRenkeiDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.HttpUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.OrderApiDao;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.store.service.OrderApiService;
import java.sql.Timestamp;
import java.util.*;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ネクストエンジン受注連携
 * <p>
 * ドキュメントリンク
 * </p>
 * <p>
 * https://developer.next-engine.com/api/start
 * </p>
 */
@Component
public class NextEngineApi
{

    private final static Logger logger = LoggerFactory.getLogger(NextEngineApi.class);

    @Resource
    private StockDao stockDao;
    @Resource
    private OrderApiService orderApiService;
    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private OrderApiDao orderApiDao;
    @Resource
    private ProductRenkeiDao productRenkeiDao;
    @Resource
    private OrderApiService apiService;
    @Resource
    private APICommonUtils apiCommonUtils;


    private final static String TOKEN_NAME = "Authorization";
    public static final String NEXT_ENGINE_BASE_URI = "https://api.next-engine.org";
    public static final String NEXT_ENGINE_SEARCH_ORDERS_URI =
        NEXT_ENGINE_BASE_URI + "/api_v1_receiveorder_base/search";

    /**
     * NextEngine受注自動取込(10分ごと自動起動 2,12,22,32,42,52)
     *
     * @date 2021/12/27
     */
    // //@Scheduled(cron = "0 2/10 * * * ?")
    public void fetchNextEngineOrders() {
        logger.info("=============================");
        logger.info("Next-Engine 受注連携 開始");

        final String fields_search =
            "receive_order_shop_id,receive_order_id,receive_order_shop_cut_form_id,receive_order_date,"
                + "receive_order_import_date,receive_order_important_check_id,receive_order_important_check_name,receive_order_confirm_check_id,"
                + "receive_order_confirm_check_name,receive_order_confirm_ids,receive_order_mail_status,receive_order_gruoping_tag,"
                + "receive_order_import_type_id,receive_order_import_type_name,receive_order_cancel_type_id,receive_order_cancel_type_name,"
                + "receive_order_cancel_date,receive_order_order_status_id,receive_order_order_status_name,receive_order_delivery_id,"
                + "receive_order_delivery_name,receive_order_payment_method_id,receive_order_payment_method_name,receive_order_total_amount,"
                + "receive_order_tax_amount,receive_order_charge_amount,receive_order_delivery_fee_amount,receive_order_other_amount,"
                + "receive_order_point_amount,receive_order_goods_amount,receive_order_deposit_amount,receive_order_deposit_type_id,"
                + "receive_order_deposit_type_name,receive_order_deposit_date,receive_order_note,receive_order_include_possible_order_id,"
                + "receive_order_include_to_order_id,receive_order_multi_delivery_parent_order_id,receive_order_divide_from_order_id,"
                + "receive_order_copy_from_order_id,receive_order_multi_delivery_parent_flag,receive_order_statement_delivery_instruct_printing_date,"
                + "receive_order_statement_delivery_printing_date,receive_order_statement_delivery_text,receive_order_send_date,receive_order_send_plan_date,"
                + "receive_order_send_sequence,receive_order_worker_text,receive_order_picking_instruct,receive_order_label_print_date,receive_order_label_print_flag,"
                + "receive_order_hope_delivery_date,receive_order_hope_delivery_time_slot_id,receive_order_hope_delivery_time_slot_name,receive_order_delivery_method_id,"
                + "receive_order_delivery_method_name,receive_order_seal1_id,receive_order_seal1_name,receive_order_seal2_id,receive_order_seal2_name,receive_order_seal3_id,"
                + "receive_order_seal3_name,receive_order_seal4_id,receive_order_seal4_name,receive_order_business_office_stop_id,"
                + "receive_order_business_office_stop_name,receive_order_invoice_id,receive_order_invoice_name,receive_order_temperature_id,receive_order_temperature_name,"
                + "receive_order_business_office_name,receive_order_gift_flag,receive_order_delivery_cut_form_id,receive_order_delivery_cut_form_note,"
                + "receive_order_credit_type_id,receive_order_credit_type_name,receive_order_credit_approval_no,receive_order_credit_approval_amount,"
                + "receive_order_credit_approval_type_id,receive_order_credit_approval_type_name,receive_order_credit_approval_date,"
                + "receive_order_credit_approval_rate,receive_order_credit_number_payments,receive_order_credit_authorization_center_id,receive_order_credit_authorization_center_name,"
                + "receive_order_credit_approval_fax_printing_date,receive_order_customer_type_id,receive_order_customer_type_name,receive_order_customer_id,receive_order_purchaser_name,"
                + "receive_order_purchaser_kana,receive_order_purchaser_zip_code,receive_order_purchaser_address1,receive_order_purchaser_address2,"
                + "receive_order_purchaser_tel,receive_order_purchaser_fax,receive_order_purchaser_mail_address,receive_order_consignee_name,receive_order_consignee_kana,"
                + "receive_order_consignee_zip_code,receive_order_consignee_address1,receive_order_consignee_address2,receive_order_consignee_tel,receive_order_consignee_fax,"
                + "receive_order_important_check_pic_id,receive_order_important_check_pic_name,receive_order_pic_id,receive_order_pic_name,receive_order_send_pic_id,receive_order_send_pic_name,"
                + "receive_order_creation_date,receive_order_last_modified_date,receive_order_last_modified_null_safe_date,receive_order_creator_id,receive_order_creator_name,receive_order_last_modified_by_id,"
                + "receive_order_last_modified_by_null_safe_id,receive_order_last_modified_by_name,receive_order_last_modified_by_null_safe_name";

        List<Tc203_order_client> errOrderClients = new ArrayList<>();
        Hashtable<String, List<String>> errHashtable = new Hashtable<>();

        // API共通パーツ初期化
        apiCommonUtils.initialize();
        // 获取所有和next-engine连携的信息
        List<Tc203_order_client> clients = orderApiService.getAllData(API.NEXTENGINE.getName());
        for (Tc203_order_client client : clients) {
            // 伝票番号連携
            checkDeliveryTrack(client);
            // 認証情報取得
            String accessToken = client.getAccess_token();
            String refreshToken = client.getRefresh_token();
            String clientId = client.getClient_id();
            if (StringTools.isNullOrEmpty(accessToken) || StringTools.isNullOrEmpty(refreshToken)) {
                logger.error("店舗【{}】の認証情報が存在しないので、次の店舗にスキップする。", clientId);
                continue;
            }
            // 店舗情報を取得(店舗情報、依頼主、配送マスタ、店舗設定情報(mc007)
            InitClientInfoBean initClientInfo = apiCommonUtils.initClientCommon(client);

            JSONArray orderData;
            try {
                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("access_token", accessToken);
                headerMap.put("refresh_token", refreshToken);
                headerMap.put("fields", fields_search);
                headerMap.put("receive_order_order_status_id-eq", "20");
                JSONObject jsonObject =
                    HttpUtils.sendHttpsPost(NEXT_ENGINE_SEARCH_ORDERS_URI, headerMap, TOKEN_NAME, accessToken);

                if (!StringTools.isNullOrEmpty(jsonObject)) {
                    if ("success".equals(jsonObject.getString("result"))) {
                        // 新発行アクセストークン
                        String newAccessToken = jsonObject.getString("access_token");
                        // 新発行リフレッシュトークン
                        String newRefreshToken = jsonObject.getString("refresh_token");
                        orderApiDao.updateToken(newAccessToken, newRefreshToken, client.getId(), clientId);

                        if (jsonObject.getInteger("count") == 0) {
                            logger.info("店舗【{}】API【{}】の受注データが0件", clientId, client.getApi_name());
                            continue;
                        }
                        orderData = jsonObject.getJSONArray("data");
                    } else if ("error".equals(jsonObject.getString("result"))) {
                        // https://developer.next-engine.com/api/param/message
                        logger.error("店舗【{}】API【{}】 受注データ取得異常、エラーコード【{}】エラー情報【{}】", clientId, client.getApi_name(),
                            jsonObject.getString("code"), jsonObject.getString("message"));
                        continue;
                    } else {
                        logger.error("店舗：" + clientId + "、API名：" + client.getApi_name() + "、受注データ取得エラー、エラーJSON："
                            + jsonObject.toJSONString());
                        continue;
                    }
                } else {
                    logger.error("店舗：" + clientId + "、API名：" + client.getApi_name() + "、受注データ情報空です。");
                    continue;
                }
            } catch (Exception e) {
                logger.error(clientId + "店铺的" + client.getApi_name() + "的Api设定发生错误！");
                logger.error(BaseException.print(e));
                continue;
            }
            // エラーフラグ
            boolean errFlg = false;
            // 取込件数
            int total = orderData.size();
            // 成功件数
            int successCnt = 0;
            // 失敗件数
            int failureCnt = 0;
            // 受注番号の枝番
            int subno = 1;
            // 保存错误信息
            ArrayList<String> errList = new ArrayList<>();
            for (int i = 0; i < total; i++) {
                JSONObject orderJson = orderData.getJSONObject(i);
                // 获取依赖的受注番号
                String receive_order_id = orderJson.getString("receive_order_id");
                // 查看订单是否已经取过了
                Integer outerOrderNo = orderDao.getOuterOrderNo(receive_order_id, clientId);
                if (outerOrderNo > 0) {
                    logger.warn("next-engine受注連携NG 店舗ID:{} 受注番号:{} 原因:過去受注取込済", clientId, receive_order_id);
                    continue;
                }
                // 受注キャンセル 0 : 有効な受注です。
                String order_cancel = orderJson.getString("receive_order_cancel_type_name");
                if (!"0".equals(orderJson.getString("receive_order_cancel_type_id"))) {
                    logger.error("店舗：" + clientId + "、受注番号：" + receive_order_id + "、受注キャンセル：" + order_cancel);
                    continue;
                }

                if (outerOrderNo == 0) {
                    try {
                        // 受注子番号をセットする ※重要
                        initClientInfo.setSubNo(subno);
                        // 受注明細を取得
                        setTc200Json(client, orderJson, initClientInfo);
                        subno++;
                        successCnt++;
                    } catch (Exception e) {
                        subno++;
                        failureCnt++;
                        errList.add(" 受注番号: " + receive_order_id + " 原因:受注TBLの書込み失敗");
                        errFlg = true;
                        String nowTime = CommonUtils.getNewDate(null);
                        logger.error(
                            "订单错误，店铺ID：" + clientId + "受注番号：" + receive_order_id + ",时间" + nowTime + ",订单数据有误！！！！");
                        logger.error(BaseException.print(e));
                    }
                }

            }

            if (errFlg) {
                errOrderClients.add(client);
            }

            // 受注履歴を記録
            if (successCnt > 0) {
                apiCommonUtils.processOrderHistory(clientId, API.NEXTENGINE.getName(), initClientInfo.getHistoryId(),
                    successCnt, failureCnt);
            }
            logger.info("next-engine受注連携 店舗ID:{} 取込件数:{} 成功件数:{} 失败件数:{}", clientId, total, successCnt, failureCnt);

            if (!errList.isEmpty()) {
                String key = clientId + "_API名：" + client.getApi_name();
                errHashtable.put(key, errList);
            }
        }

        logger.info("next-engine受注連携 終了");
        logger.info("=============================");

        // 保存受注报错次数
        apiCommonUtils.insertApiErrorCount(errOrderClients);
    }

    /**
     * @Description: 写入受注表
     * @Param:
     * @return: void
     * @Date: 2020/12/16
     */
    private void setTc200Json(Tc203_order_client order_client, JSONObject order, InitClientInfoBean init) {

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
        // 受注番号
        String outerOrderNo = order.getString("receive_order_id");
        // 入金ステータス (入金待ち，入金済み)
        String deposit = order.getString("receive_order_deposit_type_name");
        int orderType = "入金済み".equals(deposit) ? 1 : 0;
        // **********************************************************
        // ********************* 受注明細情報 *********************
        // **********************************************************
        // 受注明細情報を保存する
        int product_price_excluding_tax;
        try {
            product_price_excluding_tax = getTc201DetailData(order_client, clientId, purchaseOrderNo, apiId,
                outerOrderNo, order.getString("receive_order_shop_id"));
        } catch (Exception e) {
            logger.error("next-engine受注連携NG 店舗ID:{} 受注ID:{} 原因:受注明細TBLの登録失敗", clientId, purchaseOrderNo);
            return;
        }
        System.err.println("========受注========");
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
        // 注文種別(0:入金待ち 1:入金済み)
        tc200Order.setOrder_type(orderType);
        // 外部注文ステータス
        tc200Order.setOuter_order_status(0);
        // 个口数
        tc200Order.setBoxes(1);
        // 必要字段写入: 配送先郵便番号1, 取込日時, 配送先住所郡市区, 配送先住所都道府県, 配送先姓
        // 注文日時 *****************
        String orderDatetime = order.getString("receive_order_date");
        if (!StringTools.isNullOrEmpty(orderDatetime)) {
            Date date = DateUtils.stringToDate(orderDatetime);
            tc200Order.setOrder_datetime(date);
        }
        // 取込日時
        tc200Order.setImport_datetime(new Timestamp(System.currentTimeMillis()));
        // 配達希望日
        String deliveryDate = order.getString("receive_order_hope_delivery_date");
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            Date date = DateUtils.stringToDate(deliveryDate);
            tc200Order.setDelivery_date(date);
        }

        // 出庫予定日
        String shipmentPlanDate = order.getString("receive_order_send_plan_date");
        if (!StringTools.isNullOrEmpty(shipmentPlanDate)) {
            Date date = DateUtils.stringToDate(shipmentPlanDate);
            tc200Order.setShipment_plan_date(date);
        }

        // 合計請求金額
        String total = order.getString("receive_order_total_amount");
        if (!StringTools.isNullOrEmpty(total)) {
            tc200Order.setBilling_total(Double.valueOf(total).intValue());
        }

        // 依頼マスタ(依頼主ID 及び明細書メッセージ)
        String receiveOrderShopId = order.getString("receive_order_shop_id");
        logger.info("receiveOrderShopId: {}", receiveOrderShopId);
        Ms012_sponsor_master ms012spons = init.getMs012sponsor();
        if ("SU036".equals(clientId)) {
            if ("".equals(receiveOrderShopId)) {

            } else if ("".equals(receiveOrderShopId)) {

            } else {

            }
        } else {

        }

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
        // 明細書メッセージ
        tc200Order.setDetail_message(order.getString("receive_order_statement_delivery_text"));

        // 削除フラグ
        tc200Order.setDel_flg(0);
        // 注文者郵便番号，購入者情報
        tc200Order.setOrder_zip_code1("000");
        tc200Order.setOrder_zip_code2("0000");
        List<String> zipList = CommonUtils.checkZipToList(order.getString("receive_order_purchaser_zip_code"));
        if (!io.jsonwebtoken.lang.Collections.isEmpty(zipList)) {
            tc200Order.setOrder_zip_code1(zipList.get(0));
            tc200Order.setOrder_zip_code2(zipList.get(1));
        }
        // 都道府县分割处理
        Map<String, String> order_address =
            apiCommonUtils.addressSplice(order.getString("receive_order_purchaser_address1"));
        // 注文者住所都道府県
        tc200Order.setOrder_todoufuken(order_address.get("todoufuken"));
        // 注文者住所郡市区
        tc200Order.setReceiver_address1(order_address.get("address1"));

        // 注文者詳細住所
        String address2 = order.getString("receive_order_purchaser_address2");
        if (!StringTools.isNullOrEmpty(address2)) {
            tc200Order.setOrder_address2(address2);
        }
        // 注文者姓
        tc200Order.setOrder_family_name(order.getString("receive_order_purchaser_name"));
        // 注文者名
        // tc200Order.setOrder_first_name(order.getString("first_name"));
        // 注文者電話番号
        List<String> orderTelList = CommonUtils.checkPhoneToList(order.getString("receive_order_purchaser_tel"));
        if (!io.jsonwebtoken.lang.Collections.isEmpty(orderTelList)) {
            // 注文者電話番号1
            tc200Order.setOrder_phone_number1(orderTelList.get(0));
            // 注文者電話番号2
            tc200Order.setOrder_phone_number2(orderTelList.get(1));
            // 注文者電話番号3
            tc200Order.setOrder_phone_number3(orderTelList.get(2));
        }
        // 注文者メールアドレス
        tc200Order.setOrder_mail(order.getString("receive_order_purchaser_mail_address"));
        // 配送方法名，発送方法名
        String shippingMethod = order.getString("receive_order_delivery_name");
        tc200Order.setBikou9(shippingMethod);

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

        // 配達希望時間帯
        String deliveryTimeZone = order.getString("receive_order_hope_delivery_time_slot_name");
        if (!StringTools.isNullOrEmpty(deliveryTimeZone)) {
            // 配達希望時間帯（SunLogi定義）
            String deliveryTimeSlot = apiCommonUtils.getDeliveryTimeSlot(shippingMethod, deliveryTimeZone,
                API.NEXTENGINE.getName(), ms007SettingTimeMap);
            tc200Order.setDelivery_time_slot(deliveryTimeSlot);
        }

        // 支付方法，支払名
        String payment = order.getString("receive_order_payment_method_name");
        // 支払名ID(1:代金引換)
        String payment_id = order.getString("receive_order_payment_method_id");
        tc200Order.setBikou10(payment);
        // 支付方法（SunLogi定義）
        String paymentMethod =
            apiCommonUtils.getPaymentMethod(payment, API.NEXTENGINE.getName(), ms007SettingPaymentMap);
        tc200Order.setPayment_method(paymentMethod);
        // 送料合計，発送代
        tc200Order.setDelivery_total(Double.valueOf(order.getString("receive_order_delivery_fee_amount")).intValue());

        // 手数料
        tc200Order.setHandling_charge(Double.valueOf(order.getString("receive_order_charge_amount")).intValue());
        // 代金引換総額(合計金額)
        if ("1".equals(payment_id) && !StringTools.isNullOrEmpty(total)) {
            tc200Order.setCash_on_delivery_fee(Double.valueOf(total).intValue());
        } else {
            tc200Order.setCash_on_delivery_fee(0);
        }

        // 備考
        String remark = order.getString("receive_order_note");
        if (!StringTools.isNullOrEmpty(remark)) {
            tc200Order.setMemo(remark);
        }

        // 配送先情報設定，送り先情報
        // 配送先郵便番号
        tc200Order.setReceiver_zip_code1("000");
        tc200Order.setReceiver_zip_code2("0000");
        List<String> receiverZipList = CommonUtils.checkZipToList(order.getString("receive_order_consignee_zip_code"));
        if (!io.jsonwebtoken.lang.Collections.isEmpty(receiverZipList)) {
            // 配送先郵便番号1
            tc200Order.setReceiver_zip_code1(receiverZipList.get(0));
            // 配送先郵便番号2
            tc200Order.setReceiver_zip_code2(receiverZipList.get(1));
        }

        // 配送先都道府县分割处理
        Map<String, String> receiver_address =
            apiCommonUtils.addressSplice(order.getString("receive_order_consignee_address1"));
        // 配送先住所都道府県
        tc200Order.setReceiver_todoufuken(receiver_address.get("todoufuken"));
        // 配送先住所郡市区
        tc200Order.setReceiver_address1(receiver_address.get("address1"));

        // 配送先詳細住所
        address2 = order.getString("receive_order_consignee_address2");
        if (!StringTools.isNullOrEmpty(address2)) {
            tc200Order.setReceiver_address2(address2);
        }
        // 配送先姓
        tc200Order.setReceiver_family_name(order.getString("receive_order_consignee_name"));
        // 配送先名
        // tc200Order.setReceiver_first_name(order.getString("first_name"));
        // 配送先電話番号
        List<String> receiverTelList = CommonUtils.checkPhoneToList(order.getString("receive_order_consignee_tel"));
        if (!io.jsonwebtoken.lang.Collections.isEmpty(receiverTelList)) {
            // 配送先電話番号1
            tc200Order.setReceiver_phone_number1(receiverTelList.get(0));
            // 配送先電話番号2
            tc200Order.setReceiver_phone_number2(receiverTelList.get(1));
            // 配送先電話番号3
            tc200Order.setReceiver_phone_number3(receiverTelList.get(2));
        }

        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        tc200Order.setForm(2);
        try {
            orderDao.insertOrder(tc200Order);
        } catch (Exception e) {
            logger.error("next-engine受注連携NG 店舗ID:{} 受注ID:{} 原因:受注管理TBLの登録失敗", clientId, outerOrderNo);
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
     * @Description: 受注详细
     * @Param:
     * @return:
     * @Date: 2020/12/16
     */
    private int getTc201DetailData(Tc203_order_client order_client, String clientId, String orderNo,
        Integer apiId, String receive_order_id, String receive_order_shop_id) {
        int product_price_excluding_tax = 0;
        Tc201_order_detail tc201OrderDetail = new Tc201_order_detail();
        JSONArray orderDetailData;

        try {
            String fields_search =
                "receive_order_row_receive_order_id,receive_order_row_shop_cut_form_id,receive_order_row_no," +
                    "receive_order_row_shop_row_no,receive_order_row_goods_id,receive_order_row_goods_name,receive_order_row_quantity,"
                    +
                    "receive_order_row_unit_price,receive_order_row_received_time_first_cost,receive_order_row_tax_rate,"
                    +
                    "receive_order_row_wholesale_retail_ratio,receive_order_row_sub_total_price,receive_order_row_goods_option,"
                    +
                    "receive_order_row_cancel_flag,receive_order_include_from_order_id,receive_order_include_from_row_no,"
                    +
                    "receive_order_row_multi_delivery_parent_order_id,receive_order_row_divide_from_row_no,receive_order_row_copy_from_row_no,"
                    +
                    "receive_order_row_stock_allocation_quantity,receive_order_row_advance_order_stock_allocation_quantity,"
                    +
                    "receive_order_row_stock_allocation_date,receive_order_row_received_time_merchandise_id,receive_order_row_received_time_merchandise_name,"
                    +
                    "receive_order_row_received_time_goods_type_id,receive_order_row_received_time_goods_type_name,receive_order_row_returned_good_quantity,"
                    +
                    "receive_order_row_returned_bad_quantity,receive_order_row_returned_reason_id,receive_order_row_returned_reason_name,"
                    +
                    "receive_order_row_org_row_no,receive_order_row_deleted_flag,receive_order_row_creation_date,receive_order_row_last_modified_date,"
                    +
                    "receive_order_row_last_modified_null_safe_date,receive_order_row_last_modified_newest_date,receive_order_row_creator_id,"
                    +
                    "receive_order_row_creator_name,receive_order_row_last_modified_by_id,receive_order_row_last_modified_by_null_safe_id,"
                    +
                    "receive_order_row_last_modified_by_name,receive_order_row_last_modified_by_null_safe_name";

            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("access_token", order_client.getAccess_token());
            dataMap.put("refresh_token", order_client.getRefresh_token());
            dataMap.put("fields", fields_search);
            dataMap.put("receive_order_row_receive_order_id-eq", receive_order_id);

            String url = Constants.NextEngine_API + "/api_v1_receiveorder_row/search";
            JSONObject jsonObject = HttpUtils.sendHttpsPost(url, dataMap, TOKEN_NAME, order_client.getAccess_token());
            if (!StringTools.isNullOrEmpty(jsonObject)) {
                if (jsonObject.getInteger("count") == 0) {
                    logger.error("店舗：" + clientId + "、受注番号：" + receive_order_id + "、商品詳細情報取得エラー、データ0件です");
                    throw new BaseException(ErrorCode.E_11005);
                }
                orderDetailData = jsonObject.getJSONArray("data");
            } else {
                logger.error("店舗：" + clientId + "、受注番号：" + receive_order_id + "、商品詳細情報取得エラー、API JSONエラー");
                throw new BaseException(ErrorCode.E_11005);
            }
        } catch (Exception e) {
            logger.error("店舗：" + clientId + "、受注番号：" + receive_order_id + "商品詳細情報取得エラー、APIエラー");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.E_11005);
        }

        Integer subNo = 1;

        for (int j = 0; j < orderDetailData.size(); j++) {
            JSONObject detailJson = orderDetailData.getJSONObject(j);

            tc201OrderDetail = new Tc201_order_detail();

            // 商品合計金額を取得
            product_price_excluding_tax +=
                Double.valueOf(detailJson.getString("receive_order_row_sub_total_price")).intValue();
            // 根据code查询该商品以前是否存在
            // 如果同一种商品 商品颜色不同 可能得根据variation_id 来区别
            // 商品コード
            String code = detailJson.getString("receive_order_row_goods_id");

            // 商品オプション値 TODO 固定的に空に設定するのが問題ないでしょうか。
            String options = detailJson.getString("receive_order_row_goods_option");

            // 商品名称
            String name = detailJson.getString("receive_order_row_goods_name");

            // 商品単価
            Integer price = Double.valueOf(detailJson.getString("receive_order_row_unit_price")).intValue();

            // 商品数量
            Integer amount = detailJson.getInteger("receive_order_row_quantity");

            // 获取店铺的税区分
            String shop_fields_search = "shop_id,shop_name,shop_tax_id,shop_tax_name";

            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("access_token", order_client.getAccess_token());
            dataMap.put("refresh_token", order_client.getRefresh_token());
            dataMap.put("fields", shop_fields_search);
            dataMap.put("shop_id-eq", receive_order_shop_id);

            String url = Constants.NextEngine_API + "/api_v1_master_shop/search";
            JSONObject shopJson = HttpUtils.sendHttpsPost(url, dataMap, TOKEN_NAME, order_client.getAccess_token());

            // 税区分
            String itemTaxType = "";
            if (!StringTools.isNullOrEmpty(shopJson) && shopJson.getInteger("count") > 0) {
                JSONArray jsonData = shopJson.getJSONArray("data");
                itemTaxType = jsonData.getJSONObject(0).getString("shop_tax_name");
            }

            // 税区分(0：税込 1:税抜)
            int isReducedTax = 0;
            if ("税抜".equals(itemTaxType)) {
                isReducedTax = 1;
            }

            // EC店舗側の商品ID TODO
            String renkeiProductId = detailJson.getString("receive_order_row_goods_id");
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
                mc100Product = apiCommonUtils.insertMc100Product(productBean, API.NEXTENGINE.getName());
                // 商品之前不存在 设定为仮登録
                tc201OrderDetail.setProduct_kubun(Constants.NOT_LOGGED_PRODUCT);
            }

            // 外部商品連携管理TBL
            if (!Objects.isNull(mc100Product)) {
                apiCommonUtils.insertMc106Product(clientId, apiId, renkeiProductId, mc100Product,
                    API.NEXTENGINE.getName());
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
                logger.error("next-engine受注連携NG 店舗ID:{} 受注ID:{} 原因:商品IDの取得失敗", clientId,
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
                logger.error("next-engine受注連携NG 店舗ID:{} 受注ID:{} 原因:受注明細TBLの登録失敗, 明细", clientId,
                    tc201OrderDetail.getOrder_detail_no());
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.E_11005);
            }
        }

        return product_price_excluding_tax;
    }

    /**
     * @description: 检查传票番号并写入
     * @return: void
     * @date: 2020/1/4
     */
    public void checkDeliveryTrack(Tc203_order_client order_client) {
        // a检查出荷完了的传票番号，如果有则写入并且更改
        List<Tw200_shipment> list =
            orderDao.getShipmentInfoByIdentifier(API.NEXTENGINE.getIdentification(), order_client.getClient_id());
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("access_token", order_client.getAccess_token());
        dataMap.put("refresh_token", order_client.getRefresh_token());
        for (Tw200_shipment tw200 : list) {
            if (StringTools.isNullOrEmpty(tw200.getDelivery_tracking_nm())) {
                continue;
            }

            // 查询当前订单的最后更新日期
            String modified_date = checkLastDate(order_client, tw200.getOrder_no());
            // 写入传票番号
            dataMap.put("receive_order_id", tw200.getOrder_no());
            dataMap.put("receive_order_last_modified_date", modified_date);
            System.err.println("传票番号是： " + tw200.getDelivery_tracking_nm());
            String tracking_nm = tw200.getDelivery_tracking_nm();
            if (tracking_nm.indexOf(",") != -1) {
                tracking_nm = tracking_nm.split(",")[0];
            }

            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<root>\n" +
                "<receiveorder_base>\n" +
                "<receive_order_delivery_cut_form_id>" + tracking_nm + "</receive_order_delivery_cut_form_id>\n" +
                "</receiveorder_base>\n" +
                "</root>";
            dataMap.put("data", xml);

            String url = Constants.NextEngine_API + "/api_v1_receiveorder_base/update";
            HttpUtils.sendHttpsPost(url, dataMap, TOKEN_NAME, order_client.getAccess_token());

            // 更新next平台订单状态(需要再次获取modified_date)
            modified_date = checkLastDate(order_client, tw200.getOrder_no());
            HashMap<String, String> dataMapUpdate = new HashMap<>();
            dataMapUpdate.put("access_token", order_client.getAccess_token());
            dataMapUpdate.put("refresh_token", order_client.getRefresh_token());
            dataMapUpdate.put("receive_order_id", tw200.getOrder_no());
            dataMapUpdate.put("receive_order_last_modified_date", modified_date);
            url = Constants.NextEngine_API + "/api_v1_receiveorder_base/shipped";
            HttpUtils.sendHttpsPost(url, dataMapUpdate, TOKEN_NAME, order_client.getAccess_token());

            orderDao.updateFinishFlag(tw200.getShipment_plan_id());
        }
    }

    /**
     * 检查最后更新日
     * 
     * @param order_client
     * @param order_no
     * @return
     */
    public String checkLastDate(Tc203_order_client order_client, String order_no) {
        HashMap<String, String> dataMapSearch = new HashMap<>();
        dataMapSearch.put("access_token", order_client.getAccess_token());
        dataMapSearch.put("refresh_token", order_client.getRefresh_token());
        dataMapSearch.put("fields", "receive_order_last_modified_date");
        dataMapSearch.put("receive_order_id-eq", order_no);

        String urlSearch = Constants.NextEngine_API + "/api_v1_receiveorder_base/search";
        JSONObject jsonObject =
            HttpUtils.sendHttpsPost(urlSearch, dataMapSearch, TOKEN_NAME, order_client.getAccess_token());
        JSONArray orderData = null;
        if (!StringTools.isNullOrEmpty(jsonObject)) {
            if (jsonObject.getInteger("count") == 0) {
                return null;
            }
            orderData = jsonObject.getJSONArray("data");
        } else {
            return null;
        }

        return orderData.getJSONObject(0).getString("receive_order_last_modified_date");
    }

    /**
     * @description: 在库数和平台同步
     * @return: void
     * @date: 2020/1/4
     */
    public void setProductCnt() {
        System.err.println("开始同步next-engine在库数...");
        String template = "next-engine";
        HashMap<String, Object> hashMap = BaseAPI.getStockMap(apiService, productRenkeiDao, stockDao, template);
        if (StringTools.isNullOrEmpty(hashMap)) {
            logger.error("tc203没有获取到信息");
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        HashMap<String, Mc106_produce_renkei> renkeiMap = (HashMap<String, Mc106_produce_renkei>) hashMap.get("renkei");
        HashMap<String, Integer> stockMap = (HashMap<String, Integer>) hashMap.get("stock");
        // a遍历mc106 map
        for (Map.Entry<String, Mc106_produce_renkei> map : renkeiMap.entrySet()) {
            String key = map.getKey();
            Mc106_produce_renkei produceRenkei = map.getValue();
            // 理论在库数
            Integer available_cnt = stockMap.get(key);
            String url = Constants.NextEngine_API + "/api_v1_master_goods/upload";
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", produceRenkei.getAccess_token());
            paramMap.put("refresh_token", produceRenkei.getRefresh_token());
            paramMap.put("data_type", "csv");

            // 获取平台的在库数
            String fields_search = "goods_id,goods_name,stock_quantity,supplier_name,goods_stock_constant";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("access_token", produceRenkei.getAccess_token());
            dataMap.put("refresh_token", produceRenkei.getRefresh_token());
            dataMap.put("fields", fields_search);
            String urlgetcount = Constants.NextEngine_API + "/api_v1_master_goods/search";
            JSONObject jsonObject = HttpUtils.sendHttpsPost(urlgetcount, dataMap, TOKEN_NAME,
                produceRenkei.getAccess_token());
            // 计算平台的在库数和系统内理论在库数得出应该加减的值
            JSONArray jArray = jsonObject.getJSONArray("data");

            String stock = jArray.getJSONObject(0).getString("stock_quantity");

            int count = available_cnt - Integer.valueOf(stock);
            if (available_cnt < 0) {
                count = -Integer.valueOf(stock);
            }
            // String data = "syohin_code,zaiko_su\n" + produceRenkei.getRenkeiProduct_id() + "," + count;
            String data = "syohin_code,zaiko_su\n ," + count;
            paramMap.put("data", data);
            try {
                HttpUtils.sendHttpsPost(url, paramMap, TOKEN_NAME, produceRenkei.getAccess_token());
            } catch (Exception e) {
                logger.error("店铺Id-商品Id：" + key + "修改在库数失败");
                logger.error(BaseException.print(e));
            }
        }
    }
}
