package com.lemonico.common.dao;



import com.lemonico.common.bean.*;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: LoginDao
 * @description: login dao
 * @date: 2020/05/12 9:25
 **/
@Mapper
public interface LoginDao
{

    /**
     * @Param: loginNm
     * @Param: usekb
     * @description: ユーザーが存在するかどうかを調べるする
     * @return: Ms200Customer
     * @date: 2020/05/12
     */
    public Ms200_customer getUserByName(@Param("login_id") String loginNm, @Param("usekb") String usekb);

    /**
     * @Param ms200Customer
     * @description: ユーザ登録
     * @return: int
     * @date: 2020/05/12
     */
    public Integer register(Ms200_customer ms200Customer);

    /**
     * @Param: user_id
     * @description: 根据用户Id查询用户信息
     * @return: com.lemonico.common.bean.Ms200_customer
     * @date: 2020/05/12
     */
    public Ms200_customer getUserByUserId(@Param("user_id") String user_id);

    /**
     * @description: 最大のclient_idを取得する
     * @return: java.lang.String
     * @date: 2020/06/17
     */
    public String getLastClientId(@Param("search") String search);

    /**
     * @description: 获取最大用户Id
     * @return: java.lang.String
     * @date: 2020/07/09
     */
    String getMaxUserId();

    /**
     * @Description: 获取用户name
     * @Param: user_id
     * @return: name
     * @Date: 2020/8/3
     */
    public String getClientInfo(@Param("user_id") String user_id);

    /**
     * @Param: ms200_customer
     * @description: 初始化账户
     * @return: java.lang.Integer
     * @date: 2020/8/5
     */
    Integer updateUserInfo(Ms200_customer ms200_customer);

    /**
     * @Param: email : loginId
     * @description: 修改用户的所属店铺以及使用区分以及密码
     * @return: java.lang.Integer
     * @date: 2020/8/5
     */
    Integer updateUserClientByLoginId(@Param("login_id") String email, @Param("usekb") String usekb,
        @Param("login_pw") String login_pw, @Param("encode_key") String encode_key,
        @Param("login_nm") String login_nm);

    /**
     * @Param: authority_cd : 権限コード
     * @param: user_id : 顧客CD
     * @param: authority_kb : 使用区分
     * @description: 给用户增加权限
     * @return: java.lang.Integer
     * @date: 2020/8/5
     */
    Integer insertUserAuthority(@Param("authority_cd") String authority_cd, @Param("user_id") String user_id,
        @Param("authority_kb") String authority_kb);

    /**
     * @Param: email
     * @param: usekb
     * @description: 更改用户状态
     * @return: java.lang.Integer
     * @date: 2020/8/5
     */
    Integer updateUserStatusById(@Param("login_id") String email, @Param("usekb") String usekb);

    /**
     * @Param: value
     * @description: 获取邮编番号信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    Ms005_address getZipInfo(@Param("zip") String zip);

    /**
     * @description: 获取所有都道府県
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    List<String> getTodoufukenList();

    /**
     * @description: 仓库注册(临时用)
     * @return:
     * @date: 2020/9/17
     */
    Integer registWarehouse(Mw400_warehouse mw400);

    /**
     * @description: 仓库注册(临时用)
     * @return:
     * @date: 2020/9/17
     */
    Integer updateMs203();

    /**
     * @description: 根据用户名和用途获取用户信息(登录用)
     * @return:
     * @date: 2020/9/17
     */
    Ms200_customer getUserByNameAndYoto(@Param("login_id") String loginNm, @Param("usekb") String usekb,
        @Param("yoto") String yoto);

    /**
     * @Param: clientIdList
     * @description: userId 查询 该店铺的信息
     * @return: java.util.List<com.lemonico.common.bean.Ms201_client>
     * @date: 2020/9/21
     */
    List<Ms201_client> getClientInfoByUserId(@Param("user_id") String userId);

    /**
     * @Param: userId
     * @description: 根据user_id 查询仓库的信息
     * @return: java.util.List<com.lemonico.common.bean.Mw400_warehouse>
     * @date: 2020/9/21
     */
    List<Mw400_warehouse> getWarehouseInfoByUserId(@Param("user_id") String userId);

    /**
     * @description: 解析邮箱修改密码链接中的邮箱地址
     * @return: java.lang.String
     * @date: 2020/09/28
     */

    String checkEmailAddress(@Param("login_id") String login_id);

    /**
     * @description: 獲取首頁通知信息
     * @return: Ms015_news
     * @date: 2021/02/02
     */
    List<Ms015_news> getNewsInfo();

    /**
     * @description: 更新仓库侧默认店铺（ms200表）
     * @return: integer
     * @date: 2021/02/08
     */
    Integer updateDefaultClient(@Param("user_id") String user_id, @Param("client_id") String client_id);

    /**
     * @param user_id : 用户Id
     * @param warehouse_cd : 仓库Id
     * @description: 根据用户Id 和仓库Id 查询该用户是否属于某个仓库
     * @return: com.lemonico.common.bean.Mw402_wh_client
     * @date: 2021/2/24 16:21
     */
    Mw402_wh_client checkWarehouseUser(@Param("user_id") String user_id, @Param("warehouse_cd") String warehouse_cd);
}
