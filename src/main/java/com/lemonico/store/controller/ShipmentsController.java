package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc200_customer_delivery;
import com.lemonico.common.bean.Ms009_definition;
import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlResourceNotFoundException;
import com.lemonico.core.exception.PlValidationErrorException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.ShipmentsDao;
import com.lemonico.store.service.CustomerDeliveryService;
import com.lemonico.store.service.ShipmentDetailService;
import com.lemonico.store.service.ShipmentsService;
import com.lemonico.store.service.SponsorSerivce;
import com.lemonico.store.service.impl.MacroSettingServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 出荷依頼管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "店舗側出庫情報")
public class ShipmentsController
{

    @Autowired
    private Environment env;

    private final ShipmentsService shipmentsService;
    private final ShipmentDetailService shipmentDetailService;
    private final SponsorSerivce sponsorSerivce;
    private final CustomerDeliveryService customerDeliveryService;
    private final ShipmentsDao shipmentsDao;
    private final MacroSettingServiceImpl macroSettingService;

    /**
     * @Param: client_id ：店铺Id
     * @param: shipment_status ： 出库状态
     * @param: search ： 搜索内容
     * @param: tags_id ： tag
     * @param: startTime ： 起始日
     * @param: endTime ： 结束日
     * @description: 获取出库依赖一览数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/23
     */
    @ApiOperation(value = "获取出库依赖一览数据", notes = "出庫依頼一覧")
    @GetMapping("/store/shipments/list/{client_id}")
    public JSONObject getShipmentsList(@PathVariable("client_id") String client_id,
        Integer shipment_status, String search, String tags_id, String startTime, String endTime,
        String delivery_carrier, String cash_on_delivery, String shipping_start_date, String shipping_end_date,
        String order_datetime_start, String order_datetime_end, String request_date_start,
        String request_date_end, String sponsor_id, String shipment_plan_date_start,
        String shipment_plan_date_end, Long min_total_price, Long max_total_price, String delivery_date_start,
        String delivery_date_end, Long min_product_plan_total, Long max_product_plan_total,
        String order_first_name, String delivery_time_slot, String identifier, String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file, String instructions_special_notes, Integer[] form, String status_message,
        Integer currentPage, Integer pageSize, String orderType, String cancel, String payment_method) {

        JSONObject searchJson = new JSONObject();
        searchJson.put("client_id", client_id);
        searchJson.put("search", search);
        searchJson.put("tags_id", tags_id);
        searchJson.put("orderType", orderType);
        searchJson.put("shipment_status", shipment_status);
        searchJson.put("delivery_carrier", delivery_carrier);
        searchJson.put("payment_method", payment_method);
        searchJson.put("cash_on_delivery", cash_on_delivery);
        searchJson.put("shipping_start_date", shipping_start_date);
        searchJson.put("shipping_end_date", shipping_end_date);
        searchJson.put("order_datetime_start", order_datetime_start);
        searchJson.put("order_datetime_end", order_datetime_end);
        searchJson.put("currentPage", currentPage);
        searchJson.put("pageSize", pageSize);

        searchJson.put("request_date_start", request_date_start);
        searchJson.put("request_date_end", request_date_end);
        searchJson.put("sponsor_id", sponsor_id);
        searchJson.put("shipment_plan_date_start", shipment_plan_date_start);
        searchJson.put("shipment_plan_date_end", shipment_plan_date_end);
        searchJson.put("min_total_price", min_total_price);
        searchJson.put("max_total_price", max_total_price);
        searchJson.put("delivery_date_start", delivery_date_start);
        searchJson.put("delivery_date_end", delivery_date_end);
        searchJson.put("min_product_plan_total", min_product_plan_total);
        searchJson.put("max_product_plan_total", max_product_plan_total);
        searchJson.put("order_first_name", order_first_name);
        searchJson.put("delivery_time_slot", delivery_time_slot);
        searchJson.put("identifier", identifier);
        searchJson.put("order_phone_number", order_phone_number);
        searchJson.put("price_on_delivery_note", price_on_delivery_note);
        searchJson.put("buy_id", buy_id);
        searchJson.put("email", email);
        searchJson.put("bundled_flg", bundled_flg);
        searchJson.put("buy_cnt", buy_cnt);
        searchJson.put("company", company);
        searchJson.put("file", file);
        searchJson.put("instructions_special_notes", instructions_special_notes);
        searchJson.put("form", form);
        searchJson.put("status_message", status_message);
        searchJson.put("cancel", cancel);

        return shipmentsService.getShipmentsList(searchJson);
    }

