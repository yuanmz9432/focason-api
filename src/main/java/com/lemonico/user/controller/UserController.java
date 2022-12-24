package com.lemonico.user.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.service.CommonService;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "ユーザー管理")
public class UserController
{


    private final LoginService loginService;
    private final CommonService commonService;

    /**
     * TODO ユーザー検索機能に合併する
     *
     * @Param: userId : 邮箱
     * @description: 判断用户是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @RequestMapping(value = "/login/user/check", method = RequestMethod.GET)
    @ApiOperation(value = "验证用户是否存在")
    public JSONObject checkUserId(String userId, String usekb, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        CommonUtils.hashAllRequired(jsonObject, "userId");
        return loginService.checkUserId(userId, usekb, null, null, request);
    }

    /**
     * TODO ユーザー検索機能に合併する
     *
     * @Param: userId
     * @param: usekb
     * @param: client_id: 店铺Id
     * @description: 需要携带token的验证用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/22
     */
    @RequestMapping(value = "/login/check/user/token", method = RequestMethod.GET)
    @ApiOperation(value = "验证用户是否存在")
    public JSONObject checkUserByToken(String userId, String usekb, String yoto, String flg,
        HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        CommonUtils.hashAllRequired(jsonObject, "userId");
        return loginService.checkUserId(userId, usekb, yoto, flg, request);
    }

    /**
     * TODO registerパスに合併する
     *
     * @Param: jsonObject : loginId
     * @description: 保存用户信息，状态为申请中
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @RequestMapping(value = "/login/user/save", method = RequestMethod.POST)
    @ApiOperation(value = "保存用户信息，状态为申请中")
    public JSONObject saveUser(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        CommonUtils.hashAllRequired(jsonObject, "loginId");
        return loginService.saveUser(jsonObject, response);
    }

    /**
     * @Param: email
     * @description: 更改用户状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @RequestMapping(value = "/login/changeUserStatus", method = RequestMethod.POST)
    @ApiOperation(value = "更改用户状态")
    public JSONObject changeUserStatus(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "email");
        String email = jsonObject.getString("email");
        return loginService.changeUserStatus(email);
    }

    /**
     * @Param: jsonObject
     * @description: 更改用户密码
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    @RequestMapping(value = "/login/change/password", method = RequestMethod.POST)
    @ApiOperation(value = "更改用户密码")
    public JSONObject changeUserPwd(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "email,password");
        return loginService.changeUserPwd(jsonObject);
    }

    /**
     * TODO ユーザー検索機能に合併する
     *
     * @param key
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/clickEmail", method = RequestMethod.GET)
    @ApiOperation(value = "メールアドレスを確認する")
    public JSONObject clickEmail(String key, HttpServletRequest request) {
        return commonService.clickEmail(key, request);
    }
}
