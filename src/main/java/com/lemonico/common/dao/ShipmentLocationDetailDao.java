package com.lemonico.common.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw212_shipment_location_detail;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細
 * @create: 2020-07-13 18:42
 **/
public interface ShipmentLocationDetailDao
{

    /**
     * @program: sunlogi
     * @description: 出庫作業ロケ明細
     * @create: 2020-07-13 18:42
     **/
    public Integer insertShipmentLocationDetail(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 出庫作業ロケ明細取得
     * @Param:
     * @return: Tw212_shipment_location_detail
     * @Date: 2020/7/20
     */
    public List<Tw212_shipment_location_detail> getShipmentLocationDetail(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 出庫作業ロケ明細削除
     * @Param:
     * @return: Integer
     * @Date: 2020/8/4
     */
    public Integer delShipmentLocationDetail(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id, @Param("product_id") String product_id);
}
