package com.lemonico.api.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc203_order_client;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @className: ApiErrorService
 * @description: API报错信息记录
 * @date: 2021/8/12 9:55
 **/
public interface ApiErrorService
{

    /**
     * @param errOrderClients : 报错信息的api设定信息
     * @description: 保存api报错次数
     * @return: void
     * @date: 2021/8/12 17:01
     */
    JSONObject insertApiErrorCount(List<Tc203_order_client> errOrderClients);

    /**
     * @param client_id : 店铺ID
     * @description: 获取店铺保存信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/12 12:43
     */
    JSONObject getClientApiError(String client_id);

    /**
     * @param jsonObject : client_id：店铺Id order_id：api设定Id template：模板 count：错误次数
     * @param request : 请求
     * @description: 更改api报错信息次数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/12 14:36
     */
    JSONObject updateErrorCount(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param client_id : 店铺Id
     * @description: 判断是否有api快要过期
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/16 14:52
     */
    JSONObject getApiExpired(String client_id);
}
