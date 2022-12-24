package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc100_warehousing_plan;
import com.lemonico.common.bean.Tc101_warehousing_plan_detail;
import com.lemonico.common.bean.Tw110_warehousing_result;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: warehousingsDao
 * @description: warehousings dao
 * @date: 2020/05/12 16:02
 **/
@Mapper
public interface WarehousingsDao
{

    /**
     * @param: client_id ： 店舗ID
     * @param: status ： 入庫ステータス
     * @param: search ： 搜索内容
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @param: tags_id ： タグID
     * @description: 入库依赖一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/12
     */
    List<Tc100_warehousing_plan> getWarehousingsList(@Param("jsonObject") JSONObject jsonObject,
        @Param("status") Integer status,
        @Param("search") String search,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime,
        @Param("tags_id") String tags_id);

    /**
     * @Param jsonObject : Tc100_warehousing_plan
     * @description: 1本挿入する Tc100_warehousing_plan データ
     * @return: java.lang.Integer
     * @date: 2020/05/14
     */
    Integer insertWarehousingsList(@Param("jsonObject") JSONObject jsonObject, @Param("request_date") Date request_date,
        @Param("arrival_date") Date arrival_date, @Param("loginNm") String loginNm,
        @Param("date") Date date, @Param("status") int status);


    /**
     * @Param: jsonObject : client_id 店舗ID, id 入庫依頼ID
     * @description: 批量删除入库依赖
     * @return: JSONObject
     * @date: 2020/05/14
     */
    Integer deleteWarehousingsList(@Param("client_id") String client_id, @Param("id") String ids,
        @Param("loginNm") String loginNm, @Param("date") Date date);

    /**
     * @Param: jsonObject : client_id 店舗ID, whs_plan_id 入庫依頼ID, warehouse_cd 倉庫コード ，还有被修改的其它数据
     * @description: 入库依赖更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/14
     */
    Integer updateWarehousings(@Param("jsonObject") JSONObject jsonObject,
        @Param("arrival_date") Date arrival_date,
        @Param("quantity") Integer productNum,
        @Param("loginNm") String loginNm,
        @Param("date") Date updateDate);

    /**
     * @param: jsonObject : client_id ： 店舗ID，id : 入庫依頼ID;
     * @description: 入庫依頼による関連情報の照会
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/24
     */
    Tc100_warehousing_plan getInfoById(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @param: jsonObject
     * @description: 商品集合を取得する
     * @return: com.lemonico.common.bean.Tw110_warehousing_result
     * @date: 2020/06/01
     */
    Tw110_warehousing_result getProductInfoById(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: client_id : 店铺Id
     * @description: 最大入庫依頼IDを取得する
     * @return:
     * @date: 2020/06/05
     */
    String getLastId();

    /**
     * @Param: jsonObject: client_id,id
     * @description: 根据入库依赖Id查询商品
     * @return: JSONObject
     * @date: 2020/06/17
     */
    List<Tc100_warehousing_plan> getWarehouseInfoById(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id);

    /**
     * @Param: jsonObject: client_id,id,status
     * @description: 根据入库依赖修改入库状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/17
     */
    Integer updateStatusById(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param:
     * @description: 获取商品入库依赖明细
     * @return:
     * @date: 2020/08/13
     */
    public List<Tc101_warehousing_plan_detail> getWarehousingDetail(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * 查询状态下数据是否存在
     *
     * @param client_id
     * @param id
     * @param status
     * @return
     * @date: 2021/5/20
     */
    public Integer selectStatusCount(@Param("client_id") String client_id,
        @Param("id") String id,
        @Param("status") Integer status);

    /**
     * @param id : 入库依赖ID
     * @param client_id : 店铺ID
     * @param warehouse_cd : 仓库ID
     * @param warehouseStatus : 入库状态
     * @param loginNm : 用户名
     * @param date : 当前日期
     * @param warehousing_date : 入库日期
     * @description: 修改入库的状态
     * @return: java.lang.Integer
     * @date: 2021/7/28 17:05
     */
    Integer updateStatus(@Param("id") String id,
        @Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("status") int warehouseStatus,
        @Param("loginNm") String loginNm,
        @Param("date") Date date,
        @Param("warehousing_date") String warehousing_date);

    /**
     * @param warehouse_cd : 仓库ID
     * @param client_id : 店铺ID
     * @param id : 入库依赖ID
     * @param productKindPlanCnt : 入庫依頼商品種類数
     * @param quantity : 入庫依頼商品合記
     * @param status : 入庫ステータス
     * @param loginNm : 更新者
     * @param date : 更新日時
     * @description: 修改入库商品数量信息
     * @return: int
     * @date: 2021/8/4 9:33
     */
    int updateWarehouseInfo(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id,
        @Param("product_kind_plan_cnt") int productKindPlanCnt,
        @Param("quantity") int quantity,
        @Param("status") int status,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);

    /**
     * 出库CSV下载
     * 
     * @param jsonObject
     * @param status
     * @param search
     * @param startTime
     * @param endTime
     * @param tags_id
     * @param column
     * @param sortType
     * @return
     */
    List<Tc100_warehousing_plan> getWarehousingCsvsList(@Param("jsonObject") JSONObject jsonObject,
        @Param("status") Integer status,
        @Param("search") String search,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime,
        @Param("tags_id") String tags_id,
        @Param("column") String column,
        @Param("sortType") String sortType);

}
