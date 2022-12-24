package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc200_customer_delivery;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 顧客配送先マスタ
 * @Date: 2020/5/29
 */
@Mapper
public interface CustomerDeliveryDao
{

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    List<Mc200_customer_delivery> getCustomerDeliveryList(@Param("client_id") String client_id,
        @Param("delivery_id") Integer delivery_id,
        @Param("search") String search);

    /**
     * @Description: 顧客配送先マスタ新规
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    Integer insertCustomerDelivery(@Param("jsonParam") JSONObject jsonParam);

    /**
     * @Description: 顧客配送先マスタ更新
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    Integer updateCustomerDelivery(@Param("jsonParam") JSONObject jsonParam);

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Integer
     * @Date: 2020/5/29
     */
    Integer getCustomerDeliveryCount(@Param("client_id") String client_id,
        @Param("delivery_id") Integer delivery_id,
        @Param("name") String name);

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Integer
     * @Author: yue_dx
     * @Date: 2021/11/5
     */
    Integer getCompanyCustomerDeliveryCount(@Param("client_id") String client_id,
        @Param("delivery_id") Integer delivery_id,
        @Param("companyName") String companyName);
}
