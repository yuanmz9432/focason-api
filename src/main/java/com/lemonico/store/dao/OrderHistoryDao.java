package com.lemonico.store.dao;



import com.lemonico.common.bean.Tc202_order_history;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description 受注履歴・Daoインターフェース
 * 
 * @date 2020/06/26
 * @version 1.0
 **/
@Mapper
public interface OrderHistoryDao
{

    /**
     * @Description: 最新受注履歴番号取得
     * @Param:
     * @return: Integer
     * @Date: 2020/06/24
     */
    public String getLastOrderHistoryNo();

    /**
     * @Description: 受注履歴登録
     * @Param: Tc202_order_history
     * @return: Integer
     * @Date: 2020/06/24
     */
    public Integer insertOrderHistory(Tc202_order_history tc202_order_history);

    /**
     * @Description: 受注履歴更新
     * @Param: Tc202_order_history
     * @return: Integer
     * @Date: 2020/06/24
     */
    public Integer updateOrderHistory(Tc202_order_history tc202_order_history);

    /**
     * 受注履歴取得
     * 
     * @param client_id
     * @return List of Bean
     * @date 2020-06-29
     */
    public List<Tc202_order_history> getOrderHistoryList(@Param("client_id") String client_id,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * @Param: history_id
     * @description: 受注取込履歴ID获取受注取込履歴テーブル
     * @return: com.lemonico.common.bean.Tc202_order_history
     * @date: 2020/8/21
     */
    Tc202_order_history getOrderHistoryInfoByHistoryId(@Param("history_id") String history_id);
}
