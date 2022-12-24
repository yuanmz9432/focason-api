package com.lemonico.wms.service.impl;

import static com.lemonico.core.utils.CommonUtils.checkIntNull;
import static java.util.stream.Collectors.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.csvreader.CsvReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.lemonico.api.EcforceAPI;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.dao.CustomerHistoryDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.*;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcBadRequestException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.BizLogiPredefinedEnum;
import com.lemonico.core.utils.constants.BizLogiResEnum;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.ShipmentsService;
import com.lemonico.store.service.impl.OrderServiceImpl;
import com.lemonico.store.service.impl.ShipmentsImpl;
import com.lemonico.wms.bean.ShimentListBean;
import com.lemonico.wms.dao.*;
import com.lemonico.wms.service.ShipmentService;
import com.lemonico.wms.service.WarehouseCustomerService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 倉庫側出荷依頼管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class ShipmentServiceImpl implements ShipmentService
{

    private final static Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    private final ShipmentDao shipmentDao;
    private final ShipmentResultDao shipmentResultDao;
    private final ShipmentResultDetailDao shipmentResultDetailDao;
    private final StocksResultDao stocksResultDao;
    private final ProductDao productDao;
    private final CustomerHistoryService customerHistoryService;
    private final ProductResultDao productResultDao;
    private final ShipmentDetailDao shipmentDetailDao;
    private final SponsorDao sponsorDao;
    private final StockDao stockDao;
    private final ShipmentsService shipmentsService;
    private final ShipmentsDao shipmentsDao;
    private final CommonService commonService;
    private final ProductSettingService productSettingService;
    private final WarehouseCustomerService warehouseCustomerService;
    private final ClientDao clientDao;
    private final CommonFunctionDao commonFunctionDao;
    private final ShipmentLocationDetailService shipmentLocationDetailService;
    private final ShipmentLocationDetailHistoryService shipmentLocationDetailHistoryService;
    private final WarehouseDao warehouseDao;
    private final DeliveryDao deliveryDao;
    private final OrderCancelDao orderCancelDao;
    private final WarehouseCustomerDao warehouseCustomerDao;
    private final CustomerHistoryDao customerHistoryDao;
    private final OrderApiDao orderApiDao;
    private final SettingDao settingDao;
    private final PathProps pathProps;

    @Value("${bizApiHost}")
    private String bizApiHost;

    @Value("${bizAuthCustomId}")
    private String bizAuthCustomId;

    @Value("${bizAuthCustomPwd}")
    private String bizAuthCustomPwd;

    /**
     * List对象去重
     *
     * @param orderList
     * @return
     */
    private static List<Tw210_shipment_result> removeDuplicateOrder(List<Tw210_shipment_result> orderList) {
        Set<Tw210_shipment_result> set = new TreeSet<Tw210_shipment_result>(new Comparator<Tw210_shipment_result>() {
            @Override
            public int compare(Tw210_shipment_result a, Tw210_shipment_result b) {
                return a.getWork_name().compareTo(b.getWork_name());
            }
        });
        set.addAll(orderList);
        return new ArrayList<Tw210_shipment_result>(set);
    }

    /**
     * @Param: jsonArrStr : 需要排序的数组
     * @param: key : 需要排序的 key值
     * @param: bool ： 顺序还是倒序 (true: 正序， false: 倒序)
     * @description: 对jsonArray 进行排序
     * @return: com.alibaba.fastjson.JSONArray
     */
    public static JSONArray jsonArraySort(String jsonArrStr, String key1, String key2, String key3, boolean bool) {
        // if(jsonArrStr == null){
        // return null;
        // }
        JSONArray jsonArr = JSON.parseArray(jsonArrStr);
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            // 排序key值
            private final String KEY_NAME_1 = key1;
            private final String KEY_NAME_2 = key2;
            private final String KEY_NAME_3 = key3;

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA_1;
                String valB_1;
                String valA_2;
                String valB_2;
                String valA_3;
                String valB_3;
                try {
                    // a这里是a、b需要处理的业务，需要根据你的规则进行修改。
                    valA_1 = a.getString(KEY_NAME_1);
                    valB_1 = b.getString(KEY_NAME_1);
                    valA_2 = a.getString(KEY_NAME_2);
                    valB_2 = b.getString(KEY_NAME_2);
                    valA_3 = a.getString(KEY_NAME_3);
                    valB_3 = b.getString(KEY_NAME_3);
                    if (valA_1 == null) {
                        valA_1 = "";
                    }
                    if (valB_1 == null) {
                        valB_1 = "";
                    }
                    if (valA_2 == null) {
                        valA_2 = "";
                    }
                    if (valB_2 == null) {
                        valB_2 = "";
                    }
                    if (valA_3 == null) {
                        valA_3 = "";
                    }
                    if (valB_3 == null) {
                        valB_3 = "";
                    }
                } catch (JSONException e) {
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                if (bool) {
                    int flg = -valB_1.compareTo(valA_1);
                    if (flg == 0) {
                        int flg1 = -valB_2.compareTo(valA_2);
                        if (flg1 == 0) {
                            return -valB_3.compareTo(valA_3);
                        } else {
                            return -valB_2.compareTo(valA_2);
                        }
                    } else {
                        return -valB_1.compareTo(valA_1);
                    }
                } else {
                    int flg = -valA_1.compareTo(valB_1);
                    if (flg == 0) {
                        int flg1 = -valA_2.compareTo(valB_2);
                        if (flg1 == 0) {
                            return -valA_3.compareTo(valB_3);
                        } else {
                            return -valA_2.compareTo(valB_2);
                        }
                    } else {
                        return -valA_1.compareTo(valB_1);
                    }
                }
            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    /**
     * @Description: 出庫依頼一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/06/30
     */
    @Override
    public JSONObject getShipmentsLists(JSONObject jsonObj) {
        // 检索条件
        jsonObj = setSearchJson(jsonObj);
        String warehouse_cd = jsonObj.getString("warehouse_cd");
        Integer kubun = jsonObj.getInteger("kubun");

        // 分页
        Integer currentPage = jsonObj.getInteger("currentPage");
        Integer pageSize = jsonObj.getInteger("pageSize");

        List<ShimentListBean> collect = new ArrayList<>();
        JSONObject resultJson = new JSONObject();

        // 出庫情報を取得
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        List<ShimentListBean> shipmentsList = shipmentDao.getShipmentsLists(jsonObj);

        PageInfo<ShimentListBean> pageInfo = null;
        long totalCnt = shipmentsList.size();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(shipmentsList);
            totalCnt = pageInfo.getTotal();
        }

        // 出庫情報が取得した場合、処理
        if (StringTools.isNullOrEmpty(shipmentsList) || shipmentsList.size() <= 0) {
            resultJson.put("result_data", collect);
            resultJson.put("total", totalCnt);

            return resultJson;
        }

        // 根据出库Id集合 查出所有的出库依赖Id 对应的明细中 系统中不存在的商品count
        List<String> shipmentIdList = jsonObj.getObject("shipmentIdList", List.class);
        List<Tw201_shipment_detail> detailKubun = new ArrayList<>();
        if (StringTools.isNullOrEmpty(shipmentIdList) || shipmentIdList.size() == 0) {
            shipmentIdList = shipmentsList.stream().map(ShimentListBean::getShipment_plan_id).distinct()
                .collect(Collectors.toList());
        }
        detailKubun = shipmentDetailDao.getShipmentDetailKubun(shipmentIdList);

        // 将所有的出库依赖明细 按照出库依赖分组
        Map<String, List<Tw201_shipment_detail>> detailMap =
            detailKubun.stream().collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));

        // 获取店铺信息
        List<String> clientIdList =
            shipmentsList.stream().map(ShimentListBean::getClient_id).distinct().collect(Collectors.toList());
        List<Ms201_client> clientList = clientDao.getManyClient(clientIdList);
        // 依赖主信息
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getClientSponsorList(clientIdList);
        // 配送时间带
        List<Ms006_delivery_time> deliveryTimeList = deliveryDao.getDeliveryTimeAllList();

        // 倉庫側に関する情報を取得
        Mw400_warehouse warehouse = warehouseDao.getInfoByWarehouseCd(warehouse_cd);
        // 获取所有受注取消中的出库依赖Id
        List<String> cancelIdList = orderCancelDao.getShipmentIdList(null);
        // 全てサイズの情報を取得
        HashMap<String, String> sizeMaps = new HashMap<String, String>();
        List<Ms010_product_size> sizeList = shipmentsDao.getAllSizeName();
        sizeList.forEach((list) -> {
            sizeMaps.put(list.getSize_cd(), list.getName());
        });
        // 全て配送業者の情報を取得
        HashMap<String, List<String>> deliveryMaps = new HashMap<String, List<String>>();
        List<Ms004_delivery> deliveryList = deliveryDao.getDeliveryAll();
        deliveryList.forEach((list) -> {
            // 创建一个数组
            ArrayList<String> items = new ArrayList<>();
            items.add(list.getDelivery_nm());
            items.add(list.getDelivery_code());
            deliveryMaps.put(list.getDelivery_cd(), items);
        });

        // 找到所有受注种别
        List<Ms017_csv_template> csvTmp = settingDao.getCsvTmp();
        // 识别番号为key值 模板名称为value
        Map<String, String> tmpMap = csvTmp.stream()
            .collect(Collectors.toMap(Ms017_csv_template::getIdentification, Ms017_csv_template::getTemplate));

        for (int s = 0; s < shipmentsList.size(); s++) {
            ShimentListBean shipments = shipmentsList.get(s);
            int cancelFlg = 0;
            if (!StringTools.isNullOrEmpty(cancelIdList)) {
                boolean contains = cancelIdList.contains(shipments.getShipment_plan_id());
                cancelFlg = contains ? 1 : 0;
            }
            shipments.setCancelFlg(cancelFlg);
            // サイズ情報をセット
            String size_cd = shipments.getSize_cd();
            String sizeName = sizeMaps.get(size_cd);
            shipments.setSize_name(sizeName);
            // 配送業者情報をセット
            String deliveryCarrier = shipments.getDelivery_carrier();
            List<String> delivery = deliveryMaps.get(deliveryCarrier);
            if (!StringTools.isNullOrEmpty(delivery) && delivery.size() > 1) {
                shipments.setDelivery_nm(delivery.get(0));
                shipments.setDelivery_code_csv(delivery.get(1));
            }
            if (!StringTools.isNullOrEmpty(warehouse)) {
                shipments.setWarehouse_nm(warehouse.getWarehouse_nm());
            }

            // 店铺信息
            for (Ms201_client client : clientList) {
                if (shipments.getClient_id().equals(client.getClient_id())) {
                    shipments.setClient_nm(client.getClient_nm());
                    shipments.setTnnm(client.getTnnm());
                    shipments.setBill_customer_cd(client.getBill_customer_cd());
                    shipments.setSagawa_nisugata_code(client.getSagawa_nisugata_code());
                    shipments.setFare_manage_cd(client.getFare_manage_cd());
                    shipments.setCustomer_code(client.getCustomer_code());
                    shipments.setDelivery_code(client.getDelivery_code());
                    shipments.setSeino_delivery_code(client.getSeino_delivery_code());
                    break;
                }
            }

            // 依赖主信息
            for (Ms012_sponsor_master sponsor : sponsorList) {
                if (shipments.getClient_id().equals(sponsor.getClient_id())
                    && shipments.getSponsor_id().equals(sponsor.getSponsor_id())) {
                    shipments.setMs012_sponsor_master(sponsor);
                    break;
                }
            }

            // 配送时间带
            for (Ms006_delivery_time deliveryTime : deliveryTimeList) {
                if (!StringTools.isNullOrEmpty(shipments.getDelivery_time_slot()) &&
                    shipments.getDelivery_time_slot().equals(deliveryTime.getDelivery_time_id().toString())) {
                    shipments.setDelivery_time_name(deliveryTime.getDelivery_time_name());
                    break;
                }
            }

            // 送り状番号をセット
            List<String> deliveryTrackingNmList = new ArrayList<>();
            // 获取到 伝票番号
            String delivery_tracking_nm = shipments.getDelivery_tracking_nm();
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
            shipments.setDelivery_tracking_nm_list(deliveryTrackingNmList);

            String identifier = shipments.getIdentifier();
            String orderIdent = "";
            if (!StringTools.isNullOrEmpty(identifier)) {
                String key = "";
                List<String> list = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(identifier);
                key = list.get(0);
                if (!StringTools.isNullOrEmpty(key)) {
                    key = key.replaceAll("[0-9]", "");
                }
                orderIdent = tmpMap.get(key);
            }
            shipments.setOrderIdent(orderIdent);
            String shipment_plan_id = shipments.getShipment_plan_id();
            if (detailMap.containsKey(shipment_plan_id)) {
                List<Tw201_shipment_detail> shipmentDetailList = detailMap.get(shipment_plan_id);
                // 假登陆商品
                int count = (int) shipmentDetailList.stream()
                    .filter(x -> !StringTools.isNullOrEmpty(x.getKubun()) && 9 == x.getKubun()).count();
                if (count != 0) {
                    kubun = Constants.NOT_LOGGED_PRODUCT;
                }

                Integer bundledCnt = 0;
                for (Tw201_shipment_detail detail : shipmentDetailList) {
                    if (detail.getKubun() == 1) {
                        bundledCnt += detail.getProduct_plan_cnt();
                    }
                }
                shipments.setBundled_cnt(bundledCnt);
            }
            shipments.setKubun(kubun);
        }

        if (shipmentsList.size() > 0) {
            collect = shipmentsList.stream()
                .sorted(Comparator.comparing(ShimentListBean::getCancelFlg).reversed()).collect(toList());
        }

        resultJson.put("result_data", collect);
        resultJson.put("total", totalCnt);

        return resultJson;
    }

    /**
     * @Description: 出庫依頼按照商品code分组
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2022/01/12
     */
    @Override
    public Tw200_shipment getShipmentsDetail(String warehouse_cd, String shipment_plan_id) {

        Tw200_shipment shipments = shipmentDao.getShipmentsDetail(warehouse_cd, shipment_plan_id);
        if (StringTools.isNullOrEmpty(shipments)) {
            return null;
        }

        // 获取所有受注取消中的出库依赖Id
        List<String> cancelIdList = orderCancelDao.getShipmentIdList(null);
        // 全てサイズの情報を取得
        HashMap<String, String> sizeMaps = new HashMap<String, String>();
        List<Ms010_product_size> sizeList = shipmentsDao.getAllSizeName();
        sizeList.forEach((list) -> {
            sizeMaps.put(list.getSize_cd(), list.getName());
        });
        // 全て配送業者の情報を取得
        HashMap<String, List<String>> deliveryMaps = new HashMap<String, List<String>>();
        List<Ms004_delivery> deliveryList = deliveryDao.getDeliveryAll();
        deliveryList.forEach((list) -> {
            // 创建一个数组
            ArrayList<String> items = new ArrayList<>();
            items.add(list.getDelivery_nm());
            items.add(list.getDelivery_code());
            deliveryMaps.put(list.getDelivery_cd(), items);
        });

        // 找到所有受注种别
        List<Ms017_csv_template> csvTmp = settingDao.getCsvTmp();
        // 识别番号为key值 模板名称为value
        Map<String, String> tmpMap = csvTmp.stream()
            .collect(Collectors.toMap(Ms017_csv_template::getIdentification, Ms017_csv_template::getTemplate));

        int cancelFlg = 0;
        if (!StringTools.isNullOrEmpty(cancelIdList)) {
            boolean contains = cancelIdList.contains(shipments.getShipment_plan_id());
            cancelFlg = contains ? 1 : 0;
        }
        shipments.setCancelFlg(cancelFlg);
        // 個口数
        if (StringTools.isNullOrEmpty(shipments.getBoxes())) {
            shipments.setBoxes(1);
        }

        // サイズ情報をセット
        String size_cd = shipments.getSize_cd();
        String sizeName = sizeMaps.get(size_cd);
        shipments.setSize_name(sizeName);
        // 配送業者情報をセット
        String deliveryCarrier = shipments.getDelivery_carrier();
        List<String> delivery = deliveryMaps.get(deliveryCarrier);
        if (!StringTools.isNullOrEmpty(delivery) && delivery.size() > 1) {
            shipments.setDelivery_nm(delivery.get(0));
            shipments.setDelivery_code_csv(delivery.get(1));
        }

        String identifier = shipments.getIdentifier();
        String orderIdent = "";
        if (!StringTools.isNullOrEmpty(identifier)) {
            String key = "";
            List<String> list = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(identifier);
            key = list.get(0);
            if (!StringTools.isNullOrEmpty(key)) {
                key = key.replaceAll("[0-9]", "");
            }
            orderIdent = tmpMap.get(key);
        }
        shipments.setOrderIdent(orderIdent);

        // 送り状番号をセット
        List<String> deliveryTrackingNmList = new ArrayList<>();
        // 获取到 伝票番号
        String delivery_tracking_nm = shipments.getDelivery_tracking_nm();
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
        shipments.setDelivery_tracking_nm_list(deliveryTrackingNmList);

        // 获取依赖主信息
        List<Ms012_sponsor_master> sponsorMaster =
            sponsorDao.getSponsorList(shipments.getClient_id(), false, shipments.getSponsor_id());
        shipments.setMs012_sponsor_master(sponsorMaster.get(0));

        // 本次出库依赖的商品List
        boolean cancle_flg = shipments.getDel_flg() == 1;
        List<Tw201_shipment_detail> shipmentDetails = shipmentDetailDao.getShipmentDetailList(shipments.getClient_id(),
            shipments.getShipment_plan_id(), cancle_flg);

        List<Tw201_shipment_detail> shipmentDetailList =
            setShipmentProductDetail(shipmentDetails, shipments.getClient_id());
        shipments.setTw201_shipment_detail(shipmentDetailList);

        return shipments;
    }

    /**
     * @Description: 出庫依頼按照商品code分组
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/11/2
     */
    @Override
    public JSONObject getShipmentsGroupList(JSONObject jsonObj) {
        // 检索条件格式化
        jsonObj = setSearchJson(jsonObj);

        // 出庫情報を取得
        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsList(jsonObj);

        // 定义map存放分组数据
        HashMap<String, List<JSONObject>> shipmentsMap = new HashMap<>();
        List<JSONObject> shipmentsJSONObjectList = new ArrayList<>();
        for (Tw200_shipment shipment : shipmentsList) {
            // 存放商品code
            List<String> codeList = new ArrayList<>();
            List<JSONObject> shipmentsObjectList = new ArrayList<>();
            // 定义对象存放出库信息
            JSONObject shipmentsObject = new JSONObject();
            shipmentsObject.put("bill_barcode", shipment.getBill_barcode());
            shipmentsObject.put("client_id", shipment.getClient_id());
            shipmentsObject.put("identifier", shipment.getIdentifier());
            shipmentsObject.put("payment_id", shipment.getPayment_id());
            shipmentsObject.put("shipment_plan_id", shipment.getShipment_plan_id());
            shipmentsObject.put("total_cnt", shipment.getProduct_plan_total());
            shipmentsObject.put("warehouse_cd", shipment.getWarehouse_cd());
            // shipmentsObjectList.add(shipmentsObject);
            // 取出商品code
            for (Tw201_shipment_detail shipment_detail : shipment.getTw201_shipment_detail()) {
                List<Mc100_product> product_list = shipment_detail.getMc100_product();
                for (int i = 0; i < product_list.size(); i++) {
                    if (!StringTools.isNullOrEmpty(product_list.get(i).getCode())) {
                        codeList.add(product_list.get(i).getCode());
                    } else {
                        codeList.add("-");
                    }

                }
            }
            Collections.sort(codeList);
            String codeStr = Joiner.on("/").join(codeList);
            if (shipmentsMap.containsKey(codeStr)) {
                List<JSONObject> existObject = shipmentsMap.get(codeStr);
                existObject.add(shipmentsObject);
            } else {
                shipmentsObjectList.add(shipmentsObject);
                shipmentsMap.put(codeStr, shipmentsObjectList);
            }
        }

        for (Map.Entry<String, List<JSONObject>> entry : shipmentsMap.entrySet()) {
            JSONObject shipmentsObjectTmp = new JSONObject();
            shipmentsObjectTmp.put("code", entry.getKey());
            shipmentsObjectTmp.put("shipments", entry.getValue());
            shipmentsObjectTmp.put("cout", entry.getValue().size());
            shipmentsJSONObjectList.add(shipmentsObjectTmp);
        }

        return CommonUtils.success(shipmentsJSONObjectList);
    }

    /**
     * 详细检索条件格式化
     *
     * @param jsonObj
     * @return
     */
    private JSONObject setSearchJson(JSONObject jsonObj) {

        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String shippingStartDate = jsonObj.getString("shipping_start_date");
        String shippingEndDate = jsonObj.getString("shipping_end_date");
        String cashOnDelivery = jsonObj.getString("cash_on_delivery");
        String orderDatetimeStart = jsonObj.getString("order_datetime_start");
        String orderDatetimeEnd = jsonObj.getString("order_datetime_end");
        String requestDateStart = jsonObj.getString("request_date_start");
        String requestDateEnd = jsonObj.getString("request_date_end");
        String shipmentPlanDateStart = jsonObj.getString("shipment_plan_date_start");
        String shipmentPlanDateEnd = jsonObj.getString("shipment_plan_date_end");
        String deliveryDateStart = jsonObj.getString("delivery_date_start");
        String deliveryDateEnd = jsonObj.getString("delivery_date_end");
        String minDeliveryDateStart = jsonObj.getString("min_delivery_date");
        String maxDeliveryDateEnd = jsonObj.getString("max_delivery_date");
        String updDateEnd = jsonObj.getString("upd_date_end");
        String updDateStart = jsonObj.getString("upd_date_start");

        JSONArray bundledFlg = jsonObj.getJSONArray("bundled_flg");
        JSONArray fileFlg = jsonObj.getJSONArray("file");
        String shipment_status = jsonObj.getString("shipment_status");

        List<Integer> statusList = new ArrayList<>();
        // a判断是否是出荷作业中的页面
        if (!StringTools.isNullOrEmpty(shipment_status)) {
            if (shipment_status.length() != 1) {
                List<String> list = Splitter.on(",").splitToList(shipment_status);
                statusList = list.stream().map(Integer::parseInt).collect(toList());
            } else {
                statusList.add(Integer.valueOf(shipment_status));
            }
        }
        jsonObj.put("statusList", statusList);

        // 添付資料
        String fileIsNull = null;
        if (!StringTools.isNullOrEmpty(fileFlg) && fileFlg.size() == 1) {
            fileIsNull = fileFlg.get(0).toString();
        }
        jsonObj.put("file_flg", fileIsNull);

        // 同梱物
        Integer kubun = null;
        if (!StringTools.isNullOrEmpty(bundledFlg) && bundledFlg.size() == 1 && bundledFlg.getInteger(0) == 1) {
            kubun = Constants.BUNDLED;
        } else if (!StringTools.isNullOrEmpty(bundledFlg) && bundledFlg.size() == 1 && bundledFlg.getInteger(0) == 0) {
            kubun = Constants.ORDINARY_PRODUCT;
        }
        jsonObj.put("kubun", kubun);

        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(jsonObj.getString("keyword"))) {
            jsonObj.put("keyword", "%" + jsonObj.getString("keyword") + "%");
        }
        // 識別番号
        if (!StringTools.isNullOrEmpty(jsonObj.getString("identifier"))) {
            jsonObj.put("identifier", "%" + jsonObj.getString("identifier") + "%");
        }
        // 注文電話番号
        if (!StringTools.isNullOrEmpty(jsonObj.getString("order_phone_number"))) {
            jsonObj.put("order_phone_number", "%" + jsonObj.getString("order_phone_number") + "%");
        }
        // 注文名
        if (!StringTools.isNullOrEmpty(jsonObj.getString("order_first_name"))) {
            jsonObj.put("order_first_name", "%" + jsonObj.getString("order_first_name") + "%");
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
        // 出荷指示特記事項
        if (!StringTools.isNullOrEmpty(jsonObj.getString("instructions_special_notes"))) {
            jsonObj.put("instructions_special_notes", "%" + jsonObj.getString("instructions_special_notes") + "%");
        }
        // 確認メッセージ
        if (!StringTools.isNullOrEmpty(jsonObj.getString("status_message"))) {
            jsonObj.put("status_message", "%" + jsonObj.getString("status_message") + "%");
        }
        // 店舗区分
        if (!StringTools.isNullOrEmpty(jsonObj.getString("orderType"))) {
            jsonObj.put("orderType", jsonObj.getString("orderType") + "%");
        }
        Integer cash_on_delivery = null;
        if (!StringTools.isNullOrEmpty(cashOnDelivery)) {
            cash_on_delivery = Integer.valueOf(cashOnDelivery);
        }
        jsonObj.put("cash_on_delivery", cash_on_delivery);

        Date startDate = null, endDate = null, orderStartDate = null, orderEndDate = null, requestStartDate = null,
            requestEndDate = null, shipmentPlanStartDate = null, shipmentPlanEndDate = null, deliveryStartDate = null,
            deliveryEndDate = null, deliveryMinDate = null, deliveryMaxDate = null, updStartDate = null,
            updEndDate = null;

        // 出荷予定日
        try {
            if (!StringTools.isNullOrEmpty(shippingStartDate)) {
                startDate = date_format.parse(shippingStartDate);
                startDate = CommonUtils.getDateStar(startDate);
            }
            if (!StringTools.isNullOrEmpty(shippingEndDate)) {
                endDate = date_format.parse(shippingEndDate);
                endDate = CommonUtils.getDateEnd(endDate);
            }
            jsonObj.put("shipping_start_date", startDate);
            jsonObj.put("shipping_end_date", endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 注文日時
        try {
            if (!StringTools.isNullOrEmpty(orderDatetimeStart)) {
                orderStartDate = date_format.parse(orderDatetimeStart);
            }
            if (!StringTools.isNullOrEmpty(orderDatetimeEnd)) {
                orderEndDate = date_format.parse(orderDatetimeEnd);
            }
            jsonObj.put("order_datetime_start", orderStartDate);
            jsonObj.put("order_datetime_end", orderEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 出庫依頼日
        try {
            if (!StringTools.isNullOrEmpty(requestDateStart)) {
                requestStartDate = date_format.parse(requestDateStart);
            }
            if (!StringTools.isNullOrEmpty(requestDateEnd)) {
                requestEndDate = date_format.parse(requestDateEnd);
            }
            jsonObj.put("request_date_start", requestStartDate);
            jsonObj.put("request_date_end", requestEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 出库取消日
        try {
            if (!StringTools.isNullOrEmpty(updDateEnd)) {
                updEndDate = date_format.parse(updDateEnd);
            }
            if (!StringTools.isNullOrEmpty(updDateStart)) {
                updStartDate = date_format.parse(updDateStart);
            }
            jsonObj.put("upd_date_start", updStartDate);
            jsonObj.put("upd_date_end", updEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 出荷日
        try {
            if (!StringTools.isNullOrEmpty(shipmentPlanDateStart)) {
                shipmentPlanStartDate = date_format.parse(shipmentPlanDateStart);
            }
            if (!StringTools.isNullOrEmpty(shipmentPlanDateEnd)) {
                shipmentPlanEndDate = date_format.parse(shipmentPlanDateEnd);
            }
            jsonObj.put("shipment_plan_date_start", shipmentPlanStartDate);
            jsonObj.put("shipment_plan_date_end", shipmentPlanEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // お届け指定日
        try {
            if (!StringTools.isNullOrEmpty(deliveryDateStart)) {
                deliveryStartDate = date_format.parse(deliveryDateStart);
            }
            if (!StringTools.isNullOrEmpty(deliveryDateEnd)) {
                deliveryEndDate = date_format.parse(deliveryDateEnd);
            }
            jsonObj.put("delivery_date_start", deliveryStartDate);
            jsonObj.put("delivery_date_end", deliveryEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 本月时间
        try {
            if (!StringTools.isNullOrEmpty(minDeliveryDateStart)) {
                deliveryMinDate = date_format.parse(minDeliveryDateStart);
            }
            if (!StringTools.isNullOrEmpty(maxDeliveryDateEnd)) {
                deliveryMaxDate = date_format.parse(maxDeliveryDateEnd);
            }
            jsonObj.put("min_delivery_date", deliveryMinDate);
            jsonObj.put("max_delivery_date", deliveryMaxDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    /**
     * @Description: 出庫依頼一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/06/29
     */
    @Override
    public List<ShimentListBean> getShipmentsCsvList(JSONObject jsonObj) {
        // 检索条件格式化
        jsonObj = setSearchJson(jsonObj);
        String warehouse_cd = jsonObj.getString("warehouse_cd");
        Integer csv_flg = jsonObj.getInteger("csv_flg");

        // 出庫情報を取得
        List<ShimentListBean> shipmentsList = shipmentDao.getShipmentsLists(jsonObj);
        // 出庫情報が取得した場合、処理
        if (StringTools.isNullOrEmpty(shipmentsList) || shipmentsList.size() == 0) {
            return new ArrayList<>();
        }

        // 获取店铺信息
        List<String> clientIdList =
            shipmentsList.stream().map(ShimentListBean::getClient_id).distinct().collect(Collectors.toList());
        List<Ms201_client> clientList = clientDao.getManyClient(clientIdList);
        // 依赖主信息
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getClientSponsorList(clientIdList);
        // 配送时间带
        List<Ms006_delivery_time> deliveryTimeList = deliveryDao.getDeliveryTimeAllList();

        // 倉庫側に関する情報を取得
        Mw400_warehouse warehouse = warehouseDao.getInfoByWarehouseCd(warehouse_cd);
        // 全てサイズの情報を取得
        HashMap<String, String> sizeMaps = new HashMap<String, String>();
        List<Ms010_product_size> sizeList = shipmentsDao.getAllSizeName();
        sizeList.forEach((list) -> {
            sizeMaps.put(list.getSize_cd(), list.getName());
        });
        // 全て配送業者の情報を取得
        HashMap<String, List<String>> deliveryMaps = new HashMap<String, List<String>>();
        List<Ms004_delivery> deliveryList = deliveryDao.getDeliveryAll();
        deliveryList.forEach((list) -> {
            // 创建一个数组
            ArrayList<String> items = new ArrayList<>();
            items.add(list.getDelivery_nm());
            items.add(list.getDelivery_code());
            deliveryMaps.put(list.getDelivery_cd(), items);
        });
        // 获取所有受注取消中的出库依赖Id
        List<String> cancelIdList = orderCancelDao.getShipmentIdList(null);

        // 获取配送公司ID
        String companys = jsonObj.getString("companys");
        List<String> deliveryCdList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(companys)) {
            String compay_name = "";
            switch (companys) {
                case "sagawa":
                    compay_name = "佐川急便";
                    break;
                case "japanpost":
                    compay_name = "日本郵便";
                    break;
                case "yamato":
                    compay_name = "ヤマト運輸";
                    break;
                case "fushan":
                    compay_name = "福山通運";
                    break;
                case "seino":
                    compay_name = "西濃運輸";
                    break;
            }
            List<Ms004_delivery> deliveryAll = deliveryDao.getDeliveryAll();
            for (Ms004_delivery delivery : deliveryAll) {
                if (compay_name.equals(delivery.getDelivery_nm())) {
                    deliveryCdList.add(delivery.getDelivery_cd());
                }
            }
        }

        for (int s = 0; s < shipmentsList.size(); s++) {
            ShimentListBean shipments = shipmentsList.get(s);
            if (deliveryCdList.size() > 0 && !deliveryCdList.contains(shipments.getDelivery_carrier())) {
                shipments = null;
                continue;
            }
            // 個口数
            if (StringTools.isNullOrEmpty(shipments.getBoxes())) {
                shipments.setBoxes(1);
            }

            int cancelFlg = 0;
            if (!StringTools.isNullOrEmpty(cancelIdList)) {
                boolean contains = cancelIdList.contains(shipments.getShipment_plan_id());
                cancelFlg = contains ? 1 : 0;
            }
            shipments.setCancelFlg(cancelFlg);

            // サイズ情報をセット
            String size_cd = shipments.getSize_cd();
            String sizeName = sizeMaps.get(size_cd);
            shipments.setSize_name(sizeName);
            // 配送業者情報をセット
            String deliveryCarrier = shipments.getDelivery_carrier();
            List<String> delivery = deliveryMaps.get(deliveryCarrier);
            if (!StringTools.isNullOrEmpty(delivery) && delivery.size() > 1) {
                shipments.setDelivery_nm(delivery.get(0));
                shipments.setDelivery_code_csv(delivery.get(1));
            }
            if (!StringTools.isNullOrEmpty(warehouse)) {
                shipments.setWarehouse_nm(warehouse.getWarehouse_nm());
            }

            // 店铺信息
            for (Ms201_client client : clientList) {
                if (shipments.getClient_id().equals(client.getClient_id())) {
                    shipments.setClient_nm(client.getClient_nm());
                    shipments.setTnnm(client.getTnnm());
                    shipments.setBill_customer_cd(client.getBill_customer_cd());
                    shipments.setSagawa_nisugata_code(client.getSagawa_nisugata_code());
                    shipments.setFare_manage_cd(client.getFare_manage_cd());
                    shipments.setCustomer_code(client.getCustomer_code());
                    shipments.setDelivery_code(client.getDelivery_code());
                    shipments.setSeino_delivery_code(client.getSeino_delivery_code());
                    break;
                }
            }

            // 依赖主信息
            for (Ms012_sponsor_master sponsor : sponsorList) {
                if (shipments.getClient_id().equals(sponsor.getClient_id())
                    && shipments.getSponsor_id().equals(sponsor.getSponsor_id())) {
                    shipments.setMs012_sponsor_master(sponsor);
                    break;
                }
            }

            // 配送时间带
            for (Ms006_delivery_time deliveryTime : deliveryTimeList) {
                if (!StringTools.isNullOrEmpty(shipments.getDelivery_time_slot()) &&
                    shipments.getDelivery_time_slot().equals(deliveryTime.getDelivery_time_id().toString())) {
                    shipments.setDelivery_time_name(deliveryTime.getDelivery_time_name());
                    break;
                }
            }

            // 受注信息获取
            String orderPhone = "";
            if (!StringTools.isNullOrEmpty(shipments.getOrder_phone_number1())) {
                orderPhone += shipments.getOrder_phone_number1() + '-';
            }
            if (!StringTools.isNullOrEmpty(shipments.getOrder_phone_number2())) {
                orderPhone += shipments.getOrder_phone_number2() + '-';
            }
            if (!StringTools.isNullOrEmpty(shipments.getOrder_phone_number3())) {
                orderPhone += shipments.getOrder_phone_number3() + '-';
            }
            if (!StringTools.isNullOrEmpty(orderPhone)) {
                orderPhone = orderPhone.substring(0, orderPhone.length() - 1);
            } else {
                orderPhone = "-";
            }
            shipments.setOrder_phone_number1(orderPhone);

            // 明細単位 获取出库依赖商品明细
            if (!StringTools.isNullOrEmpty(csv_flg) && csv_flg == 1) {
                // 本次出库依赖的商品List
                boolean cancel_flg =
                    !StringTools.isNullOrEmpty(shipments.getShipment_status()) && shipments.getShipment_status() == 999;
                List<Tw201_shipment_detail> shipmentDetails = shipmentDetailDao
                    .getShipmentDetailList(shipments.getClient_id(), shipments.getShipment_plan_id(), cancel_flg);
                List<Tw201_shipment_detail> shipmentDetailList =
                    setShipmentProductDetail(shipmentDetails, shipments.getClient_id());
                shipments.setTw201_shipment_detail(shipmentDetailList);
            }

            // TODO 需要确认逻辑
            // shipments.setSubtotal_amount_tax(commodityTotal.intValue());
        }

        List<ShimentListBean> collect = new ArrayList<>();
        if (shipmentsList.size() > 0) {
            collect = shipmentsList.stream()
                .sorted(Comparator.comparing(ShimentListBean::getCancelFlg).reversed()).collect(toList());
        }

        return collect;
    }

    /**
     * @param ids 出库id
     * @description 根据出库id获取出库数据
     * @return: List
     * @date 2022/1/10
     **/
    @Override
    public List<Tw200_shipment> getShipmentsListByPlanIds(List<String> ids) {
        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsListByPlanIds(ids);
        return shipmentsList;
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    private List<Tw201_shipment_detail> setShipmentProductDetail(List<Tw201_shipment_detail> shipmentDetails,
        String client_id) {

        // 查询所有符合条件的出库依赖商品List
        List<Tw201_shipment_detail> shipmentDetailList = new ArrayList<>();

        // 税拔集合
        List<Integer> list = Arrays.asList(1, 10, 11, 12);
        // 商品合计(税拔)
        BigDecimal commodityTotal = new BigDecimal(0);

        // 处理set子商品
        Map<Integer, List<Tw201_shipment_detail>> shipmentDetailsMap = shipmentDetails.stream()
            .filter(x -> (!StringTools.isNullOrEmpty(x.getSet_sub_id()) && x.getSet_sub_id() > 0))
            .collect(groupingBy(Tw201_shipment_detail::getSet_sub_id));
        for (Map.Entry<Integer, List<Tw201_shipment_detail>> entry : shipmentDetailsMap.entrySet()) {
            List<Tw201_shipment_detail> detailList = entry.getValue();
            Tw201_shipment_detail tw201ShipmentDetail = detailList.get(0);

            // 判断是否为轻减税率商品
            Integer is_reduced_tax = tw201ShipmentDetail.getIs_reduced_tax();
            double tax = (StringTools.isNullOrEmpty(is_reduced_tax) || is_reduced_tax == 0) ? 0.1 : 0.08;
            int tax_flag = tw201ShipmentDetail.getTax_flag();
            // 单价
            BigDecimal bigUnitPrice = new BigDecimal(tw201ShipmentDetail.getUnit_price());
            // 出库依赖数
            BigDecimal bigProductPlanCnt = new BigDecimal(tw201ShipmentDetail.getProduct_plan_cnt());
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
            tw201ShipmentDetail.setTaxDivision(taxDivision);

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
                        productSet.setReserve_cnt(detail.getReserve_cnt());
                        productSet.setProduct_plan_cnt(detail.getProduct_plan_cnt());

                        // 获取商品图片
                        List<Mc102_product_img> imgList =
                            productDao.getProductImg(detail.getClient_id(), detail.getProduct_id());
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

            String[] product_id = {
                detail.getProduct_id()
            };
            List<Mc100_product> productList =
                productDao.getProductList(detail.getClient_id(), detail.getWarehouse_cd(), product_id, null,
                    null, 2, null, 0, 2, null, null, null);
            if (StringTools.isNullOrEmpty(productList) || productList.size() == 0) {
                continue;
            }
            Mc100_product productInfo = productList.get(0);

            Tw201_shipment_detail tw201ShipmentDetail = detail;
            List<String> serialNoList = new ArrayList<>();
            String serial_no = detail.getSerial_no();
            if (!StringTools.isNullOrEmpty(serial_no)) {
                serialNoList = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(serial_no);
            }
            tw201ShipmentDetail.setSerialNoList(serialNoList);
            tw201ShipmentDetail.setClient_id(detail.getClient_id());
            tw201ShipmentDetail.setName(productInfo.getName());
            tw201ShipmentDetail.setCode(productInfo.getCode());
            tw201ShipmentDetail.setBarcode(productInfo.getBarcode());

            tw201ShipmentDetail.setMc100_product(productList);
            shipmentDetailList.add(tw201ShipmentDetail);
        }

        return shipmentDetailList;
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    @Override
    public List<Tw200_shipment> getShipmentsIncidents(String warehouse_cd, String shipment_plan_id) {

        Tw200_shipment shipments = shipmentDao.getShipmentsDetail(warehouse_cd, shipment_plan_id);
        if (StringTools.isNullOrEmpty(shipments)) {
            return null;
        }
        List<Tw200_shipment> shipmentsList = new ArrayList<>();

        // 出库依赖商品明细
        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentDetailList(shipments.getClient_id(), shipment_plan_id, false);
        List<Tw201_shipment_detail> shipmentDetailList =
            setShipmentProductDetail(shipmentDetails, shipments.getClient_id());
        shipments.setTw201_shipment_detail(shipmentDetailList);
        shipmentsList.add(shipments);

        return shipmentsList;
    }

    /**
     * 出庫検品更新シリアル番号
     *
     * @param jsonObject
     * @return
     * @Date: 2021/10/22
     */
    @Override
    public JSONObject updateSerialNo(JSONObject jsonObject) {

        List<String> productSerialNo = shipmentDao.getProductSerialNo(jsonObject);
        String serialNo = jsonObject.getString("serial_no");

        // 之前商品的シリアル番号
        String beforeSerialNo = "";
        if (!StringTools.isNullOrEmpty(productSerialNo) && !productSerialNo.isEmpty()) {
            beforeSerialNo = productSerialNo.get(0);
        }

        if (!StringTools.isNullOrEmpty(beforeSerialNo) && !StringTools.isNullOrEmpty(serialNo)) {
            serialNo = beforeSerialNo + "/" + serialNo;
        }
        jsonObject.put("serial_no", serialNo);
        try {
            shipmentDao.updateSerialNo(jsonObject);
        } catch (Exception e) {
            logger.error("修改シリアル番号失败 数据={}", jsonObject);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * @param warehouseCd 仓库CD
     * @param shipmentPlanId 出库ID
     * @return null
     * @description ntm检品修改配送公司
     * @description ntm检品修改配送公司
     * @date 2021/7/19
     **/
    @Override
    public void changeNtmDeliveryMethod(String warehouseCd, String shipmentPlanId) {
        shipmentDao.changeNtmShipmentDeliveryMethod(warehouseCd, shipmentPlanId);
    }

    /**
     * @param shipmentPlanId 出库id
     * @param shipmentStatus 出库状态
     * @param statusMessage 信息
     * @description 更新eccube状态数据
     * @return: null
     * @date 2022/1/10
     **/
    @Override
    public void asyncNtmEccubeStatusMessage(String shipmentPlanId, Integer shipmentStatus, String statusMessage) {
        shipmentDao.changeNtmEccubeStatusMessage(shipmentPlanId, shipmentStatus, statusMessage);
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/11/10
     */
    @Override
    public JSONObject updateShipmentsIncidents(String warehouse_cd, String shipment_plan_id, Integer shipment_status,
        String sizeName, Integer boxes, HttpServletRequest servletRequest, String user_id) {
        // a获取仓库信息
        Mw400_warehouse warehouseInfo = warehouseCustomerDao.getWarehouseInfo(warehouse_cd);

        String[] shipmentPlanId = {
            shipment_plan_id
        };
        try {
            setShipmentStatus(servletRequest, warehouse_cd, shipment_status, shipmentPlanId, sizeName,
                "", null, false, shipment_status.toString(), user_id);
        } catch (Exception e) {
            logger.debug("出庫検品失敗しました、時間：" + CommonUtils.getNewDate(null) + " 出庫依頼ID:" + shipment_plan_id + " 依頼ステータス:"
                + shipment_status);
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (shipment_status == 5) {
            return CommonUtils.success();
        }
        boolean isNtmFlg = hasNtmFunction(warehouse_cd, null, "4");

        // 個口数編集
        if (StringTools.isNullOrEmpty(boxes)) {
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 个口数变更时获取配送会社和都道府県
        List<Tw200_shipment> shipmentList = shipmentDao.getShipmentCarrier(warehouse_cd, shipmentPlanId);
        List<Tw216_delivery_fare> deliveryFareList = shipmentDao.getDeliveryFare();

        for (int i = 0; i < shipmentList.size(); i++) {
            Tw200_shipment shipment = shipmentList.get(i);
            String delivery_carrier = shipment.getDelivery_carrier();

            if (!StringTools.isNullOrEmpty(shipment.getSize_cd())) {
                Integer size_nm = Integer.parseInt(shipment.getSize_cd());
                if ("ヤマト運輸宅急便".equals(delivery_carrier)) {
                    delivery_carrier += size_nm.toString();
                } else if ("佐川急便飛脚宅配便".equals(delivery_carrier)) {
                    delivery_carrier = "佐川急便宅配便" + size_nm;
                }
            }

            Tw200_shipment shipmentsBoxs = new Tw200_shipment();
            shipmentsBoxs.setWarehouse_cd(warehouse_cd);
            shipmentsBoxs.setShipment_plan_id(shipment.getShipment_plan_id());
            shipmentsBoxs.setBoxes(boxes);
            // ntm 仓库
            if (isNtmFlg) {
                // NTM-运费
                int freight = 0;
                for (Tw216_delivery_fare row : deliveryFareList) {
                    String delivery_name = row.getAgent_id() + row.getMethod();
                    if (delivery_name.equals(delivery_carrier) && row.getRegion().equals(shipment.getPrefecture())) {
                        freight = row.getActual_price() * boxes;
                        break;
                    }
                }
                shipmentsBoxs.setFreight(freight);
                shipmentsBoxs.setPacking(700 * boxes);
            }
            shipmentDao.updateBoxes(shipmentsBoxs);
        }

        // 検品済み
        String filePC = warehouse_cd + "/smart/" + DateUtils.getDateMonth() + "/";
        String csvPath = pathProps.getWms() + filePC;
        String filePath = pathProps.getRoot() + csvPath;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "";

        List<Mw406_wh_smartcat> smartCats = warehouseCustomerService.getSmartCatList(warehouse_cd, 1);
        String localePath = "";
        if (smartCats.size() > 0) {
            localePath = smartCats.get(0).getFile_path();
            String fileStart = smartCats.get(0).getFile_start();
            if (!StringTools.isNullOrEmpty(fileStart)) {
                fileName = fileStart + "_";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmm");
            Date date = new Date();
            String dateTime = simpleDateFormat.format(date);
            fileName += shipment_plan_id + "_" + dateTime;
            String fileEnd = smartCats.get(0).getFile_end();
            if (!StringTools.isNullOrEmpty(fileEnd)) {
                fileName += "_" + fileEnd;
            }
            fileName += ".csv";
        } else {
            return CommonUtils.success(ErrorCode.E_60002);
        }

        String rule = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

        Tw200_shipment shipment = shipmentDao.getShipmentsDetail(warehouse_cd, shipment_plan_id);
        if (StringTools.isNullOrEmpty(shipment)) {
            return CommonUtils.success(ErrorCode.E_60002);
        }

        // 获取依赖主信息
        List<Ms012_sponsor_master> sponsorMaster =
            sponsorDao.getSponsorList(shipment.getClient_id(), false, shipment.getSponsor_id());
        shipment.setMs012_sponsor_master(sponsorMaster.get(0));

        Ms004_delivery delivery = deliveryDao.getDeliveryById(shipment.getDelivery_carrier());
        csvPath = csvPath + fileName;

        if (delivery.getDelivery_nm() == "佐川急便" || "佐川急便".equals(delivery.getDelivery_nm())
            || "佐川コンビニ受取".equals(delivery.getDelivery_nm())) {
            this.sagawaCsv(shipment, rule, filePath + fileName, delivery.getDelivery_method_csv(), warehouseInfo);
        }
        if (delivery.getDelivery_nm() == "日本郵便" || "日本郵便".equals(delivery.getDelivery_nm())) {
            this.japanpostCsv(shipment, rule, filePath + fileName, delivery.getDelivery_method_csv());
        }
        if (delivery.getDelivery_nm() == "ヤマト運輸" || "ヤマト運輸".equals(delivery.getDelivery_nm())) {
            this.yamatoCsv(shipment, rule, filePath + fileName, delivery.getDelivery_method_csv());
        }
        if (delivery.getDelivery_nm() == "福山通運" || "福山通運".equals(delivery.getDelivery_nm())) {
            this.fukutsuCsv(shipment, rule, filePath + fileName);
        }
        if ("西濃運輸".equals(delivery.getDelivery_nm())) {
            this.seinoCsv(shipment, rule, filePath + fileName, delivery.getDelivery_method_csv());
        }

        // スマートCATファイル
        Date upd_date = DateUtils.getDate();
        // String upd_usr = CommonUtil.getToken("user_id", servletRequest);
        String upd_usr = null;
        Mw407_smart_file mw407_smart_file = new Mw407_smart_file();
        mw407_smart_file.setWarehouse_cd(warehouse_cd);
        mw407_smart_file.setShipment_plan_id(shipment_plan_id);
        mw407_smart_file.setFile_path(filePC);
        mw407_smart_file.setFile_name(fileName);
        mw407_smart_file.setUpd_date(upd_date);
        mw407_smart_file.setUpd_usr(upd_usr);
        mw407_smart_file.setIns_date(upd_date);
        mw407_smart_file.setIns_usr(upd_usr);
        warehouseCustomerService.inSmartCatListWindow(mw407_smart_file);

        LinkedHashMap<String, String> csvInfo = new LinkedHashMap<>();
        csvInfo.put("csv_path", csvPath);
        csvInfo.put("locale_path", localePath);

        return CommonUtils.success(csvInfo);
    }

    /**
     * @Description: 出庫検品法人个人统计
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/06/09
     */
    @Override
    public JSONObject incidentsCount(String warehouse_cd) {

        JSONObject jsonObject = new JSONObject();

        List<Tw200_shipment> shipmentList = shipmentDao.incidentsCount(warehouse_cd);
        Integer total = 0, comLegal = 0, comPersonal = 0, workLegal = 0, workPersonal = 0;
        List<Integer> workList = Arrays.asList(4, 5, 7, 41, 42);

        for (int i = 0; i < shipmentList.size(); i++) {
            Tw200_shipment shipment = shipmentList.get(i);
            if (!workList.contains(shipment.getShipment_status())) {
                continue;
            }

            total += shipment.getStatus_count();
            // 法人
            if (shipment.getForm() == 1) {
                if (shipment.getShipment_status() == 7) {
                    comLegal += shipment.getStatus_count();
                }
                workLegal += shipment.getStatus_count();
            }

            // 个人
            if (shipment.getForm() == 2) {
                if (shipment.getShipment_status() == 7) {
                    comPersonal += shipment.getStatus_count();
                }

                workPersonal += shipment.getStatus_count();
            }
        }

        jsonObject.put("total", total);
        jsonObject.put("comLegal", comLegal);
        jsonObject.put("comPersonal", comPersonal);
        jsonObject.put("workLegal", workLegal);
        jsonObject.put("workPersonal", workPersonal);

        return jsonObject;
    }

    /**
     * @Description: 出庫編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    @Override
    public Integer updateShipments(HttpServletRequest servletRequest, JSONObject jsonObject) {
        Integer result = 0;
        String type = jsonObject.getString("type");
        String upd_usr = CommonUtils.getToken("user_id", servletRequest);
        Date upd_date = DateUtils.getDate();
        jsonObject.put("upd_usr", upd_usr);
        jsonObject.put("upd_date", upd_date);

        switch (type) {
            // 出庫配送情報編集
            case "delivery":
                // 出荷希望日
                String shipping_date = jsonObject.getString("shipping_date");
                if (!StringTools.isNullOrEmpty(shipping_date)) {
                    shipping_date.substring(0, 10);
                }
                jsonObject.put("shipping_date", DateUtils.stringToDate(shipping_date));
                // お届け希望日
                String delivery_date = jsonObject.getString("delivery_date");
                if (!StringTools.isNullOrEmpty(delivery_date)) {
                    delivery_date.substring(0, 10);
                }

                String delivery_tracking_nm = jsonObject.getString("delivery_tracking_nm");
                if (!StringTools.isNullOrEmpty(delivery_tracking_nm)) {
                    List<String> splitToList = Splitter.on(",").trimResults().omitEmptyStrings()
                        .splitToList(delivery_tracking_nm);
                    List<String> collect = splitToList.stream().distinct().collect(toList());
                    delivery_tracking_nm = Joiner.on(",").skipNulls().join(collect);
                    jsonObject.put("delivery_tracking_nm", delivery_tracking_nm);
                }
                jsonObject.put("delivery_date", DateUtils.stringToDate(delivery_date));
                jsonObject.put("box_delivery", CommonUtils.toInteger(jsonObject.getString("box_delivery")));
                jsonObject.put("fragile_item", CommonUtils.toInteger(jsonObject.getString("fragile_item")));
                String[] shipment_plan_id = jsonObject.getString("shipment_plan_id").split(",");

                for (int i = 0; i < shipment_plan_id.length; i++) {
                    jsonObject.put("shipment_plan_id", shipment_plan_id[i]);
                    result = shipmentDao.updateShipmentsDelivery(jsonObject);
                }

                // 顧客別作業履歴新增
                String operation_cd = "24"; // 出荷作業中に一括変更
                customerHistoryService.insertCustomerHistory(servletRequest, shipment_plan_id, operation_cd, null);
                break;
            // 出庫備考欄・出荷指示編集
            case "special_notes":
                String delivery_instructions = jsonObject.getString("delivery_instructions");
                if (!StringTools.isNullOrEmpty(delivery_instructions)) {
                    List<String> splitToList = Splitter.on(",").trimResults().omitEmptyStrings()
                        .splitToList(delivery_instructions);
                    for (int i = 0; i < splitToList.size(); i++) {
                        if (i == 0) {
                            jsonObject.put("instructions_special_notes", splitToList.get(i));
                        } else {
                            jsonObject.put("bikou" + i, splitToList.get(i));
                        }
                    }
                    jsonObject.put("bikou_flg", 1);
                } else {
                    jsonObject.put("bikou_flg", 0);
                }
                result = shipmentDao.updateShipmentsSpecialNotes(jsonObject);
                break;
            // お届け先編集
            case "arrival":
                String postcode = jsonObject.getString("postcode");
                if (!StringTools.isNullOrEmpty(postcode)) {
                    String formatZip = CommonUtils.formatZip(postcode);
                    jsonObject.put("postcode", formatZip);
                }
                jsonObject.put("surname", CommonUtils.trimSpace(jsonObject.getString("surname")));
                jsonObject.put("company", CommonUtils.trimSpace(jsonObject.getString("company")));
                jsonObject.put("division", CommonUtils.trimSpace(jsonObject.getString("division")));
                jsonObject.put("address1", CommonUtils.trimSpace(jsonObject.getString("address1")));
                jsonObject.put("address2", CommonUtils.trimSpace(jsonObject.getString("address2")));

                result = shipmentDao.updateShipmentsArrival(jsonObject);
                break;
            default:
                result = -1;
                break;
        }
        return result;
    }

    /**
     * @Description: 出庫個口数編集
     * @Param: json
     * @return: json
     * @Author: zhangnj
     * @Date: 2020/11/26
     */
    @Override
    public Integer updateBoxes(HttpServletRequest servletRequest, JSONObject jsonObject) {
        Integer result = 0;
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String[] shipment_plan_id = jsonObject.getString("shipment_plan_id").split(",");

        // 個口数編集
        String boxes = jsonObject.getString("boxes");
        if (StringTools.isNullOrEmpty(boxes)) {
            return 0;
        }

        // ntm 仓库
        boolean ntmFlg = hasNtmFunction(warehouse_cd, null, "4");

        // 个口数变更时获取配送会社和都道府県
        List<Tw200_shipment> shipmentList = shipmentDao.getShipmentCarrier(warehouse_cd, shipment_plan_id);
        List<Tw216_delivery_fare> deliveryFareList = shipmentDao.getDeliveryFare();

        for (int i = 0; i < shipmentList.size(); i++) {
            Tw200_shipment shipment = shipmentList.get(i);
            String delivery_carrier = shipment.getDelivery_carrier();

            if (!StringTools.isNullOrEmpty(shipment.getSize_cd())) {
                Integer size_nm = Integer.parseInt(shipment.getSize_cd());
                if ("ヤマト運輸宅急便".equals(delivery_carrier)) {
                    delivery_carrier += size_nm.toString();
                } else if ("佐川急便飛脚宅配便".equals(delivery_carrier)) {
                    delivery_carrier = "佐川急便宅配便" + size_nm;
                }
            }

            Tw200_shipment shipmentsBoxs = new Tw200_shipment();
            shipmentsBoxs.setWarehouse_cd(warehouse_cd);
            shipmentsBoxs.setShipment_plan_id(shipment.getShipment_plan_id());
            shipmentsBoxs.setBoxes(CommonUtils.toInteger(boxes));
            // ntm 仓库
            if (ntmFlg) {
                // NTM-运费
                int freight = 0;
                for (Tw216_delivery_fare row : deliveryFareList) {
                    String delivery_name = row.getAgent_id() + row.getMethod();
                    if (delivery_name.equals(delivery_carrier) && row.getRegion().equals(shipment.getPrefecture())) {
                        freight = row.getOriginal_price() * CommonUtils.toInteger(boxes);
                        break;
                    }
                }
                shipmentsBoxs.setFreight(freight);
                shipmentsBoxs.setPacking(700 * CommonUtils.toInteger(boxes));
            }

            result = shipmentDao.updateBoxes(shipmentsBoxs);
        }

        // 顧客別作業履歴新增
        String operation_cd = "44"; // 出荷作業中に個口変更
        customerHistoryService.insertCustomerHistory(servletRequest, shipment_plan_id, operation_cd, null);

        return result;
    }

    /**
     * @param orderNoList 外部受注番号
     * @param warehouseCd 仓库CD
     * @return null
     * @description 更新默认运费
     * @date 2021/7/20
     **/
    @Override
    public void syncDefaultShipmentFreight(List<String> orderNoList, String warehouseCd) {

        if (orderNoList == null || (orderNoList != null && orderNoList.size() == 0)
            || StringTools.isNullOrEmpty(warehouseCd)) {
            return;
        }
        String[] orderNos = new String[orderNoList.size()];

        List<Tw200_shipment> shipmentCarrierList =
            shipmentDao.getShipmentCarrierByIdentifier(warehouseCd, orderNoList.toArray(orderNos));
        List<Tw216_delivery_fare> deliveryFareList = shipmentDao.getDeliveryFare();

        for (int i = 0; i < shipmentCarrierList.size(); i++) {
            Tw200_shipment shipment = shipmentCarrierList.get(i);
            String delivery_carrier = shipment.getDelivery_carrier();

            Integer boxes = shipment.getBoxes();
            if (boxes == null || (boxes != null && boxes == 0)) {
                continue;
            }

            // 佐川运费size默认100，取设定表的 宅急便100 的值
            // Yamato的 除了 ネコポス 和 DM便，其他为空，所以不用size也可以计算ネコポス和DM便的值
            int freight = 0;
            if (delivery_carrier.contains("ヤマト運輸")
                && (delivery_carrier.contains("DM便") || delivery_carrier.contains("ネコポス"))) {
                for (Tw216_delivery_fare row : deliveryFareList) {
                    String delivery_name = row.getAgent_id() + row.getMethod();
                    if (delivery_name.equals(delivery_carrier) && row.getRegion().equals(shipment.getPrefecture())) {
                        freight = row.getOriginal_price() * boxes;
                        break;
                    }
                }
            } else if (delivery_carrier.contains("佐川急便")) {
                int defaultFreight = 0;
                boolean existFreight = false;
                for (Tw216_delivery_fare row : deliveryFareList) {
                    String delivery_name = row.getAgent_id() + row.getMethod();
                    if (delivery_name.equals(delivery_carrier) && row.getRegion().equals(shipment.getPrefecture())) {
                        freight = row.getOriginal_price() * boxes;
                        existFreight = true;
                    }
                    if ("宅配便100".equals(row.getMethod()) && row.getRegion().equals(shipment.getPrefecture())) {
                        defaultFreight = row.getOriginal_price() * boxes;
                    }
                }
                // 如果运费配置表中不存在该配送方式的运费，则取默认的size为100的运费
                if (!existFreight) {
                    freight = defaultFreight;
                }
            } else {
                freight = 0;
            }

            Tw200_shipment shipmentsBoxs = new Tw200_shipment();
            shipmentsBoxs.setWarehouse_cd(warehouseCd);
            shipmentsBoxs.setShipment_plan_id(shipment.getShipment_plan_id());
            shipmentsBoxs.setFreight(freight);
            shipmentsBoxs.setPacking(700 * boxes);
            shipmentDao.updateFreight(shipmentsBoxs);
        }

    }

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/15
     */
    @Override
    public Integer[] getShipmentStatusCount(JSONObject jsonObject) {
        List<Tw200_shipment> shipmentStatusCount = shipmentDao.getShipmentStatusCount(jsonObject);
        Integer[] statusCount = new Integer[44];
        for (int i = 0; i < statusCount.length; i++) {
            statusCount[i] = 0;
        }

        String client_id = jsonObject.getString("client_id");
        List<Ms001_function> ms001FunctionList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(client_id)) {
            String function_cd = "1";
            List<String> functionCdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
            ms001FunctionList = clientDao.function_info(client_id, functionCdList, null);
        }

        for (int i = 0; i < shipmentStatusCount.size(); i++) {
            Tw200_shipment shipment = shipmentStatusCount.get(i);

            String str = shipment.getShipment_status().toString();
            if (ms001FunctionList.size() == 0) {
                str = str.substring(0, 1);
            }
            if (Integer.parseInt(str) == 1 || "1".equals(str)) {
                statusCount[1] += shipment.getStatus_count();
            } else if (Integer.parseInt(str) == 4 || "4".equals(str)) {
                statusCount[4] += shipment.getStatus_count();
            } else if (str.length() == 1) {
                statusCount[shipment.getShipment_status()] += shipment.getStatus_count();
            } else if (Integer.parseInt(str) == 41 || "41".equals(str)) {
                statusCount[41] += shipment.getStatus_count();
            } else if (Integer.parseInt(str) == 42 || "42".equals(str)) {
                statusCount[42] += shipment.getStatus_count();
            } else if (Integer.parseInt(str) == 11 || "11".equals(str)) {
                statusCount[11] += shipment.getStatus_count();
            }
        }
        // 店铺侧 出库依赖总数统计
        for (int i = 0; i < statusCount.length; i++) {
            if (statusCount[i] > 0 && i != 8) {
                statusCount[0] += statusCount[i];
            }
        }
        statusCount[4] += statusCount[5] + statusCount[7];
        statusCount[40] = statusCount[41] + statusCount[42] + statusCount[11];

        // 出荷済み(shipment_status=8) 只统计当月1日到当前日期件数
        // LocalDate today = LocalDate.now();
        // jsonObject.put("startDate", LocalDate.of(today.getYear(), today.getMonth(), 1).toString());
        // jsonObject.put("endDate", today.plusDays(1).toString());
        List<Tw200_shipment> shipmentStatusCountWithShipmentDate = shipmentDao.getShipmentStatusCount(jsonObject);
        Integer shippedCnt = Optional.ofNullable(shipmentStatusCountWithShipmentDate).map(
            cntWithDateList -> {
                Integer cnt = cntWithDateList.stream()
                    .filter(item -> Objects.equal(item.getShipment_status(), 8))
                    .map(value -> value.getStatus_count())
                    .collect(toList())
                    .stream()
                    .reduce(Integer::sum)
                    .orElse(0);
                return cnt;
            }).orElse(0);
        statusCount[8] = shippedCnt;

        // jsonObject.put("updStartDate", LocalDate.of(today.getYear(), today.getMonth(), 1).toString());
        // jsonObject.put("updEndDate", today.plusDays(1).toString());
        // 取消
        statusCount[43] = shipmentDao.getDeleteShipmentCount(jsonObject);

        return statusCount;
    }

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/6/21
     */
    @Override
    public JSONArray getAllShipmentStatusCount(JSONObject jsonObject) {
        List<Tw200_shipment> shipmentStatusCount = shipmentDao.getAllShipmentStatusCount(jsonObject);

        // 出荷済み(shipment_status=8) 只统计当月1日到当前日期件数
        LocalDate today = LocalDate.now();
        jsonObject.put("startDate", LocalDate.of(today.getYear(), today.getMonth(), 1).toString());
        jsonObject.put("endDate", today.plusDays(1).toString());
        List<Tw200_shipment> shipmentStatusCountWithShipmentDate = shipmentDao.getAllShipmentStatusCount(jsonObject);
        for (int i = 0; i < shipmentStatusCount.size(); i++) {
            Tw200_shipment bean = shipmentStatusCount.get(i);
            if (bean.getShipment_status() == 8) {
                bean.setStatus_count(0);
            }
            for (int j = 0; j < shipmentStatusCountWithShipmentDate.size(); j++) {
                Tw200_shipment beanWithShipmentDate = shipmentStatusCountWithShipmentDate.get(j);
                if (null != beanWithShipmentDate
                    && beanWithShipmentDate.getClient_id().equals(bean.getClient_id())
                    && Objects.equal(beanWithShipmentDate.getShipment_status(), bean.getShipment_status())
                    && beanWithShipmentDate.getShipment_status() == 8) {
                    bean.setStatus_count(beanWithShipmentDate.getStatus_count());
                }
            }
        }

        HashMap<String, HashMap<Integer, Integer>> map = new HashMap<>();
        ArrayList<String> clientIdList = new ArrayList<>();
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        HashMap<String, HashMap<Integer, Integer>> cancelStatus = getAllShipmentCancelCount(warehouse_cd, client_id);
        for (int i = 0; i < shipmentStatusCount.size(); i++) {
            Tw200_shipment shipment = shipmentStatusCount.get(i);
            String clientId = client_id;
            if (StringTools.isNullOrEmpty(clientId)) {
                clientId = shipment.getClient_id();
            }
            clientIdList.add(i, clientId);
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);
            String client_name = clientInfo.getClient_nm();
            String client_sign = clientId + "-" + client_name;
            List<Ms001_function> ms001FunctionList = new ArrayList<>();
            if (!StringTools.isNullOrEmpty(clientId)) {
                String function_cd = "1";
                List<String> functionCdList =
                    Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
                ms001FunctionList = clientDao.function_info(clientId, functionCdList, null);
            }
            String str = shipment.getShipment_status().toString();
            if (ms001FunctionList.size() == 0) {
                str = str.substring(0, 1);
            }
            if (map.containsKey(client_sign)) {
                HashMap<Integer, Integer> statusMap = map.get(client_sign);
                stateCountJudgment(str, shipment, statusMap);
                Integer count = statusMap.get(0);
                statusMap.put(0, shipment.getStatus_count() + count);
                map.put(client_sign, statusMap);
            } else {
                HashMap<Integer, Integer> statusMap = new HashMap<>(43);
                stateCountJudgment(str, shipment, statusMap);
                statusMap.put(0, shipment.getStatus_count());
                map.put(client_sign, statusMap);
            }
        }
        List<Ms201_client> allClientInfo = commonFunctionDao.getClientInfomation(warehouse_cd);
        ArrayList<String> allClientIdList = new ArrayList<>();
        for (int j = 0; j < allClientInfo.size(); j++) {
            String clientIdTmp = allClientInfo.get(j).getClient_id();
            allClientIdList.add(j, clientIdTmp);
        }
        if (shipmentStatusCount.isEmpty()) {
            HashMap<Integer, Integer> statusMap = new HashMap<>(17);
            if (StringTools.isNullOrEmpty(client_id)) {
                for (int i = 0; i < allClientIdList.size(); i++) {
                    String clientId = allClientIdList.get(i);
                    for (int j = 0; j < 10; j++) {
                        statusMap.put(j, 0);
                    }
                    Ms201_client clientInfo = clientDao.getClientInfo(clientId);
                    String client_name = clientInfo.getClient_nm();
                    String client_sign = clientId + "-" + client_name;
                    map.put(client_sign, statusMap);
                }
            } else {
                for (int j = 0; j < 10; j++) {
                    statusMap.put(j, 0);
                }
                Ms201_client clientInfo = clientDao.getClientInfo(client_id);
                String client_name = clientInfo.getClient_nm();
                String client_sign = client_id + "-" + client_name;
                map.put(client_sign, statusMap);
            }
        } else if (!shipmentStatusCount.isEmpty()) {
            HashMap<Integer, Integer> statusMap = new HashMap<>(17);
            if (StringTools.isNullOrEmpty(client_id)) {
                for (int i = 0; i < allClientIdList.size(); i++) {
                    String clientId = allClientIdList.get(i);
                    if (!clientIdList.contains(clientId)) {
                        for (int j = 0; j < 10; j++) {
                            statusMap.put(j, 0);
                        }
                        Ms201_client clientInfo = clientDao.getClientInfo(clientId);
                        String client_name = clientInfo.getClient_nm();
                        String client_sign = clientId + "-" + client_name;
                        map.put(client_sign, statusMap);
                    }
                }
            }
        }
        JSONArray status = new JSONArray();
        for (String key : map.keySet()) {
            JSONObject client = new JSONObject();
            HashMap<Integer, Integer> statusCounts = map.get(key);
            HashMap<Integer, Integer> cancelCounts = new HashMap<>();
            cancelCounts = cancelStatus.get(key);
            client.put("cancelStatusCounts", cancelCounts);
            client.put("client_sign", key);
            client.put("statusCounts", statusCounts);
            status.add(client);
        }
        return status;
    }

    private void stateCountJudgment(String str, Tw200_shipment shipment, HashMap<Integer, Integer> statusMap) {
        if (Integer.parseInt(str) == 1 || "1".equals(str)) {
            statusMap.put(1, shipment.getStatus_count());
        } else if (Integer.parseInt(str) == 4 || "4".equals(str)) {
            statusMap.put(4, shipment.getStatus_count());
        } else if (str.length() == 1) {
            statusMap.put(shipment.getShipment_status(), shipment.getStatus_count());
        } else if (Integer.parseInt(str) == 41 || "41".equals(str)) {
            statusMap.put(41, shipment.getStatus_count());
        } else if (Integer.parseInt(str) == 42 || "42".equals(str)) {
            statusMap.put(42, shipment.getStatus_count());
        } else if (Integer.parseInt(str) == 11 || "11".equals(str)) {
            statusMap.put(11, shipment.getStatus_count());
        }
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/19 9:45
     */
    @Override
    public JSONObject getShipmentCancelCount(String warehouse_cd, String client_id) {
        List<Tw200_shipment> shipmentCancelCount = shipmentDao.getShipmentCancelCount(warehouse_cd, client_id);
        Integer[] statusCount = new Integer[7];
        Arrays.fill(statusCount, 0);
        shipmentCancelCount.forEach(data -> {
            Integer status = data.getShipment_status();
            if (status == 1 || status == 11) {
                statusCount[0] += data.getStatus_count();
            } else if (status == 2) {
                statusCount[1] += data.getStatus_count();
            } else if (status == 3) {
                statusCount[2] += data.getStatus_count();
            } else if (status == 4 || status == 5 || status == 7 || status == 41 || status == 42) {
                statusCount[3] += data.getStatus_count();
            } else if (status == 6) {
                statusCount[4] += data.getStatus_count();
            } else if (status == 8) {
                statusCount[5] += data.getStatus_count();
            } else if (status == 9) {
                statusCount[6] += data.getStatus_count();
            }
        });
        return CommonUtils.success(statusCount);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/21
     */
    @Override
    public HashMap getAllShipmentCancelCount(String warehouse_cd, String client_id) {
        List<Tw200_shipment> allShipmentCancelCount = shipmentDao.getAllShipmentCancelCount(warehouse_cd, client_id);
        HashMap<String, HashMap<Integer, Integer>> map = new HashMap<>();
        ArrayList<String> clientIdList = new ArrayList<>();
        for (int i = 0; i < allShipmentCancelCount.size(); i++) {
            Tw200_shipment shipment = allShipmentCancelCount.get(i);
            String clientId = "";
            if (!StringTools.isNullOrEmpty(client_id)) {
                clientId = client_id;
            } else {
                clientId = shipment.getClient_id();
            }
            clientIdList.add(i, clientId);
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);
            String client_name = clientInfo.getClient_nm();
            String client_sign = clientId + "-" + client_name;
            if (map.containsKey(client_sign)) {
                HashMap<Integer, Integer> cancelStatusMap = map.get(client_sign);
                cancelStatusJudgment(shipment, cancelStatusMap, map, client_sign);
            } else {
                HashMap<Integer, Integer> cancelStatusMap = new HashMap<>(8);
                for (int j = 0; j <= 6; j++) {
                    cancelStatusMap.put(j, 0);
                }
                cancelStatusJudgment(shipment, cancelStatusMap, map, client_sign);
            }
        }
        List<Ms201_client> allClientInfo = commonFunctionDao.getClientInfomation(warehouse_cd);
        ArrayList<String> allClientIdList = new ArrayList<>();
        for (int j = 0; j < allClientInfo.size(); j++) {
            String clientIdTmp = allClientInfo.get(j).getClient_id();
            allClientIdList.add(j, clientIdTmp);
        }
        if (allShipmentCancelCount.isEmpty()) {
            HashMap<Integer, Integer> statusMap = new HashMap<>(17);
            if (StringTools.isNullOrEmpty(client_id)) {
                for (int i = 0; i < allClientIdList.size(); i++) {
                    String clientId = allClientIdList.get(i);
                    for (int j = 0; j < 10; j++) {
                        statusMap.put(j, 0);
                    }
                    Ms201_client clientInfo = clientDao.getClientInfo(clientId);
                    String client_name = clientInfo.getClient_nm();
                    String client_sign = clientId + "-" + client_name;
                    map.put(client_sign, statusMap);
                }
            } else {
                for (int j = 0; j < 10; j++) {
                    statusMap.put(j, 0);
                }
                Ms201_client clientInfo = clientDao.getClientInfo(client_id);
                String client_name = clientInfo.getClient_nm();
                String client_sign = client_id + "-" + client_name;
                map.put(client_sign, statusMap);
            }
        } else if (!allShipmentCancelCount.isEmpty()) {
            HashMap<Integer, Integer> statusMap = new HashMap<>(17);
            if (StringTools.isNullOrEmpty(client_id)) {
                for (int i = 0; i < allClientIdList.size(); i++) {
                    String clientId = allClientIdList.get(i);
                    if (!clientIdList.contains(clientId)) {

                        for (int j = 0; j < 10; j++) {
                            statusMap.put(j, 0);
                        }
                        Ms201_client clientInfo = clientDao.getClientInfo(clientId);
                        String client_name = clientInfo.getClient_nm();
                        String client_sign = clientId + "-" + client_name;
                        map.put(client_sign, statusMap);

                    }
                }
            }
        }
        return map;
    }

    private void cancelStatusJudgment(Tw200_shipment shipment, HashMap<Integer, Integer> cancelStatusMap,
        HashMap<String, HashMap<Integer, Integer>> map, String client_sign) {
        Integer status = shipment.getShipment_status();
        if (status == 1 || status == 11) {
            cancelStatusMap.put(0, cancelStatusMap.get(0) + shipment.getStatus_count());
        } else if (status == 2) {
            cancelStatusMap.put(1, cancelStatusMap.get(1) + shipment.getStatus_count());
        } else if (status == 3) {
            cancelStatusMap.put(2, cancelStatusMap.get(2) + shipment.getStatus_count());
        } else if (status == 4 || status == 5 || status == 7 || status == 41 || status == 42) {
            cancelStatusMap.put(3, cancelStatusMap.get(3) + shipment.getStatus_count());
        } else if (status == 6) {
            cancelStatusMap.put(4, cancelStatusMap.get(4) + shipment.getStatus_count());
        } else if (status == 8) {
            cancelStatusMap.put(5, cancelStatusMap.get(5) + shipment.getStatus_count());
        } else if (status == 9) {
            cancelStatusMap.put(6, cancelStatusMap.get(6) + shipment.getStatus_count());
        }
        map.put(client_sign, cancelStatusMap);
    }

    /**
     * @param warehouse_cd ： 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 出庫撮影
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 11:11
     */
    @Override
    public JSONObject getPhotography(String warehouse_cd, String shipment_plan_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        Tw200_shipment shipment = shipmentDao.getShipmentsDetail(warehouse_cd, shipment_plan_id);
        if (!StringTools.isNullOrEmpty(shipment)) {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Integer photography_flg = shipment.getPhotography_flg();
        if (photography_flg == 1) {
            List<Ms205_customer_history> customerHistory = customerHistoryDao.getCustomerHistory(shipment_plan_id);
            customerHistory.stream().filter(history -> {
                String operation_cd = history.getOperation_cd();
                boolean bool = false;
                // 出荷撮影 51
                if ("51".equals(operation_cd)) {
                    jsonObject.put("work_name", history.getIns_usr());
                    jsonObject.put("work_time", history.getOperation_date());
                    bool = true;
                }
                return bool;
            }).findAny();
        }
        jsonObject.put("photography_flg", photography_flg);
        jsonObject.put("shipment_status", shipment.getShipment_status());
        return CommonUtils.success(jsonObject);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 更改撮影状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 12:53
     */
    @Override
    public JSONObject updatePhotography(String warehouse_cd, String shipment_plan_id,
        HttpServletRequest servletRequest) {
        int photography_flg = 1;
        try {
            shipmentDao.updatePhotography(photography_flg, warehouse_cd, shipment_plan_id);
        } catch (Exception e) {
            logger.error("更改撮影状态失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        String operation_cd = "51";
        String[] ids = {
            shipment_plan_id
        };
        // 顧客別作業履歴新增
        try {
            customerHistoryService.insertCustomerHistory(servletRequest, ids, operation_cd, null);
        } catch (Exception e) {
            logger.error("顧客別作業履歴新增失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return CommonUtils.success();
    }

    /**
     * @param jsonObject : warehouse_cd : 仓库Id shipment_plan_id : 出库依赖Id product_id : 商品Id client_id : 店铺Id
     * @description: 获取可以进行引当振替処理的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/15 9:52
     */
    @Override
    public JSONObject getReserveData(JSONObject jsonObject) {
        // 获取引当待ち(2) 出荷待ち(3) 入金待ち(9) 確認待ち(1) 出荷保留(6) 包含改商品id的 并且商品状态为引当済み的数据
        List<Integer> shipmentStatusList = Arrays.asList(1, 2, 3, 6, 9);
        String shipmentPlanId = jsonObject.getString("shipment_plan_id");
        jsonObject.remove("shipment_plan_id");
        jsonObject.put("statusList", shipmentStatusList);
        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsList(jsonObject);

        String product_id = jsonObject.getString("product_id");

        String clientId = jsonObject.getString("client_id");
        JSONArray shipmentDetailArray = new JSONArray();
        shipmentsList.forEach(x -> {
            List<Tw201_shipment_detail> detailList = x.getTw201_shipment_detail();

            // 氏名
            String surname = x.getSurname();
            // 出庫ステータス
            Integer shipment_status = x.getShipment_status();
            // 配達希望日
            Timestamp delivery_date = x.getDelivery_date();
            String deliveryDate = "";
            String shipment_plan_id = x.getShipment_plan_id();
            if (!StringTools.isNullOrEmpty(delivery_date)) {
                deliveryDate = delivery_date.toString().substring(0, 10);
            }
            for (Tw201_shipment_detail detail : detailList) {

                // 如果出库依赖Id和 传入的依赖Id相同 在跳出本次循环
                if (shipmentPlanId.equals(detail.getShipment_plan_id())) {
                    continue;
                }
                Integer set_sub_id = detail.getSet_sub_id();
                // 初始化引当数 和 引当状态
                int reserveNum = 0;
                int reserveStatus = 0;
                boolean flg = false;
                if (product_id.equals(detail.getProduct_id())) {
                    // 如果商品ID相同 则获取相对应的引当数和引当状态
                    int reserve_cnt = detail.getReserve_cnt();
                    if (reserve_cnt > 0) {
                        reserveNum = detail.getReserve_cnt();
                        reserveStatus = detail.getReserve_status();
                        flg = true;
                    }
                }
                if (flg) {
                    JSONObject json = new JSONObject();
                    // 店铺Id
                    json.put("client_id", clientId);
                    // 出 庫ID
                    json.put("shipment_plan_id", shipment_plan_id);
                    // 仓库Id
                    json.put("warehouse_cd", detail.getWarehouse_cd());
                    // 配送先名
                    json.put("surname", surname);
                    // 出荷状況
                    json.put("shipment_status", shipment_status);
                    // 配達希望日
                    json.put("delivery_date", deliveryDate);
                    // 引当数
                    json.put("reserve_num", reserveNum);
                    // 引当状态
                    json.put("reserve_status", reserveStatus);
                    // 商品ID
                    json.put("product_id", product_id);
                    // 商品名
                    json.put("product_name", detail.getMc100_product().get(0).getName());
                    // setFlg
                    json.put("set_sub_id", set_sub_id);
                    shipmentDetailArray.add(json);
                }
            }
        });
        return CommonUtils.success(shipmentDetailArray);
    }

    /**
     * @param jsonObject : shipment: 出库数据 reserve: 被引当替换的数据 client_id: 店铺Id
     * @param request : 请求 为了获取token
     * @description: 进行引当振替処理
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/16 9:51
     */
    @Override
    public JSONObject updateReserveData(String shipment_plan_id, JSONObject jsonObject, HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        String user_id = CommonUtils.getToken("user_id", request);
        Date nowDate = DateUtils.getDate();
        // 倉庫ID
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        // 店舗ID
        String client_id = jsonObject.getString("client_id");
        // 被替换的商品
        JSONObject reserveJson = jsonObject.getJSONObject("reserve");
        // 执行引当替换的商品
        String r_product_id = reserveJson.getString("product_id");

        // 引当替换的引当数
        Integer r_reserve_num = reserveJson.getInteger("reserve_num");
        String[] shipmentPlanIdArray = {
            shipment_plan_id
        };
        List<String> shipmentIdList = new ArrayList<>();
        shipmentIdList.add(shipment_plan_id);

        // 引当替换 顧客別作業履歴新增
        String operation_cd = "15";
        customerHistoryService.insertCustomerHistory(request, shipmentPlanIdArray, operation_cd, user_id);

        // 存所有的明细信息的引当状态，判断是否要变更出库状态
        ArrayList<Integer> shipmentStatusFlags = new ArrayList<>();

        // 需要替换的商品
        JSONObject reserveProductJson = jsonObject.getJSONObject("reserve_prodcut");
        // 需要替换的商品ID
        String o_product_id = reserveProductJson.getString("product_id");
        // Set商品ID
        Integer o_set_sub_id = reserveProductJson.getInteger("set_sub_id");
        // 引当数
        Integer o_reserve_cnt = reserveProductJson.getInteger("reserve_cnt");
        // 出库依赖数
        Integer o_product_plan_cnt = reserveProductJson.getInteger("product_plan_cnt");
        // 引当状态
        Integer o_reserve_status = reserveProductJson.getInteger("reserve_status");

        if (StringTools.isNullOrEmpty(r_product_id) || !r_product_id.equals(o_product_id)) {
            return null;
        }

        // 目前的引当数
        int productReserveNum = o_reserve_cnt;
        // 需要补充的引当数 10 - 2
        int reserveNum = o_product_plan_cnt - productReserveNum;
        // 被替换的引当数 = 被替换的引当数 - 需要补充的引当数
        if (reserveNum <= r_reserve_num) { // 8 <= 30 22
            // 被替换的商品剩余的引当数 = 被替换的引当数 - 需要补充的引当数
            r_reserve_num = r_reserve_num - reserveNum;
            // 引当数 = 商品依赖数
            productReserveNum = o_product_plan_cnt;
            // 引当状态改为 引当済
            o_reserve_status = 1;
        } else {
            // 引当数 = 当前引当数 + 补充的引当数
            productReserveNum += r_reserve_num;
            // 被替换的商品剩余的引当数 = 0
            r_reserve_num = 0;
        }

        shipmentStatusFlags.add(o_reserve_status);
        // 更新出库依赖明细
        shipmentDetailDao.updateReserveStatus(shipment_plan_id, o_product_id, client_id, productReserveNum,
            o_reserve_status, o_set_sub_id);

        // 因为只有引当待ち的时候 需要改变状态， 其他的出库状态保持不变，所以只需要判断引当待ち，并且所有的明细为引当済み
        Integer shipment_status = jsonObject.getInteger("shipment_status");
        if (shipment_status == 2 && productReserveNum != 0) {
            // 查看该依赖所有商品的引当状态
            List<Tw201_shipment_detail> shipmentDetailList =
                shipmentDetailDao.getShipmentDetailList(client_id, shipmentPlanIdArray[0], false);
            boolean status_flg = false;
            for (Tw201_shipment_detail shipment_detail : shipmentDetailList) {
                // 引当ステータス(0:引当待ち 1:引当済み)、複数商品の場合、1つだけ引当待ちになる場合も引当待ちとなる
                if (shipment_detail.getReserve_status() == 0) {
                    status_flg = true;
                    break;
                }
            }

            // 如果该依赖的所有商品都引当済み，则修改依赖的状态为：出荷待ち
            if (!status_flg) {
                try {
                    shipmentDao.setShipmentStatus(warehouse_cd, 3, shipmentPlanIdArray,
                        null, loginNm, nowDate, null, null);
                } catch (Exception e) {
                    logger.error("ステータス変更失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
                // 顧客別作業履歴新增
                // 出荷引当済み
                operation_cd = "2";
                customerHistoryService.insertCustomerHistory(request, shipmentPlanIdArray, operation_cd, user_id);
            }
        }

        // 修改被引当替换的出库依赖数据
        String r_shipment_plan_id = reserveJson.getString("shipment_plan_id");
        Integer r_set_sub_id = reserveJson.getInteger("set_sub_id");
        shipmentDetailDao.updateReserveStatus(r_shipment_plan_id, r_product_id, client_id, r_reserve_num, 0,
            r_set_sub_id);

        // 因为只有出荷待ち 需要改变出库状态， 其他的保持不变。所以只需要判断出荷待ち状态
        String[] shipmentId = {
            r_shipment_plan_id
        };
        try {
            shipmentDao.setShipmentStatus(warehouse_cd, 2, shipmentId, null, loginNm, nowDate, null, null);
        } catch (Exception e) {
            logger.error("ステータス変更失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return CommonUtils.success();
    }

    /**
     * @Description: ステータス
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    @Override
    @Transactional
    public Integer setShipmentStatus(HttpServletRequest servletRequest, String warehouse_cd, Integer shipment_status,
        String[] shipment_plan_id, String sizeName, String status_message, Integer boxes, boolean chang_flg,
        String operation_cd, String user_id) {

        String upd_usr = null;
        Date upd_date = DateUtils.getDate();
        Integer change_status = 0;
        Integer result = 0;

        String sizeId = null;
        if (!StringTools.isNullOrEmpty(sizeName)) {
            sizeId = productResultDao.getSizeIdByName(sizeName);
        }
        // 出庫詳細画面：確認待ち⇒確認済み (0:確認待ち 2:引当待ち 3:出荷待ち 9:入金待ち)
        if (chang_flg
            && (shipment_status == 0 || shipment_status == 2 || shipment_status == 3 || shipment_status == 9)) {
            // 受注明細を取得
            List<Tw201_shipment_detail> shipmentDetailList =
                shipmentDetailDao.getShipmentDetailList(null, shipment_plan_id[0], false);
            boolean status_flg = false;
            for (Tw201_shipment_detail shipment_detail : shipmentDetailList) {
                // 引当ステータス(0:引当待ち 1:引当済み)、複数商品の場合、1つだけ引当待ちになる場合も引当待ちとなる
                if (shipment_detail.getReserve_status() == 0) {
                    status_flg = true;
                    break;
                }
            }
            // 出荷ステータス(2:引当待ち 3:出荷待ち)
            change_status = status_flg ? 2 : 3;
            // 出荷ステータスが引当待ちの場合、0を返却
            if (change_status.equals(shipment_status) && shipment_status.equals(2)) {
                return result;
            }
        }

        if (shipment_status > 0 && change_status == 0) {
            change_status = shipment_status;
        }

        // 判断次状态的依赖是否存在，如果存在则退出
        Integer count = getShipmentListByStatus(warehouse_cd, shipment_plan_id, change_status);
        if (count > 0) {
            return -1;
        }

        try {
            // 出荷ステータスを更新
            result = shipmentDao.setShipmentStatus(warehouse_cd, change_status, shipment_plan_id, status_message,
                upd_usr, upd_date, sizeId, boxes);
        } catch (Exception e) {
            logger.error("出荷ステータスの変更失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        // 顧客別作業履歴新增
        try {
            customerHistoryService.insertCustomerHistory(servletRequest, shipment_plan_id, operation_cd, user_id);
        } catch (Exception e) {
            logger.error("顧客別作業履歴の登録失敗");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return result;
    }

    /**
     * @Description: 自动出荷作業を開始
     * @Param: 顧客CD, 出庫依頼
     * @return: JSONObject
     * @Date: 2021/11/03
     */
    @Override
    public JSONObject automaticInsertShipmentResult(HttpServletRequest servletRequest, JSONArray jsonArray,
        String warehouse_cd) {
        JSONArray errArray = new JSONArray();
        List<String> work_ids = new ArrayList<>();
        List<JSONObject> errList = new ArrayList<>();
        JSONObject shipmentJsonObject = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                shipmentJsonObject = insertShipmentResult(servletRequest, jsonArray.getJSONObject(i), warehouse_cd);
            } catch (Exception e) {
                for (int j = 0; j < jsonArray.getJSONObject(i).getJSONArray("shipment").size(); j++) {
                    errList.add(jsonArray.getJSONObject(i).getJSONArray("shipment").getJSONObject(j));
                }
                continue;
            }
            // 货架不足时
            if (shipmentJsonObject.getString("code") == ErrorCode.E_50110.getValue()) {
                errArray.addAll(shipmentJsonObject.getJSONArray("info"));
            } else if (shipmentJsonObject.getString("code") == "200") {
                work_ids.add(shipmentJsonObject.getString("info"));
            }
        }
        JSONObject returnData = new JSONObject();
        // 出库失败时存取数据
        for (int i = 0; i < errList.size(); i++) {
            JSONObject errListObj = new JSONObject();
            errListObj.put("shipment_plan_id", errList.get(i).getString("shipment_plan_id"));
            errListObj.put("errArray", null);
            errArray.add(errListObj);
        }
        returnData.put("work_ids", work_ids);
        returnData.put("errArray", errArray);
        if (errArray.size() > 0) {
            return CommonUtils.failure(ErrorCode.E_50110, returnData);
        } else {
            return CommonUtils.success(returnData);
        }
    }

    /**
     * @Description: 出荷作业开始数组重组
     * @Param: JSONArray
     * @return: List
     * @Date: 2021/11/10
     */
    @Override
    public List<JSONObject> reorganizationArray(JSONArray errArray) {
        HashMap<String, List<JSONObject>> shipmentsErrMap = new HashMap<>();
        List<JSONObject> shipmentsErrJSONObjectList = new ArrayList<>();
        // 以出库id为主键存取
        for (int i = 0; i < errArray.size(); i++) {
            List<JSONObject> shipmentsObjectList = new ArrayList<>();
            // 定义对象存放出库信息
            JSONObject shipmentsErrObject = new JSONObject();
            shipmentsErrObject.put("code", errArray.getJSONObject(i).getString("code"));
            shipmentsErrObject.put("name", errArray.getJSONObject(i).getString("name"));
            shipmentsErrObject.put("number", errArray.getJSONObject(i).getString("number"));
            shipmentsObjectList.add(shipmentsErrObject);
            if (shipmentsErrMap.containsKey(errArray.getJSONObject(i).getString("shipment_plan_id"))) {
                List<JSONObject> exitErr = shipmentsErrMap.get(errArray.getJSONObject(i).getString("shipment_plan_id"));
                shipmentsErrMap.get(errArray.getJSONObject(i).getString("shipment_plan_id")).add(shipmentsErrObject);
            } else {
                shipmentsErrMap.put(errArray.getJSONObject(i).getString("shipment_plan_id"), shipmentsObjectList);
            }
        }

        for (Map.Entry<String, List<JSONObject>> entry : shipmentsErrMap.entrySet()) {
            JSONObject shipmentsErrObjectTmp = new JSONObject();
            shipmentsErrObjectTmp.put("shipment_plan_id", entry.getKey());
            shipmentsErrObjectTmp.put("errArray", entry.getValue());
            shipmentsErrJSONObjectList.add(shipmentsErrObjectTmp);
        }
        return shipmentsErrJSONObjectList;
    }

    /**
     * @Description: 出荷作業中
     * @Param: Json
     * @return: Integer
     * @Date: 2020/7/8
     */
    @Override
    @Transactional
    public JSONObject insertShipmentResult(HttpServletRequest servletRequest, JSONObject jsonObject,
        String warehouse_cd) {
        // 判断次状态的依赖是否存在，如果存在则退出
        Integer shipment_status = 4;
        JSONArray shipment = jsonObject.getJSONArray("shipment");
        String[] plan_id = new String[shipment.size()];
        for (int i = 0; i < shipment.size(); i++) {
            plan_id[i] = shipment.getJSONObject(i).getString("shipment_plan_id");
        }
        Integer count = getShipmentListByStatus(warehouse_cd, plan_id, 457);
        if (count > 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        List<Tw200_shipment> checkShipmentCondition = shipmentDao.getCheckShipmentCondition(warehouse_cd, plan_id);
        if (!StringTools.isNullOrEmpty(checkShipmentCondition) && !checkShipmentCondition.isEmpty()) {
            int delCount = (int) checkShipmentCondition.stream().filter(x -> x.getDel_flg() == 1).count();
            if (delCount > 0) {
                return CommonUtils.failure(ErrorCode.E_13001);
            }
        }

        Date nowTime = DateUtils.getDate();
        String login_nm = CommonUtils.getToken("login_nm", servletRequest);
        String error_message = "";

        // 货架不能出库 或者 可配送数不足的 出库依赖Id
        ArrayList<String> errShipmentIds = new ArrayList<>();

        JSONArray errArray = new JSONArray();

        // 获取到本次出库作业开始的件数
        int total_cnt = shipment.size();

        // 查询出库详细信息
        List<Tw201_shipment_detail> shipmentDetailList =
            shipmentDetailDao.getShipmentProductList(null, Arrays.asList(plan_id), false);
        Map<String, List<Tw201_shipment_detail>> detailMap =
            shipmentDetailList.stream().collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));

        // 货架分配
        for (int i = 0; i < shipment.size(); i++) {
            List<JSONObject> locationList = new ArrayList<JSONObject>();
            JSONObject json = shipment.getJSONObject(i);
            String client_id = json.getString("client_id");
            String shipment_plan_id = json.getString("shipment_plan_id");

            // 判断是否要修改为引当等待
            boolean reserveFlg = false;
            List<Tw201_shipment_detail> shipmentDetails = detailMap.get(shipment_plan_id);

            for (Tw201_shipment_detail detail : shipmentDetails) {

                JSONObject errJson = new JSONObject();

                // 引当数初始化 如果不为空 则证明有需要改为引当等待的数据
                Integer reserve_cnt = 0;
                HashMap<String, Integer> errMap = new HashMap<>();
                Integer set_sub_id = detail.getSet_sub_id();

                // 普通商品
                String productId = detail.getProduct_id();
                Mc100_product productInfo = detail.getMc100_product().get(0);
                JSONObject itemJson = new JSONObject();
                itemJson.put("warehouse_cd", warehouse_cd);
                itemJson.put("client_id", client_id);
                itemJson.put("shipment_plan_id", shipment_plan_id);
                itemJson.put("product_id", productId);
                itemJson.put("product_plan_cnt", detail.getProduct_plan_cnt());
                itemJson.put("code", productInfo.getCode());
                itemJson.put("name", productInfo.getName());
                this.stockCompute(itemJson, locationList, login_nm, nowTime, error_message, errMap, errJson);
                if (!errMap.isEmpty()) {
                    // 如果errmap不为空，则证明该商品 需要改为引当等待
                    reserve_cnt = errMap.get(productId);

                    reserveFlg = true;
                    // 如果引当数 不为空 则需改商品的引当数以及引当状态
                    shipmentDetailDao.updateReserveStatus(shipment_plan_id, detail.getProduct_id(), client_id,
                        reserve_cnt, 0, set_sub_id);
                    errArray.add(errJson);
                }
            }
            if (reserveFlg) {
                errShipmentIds.add(shipment_plan_id);
                total_cnt--;
            } else {
                // 出庫作業ロケ明細
                shipmentLocationDetailService.insertShipmentLocationDetail(locationList);
            }
        }

        if (errShipmentIds.size() != 0) {
            // 如果含有可配数不足的出库依赖Id
            String[] shipmentIdArray = errShipmentIds.toArray(new String[0]);
            // 出荷ステータスを更新
            shipmentDao.setShipmentStatus(warehouse_cd, 2, shipmentIdArray, null,
                login_nm, nowTime, null, null);

            String operation_cd = "21";
            // 顧客別作業履歴新增
            customerHistoryService.insertCustomerHistory(servletRequest, shipmentIdArray, operation_cd, null);
        }

        Integer work_id = setWorkId();
        if (total_cnt > 0) {
            // 出库依赖数大于0 则证明含有可以正常出库的依赖，所以需要走正常流程
            jsonObject.put("work_id", work_id);
            // 1:作業中 2:完了
            jsonObject.put("shipment_status", 1);
            jsonObject.put("yobi", null);

            jsonObject.put("ins_date", nowTime);
            jsonObject.put("upd_date", nowTime);
            jsonObject.put("ins_usr", login_nm);
            jsonObject.put("upd_usr", login_nm);

            // 出荷作業中
            try {
                shipmentResultDao.insertShipmentResult(jsonObject);
            } catch (Exception e) {
                logger.error("存入出庫作業管理テーブル失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            JSONArray shipmentResultDetail = jsonObject.getJSONArray("shipment");
            jsonObject.remove("shipment");
            for (int i = 0; i < shipmentResultDetail.size(); i++) {
                String clientId = shipmentResultDetail.getJSONObject(i).getString("client_id");
                String shipmentId = shipmentResultDetail.getJSONObject(i).getString("shipment_plan_id");
                // 若 该出库依赖在库数不足，需要变为引当等待状态，所以不能出荷作业开始直接跳过
                if (errShipmentIds.contains(shipmentId)) {
                    continue;
                }
                jsonObject.put("client_id", clientId);
                jsonObject.put("shipment_plan_id", shipmentId);
                // 出荷作業中
                try {
                    shipmentResultDetailDao.insertShipmentResultDetail(jsonObject);
                } catch (Exception e) {
                    logger.error("存入出庫作業明細失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
                String[] shipment_plan_id = {
                    jsonObject.getString("shipment_plan_id")
                };
                this.setShipmentStatus(servletRequest, jsonObject.getString("warehouse_cd"), shipment_status,
                    shipment_plan_id, null, null, null, false, shipment_status.toString(), null);

                String billBarcode = shipmentResultDetail.getJSONObject(i).getString("bill_barcode");
                String payment_id = shipmentResultDetail.getJSONObject(i).getString("payment_id");
                // 识别子
                String identifier = shipmentResultDetail.getJSONObject(i).getString("identifier");
                String order_no = shipmentResultDetail.getJSONObject(i).getString("order_no");

                if (StringTools.isNullOrEmpty(payment_id) || StringTools.isNullOrEmpty(identifier)) {
                    logger.warn("ecforceGMO連携NG 店舗ID:{} identifier: {} payment_id: {} order_no:{}", clientId,
                        identifier, payment_id, order_no);
                    continue;
                }

                // 分割识别子
                List<String> list = Splitter.on("-").trimResults().omitEmptyStrings().splitToList(identifier);

                // 根据店铺识别子 获取店铺api设定信息
                List<Tc203_order_client> orderClientList = orderApiDao.getClientApiByIdentifier(clientId, list.get(0));
                if (StringTools.isNullOrEmpty(orderClientList) || orderClientList.size() <= 0) {
                    logger.warn("ecforceGMO連携 店舗ID:{} 旧請求CD: {} payment_id: {}", clientId, billBarcode, payment_id);
                    continue;
                }

                for (Tc203_order_client orderClient : orderClientList) {
                    // GMO連携APIにより、請求コードを取得
                    String newBillBarcode = EcforceAPI.getBillBarcode(payment_id, orderClient);
                    if (StringTools.isNullOrEmpty(newBillBarcode)) {
                        logger.info("ecforceGMO連携(出荷作業開始) 店舗ID:" + clientId
                            + " payment_id:" + payment_id
                            + "請求コードを取得失敗しました");
                        continue;
                    }

                    // 取得した請求コードと既存と同様の場合、更新しないこと
                    if (!Objects.equal(billBarcode, newBillBarcode)) {
                        shipmentsService.updateGMOBillBarcode(shipmentId, newBillBarcode);
                        logger.info("ecforceGMO連携(出荷作業開始) 店舗ID:" + clientId
                            + " 旧請求CD:" + billBarcode
                            + " 新請求CD:" + newBillBarcode);
                    } else {
                        logger.info("ecforceGMO連携(出荷作業開始) 店舗ID:" + clientId
                            + " 旧請求CD:" + billBarcode
                            + " 新請求CD: 変更なし");
                    }
                }

            }
        }
        // 数据转换
        List<JSONObject> shipmentsErrJSONObjectList = reorganizationArray(errArray);

        if (shipmentsErrJSONObjectList.size() != 0) {
            return CommonUtils.failure(ErrorCode.E_50110, shipmentsErrJSONObjectList);
        } else {
            return CommonUtils.success(work_id);
        }
    }

    /**
     * 验证某个出库状态的数据是否存在
     *
     * @param warehouse_cd
     * @param shipment_plan_id
     * @param shipment_status
     * @return
     * @Date: 2021/5/20
     */
    @Override
    public Integer getShipmentListByStatus(String warehouse_cd, String[] shipment_plan_id, Integer shipment_status) {
        return shipmentDao.getShipmentListByStatus(warehouse_cd, shipment_plan_id, shipment_status);
    }

    /**
     * @param warehouse_cd : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param loginNm : 用户名
     * @param nowDate : 现在时间
     * @description: 在库调整后 判断是否有出库状态需要改变的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/9 18:08
     */
    @Override
    @Transactional
    public JSONObject judgeShipmentStatus(String warehouse_cd, String client_id, String product_id, String loginNm,
        Date nowDate) {
        ArrayList<String> productIdList = new ArrayList<>();
        List<Integer> shipmentStatus = Arrays.asList(1, 2, 3, 6, 9);
        // 找出所有没有出荷作业开始的数据
        List<Tw200_shipment> shipments =
            shipmentsDao.getShipmentInfoByProductId(warehouse_cd, client_id, productIdList);
        if (shipments.size() == 0) {
            logger.info("该商品没有出库依赖信息");
            return null;
        }

        // 查询出该商品的所有在货架上面的信息
        List<Mw405_product_location> locationInfo = stocksResultDao.getLocationInfo(client_id, product_id);

        // 理论在库数 排除掉已经分配货架的
        int deliveryCnt = 0;
        for (Mw405_product_location location : locationInfo) {
            deliveryCnt += location.getStock_cnt() - location.getRequesting_cnt() - location.getNot_delivery();
        }

        // 根据插入时间排序
        List<Tw200_shipment> shipmentList =
            shipments.stream().filter(x -> shipmentStatus.contains(x.getShipment_status()))
                .sorted(Comparator.comparing(Tw200_shipment::getIns_date)).collect(toList());

        ArrayList<String> shipmentIdList = new ArrayList<>();
        for (Tw200_shipment shipment : shipmentList) {
            // 判断出库依赖是否改变
            boolean statusFlg = false;
            List<Tw201_shipment_detail> shipmentDetail = shipment.getTw201_shipment_detail();
            for (Tw201_shipment_detail detail : shipmentDetail) {
                // 普通商品
                String productId = detail.getProduct_id();
                if (!product_id.equals(productId)) {
                    continue;
                }
                Integer set_sub_id = detail.getSet_sub_id();

                int reserve_cnt = detail.getReserve_cnt();
                if (deliveryCnt < reserve_cnt) {
                    statusFlg = true;
                    // 理论在库数 小于 引当数 则引当数 = 理论在库数
                    reserve_cnt = deliveryCnt;
                    // 理论在库数为0
                    deliveryCnt = 0;
                    if (detail.getReserve_status() == 1) {
                        shipmentDetailDao.updateReserveStatus(shipment.getShipment_plan_id(), detail.getProduct_id(),
                            client_id, reserve_cnt, 0, set_sub_id);
                    }
                } else {
                    // 计算剩余的理论在库数
                    deliveryCnt -= reserve_cnt;
                }
            }

            if (statusFlg) {
                // 如果 为确认等待、出荷保留状态、入金等待 不需要变为 引当等待
                Integer status = shipment.getShipment_status();
                if (status != 1 && status != 6 && status != 9) {
                    shipmentIdList.add(shipment.getShipment_plan_id());
                }
            }
        }
        if (shipmentIdList.size() != 0) {
            String[] shipmentArray = shipmentIdList.toArray(new String[shipmentIdList.size()]);
            shipmentDao.setShipmentStatus(warehouse_cd, 2, shipmentArray, null, loginNm, nowDate, null, null);
        }
        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 将该出库依赖下的所有商品的シリアル番号清空
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 12:52
     */
    @Override
    public JSONObject emptySerialNo(String warehouse_cd, String shipment_plan_id) {
        try {
            shipmentDetailDao.emptySerialNo(warehouse_cd, shipment_plan_id);
        } catch (Exception e) {
            logger.error("清空シリアル番号失败, 仓库Id={}、出库依赖Id={}", warehouse_cd, shipment_plan_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * @param jsonObject : warehouse_cd : 仓库Id shipment_plan_id : 多个出库依赖Id以，拼接
     * @description: 判断出荷作業を完了所选的的出库依赖中是否含有过期或者不可出库的商品
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/3 13:08
     */
    @Override
    public JSONObject judgmentShipment(JSONObject jsonObject) {
        // S00001,S0002
        String shipmentPlanId = jsonObject.getString("shipmentPlanId");
        List<String> shipmentIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);
        JSONArray resultArray = new JSONArray();
        if (StringTools.isNullOrEmpty(shipmentIdList) || shipmentIdList.isEmpty()) {
            return CommonUtils.success(resultArray);
        }
        // 判断次状态的依赖是否存在，如果存在则退出
        String warehouseCd = jsonObject.getString("warehouse_cd");
        // TODO
        List<Tw212_shipment_location_detail> locationDetailList =
            shipmentResultDao.getLocationDetailList(warehouseCd, null, shipmentIdList);
        if (StringTools.isNullOrEmpty(locationDetailList) || locationDetailList.isEmpty()) {
            return CommonUtils.success(resultArray);
        }
        // Date date = CommonUtil.getDate();
        // 获取到所有的商品在货架上面 过期或者不可出库的 信息 TODO ERROR
        // List<Mw405_product_location> productLocations = stocksResultDao.getUnavailableProductLocation(date);
        // if (StringTools.isNullOrEmpty(productLocations) || productLocations.isEmpty()) {
        // return CommonUtil.successJson(resultArray);
        // }
        // 将其转为map key: 货架id_店铺Id_商品Id value: 405对象
        // Map<String, Mw405_product_location> locationMap = productLocations.stream().collect(toMap(x ->
        // x.getLocation_id() + "_" + x.getClient_id() + "_" + x.getProduct_id(), o -> o));

        // 获取到货架Id集合
        List<String> locationIdList = locationDetailList.stream().map(Tw212_shipment_location_detail::getLocation_id)
            .distinct().collect(toList());
        // 获取到货架信息集合
        List<Mw404_location> mw404Locations = stocksResultDao.getLocationInfoById(locationIdList, warehouseCd);
        // 组成map key：货架Id value：货架对象
        Map<String, Mw404_location> mw404LocationMap =
            mw404Locations.stream().collect(toMap(Mw404_location::getLocation_id, o -> o));
        LocalDate today = LocalDate.now();
        for (Tw212_shipment_location_detail locationDetail : locationDetailList) {
            String clientId = locationDetail.getClient_id();
            String productId = locationDetail.getProduct_id();
            String locationId = locationDetail.getLocation_id();
            // String key = locationId + "_" + clientId + "_" + productId;
            // if (!locationMap.containsKey(key)) {
            // continue;
            // }
            // Mw405_product_location productLocation = locationMap.get(key);
            if (!mw404LocationMap.containsKey(locationId)) {
                continue;
            }
            Mw405_product_location mw405ProductLocation =
                stocksResultDao.getLocationById(locationId, clientId, productId);
            Date bestbeforeDate = mw405ProductLocation.getBestbefore_date();
            boolean dateFlg = false;
            if (!StringTools.isNullOrEmpty(bestbeforeDate)) {
                LocalDate localDate = bestbeforeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int between = (int) ChronoUnit.DAYS.between(today, localDate);
                if (between <= 0) {
                    dateFlg = true;
                }
            }
            if (!dateFlg && mw405ProductLocation.getStatus() == 0) {
                continue;
            }
            Mw404_location location = mw404LocationMap.get(locationId);
            JSONObject json = new JSONObject();
            json.put("location_nm", location.getWh_location_nm());
            json.put("lot_no", location.getLot_no());
            String bestBeforeDate = "";
            if (!StringTools.isNullOrEmpty(bestbeforeDate)) {
                bestBeforeDate = CommonUtils.getNewDate(bestbeforeDate);
            }
            json.put("bestbefore_date", bestBeforeDate);
            json.put("status", mw405ProductLocation.getStatus());
            json.put("shipment_plan_id", locationDetail.getShipment_plan_id());
            Mc100_product product = productDao.getNameByProductId(clientId, productId);
            json.put("name", product.getName());
            json.put("code", product.getCode());
            json.put("barcode", product.getBarcode());
            resultArray.add(json);
        }
        return CommonUtils.success(resultArray);
    }

    /**
     * @Description: 商品分配货架
     * @Param: items
     * @return: null
     * @Date: 2020/12/2
     */
    @Transactional
    public void stockCompute(JSONObject items, List<JSONObject> locationList, String login_nm, Date nowTime,
        String error_message, HashMap<String, Integer> errMap, JSONObject errJson) {
        String warehouse_cd = items.getString("warehouse_cd");
        String client_id = items.getString("client_id");
        String shipment_plan_id = items.getString("shipment_plan_id");
        String product_id = items.getString("product_id");
        Integer product_plan_cnt = items.getInteger("product_plan_cnt");
        String code = items.getString("code");

        // 获取商品的货架信息
        Tw300_stock stock = stockDao.getStockInfoById(items);
        if (StringTools.isNullOrEmpty(stock)) {
            error_message = ErrorCode.E_12001.getValue();
        }

        // 查询可以出库的货架信息
        List<Mw404_location> locationOrder = stocksResultDao.getLocationOrder(client_id, product_id, nowTime);
        if (locationOrder.size() == 0) {
            // 没有可以出库的货架
            // 则证明该商品状态需要改为引当等待的状态 并且引当数为0
            errMap.put(product_id, 0);

            // 商品不足数 为本次出库依赖数
            errJson.put("shipment_plan_id", shipment_plan_id);
            errJson.put("code", code);
            errJson.put("number", product_plan_cnt);
            errJson.put("name", items.getString("name"));
        } else {
            List<Mw405_product_location> product_location =
                locationOrder.stream().map(Mw404_location::getMw405_product_location).collect(toList());

            Integer plan_cnt = 0;
            if (product_location.size() == 0) {
                error_message = ErrorCode.E_12002.getValue();
            }

            // 获取货架的可配送总和
            int deliveryCntSum = 0;

            // 统计改商品理论在库数
            for (Mw405_product_location val : product_location) {
                int deliveryCnt =
                    val.getStock_cnt() - checkIntNull(val.getRequesting_cnt()) - checkIntNull(val.getNot_delivery());
                deliveryCntSum +=
                    val.getStock_cnt() - checkIntNull(val.getRequesting_cnt()) - checkIntNull(val.getNot_delivery());
                val.setAvailable_cnt(deliveryCnt);
            }
            // 如果依赖数 大于 可配送数 需要将商品的状态改为 引当等待
            if (product_plan_cnt > deliveryCntSum) {
                // key 商品Id value 引当的数量
                errMap.put(product_id, deliveryCntSum);

                errJson.put("shipment_plan_id", shipment_plan_id);
                errJson.put("code", code);
                errJson.put("number", product_plan_cnt - deliveryCntSum);
                errJson.put("name", items.getString("name"));
            } else {
                // 如果依赖数 小于 可配送数 走正常流程
                int count = 0;
                // 未分配货架的商品
                plan_cnt = product_plan_cnt;
                // 按照優先順位分配商品
                for (Mw405_product_location val : product_location) {
                    count++;
                    // 如果全部商品均已分配货架，则退出
                    if (plan_cnt <= 0) {
                        break;
                    }
                    JSONObject tmp_loca = new JSONObject();
                    tmp_loca.put("warehouse_cd", warehouse_cd);
                    tmp_loca.put("client_id", client_id);
                    tmp_loca.put("shipment_plan_id", shipment_plan_id);
                    tmp_loca.put("product_id", product_id);
                    tmp_loca.put("status", 0);
                    tmp_loca.put("ins_usr", login_nm);
                    tmp_loca.put("ins_date", nowTime);
                    tmp_loca.put("upd_usr", login_nm);
                    tmp_loca.put("upd_date", nowTime);
                    tmp_loca.put("lot_no", val.getLot_no());
                    tmp_loca.put("requesting_cnt", val.getRequesting_cnt());

                    // 如果货架上商品大于依赖数，则全部从该货架上取商品
                    if (val.getAvailable_cnt() >= plan_cnt) {
                        tmp_loca.put("location_id", val.getLocation_id());
                        tmp_loca.put("product_plan_cnt", product_plan_cnt);
                        tmp_loca.put("inventory_cnt", val.getStock_cnt());
                        tmp_loca.put("reserve_cnt", plan_cnt);
                        tmp_loca.put("requesting_cnt", plan_cnt);
                        locationList.add(tmp_loca);
                        break;
                    } else {
                        // 如果货架上商品小于依赖数，则把该货架商品全部取完，然后继续取下一个货架的商品
                        tmp_loca.put("location_id", val.getLocation_id());
                        tmp_loca.put("product_plan_cnt", product_plan_cnt);
                        tmp_loca.put("inventory_cnt", val.getStock_cnt());
                        tmp_loca.put("reserve_cnt", val.getAvailable_cnt());
                        tmp_loca.put("requesting_cnt", val.getAvailable_cnt());
                        locationList.add(tmp_loca);
                        // 计算暂未放入货架的商品
                        plan_cnt = plan_cnt - val.getAvailable_cnt();
                        // 如果是最后一个货架之后还有剩余依赖数，则输出错误信息
                        if (count == product_location.size() && plan_cnt > 0) {
                            error_message = ErrorCode.E_12003.getValue();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 出荷作業中取消
     * @Param: Json
     * @return: Integer
     * @Date: 2020/12/22
     */
    @Override
    @Transactional
    public JSONObject shipmentWorkCancel(HttpServletRequest servletRequest, JSONObject jsonObject,
        String warehouse_cd) {

        String loginNm = CommonUtils.getToken("login_nm", servletRequest);
        Date nowTime = DateUtils.getNowTime(null);
        String shipmentPlanId = jsonObject.getString("shipment_plan_id");
        String clientId = jsonObject.getString("client_id");
        String order_no = jsonObject.getString("order_no");
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);
        String status_message = "出荷作業中取消";
        // 判断次状态的依赖是否存在，如果存在则退出
        Integer status = 457;
        String[] plan_id = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            plan_id[i] = list.get(i);
        }
        Integer count = getShipmentListByStatus(warehouse_cd, plan_id, status);
        if (count == 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        // 「出荷検品済」を「出荷作業中」に変更する
        if ("4".equals(jsonObject.getString("status"))) {
            try {
                Date upd_date = DateUtils.getDate();
                // 出荷ステータスを更新
                shipmentDao.setShipmentStatus(warehouse_cd, 4, plan_id, status_message,
                    null, upd_date, null, null);
            } catch (Exception e) {
                logger.error("出荷ステータスの変更失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            // 顧客別作業履歴新增
            try {
                String operation_cd = "35";
                customerHistoryService.insertCustomerHistory(servletRequest, plan_id, operation_cd, "");
            } catch (Exception e) {
                logger.error("顧客別作業履歴の登録失敗");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return CommonUtils.success();
        }

        for (String shipmentId : list) {
            // 取出出庫作業ロケ明細数据
            List<Tw212_shipment_location_detail> shipmentLocationDetails = shipmentLocationDetailService
                .getShipmentLocationDetail(warehouse_cd, shipmentId);

            if (shipmentLocationDetails.size() == 0) {
                continue;
            }

            for (Tw212_shipment_location_detail locationDetail : shipmentLocationDetails) {
                // 复制数据到出庫作業ロケ明細履歴
                shipmentLocationDetailHistoryService.insertShipmentLocationDetailHistory(servletRequest, locationDetail,
                    status_message);

                // 删除出庫作業ロケ明細数据
                shipmentLocationDetailService.delShipmentLocationDetail(locationDetail.getWarehouse_cd(),
                    locationDetail.getClient_id(), locationDetail.getShipment_plan_id(),
                    locationDetail.getProduct_id());
                if (!"4".equals(jsonObject.getString("status"))) {
                    String location_id = locationDetail.getLocation_id();
                    Integer reserve_cnt = locationDetail.getReserve_cnt();
                    // 更改货架详细表里面的依赖数
                    Mw405_product_location productLocation = stocksResultDao.getLocationById(location_id,
                        locationDetail.getClient_id(), locationDetail.getProduct_id());
                    // 获取到之前的依赖数
                    Integer requesting_cnt = productLocation.getRequesting_cnt();
                    requesting_cnt -= reserve_cnt;
                    if (requesting_cnt <= 0) {
                        requesting_cnt = 0;
                    }
                    JSONObject json = new JSONObject();
                    json.put("requesting_cnt", requesting_cnt);
                    json.put("upd_usr", loginNm);
                    json.put("upd_date", nowTime);
                    json.put("location_id", location_id);
                    json.put("client_id", locationDetail.getClient_id());
                    json.put("product_id", locationDetail.getProduct_id());
                    stocksResultDao.updateLocationRequestingCnt(json);
                }

            }

            // 查询出庫作業明細 获取作業管理ID
            Integer workId = shipmentResultDetailDao.getWorkId(warehouse_cd, shipmentId);

            List<Tw211_shipment_result_detail> workInfoById = shipmentResultDetailDao.getWorkInfoById(workId);
            try {
                shipmentResultDao.deleteShipmentResultDetailInfo(workId, warehouse_cd, shipmentId);
            } catch (Exception e) {
                logger.error("删除以及出庫作業明細失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            final boolean[] bool = {
                false
            };
            workInfoById.stream().filter(info -> {
                int delFlg = info.getDel_flg();
                String shipment_plan_id = info.getShipment_plan_id();
                if (!shipmentId.equals(shipment_plan_id) && (delFlg == 0)) {
                    bool[0] = true;
                }
                return bool[0];
            }).findAny();
            if (!bool[0]) {
                // 删除出庫作業管理テーブル 以及出庫作業明細 的数据
                try {
                    shipmentResultDao.deleteShipmentResultInfo(workId, warehouse_cd);
                } catch (Exception e) {
                    logger.error("删除出庫作業管理テーブル失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
            String[] shipment_id = {
                shipmentId
            };
            Integer shipment_status = jsonObject.getInteger("status");
            if (shipment_status == 1) {
                status_message = jsonObject.getString("status_message");
            }
            try {
                String operation_cd = "";
                switch (shipment_status) {
                    case 6:
                        operation_cd = "64"; // 出荷作業取消(出荷保留)
                        break;
                    case 3:
                        operation_cd = "34"; // 出荷作業取消(確認待ち)
                        break;
                    case 1:
                        operation_cd = "14"; // 出荷作業取消(出荷待ち)
                        break;
                }
                // 修改 出庫管理テーブル 的 出庫ステータス 为 出荷待ち
                setShipmentStatus(servletRequest, warehouse_cd, shipment_status, shipment_id, null,
                    status_message, null, false, operation_cd, null);
            } catch (Exception e) {
                logger.error("修改出库状态失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        return CommonUtils.success();
    }

    /**
     * @Description: 作業管理IDを取得
     * @Param: null
     * @return: String
     * @Date: 2020/7/9
     */
    @Override
    public Integer setWorkId() {
        Integer work_id = 0;
        work_id = shipmentResultDao.getWorkId();
        if (work_id == null) {
            return 1;
        }
        work_id++;

        return work_id;
    }

    /**
     * @Description: 作業管理
     * @Param: null
     * @return: List
     * @Date: 2020/7/14
     */
    @Override
    public List<Tw210_shipment_result> getWorkNameList(String warehouse_cd, String client_id) {
        List<Tw210_shipment_result> list = shipmentResultDao.getWorkNameList(warehouse_cd, client_id);
        // a根据work_name进行去重
        list = removeDuplicateOrder(list);
        Collections.sort(list, new BeanSort<Tw210_shipment_result>("Ins_date", false));
        return list;
    }

    /**
     * @Param: jsonObject
     * @description: トータルピッキングリストPDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createShipmentOrderListPDF(JSONObject jsonObject) {
        // String dateMonth = CommonUtil.getDateMonth();
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "orderList", null);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        // JSONArray items = jsonObject.getJSONArray("items");
        // 存储根据商品Id 货架 去重后的json
        List<String> planIds =
            Splitter.on(",").omitEmptyStrings().trimResults().splitToList(jsonObject.getString("shipment_plan_ids"));
        jsonObject.put("shipmentIdList", planIds);

        // 获取仓库名
        Mw400_warehouse warehouseInfo = warehouseCustomerDao.getWarehouseInfo(warehouse_cd);
        if (!StringTools.isNullOrEmpty(warehouseInfo)) {
            jsonObject.put("warehouse_nm", warehouseInfo.getWarehouse_nm());
        }

        JSONObject json = spliceSetJson(warehouse_cd, null, planIds, 1);
        if (hasNtmFunction(warehouse_cd, client_id, "4")) {
            // ntm pdf按商品code排序
            jsonObject.put("setItems",
                jsonArraySort(json.getJSONArray("product").toString(), "locationName", "code", null, true));
        } else {
            // 普通商品 包含（セット）
            jsonObject.put("setItems",
                jsonArraySort(json.getJSONArray("product").toString(), "locationPriority", "product_id", null, true));
        }

        List<String> workNameList = shipmentResultDetailDao.getWorkName(planIds, warehouse_cd);
        // 去重
        workNameList = workNameList.stream().distinct().collect(Collectors.toList());
        StringBuilder workName = new StringBuilder();
        if (workNameList.size() > 3) {
            for (int i = 0; i < workNameList.size(); i++) {
                workName.append(workNameList.get(i)).append(",");
                if (i == 2) {
                    workName.append("......");
                    break;
                }
            }
        } else {
            String join = Joiner.on(",").join(workNameList);
            workName.append(join);
        }
        boolean isNtmFlg = hasNtmFunction(warehouse_cd, client_id, "4");
        jsonObject.put("isNtmFlg", isNtmFlg);
        try {
            // a新版
            PdfTools.createNewOrderListPDF(jsonObject, pdfPath, workName.toString());
        } catch (Exception e) {
            logger.error("倉庫ID：" + warehouse_cd + "トータルピッキングリストPDF生成失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.PDF_GENERATE_FAILED, e.getMessage());
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @param warehouse_cd 倉庫CD
     * @param client_id 店舗ID
     * @param function_cd 功能CD
     * @description 店铺仓库是否有ntm权限
     * @return: boolean
     * @date 2021/7/14
     **/
    private boolean hasNtmFunction(String warehouse_cd, String client_id, String function_cd) {
        List<String> functionCdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
        List<Ms001_function> ms001FunctionList = clientDao.function_info(client_id, functionCdList, warehouse_cd);
        return Optional.ofNullable(ms001FunctionList).map(
            functionList -> {
                return functionList.stream().filter(value -> value.getFunction_cd().equals("4"))
                    .collect(Collectors.toList()).size() > 0;
            }).orElse(false);
    }

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createProductDetailPDFworking(JSONObject jsonObject) {
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String codePath = null;
        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "glance", null);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        JSONObject js = new JSONObject();

        JSONObject selectJson = new JSONObject();
        selectJson.put("warehouse_cd", warehouse_cd);
        selectJson.put("client_id", client_id);

        String shipment_plan_ids = jsonObject.getString("shipment_plan_ids");
        List<String> planList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipment_plan_ids);
        selectJson.put("shipmentIdList", planList);
        String shipment_status = jsonObject.getString("shipment_status");
        List<Integer> shipmentStatusList = new ArrayList<>();
        // a判断是否是出荷作业中的页面
        if (!StringTools.isNullOrEmpty(shipment_status)) {
            if (shipment_status.length() != 1) {
                List<String> list = Splitter.on(",").splitToList(shipment_status);
                shipmentStatusList = list.stream().map(Integer::parseInt).collect(toList());
            } else {
                shipmentStatusList.add(Integer.valueOf(shipment_status));
            }
        }

        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsList(selectJson);

        JSONObject[] jsonArray = new JSONObject[shipmentsList.size()];

        for (int j = 0; j < shipmentsList.size(); j++) {
            Tw200_shipment shipment = shipmentsList.get(j);
            String product_clientId = shipment.getClient_id();
            JSONObject itemsJSONObject = new JSONObject();
            JSONObject json = new JSONObject();

            String shipment_plan_id = shipment.getShipment_plan_id();
            String codeName = shipment_plan_id;
            codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);

            json = OrderServiceImpl.getShipmentJsonDate(itemsJSONObject, shipment);

            Mc105_product_setting productSetting = productSettingService.getProductSetting(product_clientId, null);
            Integer tax = productSetting.getTax();
            Integer accordion = productSetting.getAccordion();
            String product_tax = (tax == 1) ? "税抜" : "税込";
            json.put("product_tax", product_tax);
            String sponsor_id = shipment.getSponsor_id();
            json.put("codePath", codePath);
            json.put("client_id", product_clientId);
            json.put("shipment_plan_id", shipment_plan_id);
            List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(product_clientId, false, sponsor_id);
            Ms012_sponsor_master ms012_sponsor_master = sponsorList.get(0);
            json.put("sponsor_company", ms012_sponsor_master.getCompany());
            json.put("name", ms012_sponsor_master.getName());
            json.put("postcode", ms012_sponsor_master.getPostcode());
            json.put("prefecture", ms012_sponsor_master.getPrefecture());
            json.put("address1", ms012_sponsor_master.getAddress1());
            json.put("address2", ms012_sponsor_master.getAddress2());
            json.put("phone", ms012_sponsor_master.getPhone());
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
            String order_datetime = "";
            if (!StringTools.isNullOrEmpty(shipment.getOrder_datetime())) {
                order_datetime = CommonUtils.getNewDate(shipment.getOrder_datetime());
            }
            json.put("order_datetime", order_datetime);

            String detail_logo = ms012_sponsor_master.getDetail_logo();
            String logoPath = pathProps.getRoot() + detail_logo;
            json.put("detail_logo", logoPath);
            if (!StringTools.isNullOrEmpty(json.getString("message"))) {
                json.put("detail_message", json.getString("message"));
            } else {
                json.put("detail_message", "");
            }
            json.put("payment_method_name", "");
            if (!StringTools.isNullOrEmpty(json.getString("payment_method"))) {
                String payment_method = deliveryDao.getPayById(Integer.valueOf(json.getString("payment_method")));
                if (!StringTools.isNullOrEmpty(payment_method)) {
                    json.put("payment_method_name", payment_method);
                }
            }
            // 拼接セット商品
            JSONArray items = new JSONArray();
            JSONArray allItems = json.getJSONArray("items");
            for (int i = 0; i < allItems.size(); i++) {
                JSONObject object = allItems.getJSONObject(i);
                Integer kubun = object.getInteger("kubun");
                // 同捆物
                if (!StringTools.isNullOrEmpty(kubun) && kubun != Constants.BUNDLED) {
                    items.add(object);
                }
            }
            // 计算商品消费税
            ShipmentsImpl.calculateProductTax(json, tax, accordion);
            JSONArray objects = shipmentsService.splicingSetProduct(items, 0);

            json.put("items", jsonArraySort(objects.toString(), "product_id", "", null, true));
            jsonArray[j] = json;
        }
        js.put("data", jsonArray);
        try {
            // 新版
            PdfTools.createNewShipmentsPriceWorking(js, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createProductDetailPDF(JSONObject jsonObject) {
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String shipment_plan_id = jsonObject.getString("shipment_plan_id");
        String codeName = shipment_plan_id;
        String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "detail", shipment_plan_id);
        // 全路径前端打开会有问题 还需要一个相对路径
        String relativePath = jsonObject.getString("relativePath");
        // 如果不为空 则证明时Batch生成pdf 相对路径已经拼好 如果为空 则需要手动拼接相对路径
        if (StringTools.isNullOrEmpty(relativePath)) {
            relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        }
        String pdfPath = pathProps.getRoot() + relativePath;
        Integer shipment_status = 0;
        Tw200_shipment tw200_shipment = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
        // if (!StringTools.isNullOrEmpty(jsonObject.getInteger("shipment_status"))) {
        // shipment_status = jsonObject.getInteger("shipment_status");
        // }
        // List<Tw200_shipment> shipmentsList = shipmentsDao.getShipmentsList(client_id, null, shipment_plan_id,
        // shipment_status, null, null, null, null, null, false, null, null);

        JSONObject json = null;
        if (!StringTools.isNullOrEmpty(tw200_shipment)) {
            boolean calcelFlg = tw200_shipment.getShipment_status() == 999;
            List<Tw201_shipment_detail> shipmentDetails =
                shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, calcelFlg);
            shipmentDetails = setShipmentProductDetail(shipmentDetails, client_id);
            tw200_shipment.setTw201_shipment_detail(shipmentDetails);
            json = OrderServiceImpl.getShipmentJsonDate(jsonObject, tw200_shipment);
        }
        String sponsor_id = jsonObject.getString("sponsor_id");
        json.put("client_id", client_id);
        json.put("shipment_plan_id", shipment_plan_id);
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false, sponsor_id);
        Ms012_sponsor_master ms012_sponsor_master = sponsorList.get(0);
        json.put("sponsor_company", ms012_sponsor_master.getCompany());
        json.put("name", ms012_sponsor_master.getName());
        json.put("postcode", ms012_sponsor_master.getPostcode());
        json.put("prefecture", ms012_sponsor_master.getPrefecture());
        json.put("address1", ms012_sponsor_master.getAddress1());
        json.put("address2", ms012_sponsor_master.getAddress2());
        json.put("phone", ms012_sponsor_master.getPhone());
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
        if (!StringTools.isNullOrEmpty(json.getString("message"))) {
            json.put("detail_message", json.getString("message"));
        } else {
            json.put("detail_message", "");
        }
        Mc105_product_setting productSetting = productSettingService.getProductSetting(client_id, null);
        Integer version = productSetting.getVersion();
        Integer tax = productSetting.getTax();
        Integer accordion = productSetting.getAccordion();
        String product_tax = (tax == 1) ? "税抜" : "税込";
        json.put("product_tax", product_tax);

        json.put("payment_method_name", "");
        if (!StringTools.isNullOrEmpty(json.getString("payment_method"))) {
            String payment_method = deliveryDao.getPayById(Integer.valueOf(json.getString("payment_method")));
            if (!StringTools.isNullOrEmpty(payment_method)) {
                json.put("payment_method_name", payment_method);
            }
        }

        // 拼接セット商品
        JSONArray items = json.getJSONArray("items");
        // 计算商品消费税
        ShipmentsImpl.calculateProductTax(json, tax, accordion);
        JSONArray objects = shipmentsService.splicingSetProduct(items, 0);
        json.put("items", jsonArraySort(objects.toString(), "locationName", "product_id", null, true));

        try {
            Integer price_on_delivery_note = json.getInteger("price_on_delivery_note");
            if (price_on_delivery_note == 1) {
                // 新版
                PdfTools.createNewShipmentPricePdf(json, codePath, pdfPath);
            } else {
                // 新版
                PdfTools.createNewShipmentsPDF(json, codePath, pdfPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createInstructionsPDFworking(JSONObject jsonObject) {
        JSONArray data = jsonObject.getJSONArray("data");
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String shipment_plan_ids = jsonObject.getString("shipment_plan_ids");
        List<String> planIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipment_plan_ids);

        String codePath = null;
        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "glance_work", null);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;

        JSONObject selectJson = new JSONObject();
        selectJson.put("warehouse_cd", warehouse_cd);
        selectJson.put("client_id", client_id);
        String shipment_status = jsonObject.getString("shipment_status");
        List<Integer> shipmentStatusList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(shipment_status)) {
            if (shipment_status.length() != 1) {
                List<String> list = Splitter.on(",").splitToList(shipment_status);
                shipmentStatusList = list.stream().map(Integer::parseInt).collect(toList());
            } else {
                shipmentStatusList.add(Integer.valueOf(shipment_status));
            }
        }

        selectJson.put("shipmentIdList", planIdList);
        selectJson.put("statusList", shipmentStatusList);
        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsList(selectJson);

        boolean isNtmFlg = hasNtmFunction(warehouse_cd, client_id, "4");
        JSONArray jsonArray = new JSONArray();
        for (int j = 0; j < shipmentsList.size(); j++) {
            JSONObject itemsJson = new JSONObject();
            Tw200_shipment shipment = shipmentsList.get(j);
            client_id = shipment.getClient_id();
            Ms201_client clientInfo = clientDao.getClientInfo(client_id);
            shipment.setClient_nm(clientInfo.getClient_nm());
            itemsJson.put("client_nm", clientInfo.getClient_nm());
            String shipment_plan_id = shipment.getShipment_plan_id();
            List<Tw201_shipment_detail> shipmentDetailList = shipment.getTw201_shipment_detail();
            JSONArray items = new JSONArray();
            for (Tw201_shipment_detail detail : shipmentDetailList) {
                List<Mc100_product> productList = detail.getMc100_product();
                for (Mc100_product product : productList) {
                    items.add(product);
                }
            }
            itemsJson.put("items", items);

            Integer status = 1;
            // a查询该商品是否为 セット商品
            JSONArray shipmets = shipmentsService.splicingSetProduct(items, status);

            String codeName = shipment_plan_id;

            codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
            itemsJson.put("codePath", codePath);

            JSONObject json = spliceSetJson(warehouse_cd, client_id, Collections.singletonList(shipment_plan_id), 0);

            JSONArray bundledArr =
                jsonArraySort(json.getJSONArray("bundled").toString(), "locationName", "productId", null, true);
            itemsJson.put("bundled", bundledArr);

            String sponsor_id = shipment.getSponsor_id();
            List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false, sponsor_id);

            if (sponsorList.size() != 0) {
                Ms012_sponsor_master master = sponsorList.get(0);
                itemsJson.put("sponsor_name", master.getName());
                itemsJson.put("sponsor_prefecture", master.getPrefecture());
                itemsJson.put("sponsor_postcode", master.getPostcode());
                itemsJson.put("sponsor_address1", master.getAddress1());
                itemsJson.put("sponsor_address2", master.getAddress2());
            }
            StringBuilder orderName = new StringBuilder();
            if (!StringTools.isNullOrEmpty(shipment.getOrder_family_name())) {
                orderName.append(shipment.getOrder_family_name());
            }
            if (!StringTools.isNullOrEmpty(shipment.getOrder_first_name())) {
                orderName.append(shipment.getOrder_first_name());
            }
            if (orderName.length() == 0) {
                orderName.append("-");
            }
            itemsJson.put("order_name", orderName.toString());
            itemsJson.put("order_todoufuken", shipment.getOrder_todoufuken());
            itemsJson.put("order_address1", shipment.getOrder_address1());
            itemsJson.put("order_address2", shipment.getOrder_address2());
            itemsJson.put("order_zip_code1", shipment.getOrder_zip_code1());
            itemsJson.put("order_zip_code2", shipment.getOrder_zip_code2());

            String billBarcode = shipment.getBill_barcode();
            itemsJson.put("billBarcode", billBarcode);
            itemsJson.put("product_cnt", json.getJSONArray("product").size());
            // itemsJSONObject.put("sku", sku);
            // a配送时间带处理
            String delivery_time_slot = shipment.getDelivery_time_slot();
            if (delivery_time_slot != null && !"".equals(delivery_time_slot)) {
                itemsJson.put("delivery_time_slot",
                    deliveryDao.getDeliveryTime(null, Integer.valueOf(delivery_time_slot), null, null).get(0)
                        .getDelivery_time_name());
            }

            String payment_method = shipment.getPayment_method();
            itemsJson.put("payment_method_name", "");
            if (!StringTools.isNullOrEmpty(payment_method)) {
                String paymentMethod = deliveryDao.getPayById(Integer.parseInt(payment_method));
                itemsJson.put("payment_method_name", paymentMethod);
            }

            // 同捆明细指定
            String delivery_note = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
            String delivery_note_type = itemsJson.getString("delivery_note_type");
            if ("1".equals(delivery_note_type) || "同梱する".equals(delivery_note_type)) {
                delivery_note = "納品書同梱";
            }
            itemsJson.put("delivery_note_type", delivery_note);

            String deliveryCarrier = shipment.getDelivery_carrier();
            Ms004_delivery ms004_delivery = deliveryDao.getDeliveryById(deliveryCarrier);
            itemsJson.put("method",
                ms004_delivery.getDelivery_nm() + " " + ms004_delivery.getDelivery_method_name());
            // items排序
            // itemsJSONObject.put("items", newItems);
            if (isNtmFlg) {
                itemsJson.put("items", jsonArraySort(json.getJSONArray("product").toString(), "locationName",
                    "priority", "product_id", true));
            } else {
                itemsJson.put("items",
                    jsonArraySort(json.getJSONArray("product").toString(), "priority", "product_id", null, true));
            }

            jsonArray.add(itemsJson);
        }
        jsonObject.put("data", jsonArray);
        jsonObject.put("isNtmFlg", isNtmFlg);
        try {
            // 新版
            PdfTools.createNewInstructionPDFWorking(jsonObject, pdfPath, shipmentsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);

    }

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createInstructionsPDF(JSONObject jsonObject) {
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String shipment_plan_id = jsonObject.getString("shipment_plan_id");
        String codeName = shipment_plan_id;
        String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        String pdfName = CommonUtils.getPdfName(client_id, "shipment", "detail", shipment_plan_id);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        Ms201_client clientInfo = clientDao.getClientInfo(client_id);
        Tw200_shipment shipments = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
        if (StringTools.isNullOrEmpty(shipments)) {
            return null;
        }
        boolean cancelFlg = shipments.getShipment_status() == 999;
        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, cancelFlg);
        shipments.setTw201_shipment_detail(shipmentDetails);

        JSONArray items = jsonObject.getJSONArray("items");

        JSONObject setObjects = spliceSetJson(warehouse_cd, client_id, Collections.singletonList(shipment_plan_id), 0);
        Integer sku = 0;
        String payment_method = shipments.getPayment_method();
        jsonObject.put("payment_method_name", "");
        if (!StringTools.isNullOrEmpty(payment_method)) {
            String paymentMethod = deliveryDao.getPayById(Integer.parseInt(payment_method));
            jsonObject.put("payment_method_name", paymentMethod);
        }
        JSONArray bundleds =
            jsonArraySort(setObjects.getJSONArray("bundled").toString(), "locationPriority", "product_id", null, true);
        jsonObject.put("bundled", bundleds);
        items = jsonArraySort(setObjects.getJSONArray("product").toString(), "product_id", "", null, true);
        jsonObject.put("items", items);

        jsonObject.put("proudct_cnt", items.size());
        jsonObject.put("sku", sku);
        String deliveryCarrier = jsonObject.getString("delivery_carrier");
        // 同捆明细指定
        String delivery_note_type = jsonObject.getString("delivery_note_type");
        String delivery_note = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
        if ("1".equals(delivery_note_type) || "同梱する".equals(delivery_note_type)) {
            delivery_note = "納品書同梱";
        }
        jsonObject.put("delivery_note_type", delivery_note);
        Ms004_delivery delivery = deliveryDao.getDeliveryById(deliveryCarrier);
        String deliveryName = "";
        if (!StringTools.isNullOrEmpty(delivery)) {
            deliveryName = delivery.getDelivery_nm();
        }
        jsonObject.put("method", deliveryName + " " + delivery.getDelivery_method_name());

        // a配送时间带处理
        String delivery_time_slot = jsonObject.getString("delivery_time_slot");
        if (delivery_time_slot != null && !"".equals(delivery_time_slot)) {
            jsonObject.put("delivery_time_slot", deliveryDao
                .getDeliveryTime(null, Integer.valueOf(delivery_time_slot), null, null).get(0).getDelivery_time_name());
        }
        jsonObject.put("client_nm", clientInfo.getClient_nm());

        if (!StringTools.isNullOrEmpty(shipments)) {
            String sponsor_id = shipments.getSponsor_id();
            List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false, sponsor_id);
            if (sponsorList.size() != 0) {
                Ms012_sponsor_master master = sponsorList.get(0);
                jsonObject.put("sponsor_name", master.getName());
                jsonObject.put("sponsor_prefecture", master.getPrefecture());
                jsonObject.put("sponsor_postcode", master.getPostcode());
                jsonObject.put("sponsor_address1", master.getAddress1());
                jsonObject.put("sponsor_address2", master.getAddress2());
            }
        }


        StringBuilder orderName = new StringBuilder();
        if (!StringTools.isNullOrEmpty(shipments.getOrder_family_name())) {
            orderName.append(shipments.getOrder_family_name());
        }
        if (!StringTools.isNullOrEmpty(shipments.getOrder_first_name())) {
            orderName.append(shipments.getOrder_first_name());
        }
        if (orderName.length() == 0) {
            orderName.append("-");
        }
        jsonObject.put("order_name", orderName.toString());
        jsonObject.put("order_todoufuken", shipments.getOrder_todoufuken());
        jsonObject.put("order_address1", shipments.getOrder_address1());
        jsonObject.put("order_address2", shipments.getOrder_address2());
        jsonObject.put("order_zip_code1", shipments.getOrder_zip_code1());
        jsonObject.put("order_zip_code2", shipments.getOrder_zip_code2());

        // 获取到ご請求コード
        String billBarcode = "";
        if (!StringTools.isNullOrEmpty(shipments) && !StringTools.isNullOrEmpty(shipments)) {
            billBarcode = shipments.getBill_barcode();
        }
        jsonObject.put("billBarcode", billBarcode);
        try {
            // 新版
            PdfTools.createNewInstructionPDF(jsonObject, pdfPath, codePath, shipments);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param: jsonObject
     * @description: 明細書・指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @Override
    public JSONObject createProductDetailInstructionsPDF(JSONObject jsonObject) {
        String client_id = jsonObject.getString("client_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        Boolean detailFlg = jsonObject.getBoolean("detail");
        String pdfName;
        String shipmentPlanId = jsonObject.getString("shipment_plan_ids");
        List<String> planIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);

        // type 默认为0 生成A3的pdf 如果json里面的type为空 则将默认值放到json里面
        String type = "0";
        if (StringTools.isNullOrEmpty(jsonObject.getString("type"))) {
            jsonObject.put("type", type);
        }
        // 若shipmentPlanId 为空则是work一览页 否则是 详细页
        if (!detailFlg) {
            if ("1".equals(jsonObject.getString("type"))) {
                pdfName = CommonUtils.getPdfName(client_id, "shipment", "glance_work_detail_A4", shipmentPlanId);
            } else {
                pdfName = CommonUtils.getPdfName(client_id, "shipment", "glance_work_detail", shipmentPlanId);
            }
        } else {
            if ("1".equals(jsonObject.getString("type"))) {
                pdfName = CommonUtils.getPdfName(client_id, "shipment", "work_detail_A4", shipmentPlanId);
            } else {
                pdfName = CommonUtils.getPdfName(client_id, "shipment", "work_detail", shipmentPlanId);
            }
        }
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        JSONObject selectJson = new JSONObject();
        selectJson.put("warehouse_cd", warehouse_cd);
        selectJson.put("client_id", client_id);
        String shipment_status = jsonObject.getString("shipment_status");
        List<Integer> shipmentStatusList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(shipment_status)) {
            if (shipment_status.length() != 1) {
                List<String> list = Splitter.on(",").splitToList(shipment_status);
                shipmentStatusList = list.stream().map(Integer::parseInt).collect(toList());
            } else {
                shipmentStatusList.add(Integer.valueOf(shipment_status));
            }
        }

        selectJson.put("shipmentIdList", planIdList);
        selectJson.put("statusList", shipmentStatusList);
        List<Tw200_shipment> shipmentsList = shipmentDao.getShipmentsList(selectJson);

        // JSONArray items = jsonObject.getJSONArray("items");
        jsonObject.put("items", shipmentsList);
        HashMap<String, JSONObject> map = new HashMap<>();
        boolean isNtmFlg = hasNtmFunction(warehouse_cd, client_id, "4");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shipmentsList.size(); i++) {
            JSONObject itemsJson = new JSONObject();
            Tw200_shipment shipment = shipmentsList.get(i);
            String shipment_plan_id = shipment.getShipment_plan_id();
            List<Tw201_shipment_detail> shipmentDetailList = shipment.getTw201_shipment_detail();

            itemsJson.put("items", shipmentDetailList);
            itemsJson.put("shipments", shipment);
            itemsJson.put("shipment_plan_id", shipment_plan_id);
            String clientId = shipment.getClient_id();
            itemsJson.put("client_id", clientId);

            JSONArray array = new JSONArray();
            // 存取同捆物的json
            JSONArray bundledArray = new JSONArray();
            for (int j = 0; j < shipmentDetailList.size(); j++) {
                Tw201_shipment_detail shipmentDetail = shipmentDetailList.get(j);
                // JSONObject tw201ShipmentDetailJSONObject = tw201_shipment_detail.getJSONObject(j);
                Integer kubun = shipmentDetail.getKubun();
                // 如果为同捆物，从数组中删掉
                if (kubun != Constants.BUNDLED) {
                    array.add(shipmentDetail);
                } else {
                    bundledArray.add(shipmentDetail);
                }
            }
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);
            itemsJson.put("client_nm", clientInfo.getClient_nm());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String order_datetime = "";
            if (!StringTools.isNullOrEmpty(shipment.getOrder_datetime())) {
                order_datetime = dateFormat.format(shipment.getOrder_datetime());
            }
            itemsJson.put("order_datetime", order_datetime);

            // 出荷予定日(納品日)
            String shipping_date = "";
            if (!StringTools.isNullOrEmpty(shipment.getShipping_date())) {
                shipping_date = dateFormat.format(shipment.getShipping_date());
            }
            itemsJson.put("shipping_date", shipping_date);
            Mc105_product_setting productSetting =
                productSettingService.getProductSetting(shipment.getClient_id(), null);
            Integer tax = productSetting.getTax();
            Integer accordion = productSetting.getAccordion();
            String product_tax = (tax == 1) ? "税抜" : "税込";
            itemsJson.put("product_tax", product_tax);

            // 拼接セット商品
            JSONArray setProductJson = getSetProductJson(array);

            itemsJson.put("setProductJson",
                jsonArraySort(setProductJson.toString(), "product_id", "", null, true));
            // 计算商品税率
            ShipmentsImpl.calculateProductTax(itemsJson, tax, accordion);
            // 拼接含有货架的セット商品json
            JSONObject json = spliceSetJson(warehouse_cd, clientId, Collections.singletonList(shipment_plan_id), 0);
            // 普通商品 包含（セット）
            itemsJson.put("setItems", json.getJSONArray("product"));

            // 同捆物
            itemsJson.put("bundled", json.getJSONArray("bundled"));

            Integer total_with_normal_tax = shipment.getTotal_with_normal_tax();

            // 同捆明细指定
            String delivery_note_type = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
            if ("同梱する".equals(shipment.getDelivery_note_type())) {
                delivery_note_type = "納品書同梱";
            }
            itemsJson.put("delivery_note_type", delivery_note_type);

            String billBarcode = shipment.getBill_barcode();
            OrderServiceImpl.getShipmentJsonDate(itemsJson, shipment);

            itemsJson.put("total_with_normal_tax", total_with_normal_tax);
            itemsJson.put("billBarcode", billBarcode);
            String sponsor_id = shipment.getSponsor_id();
            List<Ms012_sponsor_master> sponsorList =
                sponsorDao.getSponsorList(shipment.getClient_id(), false, sponsor_id);
            Ms012_sponsor_master ms012_sponsor_master = sponsorList.get(0);
            itemsJson.put("surname", shipment.getSurname());
            itemsJson.put("postcode", shipment.getPostcode());
            itemsJson.put("prefecture", shipment.getPrefecture());
            itemsJson.put("address1", shipment.getAddress1());
            itemsJson.put("address2", shipment.getAddress2());

            itemsJson.put("sponsor_company", ms012_sponsor_master.getCompany());
            itemsJson.put("masterPostcode", ms012_sponsor_master.getPostcode());
            itemsJson.put("masterPrefecture", ms012_sponsor_master.getPrefecture());
            itemsJson.put("masterAddress1", ms012_sponsor_master.getAddress1());
            itemsJson.put("masterAddress2", ms012_sponsor_master.getAddress2());
            itemsJson.put("phone", ms012_sponsor_master.getPhone());
            itemsJson.put("name", ms012_sponsor_master.getName());

            StringBuilder orderName = new StringBuilder();
            if (!StringTools.isNullOrEmpty(shipment.getOrder_family_name())) {
                orderName.append(shipment.getOrder_family_name());
            }
            if (!StringTools.isNullOrEmpty(shipment.getOrder_first_name())) {
                orderName.append(shipment.getOrder_first_name());
            }
            if (orderName.length() == 0) {
                orderName.append("-");
            }
            itemsJson.put("order_name", orderName.toString());
            itemsJson.put("order_todoufuken", shipment.getOrder_todoufuken());
            itemsJson.put("order_address1", shipment.getOrder_address1());
            itemsJson.put("order_address2", shipment.getOrder_address2());
            itemsJson.put("order_zip_code1", shipment.getOrder_zip_code1());
            itemsJson.put("order_zip_code2", shipment.getOrder_zip_code2());

            Integer contact = ms012_sponsor_master.getContact();
            String value = CommonUtils.getContact(contact);
            itemsJson.put("contact", value);
            itemsJson.put("sponsorEmail", ms012_sponsor_master.getEmail());
            itemsJson.put("sponsorFax", ms012_sponsor_master.getFax());
            itemsJson.put("sponsorPhone", ms012_sponsor_master.getPhone());
            if (contact == 1) {
                itemsJson.put("contact_info", ms012_sponsor_master.getEmail());
            } else {
                itemsJson.put("contact_info", ms012_sponsor_master.getPhone());
            }
            String detail_logo = ms012_sponsor_master.getDetail_logo();
            String logoPath = pathProps.getRoot() + detail_logo;
            itemsJson.put("detail_logo", logoPath);
            if (!StringTools.isNullOrEmpty(itemsJson.getString("message"))) {
                itemsJson.put("detail_message", itemsJson.getString("message"));
            } else {
                itemsJson.put("detail_message", "");
            }
            String codeName = shipment_plan_id;
            String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
            itemsJson.put("codePath", codePath);

            String deliveryCarrier = shipment.getDelivery_carrier();// itemsJSONObject.getString("delivery_carrier");
            Ms004_delivery delivery = deliveryDao.getDeliveryById(deliveryCarrier);
            String deliveryName = " ";
            if (!StringTools.isNullOrEmpty(delivery)) {
                deliveryName = delivery.getDelivery_nm();
            }
            itemsJson.put("method", deliveryName + " " + delivery.getDelivery_method_name());

            itemsJson.put("payment_method_name", "");
            if (!StringTools.isNullOrEmpty(shipment.getPayment_method())) {
                String payment_method = deliveryDao
                    .getPayById(Integer.valueOf(shipment.getPayment_method()));
                if (!StringTools.isNullOrEmpty(payment_method)) {
                    itemsJson.put("payment_method_name", payment_method);
                }
            }

            // items排序
            if (isNtmFlg) {
                itemsJson.put("setItems",
                    jsonArraySort(json.getJSONArray("product").toString(), "locationName", "locationPriority",
                        "product_id", true));
            } else {
                itemsJson.put("setItems",
                    jsonArraySort(json.getJSONArray("product").toString(), "locationPriority", "product_id", null,
                        true));
            }

            // a配送时间带处理
            String delivery_time_slot = shipment.getDelivery_time_slot();
            if (delivery_time_slot != null && !"".equals(delivery_time_slot)) {
                itemsJson.put("delivery_time_slot",
                    deliveryDao.getDeliveryTime(null, Integer.valueOf(delivery_time_slot), null, null).get(0)
                        .getDelivery_time_name());
            }

            jsonArray.add(itemsJson);
        }
        jsonObject.put("data", jsonArray);
        jsonObject.put("isNtmFlg", isNtmFlg);

        try {
            // 新版
            PdfTools.createNewShipmentsDetailPDF(jsonObject, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param shipmentPlanList : 多个出库依赖Id
     * @param status : 状态 0 需要包含同捆物 1 不需要
     * @description: 获取以货架为单位的商品信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/21 13:29
     */
    private JSONObject spliceSetJson(String warehouse_cd, String client_id, List<String> shipmentPlanList, int status) {

        // 根据出库依赖ID集合 获取全部的212 数据
        List<Tw212_shipment_location_detail> locationDetails =
            shipmentResultDao.getLocationDetailList(warehouse_cd, client_id, shipmentPlanList);

        // 查询出库依赖明细的所有数据
        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentDetails(warehouse_cd, client_id, shipmentPlanList);

        // 找出所有的普通商品的商品Id
        List<String> productIdList = shipmentDetails.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Tw201_shipment_detail::getProduct_id).distinct().collect(toList());

        Map<String, List<Tw212_shipment_location_detail>> productIdMap =
            locationDetails.stream().collect(groupingBy(Tw212_shipment_location_detail::getProduct_id));

        JSONArray bundled = new JSONArray();
        JSONArray item = new JSONArray();

        for (Map.Entry<String, List<Tw212_shipment_location_detail>> entry : productIdMap.entrySet()) {
            String productId = entry.getKey();
            if (!productIdList.contains(productId)) {
                // 如果商品Id 不存在 跳出循环
                continue;
            }
            List<Tw212_shipment_location_detail> detailList = entry.getValue();
            Map<String, List<Tw212_shipment_location_detail>> locationMap =
                detailList.stream().collect(groupingBy(Tw212_shipment_location_detail::getLocation_id));
            for (Map.Entry<String, List<Tw212_shipment_location_detail>> map : locationMap.entrySet()) {
                JSONObject json = new JSONObject();
                List<Tw212_shipment_location_detail> details = map.getValue();
                Mw404_location location = stocksResultDao.getLocationName(warehouse_cd, map.getKey());
                Mc100_product mc100_product = productDao.getNameByProductId(details.get(0).getClient_id(), productId);
                IntSummaryStatistics collect =
                    details.stream().collect(summarizingInt(Tw212_shipment_location_detail::getReserve_cnt));
                int number = (int) collect.getSum();
                // 如果引当数为0 ，跳过
                if (number == 0) {
                    continue;
                }
                json.put("client_id", details.get(0).getClient_id());
                json.put("product_id", productId);
                json.put("warehouse_cd", warehouse_cd);
                json.put("name", mc100_product.getName());
                json.put("code", mc100_product.getCode());
                json.put("barcode", mc100_product.getBarcode());
                json.put("ntm_memo", mc100_product.getNtm_memo());
                // json.put("type", sizeType);
                json.put("product_plan_cnt", number);
                json.put("inventory_cnt", details.get(0).getInventory_cnt());
                json.put("locationName", location.getWh_location_nm());
                json.put("locationPriority", location.getPriority());
                json.put("lot_no", location.getLot_no());
                if (status == 0) {
                    if (mc100_product.getBundled_flg() == 0) {
                        item.add(json);
                    } else {
                        bundled.add(json);
                    }
                } else {
                    item.add(json);
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("bundled", bundled);
        result.put("product", item);
        return result;
    }

    /**
     * @Param: items
     * @description: 拼接セット商品
     * @return: com.alibaba.fastjson.JSONArray
     * @date: 2020/9/10
     */
    private JSONArray getSetProductJson(JSONArray tw201_shipment_detail) {
        JSONArray jsonArray = new JSONArray();

        // set商品去重
        String set_id = "";

        for (int i = 0; i < tw201_shipment_detail.size(); i++) {
            JSONObject detailJSONObject = tw201_shipment_detail.getJSONObject(i);

            String product_plan_cnt = detailJSONObject.getString("product_plan_cnt");
            String set_sub_id = detailJSONObject.getString("set_sub_id");
            if (!StringTools.isNullOrEmpty(set_sub_id) && Integer.parseInt(set_sub_id) > 0) {
                product_plan_cnt = detailJSONObject.getString("set_cnt");
                if (set_id.indexOf(set_sub_id) >= 0) {
                    continue;
                }
            }
            set_id += set_sub_id + ",";

            JSONArray mc100_product = detailJSONObject.getJSONArray("mc100_product");
            JSONObject productJSONObject = mc100_product.getJSONObject(0);
            productJSONObject.put("product_plan_cnt", product_plan_cnt);
            productJSONObject.put("is_reduced_tax", detailJSONObject.getString("is_reduced_tax"));
            productJSONObject.put("unit_price", detailJSONObject.getString("unit_price"));
            productJSONObject.put("price", detailJSONObject.getString("price"));
            productJSONObject.put("tax_flag", detailJSONObject.getString("tax_flag"));

            jsonArray.add(productJSONObject);
        }
        return jsonArray;
    }

    /**
     * @Param: workId： 作業管理ID
     * @description: 根据workId 查询出庫依頼ID
     * @return: java.util.List<java.lang.String>
     * @date: 2020/7/23
     */
    @Override
    public List<String> getShipmentIdListByWorkId(List<Integer> work_id) {
        List<String> shipmentIdListByWorkId = null;
        try {
            shipmentIdListByWorkId = shipmentResultDetailDao.getShipmentIdListByWorkId(work_id);
        } catch (Exception e) {
            logger.error("根据作業管理ID 获取出庫依頼ID失败，workId=" + work_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return shipmentIdListByWorkId;
    }

    /**
     * @Param: workName : 作業管理 ,shipment_status ：処理ステータス
     * @description: 根据作业name获取作业者Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    @Override
    public JSONObject getWorkIdByName(String warehouse_cd, String client_id, String work_name, String shipment_status) {
        List<Integer> workIdList;
        try {
            workIdList = shipmentResultDao.getWorkIdByName(warehouse_cd, client_id, work_name, shipment_status);
        } catch (Exception e) {
            logger.error("根据作业name获取作业者Id失败, workName = " + work_name);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return CommonUtils.success(workIdList);
    }

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @param: servletRequest
     * @description: 出荷作業を完了
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    @Override
    @Transactional
    public JSONObject finishShipment(JSONObject jsonObject, HttpServletRequest servletRequest) {
        String loginNm = CommonUtils.getToken("login_nm", servletRequest);
        String shipmentPlanId = jsonObject.getString("shipmentPlanId");
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);

        // 判断次状态的依赖是否存在，如果存在则退出
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        // 将状态改为出荷済み
        Integer shipment_status = 8;
        String[] plan_id = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            plan_id[i] = list.get(i);
        }

        Integer count = getShipmentListByStatus(warehouse_cd, plan_id, shipment_status);
        if (count > 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }
        try {
            setShipmentStatus(servletRequest, jsonObject.getString("warehouse_cd"), shipment_status,
                plan_id, null, null, null, false, shipment_status.toString(), null);
        } catch (Exception e) {
            logger.error("修改出庫ステータス失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INSUFFICIENT_STOCK, e.getMessage());
        }
        Date date = DateUtils.getDate();
        String login_nm = CommonUtils.getToken("login_nm", servletRequest);
        // タイプ 为 出库
        Integer type = 2;
        // 查询出库详细信息
        List<Tw201_shipment_detail> shipmentDetailList = shipmentDetailDao.getShipmentDetails(warehouse_cd, null, list);
        if (StringTools.isNullOrEmpty(shipmentDetailList) || shipmentDetailList.size() == 0) {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Map<String, List<Tw201_shipment_detail>> detailMap =
            shipmentDetailList.stream().collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));

        // 获取到该商品在哪个货架上面出库数为多少
        List<Tw212_shipment_location_detail> locationDetailList;
        try {
            locationDetailList = shipmentResultDao.getShipmentLocationDetail(warehouse_cd, list, null);
        } catch (Exception e) {
            logger.error("查询出庫作業ロケ明細失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        Map<String, List<Tw212_shipment_location_detail>> locationMap =
            locationDetailList.stream().collect(groupingBy(Tw212_shipment_location_detail::getShipment_plan_id));

        for (String shipmentId : list) {
            // 从商品详细Map获取详细信息
            List<Tw201_shipment_detail> shipmentDetails = detailMap.get(shipmentId);
            if (StringTools.isNullOrEmpty(shipmentDetails) || shipmentDetails.size() == 0) {
                continue;
            }

            for (Tw201_shipment_detail item : shipmentDetails) {
                String productId = item.getProduct_id();

                List<Tw212_shipment_location_detail> locationDetails = locationMap.get(shipmentId);
                if (StringTools.isNullOrEmpty(locationDetails) || locationDetails.size() == 0) {
                    continue;
                }

                // 在库表 需要减掉的不可配送默认为0
                int tw300NotDelivery = 0;
                // 出库依赖数默认为0
                int productCnt = 0;
                boolean updateFlg = false;
                for (Tw212_shipment_location_detail location : locationDetails) {
                    if (location.getStatus() == 1 || !productId.equals(location.getProduct_id())) {
                        continue;
                    }
                    // 累加分配到不同货架的引当数
                    productCnt += location.getReserve_cnt();
                    updateFlg = true;
                    String location_id = location.getLocation_id();
                    String client_id = item.getClient_id();
                    // 查询该商品在该货架上面的信息
                    Mw405_product_location productLocation;
                    try {
                        productLocation = stocksResultDao.getLocationById(location_id, client_id, item.getProduct_id());
                    } catch (Exception e) {
                        logger.error("查询倉庫失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }

                    // false 货架正常 true 货架过期或者出荷不可
                    boolean notAvailable = StocksResultServiceImpl.getChangeFlg(productLocation.getStatus(),
                        productLocation.getBestbefore_date());

                    // 货架上的在库数 = 原有在库数 - 本次出库数
                    int stockCnt = Math.max(productLocation.getStock_cnt() - location.getReserve_cnt(), 0);
                    // 出库依赖中数
                    int number = productLocation.getRequesting_cnt() - location.getReserve_cnt();
                    if (number <= 0) {
                        number = 0;
                    }
                    Integer requestCnt = number;
                    // 默认不可配送为0
                    int notDelivery = 0;
                    if (notAvailable) {
                        // 货架过期 不可配送数需要减掉 本次出库数
                        notDelivery = productLocation.getNot_delivery() - location.getReserve_cnt();
                        tw300NotDelivery += location.getReserve_cnt();
                    }
                    try {
                        // 更改货架上固定商品的在库数 引当数
                        stocksResultDao.updateLocationCnt(stockCnt, requestCnt, Math.max(notDelivery, 0),
                            location.getLocation_id(), item.getClient_id(), item.getProduct_id(), login_nm, date);
                    } catch (Exception e) {
                        logger.error("修改货架在库数引当数失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }

                    // 出库完了时，修改状态为1
                    shipmentResultDao.updateLocationStatus(jsonObject.getString("warehouse_cd"), shipmentId,
                        location.getId());
                }

                if (updateFlg) {
                    jsonObject.put("client_id", item.getClient_id());
                    jsonObject.put("product_id", item.getProduct_id());
                    Tw300_stock stockInfo;
                    try {
                        stockInfo = stockDao.getStockInfoById(jsonObject);
                    } catch (Exception e) {
                        logger.error("查询在库数据失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                    // 実在庫数
                    int inventory_cnt = stockInfo.getInventory_cnt();

                    // 在库数小于出库依赖中数
                    if (inventory_cnt < productCnt) {
                        throw new BaseException(ErrorCode.INSUFFICIENT_STOCK);
                    }
                    // 实际在库数 - 该商品出库依赖数 = 现在在库数
                    int inventoryNum = inventory_cnt - productCnt;
                    // 出庫依頼中数 = 原有的出库依赖数 - 该商品本次出库依赖的数
                    int requestingNum = stockInfo.getRequesting_cnt() - productCnt;
                    // 不可配送 = 原有的不可配送数 - 本次出库的不可配送数
                    int notDelivery = stockInfo.getNot_delivery() - tw300NotDelivery;

                    // 理論在庫数
                    int availableNum = inventoryNum - requestingNum - notDelivery;
                    try {
                        stockDao.updateStockNum(jsonObject.getString("warehouse_cd"), item.getClient_id(),
                            item.getProduct_id(), Math.max(inventoryNum, 0), requestingNum, availableNum, notDelivery,
                            date, login_nm);
                    } catch (Exception e) {
                        logger.error("修改在库实际数失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }

                    // 获取最大在库履历Id
                    String stock_history_id = stockDao.getMaxStockHistoryId();
                    String stockHistoryId = CommonUtils.getMaxStockHistoryId(stock_history_id);
                    String info = Constants.SHIPMENT_FINISH;
                    // 添加在庫履歴
                    // stockDao.insertStockHistory(shipmentId, jsonObject.getString("client_id"), item.getProduct_id(),
                    // stockHistoryId, type, item.getProduct_plan_cnt(), loginNm, date, info);
                    Tw301_stock_history history = new Tw301_stock_history();
                    history.setHistory_id(stockHistoryId);
                    history.setPlan_id(shipmentId);
                    history.setClient_id(jsonObject.getString("client_id"));
                    history.setProduct_id(item.getProduct_id());
                    history.setInfo(info);
                    history.setBefore_num(inventory_cnt);
                    history.setAfter_num(inventoryNum);
                    history.setQuantity(productCnt);
                    history.setType(type);
                    history.setIns_date(date);
                    history.setIns_usr(loginNm);
                    history.setUpd_usr(loginNm);
                    history.setUpd_date(date);
                    // 新规在库履历
                    stocksResultDao.insertStockHistory(history);
                }
            }

            // 查询出庫作業明細 获取作業管理ID
            Integer workId = shipmentResultDetailDao.getWorkId(jsonObject.getString("warehouse_cd"), shipmentId);

            // 获取到作业Id 对应的所有出库依赖Id
            List<String> shipmentIdList =
                shipmentResultDetailDao.getShipmentIdListByWorkId(Collections.singletonList(workId));

            if (!shipmentIdList.isEmpty()) {
                // 根据出库依赖Id 得到出库状态不为8 的count
                Integer statusCount = shipmentsDao.getShipmentStatusByIdList(shipmentIdList);
                if (statusCount == 0) {
                    // 根据workId 改修 処理ステータス
                    try {
                        shipmentResultDao.updateShipmentResultStatusByWorkId(workId,
                            jsonObject.getString("warehouse_cd"));
                    } catch (Exception e) {
                        logger.error("根据workId 改修出库状态失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                }
            }
        }

        return CommonUtils.success();
    }

    /**
     * @throws ExceptionHandlerAdvice
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @param: servletRequest
     * @description: 仓库侧快递公司CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    @Override
    @Transactional
    public JSONObject shipmentsCompanyCsvUpload(HttpServletRequest req, MultipartFile file, Integer flag,
        String warehouse_cd) {

        String message = "";
        // flag 1:佐川急便, 2:ヤマト運輸, 3:日本郵便, 4:福山通運
        try {
            File fileCsv = CsvUtils.read(req, file);
            // InputStream stream = file.getInputStream();
            // BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            // //验证编码格式
            // if (!CommonUtil.determineEncoding(bufferedInputStream, new String[]{"SHIFT_JIS"})){
            // String json = JSON.toJSONString("ご指定のCSVファイルが、取り扱いできる形式（SHIFT-JIS）ではありません。");
            // throw new ExceptionHandlerAdvice(json);
            // }
            // a错误信息list
            List<String> list = new ArrayList<String>();
            // a读取上传的CSV文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fileCsv), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            int num = 0;
            while (csvReader.readRecord()) {
                num++;
                if (num > 20000) {
                    throw new LcBadRequestException("一度に登録できるデータは最大20000件です。");
                }
            }
            // a关闭csvReader
            csvReader.close();
            num = 0;
            // a验证写入信息
            InputStreamReader isr1 = new InputStreamReader(new FileInputStream(fileCsv), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                String tmp = csvReader1.getRawRecord();
                String[] params = tmp.split(",", -1);
                num++;
                int k = 0;
            }
            // a关闭csvReader
            csvReader1.close();
            int total_cnt = num;
            // a写入传票番号信息
            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(fileCsv), "Shift-JIS");
            CsvReader csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            String shipment_plan_id = null;
            String delivery_tracking_nm = null;
            String delivery_carrier = null;
            List<String> warn_plan_id = new ArrayList<>();
            int success_cnt = 0, error_cnt = 0;
            // 根据出库依赖id 区分，对用传票番号
            HashMap<String, String> trackMap = new HashMap<>();
            List<Integer> checkStatus = Arrays.asList(4, 5, 7, 41, 42);
            while (csvReader2.readRecord()) {
                String tmp = csvReader2.getRawRecord();
                boolean deliveryCheck = false;
                boolean templateFlg = false;
                String[] params = tmp.split(",", -1);
                if (flag == 1) {// a佐川急便e飛伝
                    if (params.length >= 13) {

                        // 是否是ntm店铺，是true，否false
                        boolean isNtmFlg = hasNtmFunction(warehouse_cd, null, "4");
                        // 如果是ntm店铺，则佐川急便的【お客様管理ナンバー】列保存的是受注番号。所以需要先根据受注番号查出出库id
                        if (isNtmFlg) {
                            String orderNo = specialSymbol(params[12]);
                            Tw200_shipment tw200 = shipmentDao.getShipmentPlanIdByOrderNo(warehouse_cd, orderNo);
                            shipment_plan_id = tw200.getShipment_plan_id();
                        } else {
                            // aお客様管理ナンバー
                            shipment_plan_id = specialSymbol(params[12]);
                        }
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // aお問合せ送り状№
                        delivery_tracking_nm = specialSymbol(params[0]);
                        delivery_carrier = "佐川急便";
                        String delivery_conn = "佐川コンビニ受取";
                        // a验证该出荷信息是否匹配配送公司
                        String carrier = shipmentDao.getDeliveryCarrierByShipmentId(shipment_plan_id);
                        Ms004_delivery delivery = deliveryDao.getDeliveryById(carrier);
                        if (StringTools.isNullOrEmpty(delivery)) {
                            templateFlg = true;
                        } else {
                            if (!delivery_carrier.equals(delivery.getDelivery_nm())
                                && !delivery_conn.equals(delivery.getDelivery_nm())) {
                                deliveryCheck = true;
                            }
                        }
                    } else {
                        templateFlg = true;
                    }
                } else if (flag == 2) {// aヤマト運輸B2Web
                    if (params.length >= 4) {
                        // 是否是ntm店铺，是true，否false
                        boolean isNtmFlg = hasNtmFunction(warehouse_cd, null, "4");
                        // 如果是ntm店铺，则ヤマト運輸的【お客様管理番号】列保存的是受注番号。所以需要先根据受注番号查出出库id
                        if (isNtmFlg) {
                            String orderNo = specialSymbol(params[0]);
                            Tw200_shipment tw200 = shipmentDao.getShipmentPlanIdByOrderNo(warehouse_cd, orderNo);
                            shipment_plan_id = tw200.getShipment_plan_id();
                        } else {
                            // aお客様管理番号
                            shipment_plan_id = specialSymbol(params[0]);
                        }
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // a伝票番号
                        delivery_tracking_nm = specialSymbol(params[3]);
                        delivery_carrier = "ヤマト運輸";
                        // a验证该出荷信息是否匹配配送公司
                        String carrier = shipmentDao.getDeliveryCarrierByShipmentId(shipment_plan_id);
                        Ms004_delivery delivery = deliveryDao.getDeliveryById(carrier);
                        if (StringTools.isNullOrEmpty(delivery)) {
                            templateFlg = true;
                        } else {
                            if (!delivery_carrier.equals(delivery.getDelivery_nm())) {
                                deliveryCheck = true;
                            }
                        }
                    } else {
                        templateFlg = true;
                    }
                } else if (flag == 3) {// a日本郵便
                    if (params.length >= 2) {
                        // aお客様管理番号
                        shipment_plan_id = specialSymbol(params[1]);
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // a伝票番号
                        delivery_tracking_nm = specialSymbol(params[0]);
                        delivery_carrier = "日本郵便";
                        // a验证该出荷信息是否匹配配送公司
                        String carrier = shipmentDao.getDeliveryCarrierByShipmentId(shipment_plan_id);
                        Ms004_delivery delivery = deliveryDao.getDeliveryById(carrier);
                        if (StringTools.isNullOrEmpty(delivery)) {
                            templateFlg = true;
                        } else {
                            if (!delivery_carrier.equals(delivery.getDelivery_nm())) {
                                deliveryCheck = true;
                            }
                        }
                    } else {
                        templateFlg = true;
                    }
                } else if (flag == 4) {// a福山通運
                    if (params.length >= 35) {
                        // aお客様管理番号
                        shipment_plan_id = specialSymbol(params[34]);
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // a伝票番号
                        delivery_tracking_nm = specialSymbol(params[2]);
                        if (!StringTools.isNullOrEmpty(delivery_tracking_nm)) {
                            delivery_tracking_nm = delivery_tracking_nm.replaceAll("-", "");
                        }
                        if (delivery_tracking_nm.length() < 13) {
                            delivery_tracking_nm += "01";
                        }
                        delivery_carrier = "福山通運";
                        // a验证该出荷信息是否匹配配送公司
                        String carrier = shipmentDao.getDeliveryCarrierByShipmentId(shipment_plan_id);
                        Ms004_delivery delivery = deliveryDao.getDeliveryById(carrier);
                        if (StringTools.isNullOrEmpty(delivery)) {
                            templateFlg = true;
                        } else {
                            if (!delivery_carrier.equals(delivery.getDelivery_nm())) {
                                deliveryCheck = true;
                            }
                        }
                    } else {
                        templateFlg = true;
                    }
                } else if (flag == 5) {// スマートキャット取込
                    if (params.length >= 4) {
                        // aお客様管理番号
                        shipment_plan_id = specialSymbol(params[0]);
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // a伝票番号
                        delivery_tracking_nm = specialSymbol(params[3]);
                    } else {
                        templateFlg = true;
                    }
                } else if (flag == 6) {
                    // 西濃運輸
                    if (params.length >= 5) {
                        // aお客様管理番号
                        shipment_plan_id = specialSymbol(params[1]);
                        // 验证该出库依赖以前是否存在
                        Tw200_shipment shipment = shipmentDao.getShipmentExist(warehouse_cd, shipment_plan_id, null);
                        if (StringTools.isNullOrEmpty(shipment)
                            || !checkStatus.contains(shipment.getShipment_status())) {
                            warn_plan_id.add(shipment_plan_id);
                            error_cnt++;
                            continue;
                        }
                        // a伝票番号
                        delivery_tracking_nm = specialSymbol(params[2]);
                    } else {
                        templateFlg = true;
                    }
                }

                if (templateFlg) {
                    throw new LcBadRequestException("CSVファイルのフォーマットがご指定の配送業者のものと異なりますのでご確認ください。");
                }
                if (deliveryCheck) {
                    throw new LcBadRequestException(
                        "出庫依頼ID(" + shipment_plan_id + ")は、CSVファイルのフォーマットがご指定の配送業者のものと異なりますのでご確認ください。");
                }

                // 判断map之前是否存过该出库id
                if (!trackMap.containsKey(shipment_plan_id)) {
                    trackMap.put(shipment_plan_id, delivery_tracking_nm);
                } else {
                    String track = trackMap.get(shipment_plan_id);
                    track = track + "," + delivery_tracking_nm;
                    trackMap.put(shipment_plan_id, track);
                }
                success_cnt++;
            }
            // 将伝票番号存到出库管理表
            shipmentDao.setDeliveryTrackingNm(trackMap);

            if (error_cnt > 0) {
                message = "取込件数：" + total_cnt + "件　（成功：" + success_cnt + "件　失敗：" + error_cnt + "件 ※出庫依頼IDが存在しません。）";
            } else {
                message = "取込件数：" + total_cnt + "件　（成功：" + success_cnt + "件　失敗：" + error_cnt + "件）";
            }
            // a关闭csvReader
            csvReader2.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommonUtils.success(message);
    }

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @description: 出荷完了を取り消す
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/3
     */
    @Override
    @Transactional
    public JSONObject cancelShipment(JSONObject jsonObject, HttpServletRequest servletRequest) {
        // 判断次状态的依赖是否存在，如果存在则退出
        String warehouse_cd = jsonObject.getString("warehouse_cd");

        Integer shipment_status = jsonObject.getInteger("status");
        String shipmentPlanId = jsonObject.getString("shipment_plan_id");
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);

        String[] plan_id = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            plan_id[i] = list.get(i);
        }
        Integer count = getShipmentListByStatus(warehouse_cd, plan_id, shipment_status);
        if (count > 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        cancelDelivery(jsonObject, servletRequest);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @param: servletRequest
     * @param: status
     * @description: 回滚数据
     * @return: void
     * @date: 2020/8/14
     */
    @Transactional
    public void cancelDelivery(JSONObject jsonObject, HttpServletRequest servletRequest) {
        Date date = DateUtils.getDate();
        String loginNm = CommonUtils.getToken("login_nm", servletRequest);
        String shipmentPlanId = jsonObject.getString("shipment_plan_id");
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String status_message = "出荷完了を取り消す";

        // 根据仓库Id 出库依赖Id 获取到多个出库依赖明细
        List<Tw201_shipment_detail> shipmentDetailList = shipmentDetailDao.getShipmentDetails(warehouse_cd, null, list);
        if (StringTools.isNullOrEmpty(shipmentDetailList) || shipmentDetailList.size() == 0) {
            return;
        }
        Map<String, List<Tw201_shipment_detail>> detailMap =
            shipmentDetailList.stream().collect(groupingBy(Tw201_shipment_detail::getShipment_plan_id));

        // 获取到该商品在哪个货架上面出库数为多少
        List<Tw212_shipment_location_detail> locationDetailList;
        try {
            locationDetailList = shipmentResultDao.getShipmentLocationDetail(warehouse_cd, list, null);
        } catch (Exception e) {
            logger.error("查询出庫作業ロケ明細失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        Map<String, List<Tw212_shipment_location_detail>> locationMap =
            locationDetailList.stream().collect(groupingBy(Tw212_shipment_location_detail::getShipment_plan_id));

        for (String shipmentId : list) {
            List<Tw201_shipment_detail> detailList = detailMap.get(shipmentId);
            if (StringTools.isNullOrEmpty(detailList) || detailList.size() == 0) {
                continue;
            }
            for (Tw201_shipment_detail detail : detailList) {
                String client_id = detail.getClient_id();
                String product_id = detail.getProduct_id();
                List<Tw212_shipment_location_detail> locationDetail = locationMap.get(shipmentId);
                if (StringTools.isNullOrEmpty(locationDetail) || locationDetail.size() == 0) {
                    continue;
                }

                final int[] tw300NotDelivery = {
                    0
                };

                // locationDetail.forEach(shipmentLocationDetail -> {
                for (Tw212_shipment_location_detail shipmentLocationDetail : locationDetail) {
                    if (!product_id.equals(shipmentLocationDetail.getProduct_id())) {
                        continue;
                    }
                    String location_id = shipmentLocationDetail.getLocation_id();

                    // 根据货架Id 查询 货架信息
                    Mw405_product_location locationInfo = productResultDao.getProductLocationInfo(
                        location_id, client_id, shipmentLocationDetail.getProduct_id());

                    // 判断货架是否过期 true 不可用 false 可用
                    boolean unavailable = StocksResultServiceImpl.getChangeFlg(locationInfo.getStatus(),
                        locationInfo.getBestbefore_date());

                    // 货架的实际在库数 = 原有实际在库数 + 货架的引当数
                    Integer stockCnt = locationInfo.getStock_cnt() + shipmentLocationDetail.getReserve_cnt();

                    // 默认不可配送为0
                    int notDelivery = 0;
                    if (unavailable) {
                        // 货架过期 不可配送数需要加上 本次出库数
                        notDelivery = locationInfo.getNot_delivery() + shipmentLocationDetail.getReserve_cnt();
                        tw300NotDelivery[0] += shipmentLocationDetail.getReserve_cnt();
                    }

                    // 修改该货架的实际在库数以
                    try {
                        productResultDao.updateReserveCntById(location_id, shipmentLocationDetail.getProduct_id(),
                            client_id, stockCnt, notDelivery, loginNm, date);
                    } catch (Exception e) {
                        logger.error("修改货架实际在库数，引当数失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }

                    // 复制数据到出庫作業ロケ明細履歴
                    shipmentLocationDetailHistoryService.insertShipmentLocationDetailHistory(servletRequest,
                        shipmentLocationDetail, status_message);

                    // 删除出庫作業ロケ明細数据
                    shipmentLocationDetailService.delShipmentLocationDetail(warehouse_cd, client_id, shipmentId,
                        detail.getProduct_id());
                }

                jsonObject.put("client_id", client_id);
                jsonObject.put("product_id", detail.getProduct_id());
                Tw300_stock stockInfo;
                try {
                    stockInfo = stockDao.getStockInfoById(jsonObject);
                } catch (Exception e) {
                    logger.error("查询在库数据失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
                // 回滚后的入库实际数 = 现在的实际在库数 + 该商品依赖数
                Integer inventoryCnt = stockInfo.getInventory_cnt() + detail.getProduct_plan_cnt();
                // 回滚后的出库依赖中数 = 现在的出库依赖中数 + 该商品依赖数
                Integer requestingCnt = stockInfo.getRequesting_cnt() + detail.getProduct_plan_cnt();
                // 回滚后的不可配送数 = 现在的不可配送数 + 该商品的从过期货架上取得数量
                int notDelivery = stockInfo.getNot_delivery() + tw300NotDelivery[0];
                // 回滚后的理论在库数 = 现在的理论在库数 - 该商品依赖数
                Integer availableCnt = inventoryCnt - requestingCnt - notDelivery;
                try {
                    stockDao.updateStockNum(warehouse_cd, detail.getClient_id(), detail.getProduct_id(), inventoryCnt,
                        requestingCnt, availableCnt, notDelivery, date, loginNm);
                } catch (Exception e) {
                    logger.error("修改在库实际数失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }

            // 查询出庫作業明細 获取作業管理ID
            Integer workId = shipmentResultDetailDao.getWorkId(warehouse_cd, shipmentId);
            // 删除出庫作業管理テーブル 以及出庫作業明細 的数据
            try {
                stockDao.deleteStockHistory(jsonObject.getString("client_id"), shipmentId);
                shipmentResultDao.deleteShipmentResultInfo(workId, warehouse_cd);
            } catch (Exception e) {
                logger.error("删除出庫作業管理テーブル失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            try {
                shipmentResultDao.deleteShipmentResultDetailInfo(workId, warehouse_cd, shipmentId);
            } catch (Exception e) {
                logger.error("删除以及出庫作業明細失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }

            String[] shipment_id = {
                shipmentId
            };
            int status = jsonObject.getInteger("status");
            try {
                // 修改 出庫管理テーブル 的 出庫ステータス 为 出荷待ち
                String operation_cd = "81"; // operation_cd
                setShipmentStatus(servletRequest, warehouse_cd, status, shipment_id, null,
                    status_message, null, false, operation_cd, null);
            } catch (Exception e) {
                logger.error("修改出库状态失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    /**
     * @Description: 出荷検品サイズPDF
     * @Param: jsonObject
     * @return: SUCCESS
     * @Date: 2020/9/14
     */
    @Override
    public JSONObject shipmentSizePdf() {
        String path = pathProps.getRoot() + pathProps.getWms() + "size/";
        String pdfName = "size.pdf";
        String pdfPath = path + pdfName;
        String codeNull = "     ";
        List<Ms010_product_size> list = commonService.getProductSize();

        for (int i = 0; i < list.size(); i++) {
            String codeName = codeNull + list.get(i).getName() + codeNull;
            String codePath = path + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, 0.2);
        }
        try {
            PdfTools.shipmentSizePdf(list, pdfPath, path, codeNull);
        } catch (Exception e) {
            e.printStackTrace();
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
    public JSONObject uploadConfirmImg(MultipartFile[] files, String[] shipment_plan_id, String client_id) {
        if (files.length < 1) {
            logger.error("アップロードしたファイルは空です");
        }
        String nowTime = DateUtils.getDateDay();
        // 图片路径
        String uploadPath =
            pathProps.getRoot() + pathProps.getStore() + client_id + "/shipment/approval/" + nowTime + "/";
        for (MultipartFile file : files) {
            String filePath = CommonUtils.uploadFile(file, uploadPath,
                pathProps.getStore() + client_id + "/shipment/approval/" + nowTime + "/");
            try {
                shipmentDao.setConfirmImg(shipment_plan_id, filePath);
            } catch (Exception e) {
                logger.error("梱包作業画像を添付上传失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库id
     * @param workIdList : 作业者Id集合
     * @description: 获取トータルピッキングlist
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 16:18
     */
    @Override
    public JSONObject getPickingList(String warehouse_cd, List<Integer> workIdList) {
        List<String> shipmentIdList = null;
        if (!StringTools.isNullOrEmpty(workIdList) && workIdList.size() > 0) {
            try {
                shipmentIdList = shipmentResultDetailDao.getShipmentIdListByWorkId(workIdList);
            } catch (Exception e) {
                logger.error("仓库Id = {},workId = {},根据作業管理ID 获取出庫依頼ID失败", warehouse_cd, workIdList);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        // 查询以商品Id为单位的 出库依赖集合
        List<Tw212_shipment_location_detail> pickingList = shipmentResultDao.getPickingList(warehouse_cd,
            shipmentIdList);
        HashMap<String, Tw212_shipment_location_detail> map = new HashMap<>();
        pickingList.forEach(detail -> {
            String key = detail.getLocation_id() + "-" + detail.getProduct_id();
            List<Integer> idList = new ArrayList<>();
            idList.add(detail.getId());
            if (map.containsKey(key)) {
                Tw212_shipment_location_detail tw212_shipment_location_detail = map.get(key);
                int product_cnt = tw212_shipment_location_detail.getReserve_cnt() + detail.getReserve_cnt();
                List<Integer> list = tw212_shipment_location_detail.getIdList();
                list.stream().sequential().collect(Collectors.toCollection(() -> idList));
                detail.setReserve_cnt(product_cnt);
            }
            detail.setIdList(idList);
            map.put(key, detail);
        });
        List<Tw212_shipment_location_detail> collect = new ArrayList<>(map.values());
        return CommonUtils.success(collect);
    }

    /**
     * @param jsonObject : 存的出庫作業ロケ明細 Id
     * @description: 修改 トータルピッキング确认状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 15:46
     */
    @Override
    @Transactional
    public JSONObject updatePinkingStatus(JSONObject jsonObject, HttpServletRequest request) {

        JSONArray idList = jsonObject.getJSONArray("idList");
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < idList.size(); i++) {
            arrayList.add(Integer.valueOf(idList.getString(i)));
        }
        if (arrayList.size() == 0) {
            logger.error("没有选择需要确认的トータルピッキング");
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        ArrayList<Integer> collect = (ArrayList<Integer>) arrayList.stream().distinct().collect(toList());
        // 修改 トータルピッキング确认状态
        try {
            shipmentResultDao.updatePinkingStatus(collect);
        } catch (Exception e) {
            logger.error("修改 トータルピッキング确认状态");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        List<Tw212_shipment_location_detail> locationDetailList = shipmentResultDao.getPickingListById(collect);
        String nowTime = jsonObject.getString("nowTime");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = format.format(new Date());
        String value = nowTime;
        if (StringTools.isNullOrEmpty(value)) {
            value = newDate;
        }
        Timestamp start_date = Timestamp.valueOf(value);
        Timestamp end_date = Timestamp.valueOf(newDate);
        String loginNm = CommonUtils.getToken("login_nm", request);
        String user_id = CommonUtils.getToken("user_id", request);
        JSONObject json = new JSONObject();
        locationDetailList.forEach(detail -> {
            json.put("warehouse_cd", detail.getWarehouse_cd());
            json.put("user_id", user_id);
            json.put("start_date", start_date);
            json.put("end_date", end_date);
            json.put("loginNm", loginNm);
            json.put("client_id", detail.getClient_id());
            json.put("shipment_plan_id", detail.getShipment_plan_id());
            json.put("product_id", detail.getProduct_id());
            json.put("product_plan_cnt", detail.getProduct_plan_cnt());
            shipmentResultDao.insertTotalPicking(json);
            shipmentResultDao.insertTotalPickingDetail(json);
        });
        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @description: 获取没有トータルピッキング确认的作业者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 17:52
     */
    @Override
    public JSONObject getWorkInfo(String warehouse_cd) {
        List<String> resultDaoShipmentId = shipmentResultDao.getShipmentId(warehouse_cd);
        List<String> shipmentId = resultDaoShipmentId.stream().distinct().collect(toList());
        List<Tw210_shipment_result> resultDaoWorkInfo = shipmentResultDao.getWorkInfo(shipmentId, warehouse_cd);
        // 根据workName 去重
        ArrayList<Tw210_shipment_result> workInfo = resultDaoWorkInfo.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(
                    () -> new TreeSet<>(Comparator.comparing(Tw210_shipment_result::getWork_name))),
                ArrayList::new));
        // 根据workName 降序
        List<Tw210_shipment_result> results = workInfo.stream()
            .sorted(Comparator.comparing(Tw210_shipment_result::getWork_name).reversed()).collect(toList());
        return CommonUtils.success(results);
    }

    /**
     * @param warehouse_cd : 仓库id
     * @param startDate: 起始时间
     * @param endDate: 结束时间
     * @param type: 1：出庫依頼ごとトータルピッキングレポート 2：作業者ごとトータルピッキングレポート
     * @param outputType: 出力単位: 0 指定期間の総計 1 指定期間の日付別
     * @description: とトータルピッキングレポートCSV数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/9 16:52
     */
    @Override
    public JSONObject getStatistics(String warehouse_cd, String startDate, String endDate, Integer type,
        String outputType) {
        Timestamp start = !StringTools.isNullOrEmpty(startDate) ? Timestamp.valueOf(startDate) : null;
        Timestamp end = !StringTools.isNullOrEmpty(endDate) ? Timestamp.valueOf(endDate) : null;
        List<Tw214_total_picking> totalPickingList = shipmentResultDao.getTotalPickingList(warehouse_cd, start, end);
        JSONArray jsonArray = new JSONArray();
        if (type == 1) {
            totalPickingList.forEach(data -> {
                JSONObject json = new JSONObject();
                Timestamp start_date = data.getStart_date();
                if (!StringTools.isNullOrEmpty(start_date)) {
                    String startTime = start_date.toString();
                    json.put("start_date", startTime.substring(0, 10));
                }
                Timestamp end_date = data.getEnd_date();
                if (!StringTools.isNullOrEmpty(end_date)) {
                    String endTime = end_date.toString();
                    json.put("end_date", endTime.substring(0, 10));
                }
                json.put("total_picking_id", data.getTotal_picking_id());
                json.put("warehouse_cd", data.getWarehouse_cd());
                json.put("ins_usr", data.getIns_usr());
                json.put("client_id", data.getClient_id());
                json.put("shipment_plan_id", data.getShipment_plan_id());
                json.put("product_id", data.getProduct_id());
                json.put("code", data.getCode());
                json.put("name", data.getName());
                json.put("product_plan_cnt", data.getProduct_plan_cnt());
                jsonArray.add(json);
            });
            JSONArray array = jsonArraySort(jsonArray.toString(), "client_id", "shipment_plan_id", "product_id", true);
            return CommonUtils.success(array);
        } else {
            List<Tw214_total_picking> pickingInsUser = shipmentResultDao.getPickingInsUser(warehouse_cd, start, end);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if ("0".equals(outputType)) {
                HashMap<String, JSONObject> map = new HashMap<>();
                pickingInsUser.forEach(pick -> {
                    String insUsr = pick.getIns_usr();
                    boolean containsKey = map.containsKey(insUsr);
                    JSONObject jsonObject;
                    String minTime = "";
                    String maxTime = "";
                    long value = 0;
                    if (containsKey) {
                        jsonObject = map.get(insUsr);
                        value = Integer.parseInt(jsonObject.getString("value"));
                        minTime = jsonObject.getString("minTime");
                        maxTime = jsonObject.getString("maxTime");
                        String minDate = pick.getStart_date().toString();
                        String maxDate = pick.getEnd_date().toString();
                        try {
                            long min = df.parse(minDate).getTime();
                            long max = df.parse(maxDate).getTime();
                            value += (max - min) / 1000 / 60;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int compare = minTime.compareTo(minDate);
                        minTime = compare > 0 ? minDate : minTime;
                        int compareTo = maxTime.compareTo(maxDate);
                        maxTime = compareTo > 0 ? maxTime : maxDate;
                    } else {
                        jsonObject = new JSONObject();
                        minTime = pick.getStart_date().toString();
                        maxTime = pick.getEnd_date().toString();
                        try {
                            long min = df.parse(minTime).getTime();
                            long max = df.parse(maxTime).getTime();
                            value = (max - min) / 1000 / 60;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    jsonObject.put("minTime", minTime);
                    jsonObject.put("maxTime", maxTime);
                    jsonObject.put("value", value);
                    jsonObject.put("date", minTime.substring(0, 10) + "~" + maxTime.substring(0, 10));
                    map.put(insUsr, jsonObject);
                });
                List<String> userName = pickingInsUser.stream().map(Tw214_total_picking::getIns_usr).distinct()
                    .collect(toList());

                userName.forEach(name -> {
                    List<Tw214_total_picking> totalPickings = pickingInsUser.stream()
                        .filter(user -> user.getIns_usr().equals(name)).collect(toList());
                    JSONObject jsonObject = new JSONObject();
                    // 作業者
                    jsonObject.put("user", name);
                    jsonObject.put("date", map.get(name).getString("date"));
                    // 倉庫コード
                    jsonObject.put("warehouse_cd", warehouse_cd);
                    // 合計作業数（指定期間中のトータルピッキングIDの件数）
                    jsonObject.put("pickCnt", totalPickings.size());
                    // 合計作業時間（指定期間中の開始時間-終了時間の合計）
                    jsonObject.put("time", map.get(name).getString("value"));
                    // 获取到トータルピッキングID集合
                    List<Integer> pickingIdList = totalPickings.stream().map(Tw214_total_picking::getTotal_picking_id)
                        .distinct().collect(toList());
                    List<Tw215_total_picking_detail> pickingDetail = shipmentResultDao.getPickingDetail(pickingIdList,
                        name);
                    // 合計出庫依頼数（指定期間中の出庫依頼IDの件数）
                    jsonObject.put("shipmentIdCnt", pickingDetail.stream()
                        .map(Tw215_total_picking_detail::getShipment_plan_id).distinct().count());
                    // 合計商品数（指定期間中の出庫依頼IDの中に入っている商品の件数）
                    jsonObject.put("productCnt",
                        pickingDetail.stream().map(Tw215_total_picking_detail::getProduct_id).distinct().count());
                    // 合計商品個数（指定期間中の出庫依頼IDの中に入っている商品の数量の合計）
                    jsonObject.put("productPlanCnt",
                        pickingDetail.stream().mapToInt(Tw215_total_picking_detail::getProduct_plan_cnt).sum());
                    jsonArray.add(jsonObject);
                });
            } else {
                List<String> userName = pickingInsUser.stream().map(Tw214_total_picking::getIns_usr).distinct()
                    .collect(toList());
                userName.forEach(name -> {
                    List<Tw214_total_picking> totalPickings = pickingInsUser.stream()
                        .filter(user -> user.getIns_usr().equals(name)).collect(toList());
                    HashMap<String, List<Integer>> hashMap = new HashMap<>();
                    HashMap<String, Long> map = new HashMap<>();

                    totalPickings.forEach(total -> {
                        long value = 0;
                        String start_date = total.getStart_date().toString().substring(0, 10);
                        String minDate = total.getStart_date().toString();
                        String maxDate = total.getEnd_date().toString();
                        try {
                            long min = df.parse(minDate).getTime();
                            long max = df.parse(maxDate).getTime();
                            value += (max - min) / 1000 / 60;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        boolean containsKey = hashMap.containsKey(start_date);
                        boolean keyContain = map.containsKey(start_date);
                        if (keyContain) {
                            value += map.get(start_date);
                        }
                        map.put(start_date, value);
                        List<Integer> ids = new ArrayList<>();
                        if (containsKey) {
                            ids = hashMap.get(start_date);
                        }
                        boolean contains = ids.contains(total.getTotal_picking_id());
                        if (!contains) {
                            ids.add(total.getTotal_picking_id());
                        }
                        hashMap.put(start_date, ids);
                    });
                    for (Map.Entry<String, List<Integer>> entry : hashMap.entrySet()) {
                        JSONObject jsonObject = new JSONObject();
                        // 获取到トータルピッキングID集合
                        List<Integer> idList = entry.getValue();
                        List<Tw215_total_picking_detail> pickingDetail = shipmentResultDao.getPickingDetail(idList,
                            name);

                        // 作業者
                        jsonObject.put("user", name);
                        // 作业期間
                        jsonObject.put("date", entry.getKey());
                        // 倉庫コード
                        jsonObject.put("warehouse_cd", warehouse_cd);
                        // 合計作業数（指定期間中のトータルピッキングIDの件数）
                        jsonObject.put("pickCnt", idList.size());
                        // 合計作業時間（指定期間中の開始時間-終了時間の合計）
                        jsonObject.put("time", map.get(entry.getKey()));
                        // 合計出庫依頼数（指定期間中の出庫依頼IDの件数）
                        jsonObject.put("shipmentIdCnt", pickingDetail.stream()
                            .map(Tw215_total_picking_detail::getShipment_plan_id).distinct().count());
                        // 合計商品数（指定期間中の出庫依頼IDの中に入っている商品の件数）
                        jsonObject.put("productCnt", pickingDetail.stream()
                            .map(Tw215_total_picking_detail::getProduct_id).distinct().count());
                        // 合計商品個数（指定期間中の出庫依頼IDの中に入っている商品の数量の合計）
                        jsonObject.put("productPlanCnt",
                            pickingDetail.stream().mapToInt(Tw215_total_picking_detail::getProduct_plan_cnt).sum());
                        jsonArray.add(jsonObject);
                    }
                });
            }
            return CommonUtils.success(jsonArray);
        }
    }

    /**
     * サイズ編集
     *
     * @param servletRequest
     * @param jsonObject
     * @date: 2021/5/17
     */
    @Override
    public Integer updateSize(HttpServletRequest servletRequest, JSONObject jsonObject) {
        Integer result = 0;
        // 個口数編集
        String size = jsonObject.getString("size");
        if (StringTools.isNullOrEmpty(size)) {
            return result;
        }

        String[] shipment_plan_id = jsonObject.getString("shipment_plan_id").split(",");

        for (int i = 0; i < shipment_plan_id.length; i++) {
            Tw200_shipment shipments = new Tw200_shipment();
            shipments.setWarehouse_cd(jsonObject.getString("warehouse_cd"));
            shipments.setShipment_plan_id(shipment_plan_id[i]);
            shipments.setSize_cd(jsonObject.getString("size"));
            result = shipmentDao.updateSize(shipments);
        }

        // 顧客別作業履歴新增
        String operation_cd = "45"; // 出荷作業中にサイズ変更
        customerHistoryService.insertCustomerHistory(servletRequest, shipment_plan_id, operation_cd, null);

        return result;
    }

    /**
     * @Description: 検品済み 佐川急便
     * @Param: Tw200_shipment, fileName
     * @return: void
     * @Date: 2020/11/11
     */
    private void sagawaCsv(Tw200_shipment shipment, String rule, String fileName, String deliveryMethodCsv,
        Mw400_warehouse warehouse) {
        List<String[]> csvData = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        String sagawaHeader = "";
        // 佐川コンビニ受取
        if (deliveryMethodCsv.equals("641")) {
            sagawaHeader =
                "住所録コード,お届け先電話番号,お届け先郵便番号,お届け先住所1,お届け先住所2,お届け先住所3,お届け先名称１,お届け先_名称2,お客様管理ナンバー,お客様コード,部署・担当者,荷送人電話番号,ご依頼主電話番号,ご依頼主郵便番号,ご依頼主ご住所1,ご依頼主ご住所2,ご依頼主名称1,ご依頼主名称2,荷姿コード,注文者名１,注文者名２,品名1,品名2,品名3,品名4,品名5,出荷個数,便種（スピードで選択）,便種（商品）,配達日,配達指定時間帯,配達指定時間（時分）,代引金額, 消費税,決済種別,保険金額,保険金額印字,指定シール1,指定シール2, 指定シール3,営業店止め,SRC区分,営業店コード,元着区分,店舗種別コード,ＥＣコード,コンビニ店舗コード,返品指示日,返品区分,返品先郵便番号,返品先住所１,返品先住所２,返品先住所３,返品先名称１,返品先名称２,返品先電話番号,認証番号,コンビニ独自ＩＤ,コンビニ支払総額,コンビニ支払消費税,コンビニ決済区分,コンビニ受取開始日,コンビニ着予定日,メールアドレス";
        } else {
            // 佐川急便
            sagawaHeader =
                "住所録コード,お届け先電話番号,お届け先郵便番号,お届け先住所1,お届け先住所2,お届け先住所3,お届け先名称１,お届け先_名称2,お客様管理ナンバー,お客様コード,部署・担当者,荷送人電話番号,ご依頼主電話番号,ご依頼主郵便番号,ご依頼主ご住所1,ご依頼主ご住所2,ご依頼主名称1,ご依頼主名称2,荷姿コード,品名1,品名2,品名3,品名4,品名5,出荷個数,便種（スピードで選択）,便種（商品）,配達日,配達指定時間帯,配達指定時間（時分）,代引金額,消費税,決済種別,保険金額,保険金額印字,指定シール1,指定シール2,指定シール3,営業店止め,SRC区分,営業店コード,元着区分";
        }
        // String sagawaHeader =
        // "住所録コード,お届け先電話番号,お届け先郵便番号,お届け先住所1,お届け先住所2,お届け先住所3,お届け先名称１,お届け先_名称2,お客様管理ナンバー,お客様コード,部署・担当者,荷送人電話番号,ご依頼主電話番号,ご依頼主郵便番号,ご依頼主ご住所1,ご依頼主ご住所2,ご依頼主名称1,ご依頼主名称2,荷姿コード,品名1,品名2,品名3,品名4,品名5,出荷個数,便種（スピードで選択）,便種（商品）,配達日,配達指定時間帯,配達指定時間（時分）,代引金額,消費税,決済種別,保険金額,保険金額印字,指定シール1,指定シール2,指定シール3,営業店止め,SRC区分,営業店コード,元着区分";
        String[] sagawaData = new String[sagawaHeader.split(rule, -1).length];

        // 電話番号
        String phone = shipment.getPhone();
        if (StringTools.isNullOrEmpty(phone) && !deliveryMethodCsv.equals("641")) {
            phone = "000-0000-0000";
        }
        sagawaData[1] = phone;
        // お届け先郵便番号
        sagawaData[2] = shipment.getPostcode();
        String add = shipment.getPrefecture() + shipment.getAddress1();
        if (!StringTools.isNullOrEmpty(shipment.getAddress2())) {
            add += shipment.getAddress2();
        }

        HashMap<String, String> address = CommonUtils.sagawaToAdderss(add, 32);
        // お届け先住所1
        sagawaData[3] = address.get("add1");
        // お届け先住所2
        sagawaData[4] = address.get("add2");
        // お届け先住所3
        sagawaData[5] = address.get("add3");
        // 送り状特記事項
        if (!StringTools.isNullOrEmpty(shipment.getInvoice_special_notes())) {
            sagawaData[22] = shipment.getInvoice_special_notes();
        }
        if (!deliveryMethodCsv.equals("641")) {
            // お届け先名称１
            String surname1 = "", surname2 = "";
            // 获取会社名、名前、部署
            String company = shipment.getCompany();
            String surname = shipment.getSurname();
            String division = shipment.getDivision();
            if (StringTools.isNullOrEmpty(division)) {
                division = " ";
            }
            // 判断部署并拼接
            if (!StringTools.isNullOrEmpty(division)) {
                surname = division + " " + surname;
            }
            if (!StringTools.isNullOrEmpty(company)) {
                surname1 = company;
                surname2 = surname;
            } else {
                surname1 = surname;
                surname2 = "";
            }
            sagawaData[6] = surname1;
            sagawaData[7] = surname2;
            // お客様コード
            sagawaData[9] = shipment.getCustomer_code();

            // お客様管理ナンバー
            sagawaData[8] = shipment.getShipment_plan_id();
            // 部署・担当者
            sagawaData[10] = shipment.getMs012_sponsor_master().getDivision();
            // ご依頼主電話番号
            String sponsorPhone = shipment.getMs012_sponsor_master().getPhone();
            if (StringTools.isNullOrEmpty(sponsorPhone)) {
                sponsorPhone = "000-0000-0000";
            }
            sagawaData[12] = sponsorPhone;
            // ご依頼主郵便番号
            sagawaData[13] = shipment.getMs012_sponsor_master().getPostcode();

            // ご依頼主ご住所1
            String sponsorAddress =
                shipment.getMs012_sponsor_master().getPrefecture() + shipment.getMs012_sponsor_master().getAddress1();
            String sponsorAddress2 = shipment.getMs012_sponsor_master().getAddress2();
            if (!StringTools.isNullOrEmpty(sponsorAddress2)) {
                sponsorAddress += sponsorAddress2;
            }

            if (!StringTools.isNullOrEmpty(sponsorAddress)) {
                HashMap<String, String> sponsorAdd = CommonUtils.sagawaToAdderss(sponsorAddress, 32);
                // ご依頼主ご住所1
                sagawaData[14] = sponsorAdd.get("add1");
                // ご依頼主ご住所2
                String add2 = sponsorAdd.get("add2");
                if (!StringTools.isNullOrEmpty(sponsorAdd.get("add3"))) {
                    add2 += sponsorAdd.get("add3");
                }
                sagawaData[15] = add2;
                // ご依頼主名称2 ※スマートの場合 「ご依頼主ご住所3」として取込対象となる @Add WQS 20210705
                // sagawaData[17] = sponsorAdd.get("add3");
            }

            // ご依頼主名称1
            sagawaData[16] = shipment.getMs012_sponsor_master().getName();
            // ご依頼主名称2
            sagawaData[17] = shipment.getMs012_sponsor_master().getName_kana();
            // 荷姿コード
            sagawaData[18] = shipment.getSagawa_nisugata_code();
            // 品名1
            String label_note = shipment.getLabel_note();
            if (StringTools.isNullOrEmpty(label_note)) {
                label_note = "商品在中";
            }
            sagawaData[19] = label_note;
            // 品名5
            if (shipment.getBox_delivery() == 1) {
                sagawaData[23] = "不在時宅配ボックス";
            }

            if (StringTools.isNullOrEmpty(shipment.getBoxes())) {
                sagawaData[24] = "1";
            } else {
                sagawaData[24] = shipment.getBoxes().toString();
            }
            // 便種（スピードで選択）
            sagawaData[25] = deliveryMethodCsv;
            // 配達日
            Date delivery_date = shipment.getDelivery_date();
            String dateDelivery = "";
            if (!StringTools.isNullOrEmpty(delivery_date)) {
                dateDelivery = simpleDateFormat.format(delivery_date);
            }
            sagawaData[27] = dateDelivery;

            // 配達指定時間帯
            String delivery_time_slot = shipment.getDelivery_time_slot();
            if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
                List<Ms006_delivery_time> deliveryTime = deliveryDao.getDeliveryTime(null,
                    Integer.valueOf(delivery_time_slot), null, null);
                if (deliveryTime.size() != 0) {
                    delivery_time_slot = deliveryTime.get(0).getDelivery_time_csv();
                }
            }

            sagawaData[28] = delivery_time_slot;

            // 代引金額
            if ("2".equals(shipment.getPayment_method())
                && !StringTools.isNullOrEmpty(shipment.getTotal_for_cash_on_delivery())
                && shipment.getTotal_for_cash_on_delivery() > 0) {
                sagawaData[30] = shipment.getTotal_for_cash_on_delivery().toString();
            } else {
                sagawaData[30] = "";
            }

            // 指定シール1
            if (shipment.getFragile_item() == 1) {
                sagawaData[35] = "011";
            }
            // 元着区分
            if (!StringTools.isNullOrEmpty(shipment.getDelivery_carrier())
                && shipment.getDelivery_carrier().equals("26")) {
                sagawaData[41] = "2";
            } else {
                sagawaData[41] = "1";
            }
        } else {
            String address2 = shipment.getAddress2();
            String substring = address2.substring(0, address2.length() - 1);
            List<String> splitToList = Splitter.on("(").trimResults().omitEmptyStrings().splitToList(substring);
            // お届け先名称１
            sagawaData[6] = splitToList.get(0);
            // お客様管理ナンバー
            sagawaData[8] = shipment.getShipment_plan_id();
            // 部署・担当者
            sagawaData[10] = shipment.getMs012_sponsor_master().getDivision();
            // 注文者名１
            sagawaData[19] = shipment.getSurname();
            // 品名1
            String label_note = shipment.getLabel_note();
            if (StringTools.isNullOrEmpty(label_note)) {
                label_note = "商品在中";
            }
            sagawaData[21] = label_note;
            // 便種（スピードで選択）
            // sagawaData[27] = shipment.getDelivery_code_csv();
            sagawaData[25] = deliveryMethodCsv;

            // 代引金額
            if ("2".equals(shipment.getPayment_method())
                && !StringTools.isNullOrEmpty(shipment.getTotal_for_cash_on_delivery())
                && shipment.getTotal_for_cash_on_delivery() > 0) {
                sagawaData[30] = shipment.getTotal_for_cash_on_delivery().toString();
            } else {
                sagawaData[30] = "";
            }

            // a元着区分
            if ("26".equals(shipment.getDelivery_carrier())) {
                sagawaData[41] = "2";
            } else {
                sagawaData[41] = "1";
            }
            // 店舗種別コード
            List<String> toList = Splitter.on("-").trimResults().omitEmptyStrings().splitToList(splitToList.get(1));
            sagawaData[42] = toList.get(0);
            // ＥＣコード
            sagawaData[43] = "9001";
            // コンビニ店舗コード
            sagawaData[44] = toList.get(1);
            // 返品先郵便番号
            sagawaData[47] = warehouse.getZip();
            // 返品先住所 1
            sagawaData[48] = warehouse.getAddress1();
            // 返品先住所２
            sagawaData[49] = warehouse.getAddress2();
            // 返品先名称１
            sagawaData[51] = warehouse.getWarehouse_nm();
            // 返品先電話番号
            sagawaData[53] = warehouse.getTel();
            // 認証番号
            sagawaData[54] = "";
            // メールアドレス 佐川コンビニ受取时获取受注番号
            sagawaData[61] = shipment.getOrder_no();
        }

        csvData.add(sagawaData);
        CsvUtils.write(fileName, sagawaHeader, csvData, false);
    }

    /**
     * @Description: 検品済み 日本郵便
     * @Param: Tw200_shipment, fileName
     * @return: void
     * @Date: 2020/11/11
     */
    private void japanpostCsv(Tw200_shipment shipment, String rule, String fileName, String deliveryMethodCsv) {

        List<String[]> csvData = new ArrayList<>();
        String japanpostHeader =
            "\"お客様側管理番号\",\"お問い合わせ番号\",\"\",\"発送予定日\",\"\",\"\",\"\",\"郵便種別\",\"保冷種別\",\"元／着払／代引\",\"書留／セキュリティ種別\",\"\",\"送り状種別\",\"\",\"お届け先  郵便番号\",\"お届け先 住所1\",\"お届け先 住所2\",\"お届け先 住所3\",\"お届け先 名称\",\"\",\"\",\"お届け先 電話番号\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"ご依頼主  郵便番号\",\"ご依頼主 住所1\",\"ご依頼主 住所2\",\"ご依頼主 住所3\",\"ご依頼主 名称\",\"\",\"\",\"ご依頼主 電話番号\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"商品サイズ／厚さ区分\",\"\",\"\",\"\",\"\",\"配達希望日\",\"配達時間帯区分\",\"\",\"\",\"\",\"\",\"\",\"複数個口数\",\"記事名１\",\"記事名２\",\"フリー項目０１\",\"フリー項目０２\",\"フリー項目０３\",\"フリー項目０４\",\"フリー項目０５\",\"フリー項目０６\",\"フリー項目０７\",\"フリー項目０８\",\"フリー項目０９\",\"フリー項目１０\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"代引金額\",\"\",\"\",\"商品番号\",\"品名\"";
        String[] japanpostData = new String[japanpostHeader.split(rule, -1).length];

        // お客様側管理番号
        japanpostData[0] = shipment.getShipment_plan_id();
        // 発送予定日
        if (!StringTools.isNullOrEmpty(shipment.getShipment_plan_date())) {
            japanpostData[3] = CommonUtils.dateToStr(shipment.getShipping_date());
        } else {
            japanpostData[3] = CommonUtils.dateToStr(DateUtils.getDate());
        }
        // 郵便種別
        japanpostData[7] = deliveryMethodCsv;
        // 保冷種別
        japanpostData[8] = "0";
        // 元／着払／代引
        if ("2".equals(shipment.getPayment_method())) {
            japanpostData[9] = "2";
        } else {
            japanpostData[9] = "0";
        }

        // 書留／セキュリティ種別
        japanpostData[10] = null;
        // 送り状種別
        japanpostData[12] = null;
        // お届け先 郵便番号
        japanpostData[14] = shipment.getPostcode();
        // お届け先 住所1
        String add = shipment.getPrefecture() + shipment.getAddress1();
        if (!StringTools.isNullOrEmpty(shipment.getAddress2())) {
            add += shipment.getAddress2();
        }
        if (!StringTools.isNullOrEmpty(shipment.getAddress3())) {
            add += shipment.getAddress3();
        }

        HashMap<String, String> address = CommonUtils.sagawaToAdderss(add, 32);
        // お届け先住所1
        japanpostData[15] = address.get("add1");
        // お届け先住所2
        japanpostData[16] = address.get("add2");
        // お届け先住所3
        japanpostData[17] = address.get("add3");
        // お届け先 名称
        String surname = "";
        // 临时名前变量
        String surname_tmp = "";
        // 判断部署变量并拼接 会社名＋部署＋名前
        if (!StringTools.isNullOrEmpty(shipment.getDivision())) {
            surname_tmp = shipment.getDivision() + " " + shipment.getSurname();
        } else {
            surname_tmp = shipment.getSurname();
        }
        if (!StringTools.isNullOrEmpty(shipment.getCompany())) {
            surname = shipment.getCompany() + " " + surname_tmp;
        } else {
            surname = surname_tmp;
        }
        japanpostData[18] = surname;
        // お届け先 電話番号
        String phone = shipment.getPhone();
        if (StringTools.isNullOrEmpty(phone)) {
            phone = "000-0000-0000";
        }
        japanpostData[21] = phone;
        // ご依頼主 郵便番号
        japanpostData[31] = shipment.getMs012_sponsor_master().getPostcode();
        // ご依頼主 住所1
        add = shipment.getMs012_sponsor_master().getPrefecture() + shipment.getMs012_sponsor_master().getAddress1();
        if (!StringTools.isNullOrEmpty(shipment.getMs012_sponsor_master().getAddress2())) {
            add += shipment.getMs012_sponsor_master().getAddress2();
        }

        HashMap<String, String> sponsorAdd = CommonUtils.sagawaToAdderss(add, 32);
        // ご依頼主ご住所1
        japanpostData[32] = sponsorAdd.get("add1");
        // ご依頼主ご住所2
        japanpostData[33] = sponsorAdd.get("add2");
        // ご依頼主ご住所3
        japanpostData[34] = sponsorAdd.get("add3");
        // ご依頼主 名称
        japanpostData[35] = shipment.getMs012_sponsor_master().getName();
        // ご依頼主 電話番号
        String sponsorPhone = shipment.getMs012_sponsor_master().getPhone();
        if (StringTools.isNullOrEmpty(sponsorPhone)) {
            sponsorPhone = "000-0000-0000";
        }
        japanpostData[38] = sponsorPhone;
        // 商品サイズ／厚さ区分
        japanpostData[61] = "030";
        // 配達希望日
        japanpostData[66] = CommonUtils.dateToStr(shipment.getDelivery_date());
        // 配達時間帯区分
        String id = shipment.getDelivery_time_slot();
        if (!StringTools.isNullOrEmpty(id)) {
            japanpostData[67] = deliveryDao.getDeliveryTimeById(Integer.valueOf(id)).getDelivery_time_csv();
        }
        // 複数個口数
        if (!StringTools.isNullOrEmpty(shipment.getBoxes())) {
            japanpostData[73] = shipment.getBoxes().toString();
        } else {
            japanpostData[73] = "1";
        }
        // 記事名１ 送り状特記事項
        if (!StringTools.isNullOrEmpty(shipment.getInvoice_special_notes())) {
            japanpostData[74] = shipment.getInvoice_special_notes();
        }
        // 記事名２
        japanpostData[75] = null;
        // フリー項目０１
        japanpostData[76] = null;
        // フリー項目０２
        japanpostData[77] = null;
        // フリー項目０３
        japanpostData[78] = null;
        // フリー項目０４
        japanpostData[79] = null;
        // フリー項目０５
        japanpostData[80] = null;
        // フリー項目０６
        japanpostData[81] = null;
        // フリー項目０７
        japanpostData[82] = null;
        // フリー項目０８
        japanpostData[83] = null;
        // フリー項目０９
        japanpostData[84] = null;
        // フリー項目１０
        japanpostData[85] = null;
        // 代引金額
        if ("2".equals(shipment.getPayment_method())
            && !StringTools.isNullOrEmpty(shipment.getTotal_for_cash_on_delivery())
            && shipment.getTotal_for_cash_on_delivery() > 0) {
            japanpostData[100] = shipment.getTotal_for_cash_on_delivery().toString();
        } else {
            japanpostData[100] = "";
        }
        // 商品番号
        japanpostData[103] = null;
        // 品名
        String label_note = shipment.getLabel_note();
        if (StringTools.isNullOrEmpty(label_note)) {
            label_note = "商品在中";
        }
        japanpostData[104] = label_note;

        csvData.add(japanpostData);
        CsvUtils.write(fileName, japanpostHeader, csvData, false);
    }

    /**
     * @Description: 検品済み ヤマト運輸
     * @Param: Tw200_shipment, fileName
     * @return: void
     * @Date: 2020/11/11
     */
    private void yamatoCsv(Tw200_shipment shipment, String rule, String fileName, String deliveryMethodCsv) {
        List<String[]> csvData = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        String yamatoHeader =
            "お客様管理番号,送り状種別,クール区分,伝票番号,出荷予定日,お届け予定（指定）日,配達時間帯,お届け先コード,お届け先電話番号,お届け先電話番号枝番,お届け先郵便番号,お届け先住所,お届け先住所（アパートマンション名）,お届け先会社・部門名１,お届け先会社・部門名２,お届け先名,お届け先名略称カナ,敬称,ご依頼主コード,ご依頼主電話番号,ご依頼主電話番号枝番,ご依頼主郵便番号,ご依頼主住所,ご依頼主住所（アパートマンション名）,ご依頼主名,ご依頼主略称カナ,品名コード１,品名１,品名コード２,品名２,荷扱い１,荷扱い２,記事,コレクト代金引換額（税込）,コレクト内消費税額等,営業所止置き,営業所コード,発行枚数,個数口表示フラグ,ご請求顧客コード,ご請求先分類コード,運賃管理番号,注文時カード払いデータ登録,注文時カード払い加盟店番号,注文時カード払い申込受付番号１,注文時カード払い申込受付番号２,注文時カード払い申込受付番号３,お届け予定ｅメール利用区分,お届け予定ｅメールe-mailアドレス,入力機種,お届け予定eメールメッセージ,お届け完了ｅメール利用区分,お届け完了ｅメールe-mailアドレス,お届け完了eメールメッセージ,クロネコ収納代行利用区分,予備,収納代行請求金額(税込),収納代行内消費税額等,収納代行請求先郵便番号,収納代行請求先住所,収納代行請求先住所（アパートマンション名）,収納代行請求先会社・部門名１,収納代行請求先会社・部門名２,収納代行請求先名(漢字),収納代行請求先名(カナ),収納代行問合せ先名(漢字),収納代行問合せ先郵便番号,収納代行問合せ先住所,収納代行問合せ先住所（アパートマンション名）,収納代行問合せ先電話番号,収納代行管理番号,収納代行品名,収納代行備考,予備０１,予備０２,予備０３,予備０４,予備０５,予備０６,予備０７,予備０８,予備０９,予備１０,予備１１,予備１２,予備１３,投函予定メール利用区分,投函予定メールe-mailアドレス,投函予定メールメッセージ,投函完了メール(お届け先宛)利用区分,投函完了メール(お届け先宛)e-mailアドレス,投函完了メール(お届け先宛)メッセージ,投函完了メール(ご依頼主宛)利用区分,投函完了メール(ご依頼主宛)e-mailアドレス,投函完了メール(ご依頼主宛)メッセージ,連携管理番号,通知メールアドレス";
        String[] yamatoData = new String[yamatoHeader.split(rule, -1).length];

        // aお客様管理番号
        yamatoData[0] = shipment.getShipment_plan_id();
        // a送り状種別
        yamatoData[1] = deliveryMethodCsv;
        // a出荷予定日
        Date shipping_date = shipment.getShipping_date();

        String SDate = "";
        if (!StringTools.isNullOrEmpty(shipping_date)) {
            SDate = simpleDateFormat.format(shipping_date);
        } else {
            Date date = new Date();
            SDate = simpleDateFormat.format(date);
        }
        yamatoData[3] = shipment.getDelivery_tracking_nm();
        yamatoData[4] = SDate;
        // aお届け予定（指定）日
        Date delivery_date = shipment.getDelivery_date();
        String dateDelivery = "";
        if (!StringTools.isNullOrEmpty(delivery_date)) {
            dateDelivery = simpleDateFormat.format(delivery_date);
        }
        yamatoData[5] = dateDelivery;
        // a配達時間帯
        String delivery_time_slot = shipment.getDelivery_time_slot();
        if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
            List<Ms006_delivery_time> deliveryTime = deliveryDao.getDeliveryTime(null,
                Integer.valueOf(delivery_time_slot), null, null);
            if (deliveryTime.size() != 0) {
                delivery_time_slot = deliveryTime.get(0).getDelivery_time_csv();
            }
        }
        yamatoData[6] = delivery_time_slot;
        // aお届け先電話番号
        String phone = shipment.getPhone();
        if (StringTools.isNullOrEmpty(phone)) {
            phone = "000-0000-0000";
        }
        yamatoData[8] = phone;
        // aお届け先郵便番号
        yamatoData[10] = shipment.getPostcode();
        // aお届け先住所
        HashMap<String, String> address =
            CommonUtils.yamatoGetAdderss(shipment.getPrefecture(), shipment.getAddress1(), shipment.getAddress2());
        // // お届け先住所
        yamatoData[11] = address.get("add1");
        // // お届け先住所（アパートマンション名）
        yamatoData[12] = address.get("add2");
        // aお届け先会社・部門名１
        // aお届け先会社・部門名２
        String company = shipment.getCompany();
        String division = shipment.getDivision();
        if (StringTools.isNullOrEmpty(division)) {
            division = " ";
        }
        if (!StringTools.isNullOrEmpty(company)) {
            yamatoData[13] = company;
            yamatoData[14] = division;
        } else {
            yamatoData[13] = division;
            yamatoData[14] = " ";
        }
        // aお届け先名
        yamatoData[15] = shipment.getSurname();
        // aご依頼主コード
        // yamatoData[18] = shipment.getDelivery_code();
        // aご依頼主電話番号
        String sponsorPhone = shipment.getMs012_sponsor_master().getPhone();
        if (StringTools.isNullOrEmpty(sponsorPhone)) {
            sponsorPhone = "000-0000-0000";
        }
        yamatoData[19] = sponsorPhone;
        // aご依頼主郵便番号
        yamatoData[21] = shipment.getMs012_sponsor_master().getPostcode();
        // aご依頼主住所
        String sponsorAddress =
            shipment.getMs012_sponsor_master().getPrefecture() + shipment.getMs012_sponsor_master().getAddress1();
        String sponsorAddress2 = shipment.getMs012_sponsor_master().getAddress2();
        if (!StringTools.isNullOrEmpty(sponsorAddress2)) {
            sponsorAddress += sponsorAddress2;
        }
        HashMap<String, String> sponsorAdd = CommonUtils.yamatoToAdderss(sponsorAddress);

        // aご依頼主住所
        yamatoData[22] = sponsorAdd.get("add1");
        // aご依頼主住所2
        yamatoData[23] = sponsorAdd.get("add2");

        // aご依頼主名
        yamatoData[24] = shipment.getMs012_sponsor_master().getName();
        // a品名１
        String label_note = shipment.getLabel_note();
        if (StringTools.isNullOrEmpty(label_note)) {
            label_note = "商品在中";
        }
        yamatoData[27] = label_note;
        // a荷扱い１
        if (shipment.getFragile_item() == 1) {
            yamatoData[30] = "取扱注意";
        }
        // 荷扱い2
        if (shipment.getBox_delivery() == 1) {
            yamatoData[31] = "不在時宅配ボックス";
        }
        // a記事
        if (!StringTools.isNullOrEmpty(shipment.getInvoice_special_notes())) {
            yamatoData[32] = shipment.getInvoice_special_notes();
        }

        // 代引金額
        if ("2".equals(shipment.getDelivery_method())
            && !StringTools.isNullOrEmpty(shipment.getTotal_for_cash_on_delivery())
            && shipment.getTotal_for_cash_on_delivery() > 0) {
            yamatoData[33] = shipment.getTotal_for_cash_on_delivery().toString();
            // 当代金引换时，选择ヤマト宅急便コンパクトの"送り状種別"不是8，应是9
            if ("8".equals(deliveryMethodCsv)) {
                yamatoData[1] = "9";
            }
            // 当代金引换时，选择ヤマト宅急便の"送り状種別"不是8，应是9
            if ("0".equals(deliveryMethodCsv)) {
                yamatoData[1] = "2";
            }
        } else {
            yamatoData[33] = "";
        }

        // aご請求顧客コード
        yamatoData[39] = shipment.getBill_customer_cd();
        // aご請求先分類コード
        yamatoData[40] = shipment.getYamato_manage_code();
        // a運賃管理番号
        yamatoData[41] = shipment.getFare_manage_cd();

        csvData.add(yamatoData);
        CsvUtils.write(fileName, yamatoHeader, csvData, false);
    }

    /**
     * @Description: 検品済み 福山通運
     * @Param: Tw200_shipment, fileName
     * @return: void
     * @Date: 2020/11/11
     */
    private void fukutsuCsv(Tw200_shipment shipment, String rule, String fileName) {
        List<String[]> csvData = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fukutsuHeader =
            "荷受人コード,電話番号,住所,住所２,住所３,名前,名前２,郵便番号,特殊計,着店コード,荷送人コード,荷送担当者,個数,才数,重量,輸送商品１,輸送商品２,品名記事１,品名記事２,品名記事３,品名記事４,品名記事５,品名記事６,配達指定日,必着区分,お客様管理番号,元払区分,保険金額,出荷日付,登録日付";

        String[] fukutsuData = new String[fukutsuHeader.split(rule, -1).length];

        fukutsuData[0] = "";
        // 電話番号
        String phone = shipment.getPhone();
        if (StringTools.isNullOrEmpty(phone)) {
            phone = "000-0000-0000";
        }
        fukutsuData[1] = phone;
        // 住所
        String add = shipment.getPrefecture() + shipment.getAddress1();
        if (!StringTools.isNullOrEmpty(shipment.getAddress2())) {
            add += shipment.getAddress2();
        }

        HashMap<String, String> address = CommonUtils.sagawaToAdderss(add, 32);
        // お届け先住所1
        fukutsuData[2] = address.get("add1");
        // お届け先住所2
        fukutsuData[3] = address.get("add2");
        // お届け先住所3
        fukutsuData[4] = address.get("add3");
        // 名前
        String surname1 = "", surname2 = "";
        // 会社名
        String company = shipment.getCompany();
        // 名前
        String surname = shipment.getSurname();
        // 部署
        String division = shipment.getDivision();
        // 部署不为空时拼接
        if (!StringTools.isNullOrEmpty(division)) {
            surname = division + " " + surname;
        }
        if (!StringTools.isNullOrEmpty(shipment.getCompany())) {
            surname1 = company;
            surname2 = surname;
        } else {
            surname1 = surname;
            surname2 = "";
        }
        fukutsuData[5] = surname1;
        // 名前２
        fukutsuData[6] = surname2;
        // 郵便番号
        fukutsuData[7] = shipment.getPostcode();
        // 荷送人コード
        fukutsuData[10] = shipment.getDelivery_code();
        // 個数
        fukutsuData[12] = !StringTools.isNullOrEmpty(shipment.getBoxes()) ? shipment.getBoxes().toString() : "1";

        String product1 = "";
        String delivery_time_slot = shipment.getDelivery_time_slot();
        if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
            List<Ms006_delivery_time> deliveryTime = deliveryDao.getDeliveryTime(null,
                Integer.valueOf(delivery_time_slot), null, null);
            if (deliveryTime.size() != 0) {
                delivery_time_slot = deliveryTime.get(0).getDelivery_time_csv();
            }
            product1 = "100";
        }
        // 輸送商品１
        fukutsuData[15] = product1;
        // a輸送商品２
        fukutsuData[16] = delivery_time_slot;
        // 品名記事１
        String label_note = shipment.getLabel_note();
        if (StringTools.isNullOrEmpty(label_note)) {
            label_note = "商品在中";
        }
        fukutsuData[17] = label_note;
        // 品名記事4 送り状特記事項
        if (!StringTools.isNullOrEmpty(shipment.getInvoice_special_notes())) {
            fukutsuData[20] = shipment.getInvoice_special_notes();
        }
        // 品名記事５
        if (shipment.getFragile_item() == 1) {
            fukutsuData[21] = "取扱注意";
        }
        // 品名記事６
        if (shipment.getBox_delivery() == 1) {
            fukutsuData[22] = "不在時宅配ボックス";
        }
        // 配達指定日
        Date delivery_date = shipment.getDelivery_date();
        String dateDelivery = "";
        if (!StringTools.isNullOrEmpty(delivery_date)) {
            dateDelivery = simpleDateFormat.format(delivery_date);
        }
        fukutsuData[23] = dateDelivery;
        // お客様管理番号
        fukutsuData[25] = shipment.getShipment_plan_id();
        // 元払区分
        fukutsuData[26] = "1";
        // 出荷日付
        Date shipping_date = shipment.getShipping_date();
        String SDate = "";
        if (!StringTools.isNullOrEmpty(shipping_date)) {
            SDate = simpleDateFormat.format(shipping_date);
        } else {
            Date date = new Date();
            SDate = simpleDateFormat.format(date);
        }
        fukutsuData[28] = SDate;

        csvData.add(fukutsuData);
        CsvUtils.write(fileName, fukutsuHeader, csvData, false);
    }

    /**
     * @param shipment
     * @param rule
     * @param fileName
     * @param delivery_method_csv
     * @description: 西濃運輸csv生成
     * @return: void
     * @date: 2021/7/23 18:19
     */
    private void seinoCsv(Tw200_shipment shipment, String rule, String fileName, String delivery_method_csv) {
        List<String[]> csvData = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String seinoHeader =
            "荷送人コード,西濃発店コード,出荷予定日,お問合せ番号,管理番号,元着区分,原票区分,個数,重量区分,重量（kg),重量（才）,荷送人名称,荷送人住所１,荷送人住所２,荷送人電話番号,部署コード,部署名,重量契約区分,お届け先郵便番号,お届け先名称１,お届け先名称２,お届け先住所１,お届け先住所２,お届け先電話番号,お届け先コード,お届け先JIS市町村コード,着店コード付け区分,着地コード,着店コード,保険金額,輸送指示１,輸送指示２,記事１,記事２,記事３,記事４,記事５,輸送指示（配達指定日付）,輸送指示コード１,輸送指示コード２,輸送指示（止め店所名）,予備,品代金,消費税等";

        String[] seinoData = new String[seinoHeader.split(rule, -1).length];
        // 荷送人コード
        seinoData[0] = shipment.getSeino_delivery_code();

        Timestamp shipmentPlanDate = shipment.getShipment_plan_date();
        if (!StringTools.isNullOrEmpty(shipmentPlanDate)) {
            // 出荷予定日
            seinoData[2] = simpleDateFormat.format(shipmentPlanDate);
        }
        // 管理番号
        seinoData[4] = shipment.getShipment_plan_id();
        // 元着区分
        seinoData[5] = "2".equals(shipment.getPayment_id()) ? "3" : "1";
        // 原票区分
        seinoData[6] = delivery_method_csv;
        // 個口数
        seinoData[7] = (StringTools.isNullOrEmpty(shipment.getBoxes()) ? "1" : shipment.getBoxes()) + "";

        Ms012_sponsor_master master = shipment.getMs012_sponsor_master();
        // 荷送人名称
        seinoData[11] = master.getName();

        String masterAdd = master.getPrefecture() + master.getAddress1();
        String sponsorAddress2 = master.getAddress2();
        if (!StringTools.isNullOrEmpty(sponsorAddress2)) {
            masterAdd += sponsorAddress2;
        }

        HashMap<String, String> masterMap = CommonUtils.sagawaToAdderss(masterAdd, 20);
        // 荷送人住所１
        seinoData[12] = masterMap.get("add1");
        // 荷送人住所２
        seinoData[13] = masterMap.get("add2") + masterMap.get("add3");
        // 荷送人電話番号
        seinoData[14] = master.getPhone();
        // お届け先郵便番号
        seinoData[18] = shipment.getPostcode();
        // お届け先名称１
        // 名前
        String surname1 = "", surname2 = "";
        // 会社名
        String company = shipment.getCompany();
        // 名前
        String surname = shipment.getSurname();
        // 部署
        String division = shipment.getDivision();
        // 部署不为空时拼接
        if (!StringTools.isNullOrEmpty(division)) {
            surname = division + " " + surname;
        }
        if (!StringTools.isNullOrEmpty(shipment.getCompany())) {
            surname1 = company;
            surname2 = surname;
        } else {
            surname1 = surname;
            surname2 = "";
        }
        seinoData[19] = surname1;
        seinoData[20] = surname2;

        String address = shipment.getPrefecture() + shipment.getAddress1();
        if (!StringTools.isNullOrEmpty(shipment.getAddress2())) {
            address += shipment.getAddress2();
        }
        if (!StringTools.isNullOrEmpty(shipment.getAddress3())) {
            address += shipment.getAddress3();
        }
        HashMap<String, String> addressMap = CommonUtils.sagawaToAdderss(address, 30);
        // お届け先住所１
        seinoData[21] = addressMap.get("add1");
        // お届け先住所２
        seinoData[22] = addressMap.get("add2") + addressMap.get("add3");
        // お届け先電話番号
        seinoData[23] = shipment.getPhone();
        // 記事２
        seinoData[33] = shipment.getLabel_note();
        // 記事３
        seinoData[34] = shipment.getShipment_plan_id();
        // 記事5 送り状特記事項
        if (!StringTools.isNullOrEmpty(shipment.getInvoice_special_notes())) {
            if (shipment.getInvoice_special_notes().length() > 15) {
                seinoData[35] = shipment.getInvoice_special_notes().substring(0, 15);
                seinoData[36] = shipment.getInvoice_special_notes().substring(15, 16);
            } else {
                seinoData[35] = shipment.getInvoice_special_notes();
            }
        }
        String delivery_instructions = "0000";
        Timestamp delivery_date = shipment.getDelivery_date();
        if (!StringTools.isNullOrEmpty(delivery_date)) {
            LocalDate localDate = delivery_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int monthValue = localDate.getMonthValue();
            int dayOfMonth = localDate.getDayOfMonth();
            String mounth = monthValue < 10 ? "0" + monthValue : monthValue + "";
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
            delivery_instructions = mounth + day;
        }
        String delivery_time = "0";
        String delivery_time_slot = shipment.getDelivery_time_slot();
        if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
            Ms006_delivery_time deliveryTime = deliveryDao.getDeliveryTimeById(Integer.valueOf(delivery_time_slot));
            delivery_time = deliveryTime.getDelivery_time_csv();
        }
        delivery_instructions += delivery_time;
        // 輸送指示（配達指定日付）
        seinoData[37] = delivery_instructions;
        // 輸送指示コード１
        seinoData[38] = "00000".equals(delivery_instructions) ? "02" : "";
        // 輸送指示コード２
        seinoData[39] = shipment.getFragile_item() == 1 ? "04" : "";
        // 品代金
        seinoData[42] = shipment.getTotal_for_cash_on_delivery() + "";
        // 消費税等
        seinoData[43] = "0";

        csvData.add(seinoData);
        CsvUtils.write(fileName, seinoHeader, csvData, false);
    }

    public String spaceTrim(String str) {
        if (!StringTools.isNullOrEmpty(str)) {
            str = str.replaceAll("\\s*", "").replaceAll("－", "-");
        }
        return str;
    }

    public String specialSymbol(String str) {
        if (!StringTools.isNullOrEmpty(str)) {
            str = str.replaceAll("\"", "");
            StringBuilder sb = new StringBuilder(str);
            if (sb.indexOf("'") == 0) {
                sb.replace(0, 1, "");
            }
            str = sb.toString();
        }
        return str;
    }

    /**
     * @param shipmentNumber 再发行ID
     * @return JSONObject
     * @description 再发行PDF获取
     * @date 2021/7/20
     **/
    @Override
    public JSONObject bizLogiRetryPdf(String shipmentNumber) {

        JSONObject resBean = new JSONObject();
        String retryPrintRequestId = null;
        resBean.put("code", 0);

        RetryPrintResponse retryPrintResponse = this.getRetryPrintRequestId(shipmentNumber);
        if (!BizLogiResEnum.S0_0001.getCode().equals(retryPrintResponse.getResultCode())) {
            resBean.put("msg", "問合番号が該当しない");
            return resBean;
        }
        retryPrintRequestId = retryPrintResponse.getPrintRequestId();

        // 线程sleep睡眠3m等待BizLogi图片生成后请求PDF URL
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ファイル存在確認 reqeust
        CheckFileResponse checkfileRes = this.fileExistCheck(retryPrintRequestId);

        if (!BizLogiResEnum.S0_0001.getCode().equals(checkfileRes.getResultCode())) {
            resBean.put("msg", "ファイル存在確認ERROR");
            return resBean;
        }

        List<CheckFileResponse.PrintDataDetail> printDataList = checkfileRes.getPrintDataList();

        String bizPdfUrl = "";
        if (null != printDataList && printDataList.size() > 0) {
            bizPdfUrl = printDataList.get(0).getUrl();
        }

        String sunlogiPdfUrl = remoteBizPdfDowload(bizPdfUrl, bizPdfUrl.substring(bizPdfUrl.lastIndexOf("/") + 1));

        resBean.put("msg", "");
        resBean.put("url", sunlogiPdfUrl);
        return resBean;
    }

    @Override
    public JSONObject bizLogiData(List<Tw200_shipment> list, List<String> ids) {
        JSONObject resBean = new JSONObject();

        String printRequestId = null;
        String shippingNumber = "";

        // 確認機能 request
        ShippingResponse confirmFunctionRes = this.confirmInvoice(list, Constants.BIZ_CONFIRM_FUNC_FLG, null);

        // 判断返回值
        if (!BizLogiResEnum.S0_0001.getCode().equals(confirmFunctionRes.getResultCode())) {
            resBean.put("msg", "確認機能ERROR");
            return resBean;
        }

        // 発行依頼機能 request
        ShippingResponse issuanceRes = this.confirmInvoice(list, Constants.BIZ_ISSUANCE_FLG, null);

        // 判断返回值
        if (!BizLogiResEnum.S0_0001.getCode().equals(issuanceRes.getResultCode())) {
            resBean.put("msg", "発行依頼機能ERROR");
            return resBean;
        }
        printRequestId = issuanceRes.getPrintRequestId();

        // 更新伝票番号
        try {
            shippingNumber = issuanceRes.getPrintDataList().get(0).getShippingNumberList().get(0);
            shipmentDao.updateDeliveryTrackingNm(ids, shippingNumber);
        } catch (Exception e) {
            logger.info("更新伝票番号ERROR");
        }

        DataOutputResponse bizDataRes = this.getBizData(printRequestId);

        return (JSONObject) JSONObject.toJSON(bizDataRes);
    }

    /**
     * @param list 出库信息
     * @param ids 出库ID
     * @return JSONObject
     * @description 根据出库信息获取传票PDF
     * @date 2021/7/20
     **/
    @Override
    public JSONObject bizLogiPdf(List<Tw200_shipment> list, List<String> ids, String boxes) {

        JSONObject resBean = new JSONObject();

        String printRequestId = null;
        String shippingNumber = "";

        // 確認機能 request
        ShippingResponse confirmFunctionRes = this.confirmInvoice(list, Constants.BIZ_CONFIRM_FUNC_FLG, boxes);

        // 判断返回值
        if (!BizLogiResEnum.S0_0001.getCode().equals(confirmFunctionRes.getResultCode())) {
            resBean.put("msg", "確認機能ERROR");
            return resBean;
        }

        // 発行依頼機能 request
        ShippingResponse issuanceRes = this.confirmInvoice(list, Constants.BIZ_ISSUANCE_FLG, boxes);

        // 判断返回值
        if (!BizLogiResEnum.S0_0001.getCode().equals(issuanceRes.getResultCode())) {
            resBean.put("msg", "発行依頼機能ERROR");
            return resBean;
        }
        printRequestId = issuanceRes.getPrintRequestId();

        // 更新伝票番号
        try {
            shippingNumber = issuanceRes.getPrintDataList().get(0).getShippingNumberList().get(0);
            shipmentDao.updateDeliveryTrackingNm(ids, shippingNumber);
        } catch (Exception e) {
            logger.info("更新伝票番号ERROR");
        }

        // 线程sleep睡眠3m等待BizLogi图片生成后请求PDF URL
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ファイル存在確認 reqeust
        CheckFileResponse checkfileRes = this.fileExistCheck(printRequestId);

        if (!BizLogiResEnum.S0_0001.getCode().equals(checkfileRes.getResultCode())) {
            resBean.put("msg", "ファイル存在確認ERROR");
            return resBean;
        }

        List<CheckFileResponse.PrintDataDetail> printDataList = checkfileRes.getPrintDataList();

        String bizPdfUrl = "";
        if (null != printDataList && printDataList.size() > 0) {
            bizPdfUrl = printDataList.get(0).getUrl();
        }

        String sunlogiPdfUrl = remoteBizPdfDowload(bizPdfUrl, shippingNumber);

        resBean.put("msg", "");
        resBean.put("url", sunlogiPdfUrl);
        return resBean;
    }

    /**
     * @param url 远程bizlogi pdf
     * @return String
     * @description 将远程bizlogi PDF下载到sunlogi服务器
     * @date 2021/7/20
     **/
    private String remoteBizPdfDowload(String url, String shippingNumber) {

        String relativePath = pathProps.getTemporary()
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + shippingNumber + ".pdf";
        String pdfPath = pathProps.getRoot() + relativePath;

        try {
            HttpUtils.download(url, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("url[" + url + "}下载失败，msg: " + e.getMessage());
        }
        return relativePath;
    }

    /**
     * @param list 待开票的数据
     * @param printOutFlg 送り状発行依頼フラグ
     *        0 - (確認機能) エラー精査のみ行います
     *        1 - (発行依頼機能) エラー精査後、出荷情報が全て正常であれば送り状発行処理を行います
     * @description 確認機能 & 発行依頼機能
     * @return: ShippingResponse
     * @date 2021/6/7
     **/
    private ShippingResponse confirmInvoice(List<Tw200_shipment> list, String printOutFlg, String boxes) {
        String url = bizApiHost + Constants.BIZ_SHIPPING;
        ShippingRequest requestBean = initShippingRequest(list, printOutFlg, boxes);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, requestBean);
            logger.info("Shipping Request Param: " + requestValue);
            HashMap<String, String> params = Maps.newHashMap();
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("Shipping Response Body: " + responseValue);
            ShippingResponse responseBean =
                (ShippingResponse) XmlUtil.xml2Bean(builder, responseValue, ShippingResponse.class);

            return responseBean;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param requestId 発行受付ID
     * @return CheckFileResponse
     * @description ファイル存在確認 文件存在确认
     * @date 2021/6/7
     **/
    private CheckFileResponse fileExistCheck(String requestId) {
        String url = bizApiHost + Constants.BIZ_CHECKFILE;
        CheckFileRequest requestBean = initCheckFileRequest(requestId);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, requestBean);
            HashMap<String, String> params = Maps.newHashMap();
            logger.info("CheckFile Request Param:" + requestValue);
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("CheckFile Response Body:" + responseValue);
            CheckFileResponse response =
                (CheckFileResponse) XmlUtil.xml2Bean(builder, responseValue, CheckFileResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param shipmentNumber 待再发行的number
     * @description 获取再发行的request id
     * @return: RetryPrintResponse
     * @date 2021/7/20
     **/
    private RetryPrintResponse getRetryPrintRequestId(String shipmentNumber) {
        String url = bizApiHost + Constants.BIZ_RETRYPRINT;
        RetryPrintRequest requestBean = initRetryPrintRequest(shipmentNumber);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, requestBean);
            HashMap<String, String> params = Maps.newHashMap();
            logger.info("Retry Print Request Param: " + requestValue);
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("Retry Print Response Body: " + responseValue);
            RetryPrintResponse response =
                (RetryPrintResponse) XmlUtil.xml2Bean(builder, responseValue, RetryPrintResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param shipmentList 待开票的数据
     * @param printOutFlg 送り状発行依頼フラグ
     *        0 - (確認機能) エラー精査のみ行います
     *        1 - (発行依頼機能) エラー精査後、出荷情報が全て正常であれば送り状発行処理を行います
     * @description 初始化 送り状発行API Request Bean
     * @return: ShippingRequest
     * @date 2021/6/7
     **/
    private ShippingRequest initShippingRequest(List<Tw200_shipment> shipmentList, String printOutFlg,
        String newBoxes) {
        ShippingRequest request = new ShippingRequest();

        ShippingRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        // 配送会社コード: 佐川急便
        request.setDeliveryCode(BizLogiPredefinedEnum.DELIVERYCODE_0001.getCode());
        request.setPrintOutFlg(printOutFlg);
        if (null != printOutFlg && "1".equals(printOutFlg)) {
            // 佐川急便圧着サーマル送り状
            request.setOkuriCode(BizLogiPredefinedEnum.OKURICODE_L02.getCode());
        }
        // 执行错误审查和警告审查
        request.setOutputLevel(BizLogiPredefinedEnum.OUTPUTLEVEL_000.getCode());
        // 帳票の背景画像をPDFに表示させるかのフラグ 背景表示
        request.setBackLayerFlg(Constants.BIZ_BACK_LAYER_FLG);

        List<ShippingRequest.PrintDataDetail> printDataList = new ArrayList<>();

        Optional.ofNullable(shipmentList).ifPresent(
            shipments -> {
                shipments.stream().filter(java.util.Objects::nonNull).forEach(
                    shipment -> {
                        ShippingRequest.PrintDataDetail printDataDetail = request.new PrintDataDetail();
                        // 个口数(为空时默认给定1)
                        Integer boxes = Optional.ofNullable(shipment).map(Tw200_shipment::getBoxes).orElse(1);
                        if (!Strings.isNullOrEmpty(newBoxes)) {
                            boxes = Integer.valueOf(newBoxes);
                        }
                        boxes = (boxes == 0) ? 1 : boxes;
                        printDataDetail.setHaisoKosu(boxes.toString());

                        // 管理番号(唯一) 使用 出庫依頼ID
                        printDataDetail.setUserManageNumber(shipment.getOrder_no());

                        // 顧客コード 配置文件
                        printDataDetail.setKokyakuCode(Constants.BIZ_CUSTOM_CODE);

                        printDataDetail.setOtodokeNm1(shipment.getSurname());

                        String addr1 = Optional.ofNullable(shipment.getAddress1()).orElse("");
                        addr1 = addr1.length() <= 25 ? addr1 : addr1.substring(0, 24);
                        printDataDetail.setOtodokeAdd1(addr1);
                        String addr2 = Optional.ofNullable(shipment.getAddress2()).orElse("");
                        addr2 = addr2.length() <= 25 ? addr2 : addr2.substring(0, 24);
                        printDataDetail.setOtodokeAdd2(addr2);
                        String tmpYubin = shipment.getPostcode();
                        if (tmpYubin != null) {
                            tmpYubin = tmpYubin.replace("-", "");
                        }
                        printDataDetail.setOtodokeYubin(tmpYubin);

                        printDataDetail.setOtodokeTel(shipment.getPhone());
                        printDataDetail.setOtodokeMailAddress(shipment.getEmail());

                        // 打印发货地址
                        printDataDetail.setIraiPrintFlg(Constants.BIZ_SPONSOR_NO_CUSTOM_ADDRESS_FLG);

                        // 运送形式(陆运、空运)
                        printDataDetail.setBinsyuCode(BizLogiPredefinedEnum.BINSYUCODE_000.getCode());

                        // 支付方式为代金, 开代金引換の送り状を発行
                        if (null != shipment.getPayment_id() && shipment.getPayment_id().equals("2")) {
                            printDataDetail.setDaibikiFlg(Constants.BIZ_CASH_INVOICE);
                            printDataDetail.setMotoChakuCode(BizLogiPredefinedEnum.MOTOCHAKUCODE_1.getCode());
                        } else {
                            // 开普通发票
                            printDataDetail.setDaibikiFlg(Constants.BIZ_NORMAL_INVOICE);
                        }

                        printDataList.add(printDataDetail);
                    });
            });

        request.setPrintDataList(printDataList);

        return request;
    }

    /**
     * @param requestId 発行受付ID
     * @return CheckFileRequest
     * @description 初始化 ファイル存在確認API Request Bean
     * @date 2021/6/7
     **/
    private CheckFileRequest initCheckFileRequest(String requestId) {
        CheckFileRequest request = new CheckFileRequest();

        CheckFileRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        List<String> printRequestIdList = Lists.newArrayList();
        printRequestIdList.add(requestId);
        request.setPrintRequestIdList(printRequestIdList);

        return request;
    }

    /**
     * @param shipmentNumber 待发行number
     * @return RetryPrintRequest
     * @description 初始化 送り状再発行依頼 Request Bean
     * @date 2021/7/20
     **/
    private RetryPrintRequest initRetryPrintRequest(String shipmentNumber) {
        RetryPrintRequest request = new RetryPrintRequest();

        RetryPrintRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        List<String> shipmentNumberList = Lists.newArrayList();
        shipmentNumberList.add(shipmentNumber);
        request.setPrintRequestIdList(shipmentNumberList);

        return request;
    }

    private DataOutputResponse getBizData(String printRequestId) {
        String url = bizApiHost + Constants.BIZ_DATAOUTPUT;

        DataOutputRequest requestBean = initDataOutputRequest(printRequestId);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, requestBean);
            HashMap<String, String> params = Maps.newHashMap();
            logger.info("DataOutput Request Param:" + requestValue);
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("DataOutput Response Body:" + responseValue);
            DataOutputResponse response =
                (DataOutputResponse) XmlUtil.xml2Bean(builder, responseValue, DataOutputResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DataOutputRequest initDataOutputRequest(String printRequestId) {
        DataOutputRequest request = new DataOutputRequest();

        DataOutputRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        List<String> printRequestIdList = Lists.newArrayList();
        printRequestIdList.add(printRequestId);
        request.setPrintRequestIdList(printRequestIdList);
        return request;
    }
}
