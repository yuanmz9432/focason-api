package com.lemonico.common.service;



import com.lemonico.common.bean.Tw212_shipment_location_detail;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細履歴
 * @create: 2020-08-04
 **/
public interface ShipmentLocationDetailHistoryService
{

    /**
     * @Description: 出庫作業ロケ明細履歴
     * @Param: json
     * @return: Integer
     * @Date: 2020/8/3
     */
    public Integer insertShipmentLocationDetailHistory(HttpServletRequest httpServletRequest,
        Tw212_shipment_location_detail locationDetail, String status);

}
