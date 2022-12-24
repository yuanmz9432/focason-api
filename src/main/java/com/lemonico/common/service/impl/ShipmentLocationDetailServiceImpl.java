package com.lemonico.common.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw405_product_location;
import com.lemonico.common.bean.Tw212_shipment_location_detail;
import com.lemonico.common.dao.ShipmentLocationDetailDao;
import com.lemonico.common.service.ShipmentLocationDetailService;
import com.lemonico.wms.dao.StocksResultDao;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細
 * @create: 2020-07-13 18:42
 **/
@Service
public class ShipmentLocationDetailServiceImpl implements ShipmentLocationDetailService
{

    @Resource
    private ShipmentLocationDetailDao shipmentLocationDetailDao;

    @Resource
    private StocksResultDao stocksResultDao;

    /**
     * @program: sunlogi
     * @description: 出庫作業ロケ明細
     * @create: 2020-07-13 18:42
     **/
    @Override
    public Integer insertShipmentLocationDetail(List<JSONObject> locationList) {
        int count = locationList.size();
        int result = 0;
        for (int j = 0; j < count; j++) {
            JSONObject locationJson = locationList.get(j);
            result += shipmentLocationDetailDao.insertShipmentLocationDetail(locationJson);
            // 更改货架的出库依赖数
            String location_id = locationJson.getString("location_id");
            String product_id = locationJson.getString("product_id");
            String client_id = locationJson.getString("client_id");
            String warehouse_cd = locationJson.getString("warehouse_cd");
            List<Mw405_product_location> locationProduct =
                stocksResultDao.getLocationProduct(warehouse_cd, location_id, product_id, null, null, client_id);
            if (locationProduct.size() != 0) {
                // 获取到之前的货架信息
                Mw405_product_location productLocation = locationProduct.get(0);
                // 获取到之前的依赖中的数
                Integer requesting_cnt = productLocation.getRequesting_cnt();
                Integer requestingCnt = locationJson.getInteger("requesting_cnt");
                locationJson.put("requesting_cnt", requesting_cnt + requestingCnt);
                stocksResultDao.updateLocationRequestingCnt(locationJson);
            }
        }

        if (result == count) {
            return result;
        }
        return 0;
    }

    /**
     * @Description: 出庫作業ロケ明細列表
     * @Param: 倉庫コード，出庫依頼ID
     * @return: Tw212_shipment_location_detail
     * @Date: 2020/8/3
     */
    @Override
    public List<Tw212_shipment_location_detail> getShipmentLocationDetail(String warehouse_cd,
        String shipment_plan_id) {
        return shipmentLocationDetailDao.getShipmentLocationDetail(warehouse_cd, shipment_plan_id);
    }

    /**
     * @Description: 出庫作業ロケ明細削除
     * @Param:
     * @return: Integer
     * @Date: 2020/8/4
     */
    @Override
    public Integer delShipmentLocationDetail(String warehouse_cd, String client_id, String shipment_plan_id,
        String product_id) {
        return shipmentLocationDetailDao.delShipmentLocationDetail(warehouse_cd, client_id, shipment_plan_id,
            product_id);
    }
}
