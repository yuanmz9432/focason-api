package com.lemonico.store.dao;



import com.lemonico.common.bean.Tc207_order_error;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description 受注失敗履歴・Daoインターフェース
 *
 * @author wang
 * @date 2020/06/26
 * @version 1.0
 **/
@Mapper
public interface OrderErrorDao
{

    /**
     * @Description: 最新受注失敗履歴番号取得
     * @Param:
     * @return: Integer
     * @Author: wang
     * @Date: 2021/1/19
     */
    public String getLastOrderErrorNo();

    /**
     * @Description: 最新受注失敗履歴番号取得
     * @Param:
     * @return: Integer
     * @Author: wang
     * @Date: 2021/1/19
     */
    public Integer getOrderError(@Param("client_id") String client_id, @Param("outer_order_no") String outer_order_no);

    /**
     * @Description: 受注失敗履歴登録
     * @Param: Tc207_order_error
     * @return: Integer
     * @Author: wang
     * @Date: 2021/01/19
     */
    public Integer insertOrderError(Tc207_order_error tc207_order_error);

    /**
     * @Description: 受注失敗履歴更新
     * @Param: Tc207_order_error
     * @return: Integer
     * @Author: wang
     * @Date: 2021/1/19
     */
    public Integer updateOrderError(Tc207_order_error tc207_order_error);

    /**
     * 受注失敗履歴取得
     * 
     * @param client_id
     * @return List of Bean
     * @author wang
     * @date 2021/1/19
     */
    public List<Tc207_order_error> getOrderErrorList(@Param("client_id") String client_id,
        @Param("history_id") String history_id,
        @Param("status") String status);

    /**
     * 根据履历Id 获取受注失败信息
     * 
     * @param client_id
     * @param history_id
     * @return
     */
    public List<Tc207_order_error> getErrorInfoByHistoryId(@Param("client_id") String client_id,
        @Param("history_id") String history_id);
}
