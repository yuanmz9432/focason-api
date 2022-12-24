package com.lemonico.common.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw400_warehouse;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.PasswordHelper;
import com.lemonico.wms.service.StocksResultService;
import com.lemonico.wms.service.WarehouseCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: LoginController
 * @description: 登録API
 * @date: 2020/05/12 9:28
 **/
@RestController
@RequestMapping("/login")
@Api(tags = "登録API")
public class LoginController
{

    @Resource
    private LoginService loginService;
    @Resource
    private LoginDao loginDao;
    @Resource
    WarehouseCustomerService warehouseCustomerService;
    @Resource
    StocksResultService stocksResultService;

    /**
     * 店鋪情報を新規登録
     *
     * @param jsonObject 店鋪情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    @RequestMapping(value = "/client/insert", method = RequestMethod.POST)
    @ApiOperation(value = "保存顧客グループマスタ信息")
    public JSONObject insertClient(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "password,client_nm,tnnm,email");
        return loginService.insertClient(jsonObject);
    }

    /**
     * @description: 仓库注册(临时用)
     * @return:
     * @date: 2020/9/17
     */
    @RequestMapping(value = "/warehouse", method = RequestMethod.POST)
    @ApiOperation(value = "仓库注册")
    public String registerWarehouse(Mw400_warehouse mw400, String login_id, String login_pw, String login_nm,
        HttpServletRequest request) {
        loginDao.registWarehouse(mw400);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouseId", mw400.getWarehouse_cd());

        jsonObject.put("login_id", login_id);
        jsonObject.put("login_pw", login_pw);
        jsonObject.put("login_nm", login_nm);
        warehouseCustomerService.insertUserInfo(jsonObject, request);
        loginDao.updateMs203();
        JSONObject json = new JSONObject();
        json.put("wh_location_nm", "NO-LOCATION");
        json.put("warehouse_cd", mw400.getWarehouse_cd());
        stocksResultService.createNewLocation(json);
        return "成功";
    }

    /**
     * @Param: userId
     * @description: 根据登录Id获取店铺信息
     * @return: java.lang.String
     * @date: 2020/9/21
     */
    @RequestMapping(value = "/client/info", method = RequestMethod.GET)
    @ApiOperation(value = "根据登录Id获取店铺信息")
    public JSONObject getClientInfoByLoginId(String userId, ServletResponse response, HttpServletRequest request) {
        return loginService.getClientInfoByLoginId(userId, response, request);
    }

    /**
     * @description: 解析邮箱修改密码链接中的邮箱地址
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/28
     */
    @RequestMapping(value = "/email/address", method = RequestMethod.GET)
    @ApiOperation(value = "解析邮箱修改密码链接中的邮箱地址")
    public JSONObject getEmailAddress(String login_id) {
        String address = login_id.replace(" ", "+");
        String email = PasswordHelper.AESDecode(address);
        String loginId = loginService.checkEmailAddress(email);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login_id", loginId);
        return jsonObject;
    }

    /**
     * @Param: login_id : 登录Id
     * @description: 获取用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/4
     */
    @RequestMapping(value = "/storeUserInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息")
    public JSONObject storeUserInfo(String login_id) {
        return loginService.storeUserInfo(login_id);
    }

    /**
     * @description: 更新仓库侧默认店铺（ms200表）
     * @return: JsonObject
     * @date: 2021/02/08
     */
    @RequestMapping(value = "/updateDefaultClient", method = RequestMethod.PUT)
    @ApiOperation(value = "更新仓库侧默认店铺（ms200表）")
    public JSONObject updateDefaultClient(String user_id, String client_id) {
        return CommonUtils.success(loginDao.updateDefaultClient(user_id, client_id));
    }
}
