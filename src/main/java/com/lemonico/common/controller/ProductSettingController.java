package com.lemonico.common.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc105_product_setting;
import com.lemonico.common.service.ClientService;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: sunlogic
 * @description: 顧客別機能マスタ
 * @create: 2020-06-03 16:48
 **/
@RestController
@Api(tags = "顧客別機能マスタ")
public class ProductSettingController
{

    @Autowired
    private ProductSettingService productSettingService;
    @Autowired
    private ClientService clientService;

    /**
     * @Description:現在登録している顧客商品設定情報を取得する
     * @Param:
     * @return:
     * @Date: 2020/5/27
     */
    @ApiOperation(value = "ユーザー商品設定を取得する", notes = "ユーザー商品設定を取得する")
    @GetMapping(value = "/items/function/{client_id}")
    public JSONObject getProducts(@PathVariable("client_id") String client_id, Integer set_cd,
        HttpServletRequest servletRequest) {
        String user_id = CommonUtils.getToken("user_id", servletRequest);
        Mc105_product_setting list = productSettingService.getProductSetting(client_id, set_cd);
        JSONObject resBean = new JSONObject();
        // list为空判断
        if (!StringTools.isNullOrEmpty(list)) {
            resBean.put("client_id", list.getClient_id());
            resBean.put("set_cd", list.getSet_cd());
            resBean.put("tax", list.getTax());
            resBean.put("accordion", list.getAccordion());
            resBean.put("version", list.getVersion());
        }
        return CommonUtils.success(resBean);
    }

    /**
     * @Description:商品消費税設定
     * @Param:
     * @return:
     * @Date: 2020/5/27
     */
    @ApiOperation(value = "商品消費税設定", notes = "商品消費税設定")
    @PutMapping(value = "/items/function/{client_id}")
    public JSONObject updateProductTax(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonObject) {
        int tax = jsonObject.getInteger("tax");
        int accordion = jsonObject.getInteger("accordion");
        productSettingService.updateProductTax(tax, accordion, client_id);
        return CommonUtils.success();
    }

    /**
     * @Description:金額印字設定
     * @Param:
     * @return:
     * @Date: 2020/5/27
     */
    @ApiOperation(value = "明細書の金額印字設定", notes = "明細書の金額印字設定")
    @PostMapping(value = "/items/function/{client_id}")
    public JSONObject updateProductNote(@PathVariable("client_id") String client_id,
        @RequestBody JSONObject jsonObject) {
        int price_on_delivery_note = jsonObject.getInteger("price_on_delivery_note");
        String delivery_note_type = jsonObject.getString("delivery_note_type");
        if (!StringTools.isNullOrEmpty(delivery_note_type)) {
            int type = Integer.parseInt(delivery_note_type);
            productSettingService.updateProductNote(price_on_delivery_note, type, client_id);
        }
        String label_note = jsonObject.getString("label_note");
        if (!StringTools.isNullOrEmpty(label_note)) {
            jsonObject.put("client_id", client_id);
            clientService.updateLabelNote(jsonObject);
        }

        return CommonUtils.success();
    }
}
