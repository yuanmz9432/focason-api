package com.lemonico.wms.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw406_wh_smartcat;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.wms.service.WarehouseCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @className: WarehouseCustomerController
 * @description: 仓库侧设定
 * @date: 2020/07/08 10:30
 **/
@Controller
@Api(tags = "仓库侧设定处理")
public class WarehouseCustomerController
{

    @Resource
    private WarehouseCustomerService warehouseCustomerService;

    /**
     * @Param: client_id : 店铺Id
     * @description: 获取到该仓库下所有用户的权限
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    @RequestMapping(value = "/warehousingsResult/getUserAuthority/{warehouse_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询该仓库下所有用户权限", notes = "请务必输入JSON格式")
    public JSONObject getCustomerAuthority(@PathVariable("warehouse_id") String warehouse_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_id", warehouse_id);
        CommonUtils.hashAllRequired(jsonObject, "warehouse_id");
        return warehouseCustomerService.getCustomerAuthority(warehouse_id);
    }

    /**
     * @Param: client_id : 店铺Id
     * @description: 获取该仓库下所有店铺的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    @RequestMapping(value = "/warehousingsResult/getClientInfoList/{warehouse_cd}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询所有店铺信息", notes = "请务必输入JSON格式")
    public JSONObject getClientInfoList(@PathVariable("warehouse_cd") String warehouse_cd) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd");
        return warehouseCustomerService.getClientInfoList(warehouse_cd);
    }

    /**
     * @param: client_id : 店铺Id
     * @description: 根据店铺Id 查询店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/09
     */
    @RequestMapping(value = "/warehousingsResult/getClientInfo/{client_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询单个店铺信息", notes = "请务必输入JSON格式")
    public JSONObject getClientInfo(@PathVariable("client_id") String client_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return warehouseCustomerService.getClientInfo(client_id);
    }

    /**
     * @Param: jsonObject : 店铺的所有信息
     * @description: 根据店铺Id修改店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/10
     */
    @RequestMapping(value = "/warehousingsResult/updateClientInfo", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "改修单个店铺信息", notes = "请务必输入JSON格式")
    public JSONObject updateClientInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_nm");
        return warehouseCustomerService.updateClientInfo(jsonObject);
    }

    /**
     * @param: warehouseId : 仓库Id
     * @description: 根据仓库Id 获取仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/getWarehouseInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "获取仓库信息", notes = "请务必输入JSON格式")
    public JSONObject getWarehouseInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "warehouseId");
        return warehouseCustomerService.getWarehouseInfo(jsonObject.getString("warehouseId"));
    }

    /**
     * @Param: jsonObject : 仓库id
     * @description: 根据仓库Id更改仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/updateWarehouseInfoByWarehouseId", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更改仓库信息", notes = "请务必输入JSON格式")
    public JSONObject updateWarehouseInfoByWarehouseId(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd");
        return warehouseCustomerService.updateWarehouseInfoByWarehouseId(jsonObject);
    }

    /**
     * @Param: user_id : 用户Id
     * @description: 根据用户Id查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/getUserInfo/{user_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据用户Id查询用户信息", notes = "请务必输入JSON格式")
    public JSONObject getUserInfo(@PathVariable("user_id") String user_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        CommonUtils.hashAllRequired(jsonObject, "user_id");
        return warehouseCustomerService.getUserInfo(user_id);
    }

    /**
     * @Param: jsonObject : 多个用户Id
     * @description: 根据用户Id删除用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/deleteUserInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "根据用户Id更改用户信息", notes = "请务必输入JSON格式")
    public JSONObject deleteUserInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "userIdList,warehouseId");
        return warehouseCustomerService.deleteUserInfo(jsonObject);
    }

    /**
     * @param: login_id : 邮箱
     * @description: 根据邮箱查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/getUserInfoByLoginId/{login_id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据用户有邮箱查询用户信息", notes = "请务必输入JSON格式")
    public JSONObject getUserInfoByLoginId(@PathVariable("login_id") String login_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login_id", login_id);
        CommonUtils.hashAllRequired(jsonObject, "login_id");
        return warehouseCustomerService.getUserInfoByLoginId(login_id);
    }

    /**
     * @param: jsonObject : login_id,login_nm
     * @description: 新增属于该仓库的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @RequestMapping(value = "/warehousingsResult/insertUserInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增属于该仓库的用户", notes = "请务必输入JSON格式")
    public JSONObject insertUserInfo(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "login_id,login_nm");
        return warehouseCustomerService.insertUserInfo(jsonObject, request);
    }

    /**
     * @Param: jsonObject
     * @description: 新增属于该仓库的店铺
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    @RequestMapping(value = "/warehousingsResult/insertClientInfo", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增属于该仓库的店铺", notes = "请务必输入JSON格式")
    public JSONObject insertClientInfo(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_nm,tnnm,email");
        return warehouseCustomerService.insertClientInfo(jsonObject, request);
    }

    /**
     * @Description: スマートCatリスト
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    @RequestMapping(value = "/wms/setting/smart/{warehouse_cd}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "スマートCatリスト")
    public JSONObject getSmartCatList(@PathVariable("warehouse_cd") String warehouse_cd) {
        List<Mw406_wh_smartcat> smartCatList = new ArrayList<>();
        try {
            smartCatList = warehouseCustomerService.getSmartCatList(warehouse_cd, 0);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return CommonUtils.success(smartCatList);
    }

    /**
     * @Description: スマートCat更新
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    @RequestMapping(value = "/wms/setting/smart/{warehouse_cd}/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "スマートCat更新")
    public JSONObject updateSmartCatList(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("id") Integer id, @RequestBody JSONObject jsonObject, HttpServletRequest request) {
        try {
            return warehouseCustomerService.updateSmartCatList(warehouse_cd, id, jsonObject, request);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: CSV windows アプロード
     * @Param: warehouse_id
     * @return: String
     * @Date: 2020/11/13
     */
    @RequestMapping(value = "/wms/setting/smart/windows/list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "windows アプロード")
    public JSONObject smartCatListWindow(String warehouse_cd) {
        return warehouseCustomerService.getSmartCatListWindow(warehouse_cd);
    }

    /**
     * @Description: CSV windows アプロード
     * @Param: warehouse_id
     * @return: String
     * @Date: 2020/11/13
     */
    @RequestMapping(value = "/wms/setting/smart/windows", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "スマートCATファイル更新")
    public String upSmartCatListWindow(String warehouse_cd, HttpServletRequest httpServletRequest,
        String shipment_plan_id) {
        try {
            warehouseCustomerService.upSmartCatListWindow(warehouse_cd, httpServletRequest, shipment_plan_id);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return "OK";
    }

    /**
     * @Param: jsonObject : 店铺的所有信息
     * @description: 根据店铺Id修改店铺配送信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/10
     */
    @RequestMapping(value = "/warehousingsResult/updateDeliveryClientInfo", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "修改店铺配送信息", notes = "请务必输入JSON格式")
    public JSONObject updateDeliveryClientInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return warehouseCustomerService.updateDeliveryClientInfo(jsonObject);
    }
}
