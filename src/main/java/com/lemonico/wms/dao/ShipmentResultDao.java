package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw210_shipment_result;
import com.lemonico.common.bean.Tw212_shipment_location_detail;
import com.lemonico.common.bean.Tw214_total_picking;
import com.lemonico.common.bean.Tw215_total_picking_detail;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 出庫作業管理テーブル
 * @create: 2020-07-08
 **/
@Mapper
public interface ShipmentResultDao
{

    /**
     * @Description: 出荷作業中
     * @Param: Json
     * @return: Integer
     * @Date: 2020/7/8
     */
    public Integer insertShipmentResult(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 作業管理IDを取得
     * @Param: null
     * @return: Integer
     * @Date: 2020/7/9
     */
    public Integer getWorkId();

    /**
     * @Description: 作業管理
     * @Param: null
     * @return: List
     * @Date: 2020/7/14
     */
    public List<Tw210_shipment_result> getWorkNameList(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id);

    /**
     * @Param: warehouse_cd ： 倉庫コード
     * @param: client_id ： 店舗ID
     * @param: shipmentPlanId ： 出庫依頼ID
     * @param: product_id ： 商品ID
     * @description: 查询 出庫作業ロケ明細
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2020/7/27
     */
    List<Tw212_shipment_location_detail> getShipmentLocationDetail(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipmentPlanId") List<String> shipmentPlanId,
        @Param("product_id") String product_id);

    /**
     * @Param: warehouse_cd ： 倉庫コード
     * @param: client_id ： 店舗ID
     * @param: shipmentPlanId ： 出庫依頼ID
     * @param: product_id ： 商品ID
     * @description: 查询 出庫作業ロケ明細
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2020/12/22
     */
    List<Tw212_shipment_location_detail> getShipmentWorkLocation(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Description: 出库完了时，修改状态为1
     * @Param: warehouse_cd ： 倉庫コード
     * @Param: product_id ： 商品ID
     * @return: Integer
     * @Date: 2020/12/23
     */
    Integer updateLocationStatus(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("id") Integer id);

    /**
     * @Param: workName : 作業管理 ,shipment_status ：処理ステータス
     * @description: 根据作业name获取作业者Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    List<Integer> getWorkIdByName(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("work_name") String work_name,
        @Param("shipment_status") String shipment_status);

    /**
     * @Param: workId : 作業管理ID
     * @param: warehouse_cd : 倉庫コード
     * @description: 根据workId 改修 処理ステータス
     * @return: java.lang.Integer
     * @date: 2020/8/3
     */
    Integer updateShipmentResultStatusByWorkId(@Param("work_id") Integer workId,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param: workId : 作業管理ID
     * @param: warehouse_cd : 倉庫コード
     * @description: 删除出庫作業管理テーブル的数据
     * @return: java.lang.Integer
     * @date: 2020/8/4
     */
    Integer deleteShipmentResultInfo(@Param("work_id") Integer workId,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param: workId : 作業管理ID
     * @param: warehouse_cd : 倉庫コード
     * @param: client_id : 店舗ID
     * @param: shipmentId : 出庫依頼ID
     * @description: 删除出庫作業明細的数据
     * @return: java.lang.Integer
     * @date: 2020/8/4
     */
    Integer deleteShipmentResultDetailInfo(@Param("work_id") Integer workId,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipmentId);

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipmentIdList : 出库依赖Id 集合
     * @description: 查询以商品Id为单位的 出库依赖集合
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2021/2/8 10:55
     */
    List<Tw212_shipment_location_detail> getPickingList(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipmentIdList") List<String> shipmentIdList);

    /**
     * @param idList : 出庫作業ロケ明細 Id
     * @description: 修改 トータルピッキング确认状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 15:46
     */
    Integer updatePinkingStatus(@Param("idList") ArrayList<Integer> idList);

    /**
     * @param idList :出庫作業ロケ明細 Id
     * @description: 根据出庫作業ロケ明細 Id 获取以商品id为单位的出库依赖信息
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2021/2/8 16:01
     */
    List<Tw212_shipment_location_detail> getPickingListById(@Param("idList") ArrayList<Integer> idList);

    /**
     * @param json : トータルピッキング新数据
     * @description: 新规 トータルピッキング
     * @return: java.lang.Integer
     * @date: 2021/2/8 17:02
     */
    Integer insertTotalPicking(@Param("json") JSONObject json);

    /**
     * @param json : トータルピッキング詳細新数据
     * @description: 新规 トータルピッキング詳細
     * @return: java.lang.Integer
     * @date: 2021/2/8 17:04
     */
    Integer insertTotalPickingDetail(@Param("json") JSONObject json);

    /**
     * @param warehouse_cd : 仓库Id
     * @description: 根据仓库Id获取到出库货架明细的出库依赖Id
     * @return: java.util.List<java.lang.String>
     * @date: 2021/2/9 9:04
     */
    List<String> getShipmentId(@Param("warehouse_cd") String warehouse_cd);

    /**
     * @param shipmentId : 出库依赖Id
     * @param warehouse_cd : 仓库Id
     * @description: 根据出库依赖Id 获取到作业者信息
     * @return: java.util.List<com.lemonico.common.bean.Tw210_shipment_result>
     * @date: 2021/2/9 9:11
     */
    List<Tw210_shipment_result> getWorkInfo(@Param("shipmentId") List<String> shipmentId,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @param warehouse_cd ：仓库id
     * @param start ： 起始时间
     * @param end : 结束时间
     * @description: 出庫依頼ごとトータルピッキングレポート
     * @return: java.util.List<com.lemonico.common.bean.Tw214_total_picking>
     * @date: 2021/2/9 16:57
     */
    List<Tw214_total_picking> getTotalPickingList(@Param("warehouse_cd") String warehouse_cd,
        @Param("start") Timestamp start,
        @Param("end") Timestamp end);

    List<Tw214_total_picking> getPickingInsUser(@Param("warehouse_cd") String warehouse_cd,
        @Param("start") Timestamp start,
        @Param("end") Timestamp end);


    List<Tw215_total_picking_detail> getPickingDetail(@Param("total_picking_id") List<Integer> total_picking_id,
        @Param("ins_usr") String ins_usr);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param shipmentPlanList : 出库依赖Id集合
     * @description: 根据出库依赖ID集合 获取全部的212 数据
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2021/10/13 10:35
     */
    List<Tw212_shipment_location_detail> getLocationDetailList(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("shipmentPlanList") List<String> shipmentPlanList);
}
