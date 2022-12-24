package com.lemonico.wms.service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw210_shipment_result;
import com.lemonico.wms.bean.ShimentListBean;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: sunlogic
 * @description: 出庫
 * @create: 2020-07-06 13:35
 **/
public interface ShipmentService
{

    /**
     * @Description: 出庫依頼一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/06/30
     */
    JSONObject getShipmentsLists(JSONObject jsonObject);

    /**
     * @Description: 出庫依頼详细
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2022/01/12
     */
    Tw200_shipment getShipmentsDetail(String warehouse_cd, String shipment_plan_id);

    /**
     * @Description: 出庫依頼按照商品code分组
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/11/2
     */
    JSONObject getShipmentsGroupList(JSONObject jsonObject);

    /**
     * @Description: 出庫依頼一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/06
     */
    List<ShimentListBean> getShipmentsCsvList(JSONObject jsonObject);

    public List<Tw200_shipment> getShipmentsListByPlanIds(List<String> ids);

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    List<Tw200_shipment> getShipmentsIncidents(String warehouse_cd, String shipment_plan_id);

    /**
     * 出庫検品更新シリアル番号
     *
     * @param jsonObject
     * @return
     * @Date: 2021/10/22
     */
    JSONObject updateSerialNo(JSONObject jsonObject);

    /**
     * @description ntm检品修改配送公司
     * @param warehouseCd 仓库CD
     * @param shipmentPlanId 出库ID
     * @return null
     * @date 2021/7/19
     **/
    public void changeNtmDeliveryMethod(String warehouseCd, String shipmentPlanId);

    public void asyncNtmEccubeStatusMessage(String shipmentPlanId, Integer shipmentStatus, String statusMessage);

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    public JSONObject updateShipmentsIncidents(String warehouse_cd, String shipment_plan_id, Integer shipment_status,
        String sizeName, Integer boxes, HttpServletRequest servletRequest, String user_id);

    /**
     * @Description: 出庫検品法人个人统计
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/06/09
     */
    public JSONObject incidentsCount(String warehouse_cd);

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/15
     */
    public Integer[] getShipmentStatusCount(JSONObject jsonObject);

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/6/21
     */
    public JSONArray getAllShipmentStatusCount(JSONObject jsonObject);

    /**
     * @Description: 出庫編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    public Integer updateShipments(HttpServletRequest servletRequest, JSONObject jsonObject);

    /**
     * @Description: 出庫個口数編集
     * @Param: json
     * @return: json
     * @Author: zhangmj
     * @Date: 2020/11/26
     */
    public Integer updateBoxes(HttpServletRequest servletRequest, JSONObject jsonObject);

    /**
     * @description 更新默认运费
     * @param orderNoList 外部受注番号
     * @param warehouseCd 仓库CD
     * @return null
     * @date 2021/7/20
     **/
    public void syncDefaultShipmentFreight(List<String> orderNoList, String warehouseCd);

    /**
     * @Description: ステータス
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    public Integer setShipmentStatus(HttpServletRequest servletRequest, String warehouse_cd, Integer shipment_status,
        String[] shipment_plan_id, String sizeName, String status_message, Integer boxes, boolean chang_flg,
        String operation_cd, String user_id);

    /**
     * @Description: 自动出荷作業を開始
     * @Param: 顧客CD, 出庫依頼
     * @return: JSONObject
     * @Date: 2021/11/03
     */
    public JSONObject automaticInsertShipmentResult(HttpServletRequest servletRequest, JSONArray jsonArray,
        String warehouse_cd);

    /**
     * @Description: 出荷作业开始数组重组
     * @Param: JSONArray
     * @return: List
     * @Date: 2021/11/10
     */
    public List<JSONObject> reorganizationArray(JSONArray errArray);

    /**
     * @Description: 出荷作業中
     * @Param: Json
     * @return: Integer
     * @Date: 2020/7/8
     */
    public JSONObject insertShipmentResult(HttpServletRequest servletRequest, JSONObject jsonObject,
        String warehouse_cd);

    /**
     * @Description: 出荷作業中取消
     * @Param: Json
     * @return: Integer
     * @Date: 2020/12/22
     */
    public JSONObject shipmentWorkCancel(HttpServletRequest servletRequest, JSONObject jsonObject, String warehouse_cd);

    /**
     * @Description: 作業管理IDを取得
     * @Param: null
     * @return: Integer
     * @Date: 2020/7/9
     */
    public Integer setWorkId();

    /**
     * @Description: 作業管理
     * @Param: null
     * @return: List
     * @Date: 2020/7/14
     */
    public List<Tw210_shipment_result> getWorkNameList(String warehouse_cd, String client_id);

    /**
     * @Param: jsonObject
     * @description: トータルピッキングリストPDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createShipmentOrderListPDF(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createProductDetailPDFworking(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createProductDetailPDF(JSONObject jsonObject);

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createInstructionsPDFworking(JSONObject jsonObject);

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createInstructionsPDF(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 明細書・指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    JSONObject createProductDetailInstructionsPDF(JSONObject jsonObject);

    /**
     * @Param: workId： 作業管理ID
     * @description: 根据workId 查询出庫依頼ID
     * @return: java.util.List<java.lang.String>
     * @date: 2020/7/23
     */
    List<String> getShipmentIdListByWorkId(List<Integer> work_id);

