package com.lemonico.common.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms201_client;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 顧客グループマスタ
 * @create: 2020-07-06 15:13
 **/
public interface ClientService
{

    /**
     * @Param: store_id : 店铺Id
     * @description: 获取店铺信息
     * @return: com.lemonico.common.bean.Ms201_customer_group
     * @date: 2020/07/09
     */
    public Ms201_client getClientInfo(@Param("client_id") String client_id);

    /**
     * @Description: 获取登录用户的信息
     * @Param: 登录邮箱
     * @return: 店铺名，用户名
     * @Date: 2020/8/24
     */
    public Ms201_client getLoginUserInfo(String client_id, String user_id);

    /**
     * @Param: client_id
     * @Param: function_cd
     * @description: 获取登录店铺或仓库的機能
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/25
     */
    JSONObject getFunctions(String warehouse_cd, String client_id, String function_cd);

    /**
     * @Description: 获取登录用户的clientid
     * @Param: user_id
     * @return: 用户名
     * @Author: Liocng
     * @Date: 2020/12/3
     */
    String getClientIdByUserId(String user_id);

    /**
     * @Param: jsonObject
     * @description: 更改店铺配送品名
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/06
     */
    JSONObject updateLabelNote(JSONObject jsonObject);
}
