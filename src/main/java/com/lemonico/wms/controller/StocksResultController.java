package com.lemonico.wms.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw405_product_location;
import com.lemonico.common.bean.Tw302_stock_management;
import com.lemonico.common.bean.Tw303_stock_detail;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.product.service.ProductService;
import com.lemonico.wms.service.StocksResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: StocksResultController
 * @description: 在库controller
 * @date: 2020/07/06
 **/
@RestController
@Api(tags = "仓库侧在库处理")
public class StocksResultController
{

    @Resource
    private StocksResultService stocksResultService;
    @Resource
    private ProductService productService;

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
    @ApiOperation(value = "获取货架location信息", notes = "获取货架location信息")
    @GetMapping(value = "/wms/location/list/{warehouse_cd}")
    public JSONObject getLocationList(@PathVariable("warehouse_cd") String warehouse_cd, int page, int size,
        String column, String sort, String location_id, String search) {
        return stocksResultService.getLocationList(warehouse_cd, page, size, column, sort, location_id, search);
    }

    /**
     * @Param:
     * @description: 货架info设定
     * @return: JsonObject
     * @date: 2020/07/07
     */
    @ApiOperation(value = "货架info设定", notes = "货架info设定")
    @PostMapping(value = "/wms/localtion/info/{warehouse_cd}")
    public JSONObject setLocationInfo(@PathVariable("warehouse_cd") String warehouse_cd, String location_id,
        String info) {
        stocksResultService.setLocationInfo(warehouse_cd, location_id, info);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 获取单个货架中的商品信息
     * @return: JsonObject
     * @date: 2020/07/07
     */
    @ApiOperation(value = "获取单个货架中的商品信息", notes = "获取单个货架中的商品信息")
    @GetMapping(value = "/wms/localtion/product/{warehouse_cd}")
    public JSONObject getLocationProduct(@PathVariable("warehouse_cd") String warehouse_cd, String location_id,
        int page,
        int size, String column, String sort, String client_id) {
        return stocksResultService.getLocationProduct(warehouse_cd, location_id, page, size, column, sort, client_id);
    }

    /**
     * @Param:
     * @description: 获取商品的货架信息
     * @return: List
     * @date: 2020/07/17
     */
    @ApiOperation(value = "获取单个货架中的商品信息", notes = "获取单个货架中的商品信息")
    @GetMapping(value = "/wms/location_product/{client_id}/{product_id}")
    public JSONObject getLocationInfo(@PathVariable("client_id") String client_id,
        @PathVariable("product_id") String product_id) {
        List<Mw405_product_location> list = stocksResultService.getLocationInfo(client_id, product_id);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 新规登录盘点表及明细表
     * @Param: JSONObject格式データ
     * @return: JSONObject
     * @Date: 2020/07/10
     */
    @ApiOperation(value = "新规登录盘点表及明细表", notes = "新规登录盘点表及明细表")
    @PostMapping(value = "/wms/location/product/check")
    public JSONObject createProductCheck(@RequestBody JSONObject jsonObject) {
        Integer manage_id = stocksResultService.createProductCheckAll(jsonObject);
        return CommonUtils.success(manage_id);
    }

    /**
     * @Description: 获取盘点明细表数据(tw303_stock_detail)
     * @Param:
     * @return: JSONObject
     * @Date: 2020/07/10
     */
    @ApiOperation(value = "获取盘点明细表数据", notes = "获取盘点明细表数据")
    @GetMapping(value = "/wms/location/product/list")
    public JSONObject getProductStockCheck(String client_id, Integer manage_id) {
        List<Tw303_stock_detail> list = stocksResultService.getStockDetail(client_id, manage_id);
        return CommonUtils.success(list);
    }

    /**
     * @Param:
     * @description: 更新盘点在库实际数(tw303_stock_detail)
     * @return: JSONObject
     * @date: 2020/07/13
     */
    @ApiOperation(value = "更新盘点在库实际数", notes = "更新盘点在库实际数")
    @PutMapping(value = "/wms/stock/inventory_cnt/update")
    public JSONObject updateStockDetailCount(String product_id, Integer count) {
        stocksResultService.updateStockDetailCount(product_id, count);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 变更盘点状态
     * @return: JSONObject
     * @date: 2020/07/13
     */
    @ApiOperation(value = "变更盘点状态", notes = "变更盘点状态")
    @PutMapping(value = "/wms/stock/change/state")
    public JSONObject changeStockCheckState(Integer manage_id, Integer state) {
        stocksResultService.changeStockCheckState(manage_id, state);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 获取盘点管理信息
     * @return: JSONObject
     * @date: 2020/07/13
     */
    @ApiOperation(value = "获取盘点管理信息", notes = "获取盘点管理信息")
    @GetMapping(value = "/wms/stock/manage/info")
    public JSONObject getStockCheckManageInfo(Integer state) {
        List<Tw302_stock_management> list = stocksResultService.getStockCheckManageInfo(state);
        return CommonUtils.success(list);
    }

    /**
     * @Param:
     * @description: 盘点结束后更新理论在库数和実在庫数
     * @return: JSONObject
     * @date: 2020/07/13
     */
    @ApiOperation(value = "更新理论在库数和実在庫数", notes = "更新理论在库数和実在庫数")
    @PutMapping(value = "/wms/stock/count/update")
    public JSONObject updateStockCount(String client_id, String product_id, Integer inventory_cnt) {
        stocksResultService.updateStockCount(client_id, product_id, inventory_cnt);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 检测是否有盘点未完成
     * @return: boolean
     * @date: 2020/07/13
     */
    @ApiOperation(value = "检测是否有盘点未完成", notes = "检测是否有盘点未完成")
    @GetMapping(value = "/wms/stock/check/status")
    public boolean stockCheckExist(String client_id) {
        return stocksResultService.stockCheckExist(client_id);
    }

    /**
     * @Param:
     * @description: 统计作业中数
     * @return: Integer
     * @date: 2020/07/13
     */
    @ApiOperation(value = "统计作业中数", notes = "统计作业中数")
    @GetMapping(value = "/wms/stock/work/count/{warehouse_cd}")
    public Integer getCheckingCount(@PathVariable("warehouse_cd") String warehouse_cd) {
        return stocksResultService.getCheckingCount(warehouse_cd);
    }

    /**
     * @Param:
     * @description: 新规货架
     * @return: JSONObject
     * @date: 2020/07/13
     */
    @ApiOperation(value = "新规货架", notes = "新规货架")
    @PostMapping(value = "/wms/location/create")
    public JSONObject createNewLocation(@RequestBody JSONObject jsonObject) {
        return stocksResultService.createNewLocation(jsonObject);
    }

    /**
     * @Param: jsonObject : 在库明细表的所有字段
     * @description: 生成在库商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @RequestMapping(value = "/wms/stock/pdf/product/detail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成在库商品明细PDF", notes = "请务必输入JSON格式")
    public JSONObject getStockProductDetail(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "stockInfo");
        return stocksResultService.getStockProductDetail(jsonObject);
    }

    /**
     * @Param: jsonObject : locationName: 货架名称, name： 商品名称, code: 商品code，
     *         product_id: 商品Id，stock_cnt： 在库数
     * @description: 生成在库货架上商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @RequestMapping(value = "/wms/stock/pdf/location/product", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成在库货架上商品明细PDF", notes = "请务必输入JSON格式")
    public JSONObject getStockLocationProduct(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "stockInfo");
        return stocksResultService.getStockLocationProduct(jsonObject);
    }

    /**
     * @Param: jsonObject : stockInfo {product_id, (stock_cnt), wh_location_nm,
     *         client_id}
     * @description: 生成在该货架是否有该商品的PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    @RequestMapping(value = "/wms/stock/pdf/location/count", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "生成在该货架有该商品的PDF", notes = "请务必输入JSON格式")
    public JSONObject getStockProductCount(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "stockInfo");
        return stocksResultService.getStockProductCount(jsonObject);
    }

    /**
     * @Param:
     * @description: 检验货架名是否重复
     * @return: String
     * @date: 2020/07/17
     */
    @ApiOperation(value = "检验货架名是否重复", notes = "检验货架名是否重复")
    @GetMapping(value = "/wms/location/name/check")
    public boolean checkLocationNameExists(String wh_location_nm, String warehouse_cd) {
        return stocksResultService.checkLocationNameExists(wh_location_nm, null, warehouse_cd);
    }

    /**
     * @Param:
     * @description: 检验优先顺序是否重复
     * @return: Integer
     * @date: 2020/08/17
     */
    @ApiOperation(value = "检验优先顺序是否重复", notes = "检验优先顺序是否重复")
    @GetMapping(value = "/wms/location/priority/check")
    public boolean checkLocationPriorityExists(Integer priority, String warehouse_cd) {
        return stocksResultService.checkLocationPriorityExists(priority, warehouse_cd);
    }

    /**
     * @Param:
     * @description: 货架信息修改
     * @return: Integer
     * @date: 2020/07/22
     */
    @ApiOperation(value = "货架信息修改", notes = "货架信息修改")
    @PostMapping(value = "/wms/location/edit/{warehouse_cd}")
    public JSONObject updateLocationInfo(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {
        return stocksResultService.updateLocationInfo(warehouse_cd, jsonObject, request);
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
    @ApiOperation(value = "获取在库一览", notes = "获取在库一览")
    @RequestMapping(value = "/wms/stock/list/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getStockList(@PathVariable("warehouse_cd") String warehouse_cd, int page, int size,
        String column, String sort, String search, String stock_flg, String client_id) {
        return stocksResultService.getStockList(warehouse_cd, page, size, column, sort, search, stock_flg, client_id);
    }

    /**
     * @throws :Exception
     * @Description: LocaltionCSVをアップロードする
     * @Param:
     * @return: JSONObject
     * @Date: 2021/01/22
     */
    @PostMapping("/wms/location/upload/{warehouse_cd}")
    @Transactional
    public JSONObject localCsvUpload(HttpServletRequest req, @PathVariable("warehouse_cd") String warehouse_cd,
        @RequestParam("file") MultipartFile file) throws Exception {
        return stocksResultService.localCsvUpload(req, warehouse_cd, file);
    }

    /**
     * @param jsonObject
     * @description: 在库数调整
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 9:39
     */
    @ApiOperation(value = "在库数调整")
    @PostMapping(value = "/wms/stocks/change/stock_cnt")
    public JSONObject changeStockCnt(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return stocksResultService.changeStockCnt(jsonObject, request);
    }

    /**
     * @param jsonObject
     * @param request
     * @description: 商品货架移动
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 18:09
     */
    @ApiOperation(value = "商品货架移动")
    @PostMapping(value = "/wms/stocks/location/move")
    public JSONObject locationMove(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return stocksResultService.locationMove(jsonObject, request);
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
    @ApiOperation(value = "获取商品在库一览", notes = "获取商品在库一览")
    @RequestMapping(value = "/wms/stock/product/list/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getProductStockList(@PathVariable("warehouse_cd") String warehouse_cd, int page, int size,
        String column, String sort, int stock_flg, String client_id, String search) {
        return stocksResultService.getProductStockList(warehouse_cd, page, size, column, sort, stock_flg, client_id,
            search);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @description: 获取商品对应的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/1 13:05
     */
    @ApiOperation(value = "获取商品对应的货架信息", notes = "获取商品对应的货架信息")
    @RequestMapping(value = "/wms/stock/product/locationInfo/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getProductLocationInfo(@PathVariable("warehouse_cd") String warehouse_cd, String product_id,
        String client_id) {
        return stocksResultService.getProductLocationInfo(warehouse_cd, product_id, client_id);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param locationNm : 货架名称
     * @description: 根据或货架名称获取货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/10 1:17
     */
    @ApiOperation(value = "根据或货架名称获取货架信息", notes = "根据或货架名称获取货架信息")
    @GetMapping(value = "/wms/stock/location/info/{warehouse_cd}")
    public JSONObject getLocationInfoByName(@PathVariable("warehouse_cd") String warehouse_cd, String locationNm) {
        return stocksResultService.getLocationInfoByName(warehouse_cd, locationNm);
    }

    /**
     * @param jsonObject : client_id（店铺Id） location_id（货架Id） product_id（商品Id） bestbefore_date（过期时间） status（出荷不可）
     * @param request : 请求
     * @description: 修改货架明细的 过期时间 和 出荷不可
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/10/22 13:57
     */
    @ApiOperation(value = "修改货架明细的 过期时间 和 出荷不可", notes = "修改货架明细的 过期时间 和 出荷不可")
    @PostMapping(value = "/wms/location/update/locationDetail")
    public JSONObject updateLocationDetail(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return stocksResultService.updateLocationDetail(jsonObject, request);

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
    @ApiOperation(value = "在库一览csv下载获取数据", notes = "在库一览csv下载获取数据")
    @GetMapping(value = "/wms/stock/list/csv/data/{warehouse_cd}")
    public JSONObject getStockListCsvData(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        int stock_flg, String search, String column, String sort) {
        return stocksResultService.getStockListCsvData(warehouse_cd, client_id, stock_flg, search, column, sort);
    }
}
