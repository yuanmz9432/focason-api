package com.lemonico.wms.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.service.CustomerHistoryService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.WarehousingService;
import com.lemonico.wms.bean.WarehousingDetailBean;
import com.lemonico.wms.dao.ProductResultDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.dao.WarehouseCustomerDao;
import com.lemonico.wms.dao.WarehousingResultDao;
import com.lemonico.wms.service.StocksResultService;
import com.lemonico.wms.service.WarehousingResultService;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 倉庫側入荷依頼管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class WarehousingResultServiceImpl implements WarehousingResultService
{

    private final static Logger logger = LoggerFactory.getLogger(WarehousingResultServiceImpl.class);

    private final WarehousingResultServiceImpl warehousingResultService;
    private final WarehousingResultDao warehousingResultDao;
    private final WarehousingsDao warehousingsDao;
    private final ProductResultDao productResultDao;
    private final StockDao stockDao;
    private final WarehousingsDetailDao warehousingsDetailDao;
    private final ShipmentDetailDao shipmentDetailDao;
    private final ProductDao productDao;
    private final WarehouseCustomerDao warehouseCustomerDao;
    private final StocksResultDao stocksResultDao;
    private final StocksResultService stocksResultService;
    private final CustomerHistoryService customerHistoryService;
    private final PathProps pathProps;

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询未入库检品的商品
     * @return: JSONObject
     * @date: 2021/07/05
     */
    @Override
    public JSONObject getWarehouseInfoUncheck(JSONObject jsonObject) {
        List<Tc100_warehousing_plan> warehouseInfoList = null;
        List<WarehousingDetailBean> detailBeanList = new ArrayList<>();

        // 1: 未检品，2：检品中，3：一时保存，4：入库完了;
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String client_id = jsonObject.getString("client_id");
        String id = jsonObject.getString("id");
        // 查询所有商品size
        List<Ms010_product_size> sizeList = productResultDao.getSizeNameList();

        try {
            // 根据入库状态查询数据
            warehouseInfoList = warehousingsDao.getWarehouseInfoById(warehouse_cd, client_id, id);
        } catch (Exception e) {
            logger.error(ErrorCode.DATABASE_ERROR.getDetail());
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }

        // 查询入库实际的详细信息
        List<Tw110_warehousing_result> warehousingResults =
            warehousingResultDao.getWarehousingInfoById(warehouse_cd, client_id, id);
        // 商品实际数的map key：入库依赖ID + "_" + 商品ID value：入库实际明细对象
        Map<String, Tw111_warehousing_result_detail> productCntMap = new HashMap<>();
        // 货架map key：货架ID value：货架对象
        Map<String, Mw404_location> locationMap = new HashMap<>();
        if (warehousingResults.size() != 0) {
            // 获取到入库依赖实际明细
            List<Tw111_warehousing_result_detail> tw111_warehousing_result_details =
                warehousingResults.get(0).getTw111_warehousing_result_details();

            // 从实际明细中 取到每个商品对应的信息
            productCntMap = tw111_warehousing_result_details.stream().distinct()
                .collect(Collectors.toMap(x -> x.getWarehousing_plan_id() + "_" + x.getProduct_id(), o -> o));

            // 获取到入库实际货架 所包含的所有 货架ID
            List<Tw113_warehousing_location_detail> warehousingLocationDetails = tw111_warehousing_result_details
                .stream().map(Tw111_warehousing_result_detail::getLocation_detail).collect(Collectors.toList());

            List<String> locationIdList = warehousingLocationDetails.stream()
                .map(Tw113_warehousing_location_detail::getLocation_id).collect(Collectors.toList());

            // 根据货架ID 查询到货架信息
            List<Mw404_location> locationInfoByIdList = stockDao.getLocationInfoByIdList(locationIdList, warehouse_cd);

            // 转为map key 货架ID value 货架对象
            locationMap = locationInfoByIdList.stream().distinct()
                .collect(Collectors.toMap(Mw404_location::getLocation_id, o -> o));
        }

        for (Tc100_warehousing_plan warehousingPlan : warehouseInfoList) {


            List<Tc101_warehousing_plan_detail> tc101_warehousing_plan_detail =
                warehousingPlan.getTc101_warehousing_plan_details();
            for (Tc101_warehousing_plan_detail planDetail : tc101_warehousing_plan_detail) {

                WarehousingDetailBean detailBean = new WarehousingDetailBean();
                detailBean.setWarehouse_cd(warehouse_cd);
                detailBean.setClient_id(client_id);
                detailBean.setWarehousing_result_id(id);
                detailBean.setRequest_date(warehousingPlan.getRequest_date());
                detailBean.setInspection_type(warehousingPlan.getInspection_type());
                detailBean.setArrival_date(warehousingPlan.getArrival_date());
                detailBean.setProduct_id(planDetail.getProduct_id());
                detailBean.setName(planDetail.getMc100_productList().getName());
                detailBean.setCode(planDetail.getMc100_productList().getCode());
                detailBean.setBarcode(planDetail.getMc100_productList().getBarcode());
                detailBean.setQuantity(planDetail.getQuantity());
                detailBean.setMc102_product_imgList(planDetail.getMc100_productList().getMc102_product_imgList());
                detailBean.setDetail_status(planDetail.getStatus());
                Double weight = planDetail.getMc100_productList().getWeight();
                detailBean.setWeight(weight);
                String product_id = planDetail.getMc100_productList().getProduct_id();
                String size_cd = planDetail.getMc100_productList().getSize_cd();
                Date bestbeforeDate = planDetail.getBestbefore_date();
                detailBean.setLot_no(planDetail.getLot_no());
                detailBean.setBestbefore_date(bestbeforeDate);
                if (size_cd != null && size_cd.length() != 0) {
                    for (Ms010_product_size sizeInfo : sizeList) {
                        if (size_cd.equals(sizeInfo.getSize_cd())) {
                            detailBean.setSizeName(sizeInfo.getName());
                            break;
                        }
                    }
                }
                // 证明改入库依赖 已经有部分入库
                if (warehousingResults.size() != 0) {
                    String key = id + "_" + planDetail.getProduct_id();
                    if (!productCntMap.isEmpty() && productCntMap.containsKey(key)) {
                        Tw111_warehousing_result_detail tw111_warehousing_result_detail = productCntMap.get(key);
                        detailBean.setProduct_total(tw111_warehousing_result_detail.getProduct_cnt());
                        Tw113_warehousing_location_detail location =
                            tw111_warehousing_result_detail.getLocation_detail();
                        if (!StringTools.isNullOrEmpty(location) && !locationMap.isEmpty()
                            && locationMap.containsKey(location.getLocation_id())) {
                            Mw404_location mw404Location = locationMap.get(location.getLocation_id());
                            detailBean.setWh_location_nm(mw404Location.getWh_location_nm());
                            detailBean.setLot_no(mw404Location.getLot_no());
                        }
                    }

                }

                List<Mw405_product_location> productLocationList =
                    productResultDao.getProductLocation(warehouse_cd, client_id, product_id);
                if (productLocationList.size() != 0) {
                    detailBean.setLocation_list(productLocationList);
                }

                detailBean.setShipping_flag(planDetail.getShipping_flag());
                detailBeanList.add(detailBean);
            }
        }

        return CommonUtils.success(detailBeanList);
    }

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询商品
     * @return: JSONObject
     * @date: 2020/06/17
     */
    @Override
    public JSONObject getWarehouseInfoById(JSONObject jsonObject) {
        List<WarehousingDetailBean> detailBeanList = new ArrayList<>();
        // 1: 未检品，2：检品中，3：一时保存，4：入库完了
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String client_id = jsonObject.getString("client_id");
        String id = jsonObject.getString("id");

        // 获取所有size
        List<Ms010_product_size> sizeList = productResultDao.getSizeNameList();

        List<Tw110_warehousing_result> warehousingResultList = new ArrayList<>();
        try {
            warehousingResultList = warehousingResultDao.getWarehousingInfoById(warehouse_cd, client_id, id);
        } catch (Exception e) {
            logger.error(ErrorCode.DATABASE_ERROR.getDetail());
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }

        List<Tc101_warehousing_plan_detail> warehousingDetailList =
            warehousingsDetailDao.getWarehousingDetailList(warehouse_cd, client_id, id);

        Map<String, List<Tc101_warehousing_plan_detail>> detailMap =
            warehousingDetailList.stream().collect(Collectors.groupingBy(Tc101_warehousing_plan_detail::getProduct_id));

        for (Tw110_warehousing_result warehousingPlan : warehousingResultList) {

            List<Tw111_warehousing_result_detail> resultDetailsList =
                warehousingPlan.getTw111_warehousing_result_details();
            for (Tw111_warehousing_result_detail planDetail : resultDetailsList) {
                WarehousingDetailBean detailBean = new WarehousingDetailBean();
                detailBean.setWarehouse_cd(warehouse_cd);
                detailBean.setClient_id(client_id);
                detailBean.setWarehousing_result_id(id);
                detailBean.setRequest_date(warehousingPlan.getRequest_date());
                detailBean.setInspection_type(warehousingPlan.getInspection_type());
                detailBean.setArrival_date(warehousingPlan.getWarehousing_plan_date());
                String productId = planDetail.getProduct_id();
                detailBean.setProduct_id(productId);
                detailBean.setName(planDetail.getMc100_products().getName());
                detailBean.setCode(planDetail.getMc100_products().getCode());
                detailBean.setBarcode(planDetail.getMc100_products().getBarcode());
                detailBean.setQuantity(planDetail.getProduct_plan_cnt());
                detailBean.setProduct_total(planDetail.getProduct_cnt());
                detailBean.setMc102_product_imgList(planDetail.getMc100_products().getMc102_product_imgList());
                detailBean.setLot_no(planDetail.getLocation_detail().getLot_no());
                Double weight = null;
                if (!StringTools.isNullOrEmpty(planDetail.getProduct_weight())) {
                    weight = Double.valueOf(planDetail.getProduct_weight());
                }
                detailBean.setWeight(weight);

                if (detailMap.containsKey(productId)) {
                    List<Tc101_warehousing_plan_detail> warehousingPlanDetails = detailMap.get(productId);
                    if (warehousingPlanDetails.size() != 0) {
                        Tc101_warehousing_plan_detail warehousingPlanDetail = warehousingPlanDetails.get(0);
                        detailBean.setDetail_status(warehousingPlanDetail.getStatus());
                        String lotNo = warehousingPlanDetail.getLot_no();
                        Date bestbefore_date = warehousingPlanDetail.getBestbefore_date();
                        Integer shippingFlag = warehousingPlanDetail.getShipping_flag();
                        detailBean.setLot_no(lotNo);
                        detailBean.setBestbefore_date(bestbefore_date);
                        detailBean.setShipping_flag(shippingFlag);
                    }
                }

                String size_cd = planDetail.getProduct_size_cd();
                if (!StringTools.isNullOrEmpty(size_cd)) {
                    for (Ms010_product_size sizeInfo : sizeList) {
                        if (size_cd.equals(sizeInfo.getSize_cd())) {
                            detailBean.setSizeName(sizeInfo.getName());
                            break;
                        }
                    }
                } else {
                    size_cd = planDetail.getMc100_products().getSize_cd();
                    if (!StringTools.isNullOrEmpty(size_cd)) {
                        for (Ms010_product_size sizeInfo : sizeList) {
                            if (size_cd.equals(sizeInfo.getSize_cd())) {
                                detailBean.setSizeName(sizeInfo.getName());
                                break;
                            }
                        }
                    }
                }

                String product_id = planDetail.getProduct_id();
                JSONObject locationJson = new JSONObject();
                locationJson.put("warehouse_cd", warehouse_cd);
                locationJson.put("client_id", client_id);
                locationJson.put("warehousing_plan_id", id);
                locationJson.put("location_id", planDetail.getLocation_detail().getLocation_id());
                List<Mw404_location> locationList = warehousingResultDao.getLocationNameById(locationJson);
                String locationNm = "";
                if (locationList.size() > 0) {
                    locationNm = locationList.get(0).getWh_location_nm();
                    detailBean.setLot_no(locationList.get(0).getLot_no());
                }
                detailBean.setWh_location_nm(locationNm);

                // else {
                // detailBean.setWh_location_nm(Constants.DEFAULT_LOCATION_NAME);
                // }

                List<Mw405_product_location> productLocationList =
                    productResultDao.getProductLocation(warehouse_cd, client_id, product_id);
                if (productLocationList.size() != 0) {
                    // 循环获取最大的在库数和相对应的货架名称
                    int stock_total = 0;
                    int number = 0;
                    for (int i = 0; i < productLocationList.size(); i++) {
                        Mw405_product_location mw405_product_location = productLocationList.get(i);
                        Integer stock_cnt = mw405_product_location.getStock_cnt();
                        number = (number >= stock_cnt) ? number : stock_cnt;
                        stock_total += stock_cnt;
                    }
                    detailBean.setLocation_list(productLocationList);
                }

                detailBeanList.add(detailBean);
            }
        }
        return CommonUtils.success(detailBeanList);
    }

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID, status : 入庫ステータス
     * @description: 根据入库依赖修改入库状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/17
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject updateStatusById(JSONObject jsonObject, HttpServletRequest servletRequest) {

        Integer status = jsonObject.getInteger("status");
        if (status != Constants.WAREHOUSE_STATUS_FINISHED) {
            Integer count = warehousingsDao.selectStatusCount(jsonObject.getString("client_id"),
                jsonObject.getString("id"), status);
            if (count > 0) {
                return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
            }
        }

        try {
            warehousingsDao.updateStatusById(jsonObject);
        } catch (Exception e) {
            logger.error("根据入库依赖修改入库状态失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        logger.info("修改入库状态成功：" + "client_id=" + jsonObject.getString("client_id")
            + "入库依赖Id=" + jsonObject.getString("id") +
            ",status=" + status);
        // 一时保存
        if (status == Constants.WAREHOUSE_STATUS_FINISHED) {

            List<Tw113_warehousing_location_detail> locationDetailList;
            try {
                locationDetailList = warehousingResultDao.getWarehousingLocationDetail(jsonObject.getString("id"),
                    jsonObject.getString("client_id"));
            } catch (Exception e) {
                logger.error("根据入库依赖Id 查询 入庫作業ロケ明細 信息失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            this.finishWarehouse(jsonObject, servletRequest);
        }
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @param: servletRequest
     * @description: 检品完了 （将数据存到入库实际表 入库实际明细表 入庫作業ロケ明細表 里面）
     * @return: void
     * @date: 2020/8/12
     */
    @Transactional(rollbackFor = BaseException.class)
    public void finishWarehouse(JSONObject jsonObject, HttpServletRequest servletRequest) {
        // 获取当前日期
        Date date = DateUtils.getDate();
        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", servletRequest);
        // 入库完了
        JSONArray items = jsonObject.getJSONArray("items");
        // 获取所有size
        List<Ms010_product_size> sizeList = productResultDao.getSizeNameList();
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String client_id = jsonObject.getString("client_id");
        String id = jsonObject.getString("id");

        // 获取商品依赖数 和 实际数
        int product_total = 0;
        int product_plan_total = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            String jsonProductTotal = itemsJSONObject.getString("product_total");
            String jsonQuantity = itemsJSONObject.getString("quantity");
            int quantity = Integer.parseInt(jsonQuantity);
            Integer productTotal = null;
            if (!StringTools.isNullOrEmpty(jsonProductTotal)) {
                productTotal = Integer.valueOf(jsonProductTotal);
                product_total += productTotal;
            }
            product_plan_total += quantity;
            String sizeName = itemsJSONObject.getString("sizeName");
            String size_cd = null;
            if (!StringTools.isNullOrEmpty(sizeName)) {
                // 如果sizeName为空，说明选择了比140 大的size，获取到值
                if (sizeName.length() == 0) {
                    sizeName = itemsJSONObject.getString("value3");
                }
                for (Ms010_product_size sizeInfo : sizeList) {
                    if (sizeName.equals(sizeInfo.getName())) {
                        size_cd = sizeInfo.getSize_cd();
                        break;
                    }
                }
            }
            String weightData = itemsJSONObject.getString("weight");
            Double weight = null;
            if (!StringTools.isNullOrEmpty(weightData)) {
                weight = Double.valueOf(weightData);
            }

            String product_id = itemsJSONObject.getString("product_id");
            // 找出该商品之前是否存在 实际明细信息
            List<Tw111_warehousing_result_detail> warehouseResultDetails =
                warehousingResultDao.getWarehouseResultDetails(warehouse_cd, client_id, id, product_id);

            if (warehouseResultDetails.size() == 0) {
                // 如果不存在 则新规一条数据
                jsonObject.put("product_id", product_id);
                jsonObject.put("product_size_cd", size_cd);
                jsonObject.put("weigth_unit", "g");
                jsonObject.put("del_flg", 0);
                // 将数据存到入库实际明细表
                try {
                    warehousingResultDao.insertWarehouseResultDetil(jsonObject, weight, productTotal, quantity, date,
                        loginNm);
                } catch (Exception e) {
                    logger.error("将数据存到入库实际明细表失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            } else {
                // 如果存在 则更改已经存在的入库实际明细表里面的实际入库数
                warehousingResultDao.updateResultDetails(warehouse_cd, client_id, id, product_id, productTotal, size_cd,
                    weight, loginNm, date);
            }

            // 将数据放到入庫作業ロケ明細表
            String wh_location_nm = itemsJSONObject.getString("wh_location_nm");
            String lot_no = itemsJSONObject.getString("lot_no");
            Date bestBeforeDate = itemsJSONObject.getDate("bestbefore_date");
            Integer shippingFlag = itemsJSONObject.getInteger("shipping_flag");
            shippingFlag = !StringTools.isNullOrEmpty(shippingFlag) ? shippingFlag : 0;

            if (!StringTools.isNullOrEmpty(wh_location_nm)) {

                // 更改入库明细的賞味期限/在庫保管期限 和 ロット番号
                warehousingsDetailDao.updateWarehouseDetailLotNo(id, client_id, warehouse_cd, product_id, lot_no,
                    bestBeforeDate, loginNm, date, shippingFlag);

                this.insertWarehousingLocationDetail(jsonObject, product_id, wh_location_nm, productTotal, loginNm,
                    lot_no,
                    bestBeforeDate, shippingFlag);
            }

            // 更改商品表的重量和size
            productResultDao.updateWeightSizeByProductId(product_id, weight, size_cd,
                jsonObject.getString("client_id"));
        }
        // 校验入库实际表之前是否存在该出库依赖ID
        List<Tw110_warehousing_result> warehousingInfoById =
            warehousingResultDao.getWarehousingInfoById(warehouse_cd, client_id, id);
        if (warehousingInfoById.size() == 0) {
            // 证明为第一次入库
            // 获取商品种类数
            int product_kind_plan_cnt = items.size();
            int product_kind_cnt = items.size();
            jsonObject.put("product_total", product_total);
            jsonObject.put("product_plan_total", product_plan_total);
            jsonObject.put("product_kind_plan_cnt", product_kind_plan_cnt);
            jsonObject.put("product_kind_cnt", product_kind_cnt);
            jsonObject.put("del_flg", 0);
            Date inspection_date = DateUtils.getNowTime(jsonObject.getString("warehousing_date"));
            Date request_date = DateUtils.getNowTime(jsonObject.getString("request_date"));
            Date warehousing_plan_date =
                DateUtils.getNowTimeWithOutTimeStamp(jsonObject.getString("arrival_date"), "yyyy-MM-dd");
            Date nowTime = DateUtils.getNowTime(null);
            try {
                warehousingResultDao.insertWarehouseResult(jsonObject, nowTime, request_date, warehousing_plan_date,
                    loginNm, date, inspection_date);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }
        } else {
            // 入库的状态
            String status = jsonObject.getString("status");
            // 之前已经部分入库完成
            Tw110_warehousing_result tw110_warehousing_result = warehousingInfoById.get(0);
            List<Tw111_warehousing_result_detail> resultDetails =
                tw110_warehousing_result.getTw111_warehousing_result_details();
            if (resultDetails.size() == 0) {
                logger.error("入库依赖ID={}不存在入库实际详细,为数据错误", id);
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            // 入庫実績商品種類数
            int product_kind_cnt = resultDetails.size();
            // 入庫依頼商品種類数
            int product_kind_plan_cnt = resultDetails.size();
            // 入庫依頼商品数計
            int product_plan_cnt =
                resultDetails.stream().mapToInt(Tw111_warehousing_result_detail::getProduct_plan_cnt).sum();
            // 入庫実績商品数計
            int product_cnt = resultDetails.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_cnt()))
                .mapToInt(Tw111_warehousing_result_detail::getProduct_cnt).sum();
            tw110_warehousing_result.setProduct_total(product_cnt);
            tw110_warehousing_result.setProduct_plan_total(product_plan_cnt);
            tw110_warehousing_result.setProduct_kind_cnt(product_kind_cnt);
            tw110_warehousing_result.setProduct_kind_plan_cnt(product_kind_plan_cnt);

            // 修改入库实际的 入庫依頼商品種類数、入庫依頼商品数計、入庫実績商品種類数、入庫実績商品数計
            try {
                warehousingResultDao.updateWarehouseResultCnt(tw110_warehousing_result, loginNm, date);
            } catch (Exception e) {
                logger.error("入库实际ID={} 修改入库的商品种类数、实际数等失败", id);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            // temporaryIdLIst
            // 获取到目前入库依赖中的所有商品ID
            // List<String> detailProductIdList =
            // resultDetails.stream().map(Tw111_warehousing_result_detail::getProduct_id).collect(Collectors.toList());
        }
    }

    /**
     * @Param: jsonObject
     * @param: itemsJSONObject
     * @param: productTotal
     * @param: date ：日期
     * @param: loginNm ：用户名
     * @description: 将数据放到入庫作業ロケ明細表
     * @return: void
     * @date: 2020/8/12
     */
    @Transactional(rollbackFor = BaseException.class)
    public void insertWarehousingLocationDetail(JSONObject jsonObject, String product_id, String wh_location_nm,
        Integer productTotal, String loginNm, String location_no,
        Date bestBeforeDate, int shipping_flag) {

        String lot_no = "";
        if (!StringTools.isNullOrEmpty(location_no)) {
            lot_no = location_no;
        }
        // 用来记录 本次依赖数的剩余值
        final Integer[] number = {
            productTotal
        };
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String client_id = jsonObject.getString("client_id");
        // 判断该商品是否有引当数大于实际在库数的货架存在
        List<Mw405_product_location> locationInfo;
        try {
            locationInfo = productResultDao.getLocationExceptionInfo(client_id, product_id, null);
        } catch (Exception e) {
            logger.error("查询该商品是否有引当数大于实际在库数的货架存在失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        // 以防止货架重复
        if (locationInfo.size() != 0) {
            String finalLot_no = lot_no;
            locationInfo.stream().filter(location -> {
                Integer num = location.getStock_cnt();
                int defaultNum = 0;
                // 本次入库数大于缺少的商品数量
                if (number[0] >= num) {
                    // 本次依赖的剩余数
                    number[0] = number[0] - num;
                    defaultNum = num;
                } else {
                    // 入库数小于缺少的商品数量
                    number[0] = 0;
                    defaultNum = productTotal;
                }
                // 将数据存到 入庫作業ロケ明細表
                try {
                    stockDao.insertWarehouseLocationDetail(jsonObject.getString("client_id"),
                        jsonObject.getString("id"),
                        product_id, defaultNum, location.getLocation_id(), finalLot_no, DateUtils.getDate(), loginNm,
                        bestBeforeDate,
                        shipping_flag);
                } catch (Exception e) {
                    logger.error("将数据存到 入庫作業ロケ明細表失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
                // 如果本次依赖数已经为0，跳出循环
                return number[0] == 0;
            }).findAny();
        }
        boolean bool = number[0] == 0 && productTotal != 0;
        // 证明入库实际数不为0 但是数组里面为0， 证明已经放在以前的货架上面了
        if (!bool) {
            long time = 3 * 1000;
            long nowTime = DateUtils.getDate().getTime() + time;
            Date dateTime = new Date(nowTime);

            // 判断ロケーション和ロット番号是否存在
            List<Mw404_location> mw404Locations = new ArrayList<>();
            try {
                mw404Locations = stocksResultDao.getLocationByName(wh_location_nm, lot_no, warehouse_cd);
            } catch (Exception e) {
                logger.error("根据货架名称查询货架信息失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }


            // ロケーション和ロット番号不存在时，新规ロケーション和ロット番号
            if (mw404Locations.isEmpty()) {
                JSONObject loctionJson = new JSONObject();
                loctionJson.put("warehouse_cd", warehouse_cd);
                loctionJson.put("wh_location_nm", wh_location_nm);
                loctionJson.put("info", "");
                loctionJson.put("lot_no", lot_no);
                stocksResultService.createNewLocation(loctionJson);
            } else {

                Mw404_location location = mw404Locations.get(0);
                location.setLot_no(lot_no);
                try {
                    stocksResultDao.updateLocationInfo(location);
                } catch (Exception e) {
                    logger.error("修改货架信息失败 货架Id={}", location.getLocation_id());
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }

            String locationId = null;
            try {
                locationId = productResultDao.getLocationId(warehouse_cd, wh_location_nm, lot_no);
            } catch (Exception e) {
                logger.error("根据货架名称查询货架ID失败2");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            String id = jsonObject.getString("id");

            List<Tw113_warehousing_location_detail> warehouseLocationInfo =
                stockDao.getWarehouseLocationInfo(client_id, id, product_id);

            if (warehouseLocationInfo.size() != 0) {
                // 证明之前已经存在 入库的货架信息 只需要更改tw113的货架Id即可
                stockDao.updateWarehouseLocationDetail(client_id, id, product_id, number[0],
                    locationId, dateTime, loginNm);
            } else {
                // 不存在 需要新规入库货架信息
                stockDao.insertWarehouseLocationDetail(client_id, id, product_id, number[0], locationId, lot_no,
                    dateTime, loginNm,
                    bestBeforeDate, shipping_flag);
            }
        }
    }

    /**
     * @param: jsonObject: product_id,name,code,barcode,quantity
     * @description: 仓库侧入库商品PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/24
     */
    @Override
    public JSONObject createWarehouseInfoPDF(JSONObject jsonObject) {
        // 获取仓库名称
        Mw400_warehouse warehouse = warehouseCustomerDao.getWarehouseInfo(jsonObject.getString("warehouse_cd"));
        jsonObject.put("warehouse_nm", warehouse.getWarehouse_nm());
        JSONArray item = jsonObject.getJSONArray("item");
        for (int i = 0; i < item.size(); i++) {
            JSONObject itemJSONObject = item.getJSONObject(i);
            // 获得入庫実績明細テーブル 实际数
            Integer product_cnt = warehousingsDetailDao.getWarehousingResultDetail(jsonObject.getString("warehouse_cd"),
                jsonObject.getString("client_id"),
                jsonObject.getString("id"), itemJSONObject.getString("product_id"));
            itemJSONObject.put("product_cnt", product_cnt);
        }
        String codeName = jsonObject.getString("id");
        String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;

        String client_id = jsonObject.getString("client_id");
        String warehousing_id = jsonObject.getString("id");
        String pdfName = CommonUtils.getPdfName(client_id, "warehousing", "any", warehousing_id);
        String pdfPath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        try {
            // a新版
            PdfTools.createNewWarehouseInfoPDF(jsonObject, codePath, pdfPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(pdfPath);
    }

    /**
     * @param: jsonObject : items 包含商品ID，入库实际数，入库依赖数等
     * @description: 一時保存
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/07/06
     */
    @Transactional
    public JSONObject updateWarehousSave(JSONObject jsonObject) {

        JSONArray items = jsonObject.getJSONArray("items");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String id = jsonObject.getString("id");
        // 获取所有size
        List<Ms010_product_size> sizeList = productResultDao.getSizeNameList();

        int productTotal = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            String client_id = jsonObject.getString("client_id");
            String product_id = itemsJSONObject.getString("product_id");
            Integer product_cnt = Integer.valueOf(itemsJSONObject.getString("product_total"));
            String product_weight = itemsJSONObject.getString("weight");
            String wh_location_nm = itemsJSONObject.getString("wh_location_nm");
            String lot_no = itemsJSONObject.getString("lot_no");

            String sizeName = itemsJSONObject.getString("sizeName");
            String product_size_cd = null;
            if (!StringTools.isNullOrEmpty(sizeName)) {
                // 如果sizeName为空，说明选择了比140 大的size，获取到值
                if (sizeName.length() == 0) {
                    sizeName = itemsJSONObject.getString("value3");
                }
                for (Ms010_product_size sizeInfo : sizeList) {
                    if (sizeName.equals(sizeInfo.getName())) {
                        product_size_cd = sizeInfo.getSize_cd();
                        break;
                    }
                }
            }

            productTotal += product_cnt;
            try {
                // 入库实际明细表 追加入库实际数
                warehousingResultDao.updateProductCnt(warehouse_cd, client_id, id, product_id, product_cnt,
                    product_size_cd, product_weight);
            } catch (Exception e) {
                logger.error("入库实际明细表追加检品数失败:" + "client_id=" + jsonObject.getString("client_id") +
                    "id=" + jsonObject.getString("id"));
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }

            if (StringTools.isNullOrEmpty(lot_no)) {
                lot_no = "";
            }
            String locationId;
            try {
                // 根据货架名称查询货架
                locationId = productResultDao.getLocationId(warehouse_cd, wh_location_nm, lot_no);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error("根据货架名称查询货架失败" + "wh_location_nm= " +
                    itemsJSONObject.getString("wh_location_nm"));
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }

            // ロケーション和ロット番号不存在时，新规ロケーション和ロット番号
            if (StringTools.isNullOrEmpty(locationId)) {
                JSONObject loctionJson = new JSONObject();
                loctionJson.put("warehouse_cd", warehouse_cd);
                loctionJson.put("wh_location_nm", wh_location_nm);
                loctionJson.put("info", "");
                loctionJson.put("lot_no", lot_no);
                stocksResultService.createNewLocation(loctionJson);
            }

            try {
                locationId = productResultDao.getLocationId(warehouse_cd, wh_location_nm, lot_no);
            } catch (Exception e) {
                logger.error("根据货架名称查询货架ID失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }

            // 入库总数
            warehousingResultDao.updateWarehouseLocation(client_id, id, product_id, product_cnt, locationId, lot_no);
        }
        warehousingResultDao.updateProductTotal(productTotal, jsonObject);

        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject : client_id 店舗ID, id 入庫依頼ID, items 几乎商品表的所有数据
     * @param: request
     * @description: 添加数据到在库管理表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/12
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject insertStock(JSONObject jsonObject, HttpServletRequest request) {
        Integer status = jsonObject.getInteger("status"); // 检品済み
        if (status != Constants.WAREHOUSE_STATUS_FINISHED) {
            Integer count = warehousingsDao.selectStatusCount(jsonObject.getString("client_id"),
                jsonObject.getString("id"), status);
            if (count > 0) {
                return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
            }
        }

        String loginNm = CommonUtils.getToken("login_nm", request);
        // 更改入庫ステータス
        jsonObject.put("status", status);
        Date date = DateUtils.getDate();

        try {
            warehousingsDao.updateStatusById(jsonObject);
        } catch (Exception e) {
            logger.error("根据入库依赖修改入库状态失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }

        /*
         * TODO 由于开发一部登录功能 修改了 finishWarehouse 的方法， 其中包含了 updateWarehousSave 的功能，所以暂时注释
         * TODO 还有一个原因 本番报错 入库一切流程正常 但是没有在库数据
         * TODO 重现方法 开始入库 点击一时保存 之后在 检品完了。 是因为一部登录之后 old_status == 3 所以直接进入到
         * TODO updateWarehousSave 方法， 由于该方法里面直接return success 所以不会走到 insertProductLocationInfo 方法 在库表里面没有数据
         * TODO 若210805上载后 一切流程正常 可以删掉 updateWarehousSave 方法和相关的代码 （紧急对应可以恢复该代码救急）
         */

        // 一时保存
        // Integer old_status = jsonObject.getInteger("old_status");
        // 修改入库明细里面的所有数据为 入库完成
        warehousingsDetailDao.updateWarehouseDetailStatus(jsonObject.getString("id"),
            jsonObject.getString("warehouse_cd"), jsonObject.getString("client_id"), null, 1, loginNm, date);
        // if (old_status == Constants.WAREHOUSE_STATUS_FINISHED) {
        // this.warehousingResultService.updateWarehousSave(jsonObject);
        // } else {
        this.warehousingResultService.finishWarehouse(jsonObject, request);
        // }
        // 更改货架信息
        this.warehousingResultService.insertProductLocationInfo(jsonObject, loginNm, date, request);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @param: loginNm : 用户名
     * @param: date ：日期
     * @description: 更改货架信息
     * @return: void
     * @date: 2020/8/12
     */
    @Transactional(rollbackFor = BaseException.class)
    public void insertProductLocationInfo(JSONObject jsonObject, String loginNm, Date date,
        HttpServletRequest request) {

        String client_id = jsonObject.getString("client_id");

        String warehouse_cd = jsonObject.getString("warehouse_cd");

        // 0 初次完了 1 用户确认覆盖之前的货架信息 2 不用修改之前的货架信息
        int confirm = jsonObject.getInteger("confirm");

        // 根据入库依赖Id 查询 入庫作業ロケ明細 信息
        List<Tw113_warehousing_location_detail> warehousingLocationDetailList;

        try {
            warehousingLocationDetailList =
                warehousingResultDao.getWarehousingLocationDetail(jsonObject.getString("id"), client_id);
        } catch (Exception e) {
            logger.error("根据入库依赖Id 查询 入庫作業ロケ明細 信息失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        JSONArray errResultArray = new JSONArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Tw113_warehousing_location_detail warehousingLocationDetail : warehousingLocationDetailList) {
            JSONArray items = jsonObject.getJSONArray("items");
            // 获取商品的 sizeId 和 重量
            String sizeId = null;
            Double weight = null;
            boolean existFlg = false;
            for (int i = 0; i < items.size(); i++) {
                JSONObject itemsJSONObject = items.getJSONObject(i);
                if (itemsJSONObject.getString("product_id").equals(warehousingLocationDetail.getProduct_id())) {
                    jsonObject.put("product_id", warehousingLocationDetail.getProduct_id());
                    jsonObject.put("shipping_flag", itemsJSONObject.getString("shipping_flag"));
                    jsonObject.put("bestbefore_date", itemsJSONObject.getDate("bestbefore_date"));
                    jsonObject.put("lot_no", itemsJSONObject.getString("lot_no"));
                    jsonObject.put("wh_location_nm", itemsJSONObject.getString("wh_location_nm"));
                    jsonObject.put("code", itemsJSONObject.getString("code"));
                    jsonObject.put("barcode", itemsJSONObject.getString("barcode"));
                    jsonObject.put("name", itemsJSONObject.getString("name"));
                    sizeId = itemsJSONObject.getString("sizeId");
                    if (!StringTools.isNullOrEmpty(itemsJSONObject.getString("weight"))) {
                        weight = Double.valueOf(itemsJSONObject.getString("weight"));
                    }
                    existFlg = true;
                    break;
                }
            }
            if (!existFlg) {
                continue;
            }
            String lot_no =
                StringTools.isNullOrEmpty(jsonObject.getString("lot_no")) ? "" : jsonObject.getString("lot_no");
            String wh_location_nm = jsonObject.getString("wh_location_nm");

            // 本次入库的货架 根据 ロケーション名称 和 ロット番号 获取货架信息
            Mw404_location mw404Location = stocksResultDao.getLocationByLotNo(warehouse_cd, lot_no, wh_location_nm);
            String location_id = warehousingLocationDetail.getLocation_id();
            String product_id = jsonObject.getString("product_id");

            boolean differentLocation = !location_id.equals(mw404Location.getLocation_id());
            // 证明本次是补充其它已经出库但是 引当的货架 和本次入力的过期时间以及出荷不可都没有关系

            // 查询该商品是否在库
            Tw300_stock stockInfoById;
            try {
                stockInfoById = stockDao.getStockInfoById(jsonObject);
            } catch (Exception e) {
                logger.error("查询该商品是否在库失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            // 查询该货架的信息
            Mw405_product_location locationInfo;
            try {
                locationInfo = productResultDao.getProductLocationInfo(location_id, client_id,
                    warehousingLocationDetail.getProduct_id());
            } catch (Exception e) {
                logger.error("查询该货架的信息失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }

            Integer shipping_flag = jsonObject.getInteger("shipping_flag");
            shipping_flag = !StringTools.isNullOrEmpty(shipping_flag) ? shipping_flag : 0;
            Date bestbeforeDate = jsonObject.getDate("bestbefore_date");

            // false 货架正常 true 货架过期或者出荷不可
            // boolean notAvailable = StocksResultServiceImpl.getChangeFlg(locationStatus, bestbeforeDate);

            // 在库数改修前的数量
            int beforeNum;
            // 在库改修后的数量
            int afterNum;

            // 实际在库数 = 入库依赖实际数
            Integer stockCnt = warehousingLocationDetail.getProduct_cnt();

            // 默认不可配送数为0
            int notDelivery = 0;

            int changeStatus = 0;

            // 如果商品没有在库信息
            if (StringTools.isNullOrEmpty(stockInfoById)) {
                boolean notAvailable = StocksResultServiceImpl.getChangeFlg(shipping_flag, bestbeforeDate);
                changeStatus = notAvailable ? 1 : 2;

                // 因为没有在库，所以改修前的在库数为0
                beforeNum = 0;
                String maxStockId = getMaxStockId();
                jsonObject.put("stock_id", maxStockId);
                if (changeStatus == 1) {
                    // 货架过期不可以出库
                    notDelivery = stockCnt;
                }

                try {
                    productResultDao.insertProductLocation(product_id, stockCnt, notDelivery,
                        location_id, shipping_flag, bestbeforeDate, client_id, date, loginNm);
                } catch (Exception e) {
                    logger.error("405表 新规商品货架失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }

                List<Tw201_shipment_detail> shipmentDetails =
                    shipmentDetailDao.getShipmentDetail(client_id, product_id);

                Integer requesting_cnt = 0;
                for (Tw201_shipment_detail detail : shipmentDetails) {
                    Integer productPlanCnt = detail.getProduct_plan_cnt();
                    if (detail.getProduct_id().equals(product_id)) {
                        requesting_cnt += productPlanCnt;
                    }
                }

                // 理论在库数 = 本次入库实际数 - 出库依赖中数
                Integer available_cnt = warehousingLocationDetail.getProduct_cnt() - requesting_cnt - notDelivery;
                // 实际在库数 = 入库实际数
                Integer inventory_cnt = warehousingLocationDetail.getProduct_cnt();
                // 不可配送数 = 入库实际数
                try {
                    // 添加在库数据
                    stockDao.insertStockInfo(jsonObject, warehousingLocationDetail.getProduct_id(),
                        available_cnt, inventory_cnt, notDelivery, sizeId, weight, requesting_cnt, loginNm, date);
                } catch (Exception e) {
                    logger.error("添加在库数据失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            } else {

                // 在库表的不可配送数
                int tw300NotDelivery =
                    StringTools.isNullOrEmpty(stockInfoById.getNot_delivery()) ? 0 : stockInfoById.getNot_delivery();

                // 改修前的在库数
                beforeNum = stockInfoById.getInventory_cnt();
                // 如果商品有在库信息的
                // 如果商品没有在该货架上存放
                if (StringTools.isNullOrEmpty(locationInfo)) {
                    boolean notAvailable = StocksResultServiceImpl.getChangeFlg(shipping_flag, bestbeforeDate);
                    changeStatus = notAvailable ? 1 : 2;

                    if (changeStatus == 1) {
                        // 货架过期不可以出库
                        // 货架的不可配送数 = 入库的件数
                        notDelivery = stockCnt;
                        // 在库表的不可配送数加上本次入库 之后 货架的不可配送数
                        tw300NotDelivery += notDelivery;
                    }
                    try {
                        productResultDao.insertProductLocation(product_id, stockCnt, notDelivery, location_id,
                            shipping_flag, bestbeforeDate, client_id, date, loginNm);
                    } catch (Exception e) {
                        logger.error("添加商品货架信息失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                } else {
                    if (!differentLocation) {
                        // 本次循环 商品是放入到入力的货架上面 而不是补充其它货架
                        int status =
                            !StringTools.isNullOrEmpty(locationInfo.getStatus()) ? locationInfo.getStatus() : 0;
                        Date bestbefore_date = locationInfo.getBestbefore_date();

                        boolean unusualFlg =
                            !StringTools.isNullOrEmpty(bestbeforeDate) && !bestbeforeDate.equals(bestbefore_date);
                        // 賞味期限/在庫保管期限 不同
                        if (!StringTools.isNullOrEmpty(bestbefore_date) && !bestbefore_date.equals(bestbeforeDate)) {
                            // 賞味期限/在庫保管期限 不同
                            unusualFlg = true;
                        }
                        if (status != shipping_flag) {
                            unusualFlg = true;
                        }
                        if (unusualFlg) {
                            // 过期时间不同 或者 出荷不可不同 需要提示客户是否覆盖
                            JSONObject json = new JSONObject();
                            json.put("product_id", product_id);
                            json.put("name", jsonObject.getString("name"));
                            json.put("code", jsonObject.getString("code"));
                            json.put("barcode", jsonObject.getString("barcode"));
                            json.put("wh_location_nm", wh_location_nm);
                            json.put("lot_no", lot_no);
                            String beforeDate = "";
                            if (!StringTools.isNullOrEmpty(bestbefore_date)) {
                                beforeDate = dateFormat.format(bestbefore_date);
                            }
                            json.put("bestbefore_date", beforeDate);
                            json.put("status", status);
                            json.put("stock_cnt", locationInfo.getStock_cnt());
                            errResultArray.add(json);
                        }
                    }
                    // 入库实际数 + 原有实际数
                    stockCnt += locationInfo.getStock_cnt();

                    // 货架之前的不可配送数
                    Integer not_delivery = locationInfo.getNot_delivery();
                    int locationInfoNotDelivery = !StringTools.isNullOrEmpty(not_delivery) ? not_delivery : 0;

                    // 根据客户意思 判断是否变更出荷不可 及 过期时间

                    if (confirm == 1 && !differentLocation) {
                        boolean notAvailable = StocksResultServiceImpl.getChangeFlg(shipping_flag, bestbeforeDate);
                        changeStatus = notAvailable ? 1 : 2;
                    } else {
                        if (!differentLocation) {
                            boolean notAvailable = StocksResultServiceImpl.getChangeFlg(locationInfo.getStatus(),
                                locationInfo.getBestbefore_date());
                            changeStatus = notAvailable ? 1 : 2;
                        }
                    }

                    if (changeStatus == 1) {
                        // 货架不可以出库
                        // 入库后的不可配送数 = 货架的实际数
                        notDelivery = stockCnt;
                        // 在库表的不可配送数 = 在库表的不可配送数 - 该货架之前的不可配送数 + 本次入库后 货架的不可配送数
                        tw300NotDelivery = tw300NotDelivery - locationInfoNotDelivery + notDelivery;
                    }

                    if (changeStatus == 2) {
                        // 货架可以出库
                        // 在库表的不可配送数 = 在库表的不可配送数 - 货架的原有实际数
                        tw300NotDelivery = Math.max(tw300NotDelivery - locationInfo.getStock_cnt(), 0);
                    }
                    try {
                        if (confirm == 1 && !differentLocation) {

                            // 客户确认覆盖之前的 货架信息
                            stocksResultDao.updateProductLocation(location_id, product_id, client_id, stockCnt,
                                notDelivery, loginNm, date,
                                shipping_flag, bestbeforeDate);
                        } else {
                            // 不覆盖之前的货架信息
                            productResultDao.updateReserveCntById(location_id, product_id,
                                jsonObject.getString("client_id"), stockCnt, notDelivery, loginNm, date);
                        }
                    } catch (Exception e) {
                        logger.error("修改商品货架信息的在库数失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                    }
                }

                // 实际在库数
                int inventory_cnt = stockInfoById.getInventory_cnt() + warehousingLocationDetail.getProduct_cnt();
                // 理论在库数
                int available_cnt = inventory_cnt - stockInfoById.getRequesting_cnt() - tw300NotDelivery;

                try {
                    // 更改在库数据
                    stockDao.updateStockInfo(jsonObject, available_cnt, inventory_cnt, tw300NotDelivery,
                        warehousingLocationDetail.getProduct_id(), weight, sizeId, date, loginNm);
                } catch (Exception e) {
                    logger.error("更改在库数据失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
            afterNum = stockCnt;
            String stock_history_id;
            try {
                // 获取最大在库履历Id
                stock_history_id = stockDao.getMaxStockHistoryId();
            } catch (Exception e) {
                logger.error("获取最大在库履历Id失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            String stockHistoryId = CommonUtils.getMaxStockHistoryId(stock_history_id);
            Integer type = 1;

            try {
                Tw301_stock_history history = new Tw301_stock_history();
                history.setHistory_id(stockHistoryId);
                history.setPlan_id(jsonObject.getString("id"));
                history.setClient_id(client_id);
                history.setProduct_id(warehousingLocationDetail.getProduct_id());
                String info = Constants.NEW_WAREHOUSING;
                if (!StringTools.isNullOrEmpty(jsonObject.getString("info"))) {
                    info = jsonObject.getString("info");
                }
                history.setInfo(info);
                history.setBefore_num(beforeNum);
                history.setAfter_num(afterNum);
                history.setQuantity(warehousingLocationDetail.getProduct_cnt());
                history.setType(type);
                history.setIns_date(date);
                history.setIns_usr(loginNm);
                history.setUpd_usr(loginNm);
                history.setUpd_date(date);
                // 新规在库履历
                stocksResultDao.insertStockHistory(history);
                warehousingResultDao.updateWarehousingDate(jsonObject, date);
            } catch (Exception e) {
                logger.error("添加在庫履歴失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }

            // 判断出库依赖明细的状态是否可以修改
            if (changeStatus == 2) {
                warehousingResultService.judgeReserveStatus(warehouse_cd, client_id,
                    warehousingLocationDetail.getProduct_id(), warehousingLocationDetail.getProduct_cnt(), request);
            }
        }

        if (!errResultArray.isEmpty() && confirm == 0) {
            logger.error("货架上面的过期时间或出荷不可不同");
            throw new BaseException(ErrorCode.E_50118, errResultArray);
        }
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param product_cnt : 入库数
     * @description: 判断包含该商品出库依赖明细的状态是否可以修改
     * @return: void
     * @date: 2021/6/26 14:06
     */
    @Override
    @Transactional
    public Boolean judgeReserveStatus(String warehouse_cd, String client_id, String product_id, int product_cnt,
        HttpServletRequest request) {
        // 入库数不存在时直接退出
        if (product_cnt <= 0) {
            return false;
        }

        // 获取到出库依赖明细里所有为引当等待的商品信息(商品Id，出库依赖数，引当数)
        int reserve_status = 0;
        List<Tw201_shipment_detail> shipmentDetailList;
        try {
            // 根据商品Id获取该出库依赖为引当等待的引当数
            shipmentDetailList =
                shipmentDetailDao.getReserveStatusList(warehouse_cd, client_id, product_id, reserve_status);
        } catch (Exception e) {
            logger.error("根据商品Id获取该出库依赖的引当数失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        ArrayList<String> shipmentIdList = new ArrayList<>();
        for (Tw201_shipment_detail detail : shipmentDetailList) {
            // 如果入库数都分配完了，则退出
            if (product_cnt <= 0) {
                break;
            }
            // 引当数
            Integer reserve_cnt = detail.getReserve_cnt();
            // 出库依赖数
            Integer product_plan_cnt = detail.getProduct_plan_cnt();
            // set_sub_id
            Integer set_sub_id = detail.getSet_sub_id();

            int reserveNum = 0;
            // 引当状态默认为0（引当等待）
            int reserveFlg = 0;
            // 入库数 大于 出库依赖数 和 引当数的差值
            if (product_cnt >= (product_plan_cnt - reserve_cnt)) {
                product_cnt -= (product_plan_cnt - reserve_cnt);
                // 引当数等于出库依赖数
                reserveNum = product_plan_cnt;
                // 引当状态修改为1（引当済み）
                reserveFlg = 1;
                // 依赖ID去重
                if (!shipmentIdList.contains(detail.getShipment_plan_id())) {
                    shipmentIdList.add(detail.getShipment_plan_id());
                }
            } else {
                // 如果入库数不足与补足缺少的引当数
                reserveNum = reserve_cnt + product_cnt;
                product_cnt = 0;
            }

            // 修改商品的引当数和引当状态
            shipmentDetailDao.updateReserveStatus(detail.getShipment_plan_id(), product_id, client_id, reserveNum,
                reserveFlg, set_sub_id);
        }

        shipmentIdList.forEach(shipmentId -> {
            // 判断出库依赖状态是否可以改变
            shipmentDetailDao.updateShipmentStatus(shipmentId);
        });

        // 可以更改为出荷等待的出库依赖Id
        String[] shipmentIdArray = shipmentIdList.toArray(new String[0]);
        String operation_cd = "31";
        // 顧客別作業履歴新增
        customerHistoryService.insertCustomerHistory(request, shipmentIdArray, operation_cd, null);

        return true;
    }

    /**
     * @param jsonObject : client_id,warehouse_cd,code,lot_not,locationName,bestbefore_date,number,info
     * @param request : 请求
     * @description: 仓库侧商品新规入库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/28 16:01
     */
    @Override
    @Transactional
    public JSONObject directWarehouse(JSONObject jsonObject, HttpServletRequest request) {
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", request);
        // 获取店铺ID
        String client_id = jsonObject.getString("client_id");
        // 获取当前时间
        Date date = DateUtils.getDate();
        Mc100_product product = productDao.getProductInfoByCode(jsonObject.getString("code"), client_id);
        String product_id = product.getProduct_id();
        jsonObject.put("product_id", product_id);
        // 获取最新入库依赖ID
        String id = warehousingsDao.getLastId();
        String warehouseId = WarehousingService.generateWarehousingId(client_id);
        jsonObject.put("id", warehouseId);
        jsonObject.put("product_kind_plan_cnt", 1);
        // 検品タイプ
        jsonObject.put("inspection_type", "サンロジ商品ID");
        // 入库依赖管理插入数据
        warehousingsDao.insertWarehousingsList(jsonObject, date, null, loginNm,
            date, 4);
        // 入库依赖明细插入数据
        int quantityNum = jsonObject.getInteger("quantity");
        jsonObject.put("detail_status", 1);
        warehousingsDetailDao.insertWarehousingsDetailList(jsonObject, quantityNum, date, loginNm);

        // 将商品放入到入库实际表中
        jsonObject.put("product_plan_total", quantityNum);
        jsonObject.put("product_kind_cnt", 1);
        jsonObject.put("product_total", quantityNum);
        jsonObject.put("del_flg", 0);
        warehousingResultDao.insertWarehouseResult(jsonObject, date, date, date, loginNm, date, date);
        // 将数据存到入库实际明细表
        warehousingResultDao.insertWarehouseResultDetil(jsonObject, null, quantityNum, quantityNum, date, loginNm);
        String wh_location_nm = jsonObject.getString("wh_location_nm");
        String lot_no = jsonObject.getString("lot_no");
        // 根据货架名称和ロット号判断之前货架是否存在
        String locationId = productResultDao.getLocationId(warehouse_cd, wh_location_nm, lot_no);
        Date bestBeforeDate = jsonObject.getDate("bestbefore_date");
        if (!StringTools.isNullOrEmpty(bestBeforeDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            jsonObject.put("bestbefore_date", simpleDateFormat.format(bestBeforeDate));
        }
        if (StringTools.isNullOrEmpty(locationId)) {
            // 如果货架不存在 需要新规货架
            stocksResultService.createNewLocation(jsonObject);
        } else {
            // 货架之前存在 需要判断 是否含有别的商品 如果是同一种商品 需要判断过期时间 是否一致
        }
        Integer shipping_flag = jsonObject.getInteger("shipping_flag");

        JSONObject json = new JSONObject();
        json.put("product_id", product_id);
        json.put("wh_location_nm", wh_location_nm);
        json.put("bestbefore_date", bestBeforeDate);
        json.put("shipping_flag", shipping_flag);
        json.put("lot_no", lot_no);
        json.put("code", product.getCode());
        json.put("name", product.getName());
        json.put("barcode", product.getBarcode());

        // 将数据放到入庫作業ロケ明細表
        this.insertWarehousingLocationDetail(jsonObject, product_id, wh_location_nm, quantityNum, loginNm, lot_no,
            bestBeforeDate, shipping_flag);

        // 将数据分配货架，并且判断出库状态是否需要改变
        JSONArray array = new JSONArray();
        array.add(json);
        jsonObject.put("items", array);
        this.insertProductLocationInfo(jsonObject, loginNm, date, request);
        return CommonUtils.success();
    }

    /**
     * @param jsonObject : warehouse_cd: 仓库ID client_id: 店铺ID id: 入库依赖ID items: 入库商品明细 warehousing_date: 入库时间
     * @param request : 请求
     * @description: 添加部分选择的入库明细到在库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/28 13:12
     */
    @Override
    @Transactional
    public JSONObject insertSectionStock(JSONObject jsonObject, HttpServletRequest request) {
        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", request);
        // 获取当前时间
        Date date = DateUtils.getDate();
        // 入库依赖ID
        String id = jsonObject.getString("id");
        // 店铺ID
        String client_id = jsonObject.getString("client_id");
        // 仓库ID
        String warehouse_cd = jsonObject.getString("warehouse_cd");

        // 获取该入库依赖下的所有明细信息
        List<Tc101_warehousing_plan_detail> warehousingDetailList =
            warehousingsDetailDao.getWarehousingDetailList(warehouse_cd, client_id, id);
        if (warehousingDetailList.size() == 0) {
            logger.error("入库依赖ID：{}不存在于入库明细。", id);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        int count = (int) warehousingDetailList.stream().filter(x -> x.getStatus() == 0).count();

        if (count == 0) {
            logger.error("入库依赖ID：{}所包含的入库明细全部都入库完了", id);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 获取到入库依赖中的所有商品ID
        List<String> productIdList = warehousingDetailList.stream().map(Tc101_warehousing_plan_detail::getProduct_id)
            .collect(Collectors.toList());

        // 存储满足条件的 入库依赖明细的商品ID
        ArrayList<String> detailsProductId = new ArrayList<>();
        JSONArray items = jsonObject.getJSONArray("items");
        // 存储符合条件的 入库依赖明细json
        JSONArray productJson = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            JSONObject json = items.getJSONObject(i);
            String product_id = json.getString("product_id");
            if (!productIdList.contains(product_id)) {
                logger.error("商品ID：{}不存在于入库依赖ID：{}的明细中, 输入错误数据", product_id, id);
                continue;
            }
            detailsProductId.add(product_id);
            productJson.add(json);
        }

        if (detailsProductId.size() == 0) {
            logger.error("从前端传给后端的json中的商品信息 不存在于入库依赖ID{}的明细中", id);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 更改满足条件的入库依赖明细的入库状态为 入库完了 (1)
        int status = 1;
        try {
            warehousingsDetailDao.updateWarehouseDetailStatus(id, warehouse_cd, client_id, detailsProductId, status,
                loginNm, date);
        } catch (Exception e) {
            logger.error("修改入库依赖Id：{}明细的入库状态失败, 其中改修的商品Id：{}", id, detailsProductId);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 查询入库明细表中没有入库完了的件数
        status = 0;
        int excessProductIdCnt = warehousingsDetailDao.getWarehousingByStatus(id, warehouse_cd, client_id, status);
        if (excessProductIdCnt == 0) {
            // 入库完了
            int warehouseStatus = 4;
            try {
                warehousingsDao.updateStatus(id, client_id, warehouse_cd, warehouseStatus, loginNm, date,
                    jsonObject.getString("warehousing_date"));
            } catch (Exception e) {
                logger.error("修改入库依赖Id{}的状态失败", id);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        jsonObject.put("items", productJson);
        warehousingResultService.finishWarehouse(jsonObject, request);

        // 修改货架信息
        warehousingResultService.insertProductLocationInfo(jsonObject, loginNm, date, request);

        if (excessProductIdCnt == 0) {
            // 全部入库完成 需要跳转页面
            return CommonUtils.success();
        } else {
            // 只有部分入库完成 去要继续保留在当前页面
            return CommonUtils.failure(ErrorCode.E_50114);
        }
    }

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 查出该仓库所有的货架
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @Override
    public JSONObject getLocationNameByWarehouseId(String warehouse_cd, String searchName, String location_id) {
        List<String> locationNameList;
        try {
            if (!StringTools.isNullOrEmpty(searchName)) {
                searchName = "%" + searchName + "%";
            }
            locationNameList = warehousingResultDao.getLocationNameByWarehouseId(warehouse_cd, searchName);
        } catch (Exception e) {
            logger.error("查出该仓库所有的货架失败,仓库Id为:" + warehouse_cd);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        List<String> nameList = new ArrayList<>();
        if (locationNameList.size() != 0) {
            nameList = locationNameList.stream().distinct().collect(Collectors.toList());
        }
        return CommonUtils.success(nameList);
    }

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 检查货架是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/15
     */
    @Override
    public JSONObject locationNameCheck(String warehouse_cd, String name) {
        Integer locationCount = 0;
        try {
            locationCount = warehousingResultDao.locationNameCheck(warehouse_cd, name);
        } catch (Exception e) {
            logger.error("查出该仓库所有的货架失败,仓库Id为:" + warehouse_cd);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success(locationCount);
    }

    /**
     * @Param: id : 入库依赖Id, client_id: 店铺Id, warehouse_id： 仓库Id
     * @description: 根据入库依赖Id查询商品异常的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/14
     */
    @Override
    public JSONObject getLocationException(JSONObject jsonObject) {
        // 根据入库依赖Id查询对应的商品
        List<Tc101_warehousing_plan_detail> productList = warehousingsDetailDao.getProductId(jsonObject);
        // 获取到所有的商品Id
        List<String> productIdList =
            productList.stream().map(Tc101_warehousing_plan_detail::getProduct_id).collect(Collectors.toList());
        List<Mw405_product_location> locationException =
            warehousingResultDao.getLocationException(jsonObject, productIdList);
        return CommonUtils.success(locationException);
    }

    /**
     * @param warehouse_id : 仓库Id
     * @param client_id ： 店铺Id
     * @description: 入庫ステータス件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/30 15:53
     */
    @Override
    public JSONObject getWarehousingStatusCount(String warehouse_id, String client_id) {
        List<Tc100_warehousing_plan> warehousingStatusCount =
            warehousingResultDao.getWarehousingStatusCount(warehouse_id, client_id);
        Integer[] statusCount = new Integer[5];
        Arrays.fill(statusCount, 0);
        warehousingStatusCount.forEach(warehousing -> {
            Integer status = warehousing.getStatus();
            if (status != 4) {
                // 入库总数
                statusCount[0] += warehousing.getStatus_count();
            }
            switch (status) {
                case 1:
                    // 入庫待ち
                    statusCount[1] += warehousing.getStatus_count();
                    break;
                case 2:
                    // 検品中
                    statusCount[2] += warehousing.getStatus_count();
                    break;
                case 3:
                    // 検品完了
                    statusCount[3] += warehousing.getStatus_count();
                    break;
                case 4:
                    // 入库履历
                    statusCount[4] += warehousing.getStatus_count();
                    break;
                default:
                    break;
            }
        });
        return CommonUtils.success(statusCount);
    }


    /**
     * @description: 获取最大在库Id
     * @return: java.lang.String
     * @date: 2020/07/03
     */
    public String getMaxStockId() {
        String maxStockId = stockDao.getMaxStockId();
        String str;
        if (maxStockId != null) {
            int num = Integer.parseInt(maxStockId);
            num++;
            str = String.format("%010d", num);
        } else {
            str = String.format("%010d", 1);
        }
        return str;
    }


}
