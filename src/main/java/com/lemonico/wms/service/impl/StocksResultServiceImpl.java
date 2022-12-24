package com.lemonico.wms.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlBadRequestException;
import com.lemonico.core.exception.PlResourceAlreadyExistsException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.wms.dao.ProductResultDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.service.ShipmentService;
import com.lemonico.wms.service.StocksResultService;
import com.lemonico.wms.service.WarehousingResultService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
 * 倉庫側在庫管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class StocksResultServiceImpl implements StocksResultService
{

    private final static Logger logger = LoggerFactory.getLogger(StocksResultServiceImpl.class);
    // 排除双引号内的逗号然后分割
    private final static String RULE = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    private final StocksResultDao stocksResultDao;
    private final ProductResultDao productResultDao;
    private final ProductDao productDao;
    private final CommonFunctionDao commonFunctionDao;
    private final ClientDao clientDao;
    private final StockDao stockDao;
    private final WarehousingResultService warehousingResultService;
    private final ShipmentService shipmentService;
    private final UpdateNotDelivery updateNotDelivery;
    private final PathProps pathProps;

    public static boolean getChangeFlg(Integer status, Date beforeDate) {
        boolean cantShipment = false;
        if (status != null && status == 1) {
            // 货架出荷不可
            cantShipment = true;
        } else {
            // 需要判断过期时间 是否过期
            if (!StringTools.isNullOrEmpty(beforeDate)) {
                LocalDate today = LocalDate.now();
                LocalDate localDate = beforeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int between = (int) ChronoUnit.DAYS.between(today, localDate);
                if (between <= 0) {
                    // 货架已经过期
                    cantShipment = true;
                }
            }
        }
        return cantShipment;
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页数
     * @param size : 条数
     * @param column : 排序的字段
     * @param sort : 排序的方式
     * @param location_id : 货架Id
     * @param search : 搜索关键字
     * @description: 获取货架location信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/22 13:25
     */
    @Override
    public JSONObject getLocationList(String warehouse_cd, int page, int size, String column, String sort,
        String location_id, String search) {
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search.trim() + "%";
        }
        PageHelper.startPage(page, size);
        // 获取到所有的货架信息
        List<Mw404_location> locationList =
            stocksResultDao.getProductLocationList(location_id, column, sortType, warehouse_cd, search);
        PageInfo<Mw404_location> pageInfo = new PageInfo<>(locationList);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        if (locationList.isEmpty()) {
            logger.warn("仓库Id={} 没有任何货架信息", warehouse_cd);
            jsonObject.put("locationInfo", jsonArray);
            // 获取到总条数
            jsonObject.put("total", 0);
            return CommonUtils.success(jsonObject);
        }

        // 获取到货架Id集合
        List<String> locationIdList =
            locationList.stream().map(Mw404_location::getLocation_id).distinct().collect(Collectors.toList());

        // 获取到货架上面对应的商品信息
        List<Mw405_product_location> locationDetails = stocksResultDao.getLocationDetail(locationIdList);

        // 根据货架Id 进行分组 key 货架上面存放的多个商品信息
        Map<String, List<Mw405_product_location>> locationDetailMap = new HashMap<>();
        if (!locationDetails.isEmpty()) {
            locationDetailMap =
                locationDetails.stream().collect(Collectors.groupingBy(Mw405_product_location::getLocation_id));
        }

        for (Mw404_location location : locationList) {

            String locationId = location.getLocation_id();
            JSONObject json = new JSONObject();
            // ロケーション
            json.put("wh_location_nm", location.getWh_location_nm());
            // 引当優先順位
            json.put("priority", location.getPriority());
            // ロット番号
            json.put("lot_no", location.getLot_no());
            // ロケーションID
            json.put("location_id", locationId);
            // 在庫数
            int stockCnt = 0;
            // 出庫依頼中
            int requestingCnt = 0;
            // フリー在庫
            int deliveryNum = 0;
            // 不可配送数
            int notDelivery = 0;

            if (locationDetailMap.containsKey(locationId)) {
                List<Mw405_product_location> productLocations = locationDetailMap.get(locationId);
                // 在库数总和
                stockCnt = productLocations.stream().mapToInt(Mw405_product_location::getStock_cnt).sum();
                // 依赖中数总和
                requestingCnt = productLocations.stream().mapToInt(Mw405_product_location::getRequesting_cnt).sum();
                // 不可配送数总和
                notDelivery = productLocations.stream().mapToInt(Mw405_product_location::getNot_delivery).sum();
                // フリー在庫总和
                deliveryNum = stockCnt - notDelivery - requestingCnt;
            }

            // 在庫数
            json.put("stock_cnt", stockCnt);
            // 出庫依頼中
            json.put("requesting_cnt", requestingCnt);
            // フリー在庫
            json.put("deliveryNum", deliveryNum);
            // 不可配送数
            json.put("notDelivery", notDelivery);
            // 備考欄
            json.put("info", location.getInfo());
            jsonArray.add(json);
        }
        jsonObject.put("locationInfo", jsonArray);
        // 获取到总条数
        jsonObject.put("total", pageInfo.getTotal());
        return CommonUtils.success(jsonObject);
    }

    /**
     * @Param:
     * @description: 货架info设定
     * @return: Integer
     * @date: 2020/07/07
     */
    @Override
    public Integer setLocationInfo(String warehouse_cd, String location_id, String info) {
        return stocksResultDao.setLocationInfo(warehouse_cd, location_id, info);
    }

    /**
     * @Param:
     * @description: 获取单个货架中的商品信息
     * @return: JSONObject
     * @date: 2020/07/07
     */
    @Override
    public JSONObject getLocationProduct(String warehouse_cd, String location_id, int page,
        int size, String column, String sort, String client_id) {
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        Mw404_location location = stocksResultDao.getLocationName(warehouse_cd, location_id);


        PageHelper.startPage(page, size);
        List<Mw405_product_location> list =
            stocksResultDao.getLocationProduct(warehouse_cd, location_id, null, column, sortType, client_id);
        PageInfo<Mw405_product_location> pageInfo = new PageInfo<>(list);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        if (list.isEmpty()) {
            logger.warn("仓库Id={} 货架Id={} 上面没有存放任何商品", warehouse_cd, location_id);
            jsonObject.put("result", jsonArray);
            jsonObject.put("total", 0);
            String wh_location_nm = "";
            String info = "";
            if (!StringTools.isNullOrEmpty(location)) {
                wh_location_nm = location.getWh_location_nm();
                info = location.getInfo();
            }
            jsonObject.put("wh_location_nm", wh_location_nm);
            jsonObject.put("info", info);
            jsonObject.put("count", 0);
            return CommonUtils.success(jsonObject);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        for (Mw405_product_location productLocation : list) {
            JSONObject json = new JSONObject();
            Mc100_product mc100Product = productLocation.getMc100_product();
            String clientId = productLocation.getClient_id();
            String product_id = mc100Product.getProduct_id();
            List<Mc102_product_img> productImg = productDao.getProductImg(clientId, product_id);
            Integer stock_cnt = productLocation.getStock_cnt();
            if (stock_cnt == 0) {
                // 在库数为0 不显示
                continue;
            }
            json.put("product_id", product_id);
            json.put("client_id", clientId);
            json.put("code", mc100Product.getCode());
            json.put("name", mc100Product.getName());
            String barcode = mc100Product.getBarcode();
            json.put("barcode", !StringTools.isNullOrEmpty(barcode) ? barcode : "");
            json.put("kubun", mc100Product.getKubun());
            json.put("wh_location_nm", location.getWh_location_nm());
            json.put("stock_cnt", stock_cnt);
            json.put("requesting_cnt", productLocation.getRequesting_cnt());
            Integer notDelivery = productLocation.getNot_delivery();
            String img = "";
            if (productImg.size() != 0) {
                img = productImg.get(0).getProduct_img();
            }
            json.put("productImg", img);
            int deliveryCnt = productLocation.getStock_cnt() - productLocation.getRequesting_cnt() - notDelivery;
            json.put("not_delivery", notDelivery);
            json.put("deliveryCnt", deliveryCnt);

            Date bestbeforeDate = productLocation.getBestbefore_date();
            String beforeDate = "";
            if (!StringTools.isNullOrEmpty(bestbeforeDate)) {
                beforeDate = dateFormat.format(bestbeforeDate);
            }
            json.put("bestbefore_date", beforeDate);

            Integer status = productLocation.getStatus();
            json.put("status", StringTools.isNullOrEmpty(status) ? 0 : status);
            jsonArray.add(json);
        }
        jsonObject.put("result", jsonArray);
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("wh_location_nm", location.getWh_location_nm());
        jsonObject.put("info", location.getInfo());
        jsonObject.put("lot_no", location.getLot_no());
        if (!StringTools.isNullOrEmpty(list)) {
            jsonObject.put("count", list.size());
        }
        return CommonUtils.success(jsonObject);
    }

    /**
     * @Param:
     * @description: 获取商品的货架信息
     * @return: List
     * @date: 2020/07/17
     */
    @Override
    public List<Mw405_product_location> getLocationInfo(String client_id, String product_id) {
        List<Mw405_product_location> locations = stocksResultDao.getLocationInfo(client_id, product_id);
        return locations;
    }

    /**
     * @Param:
     * @description: 盘点开始时新规登录盘点表及明细表
     * @return: Integer
     * @date: 2020/07/10
     */
    @Override
    public Integer createProductCheckAll(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        // a循环遍历前端数据并写入数据库
        JSONObject jsonO = jsonArray.getJSONObject(0);
        Tw302_stock_management tw302 = new Tw302_stock_management();
        // manage_id自增
        Integer manage_id = stocksResultDao.getMaxMid();
        if (manage_id != null) {
            manage_id++;
            tw302.setManage_id(manage_id);
        } else {
            tw302.setManage_id(1);
            manage_id = 1;
        }
        tw302.setWarehouse_cd(jsonO.getString("warehouse_cd"));
        tw302.setClient_id(jsonO.getString("client_id"));
        tw302.setProduct_cnt(jsonArray.size());
        tw302.setState(1);
        tw302.setCheck_date(DateUtils.getDate());
        tw302.setStart_date(DateUtils.getDate());
        stocksResultDao.createProductCheck(tw302);
        // a登录盘点明细表
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonOb = jsonArray.getJSONObject(i);
            Tw303_stock_detail tw303 = new Tw303_stock_detail();
            tw303.setManage_id(manage_id);
            tw303.setProduct_id(jsonOb.getString("product_id"));
            tw303.setStock_count(jsonOb.getJSONObject("tw300_stock").getInteger("inventory_cnt"));
            tw303.setUpdate_date(DateUtils.getDate());
            stocksResultDao.createProductCheckDetail(tw303);
        }
        return manage_id;
    }

    /**
     * @Param:
     * @description: 获取盘点明细表数据(tw303_stock_detail)
     * @return: List
     * @date: 2020/07/10
     */
    @Override
    public List<Tw303_stock_detail> getStockDetail(String client_id, Integer manage_id) {

        return stocksResultDao.getStockDetail(client_id, manage_id);
    }

    /**
     * @Param:
     * @description: 更新盘点在库实际数(tw303_stock_detail)
     * @return: Integer
     * @date: 2020/07/13
     */
    @Override
    public Integer updateStockDetailCount(String product_id, Integer count) {
        return stocksResultDao.updateStockDetailCount(product_id, count);
    }

    /**
     * @Param:
     * @description: 变更盘点状态
     * @return: Integer
     * @date: 2020/07/13
     */
    @Override
    public Integer changeStockCheckState(Integer manage_id, Integer state) {
        stocksResultDao.updateStockCheckEndDate(manage_id, DateUtils.getDate());
        return stocksResultDao.changeStockCheckState(manage_id, state);
    }

    /**
     * @Param:
     * @description: 获取盘点管理信息
     * @return: List
     * @date: 2020/07/13
     */
    @Override
    public List<Tw302_stock_management> getStockCheckManageInfo(Integer state) {
        return stocksResultDao.getStockCheckManageInfo(state);
    }

    /**
     * @Param:
     * @description: 盘点结束后更新理论在库数和実在庫数
     * @return: Integer
     * @date: 2020/07/14
     */
    @Override
    public Integer updateStockCount(String client_id, String product_id, Integer inventory_cnt) {
        // a理论在库数=実在庫数-出库依赖中数
        Integer i = commonFunctionDao.getProductShipmentCount(client_id, product_id);
        return stocksResultDao.updateStockCount(client_id, product_id, inventory_cnt - i, inventory_cnt);
    }

    /**
     * @Param:
     * @description: 检测是否有盘点未完成
     * @return: Integer
     * @date: 2020/07/13
     */
    @Override
    public boolean stockCheckExist(String client_id) {
        return stocksResultDao.stockCheckExist(client_id) != null;
    }

    /**
     * @Param:
     * @description: 统计作业中数
     * @return: Integer
     * @date: 2020/07/13
     */
    @Override
    public Integer getCheckingCount(String warehouse_cd) {
        return stocksResultDao.getCheckingCount(warehouse_cd);
    }

    /**
     * @Param:
     * @description: 新规货架
     * @return: JSONObject
     * @date: 2020/07/15
     */
    @Override
    public JSONObject createNewLocation(JSONObject jsonObject) {
        Mw404_location mw404 = new Mw404_location();
        String wh_location_nm = jsonObject.getString("wh_location_nm");
        String lot_no = jsonObject.getString("lot_no");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        // 查询改货架名 + lot_no 是否存在
        int locationCount = stocksResultDao.getLocationCount(warehouse_cd, wh_location_nm, lot_no);
        if (locationCount > 0) {
            // 货架已经存在
            return CommonUtils.failure(ErrorCode.E_50111);
        }

        // a获取当前location_id的最大值
        List<String> location_id = stocksResultDao.getMaxLocationId(warehouse_cd);
        if (location_id.size() != 0) {
            int[] i = new int[location_id.size()];
            for (int j = 0; j < i.length; j++) {
                int tmp = Integer.parseInt(location_id.get(j));
                i[j] = tmp;
            }
            Arrays.sort(i);
            mw404.setLocation_id(i[i.length - 1] + 1 + "");
            // a获取当前仓库最大的优先顺序
            Integer count = stocksResultDao.getMaxPriority(warehouse_cd);
            if (!StringTools.isNullOrEmpty(count) && count > 0) {
                int k = count + 1;
                mw404.setPriority(k);
            } else {
                mw404.setPriority(1);
            }

        } else {
            mw404.setLocation_id("1");
            mw404.setPriority(1);
        }
        mw404.setWh_location_cd("1");
        mw404.setWh_location_nm(wh_location_nm);
        mw404.setInfo(jsonObject.getString("info"));
        mw404.setWarehouse_cd(warehouse_cd);
        mw404.setLot_no(StringTools.isNullOrEmpty(lot_no) ? "" : lot_no);
        stocksResultDao.createNewLocation(mw404);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject : 在库明细表的所有字段
     * @description: 生成在库商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @Override
    public JSONObject getStockProductDetail(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("stockInfo");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject stockInfo = jsonArray.getJSONObject(i);
            List<Mw405_product_location> productLocation = productResultDao
                .getProductLocation(stockInfo.getString("warehouse_cd"), stockInfo.getString("client_id"),
                    stockInfo.getString("product_id"));
            ArrayList<String> locationName = new ArrayList<>();
            productLocation.stream().forEach(x -> {
                locationName.add(x.getWh_location_nm());
            });
            String locationNm = Joiner.on(",").join(locationName);
            stockInfo.put("locationNm", locationNm);
            Mc100_product productById = productDao.getProductById(stockInfo.getString("product_id"),
                stockInfo.getString("client_id"));
            stockInfo.put("name", productById.getName());
            stockInfo.put("barcode", productById.getBarcode());
            stockInfo.put("code", productById.getCode());
        }
        String codeName = jsonObject.getString("manage_id") + "-" + jsonObject.getString("client_id");
        String codePath = pathProps.getRoot() + pathProps.getWms() + "/" + jsonObject.getString("manage_id") + "/stock/"
            + jsonObject.getString("client_id") + "/" + codeName;
        String pdfName = jsonObject.getString("manage_id") + "-" + jsonObject.getString("client_id") + ".pdf";
        String pdfPath = pathProps.getRoot() + pathProps.getWms() + "/" + jsonObject.getString("manage_id") + "/stock/"
            + jsonObject.getString("client_id") + "/" + pdfName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, 0.2);
        try {
            PdfTools.createStockManagementPDF(jsonObject, codePath, pdfPath);
        } catch (Exception e) {
            logger.error("生成在库商品明细PDF失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.PDF_GENERATE_FAILED, e.getMessage());
        }
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject : locationName: 货架名称, name： 商品名称, code: 商品code，
     *         product_id: 商品Id，stock_cnt： 在库数
     * @description: 生成在库货架上商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @Override
    public JSONObject getStockLocationProduct(JSONObject jsonObject) {
        String dateMonth = DateUtils.getDateMonth();
        String client_id = jsonObject.getString("client_id");
        String pdfName = CommonUtils.getPdfName(client_id, "stock", "location", null);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;

        JSONArray stockInfo = jsonObject.getJSONArray("stockInfo");
        for (int i = 0; i < stockInfo.size(); i++) {
            JSONObject stock = stockInfo.getJSONObject(i);
            String product_id = stock.getString("product_id");
            String codeName = stock.getString("client_id") + "-" + product_id;
            String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
            stock.put("codePath", codePath);
        }
        try {
            PdfTools.createStockProductPDF(stockInfo, pdfPath);
        } catch (Exception e) {
            logger.error("生成在库货架上商品明细PDF失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.PDF_GENERATE_FAILED, e.getMessage());
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param: jsonObject : stockInfo {product_id, (stock_cnt), wh_location_nm,
     *         client_id}
     * @description: 生成在该货架是否有该商品的PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    @Override
    public JSONObject getStockProductCount(JSONObject jsonObject) {
        String dateMonth = DateUtils.getDateMonth();
        JSONArray stockInfo = jsonObject.getJSONArray("stockInfo");
        String locationCodeName = jsonObject.getString("wh_location_nm");
        String locationCodePath = pathProps.getRoot() + pathProps.getWms() + jsonObject.getString("warehouseId") + "/"
            + jsonObject.getString("client_id") + "/location/location/" + dateMonth + "/" + locationCodeName;
        BarcodeUtils.generateCode128Barcode(locationCodeName, locationCodePath, 0.2);
        for (int i = 0; i < stockInfo.size(); i++) {
            JSONObject stock = stockInfo.getJSONObject(i);
            Mc100_product productById = productDao.getProductById(stock.getString("product_id"),
                stock.getString("client_id"));
            String codeName = stock.getString("client_id") + "-" + stock.getString("product_id");
            String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, 0.5);
            String identifier = productById.getIdentifier();
            if (identifier == null) {
                identifier = " ";
            }
            stock.put("codePath", codePath);
            stock.put("identifier", identifier);
            stock.put("name", productById.getName());
        }
        String client_id = jsonObject.getString("client_id");
        int status = Integer.parseInt(jsonObject.getString("status"));
        String pdfName;
        if (status == 1) {
            pdfName = CommonUtils.getPdfName(client_id, "stock", "product", null);
        } else {
            pdfName = CommonUtils.getPdfName(client_id, "stock", "product_count", null);
        }

        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        // String pdfPath = null;
        try {
            PdfTools.createStockProductTablePDF(jsonObject, pdfPath, locationCodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param:
     * @description: 检验货架名是否重复
     * @return: String
     * @date: 2020/07/17
     */
    @Override
    public boolean checkLocationNameExists(String wh_location_nm, String lot_no, String warehouse_cd) {
        String name = stocksResultDao.checkLocationNameExists(wh_location_nm, lot_no, warehouse_cd);
        return !StringTools.isNullOrEmpty(name);
    }

    /**
     * @Param:
     * @description: 检验仓库货架优先顺序是否重复
     * @return: String
     * @date: 2020/08/18
     */
    @Override
    public boolean checkLocationPriorityExists(Integer priority, String warehouse_cd) {
        Integer i = stocksResultDao.checkLocationPriorityExists(priority, warehouse_cd);
        return i != null;
    }

    /**
     * @Param:
     * @description: 货架信息修改
     * @return: Integer
     * @date: 2020/07/22
     */
    @Override
    @Transactional
    public JSONObject updateLocationInfo(String warehouse_cd, JSONObject jsonObject, HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date nowTime = DateUtils.getNowTime(null);
        String locationNm = jsonObject.getString("wh_location_nm");
        Integer priority = jsonObject.getInteger("priority");
        String location_id = jsonObject.getString("location_id");
        String lotNo = jsonObject.getString("lot_no");

        // 修改之前的优先顺序
        int beforePriority = jsonObject.getInteger("beforePriority");

        List<Mw404_location> locationList = stocksResultDao.getLocationList(warehouse_cd);
        if (locationList.isEmpty()) {
            logger.error("仓库Id={} 没有任何货架信息", warehouse_cd);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Map<String, List<Mw404_location>> locationNmMap =
            locationList.stream().collect(Collectors.groupingBy(Mw404_location::getWh_location_nm));

        List<String> locationNmList =
            locationList.stream().map(Mw404_location::getWh_location_nm).distinct().collect(Collectors.toList());

        if (locationNmList.contains(locationNm)) {
            // 货架名称之前已经存在 则取货架名称相同 进行比较 ロット番号
            List<Mw404_location> mw404Locations = locationNmMap.get(locationNm);

            // 获取到优先顺位集合 并排除掉自己
            List<Integer> priorityList = mw404Locations.stream().filter(x -> x.getPriority() != beforePriority)
                .map(Mw404_location::getPriority).distinct().collect(Collectors.toList());
            if (priorityList.contains(priority)) {
                throw new PlResourceAlreadyExistsException("優先順位");
            }
            for (Mw404_location location : mw404Locations) {
                if (!StringTools.isNullOrEmpty(location_id) && location_id.equals(location.getLocation_id())) {
                    continue;
                }
                // 防止错误数据 NULL
                String lot_no = !StringTools.isNullOrEmpty(location.getLot_no()) ? location.getLot_no() : "";
                if (lotNo.equals(lot_no)) {
                    throw new PlResourceAlreadyExistsException("ロット番号");
                }
            }
        }
        // 如果上面条件都不符合 则需要更新货架信息
        Mw404_location mw404 = jsonObject.toJavaObject(Mw404_location.class);
        mw404.setUpd_date(nowTime);
        mw404.setUpd_usr(loginNm);
        stocksResultDao.updateLocationInfo(mw404);
        return CommonUtils.success();
    }

    /**
     * @throws :Exception
     * @Description: LocaltionCSVをアップロードする
     * @Param:
     * @return: JSONObject
     * @Date: 2021/01/22
     */
    @Override
    public JSONObject localCsvUpload(HttpServletRequest req, String warehouse_cd, MultipartFile file) {
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
            List<String> list = new ArrayList<>();
            // a读取上传的CSV文件
            InputStreamReader emptyCheck = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader fileCheck = new CsvReader(emptyCheck);
            boolean b = fileCheck.readHeaders();
            if (!b) {
                throw new PlBadRequestException("ロケーションCSVの中に、データが入っていないため、登録出来ません。");
            }
            InputStreamReader isr = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            // a读取csv中的header
            CsvReader csvReader = new CsvReader(isr);

            // 判断字符是否为半角英文，数字- _ 以及长度30验证
            Pattern checkLocationName = Pattern.compile("^[a-zA-Z\\d\\-\\_]{1,30}$");
            // 判断字符是否全角半角长度100验证
            Pattern checkInfo = Pattern.compile("^[0-9a-zA-Z\\\\uff66-\\\\uff6f\\\\uff71-\\\\uff9f]{0,100}$");
            // 判断字符是否为日期 2021/01/01 2021-01-01
            Pattern checkDate =
                Pattern.compile("^2[0-9]{3}[-|/](0[0-9]{1}|1[0-2]{1})[-|/]([0-2]{1}[0-9]{1}|3[0-1]{1})");
            int num = 0;
            boolean flag = true;
            while (csvReader.readRecord()) {
                num++;
                if (num == 1) {
                    if (!StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                        String header = csvReader.getRawRecord();
                        String param = header.replaceAll("\"", "");
                        if (!"ロケーション名,ロット番号,メモ".equals(param)) {
                            throw new PlBadRequestException("項目名称に不備があります。");
                        }
                    }
                }
            }
            if (num == 1) {
                if (StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                    throw new PlBadRequestException("ロケーションCSVの中に、データが入っていないため、登録出来ません。");
                }
            }
            // a关闭csvReader
            csvReader.close();
            num = 0;
            InputStreamReader isr1 = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                String tmp = csvReader1.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                num++;
                // a跳过样例
                if ("XXX".equals(params[0])) {
                    if (num == 1) {
                        continue;
                    }
                }
                int k = 0;
                // TODO QA ロケーション名和ロット番号 重复怎么处理
                // ロケーション名
                if (params[k] == null || "".equals(params[k]) || params[k].length() == 0) {
                    list.add("[" + (num + 1) + "行目]" + num + ":ロケーション名が登録内容が空です、再度ご確認ください。");
                    flag = false;
                    // } else if (checkLocationNameExists(params[k], warehouse_cd)) {
                    // list.add("[" + (num + 1) + "行目]" + num + ErrorEnum.E_40081.getErrorMsg());
                    // flag = false;
                } else if (!checkLocationName.matcher(params[k]).matches()) {
                    list.add("[" + (num + 1) + "行目]" + num + ":ロケーション名は半角英数字及び記号「_ -」を使用し、30文字以内で作成してください。");
                    flag = false;
                }
                String location_nm = params[k];
                k++;
                // ロット番号
                if (!StringTools.isNullOrEmpty(params[k])) {
                    if (!checkLocationName.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目]" + num + ": ロット番号は半角英数字及び記号「_ -」を使用し、30文字以内で作成してください。");
                        flag = false;
                    }
                }
                // 校验货架重复
                if (!StringTools.isNullOrEmpty(location_nm)) {
                    if (checkLocationNameExists(location_nm, params[k], warehouse_cd)) {
                        list.add("[" + (num + 1) + "行目]" + num + ":ロケーション名が重複登録できませんので、再度ご確認ください。");
                        flag = false;
                    }
                }
                k++;
                // 賞味期限/在庫保管期限
                // if (!StringTools.isNullOrEmpty(params[k])) {
                // if (!checkDate.matcher(params[k]).matches()) {
                // list.add("[" + (num + 1) + "行目]" + num + ErrorEnum.E_40149.getErrorMsg());
                // flag = false;
                // }
                // }
                // k++;
                // 出荷不可フラグ
                // if (!StringTools.isNullOrEmpty(params[k])) {
                // if (!"0".equals(params[k]) && !"1".equals(params[k])) {
                // list.add("[" + (num + 1) + "行目]" + num + ErrorEnum.E_40150.getErrorMsg());
                // flag = false;
                // }
                // }
                // k++;
                // メム
                if (!"".equals(params[k]) && params[k] != null) {
                    if (params[k].length() > 100) {
                        list.add("[" + (num + 1) + "行目]" + num + ":メモは全角半角文字を使用し、100文字以内で作成してください。");
                        flag = false;
                    }
                }
            }
            csvReader1.close();
            num = 0;
            // a判断CSV文件中属性是否重复
            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            // a创建List将货架Name放入其中
            List<String> locationNameCheck = new ArrayList<String>();
            while (csvReader2.readRecord()) {
                num++;
                String tmpName = csvReader2.getRawRecord();
                String[] paramsName = tmpName.split(RULE, -1);
                // a跳过样板数据
                if ("XXX".equals(paramsName[0])) {
                    if (num == 1) {
                        continue;
                    }
                }

                String locationNm = "";
                // 货架名
                if (paramsName[0] != null && !"".equals(paramsName[0])) {
                    locationNm = paramsName[0];
                }
                // ロット番号
                if (paramsName[1] != null && !"".equals(paramsName[1])) {
                    locationNm += paramsName[1];
                }
                if (locationNameCheck.contains(locationNm)) {
                    list.add("[" + (num + 1) + "行目] " + "CSVファイル内に、ロケーションとロット番号が両方一致するデータが2行以上あるため、登録できません。再度ご確認ください。");
                    flag = false;
                }
                locationNameCheck.add(locationNm);
            }
            // a如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new PlBadRequestException(json);
            }
            // a验证通过后写入数据库
            csvReader2.close();
            num = 0;
            InputStreamReader isr3 = new InputStreamReader(Files.newInputStream(destFile.toPath()), "SJIS");
            CsvReader csvReader3 = new CsvReader(isr3);
            csvReader3.readHeaders();

            while (csvReader3.readRecord()) {
                Mw404_location local = new Mw404_location();
                num++;
                String tmp = csvReader3.getRawRecord();
                String param = tmp.replaceAll("\"", "");
                String[] params = param.split(",", -1);
                // a跳过样板数据
                if ("XXX".equals(params[0])) {
                    if (num == 1) {
                        continue;
                    }
                }
                String maxLocationId = getMaxLocationId(warehouse_cd);
                local.setLocation_id(maxLocationId);
                local.setWarehouse_cd(warehouse_cd);
                local.setWh_location_cd("1");
                int k = 0;
                // ロケーション名
                local.setWh_location_nm(params[k]);
                k++;
                // ロット番号
                if (!StringTools.isNullOrEmpty(params[k])) {
                    local.setLot_no(params[k]);
                }
                k++;
                // メモ
                local.setInfo(params[k]);
                // a获取当前仓库最大的优先顺序
                Integer maxPriority = stocksResultDao.getMaxPriority(warehouse_cd);
                if (!StringTools.isNullOrEmpty(maxPriority)) {
                    int priority = maxPriority + 1;
                    local.setPriority(priority);
                } else {
                    local.setPriority(1);
                }
                stocksResultDao.createNewLocation(local);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);
        }
        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页码
     * @param size : 件数
     * @param column : 需要排序的 字段名称
     * @param sort : 排序的方式
     * @param search : 搜索内容
     * @description: 获取在库一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/25 9:39
     */
    @Override
    public JSONObject getStockList(String warehouse_cd, int page, int size, String column, String sort, String search,
        String stock_flg, String client_id) {
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        PageHelper.startPage(page, size);
        List<Mw405_product_location> stockList =
            stocksResultDao.getStockList(warehouse_cd, column, sortType, search, stock_flg, client_id);

        PageInfo<Mw405_product_location> pageInfo = new PageInfo<>(stockList);

        // 获取到店铺ID集合
        List<String> clientIdList =
            stockList.stream().map(Mw405_product_location::getClient_id).collect(Collectors.toList());
        // 根据多个店铺ID 获取多个店铺信息
        List<Ms201_client> clientList = clientDao.getManyClient(clientIdList);
        // key 为店铺Id value 为店铺名
        Map<String, String> clientMap =
            clientList.stream().collect(Collectors.toMap(Ms201_client::getClient_id, Ms201_client::getClient_nm));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
        JSONArray jsonArray = new JSONArray();
        for (Mw405_product_location locationDetail : stockList) {
            JSONObject json = new JSONObject();
            Mw404_location mw404_location = locationDetail.getMw404_location();
            Mc100_product mc100_product = locationDetail.getMc100_product();
            String clientId = locationDetail.getClient_id();
            List<Mc102_product_img> productImg = productDao.getProductImg(clientId, mc100_product.getProduct_id());
            Date bestbeforeDate = locationDetail.getBestbefore_date();
            String beforeDate = "-";
            if (!StringTools.isNullOrEmpty(bestbeforeDate)) {
                beforeDate = dateFormat.format(bestbeforeDate);
            }
            json.put("location_id", locationDetail.getLocation_id());
            json.put("status", locationDetail.getStatus());
            json.put("bestbeforeDate", beforeDate);
            json.put("client_id", clientId);
            String client_nm = clientMap.get(clientId);
            json.put("client_nm", client_nm);
            json.put("product_id", mc100_product.getProduct_id());
            json.put("code", mc100_product.getCode());
            json.put("name", mc100_product.getName());
            json.put("barcode", mc100_product.getBarcode());
            String img = "";
            if (productImg.size() != 0) {
                img = productImg.get(0).getProduct_img();
            }
            json.put("productImg", img);
            String barcode = mc100_product.getBarcode();
            String codeAndBarcode = mc100_product.getCode();
            if (StringTools.isNullOrEmpty(barcode)) {
                barcode = "-";
            }
            codeAndBarcode += "(" + barcode + ")";
            json.put("codeAndBarcode", codeAndBarcode);
            json.put("wh_location_nm", mw404_location.getWh_location_nm());
            json.put("lot_no", mw404_location.getLot_no());
            json.put("stock_cnt", locationDetail.getStock_cnt());
            Integer requesting_cnt = locationDetail.getRequesting_cnt();
            // フリー在庫
            int number = locationDetail.getStock_cnt();
            if (!StringTools.isNullOrEmpty(requesting_cnt)) {
                number -= requesting_cnt;
            }
            if (!StringTools.isNullOrEmpty(locationDetail.getNot_delivery())) {
                number -= locationDetail.getNot_delivery();
            }
            json.put("deliveryNum", number);
            json.put("requesting_cnt", requesting_cnt);
            jsonArray.add(json);
        }
        JSONObject object = new JSONObject();
        object.put("array", jsonArray);
        object.put("total", pageInfo.getTotal());
        return CommonUtils.success(object);
    }

    /**
     * @param jsonObject : warehouse_cd（仓库Id） client_id（店铺Id） product_id（商品Id） alterStockCnt（调整数） changeDate（调整时间）
     *        stockInfo（调整备考） location_id （货架Id）
     * @description: 在库数调整
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 9:39
     */
    @Override
    @Transactional
    public JSONObject changeStockCnt(JSONObject jsonObject, HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        String client_id = jsonObject.getString("client_id");
        String product_id = jsonObject.getString("product_id");
        String location_id = jsonObject.getString("location_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");

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

        // 调的在库数
        String changeStockCnt = jsonObject.getString("alterStockCnt");

        // 获取到之前的在库信息
        Tw300_stock tw300Stock = stockDao.getStockInfoById(jsonObject);

        Tw301_stock_history stockHistory = new Tw301_stock_history();
        // 实际在库数
        Integer inventoryCnt = tw300Stock.getInventory_cnt();

        stockHistory.setHistory_id(stockHistoryId);
        stockHistory.setPlan_id("-");
        stockHistory.setClient_id(client_id);
        stockHistory.setProduct_id(product_id);
        String changeDate = jsonObject.getString("changeDate");
        stockHistory.setInfo(changeDate + ": " + jsonObject.getString("stockInfo"));
        stockHistory.setBefore_num(inventoryCnt);
        Integer after_num = 0;
        // 如果为负数则为 出库调整 只需要改变在库数不需要做其它操作， 如果为正数则为入库调整，需要判断是否有出库数据需要改变状态
        Integer changeCnt = Integer.valueOf(changeStockCnt);
        // 修改后的在库数
        after_num = inventoryCnt + changeCnt;
        stockHistory.setAfter_num(after_num);
        int quantity = changeCnt;
        int type = 11;
        if (changeCnt < 0) {
            // 小于0 则为出库
            quantity = changeCnt * -1;
            type = 22;
        }
        stockHistory.setQuantity(quantity);
        stockHistory.setType(type);
        Date nowTime = DateUtils.getNowTime(null);
        stockHistory.setIns_date(nowTime);
        stockHistory.setUpd_date(nowTime);
        stockHistory.setIns_usr(loginNm);
        stockHistory.setUpd_usr(loginNm);
        // 新规在库履历
        stocksResultDao.insertStockHistory(stockHistory);

        // 获取该货架被修改之前的数据
        Mw405_product_location locationById = stocksResultDao.getLocationById(location_id, client_id, product_id);

        // 货架上面的不可配送数
        int notDelivery =
            !StringTools.isNullOrEmpty(locationById.getNot_delivery()) ? locationById.getNot_delivery() : 0;

        // 在库表的不可配送数
        int stockNotDelivery =
            !StringTools.isNullOrEmpty(tw300Stock.getNot_delivery()) ? tw300Stock.getNot_delivery() : 0;

        if (notDelivery != 0) {
            // 货架的不可配送数不等于0 就证明该货架之前就是过期的 需要在库表的不可配送数减去本次调整数（本次调整数也有负数 所以直接相加即可）
            // 不可配送数最大值为0
            stockNotDelivery = Math.max(stockNotDelivery + changeCnt, 0);
            // 货架不可在库数和 在库表的不可配送数同理
            notDelivery = Math.max(notDelivery + changeCnt, 0);
        }

        // 修改在库表的 在库数 出库依赖中数 不可配送数
        // 出库依赖中数
        Integer requesting_cnt = tw300Stock.getRequesting_cnt();

        // 理论在库数 = 在库数 - 出库依赖中数 - 不可配送数
        int available_cnt = after_num - requesting_cnt - stockNotDelivery;

        // 修改在库表的 实际在库数 理论在库数 不可配送数
        stockDao.updateStockNum(warehouse_cd, client_id, product_id,
            after_num, requesting_cnt, available_cnt, stockNotDelivery, nowTime, loginNm);

        // 修改后的在库数 = 原来的在库数 + 这次变更的数
        int afterStockCnt = locationById.getStock_cnt() + changeCnt;

        // 修改货架在库数
        try {
            stocksResultDao.updateLocationDetailCnt(afterStockCnt, -1, notDelivery, location_id, client_id, product_id,
                loginNm, nowTime);
        } catch (Exception e) {
            logger.error("修改商品货架详细的数量（实际在库数、依赖中数、不可配送数）店铺Id={} 商品Id={} 货架Id={}", client_id, product_id, location_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 若为true 货架过期 不需要改变出荷状态
        boolean notAvailable = getChangeFlg(locationById.getStatus(), locationById.getBestbefore_date());

        if (changeCnt > 0 && !notAvailable) {
            // 为入库调整 需要判断是否有出库状态需要改变
            warehousingResultService.judgeReserveStatus(warehouse_cd, client_id, product_id, changeCnt, request);
        }

        if (changeCnt < 0) {
            // 为出库调整 需要判断是否由出库状态 变为引当等待
            shipmentService.judgeShipmentStatus(warehouse_cd, client_id, product_id, loginNm, nowTime);
        }

        return CommonUtils.success();
    }

    /**
     * @param jsonObject
     * @param request
     * @description: 商品货架移动
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 18:09
     */
    @Override
    @Transactional
    public JSONObject locationMove(JSONObject jsonObject, HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date nowTime = DateUtils.getNowTime(null);
        String lot_no = jsonObject.getString("lot_no");
        String locationNm = jsonObject.getString("locationNm");

        if (Constants.NO_SETTING.equals(lot_no)) {
            lot_no = "";
        }
        String product_id = jsonObject.getString("product_id");
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        String location_id = jsonObject.getString("location_id");
        String client_id = jsonObject.getString("client_id");
        Integer changeStockCnt = jsonObject.getInteger("changeStockCnt");
        String bestbeforeDate = jsonObject.getString("bestbeforeDate");
        Integer priority = jsonObject.getInteger("priority");
        Integer status = jsonObject.getInteger("status");
        // 判断用户是否 已经确认过 直接覆盖移动 0 ：未确认 1：确认覆盖 2：保持原来货架信息
        Integer changeFlg = jsonObject.getInteger("changeFlg");

        // 根据仓库Id 货架Id 获取货架信息
        List<Mw405_product_location> productLocationList =
            stocksResultDao.getLocationProduct(warehouse_cd, location_id, product_id, null, null, null);

        if (productLocationList.isEmpty()) {
            logger.error("仓库Id={} 店铺Id={} 货架上面不存在商品Id={}", warehouse_cd, client_id, product_id);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Mw405_product_location productLocation = productLocationList.get(0);

        Integer productStockCnt = productLocation.getStock_cnt();
        int resultCnt = productStockCnt - changeStockCnt;
        // 修改被移动的货架的在库数
        try {
            stocksResultDao.updateLocationCnt(Math.max(resultCnt, 0), -1, -1, location_id, client_id, product_id,
                loginNm, nowTime);
        } catch (Exception e) {
            logger.error("修改被移动的货架的在库数失败 货架Id={} 店铺Id={} 商品Id={}", location_id, client_id, product_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Date beforeDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringTools.isNullOrEmpty(bestbeforeDate) && !"-".equals(bestbeforeDate)) {
            try {
                beforeDate = dateFormat.parse(bestbeforeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 获取到该货架名对应的所有货架
        List<Mw404_location> mw404Locations = stocksResultDao.getLocationByName(locationNm, null, warehouse_cd);

        List<Mw404_location> locations = new ArrayList<>();

        // 查询该货架名称 是否包含此 ロット番号
        if (mw404Locations.size() != 0) {
            String finalLot_no = lot_no;
            locations =
                mw404Locations.stream().filter(x -> finalLot_no.equals(x.getLot_no())).collect(Collectors.toList());
        }

        // 出库状态变更 (1: 不可出库 2: 可以出库)
        int changeStatus = 0;

        String changeLocationId = null;
        // 如果货架不存在
        if (locations.size() == 0) {
            if (mw404Locations.size() != 0) {
                // 获取到所有的优先顺位
                String finalLot_no1 = lot_no;
                List<Integer> priorityList = mw404Locations.stream()
                    .filter(x -> finalLot_no1.equals(x.getLot_no()))
                    .map(Mw404_location::getPriority).distinct().collect(Collectors.toList());
                // 如果存在本次被移动的优先顺位 提示客户
                if (priorityList.contains(priority)) {
                    throw new BaseException(ErrorCode.E_50115);
                }
            }

            Mw404_location location = new Mw404_location();
            List<String> locationIdList = stocksResultDao.getMaxLocationId(warehouse_cd);
            if (locationIdList.size() != 0) {
                int[] i = new int[locationIdList.size()];
                for (int j = 0; j < i.length; j++) {
                    int tmp = Integer.parseInt(locationIdList.get(j));
                    i[j] = tmp;
                }
                Arrays.sort(i);
                location.setLocation_id(i[i.length - 1] + 1 + "");
                // 继承被移动的货架的优先顺位
                location.setPriority(priority);

            } else {
                location.setLocation_id("1");
                location.setPriority(1);
            }

            location.setWh_location_cd("1");
            location.setWh_location_nm(locationNm);
            location.setWarehouse_cd(warehouse_cd);
            location.setLot_no(StringTools.isNullOrEmpty(lot_no) ? "" : lot_no);
            // 新规货架
            stocksResultDao.createNewLocation(location);

            changeLocationId = location.getLocation_id();
            Mw405_product_location product_location = new Mw405_product_location();
            product_location.setLocation_id(changeLocationId);
            product_location.setClient_id(client_id);
            product_location.setProduct_id(product_id);
            product_location.setStock_cnt(changeStockCnt);
            product_location.setRequesting_cnt(0);
            product_location.setNot_delivery(0);
            product_location.setIns_usr(loginNm);
            product_location.setIns_date(nowTime);
            product_location.setUpd_usr(loginNm);
            product_location.setUpd_date(nowTime);
            product_location.setBestbefore_date(beforeDate);
            product_location.setStatus(status);
            // 新规商品货架信息
            stocksResultDao.createProductLocation(product_location);

            boolean cantShipment = getChangeFlg(status, beforeDate);
            if (cantShipment) {
                // 不可出库
                changeStatus = 1;
            }
        } else {
            // 如果货架之前存在
            Mw404_location location = locations.get(0);
            List<String> locationIdList = Collections.singletonList(location.getLocation_id());
            // 获取到该货架上 放的所有商品
            List<Mw405_product_location> productLocations = stocksResultDao.getLocationDetail(locationIdList);

            // 找出 该货架名对应的所有优先顺位 并排除本次自己选择 移动后的货架
            String finalLotNo = lot_no;
            List<Mw404_location> mw404LocationList = mw404Locations.stream()
                .filter(x -> !x.getLocation_id().equals(location.getLocation_id())).collect(Collectors.toList());

            List<Integer> priorityList = mw404LocationList.stream().filter(x -> x.getLot_no().equals(finalLotNo))
                .map(Mw404_location::getPriority).distinct().collect(Collectors.toList());

            if (priorityList.contains(priority)) {
                // 之前优先顺位已经存在
                throw new BaseException(ErrorCode.E_50115);
            }

            // 判断该货架上面之前是否存在该商品
            boolean doesNotExistLocationDetail = false;
            if (productLocations.size() != 0) {
                List<String> productIdList = productLocations.stream().map(Mw405_product_location::getProduct_id)
                    .distinct().collect(Collectors.toList());
                List<Mc100_product> productInfoList = productDao.getProductInfoList(client_id, productIdList);
                Map<String, Mc100_product> productMap =
                    productInfoList.stream().collect(Collectors.toMap(Mc100_product::getProduct_id, o -> o));
                // 判断该货架上面以前是否存在 本次移动的商品

                // 使用店铺Id 和 商品Id 为组合键 生成map value 405对象
                Map<String, Mw405_product_location> productLocationMap = productLocations.stream()
                    .collect(Collectors.toMap(x -> x.getClient_id() + "_" + x.getProduct_id(), o -> o));

                String key = client_id + "_" + product_id;


                if (productLocationMap.containsKey(key)) {
                    // 商品之前存在于该货架
                    Mw405_product_location mw405ProductLocation = productLocationMap.get(key);

                    int mw405Status =
                        !StringTools.isNullOrEmpty(mw405ProductLocation.getStatus()) ? mw405ProductLocation.getStatus()
                            : 0;
                    Date bestbeforeDate1 = mw405ProductLocation.getBestbefore_date();
                    // 需要判断其中的 出荷不可フラグ 和 过期时间是否相同 如果不同需要客户确认
                    boolean unusualFlg = mw405Status != status;
                    // 出荷不可フラグ 不同
                    // 变更后的出荷状态 如果为0 则证明 将不可出库变更为可以出库 反之为 1 就是将可以出库变为不可出库
                    // changeStatus = status == 0 ? 2 : 1;
                    if (!StringTools.isNullOrEmpty(bestbeforeDate1) && !bestbeforeDate1.equals(beforeDate)) {
                        // 賞味期限/在庫保管期限 不同
                        unusualFlg = true;
                    }
                    if (!StringTools.isNullOrEmpty(beforeDate) && !beforeDate.equals(bestbeforeDate1)) {
                        // 賞味期限/在庫保管期限 不同
                        unusualFlg = true;
                    }

                    if (unusualFlg && changeFlg == 0) {
                        JSONArray resultArray = new JSONArray();
                        JSONObject json = new JSONObject();
                        String productId = mw405ProductLocation.getProduct_id();
                        Mc100_product mc100Product = productMap.get(productId);
                        json.put("client_id", client_id);
                        json.put("product_id", productId);
                        json.put("name", mc100Product.getName());
                        json.put("code", mc100Product.getCode());
                        json.put("barcode", mc100Product.getBarcode());
                        json.put("locationNm", locationNm);
                        json.put("lot_no", location.getLot_no());
                        json.put("status", mw405Status);
                        String expireDate = "";
                        if (!StringTools.isNullOrEmpty(bestbeforeDate1)) {
                            expireDate = dateFormat.format(bestbeforeDate1);
                        }
                        json.put("bestbefore_date", expireDate);
                        json.put("priority", location.getPriority());
                        json.put("stock_cnt", mw405ProductLocation.getStock_cnt());
                        resultArray.add(json);
                        throw new BaseException(ErrorCode.E_50118, resultArray);
                    }

                    // 之前的在库数
                    Integer stock_cnt = mw405ProductLocation.getStock_cnt();
                    int newStockCnt = stock_cnt + changeStockCnt;
                    // 修改已经存在的货架 移动到该货架 的在库数
                    if (changeFlg == 1) {
                        // 客户确认覆盖之前 不同的出荷不可 以及 过期时间
                        stocksResultDao.updateProductLocation(location.getLocation_id(), product_id, client_id,
                            newStockCnt, -1,
                            loginNm, nowTime, status, beforeDate);
                    } else {
                        // 不需要改变之前的 过期时间及 出荷不可
                        stocksResultDao.updateLocationCnt(newStockCnt, -1, -1, location.getLocation_id(),
                            client_id, product_id, loginNm, nowTime);
                    }

                    changeLocationId = location.getLocation_id();

                    // 变更后 货架的出荷状态 true 不可出库 false 可以出库
                    boolean afterLocationFlg = getChangeFlg(status, beforeDate);

                    // 变更前 货架的出荷状态 true 不可出库 false 可以出库
                    boolean beforeLocationFlg = getChangeFlg(mw405Status, bestbeforeDate1);

                    if (beforeLocationFlg) {
                        // 货架之前就是过期的
                        if (!afterLocationFlg) {
                            // 移动变更后 客户 确认不用覆盖之前的货架信息， 货架还是不可以出库 反之 可以出库
                            changeStatus = changeFlg == 2 ? 1 : 2;
                        } else {
                            // 变更之后还是过期
                            changeStatus = 1;
                        }
                    } else {
                        // 货架之前可以正常出库
                        if (afterLocationFlg) {
                            // 移动变更后 用户确认不用覆盖之前的货架信息 货架还是可以出库的 反之 不可以出库
                            changeStatus = changeFlg == 2 ? 2 : 1;
                        } else {
                            // 移动变更后还是可以正常出库
                            changeStatus = 2;
                        }
                    }
                } else {
                    doesNotExistLocationDetail = true;
                }
            } else {
                doesNotExistLocationDetail = true;
            }
            if (doesNotExistLocationDetail) {
                // 需要新规商品货架信息
                boolean cantShipment = getChangeFlg(status, beforeDate);
                if (cantShipment) {
                    // 不可出库
                    changeStatus = 1;
                }
                changeLocationId = location.getLocation_id();
                // 证明该货架 为空货架 可以直接移动
                Mw405_product_location product_location = new Mw405_product_location();
                product_location.setLocation_id(changeLocationId);
                product_location.setClient_id(client_id);
                product_location.setProduct_id(product_id);
                product_location.setStock_cnt(changeStockCnt);
                product_location.setRequesting_cnt(0);
                product_location.setNot_delivery(0);
                product_location.setBestbefore_date(beforeDate);
                product_location.setStatus(status);
                product_location.setIns_usr(loginNm);
                product_location.setIns_date(nowTime);
                product_location.setUpd_usr(loginNm);
                product_location.setUpd_date(nowTime);
                // 新规商品货架信息
                stocksResultDao.createProductLocation(product_location);
            }
            // 货架master 需要更改其 优先顺位
            location.setPriority(priority);
            location.setUpd_usr(loginNm);
            location.setUpd_date(nowTime);
            stocksResultDao.updateLocationInfo(location);
        }

        if (changeStatus != 0) {
            // 证明出库状态 需要变更
            updateNotDelivery.changeLocationStatus(changeStatus, client_id, warehouse_cd, request, changeLocationId,
                product_id);
        }

        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页码
     * @param size : 件数
     * @param column : 需要排序的 字段名称
     * @param sort : 排序的方式
     * @param client_id : 店铺Id
     * @param stock_flg : 是否在库
     * @description: 获取商品在库一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/31 13:35
     */
    @Override
    public JSONObject getProductStockList(String warehouse_cd, int page, int size, String column,
        String sort, int stock_flg, String client_id, String search) {

        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }

        JSONObject resultJson = new JSONObject();

        // 获取商品在库一览
        PageHelper.startPage(page, size);
        List<Tw300_stock> productStockList =
            stocksResultDao.getProductStockList(warehouse_cd, column, sortType, stock_flg,
                client_id, search);
        if (StringTools.isNullOrEmpty(productStockList) || productStockList.size() == 0) {
            logger.info("店铺Id={},没有商品有在库信息", warehouse_cd);
            resultJson.put("total", 0);
            return CommonUtils.success(resultJson);
        }

        PageInfo<Tw300_stock> pageInfo = new PageInfo<>(productStockList);

        // 获取到所有的商品Id
        List<String> productIdList =
            productStockList.stream().map(Tw300_stock::getProduct_id).distinct().collect(Collectors.toList());

        // 获取到所有的商品图片信息
        List<Mc102_product_img> productImgList = productDao.getProductImgList(productIdList, null);
        Map<String, List<Mc102_product_img>> productImgMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(productImgList) && productImgList.size() != 0) {
            // 根据 店铺Id_商品Id 进行分组转为map
            productImgMap =
                productImgList.stream().collect(Collectors.groupingBy(x -> x.getClient_id() + "_" + x.getProduct_id()));
        }

        // 获取到所有商品在货架上面的信息
        List<Mw405_product_location> locationByProductId =
            stocksResultDao.getLocationByProductId(productIdList, warehouse_cd);
        Map<String, List<Mw405_product_location>> productLocationMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(locationByProductId) && locationByProductId.size() != 0) {
            // 根据 店铺Id_商品Id 进行分组转为map
            productLocationMap = locationByProductId.stream()
                .collect(Collectors.groupingBy(x -> x.getClient_id() + "_" + x.getProduct_id()));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        JSONArray resultArray = new JSONArray();

        for (Tw300_stock stock : productStockList) {
            JSONObject json = new JSONObject();
            Mc100_product product = stock.getMc100_product();
            String clientId = stock.getClient_id();
            String productId = stock.getProduct_id();
            String key = clientId + "_" + productId;
            // 店铺Id
            json.put("client_id", clientId);
            // 商品Id
            json.put("product_id", productId);
            // 商品名称
            json.put("name", product.getName());
            // 商品code
            json.put("code", product.getCode());
            // 商品管理code
            json.put("barcode", product.getBarcode());
            // 商品区分
            json.put("kubun", product.getKubun());
            // 依赖中数
            Integer requestingCnt = stock.getRequesting_cnt();
            json.put("requesting_cnt", requestingCnt);
            // 实际在库数
            Integer inventoryCnt = stock.getInventory_cnt();
            json.put("inventory_cnt", inventoryCnt);
            // 不可配送数
            Integer notDelivery = stock.getNot_delivery();
            // 可配送数
            int deliveryCnt = inventoryCnt - requestingCnt - notDelivery;
            json.put("not_delivery", notDelivery);
            // フリー在庫
            json.put("delivery_cnt", deliveryCnt);
            // 商品图像
            // String img = "";
            List<String> imgList = new ArrayList<>();
            if (productImgMap.containsKey(key)) {
                List<Mc102_product_img> mc102ProductImgList = productImgMap.get(key);
                if (mc102ProductImgList.size() != 0) {
                    // img = mc102ProductImgList.get(0).getProduct_img();
                    imgList = mc102ProductImgList.stream().map(Mc102_product_img::getProduct_img)
                        .collect(Collectors.toList());
                }
            }
            json.put("product_img", imgList);

            JSONArray locationArray = new JSONArray();
            if (productLocationMap.containsKey(key)) {
                List<Mw405_product_location> productLocations = productLocationMap.get(key);
                for (Mw405_product_location location : productLocations) {
                    JSONObject locationJson = new JSONObject();
                    Mw404_location mw404Location = location.getMw404_location();
                    // 货架ID
                    locationJson.put("location_id", location.getLocation_id());
                    // 货架名称
                    locationJson.put("location_name", mw404Location.getWh_location_nm());
                    // 货架优先顺位
                    locationJson.put("priority", mw404Location.getPriority());
                    // ロット番号
                    locationJson.put("lot_no", mw404Location.getLot_no());
                    // 賞味期限/在庫保管期限
                    Date bestbeforeDate = location.getBestbefore_date();
                    String expireDate = "";
                    if (!StringTools.isNullOrEmpty(bestbeforeDate)) {
                        expireDate = dateFormat.format(bestbeforeDate);
                    }
                    locationJson.put("bestbefore_date", expireDate);
                    // 出荷不可フラグ
                    locationJson.put("status", location.getStatus());
                    // 出庫依頼中数
                    Integer locationRequestingCnt = location.getRequesting_cnt();
                    locationJson.put("requesting_cnt", locationRequestingCnt);
                    // 在庫数
                    Integer locationStockCnt = location.getStock_cnt();
                    locationJson.put("stock_cnt", locationStockCnt);
                    // 不可配送数
                    Integer locationNotDelivery = location.getNot_delivery();
                    locationJson.put("not_delivery",
                        !StringTools.isNullOrEmpty(locationNotDelivery) ? locationNotDelivery : 0);
                    // 可配送数
                    int locationDeliveryCnt = 0;
                    if (!StringTools.isNullOrEmpty(locationStockCnt)) {
                        locationDeliveryCnt = locationStockCnt;
                    }
                    if (!StringTools.isNullOrEmpty(locationRequestingCnt)) {
                        locationDeliveryCnt -= locationRequestingCnt;
                    }
                    if (!StringTools.isNullOrEmpty(locationNotDelivery)) {
                        locationDeliveryCnt -= locationNotDelivery;
                    }
                    locationJson.put("locationDeliveryCnt", locationDeliveryCnt);

                    if (productLocations.size() == 1) {
                        locationArray.add(locationJson);
                    } else {
                        if (locationStockCnt != 0) {
                            locationArray.add(locationJson);
                        }
                    }
                }
            }
            // 货架信息
            json.put("locationInfo", locationArray);
            resultArray.add(json);
        }
        resultJson.put("productInfo", resultArray);
        resultJson.put("total", pageInfo.getTotal());
        return CommonUtils.success(resultJson);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @description: 获取商品对应的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/1 13:05
     */
    @Override
    public JSONObject getProductLocationInfo(String warehouse_cd, String product_id, String client_id) {

        JSONObject resultJson = new JSONObject();

        // 获取店铺信息
        Ms201_client clientInfo = clientDao.getClientInfo(client_id);

        // 获取该商品的货架信息
        List<Mw405_product_location> locationInfoList = stocksResultDao.getLocationInfo(client_id, product_id);

        if (StringTools.isNullOrEmpty(locationInfoList) || locationInfoList.size() == 0) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 获取到去重的货架名称 并按照优先顺序排序 正序
        List<String> locationNmList = locationInfoList.stream()
            .filter(x -> !StringTools.isNullOrEmpty(x.getStock_cnt()))
            .sorted(Comparator.comparing(x -> x.getMw404_location().getPriority()))
            .map(x -> x.getMw404_location().getWh_location_nm()).distinct().collect(Collectors.toList());

        // 获取到根据 货架名称进行分组的map
        Map<String, List<Mw405_product_location>> locationMap = locationInfoList.stream()
            .filter(x -> !StringTools.isNullOrEmpty(x.getStock_cnt()))
            .collect(Collectors.groupingBy(x -> x.getMw404_location().getWh_location_nm()));

        resultJson.put("locationNmList", locationNmList);
        resultJson.put("locationMap", locationMap);
        resultJson.put("clientInfo", clientInfo);
        // locationInfoList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getMw404_location().getWh_location_nm()))
        // .collect(Collectors.groupingBy())
        return CommonUtils.success(resultJson);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param locationNm : 货架名称
     * @description: 根据或货架名称获取货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/10 1:17
     */
    @Override
    public JSONObject getLocationInfoByName(String warehouse_cd, String locationNm) {

        if (StringTools.isNullOrEmpty(locationNm)) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        List<Mw404_location> locations = stocksResultDao.getLocationInfoByName(warehouse_cd, locationNm);
        return CommonUtils.success(locations);
    }

    /**
     * @param jsonObject : warehouse_cd（仓库Id） location_id（货架Id） product_id（商品Id） bestbefore_date（过期时间） status（出荷不可）
     *        beforeStatus（修改之前的出荷状态） beforeDate（修改之前的过期时间）
     * @param request : 请求
     * @description: 修改货架明细的 过期时间 和 出荷不可
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/10/22 13:57
     */
    @Override
    public JSONObject updateLocationDetail(JSONObject jsonObject, HttpServletRequest request) {
        // 店铺Id
        String client_id = jsonObject.getString("client_id");
        // 货架Id
        String location_id = jsonObject.getString("location_id");
        // 商品Id
        String product_id = jsonObject.getString("product_id");
        // 过期时间
        String bestbefore_date = jsonObject.getString("bestbefore_date");
        // 仓库Id
        String warehouse_cd = jsonObject.getString("warehouse_cd");
        // 修改之前的出荷状态
        Integer beforeStatus = jsonObject.getInteger("beforeStatus");
        beforeStatus = !StringTools.isNullOrEmpty(beforeStatus) ? beforeStatus : 0;
        // 修改之前的过期时间
        Date beforeDate = jsonObject.getDate("beforeDate");

        Date changeDate = null;
        if (!StringTools.isNullOrEmpty(bestbefore_date)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                changeDate = dateFormat.parse(bestbefore_date);
            } catch (ParseException e) {
                logger.error("货架详细编辑时 转换时间失败 店铺Id={} 货架Id={} 商品Id={}", client_id, location_id, product_id);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 出荷不可
        String status = jsonObject.getString("status");
        int changeStatus = 0;
        if (!StringTools.isNullOrEmpty(status)) {
            changeStatus = Integer.parseInt(status);
        }
        String loginNm = CommonUtils.getToken("loginNm", request);
        Date nowTime = DateUtils.getNowTime(null);

        // 修改之前的货架 状态 true 不可以出库 false 可以出库
        boolean beforeChangeFlg = getChangeFlg(beforeStatus, beforeDate);

        // 修改之后的货架 状态 true 不可以出库 false 可以出库
        boolean afterChangeFlg = getChangeFlg(changeStatus, changeDate);

        int transformStatus = 0;
        if (beforeChangeFlg) {
            // 货架之前是过期的
            if (!afterChangeFlg) {
                // 修改之后的货架状态为不过期
                transformStatus = 2;
            }
        } else {
            // 货架之前是不过期的
            if (afterChangeFlg) {
                // 修改之后是过期的
                transformStatus = 1;
            }
        }

        // 判断出库状态是否需要变更
        if (transformStatus != 0) {
            updateNotDelivery.changeLocationStatus(changeStatus, client_id, warehouse_cd, request, location_id,
                product_id);
        }

        try {
            stocksResultDao.updateLocationDetail(client_id, location_id, product_id, changeDate, changeStatus, loginNm,
                nowTime);
        } catch (Exception e) {
            logger.error("更改货架明细中指定商品的过期时间和出荷不可失败, 店铺Id={} 货架Id={} 商品Id={}", client_id, location_id, product_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }


        return CommonUtils.success();
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param stock_flg : 在庫数flg
     * @param search : 检索条件
     * @param column : 需要排序的字段名称
     * @param sort : 排序的方式
     * @description: 在库一览csv下载获取数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/22 16:28
     */
    @Override
    public JSONObject getStockListCsvData(String warehouse_cd, String client_id, int stock_flg, String search,
        String column, String sort) {
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        List<Tw300_stock> productStockList =
            stocksResultDao.getProductStockList(warehouse_cd, column, sortType, stock_flg,
                client_id, search);
        JSONArray resultArray = new JSONArray();
        if (StringTools.isNullOrEmpty(productStockList) || productStockList.isEmpty()) {
            logger.error("店铺Id={}, 仓库Id={} 没有任何在库的商品信息", client_id, warehouse_cd);
            return CommonUtils.success(resultArray);
        }

        // 获取到所有店铺Id集合
        List<String> clientIdList =
            productStockList.stream().map(Tw300_stock::getClient_id).distinct().collect(Collectors.toList());
        // 批量查询店铺信息
        List<Ms201_client> clientInfoList = clientDao.getClientInfoList(clientIdList);
        // 转化为map key为店铺Id value 为店铺名称
        Map<String, String> clientMap =
            clientInfoList.stream().collect(Collectors.toMap(Ms201_client::getClient_id, Ms201_client::getClient_nm));

        // 获取到所有的商品Id
        List<String> productIdList =
            productStockList.stream().map(Tw300_stock::getProduct_id).distinct().collect(Collectors.toList());
        // 获取到所有商品在货架上面的信息
        List<Mw405_product_location> locationInfoList =
            stocksResultDao.getLocationByProductId(productIdList, warehouse_cd);
        Map<String, List<Mw405_product_location>> productLocationMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(locationInfoList) && locationInfoList.size() != 0) {
            // 根据 店铺Id_商品Id 进行分组转为map
            productLocationMap = locationInfoList.stream()
                .collect(Collectors.groupingBy(x -> x.getClient_id() + "_" + x.getProduct_id()));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Tw300_stock stock : productStockList) {
            // 商品对象
            Mc100_product mc100Product = stock.getMc100_product();
            // 店铺Id
            String clientId = stock.getClient_id();
            // 商品Id
            String productId = stock.getProduct_id();
            String key = clientId + "_" + productId;
            // 商品コード
            String code = mc100Product.getCode();
            // 管理バーコード
            String barcode = mc100Product.getBarcode();
            // 商品名
            String name = mc100Product.getName();
            // 店舗名
            String clientNm = clientMap.get(clientId);
            if (productLocationMap.containsKey(key)) {
                List<Mw405_product_location> productLocations = productLocationMap.get(key);
                for (Mw405_product_location productLocation : productLocations) {
                    Mw404_location mw404Location = productLocation.getMw404_location();
                    JSONObject json = new JSONObject();
                    // 店舗ID
                    json.put("client_id", clientId);
                    // 店舗名
                    json.put("client_nm", clientNm);
                    // 商品ID
                    json.put("product_id", productId);
                    // 商品コード
                    json.put("code", code);
                    // 商品名
                    json.put("name", name);
                    // 管理バーコード
                    json.put("barcode", barcode);
                    // ロケーション
                    json.put("wh_location_nm", mw404Location.getWh_location_nm());
                    // ロート番号
                    json.put("lot_no", mw404Location.getLot_no());
                    // 賞味期限/出荷期限
                    String bestBefore_date = "";
                    Date bestBeforeDate = productLocation.getBestbefore_date();
                    if (!StringTools.isNullOrEmpty(bestBeforeDate)) {
                        bestBefore_date = dateFormat.format(bestBeforeDate);
                    }
                    json.put("bestbefore_date", bestBefore_date);
                    // 实际在库数
                    int mw405StockCnt = productLocation.getStock_cnt();
                    json.put("mw405_stock_cnt", !StringTools.isNullOrEmpty(mw405StockCnt) ? mw405StockCnt : 0);
                    // 依赖中数
                    int mw405RequestingCnt = productLocation.getRequesting_cnt();
                    json.put("mw405_requesting_cnt",
                        !StringTools.isNullOrEmpty(mw405RequestingCnt) ? mw405RequestingCnt : 0);
                    // 不可配送数
                    int mw405NotDelivery = productLocation.getNot_delivery();
                    json.put("mw405_not_delivery", !StringTools.isNullOrEmpty(mw405NotDelivery) ? mw405NotDelivery : 0);
                    // フリー在庫数
                    json.put("delivery_cnt", Math.max(mw405StockCnt - mw405RequestingCnt - mw405NotDelivery, 0));
                    resultArray.add(json);
                }
            } else {
                // 错误数据 在库表存在该商品信息 但是货架上面没有该商品信息， 需要将货架信息出力为空
                JSONObject json = new JSONObject();
                // 店舗ID
                json.put("client_id", clientId);
                // 店舗名
                json.put("client_nm", clientNm);
                // 商品ID
                json.put("product_id", productId);
                // 商品コード
                json.put("code", code);
                // 商品名
                json.put("name", name);
                // 管理バーコード
                json.put("barcode", barcode);
                // ロケーション
                json.put("wh_location_nm", "");
                // ロート番号
                json.put("lot_no", "");
                // 賞味期限/出荷期限
                json.put("bestbefore_date", "");
                // 实际在库数
                json.put("mw405_stock_cnt", 0);
                // 依赖中数
                json.put("mw405_requesting_cnt", 0);
                // 不可配送数
                json.put("mw405_not_delivery", 0);
                // フリー在庫数
                json.put("delivery_cnt", 0);
                resultArray.add(json);
            }
        }
        return CommonUtils.success(resultArray);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @description: 获取最大货架Id
     * @return: java.lang.String
     * @date: 2021/7/14 14:01
     */
    public String getMaxLocationId(String warehouse_cd) {
        // a获取当前location_id的最大值
        List<String> location_id = stocksResultDao.getMaxLocationId(warehouse_cd);
        String maxLocationId = "1";
        if (location_id.size() != 0) {
            int[] i = new int[location_id.size()];
            for (int j = 0; j < i.length; j++) {
                int tmp = Integer.parseInt(location_id.get(j));
                i[j] = tmp;
            }
            Arrays.sort(i);
            maxLocationId = i[i.length - 1] + 1 + "";
        }
        return maxLocationId;
    }

}
