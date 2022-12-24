package com.lemonico.product.service;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.ProductSettingDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcBadRequestException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.bean.ProductRecord;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.ShipmentDetailService;
import com.lemonico.store.service.ShipmentsService;
import com.lemonico.wms.dao.ProductResultDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.service.ShipmentService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.*;
import java.nio.charset.Charset;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品マスタサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class ProductService
{
    private final static Logger logger = LoggerFactory.getLogger(ProductService.class);
    // a正则: 排除双引号内的逗号然后分割
    private final static String RULE = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    private final ShipmentsService shipmentsService;
    private final ShipmentDetailService shipmentDetailService;
    private final ShipmentService shipmentService;
    private final ProductDao productDao;
    private final WarehousingsDao warehousingsDao;
    private final ShipmentDetailDao shipmentDetailDao;
    private final ShipmentsDao shipmentsDao;
    private final WarehousingsDetailDao warehousingsDetailDao;
    private final ProductSettingDao productSettingDao;
    private final ProductResultDao productResultDao;
    private final StockDao stockDao;
    private final ClientDao clientDao;
    private final StocksResultDao stocksResultDao;
    private final PathProps pathProps;

    /**
     * @Param:
     * @description: 设定仓库侧商品size
     * @return:
     * @date: 2020/06/24
     */
    public Integer setProductSize(String warehouse_cd, String client_id, String product_id, Double weight,
        String size_cd) {
        return productResultDao.setProductSize(warehouse_cd, client_id, product_id, weight, size_cd);
    }

    /**
     * @Param:
     * @description: 获取商品货架信息
     * @return:
     * @date: 2020/06/25
     */
    public List<Mw405_product_location> getProductLocation(String warehouse_cd, String client_id, String product_id) {
        // 获取该商品的货架
        return productResultDao.getProductLocation(warehouse_cd, client_id, product_id);
    }

    /**
     * @Param:
     * @description: 商品货架移动
     * @return:
     * @date: 2020/06/25
     */
    @Transactional
    public Integer moveProductLocation(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        String strArr = JSONArray.toJSONString(jsonArray);
        List<LocationVO> list = JSONObject.parseArray(strArr, LocationVO.class);
        for (LocationVO locationVO : list) {
            // a前端选择不移动时跳过本次循环
            if ("-1".equals(locationVO.getTo_location())) {
                continue;
            }
            String client_id = locationVO.getClient_id();
            String product_id = locationVO.getProduct_id();
            String fromLocation_id = locationVO.getFrom_location();
            String toLocation_id = locationVO.getTo_location();
            int stock_cnt = locationVO.getCount();
            // a商品从货架移除
            productResultDao.moveProductLocation(client_id, product_id, fromLocation_id, -stock_cnt);
            Mw405_product_location mw405 = productResultDao.getProductLocationInfo(toLocation_id, client_id,
                product_id);
            if (mw405 != null) {
                // a商品从货架移入
                productResultDao.moveProductLocation(client_id, product_id, toLocation_id, stock_cnt);
            } else {
                // a移动到的货架没有该商品则新规数据
                productResultDao.createLocationProduct(client_id, product_id, toLocation_id, stock_cnt);
            }
        }

        return null;
    }

    /**
     * @throws Exception
     * @Param:
     * @description: 箱ラベル印刷
     * @return:
     * @date: 2020/06/23
     */
    public JSONObject itemsBoxLabelPDF(JSONObject jsonObject) {
        String dateMonth = DateUtils.getDateMonth();
        String client_id = jsonObject.getString("client_id");
        String pdfName = CommonUtils.getPdfName(client_id, "stock", "box", null);
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        JSONArray item = jsonObject.getJSONArray("item");
        // 查询该商品是否为 セット商品
        shipmentDetailService.verificationSetProduct(item);

        for (int i = 0; i < item.size(); i++) {
            JSONObject json = item.getJSONObject(i);
            String product_id = json.getString("product_id");

            // 查询该商品的在库数
            Tw300_stock stockInfoById = stockDao.getStockInfoById(json);
            Integer num = 0;
            if (!StringTools.isNullOrEmpty(stockInfoById)) {
                num = stockInfoById.getInventory_cnt() != null ? stockInfoById.getInventory_cnt() : 0;
            }
            json.put("num", num);
            // 查询商品名 和 code
            Mc100_product mc100Product = productDao.getNameByProductId(json.getString("client_id"), product_id);
            String name = mc100Product.getName();
            String code = mc100Product.getCode();
            String tmp = name;
            if (!StringTools.isNullOrEmpty(code)) {
                tmp = name + "(" + code + ")";
            }
            json.put("name", tmp);
            String codeName = json.getString("client_id") + "-" + product_id;
            String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
            json.put("codePath", codePath);
        }
        try {
            PdfTools.itemsBoxLabelPDF(item, pdfPath);
        } catch (Exception e) {
            logger.error("仓库侧商品master PDF失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.PDF_GENERATE_FAILED, e.getMessage());
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @Param: jsonObject
     * @description: 商品ラベル印刷
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/17
     */
    public JSONObject itemsLabelPDF(JSONObject jsonObject) {
        String dateMonth = DateUtils.getDateMonth();
        String warehousing_id = jsonObject.getString("id");
        String client_id = jsonObject.getString("client_id");
        String productId = jsonObject.getString("product_id");
        String shipment_plan_id = jsonObject.getString("shipment_plan_id");
        String number = jsonObject.getString("number");
        String print_flag = jsonObject.getString("print_flag");
        /**
         * flg ：前端传递的code印刷区分 1：仓库商品详细 2：仓库商品一览 3：仓库出库详细 4：仓库入库详细
         */
        String pdfName;
        Integer flg = jsonObject.getInteger("flg");
        switch (flg) {
            case 1:
                pdfName = CommonUtils.getPdfName(client_id, "warehousing", "product", productId);
                break;
            case 2:
                pdfName = CommonUtils.getPdfName(client_id, "stock", "glance_lable", null);
                break;
            case 3:
                pdfName = CommonUtils.getPdfName(client_id, "shipment", "lable", shipment_plan_id);
                break;
            case 4:
                if (!StringTools.isNullOrEmpty(productId)) {
                    pdfName = CommonUtils.getPdfName(client_id, "warehousing", "product", productId);
                    // 出库完了商品详细
                } else {
                    pdfName = CommonUtils.getPdfName(client_id, "warehousing", "any", warehousing_id);
                }
                break;
            default:
                pdfName = client_id + "-" + System.currentTimeMillis() + ".pdf";
                break;
        }
        String relativePath = pathProps.getWms() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        JSONArray item = jsonObject.getJSONArray("item");
        // 查询该商品是否为 セット商品
        // shipmentDetailService.verificationSetProduct(item);

        for (int i = 0; i < item.size(); i++) {
            JSONObject json = item.getJSONObject(i);
            String product_id = json.getString("product_id");
            String codeName = json.getString("client_id") + "-" + product_id;
            // 仓库商品一览打印，1.商品UID；2.商品code；3.商品barcode
            if (flg.equals(2)) {
                if ("2".equals(print_flag)) {
                    String product_code = json.getString("product_code");
                    if (!StringTools.isNullOrEmpty(product_code)) {
                        codeName = product_code;
                    }
                } else if ("3".equals(print_flag)) {
                    String product_barcode = json.getString("product_barcode");
                    if (!StringTools.isNullOrEmpty(product_barcode)) {
                        codeName = product_barcode;
                    }
                }
            }
            String codePath = pathProps.getRoot() + pathProps.getWms() + DateUtils.getDateMonth() + "/code/" + codeName;
            BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_2);
            json.put("codePath", codePath);
        }
        try {
            PdfTools.itemsLabelPDF(item, pdfPath, number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @param: client_id : 店铺Id
     * @param: warehouse_cd ： 仓库Id
     * @param: search ： 搜索内容
     * @param: stock_flg ： 在库状态
     * @param: set_flg
     * @description: 仓库商品一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/25
     */
    public JSONObject getItemList(String client_id, String warehouse_cd, String search, String stock_flg,
        Integer show_flg, Integer currentPage, Integer pageSize, Integer[] productDistinguish,
        String column, String sort) {

        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
            if (!StringTools.isNullOrEmpty(column)) {
                column = "mc100." + column;
            }
        }
        JSONObject resultJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        //
        // 商品种类 0: 通常商品 1: 同捆商品 2: set商品 9:假登录
        List<Integer> typeList = Arrays.asList(productDistinguish);
        List<Mc100_product> productList =
            productDao.getOperatingList(client_id, search, show_flg, Integer.parseInt(stock_flg),
                column, sortType, null, typeList, null, warehouse_cd);

        PageInfo<Mc100_product> pageInfo = null;
        long totalCnt = productList.size();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(productList);
            totalCnt = pageInfo.getTotal();
        }
        // 为空返回
        if (StringTools.isNullOrEmpty(productList) || productList.isEmpty()) {
            resultJson.put("result_data", jsonArray);
            resultJson.put("total", totalCnt);
            return CommonUtils.success(resultJson);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDay = DateUtils.getNowTime(dateFormat.format(calendar.getTime()) + " 00:00:00");
        Date nowTime = DateUtils.getNowTime(null);
        int type = 2;
        // 所有商品id
        List<String> product_id_list = productList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Mc100_product::getProduct_id).collect(Collectors.toList());
        // 根据产品id获取图片信息
        List<Mc102_product_img> product_img = productDao.getProductImgList(product_id_list, client_id);
        Map<String, List<Mc102_product_img>> product_img_map =
            product_img.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));
        // 找出所有数据店铺id
        List<String> clientIdList =
            productList.stream().map(Mc100_product::getClient_id).distinct().collect(Collectors.toList());
        Map<String, String> clientMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(clientIdList) && !clientIdList.isEmpty()) {
            // 獲取多個店鋪信息
            List<Ms201_client> clientInfoList = clientDao.getClientInfoList(clientIdList);
            clientMap = clientInfoList.stream()
                .collect(Collectors.toMap(Ms201_client::getClient_id, Ms201_client::getClient_nm));
        }

        List<String> productIds =
            productList.stream().map(Mc100_product::getProduct_id).distinct().collect(Collectors.toList());
        List<Tw301_stock_history> stockHistoryList =
            stocksResultDao.getShipmentNumSum(client_id, productIds, firstDay, nowTime, type);

        for (Mc100_product product : productList) {
            JSONObject jsonObject = new JSONObject();
            String product_id = product.getProduct_id();
            String clientId = product.getClient_id();
            jsonObject.put("product_id", product_id);
            jsonObject.put("client_id", clientId);
            // 获取店铺信息
            jsonObject.put("client_nm", clientMap.get(clientId));
            jsonObject.put("name", product.getName());
            jsonObject.put("bundled_flg", product.getBundled_flg());
            List<Mc102_product_img> img_list = product_img_map.get(product.getProduct_id());
            // 商品图像,一个商品可以有多个图像
            // String img = "";
            List<String> imgList = new ArrayList<>();
            if (!StringTools.isNullOrEmpty(img_list) && img_list.size() != 0) {
                // img = img_list.get(0).getProduct_img();
                imgList = img_list.stream().map(Mc102_product_img::getProduct_img).collect(Collectors.toList());
            }
            jsonObject.put("product_img", imgList);
            jsonObject.put("code", product.getCode());
            jsonObject.put("set_flg", product.getSet_flg());
            jsonObject.put("barcode", product.getBarcode());
            jsonObject.put("kubun", product.getKubun());
            jsonObject.put("size_cd", product.getSize_cd());
            jsonObject.put("weight", product.getWeight());
            // jsonObject.put("tw301_stock_history",product.getTw301_stock_history());

            int quantity = 0;
            for (Tw301_stock_history shipmentNumSum : stockHistoryList) {
                if (!StringTools.isNullOrEmpty(shipmentNumSum) && product_id.equals(shipmentNumSum.getProduct_id())) {
                    quantity += shipmentNumSum.getQuantity();
                }
            }
            jsonObject.put("quantity", quantity);

            List<Mw405_product_location> locations = stocksResultDao.getLocationInfo(clientId, product_id);
            JSONArray locationArray = new JSONArray();
            for (Mw405_product_location location : locations) {
                Mw404_location mw404Location = location.getMw404_location();
                JSONObject json = new JSONObject();
                json.put("lot_no", mw404Location.getLot_no());
                Date bestbefore_date = location.getBestbefore_date();
                String beforeDate = "";
                if (!StringTools.isNullOrEmpty(bestbefore_date)) {
                    beforeDate = dateFormat.format(bestbefore_date);
                }
                json.put("bestbefore_date", beforeDate);
                json.put("priority", mw404Location.getPriority());
                json.put("wh_location_nm", mw404Location.getWh_location_nm());
                if (!StringTools.isNullOrEmpty(mw404Location)) {
                    json.put("lot_no", mw404Location.getLot_no());
                }
                json.put("stock_cnt", location.getStock_cnt());
                json.put("status", location.getStatus());
                json.put("requesting_cnt", location.getRequesting_cnt());
                json.put("not_delivery", location.getNot_delivery());
                locationArray.add(json);
            }
            jsonObject.put("locationInfo", locationArray);

            if (product.getSet_flg() != 1) {
                Tw300_stock tw300_stock = product.getTw300_stock();
                if (!StringTools.isNullOrEmpty(tw300_stock)) {
                    Integer inventory_cnt = tw300_stock.getInventory_cnt();
                    Integer requesting_cnt = tw300_stock.getRequesting_cnt();
                    jsonObject.put("inventory_cnt", inventory_cnt);
                    int requestingNum = 0;
                    if (requesting_cnt != -1) {
                        requestingNum = requesting_cnt;
                    }
                    jsonObject.put("requesting_cnt", requestingNum);
                    int deliverable_cnt = 0;
                    int not_delivery = 0;
                    if (inventory_cnt != -1) {
                        not_delivery = tw300_stock.getNot_delivery();
                        deliverable_cnt = inventory_cnt - not_delivery - requestingNum;
                    }
                    jsonObject.put("deliverable_cnt", deliverable_cnt);
                    // 获取set子商品的不可配送数
                    jsonObject.put("not_delivery", not_delivery);
                    // 補充設定
                    jsonObject.put("replenish_cnt", tw300_stock.getReplenish_cnt());
                }
            } else {
                jsonObject.put("requesting_cnt", "-");
                jsonObject.put("deliverable_cnt", "-");
                jsonObject.put("not_delivery", "-");
                jsonObject.put("replenish_cnt", "-");
                List<Mc100_product> set_product_list = productDao.getProductSetList(product.getSet_sub_id(), clientId);
                if (set_product_list.size() == 0) {
                    continue;
                }

                int product_set_inventoryCnt = 0;
                JSONArray setArray = new JSONArray();

                // set商品信息
                int setProductNum = 0;
                int i = 1;
                for (Mc100_product product_set : set_product_list) {
                    String set_product_id = product_set.getProduct_id();

                    JSONObject json = new JSONObject();
                    // 获取到set商品子商品的在库数
                    Integer inventoryCnt = product_set.getTw300_stock().getInventory_cnt();
                    Integer requesting_cnt = product_set.getTw300_stock().getRequesting_cnt();
                    Integer not_delivery = product_set.getTw300_stock().getNot_delivery();

                    Double setNum = null;
                    if (product_set.getProduct_cnt() == 0) {
                        setNum = Math.floor((inventoryCnt - requesting_cnt));
                    } else {
                        setNum = Math.floor((inventoryCnt - requesting_cnt) / product_set.getProduct_cnt());
                    }
                    int set_num = setNum.intValue();
                    // 如果在库数存在， 并且小于上一个set商品的在库数，取最小值
                    if (i == 1) {
                        setProductNum = set_num;
                    } else {
                        if (setProductNum > set_num && setProductNum > 0) {
                            setProductNum = set_num;
                        }
                    }
                    json.put("client_id", clientId);
                    json.put("product_id", set_product_id);
                    json.put("code", product_set.getCode());
                    json.put("barcode", product_set.getBarcode());
                    json.put("name", product_set.getName());
                    List<Mc102_product_img> productImgList = product_set.getMc102_product_imgList();
                    // String setImg = "";
                    // set商品图像
                    List<String> setImgList = new ArrayList<>();
                    if (productImgList.size() != 0) {
                        // setImg = productImgList.get(0).getProduct_img();
                        setImgList =
                            productImgList.stream().map(Mc102_product_img::getProduct_img).collect(Collectors.toList());
                    }
                    json.put("setImg", setImgList);
                    int stockCnt = (inventoryCnt == -1) ? 0 : inventoryCnt;
                    int requestCnt = (requesting_cnt == -1) ? 0 : requesting_cnt;
                    json.put("inventoryCnt", stockCnt);
                    json.put("requesting_cnt", requestCnt);
                    int deliverable_cnt = stockCnt - requestCnt - product_set.getTw300_stock().getNot_delivery();
                    json.put("deliverable_cnt", deliverable_cnt);
                    // 获取set子商品的不可配送数
                    json.put("not_delivery", product_set.getTw300_stock().getNot_delivery());
                    setArray.add(json);
                    i++;
                }

                product_set_inventoryCnt = setProductNum;
                jsonObject.put("product_set_inventoryCnt", product_set_inventoryCnt);
                jsonObject.put("setArray", setArray);
            }
            jsonArray.add(jsonObject);
        }

        resultJson.put("result_data", jsonArray);
        resultJson.put("total", totalCnt);
        return CommonUtils.success(resultJson);
    }

    /**
     * @param: client_id
     * @param: product_id
     * @description: 获取商品图片路径
     * @return:
     * @date: 2021/4/1
     */
    public JSONObject getImagePath(String client_id, String product_id) {
        return CommonUtils.success(productResultDao.getImagePath(client_id, product_id));
    }

    /**
     * @param client_id : 店铺ID
     * @description: 根据店铺获取商品的code和商品Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/28 13:49
     */
    public JSONObject getProductCodeList(String client_id, String searchName) {
        if (!StringTools.isNullOrEmpty(searchName)) {
            searchName = "%" + searchName + "%";
        }
        // 根据店铺ID获取商品集合
        List<Mc100_product> productList = productDao.getProductByClientId(client_id, searchName);
        // List<String> codeList =
        // products.stream().map(Mc100_product::getCode).distinct().collect(Collectors.toList());
        List<HashMap<String, String>> mapList = new ArrayList<>();
        for (Mc100_product products : productList) {
            HashMap<String, String> hashMap = new LinkedHashMap<>();
            hashMap.put("code", products.getCode());
            hashMap.put("name", products.getName());
            hashMap.put("barcode", products.getBarcode());
            hashMap.put("kubun", products.getKubun().toString());
            mapList.add(hashMap);
        }
        return CommonUtils.success(mapList);
    }

    /**
     * @Description: 商品登録
     * @Param:
     * @return: Integer
     * @Date: 2020/08/11
     */
    public JSONObject insertProductMain(JSONObject jsonObject, HttpServletRequest httpServletRequest) {

        // aはフロントエンドのjsonデータを取得する
        // JSONObject jsonObject = createJson();
        String client_id = jsonObject.getString("client_id");
        // aは現在の時間を取得して、フォーマットしてyyyy-MM-dd HH:mm:ss になる
        Date date = DateUtils.getDate();
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        String isProductSet = jsonObject.getString("isProductSet");
        String login_nm = null;
        if (!StringTools.isNullOrEmpty(httpServletRequest)) {
            login_nm = CommonUtils.getToken("login_nm", httpServletRequest);
        }
        Integer maxSetSubId = productDao.getMaxSetSubId();
        int setSubId = (maxSetSubId == null) ? 1 : (maxSetSubId + 1);
        for (int k = 0; k < jsonArray.size(); k++) {
            JSONObject object = jsonArray.getJSONObject(k);
            JSONArray productSets = object.getJSONArray("mc103_product_sets");
            if (!StringTools.isNullOrEmpty(productSets)) {
                for (int i = 0; i < productSets.size(); i++) {
                    JSONObject setsJSONObject = productSets.getJSONObject(i);
                    setsJSONObject.remove("inventory_cnt");
                }
            }
            // 默认商品为普通商品
            int kubun = Constants.ORDINARY_PRODUCT;
            Mc100_product items = JSONObject.toJavaObject(object, Mc100_product.class);
            if ("1".equals(isProductSet)) {
                // set商品 区分设定为2
                kubun = Constants.SET_PRODUCT;
                items.setSet_sub_id(setSubId);
                items.setSet_flg(1);
                // insert セット商品明細マスタ
                JSONArray mc103_product_sets = object.getJSONArray("mc103_product_sets");
                // 如果含有set商品
                if (!StringTools.isNullOrEmpty(mc103_product_sets)) {
                    for (int j = 0; j < mc103_product_sets.size(); j++) {
                        JSONObject mc103_product_setsJSONObject = mc103_product_sets.getJSONObject(j);
                        Mc103_product_set mc103_product_set = new Mc103_product_set();
                        mc103_product_set.setSet_sub_id(setSubId);
                        mc103_product_set.setProduct_id(mc103_product_setsJSONObject.getString("product_id"));
                        mc103_product_set.setClient_id(client_id);
                        mc103_product_set
                            .setProduct_cnt(Integer.valueOf(mc103_product_setsJSONObject.getString("product_cnt")));
                        mc103_product_set.setIns_date(date);
                        mc103_product_set.setIns_usr(login_nm);
                        mc103_product_set.setUpd_date(date);
                        mc103_product_set.setUpd_usr(login_nm);
                        productDao.insertProductSet(mc103_product_set);
                    }
                }
            } else {
                items.setSet_flg(0);
            }
            String product_id = this.createProductId(client_id);
            // a登錄主表Mc100_product
            items.setClient_id(client_id);
            items.setProduct_id(product_id);
            // items.setBikou();
            items.setIns_date(date);
            items.setIns_usr(login_nm);
            items.setUpd_date(date);
            items.setUpd_usr(login_nm);
            // 備考
            items.setBikou(object.getString("bikou"));
            if (items.getBundled_flg() == null) {
                items.setBundled_flg(0);
            }

            if (items.getBundled_flg() == 1) {
                kubun = Constants.BUNDLED;
            }

            if (!StringTools.isNullOrEmpty(items.getKubun())) {
                kubun = items.getKubun();
            }

            items.setKubun(kubun);

            Integer show_flg = items.getShow_flg();
            if (StringTools.isNullOrEmpty(show_flg)) {
                items.setShow_flg(0);
            }
            // 获取店铺设定的税込・税抜信息
            Mc105_product_setting productSetting = productSettingDao.getProductSetting(client_id, null);
            if (!StringTools.isNullOrEmpty(productSetting) && !StringTools.isNullOrEmpty(items.getPrice())) {
                Integer tax_flag = items.getTax_flag();
                if (StringTools.isNullOrEmpty(tax_flag)) {
                    tax_flag = 0;
                }
                if (tax_flag == 1) {
                    Integer accordion = productSetting.getAccordion();
                    switch (accordion) {
                        case 0:
                            // 切り捨て
                            tax_flag = 10;
                            break;
                        case 1:
                            // 切り上げ
                            tax_flag = 11;
                            break;
                        case 2:
                            // 四捨五入
                            tax_flag = 12;
                            break;
                        default:
                            break;
                    }
                }
                items.setTax_flag(tax_flag);
            } else {
                items.setTax_flag(0);
            }
            // 商品登录 商品名为空时对应
            if (StringTools.isNullOrEmpty(items.getName())) {
                items.setName("ー");
            }
            productDao.insertProduct(items);

            // a登錄主表Mc101_product_tag
            String[] str = items.getTags();
            for (String s : str) {
                String tagId;
                int check = productDao.checkTagExist(s);
                // a判斷tag在表中是否存在
                // aはtagが表に存在するかどうかを判断する
                if (check != 0) {
                    // a如果存在则獲取相應tag的id
                    // aが存在すれば対応するtagのidを取得する
                    tagId = productDao.getTagIdByTagName(s);
                } else {
                    // a找不到既存tag則新規一條tag
                    // aは既存のtagが見つからなければ新規のtagを1つ選ぶ
                    Mc101_product_tag mc101_product_tag = new Mc101_product_tag();
                    tagId = this.createTagId();
                    mc101_product_tag.setTags_id(tagId);
                    mc101_product_tag.setTags(s);
                    mc101_product_tag.setIns_date(date);
                    mc101_product_tag.setIns_usr(login_nm);
                    mc101_product_tag.setUpd_date(date);
                    mc101_product_tag.setUpd_usr(login_nm);
                    productDao.insertProductTag(mc101_product_tag);
                }
                // a更新商品和tag关系表
                // aは商品とtagの関係テーブルを更新する
                Mc104_tag pro_tag = new Mc104_tag();
                pro_tag.setClient_id(client_id);
                pro_tag.setProduct_id(product_id);
                pro_tag.setTags_id(tagId);
                pro_tag.setUpd_usr(login_nm);
                pro_tag.setUpd_date(date);
                productDao.insertProductTagRelationship(pro_tag);
            }
            // a登录图片到商品图片表
            // aは商品画像テーブルに画像を登録する
            Mc102_product_img mc102_product_img = new Mc102_product_img();
            mc102_product_img.setClient_id(client_id);
            mc102_product_img.setProduct_id(product_id);
            JSONArray imgArr = object.getJSONArray("img");
            if (imgArr.size() > 0) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
                String nowTime = format.format(new Date());
                // 图片路径
                String uploadPath = pathProps.getImage() + client_id + "/" + nowTime + "/";

                for (int i = 0; i < imgArr.size(); i++) {
                    // a判断file数组元素是否为空
                    if (imgArr.get(i) != null) {
                        String imgRealPath = uploadPath + imgArr.get(i).toString();
                        // File file = new File(imgRealPath);
                        // if(!file.exists()){
                        // return CommonUtil.errorJson(ErrorEnum.E_10001);
                        // }
                        mc102_product_img.setProduct_img(imgRealPath);
                        mc102_product_img.setIns_date(date);
                        mc102_product_img.setIns_usr(login_nm);
                        mc102_product_img.setUpd_date(date);
                        mc102_product_img.setUpd_usr(login_nm);
                        productDao.insertProductImg(mc102_product_img);
                    }
                }
            }
            setSubId++;
        }
        return CommonUtils.success();
    }

    /**
     * @Description: 商品一览
     * @Param: 顧客ID， 商品ID,页数,每页数据量
     * @return: List
     * @Date: 2021/06/26
     */
    public List<ProductRecord> getSingleProductRecordList(String client_id, String warehouse_cd, String[] product_id,
        String search, String tags_id, Integer bundled_flg, String stock_flag, Integer set_flg, Integer show_flg,
        String count_flg, String stockShow, Integer currentPage, Integer pageSize, Integer[] productDistinguish) {

        // 商品种类 0: 通常商品 1: 同捆商品 2: set商品 9:假登录
        List<Integer> typeList = null;
        if (!StringTools.isNullOrEmpty(productDistinguish)) {
            typeList = Arrays.asList(productDistinguish);
        }
        boolean requestFlag;
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        ArrayList<ProductRecord> productRecordList = new ArrayList<>();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        // 查询商品信息
        List<Mc100_product> product_list =
            productDao.getProductRecordList(client_id, warehouse_cd, product_id, search, tags_id,
                typeList, stock_flag, set_flg, show_flg, count_flg, stockShow);
        // 查询商品的入库信息
        List<Tc101_warehousing_plan_detail> warehousingList = warehousingsDao.getWarehousingDetail(client_id, null);
        // 查询商品的出库信息
        List<Tw201_shipment_detail> shipmentList = shipmentDetailDao.getShipmentDetail(client_id, null);
        PageInfo<Mc100_product> pageInfo = null;

        Long totalCnt = null;
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(product_list);
            totalCnt = pageInfo.getTotal();
        }

        // 所有商品id
        List<String> product_id_list = product_list.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Mc100_product::getProduct_id).collect(Collectors.toList());
        // 根据产品id获取图片信息
        List<Mc102_product_img> product_img = productDao.getProductImgList(product_id_list, client_id);
        Map<String, List<Mc102_product_img>> product_img_map =
            product_img.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));

        for (Mc100_product mc100_product : product_list) {
            ProductRecord productRecord = new ProductRecord();
            requestFlag = true;
            List<Mc102_product_img> img_list = product_img_map.get(mc100_product.getProduct_id());
            productRecord.setProduct_id(mc100_product.getProduct_id());
            productRecord.setName(mc100_product.getName());
            productRecord.setCode(mc100_product.getCode());
            productRecord.setBarcode(mc100_product.getBarcode());
            productRecord.setPrice(mc100_product.getPrice());
            productRecord.setIs_reduced_tax(mc100_product.getIs_reduced_tax());
            productRecord.setTax_flag(mc100_product.getTax_flag());
            productRecord.setAvailable_cnt(mc100_product.getTw300_stock().getAvailable_cnt());
            productRecord.setInventory_cnt(mc100_product.getTw300_stock().getInventory_cnt());
            productRecord.setRequesting_cnt(mc100_product.getTw300_stock().getRequesting_cnt());
            productRecord.setNot_delivery(mc100_product.getTw300_stock().getNot_delivery());
            productRecord.setBundled_flg(mc100_product.getBundled_flg());
            productRecord.setCost_price(mc100_product.getCost_price());
            productRecord.setDescription_cd(mc100_product.getDescription_cd());
            productRecord.setSize_cd(mc100_product.getSize_cd());
            productRecord.setWeight(mc100_product.getWeight());
            productRecord.setOrigin(mc100_product.getOrigin());
            productRecord.setEnglish_name(mc100_product.getEnglish_name());
            // tag
            String[] product_ids = {
                mc100_product.getProduct_id()
            };
            List<Mc101_product_tag> getClientTags = getClientTags(client_id, product_ids);
            String tags = "";
            for (int i = 0; i < getClientTags.size(); i++) {
                if (!StringTools.isNullOrEmpty(getClientTags.get(i).getTags())) {
                    tags += getClientTags.get(i).getTags() + "/";
                }
            }
            if (tags != "") {
                tags = tags.substring(0, tags.length() - 1);
            }
            productRecord.setTags(tags);
            // QRコード
            productRecord.setUrl(mc100_product.getUrl());
            if (!StringTools.isNullOrEmpty(totalCnt)) {
                productRecord.setTotal_cont(totalCnt);
            }
            // 備考
            productRecord.setBikou(mc100_product.getBikou());

            if (!StringTools.isNullOrEmpty(img_list)) {
                String productImg = img_list.get(0).getProduct_img();
                productRecord.setProduct_img(Collections.singletonList(productImg));
            } else {
                productRecord.setProduct_img(Collections.singletonList(""));
            }

            // 判断是否在入库中
            for (Tc101_warehousing_plan_detail row : warehousingList) {
                if (mc100_product.getProduct_id().equals(row.getProduct_id())) {
                    requestFlag = false;
                    break;
                }
            }

            if (!requestFlag) {
                productRecord.setRequest_flag(requestFlag);
                productRecordList.add(productRecord);
                continue;
            }
            // 判断是否在出库中
            for (Tw201_shipment_detail row : shipmentList) {
                if (mc100_product.getProduct_id().equals(row.getProduct_id())) {
                    requestFlag = false;
                    break;
                }
            }

            productRecord.setRequest_flag(requestFlag);
            productRecordList.add(productRecord);
        }

        return productRecordList;
    }

    /**
     * @Description: set商品一览
     * @Param: 顧客ID， 商品ID,页数,每页数据量
     * @return: List
     * @Date: 2020/05/18
     */
    public List<Mc100_product> getSetProductList(String client_id, String warehouse_cd, String[] product_id,
        String search, String tags_id, Integer bundled_flg, String stock_flag, Integer set_flg, Integer show_flg,
        String count_flg, String stockShow) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        List<Mc100_product> list = productDao.getProductList(client_id, warehouse_cd, product_id, search, tags_id,
            bundled_flg, stock_flag, set_flg, show_flg, count_flg, stockShow, null);
        for (Mc100_product mc100_product : list) {
            // a获取每个商品的货架信息
            // List<Mw405_product_location> list_loc = productResultDao.getProductLocation(mc100_product.getClient_id(),
            // mc100_product.getProduct_id());
            // mc100_product.setProductLocationList(list_loc);
            List<Mc102_product_img> mc102ProductImgList = mc100_product.getMc102_product_imgList();

            // 获取画像1 、2 、3 放到mc100的对象中， 在画像批量上传页面 下载的CSV文件用到
            String img1 = null, img2 = null, img3 = null;
            if (mc102ProductImgList.size() > 0) {
                for (int i = 0; i < mc102ProductImgList.size(); i++) {
                    if (!StringTools.isNullOrEmpty(mc102ProductImgList.get(i).getProduct_img())) {
                        List<String> imgList = Splitter.on("/").omitEmptyStrings().trimResults()
                            .splitToList(mc102ProductImgList.get(i).getProduct_img());
                        if (i == 0) {
                            img1 = imgList.get(imgList.size() - 1);
                        }
                        if (i == 1) {
                            img2 = imgList.get(imgList.size() - 1);
                        }
                        if (i == 2) {
                            img3 = imgList.get(imgList.size() - 1);
                        }
                    }
                }
            }
            mc100_product.setImg1(img1);
            mc100_product.setImg2(img2);
            mc100_product.setImg3(img3);

            // set商品信息
            Integer setProductNum = 0;
            int i = 1;
            if (mc100_product.getMc103_product_sets().get(0).getProduct_id() != null
                && mc100_product.getSet_flg() == 1) {

                for (Mc103_product_set Mc103_product_set : mc100_product.getMc103_product_sets()) {
                    String id = Mc103_product_set.getClient_id();
                    String[] set_product_id = new String[1];
                    set_product_id[0] = Mc103_product_set.getProduct_id();
                    List<Mc100_product> set_list = productDao.getProductList(id, warehouse_cd, set_product_id, null,
                        null, 2, null, 0, 2, null, null, null);
                    if (set_list.size() != 0) {
                        // 获取到set商品子商品的在库数
                        Integer inventoryCnt = set_list.get(0).getTw300_stock().getInventory_cnt();
                        Integer requesting_cnt = set_list.get(0).getTw300_stock().getRequesting_cnt();
                        Double setNum = null;
                        if (Mc103_product_set.getProduct_cnt() == 0) {
                            setNum = Math.floor((inventoryCnt - requesting_cnt));
                        } else {
                            setNum = Math.floor((inventoryCnt - requesting_cnt) / Mc103_product_set.getProduct_cnt());
                        }
                        Integer set_num = setNum.intValue();
                        // 如果在库数存在， 并且小于上一个set商品的在库数，取最小值
                        if (i == 1) {
                            setProductNum = set_num;
                        } else {
                            if (setProductNum > set_num && setProductNum > 0) {
                                setProductNum = set_num;
                            }
                        }
                        Mc103_product_set.setName(set_list.get(0).getName());
                        Mc103_product_set.setCode(set_list.get(0).getCode());
                        Mc103_product_set.setPrice(set_list.get(0).getPrice());
                        Mc103_product_set.setIs_reduced_tax(set_list.get(0).getIs_reduced_tax());
                        Mc103_product_set.setAvailable_cnt(set_list.get(0).getTw300_stock().getAvailable_cnt());
                        Mc103_product_set.setInventory_cnt(set_list.get(0).getTw300_stock().getInventory_cnt());
                        Mc103_product_set.setRequesting_cnt(set_list.get(0).getTw300_stock().getRequesting_cnt());
                        List<Mc102_product_img> productImg = productDao.getProductImg(client_id,
                            Mc103_product_set.getProduct_id());
                        Mc103_product_set.setMc102_product_imgList(productImg);
                    }
                    i++;
                }
            }
            // 将set最小值存到商品对象中
            mc100_product.setProduct_set_inventoryCnt(setProductNum);

            // a是否该商品在出库或入库中
            // List<Tc101_warehousing_plan_detail> list1 = warehousingsDao
            // .getWarehousingDetail(mc100_product.getClient_id(), mc100_product.getProduct_id());
            // List<Tw201_shipment_detail> list2 = shipmentDetailDao.getShipmentDetail(mc100_product.getClient_id(),
            // mc100_product.getProduct_id());
            // if (list1.size() != 0 || list2.size() != 0) {
            // requestFlag = false;
            // }
            // mc100_product.setRequestFlag(requestFlag);

        }
        return list;
    }

    /**
     * @Description: set商品一览
     * @Param: 顧客ID， 商品ID,页数,每页数据量
     * @return: List
     * @Date: 2021/07/13
     */
    public JSONObject getSetProductRecordList(String client_id, String warehouse_cd, String[] product_id,
        String search, String tags_id, Integer bundled_flg, String stock_flag, Integer set_flg, Integer show_flg,
        String count_flg, String stockShow, Integer currentPage, Integer pageSize) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        JSONObject resultJson = new JSONObject();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        // List<Mc100_product> product_list = productDao.getProductRecordList(client_id, warehouse_cd, product_id,
        // search, tags_id,
        // bundled_flg, stock_flag, set_flg, show_flg, count_flg, stockShow);

        // TODO 采用新的sql 获得数据 kubun = 2 的为set商品
        List<Mc100_product> product_list = productDao.getOperatingList(client_id, search, show_flg, null, null, null,
            tags_id, Collections.singletonList(2), null, null);


        // 商品set_sub_id
        List<Integer> set_sub_id = product_list.stream().filter(x -> !StringTools.isNullOrEmpty(x.getSet_sub_id()))
            .map(Mc100_product::getSet_sub_id).collect(Collectors.toList());
        List<String> product_id_list = product_list.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Mc100_product::getProduct_id).collect(Collectors.toList());
        List<Mc103_product_set> set_product = productDao.getSetProductList(set_sub_id, client_id);
        Map<Integer, List<Mc103_product_set>> set_product_map =
            set_product.stream().collect(Collectors.groupingBy(Mc103_product_set::getSet_sub_id));
        // 去重整合id
        List<String> setProductIdList =
            set_product.stream().distinct().map(Mc103_product_set::getProduct_id).collect(Collectors.toList());
        product_id_list.addAll(setProductIdList);
        List<String> productIdList = product_id_list.stream().distinct().collect(Collectors.toList());

        // 根据产品id获取图片信息
        List<Mc102_product_img> product_img = productDao.getProductImgList(productIdList, client_id);
        Map<String, List<Mc102_product_img>> product_img_map =
            product_img.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));
        // Long totalCnt = productDao.getProductListCount(client_id, warehouse_cd, product_id, search, tags_id,
        // bundled_flg, stock_flag, set_flg, show_flg, count_flg, stockShow);
        PageInfo<Mc100_product> pageInfo = null;
        Long totalCnt = null;
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(product_list);
            totalCnt = pageInfo.getTotal();
        }
        ArrayList<Mc103_product_set> set_products = new ArrayList<>();

        JSONArray productObjects = new JSONArray();
        Integer key_id = 0;
        for (Mc100_product mc100_product : product_list) {
            key_id++;
            JSONObject productObject = new JSONObject();
            productObject.put("key_id", key_id);
            productObject.put("name", mc100_product.getName());
            productObject.put("product_id", mc100_product.getProduct_id());
            productObject.put("code", mc100_product.getCode());
            List<Mc102_product_img> product_imgs = product_img_map.get(mc100_product.getProduct_id());
            // String productImg = "";
            List<String> productImgSrcList = new ArrayList();
            if (!StringTools.isNullOrEmpty(product_imgs) && product_imgs.size() > 0) {
                if (!StringTools.isNullOrEmpty(product_imgs.get(0).getProduct_img())) {
                    // productImg = product_imgs.get(0).getProduct_img();
                    product_imgs.forEach(img -> {
                        productImgSrcList.add(img.getProduct_img());
                    });
                }
            }
            productObject.put("img", productImgSrcList);
            productObject.put("barcode", mc100_product.getBarcode());
            productObject.put("price", mc100_product.getPrice());
            productObject.put("is_reduced_tax", mc100_product.getIs_reduced_tax());
            productObject.put("tax_flag", mc100_product.getTax_flag());
            productObject.put("set_flg", mc100_product.getSet_flg());
            productObject.put("bikou", mc100_product.getBikou());
            productObject.put("cost_price", mc100_product.getCost_price());
            productObject.put("description_cd", mc100_product.getDescription_cd());
            productObject.put("english_name", mc100_product.getEnglish_name());
            productObject.put("origin", mc100_product.getOrigin());
            productObject.put("size_cd", mc100_product.getSize_cd());
            productObject.put("url", mc100_product.getUrl());
            productObject.put("weight", mc100_product.getWeight());
            productObject.put("set_sub_id", mc100_product.getSet_sub_id());
            // tag 通过 client_id 和 product_id 查询
            String[] product_ids = {
                mc100_product.getProduct_id()
            };
            List<Mc101_product_tag> getClientTags = getClientTags(client_id, product_ids);
            String tags = "";
            for (int i = 0; i < getClientTags.size(); i++) {
                if (!StringTools.isNullOrEmpty(getClientTags.get(i).getTags())) {
                    tags += getClientTags.get(i).getTags() + "/";
                }
            }
            if (tags != "") {
                tags = tags.substring(0, tags.length() - 1);
            }
            productObject.put("tags", tags);
            JSONArray setJsonArray = new JSONArray();

            List<Mc103_product_set> mc103_product_sets = set_product_map.get(mc100_product.getSet_sub_id());
            if (!StringTools.isNullOrEmpty(mc103_product_sets) && mc103_product_sets.size() > 0) {
                Integer key_ids = key_id;
                Integer set_key_id = 0;
                mc103_product_sets.forEach(x -> {
                    JSONObject setJson = new JSONObject();
                    String[] set_product_id = new String[1];
                    set_product_id[0] = x.getProduct_id();
                    List<Mc100_product> set_list = productDao.getProductList(x.getClient_id(), "", set_product_id, null,
                        null, 2, null, 0, 2, null, null, null);

                    Integer set_key_ids = set_key_id;
                    set_key_ids++;
                    setJson.put("key_id", key_ids.toString() + set_key_ids);
                    setJson.put("name", set_list.get(0).getName());
                    setJson.put("product_id", set_list.get(0).getProduct_id());
                    setJson.put("code", set_list.get(0).getCode());
                    setJson.put("barcode", set_list.get(0).getBarcode());
                    setJson.put("price", set_list.get(0).getPrice());
                    setJson.put("is_reduced_tax", set_list.get(0).getIs_reduced_tax());
                    setJson.put("tax_flag", set_list.get(0).getTax_flag());
                    setJson.put("product_cnt", x.getProduct_cnt());
                    setJson.put("set_flg", set_list.get(0).getSet_flg());
                    List<Mc102_product_img> mc102_product_imgs = product_img_map.get(x.getProduct_id());
                    // String img = "";
                    List<String> childProductImgSrcList = new ArrayList();
                    if (!StringTools.isNullOrEmpty(mc102_product_imgs) && mc102_product_imgs.size() > 0) {
                        if (!StringTools.isNullOrEmpty(mc102_product_imgs.get(0).getProduct_img())) {
                            // img = mc102_product_imgs.get(0).getProduct_img();
                            mc102_product_imgs.forEach(img -> {
                                childProductImgSrcList.add(img.getProduct_img());
                            });
                        }
                    }
                    setJson.put("img", childProductImgSrcList);
                    setJsonArray.add(setJson);
                });
            }
            productObject.put("mc103_product_sets", setJsonArray);

            productObjects.add(productObject);
        }
        resultJson.put("info", productObjects);
        resultJson.put("count", totalCnt);
        return resultJson;
    }

    /**
     * @param :client_id
     * @param :JSONObject
     * @Description: // 保存商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    public Integer setCorrespondingData(String client_id, JSONObject data, HttpServletRequest request) {
        Date date = DateUtils.getDate();
        String login_nm = CommonUtils.getToken("login_nm", request);

        String option_name1 = data.getString("option_name1").trim();
        String option_name2 = data.getString("option_name2").trim();
        String option_value1 = data.getString("option_value1").trim();
        String option_value2 = data.getString("option_value2").trim();

        Mc110_product_options mc110 = new Mc110_product_options();
        mc110.setClient_id(client_id);
        String id = data.getString("id");
        mc110.setShop_code(data.getString("shop_code"));
        mc110.setProduct_code(data.getString("product_code"));
        mc110.setSub_code(data.getString("sub_code"));
        mc110.setOption_name1(option_name1);
        mc110.setOption_name2(option_name2);
        mc110.setOption_value1(option_value1);
        mc110.setOption_value2(option_value2);
        mc110.setCode(data.getString("code"));

        StringBuilder tmpOptions = new StringBuilder();
        tmpOptions.append(option_name1 + ":");
        tmpOptions.append(option_value1 + ",");
        tmpOptions.append(option_name2 + ":");
        tmpOptions.append(option_value2);

        List<String> optionList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tmpOptions);
        String options = "";
        for (int i = 0; i < optionList.size(); i++) {
            if (":".equals(optionList.get(i))) {
                continue;
            }
            options += optionList.get(i) + ",";
        }

        if (!StringTools.isNullOrEmpty(options)) {
            mc110.setOptions(options.substring(0, options.length() - 1));
        }
        if (!StringTools.isNullOrEmpty(id)) {
            mc110.setId(Integer.parseInt(id));
            mc110.setUpd_date(date);
            mc110.setUpd_usr(login_nm);
            return productDao.updateCorrespondingData(mc110);
        } else {
            Integer newId = productDao.getCorrespondingMaxId();
            if (StringTools.isNullOrEmpty(newId)) {
                newId = 1;
            } else {
                newId += 1;
            }
            mc110.setId(newId);
            mc110.setIns_date(date);
            mc110.setIns_usr(login_nm);
            return productDao.setCorrespondingData(mc110);
        }
    }

    /**
     * @param client_id
     * @Description: 商品对应表CSV下载
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    public JSONObject getCorrespondingCsv(String client_id, String search) {
        JSONObject csvJson = this.getCorrespondingPaginationList(client_id, search, null, null);
        return CommonUtils.success(csvJson);
    }

    /**
     * @Description: // 获取商品对应数据 分页
     *               @Date： 2021/11/9
     * @Param：
     * @return：JSONObject
     */
    public JSONObject getCorrespondingPaginationList(String client_id, String search, Integer currentPage,
        Integer pageSize) {
        JSONObject resultJson = new JSONObject();
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        List<Mc110_product_options> product_options = productDao.getCorrespondingList(client_id, search);
        if (StringTools.isNullOrEmpty(product_options) || product_options.isEmpty()) {
            return resultJson;
        }
        PageInfo<Mc110_product_options> pageInfo = null;
        long totalCnt = product_options.size();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(product_options);
            totalCnt = pageInfo.getTotal();
        }
        // code列表
        Map<String, List<Mc100_product>> productMap = new HashMap<>();
        List<String> codeList =
            product_options.stream().map(Mc110_product_options::getCode).distinct().collect(Collectors.toList());
        List<Mc100_product> mc100Products = productDao.getProductListByCodeList(codeList, client_id);
        if (!StringTools.isNullOrEmpty(mc100Products) && !mc100Products.isEmpty()) {
            productMap = mc100Products.stream().collect(Collectors.groupingBy(Mc100_product::getCode));
        }
        JSONArray optionsArray = new JSONArray();
        // 重新构造JSONArray
        for (Mc110_product_options option : product_options) {
            String code = option.getCode();
            JSONObject optionObj = new JSONObject();
            optionObj.put("client_id", option.getClient_id());
            optionObj.put("code", code);
            optionObj.put("id", option.getId());
            optionObj.put("ins_date", option.getIns_date());
            optionObj.put("ins_usr", option.getIns_usr());
            optionObj.put("option_name1", option.getOption_name1());
            optionObj.put("option_name2", option.getOption_name2());
            optionObj.put("option_value1", option.getOption_value1());
            optionObj.put("option_value2", option.getOption_value2());
            optionObj.put("options", option.getOptions());
            optionObj.put("product_code", option.getProduct_code());
            optionObj.put("shop_code", option.getShop_code());
            optionObj.put("sub_code", option.getSub_code());
            optionObj.put("upd_date", option.getUpd_date());
            optionObj.put("upd_usr", option.getUpd_usr());
            // 如果有Mc100_product对象，kubun:9为假登录，其他商品存在；如果没有Mc100_product对象，则商品未登录
            optionObj.put("code_flg", "0");
            if (productMap.containsKey(code)) {
                List<Mc100_product> productList = productMap.get(code);
                if (!StringTools.isNullOrEmpty(productList) && !productList.isEmpty()) {
                    Integer kubun = productList.get(0).getKubun();
                    kubun = !StringTools.isNullOrEmpty(kubun) ? kubun : 1;
                    if (kubun == 9) {
                        optionObj.put("code_flg", "9");
                    } else {
                        optionObj.put("code_flg", "1");
                    }
                }
            }
            optionsArray.add(optionObj);
        }
        resultJson.put("list", optionsArray);
        resultJson.put("total", totalCnt);

        return resultJson;
    }

    /**
     * @param client_id
     * @param id
     * @Description: // 删除商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    public Integer delCorresponding(String client_id, Integer id) {
        return productDao.delCorresponding(client_id, id);
    }

    /**
     * @param client_id : 店铺Id
     * @param search : 搜索条件
     * @param page : 页数
     * @param size : 每页显示件数
     * @param showFlg : 非商品显示与否 0：不显示 1：显示
     * @param column : 需要排序的字段
     * @param sort : 排序的方式
     * @param stockFlg : 在库数判断 0：0個以下 1：1個以上 空：两个都没有选择
     * @description: 出库/入库依赖一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/18 16:12
     */
    public JSONObject getOperatingList(String client_id, String search, Integer page, Integer size,
        Integer[] productDistinguish,
        Integer showFlg, Integer stockFlg, String column, String sort, String tagsId, String fakeLoginFlg) {

        if (StringTools.isNullOrEmpty(productDistinguish) || productDistinguish.length == 0) {
            throw new LcBadRequestException("製品情報が空です。");
        }

        // 商品种类 0: 通常商品 1: 同捆商品 2: set商品 9:假登录
        List<Integer> typeList = Arrays.asList(productDistinguish);

        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
            if (!StringTools.isNullOrEmpty(column)) {
                column = "mc100." + column;
            }
        }
        // 返回的总数据
        JSONObject resultJson = new JSONObject();
        // 返回的商品信息
        JSONArray resultArray = new JSONArray();
        if (StringTools.isNullOrEmpty(stockFlg)) {
            throw new LcBadRequestException("製品情報が空です。");
        }

        // 查出满足条件的商品信息
        PageHelper.startPage(page, size);
        List<Mc100_product> operatingList =
            productDao.getOperatingList(client_id, search, showFlg, stockFlg, column, sortType, tagsId,
                typeList, null, null);
        PageInfo<Mc100_product> pageInfo = new PageInfo<>(operatingList);

        if (operatingList.isEmpty()) {
            throw new LcBadRequestException("製品情報が空です。");
        }

        // 获取到所有的setId
        List<Integer> setIdList =
            operatingList.stream().map(Mc100_product::getSet_sub_id).distinct().collect(Collectors.toList());
        // set主商品map
        Map<Integer, List<Mc103_product_set>> productSetMap = new HashMap<>();
        // set子商品map
        Map<String, Mc100_product> setProductMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(setIdList) && setIdList.size() != 0) {
            // 获取到所有的set商品 并根据set_sub_id 进行分组
            List<Mc103_product_set> productSets = productDao.getSetProductBySubIdList(setIdList, client_id);
            // 获取到所有set子商品的Id
            List<String> setProductIdLIst =
                productSets.stream().map(Mc103_product_set::getProduct_id).distinct().collect(Collectors.toList());
            // set主商品 按照set_sub_id 进行分组
            productSetMap = productSets.stream().collect(Collectors.groupingBy(Mc103_product_set::getSet_sub_id));

            // 获取到所有子商品的信息
            List<Mc100_product> setProductList = productDao.getOperatingList(client_id, null, null, null,
                null, null, null, null, setProductIdLIst, null);
            // 将set子商品转为map
            setProductMap = setProductList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
                .collect(Collectors.toMap(Mc100_product::getProduct_id, o -> o));

        }

        // 获取到商品Id集合
        List<String> productIdList =
            operatingList.stream().map(Mc100_product::getProduct_id).distinct().collect(Collectors.toList());
        // 假登录商品获取出库详细
        Map<String, List<Tw201_shipment_detail>> shipmentdetailMap = new HashMap<>();
        List<Tw200_shipment> planInfoList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(fakeLoginFlg) && "1".equals(fakeLoginFlg)) {
            List<Tw201_shipment_detail> shipmentdetailList =
                shipmentsDao.getShipmentdetailProductId(client_id, productIdList);
            shipmentdetailMap =
                shipmentdetailList.stream().collect(Collectors.groupingBy(Tw201_shipment_detail::getProduct_id));

            // 获取出库List
            List<String> planIdList = shipmentdetailList.stream().map(Tw201_shipment_detail::getShipment_plan_id)
                .distinct().collect(Collectors.toList());
            planInfoList = shipmentsDao.getShipmentInfoList(client_id, planIdList);
        }

        // 不能删除的商品Id集合
        ArrayList<String> canTDeleteProductIdList = new ArrayList<>();

        // 获取到所有商品中 包含的入库详细信息
        List<Tc101_warehousing_plan_detail> warehousingPlanDetails =
            warehousingsDetailDao.getWarehouseDetailByProductId(client_id, productIdList);

        if (!warehousingPlanDetails.isEmpty()) {
            canTDeleteProductIdList.addAll(warehousingPlanDetails.stream()
                .map(Tc101_warehousing_plan_detail::getProduct_id).distinct().collect(Collectors.toList()));
        }

        // 获取到所有商品中 包含的出库详细信息
        List<Tw201_shipment_detail> shipmentDetails =
            shipmentDetailDao.getShipmentDetailByProductId(client_id, productIdList);
        if (!shipmentDetails.isEmpty()) {
            canTDeleteProductIdList.addAll(shipmentDetails.stream().map(Tw201_shipment_detail::getProduct_id).distinct()
                .collect(Collectors.toList()));
        }

        // 获取到所有商品对应的图片信息 并按照商品Id 进行分组
        List<Mc102_product_img> productImgList = productDao.getProductImgList(productIdList, client_id);
        Map<String, List<Mc102_product_img>> productImgMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(productImgList) && productImgList.size() != 0) {
            productImgMap = productImgList.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));
        }

        for (Mc100_product product : operatingList) {
            JSONObject json = new JSONObject();

            // 商品ID
            String productId = product.getProduct_id();
            List<JSONObject> jsonObjects = new ArrayList<>();
            // 如果是假登录一括処理查询，则查询假登录的出库信息
            if (!StringTools.isNullOrEmpty(fakeLoginFlg) && "1".equals(fakeLoginFlg)) {
                if (!StringTools.isNullOrEmpty(shipmentdetailMap)) {
                    for (Map.Entry<String, List<Tw201_shipment_detail>> entry : shipmentdetailMap.entrySet()) {
                        if (!productId.equals(entry.getKey())) {
                            continue;
                        }
                        List<Tw201_shipment_detail> detailList = entry.getValue();
                        for (Tw201_shipment_detail detail : detailList) {
                            JSONObject result = new JSONObject();
                            for (Tw200_shipment shipment : planInfoList) {
                                if (detail.getShipment_plan_id().equals(shipment.getShipment_plan_id())) {
                                    result.put("shipment_status", shipment.getShipment_status());
                                    Timestamp request_date = shipment.getRequest_date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String date = "";
                                    if (!StringTools.isNullOrEmpty(request_date)) {
                                        date = format.format(request_date);
                                    }
                                    result.put("request_date", date);
                                    break;
                                }
                            }
                            result.put("shipment_plan_id", detail.getShipment_plan_id());
                            result.put("product_plan_cnt", detail.getProduct_plan_cnt());
                            jsonObjects.add(result);
                        }
                    }
                }
            }
            json.put("shipmentInfo", jsonObjects);
            json.put("product_id", productId);
            // セット商品フラグ:0：無 1:有
            Integer productSetFlg = product.getSet_flg();
            json.put("set_flg", productSetFlg);
            json.put("bundled_flg", product.getBundled_flg());
            // 店舗ID
            json.put("warehouse_cd", product.getWarehouse_cd());
            json.put("client_id", product.getClient_id());
            // 商品コード
            json.put("code", product.getCode());
            // 管理バーコード
            json.put("barcode", product.getBarcode());
            // 商品名
            json.put("name", product.getName());
            json.put("tax_flag", product.getTax_flag());
            json.put("is_reduced_tax", product.getIs_reduced_tax());
            json.put("kubun", product.getKubun());
            json.put("set_sub_id", product.getSet_sub_id());
            json.put("description_cd", product.getDescription_cd());

            boolean requestFlag = canTDeleteProductIdList.contains(productId);
            // 该商品 已经出库或者入库 则不能删除

            // 查询该商品是否为セット商品 的子商品
            List<Mc103_product_set> mc103_product_set =
                productDao.verificationSetProduct(client_id, product.getProduct_id());
            if (!StringTools.isNullOrEmpty(mc103_product_set) && mc103_product_set.size() > 0) {
                requestFlag = true;
            }
            json.put("request_flag", requestFlag);

            // String productImg = null;
            List<String> productImgSrcList = new ArrayList();
            if (!productImgMap.isEmpty() && productImgMap.containsKey(productId)) {
                List<Mc102_product_img> mc102ProductImgList = productImgMap.get(productId);
                // productImg = mc102ProductImgList.get(0).getProduct_img();
                mc102ProductImgList.forEach(img -> {
                    productImgSrcList.add(img.getProduct_img());
                });
            }
            json.put("product_img", productImgSrcList);
            // 商品価格税込
            json.put("price", product.getPrice());
            JSONArray setArray = new JSONArray();
            if (productSetFlg == 1) {
                // 为set商品 需要获取其子商品
                Integer setSubId = product.getSet_sub_id();
                if (!productSetMap.isEmpty() && productSetMap.containsKey(setSubId)) {
                    // 获取到该商品对应 的set子商品
                    List<Mc103_product_set> mc103ProductSets = productSetMap.get(setSubId);
                    for (Mc103_product_set productSet : mc103ProductSets) {
                        JSONObject setJson = new JSONObject();
                        String setProductId = productSet.getProduct_id();
                        if (setProductMap.containsKey(setProductId)) {
                            Mc100_product mc100Product = setProductMap.get(setProductId);
                            Tw300_stock tw300Stock = mc100Product.getTw300_stock();
                            // 商品ID
                            setJson.put("product_id", setProductId);
                            // 商品名
                            setJson.put("name", mc100Product.getName());
                            // 商品コード
                            setJson.put("code", mc100Product.getCode());
                            // 管理バーコード
                            setJson.put("barcode", mc100Product.getBarcode());
                            // 商品価格税込
                            setJson.put("price", mc100Product.getPrice());
                            // 実在庫数
                            setJson.put("inventory_cnt", tw300Stock.getInventory_cnt());
                            // 理论在库数
                            Integer availableCnt = tw300Stock.getAvailable_cnt();
                            setJson.put("available_cnt", availableCnt == -1 ? 0 : availableCnt);
                            // 出庫依頼中数
                            Integer requestingCnt = tw300Stock.getRequesting_cnt();
                            setJson.put("requesting_cnt", requestingCnt == -1 ? 0 : requestingCnt);
                            // 不可配送数
                            setJson.put("not_delivery", tw300Stock.getNot_delivery());
                            setJson.put("product_cnt", productSet.getProduct_cnt());

                            Integer inventoryCnt = tw300Stock.getInventory_cnt();
                            // Integer requesting_cnt = tw300Stock.getRequesting_cnt();
                            // Integer not_delivery = tw300Stock.getNot_delivery();
                            // int stockCnt = (inventoryCnt == -1) ? 0 : inventoryCnt;
                            // int requestCnt = (requesting_cnt == -1) ? 0 : requesting_cnt;
                            // int deliverable_cnt = stockCnt - requestCnt - not_delivery;
                            // setJson.put("deliverable_cnt", deliverable_cnt);
                            setJson.put("inventoryCnt", inventoryCnt == -1 ? 0 : inventoryCnt);
                            // 商品图片
                            // String setProductImg = null;
                            List<String> setProductImgSrcList = new ArrayList();
                            if (!productImgMap.isEmpty() && productImgMap.containsKey(setProductId)) {
                                List<Mc102_product_img> mc102ProductImgList = productImgMap.get(setProductId);
                                // setProductImg = mc102ProductImgList.get(0).getProduct_img();
                                mc102ProductImgList.forEach(img -> {
                                    setProductImgSrcList.add(img.getProduct_img());
                                });
                            }
                            setJson.put("setProductImg", setProductImgSrcList);

                            setArray.add(setJson);
                        }
                    }
                }
            } else {
                Tw300_stock tw300Stock = product.getTw300_stock();
                // 実在庫数
                json.put("inventory_cnt", tw300Stock.getInventory_cnt());
                // 理论在库数
                json.put("available_cnt", tw300Stock.getAvailable_cnt());
                // 出庫依頼中数
                json.put("requesting_cnt", tw300Stock.getRequesting_cnt());
                // 不可配送数
                json.put("not_delivery", tw300Stock.getNot_delivery());
            }
            json.put("setArray", setArray);
            resultArray.add(json);
        }

        // 商品信息
        resultJson.put("result", resultArray);
        // 商品总件数
        resultJson.put("total", pageInfo.getTotal());
        return CommonUtils.success(resultJson);
    }

    /**
     * @param client_id : 店铺Id
     * @param kubuns : 商品区分数组 0普通商品 1同捆物
     * @description: 获取入库csv模板数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/11 10:56
     */
    public JSONObject getWarehouseCsvData(String client_id, int[] kubuns) {
        List<Mc100_product> warehouseCsvData;
        try {
            warehouseCsvData = productDao.getWarehouseCsvData(client_id, kubuns);
        } catch (Exception e) {
            logger.error("获取入库csv模板数据失败, 店铺Id={}", client_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success(warehouseCsvData);
    }

    /**
     * @Description: 商品一览
     * @Param: 顧客ID， 商品ID,页数,每页数据量
     * @return: List
     * @Date: 2020/05/18
     */
    public List<Mc100_product> getProductList(String client_id, String warehouse_cd, String[] product_id, String search,
        String tags_id, Integer bundled_flg, String stock_flag, Integer set_flg, Integer show_flg, String count_flg,
        String stockShow, int[] kubuns) {

        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        List<Mc100_product> list = productDao.getProductList(client_id, warehouse_cd, product_id, search, tags_id,
            bundled_flg, stock_flag, set_flg, show_flg, count_flg, stockShow, kubuns);
        for (Mc100_product mc100_product : list) {
            // a获取每个商品的货架信息
            // List<Mw405_product_location> list_loc = productResultDao.getProductLocation(mc100_product.getClient_id(),
            // mc100_product.getProduct_id());
            // mc100_product.setProductLocationList(list_loc);
            List<Mc102_product_img> mc102ProductImgList = mc100_product.getMc102_product_imgList();

            // 获取画像1 、2 、3 放到mc100的对象中， 在画像批量上传页面 下载的CSV文件用到
            String img1 = null, img2 = null, img3 = null;
            if (mc102ProductImgList.size() > 0) {
                for (int i = 0; i < mc102ProductImgList.size(); i++) {
                    if (!StringTools.isNullOrEmpty(mc102ProductImgList.get(i).getProduct_img())) {
                        List<String> imgList = Splitter.on("/").omitEmptyStrings().trimResults()
                            .splitToList(mc102ProductImgList.get(i).getProduct_img());
                        if (i == 0) {
                            img1 = imgList.get(imgList.size() - 1);
                        }
                        if (i == 1) {
                            img2 = imgList.get(imgList.size() - 1);
                        }
                        if (i == 2) {
                            img3 = imgList.get(imgList.size() - 1);
                        }
                    }
                }
            }
            mc100_product.setImg1(img1);
            mc100_product.setImg2(img2);
            mc100_product.setImg3(img3);

            // set商品信息
            int setProductNum = 0;
            int i = 1;
            if (mc100_product.getMc103_product_sets().get(0).getProduct_id() != null
                && mc100_product.getSet_flg() == 1) {

                for (Mc103_product_set Mc103_product_set : mc100_product.getMc103_product_sets()) {
                    String id = Mc103_product_set.getClient_id();
                    String[] set_product_id = new String[1];
                    set_product_id[0] = Mc103_product_set.getProduct_id();
                    List<Mc100_product> set_list = productDao.getProductList(id, warehouse_cd, set_product_id, null,
                        null, 2, null, 0, show_flg, null, null, null);
                    if (set_list.size() != 0) {
                        // 获取到set商品子商品的在库数
                        Integer inventoryCnt = set_list.get(0).getTw300_stock().getInventory_cnt();
                        Integer requesting_cnt = set_list.get(0).getTw300_stock().getRequesting_cnt();
                        Integer not_delivery = set_list.get(0).getTw300_stock().getNot_delivery();
                        Double setNum = null;
                        if (Mc103_product_set.getProduct_cnt() == 0) {
                            setNum = Math.floor((inventoryCnt - requesting_cnt - not_delivery));
                        } else {
                            setNum = Math.floor(
                                (inventoryCnt - requesting_cnt - not_delivery) / Mc103_product_set.getProduct_cnt());
                        }
                        Integer set_num = setNum.intValue();
                        // 如果在库数存在， 并且小于上一个set商品的在库数，取最小值
                        if (i == 1) {
                            setProductNum = set_num;
                        } else {
                            if (setProductNum > set_num && setProductNum > 0) {
                                setProductNum = set_num;
                            }
                        }
                        Mc103_product_set.setName(set_list.get(0).getName());
                        Mc103_product_set.setCode(set_list.get(0).getCode());
                        Mc103_product_set.setBarcode(set_list.get(0).getBarcode());
                        Mc103_product_set.setPrice(set_list.get(0).getPrice());
                        Mc103_product_set.setIs_reduced_tax(set_list.get(0).getIs_reduced_tax());
                        Mc103_product_set.setAvailable_cnt(set_list.get(0).getTw300_stock().getAvailable_cnt());
                        Mc103_product_set.setInventory_cnt(set_list.get(0).getTw300_stock().getInventory_cnt());
                        Mc103_product_set.setRequesting_cnt(set_list.get(0).getTw300_stock().getRequesting_cnt());
                        Mc103_product_set.setNot_delivery(set_list.get(0).getTw300_stock().getNot_delivery());
                        List<Mc102_product_img> productImg = productDao.getProductImg(client_id,
                            Mc103_product_set.getProduct_id());
                        Mc103_product_set.setMc102_product_imgList(productImg);
                    }
                    i++;
                }
            }
            // 将set最小值存到商品对象中
            mc100_product.setProduct_set_inventoryCnt(setProductNum);

            // a是否该商品在出库或入库中
            // List<Tc101_warehousing_plan_detail> list1 =
            // warehousingsDao.getWarehousingDetail(mc100_product.getClient_id(), mc100_product.getProduct_id());
            // List<Tw201_shipment_detail> list2 = shipmentDetailDao.getShipmentDetail(mc100_product.getClient_id(),
            // mc100_product.getProduct_id());
            // if (list1.size() != 0 || list2.size() != 0) {
            // requestFlag = false;
            // }
            // mc100_product.setRequestFlag(requestFlag);
        }


        return list;
    }

    /**
     * @Description: 根据顾客ID和商品ID获取tags_id的值
     * @Param: 顧客ID， 商品ID
     * @return: List
     * @Date: 2020/05/18
     */
    public List<Mc101_product_tag> getClientTags(String client_id, String[] product_id) {
        return productDao.getClientTags(client_id, product_id);
    }

    /**
     * @Description: 商品更新
     * @Param: Mc100_product
     * @return: Integer
     * @Date: 2020/05/18
     */
    @Transactional
    public JSONObject updateProductMain(String client_id, String edit_flg, String old_code, String product_id,
        JSONObject jsonObject,
        HttpServletRequest httpServletRequest) {
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        // a获取当前时间并格式化为yyyy-MM-dd HH:mm:ss
        // aは現在の時間を取得して、フォーマットしてyyyy-MM-dd HH:mm:ss になる
        Date date = DateUtils.getDate();
        String login_nm = "admin";
        if (!StringTools.isNullOrEmpty(httpServletRequest)) {
            login_nm = CommonUtils.getToken("login_nm", httpServletRequest);
        }
        Mc100_product item_list = JSONObject.toJavaObject(jsonArray.getJSONObject(0), Mc100_product.class);
        String code = item_list.getCode();
        // 如果edit_flg存在，并且为1，则修改商品对应表
        if (!StringTools.isNullOrEmpty(edit_flg) && "1".equals(edit_flg)) {
            productDao.updateCorrespondence(client_id, code, old_code, login_nm, date);
        }
        for (int k = 0; k < jsonArray.size(); k++) {
            JSONObject object = jsonArray.getJSONObject(k);
            Mc100_product items = JSONObject.toJavaObject(object, Mc100_product.class);
            // a更新主表Mc100_product
            items.setClient_id(client_id);
            items.setProduct_id(product_id);
            items.setUpd_date(date);
            items.setUpd_usr(login_nm);

            // 获取店铺设定的税込・税抜信息
            Mc105_product_setting productSetting = productSettingDao.getProductSetting(client_id, null);

            Integer show_flg = items.getShow_flg();
            if (!StringTools.isNullOrEmpty(show_flg) && show_flg == 1) {
                items.setShow_flg(1);
            } else {
                items.setShow_flg(0);
            }
            Integer eccube_show_flg = items.getEccube_show_flg();
            if (!StringTools.isNullOrEmpty(eccube_show_flg) && eccube_show_flg == 1) {
                items.setEccube_show_flg(1);
            } else {
                items.setEccube_show_flg(0);
            }

            if (!StringTools.isNullOrEmpty(productSetting)) {
                Integer tax_flag = items.getTax_flag();
                if (StringTools.isNullOrEmpty(tax_flag)) {
                    tax_flag = 0;
                }
                if (tax_flag == 1) {
                    Integer accordion = productSetting.getAccordion();
                    switch (accordion) {
                        case 0:
                            // 切り捨て
                            tax_flag = 10;
                            break;
                        case 1:
                            // 切り上げ
                            tax_flag = 11;
                            break;
                        case 2:
                            // 四捨五入
                            tax_flag = 12;
                            break;
                        default:
                            break;
                    }
                }
                items.setTax_flag(tax_flag);
            }
            productDao.updateProduct(items);
            String oldKubun = object.getString("oldKubun");
            if (!StringTools.isNullOrEmpty(oldKubun) && Integer.parseInt(oldKubun) == Constants.NOT_LOGGED_PRODUCT
                && "0".equals(object.getString("kubun"))) {
                // 商品之前的 区分为假登录 并且现在的区分为普通商品 需要更改出库信息
                int status = 1;
                shipmentsService.shipmentInsertProduct(object, httpServletRequest, status);
            }

            String set_sub_id = object.getString("set_sub_id");
            JSONArray mc103_product_sets = object.getJSONArray("mc103_product_sets");
            String isProductSet = jsonObject.getString("isProductSet");
            if ("1".equals(isProductSet)) {
                ArrayList<String> setProductId = new ArrayList<>();
                JSONArray list1 = jsonObject.getJSONArray("setProductList");
                for (int i = 0; i < list1.size(); i++) {
                    setProductId.add(list1.getJSONObject(i).getString("product_id"));
                }
                ArrayList<String> arrayList = new ArrayList<>();
                List<Mc103_product_set> setProductIdList = productDao.getSetProductIdList(Integer.valueOf(set_sub_id),
                    object);
                for (Mc103_product_set productSet : setProductIdList) {
                    arrayList.add(productSet.getProduct_id());
                }
                // 获得哪些原有的set_product被删除了
                List<String> existSetProduct = arrayList.stream().filter(item -> !setProductId.contains(item))
                    .collect(Collectors.toList());
                if (existSetProduct.size() != 0) {
                    existSetProduct.stream().forEach(productId -> productDao
                        .deleteSetProduct(Integer.valueOf(set_sub_id), productId, object.getString("client_id")));
                }
                // 修改没有被删除原有的set_product商品个数
                List<String> repeatProductId = arrayList.stream().filter(item -> setProductId.contains(item))
                    .collect(Collectors.toList());
                if (repeatProductId.size() != 0) {
                    repeatProductId.stream().forEach(productId -> {
                        for (int i = 0; i < mc103_product_sets.size(); i++) {
                            if (mc103_product_sets.getJSONObject(i).getString("product_id").equals(productId)) {
                                productDao.updateSetProductNumber(Integer.valueOf(set_sub_id), productId,
                                    object.getString("client_id"),
                                    Integer.valueOf(mc103_product_sets.getJSONObject(i).getString("product_cnt")));
                            }
                        }
                    });
                }
                // 添加新选择的set_product
                List<String> newSetProductId = setProductId.stream().filter(item -> !arrayList.contains(item))
                    .collect(Collectors.toList());
                if (newSetProductId.size() != 0) {
                    newSetProductId.stream().forEach(productId -> {
                        for (int i = 0; i < mc103_product_sets.size(); i++) {
                            if (mc103_product_sets.getJSONObject(i).getString("product_id").equals(productId)) {
                                Mc103_product_set productSet = new Mc103_product_set();
                                productSet.setClient_id(object.getString("client_id"));
                                productSet.setProduct_id(productId);
                                productSet.setSet_sub_id(Integer.valueOf(set_sub_id));
                                productSet.setProduct_cnt(
                                    Integer.valueOf(mc103_product_sets.getJSONObject(i).getString("product_cnt")));
                                productDao.insertProductSet(productSet);
                            }
                        }
                    });
                } else if (newSetProductId.size() == 0 && setProductId.size() != 0) {
                    // 商品表中的被删除的子商品在编辑时选择，恢复子商品
                    for (int i = 0; i < setProductId.size(); i++) {
                        productDao.recoverSetProduct(Integer.valueOf(set_sub_id), setProductId.get(i), client_id);
                    }
                }
            }

            // a更新tag表Mc101_product_tag
            String[] str = {};
            if (!StringTools.isNullOrEmpty(items.getTags())) {
                str = items.getTags();
            }
            // a商品tag更新前先删除商品的tag关系
            // a商品はtag更新する前に商品のtag関係を削除する
            productDao.deleteProductTag(client_id, product_id);
            for (String s : str) {
                String togId;
                int check = productDao.checkTagExist(s);
                // a判斷tag在表中是否存在
                // aはtagが表に存在するかどうかを判断する
                if (check != 0) {
                    // a如果存在则獲取相應tag的id
                    // aが存在すれば対応するtagのidを取得する
                    togId = productDao.getTagIdByTagName(s);
                } else {
                    // a找不到既存tag則新規一條tag
                    // aは既存のtagが見つからなければ新規のtagを1つ選ぶ
                    Mc101_product_tag mc101_product_tag = new Mc101_product_tag();
                    togId = this.createTagId();
                    mc101_product_tag.setTags_id(togId);
                    mc101_product_tag.setTags(s);
                    productDao.insertProductTag(mc101_product_tag);
                }
                // a更新商品和tag关系表
                // aは商品とtagの関係テーブルを更新する
                Mc104_tag pro_tag = new Mc104_tag();
                pro_tag.setClient_id(client_id);
                pro_tag.setProduct_id(product_id);
                pro_tag.setTags_id(togId);
                productDao.insertProductTagRelationship(pro_tag);
            }
            // a商品图片更新前先删除该商品的图片路径
            productDao.productImgDelete(client_id, product_id);
            // a登录图片到商品图片
            Mc102_product_img mc102_product_img = new Mc102_product_img();
            mc102_product_img.setClient_id(client_id);
            mc102_product_img.setProduct_id(product_id);
            JSONArray imgArr = object.getJSONArray("img");
            if (imgArr != null && imgArr.size() > 0) {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
                String nowTime = format.format(new Date());
                // a图片路径
                String uploadPath = pathProps.getImage() + client_id + "/" + nowTime + "/";
                List<Mc102_product_img> productImg = productDao.getProductImg(client_id, product_id);
                if (productImg.size() > 0) {
                    System.out.println(productImg);
                }

                for (int i = 0; i < imgArr.size(); i++) {
                    // // a判断file数组元素是否为空
                    // if (imgArr.getJSONObject(i).get("flg") == "true") {
                    // continue;
                    // }
                    if (imgArr.get(i) != null) {
                        String[] arr = imgArr.get(i).toString().split("/");
                        String imgOldPath = imgArr.get(i).toString();
                        String oldImg = pathProps.getRoot() + imgOldPath;
                        String imgRealPath = uploadPath + arr[arr.length - 1];
                        String realImg = pathProps.getRoot() + imgRealPath;
                        File file = new File(realImg);
                        if (!file.exists()) {
                            File file2 = new File(oldImg);
                            if (file2.exists()) {
                                imgRealPath = imgOldPath;
                            } else {
                                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
                            }
                        }
                        mc102_product_img.setProduct_img(imgRealPath);
                        productDao.insertProductImg(mc102_product_img);
                    }
                }
            }
        }
        return CommonUtils.success();
    }

    /**
     * @throws ParseException
     * @Description: 假登录一括処理
     * @Param: JSONObject
     * @return: JSONObject
     * @Date: 2022/02/23
     */
    @Transactional
    public JSONObject allInclusiveHandling(String client_id, JSONArray jsonArray,
        HttpServletRequest httpServletRequest) {
        if (StringTools.isNullOrEmpty(jsonArray) || jsonArray.size() == 0) {
            return CommonUtils.success();
        }
        try {
            String loginNm = CommonUtils.getToken("login_nm", httpServletRequest);
            Date nowTime = DateUtils.getNowTime(null);
            for (int i = 0; i < jsonArray.size(); i++) {
                // 假登录变为普通商品
                String product_id = jsonArray.getJSONObject(i).getString("product_id");
                String warehouse_cd = jsonArray.getJSONObject(i).getString("warehouse_cd");
                productDao.updateProductKubun(client_id, product_id, 0, 0, loginNm, nowTime);
                // 包含假登录商品的出库自动处理
                JSONArray shipmentInfo = jsonArray.getJSONObject(i).getJSONArray("shipmentInfo");
                // 如果没有出库则跳过
                if (shipmentInfo.size() <= 0) {
                    continue;
                }
                List<String> shipmentIds = new ArrayList<>();
                for (int j = 0; j < shipmentInfo.size(); j++) {
                    String shipment_plan_id = shipmentInfo.getJSONObject(j).getString("shipment_plan_id");
                    Integer shipment_status = shipmentInfo.getJSONObject(j).getInteger("shipment_status");
                    shipmentIds.add(shipment_plan_id);

                    // 查找该依赖是否还有甲商品
                    List<Tw201_shipment_detail> shipmentDetailList =
                        shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, false);
                    boolean kubunFlg = false;
                    for (Tw201_shipment_detail detail : shipmentDetailList) {
                        if (detail.getKubun() == 9 && !detail.getProduct_id().equals(product_id)) {
                            kubunFlg = true;
                            break;
                        }
                    }
                    if (!kubunFlg && (!StringTools.isNullOrEmpty(shipment_status) && shipment_status != 999)) {
                        String[] shipment_plan_ids = {
                            shipment_plan_id
                        };
                        shipmentService.setShipmentStatus(httpServletRequest, warehouse_cd, 0,
                            shipment_plan_ids, null, null, null, true, "1", null);
                    }
                }
                // 更改所有满足条件的出库明细的 商品状态
                shipmentDetailDao.updateShipmentDetailKubun(client_id, product_id, shipmentIds, 0, loginNm, nowTime);
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * 商品CSVアップロード
     *
     * @param clientId 店鋪ID
     * @param req {@link HttpServletRequest}
     * @param file CSVファイル
     */
    public void uploadProductCsv(String clientId, HttpServletRequest req, MultipartFile file) {
        try {
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
            // a错误信息list
            List<String> list = new ArrayList<>();
            // a读取上传的CSV文件
            InputStreamReader emptyCheck = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader fileCheck = new CsvReader(emptyCheck);
            boolean b = fileCheck.readHeaders();
            if (!b) {
                throw new LcBadRequestException("商品情報を入力してください。");
            }
            InputStreamReader isr = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            // 读取csv中的header
            Mc100_product product = new Mc100_product();
            // a判断字符串是否为数字的正则
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            // 判断全数字前有单引号的情况
            Pattern numPattern = Pattern.compile("^[\\'][-\\+]?[\\d]*$");
            // 判断字符是否为半角英文，数字 、 - , _ 的验证
            Pattern checkProCode = Pattern.compile("^[a-zA-Z\\d\\-\\_ ]+$");
            Pattern checkBarCode = Pattern.compile(
                "[0-9a-zA-Z\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\[\\]\\{\\}\\:\\;\\'\\,\\.\\<\\>\\/\\?]+");
            Pattern checkOrigin = Pattern.compile("[a-zA-Z]+");

            int num = 0;
            boolean flag = true;
            ArrayList<String[]> productTmpObjects = new ArrayList<>();
            ArrayList<String[]> productObjects = new ArrayList<>();
            while (csvReader.readRecord()) {
                num++;
                if (num == 1) {
                    String tmp = csvReader.getRawRecord().replaceAll("\"", "");
                    if (!StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                        if (!"商品コード,商品名,管理バーコード,商品区分,子商品コード,子商品個数,税区分,商品原価,商品価格,軽減税率適用商品,品名,重量,原産国,商品英語名,タグ,QRコード,備考,シリアルフラグ"
                            .equals(tmp)) {
                            throw new LcBadRequestException("項目名称に不備があります。");
                        }
                    }
                }
                if (num > 1001) {
                    throw new LcBadRequestException("一度に登録できるデータは最大1000件です。");
                }
            }
            if (num == 1) {
                if (StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                    throw new LcBadRequestException("商品情報を入力してください。");
                }
            }
            // a关闭csvReader
            csvReader.close();
            num = 0;
            InputStreamReader isr1 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                String tmp = csvReader1.getRawRecord();
                String[] params = tmp.split(RULE, -1);
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].replaceAll("\"", "").trim();
                    if (numPattern.matcher(params[i]).matches()) {
                        params[i] = params[i].replaceAll("'", "");
                    }
                }
                num++;
                productTmpObjects.add(params);
                if (params.length < 10) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ":ご指定のCSVファイルは、規定の形式または項目と相違があります。");
                    flag = false;
                    continue;
                }
                int k = 0;
                // 商品code不能为空
                String product_code = params[k].replaceAll(" ", "");
                if (StringTools.isNullOrEmpty(product_code)) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品コードは空にしてはいけません。");
                    flag = false;
                }
                // 商品code最大长度
                if (product_code.length() > 100) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                    flag = false;
                }
                // 判断商品code是否为符合规范 （全角英文数字 以及、-, _ ）
                if (!checkProCode.matcher(product_code).matches()) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                    flag = false;
                }
                // 判断商品code在数据库中是否重复
                if (!StringTools.isNullOrEmpty(product_code)) {
                    String code = productDao.checkCodeExist(clientId, product_code);
                    if (!StringTools.isNullOrEmpty(code)) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 本商品コードはすでに存在している。");
                        flag = false;
                    }
                }
                k++;

                // 商品名不能为空
                if (StringTools.isNullOrEmpty(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + "商品名は空にしてはいけません。");
                    flag = false;
                }
                // 判断商品名最大长度
                if (params[k].length() > 250) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品名は、250文字以内でご入力ください。");
                    flag = false;
                }
                k++;

                // a判断商品barcode在数据库中是否重复
                String product_bar_code = params[k].replaceAll(" ", "");
                if (!StringTools.isNullOrEmpty(product_bar_code)) {
                    // barcode 除汉字之外
                    if (!checkBarCode.matcher(product_bar_code).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":管理バーコードは、半角英数字と記号を使用し、20文字以内でご入力ください。");
                        flag = false;
                    }

                    // 判断barcode是否重复
                    String barCode = productDao.checkBarcodeExist(clientId, product_bar_code);
                    if (!StringTools.isNullOrEmpty(barCode)) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 本商品の管理バーコードはすでに存在している。");
                        flag = false;
                    }

                    // 判断set商品barcode长度是否超过20位数
                    if (product_bar_code.length() > 20) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":管理バーコードは、半角英数字と記号を使用し、20文字以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // 判断商品种类
                if (!"1".equals(params[k]) && !"0".equals(params[k]) && !"2".equals(params[k])) {
                    list.add("[" + (num + 1) + "行目] 商品" + num + ":商品種類は空にしてはいけません。(0：通常商品　1：同梱物  2:セット商品)");
                    flag = false;
                }
                k++;

                // 判断子商品code和子商品個数
                if ("2".equals(params[k - 1])) {
                    String productId = productDao.getProductIdByCode(params[k], clientId);
                    // 子商品code不能为空
                    if (params[k] == null || "".equals(params[k]) || params[k].length() == 0) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ":子商品コードは空にしてはいけません。");
                        flag = false;
                    }
                    // 判断子商品code是否为符合规范 （全角英文数字 以及、-, _ ）
                    if (!checkProCode.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ":子商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                        flag = false;
                    }
                    // 子商品code最大长度
                    if (params[k].length() > 100) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ":子商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                        flag = false;
                    }

                    if (StringTools.isNullOrEmpty(productDao.getProductById(productId, clientId))) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ": ご記入した商品は検索にヒットしませんので、もう一度ご入力してください。");
                        flag = false;
                    } else {
                        // a判断子商品ID是否是同捆物
                        if (productDao.getProductById(productId, clientId).getBundled_flg() == 1) {
                            list.add("[" + (num + 1) + "行目] 子商品" + num + ": セット商品には同梱物(" + params[k]
                                + ")を登録することはできませんので、再度ご確認ください。");
                            flag = false;
                        }
                    }
                    k++;
                    // 判断子商品個数是否为数字
                    if (!pattern.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ":子商品個数には数字をご入力ください。");
                        flag = false;
                    }
                    // 判断子商品個数长度
                    if (params[k].length() > 8) {
                        list.add("[" + (num + 1) + "行目] 子商品" + num + ":子商品個数は、8桁以内でご入力ください。");
                        flag = false;
                    }
                    k++;
                } else {
                    k = k + 2;
                }

                // 税区分 默认0
                k++;

                // 如果商品原价为空跳过
                // 去除价格中全角、半角逗号
                String product_cost_price = params[k].replaceAll(",|，|\"", "");
                if (!StringTools.isNullOrEmpty(product_cost_price)) {
                    // a判断商品价格是否为数字
                    if (!pattern.matcher(product_cost_price).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品原価には数字をご入力ください。");
                        flag = false;
                    }
                    // a判断商品价格长度
                    if (product_cost_price.length() > 8) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品原価は、8桁以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // 如果商品价格为空跳过
                // 去除价格中全角、半角逗号
                String product_price = params[k].replaceAll(",|，|\"", "");
                if (!StringTools.isNullOrEmpty(product_price)) {
                    // a判断商品价格是否为数字
                    if (!pattern.matcher(product_price).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 商品価格には数字をご入力ください。");
                        flag = false;
                    }
                    // a判断商品价格长度
                    if (product_price.length() > 8) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":商品価格は、8桁以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // 判断軽減税率適用商品
                // if (!params[k].equals("1") && !params[k].equals("0") && !params[k].equals("") && params[k] != null) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40013.getErrorMsg());
                // flag = false;
                // }
                k++;

                // 品名不能为空
                // String description_cd = params[k].replaceAll(" ","");
                // if (StringTools.isNullOrEmpty(description_cd)) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40137.getErrorMsg());
                // flag = false;
                // }
                // 品名为空跳过判断
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // a判断品名最大长度
                    if (params[k].length() > 50) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":品名は、50文字以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // サイズ 为空跳过判断
                // if (!StringTools.isNullOrEmpty(params[k])) {
                // // 判断サイズ是否为数字
                // if (!pattern.matcher(params[k]).matches()) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40138.getErrorMsg());
                // flag = false;
                // }
                // // 判断サイズ长度
                // if (params[k].length() > 8) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40139.getErrorMsg());
                // flag = false;
                // }
                // }
                // k++;

                // 重量 为空跳过判断
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // 如果为小数，替换小数点后进行判断
                    String weight = params[k].replaceAll(".", "");
                    // 判断重量是否为数字
                    if (!pattern.matcher(weight).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 重量には数字をご入力ください。");
                        flag = false;
                    }
                    // 判断重量长度
                    if (params[k].length() > 8) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 重量は、8桁以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // 原産国 为空跳过判断
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // 判断原産国是否为英文字符
                    if (!checkOrigin.matcher(params[k]).matches()) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 原産国には半角英字をご入力ください。");
                        flag = false;
                    }
                    // 判断原産国长度 2
                    if (params[k].length() != 2) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ": 原産国には2文字以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // 商品英語名不为空时判断
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // 判断商品英語名最大长度
                    if (params[k].length() > 250) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":商品英語名は、250文字以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // タグ不为空时判断
                if (!StringTools.isNullOrEmpty(params[k])) {
                    // 判断商品英語名最大长度
                    if (params[k].length() > 30) {
                        list.add("[" + (num + 1) + "行目] 商品" + num + ":タグは、30文字以内でご入力ください。");
                        flag = false;
                    }
                }
                k++;

                // QRコード 不为空时进行判断
                // if (!StringTools.isNullOrEmpty(params[k])) {
                // // QRコード最大长度
                // if (params[k].length() > 30) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40146.getErrorMsg());
                // flag = false;
                // }
                // // 判断QRコード是否为符合规范 （全角英文数字 以及、-, _ ）
                // if (!checkProCode.matcher(params[k]).matches()) {
                // list.add("[" + (num + 1) + "行目] 商品" + num + ErrorEnum.E_40147.getErrorMsg());
                // flag = false;
                // }
                // }
                k++;

                // 判断备考文本长度
                if (!StringTools.isNullOrEmpty(params[k])) {
                    if (params[k].length() > 150) {
                        list.add("[" + (num + 1) + "行目] 備考" + num + ":備考は、150文字以内でご入力ください。");
                        flag = false;
                    }
                }
            }
            csvReader1.close();

            // 如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new LcBadRequestException(json);
            }

            // 如果セット商品含有多个子商品，重写数据
            ArrayList<Integer> integers = new ArrayList<>();
            for (int i = 0; i < productTmpObjects.size(); i++) {
                if (integers.contains(i)) {
                    continue;
                }
                for (int j = i + 1; j < productTmpObjects.size(); j++) {
                    if ("2".equals(productTmpObjects.get(i)[3]) && "2".equals(productTmpObjects.get(j)[3])
                        && productTmpObjects.get(i)[0].contains(productTmpObjects.get(j)[0])) {
                        if (!productTmpObjects.get(i)[4].contains(productTmpObjects.get(j)[4])) {
                            productTmpObjects.get(i)[4] =
                                productTmpObjects.get(i)[4] + "," + productTmpObjects.get(j)[4];
                            productTmpObjects.get(i)[5] =
                                productTmpObjects.get(i)[5] + "," + productTmpObjects.get(j)[5];
                            integers.add(j);
                        } else {
                            list.add("[" + (num + 1) + "行目] 子商品" + num + ": セット商品の子商品は既に存在している。");
                            flag = false;
                        }
                    }
                }
                productObjects.add(productTmpObjects.get(i));
            }

            // a判断CSV文件中属性是否重复
            // a创建List将name,code,barcode放入其中
            List<String> nameCheck = new ArrayList<String>();
            List<String> codeCheck = new ArrayList<String>();
            List<String> barcodeCheck = new ArrayList<String>();
            for (int i = 0; i < productObjects.size(); i++) {
                String[] paramsName = productObjects.get(i);
                if (paramsName[0] != null && !"".equals(paramsName[0])) {
                    codeCheck.add(paramsName[0]);
                }
                if (paramsName[1] != null && !"".equals(paramsName[1])) {
                    nameCheck.add(paramsName[1]);
                }
                if (paramsName[2] != null && !"".equals(paramsName[2])) {
                    barcodeCheck.add(paramsName[2]);
                }
            }

            // a将List转化为Set
            Set<String> setName = new HashSet<>(nameCheck);
            Set<String> setCode = new HashSet<>(codeCheck);
            Set<String> setBarcode = new HashSet<>(barcodeCheck);
            // a如size不一样则有重复数据
            if (nameCheck.size() != setName.size()) {
                list.add("CSVファイルにすでに存在していた商品名は再入力できません。");
                flag = false;
            }
            if (codeCheck.size() != setCode.size()) {
                list.add("CSVファイルにすでに存在していた商品コードは再入力できません。");
                flag = false;
            }
            if (barcodeCheck.size() != setBarcode.size()) {
                list.add("CSVファイルにすでに存在していた商品の管理バーコードは再入力できません。");
                flag = false;
            }
            // 如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new LcBadRequestException(json);
            }

            // a验证通过后写入数据库
            for (String[] productObject : productObjects) {
                product = new Mc100_product();
                String[] params = productObject;
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].replaceAll("\"", "");
                }

                // 插入商品code
                String product_code = params[0].replaceAll(" ", "");
                product.setCode(product_code);
                // 插入商品名
                product.setName(params[1]);
                // 插入管理code
                String product_bar_code = params[2].replaceAll(" ", "");
                product.setBarcode(product_bar_code);
                // 插入品名
                product.setDescription_cd(params[10]);
                // 軽減税率適用
                if (params[9] == null || "".equals(params[9])) {
                    product.setIs_reduced_tax(0);
                } else {
                    product.setIs_reduced_tax(Integer.valueOf(params[9]));
                }

                // サイズ
                // if (!StringTools.isNullOrEmpty(params[11])) {
                // product.setSize_cd(params[11]);
                // } else {
                // product.setSize_cd("0");
                // }
                // 重量
                if (!StringTools.isNullOrEmpty(params[11])) {
                    product.setWeight(Double.valueOf(params[11]));
                }

                // 原産国
                if (!StringTools.isNullOrEmpty(params[12])) {
                    product.setOrigin(params[12].toUpperCase());
                } else {
                    product.setOrigin("");
                }
                // 商品英語名
                if (!StringTools.isNullOrEmpty(params[13])) {
                    product.setEnglish_name(params[13]);
                } else {
                    product.setEnglish_name("");
                }

                String product_id = this.createProductId(clientId) + "";
                product.setClient_id(clientId);
                product.setProduct_id(product_id);
                String login_nm = CommonUtils.getToken("login_nm", req);
                product.setIns_usr(login_nm);
                product.setIns_date(DateUtils.getDate());
                product.setUpd_date(DateUtils.getDate());
                product.setUpd_usr(login_nm);

                // タグ
                if (!StringTools.isNullOrEmpty(params[14])) {
                    // a登錄主表Mc101_product_tag
                    String[] str = params[14].split("/");
                    for (String s : str) {
                        String tagId;
                        int check = productDao.checkTagExist(s);
                        // a判斷tag在表中是否存在
                        // aはtagが表に存在するかどうかを判断する
                        if (check != 0) {
                            // a如果存在则獲取相應tag的id
                            // aが存在すれば対応するtagのidを取得する
                            tagId = productDao.getTagIdByTagName(s);
                        } else {
                            // a找不到既存tag則新規一條tag
                            // aは既存のtagが見つからなければ新規のtagを1つ選ぶ
                            Mc101_product_tag mc101_product_tag = new Mc101_product_tag();
                            tagId = this.createTagId();
                            mc101_product_tag.setTags_id(tagId);
                            mc101_product_tag.setTags(s);
                            mc101_product_tag.setIns_date(DateUtils.getDate());
                            mc101_product_tag.setIns_usr(login_nm);
                            mc101_product_tag.setUpd_date(DateUtils.getDate());
                            mc101_product_tag.setUpd_usr(login_nm);
                            productDao.insertProductTag(mc101_product_tag);
                        }
                        // a更新商品和tag关系表
                        // aは商品とtagの関係テーブルを更新する
                        Mc104_tag pro_tag = new Mc104_tag();
                        pro_tag.setClient_id(clientId);
                        pro_tag.setProduct_id(product_id);
                        pro_tag.setTags_id(tagId);
                        pro_tag.setUpd_usr(login_nm);
                        pro_tag.setUpd_date(DateUtils.getDate());
                        productDao.insertProductTagRelationship(pro_tag);
                    }
                }
                // 商品種類
                // set商品
                if ("2".equals(params[3])) {
                    String product_price = params[8].replaceAll(",|，|\"", "");
                    if (product_price != null && !"".equals(product_price)) {
                        product.setPrice(Integer.valueOf(product_price));
                    }
                    // 税区分
                    if (!StringTools.isNullOrEmpty(params[6])) {
                        product.setTax_flag(Integer.valueOf(params[6]));
                    } else {
                        product.setTax_flag(0);
                    }
                    // 商品原価
                    if (!StringTools.isNullOrEmpty(params[7])) {
                        product.setCost_price(Integer.valueOf(params[7]));
                    }
                    Integer setSubId = (productDao.getMaxSetSubId() == null) ? 1 : (productDao.getMaxSetSubId() + 1);
                    product.setSet_flg(1);
                    product.setBundled_flg(0);
                    product.setKubun(Constants.SET_PRODUCT);
                    product.setSet_sub_id(setSubId);
                    String[] codes = params[4].split(",");
                    String[] prices = params[5].split(",");
                    if (codes != null || codes.length != 0) {
                        for (int i = 0; i < codes.length; i++) {
                            String productId = productDao.getProductIdByCode(codes[i], clientId);
                            Mc103_product_set mc103 = new Mc103_product_set();
                            mc103.setIns_usr(login_nm);
                            mc103.setUpd_usr(login_nm);
                            mc103.setIns_date(DateUtils.getDate());
                            mc103.setUpd_date(DateUtils.getDate());
                            mc103.setProduct_id(productId);
                            mc103.setProduct_cnt(Integer.valueOf(prices[i]));
                            mc103.setClient_id(clientId);
                            mc103.setSet_sub_id(setSubId);
                            productDao.insertProductSet(mc103);
                        }
                    } else {
                        String productId = productDao.getProductIdByCode(params[4], clientId);
                        Mc103_product_set mc103 = new Mc103_product_set();
                        mc103.setProduct_id(productId);
                        mc103.setProduct_cnt(Integer.valueOf(params[5]));
                        mc103.setClient_id(clientId);
                        mc103.setSet_sub_id(setSubId);
                        productDao.insertProductSet(mc103);
                    }
                } else if ("1".equals(params[3])) {
                    product.setSet_flg(0);
                    product.setBundled_flg(1);
                    product.setKubun(Constants.BUNDLED);
                } else {
                    String product_price = params[8].replaceAll(",", "").replaceAll("，", "");
                    if (product_price != null && !"".equals(product_price)) {
                        product.setPrice(Integer.valueOf(product_price));
                    }
                    // 税区分
                    if (!StringTools.isNullOrEmpty(params[6])) {
                        product.setTax_flag(Integer.valueOf(params[6]));
                    } else {
                        product.setTax_flag(0);
                    }
                    // 商品原価
                    if (!StringTools.isNullOrEmpty(params[7])) {
                        product.setCost_price(Integer.valueOf(params[7]));
                    }
                    product.setSet_flg(0);
                    product.setBundled_flg(0);
                    product.setKubun(Constants.ORDINARY_PRODUCT);
                }

                // QRコード
                if (!StringTools.isNullOrEmpty(params[15])) {
                    product.setUrl(params[15]);
                } else {
                    product.setUrl("");
                }

                // bikou
                if (params[16] != null || !"".equals(params[16])) {
                    product.setBikou(params[16]);
                }
                product.setShow_flg(0);
                // シリアルフラグ 默认无
                int serial_flg = 0;
                if (!StringTools.isNullOrEmpty(params[17]) && StringTools.isInteger(params[17])) {
                    int serialFlg = Integer.parseInt(params[17]);
                    if (serialFlg == 1) {
                        serial_flg = serialFlg;
                    }
                }
                product.setSerial_flg(serial_flg);
                productDao.insertProduct(product);
            }
        } catch (IOException e) {
            throw new LcBadRequestException("セット商品CSVのアップロードに失敗しました。");
        }
        CommonUtils.success();
    }

    /**
     * @Description: 商品対応表登录CSV
     * @Param:
     * @return:
     * @Date: 2021/7/15
     */
    public JSONObject correspondCsvUploads(String client_id, HttpServletRequest req, MultipartFile file) {
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
            // a拼接文件保存路径
            String destFileName = realPath + "resources" + File.separator + "static" + File.separator + "csv"
                + File.separator + datePath + File.separator + fileName;
            // a第一次运行的时候创建文件夹
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            // a把浏览器上传的文件复制到目标路径
            file.transferTo(destFile);
            // a错误信息list
            List<String> errorlist = new ArrayList<String>();
            // a读取上传的CSV文件
            InputStreamReader emptyCheck = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader fileCheck = new CsvReader(emptyCheck);
            boolean judgment = fileCheck.readHeaders();
            if (!judgment) {
                throw new LcBadRequestException("商品情報を入力してください。");
            }
            InputStreamReader isr = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            // 读取csv中的header
            // 判断字符是否为半角英文，数字 、 - , _ 的验证
            Pattern checkProductNumber = Pattern.compile("^[a-zA-Z\\d\\-\\_ ]+$");
            // 判断全数字前有单引号的情况
            Pattern numPattern = Pattern.compile("^[\\'][-\\+]?[\\d]*$");
            // 商品管理番号
            Pattern shopCodePattern = Pattern.compile(
                "[0-9a-zA-Z\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\[\\]\\{\\}\\:\\;\\'\\.\\<\\>\\/\\?\\/]+");

            int num = 0;
            boolean flag = true;

            // 验证csv文件头部
            while (csvReader.readRecord()) {
                num++;
                if (num == 1) {
                    String tmp = csvReader.getRawRecord().replaceAll("\"", "");
                    if (!StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                        if (!"(店舗)商品管理番号,(店舗)商品番号,(店舗)オプションコード,(サンロジ)商品コード,オプション名称1,オプション項目1,オプション名称2,オプション項目2"
                            .equals(tmp)) {
                            throw new LcBadRequestException("タイトル行がご指定のCSVテンプレートと異なりますのでご確認ください。");
                        }
                    } else {
                        throw new LcBadRequestException("商品情報を入力してください。");
                    }
                }
            }
            // a关闭csvReader
            csvReader.close();

            // 读取csv数据
            num = 0;
            InputStreamReader isr1 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                String tmp = csvReader1.getRawRecord();
                String[] params = tmp.split(RULE, -1);
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].replaceAll("\"", "");
                    params[i] = params[i].trim();
                    if (numPattern.matcher(params[i]).matches()) {
                        params[i] = params[i].replaceAll("'", "");
                    }
                }
                num++;
                if (params.length < 8) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":ご指定のCSVファイルは、規定の形式または項目と相違があります。");
                    flag = false;
                    continue;
                }
                // 商品管理番号不为空
                //
                if (StringTools.isNullOrEmpty(params[0])) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品管理番号は空にしてはいけません。");
                    flag = false;
                }
                // 商品管理番号验证
                if (!shopCodePattern.matcher(params[0]).matches()) {
                    errorlist
                        .add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品管理番号は、「，」以外のみを使用し、100文字以内で、半角英数字をご入力ください。");
                    flag = false;
                }
                // 商品管理番号最大长度
                if (params[0].length() > 100) {
                    errorlist
                        .add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品管理番号は、「，」以外のみを使用し、100文字以内で、半角英数字をご入力ください。");
                    flag = false;
                }

                // 商品番号不为空
                if (StringTools.isNullOrEmpty(params[1])) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品番号は空にしてはいけません。");
                    flag = false;
                }
                // 商品番号最大长度
                if (params[1].length() > 70) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品番号は、70文字以内でご入力ください。");
                    flag = false;
                }
                // 判断商品番号验证
                if (!shopCodePattern.matcher(params[1]).matches()) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)商品番号は、「，」以外のみを使用し、70文字以内で、半角英数字をご入力ください。");
                    flag = false;
                }

                // 対応コード最大长度
                if (params[2].length() > 30) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)オプションコードは、30文字以内でご入力ください。");
                    flag = false;
                }
                // 判断対応コード不为空时验证
                if (!StringTools.isNullOrEmpty(params[2]) && !shopCodePattern.matcher(params[2]).matches()) {
                    errorlist
                        .add("[" + (num + 1) + "行目] 商品" + num + ":(店舗)オプションコードは、「，」以外のみを使用し、30文字以内で、半角英数字をご入力ください。");
                    flag = false;
                }

                // (サンロジ)商品コード不为空
                if (StringTools.isNullOrEmpty(params[3])) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(プロロジ)商品コードは空にしてはいけません。");
                    flag = false;
                }

                // (サンロジ)商品コード最大长度
                // String product_code = "";
                if (params[3].length() > 100) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(サンロジ)商品コードは、100文字以内でご入力ください。");
                    flag = false;
                }
                // 判断商品code是否为符合规范 （全角英文数字 以及、-, _ ）
                if (!StringTools.isNullOrEmpty(params[3]) && !checkProductNumber.matcher(params[3]).matches()) {
                    errorlist
                        .add("[" + (num + 1) + "行目] 商品" + num + ":(プロロジ)商品コードは、「半角英数字、- _」のみを使用し、100文字以内でご入力ください。");
                    flag = false;
                }

                // check商品コード是否存在
                String code = "";
                if (!StringTools.isNullOrEmpty(params[3])) {
                    code = productDao.checkCodeExist(client_id, params[3]);
                }
                if (!StringTools.isNullOrEmpty(params[3]) && StringTools.isNullOrEmpty(code)) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ":(サンロジ)商品コードが存在しないので、ご確認してください。");
                    flag = false;
                }

                // オプション名称1 最大长度
                if (params[4].length() > 100) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション名称1は、100文字以内でご入力ください。");
                    flag = false;
                }
                // オプション名称1 不带 : 和 ,
                if (params[4].contains(":") || params[4].contains(",")) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション名称1の形式は100文字以内で、【，：】以外の文字を入力することができます。");
                    flag = false;
                }

                // オプション項目1 最大长度
                if (params[5].length() > 100) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション項目1は、100文字以内でご入力ください。");
                    flag = false;
                }
                // オプション項目1 不带 : 和 ,
                if (params[5].contains(":") || params[5].contains(",")) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション項目1の形式は100文字以内で、【，：】以外の文字を入力することができます。");
                    flag = false;
                }

                // オプション名称2 最大长度
                if (params[6].length() > 100) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション名称2は、100文字以内でご入力ください。");
                    flag = false;
                }
                // オプション名称2 不带 : 和 ,
                if (params[6].contains(":") || params[6].contains(",")) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション名称2の形式は100文字以内で、【，：】以外の文字を入力することができます。");
                    flag = false;
                }

                // オプション項目2 最大长度
                if (params[7].length() > 100) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション項目2は、100文字以内でご入力ください。");
                    flag = false;
                }
                // オプション項目2 不带 : 和 ,
                if (params[7].contains(":") || params[7].contains(",")) {
                    errorlist.add("[" + (num + 1) + "行目] 商品" + num + ": オプション項目2の形式は100文字以内で、【，：】以外の文字を入力することができます。");
                    flag = false;
                }
            }
            csvReader1.close();

            // 如验证不通过则抛出异常
            if (flag == false) {
                String json = JSON.toJSONString(errorlist);
                throw new LcBadRequestException(json);
            }

            // 写入数据
            num = 0;
            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            Date time = DateUtils.getDate();
            String login_nm = CommonUtils.getToken("login_nm", req);
            while (csvReader2.readRecord()) {
                String tmp = csvReader2.getRawRecord();
                String[] params = tmp.split(RULE, -1);
                for (int i = 0; i < params.length; i++) {
                    params[i] = params[i].replaceAll("\"", "");
                    params[i] = params[i].trim();
                    if (numPattern.matcher(params[i]).matches()) {
                        params[i] = params[i].replaceAll("'", "");
                    }
                }
                num++;
                Mc110_product_options mc110 = new Mc110_product_options();
                mc110.setClient_id(client_id);
                mc110.setShop_code(params[0]);
                mc110.setProduct_code(params[1]);
                mc110.setSub_code(params[2]);
                mc110.setCode(params[3]);
                mc110.setOption_name1(params[4]);
                mc110.setOption_name2(params[6]);
                mc110.setOption_value1(params[5]);
                mc110.setOption_value2(params[7]);
                StringBuilder tmpOptions = new StringBuilder();
                tmpOptions.append(params[4] + ":");
                tmpOptions.append(params[5] + ",");
                tmpOptions.append(params[6] + ":");
                tmpOptions.append(params[7]);
                List<String> optionList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tmpOptions);
                String options = "";
                for (int i = 0; i < optionList.size(); i++) {
                    if (":".equals(optionList.get(i))) {
                        continue;
                    }
                    options += optionList.get(i) + ",";
                }
                if (!StringTools.isNullOrEmpty(options)) {
                    mc110.setOptions(options.substring(0, options.length() - 1));
                }
                Integer newId = productDao.getCorrespondingMaxId();
                if (StringTools.isNullOrEmpty(newId)) {
                    newId = 1;
                } else {
                    newId += 1;
                }
                mc110.setId(newId);
                mc110.setIns_date(time);
                mc110.setIns_usr(login_nm);
                productDao.setCorrespondingData(mc110);
            }
        } catch (IOException e) {
            throw new LcBadRequestException("セット商品CSVのアップロードに失敗しました。");
        }
        return CommonUtils.success();
    }

    /**
     * @Description: 查询商品是否在商品对应表中存在个数
     * @Param: 商品code
     * @return: Integer
     * @Date: 2021/10/08
     */
    public JSONObject getCorrespondence(String client_id, String code) {
        Integer product_count = productDao.getCorrespondence(client_id, code);
        return CommonUtils.success(product_count);
    }

    /**
     * @Description: 商品删除(del_flg)
     * @Param: String[]
     * @return: Integer
     * @Date: 2020/05/18
     */
    public JSONObject deleteProduct(String client_id, String[] product_id, HttpServletRequest httpServletRequest) {
        Date upd_date = DateUtils.getDate();
        String upd_usr = CommonUtils.getToken("login_nm", httpServletRequest);
        // 查询该商品是否为セット商品 的子商品
        for (int i = 0; i < product_id.length; i++) {
            List<Mc103_product_set> mc103_product_set = productDao.verificationSetProduct(client_id, product_id[i]);
            if (mc103_product_set.size() != 0) {
                return CommonUtils.failure(ErrorCode.SET_PRODUCT_UNDETECTABLE);
            }
        }
        try {
            productDao.deleteProduct(client_id, product_id, upd_usr, upd_date);
        } catch (Exception e) {
            logger.error("商品删除失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * @Description: 非表示商品設定(show_flg)
     * @Param: 商品id數組
     * @return: JSONObject
     * @Author: zhangmj
     * @Date: 2020/11/11
     */
    public JSONObject showProduct(String client_id, String[] product_id, Integer show_flg,
        HttpServletRequest httpServletRequest) {
        Date upd_date = DateUtils.getDate();
        String upd_usr = CommonUtils.getToken("login_nm", httpServletRequest);
        // 查询该商品是否为セット商品 的子商品
        // if (show_flg == 1) {
        // for (int i = 0; i < product_id.length; i++) {
        // List<Mc103_product_set> mc103_product_set = productDao.verificationSetProduct(client_id, product_id[i]);
        // if (mc103_product_set.size() != 0) {
        // return CommonUtil.errorJson(ErrorEnum.E_20008);
        // }
        // }
        // }

        try {
            productDao.showProduct(client_id, product_id, show_flg, upd_usr, upd_date);
        } catch (Exception e) {
            logger.error("商品非表示設定失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * @Description: 新规登录商品时获取tags options
     * @Param: client_id
     * @return: List
     * @Date: 2020/06/1
     */
    public List<Mc101_product_tag> getTagsOptionsByClientId(String client_id) {
        return productDao.getTagsOptionsByClientId(client_id);
    }

    /**
     * @Description: 获取品名数据
     * @Param:
     * @return: List
     * @Date: 2020/06/8
     */
    public List<Ms008_items> getItemsList(String category_cd) {
        return productDao.getItemsList(category_cd);
    }

    /**
     * @Description: 新规商品时商品名重复验证
     * @Param:
     * @return: boolean
     * @Date: 2020/06/10
     */
    public Boolean checkNameExist(String client_id, String name) {
        boolean flag = false;
        String nm = productDao.checkNameExist(client_id, name);
        if (nm != null && nm != "") {
            flag = true;
        }
        return flag;
    }

    /**
     * @Description: 新规商品时商品code重复验证
     * @Param:
     * @return: String
     * @Date: 2020/06/10
     */
    public Boolean checkCodeExist(String client_id, String code) {
        boolean flag = false;
        String cd = productDao.checkCodeExist(client_id, code);
        if (cd != null && cd != "") {
            flag = true;
        }
        return flag;
    }

    /**
     * @Description: 新规商品时商品barcode重复验证
     * @Param:
     * @return: String
     * @Date: 2020/06/10
     */
    public Boolean checkBarcodeExist(String client_id, String barcode) {
        boolean flag = false;
        String bc = productDao.checkBarcodeExist(client_id, barcode);
        if (bc != null && bc != "") {
            flag = true;
        }
        return flag;
    }

    /**
     * @Description:
     * @Param:
     * @return:
     * @Date: 2020/6/18
     */
    public List<Mc102_product_img> getProductImg(String client_id, String product_id) {
        return productDao.getProductImg(client_id, product_id);
    }

    /**
     * @Description: 生成product_id(规则)
     * @Param: String
     * @return:
     * @Date: 2020/06/3
     */
    public String createProductId(String client_id) {
        String product_id = productDao.getMaxProductId(client_id);
        String temp = "";
        if (product_id != null && product_id != "") {
            temp = product_id.substring(1);
            int num = Integer.valueOf(temp);
            num++;
            String id = String.valueOf(num);
            if (id.length() < temp.length()) {
                for (int i = 0; i < temp.length() - id.length(); i = i + 0) {
                    id = "0" + id;
                }
                id = "P" + id;
            }
            return id;
        } else {
            return "P000000001";
        }
    }

    /**
     * @Description: 生成tags_id(规则)
     * @Param: String
     * @return:
     * @Date: 2020/06/15
     */
    public String createTagId() {
        String tagId = productDao.getMaxTagId();
        if (tagId != null && tagId != "") {
            int num = Integer.valueOf(tagId);
            num++;
            String id = String.format("%010d", num);
            return id;
        } else {
            return "0000000001";
        }
    }

    /**
     * @Param: client_id
     * @param: file
     * @Param: imgFile
     * @description: 商品画像CSV上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/15
     */
    @Transactional
    public JSONObject importImgCSV(String client_id, MultipartFile imgFile, MultipartFile csvFile,
        HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date date = DateUtils.getDate();

        // 将MultipartFile类型 转换为 File
        Integer n;
        File file = new File(imgFile.getOriginalFilename());
        InputStream in = null;
        OutputStream os = null;
        try {
            in = imgFile.getInputStream();
            os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while ((n = in.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, n);
            }
            // 读取文件第一行
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            // 输出路径
            bufferedReader.close();

            File f = new File(file.toURI());
            String p = file.getPath();
            // 将图像压缩包 解压到指定路径
            if (imgFile.isEmpty()) {
                throw new LcBadRequestException("アップロードしたファイルは空です");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String nowTime = format.format(new Date());
            // 解压后的路径
            String uploadPath = pathProps.getRoot() + pathProps.getImage() + client_id + "/" + nowTime + "/";
            List<String> pathList = null;
            // 解压
            pathList = ZipUtil.unzipFile(f, uploadPath);
            // 存读取到的图像名
            ArrayList<String> fileNameList = new ArrayList<>();
            pathList.stream().forEach(path -> {
                List<String> list = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(path);
                String fileName = list.get(list.size() - 1);
                String type = fileName.toUpperCase().substring(fileName.length() - 3);
                if ("JPG".equals(type) || "JPEG".equals(type) || "PNG".equals(type)) {
                    fileNameList.add(fileName);
                } else {
                    throw new LcBadRequestException("JPG/PNG形式のファイルのみアップロードできます");
                }
            });
            if (!csvFile.isEmpty()) {
                InputStream stream = null;
                stream = csvFile.getInputStream();
                Reader reader = null;
                BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
                // 验证编码格式
                if (!CommonUtils.determineEncoding(bufferedInputStream, new String[] {
                    "SHIFT-JIS"
                })) {
                    bufferedInputStream.close();
                    throw new LcBadRequestException("ご指定のCSVファイルが、取り扱いできる形式（SHIFT-JIS）ではありません。");
                }

                reader = new InputStreamReader(bufferedInputStream, Charset.forName("SJIS"));
                // 获取CSV的数据
                Map<String, Collection<Rk145_csv_img>> maps = csvToMap(reader);
                updateProductImg(maps, fileNameList, client_id, loginNm, date);
                bufferedInputStream.close();
                reader.close();
            }
            in.close();
            os.close();
            File fileZip = new File(p);
            if (fileZip.exists()) {
                boolean delete = fileZip.delete();
            }
        } catch (BaseException e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            file.delete();
        }
        return CommonUtils.success();
    }

    /**
     * @Param: client_id
     * @param: product_id
     * @description: 查询该商品是否为セット商品的子商品
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/10/27
     */
    public JSONObject confirmSetProduct(String client_id, String product_id) {
        List<Mc103_product_set> mc103_product_sets = null;
        try {
            mc103_product_sets = productDao.verificationSetProduct(client_id, product_id);
        } catch (Exception e) {
            logger.error("查询该商品是否为セット商品的子商品失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (mc103_product_sets.size() != 0) {
            return CommonUtils.success();
        }
        return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * @param client_id
     * @description: 查询正在出库状态的セット商品
     * @return: java.util.Integer
     * @date: 2020/11/25
     */
    public Boolean selectShipmentSetProduct(String client_id, String set_sub_id) {
        Integer rows = productDao.selectShipmentSetProduct(client_id, set_sub_id);
        return rows > 0;
    }

    /**
     * @Param: list
     * @param: fileNameList
     * @param: client_id
     * @param: loginNm
     * @param: date
     * @description: 将图片添加到数据库
     * @return: void
     * @date: 2020/9/16
     */
    private void updateProductImg(Map<String, Collection<Rk145_csv_img>> maps, ArrayList<String> fileNameList,
        String client_id, String loginNm, Date date) {

        for (Map.Entry<String, Collection<Rk145_csv_img>> entry : maps.entrySet()) {
            Collection<Rk145_csv_img> list = entry.getValue();
            if (list.size() <= 0) {
                continue;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String nowTime = format.format(new Date());
            // 数据库保存的路径
            String uploadPath = pathProps.getImage() + client_id + "/" + nowTime + "/";

            Optional<Rk145_csv_img> rk145CsvImg = list.stream().findFirst();
            Rk145_csv_img rk145_csv_img = rk145CsvImg.get();
            String product_id = rk145_csv_img.getItem001();
            Mc100_product productInfoById = productDao.getProductById(product_id, client_id);
            // 判断该商品是否存在
            ArrayList<String> imgPath = new ArrayList<>();
            if (StringTools.isNullOrEmpty(productInfoById)) {
                continue;
            }
            // 查询该商品之前的图像
            List<Mc102_product_img> imgList = productDao.getProductImg(client_id, product_id);
            // 存要删除哪些图像的 集合
            String img1 = rk145_csv_img.getItem004();
            if (StringTools.isNullOrEmpty(img1)) {
                if (imgList.size() != 0) {
                    Mc102_product_img mc102_product_img = imgList.get(0);
                    productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                        mc102_product_img.getImg_sub_id());
                }
            } else {
                // 判断图像1 是否在压缩包中
                boolean boolImg1 = fileNameList.stream().anyMatch(x -> x.equals(img1));
                if (boolImg1) {
                    // 如果存在， 则删除以前的图片
                    imgPath.add(uploadPath + img1);
                    if (imgList.size() != 0) {
                        Mc102_product_img mc102_product_img = imgList.get(0);
                        productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                            mc102_product_img.getImg_sub_id());
                    }
                }
            }
            // 判断图像2 是否在压缩包中
            String img2 = rk145_csv_img.getItem005();
            if (StringTools.isNullOrEmpty(img2)) {
                if (imgList.size() > 1) {
                    Mc102_product_img mc102_product_img = imgList.get(1);
                    productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                        mc102_product_img.getImg_sub_id());
                }
            } else {
                boolean boolImg2 = fileNameList.stream().anyMatch(x -> x.equals(img2));
                if (boolImg2) {
                    imgPath.add(uploadPath + img2);
                    if (imgList.size() > 1) {
                        Mc102_product_img mc102_product_img = imgList.get(1);
                        productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                            mc102_product_img.getImg_sub_id());
                    }
                }
            }

            // 判断图像3 是否在压缩包中
            String img3 = rk145_csv_img.getItem006();
            if (StringTools.isNullOrEmpty(img3)) {
                if (imgList.size() == 3) {
                    Mc102_product_img mc102_product_img = imgList.get(2);
                    productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                        mc102_product_img.getImg_sub_id());
                }
            } else {
                boolean boolImg3 = fileNameList.stream().anyMatch(x -> x.equals(img3));
                if (boolImg3) {
                    imgPath.add(uploadPath + img3);
                    if (imgList.size() == 3) {
                        Mc102_product_img mc102_product_img = imgList.get(2);
                        productDao.deleteProductImg(rk145_csv_img.getItem001(), client_id,
                            mc102_product_img.getImg_sub_id());
                    }
                }
            }
            imgPath.stream().forEach(path -> {
                Mc102_product_img mc102_product_img = new Mc102_product_img();
                mc102_product_img.setClient_id(client_id);
                mc102_product_img.setProduct_id(product_id);
                mc102_product_img.setProduct_img(path);
                mc102_product_img.setIns_usr(loginNm);
                mc102_product_img.setIns_date(date);
                mc102_product_img.setUpd_date(date);
                mc102_product_img.setUpd_usr(loginNm);
                // 添加图片
                productDao.insertProductImg(mc102_product_img);
            });
        }
    }

    /**
     * @Param: reader
     * @description: 获取CSV的数据
     * @return: java.util.Map<java.lang.String, java.util.Collection <
     *          com.lemonico.common.bean.Rk145_csv_img>>
     * @date: 2020/9/16
     */
    private Map<String, Collection<Rk145_csv_img>> csvToMap(Reader reader) {
        CsvToBean<Rk145_csv_img> csvToBean = new CsvToBeanBuilder<Rk145_csv_img>(reader).withType(Rk145_csv_img.class)
            .build();
        List<Rk145_csv_img> items = csvToBean.parse();
        Multimap<String, Rk145_csv_img> maps = ArrayListMultimap.create();

        for (Rk145_csv_img item : items) {
            maps.put(item.getItem001(), item);
        }
        // a結果を返す
        return maps.asMap();
    }

    public List<Mc106_produce_renkei> getAllProductData(String template) {
        return productDao.selectRenkeiProduct(template);
    }

    public Tw300_stock getProductCnt(String client_id, String product_id) {
        return productDao.getProductCnt(client_id, product_id);
    }

    public void setLocationCode(String client_id, String variant_sku, String stock_location_code) {
        productDao.setLocationCode(client_id, variant_sku, stock_location_code);
    }

    public List<Mc106_produce_renkei> getAllProductDataById(String client_id, Integer api_id) {
        return productDao.getAllProductDataById(client_id, api_id);
    }

    /**
     * @param client_id
     * @param search
     * @param tags_id
     * @param bundled_flg
     * @param show_flg
     * @param stockShow
     * @return JSONObject
     * @Description: 普通商品CSV下载
     * @Date: 2021/06/26
     */
    public List<ProductRecord> getProductCsvList(String client_id, String search, String tags_id, Integer bundled_flg,
        Integer show_flg, String stock_flag, String stockShow) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        ArrayList<ProductRecord> productRecordList = new ArrayList<>();

        // 查询商品信息
        // List<Mc100_product> product_list = productDao.getProductRecordList(client_id, search, tags_id, bundled_flg,
        // show_flg, stock_flag, stockShow);

        List<Integer> kubunList = Arrays.asList(Constants.ORDINARY_PRODUCT, Constants.BUNDLED);
        List<Mc100_product> product_list =
            productDao.getOperatingList(client_id, search, show_flg, null, null, null, tags_id, kubunList, null, null);

        // 所有商品id
        List<String> product_id_list = product_list.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Mc100_product::getProduct_id).collect(Collectors.toList());
        // 根据产品id获取图片信息
        List<Mc102_product_img> product_img = productDao.getProductImgList(product_id_list, client_id);
        Map<String, List<Mc102_product_img>> product_img_map =
            product_img.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));

        for (Mc100_product mc100_product : product_list) {
            ProductRecord productRecord = new ProductRecord();
            List<Mc102_product_img> img_list = product_img_map.get(mc100_product.getProduct_id());
            productRecord.setProduct_id(mc100_product.getProduct_id());
            productRecord.setName(mc100_product.getName());
            productRecord.setCode(mc100_product.getCode());
            productRecord.setBarcode(mc100_product.getBarcode());
            productRecord.setPrice(mc100_product.getPrice());
            productRecord.setIs_reduced_tax(mc100_product.getIs_reduced_tax());
            productRecord.setTax_flag(mc100_product.getTax_flag());
            productRecord.setAvailable_cnt(mc100_product.getTw300_stock().getAvailable_cnt());
            productRecord.setInventory_cnt(mc100_product.getTw300_stock().getInventory_cnt());
            productRecord.setRequesting_cnt(mc100_product.getTw300_stock().getRequesting_cnt());
            productRecord.setBundled_flg(mc100_product.getBundled_flg());
            productRecord.setCost_price(mc100_product.getCost_price());
            productRecord.setDescription_cd(mc100_product.getDescription_cd());
            productRecord.setSize_cd(mc100_product.getSize_cd());
            productRecord.setWeight(mc100_product.getWeight());
            productRecord.setOrigin(mc100_product.getOrigin());
            productRecord.setEnglish_name(mc100_product.getEnglish_name());
            // tag
            String[] product_ids = {
                mc100_product.getProduct_id()
            };
            List<Mc101_product_tag> getClientTags = getClientTags(client_id, product_ids);
            String tags = "";
            for (int i = 0; i < getClientTags.size(); i++) {
                if (!StringTools.isNullOrEmpty(getClientTags.get(i).getTags())) {
                    tags += getClientTags.get(i).getTags() + "/";
                }
            }
            if (tags != "") {
                tags = tags.substring(0, tags.length() - 1);
            }
            productRecord.setTags(tags);
            // QRコード
            productRecord.setUrl(mc100_product.getUrl());
            // 備考
            productRecord.setBikou(mc100_product.getBikou());

            if (!StringTools.isNullOrEmpty(img_list)) {
                // 存放 imgList
                List<String> productImgList =
                    img_list.stream().map(Mc102_product_img::getProduct_img).collect(Collectors.toList());
                // productRecord.setProduct_img(img_list.get(0).getProduct_img());
                productRecord.setProduct_img(productImgList);
            } else {
                productRecord.setProduct_img(new ArrayList<>());
            }
            productRecordList.add(productRecord);
        }

        return productRecordList;
    }

    public Tc203_order_client getApiDataStock(String client_id, Integer api_id) {
        // TODO 自動生成されたメソッド・スタブ
        return productDao.getApiDataStock(client_id, api_id);
    }

    public Integer getNtmProductCnt(String client_id, String product_id) {
        // 実在庫数ー依頼中数
        Integer Avacnt = productDao.getProductAvaCnt(client_id, product_id);
        // 予備在庫数
        Integer YobiCnt = productDao.getProductYobiCnt(client_id, product_id);
        // 個人宅出荷予想数
        Integer PersonCnt = productDao.getProductPersonCnt(client_id, product_id);
        // ECCUBEへ反映する数= WMS実在庫数ー予備在庫数ー依頼中数ー個人宅出荷予想数
        int NtmCnt = Avacnt - YobiCnt - PersonCnt;
        if (NtmCnt < 0) {
            NtmCnt = 0;
        }
        return NtmCnt;
    }

    public JSONObject getAllItemList(String client_id, String warehouse_cd, String search, String stock_flg,
        Integer show_flg, Integer[] productDistinguish, String column, String sort, String[] productidLists) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
            if (!StringTools.isNullOrEmpty(column)) {
                column = "mc100." + column;
            }
        }
        JSONObject resultJson = new JSONObject();

        // if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0) && (!StringTools.isNullOrEmpty(pageSize) &&
        // pageSize > 0)) {
        // PageHelper.startPage(currentPage, pageSize);
        // }
        //
        // 商品种类 0: 通常商品 1: 同捆商品 2: set商品 9:假登录
        List<Integer> typeList = Arrays.asList(productDistinguish);
        // 商品id
        List<String> productidList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(productidLists)) {
            productidList = Arrays.asList(productidLists);
            System.out.println("productidList = " + productidList);
        }
        List<Mc100_product> productList =
            productDao.getOperatingList(client_id, search, show_flg, Integer.parseInt(stock_flg),
                column, sortType, null, typeList, productidList, warehouse_cd);

        // PageInfo<Mc100_product> pageInfo = null;
        long totalCnt = productList.size();
        // if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0) && (!StringTools.isNullOrEmpty(pageSize) &&
        // pageSize > 0)) {
        // pageInfo = new PageInfo<>(productList);
        // totalCnt = pageInfo.getTotal();
        // }

        // 所有商品id
        List<String> product_id_list = productList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .map(Mc100_product::getProduct_id).collect(Collectors.toList());
        // 根据产品id获取图片信息
        List<Mc102_product_img> product_img = productDao.getProductImgList(product_id_list, client_id);
        Map<String, List<Mc102_product_img>> product_img_map =
            product_img.stream().collect(Collectors.groupingBy(Mc102_product_img::getProduct_id));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        JSONArray jsonArray = new JSONArray();
        for (Mc100_product product : productList) {
            JSONObject jsonObject = new JSONObject();
            String product_id = product.getProduct_id();
            String clientId = product.getClient_id();
            jsonObject.put("product_id", product_id);
            jsonObject.put("client_id", clientId);
            jsonObject.put("name", product.getName());
            jsonObject.put("bundled_flg", product.getBundled_flg());
            List<Mc102_product_img> img_list = product_img_map.get(product.getProduct_id());

            String img = "";
            if (!StringTools.isNullOrEmpty(img_list) && img_list.size() != 0) {
                img = img_list.get(0).getProduct_img();
            }
            jsonObject.put("product_img", img);
            jsonObject.put("code", product.getCode());
            jsonObject.put("set_flg", product.getSet_flg());
            jsonObject.put("barcode", product.getBarcode());
            jsonObject.put("kubun", product.getKubun());

            List<Mw405_product_location> locations = stocksResultDao.getLocationInfo(clientId, product_id);

            JSONArray locationArray = new JSONArray();
            for (Mw405_product_location location : locations) {
                Mw404_location mw404Location = location.getMw404_location();
                JSONObject json = new JSONObject();
                json.put("lot_no", mw404Location.getLot_no());
                Date bestbefore_date = mw404Location.getBestbefore_date();
                String beforeDate = "";
                if (!StringTools.isNullOrEmpty(bestbefore_date)) {
                    beforeDate = dateFormat.format(bestbefore_date);
                }
                json.put("bestbefore_date", beforeDate);
                json.put("priority", mw404Location.getPriority());
                json.put("wh_location_nm", mw404Location.getWh_location_nm());
                if (!StringTools.isNullOrEmpty(mw404Location)) {
                    json.put("lot_no", mw404Location.getLot_no());
                }
                json.put("stock_cnt", location.getStock_cnt());
                json.put("status", mw404Location.getStatus());
                json.put("requesting_cnt", location.getRequesting_cnt());
                locationArray.add(json);
            }
            jsonObject.put("locationInfo", locationArray);

            if (product.getSet_flg() != 1) {
                Tw300_stock tw300_stock = product.getTw300_stock();
                if (!StringTools.isNullOrEmpty(tw300_stock)) {
                    Integer inventory_cnt = tw300_stock.getInventory_cnt();
                    Integer requesting_cnt = tw300_stock.getRequesting_cnt();
                    jsonObject.put("inventory_cnt", inventory_cnt);
                    int requestingNum = 0;
                    if (requesting_cnt != -1) {
                        requestingNum = requesting_cnt;
                    }
                    jsonObject.put("requesting_cnt", requestingNum);
                    int deliverable_cnt = 0;
                    if (inventory_cnt != -1) {
                        Integer not_delivery = tw300_stock.getNot_delivery();
                        deliverable_cnt = inventory_cnt - not_delivery - requestingNum;
                    }
                    jsonObject.put("deliverable_cnt", deliverable_cnt);
                }
            } else {

                jsonObject.put("requesting_cnt", "-");
                jsonObject.put("deliverable_cnt", "-");

                List<Mc100_product> set_product_list = productDao.getProductSetList(product.getSet_sub_id(), clientId);
                if (set_product_list.size() == 0) {
                    continue;
                }
                int product_set_inventoryCnt = 0;
                JSONArray setArray = new JSONArray();
                // set商品信息
                int setProductNum = 0;
                int i = 1;
                for (Mc100_product product_set : set_product_list) {
                    String set_product_id = product_set.getProduct_id();

                    JSONObject json = new JSONObject();
                    // 获取到set商品子商品的在库数
                    Integer inventoryCnt = product_set.getTw300_stock().getInventory_cnt();
                    Integer requesting_cnt = product_set.getTw300_stock().getRequesting_cnt();
                    Integer not_delivery = product_set.getTw300_stock().getNot_delivery();

                    Double setNum = null;
                    if (product_set.getProduct_cnt() == 0) {
                        setNum = Math.floor((inventoryCnt - requesting_cnt));
                    } else {
                        setNum = Math.floor((inventoryCnt - requesting_cnt) / product_set.getProduct_cnt());
                    }
                    int set_num = setNum.intValue();
                    // 如果在库数存在， 并且小于上一个set商品的在库数，取最小值
                    if (i == 1) {
                        setProductNum = set_num;
                    } else {
                        if (setProductNum > set_num && setProductNum > 0) {
                            setProductNum = set_num;
                        }
                    }
                    json.put("client_id", clientId);
                    json.put("product_id", set_product_id);
                    json.put("code", product_set.getCode());
                    json.put("barcode", product_set.getBarcode());
                    json.put("name", product_set.getName());
                    List<Mc102_product_img> productImgList = product_set.getMc102_product_imgList();
                    String setImg = "";
                    if (productImgList.size() != 0) {
                        setImg = productImgList.get(0).getProduct_img();
                    }
                    json.put("setImg", setImg);
                    int stockCnt = (inventoryCnt == -1) ? 0 : inventoryCnt;
                    int requestCnt = (requesting_cnt == -1) ? 0 : requesting_cnt;
                    json.put("inventoryCnt", stockCnt);
                    json.put("requesting_cnt", requestCnt);
                    int deliverable_cnt = stockCnt - requestCnt - product_set.getTw300_stock().getNot_delivery();
                    json.put("deliverable_cnt", deliverable_cnt);
                    setArray.add(json);
                    i++;
                }

                product_set_inventoryCnt = setProductNum;
                jsonObject.put("product_set_inventoryCnt", product_set_inventoryCnt);
                jsonObject.put("setArray", setArray);
            }
            jsonArray.add(jsonObject);
        }

        resultJson.put("result_data", jsonArray);
        resultJson.put("total", totalCnt);
        return CommonUtils.success(resultJson);
    }

}
