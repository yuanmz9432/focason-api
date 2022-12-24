package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Mc013_api_template
 * @description: APIのテンプレート
 * @author: Hzm
 * @date: 2020/12/16 10:00
 **/
@ApiModel(value = "外部連携商品ID")
public class Mc106_produce_renkei
{
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "クライアントID", required = true)
    private String client_id;
    @ApiModelProperty(value = "API識別コード", required = true)
    private Integer api_id;
    @ApiModelProperty(value = "外部連携商品ID", required = true)
    private String renkei_product_id;
    @ApiModelProperty(value = "外部連携商品variant_id", required = true)
    private String variant_id;
    @ApiModelProperty(value = "備考")
    private String biko;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "乐天在库类别")
    private String inventory_type;
    @ApiModelProperty(value = "token")
    private String access_token;
    @ApiModelProperty(value = "刷新token")
    private String refresh_token;
    @ApiModelProperty(value = "店舗URL")
    private String client_url;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Integer getApi_id() {
        return api_id;
    }

    public void setApi_id(Integer api_id) {
        this.api_id = api_id;
    }

    public String getRenkei_product_id() {
        return renkei_product_id;
    }

    public void setRenkei_product_id(String renkei_product_id) {
        this.renkei_product_id = renkei_product_id;
    }

    public String getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(String variant_id) {
        this.variant_id = variant_id;
    }

    public String getBiko() {
        return biko;
    }

    public void setBiko(String biko) {
        this.biko = biko;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public String getInventory_type() {
        return inventory_type;
    }

    public void setInventory_type(String inventory_type) {
        this.inventory_type = inventory_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getClient_url() {
        return client_url;
    }

    public void setClient_url(String client_url) {
        this.client_url = client_url;
    }

    @Override
    public String toString() {
        return "Mc106_produce_renkei{" + "product_id='" + product_id + '\'' + ", client_id='" + client_id + '\''
            + ", api_id=" + api_id + ", renkei_product_id='" + renkei_product_id + '\'' + ", variant_id='"
            + variant_id + '\'' + ", biko='" + biko + '\'' + ", ins_usr='" + ins_usr + '\'' + ", ins_date="
            + ins_date + ", del_flg=" + del_flg + ", inventory_type='" + inventory_type + '\'' + ", access_token='"
            + access_token + '\'' + ", refresh_token='" + refresh_token + '\'' + ", client_url='" + client_url
            + '\'' + '}';
    }
}
