package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version 1.0
 * @description 受注管理・Daoインターフェース
 * @date 2020/06/18
 **/
@Mapper
public interface OrderDao
{

    /**
     * @return 最新の受注番号
     * @Description 最新受注番号を取得
     * @Param なし
     */
    String getLastPurchaseOrderNo();

    /**
     * @Description: 受注管理登録
     * @Param: Tc200_order
     * @return: Integer
     * @Date: 2020/06/24
     */
    Integer insertOrder(Tc200_order tc200_order);

    /**
     * @Description: 外部受注番号確認
     * @Param: 外部受注番号
     * @return: Integer
     */
    Integer getOuterOrderNo(@Param("outer_order_no") String outer_order_no,
        @Param("client_id") String client_id);

    /**
     * アカウントに紐づいた受注管理テーブルの情報の取得
     *
     * @param client_id
     * @return List<Tc200_order>
     * @date 2020-06-24
     */
    public List<Tc200_order> getOrderList(@Param("client_id") String client_id,
        @Param("outer_order_status") Integer outer_order_status, @Param("del_flg") Integer del_flg,
        @Param("column") String column, @Param("sortType") String sortType,
        @Param("startDate") Date startDate, @Param("endDate") Date endDate,
        @Param("orderType") String orderType, @Param("search") String search,
        @Param("request_date_start") String request_date_start, @Param("request_date_end") String request_date_end,
        @Param("delivery_date_start") String delivery_date_start, @Param("delivery_date_end") String delivery_date_end,
        @Param("form") String form, @Param("identifier") String identifier);

    /**
     * @Param: client_id : 店铺Id
     * @description: 根据店铺Id 获取仓库Id信息
     * @return: java.util.List<java.lang.String>
     * @date: 2020/8/19
     */
    List<String> getWarehouseIdListByClientId(@Param("client_id") String client_id);

    /**
     * @Param: client_id : 店铺Id
     * @description: 根据店铺Id 获取仓新规受付数量
     * @return: java.util.Integer
     * @date: 2022/2/24
     */
    Integer getOrderCount(@Param("client_id") String client_id);

    /**
     * @Param: warehouseIdList : 仓库id
     * @description: 根据仓库Id获取仓库信息
     * @return: java.util.List<com.lemonico.common.bean.Mw400_warehouse>
     * @date: 2020/8/19
     */
    List<Mw400_warehouse> getWarehouseInfoByWarehouseId(@Param("warehouseIdList") List<String> warehouseIdList);

    /**
     * 获取受注信息
     * 
     * @param purchaseOrderNo
     * @return
     */
    Tc200_order getOrderInfoByPurchaseOrderNo(@Param("purchase_order_no") String purchaseOrderNo);

