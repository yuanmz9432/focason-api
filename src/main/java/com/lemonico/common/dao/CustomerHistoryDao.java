package com.lemonico.common.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms205_customer_history;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 顧客別作業履歴
 * @create: 2020-07-13 10:13
 **/
public interface CustomerHistoryDao
{

    /**
     * @program: sunlogi
     * @description: 获取顧客別作業履歴
     * @create: 2020-07-16
     **/
    public List<Ms205_customer_history> getCustomerHistory(String plan_id);

    /**
     * @program: sunlogi
     * @description: 获取顧客別作業履歴
     * @create: 2020-07-12
     **/
    public Integer insertCustomerHistory(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param plan_id : 依赖Id
     * @description: 查询最新一条操作履历
     * @return: com.lemonico.common.bean.Ms205_customer_history
     * @date: 2022/1/25 18:30
     */
    Ms205_customer_history getCustomerInfo(@Param("plan_id") String plan_id);

}
