package com.lemonico.wms.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.bean.Ms205_customer_history;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.service.CommonService;
import com.lemonico.common.service.CustomerHistoryService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlValidationErrorException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.SponsorDao;
import com.lemonico.wms.bean.ShimentListBean;
import com.lemonico.wms.service.ShipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: sunlogi
 * @description: 出庫
 * @create: 2020-07-06 13:35
 **/
@RestController
@Api(tags = "仓库侧出库处理")
public class ShipmentController
{

    private final static Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private CustomerHistoryService customerHistoryService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SponsorDao sponsorDao;

    /**
     * @Description: 出库依赖一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/06/30
     */
    @ApiOperation(value = "出庫依頼一览", notes = "出庫依頼一览")
    @GetMapping("/wms/shipments/list/{warehouse_cd}")
    public JSONObject getShipmentsLists(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String shipment_status, String keyword, String delivery_carrier, String cash_on_delivery,
        String shipping_start_date, String shipping_end_date, String shipment_plan_id,
        String order_datetime_start, String order_datetime_end, String request_date_start,
        String request_date_end, String sponsor_id, String shipment_plan_date_start, String shipment_plan_date_end,
        Long min_total_price, Long max_total_price,
        String delivery_date_start, String delivery_date_end, Long min_product_plan_total,
        Long max_product_plan_total, String order_first_name, String delivery_time_slot, String identifier,
        String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file,
        String instructions_special_notes, Integer[] form, String status_message, String orderType,
        Integer currentPage, Integer pageSize, String min_delivery_date, String max_delivery_date,
        String upd_date_start, String upd_date_end,
        @RequestParam(value = "work_id", required = false) List<Integer> work_id,
        Integer payment_method, String serial_no) {
        List<String> shipmentIdList = null;
        if (!StringTools.isNullOrEmpty(work_id) && work_id.size() > 0) {
            shipmentIdList = shipmentService.getShipmentIdListByWorkId(work_id);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("client_id", client_id);
        jsonObject.put("shipment_status", shipment_status);
        jsonObject.put("shipmentIdList", shipmentIdList);
        jsonObject.put("payment_method", payment_method);
        jsonObject.put("keyword", keyword);
        jsonObject.put("form", form);
        jsonObject.put("delivery_carrier", delivery_carrier);
        jsonObject.put("cash_on_delivery", cash_on_delivery);
        jsonObject.put("shipping_start_date", shipping_start_date);
        jsonObject.put("shipping_end_date", shipping_end_date);
        jsonObject.put("order_datetime_start", order_datetime_start);
        jsonObject.put("order_datetime_end", order_datetime_end);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("pageSize", pageSize);
        jsonObject.put("request_date_start", request_date_start);
        jsonObject.put("request_date_end", request_date_end);
        if (!StringTools.isNullOrEmpty(sponsor_id)) {
            String[] sponsor_id_list = sponsor_id.split(",");
            jsonObject.put("sponsor_id", sponsor_id_list);
        } else {
            jsonObject.put("sponsor_id", sponsor_id);
        }
        jsonObject.put("shipment_plan_date_start", shipment_plan_date_start);
        jsonObject.put("shipment_plan_date_end", shipment_plan_date_end);
        jsonObject.put("min_total_price", min_total_price);
        jsonObject.put("max_total_price", max_total_price);
        jsonObject.put("delivery_date_start", delivery_date_start);
        jsonObject.put("delivery_date_end", delivery_date_end);
        jsonObject.put("upd_date_start", upd_date_start);
        jsonObject.put("upd_date_end", upd_date_end);
        jsonObject.put("min_product_plan_total", min_product_plan_total);
        jsonObject.put("max_product_plan_total", max_product_plan_total);
        // 注文者名去空
        if (!StringTools.isNullOrEmpty(order_first_name)) {
            order_first_name = order_first_name.replaceAll(" ", "");
        }
        jsonObject.put("order_first_name", order_first_name);
        jsonObject.put("delivery_time_slot", delivery_time_slot);
        jsonObject.put("identifier", identifier);
        // 电话去除‘-’
        if (!StringTools.isNullOrEmpty(order_phone_number)) {
            order_phone_number = order_phone_number.replaceAll("-", "");
        }
        jsonObject.put("order_phone_number", order_phone_number);
        jsonObject.put("price_on_delivery_note", price_on_delivery_note);
        jsonObject.put("buy_id", buy_id);
        jsonObject.put("email", email);
        jsonObject.put("bundled_flg", bundled_flg);
        jsonObject.put("buy_cnt", buy_cnt);
        jsonObject.put("company", company);
        jsonObject.put("file", file);
        jsonObject.put("instructions_special_notes", instructions_special_notes);
        jsonObject.put("form", form);
        jsonObject.put("status_message", status_message);
        jsonObject.put("orderType", orderType);
        jsonObject.put("min_delivery_date", min_delivery_date);
        jsonObject.put("max_delivery_date", max_delivery_date);
        // シリアル番号
        jsonObject.put("serial_no", serial_no);
        try {
            JSONObject resultJson = shipmentService.getShipmentsLists(jsonObject);
            return CommonUtils.success(resultJson);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 出庫依頼详细
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2022/01/12
     */
    @ApiOperation(value = "出庫依頼详细", notes = "出庫依頼详细")
    @GetMapping("/wms/shipments/detail/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject getShipmentsDetail(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {
        try {
            Tw200_shipment list = shipmentService.getShipmentsDetail(warehouse_cd, shipment_plan_id);
            return CommonUtils.success(list);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 出庫依頼按照商品code分组
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2021/11/2
     */
    @ApiOperation(value = "出庫依頼一览", notes = "出庫依頼一览")
    @GetMapping("/wms/shipments/groups/{warehouse_cd}")
    public JSONObject getShipmentsGroupList(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String shipment_status, String keyword, String delivery_carrier, String cash_on_delivery,
        String shipping_start_date, String shipping_end_date, String shipment_plan_id,
        String order_datetime_start, String order_datetime_end, String request_date_start,
        String request_date_end, String sponsor_id, String shipment_plan_date_start, String shipment_plan_date_end,
        Long min_total_price, Long max_total_price,
        String delivery_date_start, String delivery_date_end, Long min_product_plan_total,
        Long max_product_plan_total, String order_first_name, String delivery_time_slot, String identifier,
        String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file,
        String instructions_special_notes, Integer[] form, String status_message, String orderType,
        String min_delivery_date, String max_delivery_date,
        @RequestParam(value = "work_id", required = false) List<Integer> work_id, Integer payment_method) {
        List<String> shipmentIdList = null;
        if (!StringTools.isNullOrEmpty(work_id) && work_id.size() > 0) {
            shipmentIdList = shipmentService.getShipmentIdListByWorkId(work_id);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("client_id", client_id);
        jsonObject.put("shipment_status", shipment_status);
        jsonObject.put("shipmentIdList", shipmentIdList);
        jsonObject.put("payment_method", payment_method);
        jsonObject.put("keyword", keyword);
        jsonObject.put("delivery_carrier", delivery_carrier);
        jsonObject.put("cash_on_delivery", cash_on_delivery);
        jsonObject.put("shipping_start_date", shipping_start_date);
        jsonObject.put("shipping_end_date", shipping_end_date);
        jsonObject.put("order_datetime_start", order_datetime_start);
        jsonObject.put("order_datetime_end", order_datetime_end);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("min_delivery_date", min_delivery_date);

        jsonObject.put("request_date_start", request_date_start);
        jsonObject.put("request_date_end", request_date_end);
        if (!StringTools.isNullOrEmpty(sponsor_id)) {
            String[] sponsor_id_list = sponsor_id.split(",");
            jsonObject.put("sponsor_id", sponsor_id_list);
        } else {
            jsonObject.put("sponsor_id", sponsor_id);
        }
        jsonObject.put("shipment_plan_date_start", shipment_plan_date_start);
        jsonObject.put("shipment_plan_date_end", shipment_plan_date_end);
        jsonObject.put("min_total_price", min_total_price);
        jsonObject.put("max_total_price", max_total_price);
        jsonObject.put("delivery_date_start", delivery_date_start);
        jsonObject.put("delivery_date_end", delivery_date_end);
        jsonObject.put("min_product_plan_total", min_product_plan_total);
        jsonObject.put("max_product_plan_total", max_product_plan_total);
        // 注文者名去空
        if (!StringTools.isNullOrEmpty(order_first_name)) {
            order_first_name = order_first_name.replaceAll(" ", "");
        }
        jsonObject.put("order_first_name", order_first_name);
        jsonObject.put("delivery_time_slot", delivery_time_slot);
        jsonObject.put("identifier", identifier);
        // 电话去除‘-’
        if (!StringTools.isNullOrEmpty(order_phone_number)) {
            order_phone_number = order_phone_number.replaceAll("-", "");
        }
        jsonObject.put("order_phone_number", order_phone_number);
        jsonObject.put("price_on_delivery_note", price_on_delivery_note);
        jsonObject.put("buy_id", buy_id);
        jsonObject.put("email", email);
        jsonObject.put("bundled_flg", bundled_flg);
        jsonObject.put("buy_cnt", buy_cnt);
        jsonObject.put("company", company);
        jsonObject.put("file", file);
        jsonObject.put("instructions_special_notes", instructions_special_notes);
        jsonObject.put("form", form);
        jsonObject.put("status_message", status_message);
        jsonObject.put("orderType", orderType);
        jsonObject.put("min_delivery_date", min_delivery_date);
        jsonObject.put("max_delivery_date", max_delivery_date);
        try {
            JSONObject list = shipmentService.getShipmentsGroupList(jsonObject);
            return list;
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 出庫依頼CSV一览
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/06
     */
    @ApiOperation(value = "出庫依頼CSV一览", notes = "出庫依頼CSV一览")
    @GetMapping("/wms/shipments/csv/{warehouse_cd}")
    public JSONObject getShipmentsCsvList(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String shipment_status, String keyword, String delivery_carrier, String cash_on_delivery,
        String shipping_start_date, String shipping_end_date, String shipment_plan_id,
        String order_datetime_start, String order_datetime_end, String request_date_start,
        String request_date_end, String sponsor_id, String shipment_plan_date_start, String shipment_plan_date_end,
        Long min_total_price, Long max_total_price,
        String delivery_date_start, String delivery_date_end, Long min_product_plan_total,
        Long max_product_plan_total, String order_first_name, String delivery_time_slot, String identifier,
        String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file,
        String instructions_special_notes, Integer[] form, String status_message, String orderType,
        String min_delivery_date, String max_delivery_date, String upd_date_start, String upd_date_end,
        @RequestParam(value = "work_id", required = false) List<Integer> work_id,
        Integer payment_method, Integer csv_flg) {

        List<String> shipmentIdList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(work_id) && work_id.size() > 0) {
            shipmentIdList = shipmentService.getShipmentIdListByWorkId(work_id);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("client_id", client_id);
        jsonObject.put("shipment_status", shipment_status);
        jsonObject.put("shipmentIdList", shipmentIdList);
        jsonObject.put("payment_method", payment_method);
        jsonObject.put("keyword", keyword);
        jsonObject.put("delivery_carrier", delivery_carrier);
        jsonObject.put("cash_on_delivery", cash_on_delivery);
        jsonObject.put("shipping_start_date", shipping_start_date);
        jsonObject.put("shipping_end_date", shipping_end_date);
        jsonObject.put("order_datetime_start", order_datetime_start);
        jsonObject.put("order_datetime_end", order_datetime_end);
        jsonObject.put("upd_date_start", upd_date_start);
        jsonObject.put("upd_date_end", upd_date_end);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("min_delivery_date", min_delivery_date);

        jsonObject.put("request_date_start", request_date_start);
        jsonObject.put("request_date_end", request_date_end);
        if (!StringTools.isNullOrEmpty(sponsor_id)) {
            String[] sponsor_id_list = sponsor_id.split(",");
            jsonObject.put("sponsor_id", sponsor_id_list);
        } else {
            jsonObject.put("sponsor_id", sponsor_id);
        }
        jsonObject.put("shipment_plan_date_start", shipment_plan_date_start);
        jsonObject.put("shipment_plan_date_end", shipment_plan_date_end);
        jsonObject.put("min_total_price", min_total_price);
        jsonObject.put("max_total_price", max_total_price);
        jsonObject.put("delivery_date_start", delivery_date_start);
        jsonObject.put("delivery_date_end", delivery_date_end);
        jsonObject.put("min_product_plan_total", min_product_plan_total);
        jsonObject.put("max_product_plan_total", max_product_plan_total);
        // 注文者名去空
        if (!StringTools.isNullOrEmpty(order_first_name)) {
            order_first_name = order_first_name.replaceAll(" ", "");
        }
        jsonObject.put("order_first_name", order_first_name);
        jsonObject.put("delivery_time_slot", delivery_time_slot);
        jsonObject.put("identifier", identifier);
        // 电话去除‘-’
        if (!StringTools.isNullOrEmpty(order_phone_number)) {
            order_phone_number = order_phone_number.replaceAll("-", "");
        }
        jsonObject.put("order_phone_number", order_phone_number);
        jsonObject.put("price_on_delivery_note", price_on_delivery_note);
        jsonObject.put("buy_id", buy_id);
        jsonObject.put("email", email);
        jsonObject.put("bundled_flg", bundled_flg);
        jsonObject.put("buy_cnt", buy_cnt);
        jsonObject.put("company", company);
        jsonObject.put("file", file);
        jsonObject.put("instructions_special_notes", instructions_special_notes);
        jsonObject.put("form", form);
        jsonObject.put("status_message", status_message);
        jsonObject.put("orderType", orderType);
        jsonObject.put("min_delivery_date", min_delivery_date);
        jsonObject.put("max_delivery_date", max_delivery_date);
        jsonObject.put("csv_flg", csv_flg);
        try {
            List<ShimentListBean> list = shipmentService.getShipmentsCsvList(jsonObject);
            return CommonUtils.success(list);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @ApiModelProperty(value = "")
    @GetMapping("/wms/shipments/company/csv/{companys}")
    public JSONObject getCompanyCsvList(@PathVariable("companys") String companys, String client_id,
        String shipment_status, String keyword, String delivery_carrier, String cash_on_delivery,
        String shipping_start_date, String shipping_end_date, String shipment_plan_id,
        String order_datetime_start, String order_datetime_end, String request_date_start,
        String request_date_end, String sponsor_id, String shipment_plan_date_start, String shipment_plan_date_end,
        Long min_total_price, Long max_total_price,
        String delivery_date_start, String delivery_date_end, Long min_product_plan_total,
        Long max_product_plan_total, String order_first_name, String delivery_time_slot, String identifier,
        String order_phone_number,
        Integer[] price_on_delivery_note, String buy_id, String email, Integer[] bundled_flg, Long buy_cnt,
        String company, String[] file,
        String instructions_special_notes, Integer[] form, String status_message, String orderType,
        String min_delivery_date, String max_delivery_date,
        @RequestParam(value = "work_id", required = false) List<Integer> work_id, Integer payment_method) {

        List<String> shipmentIdList = null;
        if (!StringTools.isNullOrEmpty(work_id) && work_id.size() > 0) {
            shipmentIdList = shipmentService.getShipmentIdListByWorkId(work_id);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        jsonObject.put("shipment_status", shipment_status);
        jsonObject.put("shipmentIdList", shipmentIdList);
        jsonObject.put("payment_method", payment_method);
        jsonObject.put("keyword", keyword);
        jsonObject.put("delivery_carrier", delivery_carrier);
        jsonObject.put("cash_on_delivery", cash_on_delivery);
        jsonObject.put("shipping_start_date", shipping_start_date);
        jsonObject.put("shipping_end_date", shipping_end_date);
        jsonObject.put("order_datetime_start", order_datetime_start);
        jsonObject.put("order_datetime_end", order_datetime_end);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("min_delivery_date", min_delivery_date);

        jsonObject.put("request_date_start", request_date_start);
        jsonObject.put("request_date_end", request_date_end);
        if (!StringTools.isNullOrEmpty(sponsor_id)) {
            String[] sponsor_id_list = sponsor_id.split(",");
            jsonObject.put("sponsor_id", sponsor_id_list);
        } else {
            jsonObject.put("sponsor_id", sponsor_id);
        }
        jsonObject.put("shipment_plan_date_start", shipment_plan_date_start);
        jsonObject.put("shipment_plan_date_end", shipment_plan_date_end);
        jsonObject.put("min_total_price", min_total_price);
        jsonObject.put("max_total_price", max_total_price);
        jsonObject.put("delivery_date_start", delivery_date_start);
        jsonObject.put("delivery_date_end", delivery_date_end);
        jsonObject.put("min_product_plan_total", min_product_plan_total);
        jsonObject.put("max_product_plan_total", max_product_plan_total);
        // 注文者名去空
        if (!StringTools.isNullOrEmpty(order_first_name)) {
            order_first_name = order_first_name.replaceAll(" ", "");
        }
        jsonObject.put("order_first_name", order_first_name);
        jsonObject.put("delivery_time_slot", delivery_time_slot);
        jsonObject.put("identifier", identifier);
        // 电话去除‘-’
        if (!StringTools.isNullOrEmpty(order_phone_number)) {
            order_phone_number = order_phone_number.replaceAll("-", "");
        }
        jsonObject.put("order_phone_number", order_phone_number);
        jsonObject.put("price_on_delivery_note", price_on_delivery_note);
        jsonObject.put("buy_id", buy_id);
        jsonObject.put("email", email);
        jsonObject.put("bundled_flg", bundled_flg);
        jsonObject.put("buy_cnt", buy_cnt);
        jsonObject.put("company", company);
        jsonObject.put("file", file);
        jsonObject.put("instructions_special_notes", instructions_special_notes);
        jsonObject.put("form", form);
        jsonObject.put("status_message", status_message);
        jsonObject.put("orderType", orderType);
        jsonObject.put("min_delivery_date", min_delivery_date);
        jsonObject.put("max_delivery_date", max_delivery_date);
        jsonObject.put("companys", companys);

        try {
            List<ShimentListBean> list = shipmentService.getShipmentsCsvList(jsonObject);
            return CommonUtils.success(list);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 查询依頼主
     * @Param: 店铺id
     * @return: json
     * @Date: 2021/08/10
     */
    @ApiOperation(value = "查询依頼主", notes = "查询依頼主")
    @GetMapping("/wms/shipment/getSponsorList/{client_id}/{warehouse_cd}")
    public JSONObject getSponsorList(@PathVariable("client_id") String client_id,
        @PathVariable("warehouse_cd") String warehouse_cd, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        ArrayList<Ms012_sponsor_master> sponsorListArray = new ArrayList<>();
        if ("all".equals(client_id)) {
            List<Ms201_client> list = commonService.getClientsByWarehouseCd(warehouse_cd, request);
            for (int i = 0; i < list.size(); i++) {
                List<Ms012_sponsor_master> sponsorList =
                    sponsorDao.getSponsorList(list.get(i).getClient_id(), false, "");
                for (int j = 0; j < sponsorList.size(); j++) {
                    sponsorListArray.add(sponsorList.get(j));
                }
            }
        } else {
            List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, false, "");
            for (int j = 0; j < sponsorList.size(); j++) {
                sponsorListArray.add(sponsorList.get(j));
            }
        }
        // 依頼主去重，拼接id
        for (int i = 0; i < sponsorListArray.size(); i++) {
            for (int j = i + 1; j < sponsorListArray.size(); j++) {
                if (sponsorListArray.get(i).getName().equals(sponsorListArray.get(j).getName())) {
                    sponsorListArray.get(i).setSponsor_id(
                        sponsorListArray.get(i).getSponsor_id() + ',' + sponsorListArray.get(j).getSponsor_id());
                    sponsorListArray.remove(j);
                }
            }
        }
        result.put("info", sponsorListArray);
        return result;
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    @ApiOperation(value = "出庫検品", notes = "出庫検品")
    @GetMapping("/wms/shipments/incidents/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject getShipmentsIncidents(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {

        try {
            List<Tw200_shipment> list = shipmentService.getShipmentsIncidents(warehouse_cd, shipment_plan_id);
            return CommonUtils.success(list);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 将该出库依赖下的所有商品的シリアル番号清空
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 12:52
     */
    @ApiOperation(value = "出庫検品更新シリアル番号", notes = "出庫検品更新シリアル番号")
    @PutMapping("/wms/shipments/empty/serial_no/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject emptySerialNo(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {
        return shipmentService.emptySerialNo(warehouse_cd, shipment_plan_id);
    }

    /**
     * 出庫検品更新シリアル番号
     *
     * @param warehouse_cd
     * @param shipment_plan_id
     * @param serial_no
     * @return
     * @Date: 2021/10/22
     */
    @ApiOperation(value = "出庫検品更新シリアル番号", notes = "出庫検品更新シリアル番号")
    @PutMapping("/wms/shipments/serial_no/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject updateSerialNo(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        String product_id, String serial_no, String set_sub_id) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("product_id", product_id);
        jsonObject.put("serial_no", !StringTools.isNullOrEmpty(serial_no) ? serial_no.trim() : "");
        jsonObject.put("set_sub_id", set_sub_id);

        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,shipment_plan_id,product_id,serial_no");
        return shipmentService.updateSerialNo(jsonObject);
    }

    /**
     * @description ntm检品修改配送公司
     * @param warehouse_cd 仓库CD
     * @param shipment_plan_id 出库ID
     * @return JSONObject
     * @date 2021/7/19
     **/
    @GetMapping("/wms/shipments/incidents/delivery/method/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject changeShipmentDeliveryMethod(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {
        try {
            shipmentService.changeNtmDeliveryMethod(warehouse_cd, shipment_plan_id);
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param warehouse_cd ： 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 出庫撮影
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 11:11
     */
    @ApiOperation(value = "出庫撮影", notes = "出庫撮影")
    @GetMapping("/wms/shipments/photography/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject getPhotography(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {
        return shipmentService.getPhotography(warehouse_cd, shipment_plan_id);
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/11/10
     */
    @ApiOperation(value = "出庫検品", notes = "出庫検品")
    @PutMapping("/wms/shipments/incidents/{warehouse_cd}/{shipment_plan_id}/{shipment_status}")
    public JSONObject updateShipmentsIncidents(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        @PathVariable("shipment_status") Integer shipment_status, HttpServletRequest servletRequest,
        String sizeName, Integer boxes) {

        try {
            return shipmentService.updateShipmentsIncidents(warehouse_cd, shipment_plan_id, shipment_status, sizeName,
                boxes, servletRequest, null);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/07/15
     */
    @ApiOperation(value = "出庫ステータス件数", notes = "出庫ステータス件数")
    @GetMapping("/wms/shipments/count")
    public JSONObject getShipmentStatusCount(String warehouse_cd,
        String client_id,
        String startTime,
        String endTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("client_id", client_id);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("updStartDate", startTime);
        jsonObject.put("updEndDate", endTime);
        Integer[] statusCount = shipmentService.getShipmentStatusCount(jsonObject);
        return CommonUtils.success(statusCount);
    }

    /**
     * @Description: 出庫ステータス件数
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/6/21
     */
    @ApiOperation(value = "出庫ステータス件数", notes = "出庫ステータス件数")
    @GetMapping("/wms/shipments/counts")
    public JSONObject getAllShipmentStatusCount(String warehouse_cd, String client_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("client_id", client_id);
        JSONArray statusCount = shipmentService.getAllShipmentStatusCount(jsonObject);
        return CommonUtils.success(statusCount);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @description: 获取到受注キャンセルあり各个出库状态下的件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/19 9:45
     */
    @ApiOperation(value = "出庫ステータス件数", notes = "出庫ステータス件数")
    @RequestMapping(value = "/wms/shipment/cancel", method = RequestMethod.GET)
    public JSONObject getShipmentCancelCount(String warehouse_cd, String client_id) {
        return shipmentService.getShipmentCancelCount(warehouse_cd, client_id);
    }

    /**
     * @Description: 出庫編集
     * @Param: json
     * @return: json
     * @Date: 2020/07/16
     */
    @ApiOperation(value = "出庫編集", notes = "出庫編集")
    @PutMapping("/wms/shipments/edit")
    public JSONObject updateShipments(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        Integer result = shipmentService.updateShipments(servletRequest, jsonObject);

        if (result > 0) {
            return CommonUtils.success("SUCCESS");
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 出庫個口数編集
     * @Param: json
     * @return: json
     * @Author: zhangmj
     * @Date: 2020/11/26
     */
    @ApiOperation(value = "出庫個口数編集", notes = "出庫個口数編集")
    @PutMapping("/wms/shipments/boxs")
    public JSONObject boxEdit(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        Integer result = shipmentService.updateBoxes(servletRequest, jsonObject);
        if (result > 0) {
            return CommonUtils.success("SUCCESS");
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: ステータス変更
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    @ApiOperation(value = "ステータス変更", notes = "ステータス変更")
    @PutMapping("/wms/shipments/status/{warehouse_cd}/{shipment_status}")
    public JSONObject setShipmentStatus(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_status") Integer shipment_status, String[] shipment_plan_id, String status_message,
        HttpServletRequest servletRequest, String sizeName, Integer boxes) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_status", shipment_status);
        CommonUtils.hashAllRequired(jsonObject, "shipment_status,warehouse_cd");

        String operation_cd = shipment_status.toString();
        if (shipment_status == 0) {
            operation_cd = "1"; // 出荷確認完了
        } else if (shipment_status == 1) {
            operation_cd = "19"; // 出荷確認待ち
        } else if (shipment_status == 3) {
            operation_cd = "63"; // 出荷保留解除
        }

        Integer result = shipmentService.setShipmentStatus(servletRequest, warehouse_cd, shipment_status,
            shipment_plan_id, sizeName, status_message, boxes, true, operation_cd, null);

        if (result <= 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        } else {
            return CommonUtils.success(result);
        }
    }

    /**
     * @Description: 复数ステータス変更
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    @ApiOperation(value = "ステータス変更", notes = "ステータス変更")
    @PutMapping("/wms/shipments/reserve/status/{warehouse_cd}/{shipment_status}")
    public JSONObject setReserveShipmentStatus(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_status") Integer shipment_status, String[] shipment_plan_id, String status_message,
        HttpServletRequest servletRequest, String sizeName, Integer boxes) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_status", shipment_status);
        CommonUtils.hashAllRequired(jsonObject, "shipment_status,warehouse_cd");
        Integer result = 0;
        // 多个执行，循环
        for (int i = 0; i < shipment_plan_id.length; i++) {
            result += shipmentService.setShipmentStatus(servletRequest, warehouse_cd, 0,
                shipment_plan_id, null, null, null, true, "1", null);
        }
        if (result <= 0) {
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        } else {
            return CommonUtils.success(result);
        }
    }

    /**
     * @Description: 出荷作業を開始
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/07/07
     */
    @ApiOperation(value = "出荷作業を開始", notes = "出荷作業を開始")
    @PostMapping("/wms/shipments/work/start/{warehouse_cd}")
    public JSONObject insertShipmentResult(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        CommonUtils.hashAllRequired(jsonObject, "shipment");
        return shipmentService.insertShipmentResult(servletRequest, jsonObject, warehouse_cd);
    }

    /**
     * @Description: 自动出荷作業を開始
     * @Param: 顧客CD, 出庫依頼
     * @return: JSONObject
     * @Date: 2021/11/03
     */
    @ApiOperation(value = "出荷作業を開始", notes = "出荷作業を開始")
    @PostMapping("/wms/shipments/automatic/work/start/{warehouse_cd}")
    public JSONObject automaticInsertShipmentResult(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONArray jsonArray, HttpServletRequest servletRequest) {
        for (int i = 0; i < jsonArray.size(); i++) {
            CommonUtils.hashAllRequired(jsonArray.getJSONObject(i), "shipment");
        }
        return shipmentService.automaticInsertShipmentResult(servletRequest, jsonArray, warehouse_cd);
    }

    /**
     * @Description: 出荷作業中取消
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/12/22
     */
    @ApiOperation(value = "出荷作業中取消", notes = "出荷作業中取消")
    @PostMapping("/wms/shipments/work/cancel/{warehouse_cd}")
    public JSONObject shipmentWorkCancel(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        return shipmentService.shipmentWorkCancel(servletRequest, jsonObject, warehouse_cd);
    }

    /**
     * @Description: 作業管理
     * @Param: null
     * @return: List
     * @Date: 2020/7/14
     */
    @ApiOperation(value = "作業管理", notes = "作業管理")
    @GetMapping("/wms/shipments/work/group/{warehouse_cd}")
    public JSONObject getWorkNameList(@PathVariable("warehouse_cd") String warehouse_cd, String client_id) {
        return CommonUtils.success(shipmentService.getWorkNameList(warehouse_cd, client_id));
    }

    /**
     * @Description: 顧客別作業履歴
     * @Param:
     * @return:
     * @Date: 2020/7/13
     */
    @ApiOperation(value = "顧客別作業履歴", notes = "顧客別作業履歴")
    @GetMapping("/wms/shipments/customer/history/{plan_id}")
    public JSONObject getCustomerHistory(@PathVariable("plan_id") String plan_id) {
        List<Ms205_customer_history> list = customerHistoryService.getCustomerHistory(plan_id);
        return CommonUtils.success(list);
    }

    /**
     * @Param: jsonObject
     * @description: トータルピッキングリストPDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "トータルピッキングリストPDF", notes = "トータルピッキングリストPDF")
    @PostMapping("/wms/shipments/pdf/order_list")
    public JSONObject createShipmentOrderListPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,client_id");
        return shipmentService.createShipmentOrderListPDF(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "同梱明細書PDF仓库侧working页面", notes = "同梱明細書PDF仓库侧working页面")
    @PostMapping("/wms/shipments/pdf/bundled/detail/work")
    public JSONObject createProductDetailPDFworking(@RequestBody JSONObject jsonObject) {
        // CommonUtil.hashAllRequired(jsonObject, "warehouse_cd,client_id");
        return shipmentService.createProductDetailPDFworking(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 同梱明細書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "同梱明細書PDF", notes = "同梱明細書PDF")
    @PostMapping("/wms/shipments/pdf/bundled/detail")
    public JSONObject createProductDetailPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,client_id");
        return shipmentService.createProductDetailPDF(jsonObject);
    }

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF仓库侧working页面
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "作業指示書PDF仓库侧working页面", notes = "作業指示書PDF仓库侧working页面")
    @PostMapping("/wms/shipments/pdf/instructions/work")
    public JSONObject createInstructionsPDFworking(@RequestBody JSONObject jsonObject) {
        // CommonUtil.hashAllRequired(jsonObject, "items,warehouse_cd,client_id");
        return shipmentService.createInstructionsPDFworking(jsonObject);
    }

    /**
     * @Param * @param: jsonObject
     * @description: 作業指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "作業指示書PDF", notes = "作業指示書PDF")
    @PostMapping("/wms/shipments/pdf/instructions")
    public JSONObject createInstructionsPDF(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "items,warehouse_cd,client_id");
        return shipmentService.createInstructionsPDF(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 明細書・指示書PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/20
     */
    @ApiOperation(value = "明細書・指示書PDF", notes = "明細書・指示書PDF")
    @PostMapping("/wms/shipments/pdf/instructions/detail")
    public JSONObject createProductDetailInstructionsPDF(@RequestBody JSONObject jsonObject) {
        return shipmentService.createProductDetailInstructionsPDF(jsonObject);
    }

    /**
     * @throws ExceptionHandlerAdvice
     * @param: jsonObject
     * @description:仓库侧快递公司CSV登录
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/24
     */
    @ApiOperation(value = "仓库侧快递公司CSV登录", notes = "仓库侧快递公司CSV登录")
    @PostMapping("/wms/shipments/csv/upload")
    public JSONObject shipmentsCompanyCsvUpload(HttpServletRequest req, @RequestParam("file") MultipartFile file,
        Integer flag, String warehouse_cd) {
        return shipmentService.shipmentsCompanyCsvUpload(req, file, flag, warehouse_cd);
    }

    /**
     * @Param: workName : 作業管理 ,shipment_status ：処理ステータス
     * @description: 根据作业name获取作业者Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    @ApiOperation(value = "根据作业name获取作业者Id")
    @GetMapping("/wms/shipment/work_name/{warehouse_cd}")
    public JSONObject getWorkIdByName(@PathVariable("warehouse_cd") String warehouse_cd, String client_id,
        String work_name, String shipment_status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("work_name", work_name);
        jsonObject.put("shipment_status", shipment_status);
        CommonUtils.hashAllRequired(jsonObject, "work_name,shipment_status");
        return shipmentService.getWorkIdByName(warehouse_cd, client_id, work_name, shipment_status);
    }

    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @param: servletRequest
     * @description: 出荷作業を完了
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/7/24
     */
    @ApiOperation(value = "出荷作業を完了")
    @PostMapping("/wms/shipments/finish")
    public JSONObject finishShipment(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,shipmentPlanId");
        // // 获取Session
        // HttpSession session = servletRequest.getSession();
        // // 页面传过来的依赖ID
        // String shipmentPlanId = jsonObject.getString("shipmentPlanId");
        // List<String> shipmentList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(shipmentPlanId);
        // // 获取已经缓存的Session
        // Object sessionObject = session.getAttribute("sessionPlanId");
        // System.err.println("SSSSSSSSSS____:"+sessionObject);
        // if (StringTools.isNullOrEmpty(sessionObject)) {
        // // Session 不存在的情况，直接写入Session
        // session.setAttribute("sessionPlanId", shipmentPlanId);
        // } else {
        // // Session 存在的情况，进行比较
        // String sessionPlanId = sessionObject.toString();
        // System.err.println("Session：" + sessionPlanId);
        // List<String> sessionList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(sessionPlanId);
        //
        // boolean repeatFlg = false;
        // for (String planId : sessionList) {
        // // 验证是否有重复的值
        // if (shipmentList.contains(planId)) {
        // repeatFlg = true;
        // System.err.println("重复ID："+ planId);
        // break;
        // }
        // }
        //
        // // 没有重复的情况下拼接到一起写入Session
        // if (!repeatFlg) {
        // sessionPlanId += "," + shipmentPlanId;
        // session.setAttribute("sessionPlanId", sessionPlanId);
        // } else {
        // System.err.println("异常信息");
        // throw new JsonException(ErrorEnum.E_10002);
        // }
        // }

        JSONObject finishJson = new JSONObject();
        try {
            finishJson = shipmentService.finishShipment(jsonObject, servletRequest);
        } catch (Exception e) {
            logger.error("出库完了失败");
            logger.error(BaseException.print(e));
        } finally {
            // String sessionPlanId = session.getAttribute("sessionPlanId").toString();
            // // 删除已经执行完了的出库依赖ID
            // for (String planId : shipmentList) {
            // // 验证是否有重复的值
            // if (sessionPlanId.contains(planId)) {
            // sessionPlanId = sessionPlanId.replaceAll(planId + ",?", "");
            // }
            // }
            //
            // // 重新设置Session
            // session.setAttribute("sessionPlanId", sessionPlanId.replaceAll(",?$", ""));
            return finishJson;
        }
    }

    /**
     * @param jsonObject : warehouse_cd : 仓库Id shipment_plan_id : 多个出库依赖Id以，拼接
     * @description: 判断出荷作業を完了所选的的出库依赖中是否含有过期或者不可出库的商品
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/3 13:08
     */
    @ApiOperation(value = "判断出荷作業を完了所选的的出库依赖中是否含有过期或者不可出库的商品")
    @PostMapping("/wms/shipments/finish/judgment/cant")
    public JSONObject judgmentShipment(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,shipmentPlanId");
        return shipmentService.judgmentShipment(jsonObject);
    }


    /**
     * @Param: jsonObject : warehouse_cd\shipment_plan_id
     * @description: 出荷完了を取り消す
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/3
     */
    @ApiOperation(value = "出荷完了を取り消す")
    @PostMapping("/wms/shipments/cancel")
    public JSONObject cancelShipment(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,shipment_plan_id");
        return shipmentService.cancelShipment(jsonObject, servletRequest);
    }

    /**
     * @Description: 出荷検品サイズPDF
     * @Param: jsonObject
     * @return: SUCCESS
     * @Date: 2020/9/14
     */
    @ApiOperation(value = "出荷検品サイズPDF")
    @GetMapping("/wms/shipments/incidens/size/pdf")
    public JSONObject shipmentSizePdf() {
        shipmentService.shipmentSizePdf();
        return CommonUtils.success("SUCCESS");
    }

    /**
     * @Param: file
     * @param: shipment_plan_id
     * @Param: client_id
     * @description: 梱包作業画像を添付上传
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/27
     */
    @ApiOperation(value = "梱包作業画像を添付上传")
    @PostMapping("/wms/shipments/confirm_img")
    public JSONObject uploadConfirmImg(@RequestParam("file") MultipartFile file[], String[] shipment_plan_id,
        String client_id) {
        return shipmentService.uploadConfirmImg(file, shipment_plan_id, client_id);
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/09/11
     */
    @ApiOperation(value = "出庫検品", notes = "出庫検品")
    @GetMapping("/wms/pc/shipments/incidents/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject getPcShipmentsIncidents(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {

        try {
            List<Tw200_shipment> list = shipmentService.getShipmentsIncidents(warehouse_cd, shipment_plan_id);
            return CommonUtils.success(list);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * @Description: 出庫検品
     * @Param: 顧客CD, 出庫依頼ID
     * @return: json
     * @Date: 2020/11/10
     */
    @ApiOperation(value = "出庫検品", notes = "出庫検品")
    @PutMapping("/wms/pc/shipments/incidents/{warehouse_cd}/{shipment_plan_id}/{shipment_status}/{user_id}")
    public JSONObject updatePcShipmentsIncidents(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        @PathVariable("shipment_status") Integer shipment_status, HttpServletRequest servletRequest,
        String sizeName, Integer boxes, @PathVariable("user_id") String user_id) {

        try {
            return shipmentService.updateShipmentsIncidents(warehouse_cd, shipment_plan_id, shipment_status, sizeName,
                boxes, servletRequest, user_id);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 确认未调用，删除处理
    /**
     * @Description: 出庫検品法人个人统计
     * @param warehouse_cd
     * @Date: 2021/06/09
     */
    @ApiModelProperty(value = "出庫検品件数")
    @GetMapping("/wms/shipments/incidents/count/{warehouse_cd}")
    public JSONObject incidentsCount(@PathVariable("warehouse_cd") String warehouse_cd) {

        try {
            JSONObject jsonObject = shipmentService.incidentsCount(warehouse_cd);

            return CommonUtils.success(jsonObject);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @param product_id : 商品Id
     * @param serial_no : シリアル番号
     * @description: PC出庫検品更新シリアル番号
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 14:07
     */
    @ApiOperation(value = "PC出庫検品更新シリアル番号", notes = "出庫検品更新シリアル番号")
    @PutMapping("/wms/pc/shipments/serial_no/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject pcUpdateSerialNo(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id,
        String product_id, String serial_no, String set_sub_id) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("product_id", product_id);
        jsonObject.put("serial_no", !StringTools.isNullOrEmpty(serial_no) ? serial_no.trim() : "");
        jsonObject.put("set_sub_id", StringTools.isNullOrEmpty(set_sub_id) ? "" : set_sub_id);

        CommonUtils.hashAllRequired(jsonObject, "warehouse_cd,shipment_plan_id,product_id,serial_no");
        return shipmentService.updateSerialNo(jsonObject);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: PC将该出库依赖下的所有商品的シリアル番号清空
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/24 12:52
     */
    @ApiOperation(value = "PC出庫検品更新シリアル番号", notes = "PC出庫検品更新シリアル番号")
    @PutMapping("/wms/pc/shipments/empty/serial_no/{warehouse_cd}/{shipment_plan_id}")
    public JSONObject emptyPcSerialNo(@PathVariable("warehouse_cd") String warehouse_cd,
        @PathVariable("shipment_plan_id") String shipment_plan_id) {
        return shipmentService.emptySerialNo(warehouse_cd, shipment_plan_id);
    }


    /**
     * @param warehouse_cd : 仓库id
     * @param workIdList : 作业者Id集合
     * @description: 获取トータルピッキングlist
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 16:18
     */
    @ApiOperation(value = "トータルピッキング")
    @RequestMapping(value = "/wms/shipments/getPickingList/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getPickingList(@PathVariable("warehouse_cd") String warehouse_cd,
        @RequestParam(value = "workIdList", required = false) List<Integer> workIdList) {
        return shipmentService.getPickingList(warehouse_cd, workIdList);
    }

    /**
     * @param jsonObject : 存的出庫作業ロケ明細 Id
     * @description: 修改 トータルピッキング确认状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 15:46
     */
    @ApiOperation(value = "更改トータルピッキング确认状态")
    @RequestMapping(value = "/wms/shipment/update/total_pinking_status", method = RequestMethod.POST)
    public JSONObject updatePinkingStatus(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return shipmentService.updatePinkingStatus(jsonObject, request);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @description: 获取没有トータルピッキング确认的作业者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/8 17:52
     */
    @ApiOperation(value = "获取没有トータルピッキング确认的作业者信息")
    @RequestMapping(value = "/wms/shipments/workInfo/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getWorkInfo(@PathVariable("warehouse_cd") String warehouse_cd) {
        return shipmentService.getWorkInfo(warehouse_cd);
    }

    /**
     * @param warehouse_cd : 仓库id
     * @param startDate : 起始时间
     * @param endDate : 结束时间
     * @param type : 1：出庫依頼ごとトータルピッキングレポート 2：作業者ごとトータルピッキングレポート
     * @description: とトータルピッキングレポートCSV数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/9 16:49
     */
    @ApiOperation(value = "とトータルピッキングレポートCSV数据")
    @RequestMapping(value = "/wms/shipments/statistics/{warehouse_cd}", method = RequestMethod.GET)
    public JSONObject getStatistics(@PathVariable("warehouse_cd") String warehouse_cd, String startDate, String endDate,
        Integer type, String outputType) {
        return shipmentService.getStatistics(warehouse_cd, startDate, endDate, type, outputType);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @description: 更改撮影状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/23 12:53
     */
    @ApiOperation(value = "更改撮影状态")
    @PutMapping("/wms/shipments/update/photography")
    public JSONObject updatePhotography(String warehouse_cd, String shipment_plan_id, HttpServletRequest request) {
        return shipmentService.updatePhotography(warehouse_cd, shipment_plan_id, request);
    }

    /**
     * @param warehouse_cd : 仓库Id
     * @param shipment_plan_id : 出库依赖Id
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @description: 获取可以进行引当振替処理的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/15 9:52
     */
    @ApiOperation(value = "获取可以进行引当振替処理的数据")
    @GetMapping("/wms/shipments/reserve/transfer/{warehouse_cd}")
    public JSONObject getReserveData(@PathVariable(value = "warehouse_cd") String warehouse_cd,
        String shipment_plan_id, String product_id, String client_id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", client_id);
        jsonObject.put("warehouse_cd", warehouse_cd);
        jsonObject.put("shipment_plan_id", shipment_plan_id);
        jsonObject.put("product_id", product_id);
        CommonUtils.hashAllRequired(jsonObject, "client_id,warehouse_cd,shipment_plan_id,product_id");
        return shipmentService.getReserveData(jsonObject);
    }

    /**
     * @param jsonObject : shipment: 出库数据 reserve: 被引当替换的数据 client_id: 店铺Id
     * @param request : 请求 为了获取token
     * @description: 进行引当振替処理
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/16 9:51
     */
    @ApiOperation(value = "进行引当振替処理")
    @PostMapping("/wms/shipments/reserve/update/{shipment_plan_id}")
    public JSONObject updateReserveData(@PathVariable("shipment_plan_id") String shipment_plan_id,
        @RequestBody JSONObject jsonObject, HttpServletRequest request) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,warehouse_cd");
        return shipmentService.updateReserveData(shipment_plan_id, jsonObject, request);
    }

    /**
     * @Description: 出庫個口数編集
     * @Param: json
     * @return: json
     * @Author: zhangmj
     * @Date: 2020/11/26
     */
    @ApiOperation(value = "サイズ編集", notes = "サイズ編集")
    @PutMapping("/wms/shipments/size")
    public JSONObject updateSize(@RequestBody JSONObject jsonObject, HttpServletRequest servletRequest) {
        Integer result = shipmentService.updateSize(servletRequest, jsonObject);
        if (result > 0) {
            return CommonUtils.success("SUCCESS");
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description 获取bizlogi pdf
     * @param requestJsonObject 出库相关参数
     * @param servletRequest http请求
     * @return JSONObject
     * @date 2021/7/20
     **/
    @ApiModelProperty(value = "bizlogi", notes = "bizlogi pdf")
    @PostMapping("/wms/shipment/bizLogiPdf")
    public JSONObject bizLogiPdf(@RequestBody JSONObject requestJsonObject, HttpServletRequest servletRequest) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", requestJsonObject.getString("warehouse_cd"));
        jsonObject.put("shipment_status", requestJsonObject.getString("shipment_status"));
        jsonObject.put("shipping_end_date", requestJsonObject.getString("shipping_end_date"));
        jsonObject.put("client_id", requestJsonObject.getString("client_id"));
        jsonObject.put("keyword", requestJsonObject.getString("keyword"));

        String boxes = requestJsonObject.getString("boxes");
        String shipmentPlanIdsParam = requestJsonObject.getString("ids");
        List<String> ids = Splitter.on(",").trimResults().splitToList(shipmentPlanIdsParam);

        List<Tw200_shipment> list = shipmentService.getShipmentsListByPlanIds(ids);

        JSONObject resBean = shipmentService.bizLogiPdf(list, ids, boxes);

        return CommonUtils.success(resBean);
    }

    /**
     * @description bizlogi pdf 再发行
     * @param jsonParams 前端提交的shipmentNumber
     * @return JSONObject
     * @date 2021/7/20
     **/
    @ApiModelProperty(value = "bizlogi retry", notes = "bizlogi retry")
    @PostMapping("/wms/shipment/bizLogiRetryPdf")
    public JSONObject bizlogiRetryPdf(@RequestBody JSONObject jsonParams) {
        String shipmentNumber = jsonParams.getString("shipmentNumber");
        if (Strings.isNullOrEmpty(shipmentNumber)) {
            throw new PlValidationErrorException("必須パラメーターが存在するので、ご確認お願いします。");
        }
        JSONObject resBean = new JSONObject();
        resBean = shipmentService.bizLogiRetryPdf(shipmentNumber);
        return resBean;
    }

    /**
     * @description 获取bizlogi pdf
     * @param requestJsonObject 出库相关参数
     * @param servletRequest http请求
     * @return JSONObject
     * @date 2021/7/20
     **/
    @ApiModelProperty(value = "bizlogi", notes = "bizlogi pdf")
    @PostMapping("/wms/shipment/bizLogiData")
    public JSONObject bizLogiData(@RequestBody JSONObject requestJsonObject, HttpServletRequest servletRequest) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse_cd", requestJsonObject.getString("warehouse_cd"));
        jsonObject.put("shipment_status", requestJsonObject.getString("shipment_status"));
        jsonObject.put("shipping_end_date", requestJsonObject.getString("shipping_end_date"));
        jsonObject.put("client_id", requestJsonObject.getString("client_id"));
        jsonObject.put("keyword", requestJsonObject.getString("keyword"));

        String shipmentPlanIdsParam = requestJsonObject.getString("ids");
        List<String> ids = Splitter.on(",").trimResults().splitToList(shipmentPlanIdsParam);

        List<Tw200_shipment> list = shipmentService.getShipmentsListByPlanIds(ids);

        JSONObject resBean = shipmentService.bizLogiData(list, ids);

        return CommonUtils.success(resBean);
    }
}
