package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: StocksResultDao
 * @description: 在库Dao层
 * @date: 2020/07/06
 **/
@Mapper
public interface StocksResultDao
{

    /**
     * @Param:
     * @description: 获取父货架中所有location信息
     * @return: List
     * @date: 2020/07/06
     */
    public List<Mw404_location> getLocationList(@Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param:
     * @description: 货架info设定
     * @return: Integer
     * @date: 2020/07/07
     */
    public Integer setLocationInfo(@Param("warehouse_cd") String warehouse_cd,
        @Param("location_id") String location_id,
        @Param("info") String info);

    /**
     * @Param:
     * @description: 获取单个货架中的商品信息
     * @return: List
     * @date: 2020/07/07
     */
    public List<Mw405_product_location> getLocationProduct(@Param("warehouse_cd") String warehouse_cd,
        @Param("location_id") String location_id,
        @Param("product_id") String product_id,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 获取商品的货架信息
     * @return: List
     * @date: 2020/07/17
     */
    public List<Mw405_product_location> getLocationInfo(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Param:
     * @description: 盘点开始时新规登录盘点表(tw302_stock_management)
     * @return: Integer
     * @date: 2020/07/10
     */
    public Integer createProductCheck(Tw302_stock_management tw302_stock_management);

    /**
     * @Param:
     * @description: 盘点开始时新规登录盘点明细表(tw303_stock_detail)
     * @return: Integer
     * @date: 2020/07/10
     */
    public Integer createProductCheckDetail(Tw303_stock_detail tw303_stock_detail);

    /**
     * @Param:
     * @description: 获取最大的manage_id
     * @return: Integer
     * @date: 2020/07/10
     */
    public Integer getMaxMid();

    /**
     * @Param:
     * @description: 获取盘点明细表数据(tw303_stock_detail)
     * @return: List
     * @date: 2020/07/10
     */
    public List<Tw303_stock_detail> getStockDetail(@Param("client_id") String client_id,
        @Param("manage_id") Integer manage_id);

    /**
     * @Param:
     * @description: 更新盘点在库实际数(tw303_stock_detail)
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer updateStockDetailCount(@Param("product_id") String product_id,
        @Param("count") Integer count);

    /**
     * @Param:
     * @description: 变更盘点状态
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer changeStockCheckState(@Param("manage_id") Integer manage_id,
        @Param("state") Integer state);

    /**
     * @Param:
     * @description: 获取盘点管理信息
     * @return: List
     * @date: 2020/07/13
     */
    public List<Tw302_stock_management> getStockCheckManageInfo(@Param("state") Integer state);

    /**
     * @Param:
     * @description: 更新盘点结束日期
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer updateStockCheckEndDate(@Param("manage_id") Integer manage_id,
        @Param("end_date") Date end_date);

    /**
     * @Param:
     * @description: 盘点结束后更新理论在库数和実在庫数
     * @return: Integer
     * @date: 2020/07/14
     */
    public Integer updateStockCount(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("available_cnt") Integer available_cnt,
        @Param("inventory_cnt") Integer inventory_cnt);

    /**
     * @Param:
     * @description: 检测是否有盘点未完成
     * @return: Integer
     * @date: 2020/07/13
     */
    public String stockCheckExist(@Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 统计作业中数
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer getCheckingCount(String warehouse_cd);

    /**
     * @Param:
     * @description: 新规货架
     * @return: Integer
     * @date: 2020/07/15
     */
    public Integer createNewLocation(Mw404_location mw404_location);

    /**
     * @Param:
     * @description: 获取仓库最大的货架ID
     * @return: Integer
     * @date: 2020/07/15
     */
    public List<String> getMaxLocationId(String warehouse_cd);

    /**
     * @Param:
     * @description: 检验货架名是否重复
     * @return: String
     * @date: 2020/07/17
     */
    public String checkLocationNameExists(@Param("wh_location_nm") String wh_location_nm,
        @Param("lot_no") String lot_no,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param:
     * @description: 检验仓库货架优先顺序是否重复
     * @return: String
     * @date: 2020/08/18
     */
    public Integer checkLocationPriorityExists(@Param("priority") Integer priority,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param: location_id : ロケーションID
     * @description: 根据货架Id 获取货架名称
     * @return: java.lang.String
     * @date: 2020/7/20
     */
    Mw404_location getLocationName(@Param("warehouse_cd") String warehouse_cd,
        @Param("location_id") String location_id);

    /**
     * @Param: size_cd : サイズCD
     * @description: 根据size_cd 获取 尺寸信息
     * @return: java.lang.String
     * @date: 2020/7/20
     */
    String getSizeType(String size_cd);

    /**
     * @Param:
     * @description: 货架信息修改
     * @return: Integer
     * @date: 2020/07/22
     */
    public Integer updateLocationInfo(Mw404_location mw404_location);

    /**
     * @Param: location_id : ロケーションID
     * @param: client_id : 店舗ID
     * @param: product_id : 商品ID
     * @description: 获取到该商品在该货架上面的信息
     * @return: com.lemonico.common.bean.Mw405_product_location
     * @date: 2020/7/27
     */
    Mw405_product_location getLocationById(@Param("location_id") String location_id,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @param stockCnt : 在库数
     * @param requestCnt : 依赖中数
     * @param not_delivery : 不可配送数
     * @param location_id : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param upd_str : 修改者
     * @param upd_date : 修改时间
     * @description: 修改商品在货架上面存放的数量
     * @return: java.lang.Integer
     * @date: 2020/7/27 10:57
     */
    Integer updateLocationCnt(@Param("stock_cnt") Integer stockCnt, @Param("requesting_cnt") Integer requestCnt,
        @Param("not_delivery") int not_delivery, @Param("location_id") String location_id,
        @Param("client_id") String client_id, @Param("product_id") String product_id,
        @Param("upd_usr") String upd_str, @Param("upd_date") Date upd_date);

    /**
     * @Param:
     * @description: 获取当前仓库货架的最大优先顺序
     * @return: Integer
     * @date: 2020/08/14
     */
    public Integer getMaxPriority(String warehouse_cd);

    /**
     * @param: warehouse_cd
     * @param: defaultLocationName
     * @description: 获取店铺优先顺位
     * @return: java.lang.Integer
     * @date: 2020/11/17
     */
    Integer getLocationPriority(@Param("warehouse_cd") String warehouse_cd,
        @Param("wh_location_nm") String defaultLocationName);

    /**
     * @param column : 排序的字段
     * @param location_id : 货架Id
     * @param sortType : 排序方式
     * @param warehouse_cd : 仓库Id
     * @param search : 搜索关键字
     * @description: 获取货架location信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/22 13:25
     */
    List<Mw404_location> getProductLocationList(@Param("location_id") String location_id,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("search") String search);

    /**
     * @param locationIdList : 货架Id集合
     * @param warehouse_cd : 仓库Id
     * @description: 获取到货架的基本信息
     * @return: java.util.List<com.lemonico.common.bean.Mw404_location>
     * @date: 2021/6/22 13:31
     */
    List<Mw404_location> getLocationInfoById(@Param("locationIdList") List<String> locationIdList,
        @Param("warehouse_cd") String warehouse_cd);


    /**
     * @param productIdList : 商品Id集合
     * @description: 根据商品Id获取货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/6/26 18:07
     */
    List<Mw405_product_location> getLocationProductById(@Param("productIdList") List<String> productIdList);

    /**
     * @param warehouse_cd : 仓库Id
     * @param column : 需要排序的 字段名称
     * @param sortType : 排序的方式
     * @param search : 搜索内容
     * @description: 获取在库一览
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/6/25 9:42
     */
    List<Mw405_product_location> getStockList(@Param("warehouse_cd") String warehouse_cd,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("search") String search,
        @Param("stock_flg") String stock_flg,
        @Param("client_id") String client_id);

    /**
     * @param stockHistory
     * @description: 新规在库履历信息
     * @return: java.lang.Integer
     * @date: 2021/6/26 18:06
     */
    Integer insertStockHistory(Tw301_stock_history stockHistory);

    /**
     * @param locationNm : 货架名称
     * @param lot_no : ロット番号
     * @param warehouse_cd : 仓库Id
     * @description: 根据货架名称 和 ロット番号 获取货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw404_location>
     * @date: 2021/6/26 18:06
     */
    List<Mw404_location> getLocationByName(@Param("wh_location_nm") String locationNm,
        @Param("lot_no") String lot_no,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @param product_location
     * @description: 新规商品货架信息
     * @return: java.lang.Integer
     * @date: 2021/6/26 18:05
     */
    Integer createProductLocation(Mw405_product_location product_location);

    /**
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param date : 今天的日期
     * @description: 查询可以出库的货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw404_location>
     * @date: 2021/6/26 18:05
     */
    List<Mw404_location> getLocationOrder(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("date") Date date);

    /**
     * @param json
     * @description: 更改货架的出库依赖数
     * @return: java.lang.Integer
     * @date: 2021/6/26 18:04
     */
    Integer updateLocationRequestingCnt(@Param("json") JSONObject json);


    /**
     * @param locationIdList : 货架ID
     * @description: 查询到货架上面存放的商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/8/31 13:40
     */
    List<Mw405_product_location> getLocationDetail(@Param("locationIdList") List<String> locationIdList);

    /**
     * @param locationId : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param not_delivery : 不可配送数
     * @param upd_usr : 操作者
     * @param upd_date : 修改时间
     * @description: 更改货架上面的不可配送数
     * @return: java.lang.Integer
     * @date: 2021/8/31 13:40
     */
    Integer updateLocationNotDelivery(@Param("location_id") List<String> locationId,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("not_delivery") int not_delivery,
        @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date);

    /**
     * @param warehouse_cd : 仓库Id
     * @param column : 需要排序的 字段名称
     * @param sortType : 排序的方式
     * @param client_id : 店铺Id
     * @param stockFlg : 是否在库
     * @description: 获取商品在库一览
     * @return: java.util.List<com.lemonico.common.bean.Tw300_stock>
     * @date: 2021/8/31 13:41
     */
    List<Tw300_stock> getProductStockList(@Param("warehouse_cd") String warehouse_cd,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("stockFlg") int stockFlg,
        @Param("client_id") String client_id,
        @Param("search") String search);

    /**
     * @param productIdList : 商品Id集合
     * @param warehouse_cd : 仓库Id
     * @description: 根据商品Id 获取货架上面该商品的信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/8/31 14:44
     */
    List<Mw405_product_location> getLocationByProductId(@Param("productIdList") List<String> productIdList,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @param warehouse_cd : 仓库Id
     * @param locationNm : 货架名称
     * @description: 根据或货架名称获取货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/10 1:17
     */
    List<Mw404_location> getLocationInfoByName(@Param("warehouse_cd") String warehouse_cd,
        @Param("wh_location_nm") String locationNm);

    /**
     * @param client_id : 店铺Id
     * @param location_id : 货架Id
     * @param product_id : 商品Id
     * @param beforeDate : 过期时间
     * @param changeStatus : 出荷不可
     * @param loginNm : 操作者
     * @param nowTime : 当前时间
     * @description: 更改货架明细中指定商品的过期时间和出荷不可
     * @return: java.lang.Integer
     * @date: 2021/10/22 14:14
     */
    Integer updateLocationDetail(@Param("client_id") String client_id,
        @Param("location_id") String location_id,
        @Param("product_id") String product_id,
        @Param("bestbefore_date") Date beforeDate,
        @Param("status") int changeStatus,
        @Param("upd_usr") String loginNm,
        @Param("upd_date") Date nowTime);

    /**
     * @param locationIdList : 货架Id 集合
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @description: 根据货架ID 店铺Id 商品Id 获取商品在货架上面的详细信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/10/13 9:34
     */
    List<Mw405_product_location> getLocationDetailById(@Param("locationIdList") List<String> locationIdList,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @param locationIdList : 货架Id集合
     * @param updUsr : 修改人
     * @param updDate : 修改时间
     * @description: 修改货架的不可配送数为默认值
     * @return: java.lang.Integer
     * @date: 2021/10/15 12:38
     */
    Integer updateNotDeliveryDefault(@Param("locationIdList") List<String> locationIdList,
        @Param("upd_usr") String updUsr,
        @Param("upd_date") Date updDate);

    /**
     * @param location_id : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @description: 物理删除货架上面的商品信息
     * @return: java.lang.Integer
     * @date: 2021/10/15 13:22
     */
    Integer deleteLocationDetail(@Param("location_id") String location_id,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @param stock_cnt : 实际在库数
     * @param requesting_cnt : 依赖中数 (-1：不需要变更)
     * @param not_delivery : 不可配送数 (-1：不需要变更)
     * @param location_id : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param loginNm : 用户名
     * @param nowTime : 现在时间
     * @description: 修改商品货架详细的数量（实际在库数、依赖中数、不可配送数）
     * @return: java.lang.Integer
     * @date: 2021/10/18 9:54
     */
    Integer updateLocationDetailCnt(@Param("stock_cnt") int stock_cnt, @Param("requesting_cnt") int requesting_cnt,
        @Param("not_delivery") int not_delivery, @Param("location_id") String location_id,
        @Param("client_id") String client_id, @Param("product_id") String product_id,
        @Param("upd_usr") String loginNm, @Param("upd_date") Date nowTime);

    /**
     * @param warehouse_cd : 仓库Id
     * @param lot_no : ロット番号
     * @param wh_location_nm : ロケーション名称
     * @description: 根据 ロケーション名称 和 ロット番号 获取货架信息
     * @return: com.lemonico.common.bean.Mw404_location
     * @date: 2021/10/25 15:29
     */
    Mw404_location getLocationByLotNo(@Param("warehouse_cd") String warehouse_cd,
        @Param("lot_no") String lot_no,
        @Param("wh_location_nm") String wh_location_nm);

    /**
     * @param location_id : ロケーションID
     * @param product_id : 商品ID
     * @param client_id : 顧客CD
     * @param stockCnt : 在庫数
     * @param notDelivery : 不可配送数
     * @param loginNm : 更新者
     * @param date : 更新日時
     * @param locationStatus : 出荷不可フラグ
     * @param bestbeforeDate : 賞味期限/在庫保管期限
     * @description: 更改商品货架的信息
     * @return: java.lang.Integer
     * @date: 2021/10/25 16:50
     */
    Integer updateProductLocation(@Param("location_id") String location_id,
        @Param("product_id") String product_id,
        @Param("client_id") String client_id,
        @Param("stock_cnt") Integer stockCnt,
        @Param("not_delivery") int notDelivery,
        @Param("upd_usr") String loginNm,
        @Param("upd_date") Date date,
        @Param("status") Integer locationStatus,
        @Param("bestbefore_date") Date bestbeforeDate);

    /**
     * @param date : 当前时间
     * @description: 获取到不可用的商品货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/10/27 12:38
     */
    List<Mw405_product_location> getUnavailableProductLocation(@Param("bestbefore_date") Date date);

    /**
     * @param productLocations : 商品在货架上面的详细信息集合
     * @param date : 修改日期
     * @param updUsr : 修改者
     * @description: 修改货架的出荷状态为不可出库并修改不可配送数
     * @return: java.lang.Integer
     * @date: 2021/11/9 15:36
     */
    Integer updateLocationStatus(@Param("productLocations") List<Mw405_product_location> productLocations,
        @Param("upd_date") Date date,
        @Param("upd_usr") String updUsr);

    /**
     * @description: 获取到所有不可用的商品货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2021/11/9 15:59
     */
    List<Mw405_product_location> getAllCannotProductLocation();

    /**
     * @param client_id : 店鋪Id
     * @param product_id : 商品Id
     * @param firstDay : 開始日期
     * @param nowTime : 現在日期
     * @param type : 在庫狀態
     * @description: 獲取在庫履歷的信息
     */
    List<Tw301_stock_history> getShipmentNumSum(@Param("client_id") String client_id,
        @Param("product_id") List<String> product_id,
        @Param("startDate") Date firstDay,
        @Param("endDate") Date nowTime,
        @Param("type") int type);

    /**
     * @param warehouse_cd : 仓库Id
     * @param wh_location_nm : 货架名称
     * @param lot_no : ロット番号
     * @description: 判断该货架之前是否存在
     * @return: int
     * @date: 2022/3/2 9:41
     */
    int getLocationCount(@Param("warehouse_cd") String warehouse_cd,
        @Param("wh_location_nm") String wh_location_nm,
        @Param("lot_no") String lot_no);
}