    /**
     * @Param: workName : 作業管理 ,shipment_status ：処理ステータス
     * @description: 根据作业name获取作业者Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    JSONObject getWorkIdByName(String warehouse_cd, String client_id, String work_name, String shipment_status);

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @param: servletRequest
     * @description: 出荷作業を完了
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    JSONObject finishShipment(JSONObject jsonObject, HttpServletRequest servletRequest);

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @param: servletRequest
     * @description: 仓库侧快递公司CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    JSONObject shipmentsCompanyCsvUpload(HttpServletRequest req, MultipartFile file, Integer flag, String warehouse_cd);

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @description: 出荷完了を取り消す
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/3
     */
    JSONObject cancelShipment(JSONObject jsonObject, HttpServletRequest servletRequest);

    /**
     * @Description: 出荷検品サイズPDF
     * @Param: jsonObject
     * @return: SUCCESS
     * @Date: 2020/9/14
     */
    public JSONObject shipmentSizePdf();

    /**
     * @Param: file
     * @param: shipment_plan_id
     * @Param: client_id
     * @description: 梱包作業画像を添付上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/27
     */
    JSONObject uploadConfirmImg(MultipartFile[] file, String[] shipment_plan_id, String client_id);

    /**
     * @param warehouse_cd : 仓库id
     * @param workIdList : 作业者Id集合
     * @description: 获取トータルピッキングlist
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 16:18
     */
    JSONObject getPickingList(String warehouse_cd, List<Integer> workIdList);

    /**
     * @param jsonObject : 存的出庫作業ロケ明細 Id
     * @description: 修改 トータルピッキング确认状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 15:46
     */
    JSONObject updatePinkingStatus(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param warehouse_cd : 仓库Id
     * @description: 获取没有トータルピッキング确认的作业者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 17:52
     */
    JSONObject getWorkInfo(String warehouse_cd);

    /**
     * @param warehouse_cd : 仓库id
     * @param startDate : 起始时间
     * @param endDate : 结束时间
     * @param type : 1：出庫依頼ごとトータルピッキングレポート 2：作業者ごとトータルピッキングレポート
     * @param outputType: 出力単位: 0 指定期間の総計 1 指定期間の日付別
     * @description: とトータルピッキングレポートCSV数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/9 16:49
     */
    JSONObject getStatistics(String warehouse_cd, String startDate, String endDate, Integer type, String outputType);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/19 9:45
     */
    JSONObject getShipmentCancelCount(String warehouse_cd, String client_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/21
     */
    HashMap getAllShipmentCancelCount(String warehouse_cd, String client_id);

    /**
     * @param warehouse_cd ： 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 出庫撮影
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 11:11
     */
    JSONObject getPhotography(String warehouse_cd, String shipment_plan_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 更改撮影状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 12:53
     */
    JSONObject updatePhotography(String warehouse_cd, String shipment_plan_id, HttpServletRequest request);

    /**
     * @param jsonObject : warehouse_cd : 仓库Id shipment_plan_id : 出库依赖Id product_id : 商品Id client_id : 店铺Id
     * @description: 获取可以进行引当振替処理的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/15 9:52
     */
    JSONObject getReserveData(JSONObject jsonObject);

    /**
     * @param jsonObject : shipment: 出库数据 reserve: 被引当替换的数据 client_id: 店铺Id
     * @param request : 请求 为了获取token
     * @description: 进行引当振替処理
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/16 9:51
     */
    JSONObject updateReserveData(String shipment_plan_id, JSONObject jsonObject, HttpServletRequest request);

    /**
     * サイズ編集
     *
     * @param servletRequest
     * @param jsonObject
     * @date: 2021/5/17
     */
    Integer updateSize(HttpServletRequest servletRequest, JSONObject jsonObject);

    /**
     * 验证某个出库状态的数据是否存在
     *
     * @param warehouse_cd
     * @param shipment_plan_id
     * @param shipment_status
     * @return
     * @Date: 2021/5/20
     */
    public Integer getShipmentListByStatus(String warehouse_cd, String[] shipment_plan_id, Integer shipment_status);

    /**
     * @param warehouse_cd : 货架Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param loginNm : 用户名
     * @param nowDate : 现在时间
     * @description: 在库调整后 判断是否有出库状态需要改变的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/9 18:08
     */
    JSONObject judgeShipmentStatus(String warehouse_cd, String client_id, String product_id, String loginNm,
        Date nowDate);

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 将该出库依赖下的所有商品的シリアル番号清空
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 12:52
     */
    JSONObject emptySerialNo(String warehouse_cd, String shipment_plan_id);

    /**
     * @description 根据出库信息获取传票PDF
     * @param list 出库信息
     * @param ids 出库ID
     * @return JSONObject
     * @date 2021/7/20
     **/
    public JSONObject bizLogiPdf(List<Tw200_shipment> list, List<String> ids, String boxes);

    /**
     * @description 再发行PDF获取
     * @param shipmentNumber 再发行ID
     * @return JSONObject
     * @date 2021/7/20
     **/
    public JSONObject bizLogiRetryPdf(String shipmentNumber);

    public JSONObject bizLogiData(List<Tw200_shipment> list, List<String> ids);

    /**
     * @param jsonObject : warehouse_cd : 仓库Id shipment_plan_id : 多个出库依赖Id以，拼接
     * @description: 判断出荷作業を完了所选的的出库依赖中是否含有过期或者不可出库的商品
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/3 13:08
     */
    JSONObject judgmentShipment(JSONObject jsonObject);
}