    /**
     * @Param: purchaseOrderNo 受注番号
     * @Param: shipment_plan_id 出库依赖id
     * @description: 更改受注状态
     * @return: java.lang.Integer
     * @date: 2020/8/20
     */
    Integer updateOuterOrderStatus(@Param("purchaseOrderNo") String purchaseOrderNo,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Param: jsonObject : client_id,history_id
     * @description: 根据受注取込履歴ID 获取受注信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    List<Tc200_order> getOrderListByHistoryId(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 更改店铺配送方法
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    Integer changeDeliveryMethod(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param:
     * @description: 获取客户自定义受注csv模板信息
     * @return: list
     * @date: 2020/9/10
     */
    List<Tc204_order_template> getClientTemplate(@Param("client_id") String client_id,
        @Param("company_id") String company_id,
        @Param("template_cd") Integer template_cd);

    /**
     * @Param: jsonObject
     * @description: 获取各公司受注csv模板信息
     * @return: Tc205_order_company
     * @date: 2020/9/15
     */
    List<Tc205_order_company> getCompanyTemplate(@Param("company_id") String company_id);

    /**
     * @Param:
     * @description: 新规店铺受注csv模板信息
     * @return: Tc204_order_template
     * @date: 2020/9/16
     */
    Integer createClientTemplate(Tc204_order_template tc204);

    /**
     * @Param:
     * @description: 编集店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    Integer updateClientTemplate(Tc204_order_template tc204);

    /**
     * @Param:
     * @description: 删除店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    Integer deleteClientTemplate(Integer template_cd);

    /**
     * @Description: 受注取消
     * @Param: 受注番号
     * @return: Integer
     * @Date: 2020/11/12
     */
    Integer orderShipmentsDelete(@Param("purchase_order_no") List<String> purchase_order_no,
        @Param("upd_date") Date upd_date,
        @Param("upd_usr") String upd_usr,
        @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 受注取消
     * @Param: 受注番号
     * @return: Integer
     * @Date: 2020/11/12
     */
    Integer orderDetailDelete(@Param("purchase_order_no") List<String> purchase_order_no,
        @Param("upd_date") Date upd_date,
        @Param("upd_usr") String upd_usr,
        @Param("order_no") String order_no);

    /**
     * @param clientId 店铺ID
     * @param list 外部订单list
     * @description 根据外部受注订单查询订单是否已导入
     * @return: List
     * @date 2021/6/28
     **/
    List<String> getOuterOrderNoListBySpecificNo(@Param("clientId") String clientId, @Param("list") List<String> list);

    /**
     * @description: Apiのテンプレートを取得し
     * @return: java.util.List<java.lang.String>
     * @author: Hzm
     * @date: 2020/12/16
     */
    List<String> getApiTemplates();

    /**
     * 获取API识别番号
     * 
     * @param template
     * @return
     */
    String getApiIdentification(@Param("template") String template);

    /**
     * @Param:
     * @description: 根据shipment_plan_id查询purchase_order_no
     * @return: String
     * @date: 2020/12/29
     */
    String getPurchaseOrderNo(@Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Param:
     * @description: 获取s3设定信息
     * @return: Tc207_order_s3
     * @date: 2021/1/15
     */
    Tc207_order_s3 getS3Setting(@Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 获取s3所有设定信息
     * @return: Tc207_order_s3
     * @date: 2021/1/15
     */
    List<Tc207_order_s3> getS3SettingAll();

    /**
     * @Param:
     * @description: 新规s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    void insertS3Setting(Tc207_order_s3 tc207);

    /**
     * @Param:
     * @description: 更新s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    void updateS3Setting(Tc207_order_s3 tc207);

    /**
     * @Param:
     * @description: 获取api连携平台信息
     * @return:
     * @date: 2020/12/21
     */
    List<Ms013_api_template> getApiStoreInfo(@Param("template") String template);

    /**
     * @Param:
     * @description: 根据识别子获取finishi_flg为0的出库信息
     * @return:
     * @date: 2020/12/21
     */
    List<Tw200_shipment> getShipmentInfoByIdentifier(@Param("identifier") String identifier,
        @Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 乐天根据识别子获取finishi_flg为0的出库信息
     * @return:
     * @date: 2021/03/12
     */
    List<Tw200_shipment> getRkShipmentInfoByIdentifier(@Param("identifier") String identifier,
        @Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 根据出荷id更新finish_flg
     * @return:
     * @date: 2020/12/21
     */
    Boolean updateFinishFlag(@Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: 获取受注时的错误信息
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/26
     */
    List<Tc207_order_error> getOrderErrorMes(@Param("client_id") String client_id,
        @Param("status") Integer status,
        @Param("sort") String sort);

    /**
     * @Description: 更新消息为已读状态
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/27
     */
    Integer updOrderErrorMes(@Param("client_id") String client_id,
        @Param("order_error_no") Integer[] order_error_no);

    /**
     * @param client_id : 店铺Id
     * @param status ： 処理状況 (0:未確認 1確認済)
     * @param sort : 排序
     * @description: 获取不同处理状态的受注取消信息
     * @return: java.util.List<com.lemonico.common.bean.Tc208_order_cancel>
     * @date: 2021/2/7 9:35
     */
    List<Tc208_order_cancel> getOrderCancelMes(@Param("client_id") String client_id,
        @Param("status") int status,
        @Param("sort") String sort);

    /**
     * @param client_id ：店铺Id
     * @param order_cancel_no ： 多个受注番号
     * @description: 更新受注取消为確認済
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 10:37
     */
    Integer updOrderCancelMes(@Param("client_id") String client_id,
        @Param("order_cancel_no") String[] order_cancel_no);

    /**
     * @param tc200_orders : 多条受注信息
     * @description: 批量插入受注信息
     * @return: java.lang.Integer
     * @date: 2021/4/6 12:48
     */
    Integer bulkInsertOrder(@Param("orders") ArrayList<Tc200_order> tc200_orders);

    /**
     * @param orderNoList : 多个受注番号
     * @description: 根据多个受注番号查询 返回多个受注信息
     * @return: java.util.List<com.lemonico.common.bean.Tc200_order>
     * @date: 2021/4/6 15:57
     */
    List<Tc200_order> getOrderInfoByOrderNo(@Param("orderNo") ArrayList<String> orderNoList);

    /**
     * @Description: 外部受注番号確認
     * @Param: 外部受注番号
     * @return: JSON
     */
    List<Tc200_order> getOrderType(@Param("outer_order_no") String outer_order_no,
        @Param("client_id") String client_id);

    /**
     * @param client_id : 店铺ID
     * @param outer_order_no : 外部受注番号
     * @description: 更新为入金済み
     * @return: Integer
     * @author: HZM
     * @date: 2021/4/15
     */
    Integer upOrderType(@Param("client_id") String client_id,
        @Param("outer_order_no") String outer_order_no);

    /**
     * @param client_id : 店铺ID
     * @param outer_order_no : 外部受注番号
     * @description: 获取受注商品明细
     * @return: Tc201_order_detail
     * @author: HZM
     * @date: 2021/4/15
     */
    List<Tc201_order_detail> getOrderProductList(@Param("client_id") String client_id,
        @Param("outer_order_no") String outer_order_no);

    /**
     * 入金待ちの受注データを取得する
     *
     * @param client_id : 店铺ID
     * @return 出庫データリスト
     * @author: YuanMingZe
     * @date: 2021/07/05
     */
    List<Tw200_shipment> getOrderByStatus(@Param("client_id") String client_id);

    /**
     * 受注管理データを削除する
     *
     * @param client_id 店铺ID
     * @param purchase_order_no 受注番号(SunLogi採番)
     * @return void
     * @author: YuanMingZe
     * @date: 2021/07/05
     */
    void orderDelete(@Param("client_id") String client_id,
        @Param("purchase_order_no") String purchase_order_no);

    /**
     * 入金待ちの受注データを取得する
     *
     * @param client_id 店舗ID
     * @param identifier 識別子
     * @return
     */
    List<Tw200_shipment> getUnPaidOrder(@Param("client_id") String client_id,
        @Param("identifier") String identifier);

    /**
     * 外部受注idのリストから、Tc200_orderテーブル内に存在する外部受注idのデータをリストで取得
     *
     * @param orderIdList 外部受注idのリスト
     * @return DBに存在する外部受注idのリスト
     */
    List<String> getExistOrderId(@Param("orderIdList") List<String> orderIdList);

    /**
     * 更新商品税抜金額
     * 
     * @param totalPrice
     * @param client_id
     * @param orderNo
     */
    void setOrderProductTotalPrice(@Param("product_price_excluding_tax") Integer totalPrice,
        @Param("client_id") String client_id,
        @Param("purchase_order_no") String orderNo);

    /**
     * @param client_id : 店铺Id
     * @param historyId : 履历Id
     * @param purchaseOrderNoList : 受注番号集合
     * @description: 更改受注表的履历Id
     * @return: java.lang.Integer
     * @date: 2022/1/17 17:15
     */
    Integer updateHistoryId(@Param("client_id") String client_id,
        @Param("history_id") Integer historyId,
        @Param("purchaseOrderNoList") List<String> purchaseOrderNoList);
}
