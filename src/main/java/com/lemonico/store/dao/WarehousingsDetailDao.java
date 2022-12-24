package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc101_warehousing_plan_detail;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: warehousingsDetailDao
 * @description: 入庫依頼明細dao
 * @date: 2020/05/13 15:19
 **/
@Mapper
public interface WarehousingsDetailDao
{

    /**
     * @Param: wpd_warehouse_cd :倉庫コード
     * @param: wpd_account_id : 顧客CD
     * @param: wpd_warehousing_plan_id : 入庫依頼ID
     * @param: wpd_product_plan_cnt : 入庫依頼数
     * @param: ins_usr : 作成者
     * @param: ins_date : 作成日時
     * @param: upd_usr : 更新者
     * @param: upd_date : 更新日時
     * @param: wpd_del_flg : 削除フラグ
     * @param: tc101_warehousing_plan_detailList : 商品ID集合
     * @description: 複数の条を挿入するTc101_warehousing_plan_detail データ
     * @return: java.lang.Integer
     * @date: 2020/05/14
     */
    Integer insertWarehousingsDetailList(@Param("jsonObject") JSONObject jsonObject,
        @Param("quantity") Integer quantity,
        @Param("date") Date date, @Param("loginNm") String loginNm);

    /**
     * @Param： account_id : 用户ID
     * 
     * @param: ids : 入庫依頼ID
     * @Param: warehouse_cd : 倉庫コード
     * @description: 入庫依頼明細を削除する
     * @return: java.lang.Integer
     * @date: 2020/05/14
     */
    Integer deleteWarehousingsDetailList(@Param("client_id") String account_id, @Param("id") String ids,
        @Param("loginNm") String loginNm, @Param("date") Date date);

    /**
     * @description: 最大商品Idを取得する
     * @return: java.lang.Integer
     * @date: 2020/05/18
     */
    Integer getMaxProductId();

    /**
     * @Param: jsonObject
     * @description: 入庫依頼明細情報を取得する
     * @return: java.util.List<com.lemonico.common.bean.Tc101_warehousing_plan_detail>
     * @date: 2020/05/29
     */
    List<Tc101_warehousing_plan_detail> getProductId(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: warehouse_cd
     * @param: client_id
     * @param: id
     * @param: product_id
     * @description: 获得入庫実績明細テーブル 实际数
     * @return: java.lang.Integer
     * @date: 2020/8/22
     */
    Integer getWarehousingResultDetail(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("warehousing_plan_id") String id,
        @Param("product_id") String product_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param id : 入库依赖Id
     * @description: 获取该入库依赖下的所有明细信息
     * @return: java.util.List<com.lemonico.common.bean.Tw201_shipment_detail>
     * @date: 2021/7/28 13:21
     */
    List<Tc101_warehousing_plan_detail> getWarehousingDetailList(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id);

    /**
     * @param id : 入库依赖Id
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param detailsProductId : 商品Id集合
     * @param status : 入库状态
     * @description: 修改入库明细的入库状态
     * @return: java.lang.Integer
     * @date: 2021/7/28 16:46
     */
    Integer updateWarehouseDetailStatus(@Param("id") String id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("productIdList") List<String> detailsProductId,
        @Param("status") int status,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);

    /**
     * @param id : 入库依赖ID
     * @param warehouse_cd : 仓库ID
     * @param client_id : 店铺ID
     * @param status : 入库明细的入库状态
     * @description: 获取相应入库状态的入库件数
     * @return: int
     * @date: 2021/8/2 13:57
     */
    int getWarehousingByStatus(@Param("id") String id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("status") int status);

    /**
     * @param warehouse_cd : 仓库ID
     * @param client_id : 店铺ID
     * @param id : 入库依赖ID
     * @param status : 入库明细的入库状态
     * @description: 根据入库明细的入库状态删除入库明细信息
     * @return: int
     * @date: 2021/8/3 17:43
     */
    int deleteWarehouseDetailByStatus(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("id") String id,
        @Param("status") int status);

    /**
     * 查询商品是否入库依赖中或者依赖过
     *
     * @param client_id
     * @param productIdList
     * @return
     */
    List<Tc101_warehousing_plan_detail> getWarehouseDetailByProductId(@Param("client_id") String client_id,
        @Param("productIdList") List<String> productIdList);

    /**
     * @param tc101WarehousingPlanDetails : 入库明细信息集合
     * @description: 批量插入入库明细信息
     * @return: java.lang.Integer
     * @date: 2021/10/3 9:48
     */
    Integer insertListDetail(@Param("details") List<Tc101_warehousing_plan_detail> tc101WarehousingPlanDetails);

    /**
     * @param tc101WarehousingPlanDetails : 入库明细信息集合
     * @description: 批量更改入库明细信息
     * @return: java.lang.Integer
     * @date: 2021/10/3 9:48
     */
    Integer updateListDetail(@Param("details") List<Tc101_warehousing_plan_detail> tc101WarehousingPlanDetails);

    /**
     * @param id : 入庫依頼ID
     * @param client_id : 顧客CD
     * @param warehouse_cd : 倉庫コード
     * @param product_id : 商品ID
     * @param lot_no : ロット番号
     * @param bestBeforeDate : 賞味期限/在庫保管期限
     * @param upd_date : 更新日時
     * @param upd_usr : 更新者
     * @param shippingFlag : 出荷フラグ
     * @description: 修改入库明细的賞味期限/在庫保管期限和ロット番号
     * @return: java.lang.Integer
     * @date: 2021/10/27 17:49
     */
    Integer updateWarehouseDetailLotNo(@Param("id") String id,
        @Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("product_id") String product_id,
        @Param("lot_no") String lot_no,
        @Param("bestbefore_date") Date bestBeforeDate,
        @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date,
        @Param("shipping_flag") int shippingFlag);
}
