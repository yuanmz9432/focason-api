package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogi
 * @description: 顧客別作業履歴
 * @create: 2020-07-13 10:13
 **/
@ApiModel(value = "ms205_customer_history", description = "ms205_customer_history")
public class Ms205_customer_history
{

    @ApiModelProperty(value = "作業管理ID", required = true)
    private Integer operation_id;
    @ApiModelProperty(value = "作業者(顧客CD)", required = true)
    private String user_id;
    @ApiModelProperty(value = "依頼ID", required = true)
    private String plan_id;
    @ApiModelProperty(value = "作業時間", required = true)
    private String operation_date;
    @ApiModelProperty(value = "作業コード", required = true)
    private String operation_cd;
    @ApiModelProperty(value = "API URL")
    private Integer api_url;
    @ApiModelProperty(value = "備考")
    private String biko;
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
    @ApiModelProperty(value = "作業名")
    private String operation_nm;
    @ApiModelProperty(value = "ログイン名")
    private String login_nm;

    public Integer getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(Integer operation_id) {
        this.operation_id = operation_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getOperation_date() {
        return operation_date;
    }

    public void setOperation_date(String operation_date) {
        this.operation_date = operation_date;
    }

    public String getOperation_cd() {
        return operation_cd;
    }

    public void setOperation_cd(String operation_cd) {
        this.operation_cd = operation_cd;
    }

    public Integer getApi_url() {
        return api_url;
    }

    public void setApi_url(Integer api_url) {
        this.api_url = api_url;
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

    public String getOperation_nm() {
        return operation_nm;
    }

    public void setOperation_nm(String operation_nm) {
        this.operation_nm = operation_nm;
    }

    public String getLogin_nm() {
        return login_nm;
    }

    public void setLogin_nm(String login_nm) {
        this.login_nm = login_nm;
    }
}
