package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.lemonico.common.bean.Tc204_order_template;
import com.lemonico.common.bean.Tc205_order_company;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.datasource.DataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 受注依頼管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
@Api(tags = "受注連携")
public class OrderController
{
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final OrderHistoryService orderHistoryService;
    private final OrderApiService orderApiService;
    private final NtmOrderService ntmOrderService;
    private final OrderDao orderDao;

    /**
     * 店鋪情報および受注CSVテンプレート情報を取得する
     *
     * @param client_id 店鋪ID
     * @return 店鋪情報
     * @since 1.0.0
     */
    @ApiOperation(value = "店舗情報取得", notes = "店舗情報取得")
    @GetMapping(value = "/getStoreInfo/{client_id}")
    public JSONObject getClientAndCsvTemplates(@PathVariable("client_id") String client_id) {
        logger.info("店舗情報の取得:" + client_id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return orderService.getStoreInfo(client_id);
    }

    /**
     * 受注依頼情報を検索する
     *
     * @param client_id 店鋪ID
     * @param status ステータス
     * @param page ページ
     * @param size サイズ
     * @param column カラム
     * @param sort ソート順
     * @param checkList 選択行
     * @param start_date 開始日付
     * @param end_date 終了日付
     * @param orderType 受注タイプ
     * @param search 検索条件
     * @param request_date_start 注文開始日
     * @param request_date_end 注文終了日
     * @param delivery_date_start 配送開始日
     * @param delivery_date_end 配送終了日
     * @param form form
     * @param identifier 識別子
     * @return 受注依頼情報
     * @since 1.0.0
     */
    @ApiOperation(value = "受注情報取得", notes = "店舗情報取得")
    @GetMapping(value = "/getOrderDateList/{client_id}")
    public JSONObject getOrderDateList(@PathVariable("client_id") String client_id, Integer status, Integer page,
        Integer size, String column, String sort, @RequestParam("checkList") List<String> checkList,
        String start_date, String end_date,
        String orderType, String search, String request_date_start, String request_date_end,
        String delivery_date_start, String delivery_date_end, String form, String identifier) {
        // ログ出力
        logger.info("店舗情報の取得:" + client_id);
        try {
            JSONObject jsonObject =
                orderService.getOrderDateList(client_id, status, 0, page, size, column, sort, start_date,
                    end_date, checkList, orderType, search, request_date_start, request_date_end, delivery_date_start,
                    delivery_date_end, form, identifier);
            return CommonUtils.success(jsonObject);
        } catch (Exception e) {
            // 該当するテーブルから店舗情報＆倉庫情報を取得し、FrontにReponse
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 受注管理、受注明細にInsertする実部分
     *
     * @param jsonObject Tc200_order Tc201_order_detail String[] OrderSqlService
     * @return JSONObject
     * @author wang
     * @date 2020-07-09
     */
    @ApiOperation(value = "出庫依頼する", notes = "出庫依頼する")
    @PostMapping(value = "/shipments/create/{client_id}")
    public JSONObject createShipments(@PathVariable("client_id") String client_id, @RequestBody JSONObject jsonObject,
        HttpServletRequest request) {
        // ログ出力
        logger.info("出庫依頼の実施:" + client_id);
        logger.info("選択したITEM:" + jsonObject.getString("list"));
        return orderService.createShipments(jsonObject, client_id, request);
    }

    /**
     * @param client_id 顧客管理番号(店舗IDにも呼ばれる)
     * @param warehouse_cd 倉庫管理番号(倉庫コード)
     * @return 成功或いは失敗した情報
     * @Description CSV取込画面アップロードしたCSVファイルを取込</ br> 成功した場合、関連DBに登録や更新</br>
     *              失敗した場合、DBトランザクションロールバックし、エラー情報を返す
     */
    @ApiOperation(value = "受注CSV取込")
    @PostMapping("/import/csv/{client_id}")
    public JSONObject importOrdersByCsv(@PathVariable("client_id") String client_id,
        @RequestParam("warehouse_cd") String warehouse_cd,
        @RequestParam("template_cd") Integer template_cd,
        @RequestParam("filename") MultipartFile file,
        @RequestParam("shipmentStatus") String shipmentStatus,
        String company_id, HttpServletRequest request) {
        logger.info("受注CSV取込開始： {}-{}-{}", client_id, warehouse_cd, template_cd);
        logger.info("受注CSV取込ファイル名： {}", file.getOriginalFilename());

        return orderService.importOrderCsv(file, client_id, warehouse_cd, shipmentStatus, request, template_cd,
            company_id, false);
    }

    /**
     * @param client_id 顧客管理番号(店舗IDにも呼ばれる)
     * @param warehouse_cd 倉庫管理番号(倉庫コード)
     * @return 成功或いは失敗した情報
     * @Description CSV取込画面アップロードしたCSVファイルを取込</ br> 成功した場合、関連DBに登録や更新</br>
     *              失敗した場合、DBトランザクションロールバックし、エラー情報を返す
     */
    @ApiOperation(value = "受注CSV取込")
    @PostMapping("/import/ftp/csv")
    @Transactional(rollbackFor = Exception.class)
    public JSONObject importOrderFtpCsv(@RequestParam("client_id") String client_id,
        @RequestParam("warehouse_cd") String warehouse_cd, @RequestParam("template_cd") Integer template_cd,
        @RequestParam("filename") MultipartFile file, @RequestParam("shipmentStatus") String shipmentStatus,
        String company_id, HttpServletRequest request) throws Exception {
        logger.info("受注CSV取込の開始：" + client_id + "-" + warehouse_cd + "-" + template_cd);
        logger.info("受注CSV取込のFILE：" + file.getName());
        return orderService.importOrderCsv(file, client_id, warehouse_cd, shipmentStatus, request, template_cd,
            company_id, false);
    }

    /**
     * @param
     * @return
     * @throws @Description 获取csv头部信息
     */
    @ApiOperation(value = "获取csv头部信息")
    @PostMapping("/import/csv/getCsvHeader")
    public String getCsvHeader(@RequestParam("file") MultipartFile file, HttpServletRequest request, String encoding)
        throws Exception {
        InputStream stream = file.getInputStream();
        Reader reader = new InputStreamReader(stream, encoding);
        CsvReader csvReader = new CsvReader(reader);
        csvReader.readHeaders();
        return csvReader.getRawRecord();
    }

    /**
     * @Param: client_id : 店铺Id
     * @description: 受注取込試行一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/20
     */
    @RequestMapping(value = "/history/{client_id}", method = RequestMethod.GET)
    @ApiOperation("受注取込試行一覧")
    public JSONObject getOrderHistory(@PathVariable("client_id") String client_id, Integer page, Integer size,
        String column, String sort, String start_date, String end_date) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return orderHistoryService.getOrderHistoryList(client_id, page, size, column, sort, start_date, end_date);
    }

    /**
     * ある受注履歴IDに含まれる失敗の記録と成功した場合はその具体的内容を表示する
     *
     * @param history_id 受注履歴ID
     * @return JSON
     * @date 2020-06-26
     */
    @ApiOperation("受注取込履歴詳細表示")
    @GetMapping("/order/history/detail/{history_id}")
    public JSONObject getOrderHistoryDetail(@PathVariable("history_id") String history_id) {
        JSONObject resultJsonObject = new JSONObject();
        try {
            logger.info("受注明細情報(+α)取得");
            resultJsonObject = orderDetailService.getOrderHistoryDetail(history_id);
        } catch (DataSourceException e) {
            logger.error("データベースアクセス時にエラーが発生しました", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success(resultJsonObject);
    }

    /**
     * @Param: jsonObject : client_id,history_id
     * @description: 根据受注取込履歴ID 获取受注信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @RequestMapping(value = "getOrderListByHistoryId", method = RequestMethod.POST)
    @ApiOperation("根据受注取込履歴ID 获取受注信息")
    public JSONObject getOrderListByHistoryId(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,history_id");
        return orderService.getOrderListByHistoryId(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 更改店铺配送方法
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @RequestMapping(value = "delivery", method = RequestMethod.POST)
    @ApiOperation("修改店铺的配送方法")
    public JSONObject changeDeliveryMethod(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        return orderService.changeDeliveryMethod(jsonObject);
    }

    /**
     * @Param: jsonObject client_id,shipment_plan_id
     * @description: 生成出库PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @RequestMapping(value = "createShipmentPDF", method = RequestMethod.POST)
    @ApiOperation("生成出库PDF")
    public JSONObject createShipmentPDF(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,shipment_plan_id");
        return orderService.createShipmentPDF(jsonObject, request);
    }

    /**
     * @Param: jsonObject client_id,client_url
     * @description: 保存店铺受注关系表信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/9
     */
    @RequestMapping(value = "insertOrderClient", method = RequestMethod.POST)
    @ApiOperation("保存店铺受注关系表信息")
    public JSONObject insertOrderClient(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderService.insertOrderClient(jsonObject);
    }

    /**
     * @Param: jsonObject :
     *         client_id,ftp_host,ftp_user,ftp_passwd,ftp_path,ftp_filename
     * @description: 保存ftp表信息
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZMo
     * @date: 2020/9/9
     */
    @RequestMapping(value = "insertFtpClient", method = RequestMethod.POST)
    @ApiOperation("保存店铺FTP信息")
    public JSONObject insertFtpClient(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,ftp_host,ftp_file");
        return orderService.insertFtpClient(jsonObject);
    }

    /**
     * @Param:
     * @description: 获取店铺模板信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    @RequestMapping(value = "/store/getClientTemplate", method = RequestMethod.GET)
    @ApiOperation("获取店铺模板信息")
    public JSONObject getClientTemplate(String client_id, String company_id, Integer template_cd) {
        List<Tc204_order_template> list = orderDao.getClientTemplate(client_id, company_id, template_cd);
        return CommonUtils.success(list);
    }

    /**
     * @Param:
     * @description: 获取各大公司模板信息
     * @return:
     * @date: 2020/9/15
     */
    @RequestMapping(value = "/store/getCompanyTemplate", method = RequestMethod.GET)
    @ApiOperation("获取各大公司模板信息")
    public JSONObject getCompanyTemplate(String company_id) {
        List<Tc205_order_company> list = orderDao.getCompanyTemplate(company_id);
        return CommonUtils.success(list);
    }

    /**
     * @Param: client_id
     * @param: template
     * @Param: apiName
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    @RequestMapping(value = "getOrderClientInfo/{client_id}", method = RequestMethod.GET)
    @ApiOperation("根据模板查询店铺受注关系表")
    public JSONObject getOrderClientInfo(@PathVariable("client_id") String client_id, String template, String apiName) {
        return orderService.getOrderClientInfo(client_id, template, apiName);
    }

    /**
     * @Param: client_id
     * @description: 查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    @RequestMapping(value = "getOrderClientList/{client_id}", method = RequestMethod.GET)
    @ApiOperation("查询店铺受注关系表")
    public JSONObject getOrderClientList(@PathVariable("client_id") String client_id) {
        return orderApiService.getOrderClientList(client_id);
    }

    /**
     * @Param: jsonObject
     * @description: 删除Api设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    @RequestMapping(value = "deleteApiSet", method = RequestMethod.POST)
    @ApiOperation("删除Api设定数据")
    public JSONObject deleteApiSet(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "id,client_id");
        return orderApiService.deleteApiSet(jsonObject);
    }

    /**
     * @Param:
     * @description: 新规店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    @RequestMapping(value = "createClientTemplate", method = RequestMethod.POST)
    @ApiOperation("新规店铺受注csv模板信息")
    public JSONObject getOrderClientInfo(@RequestBody JSONObject js) {
        orderService.createClientTemplate(js);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 更新店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    @RequestMapping(value = "updateClientTemplate", method = RequestMethod.PUT)
    @ApiOperation("更新店铺受注csv模板信息")
    public JSONObject updateClientTemplate(@RequestBody JSONObject js) {
        orderService.updateClientTemplate(js);
        return CommonUtils.success();
    }

    /**
     * @Param:
     * @description: 删除店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    @RequestMapping(value = "deleteClientTemplate", method = RequestMethod.DELETE)
    @ApiOperation("删除店铺受注csv模板信息")
    public JSONObject deleteClientTemplate(Integer template_cd) {
        orderDao.deleteClientTemplate(template_cd);
        return CommonUtils.success();
    }

    /**
     * @Description: 受注取消
     * @Param:
     * @return:
     * @Date: 2020/11/12
     */
    @RequestMapping(value = "/shipments/delete", method = RequestMethod.DELETE)
    @ApiOperation("受注取消")
    public JSONObject orderShipmentsDelete(String[] purchase_order_no, HttpServletRequest httpServletRequest) {
        orderService.orderShipmentsDelete(purchase_order_no, httpServletRequest);
        return CommonUtils.success();
    }

    /**
     * @Param: client_id
     * @param: get_send_flag
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2020/11/4
     */
    @RequestMapping(value = "getFtpClientInfo/{client_id}", method = RequestMethod.GET)
    @ApiOperation("根据模板查询店铺受注关系表")
    public JSONObject getOrderFtpInfo(@PathVariable("client_id") String client_id, Integer get_send_flag) {
        return orderService.getFtpClientInfo(client_id, get_send_flag);
    }

    /**
     * @Description: 受注取消履历一览
     * @Param:
     * @return:
     * @Date: 2020/11/18
     */
    @ApiOperation("受注取消履历一览")
    @RequestMapping(value = "/cancel/history/{client_id}", method = RequestMethod.GET)
    public JSONObject selectCancelHistory(@PathVariable("client_id") String client_id, Integer page, Integer size,
        String column, String sort, String start_date, String end_date) {
        try {
            JSONObject jsonObject = orderService.getOrderDateList(client_id, null, 1, page, size, column, sort,
                start_date, end_date, null, null, null, null, null, null, null, null, null);
            return CommonUtils.success(jsonObject);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: S3获取文件列表
     * @Param:
     * @return:
     * @Date: 2021/01/14
     */
    @ApiOperation("S3获取文件列表")
    @RequestMapping(value = "/order/s3/fileList", method = RequestMethod.GET)
    public JSONObject getS3FileList(String bucket, String password1, String password2, String folder) {
        return CommonUtils.success(orderService.getS3FileList(bucket, password1, password2, folder));

    }

    /**
     * @Description: S3获取文件夹
     * @Param:
     * @return:
     * @Date: 2021/01/14
     */
    @ApiOperation("S3获取文件夹")
    @RequestMapping(value = "/order/s3/folder", method = RequestMethod.GET)
    public JSONObject getS3Folder(String bucket, String password1, String password2) {
        return CommonUtils.success(orderService.getS3Folder(bucket, password1, password2));
    }

    /**
     * @Param:
     * @description: 下载读取S3指定文件
     * @return: list
     * @date: 2021/1/14
     */
    @ApiOperation("下载读取S3指定文件")
    @RequestMapping(value = "/s3/s3CsvDownload", method = RequestMethod.POST)
    public JSONObject s3CsvDownload(String bucket, String password1, String password2, String filePath,
        String client_id, @RequestParam("warehouse_cd") String warehouse_cd,
        @RequestParam("template_cd") Integer template_cd, @RequestParam("shipmentStatus") String shipmentStatus,
        String company_id, HttpServletRequest request) {
        JSONObject js = orderService.s3CsvDownload(bucket, password1, password2, filePath, client_id, warehouse_cd,
            template_cd, shipmentStatus, company_id, request);
        return js;
    }

    /**
     * @Param:
     * @description: 获取s3设定信息
     * @return:
     * @date: 2021/1/14
     */
    @ApiOperation("获取s3设定信息")
    @RequestMapping(value = "/s3/getS3Setting", method = RequestMethod.GET)
    public JSONObject getS3Setting(String client_id) {
        return CommonUtils.success(orderDao.getS3Setting(client_id));
    }

    /**
     * @Param:
     * @description: 新规s3设定信息
     * @return:
     * @date: 2021/1/14
     */
    @ApiOperation("新规s3设定信息")
    @RequestMapping(value = "/s3/insertS3Setting", method = RequestMethod.POST)
    public JSONObject insertS3Setting(String client_id, String bucket, String password1, String password2,
        String folder, String upload_folder) {
        orderService.insertS3Setting(client_id, bucket, password1, password2, folder, upload_folder);

        return CommonUtils.success();

    }

    /**
     * @Param: jsonObject
     * @description: 获取base token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    @ApiOperation("获取受注連携 token")
    @RequestMapping(value = "/update/token/", method = RequestMethod.POST)
    public JSONObject setOrderToken(@RequestBody JSONObject jsonObject) {
        // CommonUtil.hashAllRequired(jsonObject, "code,state");
        if (!StringTools.isNullOrEmpty(jsonObject.getString("code"))) {
            orderApiService.setBaseToken(jsonObject);
        }

        if (!StringTools.isNullOrEmpty(jsonObject.getString("uid"))) {
            orderApiService.setNextEngineToken(jsonObject);
        }
        if (!StringTools.isNullOrEmpty(jsonObject.getString("yahooCode"))) {
            orderApiService.setYahooToken(jsonObject);
        }
        return CommonUtils.success();
    }

    /**
     * @param jsonObject : BASE API模板的数据
     * @description: 新规或者编辑 BASE API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/9 15:44
     */
    @ApiOperation("新规或者编辑 BASE API模板")
    @RequestMapping(value = "insert/order/BASE", method = RequestMethod.POST)
    public JSONObject insertBASEOrder(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderApiService.insertBASEOrder(jsonObject);
    }

    /**
     * @param jsonObject : ColorMe API模板的数据
     * @description: 新规或者编辑 ColorMe API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/06/24 12:01
     */
    @ApiOperation("新规或者编辑 ColorMe API模板")
    @RequestMapping(value = "insert/order/colorme", method = RequestMethod.POST)
    public JSONObject insertColorMeOrder(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderApiService.insertColorMeOrder(jsonObject);
    }

    /**
     * @param jsonObject : MakeShop API模板的数据
     * @description: 新规或者编辑 MakeShop API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/07/05 19:44
     */
    @ApiOperation("新规或者编辑 MakeShop API模板")
    @RequestMapping(value = "insert/MakeShop", method = RequestMethod.POST)
    public JSONObject insertMakeShopAPIInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderApiService.insertMakeShopAPIInfo(jsonObject);
    }

    /**
     * @param jsonObject : MakeShop API模板的数据
     * @description: 新规或者编辑 MakeShop API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @author: YuanMingZe
     * @date: 2021/07/05 19:44
     */
    @ApiOperation("新规或者编辑 YAHOO API模板")
    @RequestMapping(value = "insert/order/yahoo", method = RequestMethod.POST)
    public JSONObject insertYahooAPIInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderApiService.insertYahooAPIInfo(jsonObject);
    }

    /**
     * @param jsonObject : next-engine模板的数据
     * @description: 新规或者编辑 next-engine API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/17 10:47
     */
    @ApiOperation("新规或者编辑 next-engine API模板")
    @RequestMapping(value = "insert/order/next-engine", method = RequestMethod.POST)
    public JSONObject insertNextEngineAPIInfo(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,client_url");
        return orderApiService.insertNextEngineAPIInfo(jsonObject);
    }

    /**
     * @Param:
     * @description: 更新s3设定信息
     * @return:
     * @date: 2021/1/14
     */
    @ApiOperation("更新s3设定信息")
    @RequestMapping(value = "/s3/updateS3Setting", method = RequestMethod.PUT)
    public JSONObject updateS3Setting(String client_id, String bucket, String password1, String password2,
        String folder, String upload_folder) {
        orderService.updateS3Setting(client_id, bucket, password1, password2, folder, upload_folder);
        return CommonUtils.success();
    }

    /*
     * @Description: 受注各个状态件数取得
     *
     * @Param: client_id
     *
     * @return: JSON
     *
     *
     * @Date: 2021/01/26
     */
    @ApiOperation("受注状态统计")
    @RequestMapping(value = "/count/{client_id}", method = RequestMethod.GET)
    public JSONObject orderCount(@PathVariable("client_id") String client_id) {
        try {
            return orderService.orderCount(client_id);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 获取受注时的错误信息
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/26
     */
    @ApiOperation("受注状态统计")
    @RequestMapping(value = "/error/message/{client_id}", method = RequestMethod.GET)
    public JSONObject orderErrorMessages(@PathVariable("client_id") String client_id, Integer status, Integer page,
        Integer size, String sort) {
        try {
            return orderService.orderErrorMessages(client_id, status, page, size, sort);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 更新消息为已读状态
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/27
     */
    @ApiOperation("更新消息为已读状态")
    @RequestMapping(value = "/error/status/{client_id}", method = RequestMethod.PUT)
    public JSONObject updOrderErrorMes(@PathVariable("client_id") String client_id, Integer[] order_error_no) {
        try {
            return orderService.updOrderErrorMes(client_id, order_error_no);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("受注取消统计")
    @RequestMapping(value = "/cancel/message/{client_id}", method = RequestMethod.GET)
    public JSONObject orderCancelMessages(@PathVariable("client_id") String client_id, Integer status, Integer page,
        Integer size, String sort) {
        return orderService.orderCancelMessages(client_id, status, page, size, sort);
    }

    /**
     * @param client_id ：店铺Id
     * @param order_cancel_no ： 多个受注番号
     * @description: 更新受注取消为確認済
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 10:37
     */
    @ApiOperation("更新受注取消为確認済")
    @RequestMapping(value = "/cancel/status/{client_id}", method = RequestMethod.PUT)
    public JSONObject updOrderCancelMes(@PathVariable("client_id") String client_id, String[] order_cancel_no) {
        return orderService.updOrderCancelMes(client_id, order_cancel_no);
    }

    /**
     * @param shipment_plan_id : 出库依赖Id
     * @description: 根据出库依赖Id获取出库取消信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/18 16:25
     */
    @ApiOperation("根据出库依赖Id获取出库取消信息")
    @RequestMapping(value = "/cancelInfo/{shipment_plan_id}", method = RequestMethod.GET)
    public JSONObject getCancelInfo(@PathVariable("shipment_plan_id") String shipment_plan_id) {
        return orderService.getCancelInfo(shipment_plan_id);
    }

    /**
     * @description:
     * @return:
     * @date: 2020/12/21
     */
    @ApiOperation("获取api连携平台信息")
    @RequestMapping(value = "/getApiStoreInfo", method = RequestMethod.GET)
    public JSONObject getApiStoreInfo(String template) {
        return CommonUtils.success(orderService.getApiStoreInfo(template));
    }

    /**
     * @param client_id : 店铺Id
     * @param warehouse_cd : 仓库Id
     * @param file : 上传的excel文件
     * @param shipmentStatus : 是否出库的flg， 1：出庫依頼する 2：出庫依頼しない
     * @param request : 请求
     * @description: NTM受注Excel取込
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/23 14:02
     */
    @ApiOperation(value = "NTM受注Excel取込")
    @PostMapping("/import/excel")
    public JSONObject importOrderExcel(@RequestParam("client_id") String client_id,
        @RequestParam("warehouse_cd") String warehouse_cd,
        @RequestParam("excelFile") MultipartFile file,
        @RequestParam("shipmentStatus") String shipmentStatus,
        @RequestParam("eccubeFLg") String eccubeFLg,
        HttpServletRequest request) {
        logger.info("受注Excel取込の開始: 店铺Id={}, 仓库Id={}, 上传文件的名称={}", client_id, warehouse_cd, file.getOriginalFilename());
        return ntmOrderService.importOrderExcel(file, client_id, warehouse_cd, shipmentStatus, eccubeFLg, request);
    }

    /**
     * @param api_name : api设定名称
     * @param client_id : 店铺Id
     * @param certificate : 证书
     * @param secretKey : 秘密键
     * @description: 上传yahoo的证书及秘密键
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/23 12:33
     */
    @ApiOperation("上传yahoo的证书及秘密键")
    @RequestMapping(value = "/upload/yahoo/file", method = RequestMethod.POST)
    public JSONObject uploadYahoo(@RequestParam("api_name") String api_name,
        @RequestParam("client_id") String client_id,
        @RequestParam("certificate") MultipartFile certificate,
        @RequestParam("secretKey") MultipartFile secretKey) {
        return orderService.uploadYahoo(api_name, client_id, certificate, secretKey);
    }

}
