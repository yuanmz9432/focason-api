package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @description csv出力模板表
 * @date 2021/03/19
 **/
@ApiModel(value = "Tc209_setting_template", description = "テンプレート設定詳細")
public class Tc209_setting_template
{
    @ApiModelProperty(value = "テンプレートCD", required = true)
    private Integer template_cd;
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "テンプレート名")
    private String template_nm;
    @ApiModelProperty(value = "用途ID")
    private String yoto_id;
    @ApiModelProperty(value = "文字コード")
    private String encoding;
    @ApiModelProperty(value = "固定値")
    private String constant;
    @ApiModelProperty(value = "テンプレートデータ")
    private String data;
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

    public Integer getTemplate_cd() {
        return template_cd;
    }

    public void setTemplate_cd(Integer template_cd) {
        this.template_cd = template_cd;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getTemplate_nm() {
        return template_nm;
    }

    public void setTemplate_nm(String template_nm) {
        this.template_nm = template_nm;
    }

    public String getYoto_id() {
        return yoto_id;
    }

    public void setYoto_id(String yoto_id) {
        this.yoto_id = yoto_id;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    @Override
    public String toString() {
        return "Tc209_setting_template{" +
            "template_cd=" + template_cd +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", template_nm='" + template_nm + '\'' +
            ", yoto_id='" + yoto_id + '\'' +
            ", encoding='" + encoding + '\'' +
            ", constant='" + constant + '\'' +
            ", data='" + data + '\'' +
            ", ins_user='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_user='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
