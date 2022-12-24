package com.lemonico.core.utils;



import com.lemonico.common.bean.Mw405_product_location;
import com.lemonico.common.bean.Tw300_stock;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.store.dao.StockDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.service.WarehousingResultService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: updateNotDelivery
 * @description: 更改在库表的不可配送数
 * @date: 2021/6/26 18:13
 **/
@Component
@EnableScheduling
public class UpdateNotDelivery
{

    private final static Logger logger = LoggerFactory.getLogger(UpdateNotDelivery.class);

    @Resource
    private StockDao stockDao;

    @Resource
    private StocksResultDao stocksResultDao;

    @Resource
    private WarehousingResultService warehousingResultService;

    /**
     * @param changeStatus : 1: 不可出库 2: 可以出库
     * @param client_id : 店铺Id
     * @param warehouse_cd : 仓库Id
     * @param request : 请求
     * @description: 由于货架出库状态变更 做出相应的操作
     * @return: void
     * @date: 2021/10/8 14:29
     */
    @Transactional
    public void changeLocationStatus(int changeStatus, String client_id, String warehouse_cd,
        HttpServletRequest request, String locationId, String product_id) {

        // 获取到货架上面存放的商品信息
        List<Mw405_product_location> locationDetails =
            stocksResultDao.getLocationDetailById(Collections.singletonList(locationId), client_id, product_id);
        if (StringTools.isNullOrEmpty(locationDetails) || locationDetails.isEmpty()) {
            logger.error("货架Id={} 没有任何商品={}的信息", locationId, product_id);
            return;
        }

        if (locationDetails.size() > 1) {
            logger.warn("错误数据，同一个店铺 同一个货架 同一个商品有条数据 需要调查，店铺Id={} 货架Id={} 商品Id={}",
                client_id, locationId, product_id);
            return;
        }

        // 获取到该商品在货架上面存放的信息
        Mw405_product_location productLocation = locationDetails.get(0);

        if (StringTools.isNullOrEmpty(client_id)) {
            client_id = productLocation.getClient_id();
        }

        // 获取该商品的在库信息
        List<Tw300_stock> stocksByProductIds =
            stockDao.getStocksByProductIds(client_id, warehouse_cd, Collections.singletonList(product_id));

        // if (stocksByProductIds.isEmpty()) {
        // logger.warn("店铺Id={} 商品Id={} 没有任何在库信息", client_id, product_id);
        // return;
        // }
        // if (stocksByProductIds.size() > 1) {
        // logger.warn("店铺Id={} 商品Id={} 在库表里面有多条数据 属于错误数据", client_id, product_id);
        // return;
        // }

        Tw300_stock stock = stocksByProductIds.get(0);

        // 在库表的不可配送数
        int tw300NotDelivery = !StringTools.isNullOrEmpty(stock.getNot_delivery()) ? stock.getNot_delivery() : 0;
        // 在库表的理论在库数
        int tw300AvailableCnt = !StringTools.isNullOrEmpty(stock.getAvailable_cnt()) ? stock.getAvailable_cnt() : 0;
        // 在库表修改后的理论在库数
        int afterAvailableCnt;
        // 在库表修改后的不可配送数
        int afterNotDelivery;
        // 货架修改后的不可配送数
        int locationNotDelivery;


        Date nowTime = DateUtils.getNowTime(null);
        String loginNm = CommonUtils.getToken("loginNm", request);
        if (changeStatus == 1) {
            // 不可出库

            // 货架的不可配送数 （货架之前的实际在库数）
            int locationNotDeliveryCnt = productLocation.getStock_cnt();

            // 货架的不可配送数
            locationNotDelivery = locationNotDeliveryCnt;

            // 在库表的不可配送数 = 原有的不可配送数 + 本次货架改修后的不可配送数 - 货架之前的不可配送数
            afterNotDelivery = tw300NotDelivery + locationNotDeliveryCnt - productLocation.getNot_delivery();
            // 在库表的理论在库数 = 原有的理论在库数 - 本次货架改修后的不可配送数 + 货架之前的不可配送数
            afterAvailableCnt = tw300AvailableCnt - locationNotDeliveryCnt + productLocation.getNot_delivery();
        } else {
            // 有出库不可改变为出荷可
            // 本次变更后可出库的数 = 货架上面的实际在库数
            int deliveryCnt = productLocation.getStock_cnt();

            // 判断包含该商品出库依赖明细的状态是否可以修改
            warehousingResultService.judgeReserveStatus(warehouse_cd, client_id, product_id, deliveryCnt, request);

            // 在库表的不可配送数 = 原有的不可配送数 - 本次货架移动后释放的不可配送数
            afterNotDelivery = stock.getNot_delivery() - productLocation.getNot_delivery();

            // 在库表的理论在库数 = 实际在库数 - 出荷依赖数 - 在库表的不可配送数
            afterAvailableCnt = stock.getInventory_cnt() - stock.getRequesting_cnt() - Math.max(afterNotDelivery, 0);

            // 货架的不可配送数
            locationNotDelivery = 0;
        }

        try {
            // 更改货架的不可配送数
            stocksResultDao.updateLocationNotDelivery(Collections.singletonList(locationId), client_id, product_id,
                locationNotDelivery, loginNm, nowTime);
        } catch (Exception e) {
            logger.error("修改货架的不可配送数, 货架Id={} 店铺Id={} 商品Id={}", locationId, client_id, product_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        // 理论在库数
        stock.setAvailable_cnt(afterAvailableCnt);
        // 不可配送数
        stock.setNot_delivery(Math.max(afterNotDelivery, 0));
        // 更新者
        stock.setUpd_usr(loginNm);
        // 更新日時
        stock.setUpd_date(nowTime);
        // 更改在库表的不可配送数
        try {
            stockDao.updateNotDelivery(Collections.singletonList(stock));
        } catch (Exception e) {
            logger.error("修改在库表的不可配送数失败, 店铺Id={}，商品Id={}", client_id, product_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        // }


    }
}
