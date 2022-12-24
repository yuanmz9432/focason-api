package com.lemonico.common.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms018_api_error;
import com.lemonico.common.bean.Tc203_order_client;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ApiErrorDao
 * @description: API报错信息记录
 * @date: 2021/8/12 9:56
 **/
public interface ApiErrorDao
{

    /**
     * @param ms018ApiErrors : Ms018_api_error集合
     * @description: 修改多个api报错信息次数
     * @return: java.lang.Integer
     * @date: 2021/8/12 14:44
     */
    Integer updateErrorCount(@Param("ms018ApiErrors") ArrayList<Ms018_api_error> ms018ApiErrors);

    /**
     * @param client_id : 店铺ID
     * @param count : 报错次数
     * @description: 获取店铺报错次数信息
     * @return: java.util.List<com.lemonico.common.bean.Ms018_api_error>
     * @date: 2021/8/12 12:46
     */
    List<Ms018_api_error> getClientApiError(@Param("client_id") String client_id,
        @Param("error_count") Integer count);

    /**
     * @description: 获取所有api报错信息
     * @return: java.util.List<com.lemonico.common.bean.Ms018_api_error>
     * @date: 2021/8/12 14:43
     */
    List<Ms018_api_error> getAllApiError();

    /**
     * @param notExistApiErrors : Ms018_api_error集合
     * @description: 新规多条api报错信息
     * @return: java.lang.Integer
     * @date: 2021/8/12 14:43
     */
    Integer insertApiError(@Param("apiError") ArrayList<Ms018_api_error> notExistApiErrors);

    /**
     * @param jsonObject : client_id：店铺Id order_id：api设定Id template：模板 count：错误次数
     * @param loginNm : 用户名
     * @param nowTime : 现在时间
     * @description: 修改单个api报错信息次数
     * @return: java.lang.Integer
     * @date: 2021/8/12 14:42
     */
    Integer updateApiErrorCount(@Param("jsonObject") JSONObject jsonObject,
        @Param("loginNm") String loginNm,
        @Param("date") Date nowTime);

    /**
     * @param client_id : 店铺Id
     * @param templateList : 模板
     * @description: 获取到乐天和雅虎的 api设定信息
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2021/8/16 16:09
     */
    List<Tc203_order_client> getApiExpired(@Param("client_id") String client_id,
        @Param("templateList") List<String> templateList);

    /**
     * @param id : api设定id
     * @param client_id : 店铺Id
     * @description: 根据 api设定id 和 店铺id 获取api报错次数信息
     * @return: java.util.List<com.lemonico.common.bean.Ms018_api_error>
     * @date: 2021/8/20 13:29
     */
    List<Ms018_api_error> getApiErrorInfo(@Param("order_id") String id, @Param("client_id") String client_id);

    /**
     * @param idList : api报错信息 管理Id集合
     * @description: 删除对应的api报错信息
     * @return: int
     * @date: 2021/8/20 13:34
     */
    int deleteApiErrorInfo(@Param("idList") List<Integer> idList);

}
