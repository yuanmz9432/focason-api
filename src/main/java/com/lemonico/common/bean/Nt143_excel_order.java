package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @className: Nt143_excel_order
 * @description: ntm-excel读取的数据实体类
 * @date: 2021/3/24 10:07
 **/
@ApiModel(value = "Nt143_excel_order", description = "ntm-excel读取的数据实体类")
public class Nt143_excel_order implements Serializable
{
    @ApiModelProperty(value = "通しNo")
    private String commonNo;
    @ApiModelProperty(value = "請求先支店コード(メールアドレス)")
    private String email;
    @ApiModelProperty(value = "請求先支店名(月報表示用)")
    private String shopName;
    @ApiModelProperty(value = "ツール1数量")
    private String productNum1;
    @ApiModelProperty(value = "ツール2数量")
    private String productNum2;
    @ApiModelProperty(value = "ツール3数量")
    private String productNum3;
    @ApiModelProperty(value = "ツール4数量")
    private String productNum4;
    @ApiModelProperty(value = "ツール5数量")
    private String productNum5;
    @ApiModelProperty(value = "ツール6数量")
    private String productNum6;
    @ApiModelProperty(value = "ツール7数量")
    private String productNum7;
    @ApiModelProperty(value = "ツール8数量")
    private String productNum8;
    @ApiModelProperty(value = "ツール9数量")
    private String productNum9;
    @ApiModelProperty(value = "ツール10数量")
    private String productNum10;
    @ApiModelProperty(value = "同梱有無")
    private String bundledFlg;
    @ApiModelProperty(value = "同梱先遠しNo")
    private String bundledCommonNo;
    @ApiModelProperty(value = "郵便番号")
    private String zip;
    @ApiModelProperty(value = "住所1")
    private String address;
    @ApiModelProperty(value = "住所2")
    private String address1;
    @ApiModelProperty(value = "住所3")
    private String address2;
    @ApiModelProperty(value = "名前１（部課名等）")
    private String name1;
    @ApiModelProperty(value = "名前２（担当者名等）")
    private String name2;
    @ApiModelProperty(value = "電話番号")
    private String phone;
    @ApiModelProperty(value = "配達指定日")
    private String specifiedDeliveryDate;
    @ApiModelProperty(value = "配達時間帯")
    private String deliveryTimeZone;
    @ApiModelProperty(value = "品名1")
    private String label_note1;
    @ApiModelProperty(value = "品名2")
    private String label_note2;
    @ApiModelProperty(value = "品名3")
    private String label_note3;
    @ApiModelProperty(value = "品名4")
    private String label_note4;
    @ApiModelProperty(value = "品名5")
    private String label_note5;
    @ApiModelProperty(value = "个口数")
    private Integer boxes;

    public String getCommonNo() {
        return commonNo;
    }

    public void setCommonNo(String commonNo) {
        this.commonNo = commonNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductNum1() {
        return productNum1;
    }

    public void setProductNum1(String productNum1) {
        this.productNum1 = productNum1;
    }

    public String getProductNum2() {
        return productNum2;
    }

    public void setProductNum2(String productNum2) {
        this.productNum2 = productNum2;
    }

    public String getProductNum3() {
        return productNum3;
    }

    public void setProductNum3(String productNum3) {
        this.productNum3 = productNum3;
    }

    public String getProductNum4() {
        return productNum4;
    }

    public void setProductNum4(String productNum4) {
        this.productNum4 = productNum4;
    }

    public String getProductNum5() {
        return productNum5;
    }

    public void setProductNum5(String productNum5) {
        this.productNum5 = productNum5;
    }

    public String getProductNum6() {
        return productNum6;
    }

    public void setProductNum6(String productNum6) {
        this.productNum6 = productNum6;
    }

    public String getProductNum7() {
        return productNum7;
    }

    public void setProductNum7(String productNum7) {
        this.productNum7 = productNum7;
    }

    public String getProductNum8() {
        return productNum8;
    }

    public void setProductNum8(String productNum8) {
        this.productNum8 = productNum8;
    }

    public String getProductNum9() {
        return productNum9;
    }

    public void setProductNum9(String productNum9) {
        this.productNum9 = productNum9;
    }

    public String getProductNum10() {
        return productNum10;
    }

    public void setProductNum10(String productNum10) {
        this.productNum10 = productNum10;
    }

    public String getBundledFlg() {
        return bundledFlg;
    }

    public void setBundledFlg(String bundledFlg) {
        this.bundledFlg = bundledFlg;
    }

    public String getBundledCommonNo() {
        return bundledCommonNo;
    }

    public void setBundledCommonNo(String bundledCommonNo) {
        this.bundledCommonNo = bundledCommonNo;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecifiedDeliveryDate() {
        return specifiedDeliveryDate;
    }

    public void setSpecifiedDeliveryDate(String specifiedDeliveryDate) {
        this.specifiedDeliveryDate = specifiedDeliveryDate;
    }

    public String getDeliveryTimeZone() {
        return deliveryTimeZone;
    }

    public void setDeliveryTimeZone(String deliveryTimeZone) {
        this.deliveryTimeZone = deliveryTimeZone;
    }

    public String getLabel_note1() {
        return label_note1;
    }

    public void setLabel_note1(String label_note1) {
        this.label_note1 = label_note1;
    }

    public String getLabel_note2() {
        return label_note2;
    }

    public void setLabel_note2(String label_note2) {
        this.label_note2 = label_note2;
    }

    public String getLabel_note3() {
        return label_note3;
    }

    public void setLabel_note3(String label_note3) {
        this.label_note3 = label_note3;
    }

    public String getLabel_note4() {
        return label_note4;
    }

    public void setLabel_note4(String label_note4) {
        this.label_note4 = label_note4;
    }

    public String getLabel_note5() {
        return label_note5;
    }

    public void setLabel_note5(String label_note5) {
        this.label_note5 = label_note5;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public void setBoxes(Integer boxes) {
        this.boxes = boxes;
    }

    @Override
    public String toString() {
        return "Nt143_excel_order{" +
            "commonNo='" + commonNo + '\'' +
            ", email='" + email + '\'' +
            ", shopName='" + shopName + '\'' +
            ", productNum1='" + productNum1 + '\'' +
            ", productNum2='" + productNum2 + '\'' +
            ", productNum3='" + productNum3 + '\'' +
            ", productNum4='" + productNum4 + '\'' +
            ", productNum5='" + productNum5 + '\'' +
            ", productNum6='" + productNum6 + '\'' +
            ", productNum7='" + productNum7 + '\'' +
            ", productNum8='" + productNum8 + '\'' +
            ", productNum9='" + productNum9 + '\'' +
            ", productNum10='" + productNum10 + '\'' +
            ", bundledFlg='" + bundledFlg + '\'' +
            ", bundledCommonNo='" + bundledCommonNo + '\'' +
            ", zip='" + zip + '\'' +
            ", address='" + address + '\'' +
            ", address1='" + address1 + '\'' +
            ", address2='" + address2 + '\'' +
            ", name1='" + name1 + '\'' +
            ", name2='" + name2 + '\'' +
            ", phone='" + phone + '\'' +
            ", specifiedDeliveryDate='" + specifiedDeliveryDate + '\'' +
            ", deliveryTimeZone='" + deliveryTimeZone + '\'' +
            ", label_note1='" + label_note1 + '\'' +
            ", label_note2='" + label_note2 + '\'' +
            ", label_note3='" + label_note3 + '\'' +
            ", label_note4='" + label_note4 + '\'' +
            ", label_note5='" + label_note5 + '\'' +
            ", boxes='" + boxes + '\'' +
            '}';
    }
}
