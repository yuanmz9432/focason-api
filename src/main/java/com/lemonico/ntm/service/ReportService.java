package com.lemonico.ntm.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw200_shipment;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface ReportService
{

    /**
     * @Description: 法人出荷相关接口
     *               @Date： 2021/4/14
     *               @Param： client_id,warehouse_cd,shipment_plan_date
     *               @return： JSONObject
     */
    List<Tw200_shipment> monthDeliveryDetails(HttpServletRequest httpServletRequest, String warehouse_cd, String search,
        String starTime, String endTime, String form, String type, String transform);

    /**
     * @Description: 出荷确定处理
     *               @Date： 2021/4/14
     *               @Param： client_id,warehouse_cd,shipment_plan_date
     *               @return： JSONObject
     */
    Integer updateDeliveryHandle(JSONObject jsonObject, HttpServletRequest httpServletRequest);

    /**
     * @description 获取有效在库数
     * @param clientId 店铺id
     * @return String
     * @date 2021/4/25
     **/
    String getStockAvailableCnt(String clientId);

    /**
     * @description 更新在库表
     * @param cnt 商品数量
     * @param clientId 店铺id
     * @return: boolean
     * @date 2021/4/25
     **/
    boolean updateStock(String cnt, String clientId);

    /**
     * @description 根据日期获取封筒数据
     * @param date 日期
     * @param deliveryCarrier 配送方式
     * @param clientId 店铺id
     * @return: List
     * @date 2021/4/25
     **/
    List<JSONObject> getSealDataByDate(String date, String deliveryCarrier, String clientId);
}
