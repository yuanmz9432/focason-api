package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tc201_order_detail;
import com.lemonico.store.dao.OrderDetailDao;
import com.lemonico.store.service.OrderDetailService;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.datasource.DataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description 受注管理・サービス実装
 * 
 * @date 2020/06/18
 * @version 1.0
 **/
@Service
public class OrderDetailServiceImpl implements OrderDetailService
{

    @Autowired
    private OrderDetailDao orderDetailDao;
    /**
     * @Description 最新受注明細番号を取得
     * @Param なし
     * @return 最新の受注明細番号
     */

    private final static Logger logger = LoggerFactory.getLogger(OrderDetailServiceImpl.class);

    @Override
    public String getLastOrderDetailNo() {
        String lastOrderNo = orderDetailDao.getLastOrderDetailNo();
        return lastOrderNo;
    }

    /**
     * @Description: 新規受注明細テーブルの登録
     * @Param: Tc201_order_detail
     * @return: Integer
     */
    @Override
    public Integer setOrderDetail(Tc201_order_detail tc201_order_detail) throws SQLException {
        try {
            return orderDetailDao.insertOrderDetail(tc201_order_detail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("受注明細テーブルへのデータ追加に失敗しました。");
        }
    }

    @Override
    public JSONObject getOrderDetail(String order_detail_no) {
        JSONObject orderDetail = new JSONObject();
        try {
            List<Tc201_order_detail> order_details = orderDetailDao.getOrderDetail(order_detail_no);
            orderDetail.put("order_detail_no", order_details.get(0).getOrder_detail_no());
            orderDetail.put("product_name", order_details.get(0).getProduct_name());
        } catch (DataSourceException e) {
            logger.error("db error", e);
        } catch (IndexOutOfBoundsException e) {
            logger.error("受注明細ID" + order_detail_no + "に関するデータの件数が不正");
        }
        return orderDetail;
    }

    /**
     * 受注取込履歴詳細表示
     * 
     * @param history_id
     * @return
     */
    @Override
    public JSONObject getOrderHistoryDetail(String history_id) {
        JSONObject data = new JSONObject();
        data.put("orderHistoryDetails", orderDetailDao.getOrderHistoryDetail(history_id));
        return data;
    }
}
