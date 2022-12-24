package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Ms010_product_size;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 出庫
 * @create: 2020-07-06 13:35
 **/
@Mapper
public interface NtmShipmentDao
{

    /**
     * @description: NTM top页法人数据
     * @param clientId 店铺ID
     * @param pendingVerification 状态值
     * @param waitingForAllocation 状态值
     * @param waitingForShipping 状态值
     * @param shipmentPending 状态值
     * @param duringShipmentWork 状态值
     * @param shipped 状态值
     * @return HashMap
     * @date: 2021/05/27
     */
    HashMap<String, Integer> getNtmTopEnterpriseCntByStatus(@Param("clientId") String clientId,
        @Param("pendingVerification") Integer pendingVerification,
        @Param("waitingForAllocation") Integer waitingForAllocation,
        @Param("waitingForShipping") Integer waitingForShipping,
        @Param("shipmentPending") Integer shipmentPending,
        @Param("duringShipmentWork") Integer duringShipmentWork,
        @Param("shipped") Integer shipped);

    /**
     * @description: NTM top页个人数据
     * @param clientId 店铺ID
     * @param todayStartDate 当日开始时间
     * @param todayEndDate 当日结束时间
     * @param tomorrowStartDate 翌日开始时间
     * @param tomorrowEndDate 翌日结束时间
     * @param statusList 状态值
     * @return HashMap
     * @date: 2021/05/27
     */
    HashMap<String, Integer> getNtmTopPersonCntByStatus(@Param("clientId") String clientId,
        @Param("todayStartDate") String todayStartDate,
        @Param("todayEndDate") String todayEndDate,
        @Param("tomorrowStartDate") String tomorrowStartDate,
        @Param("tomorrowEndDate") String tomorrowEndDate,
        @Param("statusList") List<Integer> statusList);

    /**
     * @description: 商品サイズ情報を取得
     * @return: java.util.List<com.lemonico.common.bean.Ms010_product_size>
     * @author: wang
     * @date: 2021/4/20
     */
    List<Ms010_product_size> getAllSizeName();

}
