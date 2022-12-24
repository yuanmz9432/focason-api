package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;


/**
 * @className: Ms206_client_customer
 * @description: 店舗別顧客マスタ
 * @date: 2020/9/21 15:23
 **/
@ApiModel(value = "ms205_customer_history", description = "店舗別顧客マスタ")
public class Ms206_client_customer implements Serializable
{
    @ApiModelProperty(value = "店舗ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String user_id;
    @ApiModelProperty(value = "備考", required = true)
    private String yobi;
    @ApiModelProperty(value = "作成者", required = true)
    private String ins_usr;
    @ApiModelProperty(value = "作成日時", required = true)
    private Date ins_date;
    @ApiModelProperty(value = "更新者", required = true)
    private String upd_usr;
    @ApiModelProperty(value = "更新日時", required = true)
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ", required = true)
    private Integer del_flg;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getYobi() {
        return yobi;
    }

    public void setYobi(String yobi) {
        this.yobi = yobi;
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
        return "Ms206_client_customer{" +
            "client_id='" + client_id + '\'' +
            ", user_id='" + user_id + '\'' +
            ", yobi='" + yobi + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
