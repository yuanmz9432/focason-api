package com.lemonico.store.dao;



import com.lemonico.common.bean.Tc208_order_cancel;
import com.lemonico.common.bean.Tw200_shipment;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author wang
 * @version 1.0
 * @description 受注失敗履歴・Daoインターフェース
 * @date 2020/06/26
 **/
@Mapper
public interface OrderCancelDao
{

    /**
     * @Description: 受注キャセンル情報登録
     * @Param: tc208_order_cancel
     * @return: Integer
     * @Author: wang
     * @Date: 2021/01/19
     */
    Integer insertOrderCancel(Tc208_order_cancel tc208_order_cancel);

    /**
     * @Description: 受注キャセンル情報更新
     * @Param: tc208_order_cancel
     * @return: Integer
     * @Author: wang
     * @Date: 2021/1/19
     */
    Integer updateOrderCancel(Tc208_order_cancel tc208_order_cancel);

    /**
     * 受注キャセンル情報取得
     *
     * @param client_id
     * @return List of Bean
     * @author wang
     * @date 2021/1/19
     */
    List<Tc208_order_cancel> getOrderCancelList(@Param("client_id") String client_id,
        @Param("outer_order_no") String outer_order_no,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("status") String status);

    /**
     * 受注キャセンル情報取得
     *
     * @param client_id
     * @return List of Bean
     * @author wang
     * @date 2021/1/19
     */
    Integer getOrderCancel(@Param("client_id") String client_id,
        @Param("outer_order_no") String outer_order_no,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * 出庫依頼ID情報取得
     *
     * @param client_id
     * @return List of Bean
     * @author wang
     * @date 2021/1/19
     */
    Tw200_shipment getShipmentList(@Param("client_id") String client_id,
        @Param("order_no") String order_no);

    /**
     * @param client_id : 店铺Id
     * @description: 根据店铺Id获取出库依赖Id
     * @return: java.util.List<java.lang.String>
     * @date: 2021/2/18 15:24
     */
    List<String> getShipmentIdList(@Param("client_id") String client_id);

    /**
     * @param shipment_plan_id : 出库依赖Id
     * @description: 根据出库依赖Id获取出库取消信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/18 16:25
     */
    List<Tc208_order_cancel> getCancelInfo(@Param("shipment_plan_id") String shipment_plan_id);
}
