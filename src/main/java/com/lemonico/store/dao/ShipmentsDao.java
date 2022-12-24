package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms010_product_size;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw201_shipment_detail;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 店舗側出庫情報
 * @create: 2020-05-12 10:31
 **/
@Mapper
public interface ShipmentsDao
{

    /**
     * 获取出库依赖详细
     * 
     * @param client_id
     * @param shipment_plan_id
     * @return
     */
    Tw200_shipment getShipmentsDetail(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 店舗側出庫情報一覧
     * @Param: 顧客CD， 出庫依頼ID
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: List
     * @Date: 2020/05/12
     */
    public List<Tw200_shipment> getShipmentsList(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: 店舗側出庫情報新規
     * @Param: Tw200_shipment
     * @return: Integer
     * @Date: 2020/05/13
     */
    public Integer insertShipments(@Param("jsonParam") JSONObject jsonParam);

    /**
     * @Description: 店舗側出庫情報新規
     * @Param: Tw200_shipment
     * @return: Integer
     * @Date: 2020/05/13
     */
    public Integer updateShipments(@Param("jsonParam") JSONObject jsonParam);

    /**
     * @Description: 店舗側出庫情報取得
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer countShipments(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer deleteShipments(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("upd_date") Date upd_date,
        @Param("upd_usr") String upd_usr);

    /**
     * @Description: 出库一览恢复删除
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2021/9/18
     */
    public Integer recoverShipments(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("status_message") String status_message);

    /**
     * @Param: shipment_plan_id
     * @param: filePath
     * @description: PDF添付上传
     * @return: java.lang.Integer
     * @date: 2020/9/27
     */
    Integer setConfirmPdf(@Param("shipment_plan_id") String[] shipment_plan_id,
        @Param("pdf_name") String filePath);

    /**
     * @Param: getSizeName
     * @param: size_cd
     * @description: 根据size_cd获取size_name
     * @return: String
     * @date: 2020/10/12
     */
    String getSizeName(String size_cd);

    /**
     * @Description: ステータス
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    public Integer setShipmentStatus(@Param("warehouse_cd") String warehouse_cd,
        @Param("shipment_plan_id") String shipment_plan_id, @Param("shipment_status") Integer shipment_status,
        @Param("status_message") String status_message, @Param("product_plan_total") Integer product_plan_total,
        @Param("total_price") Integer total_price, @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date);

    public List<Tw200_shipment> getShipmentShopifyList(@Param("client_id") String client_id,
        @Param("template") String template);

    public Integer setShipmentFinishFlg(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd, @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 出库编辑删除时查询该商品引当的订单
     * @Param: 店舗ID、商品ID
     * @return: List<Tw200_shipment>
     * @Date: 2020/12/18
     */
    List<Tw200_shipment> shipmentReserve(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param shipment_plan_id : 出库依赖Id
     * @description: 根据出库依赖Id查询出庫管理テーブル信息
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/2/9 11:13
     */
    List<Tw200_shipment> getShipmentInfoByShipmentId(@Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param:
     * @description: 出库取消后更新引当数
     * @return:
     * @date: 2021/2/19
     */
    Integer updateReserve_cnt(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("product_id") String product_id,
        @Param("reserve_cnt") Integer reserve_cnt);

    /**
     * @param:
     * @description: 更新引当状态
     * @return:
     * @date: 2021/2/20
     */
    Integer updateReserve_status(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("product_id") String product_id,
        @Param("reserve_status") Integer reserve_status);

    /**
     * @param:
     * @description: 更新出庫状态
     * @return:
     * @date: 2021/2/20
     */
    Integer updateShipmentStatus(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("shipment_status") Integer shipment_status);

    /**
     * @param:
     * @description: 更新出庫状态和ステータス理由
     * @return:Integer
     * @date: 2021/11/25
     */
    Integer updateShipmentStatusMessage(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("client_id") String client_id,
        @Param("shipment_status") Integer shipment_status,
        @Param("status_message") String status_message);

    /**
     * @Param: jsonParam : 添付ファイル路径写入
     * @description: 添付ファイル路径写入
     * @return: Integer
     * @date: 2021/3/18
     */
    public Integer saveDeliveryFilePath(@Param("jsonParam") JSONObject jsonParam);

    /**
     * @Description: GMO請求情報を取得
     * @Param: 店舗ID
     * @return: List<Tw200_shipment>
     * @Author: HuangZhimin
     * @Date: 2021/4/16
     */
    public List<Tw200_shipment> getGMOBillBarcode(@Param("client_id") String client_id);

    /**
     * @Description: GMO請求情報を更新
     * @Param: 店舗ID
     * @return: List<Tw200_shipment>
     * @Author: HuangZhimin
     * @Date: 2021/4/16
     */
    public Integer updateGMOBillBarcode(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("bill_barcode") String bill_barcode);

    /**
     * @description: 商品サイズ情報を取得
     * @return: java.util.List<com.lemonico.common.bean.Ms010_product_size>
     * @author: wang
     * @date: 2021/4/20
     */
    List<Ms010_product_size> getAllSizeName();

    /**
     * @param warehouseId : 仓库Id
     * @param clientId : 店铺Id
     * @param productIdList : 多个商品Id信息
     * @description: 根据多个商品Id获取出库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/7/9 12:50
     */
    List<Tw200_shipment> getShipmentInfoByProductId(@Param("warehouse_cd") String warehouseId,
        @Param("client_id") String clientId,
        @Param("productIdList") List<String> productIdList);

    /**
     * @param clientId : 店铺Id
     * @param productId : 商品Id信息
     * @description: 根据商品Id获取出库信息
     * @return: java.util.List<com.lemonico.common.bean.tw200_shipment>
     * @date: 2022/2/21 12:50
     */
    List<Tw201_shipment_detail> getShipmentdetailProductId(@Param("client_id") String clientId,
        @Param("productId") List<String> productId);

    /**
     * @param shipment : 出库依赖对象
     * @description: 修改出库依赖信息
     * @return: java.lang.Integer
     * @date: 2021/7/15 15:11
     */
    Integer updateShipmentInfo(Tw200_shipment shipment);

    /**
     * @param:
     * @description: 获取可以合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    List<Tw200_shipment> getMergeShipmentList(@Param("client_id") String client_id,
        @Param("search") String search,
        @Param("shipment_status") List<Integer> shipment_status);

    /**
     * @param:
     * @description: 修改要合并出库依赖的del_flg
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    Integer updateChildrenShipment(@Param("shipmentIds") HashMap<String, String> shipmentIds);

    /**
     * @param:
     * @description: 变更tw201明细表 被合并依赖ID变更为主依赖ID
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    Integer mergeShipment(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("childrenShipments") List<String> childrenShipments);

    /**
     * @param:
     * @description: 变更tw200 主依赖状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    Integer updateMainShipment(Tw200_shipment tw200);

    /**
     * @param:
     * @description: 获取合并出库依赖明细
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    List<Tw200_shipment> getChildShipment(@Param("client_id") String client_id,
        @Param("childrenShipments") List<String> childrenShipments);

    /**
     * @param:
     * @description: 变更tw201明细表 重复的明细信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    Integer deleteExistsDetail(@Param("existsProduct") HashMap<String, String> existsProduct);

    /**
     * @Description: //更新TW201表相同明细的依赖数
     *               @Date： 2021/6/30
     * @Param：
     * @return：
     */
    Integer updateProductQuantity(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id,
        @Param("productQuantity") List<Map<String, Object>> productQuantity);

    Tw200_shipment getShipmentOrderInfo(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param client_id
     * @param shipment_plan_id
     * @return
     * @description 获取入金等待出库依赖配送时间带
     * @date: 2021/8/30 16:35
     */
    String getShipmentDeliveryTimeSlot(@Param("client_id") String client_id,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @param client_id : 店铺Id
     * @param shipmentIdList : 出库依赖Id集合
     * @description: 获取多个出库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw200_shipment>
     * @date: 2021/9/7 10:02
     */
    List<Tw200_shipment> getShipmentInfoList(@Param("client_id") String client_id,
        @Param("shipmentIdList") List<String> shipmentIdList);

    /**
     * @Description: Qoo10店舗の送状番号連携情報を取得
     * @Param: client_id, template
     * @return: List<Tw200_shipment>
     * @Date: 2021/09/07
     */
    public List<Tw200_shipment> getShipmentListQoo10(@Param("client_id") String client_id,
        @Param("template") String template);

    /**
     * @param shipmentIdList : 出库依赖Id集合
     * @description: 根据出库依赖Id 获取到所有的出库状态
     * @return: java.lang.Integer
     * @date: 2021/9/22 17:29
     */
    Integer getShipmentStatusByIdList(@Param("shipmentIdList") List<String> shipmentIdList);

    /**
     * 判断出库状态
     * 
     * @param client_id
     * @param shipment_status
     * @param shipment_plan_id
     * @return
     */
    Integer getShipmentStatus(@Param("client_id") String client_id,
        @Param("shipment_status") List<Integer> shipment_status,
        @Param("shipment_plan_id") String shipment_plan_id);
}
