package com.lemonico.store.service;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.*;
import com.lemonico.common.service.ClientService;
import com.lemonico.core.enums.WarehousingEnum;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlBadRequestException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.WarehouseDao;
import com.lemonico.store.dao.WarehousingsDao;
import com.lemonico.store.dao.WarehousingsDetailDao;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 入荷依頼管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehousingService
{

    private final static Logger logger = LoggerFactory.getLogger(WarehousingService.class);

    private final WarehousingsDao warehousingsDao;
    private final WarehousingsDetailDao warehousingsDetailDao;
    private final ProductDao productDao;
    private final WarehouseDao mw400WarehouseDao;
    private final ClientService clientService;
    private final PathProps pathProps;

    /**
     * 入荷依頼IDを採番する
     *
     * @param clientId 店鋪ID
     * @return 入荷依頼ID
     * @since 1.0.0
     */
    public static String generateWarehousingId(String clientId) {
        // 新入库id
        int randomNum = CommonUtils.getRandomNum(100000000);
        String suffix = String.valueOf(randomNum + (System.currentTimeMillis() / 1000L));
        return "W" + clientId + suffix;
    }

    /**
     * @param: client_id ： 店舗ID
     * @param: status ： 入庫ステータス
     * @param: search ： 搜索内容
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @param: tags_id ： タグID
     * @description: 入库依赖一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/12
     */
    public JSONObject getWarehousingList(JSONObject jsonObject, String status1, String search, String startTime,
        String endTime, String tags_id) {
        JSONObject json = new JSONObject();
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        Date start = DateUtils.stringToDate(startTime);
        // 検索日付を加工（23:59:59）にセットする @Add wang 2021/4/20
        Date end = CommonUtils.getDateEnd(endTime);
        int status = 0;
        // 判断是入库一览 or 入库履历
        if (status1 != null) {
            status = Integer.parseInt(status1);
        }
        List<Tc100_warehousing_plan> warehousingsList = warehousingsDao.getWarehousingsList(jsonObject, status, search,
            start, end, tags_id);
        // 遍历集合 获取商品name放到nameList里面
        warehousingsList.forEach(x -> {
            ArrayList<Object> nameList = new ArrayList<>();
            x.getTc101_warehousing_plan_details().forEach(y -> {
                if (y.getMc100_productList().getName() != null) {
                    nameList.add(y.getMc100_productList().getName());
                }
            });

            // 将list里面的数据之间加上 / 转为字符串
            String nameJoin = nameList.size() != 1 ? Joiner.on("/").join(nameList) : Joiner.on(' ').join(nameList);
            x.setNames(nameJoin);

            ArrayList<Object> trackingList = new ArrayList<>();
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_1())) {
                trackingList.add(x.getTracking_codes_1());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_2())) {
                trackingList.add(x.getTracking_codes_2());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_3())) {
                trackingList.add(x.getTracking_codes_3());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_4())) {
                trackingList.add(x.getTracking_codes_4());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_5())) {
                trackingList.add(x.getTracking_codes_5());
            }
            // 将list里面的数据之间加上 / 转为字符串
            String trackingJoin = null;
            if (trackingList.size() > 0) {
                trackingJoin = trackingList.size() != 1 ? Joiner.on("/").join(trackingList)
                    : Joiner.on(' ').join(trackingList);
            }
            x.setTracking_codes(trackingJoin);
        });
        json.put("WarehousingsList", warehousingsList);
        return CommonUtils.success(json);
    }

    public JSONObject getWarehousingsCsvList(JSONObject jsonObject, String status1, String search, String startTime,
        String endTime, String tags_id, String column, String sort) {
        JSONObject json = new JSONObject();
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        Date start = DateUtils.stringToDate(startTime);
        // 検索日付を加工（23:59:59）にセットする @Add wang 2021/4/20
        Date end = CommonUtils.getDateEnd(endTime);
        int status = 0;
        // 判断是入库一览 or 入库履历

        if (status1 != null) {
            status = Integer.parseInt(status1);
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        List<Tc100_warehousing_plan> warehousingsList =
            warehousingsDao.getWarehousingCsvsList(jsonObject, status, search,
                start, end, tags_id, column, sortType);
        // 遍历集合 获取商品name放到nameList里面
        warehousingsList.forEach(x -> {
            ArrayList<Object> nameList = new ArrayList<>();
            x.getTc101_warehousing_plan_details().forEach(y -> {
                if (y.getMc100_productList().getName() != null) {
                    nameList.add(y.getMc100_productList().getName());
                }
            });

            // 将list里面的数据之间加上 / 转为字符串
            String nameJoin = nameList.size() != 1 ? Joiner.on("/").join(nameList) : Joiner.on(' ').join(nameList);
            x.setNames(nameJoin);

            ArrayList<Object> trackingList = new ArrayList<>();
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_1())) {
                trackingList.add(x.getTracking_codes_1());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_2())) {
                trackingList.add(x.getTracking_codes_2());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_3())) {
                trackingList.add(x.getTracking_codes_3());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_4())) {
                trackingList.add(x.getTracking_codes_4());
            }
            if (!StringTools.isNullOrEmpty(x.getTracking_codes_5())) {
                trackingList.add(x.getTracking_codes_5());
            }
            // 将list里面的数据之间加上 / 转为字符串
            String trackingJoin = null;
            if (trackingList.size() > 0) {
                trackingJoin = trackingList.size() != 1 ? Joiner.on("/").join(trackingList)
                    : Joiner.on(' ').join(trackingList);
            }
            x.setTracking_codes(trackingJoin);
        });
        json.put("WarehousingsList", warehousingsList);
        return CommonUtils.success(json);
    }

    /**
     * @Param jsonObject : 入库依赖作成所需全部数据
     * @description: 入庫依頼作成实现类
     * @return: JSONObject
     * @date: 2020/05/13
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject createWarehousing(JSONObject jsonObject, HttpServletRequest request) {
        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", request);
        // 获取当前时间
        Date date = DateUtils.getDate();
        // 获取最新入库依赖ID
        // String id = warehousingsDao.getLastId();
        String client_id = jsonObject.getString("client_id");
        String newId = generateWarehousingId(client_id);
        jsonObject.put("id", newId);
        JSONArray jsonItems = jsonObject.getJSONArray("items");
        String product_id = null, quantity = null;
        int sum = 0;
        for (int i = 0; i < jsonItems.size(); i++) {
            JSONObject json = jsonItems.getJSONObject(i);
            product_id = json.getString("product_id");
            quantity = json.getString("quantity");
            // 计算需要入库依赖商品的总数
            sum += Integer.parseInt(quantity);
        }
        // TODO error
        Mc100_product productById = productDao.getProductById(product_id, client_id);
        String warehouseCd = productById.getWarehouse_cd();
        jsonObject.put("warehouse_cd", warehouseCd);
        Date request_date = DateUtils.getNowTime(null);
        String arrivalDate = jsonObject.getString("arrival_date");
        // Date arrival_date = CommonUtil.getNowTime(arrivalDate);
        Date arrival_date = null;
        if (!StringTools.isNullOrEmpty(arrivalDate)) {
            arrival_date = DateUtils.stringToDate(arrivalDate);
        }
        jsonObject.put("quantity", sum);
        jsonObject.put("product_kind_plan_cnt", jsonItems.size());

        JSONArray tracking_codes = jsonObject.getJSONArray("tracking_codes");
        int size = tracking_codes.size();
        for (int i = 0; i < size; i++) {
            jsonObject.put("tracking_codes_" + (i + 1), tracking_codes.get(i));
        }
        boolean result = warehousingsDao.insertWarehousingsList(jsonObject, request_date, arrival_date, loginNm,
            date, 1) > 0;
        if (!result) {
            logger.error("入库依赖管理插入数据失败");
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        JSONArray items = jsonObject.getJSONArray("items");
        // Integer quantityNum = 0;
        ArrayList<Tc101_warehousing_plan_detail> tc101WarehousingPlanDetails = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject json = items.getJSONObject(i);
            Tc101_warehousing_plan_detail warehousingPlanDetail = new Tc101_warehousing_plan_detail();
            // 顧客CD
            warehousingPlanDetail.setClient_id(client_id);
            // 倉庫コード
            warehousingPlanDetail.setWarehouse_cd(warehouseCd);
            // 入庫依頼ID
            warehousingPlanDetail.setId(newId);
            // 商品ID
            warehousingPlanDetail.setProduct_id(json.getString("product_id"));
            // 作成者
            warehousingPlanDetail.setIns_usr(loginNm);
            // 作成日時
            warehousingPlanDetail.setIns_date(date);
            // 更新者
            warehousingPlanDetail.setUpd_usr(loginNm);
            // 更新日時
            warehousingPlanDetail.setUpd_date(date);
            // 入庫ステータス(0:未入库 1:入库完了)
            warehousingPlanDetail.setStatus(0);
            // 削除フラグ
            warehousingPlanDetail.setDel_flg(0);
            // 入庫実績数
            int productQuantity = 0;
            String quantityNum = json.getString("quantity");
            if (StringTools.isInteger(quantityNum)) {
                productQuantity = Integer.parseInt(quantityNum);
            }
            warehousingPlanDetail.setQuantity(productQuantity);
            // ロット番号
            String lotNo = "";
            if (!StringTools.isNullOrEmpty(json.getString("lot_no"))) {
                lotNo = json.getString("lot_no");
            }
            warehousingPlanDetail.setLot_no(lotNo);
            // 賞味期限/在庫保管期限
            Date bestBeforeDate = null;
            if (!StringTools.isNullOrEmpty(json.getDate("bestbefore_date"))) {
                bestBeforeDate = json.getDate("bestbefore_date");
            }
            warehousingPlanDetail.setBestbefore_date(bestBeforeDate);

            Integer shippingFlag = json.getInteger("shipping_flag");
            warehousingPlanDetail.setShipping_flag(!StringTools.isNullOrEmpty(shippingFlag) ? shippingFlag : 0);

            tc101WarehousingPlanDetails.add(warehousingPlanDetail);
        }
        if (!tc101WarehousingPlanDetails.isEmpty()) {
            try {
                warehousingsDetailDao.insertListDetail(tc101WarehousingPlanDetails);
            } catch (Exception e) {
                logger.error("入库依赖明细插入数据失败, 仓库Id={}, 店铺Id={}, 入库依赖Id={}", warehouseCd, client_id, newId);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info("入库依赖插入数据成功");
        return CommonUtils.success(jsonObject.getString("id"));
    }

    /**
     * @Param: jsonObject : client_id 店舗ID, id 入庫依頼ID
     * @description: 批量删除入库依赖
     * @return: JSONObject
     * @date: 2020/05/14
     */
    @Transactional(rollbackFor = {
        RuntimeException.class, Error.class
    })
    public JSONObject deleteWarehousingsList(JSONObject jsonObject, HttpServletRequest request) {
        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", request);
        // 获取当前时间
        Date date = DateUtils.getDate();

        String delete_flg = jsonObject.getString("delete_flg");

        String id = jsonObject.getString("id");
        String client_id = jsonObject.getString("client_id");

        // 删除之前判断状态，防止误操作
        Integer count = warehousingsDao.selectStatusCount(client_id, id, Constants.WAREHOUSE_STATUS_WAIT);
        if (StringTools.isNullOrEmpty(count) || count <= 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        // 对需要删除入库信息的入库依赖Id进行分割
        Iterable<String> idList = Splitter.on(",").omitEmptyStrings().trimResults().split(id);
        if (!StringTools.isNullOrEmpty(delete_flg) && "1".equals(delete_flg)) {

            List<Tc101_warehousing_plan_detail> warehousingDetailList =
                warehousingsDetailDao.getWarehousingDetailList(null, client_id, id);

            if (warehousingDetailList.size() == 0) {
                logger.error("店铺ID={} 入库依赖ID={} 没有查询出入库依赖明细信息 请检查是否为数据错误", client_id, id);
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            // 取得所有入库完了的明细信息
            List<Tc101_warehousing_plan_detail> planDetails = warehousingDetailList.stream()
                .filter(x -> x.getDel_flg() == 0 && x.getStatus() == 1).collect(Collectors.toList());

            // 入庫依頼商品種類数
            int productKindPlanCnt = planDetails.size();

            // 入庫依頼商品合記
            int quantity = planDetails.stream().mapToInt(Tc101_warehousing_plan_detail::getQuantity).sum();
            int status = 4;
            // 修改入库商品数量信息
            warehousingsDao.updateWarehouseInfo(null, client_id, id, productKindPlanCnt, quantity, status, loginNm,
                date);
            status = 0;
            // 删除没有入库完了的明细
            warehousingsDetailDao.deleteWarehouseDetailByStatus(null, client_id, id, status);

        } else {
            for (String ids : idList) {
                boolean result = warehousingsDao.deleteWarehousingsList(client_id, ids, loginNm, date) > 0;
                if (!result) {
                    logger.error("入库依赖管理删除数据失败");
                    return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                result = warehousingsDetailDao.deleteWarehousingsDetailList(client_id, ids, loginNm, date) > 0;
                if (!result) {
                    logger.error("入库依赖明细删除数据失败");
                    return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        logger.info("入库依赖管理删除数据成功:" + "---用户ID：" + jsonObject.getString("client_id") + "---入库依赖ID："
            + jsonObject.getString("id"));
        return CommonUtils.success("SUCCESS");
    }

    /**
     * @Param: jsonObject : client_id 店舗ID, whs_plan_id 入庫依頼ID, warehouse_cd 倉庫コード
     *         ，还有被修改的其它数据
     * @description: 入库依赖更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/14
     */
    @Transactional(rollbackFor = {
        RuntimeException.class, Error.class
    })
    public JSONObject updateWarehousings(JSONObject jsonObject, HttpServletRequest request) {

        // 更新之前判断状态，防止误操作
        Integer count = warehousingsDao.selectStatusCount(jsonObject.getString("client_id"), jsonObject.getString("id"),
            Constants.WAREHOUSE_STATUS_WAIT);
        if (StringTools.isNullOrEmpty(count) || count <= 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        // 获取用户名
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date updateDate = DateUtils.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = jsonObject.getString("arrival_date");
        Date arrival_date = null;
        if (date != null) {
            try {
                arrival_date = format.parse(date);
            } catch (ParseException e) {
                logger.error("入库依赖明细插入数据失败");
                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        boolean result;
        JSONArray items = jsonObject.getJSONArray("items");
        int productNum = 0;
        List<Tc101_warehousing_plan_detail> tc101WarehousingPlanDetails = new ArrayList<>();
        String clientId = jsonObject.getString("client_id");
        String warehouseCd = jsonObject.getString("warehouse_cd");
        String id = jsonObject.getString("id");
        for (int i = 0; i < items.size(); i++) {
            JSONObject json = items.getJSONObject(i);
            Tc101_warehousing_plan_detail warehousingPlanDetail = new Tc101_warehousing_plan_detail();
            // 顧客CD
            warehousingPlanDetail.setClient_id(clientId);
            // 倉庫コード
            warehousingPlanDetail.setWarehouse_cd(warehouseCd);
            // 入庫依頼ID
            warehousingPlanDetail.setId(id);
            // 商品ID
            warehousingPlanDetail.setProduct_id(json.getString("product_id"));
            // 更新者
            warehousingPlanDetail.setUpd_usr(loginNm);
            // 更新日時
            warehousingPlanDetail.setUpd_date(updateDate);
            // 入庫依頼数
            int productQuantity = 0;
            String quantityNum = json.getString("quantity");
            if (StringTools.isInteger(quantityNum)) {
                productQuantity = Integer.parseInt(quantityNum);
            }
            productNum += productQuantity;
            warehousingPlanDetail.setQuantity(productQuantity);
            // ロット番号
            String lotNo = "";
            if (!StringTools.isNullOrEmpty(json.getString("lot_no"))) {
                lotNo = json.getString("lot_no");
            }
            warehousingPlanDetail.setLot_no(lotNo);
            // 賞味期限/在庫保管期限
            Date bestBeforeDate = null;
            if (!StringTools.isNullOrEmpty(json.getDate("bestbefore_date"))) {
                bestBeforeDate = json.getDate("bestbefore_date");
            }
            warehousingPlanDetail.setBestbefore_date(bestBeforeDate);
            Integer shippingFlag = json.getInteger("shipping_flag");
            warehousingPlanDetail.setShipping_flag(!StringTools.isNullOrEmpty(shippingFlag) ? shippingFlag : 0);
            tc101WarehousingPlanDetails.add(warehousingPlanDetail);
        }

        try {
            warehousingsDetailDao.updateListDetail(tc101WarehousingPlanDetails);
        } catch (Exception e) {
            logger.error("入库依赖明细修改数据失败, 店铺Id={}, 仓库Id={}, 入库依赖Id={}", clientId, warehouseCd, id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        logger.info("入库依赖修改数据成功:" + "---用户ID：" + jsonObject.getString("client_id") + "---入库依赖ID："
            + jsonObject.getString("id"));
        JSONArray tracking_codes = jsonObject.getJSONArray("tracking_codes");
        int size = tracking_codes.size();
        for (int i = 0; i < size; i++) {
            jsonObject.put("tracking_codes_" + (i + 1), tracking_codes.get(i));
        }
        result = warehousingsDao.updateWarehousings(jsonObject, arrival_date, productNum, loginNm, updateDate) > 0;
        if (!result) {
            logger.error("入库依赖管理修改数据失败");
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success("SUCCESS");
    }

    /**
     * @description: 验证 quantity
     * @return: java.lang.Integer
     * @date: 2020/05/14
     */
    private Integer chickQuantity(JSONObject jsonObject, JSONArray items, int i) {
        JSONObject json = items.getJSONObject(i);
        String jsonQuantity = json.getString("quantity");
        if (jsonQuantity == null || "".equals(jsonQuantity)) {
            jsonQuantity = "0";
        }
        Integer quantity = Integer.valueOf(jsonQuantity);
        jsonObject.put("product_id", json.getString("product_id"));
        return quantity;
    }

    /**
     * @param: jsonObject : client_id ： 店舗ID，id : 入庫依頼ID;
     * @description: 入庫依頼による関連情報の照会
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/24
     */
    public JSONObject getInfoById(JSONObject jsonObject) {
        JSONObject jsonObject1 = new JSONObject();
        // 入库依赖管理信息
        Tc100_warehousing_plan tc100_warehousing_plan = warehousingsDao.getInfoById(jsonObject);
        jsonObject1.put("tc100_warehousing_plan", tc100_warehousing_plan);
        // 入库依赖明细信息
        List<Tc101_warehousing_plan_detail> tc101_warehousing_plan_detailList = warehousingsDetailDao
            .getProductId(jsonObject);
        tc101_warehousing_plan_detailList.forEach(tc101 -> {
            Tw111_warehousing_result_detail tw111_warehousing_result_detail = tc101
                .getTw111_warehousing_result_detail();
            tc101.setProduct_cnt(tw111_warehousing_result_detail.getProduct_cnt());
        });

        jsonObject1.put("tc101_warehousing_plan_detail", tc101_warehousing_plan_detailList);
        // 入库实际信息
        Tw110_warehousing_result tw110_warehousing_result = warehousingsDao.getProductInfoById(jsonObject);
        // 入库依赖数
        int product_plan_total = 0;
        // 入库依赖种类数
        int product_kind_plan_cnt = 0;
        if (!StringTools.isNullOrEmpty(tw110_warehousing_result)) {
            product_plan_total = StringTools.isNullOrEmpty(tw110_warehousing_result.getProduct_plan_total()) ? 0
                : tw110_warehousing_result.getProduct_plan_total();
            product_kind_plan_cnt = StringTools.isNullOrEmpty(tw110_warehousing_result.getProduct_kind_plan_cnt()) ? 0
                : tw110_warehousing_result.getProduct_kind_plan_cnt();
        }
        // 入库实际数
        int product_total = 0;
        // 入库实际种类数
        int product_kind_cnt = 0;
        if (!StringTools.isNullOrEmpty(tw110_warehousing_result)) {
            product_total = tw110_warehousing_result.getProduct_total();
            product_kind_cnt = tw110_warehousing_result.getProduct_kind_cnt();
        }
        jsonObject1.put("product_plan_total", product_plan_total);
        jsonObject1.put("product_kind_plan_cnt", product_kind_plan_cnt);
        jsonObject1.put("product_total", product_total);
        jsonObject1.put("product_kind_cnt", product_kind_cnt);
        return CommonUtils.success(jsonObject1);
    }

    /**
     * 入荷依頼PDFを出力する
     *
     * @param jsonObject 入荷依頼情報
     * @return 入荷依頼PDF
     * @since 1.0.0
     */
    public JSONObject getWarehousingPDF(JSONObject jsonObject) {
        // 依頼主マスタ
        final String clientId = jsonObject.getString("client_id");
        final String warehousingId = jsonObject.getString("id");
        Ms201_client ms201Client = clientService.getClientInfo(clientId);
        List<Tc100_warehousing_plan> warehousingList = warehousingsDao.getWarehousingsList(jsonObject, null, null,
            null, null, null);
        String warehouseCd = warehousingList.get(0).getWarehouse_cd();
        jsonObject.put("warehouse_cd", warehouseCd);
        // 商品集合
        List<Tc101_warehousing_plan_detail> productList = warehousingsDetailDao.getProductId(jsonObject);
        // a集合根据商品ID排序
        productList.sort(new BeanSort<>("Product_id", true));
        // 倉庫情報
        Mw400_warehouse mw400Warehouse = mw400WarehouseDao.getInfoByWarehouseCd(warehouseCd);
        final String barcodePath =
            pathProps.getRoot() + pathProps.getStore() + DateUtils.getDateMonth() + "/code/" + warehousingId;

        String pdfName = clientId + "-" + warehousingId + "-" + System.currentTimeMillis() + ".pdf";
        // 全路径前端打开会有问题 还需要一个相对路径
        String pdfPath = pathProps.getRoot() + pathProps.getStore() + DateUtils.getDateMonth() + "/" + pdfName;
        BarcodeUtils.generateCode128Barcode(warehousingId, barcodePath, Constants.WIDTH_3);
        try {
            // a新版
            PdfTools.createWarehousingPDF(productList, mw400Warehouse, ms201Client, clientId, warehousingId,
                barcodePath, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(pdfPath);
    }

    /**
     * @throws ExceptionHandlerAdvice
     * @param: jsonObject
     * @description:入庫依赖CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @Transactional
    public JSONObject warehousingCsvUpload(HttpServletRequest req, String client_id, MultipartFile file,
        String arrival_date, String inspection_type) {
        // 假登录商品信息list
        List<String> fakeLoginList = new ArrayList<String>();
        try {
            List<String> productList = new ArrayList<String>();
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            // a获取当前项目的真实路径
            String path = req.getServletContext().getRealPath("");
            String realPath = (String) path.subSequence(0, path.length() - 7);
            // a获取当前的年份+月份
            Calendar date = Calendar.getInstance();
            int year = date.get(Calendar.YEAR);
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
            // a判断字符串是否为数字的正则
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            // 判断字符是否为半角英文，数字 、 - , _ 的验证
            Pattern checkProCode = Pattern.compile("^[a-zA-Z\\d\\-\\_ ]+$");
            // 校验ロット番号
            Pattern checkLotNo = Pattern.compile("^[a-zA-Z\\d\\-\\_]{1,30}$");
            // 判断字符是否为日期 2021/01/01 2021-01-01
            Pattern checkDate =
                Pattern.compile("^2[0-9]{3}[-|/](0[0-9]{1}|1[0-2]{1})[-|/]([0-2]{1}[0-9]{1}|3[0-1]{1})");
            // a读取上传的CSV文件
            InputStreamReader isr = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            int num = 0;
            int count = 0;
            boolean flag = true;
            while (csvReader.readRecord()) {
                num++;
                if (num == 1) {
                    if (!StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                        String tmp = csvReader.getRawRecord().replaceAll("\"", "");
                        if (!"商品コード,商品名,入庫依頼数,ロット番号,賞味期限,出荷フラグ".equals(tmp)) {
                            throw new PlBadRequestException("タイトル行がご指定のCSVテンプレートと異なりますのでご確認ください。");
                        }
                    }
                }
                if (num > 1001) {
                    throw new PlBadRequestException("一度に登録できるデータは最大1000件です。");
                }
            }
            // a关闭csvReader
            csvReader.close();
            num = 0;
            // a验证入库信息
            InputStreamReader isr1 = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                // count计算创建的数组长度
                count++;
                String tmp = csvReader1.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                num++;
                if (params.length != 6) {
                    list.add("[" + (num + 1) + "行目] " + ErrorCode.E_50120.getDetail());
                    flag = false;
                    continue;
                }

                int k = 0;
                // a商品コード
                if (StringTools.isNullOrEmpty(params[k]) || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] " + ": 商品コードは空にしてはいけません。");
                    flag = false;
                }

                // 判断商品コード是否为符合规范 （全角英文数字 以及、-, _ ）
                if (!StringTools.isNullOrEmpty(params[k]) && !checkProCode.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] " + ": 商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                    flag = false;
                }

                // 判断是否为假登录商品
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // 判断商品コード是否存在
                    String code = productDao.checkCodeExist(client_id, params[k]);
                    boolean productCheck = StringTools.isNullOrEmpty(code);
                    if (productCheck) {
                        list.add("[" + (num + 1) + "行目] " + ": ご記入した商品は検索にヒットしませんので、もう一度ご入力してください。");
                        flag = false;
                    }

                    if (!productCheck) {
                        Integer kubun = productDao.getKubun(client_id, params[k]);
                        if (StringTools.isNullOrEmpty(kubun)) {
                            continue;
                        }
                        if (kubun == 9) {
                            productList.add(params[k]);
                            fakeLoginList.add("[" + (num + 1) + "行目] " + ": 仮登録商品を入庫依頼することはできません。先に商品マスタでご登録ください。");
                            list.add("[" + (num + 1) + "行目] " + ": 仮登録商品を入庫依頼することはできません。先に商品マスタでご登録ください。");
                            count--;
                            continue;
                        } else if (kubun == 2) {
                            productList.add(params[k]);
                            fakeLoginList.add("[" + (num + 1) + "行目] " + ErrorCode.E_40134.getDetail());
                            list.add("[" + (num + 1) + "行目] " + ErrorCode.E_40134.getDetail());
                            count--;
                            continue;
                        }
                    }
                }
                k++;
                // a商品名
                if (!StringTools.isNullOrEmpty(params[k]) && params[k].length() > 250) {
                    list.add("[" + (num + 1) + "行目] " + ": 商品名は、250文字以内でご入力ください。");
                    flag = false;
                }
                k++;
                // a入庫依頼数不能为空
                if (StringTools.isNullOrEmpty(params[k]) || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目] " + ": 入庫依頼数は空にしてはいけません。");
                    flag = false;
                }
                // a入庫依頼数必须为数字
                if (!pattern.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] " + ": 入庫依頼には、半角数字をご入力ください。");
                    flag = false;
                }
                // a入庫依頼数不能超过5位数字
                if (params[k].length() > 5) {
                    list.add("[" + (num + 1) + "行目] " + ": 入庫依頼数には、5桁以内で、半角数字をご入力ください。");
                    flag = false;
                }
                // 入庫依頼数必须大于0
                if (pattern.matcher(params[k]).matches() && Integer.parseInt(params[k]) <= 0) {
                    list.add("[" + (num + 1) + "行目] " + ": 入庫依頼数には、1以上の数字をご入力ください。");
                    flag = false;
                }
                k++;
                // 验证ロット番号 30文字内
                if (!StringTools.isNullOrEmpty(params[k]) && !checkLotNo.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] " + ": ロット番号は半角英数字及び記号「_ -」を使用し、30文字以内で作成してください。");
                    flag = false;
                }
                k++;
                // 賞味期限/在庫保管期限
                if (!StringTools.isNullOrEmpty(params[k]) && !checkDate.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目] " + ": 賞味期限/出荷期限の形式不正。例:2021/01/01、2021-01-01。");
                    flag = false;
                }
                k++;
                // 出荷不可フラグ
                if (!StringTools.isNullOrEmpty(params[k]) && !"0".equals(params[k]) && !"1".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] " + ":出荷不可フラグは0または1をご入力ください。");
                    flag = false;
                }
            }
            // a关闭csvReader
            csvReader1.close();
            num = 0;
            // a判断CSV文件中属性是否重复
            InputStreamReader isr2 = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            // a创建List将product_id放入其中
            List<String> codeList = new ArrayList<String>();
            while (csvReader2.readRecord()) {
                num++;
                String tmpName = csvReader2.getRawRecord();
                String[] paramsName = tmpName.split(",", -1);
                if (num != 1 && codeList.contains(paramsName[0])) {
                    list.add("[" + (num + 1) + "行目] "
                        + "1つのCSVファイル内に、同一商品を複数行記載できません。入庫依頼数を合計して1行にしていただくか、CSVファイルを分けてご依頼ください。");
                    flag = false;
                }

                if (paramsName[0] != null && !"".equals(paramsName[0])) {
                    codeList.add(paramsName[0].replaceAll("\"", ""));
                }
            }

            csvReader2.close();
            // a如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new PlBadRequestException(json);
            }
            // a创建一个长度和CSV长度一样的数组

            List<Mc100_product> mc100Products = productDao.getProductListByCodeList(codeList, client_id);
            if (mc100Products.isEmpty()) {
                logger.error("根据code={} 没有对应的商品信息", codeList);
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            Map<String, List<Mc100_product>> productMap =
                mc100Products.stream().collect(Collectors.groupingBy(Mc100_product::getCode));

            JSONObject[] jsonOb = new JSONObject[count];
            JSONObject jsonBig = new JSONObject();
            InputStreamReader isr3 = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader3 = new CsvReader(isr3);
            csvReader3.readHeaders();
            int index = 0;
            while (csvReader3.readRecord()) {
                String tmp = csvReader3.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                JSONObject jSONObject = new JSONObject();
                int k = 0;
                // 判断是否为假登录商品
                if (!StringTools.isNullOrEmpty(params[k])) {
                    if (productList.contains(params[k])) {
                        continue;
                    }
                }
                // jSONObject.put("product_id", params[k]);
                jSONObject.put("code", params[k]);
                List<Mc100_product> products = productMap.get(params[k]);
                if (products.isEmpty()) {
                    continue;
                }
                jSONObject.put("product_id", products.get(0).getProduct_id());
                k++;
                jSONObject.put("name", params[k]);
                k++;
                jSONObject.put("quantity", params[k]);
                k++;
                jSONObject.put("lot_no", params[k]);
                k++;
                jSONObject.put("bestbefore_date", params[k]);
                k++;
                jSONObject.put("shipping_flag", !StringTools.isNullOrEmpty(params[k]) ? params[k] : 0);
                jsonOb[index] = jSONObject;
                index++;
            }
            jsonBig.put("inspection_type", inspection_type);
            jsonBig.put("items", jsonOb);
            jsonBig.put("client_id", client_id);
            String[] tpm = new String[0];
            jsonBig.put("tracking_codes", tpm);
            jsonBig.put("arrival_date", arrival_date);
            csvReader3.close();
            // 入库商品都为假登录商品时，不进行数据库操作
            if (jsonBig.getJSONArray("items").size() > 0) {
                this.createWarehousing(jsonBig, req);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);

        }

        return CommonUtils.success(fakeLoginList);
    }

    /**
     * @param client_id : 店铺Id
     * @param startTime : 开始时间
     * @param endTime : 结束时间
     * @param tags_id : tag
     * @param search : 检索内容
     * @param status : 出库状态 0：出库依赖中 4：出库履历
     * @description: 入库履历csv数据获取
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/1 13:59
     */
    public JSONObject getHistoryCsvData(String client_id, String startTime, String endTime, String tags_id,
        String search, int status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        Date start = DateUtils.stringToDate(startTime);
        // 検索日付を加工
        Date end = CommonUtils.getDateEnd(endTime);
        List<Tc100_warehousing_plan> warehousingsList = warehousingsDao.getWarehousingsList(jsonObject, status, search,
            start, end, tags_id);

        JSONArray resultArray = new JSONArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Tc100_warehousing_plan warehousingPlan : warehousingsList) {
            JSONObject json = new JSONObject();
            json.put("id", warehousingPlan.getId());

            // 入库状态
            Integer warehousingPlanStatus = warehousingPlan.getStatus();
            String warehousingType = "";
            switch (WarehousingEnum.getStatus(warehousingPlanStatus)) {
                case W_WAITING_FOR_WAREHOUSING:
                    warehousingType = WarehousingEnum.W_WAITING_FOR_WAREHOUSING.getMsg();
                    break;
                case W_UNDER_INSPECTION:
                    warehousingType = WarehousingEnum.W_UNDER_INSPECTION.getMsg();
                    break;
                case W_FINISHED_INSPECTION:
                    warehousingType = WarehousingEnum.W_FINISHED_INSPECTION.getMsg();
                    break;
                case W_FINISHED:
                    warehousingType = WarehousingEnum.W_FINISHED.getMsg();
                    break;
                default:
                    break;
            }
            json.put("status", warehousingType);
            // 入庫依頼日
            Timestamp request_date = warehousingPlan.getRequest_date();
            json.put("request_date", dateFormat.format(request_date));
            // 倉庫着予定日
            Date arrival_date = warehousingPlan.getArrival_date();
            json.put("arrival_date", CommonUtils.dateToStr(arrival_date));
            // 入庫処理日
            Timestamp warehousing_date = warehousingPlan.getWarehousing_date();
            json.put("warehousing_date",
                !StringTools.isNullOrEmpty(warehousing_date) ? dateFormat.format(warehousing_date) : "");
            // 検品タイプ
            json.put("inspection_type", warehousingPlan.getInspection_type());
            // 商品種類数(SKU)
            json.put("product_kind_plan_cnt", warehousingPlan.getProduct_kind_plan_cnt());
            // 入庫依頼数量(PCS)
            json.put("quantity", warehousingPlan.getQuantity());
            // 伝票番号
            List<String> trackingCodeList =
                Arrays.asList(warehousingPlan.getTracking_codes_1(), warehousingPlan.getTracking_codes_2(),
                    warehousingPlan.getTracking_codes_3(), warehousingPlan.getTracking_codes_4(),
                    warehousingPlan.getTracking_codes_5());
            List<String> codeList =
                trackingCodeList.stream().filter(x -> !StringTools.isNullOrEmpty(x)).collect(Collectors.toList());
            String trackingCode = "";
            if (!codeList.isEmpty()) {
                trackingCode = Joiner.on("/").join(codeList);
            }
            json.put("tracking_codes", trackingCode);

            List<Tc101_warehousing_plan_detail> warehousingPlanDetails =
                warehousingPlan.getTc101_warehousing_plan_details();

            JSONArray itemArray = new JSONArray();
            for (Tc101_warehousing_plan_detail warehousingPlanDetail : warehousingPlanDetails) {
                JSONObject detailJson = new JSONObject();
                Mc100_product product = warehousingPlanDetail.getMc100_productList();
                detailJson.put("code", product.getCode());
                detailJson.put("name", product.getName());
                detailJson.put("quantity", warehousingPlanDetail.getQuantity());
                detailJson.put("lot_no", warehousingPlanDetail.getLot_no());
                detailJson.put("shipping_flag", warehousingPlanDetail.getShipping_flag());
                detailJson.put("bestbefore_date", CommonUtils.dateToStr(warehousingPlanDetail.getBestbefore_date()));
                Tw111_warehousing_result_detail warehousingResultDetail =
                    warehousingPlanDetail.getTw111_warehousing_result_detail();
                int realCnt = 0;
                if (!StringTools.isNullOrEmpty(warehousingResultDetail)) {
                    Integer product_cnt = warehousingResultDetail.getProduct_cnt();
                    realCnt = !StringTools.isNullOrEmpty(product_cnt) ? product_cnt : 0;
                }
                detailJson.put("realCnt", realCnt);
                itemArray.add(detailJson);
            }
            json.put("item", itemArray);
            resultArray.add(json);
        }
        return CommonUtils.success(resultArray);
    }
}
