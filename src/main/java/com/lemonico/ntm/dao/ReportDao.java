package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Tw200_shipment;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: NTM ReportDao
 *               @Date： 2021/4/15
 */
@Mapper
public interface ReportDao
{
    /**
     * @Description: NTM レポート関連_月次出荷明細
     *               @Date： 2021/4/15
     */
    List<Tw200_shipment> getShipmentList(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("search") String search,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime,
        @Param("form") Integer form,
        @Param("type") Integer type,
        @Param("identification") String identification,
        @Param("shipment_status") List<Integer> shipmentStatusList);


    /**
     * @description ntm法人出荷关联
     * @param client_id 店铺ID
     * @param warehouse_cd 仓库CD
     * @param search 检索值
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param form 法人个人表示
     * @param type 请求类型
     * @param identification 识别子
     * @param shipmentStatusList 出库状态
     * @return List
     */
    List<Tw200_shipment> getNtmLegalShipmentList(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("search") String search,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime,
        @Param("form") Integer form,
        @Param("type") Integer type,
        @Param("identification") String identification,
        @Param("shipment_status") List<Integer> shipmentStatusList);

    /**
     * @Description: //TODO 出荷确定处理
     *               @Date： 2021/4/14
     *               @Param： client_id, warehouse_cd, shipment_plan_date
     *               @return： JSONObject
     */
    Integer updateDeliveryHandle(@Param("checkList") List<HashMap<String, Object>> checkList,
        @Param("upd_date") Date upd_date);

    /**
     * @description: 根据条件获取有效在库数
     * @param clientId 店铺id
     * @param productId 商品id
     * @param warehouseCd 仓库id
     * @return: String
     * @date: 2021/4/25
     **/
    String getAvailableCntByCondition(@Param("clientId") String clientId,
        @Param("productId") String productId,
        @Param("warehouseCd") String warehouseCd);

    /**
     * @description 更新 有效在库数 和 出庫依頼中数
     * @param cnt 变更数量
     * @param clientId 店铺id
     * @param productId 商品id
     * @param warehouseCd 仓库id
     * @return: void
     * @date 2021/4/25
     **/
    void updateStockAvailableCntAndRequestingCnt(@Param("cnt") String cnt,
        @Param("clientId") String clientId,
        @Param("productId") String productId,
        @Param("warehouseCd") String warehouseCd);

    /**
     * @description 根据仓库id获取货架优先顺序
     * @param warehouseCd 仓库id
     * @return: HashMap
     * @date 2021/4/25
     **/
    HashMap<String, String> getLocationPriorityByWarehouseCd(String warehouseCd);

    /**
     * @description 根据日期获取个口数数据
     * @param clientId 店铺id
     * @param warehouseCd 仓库id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param deliveryCarrier 配送方式
     * @return: List
     * @date 2021/4/25
     **/
    List<Tw200_shipment> getBoxesBySealDate(@Param("clientId") String clientId,
        @Param("warehouseCd") String warehouseCd,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("deliveryCarrier") String deliveryCarrier);
}
