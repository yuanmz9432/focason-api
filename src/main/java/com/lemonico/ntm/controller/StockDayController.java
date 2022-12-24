package com.lemonico.ntm.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.ntm.service.StockDayService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: StockDayController
 * @description: NTM连携
 * @date: 2021/3/9 15:12
 **/
@RestController
public class StockDayController
{

    @Resource
    private StockDayService stockDayService;

    /**
     * @description: 获取月次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/10 14:36
     */
    @GetMapping("/stock/months")
    public JSONObject getStockMonths(@Param("type") String type, HttpServletRequest request) {
        return stockDayService.getCsvData(type, request);
    }

    /**
     * @description: 获取周次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/12 12:46
     */
    @GetMapping("/stock/weeks")
    public Map<String, List<JSONObject>> getStockWeeks(String type, HttpServletRequest request) {
        return stockDayService.getWeeksCsvData(type, request);
    }

    /**
     * @param shipment_plan_date : 出庫予定日
     * @param type : 1: ECCUBE受注&一斉発送 2: 一斉注文
     * @param form : 1: 法人 2: 个人
     * @description: 获取日次出荷数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/12 14:42
     */
    @GetMapping("/stock/day")
    public JSONObject getStockDay(String shipment_plan_date, String type, String form, HttpServletRequest request) {
        return stockDayService.getStockDayData(shipment_plan_date, type, form, request);
    }
}
