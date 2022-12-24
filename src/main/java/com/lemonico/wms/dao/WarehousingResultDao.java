package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: WarehousingResultDao
 * @description: 仓库侧入库处理
 * @date: 2020/06/17 14:00
 **/
@Mapper
public interface WarehousingResultDao
{

    /**
     * @Param: jsonObject ： warehouse_cd ：倉庫コード， client_id ： 店舗ID， warehousing_plan_id ：入庫依頼ID
     * @param: date ： 検品処理日
     * @param: request_date ： 入庫依頼日
     * @param: warehousing_plan_date ： 入庫予定日
     * @description: 将数据插入到 入庫実績テーブル
     * @return: java.lang.Integer
     * @date: 2020/07/14
     */
    Integer insertWarehouseResult(@Param("jsonObject") JSONObject jsonObject,
        @Param("warehousing_date") Date nowTime,
        @Param("request_date") Date request_date,
        @Param("warehousing_plan_date") Date warehousing_plan_date,
        @Param("loginNm") String loginNm,
        @Param("date") Date date,
        @Param("inspection_date") Date inspection_date);

    /**
     * @Param: jsonObject warehouse_cd ：倉庫コード， client_id ： 店舗ID， warehousing_plan_id ：入庫依頼ID
     *         product_id ： 商品ID
     * @param: weight ： 商品重量
     * @param: productTotal ： 入庫実績数
     * @param: quantity ： 入庫依頼数
     * @description: 将数据插入到 入庫実績明細テーブル
     * @return: java.lang.Integer
     * @date: 2020/07/14
     */
    Integer insertWarehouseResultDetil(@Param("jsonObject") JSONObject jsonObject,
        @Param("product_weight") Double weight,
        @Param("product_cnt") Integer productTotal,
        @Param("product_plan_cnt") Integer quantity,
        @Param("date") Date date,
        @Param("loginNm") String loginNm);

    /**
     * @Param: jsonObject client_id ： 店舗ID， id ：入庫依頼ID
     * @param: product_id : 商品ID
     * @description: 根据店铺Id，入库依赖Id，商品Id查出 入庫実績明細テーブル 的数据
     * @return: com.lemonico.common.bean.Tw111_warehousing_result_detail
     * @date: 2020/07/14
     */
    List<Tw110_warehousing_result> getWarehousingInfoById(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id);

    /**
     * @param: jsonObject : items 包含商品ID，入库实际数，入库依赖数等
     * @description: 追加检品数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/01
     */
    Integer updateProductTotal(@Param("product_total") int number,
        @Param("jsonObject") JSONObject jsonObject);

    /**
     * @param: number ： 入库实际数
     * @param: jsonObject ： client_id,id
     * @param: product_id : 商品ID
     * @description: 入库实际明细追加检品数
     * @return: java.lang.Integer
     * @date: 2020/07/02
     */
    Integer updateProductCnt(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id, @Param("product_id") String product_id,
        @Param("product_cnt") int product_cnt,
        @Param("product_size_cd") String product_size_cd,
        @Param("product_weight") String product_weight);

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 查出该仓库所有的货架
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    List<String> getLocationNameByWarehouseId(@Param("warehouse_cd") String warehouse_cd,
        @Param("searchName") String searchName);

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 检查货架是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/15
     */
    Integer locationNameCheck(@Param("warehouse_cd") String warehouse_cd,
        @Param("name") String name);

    /**
     * @Param: id : 入庫依頼ID
     * @Param: client_id: 店舗ID
     * @description: 根据入库依赖Id 查询 入庫作業ロケ明細 信息
     * @return: java.util.List<com.lemonico.common.bean.Tw113_warehousing_location_detail>
     * @date: 2020/8/12
     */
    List<Tw113_warehousing_location_detail> getWarehousingLocationDetail(@Param("warehousing_plan_id") String id,
        @Param("client_id") String client_id);

    /**
     * @Param: jsonObject
     * @description: 查询该商品的入庫作業ロケ明細 里所在的货架
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/13
     */
    List<Mw404_location> getLocationNameById(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @Param: productIdList ：商品Id集合
     * @description: 查询在库数小于出库依赖数的货架信息
     * @return: java.util.List<com.lemonico.common.bean.Mw405_product_location>
     * @date: 2020/8/14
     */
    List<Mw405_product_location> getLocationException(@Param("jsonObject") JSONObject jsonObject,
        @Param("productIdList") List<String> productIdList);

    /**
     * @Param: product_id
     * @param: client_id
     * @param: id
     * @param: locationId
     * @description: 查询入庫実績数
     * @return: java.lang.Integer
     * @date: 2020/8/22
     */
    Integer getWarehouseLocationInfoById(@Param("product_id") String product_id,
        @Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("location_id") String locationId);

    /**
     * @Param: productCnt
     * @param: product_id
     * @param: client_id
     * @param: id
     * @param: locationId
     * @description: 修改入库作業ロケ明細的入库实际数
     * @return: java.lang.Integer
     * @date: 2020/8/22
     */
    Integer updateWarehouseLocation(@Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("product_id") String product_id,
        @Param("product_cnt") Integer product_cnt,
        @Param("location_id") String locationId,
        @Param("lot_no") String lot_no);

    /**
     * @Param: jsonObject: client_id,id,status
     * @description: 根据入库依赖修改tw110入库处理结束日
     * @return:
     * @date: 2020/11/24
     */
    Integer updateWarehousingDate(@Param("jsonObject") JSONObject jsonObject,
        @Param("date") Date date);

    /**
     * @param warehouse_id : 仓库Id
     * @param client_id ： 店铺Id
     * @description: 入庫ステータス件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/30 15:53
     */
    List<Tc100_warehousing_plan> getWarehousingStatusCount(@Param("warehouse_cd") String warehouse_id,
        @Param("client_id") String client_id);

    /**
     * @param tw110_warehousing_result : 入库实际对象
     * @param loginNm : 用户名
     * @param date : 当前时间
     * @description: 修改入库实际的 入庫依頼商品種類数、入庫依頼商品数計、入庫実績商品種類数、入庫実績商品数計
     * @return: java.lang.Integer
     * @date: 2021/8/2 10:28
     */
    Integer updateWarehouseResultCnt(@Param("result") Tw110_warehousing_result tw110_warehousing_result,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);

    List<Tw111_warehousing_result_detail> getWarehouseResultDetails(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("product_id") String product_id);

    int updateResultDetails(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("product_id") String product_id,
        @Param("product_cnt") Integer productTotal,
        @Param("product_size_cd") String size_cd,
        @Param("product_weight") Double weight,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);
}
