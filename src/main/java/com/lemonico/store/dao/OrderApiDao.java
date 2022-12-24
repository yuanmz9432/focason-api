package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc203_order_client;
import com.lemonico.common.bean.Tc206_order_ftp;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ApiDao
 * @description: TODO 类描述
 * @date: 2020/9/9 12:37
 **/
@Mapper
public interface OrderApiDao
{

    /**
     * @description: API自動連携(全て携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllData(@Param("template") String template);

    /**
     * @description: API自動連携(伝票連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataOrder(@Param("template") String template);

    /**
     * @description: API自動連携(伝票連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataDelivery(@Param("template") String template);

    /**
     * @description: API自動連携(在庫連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataStock(@Param("template") String template);

    /**
     * @Param: apiName
     * @param: client_id 店铺Id
     * @description: 根据id 和 client_id 查询以前是否存在
     * @return: com.lemonico.common.bean.Tc203_order_client
     * @date: 2020/9/9
     */
    Tc203_order_client getOrderClientInfoById(@Param("api_name") String apiName,
        @Param("client_id") String client_id,
        @Param("api_key") String api_key,
        @Param("template") String template);

    /**
     * @Param: jsonObject
     * @description: 新规店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer insertOrderClient(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 修改店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer updateOrderClient(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: client_id
     * @param: template
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    Tc203_order_client getOrderClientInfo(@Param("client_id") String client_id,
        @Param("template") String template,
        @Param("api_name") String apiName);

    /**
     * @Param: client_id
     * @description: 查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    List<Tc203_order_client> getOrderClientList(@Param("client_id") String client_id);

    /**
     * @description: 获取最大Id
     * @param: client_id
     * @return: java.lang.Integer
     * @date: 2020/9/16
     */
    Integer getMaxId(@Param("client_id") String clientId);

    /**
     * @Param: jsonObject
     * @description: 删除Api设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    Integer deleteApiSet(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: ftp_host
     * @param: client_id 店铺Id
     * @description: 根据id 和 client_id 查询以前是否存在
     * @return: com.lemonico.common.bean.Tc206_order_ftp
     * @author: HZM
     * @date: 2020/10/19
     */
    Tc206_order_ftp getFtpClientInfoById(@Param("ftp_host") String ftp_host,
        @Param("client_id") String client_id,
        @Param("get_send_flg") Integer get_send_flg);

    /**
     * @Param: jsonObject
     * @description: 新规店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/10/19
     */
    Integer insertFtpClient(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 修改店铺受注关系数据
     * @return: java.lang.Integer
     * @author: HZM
     * @date: 2020/10/19
     */
    Integer updateFtpClient(@Param("jsonObject") JSONObject jsonObject);

    /**
     * 获取PDF信息
     * 
     * @param client_id
     * @param get_send_flag
     * @return
     */
    Tc206_order_ftp getFtpClientInfo(@Param("client_id") String client_id,
        @Param("get_send_flg") Integer get_send_flag);

    /**
     * @Param: access_token
     * @param: refresh_token
     * @param: id
     * @description: 修改模板的token
     * @return: java.lang.Integer
     * @date: 2020/12/14
     */
    Integer updateToken(@Param("access_token") String access_token,
        @Param("refresh_token") String refresh_token,
        @Param("id") Integer id,
        @Param("client_id") String client_id);

    /**
     * @param jsonObject ： 数据
     * @description: 更改BASE模板的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/9 15:41
     */
    int updateOrderBase(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject : 数据
     * @description: 新规BASE模板的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/9 15:43
     */
    Integer insertOrderBase(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject : 数据
     * @description: 新规Color Me模板的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/06/24 13:57
     */
    Integer updateOrderColorMe(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject : 数据
     * @description: 新规Color Me模板的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/06/24 13:57
     */
    Integer insertOrderColorMe(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: MakeShopのAPI情報更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/07/05 20:13
     */
    Integer updateMakeShopAPIInfo(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: MakeShopのAPI情報新規
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/07/05 20:13
     */
    Integer insertMakeShopAPIInfo(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: YahooのAPI情報更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2021/7/13 15:41
     */
    int updateYahooApi(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: YahooのAPI情報新規
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2021/7/13 15:43
     */
    Integer insertYahooApi(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: NextEngineのAPI情報更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/16 15:41
     */
    int updateNextEngineApi(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject
     * @description: NextEngineのAPI情報更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/16 15:41
     */
    Integer insertNextEngineApi(@Param("jsonObject") JSONObject jsonObject);

    /**
     * Tc200_orderレコードを削除する
     *
     * @param client_id 店舗ID
     * @param outer_order_no 外部受注番号
     */
    void deleteTc200OrderByOuterOrderNo(@Param("client_id") String client_id,
        @Param("outer_order_no") Integer outer_order_no);

    /**
     * @param clientId : 店舗Id
     * @param identifier : 店舗識別コード
     * @description: 根据店铺识别子 获取店铺api设定信息
     * @return: com.lemonico.common.bean.Tc203_order_client
     * @date: 2021/9/8 14:26
     */
    List<Tc203_order_client> getClientApiByIdentifier(@Param("client_id") String clientId,
        @Param("identification") String identifier);
}
