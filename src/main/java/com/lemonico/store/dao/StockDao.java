package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @className: StockDao
 * @description: StockDao
 * @date: 2020/05/28
 **/
public interface StockDao
{

    /**
     * @Description: 在庫一覧
     * @Param:
     * @return: List
     * @Date: 2020/05/28
     */
    List<Mc100_product> getStockList(@Param("client_id") String client_id,
        @Param("product_id") String[] product_id,
        @Param("search") String search,
        @Param("tags_id") String tags_id,
        @Param("stock_search") Integer stock_search);

    /**
     * @Description: 在庫補充設定
     * @Param:
     * @return: Integer
     * @Date: 2020/05/28
     */
    Integer updateReplenishCount(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("replenish_cnt") Integer replenish_cnt);

    /**
     * @Description: 更新出庫依頼中数
     * @Param:
     * @return: Integer
     * @Date: 2020/07/18
     */
    Integer updateStockRequestingCnt(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("requesting_cnt") Integer requesting_cnt,
        @Param("available_cnt") Integer available_cnt,
        @Param("ins_usr") String ins_usr,
        @Param("ins_date") Date ins_date);

    /**
     * @Description: 在庫履歴
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: List
     * @Date: 2020/05/29
     */
    List<Tw301_stock_history> getStockHistoryList(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("proudct_id") String proudct_id,
        @Param("search") String search,
        @Param("tags_id") String tags_id,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime,
        @Param("type") Integer type);

    /**
     * @Description: 在庫商品のtagsとtags_idを取得する
     * @Param:
     * @return: List
     * @Date: 2020/05/29
     */
    List<Mc101_product_tag> getStockTags(@Param("client_id") String client_id);

    /**
     * @description: 获取最大在库Id
     * @return: java.lang.String
     * @date: 2020/07/03
     */
    String getMaxStockId();

