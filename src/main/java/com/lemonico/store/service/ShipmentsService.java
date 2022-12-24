package com.lemonico.store.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms009_definition;
import com.lemonico.common.bean.Tw200_shipment;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Program: sunlogic
 * @Description: 店舗側出庫情報
 * @Date: 2020/05/12
 */
public interface ShipmentsService
{

    /**
     * @Description: 获取店舗側出庫情報
     * @Param: client_id: 店铺ID，page: ページ数，pageSize: 表示数量
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: List
     * @Date: 2020/05/12
     */
    List<Tw200_shipment> getShipmentsCsvList(JSONObject searchJson);

    /**
     * 获取出库依赖详细
     *
     * @param client_id
     * @param shipment_plan_id
     * @return
     */
    Tw200_shipment getShipmentsDetail(String client_id, String shipment_plan_id, String copy_flg);

    /**
     * @Description: 店舗側出庫情報新規
     * @Param: Tw200_shipment
     * @return: Integer
     * @Date: 2020/05/12
     */
    public Integer setShipments(JSONObject jsonParam, boolean insertFlg, HttpServletRequest httpServletRequest);

    /**
     * @Description: 店舗側出庫情報取得
     * @Param: 顧客CD， 出庫依頼ID
     * @return: List
     * @Date: 2020/5/13
     */
    public Integer countShipments(String client_id, String shipment_plan_id);

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD, 出庫依頼ID
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    public JSONObject deleteShipmentsList(String client_id, String shipment_plan_id,
        HttpServletRequest httpServletRequest);

    /**
     * @Description: 店舗側出庫情報を保留する
     * @Param: 顧客CD, 出庫依頼ID，ステータス理由
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    public JSONObject keepShipmentsList(String client_id, JSONObject jsonObject, HttpServletRequest httpServletRequest);

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD， 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/13
     */
    public Integer deleteShipments(HttpServletRequest httpServletRequest, String account_id, JSONObject jsonObject,
        boolean deleteFlg);

    /**
     * @Description: 出庫依頼ID生成
     * @param client_id : 店铺Id
     * @return: String
     * @Date: 2020/5/14
     */
    public String setShipmentPlanId(String client_id);

    /**
     * @Description: 名称区分マスタ
     * @Param: 名称区分, 名称コード, 倉庫コード
     * @return: List
     * @Date: 2020/5/29
     */
    public List<Ms009_definition> getDefinitionList(String warehouse_cd, Integer[] sys_kind, String sys_cd);

    /**
     * @param: jsonObject
     * @param: request
     * @description: 出庫依頼PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/10
     */
    public JSONObject getShipmentsPDF(JSONObject jsonObject, ServletRequest servletRequest, Integer flg,
        String startTime, String endTime);

    /**
     * @param: jsonObject
     * @description:出庫依赖CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    public JSONObject shipmentsCsvUpload(HttpServletRequest req, MultipartFile file, String client_id,
        ServletRequest servletRequest);

    /**
     * @Param: items
     * @description: 拼接セット商品
     * @return: com.alibaba.fastjson.JSONArray
     * @date: 2020/9/10
     */
    JSONArray splicingSetProduct(JSONArray items, Integer status);

    /**
     * @Param: file
     * @param: shipment_plan_id
     * @Param: client_id
     * @description: 梱包作業画像を添付上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/27
     */
    JSONObject uploadConfirmPdf(MultipartFile file[], String[] shipment_plan_id, String client_id);

    /**
     * @Param: client_id ：店铺Id
     * @param: shipment_status ： 出库状态
     * @param: search ： 搜索内容
     * @param: tags_id ： tag
     * @param: startTime ： 起始日
     * @param: endTime ： 结束日
     * @param: calcen : 删除状态
     * @description: 获取出库依赖一览数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/23
     */
    JSONObject getShipmentsList(JSONObject jsonObject);

    /**
     * @Param: file : 添付ファイルについて
     * @param: client_id : 店铺Id
     * @param: shipments_plan_id ： 出库依赖Id
     * @description: 添付ファイルについて
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/7
     */
    JSONObject uploadDeliveryFile(MultipartFile[] file, String shipment_plan_id, String client_id);

