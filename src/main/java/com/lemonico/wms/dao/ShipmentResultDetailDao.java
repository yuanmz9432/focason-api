package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw211_shipment_result_detail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 出庫作業明細
 * @create: 2020-07-08
 **/
@Mapper
public interface ShipmentResultDetailDao
{

    /**
     * @Description: 出荷作業中
     * @Param: Json
     * @return: Integer
     * @Date: 2020/7/8
     */
    public Integer insertShipmentResultDetail(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param: warehouse_cd : 倉庫コード
     * @param: client_id : 店舗ID
     * @param: shipmentPlanId : 出庫依頼ID
     * @description: 获取作業管理ID
     * @return: java.lang.Integer
     * @date: 2020/7/20
     */
    Integer getWorkId(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipmentPlanId") String shipmentPlanId);

    /**
     * @Param: workId： 作業管理ID
     * @description: 根据workId 查询出庫依頼ID
     * @return: java.util.List<java.lang.String>
     * @date: 2020/7/23
     */
    List<String> getShipmentIdListByWorkId(@Param("work_id") List<Integer> work_id);

    /**
     * @param shipmentList : 出库依赖Id集合
     * @param warehouse_cd : 仓库Id
     * @description: 获取出库作业name
     * @return: java.util.List<java.lang.String>
     * @date: 2021/1/15 14:43
     */
    List<String> getWorkName(@Param("shipmentList") List<String> shipmentList,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @param workId : 作业者Id
     * @description: 根据作业者ID获取作业信息
     * @return: java.util.List<com.lemonico.common.bean.Tw211_shipment_result_detail>
     * @date: 2021/2/20 9:56
     */
    List<Tw211_shipment_result_detail> getWorkInfoById(@Param("work_id") Integer workId);
}
