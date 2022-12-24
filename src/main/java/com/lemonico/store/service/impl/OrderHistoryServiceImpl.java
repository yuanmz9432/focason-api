package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lemonico.common.bean.Tc202_order_history;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.OrderHistoryDao;
import com.lemonico.store.service.OrderHistoryService;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @description 受注履歴・サービス実装
 * 
 * @date 2020/06/26
 * @version 1.0
 **/
@Service
public class OrderHistoryServiceImpl implements OrderHistoryService
{

    @Resource
    OrderHistoryDao orderHistoryDao;

    private final static Logger logger = LoggerFactory.getLogger(OrderDetailServiceImpl.class);

    /**
     * @Description 最新受注履歴番号を取得
     * @Param なし
     * @return 最新の受注履歴番号
     */
    @Override
    public String getLastOrderHistoryNo() {
        return orderHistoryDao.getLastOrderHistoryNo();
    }

    /**
     * @Description 新規受注履歴を登録
     * @Param Tc202_order_history
     * @return Integer
     */
    @Override
    public Integer setOrderHistory(Tc202_order_history tc202_order_history) throws SQLException {
        try {
            return orderHistoryDao.insertOrderHistory(tc202_order_history);
        } catch (Exception e) {
            throw new SQLException("受注取込履歴テーブルへのデータ追加に失敗しました。");
        }
    }

    /**
     * @Description 受注履歴を更新
     * @Param Tc202_order_history
     * @return Integer
     */
    @Override
    public Integer updateOrderHistory(Tc202_order_history tc202_order_history) throws SQLException {
        try {
            return orderHistoryDao.updateOrderHistory(tc202_order_history);
        } catch (Exception e) {
            throw new SQLException("受注取込履歴テーブルのデータ更新に失敗しました。");
        }
    }

    /**
     * @Param: client_id : 店铺Id
     * @description: 受注取込試行一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/20
     */
    @Override
    public JSONObject getOrderHistoryList(String client_id, Integer page, Integer size, String column, String sort,
        String start_date, String end_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        if (!StringTools.isNullOrEmpty(start_date)) {
            try {
                startDate = format.parse(start_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringTools.isNullOrEmpty(end_date)) {
            try {
                endDate = format.parse(end_date);
                // endDate处理
                endDate = CommonUtils.getDateEnd(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = sort.equals("descending") ? "DESC" : "ASC";
        }
        PageHelper.startPage(page, size);
        List<Tc202_order_history> orderHistories =
            orderHistoryDao.getOrderHistoryList(client_id, column, sortType, startDate, endDate);
        PageInfo<Tc202_order_history> pageInfo = new PageInfo<>(orderHistories);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONArray jsonArray = new JSONArray();
        orderHistories.forEach(orderHistory -> {
            JSONObject jsonObject = new JSONObject();
            Date importDatetime = orderHistory.getImport_datetime();
            jsonObject.put("import_datetime", simpleDateFormat.format(importDatetime));
            jsonObject.put("total_cnt", orderHistory.getTotal_cnt());
            jsonObject.put("success_cnt", orderHistory.getSuccess_cnt());
            jsonObject.put("failure_cnt", orderHistory.getFailure_cnt());
            jsonObject.put("biko01", orderHistory.getBiko01());
            jsonObject.put("history_id", orderHistory.getHistory_id());
            jsonArray.add(jsonObject);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);
        jsonObject.put("total", pageInfo.getTotal());
        return CommonUtils.success(jsonObject);
    }
}
