package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Mc105_product_setting
 * @description: 商品設定
 * @date: 2020/06/4
 **/
@ApiModel(value = "商品設定")
public class Mc105_product_setting implements Serializable
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "設定CD", required = true)
    private Integer set_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "備考")
    private String info;
    @ApiModelProperty(value = "税込,税抜")
    private Integer tax;
    @ApiModelProperty(value = "切り捨て,切り上げ,四捨五入", required = true)
    private Integer accordion;
    @ApiModelProperty(value = "印字する,印字しない", required = true)
    private Integer price_on_delivery_note;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "明細書の同梱設定")
    private String delivery_note_type;
    @ApiModelProperty(value = "明細書のバージョン")
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDelivery_note_type() {
        return delivery_note_type;
    }

    public void setDelivery_note_type(String delivery_note_type) {
        this.delivery_note_type = delivery_note_type;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Integer getSet_cd() {
        return set_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getInfo() {
        return info;
    }

    public Integer getTax() {
        return tax;
    }

    public Integer getAccordion() {
        return accordion;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setSet_cd(Integer set_cd) {
        this.set_cd = set_cd;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public void setAccordion(Integer accordion) {
        this.accordion = accordion;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