    /**
     * @Param: jsonObject : client_id, warehouse_cd
     * @param: itemsJSONObject : product_id
     * @description: 查询在库数据
     * @return: com.lemonico.common.bean.Tw300_stock
     * @date: 2020/07/03
     */
    Tw300_stock getStockInfoById(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param jsonObject : warehouse_cd(仓库Id) client_id(店铺Id)
     * @param available_cnt : 理论在库数
     * @param inventory_cnt : 实际在库数
     * @param not_delivery : 不可配送数
     * @param product_id : 商品Id
     * @param weight : 重量
     * @param sizeId : 尺寸Id
     * @param date : 当前日期
     * @param loginNm : 操作者
     * @description: 更改在库数据
     * @return: java.lang.Integer
     * @date: 2021/10/20 13:50
     */
    Integer updateStockInfo(@Param("jsonObject") JSONObject jsonObject, @Param("available_cnt") Integer available_cnt,
        @Param("inventory_cnt") int inventory_cnt, @Param("not_delivery") int not_delivery,
        @Param("product_id") String product_id, @Param("product_weight") Double weight,
        @Param("product_size_cd") String sizeId, @Param("date") Date date,
        @Param("loginNm") String loginNm);

    /**
     * @param jsonObject : jsonObject： stock_id（在库Id） warehouse_cd（仓库Id） client_id（店铺Id）
     * @param product_id : 商品Id
     * @param available_cnt : 理论在库数
     * @param inventory_cnt : 实际在库数
     * @param notDelivery : 不可配送数
     * @param sizeId : 尺寸Id
     * @param weight : 重量
     * @param requesting_cnt : 依赖中数
     * @param loginNm : 操作者
     * @param date : 当前时间
     * @description: 添加在库数据
     * @return: java.lang.Integer
     * @date: 2021/10/20 13:15
     */
    Integer insertStockInfo(@Param("jsonObject") JSONObject jsonObject, @Param("product_id") String product_id,
        @Param("available_cnt") Integer available_cnt, @Param("inventory_cnt") int inventory_cnt,
        @Param("not_delivery") int notDelivery, @Param("product_size_cd") String sizeId,
        @Param("product_weight") Double weight, @Param("requesting_cnt") Integer requesting_cnt,
        @Param("loginNm") String loginNm, @Param("date") Date date);

    /**
     * @description: 获取最大在庫履歴ID
     * @return: java.lang.String
     * @date: 2020/07/03
     */
    String getMaxStockHistoryId();

    /**
     * @param: jsonObject
     * @param: product_id
     * @param: stockHistoryId
     * @param: type
     * @param: inventory_cnt
     * @description: 添加在庫履歴
     * @return: java.lang.Integer
     * @date: 2020/07/03
     */
    Integer insertStockHistory(@Param("plan_id") String id, @Param("client_id") String client_id,
        @Param("product_id") String product_id, @Param("history_id") String stockHistoryId,
        @Param("type") Integer type, @Param("quantity") int inventory_cnt, @Param("loginNm") String loginNm,
        @Param("date") Date date, @Param("info") String info);

    /**
     * @param client_id : 店舗ID
     * @param id : 入庫依頼ID
     * @param product_id : 商品ID
     * @param num : 入庫実績数
     * @param location_id : ロケーションID
     * @param lot_no : ロット番号
     * @param date : 获取当前时间
     * @param loginNm : 用户名
     * @param bestBeforeDate : 过期时间
     * @param shipping_flag : 出荷フラグ
     * @description: 将入库信息存到入庫作業ロケ明細表里面
     * @return: java.lang.Integer
     * @date: 2020/8/12
     */
    Integer insertWarehouseLocationDetail(@Param("client_id") String client_id, @Param("id") String id,
        @Param("product_id") String product_id, @Param("product_cnt") Integer num,
        @Param("locationId") String location_id, @Param("lot_no") String lot_no,
        @Param("date") Date date, @Param("loginNm") String loginNm,
        @Param("bestBeforeDate") Date bestBeforeDate,
        @Param("shipping_flag") int shipping_flag);

    /**
     * @Param: inventoryNum : 実在庫数
     * @param: requesting_cnt ： 出庫依頼中数
     * @param: warehouseId ： 倉庫コード
     * @param: client_id ： 店舗ID
     * @param: product_id ： 商品ID
     * @param: date ： 更新日時
     * @description: 出库后更改在库数
     * @return: java.lang.Integer
     * @date: 2020/7/26
     */
    Integer updateStockNum(@Param("warehouse_cd") String warehouseId, @Param("client_id") String client_id,
        @Param("product_id") String product_id, @Param("inventory_cnt") Integer inventoryNum,
        @Param("requesting_cnt") Integer requesting_cnt, @Param("available_cnt") Integer available_cnt,
        @Param("not_delivery") Integer not_delivery,
        @Param("upd_date") Date date, @Param("upd_usr") String upd_usr);

    /**
     * @Param: client_id : 店舗ID
     * @param: id : 入庫依頼ID
     * @param: product_id : 商品ID
     * @param: productTotal : 入庫実績数
     * @param: location_id : ロケーションID
     * @param: date : 更新日時
     * @param: loginNm : 更新者
     * @description: 更新入庫作業ロケ明細的入庫実績数
     * @return: java.lang.Integer
     * @date: 2020/8/13
     */
    Integer updateWarehouseLocationDetail(@Param("client_id") String client_id, @Param("id") String id,
        @Param("product_id") String product_id, @Param("productTotal") Integer productTotal,
        @Param("location_id") String location_id, @Param("date") Date date, @Param("loginNm") String loginNm);

    /**
     * @description: 获取所有在库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2021/1/12 14:51
     */
    List<Tw300_stock> getAllAvailableCnt();

    /**
     * @description: 获取当天没有同步到tw304中的在库数据
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2022/1/13 15:32
     */
    List<Tw300_stock> getTodayAvailableData(@Param("stockDate") String stockDate);

    /**
     * @param productIdList 商品ID
     * @param warehouseCd 仓库CD
     * @param clientId 店铺ID
     * @return List
     * @description 获取商品数
     * @date 2021/8/4
     **/
    List<Tw300_stock> getStockCntByProductList(@Param("clientId") String clientId,
        @Param("warehouseCd") String warehouseCd,
        @Param("productIdList") List<String> productIdList);

    /**
     * @param clientId : 店铺Id
     * @param startDate : 起始时间
     * @param endDate : 结束时间
     * @description: 获取固定时间内的在库履历信息
     * @return: java.util.List<com.lemonico.common.bean.Tw301_stock_history>
     * @date: 2021/3/10 13:21
     */
    List<Tw301_stock_history> getHistoryList(@Param("client_id") String clientId,
        @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * @param clientId : 店铺id
     * @param productIdList : 多个商品Id
     * @param type : 在库状态 (出库或者入库)
     * @description: 获取商品的出库或者入库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw301_stock_history>
     * @date: 2021/3/10 14:04
     */
    List<Tw301_stock_history> getShipmentHistory(@Param("client_id") String clientId,
        @Param("productIdList") List<String> productIdList,
        @Param("shipment_type") int type);

    /**
     * @description: 获取店铺所有在库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @author: wang
     * @date: 2021/3/25 14:51
     */
    List<Tw300_stock> getAllAvailableCntNew(@Param("client_id") String client_id);

    /**
     * @description: 根据client_id, plan_id删除在库履历tw301
     * @return:
     * @date: 2021/3/9
     */
    Integer deleteStockHistory(@Param("client_id") String client_id, @Param("plan_id") String plan_id);

    /**
     * @param locationDetail : 货架商品详细信息集合
     * @description: 获取多个商品的在库信息
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2021/7/5 15:30
     */
    List<Tw300_stock> getStockListByLocation(@Param("locationDetail") List<Mw405_product_location> locationDetail);

    /**
     * @param tw300StockArrayList : 在库对象集合
     * @description: 更改多条在库信息的不可配送数和理论在库数
     * @return: java.lang.Integer
     * @date: 2021/7/5 17:08
     */
    Integer updateNotDelivery(@Param("stockList") List<Tw300_stock> tw300StockArrayList);

    /**
     * 商品在庫情報取得
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫ID
     * @param productIds 商品IDリスト
     * @return Tw300_stockレコードリスト
     */
    List<Tw300_stock> getStocksByProductIds(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("productIds") List<String> productIds);

    /**
     * @param locationIdList : 货架ID 集合
     * @param warehouse_cd : 仓库ID
     * @description: 根据货架Id 获取相应的货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw404_location>
     * @date: 2021/8/3 14:03
     */
    List<Mw404_location> getLocationInfoByIdList(@Param("locationIdList") List<String> locationIdList,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * 获取出入实际的货架信息
     *
     * @param client_id
     * @param id
     * @param product_id
     * @return
     */
    List<Tw113_warehousing_location_detail> getWarehouseLocationInfo(@Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("product_id") String product_id);
}
