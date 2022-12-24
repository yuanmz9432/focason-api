package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw201_shipment_detail;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 出庫明細
 * @create: 2020-05-13 10:21
 **/
@Mapper
public interface ShipmentDetailDao
{

    /**
     * @Description: 出庫明細テーブル一覧
     * @Param:
     * @return: List
     * @Date: 2020/5/13
     */
    public List<Tw201_shipment_detail> getShipmentDetailList(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("cancel") boolean cancel);

    /**
     * @Description: 出庫明細商品テーブル一覧
     * @Param:
     * @return: List
     * @Date: 2021/7/9
     */
    public List<Tw201_shipment_detail> getShipmentProductList(@Param("client_id") String client_id,
        @Param("shipmentIdList") List<String> shipmentIdList,
        @Param("cancel") boolean cancel);

    /**
     * @Description: 出庫明細テーブル新規
     * @Param: Tw201_shipment_detail
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer insertShipmentDetail(@Param("detailJson") JSONObject detailJson);

    /**
     * @Description: 出庫明細テーブル新規
     * @Param: Tw201_shipment_detail, detailJson
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer updateShipmentDetail(@Param("detailJson") JSONObject detailJson);

    /**
     * @Description: 出庫明細テーブル削除
     * @Param: 顧客CD client_id, 出庫依頼ID shipment_plan_id
     * @return: Integer
     * @Date: 2020/5/14
     */
    public Integer deleteShipmentDetail(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("upd_date") Date upd_date,
        @Param("upd_usr") String upd_usr);

    /**
     * @Description: 出庫明細テーブル削除
     * @Param: 顧客CD client_id, 出庫依頼ID shipment_plan_id
     * @return: Integer
     * @Date: 2020/5/14
     */
    public Integer deleteShipmentProduct(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("product_id") String product_id,
        @Param("upd_date") Date upd_date, @Param("upd_usr") String upd_usr,
        @Param("set_sub_id") Integer set_sub_id);

    /**
     * @Param: warehouseId : 倉庫コード
     * @param: shipment_plan_id : 出庫依頼ID
     * @Param: product_id : 商品ID
     * @description: 查询出庫明細テーブル
     * @return: java.util.List<com.lemonico.common.bean.Tw201_shipment_detail>
     * @date: 2020/7/26
     */
    List<Tw201_shipment_detail> getShipmentDetailById(@Param("warehouse_cd") String warehouseId,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("product_id") String product_id,
        @Param("cancel") boolean cancel);

    /**
     * @Param:
     * @description: 获取商品出库依赖明细
     * @return:
     * @date: 2020/08/13
     */
    public List<Tw201_shipment_detail> getShipmentDetail(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Param: product_id : 商品Id
     * @Param: reserve_status ：引当状态
     * @Param: client_id ：店铺Id
     * @description: 根据商品Id获取该出库依赖的引当数
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2020/11/30
     */
    List<Tw201_shipment_detail> getShipmentReserveCnt(@Param("product_id") String product_id,
        @Param("reserve_status") Integer reserve_status,
        @Param("client_id") String client_id);

    /**
     * @Param: shipment_plan_id : 出库依赖Id
     * @param: product_id：商品Id
     * @param: client_id： 店铺Id
     * @param: reserve_cnt： 引当数
     * @param: reserve_status： 引当状态
     * @description: 修改商品的引当数和引当状态
     * @return: java.lang.Integer
     * @date: 2020/11/30
     */
    Integer updateReserveStatus(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("product_id") String product_id,
        @Param("client_id") String client_id,
        @Param("reserve_cnt") int reserve_cnt,
        @Param("reserve_status") int reserve_status,
        @Param("set_sub_id") Integer set_sub_id);


    /**
     * @Param: shipmentId : 出库依赖Id
     * @description: 判断出库依赖状态是否可以改变
     * @return: java.lang.Integer
     * @date: 2020/11/30
     */
    Integer updateShipmentStatus(@Param("shipment_plan_id") String shipmentId);

    /**
     * @Param: warehouse_cd ：仓库Id
     * @Param: client_id ： 店铺ID
     * @Param: reserve_status ： 引当状态
     * @Description: 获取该店铺不同引当状态的数据
     * @Return: java.util.List<com.lemonico.common.bean.Tw201_shipment_detail>
     * @Date: 2020/12/3
     */
    List<Tw201_shipment_detail> getReserveStatusList(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("reserve_status") int reserve_status);

    /**
     * 获取出库依赖中商品的引当数
     *
     * @param client_id
     * @param product_id
     * @return
     * @Date: 2021/4/21
     */
    List<Tw201_shipment_detail> getProductReserveList(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param shipmentPlanList : 出库依赖集合
     * @description: 根据出库依赖查询出库依赖明细的所有数据
     * @return: java.util.List<com.lemonico.common.bean.Tw201_shipment_detail>
     * @date: 2021/6/29 22:18
     */
    List<Tw201_shipment_detail> getShipmentDetails(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("shipmentPlanList") List<String> shipmentPlanList);

    /**
     * @param shipmentPlanId : 修改后的出库依赖Id
     * @param client_id : 店铺Id
     * @param shipment_plan_id : 修改前的出库依赖Id
     * @param idList : 出库依赖明细管理Id
     * @description: 修改出库管理明细表的出库依赖Id
     * @return: java.lang.Integer
     * @date: 2021/7/15 16:01
     */
    Integer updateShipmentId(@Param("newShipmentId") String shipmentPlanId,
        @Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("idList") List<Integer> idList,
        @Param("name") String loginNm,
        @Param("date") Date nowTime);

    List<Tw201_shipment_detail> getShipmentDetailKubun(@Param("shipmentIdList") List<String> shipmentIdList);

    /**
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param shipmentIds : 出库依赖Id集合
     * @param kubun : 商品区分
     * @param loginNm : 操作者
     * @param date : 日期
     * @description: 更改出库依赖明细中的 kubun 为普通商品
     * @return: java.lang.Integer
     * @date: 2021/8/9 17:59
     */
    Integer updateShipmentDetailKubun(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("shipmentIds") List<String> shipmentIds,
        @Param("kubun") int kubun,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);

    List<Tw201_shipment_detail> getShipmentDetailByProductId(@Param("client_id") String client_id,
        @Param("productIdList") List<String> productIdList);

    /**
     * @Description: 出库详细恢复删除
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2021/9/18
     */
    public Integer recoverShipments(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 将该出库依赖下的所有商品的シリアル番号清空
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 12:52
     */
    Integer emptySerialNo(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param shipmentId : 出库依赖Id
     * @param clientId : 店铺Id
     * @param productId : 商品Id
     * @description: 获取出库详细信息
     * @return: java.util.List<com.lemonico.common.bean.Tw201_shipment_detail>
     * @date: 2022/2/22 10:42
     */
    List<Tw201_shipment_detail> getShipmentDetailsById(@Param("shipment_plan_id") String shipmentId,
        @Param("client_id") String clientId,
        @Param("product_id") String productId);

}
