package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogic
 * @description: 依頼主マスタ
 * @create: 2020-05-27 14:43
 **/
public class Ms012_sponsor_master
{
    @ApiModelProperty(value = "管理ID", required = true)
    private String sponsor_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "利用区分")
    private String utilize;
    @ApiModelProperty(value = "お名前", required = true)
    private String name;
    @ApiModelProperty(value = "お名前（フリガナ）")
    private String name_kana;
    @ApiModelProperty(value = "会社名")
    private String company;
    @ApiModelProperty(value = "部署")
    private String division;
    @ApiModelProperty(value = "郵便番号", required = true)
    private String postcode;
    @ApiModelProperty(value = "都道府県")
    private String prefecture;
    @ApiModelProperty(value = "住所", required = true)
    private String address1;
    @ApiModelProperty(value = "マンション・ビル名")
    private String address2;
    @ApiModelProperty(value = "電話番号", required = true)
    private String phone;
    @ApiModelProperty(value = "メールアドレス", required = true)
    private String email;
    @ApiModelProperty(value = "お問い合わせ先設定", required = true)
    private Integer contact;
    @ApiModelProperty(value = "お問い合わせフォームURL")
    private String contact_url;
    @ApiModelProperty(value = "明細書ロゴ")
    private String detail_logo;
    @ApiModelProperty(value = "明細書メッセージ")
    private String detail_message;
    @ApiModelProperty(value = "発送通知メッセージ")
    private String send_message;
    @ApiModelProperty(value = "デフォルト")
    private Integer sponsor_default;
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
    @ApiModelProperty(value = "FAX")
    private String fax;
    @ApiModelProperty(value = "品名")
    private String label_note;
    @ApiModelProperty(value = "納品書の同梱設定")
    private Integer delivery_note_type;
    @ApiModelProperty(value = "納品書の金額印字")
    private Integer price_on_delivery_note;
    @ApiModelProperty(value = "品名設定")
    private Integer description_setting;

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUtilize() {
        return utilize;
    }

    public void setUtilize(String utilize) {
        this.utilize = utilize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_kana() {
        return name_kana;
    }

    public void setName_kana(String name_kana) {
        this.name_kana = name_kana;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getContact() {
        return contact;
    }

    public void setContact(Integer contact) {
        this.contact = contact;
    }

    public String getContact_url() {
        return contact_url;
    }

    public void setContact_url(String contact_url) {
        this.contact_url = contact_url;
    }

    public String getDetail_logo() {
        return detail_logo;
    }

    public void setDetail_logo(String detail_logo) {
        this.detail_logo = detail_logo;
    }

    public String getDetail_message() {
        return detail_message;
    }

    public void setDetail_message(String detail_message) {
        this.detail_message = detail_message;
    }

    public String getSend_message() {
        return send_message;
    }

    public void setSend_message(String send_message) {
        this.send_message = send_message;
    }

    public Integer getSponsor_default() {
        return sponsor_default;
    }

    public void setSponsor_default(Integer sponsor_default) {
        this.sponsor_default = sponsor_default;
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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getLabel_note() {
        return label_note;
    }

    public void setLabel_note(String label_note) {
        this.label_note = label_note;
    }

    public Integer getDelivery_note_type() {
        return delivery_note_type;
    }

    public void setDelivery_note_type(Integer delivery_note_type) {
        this.delivery_note_type = delivery_note_type;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
    }

    public Integer getDescription_setting() {
        return description_setting;
    }

    public void setDescription_setting(Integer description_setting) {
        this.description_setting = description_setting;
    }

    @Override
    public String toString() {
        return "Ms012_sponsor_master{" +
            "sponsor_id='" + sponsor_id + '\'' +
            ", client_id='" + client_id + '\'' +
            ", utilize='" + utilize + '\'' +
            ", name='" + name + '\'' +
            ", name_kana='" + name_kana + '\'' +
            ", company='" + company + '\'' +
            ", division='" + division + '\'' +
            ", postcode='" + postcode + '\'' +
            ", prefecture='" + prefecture + '\'' +
            ", address1='" + address1 + '\'' +
            ", address2='" + address2 + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            ", contact=" + contact +
            ", contact_url='" + contact_url + '\'' +
            ", detail_logo='" + detail_logo + '\'' +
            ", detail_message='" + detail_message + '\'' +
            ", send_message='" + send_message + '\'' +
            ", sponsor_default=" + sponsor_default +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", fax='" + fax + '\'' +
            ", label_note='" + label_note + '\'' +
            ", delivery_note_type=" + delivery_note_type +
            ", price_on_delivery_note=" + price_on_delivery_note +
            '}';
    }
}
