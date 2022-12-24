package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @className: ms014_payment
 * @description: ms014_payment
 * @date: 2021/01/26
 **/
@ApiModel(value = "ms014_payment", description = "ms014_payment")
public class Ms014_payment
{
    @ApiModelProperty(value = "決済管理ID", required = true)
    private Integer payment_id;
    @ApiModelProperty(value = "利用区分")
    private Integer kubu;
    @ApiModelProperty(value = "決済方法")
    private String payment_name;
    @ApiModelProperty(value = "csv出力値")
    private String payment_csv;
    @ApiModelProperty(value = "優先順位")
    private Integer sort_no;
    @ApiModelProperty(value = "説明")
    private String info;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public Integer getPayment_id() {
        return payment_id;
    }

    public Integer getKubu() {
        return kubu;
    }

    public void setKubu(Integer kubu) {
        this.kubu = kubu;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public String getPayment_csv() {
        return payment_csv;
    }

    public Integer getSort_no() {
        return sort_no;
    }

    public String getInfo() {
        return info;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public String getIns_date() {
        return ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public String getUpd_date() {
        return upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setPayment_id(Integer payment_id) {
        this.payment_id = payment_id;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public void setPayment_csv(String payment_csv) {
        this.payment_csv = payment_csv;
    }

    public void setSort_no(Integer sort_no) {
        this.sort_no = sort_no;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public void setIns_date(String ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public void setUpd_date(String upd_date) {
        this.upd_date = upd_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
