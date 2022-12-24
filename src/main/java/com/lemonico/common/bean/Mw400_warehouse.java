package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @className: Mw400_warehouse
 * @description: 倉庫マスタ
 * @date: 2020/05/29 13:03
 **/
public class Mw400_warehouse implements Serializable
{
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "倉庫名称", required = true)
    private String warehouse_nm;
    @ApiModelProperty(value = "别名")
    private String alias;
    @ApiModelProperty(value = "電話番号")
    private String tel;
    @ApiModelProperty(value = "FAX")
    private String fax;
    @ApiModelProperty(value = "郵便番号")
    private String zip;
    @ApiModelProperty(value = "都道府県")
    private String todoufuken;
    @ApiModelProperty(value = "住所１")
    private String address1;
    @ApiModelProperty(value = "住所２")
    private String address2;
    @ApiModelProperty(value = "担当者名")
    private String responsible;
    @ApiModelProperty(value = "メール")
    private String mail;
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
    @ApiModelProperty(value = "可登录ip地址")
    private String ip_address;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getWarehouse_nm() {
        return warehouse_nm;
    }

    public void setWarehouse_nm(String warehouse_nm) {
        this.warehouse_nm = warehouse_nm;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTodoufuken() {
        return todoufuken;
    }

    public void setTodoufuken(String todoufuken) {
        this.todoufuken = todoufuken;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public String getIns_date() {
        return ins_date;
    }

    public void setIns_date(String ins_date) {
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

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
}