    /**
     * @Description: 店铺SHOPIFYから受注情報を取得し
     * @Param: 店舗ID
     * @return: List
     * @Author: HuangZhimin
     * @Date: 2020/12/8
     */
    public List<Tw200_shipment> getUntrackedShipments(@Param("client_id") String client_id,
        @Param("template") String template);

    /**
     * @Description: 外部連携フラグ更新
     * @Param: 店舗ID
     * @Param: 倉庫CD
     * @return: Integer
     * @Author: HuangZhimin
     * @Date: 2020/12/8
     */
    public Integer setShipmentFinishFlg(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd, @Param("shipment_plan_id") String shipment_plan_id);

    /**
     * @Description: EcForce連携の受注番号を取得(出荷完了・送り状番号あり)
     * @Param: 店舗ID
     * @return: List<Tw200_shipment>
     * @Author: HuangZhimin
     * @Date: 2020/12/8
     */
    public List<Tw200_shipment> getEcShipInfo(@Param("client_id") String client_id,
        @Param("outer_order_no") String outer_order_no);

    /**
     * @Param: jsonParam : 添付ファイル路径写入
     * @description: 添付ファイル路径写入
     * @return: Integer
     * @date: 2021/3/18
     */
    public Integer saveDeliveryFilePath(JSONObject jsonObject);

    /**
     * @Description: GMO請求情報を取得
     * @Param: 店舗ID
     * @return: List<Tw200_shipment>
     * @Author: HuangZhimin
     * @Date: 2021/4/16
     */
    public List<Tw200_shipment> getGMOBillBarcode(@Param("client_id") String client_id);

    /**
     * @Description: GMO請求情報を更新
     * @Param: 店舗ID
     * @return: List<Tw200_shipment>
     * @Author: HuangZhimin
     * @Date: 2021/4/16
     */
    public Integer updateGMOBillBarcode(@Param("shipment_plan_id") String shipment_plan_id,
        @Param("bill_barcode") String bill_barcode);

    /**
     * @param jsonParam
     * @description: 店铺侧 纳品书—作业指示书 数据拼接
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/8 15:50
     */
    JSONObject getShipmentsA3PDF(JSONObject jsonParam);

    /**
     * @param jsonObject : 被拆分的商品Id 出库依赖Id
     * @param request : 响应
     * @description: 拆分出库依赖ID
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/15 11:17
     */
    JSONObject splitShipment(String client_id, String shipment_plan_id, JSONObject jsonObject,
        HttpServletRequest request);

    /**
     * @param:
     * @description: 获取可以合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    JSONArray getMergeShipmentList(String client_id, String search);

    /**
     * @param:
     * @description: 合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    Boolean mergeShipment(String client_id, String shipment_plan_id, JSONObject jsonParam);

    /**
     * @param: jsonObject
     * @description: 出库删除恢复
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/09/18
     */
    JSONObject shipmentsResurrection(JSONObject jsonParam, HttpServletRequest servletRequest);

    /**
     * @Description: Qoo10店舗の送状番号連携情報を取得
     * @Param: 店舗ID
     * @return: List
     * @Date: 2021/09/07
     */
    public List<Tw200_shipment> getShipmentListQoo10(@Param("client_id") String client_id,
        @Param("template") String template);

    /**
     * @param jsonObject : product_id = 商品Id shipment_plan_id = 出库依赖Id client_id = 店铺Id
     * @param request : 请求
     * @param status : 0 ---> 修改商品信息 1 ---> 不修改商品信息
     * @description: 系统中之前不存在的商品登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/9 18:01
     */
    JSONObject shipmentInsertProduct(JSONObject jsonObject, HttpServletRequest request, int status);

    /**
     * 获取出库状态下的出库依赖数
     * 
     * @param client_id
     * @param statusList
     * @param shipment_plan_id
     * @return
     */
    Integer getShipmentStatus(String client_id, List<Integer> statusList, String shipment_plan_id);

}
