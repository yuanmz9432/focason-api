package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Ms203_customer_auth
 * @description:
 * @date: 2020/09/17
 **/
public class Ms203_customer_auth implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String user_id;
    @ApiModelProperty(value = "権限コード", required = true)
    private String authority_cd;
    @ApiModelProperty(value = "使用区分", required = true)
    private String authority_kb;
    @ApiModelProperty(value = "備考")
    private String biko;
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

    public String getUser_id() {
        return user_id;
    }

    public String getAuthority_cd() {
        return authority_cd;
    }

    public String getAuthority_kb() {
        return authority_kb;
    }

    public String getBiko() {
        return biko;
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

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAuthority_cd(String authority_cd) {
        this.authority_cd = authority_cd;
    }

    public void setAuthority_kb(String authority_kb) {
        this.authority_kb = authority_kb;
    }

    public void setBiko(String biko) {
        this.biko = biko;
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
