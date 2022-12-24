package com.lemonico.batch.dao;



import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw201_shipment_detail;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ShipmentHistoryDao
 * @description: 生成出库履历数据
 * @date: 2022/2/23 9:31
 **/
public interface ShipmentHistoryDao
{

    /**
     * 查询出库3月或者删除3月以上的数据进行备份
     * 
     * @return
     */
    List<Tw200_shipment> getShipmentHistory();

    /**
     * 查询出库3月或者删除3月以上的数据进行备份
     * 
     * @return
     */
    List<Tw201_shipment_detail> getShipmentDetailHistory(@Param("shipmentPlanIdList") List<String> shipmentPlanIdList);

    /**
     * 删除旧数据
     */
    Integer delShipment(@Param("shipmentPlanIdList") List<String> shipmentPlanIdList);

    /**
     * 删除旧数据
     */
    Integer delShipmentDetail(@Param("shipmentPlanIdList") List<String> shipmentPlanIdList);

    /**
     * 插入出库履历表
     * 
     * @param shipments
     * @return
     */
    Integer insertShipmentsHistory(@Param("shipments") List<Tw200_shipment> shipments);

    /**
     * 插入出库履历明细表
     * 
     * @param shipmentDetails
     */
    Integer insertShipmentDetailHistory(@Param("shipmentDetails") List<Tw201_shipment_detail> shipmentDetails);
}
