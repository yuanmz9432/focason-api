package com.lemonico.ntm.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.ntm.service.ReportService;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * @className: ReportController
 * @description: TODO 报表相关
 * @date: 2021/4/15 13:32
 **/
@RestController
@RequestMapping("/ntm")
public class ReportController
{
    @Resource
    ReportService reportService;

    private final static Logger logger = LoggerFactory.getLogger(ReportController.class);

    /**
     * @Description: 法人出荷相关接口
     *               @Date： 2021/4/14
     *               @Param： client_id,warehouse_cd,shipment_plan_date
     *               @return： JSONObject
     */
    @GetMapping("/DeliveryRelation")
    public JSONObject monthDeliveryDetails(HttpServletRequest httpServletRequest, String warehouse_cd, String search,
        String startTime, String endTime, String form, String type, String transform) {
        logger.info("检索内容：{},检索开始时间：{},结束时间：{}", search, startTime, endTime);
        List<Tw200_shipment> tw200_shipments = reportService.monthDeliveryDetails(httpServletRequest, warehouse_cd,
            search, startTime, endTime, form, type, transform);
        if (tw200_shipments != null) {
            return CommonUtils.success(tw200_shipments);
        }
        return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * @Description: 出荷确定处理
     *               @Date： 2021/4/14
     *               @Param： client_id,warehouse_cd,shipment_plan_date
     *               @return： JSONObject
     */
    @PostMapping("/updateDeliveryHandle")
    public JSONObject updateDeliveryHandle(@RequestBody JSONObject jsonObject, HttpServletRequest httpServletRequest) {
        Integer number = reportService.updateDeliveryHandle(jsonObject, httpServletRequest);
        if (number != null && number != 0) {
            return CommonUtils.success(number);
        }
        logger.warn("NTM----出荷处理失败~~~");
        return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * @description: 查询有效在库接口
     * @param request http请求
     * @return: JSONObject
     * @date: 2021/4/25
     **/
    @GetMapping("/person/stockAvailableCnt")
    public JSONObject getStockAvailableCnt(HttpServletRequest request) {
        String clientId = CommonUtils.getToken("client_id", request);

        JSONObject resBean = new JSONObject();
        String cnt = reportService.getStockAvailableCnt(clientId);
        resBean.put("cnt", cnt);

        return CommonUtils.success(resBean);
    }

    /**
     * @description 封筒数据查询接口(batch使用)
     * @param date 查询日期
     * @param request http请求
     * @return: JSONObject
     * @date 2021/4/25
     **/
    @GetMapping("/person/batchRequestSealData")
    public JSONObject batchRequestSealData(@RequestParam("date") String date,
        @RequestParam("delivery_carrier") String deliveryCarrier,
        HttpServletRequest request) {
        String clientId = CommonUtils.getToken("client_id", request);
        List<JSONObject> list = reportService.getSealDataByDate(date, deliveryCarrier, clientId);
        return CommonUtils.success(list);
    }

    /**
     * @description: 更新在库表
     * @param cnt 更新数量
     * @return: JSONObject
     * @date: 2021/4/25
     **/
    @GetMapping("/person/updateStockAndProductLocation")
    public JSONObject updateStock(@RequestParam("cnt") String cnt, HttpServletRequest request) {
        String clientId = CommonUtils.getToken("client_id", request);

        boolean status = reportService.updateStock(cnt, clientId);

        JSONObject resBean = new JSONObject();
        resBean.put("status", status);
        return CommonUtils.success(resBean);
    }

}
