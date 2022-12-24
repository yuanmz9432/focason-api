package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc200_order;
import com.lemonico.common.bean.Tc203_order_client;
import com.lemonico.common.bean.Tc206_order_ftp;
import java.util.List;

/**
 * @className: ApiService
 * @description: TODO 类描述
 * @date: 2020/9/9 12:36
 **/
public interface OrderApiService
{

    /**
     * @description: 获取所有和shopify连携的信息
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllData(String template);

    /**
     * @description: 外部API連携(送り状連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataDelivery(String template);

    /**
     * @description: 外部API連携(受注連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataOrder(String template);

    /**
     * @description: 外部API連携(在庫連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    List<Tc203_order_client> getAllDataStock(String template);

    /**
     * @Param: apiName
     * @param: client_id 店铺Id
     * @description: 根据id 和 client_id 查询以前是否存在
     * @return: com.lemonico.common.bean.Tc203_order_client
     * @date: 2020/9/9
     */
    Tc203_order_client getOrderClientInfoById(String apiName, String client_id, String template);

    /**
     * @Param: jsonObject
     * @description: 新规店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer insertOrderClient(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 修改店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer updateOrderClient(JSONObject jsonObject);

    /**
     * @Param: client_id
     * @param: template
     * @param: apiName
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    JSONObject getOrderClientInfo(String client_id, String template, String apiName);

    /**
     * @Param: client_id
     * @description: 查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    JSONObject getOrderClientList(String client_id);

    /**
     * @description: 获取最大Id
     * @Param: clientId
     * @return: java.lang.Integer
     * @date: 2020/9/16
     */
    Integer getMaxId(String clientId);

    /**
     * @Param: jsonObject
     * @description: 删除Api设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    JSONObject deleteApiSet(JSONObject jsonObject);

    Tc206_order_ftp getFtpClientInfoById(String ftp_host, String clientId, Integer get_send_flg);

    /**
     * @Param: jsonObject
     * @description: 修改店铺FTP数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer updateFtpClient(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 新规店铺FTP数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    Integer insertFtpClient(JSONObject jsonObject);

    JSONObject getFtpClientInfo(String client_id, Integer get_send_flag);

    /**
     * @Param: jsonObject
     * @description: 获取base token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    Boolean setBaseToken(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 获取 ネクストエンジン token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/15
     */
    Boolean setNextEngineToken(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 获取 yahoo token
     * @return: com.alibaba.fastjson.JSONObject
     */
    Boolean setYahooToken(JSONObject jsonObject);

    JSONObject insertBASEOrder(JSONObject jsonObject);

    /**
     * @param jsonObject : ColorMe API模板的数据
     * @description: 新规或者编辑 ColorMe API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/06/24 12:01
     */
    JSONObject insertColorMeOrder(JSONObject jsonObject);

    /**
     * MakeShopのAPI情報を新規登録・編集
     * 
     * @param jsonObject API情報
     * @return com.alibaba.fastjson.JSONObject
     * @author: YuanMingZw
     * @date: 2021/07/05
     */
    JSONObject insertMakeShopAPIInfo(JSONObject jsonObject);

    /**
     * YahooのAPI情報を新規登録・編集
     * 
     * @param jsonObject API情報
     * @return com.alibaba.fastjson.JSONObject
     * @author: Hzm
     * @date: 2021/07/13
     */
    JSONObject insertYahooAPIInfo(JSONObject jsonObject);

    /**
     * @description 店铺仓库是否有ntm权限
     * @param warehouse_cd 倉庫CD
     * @param client_id 店舗ID
     * @param function_cd 功能CD
     * @return: boolean
     **/
    boolean hasNtmFunction(String warehouse_cd, String client_id, String function_cd);

    /**
     * 校验手机号
     */
    String checkPhoneNumber(Tc200_order tc200);

    /**
     * 校验邮编番号合法性
     */
    String checkYubinLegal(Tc200_order tc200);

    /**
     * @param jsonObject : next-engine模板的数据
     * @description: 新规或者编辑 next-engine API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/17 10:47
     */
    JSONObject insertNextEngineAPIInfo(JSONObject jsonObject);
}
