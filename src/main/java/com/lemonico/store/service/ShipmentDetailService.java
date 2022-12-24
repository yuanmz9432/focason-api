package com.lemonico.store.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw201_shipment_detail;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: sunlogic
 * @description: 出庫明細
 * @create: 2020-05-13
 **/
public interface ShipmentDetailService
{

    /**
     * @Description: 出庫明細テーブル一覧
     * @Param:
     * @return: List
     * @Date: 2020/5/13
     */
    public List<Tw201_shipment_detail> getShipmentDetailList(String client_id, String shipment_plan_id);

    /**
     * @Description: 出庫明細テーブル新规
     * @Param: Tw201_shipment_detail
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer setShipmentDetail(JSONObject jsonParam, boolean insertFlg, HttpServletRequest servletRequest);

    /**
     * @Description: 出庫明細テーブル削除
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/14
     */
    public Integer deleteShipmentDetail(String client_id, String shipment_plan_id, Date upd_date, String upd_usr);

    /**
     * @Param: items
     * @description: 验证 セット商品
     * @return: void
     * @date: 2020/9/3
     */
    void verificationSetProduct(JSONArray items);

    /**
     * set子商品拆分
     * 
     * @param items
     * @return
     */
    JSONArray setProductSplice(JSONArray items);

}
