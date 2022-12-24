package com.lemonico.common.bean;



import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @className: Ms201_customer_group
 * @description: 顧客グループマスタ
 * @date: 2020/05/29 13:20
 **/
public class Ms201_client implements Serializable
{
    @ApiModelProperty(value = "店舗ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "店舗名")
    private String client_nm;
    @ApiModelProperty(value = "店舗表示名")
    private String shop_nm;
    @ApiModelProperty(value = "店舗ログ")
    private String logo;
    @ApiModelProperty(value = "有効開始日付")
    private Date validity_str_date;
    @ApiModelProperty(value = "有効終了日付")
    private Date validity_end_date;
    @ApiModelProperty(value = "事業形態")
    private String corporation_flg;
    @ApiModelProperty(value = "法人番号")
    private String corporation_number;
    @ApiModelProperty(value = "部署")
    private String department;
    @ApiModelProperty(value = "本社所在地フラグ")
    private Integer main_office_flg;
    @ApiModelProperty(value = "生年月日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    @ApiModelProperty(value = "国・地域")
    private String country_region;
    @ApiModelProperty(value = "電話番号")
    private String tel;
    @ApiModelProperty(value = "FAX")
    private String fax;
    @ApiModelProperty(value = "郵便番号")
    private String zip;
    @ApiModelProperty(value = "都道府県")
    private String tdfk;
    @ApiModelProperty(value = "住所１")
    private String add1;
    @ApiModelProperty(value = "住所２")
    private String add2;
    @ApiModelProperty(value = "担当者名")
    private String tnnm;
    @ApiModelProperty(value = "担当部署")
    private String tnbs;
    @ApiModelProperty(value = "担当メール")
    private String mail;
    @ApiModelProperty(value = "連絡可能時間帯")
    private String contact_time;
    @ApiModelProperty(value = "店舗URL")
    private String url;
    @ApiModelProperty(value = "出荷件数")
    private Integer permonth;
    @ApiModelProperty(value = "顔色")
    private String color;
    @ApiModelProperty(value = "備考")
    private String biko;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
    @ApiModelProperty(value = "配送方法")
    private String delivery_method;
    @ApiModelProperty(value = "ユーザー")
    private String login_nm;
    @ApiModelProperty(value = "品名")
    private String label_note;
    @ApiModelProperty(value = "請求顧客コード")
    private String bill_customer_cd;
    @ApiModelProperty(value = "運賃管理番号")
    private String fare_manage_cd;
    @ApiModelProperty(value = "お客様コード")
    private String customer_code;
    @ApiModelProperty(value = "ご請求先分類コード")
    private String sagawa_nisugata_code;
    @ApiModelProperty(value = "荷姿コード")
    private String yamato_manage_code;
    @ApiModelProperty(value = "荷送人コード")
    private String delivery_code;
    @ApiModelProperty(value = "咨询类型")
    private Integer contact;

    public Integer getContact() {
        return contact;
    }

    public void setContact(Integer contact) {
        this.contact = contact;
    }

    @ApiModelProperty(value = "仓库Id")
    private String warehouse_cd;
    @ApiModelProperty(value = "明細書金額印字")
    private Integer price_on_delivery_note;
    @ApiModelProperty(value = "默认依頼主ID")
    private String sponsor_id;
    @ApiModelProperty(value = "可登录ip地址")
    private String ip_address;
    @ApiModelProperty(value = "西濃運輸荷送人コード")
    private String seino_delivery_code;
    @ApiModelProperty(value = "品名設定")
    private String description_setting;

    public String getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(String delivery_code) {
        this.delivery_code = delivery_code;
    }

    public String getSagawa_nisugata_code() {
        return sagawa_nisugata_code;
    }

    public void setSagawa_nisugata_code(String sagawa_nisugata_code) {
        this.sagawa_nisugata_code = sagawa_nisugata_code;
    }

    public String getYamato_manage_code() {
        return yamato_manage_code;
    }

    public void setYamato_manage_code(String yamato_manage_code) {
        this.yamato_manage_code = yamato_manage_code;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_nm() {
        return client_nm;
    }

    public void setClient_nm(String client_nm) {
        this.client_nm = client_nm;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Date getValidity_str_date() {
        return validity_str_date;
    }

    public void setValidity_str_date(Date validity_str_date) {
        this.validity_str_date = validity_str_date;
    }

    public Date getValidity_end_date() {
        return validity_end_date;
    }

    public void setValidity_end_date(Date validity_end_date) {
        this.validity_end_date = validity_end_date;
    }

    public String getCorporation_flg() {
        return corporation_flg;
    }

    public void setCorporation_flg(String corporation_flg) {
        this.corporation_flg = corporation_flg;
    }

    public String getCorporation_number() {
        return corporation_number;
    }

    public void setCorporation_number(String corporation_number) {
        this.corporation_number = corporation_number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getMain_office_flg() {
        return main_office_flg;
    }

    public void setMain_office_flg(Integer main_office_flg) {
        this.main_office_flg = main_office_flg;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getShop_nm() {
        return shop_nm;
    }

    public void setShop_nm(String shop_nm) {
        this.shop_nm = shop_nm;
    }

    public String getCountry_region() {
        return country_region;
    }

    public void setCountry_region(String country_region) {
        this.country_region = country_region;
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

    public String getTdfk() {
        return tdfk;
    }

    public void setTdfk(String tdfk) {
        this.tdfk = tdfk;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getTnnm() {
        return tnnm;
    }

    public void setTnnm(String tnnm) {
        this.tnnm = tnnm;
    }

    public String getTnbs() {
        return tnbs;
    }

    public void setTnbs(String tnbs) {
        this.tnbs = tnbs;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getContact_time() {
        return contact_time;
    }

    public void setContact_time(String contact_time) {
        this.contact_time = contact_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPermonth() {
        return permonth;
    }

    public void setPermonth(Integer permonth) {
        this.permonth = permonth;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getDelivery_method() {
        return delivery_method;
    }

    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

    public String getLogin_nm() {
        return login_nm;
    }

    public void setLogin_nm(String login_nm) {
        this.login_nm = login_nm;
    }

    public String getLabel_note() {
        return label_note;
    }

    public void setLabel_note(String label_note) {
        this.label_note = label_note;
    }

    public String getBill_customer_cd() {
        return bill_customer_cd;
    }

    public void setBill_customer_cd(String bill_customer_cd) {
        this.bill_customer_cd = bill_customer_cd;
    }

    public String getFare_manage_cd() {
        return fare_manage_cd;
    }

    public void setFare_manage_cd(String fare_manage_cd) {
        this.fare_manage_cd = fare_manage_cd;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getSeino_delivery_code() {
        return seino_delivery_code;
    }

    public void setSeino_delivery_code(String seino_delivery_code) {
        this.seino_delivery_code = seino_delivery_code;
    }

    public String getDescription_setting() {
        return description_setting;
    }

    public void setDescription_setting(String description_setting) {
        this.description_setting = description_setting;
    }

    @Override
    public String toString() {
        return "Ms201_client{" +
            "client_id='" + client_id + '\'' +
            ", client_nm='" + client_nm + '\'' +
            ", shop_nm='" + shop_nm + '\'' +
            ", logo='" + logo + '\'' +
            ", validity_str_date=" + validity_str_date +
            ", validity_end_date=" + validity_end_date +
            ", corporation_flg='" + corporation_flg + '\'' +
            ", corporation_number='" + corporation_number + '\'' +
            ", department='" + department + '\'' +
            ", main_office_flg=" + main_office_flg +
            ", birthday=" + birthday +
            ", country_region='" + country_region + '\'' +
            ", tel='" + tel + '\'' +
            ", fax='" + fax + '\'' +
            ", zip='" + zip + '\'' +
            ", tdfk='" + tdfk + '\'' +
            ", add1='" + add1 + '\'' +
            ", add2='" + add2 + '\'' +
            ", tnnm='" + tnnm + '\'' +
            ", tnbs='" + tnbs + '\'' +
            ", mail='" + mail + '\'' +
            ", contact_time='" + contact_time + '\'' +
            ", url='" + url + '\'' +
            ", permonth=" + permonth +
            ", color='" + color + '\'' +
            ", biko='" + biko + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", delivery_method='" + delivery_method + '\'' +
            ", login_nm='" + login_nm + '\'' +
            ", label_note='" + label_note + '\'' +
            ", bill_customer_cd='" + bill_customer_cd + '\'' +
            ", fare_manage_cd='" + fare_manage_cd + '\'' +
            ", customer_code='" + customer_code + '\'' +
            ", sagawa_nisugata_code='" + sagawa_nisugata_code + '\'' +
            ", yamato_manage_code='" + yamato_manage_code + '\'' +
            ", delivery_code='" + delivery_code + '\'' +
            ", contact=" + contact +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", price_on_delivery_note=" + price_on_delivery_note +
            ", sponsor_id='" + sponsor_id + '\'' +
            ", ip_address='" + ip_address + '\'' +
            ", seino_delivery_code='" + seino_delivery_code + '\'' +
            ", description_setting='" + description_setting + '\'' +
            '}';
    }
}
