package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author WangQuanSheng
 * @className InitInsProductBean
 * @description Mail message
 * @date 2020/07/24 09:24
 **/
@ApiModel(value = "商品登録情報")
public class ProductBean
{
    @ApiModelProperty(value = "倉庫CD")
    private String warehouseCd;
    @ApiModelProperty(value = "店舗ID")
    private String clientId;
    @ApiModelProperty(value = "商品CODE")
    private String code;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品単価")
    private String price;
    @ApiModelProperty(value = "税区分")
    private Integer isReducedTax;
    @ApiModelProperty(value = "バリエーションID")
    private String variantId;
    @ApiModelProperty(value = "外部連携ID")
    private String renkeiPid;
    @ApiModelProperty(value = "API番号")
    private Integer apiId;
    @ApiModelProperty(value = "API名称")
    private String apiName;
    @ApiModelProperty(value = "オプション値")
    private String options;
    @ApiModelProperty(value = "在庫タイプ")
    private String type;
    @ApiModelProperty(value = "管理バーコード")
    private String barcode;

    /**
     * Getting & Setting
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWarehouseCd() {
        return warehouseCd;
    }

    public void setWarehouseCd(String warehouseCd) {
        this.warehouseCd = warehouseCd;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getIsReducedTax() {
        return isReducedTax;
    }

    public void setIsReducedTax(Integer isReducedTax) {
        this.isReducedTax = isReducedTax;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public String getRenkeiPid() {
        return renkeiPid;
    }

    public void setRenkeiPid(String renkeiPid) {
        this.renkeiPid = renkeiPid;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
