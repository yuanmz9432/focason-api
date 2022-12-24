package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogic
 * @description: 顧客配送先マスタ
 * @create: 2020-05-29 10:47
 **/
public class Mc200_customer_delivery
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer delivery_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "配送先形態")
    private Integer form;
    @ApiModelProperty(value = "顧客名")
    private String name;
    @ApiModelProperty(value = "郵便番号", required = true)
    private String postcode;
    @ApiModelProperty(value = "都道府県", required = true)
    private String prefecture;
    @ApiModelProperty(value = "住所１", required = true)
    private String address1;
    @ApiModelProperty(value = "住所２")
    private String address2;
    @ApiModelProperty(value = "会社名")
    private String company;
    @ApiModelProperty(value = "部署")
    private String division;
    @ApiModelProperty(value = "電話番号")
    private String phone;
    @ApiModelProperty(value = "メールアドレス")
    private String email;
    @ApiModelProperty(value = "配送便指定")
    private String delivery_method;
    @ApiModelProperty(value = "配送会社")
    private String delivery_carrier;
    @ApiModelProperty(value = "お届け希望日")
    private Integer delivery_date;
    @ApiModelProperty(value = "配達希望時間帯")
    private String delivery_time_slot;
    @ApiModelProperty(value = "不在時宅配ボックス")
    private Integer box_delivery;
    @ApiModelProperty(value = "割れ物注意")
    private Integer fragile_item;
    @ApiModelProperty(value = "緩衝材単位:0: なし 1:注文単位")
    private String cushioning_unit;
    @ApiModelProperty(value = "ギフトラッピング単位:0: なし 1:注文単位")
    private String gift_wrapping_unit;
    @ApiModelProperty(value = "緩衝材種別")
    private String cushioning_type;
    @ApiModelProperty(value = "ギフトラッピングタイプ")
    private String gift_wrapping_type;
    @ApiModelProperty(value = "明細書の同梱設定")
    private String delivery_note_type;
    @ApiModelProperty(value = "明細書への金額印字指定")
    private Integer price_on_delivery_note;
    @ApiModelProperty(value = "出荷指示特記事項")
    private String invoice_special_notes;
    @ApiModelProperty(value = "備考1")
    private String bikou1;
    @ApiModelProperty(value = "備考2")
    private String bikou2;
    @ApiModelProperty(value = "備考3")
    private String bikou3;
    @ApiModelProperty(value = "備考4")
    private String bikou4;
    @ApiModelProperty(value = "備考5")
    private String bikou5;
    @ApiModelProperty(value = "備考6")
    private String bikou6;
    @ApiModelProperty(value = "備考7")
    private String bikou7;
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

    public String getDelivery_method() {
        return delivery_method;
    }

    public String getDelivery_carrier() {
        return delivery_carrier;
    }

    public Integer getDelivery_date() {
        return delivery_date;
    }

    public String getDelivery_time_slot() {
        return delivery_time_slot;
    }

    public Integer getBox_delivery() {
        return box_delivery;
    }

    public Integer getFragile_item() {
        return fragile_item;
    }

    public String getCushioning_unit() {
        return cushioning_unit;
    }

    public String getGift_wrapping_unit() {
        return gift_wrapping_unit;
    }

    public String getCushioning_type() {
        return cushioning_type;
    }

    public String getGift_wrapping_type() {
        return gift_wrapping_type;
    }

    public String getDelivery_note_type() {
        return delivery_note_type;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public String getInvoice_special_notes() {
        return invoice_special_notes;
    }

    public String getBikou1() {
        return bikou1;
    }

    public String getBikou2() {
        return bikou2;
    }

    public String getBikou3() {
        return bikou3;
    }

    public String getBikou4() {
        return bikou4;
    }

    public String getBikou5() {
        return bikou5;
    }

    public String getBikou6() {
        return bikou6;
    }

    public String getBikou7() {
        return bikou7;
    }

    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

    public void setDelivery_carrier(String delivery_carrier) {
        this.delivery_carrier = delivery_carrier;
    }

    public void setDelivery_date(Integer delivery_date) {
        this.delivery_date = delivery_date;
    }

    public void setDelivery_time_slot(String delivery_time_slot) {
        this.delivery_time_slot = delivery_time_slot;
    }

    public void setBox_delivery(Integer box_delivery) {
        this.box_delivery = box_delivery;
    }

    public void setFragile_item(Integer fragile_item) {
        this.fragile_item = fragile_item;
    }

    public void setCushioning_unit(String cushioning_unit) {
        this.cushioning_unit = cushioning_unit;
    }

    public void setGift_wrapping_unit(String gift_wrapping_unit) {
        this.gift_wrapping_unit = gift_wrapping_unit;
    }

    public void setCushioning_type(String cushioning_type) {
        this.cushioning_type = cushioning_type;
    }

    public void setGift_wrapping_type(String gift_wrapping_type) {
        this.gift_wrapping_type = gift_wrapping_type;
    }

    public void setDelivery_note_type(String delivery_note_type) {
        this.delivery_note_type = delivery_note_type;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
    }

    public void setInvoice_special_notes(String invoice_special_notes) {
        this.invoice_special_notes = invoice_special_notes;
    }

    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
    }

    public void setBikou4(String bikou4) {
        this.bikou4 = bikou4;
    }

    public void setBikou5(String bikou5) {
        this.bikou5 = bikou5;
    }

    public void setBikou6(String bikou6) {
        this.bikou6 = bikou6;
    }

    public void setBikou7(String bikou7) {
        this.bikou7 = bikou7;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public Integer getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(Integer delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
