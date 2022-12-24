package com.lemonico.ntm.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.ntm.dao.NtmShipmentDao;
import com.lemonico.ntm.dao.ReportDao;
import com.lemonico.ntm.service.ReportService;
import com.lemonico.store.dao.*;
import com.lemonico.wms.dao.ShipmentResultDao;
import com.lemonico.wms.dao.ShipmentResultDetailDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.service.ShipmentService;
import com.lemonico.wms.service.impl.StocksResultServiceImpl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService
{

    private final static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Resource
    private ReportDao reportDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private OrderApiDao orderApiDao;

    @Resource
    private CommonFunctionDao commonFunctionDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private NtmShipmentDao ntmShipmentDao;

    @Resource
    private ShipmentDetailDao shipmentDetailDao;

    @Resource
    private StockDao stockDao;

    @Resource
    private ShipmentResultDao shipmentResultDao;

    @Resource
    private StocksResultDao stocksResultDao;

    @Resource
    private ShipmentService shipmentService;

    @Resource
    private ShipmentsDao shipmentsDao;

    @Resource
    private ShipmentResultDetailDao shipmentResultDetailDao;

    // ntm封筒商品コード
    static final String productCode = "9993";

    /**
     * @Description: 法人出荷相关接口
     *               @Date： 2021/4/14
     *               @Param： client_id
     *               @Param： search
     *               @Param： start
     *               @Param： end
     *               @Param： form 配送形态 1：法人 2：个人
     *               @return： JSONObject
     */
    @Override
    public List<Tw200_shipment> monthDeliveryDetails(HttpServletRequest request, String warehouse_cd, String search,
        String start, String end, String form, String type, String transform) {
        // 解析token 获取client_id
        String client_id = CommonUtils.getToken("client_id", request);
        Date startTime = DateUtils.stringToDate(start);
        // 获取ntm绑定eccube店铺识别子
        Tc203_order_client tc203 = orderApiDao.getOrderClientInfo(client_id, Constants.ECCUBE, null);
        String identification = "";
        if (!StringTools.isNullOrEmpty(tc203)) {
            identification = tc203.getIdentification();
        }
        // 出库状态 集合
        List<Integer> shipmentStatusList = Arrays.asList(8);
        if (!StringTools.isNullOrEmpty(transform)) {
            shipmentStatusList = Arrays.asList(4, 5, 7, 41, 42);
        }
        List<Tw200_shipment> shipmentsList = reportDao.getNtmLegalShipmentList(client_id, warehouse_cd, search,
            startTime, CommonUtils.getDateEnd(end), Integer.parseInt(form),
            Strings.isNullOrEmpty(type) ? null : Integer.parseInt(type), identification, shipmentStatusList);
        if (shipmentsList.isEmpty()) {
            return null;
        }

        // 查询配送会社
        List<Ms004_delivery> deliveryList = deliveryDao.getDeliveryInfo(null);
        // 查询配送希望时间带
        List<Ms006_delivery_time> deliveryTimeList = deliveryDao.getDeliveryTime(null, null, null, null);
        // 获取出荷サイズ
        List<Ms010_product_size> sizeList = ntmShipmentDao.getAllSizeName();
        // 装填配送会社名
        for (int i = 0; i < shipmentsList.size(); i++) {
            Tw200_shipment shipments = shipmentsList.get(i);
            String deliveryCarrier = shipments.getDelivery_carrier();

            // 查询配送希望时间带
            if (!StringTools.isNullOrEmpty(shipments.getDelivery_time_slot())) {
                for (int j = 0; j < deliveryTimeList.size(); j++) {
                    Ms006_delivery_time deliveryTime = deliveryTimeList.get(j);
                    String time_id = deliveryTime.getDelivery_time_id().toString();
                    if (time_id == shipments.getDelivery_time_slot()
                        || time_id.equals(shipments.getDelivery_time_slot())) {
                        shipments.setDelivery_time_name(deliveryTime.getDelivery_time_name());
                        continue;
                    }
                }
            }

            // 获取出荷サイズ名
            if (!StringTools.isNullOrEmpty(shipments.getSize_cd())) {
                for (int j = 0; j < sizeList.size(); j++) {
                    Ms010_product_size size = sizeList.get(j);
                    if (size.getSize_cd() == shipments.getSize_cd()
                        || shipments.getSize_cd().equals(size.getSize_cd())) {
                        shipments.setSize_cd(size.getName());
                        continue;
                    }
                }
            }

            // 查询配送会社
            if (!StringTools.isNullOrEmpty(deliveryCarrier)) {
                for (int j = 0; j < deliveryList.size(); j++) {
                    Ms004_delivery delivery = deliveryList.get(j);
                    if (deliveryCarrier == delivery.getDelivery_cd()
                        || delivery.getDelivery_cd().equals(deliveryCarrier)) {
                        shipments.setDelivery_nm(delivery.getDelivery_nm());
                        shipments.setDelivery_method(delivery.getDelivery_method_name());
                        continue;
                    }
                }
            }

        }
        return shipmentsList;
    }

    /**
     * @param jsonObject
     * @Description: 出荷确定处理
     *               @Date： 2021/4/14
     *               @Param： JSONObject
     *               @return： Integer
     */
    @Override
    public Integer updateDeliveryHandle(JSONObject jsonObject, HttpServletRequest request) {
        Date upd_date = DateUtils.getDate();

        // 获取参数中选择处理的出库ID
        JSONArray checks = jsonObject.getJSONArray("check");
        // 转换成List
        ArrayList<HashMap<String, Object>> params = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++) {
            JSONObject obj = checks.getJSONObject(i);
            HashMap<String, Object> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            params.add(map);
        }
        Integer integer = reportDao.updateDeliveryHandle(params, upd_date);

        List<String> list = params.stream().map(v -> (String) v.get("shipment_plan_id")).collect(Collectors.toList());

        // 将状态改为出荷済み
        Integer shipment_status = 8;
        String warehouse_cd = (String) params.stream().findFirst().get().get("warehouse_cd");

        Date date = DateUtils.getDate();
        String login_nm = CommonUtils.getToken("login_nm", request);
        // タイプ 为 出库
        Integer type = 2;
        list.forEach(shipmentId -> {
            List<Tw201_shipment_detail> shipmentDetailList =
                shipmentDetailDao.getShipmentDetailById(warehouse_cd, shipmentId, null, false);

            shipmentDetailList.stream().forEach(item -> {
                JSONObject stockJsonParam = new JSONObject();
                stockJsonParam.put("client_id", item.getClient_id());
                stockJsonParam.put("product_id", item.getProduct_id());
                stockJsonParam.put("warehouse_cd", warehouse_cd);
                Tw300_stock stockInfo;
                try {
                    stockInfo = stockDao.getStockInfoById(stockJsonParam);
                } catch (Exception e) {
                    logger.error("查询在库数据失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }

                // 获取到该商品在哪个货架上面出库数为多少
                List<Tw212_shipment_location_detail> locationDetail;
                try {
                    List<String> shipmentIds = new ArrayList<>();
                    shipmentIds.add(shipmentId);
                    locationDetail =
                        shipmentResultDao.getShipmentLocationDetail(warehouse_cd, shipmentIds, item.getProduct_id());
                } catch (Exception e) {
                    logger.error("查询出庫作業ロケ明細失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }

                // 在库表 需要减掉的不可配送默认为0
                int tw300NotDelivery = 0;
                for (Tw212_shipment_location_detail location : locationDetail) {
                    if (location.getStatus() == 1) {
                        continue;
                    }
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

                    // 获取到货架信息
                    Mw404_location mw404Location = stocksResultDao.getLocationName(warehouse_cd, location_id);
                    // false 货架正常 true 货架过期或者出荷不可
                    boolean notAvailable = StocksResultServiceImpl.getChangeFlg(mw404Location.getStatus(),
                        mw404Location.getBestbefore_date());

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
                    shipmentResultDao.updateLocationStatus(warehouse_cd, shipmentId, location.getId());
                }

                // 実在庫数
                int inventory_cnt = stockInfo.getInventory_cnt();
                // 出庫依頼中数
                int requesting_cnt = item.getProduct_plan_cnt();
                // 在库数小于出库依赖中数
                if (inventory_cnt < requesting_cnt) {
                    throw new BaseException(ErrorCode.INSUFFICIENT_STOCK);
                }
                // 实际在库数 - 该商品出库依赖数 = 现在在库数
                int inventoryNum = inventory_cnt - requesting_cnt;
                // 出庫依頼中数 = 原有的出库依赖数 - 该商品本次出库依赖的数
                int requestingNum = stockInfo.getRequesting_cnt() - requesting_cnt;
                // 不可配送 = 原有的不可配送数 - 本次出库的不可配送数
                int notDelivery = stockInfo.getNot_delivery() - tw300NotDelivery;

                // 理論在庫数
                int availableNum = inventoryNum - requestingNum - notDelivery;
                try {
                    stockDao.updateStockNum(warehouse_cd, item.getClient_id(),
                        item.getProduct_id(), Math.max(inventoryNum, 0), requestingNum, availableNum, notDelivery, date,
                        login_nm);
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
                stockDao.insertStockHistory(shipmentId, item.getClient_id(), item.getProduct_id(),
                    stockHistoryId, type, item.getProduct_plan_cnt(), login_nm, date, info);
            });


            String[] shipmentIds = {
                shipmentId
            };
            try {
                shipmentService.setShipmentStatus(request, warehouse_cd, shipment_status,
                    shipmentIds, null, null, null, false, shipment_status.toString(), null);
            } catch (Exception e) {
                logger.error("修改出庫ステータス失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INSUFFICIENT_STOCK, e.getMessage());
            }

            // 查询出庫作業明細 获取作業管理ID
            Integer workId = shipmentResultDetailDao.getWorkId(warehouse_cd, shipmentId);

            // 获取到作业Id 对应的所有出库依赖Id
            List<String> shipmentIdList =
                shipmentResultDetailDao.getShipmentIdListByWorkId(Collections.singletonList(workId));

            if (!shipmentIdList.isEmpty()) {
                // 根据出库依赖Id 得到出库状态不为8 的count
                Integer statusCount = shipmentsDao.getShipmentStatusByIdList(shipmentIdList);
                if (statusCount == 0) {
                    // 根据workId 改修 処理ステータス
                    try {
                        shipmentResultDao.updateShipmentResultStatusByWorkId(workId, warehouse_cd);
                    } catch (Exception e) {
                        logger.error("根据workId 改修出库状态失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                }
            }
        });

        return integer;
    }

    /**
     * @description 获取有效在库数
     * @param clientId 店铺id
     * @return: String
     * @date 2021/4/25
     **/
    @Override
    public String getStockAvailableCnt(String clientId) {
        Mc100_product product = productDao.getProductInfoByCode(productCode, clientId);
        String productId = product.getProduct_id();
        String warehouseCd = getWarehouseCdByClientId(clientId);
        return reportDao.getAvailableCntByCondition(clientId, productId, warehouseCd);
    }

    /**
     * @description 更新仓库表
     * @param cnt 商品数量
     * @param clientId 店铺id
     * @return: boolean
     * @date 2021/4/25
     **/
    @Override
    public boolean updateStock(String cnt, String clientId) {
        Mc100_product product = productDao.getProductInfoByCode(productCode, clientId);
        String productId = product.getProduct_id();
        String warehouseCd = getWarehouseCdByClientId(clientId);
        try {
            reportDao.updateStockAvailableCntAndRequestingCnt(cnt, clientId, productId, warehouseCd);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @description 根据日期获取封筒数据
     * @param date 日期
     * @param deliveryCarrier 配送方式
     * @param clientId 店铺id
     * @return: List
     * @date 2021/4/25
     **/
    @Override
    public List<JSONObject> getSealDataByDate(String date, String deliveryCarrier, String clientId) {
        String warehouseCd = getWarehouseCdByClientId(clientId);
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 按 delivery_date 日期取数据
        List<Tw200_shipment> shipments = reportDao.getBoxesBySealDate(clientId, warehouseCd, localDate.toString(),
            localDate.plusDays(1).toString(), deliveryCarrier);

        List<JSONObject> resList = new ArrayList<>();
        JSONObject bean = new JSONObject();
        shipments.stream().forEach(x -> {
            bean.put("delivery_date", x.getDelivery_date());
            bean.put("delivery_carrier", x.getDelivery_carrier());
            bean.put("boxes", x.getBoxes());
            resList.add(bean);
        });

        return resList;
    }

    /**
     * @description: 根据client_id获取仓库warehouse_cd
     * @param clientId 店舗ID
     * @return: String
     * @date: 2021/4/25
     **/
    private String getWarehouseCdByClientId(String clientId) {
        String warehouseCd = commonFunctionDao.getWarehouseIdByClientId(clientId);
        if (StringTools.isNullOrEmpty(warehouseCd)) {
            return null;
        }
        return warehouseCd;
    }

}
