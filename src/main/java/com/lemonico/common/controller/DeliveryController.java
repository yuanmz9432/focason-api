package com.lemonico.common.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms004_delivery;
import com.lemonico.common.bean.Ms006_delivery_time;
import com.lemonico.common.bean.Ms007_setting;
import com.lemonico.common.bean.Ms014_payment;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.DeliveryService;
import com.lemonico.core.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: sunlogi
 * @description: 運送業者マスタ
 * @create: 2020-07-09 13:19
 **/
@RestController
@Api(tags = "運送業者マスタ")
public class DeliveryController
{

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private DeliveryDao deliveryDao;

    /**
     * @Param: delivery_method : 配送便指定 1:ポスト便 2:宅配便
     * @description: 運送業者マスタ
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/6
     */
    @ApiOperation(value = "運送業者マスタ", notes = "運送業者マスタ")
    @GetMapping("/delivery")
    public JSONObject getDeliveryInfo(String delivery_method) {
        return deliveryService.getDeliveryInfo(delivery_method);
    }

    /**
     * @param: delivery_cd : 配送業者CD
     * @description: 根据配送業者CD获取運送業者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    @ApiOperation(value = "根据配送業者CD获取運送業者信息")
    @GetMapping("/delivery/{delivery_cd}")
    public JSONObject getDeliveryById(@PathVariable("delivery_cd") String delivery_cd) {
        return deliveryService.getDeliveryById(delivery_cd);
    }

    /**
     * @param: delivery_cd : 配送業者CD
     * @description: 根据配送業者CD获取運送業者信息PC版
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    @ApiOperation(value = "根据配送業者CD获取運送業者信息PC版")
    @GetMapping("/pc/delivery/{delivery_cd}")
    public JSONObject getPcDeliveryById(@PathVariable("delivery_cd") String delivery_cd) {
        return deliveryService.getDeliveryById(delivery_cd);
    }

    /**
     * @Param: delivery_nm : 配送業者名称
     * @description: 根据配送業者名称获取運送会社时间带
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    @ApiOperation(value = "根据配送業者名称获取運送会社时间带")
    @GetMapping("/delivery/delivery_time")
    public JSONObject getDeliveryTime(String delivery_nm, Integer delivery_time_id, Integer kubu) {
        return deliveryService.getDeliveryTime(delivery_nm, delivery_time_id, kubu);
    }

    /**
     * @Param: delivery_nm : 配送業者名称
     * @description: 根据配送業者名称获取運送会社时间带PC版
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    @ApiOperation(value = "根据配送業者名称获取運送会社时间带PC版")
    @GetMapping("/pc/delivery/delivery_time")
    public JSONObject getPcDeliveryTime(String delivery_nm, Integer delivery_time_id, Integer kubu) {
        return deliveryService.getDeliveryTime(delivery_nm, delivery_time_id, kubu);
    }

    /**
     * @Description: 運送業者マスタ
     * @Param: 配送業者CD
     * @return: Json
     * @Date: 2020/7/9
     */
    // @ApiOperation(value = "運送業者マスタ", notes = "運送業者マスタ")
    // @GetMapping("/delivery")
    // public JSONObject getDelivery(){
    // List<Ms004_delivery> list = deliveryService.getDelivery();
    // return CommonUtil.successJson(list);
    // }

    /**
     * @Description: 荷送人コード
     * @Param: 配送業者CD
     * @return: java.lang.Integer
     * @Date: 2020/10/14
     */
    @ApiOperation(value = "荷送人コード修改", notes = "荷送人コード修改")
    @PostMapping("/delivery/code/update")
    public JSONObject updateDCByDcode(@RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "delivery_cd");
        return deliveryService.updateDCByDcode(jsonObject);
    }

    /**
     * @description: 配送業者名称数量获取
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/11
     */
    @ApiOperation(value = "配送業者名称数量获取")
    @GetMapping("/delivery/delivery_type")
    public JSONObject getDeliveryType() {
        return deliveryService.getDeliveryType();
    }

    /**
     * @description: 查询Ms007_setting客户取入连携设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/16
     */
    @ApiOperation(value = "查询Ms007_setting客户取入连携设定数据")
    @GetMapping("/delivery/converted")
    public JSONObject getConvertedDataAll(String client_id, Integer kubun) {
        List<Ms007_setting> list = deliveryService.getConvertedDataAll(client_id, kubun);
        return CommonUtils.success(list);
    }

    /**
     * @description: 新规Ms007_setting客户取入连携设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/16
     */
    @ApiOperation(value = "新规Ms007_setting客户取入连携设定数据")
    @PostMapping("/delivery/converted")
    public JSONObject insertConvertedDat(@RequestBody JSONObject jsonObject, HttpServletRequest httpServletRequest) {
        deliveryService.insertConvertedData(jsonObject, httpServletRequest);
        return CommonUtils.success();
    }

    /**
     * @description: 获取所有的配送者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/16
     */
    @ApiOperation(value = "获取所有的配送者信息")
    @GetMapping("/delivery/getDeliveryAll")
    public JSONObject getDeliveryAll() {
        List<Ms004_delivery> list = deliveryDao.getDeliveryAll();
        return CommonUtils.success(list);
    }

    /**
     * @description: 获取所有的配送者时间带信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/16
     */
    @ApiOperation(value = "获取所有的配送者时间带信息")
    @GetMapping("/delivery/getDeliveryTimeAll")
    public JSONObject getDeliveryTimeAll() {
        List<Ms006_delivery_time> list = deliveryDao.getDeliveryTimeAll();
        return CommonUtils.success(list);
    }

    /**
     * @description: 获取所有的支付方法信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/01/26
     */
    @ApiOperation(value = "获取所有的支付方法信息")
    @GetMapping("/delivery/getDeliveryPaymentAll")
    public JSONObject getDeliveryPaymentAll(Integer kubu) {
        List<Ms014_payment> list = deliveryDao.getDeliveryPaymentAll(kubu);
        return CommonUtils.success(list);
    }

    /**
     * @description: 根据支付方法id获取名称
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/01/26
     */
    @ApiOperation(value = "根据支付方法id获取名称")
    @GetMapping("/delivery/getPayById/{payment_id}")
    public String getPayById(@PathVariable("payment_id") Integer payment_id) {
        String payName = deliveryDao.getPayById(payment_id);
        return payName;
    }
}
