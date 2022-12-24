package com.lemonico.common.service.impl;



import com.lemonico.common.bean.Tw212_shipment_location_detail;
import com.lemonico.common.bean.Tw213_shipment_location_detail_history;
import com.lemonico.common.dao.ShipmentLocationDetailHistoryDao;
import com.lemonico.common.service.ShipmentLocationDetailHistoryService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細履歴
 * @create: 2020-08-04
 **/
@Service
public class ShipmentLocationDetailHistoryServiceImpl implements ShipmentLocationDetailHistoryService
{

    @Resource
    private ShipmentLocationDetailHistoryDao shipmentLocationDetailHistoryDao;

    /**
     * @Description: 出庫作業ロケ明細履歴
     * @Param: json
     * @return: Integer
     * @Date: 2020/8/3
     */
    @Override
    public Integer insertShipmentLocationDetailHistory(HttpServletRequest httpServletRequest,
        Tw212_shipment_location_detail locationDetail, String status) {
        Date nowTime = DateUtils.getDate();
        String user = CommonUtils.getToken("user_id", httpServletRequest);

        Tw213_shipment_location_detail_history shipmentLocationDetailHistories =
            new Tw213_shipment_location_detail_history();

        shipmentLocationDetailHistories.setWarehouse_cd(locationDetail.getWarehouse_cd());
        shipmentLocationDetailHistories.setClient_id(locationDetail.getClient_id());
        shipmentLocationDetailHistories.setShipment_plan_id(locationDetail.getShipment_plan_id());
        shipmentLocationDetailHistories.setProduct_id(locationDetail.getProduct_id());
        shipmentLocationDetailHistories.setLocation_id(locationDetail.getLocation_id());
        shipmentLocationDetailHistories.setStatus(status);
        shipmentLocationDetailHistories.setProduct_plan_cnt(locationDetail.getProduct_plan_cnt());
        shipmentLocationDetailHistories.setInventory_cnt(locationDetail.getInventory_cnt());
        shipmentLocationDetailHistories.setReserve_cnt(locationDetail.getReserve_cnt());
        shipmentLocationDetailHistories.setIns_date(nowTime);
        shipmentLocationDetailHistories.setIns_usr(user);
        shipmentLocationDetailHistories.setUpd_date(nowTime);
        shipmentLocationDetailHistories.setUpd_usr(user);

        return shipmentLocationDetailHistoryDao.insertShipmentLocationDetailHistory(shipmentLocationDetailHistories);
    }
}
