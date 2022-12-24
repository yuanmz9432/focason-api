package com.lemonico.wms.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.wms.service.WarehousingResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @className: WarehousingResult
 * @description: 仓库侧入库处理
 * @date: 2020/06/17 13:52
 **/


@Controller
@Api(tags = "仓库侧入库处理")
public class WarehousingResultController
{

    @Resource
    private WarehousingResultService warehousingResultService;

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询商品
     * @return: JSONObject
     * @date: 2020/06/17
     */
    @RequestMapping(value = "/wms/warehousings/uncheck/detail/{warehouse_cd}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据入库依赖Id查询商品", notes = "请务必输入JSON格式")
    public JSONObject getWarehouseInfoUncheck(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id");
        jsonObject.put("warehouse_cd", warehouse_cd);
        return warehousingResultService.getWarehouseInfoUncheck(jsonObject);
    }

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询商品
     * @return: JSONObject
     * @date: 2020/06/17
     */
    @RequestMapping(value = "/wms/warehousings/detail/{warehouse_cd}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据入库依赖Id查询商品", notes = "请务必输入JSON格式")
    public JSONObject getWarehouseInfoById(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id");
        jsonObject.put("warehouse_cd", warehouse_cd);
        return warehousingResultService.getWarehouseInfoById(jsonObject);
    }

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID, status : 入庫ステータス
     * @description: 根据入库依赖修改入库状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/17
     */
    @RequestMapping(value = "/wms/warehousings/status/update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "修改入库状态", notes = "请务必输入JSON格式")
    public JSONObject updateStatusById(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,client_id,id");
        return warehousingResultService.updateStatusById(jsonObject, servletRequest);
    }

    /**
     * @param: jsonObject: product_id,name,code,barcode,quantity
     * @description: 仓库侧入库商品PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/24
     */
    @RequestMapping(value = "/wms/warehousings/create/pdf", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "仓库侧入库商品PDF生成", notes = "请务必输入JSON格式")
    public JSONObject createWarehouseInfoPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "item");
        return warehousingResultService.createWarehouseInfoPDF(jsonObject);
    }

    // /**
    // * @param: jsonObject : items 包含商品ID，入库实际数，入库依赖数等
    // * @description: 一時保存
    // * @return: com.alibaba.fastjson.JSONObject
    //// * @date: 2021/07/06
    // */
    // @RequestMapping(value = "/wms/warehousings/tmpsave", method = RequestMethod.POST)
    // @ResponseBody
    // @ApiOperation(value = "追加检品", notes = "请务必输入JSON格式")
    // public JSONObject updatewarehousSave(@RequestBody JSONObject jsonObject) {
    // CommonUtil.hashAllRequired(jsonObject, "items");
    // return warehousingResultService.updatewarehousSave(jsonObject);
    // }

    /**
     * @param: jsonObject : client_id 店舗ID, id 入庫依頼ID, items 几乎商品表的所有数据
     * @description: 添加数据到在库管理表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/03
     */
    @RequestMapping(value = "/wms/warehousings/insert/stock", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加数据到在库管理表", notes = "请务必输入JSON格式")
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject insertStock(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id,items");
        return warehousingResultService.insertStock(jsonObject, request);
    }

    /**
     * @param jsonObject : warehouse_cd: 仓库ID client_id: 店铺ID id: 入库依赖ID items: 入库商品明细 warehousing_date: 入库时间
     * @param request : 请求
     * @description: 添加部分选择的入库明细到在库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/28 13:12
     */
    @RequestMapping(value = "/wms/warehouseings/section/insert/stock", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加部分选择的入库明细到在库")
    public JSONObject insertSectionStock(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,id,items");
        return warehousingResultService.insertSectionStock(jsonObject, request);
    }


    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 查出该仓库所有的货架
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    @RequestMapping(
        value = "/warehousingsResult/getLocationNameByWarehouseId/{warehouse_cd}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查出该仓库所有的货架", notes = "请务必输入JSON格式")
    public JSONObject getLocationNameByWarehouseId(@PathVariable("warehouse_cd") String warehouse_cd, String searchName,
        String location_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd");
        return warehousingResultService.getLocationNameByWarehouseId(warehouse_cd, searchName, location_id);
    }

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 检查货架是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/15
     */
    @RequestMapping(value = "/warehousingsResult/location/check/{warehouse_cd}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查出该仓库所有的货架", notes = "请务必输入JSON格式")
    public JSONObject locationNameCheck(@PathVariable("warehouse_cd") String warehouse_cd, String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd");
        return warehousingResultService.locationNameCheck(warehouse_cd, name);
    }

    /**
     * @Param: id : 入库依赖Id, client_id: 店铺Id, warehouse_id： 仓库Id
     * @description: 根据入库依赖Id查询商品异常的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/14
     */
    @RequestMapping(value = "/wms/warehousings/location/exception", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据入库依赖Id查询商品异常的货架信息", notes = "请务必输入JSON格式")
    public JSONObject getLocationException(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "id,client_id,warehouse_id");
        return warehousingResultService.getLocationException(jsonObject);
    }

    /**
     * @param warehouse_id : 仓库Id
     * @param client_id ： 店铺Id
     * @description: 入庫ステータス件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/30 15:53
     */
    @RequestMapping(value = "/warehousingsResult/count", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "入庫ステータス件数", notes = "请务必输入JSON格式")
    public JSONObject getWarehousingStatusCount(String warehouse_id, String client_id) {
        return warehousingResultService.getWarehousingStatusCount(warehouse_id, client_id);
    }

    /**
     * @param jsonObject : client_id,warehouse_cd,code,lot_not,locationName,bestbefore_date,number,info
     * @param request : 请求
     * @description: 仓库侧商品新规入库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/28 16:01
     */
    @RequestMapping(value = "/warehousingsResult/direct/warehousing", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "仓库侧商品新规入库", notes = "仓库侧商品新规入库")
    public JSONObject directWarehouse(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return warehousingResultService.directWarehouse(jsonObject, request);
    }
}
