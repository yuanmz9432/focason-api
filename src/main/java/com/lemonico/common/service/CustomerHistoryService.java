package com.lemonico.common.service;



import com.lemonico.common.bean.Ms205_customer_history;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: sunlogi
 * @description: 顧客別作業履歴
 * @create: 2020-07-13 11:08
 **/
public interface CustomerHistoryService
{

    /**
     * @Description: 顧客別作業履歴
     * @Param:
     * @return:
     * @Date: 2020/7/13
     */
    public List<Ms205_customer_history> getCustomerHistory(String plan_id);

    /**
     * @Description: 顧客別作業履歴
     * @Param: Json
     * @return: Integer
     * @Date: 2020/7/13
     */
    public Integer insertCustomerHistory(HttpServletRequest httpServletRequest, String[] shipment_plan_id,
        String operation_cd, String user_id);

}
