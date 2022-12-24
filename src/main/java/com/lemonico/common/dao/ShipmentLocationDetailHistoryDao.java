package com.lemonico.common.dao;



import com.lemonico.common.bean.Tw213_shipment_location_detail_history;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細履歴
 * @create: 2020-07-13 18:42
 **/
public interface ShipmentLocationDetailHistoryDao
{

    /**
     * @Description: 出庫作業ロケ明細履歴
     * @Param: json
     * @return: Integer
     * @Date: 2020/8/3
     */
    public Integer insertShipmentLocationDetailHistory(Tw213_shipment_location_detail_history location_detail_history);
}
