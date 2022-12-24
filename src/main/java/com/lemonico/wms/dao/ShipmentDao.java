package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms010_product_size;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw216_delivery_fare;
import com.lemonico.wms.bean.ShimentListBean;
import java.util.Date;
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
public interface ShipmentDao
{

    /**
     * @Description: 確認待ち
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/06
     */
    List<ShimentListBean> getShipmentsLists(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 出庫依頼详细
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2022/01/12
     */
    Tw200_shipment getShipmentsDetail(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 確認待ち
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/06
     */
    public List<Tw200_shipment> getShipmentsList(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @description 根据出库ID获取出库List
     * @param ids 出库ID列表
     * @return List
     * @date 2021/7/19
     **/
    public List<Tw200_shipment> getShipmentsListByPlanIds(@Param("ids") List<String> ids);

    /**
     * @description ntm修改配送公司
     * @param warehouseCd 仓库CD
     * @param shipmentPlanId 出库ID
     * @return null
     * @date 2021/7/19
     **/
    public void changeNtmShipmentDeliveryMethod(@Param("warehouseCd") String warehouseCd,
        @Param("shipmentPlanId") String shipmentPlanId);

    /**
     * @description 更新ntm的eccube数据状态及message
     * @param shipmentPlantId 出库id
     * @param shipmentStatus 出库状态
     * @param statusMessage message
     * @return: null
     * @throws
     * @date 2022/1/10
     **/
    public void changeNtmEccubeStatusMessage(@Param("shipmentPlantId") String shipmentPlantId,
        @Param("shimentStatus") Integer shipmentStatus, @Param("statusMessage") String statusMessage);

    /**
     * @description 更新传票番号
     * @param ids 待更新的出库ID
     * @param num 传票番号
     * @return null
     * @date 2021/7/19
     **/
    public void updateDeliveryTrackingNm(@Param("ids") List<String> ids, @Param("num") String num);

    /**
     * @Description: 出庫配送情報編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    public Integer updateShipmentsDelivery(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 出庫備考欄・出荷指示編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    public Integer updateShipmentsSpecialNotes(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: お届け先編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    public Integer updateShipmentsArrival(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 個口数編集
     * @Param: json
     * @return: json
     * @Author: zhangmj
     * @Date: 2020/11/26
     */
    public Integer updateBoxes(Tw200_shipment shipmentList);

    /**
     * @description 更新运费
     * @param shipment 出库信息
     * @return Integer
     * @date 2021/7/20
     **/
    public Integer updateFreight(Tw200_shipment shipment);

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/15
     */
    public List<Tw200_shipment> getShipmentStatusCount(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 出庫取消件数
     * @Param:
     * @return: Integer
     * @Author: xa_caomeng
     * @Date: 2021/12/12
     */
    public Integer getDeleteShipmentCount(@Param("jsonObject") JSONObject jsonObject);


    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/6/21
     */
    public List<Tw200_shipment> getAllShipmentStatusCount(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: ステータス
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    public Integer setShipmentStatus(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_status") Integer shipment_status, @Param("shipment_plan_id") String[] shipment_plan_id,
        @Param("status_message") String status_message, @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date, @Param("size_cd") String size_cd, @Param("boxes") Integer boxes);

    /**
     * @Description: ステータス
     * @Param:
     * @return: Integer
     * @Date: 2020/07/27
     */
    public Integer setDeliveryTrackingNm(@Param("trackMap") HashMap<String, String> trackMap);

    /**
     * @Description: ステータス
     * @Param:
     * @return: Integer
     * @Date: 2020/07/27
     */
    public Tw200_shipment getShipmentExist(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id, @Param("delivery_carrier") String delivery_carrier);

    /**
     * @Description: ステータス
     * @Param:
     * @return: Integer
     * @Date: 2021/11/18
     */
    public Tw200_shipment getShipmentPlanIdByOrderNo(@Param("warehouse_cd") String warehouse_cd,
        @Param("order_no") String orderNo);

    /**
     * 验证某个出库状态的数据是否存在
     *
     * @param warehouse_cd
     * @param shipment_plan_id
     * @param shipment_status
     * @return
     * @Date: 2021/5/20
     */
    Integer getShipmentListByStatus(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String[] shipment_plan_id,
        @Param("shipment_status") Integer shipment_status);

    /**
     * @Param: shipment_plan_id
     * @param: filePath
     * @description: 梱包作業画像を添付上传
     * @return: java.lang.Integer
     * @date: 2020/9/27
     */
    Integer setConfirmImg(@Param("shipment_plan_id") String[] shipment_plan_id,
        @Param("pdf_confirm_img") String filePath);

    /**
     * @Param: getSizeName
     * @param: size_cd
     * @description: 根据size_cd获取size_name
     * @return: String
     * @date: 2020/10/12
     */
    String getSizeName(String size_cd);

    /**
     * @Param:
     * @param:
     * @description: 根据ShipmentId获取DeliveryCarrier
     * @return: String
     * @date: 2020/12/31
     */
    public String getDeliveryCarrierByShipmentId(@Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/19 9:45
     */
    List<Tw200_shipment> getShipmentCancelCount(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/21
     */
    List<Tw200_shipment> getAllShipmentCancelCount(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id);

    /**
     * @param photography_flg : 撮影状态
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 更改撮影状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 12:53
     */
    Integer updatePhotography(@Param("photography_flg") Integer photography_flg,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @description: 商品サイズ情報を取得
     * @return: java.util.List<com.lemonico.common.bean.Ms010_product_size>
     * @author: wang
     * @date: 2021/4/20
     */
    List<Ms010_product_size> getAllSizeName();

    /**
     * 个口数变更时获取配送会社和都道府県
     * 
     * @param warehouse_cd
     * @param shipment_plan_id
     * @return
     */
    List<Tw200_shipment> getShipmentCarrier(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String[] shipment_plan_id);

    /**
     * @description 根据识别子获取出库List
     * @param warehouse_cd 仓库ＣＤ
     * @param orderNo 受注番号
     * @return List
     * @date 2021/7/20
     **/
    List<Tw200_shipment> getShipmentCarrierByIdentifier(@Param("warehouse_cd") String warehouse_cd,
        @Param("orderNo") String[] orderNo);

    /**
     * 获取配送价格表
     * 
     * @return
     */
    List<Tw216_delivery_fare> getDeliveryFare();

    /**
     * サイズ編集
     *
     * @param shipments
     * @date: 2021/5/17
     */
    Integer updateSize(Tw200_shipment shipments);

    /**
     * @param clientIdList : 店铺ID集合
     * @description: 根据店铺ID 获取特殊的出库依赖信息
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/7/26 16:34
     */
    List<Tw200_shipment> getSpecificShipment(@Param("clientIdList") List<String> clientIdList);

    /**
     * @param shipments : 多个出库依赖信息
     * @description: 批量更新出库依赖中的纳品书URL和领取书URL
     * @return: int
     * @date: 2021/7/26 16:35
     */
    int updateDeliveryUrl(@Param("shipments") List<Tw200_shipment> shipments);

    /**
     * 出庫検品更新シリアル番号
     *
     * @param jsonObject
     * @return
     * @Date: 2021/10/22
     */
    Integer updateSerialNo(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject : warehouse_cd : 倉庫コード shipment_plan_id : 出庫依頼ID product_id : 商品ID serial_no: シリアル番号
     * @description: 获取之前存在的シリアル番号
     * @return: java.util.List<java.lang.String>
     * @date: 2021/11/22 10:59
     */
    List<String> getProductSerialNo(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 出庫検品法人个人统计
     * @Param: warehouse_cd
     * @return: json
     * @Date: 2021/06/09
     */
    List<Tw200_shipment> incidentsCount(@Param("warehouse_cd") String warehouse_cd);

    List<Tw200_shipment> getCheckShipmentCondition(@Param("warehouse_cd") String warehouse_cd,
        @Param("plan_id") String[] plan_id);

    /**
     * @Description: 根据warehouse_cd，warehouse_cd获得ShipmentStatus
     * @Param: warehouse_cd,shipment_plan_id
     * @return: String
     * @Author: caomeng
     * @Date: 2022/01/24
     */
    String getShipmentStatusById(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("warehouse_cd") String warehouse_cd);
}
