package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc202_order_history;
import java.sql.SQLException;

/**
 * @description 受注履歴・サービスインターフェース
 * 
 * @date 2020/06/26
 * @version 1.0
 **/
public interface OrderHistoryService
{

    /**
     * 最新受注履歴番号を取得
     * 
     * @param
     * @return String
     * @date 2020-06-26
     */
    public String getLastOrderHistoryNo();

    /**
     * 新規受注履歴を登録
     * 
     * @param Tc202_order_history
     * @return Integer
     * @date 2020-06-26
     */
    public Integer setOrderHistory(Tc202_order_history tc202_order_history) throws SQLException;

    /**
     * 受注履歴を更新
     * 
     * @param Tc202_order_history
     * @return Integer
     * @date 2020-06-26
     */
    public Integer updateOrderHistory(Tc202_order_history tc202_order_history) throws SQLException;

    /**
     * @Param: client_id : 店铺Id
     * @description: 受注取込試行一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/20
     */
    public JSONObject getOrderHistoryList(String client_id, Integer page, Integer size, String column, String sort,
        String start_date, String end_date);
}
