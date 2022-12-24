package com.lemonico.store.service.impl;

import static java.util.stream.Collectors.groupingBy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.CustomerHistoryService;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.*;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlBadRequestException;
import com.lemonico.core.exception.PlValidationErrorException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.CustomerDeliveryService;
import com.lemonico.store.service.ShipmentDetailService;
import com.lemonico.store.service.ShipmentsService;
import com.lemonico.wms.dao.ProductResultDao;
import com.lemonico.wms.service.impl.ShipmentServiceImpl;
import io.jsonwebtoken.lang.Collections;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: sunlogic
 * @description: 店铺侧出库情报
 * @create: 2020-05-12 10:42
 **/
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class ShipmentsImpl implements ShipmentsService
{
    private final static Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    private final ProductDao productDao;
    private final ProductService productService;
    private final ShipmentsDao shipmentsDao;
    private final DefinitionDao definitionDao;
    private final CustomerDeliveryService customerDeliveryService;
    private final SponsorDao sponsorDao;
    private final SettingDao settingDao;
    private final CommonFunctionDao commonFunctionDao;
    private final ShipmentDetailService shipmentDetailService;
    private final StockDao stockDao;
    private final ProductSettingService productSettingService;
    private final WarehouseDao warehouseDao;
    private final ClientDao clientDao;
    private final DeliveryDao deliveryDao;
    private final ProductResultDao productResultDao;
    private final ShipmentDetailDao shipmentDetailDao;
    private final CustomerHistoryService customerHistoryService;
    private final OrderCancelDao orderCancelDao;
    private final OrderDetailDao orderDetailDao;
    private final PathProps pathProps;

    /**
     * @description: 获取出库依赖一览数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/23
     */
    @Override
    public JSONObject getShipmentsList(JSONObject jsonObj) {

        // 检索条件处理
        jsonObj = setSearchJson(jsonObj);

        String client_id = jsonObj.getString("client_id");
        String cancel = jsonObj.getString("cancel");
        Integer currentPage = jsonObj.getInteger("currentPage");
        Integer pageSize = jsonObj.getInteger("pageSize");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        boolean delFlg = Optional.ofNullable(cancel).map(value -> "1".equals(value) ? true : false).orElse(false);

        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        List<Tw200_shipment> shipments = shipmentsDao.getShipmentsList(jsonObj);
        PageInfo<Tw200_shipment> pageInfo = null;
        long totalCnt = shipments.size();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(shipments);
            totalCnt = pageInfo.getTotal();
        }

        // 获取到所有的配送公司信息
        List<Ms004_delivery> deliveryInfo = deliveryDao.getDeliveryInfo(null);

        Map<String, Ms004_delivery> deliveryMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(deliveryInfo) && !deliveryInfo.isEmpty()) {
            deliveryMap = deliveryInfo.stream().collect(Collectors.toMap(Ms004_delivery::getDelivery_cd, o -> o));
        }

        // 获取所有的商品サイズマスタ
        List<Ms010_product_size> sizeNameList = productResultDao.getSizeNameList();
        Map<String, Ms010_product_size> sizeMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(sizeNameList) && !sizeNameList.isEmpty()) {
            sizeMap = sizeNameList.stream().collect(Collectors.toMap(x -> x.getSize_cd(), o -> o));
        }

        // 获取所有的支付方法
        List<Ms014_payment> paymentList = deliveryDao.getDeliveryPaymentAllList();
        Map<Integer, Ms014_payment> paymentMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(paymentList) && !paymentList.isEmpty()) {
            paymentMap = paymentList.stream().collect(Collectors.toMap(Ms014_payment::getPayment_id, o -> o));
        }

        List<Ms006_delivery_time> deliveryTimes = deliveryDao.getDeliveryTimeAll();
        Map<Integer, Ms006_delivery_time> timeMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(deliveryTimes) && !deliveryTimes.isEmpty()) {
            timeMap =
                deliveryTimes.stream().collect(Collectors.toMap(Ms006_delivery_time::getDelivery_time_id, o -> o));
        }

        // 根据店铺Id获取受注取消中的出库依赖Id
        List<String> shipmentIdList = orderCancelDao.getShipmentIdList(client_id);
        // 找到所有受注种别
        List<Ms017_csv_template> csvTmp = settingDao.getCsvTmp();
        // 识别番号为key值 模板名称为value
        Map<String, String> tmpMap = csvTmp.stream()
            .collect(Collectors.toMap(Ms017_csv_template::getIdentification, Ms017_csv_template::getTemplate));

        Map<String, List<Tw201_shipment_detail>> detailMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(shipments) && !shipments.isEmpty()) {
            List<String> shipmentIds =
                shipments.stream().map(Tw200_shipment::getShipment_plan_id).distinct().collect(Collectors.toList());
            List<Tw201_shipment_detail> shipmentDetail =
                shipmentDetailDao.getShipmentProductList(client_id, shipmentIds, false);
            detailMap = shipmentDetail.stream().collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));
        }
        JSONArray jsonArray = new JSONArray();
        for (Tw200_shipment shipment : shipments) {
            JSONObject jsonObject = new JSONObject();
            String shipmentPlanId = shipment.getShipment_plan_id();
            boolean containsId = false;
            if (!StringTools.isNullOrEmpty(shipmentIdList)) {
                containsId = shipmentIdList.contains(shipmentPlanId);
            }
            int cancelFlg = containsId ? 1 : 0;
            jsonObject.put("cancelFlg", cancelFlg);
            jsonObject.put("shipment_plan_id", shipment.getShipment_plan_id());
            jsonObject.put("shipment_status", shipment.getShipment_status());
            if (!StringTools.isNullOrEmpty(shipment.getRequest_date())) {
                jsonObject.put("request_date", format.format(shipment.getRequest_date()));
            }
            if (!StringTools.isNullOrEmpty(shipment.getUpd_date())) {
                jsonObject.put("upd_date", format.format(shipment.getUpd_date()));
            }
            if (!StringTools.isNullOrEmpty(shipment.getShipment_plan_date())) {
                jsonObject.put("shipment_plan_date", format.format(shipment.getShipment_plan_date()));
            }
            jsonObject.put("pdf_confirm_img", shipment.getPdf_confirm_img());
            // 同捆物数量计算
            int bundled_cnt = 0;
            String product_name = "";
            if (detailMap.containsKey(shipmentPlanId)) {
                List<Tw201_shipment_detail> details = detailMap.get(shipmentPlanId);
                bundled_cnt = details.stream().filter(x -> x.getKubun() == Constants.BUNDLED)
                    .mapToInt(Tw201_shipment_detail::getProduct_plan_cnt).sum();

                List<String> nameList =
                    details.stream().map(Tw201_shipment_detail::getName).collect(Collectors.toList());
                product_name = Joiner.on("/").join(nameList);
            }

            jsonObject.put("bundledNum", bundled_cnt);
            String name = product_name.length() <= 0 ? "" : product_name.substring(0, product_name.length() - 1);
            jsonObject.put("product_name", name);
            jsonObject.put("company", shipment.getCompany());
            jsonObject.put("warehouse_cd", shipment.getWarehouse_cd());
            jsonObject.put("surname", shipment.getSurname());
            jsonObject.put("surname_kana", shipment.getSurname_kana());
            jsonObject.put("prefecture", shipment.getPrefecture());
            jsonObject.put("status_message", shipment.getStatus_message());
            JSONArray fileList = new JSONArray();
            for (int i = 0; i < 5; i++) {
                String fileName = CommonUtils.savePath(shipment, i);
                if (StringTools.isNullOrEmpty(fileName)) {
                    break;
                } else {
                    JSONObject file = new JSONObject();
                    int index = fileName.lastIndexOf("/");
                    String substring = fileName.substring(index + 1);
                    file.put("name", substring);
                    file.put("path", fileName);
                    fileList.add(file);
                }
            }
            jsonObject.put("fileList", fileList);
            StringBuilder delivery_carrier_nm = new StringBuilder();
            String deliveryCarrier = shipment.getDelivery_carrier();
            if (deliveryMap.containsKey(deliveryCarrier)) {
                Ms004_delivery delivery = deliveryMap.get(deliveryCarrier);
                delivery_carrier_nm.append(delivery.getDelivery_nm()).append(" ")
                    .append(delivery.getDelivery_method_name());
                jsonObject.put("delivery_method", delivery.getDelivery_method());
                jsonObject.put("delivery_nm", delivery.getDelivery_nm());
            }
            jsonObject.put("delivery_carrier_nm", delivery_carrier_nm.toString());
            Date shipping_date = shipment.getShipping_date();
            if (!StringTools.isNullOrEmpty(shipping_date)) {
                jsonObject.put("shipping_date", simpleDateFormat.format(shipping_date));
            }
            jsonObject.put("pdf_name", shipment.getPdf_name());

            String sizeCd = shipment.getSize_cd();
            String sizeName = "";
            if (sizeMap.containsKey(sizeCd)) {
                Ms010_product_size productSize = sizeMap.get(sizeCd);
                sizeName = productSize.getName();
            }
            // 受注日付
            if (!StringTools.isNullOrEmpty(shipment.getOrder_datetime())) {
                jsonObject.put("order_datetime", format.format(shipment.getOrder_datetime()));
            }
            jsonObject.put("size_name", sizeName);
            jsonObject.put("cushioning_unit", shipment.getCushioning_unit());
            jsonObject.put("gift_wrapping_unit", shipment.getGift_wrapping_unit());
            jsonObject.put("delivery_carrier", shipment.getDelivery_carrier());
            jsonObject.put("box_delivery", shipment.getBox_delivery());
            jsonObject.put("fragile_item", shipment.getFragile_item());
            String deliveryTimeSlot = shipment.getDelivery_time_slot();
            if (!StringTools.isNullOrEmpty(deliveryTimeSlot) && StringTools.isInteger(deliveryTimeSlot)) {
                int deliveryId = Integer.parseInt(deliveryTimeSlot);
                if (timeMap.containsKey(deliveryId)) {
                    Ms006_delivery_time ms006DeliveryTime = timeMap.get(deliveryId);
                    String delivery_time_name = ms006DeliveryTime.getDelivery_time_name();
                    jsonObject.put("delivery_time_slot", delivery_time_name);
                }
            }
            String delivery_date = "";
            if (!StringTools.isNullOrEmpty(shipment.getDelivery_date())) {
                delivery_date = simpleDateFormat.format(shipment.getDelivery_date());
            }
            jsonObject.put("delivery_date", delivery_date);
            String payment_method = "";
            String paymentMethod = shipment.getPayment_method();
            if (!StringTools.isNullOrEmpty(paymentMethod) && StringTools.isInteger(paymentMethod)) {
                int payId = Integer.parseInt(paymentMethod);
                if (paymentMap.containsKey(payId)) {
                    Ms014_payment ms014Payment = paymentMap.get(payId);
                    payment_method = ms014Payment.getPayment_name();
                }
            }

            jsonObject.put("invoice_special_notes", shipment.getInvoice_special_notes());
            jsonObject.put("payment_method", payment_method);
            jsonObject.put("total_for_cash_on_delivery", shipment.getTotal_for_cash_on_delivery());
            jsonObject.put("total_price", shipment.getTotal_price());
            jsonObject.put("order_no", shipment.getOrder_no());
            jsonObject.put("delivery_tracking_nm", shipment.getDelivery_tracking_nm());
            jsonObject.put("product_plan_total", shipment.getProduct_plan_total());
            jsonObject.put("cash_on_delivery", shipment.getCash_on_delivery());
            jsonObject.put("total_amount", shipment.getTotal_amount());
            // 获取受注模板名称
            String identifier = shipment.getIdentifier();
            String orderIdent = "";
            if (!StringTools.isNullOrEmpty(identifier)) {
                // String patt = "(^RK[0-9]+\\-)(.*)";
                String key = "";
                // if (identifier.matches(patt)) {
                // // API受注乐天 : RK004-202103191725-00012
                // key = Constants.RAKUTE_ORDER_NO;
                // } else {
                List<String> list = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(identifier);
                key = list.get(0);
                if (!StringTools.isNullOrEmpty(key)) {
                    key = key.replaceAll("[0-9]", "");
                }
                // }
                orderIdent = tmpMap.get(key);
            }
            jsonObject.put("orderIdent", orderIdent);

            List<String> deliveryTrackingNmList = new ArrayList<>();
            // 获取到 伝票番号
            String delivery_tracking_nm = shipment.getDelivery_tracking_nm();
            if (!StringTools.isNullOrEmpty(delivery_tracking_nm)) {
                // 如果伝票番号不为空， 判断其中是否为多个
                boolean contains = delivery_tracking_nm.contains(",");
                if (contains) {
                    // 如果多个，根据逗号切割
                    deliveryTrackingNmList = Splitter.on(",").trimResults().omitEmptyStrings()
                        .splitToList(delivery_tracking_nm);
                } else {
                    // 单个
                    deliveryTrackingNmList.add(delivery_tracking_nm);
                }
            }
            jsonObject.put("deliveryTrackingNmList", deliveryTrackingNmList);
            jsonArray.add(jsonObject);
        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("result_data", jsonArray);
        resultJson.put("total", totalCnt);
        return CommonUtils.success(resultJson);
    }

    /**
     * 获取出库依赖详细
     *
     * @param client_id
     * @param shipment_plan_id
     * @return
     */
    @Override
    public Tw200_shipment getShipmentsDetail(String client_id, String shipment_plan_id, String copy_flg) {
        Tw200_shipment shipment = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
        if (StringTools.isNullOrEmpty(shipment)) {
            return null;
        }

        List<String> deliveryTrackingNmList = new ArrayList<>();
        // 获取到 伝票番号
        String delivery_tracking_nm = shipment.getDelivery_tracking_nm();
        if (!StringTools.isNullOrEmpty(delivery_tracking_nm)) {
            // 如果伝票番号不为空， 判断其中是否为多个
            boolean contains = delivery_tracking_nm.contains(",");
            if (contains) {
                // 如果多个，根据逗号切割
                deliveryTrackingNmList =
                    Splitter.on(",").trimResults().omitEmptyStrings().splitToList(delivery_tracking_nm);
            } else {
                // 单个
                deliveryTrackingNmList.add(delivery_tracking_nm);
            }
        }
        shipment.setDelivery_tracking_nm_list(deliveryTrackingNmList);

        boolean cancle_flg = false;
        if (shipment.getDel_flg() == 1) {
            cancle_flg = true;
        }

        // 受注信息获取
        String orderPhone = "";
        if (!StringTools.isNullOrEmpty(shipment.getOrder_phone_number1())) {
            orderPhone += shipment.getOrder_phone_number1() + '-';
        }
        if (!StringTools.isNullOrEmpty(shipment.getOrder_phone_number2())) {
            orderPhone += shipment.getOrder_phone_number2() + '-';
        }
        if (!StringTools.isNullOrEmpty(shipment.getOrder_phone_number3())) {
            orderPhone += shipment.getOrder_phone_number3() + '-';
        }
        if (!StringTools.isNullOrEmpty(orderPhone)) {
            orderPhone = orderPhone.substring(0, orderPhone.length() - 1);
        } else {
            orderPhone = "-";
        }
        shipment.setOrder_phone_number1(orderPhone);

        String orderZip = "-";
        String order_zip_code1 = shipment.getOrder_zip_code1();
        String order_zip_code2 = shipment.getOrder_zip_code2();
        if (!StringTools.isNullOrEmpty(order_zip_code1)) {
            if (!StringTools.isNullOrEmpty(order_zip_code2)) {
                order_zip_code1 += order_zip_code2;
            }
            orderZip = CommonUtils.getZip(order_zip_code1.replaceAll("-", ""));
        }
        shipment.setOrder_zip_code(orderZip);

        // 获取商品详细信息
        List<Tw201_shipment_detail> shipmentDetailList =
            shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, cancle_flg);
        List<Tw201_shipment_detail> shipmentDetails =
            setShipmentProductDetail(shipmentDetailList, client_id, copy_flg, true);
        shipment.setTw201_shipment_detail(shipmentDetails);

        return shipment;
    }

    /**
     * @Description: 店铺侧出库情报一览
     * @return: jsonObj
     * @Date: 2020/5/12
     */
    @Override
    public List<Tw200_shipment> getShipmentsCsvList(JSONObject jsonObj) {

        jsonObj = setSearchJson(jsonObj);
        // true: 伝票単位下载，false: 明細単位
        boolean csv_flg = jsonObj.getBoolean("csv_flg");
        String client_id = jsonObj.getString("client_id");
        String request_date_start = jsonObj.getString("request_date_start");
        String request_date_end = jsonObj.getString("request_date_end");

        Date start = DateUtils.stringToDate(request_date_start);
        // 検索日付を加工（23:59:59）にセットする @Add wang 2021/4/20
        Date end = CommonUtils.getDateEnd(request_date_end);
        // 获得店铺信息
        Ms201_client clientInfo = clientDao.getClientInfo(client_id);

        List<Tw200_shipment> shipmentsList = shipmentsDao.getShipmentsList(jsonObj);

        String warehouse_cd = warehouseDao.getWarehouseName(client_id);
        Mw400_warehouse warehouse = warehouseDao.getInfoByWarehouseCd(warehouse_cd);
        AtomicBoolean cancel_flg = new AtomicBoolean(false);

        // 获取所有的商品サイズマスタ
        List<Ms010_product_size> sizeNameList = productResultDao.getSizeNameList();
        Map<String, Ms010_product_size> sizeMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(sizeNameList) && !sizeNameList.isEmpty()) {
            sizeMap = sizeNameList.stream().collect(Collectors.toMap(Ms010_product_size::getSize_cd, o -> o));
        }
        List<String> sponsorIds =
            shipmentsList.stream().map(Tw200_shipment::getSponsor_id).distinct().collect(Collectors.toList());
        List<Ms012_sponsor_master> sponsorMasters = settingDao.getDeliveryListMore(client_id, sponsorIds);
        Map<String, Ms012_sponsor_master> sponsorMasterHashMap = new HashMap<String, Ms012_sponsor_master>();
        if (!StringTools.isNullOrEmpty(sponsorMasters) && !sponsorMasters.isEmpty()) {
            sponsorMasterHashMap =
                sponsorMasters.stream().collect(Collectors.toMap(Ms012_sponsor_master::getSponsor_id, o -> o));
        }

        List<String> shipmentIdList =
            shipmentsList.stream().map(Tw200_shipment::getShipment_plan_id).collect(Collectors.toList());

        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentProductList(client_id, shipmentIdList, true);
        Map<String, List<Tw201_shipment_detail>> detailMap = shipmentDetails.stream()
            .collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));

        for (Tw200_shipment list : shipmentsList) {
            String size_cd = list.getSize_cd();
            if (sizeMap.containsKey(size_cd)) {
                Ms010_product_size size = sizeMap.get(size_cd);
                list.setSize_name(size.getName());
            }
            if (StringTools.isNullOrEmpty(list.getBoxes())) {
                list.setBoxes(1);
            }

            String sponsorId = list.getSponsor_id();
            if (sponsorMasterHashMap.containsKey(sponsorId)) {
                Ms012_sponsor_master master = sponsorMasterHashMap.get(sponsorId);
                list.setMs012_sponsor_master(master);
            }
            if (!StringTools.isNullOrEmpty(warehouse)) {
                list.setWarehouse_nm(warehouse.getWarehouse_nm());
            }
            if (!StringTools.isNullOrEmpty(clientInfo)) {
                list.setClient_nm(clientInfo.getClient_nm());
            }

            String file = list.getFile();
            if (!StringTools.isNullOrEmpty(file)) {
                if (file.contains("/")) {
                    List<String> splitToList = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(file);
                    list.setFileName(splitToList.get(splitToList.size() - 1));
                }
            }
            // 受注信息获取
            String orderPhone = "";
            if (!StringTools.isNullOrEmpty(list.getOrder_phone_number1())) {
                orderPhone += list.getOrder_phone_number1() + '-';
            }
            if (!StringTools.isNullOrEmpty(list.getOrder_phone_number2())) {
                orderPhone += list.getOrder_phone_number2() + '-';
            }
            if (!StringTools.isNullOrEmpty(list.getOrder_phone_number3())) {
                orderPhone += list.getOrder_phone_number3() + '-';
            }
            if (!StringTools.isNullOrEmpty(orderPhone)) {
                orderPhone = orderPhone.substring(0, orderPhone.length() - 1);
            } else {
                orderPhone = "-";
            }
            list.setOrder_phone_number1(orderPhone);

            String orderZip = "-";
            String order_zip_code1 = list.getOrder_zip_code1();
            String order_zip_code2 = list.getOrder_zip_code2();
            if (!StringTools.isNullOrEmpty(order_zip_code1)) {
                if (!StringTools.isNullOrEmpty(order_zip_code2)) {
                    order_zip_code1 += order_zip_code2;
                }
                orderZip = CommonUtils.getZip(order_zip_code1.replaceAll("-", ""));
            }
            list.setOrder_zip_code(orderZip);

            Integer status = list.getShipment_status();
            Integer del_flg = list.getDel_flg();
            if (status == 999 && del_flg == 1) {
                cancel_flg.set(true);
            }

            String planId = list.getShipment_plan_id();
            List<Tw201_shipment_detail> shipmentDetailList = detailMap.get(planId);
            // 计算商品税率
            Integer commodity = commodityTaxRate(shipmentDetailList, cancel_flg);
            list.setSubtotal_amount_tax(commodity);

            // true: 伝票単位下载，false: 明細単位
            if (!csv_flg) {
                List<Tw201_shipment_detail> shipmentDetail =
                    setShipmentProductDetail(shipmentDetailList, client_id, "0", false);
                list.setTw201_shipment_detail(shipmentDetail);
            }
        }

        return shipmentsList;
    }

    /**
     * 计算出库总价
     *
     */
    public Integer commodityTaxRate(List<Tw201_shipment_detail> shipmentDetails, AtomicBoolean cancel_flg) {
        // 商品合计(税拔)
        BigDecimal commodityTotal = new BigDecimal(0);
        // 税拔集合 1, 10, 11, 12
        List<Integer> list = Arrays.asList(1, 10, 11, 12);
        for (Tw201_shipment_detail detail : shipmentDetails) {
            // 判断是否为轻减税率商品
            Integer is_reduced_tax = detail.getIs_reduced_tax();

            double tax = (StringTools.isNullOrEmpty(is_reduced_tax) || is_reduced_tax == 0) ? 0.1 : 0.08;

            int tax_flag = detail.getTax_flag();
            // 单价
            BigDecimal bigUnitPrice = new BigDecimal(detail.getUnit_price());
            // 出库依赖数
            BigDecimal bigProductPlanCnt = new BigDecimal(detail.getProduct_plan_cnt());
            // 商品税拔价格
            BigDecimal bigPrice = new BigDecimal(0);
            // 如果商品为 税入 需要计算其不含税的价格
            if (tax_flag == 0) {
                BigDecimal bigTax = BigDecimal.valueOf(1.0 + tax);
                bigPrice = bigUnitPrice.divide(bigTax, 2).multiply(bigProductPlanCnt);
            } else {
                bigPrice = bigUnitPrice.multiply(bigProductPlanCnt);
            }

            commodityTotal = bigPrice.add(commodityTotal);

            String taxDivision = "税込";
            if (list.contains(tax_flag)) {
                taxDivision = "税抜";
            }
            if (tax_flag == 3) {
                taxDivision = "非課税";
            }

            detail.setTaxDivision(taxDivision);
        }
        return commodityTotal.intValue();
    }

    /**
     * @Description: 店铺侧出库情报新规
     * @Param: Tw200_shipment
     * @return: Integer
     * @Date: 2020/05/13
     */
    @Override
    @Transactional
    public Integer setShipments(JSONObject jsonParam, boolean insertFlg, HttpServletRequest servletRequest) {
        Integer result = 0;
        Date nowTime = DateUtils.getDate();
        String login_nm = null;
        if (!StringTools.isNullOrEmpty(servletRequest)) {
            login_nm = CommonUtils.getToken("login_nm", servletRequest);
        }
        String clientId = jsonParam.getString("client_id");
        if (CommonUtils.toInteger(jsonParam.getString("customer_delivery")) == 1) {
            jsonParam.getJSONObject("sender").put("client_id", clientId);
            jsonParam.getJSONObject("sender").put("name",
                CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("surname")));
            jsonParam.getJSONObject("sender").put("ins_usr", CommonUtils.trimSpace(login_nm));
            jsonParam.getJSONObject("sender").put("ins_date", nowTime);
            jsonParam.getJSONObject("sender").put("upd_usr", login_nm);
            jsonParam.getJSONObject("sender").put("upd_date", nowTime);
            // 判断是法人还是个人
            String fromType = jsonParam.getJSONObject("sender").getString("form");
            Integer senderCount = 0;
            if (!StringTools.isNullOrEmpty(fromType) && "1".equals(fromType)) {// 是法人
                senderCount = customerDeliveryService.getCompanyCustomerDeliveryCount(clientId,
                    jsonParam.getJSONObject("sender").getInteger("delivery_id"),
                    jsonParam.getJSONObject("sender").getString("company"));
            } else {
                // 是”个人“ 查询数据库 判断是新增还是修改
                senderCount = customerDeliveryService.getCustomerDeliveryCount(clientId,
                    jsonParam.getJSONObject("sender").getInteger("delivery_id"),
                    jsonParam.getJSONObject("sender").getString("surname"));
            }
            if (!StringTools.isNullOrEmpty(senderCount) && senderCount > 0) {
                customerDeliveryService.updateCustomerDelivery(jsonParam.getJSONObject("sender"));
            } else {
                jsonParam.getJSONObject("sender").remove("delivery_id");
                customerDeliveryService.insertCustomerDelivery(jsonParam.getJSONObject("sender"));
            }

        }
        // 获取到出荷指示特記事項
        String instructionsList = jsonParam.getString("delivery_instructions");
        if (!StringTools.isNullOrEmpty(instructionsList)) {
            List<String> splitToList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(instructionsList);
            for (int i = 0; i < splitToList.size(); i++) {
                if (i == 0) {
                    jsonParam.put("instructions_special_notes", splitToList.get(i));
                } else {
                    jsonParam.put("bikou" + i, splitToList.get(i));
                }
            }
            jsonParam.put("bikou_flg", 1);
        } else if (StringTools.isNullOrEmpty(jsonParam.getString("order_flg"))) {
            jsonParam.put("bikou_flg", 0);
        }

        jsonParam.put("shipment_plan_date", null);
        jsonParam.put("delivery_plan_date", DateUtils.stringToDate(jsonParam.getString("delivery_plan_date")));
        jsonParam.put("shipping_date", DateUtils.stringToDate(jsonParam.getString("shipping_date")));
        jsonParam.put("delivery_date", DateUtils.stringToDate(jsonParam.getString("delivery_date")));
        jsonParam.put("delivery_time_slot", jsonParam.getString("delivery_time_slot"));

        jsonParam.put("box_delivery",
            CommonUtils.toInteger(jsonParam.getJSONObject("delivery_options").getString("box_delivery")));
        jsonParam.put("fragile_item",
            CommonUtils.toInteger(jsonParam.getJSONObject("delivery_options").getString("fragile_item")));
        jsonParam.remove("delivery_options");
        String postcode = jsonParam.getJSONObject("sender").getString("postcode");
        if (!StringTools.isNullOrEmpty(postcode)) {
            String formatZip = CommonUtils.formatZip(postcode);
            jsonParam.put("postcode", formatZip.trim());
        }

        jsonParam.put("surname", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("surname")));
        jsonParam.put("gift_sender_name", CommonUtils.trimSpace(jsonParam.getString("gift_sender_name")));
        jsonParam.put("label_note", CommonUtils.trimSpace(jsonParam.getString("label_note")));
        jsonParam.put("phone", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("phone")));
        jsonParam.put("prefecture", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("prefecture")));
        jsonParam.put("address1", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("address1")));
        jsonParam.put("address2", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("address2")));
        jsonParam.put("company", CommonUtils.trimSpace(jsonParam.getJSONObject("sender").getString("company")));
        jsonParam.put("division", jsonParam.getJSONObject("sender").getString("division"));
        // 注文者情报
        // 会社名
        jsonParam.put("order_company", jsonParam.getJSONObject("sender").getString("order_company"));
        // 部署
        jsonParam.put("order_division", jsonParam.getJSONObject("sender").getString("order_division"));
        // お名前
        jsonParam.put("order_family_name", jsonParam.getJSONObject("sender").getString("order_family_name"));
        // 注文者名
        jsonParam.put("order_first_name", jsonParam.getJSONObject("sender").getString("order_first_name"));
        // 注文者姓カナ
        jsonParam.put("order_family_kana", jsonParam.getJSONObject("sender").getString("order_family_kana"));
        // 注文者名カナ
        jsonParam.put("order_first_kana", jsonParam.getJSONObject("sender").getString("order_first_kana"));
        // 郵便番号
        jsonParam.put("order_zip_code1", jsonParam.getJSONObject("sender").getString("order_zip_code1"));
        jsonParam.put("order_zip_code2", jsonParam.getJSONObject("sender").getString("order_zip_code2"));
        // 都道府県
        jsonParam.put("order_todoufuken", jsonParam.getJSONObject("sender").getString("order_todoufuken"));
        // 住所
        jsonParam.put("order_address1", jsonParam.getJSONObject("sender").getString("order_address1"));
        // マンション・ビル名
        jsonParam.put("order_address2", jsonParam.getJSONObject("sender").getString("order_address2"));
        // 電話番号1
        jsonParam.put("order_phone_number1", jsonParam.getJSONObject("sender").getString("order_phone_number1"));
        // 電話番号2
        jsonParam.put("order_phone_number2", jsonParam.getJSONObject("sender").getString("order_phone_number2"));
        // 電話番号3
        jsonParam.put("order_phone_number3", jsonParam.getJSONObject("sender").getString("order_phone_number3"));
        // 注文者性別
        jsonParam.put("order_gender", jsonParam.getJSONObject("sender").getString("order_gender"));

        jsonParam.put("order_mail", jsonParam.getJSONObject("sender").getString("order_mail"));

        String email = jsonParam.getJSONObject("sender").getString("email");
        // 加入定期购入id
        jsonParam.put("buy_id", jsonParam.getString("buy_id"));
        // 加入定期購入回数
        jsonParam.put("buy_cnt", jsonParam.getInteger("buy_cnt"));
        // 加入次回お届け予定日
        jsonParam.put("next_delivery_date", DateUtils.stringToDate(jsonParam.getString("next_delivery_date")));

        if (StringTools.isNullOrEmpty(email)) {
            jsonParam.put("email", null);
        } else {
            jsonParam.put("email", email.trim());
        }
        jsonParam.put("form", CommonUtils.toInteger(jsonParam.getJSONObject("sender").getString("form")));
        jsonParam.remove("sender");
        // 送り状特記事項
        jsonParam.put("invoice_special_notes", CommonUtils.trimSpace(jsonParam.getString("invoice_special_notes")));

        jsonParam.put("delivery_charge", CommonUtils.toInteger(jsonParam.getString("delivery_charge")));
        jsonParam.put("handling_charge", CommonUtils.toInteger(jsonParam.getString("handling_charge")));
        jsonParam.put("discount_amount", CommonUtils.toLong(jsonParam.getString("discount_amount")));
        jsonParam.put("total_amount", CommonUtils.toLong(jsonParam.getString("total_amount")));
        jsonParam.put("tax", CommonUtils.toLong(jsonParam.getString("tax")));
        jsonParam.put("total_with_normal_tax", CommonUtils.toLong(jsonParam.getString("total_with_normal_tax")));
        jsonParam.put("total_with_reduced_tax", CommonUtils.toLong(jsonParam.getString("total_with_reduced_tax")));
        jsonParam.put("cash_on_delivery", CommonUtils.toInteger(jsonParam.getString("cash_on_delivery")));
        jsonParam.put("total_for_cash_on_delivery",
            CommonUtils.toInteger(jsonParam.getString("total_for_cash_on_delivery")));
        jsonParam.put("tax_for_cash_on_delivery",
            CommonUtils.toInteger(jsonParam.getString("tax_for_cash_on_delivery")));
        jsonParam.put("price_on_delivery_note", CommonUtils.toInteger(jsonParam.getString("price_on_delivery_note")));
        int product_kind_plan_cnt = jsonParam.getJSONArray("items").size();
        for (int i = 0; i < jsonParam.getJSONArray("items").size(); i++) {
            JSONObject items = jsonParam.getJSONArray("items").getJSONObject(i);
            if (!StringTools.isNullOrEmpty(items.get("bundled_flg")) && items.getInteger("bundled_flg") == 1) {
                product_kind_plan_cnt--;
            }
        }
        jsonParam.put("product_kind_plan_cnt", product_kind_plan_cnt);
        if (!StringTools.isNullOrEmpty(jsonParam.getString("payment_method"))) {
            jsonParam.put("payment_method", jsonParam.getString("payment_method"));
        } else {
            jsonParam.put("payment_method", null);
        }
        // PDF添附
        String pdf_name = jsonParam.getString("pdf_name");
        if (pdf_name != null && pdf_name != "") {
            String nowDate = DateUtils.getDateDay();

            // 图片路径
            String uploadPath = pathProps.getStore() + clientId + "/shipment/approval/" + nowDate + "/";
            jsonParam.put("pdf_name", uploadPath + pdf_name);
        }
        jsonParam.put("upd_usr", login_nm);
        jsonParam.put("upd_date", nowTime);
        // 請求コード @Add wang 20021/04/1 start
        jsonParam.put("bill_barcode", jsonParam.getString("bill_barcode"));
        jsonParam.put("bikou9", jsonParam.getString("bikou9"));
        jsonParam.put("bikou10", jsonParam.getString("bikou10"));
        // 請求コード @Add wang 20021/04/1 end

        if (insertFlg) {
            if (CommonUtils.toInteger(jsonParam.getString("delivery_note_type")) == 1) {
                jsonParam.put("delivery_note_type", "同梱する");
            } else {
                jsonParam.put("delivery_note_type", "同梱しない");
            }
            jsonParam.put("request_date", nowTime);
            jsonParam.put("ins_usr", login_nm);
            jsonParam.put("ins_date", nowTime);
            // 店铺侧页面新规出库依赖
            if (StringTools.isNullOrEmpty(jsonParam.getString("order_no"))) {
                jsonParam.put("order_datetime", nowTime);
            }
            // Qoo10関連注文番号
            jsonParam.put("related_order_no", jsonParam.getString("related_order_no"));
            result = shipmentsDao.insertShipments(jsonParam);
        } else {
            // 回滚原有数据
            deleteShipments(servletRequest, clientId, jsonParam, false);
            result = shipmentsDao.updateShipments(jsonParam);
        }
        // 利用されていないため、削除 @Add wang 2021/4/20
        // Integer priceOnDeliveryNote = Integer.valueOf(jsonParam.getString("price_on_delivery_note"));
        return result;
    }

    /**
     * @Description: 店铺侧出库情报新规
     * @Param: 顧客CD， 出庫依頼ID
     * @return: List
     * @Date: 2020/05/13
     */
    @Override
    public Integer countShipments(String client_id, String shipment_plan_id) {
        return shipmentsDao.countShipments(client_id, shipment_plan_id);
    }

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD, 出庫依頼ID
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    @Override
    public JSONObject deleteShipmentsList(String client_id, String shipment_plan_id,
        HttpServletRequest httpServletRequest) {
        String[] shipmentIdList = shipment_plan_id.split(",");
        ArrayList<String> errList = new ArrayList<>();
        JSONObject shipmentJsonObject = new JSONObject();
        for (int i = 0; i < shipmentIdList.length; i++) {
            // 不存在的出库
            int countShipments = countShipments(client_id, shipmentIdList[i]);
            if (countShipments <= 0) {
                errList.add(shipmentIdList[i]);
                continue;
            }
            // 出库状态是出荷开始，不允许取消
            List<Integer> statusList = Arrays.asList(4, 5, 7, 41, 42, 8);
            Integer statusCount = getShipmentStatus(client_id, statusList, shipmentIdList[i]);
            if (!StringTools.isNullOrEmpty(statusCount) && statusCount > 0) {
                errList.add(shipmentIdList[i]);
                continue;
            }

            // 削除
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("shipment_plan_id", shipmentIdList[i]);
                deleteShipments(httpServletRequest, client_id, jsonObject, true);
            } catch (Exception e) {
                errList.add(shipmentIdList[i]);
            }
        }
        shipmentJsonObject.put("err_list", errList);
        return CommonUtils.success(shipmentJsonObject);
    }

    /**
     * @Description: 店舗側出庫情報を保留する
     * @Param: 顧客CD, 出庫依頼ID，ステータス理由
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    @Override
    public JSONObject keepShipmentsList(String client_id, JSONObject jsonObject,
        HttpServletRequest httpServletRequest) {
        String shipmentIds = jsonObject.getString("shipment_plan_id");
        String statusMessage = jsonObject.getString("status_message");
        String[] shipmentIdList = shipmentIds.split(",");
        JSONObject keepObject = new JSONObject();
        List<String> errList = new ArrayList<>();
        for (int i = 0; i < shipmentIdList.length; i++) {
            // 不存在的出库信息，不进行出庫保留操作
            int countShipments = countShipments(client_id, shipmentIdList[i]);
            if (countShipments <= 0) {
                errList.add(shipmentIdList[i]);
                continue;
            }
            // 出库开始，入金等待，出库保留，不进行出库保留
            List<Integer> statusList = Arrays.asList(4, 5, 7, 41, 42, 8, 9, 6);
            Integer statusCount = getShipmentStatus(client_id, statusList, shipmentIdList[i]);
            if (!StringTools.isNullOrEmpty(statusCount) && statusCount > 0) {
                errList.add(shipmentIdList[i]);
                continue;
            }
            try {
                shipmentsDao.updateShipmentStatusMessage(shipmentIdList[i], client_id, 6, statusMessage);
                // 顧客別作業履歴新增
                String[] plan_id = {
                    shipmentIdList[i]
                };
                customerHistoryService.insertCustomerHistory(httpServletRequest, plan_id, "06", null);
            } catch (Exception e) {
                errList.add(shipmentIdList[i]);
            }
        }
        keepObject.put("err_list", errList);
        return CommonUtils.success(keepObject);
    }

    /**
     * @Description: 店铺侧出库情报删除
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/13
     */
    @Override
    @Transactional
    public Integer deleteShipments(HttpServletRequest servletRequest, String client_id, JSONObject jsonParam,
        boolean deleteFlg) {
        Date upd_date = DateUtils.getDate();
        String upd_usr = CommonUtils.getToken("login_nm", servletRequest);
        String shipment_plan_id = jsonParam.getString("shipment_plan_id");

        if (!deleteFlg) {
            logger.info("出庫依頼編集、出庫ID：" + shipment_plan_id + "、編集時間：" + upd_date + "、編集者：" + upd_usr);
        } else {
            logger.info("出庫依頼削除、出庫ID：" + shipment_plan_id + "、削除時間：" + upd_date + "、削除者：" + upd_usr);
        }
        // 获取商品依赖数
        List<Tw201_shipment_detail> list = shipmentDetailService.getShipmentDetailList(client_id, shipment_plan_id);

        String delete_data = jsonParam.getString("delete_data");
        // 如果本次编辑有删除 商品
        List<String> deleteProductIdList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(delete_data)) {
            logger.info("出庫依頼変更、出庫ID：" + shipment_plan_id + "、削除の商品ID：" + delete_data);
            deleteProductIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(delete_data);
        }

        // 处理取消后订单状态变化以及引当数逻辑
        for (Tw201_shipment_detail canceledTw201 : list) {

            // 从提交参数中解析 修改后的出庫依頼数 和 修改前的出庫依頼数
            int productPlanCnt = 0, oldPlanCnt = 0;
            // 编辑
            if (!deleteFlg) {
                // set子商品拆分
                JSONArray items = shipmentDetailService.setProductSplice(jsonParam.getJSONArray("items"));
                for (int i = 0; i < items.size(); i++) {
                    if (items.getJSONObject(i).getString("product_id").equals(canceledTw201.getProduct_id())) {
                        productPlanCnt = items.getJSONObject(i).getInteger("product_plan_cnt");
                        if (!StringTools.isNullOrEmpty(items.getJSONObject(i).getInteger("old_plan_cnt"))) {
                            oldPlanCnt = items.getJSONObject(i).getInteger("old_plan_cnt");
                        }
                        logger.info("出庫依頼変更、出庫ID：" + shipment_plan_id + "、商品ID：" + canceledTw201.getProduct_id()
                            + "、変更前：" + oldPlanCnt + "件、変更後：" + productPlanCnt + "件");
                        break;
                    }
                }
            }

            // 此处if往下是引当数变更逻辑（3个逻辑：201引当数变更，201引当状态变更，200出库状态变更）
            // 判断数据是否为空 如果为空则跳过该数据
            if (StringTools.isNullOrEmpty(canceledTw201.getReserve_cnt()) || canceledTw201.getReserve_cnt() <= 0) {
                logger.info("出庫依頼変更、出庫ID：" + shipment_plan_id + "、商品ID：" + canceledTw201.getProduct_id() + "、引当数0件");
                continue;
            }

            // 如果不为0则需要进行回滚处理
            // num记录需要回滚的商品数量
            int num = 0;
            if (deleteFlg) {
                // 如果是删除订单，则释放订单的所有，保留
                num = canceledTw201.getReserve_cnt();
            } else {
                // 商品编辑回滚
                if (deleteProductIdList.contains(canceledTw201.getProduct_id())) {
                    // 释放部分为 删除删除商品的引当数
                    num = canceledTw201.getReserve_cnt();
                } else {
                    // 如果是编辑订单，则释放部分
                    num = oldPlanCnt - productPlanCnt;
                }
            }
            // 释放订单数量小于0时结束本次循环
            if (num <= 0) {
                continue;
            }
            List<Tw200_shipment> shipmentReserve = new ArrayList<Tw200_shipment>();
            // 获取所有被补充的数据
            try {
                shipmentReserve = shipmentsDao.shipmentReserve(client_id, canceledTw201.getProduct_id(),
                    canceledTw201.getShipment_plan_id());
            } catch (Exception e) {
                logger.error("获取所有被补充的数据时出错,商品ID为：" + canceledTw201.getProduct_id());
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            // 如果没有则跳过本次循环
            if (StringTools.isNullOrEmpty(shipmentReserve) || shipmentReserve.size() == 0) {
                continue;
            }
            logger.info("出庫依頼変更、出庫ID：" + shipment_plan_id + "、補足依頼件数：" + shipmentReserve.size());

            // 如果有则需要逻辑判断(开始补充引当数逻辑)
            for (Tw200_shipment shipment : shipmentReserve) {
                // 本删除的商品已经全部补充给别的依赖 不需要再进行循环
                if (num <= 0) {
                    break;
                }
                try {
                    // 出库取消后补充其他订单的逻辑
                    num = toFullOtherShipments(shipment, num, client_id, canceledTw201, null);
                } catch (Exception e) {
                    logger.error("出库取消补充时异常！被取消的订单ID：" + shipment.getShipment_plan_id());
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                try {
                    // 重新获取tw200数据判断是否需要变更出库状态
                    checkShipmentStatus(client_id, shipment.getShipment_plan_id(), shipment.getShipment_status());
                } catch (Exception e) {
                    logger.error("判断是否需要变更出库状态时异常！被判断补充的订单ID：" + shipment.getShipment_plan_id());
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        // 删除订单信息
        if (deleteFlg) {
            // 出庫管理テーブル削除
            shipmentsDao.deleteShipments(client_id, shipment_plan_id, upd_date, upd_usr);
            // 出庫明細テーブル
            shipmentDetailService.deleteShipmentDetail(client_id, shipment_plan_id, upd_date, upd_usr);
            String[] plan_id = {
                shipment_plan_id
            };
            // 顧客別作業履歴新增
            // 出庫依頼取消
            String operation_cd = "09";
            customerHistoryService.insertCustomerHistory(servletRequest, plan_id, operation_cd, null);
        }

        // 删除编辑时删除的商品
        List<String> deleteIdList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(jsonParam.getString("delete_data")) && !deleteFlg) {
            String deleteData = jsonParam.getString("delete_data");
            boolean contains = deleteData.contains(",");
            List<String> productIdList = new ArrayList<>();

            if (contains) {
                productIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(deleteData);
            } else {
                productIdList.add(deleteData);
            }

            for (int i = 0; i < productIdList.size(); i++) {
                Integer set_sub_id = null;
                String product_id = productIdList.get(i);

                if (productIdList.get(i).contains("_")) {
                    String sub_id =
                        Splitter.on("_").omitEmptyStrings().trimResults().splitToList(productIdList.get(i)).get(1);
                    product_id =
                        Splitter.on("_").omitEmptyStrings().trimResults().splitToList(productIdList.get(i)).get(0);
                    set_sub_id = Integer.parseInt(sub_id);
                }
                deleteIdList.add(product_id);
                shipmentDetailDao.deleteShipmentProduct(client_id, shipment_plan_id, product_id, upd_date, upd_usr,
                    set_sub_id);
            }
        }

        // 处理在库数变更及删除逻辑逻辑
        for (Tw201_shipment_detail val : list) {
            // 更新在库表
            JSONObject stockJson = new JSONObject();
            stockJson.put("warehouse_cd", val.getWarehouse_cd());
            stockJson.put("client_id", client_id);
            stockJson.put("product_id", val.getProduct_id());
            Tw300_stock stock = stockDao.getStockInfoById(stockJson);
            if (StringTools.isNullOrEmpty(stock)) {
                continue;
            }

            // 编辑时回滚在库表数据
            // if (!deleteFlg && (deleteIdList.size() == 0 || !deleteIdList.contains(val.getProduct_id()))) {
            // continue;
            // }

            // 减去在库表中该商品依赖数
            Integer requesting_cnt = stock.getRequesting_cnt() - val.getProduct_plan_cnt();
            // 加上在库表中该商品理論在庫数
            Integer available_cnt = stock.getAvailable_cnt() + val.getProduct_plan_cnt();
            // 回滚在库表数据
            stockDao.updateStockRequestingCnt(val.getClient_id(), val.getProduct_id(), Math.max(requesting_cnt, 0),
                available_cnt,
                upd_usr, upd_date);
        }

        return 0;
    }

    /**
     * @param client_id : 店铺Id
     * @Description: 出庫依頼ID生成
     * @return: String
     * @Date: 2020/5/14
     */
    @Override
    public String setShipmentPlanId(String client_id) {
        int i = CommonUtils.getRandomNum(100000000);
        String timeMillis = String.valueOf(i + (System.currentTimeMillis() / 1000L));

        // 获取随机数
        Random random = new Random();
        String ranNum = "";
        for (int j = 0; j < 4; j++) {
            ranNum += random.nextInt(100);
        }

        Long newId = Long.valueOf(timeMillis) + (Long.valueOf(ranNum) * random.nextInt(100));
        return client_id + newId.toString();
    }

    /**
     * @Description: 名称区分マスタ
     * @Param: 名称区分, 名称コード, 倉庫コード
     * @return: List
     * @Date: 2020/5/29
     */
    @Override
    public List<Ms009_definition> getDefinitionList(String warehouse_cd, Integer[] sys_kind, String sys_cd) {
        if (sys_kind == null && warehouse_cd == null) {
            return null;
        }
        return definitionDao.getDefinitionList(warehouse_cd, sys_kind, sys_cd);
    }

    /**
     * @param: jsonObject
     * @param: request
     * @description: 出库依赖PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/10
     */
    @Override
    public JSONObject getShipmentsPDF(JSONObject jsonObject, ServletRequest servletRequest, Integer flg,
        String startTime, String endTime) {

        String client_id = jsonObject.getString("client_id");

        // 判断是否为修改
        String updateStatus = jsonObject.getString("updateStatus");
        String shipment_plan_id = jsonObject.getString("shipment_plan_id");
        // 获取到出荷指示特記事項
        String instructionsList = jsonObject.getString("delivery_instructions");
        if (!StringTools.isNullOrEmpty(instructionsList)) {
            List<String> splitToList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(instructionsList);
            // todo macro 出荷特记事项追加
            for (int i = 0; i < splitToList.size(); i++) {
                if (i == 0) {
                    jsonObject.put("instructions_special_notes", splitToList.get(i));
                } else {
                    jsonObject.put("bikou" + i, splitToList.get(i));
                }
            }
            jsonObject.put("bikou_flg", 1);
        } else if (StringTools.isNullOrEmpty(jsonObject.getString("order_flg"))) {
            jsonObject.put("bikou_flg", 0);
        }
        JSONObject json = null;
        if (StringTools.isNullOrEmpty(updateStatus)) {
            // 如果不是修改， 则为出库依赖新规，获取最大出库ID
            if (StringTools.isNullOrEmpty(shipment_plan_id)) {
                shipment_plan_id = this.setShipmentPlanId(client_id);
            }
            // 判断是否为 出库依赖 只查看PDF
            String previewFlg = jsonObject.getString("preview_flg");
            if (!StringTools.isNullOrEmpty(previewFlg)) {
                if (Integer.parseInt(previewFlg) == 1) {
                    shipment_plan_id = this.setShipmentPlanId(client_id);
                    getSenderJson(jsonObject);
                }
            }

            Integer shipment_status = 0;
            if (!StringTools.isNullOrEmpty(jsonObject.getInteger("status"))) {
                shipment_status = jsonObject.getInteger("status");
            }
            Tw200_shipment shipmentsList = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
            if (!StringTools.isNullOrEmpty(shipmentsList)) {
                boolean cancle_flg = false;
                if (shipmentsList.getDel_flg() == 1) {
                    cancle_flg = true;
                }
                List<Tw201_shipment_detail> shipmentDetails =
                    shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, cancle_flg);
                shipmentDetails = setShipmentProductDetail(shipmentDetails, client_id, "0", false);
                shipmentsList.setTw201_shipment_detail(shipmentDetails);
                json = OrderServiceImpl.getShipmentJsonDate(jsonObject, shipmentsList);
            } else {
                getSenderJson(jsonObject);
                json = jsonObject;
            }
        } else {
            getSenderJson(jsonObject);
            json = jsonObject;
        }

        JSONObject jsonObjectPath =
            PdfTools.creatPath(client_id, shipment_plan_id, pathProps.getRoot(), pathProps.getStore());
        String codePath = jsonObjectPath.getString("codePath");
        String pdfPath = jsonObjectPath.getString("pdfPath");

        String sponsor_id = jsonObject.getString("sponsor_id");
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false, sponsor_id);
        Ms012_sponsor_master ms012_sponsor_master = sponsorList.get(0);
        json.put("sponsor_company", ms012_sponsor_master.getCompany());
        json.put("name", ms012_sponsor_master.getName());
        json.put("postcode", ms012_sponsor_master.getPostcode());
        json.put("prefecture", ms012_sponsor_master.getPrefecture());
        json.put("address1", ms012_sponsor_master.getAddress1());
        json.put("address2", ms012_sponsor_master.getAddress2());
        Integer contact = ms012_sponsor_master.getContact();
        String value = CommonUtils.getContact(contact);
        json.put("contact", value);
        json.put("sponsorEmail", ms012_sponsor_master.getEmail());
        json.put("sponsorFax", ms012_sponsor_master.getFax());
        json.put("sponsorPhone", ms012_sponsor_master.getPhone());
        if (contact == 1) {
            json.put("contact_info", ms012_sponsor_master.getEmail());
        } else {
            json.put("contact_info", ms012_sponsor_master.getPhone());
        }
        String detail_logo = ms012_sponsor_master.getDetail_logo();
        String logoPath = pathProps.getRoot() + detail_logo;
        json.put("detail_logo", logoPath);
        json.put("payment_method_name", "");
        if (!StringTools.isNullOrEmpty(json.getString("payment_method"))) {
            String payment_method = deliveryDao.getPayById(Integer.valueOf(json.getString("payment_method")));
            if (!StringTools.isNullOrEmpty(payment_method)) {
                json.put("payment_method_name", payment_method);
            }
        }
        if (!StringTools.isNullOrEmpty(jsonObject.getString("message"))) {
            json.put("detail_message", jsonObject.getString("message"));
        } else {
            json.put("detail_message", "");
        }
        Mc105_product_setting productSetting = productSettingService.getProductSetting(client_id, null);
        Integer version = productSetting.getVersion();
        Integer tax = productSetting.getTax();
        Integer accordion = productSetting.getAccordion();
        String product_tax = (tax == 1) ? "税抜" : "税込";
        json.put("product_tax", product_tax);
        // 拼接セット商品
        JSONArray items = json.getJSONArray("items");
        // 计算商品消费税
        calculateProductTax(json, tax, accordion);
        JSONArray objects = splicingSetProduct(items, 0);
        json.put("items",
            ShipmentServiceImpl.jsonArraySort(objects.toString(), "product_id", "locationName", null, true));

        // flg == 1 生成包含价格的PDF
        if (flg == 0) {
            try {
                // 新版
                PdfTools.createNewShipmentsPDF(json, codePath, pdfPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                // 新版
                PdfTools.createNewShipmentPricePdf(json, codePath, pdfPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return CommonUtils.success(jsonObjectPath);
    }

    /**
     * @Param: items
     * @description: 拼接セット商品
     * @return: com.alibaba.fastjson.JSONArray
     * @date: 2020/9/10
     */
    @Override
    public JSONArray splicingSetProduct(JSONArray items, Integer status) {
        JSONArray jsonArray = new JSONArray();
        HashMap<String, JSONObject> map = new HashMap<>();
        List<Integer> setId = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject product = items.getJSONObject(i);
            Integer set_sub_id = product.getInteger("set_sub_id");

            // Set 商品
            if (!StringTools.isNullOrEmpty(set_sub_id) && set_sub_id > 0) {
                if (setId.contains(set_sub_id)) {
                    continue;
                }
                setId.add(set_sub_id);
                List<Mc100_product> setProductList =
                    productDao.getSetProductInfo(product.getString("client_id"), set_sub_id);
                if (StringTools.isNullOrEmpty(setProductList) || setProductList.size() == 0) {
                    continue;
                }
                Mc100_product mc100Product = setProductList.get(0);
                // 修改set商品商品 商品code、商品名、数量
                product.put("name", mc100Product.getName());
                product.put("code", mc100Product.getCode());

                // 编辑页面和详细页面参数不一致，做判断
                if (!StringTools.isNullOrEmpty(product.getString("set_cnt"))) {
                    product.put("product_plan_cnt", product.getString("set_cnt"));
                }

                product.put("UID", product.getString("client_id") + "-" + mc100Product.getProduct_id());
                jsonArray.add(product);
            } else {
                product.put("UID", product.getString("client_id") + "-" + product.getString("product_id"));
                jsonArray.add(product);
            }
        }
        return jsonArray;
    }

    private void getSenderJson(JSONObject jsonObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String order_datetime = "";
        if (!StringTools.isNullOrEmpty(jsonObject.getString("order_datetime"))) {
            String orderDatetime = jsonObject.getString("order_datetime");
            order_datetime = orderDatetime.substring(0, 10);
        }
        jsonObject.put("order_datetime", order_datetime);
        JSONObject sender = jsonObject.getJSONObject("sender");
        jsonObject.put("delivery_id", sender.getString("delivery_id"));
        jsonObject.put("form", sender.getString("form"));
        jsonObject.put("postcode", sender.getString("postcode"));
        jsonObject.put("prefecture", sender.getString("prefecture"));
        jsonObject.put("address1", sender.getString("address1"));
        jsonObject.put("address2", sender.getString("address2"));
        jsonObject.put("company", sender.getString("company"));
        jsonObject.put("surname", sender.getString("surname"));
        jsonObject.put("division", sender.getString("division"));
        jsonObject.put("phone", sender.getString("phone"));
        jsonObject.put("email", sender.getString("email"));
    }

    /**
     * @param: jsonObject
     * @description:出庫依赖CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    @Override
    @Transactional
    public JSONObject shipmentsCsvUpload(HttpServletRequest req, MultipartFile file, String client_id,
        ServletRequest servletRequest) {
        try {
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            // a获取当前项目的真实路径
            String path = req.getServletContext().getRealPath("");
            String realPath = (String) path.subSequence(0, path.length() - 7);
            // a获取当前的年份+月份
            Calendar date = Calendar.getInstance();
            int year = Integer.valueOf(date.get(Calendar.YEAR));
            int month = date.get(Calendar.MONTH) + 1;
            String datePath = year + "" + month;
            // a拼接图片保存路径
            String destFileName = realPath + "resources" + File.separator + "static" + File.separator + "csv"
                + File.separator + datePath + File.separator + fileName;
            // a第一次运行的时候创建文件夹
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            // a把浏览器上传的文件复制到目标路径
            file.transferTo(destFile);
            // 验证编码格式
            if (!CommonUtils.determineEncoding(destFile.toURI().toURL(), new String[] {
                "SHIFT_JIS"
            })) {
                throw new PlBadRequestException("ご指定のCSVファイルが、取り扱いできる形式（SHIFT-JIS）ではありません。");
            }
            // a错误信息list
            List<String> list = new ArrayList<String>();
            // a读取上传的CSV文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            // a判断字符串是否为数字的正则
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            // a邮编正则/^\d{3}-\d{4}$/
            Pattern post = Pattern.compile("^\\d{3}-\\d{4}$");
            // a日期验证/^(0?[1-9]|1[0-2])/((0?[1-9])|((1|2)[0-9])|30|31)/\d{4}$/
            Pattern dateCheck = Pattern.compile("^(0?[1-9]|1[0-2])/((0?[1-9])|((1|2)[0-9])|30|31)/\\d{4}$");
            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
            int num = 0;
            int count = 0;
            boolean flag = true;
            while (csvReader.readRecord()) {
                num++;
                if (num == 1) {
                    if (!"商品ID,商品名,商品コード,軽減税率適用商品,出庫依頼数,単価,出庫識別番号,配送料,手数料,割引額,配送先郵便番号,配送先都道府県,配送先住所,配送先マンション・ビル名,配送先氏名,配送先会社名,配送先電話番号,配送先連絡メール,配送便指定,配送会社指定,出荷希望日,お届け希望日,希望時間帯,緩衝材指定,緩衝材,ギフトラッピング単位,ラッピングタイプ,贈り主氏名,同梱指定,不在時宅配ボックス,割れ物注意,代引き指定,代金引換総計,代金引換消費税,品名,明細書メッセージ,発送通知メッセージ,明細書の同梱,明細書の金額印字,ご依頼主郵便番号,ご依頼主都道府県,ご依頼主住所,ご依頼主マンション・ビル名,ご依頼主氏名,ご依頼主会社名,ご依頼主部署名,お問い合わせ先,ご依頼主電話番号,出荷指示書 特記事項,送り状 特記事項,"
                        .equals(
                            csvReader.getRawRecord())
                        && !"商品ID,商品名,商品コード,軽減税率適用商品,出庫依頼数,単価,出庫識別番号,配送料,手数料,割引額,配送先郵便番号,配送先都道府県,配送先住所,配送先マンション・ビル名,配送先氏名,配送先会社名,配送先電話番号,配送先連絡メール,配送便指定,配送会社指定,出荷希望日,お届け希望日,希望時間帯,緩衝材指定,緩衝材,ギフトラッピング単位,ラッピングタイプ,贈り主氏名,同梱指定,不在時宅配ボックス,割れ物注意,代引き指定,代金引換総計,代金引換消費税,品名,明細書メッセージ,発送通知メッセージ,明細書の同梱,明細書の金額印字,ご依頼主郵便番号,ご依頼主都道府県,ご依頼主住所,ご依頼主マンション・ビル名,ご依頼主氏名,ご依頼主会社名,ご依頼主部署名,お問い合わせ先,ご依頼主電話番号,出荷指示書 特記事項,送り状 特記事項"
                            .equals(
                                csvReader.getRawRecord())) {
                        throw new PlBadRequestException("項目名称に不備があります。");
                    }
                }
                // a包含非数据的前两行为1002
                if (num > 1002) {
                    throw new PlBadRequestException("一度に登録できるデータは最大1000件です。");
                }
            }
            // a关闭csvReader
            csvReader.close();
            num = 0;
            // a验证出库信息
            InputStreamReader isr1 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                count++;
                num++;
                // // a跳过第二行说明行
                // if (num == 1) {
                // continue;
                // }
                String tmp = csvReader1.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                int k = 0;
                // a商品ID不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 商品IDは空にしてはいけません。");
                    flag = false;
                }
                // a商品ID不存在
                k++;
                // a商品名
                k++;
                // a商品コード
                k++;
                // a軽減税率適用商品不能为空
                if (params[k] == null || "".equals(params[k]) || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 軽減税率適用商品は空にしてはいけません。");
                    flag = false;
                }
                // a軽減税率適用商品只能为0或1
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 軽減税率適用商品なら、１をご記入してください。逆に０をご記入してください。");
                    flag = false;
                }
                k++;
                // a出庫依頼数不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 出庫依頼数は空にしてはいけません。");
                    flag = false;
                }
                // a出庫依頼数只能为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 出庫依頼数には数字をご入力ください。");
                    flag = false;
                }
                // a出庫依頼数最大8位
                if (params[k].length() > 8) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 出庫依頼数の桁数は8に限る。");
                    flag = false;
                }
                k++;
                // a単価不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 単価は空にしてはいけません。");
                    flag = false;
                }
                // a単価只能为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 単価には数字をご入力ください。");
                    flag = false;
                }
                // a単価不能超过8位
                if (params[k].length() > 8) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 単価の桁数は8に限る。");
                    flag = false;
                }
                k++;
                // a出庫識別番号
                k++;
                // a配送料必须为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送料には数字をご入力ください。");
                    flag = false;
                }
                k++;
                // a手数料必须为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 手数料には数字をご入力ください。");
                    flag = false;
                }
                k++;
                // a割引額必须为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 割引額には数字をご入力ください。");
                    flag = false;
                }
                k++;
                // a配送先郵便番号不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送先郵便番号は空にしてはいけません。");
                    flag = false;
                }
                // a配送先郵便番号格式000-0000验证
                if (!post.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送先郵便番号の形式不正。例:000-0000。");
                    flag = false;
                }
                k++;
                // a配送先都道府県不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送先都道府県は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // a配送先住所不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送先住所は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // a配送先マンション・ビル名
                k++;
                // a配送先氏名不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送先氏名は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // a配送先会社名
                k++;
                // a配送先電話番号
                k++;
                // a配送先連絡メール
                k++;
                // a配送便指定只能为0或者1
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送便指定ボックスは0または1をご入力ください。");
                    flag = false;
                }
                k++;
                // a如果配送便指定为ポスト便，则不能填写配送会社指定
                if ("0".equals(params[k - 1])) {
                    if (params[k] != null && !"".equals(params[k])) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送便が０（ポスト便）を指定した時、配送会社に関する情報はご記入ないでください。");
                        flag = false;
                    }
                }
                // a配送会社指定只能为1,2,3
                if ("1".equals(params[k - 1])) {
                    if (!"1".equals(params[k]) && !"2".equals(params[k]) && !"3".equals(params[k])) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送会社について「１：佐川急便２：日本郵便３：ヤマト運輸」でしか指定できません。");
                        flag = false;
                    }
                }
                k++;
                // a出荷希望日校验
                if (!dateCheck.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 出荷の期日の形式不正。例:12/25/2020。");
                    flag = false;
                }
                k++;
                // aお届け希望日校验
                System.err.println(params[k - 3]);
                if ("1".equals(params[k - 3])) {
                    if (params[k] != null && !"".equals(params[k])) {
                        if (!dateCheck.matcher(params[k]).matches()) {
                            list.add("[" + (num + 1) + "行目] 商品" + (num) + ": お届けの期日の形式不正。例:12/25/2020。");
                            flag = false;
                        }
                    }
                } else {
                    if (params[k] != null && !"".equals(params[k])) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 配送便が０（ポスト便）を指定した時、お届け希望日に関する情報はご記入ないでください。");
                        flag = false;
                    }
                }
                k++;
                // a希望時間帯
                k++;
                // a緩衝材指定
                if (!"0".equals(params[k]) && !"1".equals(params[k]) && !"2".equals(params[k])
                    && !"".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 緩衝材指定について「0：なし1：注文単位2：商品単位」でしか指定できません。");
                    flag = false;
                }
                k++;
                // a緩衝材
                k++;
                // aギフトラッピング単位
                if (!"0".equals(params[k]) && !"1".equals(params[k]) && !"2".equals(params[k])
                    && !"".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ギフトラッピング単位について「0：なし1：注文単位2：商品単位」でしか指定できません。");
                    flag = false;
                }
                k++;
                // aラッピングタイプ
                k++;
                // a贈り主氏名
                k++;
                // a同梱指定
                k++;
                // a不在時宅配ボックス只能为0或者1
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 不在時宅配ボックスは0または1をご入力ください。");
                    flag = false;
                }
                k++;
                // a割れ物注意只能为0或者1
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 割れ物注意は0または1をご入力ください。");
                    flag = false;
                }
                k++;
                // a代引き指定
                if (!"1".equals(params[k]) && !"0".equals(params[k]) && !"".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 代引き指定は0または1をご入力ください。");
                    flag = false;
                }
                k++;
                // a代金引換総計
                if ("1".equals(params[k - 1])) {
                    if (!pattern.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 代金引換総計は数字をご入力ください。");
                        flag = false;
                    }
                } else {
                    if (params[k] != null && !"".equals(params[k])) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 代引き指定を指定した時、代金引換総計に関する情報はご記入ないでください。");
                        flag = false;
                    }
                }
                k++;
                // a代金引換消費税
                if ("1".equals(params[k - 2])) {
                    if (!pattern.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 代金引換消費税は数字をご入力ください。");
                        flag = false;
                    }
                } else {
                    if (params[k] != null && !"".equals(params[k])) {
                        list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 代引き指定を指定した時、代金引換消費税に関する情報はご記入ないでください。");
                        flag = false;
                    }
                }
                k++;
                // a品名
                k++;
                // a明細書メッセージ
                k++;
                // a発送通知メッセージ
                k++;
                // a明細書の同梱
                if (!"同梱する".equals(params[k]) && !"同梱しない".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 明細書の同梱は同梱するまたは同梱しないをご入力ください。");
                    flag = false;
                }
                k++;
                // a明細書の金額印字只能是0或者1且不为空
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": 明細書の金額印字は0または1をご入力ください。");
                    flag = false;
                }
                k++;
                // aご依頼主郵便番号不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主郵便番号は空にしてはいけません。");
                    flag = false;
                }
                // aご依頼主郵便番号格式000-0000验证
                if (!post.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主郵便番号の形式不正。例:000-0000。");
                    flag = false;
                }
                k++;
                // aご依頼主都道府県不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主都道府県は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // aご依頼主住所不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主住所は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // aご依頼主マンション・ビル名
                k++;
                // aご依頼主氏名不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主氏名は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // aご依頼主会社名
                k++;
                // aご依頼主部署名
                k++;
                // aお問い合わせ先
                if (!"1".equals(params[k]) && !"0".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": お問い合わせ先は0[電話番号]または1[メールアドレス]をご入力ください。");
                    flag = false;
                }
                k++;
                // aご依頼主電話番号不能为空
                if (params[k] == null || params[k] == "" || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] 商品" + (num) + ": ご依頼主電話番号は空にしてはいけません。");
                    flag = false;
                }
                k++;
                // a出荷指示書 特記事項
                k++;
                // a送り状 特記事項

            }
            // a关闭csvReader
            csvReader1.close();
            num = 0;
            // a如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new PlBadRequestException(json);
            }
            // a创建一个长度和CSV数据量相等的数组
            JSONObject items[] = new JSONObject[count];
            JSONObject jsonBig = new JSONObject();
            JSONObject sender = new JSONObject();
            JSONObject delivery_options = new JSONObject();
            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            int index = 0;
            int tax = 0;
            Ms012_sponsor_master ms012 = new Ms012_sponsor_master();
            while (csvReader2.readRecord()) {
                // // a跳过CSV第二行注释行
                // if (num == 0) {
                // num++;
                // continue;
                // }
                String tmp = csvReader2.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                JSONObject jSONObject = new JSONObject();
                int k = 0;
                // a商品ID
                jSONObject.put("product_id", params[k]);
                k++;
                // a商品名
                jSONObject.put("name", params[k]);
                k++;
                // a商品コード
                jSONObject.put("code", params[k]);
                k++;
                // a軽減税率適用商品
                jSONObject.put("is_reduced_tax", params[k]);
                int is_reduced_tax = Integer.valueOf(params[k]);
                k++;
                // a出庫依頼数
                jSONObject.put("product_plan_cnt", params[k]);
                int cnt = Integer.parseInt(params[k]);
                k++;
                // a単価
                jSONObject.put("unit_price", params[k]);
                // a総額
                int unit = Integer.parseInt(params[k]);
                jSONObject.put("price", cnt * unit);
                jsonBig.put("subtotal_amount", cnt * unit);
                if (is_reduced_tax == 0) {
                    tax += cnt * unit * 0.08;
                } else {
                    tax += cnt * unit * 0.1;
                }
                k++;
                // a出庫識別番号
                jsonBig.put("identifier", params[k]);
                k++;
                // a配送料
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                jsonBig.put("delivery_charge", params[k]);
                int delivery_charge = Integer.valueOf(params[k]);
                k++;
                // a手数料
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                jsonBig.put("handling_charge", params[k]);
                int handling_charge = Integer.valueOf(params[k]);
                k++;
                // a割引額
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                int discount_amount = Integer.valueOf(params[k]);
                jsonBig.put("discount_amount", params[k]);
                jsonBig.put("total_amount", cnt * unit + handling_charge + delivery_charge - discount_amount);
                k++;
                // a配送先郵便番号
                sender.put("postcode", params[k]);
                k++;
                // a配送先都道府県
                sender.put("prefecture", params[k]);
                k++;
                // a配送先住所
                sender.put("address1", params[k]);
                k++;
                // a配送先マンション・ビル名
                sender.put("address2", params[k]);
                k++;
                // a配送先氏名
                sender.put("surname", params[k]);
                k++;
                // a配送先会社名
                sender.put("company", params[k]);
                k++;
                // a配送先電話番号
                sender.put("phone", params[k]);
                k++;
                // a配送先連絡メール
                sender.put("email", params[k]);
                k++;
                // a配送便指定
                if ("0".equals(params[k])) {
                    jsonBig.put("delivery_method", "ポスト便（宅配便スピード）");
                }
                if ("1".equals(params[k])) {
                    jsonBig.put("delivery_method", "宅配便");
                }
                k++;
                // a配送会社指定
                if ("1".equals(params[k])) {
                    jsonBig.put("delivery_carrier", "佐川急便");
                } else if ("2".equals(params[k])) {
                    jsonBig.put("delivery_carrier", "日本郵便");
                } else if ("3".equals(params[k])) {
                    jsonBig.put("delivery_carrier", "ヤマト運輸");
                } else {
                    jsonBig.put("delivery_carrier", "日本郵便");
                }
                k++;
                // a出荷希望日
                if (params[k] != null && !"".equals(params[k])) {
                    try {
                        Date d = formatter1.parse(params[k]);
                        jsonBig.put("shipping_date", formatter2.format(d));
                    } catch (ParseException e) {
                        throw new PlValidationErrorException("%s: 期日の形式不正。例:12/25/2020。", "出荷希望日");
                    }
                }
                k++;
                // aお届け希望日
                if (params[k] != null && !"".equals(params[k])) {
                    try {
                        Date d = formatter1.parse(params[k]);
                        jsonBig.put("delivery_plan_date", formatter2.format(d));
                    } catch (ParseException e) {
                        throw new PlValidationErrorException("%s: 期日の形式不正。例:12/25/2020。", "お届け希望日");
                    }
                }
                k++;
                // a希望時間帯
                jsonBig.put("delivery_time_slot", params[k]);
                k++;
                // a緩衝材指定
                if (params[k] == null || "".equals(params[k])) {
                    jsonBig.put("cushioning_unit", 0);
                } else {
                    jsonBig.put("cushioning_unit", params[k]);
                }
                k++;
                // a緩衝材
                jSONObject.put("cushioning_type", params[k]);
                k++;
                // aギフトラッピング単位
                if (params[k] == null || "".equals(params[k])) {
                    jsonBig.put("gift_wrapping_unit", 0);
                    jSONObject.put("gift_wrapping_unit", 0);
                } else {
                    jsonBig.put("gift_wrapping_unit", params[k]);
                    jSONObject.put("gift_wrapping_unit", params[k]);
                }
                k++;
                // aラッピングタイプ
                jSONObject.put("gift_wrapping_type", params[k]);
                k++;
                // a贈り主氏名
                jsonBig.put("gift_sender_name", params[k]);
                k++;
                // a同梱指定
                jsonBig.put("bundled_items", params[k]);
                k++;
                // a不在時宅配ボックス
                delivery_options.put("box_delivery", params[k]);
                k++;
                // a割れ物注意
                delivery_options.put("fragile_item", params[k]);
                k++;
                // a代引き指定
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                jsonBig.put("cash_on_delivery", params[k]);
                k++;
                // a代金引換総計
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                jsonBig.put("total_for_cash_on_delivery", params[k]);
                k++;
                // a代金引換消費税
                if ("".equals(params[k]) || params[k] == null) {
                    params[k] = "0";
                }
                jsonBig.put("tax_for_cash_on_delivery", params[k]);
                k++;
                // a品名
                jsonBig.put("label_note", params[k]);
                k++;
                // a明細書メッセージ
                jsonBig.put("message", params[k]);
                k++;
                // a発送通知メッセージ

                k++;
                // a明細書の同梱
                jsonBig.put("delivery_note_type", params[k]);
                k++;
                // a明細書の金額印字
                jsonBig.put("price_on_delivery_note", params[k]);
                k++;
                // aご依頼主郵便番号
                ms012.setPostcode(params[k]);
                k++;
                // aご依頼主都道府県
                ms012.setPrefecture(params[k]);
                k++;
                // aご依頼主住所
                ms012.setAddress1(params[k]);
                k++;
                // aご依頼主マンション・ビル名
                ms012.setAddress2(params[k]);
                k++;
                // aご依頼主氏名
                ms012.setName(params[k]);
                k++;
                // aご依頼主会社名
                ms012.setCompany(params[k]);
                k++;
                // aご依頼主部署名
                ms012.setDivision(params[k]);
                k++;
                // aお問い合わせ先
                ms012.setContact(Integer.valueOf(params[k]));
                k++;
                // aご依頼主電話番号
                ms012.setPhone(params[k]);
                k++;
                // a出荷指示書 特記事項
                k++;
                // a送り状 特記事項
                items[index] = jSONObject;
                index++;
            }
            // a税金
            jsonBig.put("tax", tax);
            // a检查依赖主信息是否存在
            ms012.setClient_id(client_id);
            Ms012_sponsor_master checked = new Ms012_sponsor_master();
            checked = settingDao.checkSponsorExist(ms012);
            // a如果存在依赖主则写入id，如果不存在则新规依赖主并写入id
            if (!StringTools.isNullOrEmpty(checked)) {
                jsonBig.put("sponsor_id", checked.getSponsor_id());
            } else {
                ms012.setSponsor_id(String.valueOf(settingDao.getMaxSponsorId() + 1));
                settingDao.createDeliveryList(ms012);
                jsonBig.put("sponsor_id", settingDao.getMaxSponsorId());
            }
            jsonBig.put("items", items);
            jsonBig.put("delivery_options", delivery_options);
            jsonBig.put("sender", sender);
            String shipment_plan_id = this.setShipmentPlanId(client_id);
            jsonBig.put("shipment_plan_id", shipment_plan_id);
            jsonBig.put("client_id", client_id);
            jsonBig.put("warehouse_cd", commonFunctionDao.getWarehouseIdByClientId(client_id));
            jsonBig.put("shipment_status", 2);
            Integer result = 0;
            result = this.setShipments(jsonBig, true, req);
            if (result <= 0) {
                return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            shipmentDetailService.setShipmentDetail(jsonBig, true, req);
            if (result <= 0) {
                return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);

        } catch (IOException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);
        }
        return CommonUtils.success();
    }

    /**
     * @Param: file
     * @param: shipment_plan_id
     * @Param: client_id
     * @description: 梱包作業画像を添付上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/27
     */
    @Override
    public JSONObject uploadConfirmPdf(MultipartFile[] files, String[] shipment_plan_id, String client_id) {
        if (files.length < 1) {
            logger.error("アップロードしたファイルは空です");
        }
        String nowTime = DateUtils.getDateDay();

        // 图片路径
        String uploadPath = pathProps.getStore() + client_id + "/shipment/approval/" + nowTime + "/";
        for (MultipartFile file : files) {
            String filePath = CommonUtils.uploadFile(file, pathProps.getRoot() + uploadPath, uploadPath);
            if (shipment_plan_id != null && shipment_plan_id.length > 0) {
                try {
                    shipmentsDao.setConfirmPdf(shipment_plan_id, filePath);
                } catch (Exception e) {
                    logger.error("PDF添付上传失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return CommonUtils.success();
    }

    /**
     * @Param: file : 添付ファイルについて
     * @param: client_id : 店铺Id
     * @param: shipments_plan_id ： 出库依赖Id
     * @description: 添付ファイルについて
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/7
     */
    @Override
    public JSONObject uploadDeliveryFile(MultipartFile[] files, String shipment_plan_id, String client_id) {
        if (files.length < 1) {
            // shipmentsDao.setDeliveryFile(shipment_plan_id, null);
            logger.error("アップロードしたファイルは空です");
        }
        String nowTime = DateUtils.getDateDay();
        // 文件路径
        String uploadPath =
            pathProps.getRoot() + pathProps.getStore() + client_id + "/shipment/delivery_file/" + nowTime + "/";
        File uploadDirectory = new File(uploadPath);
        if (uploadDirectory.exists()) {
            if (!uploadDirectory.isDirectory()) {
                uploadDirectory.delete();
            }
        } else {
            uploadDirectory.mkdirs();
        }
        String savePath = null;
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            savePath = pathProps.getStore() + client_id + "/shipment/delivery_file/" + nowTime + "/" + fileName;
            String filePath = uploadPath + fileName;
            File outFile = new File(filePath);
            // if (!StringTools.isNullOrEmpty(shipment_plan_id)) {
            // shipmentsDao.setDeliveryFile(shipment_plan_id, savePath);
            // }
            // 拷贝文件到输出文件对象
            try {
                file.transferTo(outFile);
            } catch (Exception e) {
                logger.error("添付ファイルについて上传失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("savePath", savePath);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        return CommonUtils.success(jsonObject);
    }

    /**
     * @Param: file : 添付ファイルについて路径保存
     * @param: shipments_plan_id ： 出库依赖Id
     * @description: 添付ファイルについて
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/03/15
     */
    @Override
    public Integer saveDeliveryFilePath(JSONObject jsonObject) {
        JSONObject jsonParam = new JSONObject();
        JSONArray pathList = jsonObject.getJSONArray("path_list");
        jsonParam.put("shipment_plan_id", jsonObject.getString("shipment_plan_id"));
        if (pathList.size() <= 0) {
            for (int i = 1; i <= 5; i++) {
                if (i == 1) {
                    jsonParam.put("file", null);
                } else {
                    jsonParam.put("file" + i, null);
                }
            }
            return shipmentsDao.saveDeliveryFilePath(jsonParam);
        }
        JSONObject obj = pathList.getJSONObject(0);
        jsonParam.put("shipment_plan_id", obj.getString("shipment_plan_id"));
        String nowTime = DateUtils.getDateDay();
        // 文件路径
        String uploadPath =
            pathProps.getStore() + jsonObject.getString("client_id") + "/shipment/delivery_file/" + nowTime + "/";
        for (int i = 0; i < pathList.size(); i++) {
            // 获取前端传递的url，如果url存在，则保存原来文件路径
            String url = pathList.getJSONObject(i).getString("url");
            if (!StringTools.isNullOrEmpty(url)) {
                if (i == 0) {
                    jsonParam.put("file", url);
                } else {
                    jsonParam.put("file" + (i + 1), url);
                }
            } else {
                if (i == 0) {
                    jsonParam.put("file", uploadPath + obj.getString("name"));
                } else {
                    jsonParam.put("file" + (i + 1), uploadPath + (pathList.getJSONObject(i).getString("name")));
                }
            }
        }
        return shipmentsDao.saveDeliveryFilePath(jsonParam);
    }

    @Override
    public List<Tw200_shipment> getUntrackedShipments(String client_id, String template) {
        return shipmentsDao.getShipmentShopifyList(client_id, template);
    }

    @Override
    public Integer setShipmentFinishFlg(String client_id, String warehouse_cd, String shipment_plan_id) {
        return shipmentsDao.setShipmentFinishFlg(client_id, warehouse_cd, shipment_plan_id);
    }

    @Override
    public List<Tw200_shipment> getEcShipInfo(String client_id, String outer_order_no) {
        return shipmentsDao.getShipmentShopifyList(client_id, outer_order_no);
    }

    @Override
    public List<Tw200_shipment> getShipmentListQoo10(String client_id, String template) {
        return shipmentsDao.getShipmentListQoo10(client_id, template);
    }

    public Integer getSetCnt(String product_id, String client_id, Integer product_plan_cnt, String needFullProductId) {
        // 计算出该需要补充的set子商品总共需要多少个
        String[] productList = {
            product_id
        };
        List<Mc100_product> mc100List = productService.getSetProductList(client_id, null, productList, null, null, null,
            null, 1, 0, null, null);
        List<Mc103_product_set> mc103List = mc100List.get(0).getMc103_product_sets();
        int plan_cnt = 0;
        for (Mc103_product_set mc103 : mc103List) {
            if (mc103.getProduct_id().equals(needFullProductId)) {
                plan_cnt = mc103.getProduct_cnt();
            }
        }
        return product_plan_cnt * plan_cnt;
    }

    /**
     * @param client_id
     * @param shipment_plan_id
     * @description: 重新获取tw200数据判断是否需要变更出库状态
     * @return:
     * @date: 2021/3/12
     */
    public void checkShipmentStatus(String client_id, String shipment_plan_id, Integer shipment_status) {
        List<Tw201_shipment_detail> shipmentDetailList =
            shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, false);
        if (StringTools.isNullOrEmpty(shipmentDetailList) || shipmentDetailList.size() == 0) {
            return;
        }

        boolean updateFlag = true;
        for (Tw201_shipment_detail tw201_shipment_detail : shipmentDetailList) {
            if (tw201_shipment_detail.getReserve_status() != 1) {
                updateFlag = false;
                break;
            }
        }
        // 如果200对应的所有201数据引当状态都为1, 且是引当等待状态, 则需要变更出库状态
        if (updateFlag && shipment_status == 2) {
            shipmentsDao.updateShipmentStatus(shipment_plan_id, 3);
        }
    }

    /**
     * @param :tw200需要补充的订单实体类
     * @param :num需要回滚的商品数量
     * @param :client_id用戶ID
     * @param :canceledTw201被取消的tw201实体类
     * @description: 出库取消补充逻辑
     * @return:
     * @date: 2021/3/12
     */
    public Integer toFullOtherShipments(Tw200_shipment tw200, Integer num, String client_id,
        Tw201_shipment_detail canceledTw201, String setSubPorduct_id) {
        List<Tw201_shipment_detail> tw201List = tw200.getTw201_shipment_detail();
        for (Tw201_shipment_detail tw201 : tw201List) {
            // tem为需要补的数量
            int tem = tw201.getProduct_plan_cnt() - tw201.getReserve_cnt();
            // int tem = customProductPlanCnt - Integer.valueOf(tw201.getReserve_cnt());
            // 不等于0说明需要补充，否则不需要补充
            if (tem != 0) {
                // num为补充后剩余的数量
                if (num > tem) {
                    num = num - tem;
                } else if (num == tem) {
                    num = 0;
                } else {
                    tem = num;
                    num = 0;
                }
                // 更新补充之后的tw201数据
                shipmentsDao.updateReserve_cnt(tw201.getShipment_plan_id(), tw201.getProduct_id(),
                    (tem + tw201.getReserve_cnt()));
                // 检查是否需要将引当状态变更为1
                if ((tem + tw201.getReserve_cnt()) == tw201.getProduct_plan_cnt()) {
                    shipmentsDao.updateReserve_status(tw201.getShipment_plan_id(), tw201.getProduct_id(), 1);
                }
            }
            // 如果num补完了则跳出循环不继续
            if (num == 0) {
                break;
            }
        }

        return num;
    }

    /**
     * @param json : 数据
     * @param tax : 消费税类别
     * @param accordion : 是否为轻减税率商品
     * @description: 计算商品消费税
     * @return: void
     * @date: 2021/3/30 18:03
     */
    public static void calculateProductTax(JSONObject json, int tax, int accordion) {
        JSONArray items = json.getJSONArray("items");
        if (StringTools.isNullOrEmpty(items)) {
            items = json.getJSONArray("setProductJson");
        }
        // 総計通常税率金额
        int totalWithNormalTaxPrice = 0;
        // 総計軽減税率金额
        int totalWithReducedTaxPrice = 0;
        // 10%消费税
        int normalTax = 0;
        // 8%消费税
        int reducedTax = 0;
        //
        String set_id = "";
        for (int i = 0; i < items.size(); i++) {
            JSONObject object = items.getJSONObject(i);
            String taxFlag = object.getString("tax_flag");
            int unitPrice = 0;
            if (!StringTools.isNullOrEmpty(object.getString("unit_price"))) {
                unitPrice = Integer.parseInt(object.getString("unit_price"));
            }
            if (StringTools.isNullOrEmpty(taxFlag)) {
                continue;
            }
            String kubun = object.getString("kubun");
            int productKubun = !StringTools.isNullOrEmpty(kubun) ? Integer.parseInt(kubun) : 0;
            if (productKubun == 1) {
                continue;
            }

            // 因为出库依赖只查看PDF 传过来的值为 "税抜" 而新规成功后查看PDF的值 穿过的来的为数字
            List<String> taxList = Arrays.asList("税抜", "10", "11", "12", "1");
            int isReducedTax = (!StringTools.isNullOrEmpty(object.getString("is_reduced_tax")))
                ? Integer.parseInt(object.getString("is_reduced_tax"))
                : 0;
            // 判断是否为轻减税率
            double reduceRax = (isReducedTax == 1) ? 0.08 : 0.1;
            Integer productPlanCnt = 0;
            // 如果是set商品
            Integer set_sub_id = object.getInteger("set_sub_id");
            if (!StringTools.isNullOrEmpty(set_sub_id) && set_sub_id > 0
                && !StringTools.isNullOrEmpty(object.getInteger("set_cnt"))) {
                productPlanCnt = object.getInteger("set_cnt");
            } else {
                // 同捆物或者普通商品
                productPlanCnt = object.getInteger("product_plan_cnt");
            }

            // 统计消费税
            // int taxParam = 0;
            // 税入前 或者税拔后的金额
            double temporaryPrice = unitPrice;
            // 税込
            if (tax == 0) {
                // 如果店铺设置为税込只有税抜需要计算
                if (taxList.contains(taxFlag)) {
                    temporaryPrice = calculateTheNumbers((unitPrice * reduceRax), accordion) + unitPrice;
                }
            } else {
                // 税抜 店铺设定为税抜 只有税込 需要计算
                if ("税込".equals(taxFlag) || "0".equals(taxFlag)) {
                    int param = 10;
                    if (isReducedTax == 1) {
                        param = 8;
                    }
                    // double param1 = (unitPrice * param) / (100 + param);
                    double tax1 = getTax(unitPrice, param);
                    temporaryPrice = unitPrice - calculateTheNumbers(tax1, accordion);
                }
            }
            int currentTax = 0;
            // 商品金额 非课税和税入 为商品单价 * 商品个数
            int theAmountOfGoods = unitPrice * productPlanCnt;
            // 计算其消费税 如果商品为非课税 消费税为0 计算pdf最下面的うち消費税
            // 商品为税拔
            if (taxList.contains(taxFlag)) {
                // taxParam = calculateTheNumbers((unitPrice * reduceRax), accordion) * productPlanCnt;
                currentTax = (calculateTheNumbers((unitPrice * reduceRax), accordion) + unitPrice) * productPlanCnt;
                // 税拔为 计算后的商品单价 * 商品个数
                theAmountOfGoods = currentTax;
            }
            // 商品为税込
            if ("税込".equals(taxFlag) || "0".equals(taxFlag)) {
                // int param = 10;
                // if (isReducedTax == 1) {
                // param = 8;
                // }
                // double param1 = (unitPrice * param) / (100 + param);
                // taxParam = calculateTheNumbers(param1, accordion) * productPlanCnt;
                currentTax = unitPrice * productPlanCnt;
            }

            if (!StringTools.isNullOrEmpty(object.getString("set_sub_id"))
                && set_id.indexOf(object.getString("set_sub_id")) >= 0) {
                continue;
            }
            set_id += object.getString("set_sub_id") + ",";

            if (isReducedTax == 0) {
                // 10%対象税込の累計
                // totalWithNormalTaxPrice += taxParam;
                normalTax += currentTax;
            } else {
                // totalWithReducedTaxPrice += taxParam;
                // 8%対象税込の累計
                reducedTax += currentTax;
            }

            // 商品単価（税込の端末処理）
            unitPrice = calculateTheNumbers(temporaryPrice, accordion);
            // 商品単価
            object.put("unit_price", unitPrice);
            // 商品金額=単価*数量
            object.put("price", theAmountOfGoods);
        }
        // @Add wang 2021/4/1 更新 start
        // 10%対象のうち消費税
        json.put("total_with_normal_tax", normalTax);
        double saleTax = getTax(normalTax, 10);
        totalWithNormalTaxPrice = calculateTheNumbers(saleTax, accordion);
        json.put("totalWithNormalTaxPrice", totalWithNormalTaxPrice);
        // 8%対象のうち消費税
        json.put("total_with_reduced_tax", reducedTax);
        double saleTax1 = getTax(reducedTax, 8);
        totalWithReducedTaxPrice = calculateTheNumbers(saleTax1, accordion);
        json.put("totalWithReducedTaxPrice", totalWithReducedTaxPrice);
        // @Add wang 2021/4/1 更新 end
    }

    /**
     * @param tax : 消费税
     * @param reduced : 消费税率
     * @description: 计算消费税金额
     * @return: double
     * @date: 2021/4/2 16:18
     */
    private static double getTax(int tax, int reduced) {
        double param = tax * reduced;
        return param / (100 + reduced);
    }

    /**
     * @param number : 数字
     * @param accordion : 计算方式
     * @description: 根据计算方式 获取结果
     * @return: int
     * @date: 2021/3/31 16:07
     */
    private static int calculateTheNumbers(double number, int accordion) {
        int unitPrice = 0;
        switch (accordion) {
            case 0:
                // 切り捨て
                unitPrice = (int) Math.floor(number);
                break;
            case 1:
                // 切り上げ
                unitPrice = (int) Math.ceil(number);
                break;
            case 2:
                // 四捨五入
                unitPrice = (int) Math.round(number);
                break;
            default:
                break;
        }
        return unitPrice;
    }

    @Override
    public List<Tw200_shipment> getGMOBillBarcode(String client_id) {
        // TODO 自動生成されたメソッド・スタブ
        return shipmentsDao.getGMOBillBarcode(client_id);
    }

    @Override
    public Integer updateGMOBillBarcode(String shipment_plan_id, String bill_barcode) {
        // TODO 自動生成されたメソッド・スタブ
        return shipmentsDao.updateGMOBillBarcode(shipment_plan_id, bill_barcode);
    }

    /**
     * @param jsonParam
     * @description: 店铺侧 纳品书—作业指示书 数据拼接
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/8 15:50
     */
    @Override
    public JSONObject getShipmentsA3PDF(JSONObject jsonParam) {
        String client_id = jsonParam.getString("client_id");
        String shipment_plan_id = jsonParam.getString("shipment_plan_id");
        JSONObject sender = jsonParam.getJSONObject("sender");
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false,
            jsonParam.getString("sponsor_id"));
        Ms012_sponsor_master ms012_sponsor_master = sponsorList.get(0);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("price_on_delivery_note", jsonParam.getString("price_on_delivery_note"));
        String detail_logo = ms012_sponsor_master.getDetail_logo();
        String logoPath = pathProps.getRoot() + detail_logo;
        jsonObject.put("detail_logo", logoPath);
        String codeName = shipment_plan_id;
        String codePath = pathProps.getRoot() + pathProps.getStore() + DateUtils.getDateMonth() + "/code/" + codeName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        jsonObject.put("codePath", codePath);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("client_id", client_id);
        jsonObject.put("company", sender.getString("company"));
        jsonObject.put("surname", sender.getString("surname"));
        jsonObject.put("division", sender.getString("division"));
        jsonObject.put("postcode", sender.getString("postcode"));
        jsonObject.put("prefecture", sender.getString("prefecture"));
        jsonObject.put("address1", sender.getString("address1"));
        jsonObject.put("address2", sender.getString("address2"));
        StringBuilder orderName = new StringBuilder();
        if (!StringTools.isNullOrEmpty(sender.getString("order_family_name"))) {
            orderName.append(sender.getString("order_family_name"));
        }
        if (!StringTools.isNullOrEmpty(sender.getString("order_first_name"))) {
            orderName.append(sender.getString("order_first_name"));
        }
        if (orderName.length() == 0) {
            orderName.append("-");
        }
        jsonObject.put("order_name", orderName.toString());
        jsonObject.put("order_todoufuken", sender.getString("order_todoufuken"));
        jsonObject.put("order_address1", sender.getString("order_address1"));
        jsonObject.put("order_address2", sender.getString("order_address2"));
        jsonObject.put("order_zip_code1", sender.getString("order_zip_code1"));
        jsonObject.put("order_zip_code2", sender.getString("order_zip_code2"));

        jsonObject.put("masterPostcode", ms012_sponsor_master.getPostcode());
        jsonObject.put("masterPrefecture", ms012_sponsor_master.getPrefecture());
        jsonObject.put("masterAddress1", ms012_sponsor_master.getAddress1());
        jsonObject.put("masterAddress2", ms012_sponsor_master.getAddress2());
        jsonObject.put("order_no", jsonParam.getString("order_no"));
        jsonObject.put("identifier", jsonParam.getString("identifier"));
        jsonObject.put("gift_sender_name", jsonParam.getString("gift_sender_name"));
        jsonObject.put("name", ms012_sponsor_master.getName());
        jsonObject.put("total_amount", jsonParam.getString("total_amount"));
        Integer contact = ms012_sponsor_master.getContact();
        String value = CommonUtils.getContact(contact);
        jsonObject.put("contact", value);
        jsonObject.put("sponsorEmail", ms012_sponsor_master.getEmail());
        jsonObject.put("sponsorFax", ms012_sponsor_master.getFax());
        jsonObject.put("sponsorPhone", ms012_sponsor_master.getPhone());
        String order_datetime = "";
        if (!StringTools.isNullOrEmpty(jsonParam.getString("order_datetime"))) {
            String orderDatetime = jsonParam.getString("order_datetime");
            order_datetime = orderDatetime.substring(0, 10);
        }
        jsonObject.put("order_datetime", order_datetime);
        Mc105_product_setting productSetting = productSettingService.getProductSetting(client_id, null);
        Integer tax = productSetting.getTax();
        Integer accordion = productSetting.getAccordion();
        String product_tax = (tax == 1) ? "税抜" : "税込";
        jsonObject.put("product_tax", product_tax);
        JSONArray items = jsonParam.getJSONArray("items");
        JSONArray product = new JSONArray();
        JSONArray bundled = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            Integer bundledFlg = itemsJSONObject.getInteger("bundled_flg");
            if (bundledFlg == 0) {
                product.add(itemsJSONObject);
            } else {
                String product_id = itemsJSONObject.getString("product_id");
                Mc100_product mc100Product = productDao.getProductById(product_id, client_id);
                itemsJSONObject.put("name", mc100Product.getName());
                itemsJSONObject.put("code", mc100Product.getCode());
                itemsJSONObject.put("barcode", mc100Product.getBarcode());
                bundled.add(itemsJSONObject);
            }
        }
        // 拼接セット商品
        // 计算商品消费税
        calculateProductTax(jsonParam, tax, accordion);
        JSONArray objects = splicingSetProduct(product, 0);
        jsonObject.put("items",
            ShipmentServiceImpl.jsonArraySort(objects.toString(), "product_id", "locationName", null, true));
        jsonObject.put("payment_method", jsonParam.getString("payment_method"));
        if (!StringTools.isNullOrEmpty(jsonParam.getString("message"))) {
            jsonObject.put("detail_message", jsonParam.getString("message"));
        } else {
            jsonObject.put("detail_message", "");
        }
        jsonObject.put("subtotal_amount", jsonParam.getString("subtotal_amount"));
        jsonObject.put("delivery_charge", jsonParam.getString("delivery_charge"));
        jsonObject.put("handling_charge", jsonParam.getString("handling_charge"));
        jsonObject.put("discount_amount", jsonParam.getString("discount_amount"));
        jsonObject.put("total_amount", jsonParam.getString("total_amount"));
        jsonObject.put("payment_method_name", "");
        if (!StringTools.isNullOrEmpty(jsonParam.getString("payment_method"))) {
            String payment_method = deliveryDao
                .getPayById(Integer.valueOf(jsonParam.getString("payment_method")));
            if (!StringTools.isNullOrEmpty(payment_method)) {
                jsonObject.put("payment_method_name", payment_method);
            }
        }
        jsonObject.put("total_for_cash_on_delivery", jsonParam.getString("total_for_cash_on_delivery"));
        String delivery_time_slot = jsonParam.getString("delivery_time_slot");
        if (delivery_time_slot != null && !"".equals(delivery_time_slot)) {
            jsonObject.put("delivery_time_slot",
                deliveryDao.getDeliveryTime(null, Integer.valueOf(delivery_time_slot), null, null).get(0)
                    .getDelivery_time_name());
        }
        jsonObject.put("total_with_normal_tax", jsonParam.getString("total_with_normal_tax"));
        jsonObject.put("totalWithNormalTaxPrice", jsonParam.getString("totalWithNormalTaxPrice"));
        jsonObject.put("total_with_reduced_tax", jsonParam.getString("total_with_reduced_tax"));
        jsonObject.put("totalWithReducedTaxPrice", jsonParam.getString("totalWithReducedTaxPrice"));
        // 同捆明细指定
        String delivery_note_type = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
        if ("同梱する".equals(jsonParam.getString("delivery_note_type"))
            || "1".equals(jsonParam.getString("delivery_note_type"))) {
            delivery_note_type = "納品書同梱";
        }
        jsonObject.put("delivery_note_type", delivery_note_type);

        Ms201_client clientInfo = clientDao.getClientInfo(client_id);
        jsonObject.put("client_nm", clientInfo.getClient_nm());

        // 获取到出荷指示特記事項
        String instructionsList = jsonParam.getString("delivery_instructions");
        if (!StringTools.isNullOrEmpty(instructionsList)) {
            List<String> splitToList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(instructionsList);
            // 出荷特记事项追加
            for (int i = 0; i < splitToList.size(); i++) {
                if (i == 0) {
                    jsonObject.put("instructions_special_notes", splitToList.get(i));
                } else {
                    jsonObject.put("bikou" + i, splitToList.get(i));
                }
            }
            jsonObject.put("bikou_flg", 1);
        } else if (StringTools.isNullOrEmpty(jsonObject.getString("order_flg"))) {
            jsonObject.put("bikou_flg", 0);
        }
        jsonObject.put("shipping_date", jsonParam.getString("shipping_date"));
        jsonObject.put("delivery_date", jsonParam.getString("delivery_date"));
        jsonObject.put("gift_wrapping_unit", jsonParam.getString("gift_wrapping_unit"));
        jsonObject.put("gift_wrapping_type", jsonParam.getString("gift_wrapping_type"));
        jsonObject.put("cushioning_unit", jsonParam.getString("cushioning_unit"));
        jsonObject.put("cushioning_type", jsonParam.getString("cushioning_type"));
        jsonObject.put("memo", jsonParam.getString("memo"));

        // 普通商品 包含（セット）
        JSONArray detailJson = shipmentDetailService.setProductSplice(product);
        for (int i = 0; i < detailJson.size(); i++) {
            JSONObject setJson = detailJson.getJSONObject(i);
            Integer set_sub_id = setJson.getInteger("set_sub_id");
            if (StringTools.isNullOrEmpty(set_sub_id) || set_sub_id == 0) {
                continue;
            }
            List<Mc103_product_set> productList = productDao.getSetProductDetail(client_id, set_sub_id);
            for (Mc103_product_set set : productList) {
                if (setJson.getString("product_id").equals(set.getProduct_id())) {
                    setJson.put("name", set.getName());
                    setJson.put("code", set.getCode());
                    setJson.put("barcode", set.getBarcode());
                }
            }
        }
        jsonObject.put("setItems", detailJson);

        // 同捆物
        jsonObject.put("bundled", bundled);
        String deliveryCarrier = jsonParam.getString("delivery_carrier");
        Ms004_delivery delivery = deliveryDao.getDeliveryById(deliveryCarrier);
        String deliveryName = " ";
        if (!StringTools.isNullOrEmpty(delivery)) {
            deliveryName = delivery.getDelivery_nm();
        }
        jsonObject.put("method", deliveryName + " " + delivery.getDelivery_method_name());

        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "detail", shipment_plan_id);
        String relativePath = pathProps.getStore() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        try {
            PdfTools.createStoreShipmentsDetailPDF(jsonObject, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @param jsonObject : 被拆分的商品Id 出库依赖Id
     * @param request : 响应
     * @description: 拆分出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/15 11:17
     */
    @Override
    @Transactional
    public JSONObject splitShipment(String client_id, String shipment_plan_id, JSONObject jsonObject,
        HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date nowTime = DateUtils.getNowTime(null);

        // 被拆分的出库依赖Id

        JSONArray splitProductId = jsonObject.getJSONArray("splitProductId");
        // 被拆分的商品Id 集合
        List<String> splitProductList = JSONObject.parseArray(splitProductId.toString(), String.class);
        String newShipmentId = "";

        logger.info("出库依赖拆分开始,拆分的出库依赖Id={},店铺Id={},商品Id={}", shipment_plan_id, client_id, splitProductList);
        Tw200_shipment shipment = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
        if (!StringTools.isNullOrEmpty(shipment)) {
            logger.error("根据出库依赖Id:{} 没有查到出库依赖信息", shipment_plan_id);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, false);

        // 被拆分剩余的商品信息
        List<Tw201_shipment_detail> afterSplitDetail = new ArrayList<>();
        // 拆分出来的商品信息
        List<Tw201_shipment_detail> beforeSplitDetail = new ArrayList<>();
        // 拆分出来的依赖管理Id
        List<Integer> splitIdList = new ArrayList<>();

        for (Tw201_shipment_detail detail : shipmentDetails) {
            String product_id = detail.getProduct_id();
            // 如果 商品Id 不存在与被拆分商品的集合中， 组成一个新的出库详细管理集合
            // set商品
            if (!StringTools.isNullOrEmpty(detail.getSet_sub_id()) && detail.getSet_sub_id() > 0) {
                List<Mc100_product> setList =
                    productDao.getSetProductInfo(detail.getClient_id(), detail.getSet_sub_id());
                if (!StringTools.isNullOrEmpty(setList) && setList.size() > 0) {
                    product_id = setList.get(0).getProduct_id();
                }
            }

            if (!splitProductList.contains(product_id)) {
                afterSplitDetail.add(detail);
            } else {
                beforeSplitDetail.add(detail);
                splitIdList.add(detail.getId());
            }
        }
        if (!afterSplitDetail.isEmpty()) {
            // 分割后只有同捆物
            int afterCount = (int) afterSplitDetail.stream().filter(x -> x.getKubun() == 1).count();
            if (afterCount == afterSplitDetail.size()) {
                throw new BaseException(ErrorCode.E_13002);
            }
        }

        if (!beforeSplitDetail.isEmpty()) {
            // 分割后只有同捆物
            int beforeCount = (int) beforeSplitDetail.stream().filter(x -> x.getKubun() == 1).count();
            if (beforeCount == beforeSplitDetail.size()) {
                throw new BaseException(ErrorCode.E_13002);
            }
        }


        Map<String, Integer> variousPrices;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                // 计算出被拆分后 商品合计 轻减税率 非轻减税率 出庫依頼商品種類数 出庫依頼商品数計
                variousPrices = getProductVariousPrices(afterSplitDetail);
            } else {
                // 计算出拆分出来的 商品合计 轻减税率 非轻减税率 出庫依頼商品種類数 出庫依頼商品数計
                variousPrices = getProductVariousPrices(beforeSplitDetail);
            }
            // 所有价格的总和 = 商品合计 + 納品書 配送料 + 納品書 手数料 - 納品書 割引額
            Integer subtotalAmount = variousPrices.get("subtotalAmount");
            int totalAmount = subtotalAmount + shipment.getDelivery_charge() +
                shipment.getHandling_charge() - shipment.getDiscount_amount();
            // 納品書 合計
            shipment.setTotal_amount(totalAmount + "");
            // 商品合計金額
            shipment.setTotal_price(Long.valueOf(subtotalAmount));
            // 納品書 小計
            shipment.setSubtotal_amount(String.valueOf(subtotalAmount));
            // 総計軽減税率
            shipment.setTotal_with_reduced_tax(variousPrices.get("totalWithReducedTax"));
            // 総計通常税率
            shipment.setTotal_with_normal_tax(variousPrices.get("totalWithNormalTax"));
            // 出庫依頼商品種類数
            shipment.setProduct_kind_plan_cnt(variousPrices.get("productKindPlanCnt"));
            // 出庫依頼商品数計
            shipment.setProduct_plan_total(variousPrices.get("productPlanTotal"));
            Integer status = variousPrices.get("status");
            shipment.setUpd_date(nowTime);
            shipment.setUpd_usr(loginNm);
            Integer shipment_status = shipment.getShipment_status();

            if (i == 0) {
                // 如果之前为引当等待 并且 明细中全部为引当完了 需要经状态变为 出荷等待
                if (shipment_status == 2 && status == 1) {
                    shipment.setShipment_status(3);
                }
                if (shipment_status == 1 || shipment_status == 6) {
                    // 确认等待 或者 出库保留 不需要改变
                    shipment.setShipment_status(shipment_status);
                }
                // 修改 出库依赖信息
                shipmentsDao.updateShipmentInfo(shipment);
            } else {
                // 获取到新的出库依赖Id
                newShipmentId = setShipmentPlanId(client_id);
                shipment.setShipment_plan_id(newShipmentId);
                shipment.setIns_date(nowTime);
                shipment.setIns_usr(loginNm);
                // 如果status=1则证明 明细中全部都是引当完了 状态应该变为出荷等待。 如果不等于1，则还存在引当等待，状态变为应当等待
                if (shipment_status == 1 || shipment_status == 6) {
                    // 确认等待 或者 出库保留 不需要改变
                    shipment.setShipment_status(shipment_status);
                } else {
                    shipment.setShipment_status(status == 1 ? 3 : 2);
                }
                JSONObject json = (JSONObject) JSONObject.toJSON(shipment);
                // 新规 出库依赖ID
                shipmentsDao.insertShipments(json);
                // 将被拆分商品的详细信息的出库依赖Id 变更为新的出库依赖Id
                shipmentDetailDao.updateShipmentId(newShipmentId, client_id, shipment_plan_id, splitIdList, loginNm,
                    nowTime);
            }
        }
        logger.info("出库依赖拆分完成,生成新的出库依赖Id={}", newShipmentId);
        return CommonUtils.success(newShipmentId);
    }

    public Map<String, Integer> getProductVariousPrices(List<Tw201_shipment_detail> details) {
        HashMap<String, Integer> priceMap = new HashMap<>();
        // 商品总价
        int subtotalAmount = 0;
        // 统计所有轻减商品的税率
        int totalWithReducedTax = 0;
        // 统计所有通常商品的税率
        int totalWithNormalTax = 0;
        // 出庫依頼商品種類数
        int productKindPlanCnt = details.size();
        // 出庫依頼商品数計
        int productPlanTotal = 0;

        boolean statusFlg = false;
        for (Tw201_shipment_detail detail : details) {
            Integer price = detail.getPrice();
            subtotalAmount += price;
            Integer isReducedTax = detail.getIs_reduced_tax();
            if (isReducedTax == 1) {
                totalWithReducedTax += price;
            } else {
                totalWithNormalTax += price;
            }
            productPlanTotal += detail.getProduct_plan_cnt();
            if (detail.getReserve_status() == 0) {
                statusFlg = true;
            }
        }
        int status = 1;
        if (statusFlg) {
            status = 0;
        }
        priceMap.put("status", status);
        priceMap.put("subtotalAmount", subtotalAmount);
        priceMap.put("totalWithReducedTax", totalWithReducedTax);
        priceMap.put("totalWithNormalTax", totalWithNormalTax);
        priceMap.put("productKindPlanCnt", productKindPlanCnt);
        priceMap.put("productPlanTotal", productPlanTotal);
        return priceMap;
    }

    /**
     * @param client_id
     * @param:
     * @description: 获取可以合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    @Override
    public JSONArray getMergeShipmentList(String client_id, String search) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }

        // 可以合并的状态：確認待ち、引当待ち、出荷待ち、出荷保留
        List<Integer> shipment_status = Arrays.asList(1, 2, 3, 6);
        List<Tw200_shipment> mergeShipment = shipmentsDao.getMergeShipmentList(client_id, search, shipment_status);
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(mergeShipment));
        return array;
    }

    /**
     * @param client_id
     * @param jsonParam
     * @param:
     * @description: 合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    @Override
    @Transactional
    public Boolean mergeShipment(String client_id, String main_plan_id, JSONObject jsonParam) {
        // 主依赖状态
        Integer shipment_status = jsonParam.getInteger("shipment_status");
        // 被合并项
        JSONArray mergeList = jsonParam.getJSONArray("mergeList");
        // 要变更del_flg的数据<key->shipment_plan_id ,value-> client_id>
        HashMap<String, String> shipmentIds = new HashMap<>();
        // 记录更新TW201表的出库依赖ID
        ArrayList<String> childrenShipments = new ArrayList<>();
        // 记录被合并出库依赖状态status
        ArrayList<Integer> childrenShipmentsStatus = new ArrayList<>();
        for (int i = 0; i < mergeList.size(); i++) {
            JSONObject item = mergeList.getJSONObject(i);
            // 根据出库依赖ID获取被合并出库依赖信息
            String shipment_plan_id = item.getString("shipment_plan_id");
            shipmentIds.put(shipment_plan_id, client_id);
            childrenShipments.add(shipment_plan_id);
            childrenShipmentsStatus.add(item.getInteger("shipment_status"));
        }
        // 将主依赖id添加到要获取的依赖信息中
        childrenShipments.add(main_plan_id);
        // 所有依赖
        List<Tw200_shipment> shipment = shipmentsDao.getChildShipment(client_id, childrenShipments);
        // 找到主依赖
        Tw200_shipment mainShipment =
            shipment.stream().filter(item -> main_plan_id.equals(item.getShipment_plan_id())).findAny().get();
        List<Tw201_shipment_detail> mailShipmentDetail = mainShipment.getTw201_shipment_detail();
        // key:id --> value: 主依赖商品明细
        Map<String, Tw201_shipment_detail> mailProductInfo =
            mailShipmentDetail.stream().filter(x -> !StringTools.isNullOrEmpty(x.getId()))
                .collect(Collectors.toMap(tw201 -> tw201.getId().toString(), o -> o));
        // 记录tw201表中主依赖已存在的商品id
        HashMap<String, String> existsProduct = new HashMap<>();
        // 主依赖商品小计
        Integer subtotal_amount = Integer.parseInt(mainShipment.getSubtotal_amount());
        // 主依赖合计
        Integer total_amount = Integer.parseInt(mainShipment.getTotal_amount());
        // 主依赖商品依赖总数
        Integer product_plan_total = mainShipment.getProduct_plan_total();
        // 主依赖商品种类数
        Integer product_kind_plan_cnt = mainShipment.getProduct_kind_plan_cnt();
        // 主依赖支払総計(税込)
        Integer total_for_cash_on_delivery = mainShipment.getTotal_for_cash_on_delivery();
        total_for_cash_on_delivery =
            StringTools.isNullOrEmpty(total_for_cash_on_delivery) ? 0 : total_for_cash_on_delivery;
        // 主依赖支付方法
        String payment_method = mainShipment.getPayment_method();
        // 是否需要更新 明细表依赖数
        // 清空 之前查询所有依赖详细的依赖ID
        ArrayList<Map<String, Object>> updateQuantityLists = new ArrayList<>();
        for (int i = 0; i < shipment.size(); i++) {
            Tw200_shipment tw200 = shipment.get(i);
            String shipment_plan_id = tw200.getShipment_plan_id();
            // 如果是主依赖
            if (shipment_plan_id.equals(main_plan_id)) {
                continue;
            }
            // 检测明细是否重复flg
            boolean exists = false;
            List<Tw201_shipment_detail> shipment_detail = tw200.getTw201_shipment_detail();
            for (Tw201_shipment_detail detail : shipment_detail) {
                // 检测明细是否重复flg
                exists = false;
                String productId = detail.getProduct_id();
                String id = detail.getId().toString();
                Integer set_sub_id = detail.getSet_sub_id();
                // 如果主依赖里存在要被合并的商品id,需要将数量也合并
                if (mailProductInfo.containsKey(id)) {
                    // 依赖明细相同 ，变更flg
                    exists = true;
                    Tw201_shipment_detail tw201 = mailProductInfo.get(id);
                    HashMap<String, Object> map = new HashMap<>();
                    // 依赖数
                    Integer planCnt = tw201.getProduct_plan_cnt() + detail.getProduct_plan_cnt();
                    map.put("product_id", productId);
                    // 明细价格
                    map.put("price", tw201.getPrice() + (detail.getProduct_plan_cnt() * detail.getUnit_price()));
                    // key->数量；value->价格
                    map.put("product_plan_cnt", planCnt);
                    // 引当数
                    Integer reserveCnt = detail.getReserve_cnt();
                    // 主依赖的引当数
                    Integer mainReserveCnt = tw201.getReserve_cnt();
                    // 合并后的引当数
                    int reserve_cnt = 0;
                    if (StringTools.isNullOrEmpty(reserveCnt)) {
                        reserveCnt = 0;
                    }
                    if (StringTools.isNullOrEmpty(mainReserveCnt)) {
                        mainReserveCnt = 0;
                    }
                    reserve_cnt = reserveCnt + mainReserveCnt;

                    map.put("reserve_cnt", reserve_cnt);
                    // 主依赖的set_cnt
                    int mainSetCnt = !StringTools.isNullOrEmpty(tw201.getSet_cnt()) ? tw201.getSet_cnt() : 0;
                    // 被合并依赖的set_cnt
                    int setCnt = !StringTools.isNullOrEmpty(detail.getSet_cnt()) ? detail.getSet_cnt() : 0;
                    map.put("set_cnt", mainSetCnt + setCnt);
                    map.put("set_sub_id", set_sub_id); // 将重复的tw201表数据的del_flg
                    existsProduct.put(productId, shipment_plan_id);
                    // updateQuantityLists
                    updateQuantityLists.add(map);
                } else {
                    // 商品种类数
                    product_kind_plan_cnt++;
                }
            }
            // 納品書 小計
            subtotal_amount += Integer.parseInt(tw200.getSubtotal_amount());
            // 納品書 合計
            total_amount += Integer.parseInt(tw200.getTotal_amount());
            // 出庫依頼商品数計
            product_plan_total += tw200.getProduct_plan_total();
            // 如果支付方法为代金引换的时候，支付总额想加在一起
            if ("2".equals(payment_method) && "2".equals(tw200.getPayment_method())) {
                int s_total_for_cash_on_delivery = StringTools.isNullOrEmpty(tw200.getTotal_for_cash_on_delivery()) ? 0
                    : tw200.getTotal_for_cash_on_delivery();
                total_for_cash_on_delivery += s_total_for_cash_on_delivery;
            }
            // 如果明细不同，更新明细依赖ID
            if (!exists) {
                childrenShipments.add(shipment_plan_id);
            }
        }
        // 将被合并依赖的商品小计 ，合计 ，商品种类数 ，商品总数 更新到主依赖上
        mainShipment.setSubtotal_amount(String.valueOf(subtotal_amount));
        mainShipment.setTotal_amount(String.valueOf(total_amount));
        mainShipment.setProduct_plan_total(product_plan_total);
        mainShipment.setProduct_kind_plan_cnt(product_kind_plan_cnt);
        mainShipment.setTotal_for_cash_on_delivery(total_for_cash_on_delivery);
        // 主依赖最终状态
        Integer state = updateShipmentStatus(shipment_status, childrenShipmentsStatus);
        mainShipment.setShipment_status(state);
        try {
            // 更新tw200的del_flg
            shipmentsDao.updateChildrenShipment(shipmentIds);
            // 删除tw201重复的del_flg
            if (existsProduct.size() > 0) {
                shipmentsDao.deleteExistsDetail(existsProduct);
            }
            // 更新TW201表相同明细的依赖数，总价
            if (updateQuantityLists.size() > 0) {
                shipmentsDao.updateProductQuantity(client_id, main_plan_id, updateQuantityLists);
            }
            // 更新TW201表的出库依赖ID
            if (childrenShipments.size() > 0) {
                shipmentsDao.mergeShipment(client_id, main_plan_id, childrenShipments);
            }
            // 更新tw200主依赖的出库状态
            shipmentsDao.updateMainShipment(mainShipment);
        } catch (Exception e) {
            // 更新結果が0場合、更新失敗として返す
            logger.error("合并失敗として返す");
            logger.error(BaseException.print(e));
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * @param jsonObject : product_id = 商品Id shipment_plan_id = 出库依赖Id client_id = 店铺Id
     * @param request : 请求
     * @param status : 0 ---> 修改商品信息 1 ---> 不修改商品信息
     * @description: 系统中之前不存在的商品登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/9 18:01
     */
    @Override
    @Transactional
    public JSONObject shipmentInsertProduct(JSONObject jsonObject, HttpServletRequest request, int status) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date nowTime = DateUtils.getNowTime(null);

        // 店铺Id
        String client_id = jsonObject.getString("client_id");
        // 商品Id
        String product_id = jsonObject.getString("product_id");

        // 更改出库依赖明细中的 kubun 为普通商品
        int kubun = 0;

        // 找出所有包含该商品 的出库明细信息
        List<Tw201_shipment_detail> shipmentDetailList = shipmentDetailDao.getShipmentDetail(client_id, product_id);

        // 获取到集合中包含的所有仮登録的出库依赖Id
        List<String> shipmentIdList =
            shipmentDetailList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getKubun()) && x.getKubun() == 9)
                .map(Tw201_shipment_detail::getShipment_plan_id).distinct().collect(Collectors.toList());

        if (!Collections.isEmpty(shipmentIdList)) {
            // 根据出库依赖Id 获取到出库依赖信息
            List<Tw200_shipment> shipmentInfoList = shipmentsDao.getShipmentInfoList(client_id, shipmentIdList);
            // 取出所有 为确认等待状态的出库依赖Id
            List<String> shipmentIds = shipmentInfoList.stream().filter(x -> x.getShipment_status() == 1)
                .map(Tw200_shipment::getShipment_plan_id).distinct().collect(Collectors.toList());

            if (!Collections.isEmpty(shipmentIds)) {
                // 更改所有满足条件的出库明细的 商品状态
                shipmentDetailDao.updateShipmentDetailKubun(client_id, product_id, shipmentIds, kubun, loginNm,
                    nowTime);
            }
        }

        try {
            orderDetailDao.updateProductKubunByProductId(product_id, client_id, kubun, loginNm, nowTime);
        } catch (Exception e) {
            logger.error("修改受注详细商品区分失败, 店铺Id={} 商品Id={}", client_id, product_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (status == 0) {
            // 修改商品为普通商品
            int show_flg = 0;
            try {
                productDao.updateProductKubun(client_id, product_id, kubun, show_flg, loginNm, nowTime);
            } catch (Exception e) {
                logger.error("修改商品为普通商品失败, 店铺Id={}  商品Id={}", client_id, product_id);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return CommonUtils.success();
    }

    /**
     * @Description: //根据主依赖和被合并的依赖状态修改主依赖状态
     *               @Date： 2021/6/23
     * @Param：
     * @return：
     */
    public Integer updateShipmentStatus(Integer status, ArrayList<Integer> statusList) {
        /*
         * 1出庫確認待ち
         * 2引当待ち
         * 3出荷等待
         * 4出荷作业中
         * 6出庫保留中
         * 8
         * 9
         */
        // 主引当，被入金 入金
        if (status == 2 && statusList.contains(9)) {
            return 9;
            // 主入金 || 被入金 入金
        } else if (status == 9 || statusList.contains(9)) {
            return 9;
            // 主引当 || 被引当 引当 ？？
        }
        // else if (status == 2 || statusList.contains(2)) {
        // return 2;
        // //主引当 && 被確認待ち 確認待ち
        // }
        else if (status == 2 && statusList.contains(1)) {
            return 1;
            // 主引当 && 被出庫保留中 出庫保留中
        } else if (status == 2 && statusList.contains(6)) {
            return 6;
            // 主出荷等待 && 被引当待ち 引当待ち
        } else if (status == 3 && statusList.contains(2)) {
            return 2;
            // 主出荷等待 && 被確認待ち 確認待ち
        } else if (status == 3 && statusList.contains(1)) {
            return 1;
            // 主出荷等待 && 被出庫保留中 出庫保留中
        } else if (status == 3 && statusList.contains(6)) {
            return 6;
            // 主确认等待 && 被引当 确认等待
        } else if (status == 1 && statusList.contains(2)) {
            return 1;
            // 主确认等待 && 被出库等待 确认等待
        } else if (status == 1 && statusList.contains(3)) {
            return 1;
            // 主确认等待 && 被出荷保留 确认等待
        } else if (status == 1 && statusList.contains(6)) {
            return 1;
            // 主出荷保留 && 被引当 确认等待
        } else if (status == 6 && statusList.contains(2)) {
            return 6;
            // 主出荷保留 && 被出荷等待 出荷保留
        } else if (status == 6 && statusList.contains(3)) {
            return 6;
            // 主出荷保留 && 被确认等待 出荷保留
        } else if (status == 6 && statusList.contains(1)) {
            return 6;
        } else {
            return status;
        }
    }

    /**
     * @param reserveNum : 引当数
     * @param product_id : 商品Id
     * @param inventory_cnt : 実在庫数
     * @param not_delivery : 不可配送数
     * @description: 统计出库商品的引当数
     * @return: java.lang.Integer
     * @date: 2021/4/25
     */
    public static Integer getReserveCnt(String client_id, int reserveNum, String product_id,
        ShipmentDetailDao detailDao, int inventory_cnt, int not_delivery, String copyFlg) {

        // 出库状态：4: 出荷作業中 41: 出庫承認待ち 42: 出庫承認済み 5:検品中 7:検品済み 8:出荷済み 9:入金待ち是不可以进行编辑的
        List<Tw201_shipment_detail> productReserveList = detailDao.getProductReserveList(client_id, product_id);

        int reserve_cnt = 0;
        // 统计该商品所有出库依赖中的引当数
        for (Tw201_shipment_detail reserveDetail : productReserveList) {
            if (product_id.equals(reserveDetail.getProduct_id())) {
                reserve_cnt += reserveDetail.getReserve_cnt();
            }
        }

        //
        if (reserve_cnt > 0) {
            // 依赖复制显示真是的在库数 不用减去本次依赖数
            if (StringTools.isNullOrEmpty(copyFlg) || !"1".equals(copyFlg)) {
                reserve_cnt = reserve_cnt - reserveNum;
            }
        } else {
            reserve_cnt = 0;
        }

        // 实际在库数 - 除本次之外所有引当数
        int total_reserve = inventory_cnt - reserve_cnt - not_delivery;

        if (total_reserve < 0) {
            total_reserve = 0;
        }

        return total_reserve;
    }

    /**
     * @Description: //根据主依赖和被合并的依赖状态修改主依赖状态
     *               @Date： 2021/6/23
     * @Param：
     * @return：
     */
    @Override
    @Transactional
    public JSONObject shipmentsResurrection(JSONObject jsonParam, HttpServletRequest servletRequest) {
        String shipment_plan_id = jsonParam.getString("shipment_plan_id");
        String client_id = jsonParam.getString("client_id");
        String status_message = jsonParam.getString("status_message");
        // 出库表修改状态和删除状态
        try {
            shipmentsDao.recoverShipments(client_id, shipment_plan_id, status_message);
        } catch (Exception e) {
            logger.error("出库表出库状态和删除状态恢复失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.E_40132);
        }
        // 出库详细表修改状态和删除状态
        try {
            shipmentDetailDao.recoverShipments(client_id, shipment_plan_id);
        } catch (Exception e) {
            logger.error("出库详细表出库状态和删除状态恢复失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.E_40132);
        }
        // 出库新规
        try {
            shipmentDetailService.setShipmentDetail(jsonParam, false, servletRequest);
        } catch (Exception e) {
            logger.error("出库新规失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.E_40132);
        }
        return CommonUtils.success();
    }

    /**
     * 获取出库状态下的出库依赖数
     *
     * @param client_id
     * @param statusList
     * @param shipment_plan_id
     * @return
     */
    @Override
    public Integer getShipmentStatus(String client_id, List<Integer> statusList, String shipment_plan_id) {
        return shipmentsDao.getShipmentStatus(client_id, statusList, shipment_plan_id);
    }

    /**
     * 详细检索条件格式化
     *
     * @param jsonObj
     * @return
     */
    private JSONObject setSearchJson(JSONObject jsonObj) {

        // 处理模糊查询拼接%
        String search = jsonObj.getString("search");
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
            jsonObj.put("search", search);
        }

        String orderType = jsonObj.getString("orderType");
        if (!StringTools.isNullOrEmpty(orderType)) {
            orderType = orderType + "%";
            jsonObj.put("orderType", orderType);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String requestDateStart = jsonObj.getString("request_date_start");
        String requestDateEnd = jsonObj.getString("request_date_end");
        String shippingDateStart = jsonObj.getString("shipping_start_date");
        String shippingDateEnd = jsonObj.getString("shipping_end_date");
        String orderDatetimeStart = jsonObj.getString("order_datetime_start");
        String orderDatetimeEnd = jsonObj.getString("order_datetime_end");
        String deliveryDateStart = jsonObj.getString("delivery_date_start");
        String deliveryDateEnd = jsonObj.getString("delivery_date_end");
        JSONArray bundledFlg = jsonObj.getJSONArray("bundled_flg");
        JSONArray fileFlg = jsonObj.getJSONArray("file");
        String copy_flg = jsonObj.getString("copy_flg");

        // 添付資料
        String fileIsNull = null;
        if (!StringTools.isNullOrEmpty(fileFlg) && fileFlg.size() == 1) {
            fileIsNull = fileFlg.get(0).toString();
        }
        // 同梱物
        String bundledIsNull = null;
        if (!StringTools.isNullOrEmpty(bundledFlg) && bundledFlg.size() == 1) {
            bundledIsNull = bundledFlg.get(0).toString();
        }
        // 依赖主
        String[] Sponsors = null;
        if (!StringTools.isNullOrEmpty(jsonObj.getString("sponsor_id"))) {
            Sponsors = jsonObj.getString("sponsor_id").split(",");
        }
        jsonObj.put("sponsor_id", Sponsors);
        // 識別番号
        if (!StringTools.isNullOrEmpty(jsonObj.getString("identifier"))) {
            jsonObj.put("identifier", "%" + jsonObj.getString("identifier") + "%");
        }
        // 注文電話番号
        if (!StringTools.isNullOrEmpty(jsonObj.getString("order_phone_number"))) {
            jsonObj.put("order_phone_number", "%" + jsonObj.getString("order_phone_number").replace("-", "") + "%");
        }
        // 定期購入ID
        if (!StringTools.isNullOrEmpty(jsonObj.getString("buy_id"))) {
            jsonObj.put("buy_id", "%" + jsonObj.getString("buy_id") + "%");
        }
        // メールアドレス
        if (!StringTools.isNullOrEmpty(jsonObj.getString("email"))) {
            jsonObj.put("email", "%" + jsonObj.getString("email") + "%");
        }
        // 会社名
        if (!StringTools.isNullOrEmpty(jsonObj.getString("company"))) {
            jsonObj.put("company", "%" + jsonObj.getString("company") + "%");
        }
        // 注文者名
        if (!StringTools.isNullOrEmpty(jsonObj.getString("order_first_name"))) {
            jsonObj.put("order_first_name", "%" + (jsonObj.getString("order_first_name") + "%").replace(" ", ""));
        }
        // 出荷指示特記事項
        if (!StringTools.isNullOrEmpty(jsonObj.getString("instructions_special_notes"))) {
            jsonObj.put("instructions_special_notes", "%" + jsonObj.getString("instructions_special_notes") + "%");
        }
        // 確認メッセージ
        if (!StringTools.isNullOrEmpty(jsonObj.getString("status_message"))) {
            jsonObj.put("status_message", "%" + jsonObj.getString("status_message") + "%");
        }
        Date startDate = null, endDate = null, orderStartDate = null, orderEndDate = null, shippingStartDate = null,
            shippingEndDate = null, deliveryStartDate = null, deliveryEndDate = null;
        // 注文日時
        try {
            if (!StringTools.isNullOrEmpty(orderDatetimeStart)) {
                orderStartDate = format.parse(orderDatetimeStart);
            }
            if (!StringTools.isNullOrEmpty(orderDatetimeEnd)) {
                orderEndDate = format.parse(orderDatetimeEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 出荷予定日
        try {
            if (!StringTools.isNullOrEmpty(shippingDateStart)) {
                shippingStartDate = format.parse(shippingDateStart);
            }
            if (!StringTools.isNullOrEmpty(shippingDateEnd)) {
                shippingEndDate = format.parse(shippingDateEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // お届け指定日
        try {
            if (!StringTools.isNullOrEmpty(deliveryDateStart)) {
                deliveryStartDate = format.parse(deliveryDateStart);
            }
            if (!StringTools.isNullOrEmpty(deliveryDateEnd)) {
                deliveryEndDate = format.parse(deliveryDateEnd);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        jsonObj.put("order_datetime_start", orderStartDate);
        jsonObj.put("order_datetime_end", orderEndDate);
        jsonObj.put("shipping_start_date", shippingStartDate);
        jsonObj.put("shipping_end_date", shippingEndDate);
        jsonObj.put("delivery_date_start", deliveryStartDate);
        jsonObj.put("delivery_date_end", deliveryEndDate);
        jsonObj.put("file_flg", fileIsNull);
        jsonObj.put("bundled_flg", bundledIsNull);

        return jsonObj;
    }

    /**
     * 出库明细商品重组用于页面显示CSV下载
     *
     * @param shipmentDetails
     * @param client_id
     * @return
     */
    private List<Tw201_shipment_detail> setShipmentProductDetail(List<Tw201_shipment_detail> shipmentDetails,
        String client_id, String copy_flg, boolean detail_flg) {
        // 查询所有符合条件的出库依赖商品List
        List<Tw201_shipment_detail> shipmentDetailList = new ArrayList<>();

        // 处理set子商品
        Map<Integer, List<Tw201_shipment_detail>> shipmentDetailsMap = shipmentDetails.stream()
            .filter(x -> (!StringTools.isNullOrEmpty(x.getSet_sub_id()) && x.getSet_sub_id() > 0))
            .collect(groupingBy(Tw201_shipment_detail::getSet_sub_id));
        for (Map.Entry<Integer, List<Tw201_shipment_detail>> entry : shipmentDetailsMap.entrySet()) {
            List<Tw201_shipment_detail> detailList = entry.getValue();
            Tw201_shipment_detail tw201ShipmentDetail = detailList.get(0);

            List<Mc100_product> setProductList = productDao.getSetProductInfo(client_id, entry.getKey());
            if (StringTools.isNullOrEmpty(setProductList) || setProductList.size() == 0) {
                continue;
            }
            Mc100_product setProductInfo = setProductList.get(0);

            // 判断set商品的引当状态
            int reserveStatus = 1;
            for (Tw201_shipment_detail detail : detailList) {
                if (detail.getReserve_status() == 0) {
                    reserveStatus = 0;
                    break;
                }
            }

            // 获取set商品的子商品
            List<Mc103_product_set> productSetList = productDao.getSetProductDetail(client_id, entry.getKey());
            // 获取set子商品的引当件数
            for (Mc103_product_set productSet : productSetList) {
                for (Tw201_shipment_detail detail : detailList) {
                    if (productSet.getProduct_id().equals(detail.getProduct_id())) {
                        int inventory_cnt = productSet.getInventory_cnt();
                        int not_delivery = productSet.getNot_delivery();
                        // 出库优化，只有出库详细，编辑页面才需要
                        int total_reserve = 0;
                        if (detail_flg) {
                            total_reserve = getReserveCnt(detail.getClient_id(), detail.getReserve_cnt(),
                                detail.getProduct_id(), shipmentDetailDao, inventory_cnt, not_delivery, copy_flg);
                        }

                        productSet.setProduct_plan_cnt(detail.getProduct_plan_cnt());
                        productSet.setReserve_cnt(detail.getReserve_cnt());
                        productSet.setEdit_stock_cnt(total_reserve);

                        // 获取商品图片
                        List<Mc102_product_img> imgList = productDao.getProductImg(client_id, detail.getProduct_id());
                        productSet.setMc102_product_imgList(imgList);

                        List<String> serialNoList = new ArrayList<>();
                        String serial_no = detail.getSerial_no();
                        if (!StringTools.isNullOrEmpty(serial_no)) {
                            serialNoList = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(serial_no);
                        }
                        productSet.setSerialNoList(serialNoList);
                        break;
                    }
                }
            }

            List<String> serialNoList = new ArrayList<>();
            tw201ShipmentDetail.setSerialNoList(serialNoList);
            tw201ShipmentDetail.setClient_id(client_id);
            tw201ShipmentDetail.setProduct_id(setProductInfo.getProduct_id());
            tw201ShipmentDetail.setName(setProductInfo.getName());
            tw201ShipmentDetail.setCode(setProductInfo.getCode());
            tw201ShipmentDetail.setBarcode(setProductInfo.getBarcode());
            tw201ShipmentDetail.setSet_sub_id(setProductInfo.getSet_sub_id());
            tw201ShipmentDetail.setReserve_status(reserveStatus);
            tw201ShipmentDetail.setProduct_plan_cnt(tw201ShipmentDetail.getSet_cnt());

            // 获取商品图片
            List<Mc102_product_img> imgList = productDao.getProductImg(client_id, setProductInfo.getProduct_id());
            setProductList.get(0).setMc102_product_imgList(imgList);
            setProductList.get(0).setMc103_product_sets(productSetList);

            tw201ShipmentDetail.setMc100_product(setProductList);

            shipmentDetailList.add(tw201ShipmentDetail);
        }

        // 处理普通商品
        for (Tw201_shipment_detail detail : shipmentDetails) {
            if (!StringTools.isNullOrEmpty(detail.getSet_sub_id()) && detail.getSet_sub_id() > 0) {
                continue;
            }
            String[] product_id = {
                detail.getProduct_id()
            };
            List<Mc100_product> productList =
                productDao.getProductList(client_id, detail.getWarehouse_cd(), product_id, null,
                    null, 2, null, 0, 2, null, null, null);
            if (StringTools.isNullOrEmpty(productList) || productList.size() == 0) {
                continue;
            }

            Mc100_product productInfo = productList.get(0);
            int inventory_cnt = productInfo.getTw300_stock().getInventory_cnt();
            int not_delivery = productInfo.getTw300_stock().getNot_delivery();
            // 出库优化，只有出库详细，编辑页面才需要
            int total_reserve = 0;
            if (detail_flg) {
                total_reserve =
                    getReserveCnt(client_id, detail.getReserve_cnt(), detail.getProduct_id(), shipmentDetailDao,
                        inventory_cnt, not_delivery, copy_flg);
            }

            Tw201_shipment_detail tw201ShipmentDetail = detail;
            List<String> serialNoList = new ArrayList<>();
            String serial_no = detail.getSerial_no();
            if (!StringTools.isNullOrEmpty(serial_no)) {
                serialNoList = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(serial_no);
            }
            tw201ShipmentDetail.setSerialNoList(serialNoList);
            tw201ShipmentDetail.setClient_id(client_id);
            tw201ShipmentDetail.setName(productInfo.getName());
            tw201ShipmentDetail.setCode(productInfo.getCode());
            tw201ShipmentDetail.setBarcode(productInfo.getBarcode());
            tw201ShipmentDetail.setEdit_stock_cnt(total_reserve);

            tw201ShipmentDetail.setMc100_product(productList);
            shipmentDetailList.add(tw201ShipmentDetail);
        }

        return shipmentDetailList;
    }

}
