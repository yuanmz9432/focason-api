package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc201_order_detail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @version 1.0
 * @description 受注詳細・Daoインターフェース
 * @date 2020/06/18
 **/
@Mapper
public interface OrderDetailDao
{

    /**
     * @return 最新の受注明細番号
     * @Description 最新受注明細番号を取得
     * @Param なし
     */
    String getLastOrderDetailNo();

    /**
     * @Description: 受注明細登録
     * @Param: Tc201_order_detail
     * @return: Integer
     * @Date: 2020/06/24
     */
    Integer insertOrderDetail(Tc201_order_detail tc201_order_detail);

    /**
     * 受注明細テーブルにアクセスし、PKを条件に情報を取得。
     *
     * @param order_detail_no
     * @return List<Tc201_order_detail> PKを条件にするので、項目数は1か0でないとまずい。
     * @date 2020-06-24
     */
    List<Tc201_order_detail> getOrderDetail(String order_detail_no);

    /**
     * 受注取込履歴詳細表示
     * 
     * @param purchase_order_no
     * @return
     */
    List<JSONObject> getOrderHistoryDetail(String purchase_order_no);

    /**
     * @Param: purchase_order_no 受注番号
     * @description: 查询 受注明細テーブル 的商品名称
     * @return: java.util.List<com.lemonico.common.bean.Tc201_order_detail>
     * @date: 2020/8/26
     */
    List<Tc201_order_detail> getOrderDetailEntityListByPurchaseOrderNo(
        @Param("purchase_order_no") String purchase_order_no,
        @Param("del_flg") Integer del_flg);

    /**
     * @Param: purchase_order_no 受注番号
     * @description: 查询 受注明細テーブル 的商品名称
     * @return: java.util.List<com.lemonico.common.bean.Tc201_order_detail>
     * @date: 2020/8/26
     */
    List<String> getOrderDetailListByPurchaseOrderNo(@Param("purchase_order_no") String purchase_order_no);

    /**
     * 受注明細を削除
     *
     * @param purchase_order_no 受注番号
     * @return: void
     * @author: YuanMingZe
     * @date: 2021/07/05
     */
    void orderDetailDelete(@Param("purchase_order_no") String purchase_order_no);

    /**
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @param kubun : 商品区分
     * @description: 修改店铺 受注详细的商品区分
     * @return: java.lang.Integer
     * @date: 2021/9/24 14:46
     */
    Integer updateProductKubunByProductId(@Param("product_id") String product_id,
        @Param("client_id") String client_id,
        @Param("product_kubun") int kubun,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);

    /**
     * @param orderDetails : 多条受注明细数据
     * @description: 批量插入受注明细数据
     * @return: java.lang.Integer
     * @date: 2021/4/6
     */
    Integer bulkInsertOrderDetail(@Param("orderDetails") ArrayList<Tc201_order_detail> orderDetails);

}
