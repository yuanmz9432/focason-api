package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Ms007_setting
 * @description: 店舗側連携設定
 * @date: 2020/11/11 18:01
 **/
@ApiModel(value = "Ms007_setting", description = "Ms007_setting")
public class Ms007_setting
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "店舗ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "区分", required = true)
    private Integer kubun;
    @ApiModelProperty(value = "変換前値")
    private String mapping_value;
    @ApiModelProperty(value = "変換後値")
    private String converted_value;
    @ApiModelProperty(value = "変換後管理ID")
    private String converted_id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Integer getKubun() {
        return kubun;
    }

    public void setKubun(Integer kubun) {
        this.kubun = kubun;
    }

    public String getMapping_value() {
        return mapping_value;
    }

    public void setMapping_value(String mapping_value) {
        this.mapping_value = mapping_value;
    }

    public String getConverted_value() {
        return converted_value;
    }

    public void setConverted_value(String converted_value) {
        this.converted_value = converted_value;
    }

    public String getConverted_id() {
        return converted_id;
    }

    public void setConverted_id(String converted_id) {
        this.converted_id = converted_id;
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
        return "Ms007_setting{" +
            "id=" + id +
            ", client_id='" + client_id + '\'' +
            ", kubun=" + kubun +
            ", mapping_value='" + mapping_value + '\'' +
            ", converted_value='" + converted_value + '\'' +
            ", converted_id='" + converted_id + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
