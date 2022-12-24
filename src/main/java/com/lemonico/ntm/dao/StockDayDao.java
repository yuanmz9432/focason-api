package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Tw304_stock_day;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: StockDayDao
 * @description: tw304连携数据库
 * @date: 2021/3/9 14:13
 **/
@Mapper
public interface StockDayDao
{
    /**
     * @param stockDays : 在库数数据
     * @description: 将数据每天存到Tw304表里面
     * @date: 2021/3/9 15:01
     */
    void insertAllData(@Param("stockDays") List<Tw304_stock_day> stockDays);

    /**
     * @param minDate : 起始时间
     * @param maxDate : 结束时间
     * @param warehouseId : 仓库ID
     * @param clientId : 店铺ID
     * @description: 取得某个时间多内所有的数据
     * @return: java.util.List<com.lemonico.common.bean.Tw304_stock_day>
     * @date: 2021/3/10 12:34
     */
    List<Tw304_stock_day> getAllData(@Param("minDate") String minDate,
        @Param("maxDate") String maxDate,
        @Param("warehouse_cd") String warehouseId,
        @Param("client_id") String clientId);

    /**
     * @param localDates : 日期集合
     * @param warehouse_id : 仓库ID
     * @param client_id : 店铺ID
     * @description: 获取某些日期的所有数据
     * @return: java.util.List<com.lemonico.common.bean.Tw304_stock_day>
     * @date: 2021/4/7 12:35
     */
    List<Tw304_stock_day> getDataByStockDate(@Param("stock_date") List<String> localDates,
        @Param("warehouse_cd") String warehouse_id,
        @Param("client_id") String client_id);

    /**
     * @description 获取指定时间段的商品出库数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param clinetId 店铺ID
     * @param warehouseCd 仓库CD
     * @return List
     * @date 2021/8/4
     **/
    List<Map<String, String>> getTotalOfShipmentsNumWithTime(@Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("clientId") String clinetId,
        @Param("warehouseCd") String warehouseCd);

    List<Map<String, String>> getTotalOfShipmentsNumWithTime2(@Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("clientId") String clinetId,
        @Param("warehouseCd") String warehouseCd);
}