    /**
     * @Description: 出庫依頼明细
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2022/01/10
     */
    @ApiOperation(value = "出庫依頼明细", notes = "出庫依頼明细")
    @GetMapping("/store/shipments/detail/{client_id}/{shipment_plan_id}")
    public JSONObject getShipmentsDetail(@PathVariable("client_id") String client_id,
        @PathVariable("shipment_plan_id") String shipment_plan_id, String copy_flg) {

        Tw200_shipment list = shipmentsService.getShipmentsDetail(client_id, shipment_plan_id, copy_flg);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 出庫依頼CSV下载
     * @Param: 顧客CD, 出庫依頼ID
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: json
     * @Date: 2020/05/12
     */
    @ApiOperation(value = "出庫依頼CSV下载", notes = "出庫依頼CSV下载")
    @GetMapping("/store/shipments/csv/{client_id}")
    public JSONObject getShipmentsCsvList(@PathVariable("client_id") String client_id, Integer shipment_status,
        String search,
        String tags_id, String request_date_start, String request_date_end, String orderType,
        String shipping_start_date, String shipping_end_date, String delivery_carrier, String cash_on_delivery,
        String order_datetime_start, String order_datetime_end, String sponsor_id, String shipment_plan_date_start,
        String shipment_plan_date_end, Long min_total_price, Long max_total_price, String delivery_date_start,
        String delivery_date_end, Long min_product_plan_total, Long max_product_plan_total,
        String order_first_name, String delivery_time_slot, String identifier, String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file, String instructions_special_notes, Integer[] form, String status_message,
        String payment_method, boolean csv_flg) {

        JSONObject searchJson = new JSONObject();
        searchJson.put("client_id", client_id);
        searchJson.put("shipment_status", shipment_status);
        searchJson.put("search", search);
        searchJson.put("tags_id", tags_id);
        searchJson.put("request_date_start", request_date_start);
        searchJson.put("request_date_end", request_date_end);
        searchJson.put("orderType", orderType);
        searchJson.put("delivery_carrier", delivery_carrier);
        searchJson.put("payment_method", payment_method);
        searchJson.put("cash_on_delivery", cash_on_delivery);
        searchJson.put("shipping_start_date", shipping_start_date);
        searchJson.put("shipping_end_date", shipping_end_date);
        searchJson.put("order_datetime_start", order_datetime_start);
        searchJson.put("order_datetime_end", order_datetime_end);
        searchJson.put("sponsor_id", sponsor_id);
        searchJson.put("shipment_plan_date_start", shipment_plan_date_start);
        searchJson.put("shipment_plan_date_end", shipment_plan_date_end);
        searchJson.put("min_total_price", min_total_price);
        searchJson.put("max_total_price", max_total_price);
        searchJson.put("delivery_date_start", delivery_date_start);
        searchJson.put("delivery_date_end", delivery_date_end);
        searchJson.put("min_product_plan_total", min_product_plan_total);
        searchJson.put("max_product_plan_total", max_product_plan_total);
        searchJson.put("order_first_name", order_first_name);
        searchJson.put("delivery_time_slot", delivery_time_slot);
        searchJson.put("identifier", identifier);
        searchJson.put("order_phone_number", order_phone_number);
        searchJson.put("price_on_delivery_note", price_on_delivery_note);
        searchJson.put("buy_id", buy_id);
        searchJson.put("email", email);
        searchJson.put("bundled_flg", bundled_flg);
        searchJson.put("buy_cnt", buy_cnt);
        searchJson.put("company", company);
        searchJson.put("file", file);
        searchJson.put("instructions_special_notes", instructions_special_notes);
        searchJson.put("form", form);
        searchJson.put("status_message", status_message);
        searchJson.put("csv_flg", csv_flg);

        List<Tw200_shipment> list = shipmentsService.getShipmentsCsvList(searchJson);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 店舗側出庫情報登録
     * @Param:
     * @return:
     * @Date: 2020/05/12
     */
    @ApiOperation(value = "出庫依頼登録", notes = "出庫依頼登録")
    @PostMapping("/store/shipments/insert/{client_id}")
    @Transactional(rollbackFor = {
        RuntimeException.class, Error.class
    })
    public JSONObject insertShipments(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonParam, HttpServletRequest servletRequest,
        String startTime, String endTime) {

        String shipment_plan_id = shipmentsService.setShipmentPlanId(client_id);

        jsonParam.put("shipment_plan_id", shipment_plan_id);
        // jsonParam.put("memo", "color-白色--1");
        // 调用比较匹配比较方法 返回修改完的json 并重新赋值
        jsonParam = macroSettingService.setValueByCondition(jsonParam, client_id);
        // previewFlg 0:出庫依頼 1:明細書プレビュー;明細書プレビュー时只打印pdf，不更新表
        Integer previewFlg = jsonParam.getInteger("preview_flg");
        if (previewFlg == 1) {
            int priceOnDeliveryNote = Integer.parseInt(jsonParam.getString("price_on_delivery_note"));
            int flg = 0;
            if (priceOnDeliveryNote == 1) {
                // 价格PDF生成
                flg = 1;
            }
            return shipmentsService.getShipmentsPDF(jsonParam, servletRequest, flg, startTime, endTime);
        }
        if (previewFlg == 2) {
            return shipmentsService.getShipmentsA3PDF(jsonParam);
        }

        Integer result = 0;
        result = shipmentsService.setShipments(jsonParam, true, servletRequest);
        if (result <= 0) {
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        result = shipmentDetailService.setShipmentDetail(jsonParam, true, servletRequest);
        if (result <= 0) {
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return CommonUtils.success(shipment_plan_id);
    }

    /**
     * @Description: 店舗側出庫情報更新
     * @Param:
     * @return:
     * @Date: 2020/05/12
     */
    @ApiOperation(value = "出庫依頼更新", notes = "出庫依頼更新")
    @PutMapping("/store/shipments/update/{client_id}")
    @Transactional(rollbackFor = {
        RuntimeException.class, Error.class
    })
    public JSONObject updateShipments(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonParam, HttpServletRequest servletRequest,
        String startTime, String endTime) {
        List<Integer> statusList = Arrays.asList(4, 5, 7, 41, 42, 8, 9);
        Integer statusCount =
            shipmentsService.getShipmentStatus(client_id, statusList, jsonParam.getString("shipment_plan_id"));
        if (!StringTools.isNullOrEmpty(statusCount) && statusCount > 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }
        // previewFlg 0:出庫依頼 1:明細書プレビュー;明細書プレビュー时只打印pdf，不更新表
        Integer previewFlg = jsonParam.getInteger("preview_flg");
        if (previewFlg == 1) {
            Integer priceOnDeliveryNote = Integer.valueOf(jsonParam.getString("price_on_delivery_note"));
            Integer flg = 0;
            if (priceOnDeliveryNote == 1) {
                flg = 1;
            }
            return shipmentsService.getShipmentsPDF(jsonParam, servletRequest, flg, startTime, endTime);
        }
        if (previewFlg == 2) {
            return shipmentsService.getShipmentsA3PDF(jsonParam);
        }
        Integer result = 0;
        result = shipmentsService.setShipments(jsonParam, false, servletRequest);
        if (result <= 0) {
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        shipmentDetailService.setShipmentDetail(jsonParam, false, servletRequest);
        if (result <= 0) {
            return CommonUtils.success(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return CommonUtils.success("SUCCESS");
    }

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Json
     * @Date: 2020/05/12
     */
    @ApiOperation(value = "出庫依頼を削除する", notes = "出庫依頼を削除する")
    @DeleteMapping("/store/shipments/delete/{client_id}/{shipment_plan_id}")
    public JSONObject deleteShipments(@PathVariable("client_id") String client_id,
        @PathVariable("shipment_plan_id") String shipment_plan_id, HttpServletRequest servletRequest) {
        int countShipments = shipmentsService.countShipments(client_id, shipment_plan_id);
        if (countShipments <= 0) {
            throw new PlResourceNotFoundException("出荷依頼: " + shipment_plan_id);
        }

        List<Integer> statusList = Arrays.asList(4, 5, 7, 41, 42, 8);
        Integer statusCount = shipmentsService.getShipmentStatus(client_id, statusList, shipment_plan_id);
        if (!StringTools.isNullOrEmpty(statusCount) && statusCount > 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }
        // 削除
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("shipment_plan_id", shipment_plan_id);
            shipmentsService.deleteShipments(servletRequest, client_id, jsonObject, true);
        } catch (Exception e) {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return CommonUtils.success();
    }

    /**
     * @Description: 店舗側出庫情報を削除する
     * @Param: 顧客CD, 出庫依頼ID
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    @ApiOperation(value = "出庫依頼のリストを削除する", notes = "出庫依頼のリストを削除する")
    @DeleteMapping("/shipments/list/delete/{client_id}/{shipment_plan_id}")
    public JSONObject deleteShipmentsList(@PathVariable("client_id") String client_id,
        @PathVariable("shipment_plan_id") String shipment_plan_id, HttpServletRequest servletRequest) {
        return shipmentsService.deleteShipmentsList(client_id, shipment_plan_id, servletRequest);
    }

    /**
     * @Description: 店舗側出庫情報を保留する
     * @Param: 顧客CD, 出庫依頼ID，ステータス理由
     * @return: JSONObject
     * @Date: 2021/11/25
     */
    @ApiOperation(value = "出庫依頼のリストを保留する", notes = "出庫依頼のリストを保留する")
    @PutMapping("/shipments/list/keep/{client_id}")
    public JSONObject keepShipmentsList(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        return shipmentsService.keepShipmentsList(client_id, jsonObject, servletRequest);
    }

    /**
     * @Description: 依頼主マスタ
     * @Param: 顧客CD
     * @return: Json
     * @Date: 2020/5/27
     */
    @ApiOperation(value = "依頼主マスタ", notes = "依頼主マスタ")
    @GetMapping("/sponsor/{client_id}")
    public JSONObject getSponsorList(@PathVariable("client_id") String client_id, boolean sponsor_default,
        String sponsor_id) {
        List<Ms012_sponsor_master> list = sponsorSerivce.getSponsorList(client_id, sponsor_default, sponsor_id);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 顧客配送先マスタ
     * @Param: 管理ID, 顧客CD
     * @return: Mc200_customer_delivery
     * @Date: 2020/5/29
     */
    @ApiOperation(value = "顧客配送先マスタ", notes = "顧客配送先マスタ")
    @GetMapping("/customer/delivery/{client_id}")
    public JSONObject getCustomerDeliveryList(@PathVariable("client_id") String client_id, Integer delivery_id,
        String search) {
        List<Mc200_customer_delivery> list = customerDeliveryService.getCustomerDeliveryList(client_id, delivery_id,
            search);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 名称区分マスタ
     * @Param: 名称区分, 名称コード, 倉庫コード
     * @return: Ms009_definition
     * @Date: 2020/5/29
     */
    @ApiOperation(value = "名称区分マスタ", notes = "名称区分マスタ")
    @GetMapping("/definition/{warehouse_cd}")
    public JSONObject getDefinitionList(@PathVariable("warehouse_cd") String warehouse_cd, Integer[] sys_kind,
        String sys_cd) {
        if (sys_kind.length == 0) {
            throw new PlValidationErrorException("必須パラメーターが存在するので、ご確認お願いします。");
        }
        List<Ms009_definition> list = shipmentsService.getDefinitionList(warehouse_cd, sys_kind, sys_cd);

        return CommonUtils.success(list);
    }

    /**
     * @throws ExceptionHandlerAdvice
     * @param: jsonObject
     * @description:出庫依赖CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    @ApiOperation(value = "出庫依赖CSV登录", notes = "出庫依赖CSV登录")
    @PostMapping("/shipments/shipmentsCsvUpload")
    public JSONObject shipmentsCsvUpload(HttpServletRequest req, @RequestParam("file") MultipartFile file,
        String client_id, ServletRequest servletRequest) {
        shipmentsService.shipmentsCsvUpload(req, file, client_id, servletRequest);
        return CommonUtils.success("SUCCESS");
    }

    /**
     * @param: jsonObject
     * @description: 出库删除恢复
     * @return:
     * @date: 2021/09/18
     */
    @ApiOperation(value = "出库删除恢复", notes = "出库删除恢复")
    @PutMapping("/shipments/resurrection")
    public JSONObject shipmentsResurrection(@RequestBody JSONObject jsonParam, HttpServletRequest servletRequest) {
        return shipmentsService.shipmentsResurrection(jsonParam, servletRequest);
    }

    /**
     * @Param: jsonObject
     * @param: request
     * @description: 出庫PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/25
     */
    @ApiOperation(value = "出庫PDF生成", notes = "出庫PDF生成")
    @PostMapping("/store/shipments/outputShipmentsPricePDF")
    public void outputShipmentsPricePDF(@RequestBody JSONObject jsonObject, ServletRequest request,
        HttpServletResponse response, String startTime, String endTime) throws IOException {
        CommonUtils.hashAllRequired(jsonObject, "client_id");
        Integer flg = CommonUtils.toInteger(jsonObject.getString("flg"));
        JSONObject createdShipmentsPricePDF = shipmentsService.getShipmentsPDF(jsonObject, request, flg, null, null);

        // Properties properties = System.getProperties();

        // String remotePdfUrl = properties.getProperty("user.dir") + "/" + env.getProperty("path.root") +
        // createdShipmentsPricePDF.get("info").toString();

        // Properties properties2 = new Properties();
        // 读取resources里的文件 跟application.properties同级
        // InputStream fis = this.getClass().getClassLoader().getResourceAsStream("application.yml");

        // properties2.load(fis);
        // fis.close();

        String remotePdfUrl = createdShipmentsPricePDF.getJSONObject("info").getString("pdfPath");

        try {
            FileInputStream in = null;
            OutputStream out = null;
            BufferedOutputStream bos = null;
            response.setContentType("application/pdf");
            // 設置響應頭
            response.setHeader("content-disposition", "inline;filename="
                + URLEncoder.encode(createdShipmentsPricePDF.getJSONObject("info").getString("pdfPath"), "UTF-8"));
            // 讀取下載文件保存文件流
            in = new FileInputStream(remotePdfUrl);
            // 創建輸出流
            out = response.getOutputStream();
            bos = new BufferedOutputStream(out);

            // 緩存區
            byte buffer[] = new byte[1024];
            int len = 0;
            // 循環内容到緩存區
            while ((len = in.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            in.close();
            bos.close();
            // 刪除臨時文件
            File deleteFile = new File(remotePdfUrl);
            deleteFile.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }

    /**
     * @Param: file
     * @param: shipment_plan_id
     * @Param: client_id
     * @description: 梱包作業画像を添付上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/27
     */
    @ApiOperation(value = "PDF添付上传")
    @PostMapping("/shipments/approval/pdf/upload")
    public JSONObject uploadConfirmPdf(@RequestParam("file") MultipartFile[] file, String[] shipments_plan_id,
        String client_id) {
        return shipmentsService.uploadConfirmPdf(file, shipments_plan_id, client_id);
    }

    /**
     * @Param: file : 添付ファイルについて
     * @param: client_id : 店铺Id
     * @param: shipments_plan_id ： 出库依赖Id
     * @description: 添付ファイルについて
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/7
     */
    @ApiOperation(value = "添付ファイル")
    @PostMapping("shipments/approval/delivery/upload")
    public JSONObject uploadDeliveryFile(@RequestParam("file") MultipartFile[] file, String client_id,
        String shipment_plan_id) {
        return shipmentsService.uploadDeliveryFile(file, shipment_plan_id, client_id);
    }

    /**
     * @Param: jsonObject
     * @description: 添付ファイル路径写入
     * @return: Integer
     * @date: 2021/3/18
     */
    @ApiOperation(value = "添付ファイル路径写入")
    @PostMapping("shipments/approval/delivery/savePath")
    public Integer saveDeliveryFilePath(@RequestBody JSONObject jsonObject) {
        return shipmentsService.saveDeliveryFilePath(jsonObject);
    }

    /**
     * @param:
     * @description: 根据size_cd获取sizeName
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/23
     */
    @ApiOperation(value = "根据size_cd获取sizeName", notes = "根据size_cd获取sizeName")
    @GetMapping("/shipments/getSizeName")
    public JSONObject getSizeName(String size_cd) {
        String sizeName = shipmentsDao.getSizeName(size_cd);
        return CommonUtils.success(sizeName);
    }

    /**
     * @param jsonObject : 被拆分的商品Id 出库依赖Id
     * @param request : 响应
     * @description: 拆分出库依赖ID
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/15 11:17
     */
    @ApiOperation(value = "拆分出库依赖ID", notes = "拆分出库依赖ID")
    @PostMapping("/store/split/shipment/{client_id}/{shipment_plan_id}")
    public JSONObject splitShipment(@PathVariable("client_id") String client_id,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        @RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return shipmentsService.splitShipment(client_id, shipment_plan_id, jsonObject, request);
    }

    /**
     * @param:
     * @description: 获取可以合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/22
     */
    @ApiOperation(value = "获取可以合并出库依赖", notes = "获取可以合并出库依赖")
    @GetMapping("/store/shipments/merge/list/{client_id}")
    public JSONObject getMergeShipmentList(@PathVariable("client_id") String client_id, String search) {
        JSONArray mergeShipment = shipmentsService.getMergeShipmentList(client_id, search);

        return CommonUtils.success(mergeShipment);
    }

    /**
     * @param:
     * @description: 合并出库依赖
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/23
     */
    @ApiOperation(value = "合并出库依赖", notes = "合并出库依赖")
    @PostMapping("/store/shipments/merge/{client_id}/{shipment_plan_id}")
    public JSONObject mergeShipment(@PathVariable("client_id") String client_id,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        @RequestBody JSONObject jsonObject) {
        try {
            Boolean bool = shipmentsService.mergeShipment(client_id, shipment_plan_id, jsonObject);
            if (bool) {
                return CommonUtils.success();
            }
        } catch (Exception e) {
            return CommonUtils.failure(ErrorCode.E_40131);
        }
        return CommonUtils.failure(ErrorCode.E_40131);
    }

    /**
     * @param jsonObject : product_id = 商品Id shipment_plan_id = 出库依赖Id client_id = 店铺Id
     * @param request : 请求
     * @description: 系统中之前不存在的商品登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/9 18:01
     */
    @ApiOperation(value = "系统中之前不存在的商品登录")
    @PostMapping("/store/shipment/detail/product/insert")
    public JSONObject shipmentInsertProduct(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "product_id,shipment_plan_id");
        int status = 0;
        return shipmentsService.shipmentInsertProduct(jsonObject, request, status);
    }
}
