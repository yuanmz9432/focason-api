package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Mc100_product
 * @description: 顧客別機能マスタ
 * @date: 2020/05/27 10:00
 **/
@ApiModel(value = "顧客別機能マスタ")
public class Ms204_customer_func implements Serializable
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "機能コード", required = true)
    private String function_cd;
    @ApiModelProperty(value = "使用区分", required = true)
    private String function_kb;
    @ApiModelProperty(value = "備考")
    private String biko;
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
    private String upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getFunction_cd() {
        return function_cd;
    }

    public void setFunction_cd(String function_cd) {
        this.function_cd = function_cd;
    }

    public String getFunction_kb() {
        return function_kb;
    }

    public void setFunction_kb(String function_kb) {
        this.function_kb = function_kb;
    }

    public String getBiko() {
        return biko;
    }

    public void setBiko(String biko) {
        this.biko = biko;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getAccordion() {
        return accordion;
    }

    public void setAccordion(Integer accordion) {
        this.accordion = accordion;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
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

    public String getUpd_usr() {
        return upd_usr;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public String getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(String upd_date) {
        this.upd_date = upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
