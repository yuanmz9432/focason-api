package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc200_customer_delivery;
import java.util.List;

public interface CustomerDeliveryService
{
    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    public List<Mc200_customer_delivery> getCustomerDeliveryList(String client_id, Integer delivery_id, String search);

    /**
     * @Description: 顧客配送先マスタ新规
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    public Integer insertCustomerDelivery(JSONObject jsonParam);

    /**
     * @Description: 顧客配送先マスタ更新
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    public Integer updateCustomerDelivery(JSONObject jsonParam);

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Integer
     * @Date: 2020/5/29
     */
    public Integer getCustomerDeliveryCount(String client_id, Integer delivery_id, String name);

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Integer
     * @Author: yue_dx
     * @Date: 2021/11/5
     */
    public Integer getCompanyCustomerDeliveryCount(String client_id, Integer delivery_id, String companyName);
}
