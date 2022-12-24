package com.lemonico.batch.dao;



import com.lemonico.batch.bean.*;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw300_stock;
import com.lemonico.common.bean.Tw301_stock_history;
import java.util.List;

/**
 * @className: CheckDataDao
 * @description: 整理 SUNLOGI 的错误数据
 * @date: 2021/12/10 9:52
 **/
public interface CheckDataDao
{

    /**
     * @description: 在库表和货架表在库数不一致数据获取
     * @return: java.util.List<com.lemonico.batch.bean.Tw300Mw405InconsistentInventory>
     * @date: 2021/12/10 10:10
     */
    List<Tw300Mw405InconsistentInventory> getTw300Mw405InconsistentInventory();

    /**
     * @description: 在库表的实际在库，理论在库，依赖中，不可配送在库数对不上
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2021/12/13 9:47
     */
    List<Tw300_stock> getWrongNumberInStock();

    /**
     * @description: 实际在库，依赖中，不可配送在库数出现负数
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2021/12/13 13:37
     */
    List<Tw300_stock> getInventoryTable();

    /**
     * @description: 没有出荷作业开始，但是tw212状态为出荷作业中的数据
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/12/13 14:03
     */
    List<Tw200_shipment> getNoWorkStarted();

    /**
     * @description: 出荷作业中，货架履历表没有数据
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/12/13 14:24
     */
    List<Tw200_shipment> getNoDataInTheJob();

    /**
     * @description: 出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致
     * @return: java.util.List<com.lemonico.batch.bean.Mw405ErrorData>
     * @date: 2021/12/13 14:45
     */
    List<Mw405ErrorData> getMw405ErrorDataList();

    /**
     * @description: 还在出库的商品被删除
     * @return: java.util.List<com.lemonico.batch.bean.AbnormalProducts>
     * @date: 2021/12/13 14:57
     */
    List<AbnormalProducts> getAbnormalProducts();

    /**
     * @description: 出库依赖和出库依赖明细的删除状态不一致
     * @return: java.util.List<com.lemonico.batch.bean.Tw200Tw201DifferentDelFlg>
     * @date: 2022/2/16 10:41
     */
    List<Tw200Tw201DifferentDelFlg> getTw200Tw201DifferentDelFlgs();

    /**
     * @description: 在库表依赖中数和出库详细表统计出来的依赖数不一致
     * @return: java.util.List<com.lemonico.batch.bean.Tw300ErrorRequestCnt>
     * @date: 2022/2/16 11:08
     */
    List<Tw300ErrorRequestCnt> getErrorRequestCnts();

    /**
     * @description: 获取前一天的在库履历数据
     * @return: java.util.List<com.lemonico.common.bean.Tw301_stock_history>
     * @date: 2022/2/22 9:27
     */
    List<Tw301_stock_history> getStockHistories();
}
