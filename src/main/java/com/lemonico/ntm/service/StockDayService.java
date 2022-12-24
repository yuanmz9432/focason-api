package com.lemonico.ntm.service;



import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @className: StockDayService
 * @description: 在库相关Services
 * @date: 2021/3/9 16:23
 **/
public interface StockDayService
{

    /**
     * @description: 获取月次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/10 14:36
     */
    JSONObject getCsvData(String type, HttpServletRequest request);

    /**
     * @description: 获取周次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/12 12:46
     */
    Map<String, List<JSONObject>> getWeeksCsvData(String type, HttpServletRequest request);

    /**
     * @param shipment_plan_date : 出庫予定日
     * @param type : 1: ECCUBE受注&一斉発送 2: 一斉注文
     * @param form: 1: 法人 2: 个人
     * @description: 获取日次出荷数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/12 14:42
     */
    JSONObject getStockDayData(String shipment_plan_date, String type, String form, HttpServletRequest request);
}
