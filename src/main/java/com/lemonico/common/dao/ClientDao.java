package com.lemonico.common.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms001_function;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.bean.Ms206_client_customer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 顧客グループマスタ
 * @create: 2020-07-06 15:13
 **/
public interface ClientDao
{

    /**
     * @Param: store_id : 店铺Id
     * @description: 获取店铺信息
     * @return: com.lemonico.common.bean.Ms201_customer_group
     * @date: 2020/07/09
     */
    public Ms201_client getClientInfo(String client_id);

    /**
     * @Param:
     * @description: 获取所有店铺信息
     * @return: com.lemonico.common.bean.Ms201_customer_group
     * @date: 2020/08/04
     */
    public List<Ms201_client> getAllClientInfo();

    /**
     * @Param: jsonObject
     * @description: 新增店铺信息
     * @return: java.lang.Integer
     * @date: 2020/8/5
     */
    Integer insertClientInfo(@Param("jsonObject") JSONObject jsonObject, @Param("birthday") Date birthday,
        @Param("permonth") Integer permonth);

    /**
     * @Param: warehouse_cd
     * @param: clientId
     * @param: kubun
     * @description: 将店铺和该仓库绑定
     * @return: java.lang.Integer
     * @date: 2020/8/6
     */
    Integer insertWarehouseAndClient(@Param("warehouse_cd") String warehouse_cd, @Param("client_id") String clientId,
        @Param("kubun") String kubun);

    /**
     * @Param: clientId
     * @description: 将店铺信息 存到 商品設定用表
     * @return: java.lang.Integer
     * @date: 2020/8/24
     */
    Integer insertProductSetting(@Param("client_id") String clientId);

    /**
     * @Description: 获取登录用户的信息
     * @Param: 登录邮箱
     * @return: 店铺名，用户名
     * @Date: 2020/8/24
     */
    public Ms201_client getLoginUserInfo(@Param("client_id") String client_id, @Param("user_id") String user_id);

    /**
     * @Param: new_email
     * @param: client_id
     * @description: 更改店铺担当者邮箱
     * @return: java.lang.Integer
     * @date: 2020/9/1
     */
    Integer updateMailById(@Param("mail") String new_email, @Param("client_id") String client_id);

    /**
     * @Param: clientId
     * @param: user_id
     * @description: 将店铺和用户信息存到店舗別顧客マスタ
     * @return: java.lang.Integer
     * @date: 2020/9/21
     */
    Integer insertClientCustomer(@Param("client_id") String clientId, @Param("user_id") String user_id);

    /**
     * @Param: client_id
     * @param: userIdList
     * @description: 删除店铺和用户的关联
     * @return: java.lang.Integer
     * @date: 2020/9/22
     */
    Integer deleteClientCustomer(@Param("client_id") String client_id,
        @Param("userIdList") ArrayList<String> userIdList);

    /**
     * @Param: client_id
     * @Param: functionCdList
     * @description: 获取登录店铺的機能
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/25
     */
    List<Ms001_function> function_info(@Param("client_id") String client_id,
        @Param("functionCdList") List<String> functionCdList, @Param("warehouse_cd") String warehouse_cd);

    /**
     * @param: loginId ：用户Id
     * @description: 根据用户id 查询绑定信息
     * @return: java.util.List<com.lemonico.common.bean.Ms206_client_customer>
     * @date: 2020/11/4
     */
    List<Ms206_client_customer> getClientListByUserId(@Param("user_id") String loginId,
        @Param("client_id") String client_id);

    /**
     * @Description: 根据 店铺Id 查询ms200表里的店铺绑定的client_id
     * @Param: 登录邮箱
     * @return: 店铺名，用户名
     * @Date: 2020/8/24
     */
    public String getClientIdByUserId(@Param("user_id") String user_id);

    /**
     * @Description: 获取仓库所属所有的店铺id
     * @Param:
     * @return:
     * @Date: 2021/2/23
     */
    public List<String> getAllClientIdByWarehouseId(@Param("warehouse_cd") String warehouse_cd);

    /**
     * @param client_id : 店铺Id
     * @description: 获取店铺的详细信息
     * @return: com.lemonico.common.bean.Ms201_client
     * @date: 2020/12/16 13:19
     */
    Ms201_client getClientDetailInfo(String client_id);

    /**
     * @Param: jsonObject
     * @description: 更改店铺品名
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/06
     */

    /**
     * @param clientIdList : 店铺ID集合
     * @description: 根据多个店铺ID 获取多个店铺信息
     * @return: java.util.List<com.lemonico.common.bean.Ms201_client>
     * @date: 2021/6/25 16:22
     */
    List<Ms201_client> getManyClient(@Param("clientIdList") List<String> clientIdList);

    /**
     * @param functionId : 功能Id
     * @description: 获取具有该功能的店铺Id
     * @return: java.util.List<java.lang.String>
     * @date: 2021/7/26 16:31
     */
    List<String> getClientIdByFunctionId(@Param("function_cd") String functionId);

    /**
     * @param clientIdList : 店鋪Id集合
     *        獲取多個店鋪信息
     */
    List<Ms201_client> getClientInfoList(@Param("clientIdList") List<String> clientIdList);
}
