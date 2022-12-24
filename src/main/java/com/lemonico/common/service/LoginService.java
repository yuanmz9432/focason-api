package com.lemonico.common.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms200_customer;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @className: LoginService
 * @description: ログインのインターフェース
 * @date: 2020/05/12 9:26
 **/
public interface LoginService
{
    /**
     * @Param loginNm :ユーザ名
     * @description: ユーザーが存在するかどうかを調べる
     * @return: Ms200Customer
     * @date: 2020/05/12
     */
    public Ms200_customer getUserByName(String loginNm, String usekb);

    /**
     * @Param: client_id : 店铺ID
     * @description: 根据店铺ID获取店铺信息
     * @return: com.lemonico.common.bean.Ms200_customer
     * @date: 2020/05/12
     */
    public Ms200_customer getUserByUserId(String client_id);

    /**
     * @Param: userId
     * @param: usekb
     * @description: 需要携带token的验证用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/22
     */
    JSONObject checkUserId(String userId, String usekb, String yoto, String flg, HttpServletRequest request);

    /**
     * @Param: jsonObject : loginId
     * @description: 保存用户信息，状态为申请中
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    JSONObject saveUser(JSONObject jsonObject, HttpServletResponse response);

    /**
     * @Param: jsonObject
     * @description: 新增店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    JSONObject insertClient(JSONObject jsonObject);

    /**
     * @Param: email
     * @description: 更改用户状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    JSONObject changeUserStatus(String email);

    /**
     * @Param: jsonObject
     * @description: 更改用户密码
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    JSONObject changeUserPwd(JSONObject jsonObject);

    /**
     * @Param: value
     * @description: 获取邮编番号信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    JSONObject getZipInfo(String zip);

    /**
     * @description: 获取所有都道府県
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    JSONObject getPrefectureList();

    /**
     * @Param: userId
     * @description: 根据登录Id获取店铺信息
     * @return: java.lang.String
     * @date: 2020/9/21
     */
    JSONObject getClientInfoByLoginId(String userId, ServletResponse response, HttpServletRequest request);

    /**
     * @Param: jsonObject
     * @description: 店铺侧登陆
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/21
     */
    JSONObject getClientToken(JSONObject jsonObject, HttpServletResponse response);

    /**
     * @Param: jsonObject
     * @param: response
     * @description: 仓库侧登陆
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/21
     */
    JSONObject getWarehouseToken(JSONObject jsonObject, HttpServletResponse response);

    /**
     * @Param: userId : 邮箱
     * @description: 判断邮箱是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    String checkEmailAddress(String login_id);

    /**
     * @Param: login_id : 登录Id
     * @description: 获取用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/4
     */
    JSONObject storeUserInfo(String login_id);
}
