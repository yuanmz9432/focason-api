package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc201_order_detail;
import java.sql.SQLException;

/**
 * @description 受注詳細・サービスインターフェース
 * 
 * @date 2020/06/18
 * @version 1.0
 **/
public interface OrderDetailService
{

    /**
     * @Description 最新受注明細番号を取得
     * @Param なし
     * @return 最新の受注明細番号
     */
    public String getLastOrderDetailNo();

    /**
     * @Description: 新規受注明細テーブルの登録
     * @Param: Tc201_order_detail
     * @return: Integer
     */
    public Integer setOrderDetail(Tc201_order_detail tc201_order_detail) throws SQLException;

    // /**
    // * @Description: 出庫明細テーブル新规
    // * @Param: Tw201_shipment_detail
    // * @return: Integer
    // */
    // public Integer setShipmentDetail(JSONObject jsonParam, boolean insertFlg);
    //
    // /**
    // * @Description: 出庫明細テーブル删除
    // * @Param: 顧客CD, 出庫依頼ID
    // * @return: Integer
    // */
    // public Integer deleteShipmentDetail(String client_id, String shipment_plan_id);

    public JSONObject getOrderDetail(String order_detail_no);

    public JSONObject getOrderHistoryDetail(String history_id);

}
