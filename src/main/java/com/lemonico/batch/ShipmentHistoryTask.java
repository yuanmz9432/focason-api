package com.lemonico.batch;

import static java.util.stream.Collectors.toList;

import com.lemonico.batch.dao.ShipmentHistoryDao;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw201_shipment_detail;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import java.util.List;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: ShipmentHistoryTask
 * @description: 对出库完了或者出库取消3月以上的数据进行备份
 * @date: 2022/2/23 9:31
 **/
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShipmentHistoryTask
{

    private final static Logger logger = LoggerFactory.getLogger(ShipmentHistoryTask.class);

    @Resource
    private ShipmentHistoryDao shipmentHistoryDao;

    // //@Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void getShipmentData() throws Exception {

        logger.info("出库表数据备份开始执行, 时间:" + DateUtils.getDate());
        // 查询出库完了或者出库取消3月以上的数据
        List<Tw200_shipment> shipmentList = shipmentHistoryDao.getShipmentHistory();
        int shipmentCount = shipmentList.size();
        if (shipmentCount == 0) {
            logger.info("没有符合条件的数据");
            return;
        }

        // 插入到出库履历表
        try {
            Integer historyCount = shipmentHistoryDao.insertShipmentsHistory(shipmentList);
            if (StringTools.isNullOrEmpty(historyCount) || historyCount != shipmentCount) {
                logger.info("TW202履历表插入失败：出库件数:" + shipmentCount + ", 插入件数:" + historyCount);
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }

        // 查询出库明细信息
        List<String> shipmentPlanIdList =
            shipmentList.stream().map(Tw200_shipment::getShipment_plan_id).distinct().collect(toList());
        List<Tw201_shipment_detail> shipmentDetailList =
            shipmentHistoryDao.getShipmentDetailHistory(shipmentPlanIdList);
        int shipmentDetailCount = shipmentDetailList.size();
        if (shipmentDetailCount == 0) {
            logger.warn("TW201出库明细表数据为空，数据备份失败");
            throw new Exception();
        }

        // 插入到出库履历明细表
        try {
            Integer historyDetailCount = shipmentHistoryDao.insertShipmentDetailHistory(shipmentDetailList);
            if (StringTools.isNullOrEmpty(historyDetailCount) || historyDetailCount != shipmentDetailCount) {
                logger.info("TW203履历明细表插入失败：出库明细件数:" + shipmentDetailCount + ", 插入件数:" + historyDetailCount);
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }

        // 删除出库表
        try {
            Integer shipmentDelCount = shipmentHistoryDao.delShipment(shipmentPlanIdList);
            if (StringTools.isNullOrEmpty(shipmentDelCount) || shipmentDelCount != shipmentCount) {
                logger.info("TW200出库表删除失败：出库件数:" + shipmentCount + ", 删除件数:" + shipmentDelCount);
                throw new Exception();
            }
        } catch (Exception e) {
            logger.info("TW200出库表删除失败");
            throw new Exception();
        }

        // 删除出库明细表
        try {
            Integer shipmentDetailDelCount = shipmentHistoryDao.delShipmentDetail(shipmentPlanIdList);
            if (StringTools.isNullOrEmpty(shipmentDetailDelCount) || shipmentDetailDelCount != shipmentDetailCount) {
                logger.info("TW201出库明细表删除失败：出库件数:" + shipmentDetailCount + ", 删除件数:" + shipmentDetailDelCount);
                throw new Exception();
            }
        } catch (Exception e) {
            logger.info("TW201出库表删除失败");
            throw new Exception();
        }

        logger.info("出库表数据备份结束执行, 出库件数: " + shipmentCount + ",时间:" + DateUtils.getDate());

    }
}
