package com.lemonico.ntm.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw216_delivery_fare;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.ntm.service.NtmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ntm")
@Api(tags = "ntm api")
public class NtmController
{

    private final static Logger logger = LoggerFactory.getLogger(NtmController.class);

    @Autowired
    private NtmService ntmService;

    /**
     * @description: ntm top页api
     * @return: JSONObject: enterprise,person
     * @date: 2021/4/15
     **/
    @GetMapping("/top")
    public JSONObject top(HttpServletRequest request) {
        JSONObject resBean = new JSONObject();
        String clientId = CommonUtils.getToken("client_id", request);

        resBean = ntmService.getTopData(clientId);
        return CommonUtils.success(resBean);
    }

    /**
     * @description ntm的配送運賃数据同步到sunlogi
     * @param params 配送運賃数据
     * @return JSONObject
     * @date 2021/6/16
     **/
    @PostMapping("/syncDeliveryFare")
    @Transactional(rollbackFor = {
        RuntimeException.class, Error.class
    })
    public JSONObject syncDeliveryFare(@RequestBody JSONObject params, HttpServletRequest request) {
        JSONObject resBean = new JSONObject();
        List<Tw216_delivery_fare> deliveryFareList = null;
        JSONArray deliveryFareString = params.getJSONArray("list");
        try {
            deliveryFareList = JSONObject.parseArray(deliveryFareString.toString(), Tw216_delivery_fare.class);
            ntmService.syncDeliveryFare(deliveryFareList);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.DATABASE_ERROR);
        }

        return CommonUtils.success();
    }

    /**
     * @description NTM个人受注CSV取込
     * @param client_id 店铺ID
     * @param warehouse_cd 仓库cd
     * @param template_cd 模板cd
     * @param file 上传的文件
     * @param shipmentStatus 出库状态
     * @param company_id 公司id
     * @param request http请求
     * @return JSONObject
     * @date 2021/7/15
     **/
    @ApiOperation(value = "NTM个人受注CSV取込")
    @PostMapping("/order/import/csv/{client_id}")
    public JSONObject inportOrderCsv(@PathVariable("client_id") String client_id,
        @RequestParam("warehouse_cd") String warehouse_cd, @RequestParam("template_cd") Integer template_cd,
        @RequestParam("filename") MultipartFile file, @RequestParam("shipmentStatus") String shipmentStatus,
        String company_id, HttpServletRequest request) {
        logger.info("受注CSV取込の開始：" + client_id + "-" + warehouse_cd);
        logger.info("受注CSV取込のFILE：" + file.getName());
        return ntmService.importOrderCsv(file, client_id, warehouse_cd, shipmentStatus, request);
    }
}
