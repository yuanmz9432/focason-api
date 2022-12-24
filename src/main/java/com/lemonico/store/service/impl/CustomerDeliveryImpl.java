package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc200_customer_delivery;
import com.lemonico.store.dao.CustomerDeliveryDao;
import com.lemonico.store.service.CustomerDeliveryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogic
 * @description:
 * @create: 2020-06-04 11:07
 **/
@Service
public class CustomerDeliveryImpl implements CustomerDeliveryService
{

    @Autowired
    private CustomerDeliveryDao customerDeliveryDao;

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    @Override
    public List<Mc200_customer_delivery> getCustomerDeliveryList(String client_id, Integer delivery_id, String search) {
        return customerDeliveryDao.getCustomerDeliveryList(client_id, delivery_id, search);
    }

    /**
     * @Description: 顧客配送先マスタ新规
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    @Override
    public Integer insertCustomerDelivery(JSONObject jsonParam) {
        return customerDeliveryDao.insertCustomerDelivery(jsonParam);
    }

    /**
     * @Description: 顧客配送先マスタ更新
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    @Override
    public Integer updateCustomerDelivery(JSONObject jsonParam) {
        return customerDeliveryDao.updateCustomerDelivery(jsonParam);
    }

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Integer
     * @Date: 2020/5/29
     */
    @Override
    public Integer getCustomerDeliveryCount(String client_id, Integer delivery_id, String name) {
        return customerDeliveryDao.getCustomerDeliveryCount(client_id, delivery_id, name);
    }

    @Override
    public Integer getCompanyCustomerDeliveryCount(String client_id, Integer delivery_id, String companyName) {
        return customerDeliveryDao.getCompanyCustomerDeliveryCount(client_id, delivery_id, companyName);
    }
}
