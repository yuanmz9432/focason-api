package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Ms200_customer
 * @description: 顧客マスタ
 * @date: 2020/05/12 9:07
 **/
@ApiModel(value = "顧客マスタ")
public class Ms200_customer implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String user_id;
    @ApiModelProperty(value = "ログインID", required = true)
    private String login_id;
    @ApiModelProperty(value = "パスワード", required = true)
    private String login_pw;
    @ApiModelProperty(value = "")
    private String client_id;
    @ApiModelProperty(value = "所属顧客CD")
    private String parent_account_id;
    @ApiModelProperty(value = "ログイン名")
    private String login_nm;
    @ApiModelProperty(value = "使用区分", required = true)
    private String usekb;
    @ApiModelProperty(value = "用途", required = true)
    private String yoto;
    @ApiModelProperty(value = "通知")
    private String notice;
    @ApiModelProperty(value = "ソルト")
    private String encode_key;
    @ApiModelProperty(value = "旧パスワード")
    private String old_login_pw;
    @ApiModelProperty(value = "旧ログインID")
    private String old_login_id;
    @ApiModelProperty(value = "備考1")
    private String biko1;
    @ApiModelProperty(value = "備考2")
    private String biko2;
    @ApiModelProperty(value = "備考3")
    private String biko3;
    @ApiModelProperty(value = "備考4")
    private String biko4;
    @ApiModelProperty(value = "備考5")
    private String biko5;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "最終ログイン")
    private String lst_date;
    @ApiModelProperty(value = "削除フラグ", required = true)
    private Integer del_flg;
    @ApiModelProperty(value = "権限名")
    private String authority_nm;
    @ApiModelProperty(value = "担当者名")
    private String tnnm;
    @ApiModelProperty(value = "担当メール")
    private String mail;
    @ApiModelProperty(value = "電話番号")
    private String tel;
    @ApiModelProperty(value = "ロゴ")
    private String logo;

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

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getLogin_pw() {
        return login_pw;
    }

    public void setLogin_pw(String login_pw) {
        this.login_pw = login_pw;
    }

    public String getParent_account_id() {
        return parent_account_id;
    }

    public void setParent_account_id(String parent_account_id) {
        this.parent_account_id = parent_account_id;
    }

    public String getLogin_nm() {
        return login_nm;
    }

    public void setLogin_nm(String login_nm) {
        this.login_nm = login_nm;
    }

    public String getUsekb() {
        return usekb;
    }

    public void setUsekb(String usekb) {
        this.usekb = usekb;
    }

    public String getYoto() {
        return yoto;
    }

    public void setYoto(String yoto) {
        this.yoto = yoto;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getEncode_key() {
        return encode_key;
    }

    public void setEncode_key(String encode_key) {
        this.encode_key = encode_key;
    }

    public String getCredentialsSalt() {
        return user_id + encode_key + encode_key;
    }

    public String getOld_login_pw() {
        return old_login_pw;
    }

    public void setOld_login_pw(String old_login_pw) {
        this.old_login_pw = old_login_pw;
    }

    public String getOld_login_id() {
        return old_login_id;
    }

    public void setOld_login_id(String old_login_id) {
        this.old_login_id = old_login_id;
    }

    public String getBiko1() {
        return biko1;
    }

    public void setBiko1(String biko1) {
        this.biko1 = biko1;
    }

    public String getBiko2() {
        return biko2;
    }

    public void setBiko2(String biko2) {
        this.biko2 = biko2;
    }

    public String getBiko3() {
        return biko3;
    }

    public void setBiko3(String biko3) {
        this.biko3 = biko3;
    }

    public String getBiko4() {
        return biko4;
    }

    public void setBiko4(String biko4) {
        this.biko4 = biko4;
    }

    public String getBiko5() {
        return biko5;
    }

    public void setBiko5(String biko5) {
        this.biko5 = biko5;
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

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public String getLst_date() {
        return lst_date;
    }

    public void setLst_date(String lst_date) {
        this.lst_date = lst_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public String getAuthority_nm() {
        return authority_nm;
    }

    public void setAuthority_nm(String authority_nm) {
        this.authority_nm = authority_nm;
    }

    public String getTnnm() {
        return tnnm;
    }

    public void setTnnm(String tnnm) {
        this.tnnm = tnnm;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Ms200_customer{" +
            "user_id='" + user_id + '\'' +
            ", login_id='" + login_id + '\'' +
            ", login_pw='" + login_pw + '\'' +
            ", client_id='" + client_id + '\'' +
            ", parent_account_id='" + parent_account_id + '\'' +
            ", login_nm='" + login_nm + '\'' +
            ", usekb='" + usekb + '\'' +
            ", yoto='" + yoto + '\'' +
            ", notice='" + notice + '\'' +
            ", encode_key='" + encode_key + '\'' +
            ", old_login_pw='" + old_login_pw + '\'' +
            ", old_login_id='" + old_login_id + '\'' +
            ", biko1='" + biko1 + '\'' +
            ", biko2='" + biko2 + '\'' +
            ", biko3='" + biko3 + '\'' +
            ", biko4='" + biko4 + '\'' +
            ", biko5='" + biko5 + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_date=" + upd_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", lst_date='" + lst_date + '\'' +
            ", del_flg=" + del_flg +
            ", authority_nm='" + authority_nm + '\'' +
            ", tnnm='" + tnnm + '\'' +
            ", mail='" + mail + '\'' +
            ", tel='" + tel + '\'' +
            ", logo='" + logo + '\'' +
            '}';
    }
}
