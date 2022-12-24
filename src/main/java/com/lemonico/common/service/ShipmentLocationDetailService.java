package com.lemonico.common.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw212_shipment_location_detail;
import java.util.List;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細
 * @create: 2020-07-13 18:42
 **/
public interface ShipmentLocationDetailService
{

    /**
     * @program: sunlogi
     * @description: 出庫作業ロケ明細
     * @create: 2020-07-13 18:42
     **/
    public Integer insertShipmentLocationDetail(List<JSONObject> locationList);

    /**
     * @Description: 出庫作業ロケ明細列表
     * @Param: 倉庫コード，出庫依頼ID
     * @return: Tw212_shipment_location_detail
     * @Date: 2020/8/3
     */
    public List<Tw212_shipment_location_detail> getShipmentLocationDetail(String warehouse_cd, String shipment_plan_id);

    /**
     * @Description: 出庫作業ロケ明細削除
     * @Param:
     * @return: Integer
     * @Date: 2020/8/4
     */
    public Integer delShipmentLocationDetail(String warehouse_cd, String client_id, String shipment_plan_id,
        String product_id);

}
