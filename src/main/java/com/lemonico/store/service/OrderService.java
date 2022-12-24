package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.lemonico.common.bean.Ms013_api_template;
import com.lemonico.common.bean.Tc200_order;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.0
 * @description 受注管理・サービスインターフェース
 * @date 2020/06/18
 **/
public interface OrderService
{

    /**
     * @return 最新の受注番号
     * @Description 最新受注番号を取得
     * @Param なし
     */
    public String getLastPurchaseOrderNo();

    /**
     * @return Integer
     * @Description 新規受注を登録
     * @Param Tc200_order
     */
    public Integer setOrder(Tc200_order tc200_order) throws SQLException;


    /**
     * @description 批量插入受注订单和详情
     * @param list 受注订单list
     * @return: null
     * @date 2021/6/22
     **/
    public void batchInsertOrderAndOrderDetail(List<Tc200_order> list) throws SQLException;

    // /**
    // * アカウントに紐づいた受注管理テーブルの情報の取得
    // * @param client_id
    // * @return
    //// * @date 2020-06-24
    // */
    // public JSONObject getOrderList(String client_id);

    /**
     * 店舗情報の取得
     *
     * @param client_id
     * @return
     * @author wang
     * @date 2020-07-09
     */
    public JSONObject getStoreInfo(String client_id);

    /**
     * 店舗情報の取得
     *
     * @param client_id
     * @return
     * @author wang
     * @date 2020-07-15
     */
    public JSONObject importOrderCsv(MultipartFile file, String client_id, String wharehouse_cd, String status,
        HttpServletRequest request, Integer template_cd, String company_id, Boolean s3_flag);

    /**
     * NTM受注情報の取得
     *
     * @param client_id
     * @return
     * @author wang
     * @date 2020-07-15
     */
    public JSONObject getOrderDateList(String client_id, Integer status, Integer del_flg, Integer page, Integer size,
        String column, String sort, String start_date, String end_date, List<String> checkList,
        String orderType, String search, String request_date_start, String request_date_end, String delivery_date_start,
        String delivery_date_end, String form, String identifier);


    /**
     * 受注情報の取得
     *
     * @param client_id
     * @return
     * @date 2022-01-13
     */
    public JSONObject getSunlogiOrderDateList(String client_id, Integer status, Integer del_flg, Integer page,
        Integer size,
        String column, String sort, String start_date, String end_date);

    /**
     * 受注出庫依頼の処理
     *
     * @return
     * @author wang
     * @date 2020-07-15
     */
    public JSONObject createShipments(JSONObject jsonObject, String client_id, HttpServletRequest request);

    /**
     * @Param: jsonObject : client_id,history_id
     * @description: 根据受注取込履歴ID 获取受注信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    JSONObject getOrderListByHistoryId(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 更改店铺配送方法
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    JSONObject changeDeliveryMethod(JSONObject jsonObject);

    /**
     * @Param: jsonObject client_id,shipment_plan_id
     * @description: 生成出库PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    JSONObject createShipmentPDF(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Param: jsonObject client_id,client_url,template
     * @description: 保存店铺受注关系表信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/9
     */
    JSONObject insertOrderClient(JSONObject jsonObject);

    /**
     * @Param:
     * @description: 新规店铺受注csv模板信息
     * @return: Tc204_order_template
     * @date: 2020/9/16
     */
    Integer createClientTemplate(JSONObject js);

    /**
     * @Param:
     * @description: 编集店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    Integer updateClientTemplate(JSONObject js);

    /**
     * @Param: client_id
     * @param: template
     * @Param: apiName
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    JSONObject getOrderClientInfo(String client_id, String template, String apiName);

    /**
     * @Description: 受注取消
     * @Param:
     * @return:
     * @Date: 2020/11/12
     */
    Integer orderShipmentsDelete(String[] purchase_order_no, HttpServletRequest httpServletRequest);

    /**
     * @Param: jsonObject :
     *         client_id,client_id,ftp_host,ftp_user,ftp_passwd,ftp_path,ftp_filename
     * @description: 保存店铺FTP信息
     * @return: com.alibaba.fastjson.JSONObject
     *
     * @author: HZM
     * @date: 2020/10/19
     */
    public JSONObject insertFtpClient(JSONObject jsonObject);

    /**
     * @Param:
     * @description: 获取S3文件列表
     * @return: String
     * @date: 2021/1/14
     */
    public List<S3ObjectSummary> getS3FileList(String bucket, String password1, String password2, String folder);

    /**
     * @Param:
     * @description: 获取S3所有文件夹
     * @return: list
     * @date: 2021/1/14
     */
    public List<S3ObjectSummary> getS3Folder(String bucket, String password1, String password2);

    /**
     * @Param:
     * @description: 下载读取S3指定文件
     * @return: list
     * @date: 2021/1/14
     */
    public JSONObject s3CsvDownload(String bucket, String password1, String password2, String filePath,
        String client_id, String warehouse_cd, Integer template_cd, String shipmentStatus, String company_id,
        HttpServletRequest request);

    /**
     * @Param:
     * @description: 新规s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    public void insertS3Setting(String client_id, String bucket, String password1, String password2, String folder,
        String uploadFolder);

    /**
     * @Param:
     * @description: 更新s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    public void updateS3Setting(String client_id, String bucket, String password1, String password2, String folder,
        String uploadFolder);

    public JSONObject getFtpClientInfo(String client_id, Integer get_send_flag);

    /**
     * @Description: 受注各个状态件数取得
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/26
     */
    JSONObject orderCount(String client_id);

    /**
     * @Description: 获取受注时的错误信息
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/26
     */
    JSONObject orderErrorMessages(String client_id, Integer status, Integer page, Integer size, String sort);

    /**
     * @Description: 更新消息为已读状态
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/27
     */
    JSONObject updOrderErrorMes(String client_id, Integer[] order_error_no);

    /**
     * @param client_id : 店铺Id
     * @param status ： 受注取消 确认状态
     * @param page ： 页数
     * @param size ： 每页显示行数
     * @param sort ： 排序方式
     * @description: 受注取消统计
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 9:55
     */
    JSONObject orderCancelMessages(String client_id, Integer status, Integer page, Integer size, String sort);

    /**
     * @param client_id ：店铺Id
     * @param order_cancel_no ： 多个受注番号
     * @description: 更新受注取消为確認済
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 10:37
     */
    JSONObject updOrderCancelMes(String client_id, String[] order_cancel_no);

    /**
     * @param shipment_plan_id : 出库依赖Id
     * @description: 根据出库依赖Id获取出库取消信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/18 16:25
     */
    JSONObject getCancelInfo(String shipment_plan_id);

    /**
     * @Param:
     * @description: 获取api连携平台信息
     * @return:
     * @date: 2020/12/21
     */
    List<Ms013_api_template> getApiStoreInfo(String template);

    /**
     * 入金待ちから入金済みに変更処理
     *
     * @return
     * @author HZM
     * @date 2021-03-19
     */
    public JSONObject upOrderStatus(String client_id, String outer_order_no, String warehouse_cd,
        String shipment_plan_id, HttpServletRequest request);

    /**
     * @param api_name : api设定名称
     * @param client_id : 店铺Id
     * @param certificate : 证书
     * @param secretKey : 秘密键
     * @description: 上传yahoo的证书及秘密键
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/23 12:33
     */
    JSONObject uploadYahoo(String api_name, String client_id, MultipartFile certificate, MultipartFile secretKey);
}
