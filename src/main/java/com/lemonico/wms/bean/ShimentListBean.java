package com.lemonico.wms.bean;



import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.common.bean.Tw201_shipment_detail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @className: tw200_shipment
 * @description: tw200_shipment
 * @date: 2021/06/29
 **/
@ApiModel(value = "tw200_shipment", description = "出庫管理テーブル")
public class ShimentListBean
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "出庫依頼ID", required = true)
    private String shipment_plan_id;
    @ApiModelProperty(value = "出庫依頼日")
    private Timestamp request_date;
    @ApiModelProperty(value = "出庫予定日")
    private Timestamp shipment_plan_date;
    @ApiModelProperty(value = "到着予定日")
    private Timestamp delivery_plan_date;
    @ApiModelProperty(value = "配送先形態")
    private Integer form;
    @ApiModelProperty(value = "郵便番号", required = true)
    private String postcode;
    @ApiModelProperty(value = "氏名")
    private String surname;
    @ApiModelProperty(value = "電話番号")
    private String phone;
    @ApiModelProperty(value = "都道府県", required = true)
    private String prefecture;
    @ApiModelProperty(value = "住所")
    private String address1;
    @ApiModelProperty(value = "マンション・ビル名")
    private String address2;
    @ApiModelProperty(value = "住所3")
    private String address3;
    @ApiModelProperty(value = "会社名")
    private String company;
    @ApiModelProperty(value = "部署")
    private String division;
    @ApiModelProperty(value = "メールアドレス")
    private String email;
    @ApiModelProperty(value = "ご依頼主ID")
    private String sponsor_id;
    @ApiModelProperty(value = "出庫ステータス")
    private Integer shipment_status;
    @ApiModelProperty(value = "ステータス理由")
    private String status_message;
    @ApiModelProperty(value = "出庫ステータス件数")
    private Integer status_count;
    @ApiModelProperty(value = "検品タイプ")
    private String inspection_type;
    @ApiModelProperty(value = "出庫識別番号")
    private String identifier;
    @ApiModelProperty(value = "伝票番号")
    private String delivery_tracking_nm;
    @ApiModelProperty(value = "注文番号")
    private String order_no;
    @ApiModelProperty(value = "出庫依頼商品種類数")
    private Integer product_kind_plan_cnt;
    @ApiModelProperty(value = "出庫依頼商品数計")
    private Integer product_plan_total;
    @ApiModelProperty(value = "商品合計金額")
    private Long total_price;
    @ApiModelProperty(value = "納品書 小計")
    private String subtotal_amount;
    @ApiModelProperty(value = "納品書 配送料")
    private Integer delivery_charge;
    @ApiModelProperty(value = "納品書 手数料")
    private Integer handling_charge;
    @ApiModelProperty(value = "納品書 割引額")
    private Integer discount_amount;
    @ApiModelProperty(value = "納品書 合計")
    private String total_amount;
    @ApiModelProperty(value = "緩衝材単位")
    private String cushioning_unit;
    @ApiModelProperty(value = "緩衝材種別")
    private String cushioning_type;
    @ApiModelProperty(value = "ギフトラッピング単位")
    private String gift_wrapping_unit;
    @ApiModelProperty(value = "ギフトラッピングタイプ")
    private String gift_wrapping_type;
    @ApiModelProperty(value = "ギフト贈り主氏名")
    private String gift_sender_name;
    @ApiModelProperty(value = "同梱物の商品ID or 商品コード")
    private String bundled_items;
    @ApiModelProperty(value = "配送先連絡メール")
    private String shipping_email;
    @ApiModelProperty(value = "明細書の同梱設定")
    private String delivery_note_type;
    @ApiModelProperty(value = "明細書への金額印字指定")
    private Integer price_on_delivery_note;
    @ApiModelProperty(value = "明細書メッセージ")
    private String message;
    @ApiModelProperty(value = "品名")
    private String label_note;
    @ApiModelProperty(value = "出荷希望日")
    private Timestamp shipping_date;
    @ApiModelProperty(value = "税")
    private Long tax;
    @ApiModelProperty(value = "個口数")
    private Integer boxes;
    @ApiModelProperty(value = "総計通常税率")
    private Integer total_with_normal_tax;
    @ApiModelProperty(value = "総計軽減税率")
    private Integer total_with_reduced_tax;
    @ApiModelProperty(value = "配送会社")
    private String delivery_carrier;
    @ApiModelProperty(value = "配達希望時間帯")
    private String delivery_time_slot;
    @ApiModelProperty(value = "配達希望日")
    private Timestamp delivery_date;
    @ApiModelProperty(value = "代金引換指定")
    private Integer cash_on_delivery;
    @ApiModelProperty(value = "代金引換総計")
    private Integer total_for_cash_on_delivery;
    @ApiModelProperty(value = "代金引換消費税")
    private Integer tax_for_cash_on_delivery;
    @ApiModelProperty(value = "配送便指定")
    private String delivery_method;
    @ApiModelProperty(value = "不在時宅配ボックス")
    private Integer box_delivery;
    @ApiModelProperty(value = "割れ物注意")
    private Integer fragile_item;
    @ApiModelProperty(value = "送り状特記事項")
    private String invoice_special_notes;
    @ApiModelProperty(value = "出荷指示書特記事項")
    private String instructions_special_notes;
    @ApiModelProperty(value = "海外発送指定")
    private Integer international;
    @ApiModelProperty(value = "海外発送用配送サービス")
    private String delivery_service;
    @ApiModelProperty(value = "海外発送用通貨コード")
    private String currency_code;
    @ApiModelProperty(value = "PDF名")
    private String pdf_name;
    @ApiModelProperty(value = "PDF確認画像")
    private String pdf_confirm_img;
    @ApiModelProperty(value = "海外発送用損害保証制度の加入希望")
    private Integer insurance;
    @ApiModelProperty(value = "備考1")
    private String file;
    @ApiModelProperty(value = "添付ファイル2")
    private String file2;
    @ApiModelProperty(value = "添付ファイル3")
    private String file3;
    @ApiModelProperty(value = "添付ファイル4")
    private String file4;
    @ApiModelProperty(value = "添付ファイル5")
    private String file5;
    @ApiModelProperty(value = "店舗表示名")
    private String client_nm;
    @ApiModelProperty(value = "商品サイズコード")
    private String size_cd;
    @ApiModelProperty(value = "依頼主マスタ")
    private Ms012_sponsor_master ms012_sponsor_master;
    @ApiModelProperty(value = "出荷サイズ")
    private String size_name;
    @ApiModelProperty(value = "担当者名")
    private String tnnm;
    @ApiModelProperty(value = "ご請求顧客コード")
    private String bill_customer_cd;
    @ApiModelProperty(value = "ご請求先分類コード")
    private String yamato_manage_code;
    @ApiModelProperty(value = "運賃管理番号")
    private String fare_manage_cd;
    @ApiModelProperty(value = "荷姿コード")
    private String sagawa_nisugata_code;
    @ApiModelProperty(value = "")
    private String warehouse_nm;
    @ApiModelProperty(value = "配送業者名称")
    private String delivery_nm;
    @ApiModelProperty(value = "お客様コード")
    private String customer_code;
    @ApiModelProperty(value = "荷送人コード")
    private String delivery_code;
    @ApiModelProperty(value = "運輸会社编号")
    private String delivery_code_csv;
    @ApiModelProperty(value = "伝票番号集合")
    private List<String> delivery_tracking_nm_list;
    @ApiModelProperty(value = "添付ファイル名称")
    private String fileName;
    @ApiModelProperty(value = "配送業者ID")
    private String delivery_id;
    @ApiModelProperty(value = "外部受注番号")
    private String outer_order_no;
    @ApiModelProperty(value = "支払方法")
    private String payment_method;
    @ApiModelProperty(value = "受注取消flg")
    private Integer cancelFlg;
    @ApiModelProperty(value = "撮影flg")
    private Integer photography_flg;
    @ApiModelProperty(value = "お届け希望時間帯")
    private String delivery_time_name;
    @ApiModelProperty(value = "請求code")
    private String bill_barcode;
    @ApiModelProperty(value = "取引ID")
    private String payment_id;
    @ApiModelProperty(value = "注文者会社名")
    private String order_company;
    @ApiModelProperty(value = "注文者部署")
    private String order_division;
    @ApiModelProperty(value = "注文者郵便番号1")
    private String order_zip_code1;
    @ApiModelProperty(value = "注文者郵便番号2")
    private String order_zip_code2;
    @ApiModelProperty(value = "注文者住所都道府県")
    private String order_todoufuken;
    @ApiModelProperty(value = "注文者住所郡市区")
    private String order_address1;
    @ApiModelProperty(value = "注文者詳細住所")
    private String order_address2;
    @ApiModelProperty(value = "注文者姓")
    private String order_family_name;
    @ApiModelProperty(value = "注文者名")
    private String order_first_name;
    @ApiModelProperty(value = "注文者姓カナ")
    private String order_family_kana;
    @ApiModelProperty(value = "注文者名カナ")
    private String order_first_kana;
    @ApiModelProperty(value = "注文者電話番号1")
    private String order_phone_number1;
    @ApiModelProperty(value = "注文者電話番号2")
    private String order_phone_number2;
    @ApiModelProperty(value = "注文者電話番号3")
    private String order_phone_number3;
    @ApiModelProperty(value = "注文者メールアドレス")
    private String order_mail;
    @ApiModelProperty(value = "注文者性別")
    private Integer order_gender;
    @ApiModelProperty(value = "配送会社")
    private String delivery_method_name;
    @ApiModelProperty(value = "注文日時")
    private Timestamp order_datetime;
    @ApiModelProperty(value = "出庫依頼明细")
    private List<Tw201_shipment_detail> tw201_shipment_detail;
    @ApiModelProperty(value = "同捆物件数")
    private Integer bundled_cnt;
    @ApiModelProperty(value = "納品書同梱指示")
    private String orderIdent;
    @ApiModelProperty(value = "西濃運輸荷送人コード")
    private String seino_delivery_code;
    @ApiModelProperty(value = "商品区分标识")
    private Integer kubun;
    @ApiModelProperty(value = "商品合计(税拔)")
    public Integer subtotal_amount_tax;
    @ApiModelProperty(value = "納品書URL")
    private String delivery_url;
    @ApiModelProperty(value = "領収書URL")
    private String receipt_url;
    @ApiModelProperty(value = "定期購入ID")
    private String buy_id;
    @ApiModelProperty(value = "定期購入回数")
    private Integer buy_cnt;
    @ApiModelProperty(value = "次回お届け予定日")
    private Date next_delivery_date;
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
    @ApiModelProperty(value = "備考8")
    private String bikou8;
    @ApiModelProperty(value = "備考9")
    private String bikou9;
    @ApiModelProperty(value = "備考10")
    private String bikou10;
    @ApiModelProperty(value = "メモ")
    private String memo;
    @ApiModelProperty(value = "更新日時")
    private Timestamp upd_date;

    public Timestamp getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Timestamp upd_date) {
        this.upd_date = upd_date;
    }

    public List<Tw201_shipment_detail> getTw201_shipment_detail() {
        return tw201_shipment_detail;
    }

    public void setTw201_shipment_detail(List<Tw201_shipment_detail> tw201_shipment_detail) {
        this.tw201_shipment_detail = tw201_shipment_detail;
    }

    public Integer getBundled_cnt() {
        return bundled_cnt;
    }

    public void setBundled_cnt(Integer bundled_cnt) {
        this.bundled_cnt = bundled_cnt;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
    }

    public Timestamp getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Timestamp request_date) {
        this.request_date = request_date;
    }

    public Timestamp getShipment_plan_date() {
        return shipment_plan_date;
    }

    public void setShipment_plan_date(Timestamp shipment_plan_date) {
        this.shipment_plan_date = shipment_plan_date;
    }

    public Timestamp getDelivery_plan_date() {
        return delivery_plan_date;
    }

    public void setDelivery_plan_date(Timestamp delivery_plan_date) {
        this.delivery_plan_date = delivery_plan_date;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public Integer getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(Integer shipment_status) {
        this.shipment_status = shipment_status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public Integer getStatus_count() {
        return status_count;
    }

    public void setStatus_count(Integer status_count) {
        this.status_count = status_count;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDelivery_tracking_nm() {
        return delivery_tracking_nm;
    }

    public void setDelivery_tracking_nm(String delivery_tracking_nm) {
        this.delivery_tracking_nm = delivery_tracking_nm;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public Integer getProduct_kind_plan_cnt() {
        return product_kind_plan_cnt;
    }

    public void setProduct_kind_plan_cnt(Integer product_kind_plan_cnt) {
        this.product_kind_plan_cnt = product_kind_plan_cnt;
    }

    public Integer getProduct_plan_total() {
        return product_plan_total;
    }

    public void setProduct_plan_total(Integer product_plan_total) {
        this.product_plan_total = product_plan_total;
    }

    public Long getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Long total_price) {
        this.total_price = total_price;
    }

    public String getSubtotal_amount() {
        return subtotal_amount;
    }

    public void setSubtotal_amount(String subtotal_amount) {
        this.subtotal_amount = subtotal_amount;
    }

    public Integer getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(Integer delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public Integer getHandling_charge() {
        return handling_charge;
    }

    public void setHandling_charge(Integer handling_charge) {
        this.handling_charge = handling_charge;
    }

    public Integer getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Integer discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getCushioning_unit() {
        return cushioning_unit;
    }

    public void setCushioning_unit(String cushioning_unit) {
        this.cushioning_unit = cushioning_unit;
    }

    public String getCushioning_type() {
        return cushioning_type;
    }

    public void setCushioning_type(String cushioning_type) {
        this.cushioning_type = cushioning_type;
    }

    public String getGift_wrapping_unit() {
        return gift_wrapping_unit;
    }

    public void setGift_wrapping_unit(String gift_wrapping_unit) {
        this.gift_wrapping_unit = gift_wrapping_unit;
    }

    public String getGift_wrapping_type() {
        return gift_wrapping_type;
    }

    public void setGift_wrapping_type(String gift_wrapping_type) {
        this.gift_wrapping_type = gift_wrapping_type;
    }

    public String getGift_sender_name() {
        return gift_sender_name;
    }

    public void setGift_sender_name(String gift_sender_name) {
        this.gift_sender_name = gift_sender_name;
    }

    public String getBundled_items() {
        return bundled_items;
    }

    public void setBundled_items(String bundled_items) {
        this.bundled_items = bundled_items;
    }

    public String getShipping_email() {
        return shipping_email;
    }

    public void setShipping_email(String shipping_email) {
        this.shipping_email = shipping_email;
    }

    public String getDelivery_note_type() {
        return delivery_note_type;
    }

    public void setDelivery_note_type(String delivery_note_type) {
        this.delivery_note_type = delivery_note_type;
    }

    public Integer getPrice_on_delivery_note() {
        return price_on_delivery_note;
    }

    public void setPrice_on_delivery_note(Integer price_on_delivery_note) {
        this.price_on_delivery_note = price_on_delivery_note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLabel_note() {
        return label_note;
    }

    public void setLabel_note(String label_note) {
        this.label_note = label_note;
    }

    public Timestamp getShipping_date() {
        return shipping_date;
    }

    public void setShipping_date(Timestamp shipping_date) {
        this.shipping_date = shipping_date;
    }

    public Long getTax() {
        return tax;
    }

    public void setTax(Long tax) {
        this.tax = tax;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public void setBoxes(Integer boxes) {
        this.boxes = boxes;
    }

    public Integer getTotal_with_normal_tax() {
        return total_with_normal_tax;
    }

    public void setTotal_with_normal_tax(Integer total_with_normal_tax) {
        this.total_with_normal_tax = total_with_normal_tax;
    }

    public Integer getTotal_with_reduced_tax() {
        return total_with_reduced_tax;
    }

    public void setTotal_with_reduced_tax(Integer total_with_reduced_tax) {
        this.total_with_reduced_tax = total_with_reduced_tax;
    }

    public String getDelivery_carrier() {
        return delivery_carrier;
    }

    public void setDelivery_carrier(String delivery_carrier) {
        this.delivery_carrier = delivery_carrier;
    }

    public String getDelivery_time_slot() {
        return delivery_time_slot;
    }

    public void setDelivery_time_slot(String delivery_time_slot) {
        this.delivery_time_slot = delivery_time_slot;
    }

    public Timestamp getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(Timestamp delivery_date) {
        this.delivery_date = delivery_date;
    }

    public Integer getCash_on_delivery() {
        return cash_on_delivery;
    }

    public void setCash_on_delivery(Integer cash_on_delivery) {
        this.cash_on_delivery = cash_on_delivery;
    }

    public Integer getTotal_for_cash_on_delivery() {
        return total_for_cash_on_delivery;
    }

    public void setTotal_for_cash_on_delivery(Integer total_for_cash_on_delivery) {
        this.total_for_cash_on_delivery = total_for_cash_on_delivery;
    }

    public Integer getTax_for_cash_on_delivery() {
        return tax_for_cash_on_delivery;
    }

    public void setTax_for_cash_on_delivery(Integer tax_for_cash_on_delivery) {
        this.tax_for_cash_on_delivery = tax_for_cash_on_delivery;
    }

    public String getDelivery_method() {
        return delivery_method;
    }

    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

    public Integer getBox_delivery() {
        return box_delivery;
    }

    public void setBox_delivery(Integer box_delivery) {
        this.box_delivery = box_delivery;
    }

    public Integer getFragile_item() {
        return fragile_item;
    }

    public void setFragile_item(Integer fragile_item) {
        this.fragile_item = fragile_item;
    }

    public String getInvoice_special_notes() {
        return invoice_special_notes;
    }

    public void setInvoice_special_notes(String invoice_special_notes) {
        this.invoice_special_notes = invoice_special_notes;
    }

    public String getInstructions_special_notes() {
        return instructions_special_notes;
    }

    public void setInstructions_special_notes(String instructions_special_notes) {
        this.instructions_special_notes = instructions_special_notes;
    }

    public Integer getInternational() {
        return international;
    }

    public void setInternational(Integer international) {
        this.international = international;
    }

    public String getDelivery_service() {
        return delivery_service;
    }

    public void setDelivery_service(String delivery_service) {
        this.delivery_service = delivery_service;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getPdf_name() {
        return pdf_name;
    }

    public void setPdf_name(String pdf_name) {
        this.pdf_name = pdf_name;
    }

    public String getPdf_confirm_img() {
        return pdf_confirm_img;
    }

    public void setPdf_confirm_img(String pdf_confirm_img) {
        this.pdf_confirm_img = pdf_confirm_img;
    }

    public Integer getInsurance() {
        return insurance;
    }

    public void setInsurance(Integer insurance) {
        this.insurance = insurance;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getFile3() {
        return file3;
    }

    public void setFile3(String file3) {
        this.file3 = file3;
    }

    public String getFile4() {
        return file4;
    }

    public void setFile4(String file4) {
        this.file4 = file4;
    }

    public String getFile5() {
        return file5;
    }

    public void setFile5(String file5) {
        this.file5 = file5;
    }

    public String getClient_nm() {
        return client_nm;
    }

    public void setClient_nm(String client_nm) {
        this.client_nm = client_nm;
    }

    public String getSize_cd() {
        return size_cd;
    }

    public void setSize_cd(String size_cd) {
        this.size_cd = size_cd;
    }

    public Ms012_sponsor_master getMs012_sponsor_master() {
        return ms012_sponsor_master;
    }

    public void setMs012_sponsor_master(Ms012_sponsor_master ms012_sponsor_master) {
        this.ms012_sponsor_master = ms012_sponsor_master;
    }

    public String getSize_name() {
        return size_name;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    public String getTnnm() {
        return tnnm;
    }

    public void setTnnm(String tnnm) {
        this.tnnm = tnnm;
    }

    public String getBill_customer_cd() {
        return bill_customer_cd;
    }

    public void setBill_customer_cd(String bill_customer_cd) {
        this.bill_customer_cd = bill_customer_cd;
    }

    public String getYamato_manage_code() {
        return yamato_manage_code;
    }

    public void setYamato_manage_code(String yamato_manage_code) {
        this.yamato_manage_code = yamato_manage_code;
    }

    public String getFare_manage_cd() {
        return fare_manage_cd;
    }

    public void setFare_manage_cd(String fare_manage_cd) {
        this.fare_manage_cd = fare_manage_cd;
    }

    public String getSagawa_nisugata_code() {
        return sagawa_nisugata_code;
    }

    public void setSagawa_nisugata_code(String sagawa_nisugata_code) {
        this.sagawa_nisugata_code = sagawa_nisugata_code;
    }

    public String getWarehouse_nm() {
        return warehouse_nm;
    }

    public void setWarehouse_nm(String warehouse_nm) {
        this.warehouse_nm = warehouse_nm;
    }

    public String getDelivery_nm() {
        return delivery_nm;
    }

    public void setDelivery_nm(String delivery_nm) {
        this.delivery_nm = delivery_nm;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(String delivery_code) {
        this.delivery_code = delivery_code;
    }

    public String getDelivery_code_csv() {
        return delivery_code_csv;
    }

    public void setDelivery_code_csv(String delivery_code_csv) {
        this.delivery_code_csv = delivery_code_csv;
    }

    public List<String> getDelivery_tracking_nm_list() {
        return delivery_tracking_nm_list;
    }

    public void setDelivery_tracking_nm_list(List<String> delivery_tracking_nm_list) {
        this.delivery_tracking_nm_list = delivery_tracking_nm_list;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getOuter_order_no() {
        return outer_order_no;
    }

    public void setOuter_order_no(String outer_order_no) {
        this.outer_order_no = outer_order_no;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Integer getCancelFlg() {
        return cancelFlg;
    }

    public void setCancelFlg(Integer cancelFlg) {
        this.cancelFlg = cancelFlg;
    }

    public Integer getPhotography_flg() {
        return photography_flg;
    }

    public void setPhotography_flg(Integer photography_flg) {
        this.photography_flg = photography_flg;
    }

    public String getDelivery_time_name() {
        return delivery_time_name;
    }

    public void setDelivery_time_name(String delivery_time_name) {
        this.delivery_time_name = delivery_time_name;
    }

    public String getBill_barcode() {
        return bill_barcode;
    }

    public void setBill_barcode(String bill_barcode) {
        this.bill_barcode = bill_barcode;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getOrder_company() {
        return order_company;
    }

    public void setOrder_company(String order_company) {
        this.order_company = order_company;
    }

    public String getOrder_division() {
        return order_division;
    }

    public void setOrder_division(String order_division) {
        this.order_division = order_division;
    }

    public String getOrder_zip_code1() {
        return order_zip_code1;
    }

    public void setOrder_zip_code1(String order_zip_code1) {
        this.order_zip_code1 = order_zip_code1;
    }

    public String getOrder_zip_code2() {
        return order_zip_code2;
    }

    public void setOrder_zip_code2(String order_zip_code2) {
        this.order_zip_code2 = order_zip_code2;
    }

    public String getOrder_todoufuken() {
        return order_todoufuken;
    }

    public void setOrder_todoufuken(String order_todoufuken) {
        this.order_todoufuken = order_todoufuken;
    }

    public String getOrder_address1() {
        return order_address1;
    }

    public void setOrder_address1(String order_address1) {
        this.order_address1 = order_address1;
    }

    public String getOrder_address2() {
        return order_address2;
    }

    public void setOrder_address2(String order_address2) {
        this.order_address2 = order_address2;
    }

    public String getOrder_family_name() {
        return order_family_name;
    }

    public void setOrder_family_name(String order_family_name) {
        this.order_family_name = order_family_name;
    }

    public String getOrder_first_name() {
        return order_first_name;
    }

    public void setOrder_first_name(String order_first_name) {
        this.order_first_name = order_first_name;
    }

    public String getOrder_family_kana() {
        return order_family_kana;
    }

    public void setOrder_family_kana(String order_family_kana) {
        this.order_family_kana = order_family_kana;
    }

    public String getOrder_first_kana() {
        return order_first_kana;
    }

    public void setOrder_first_kana(String order_first_kana) {
        this.order_first_kana = order_first_kana;
    }

    public String getOrder_phone_number1() {
        return order_phone_number1;
    }

    public void setOrder_phone_number1(String order_phone_number1) {
        this.order_phone_number1 = order_phone_number1;
    }

    public String getOrder_phone_number2() {
        return order_phone_number2;
    }

    public void setOrder_phone_number2(String order_phone_number2) {
        this.order_phone_number2 = order_phone_number2;
    }

    public String getOrder_phone_number3() {
        return order_phone_number3;
    }

    public void setOrder_phone_number3(String order_phone_number3) {
        this.order_phone_number3 = order_phone_number3;
    }

    public String getOrder_mail() {
        return order_mail;
    }

    public void setOrder_mail(String order_mail) {
        this.order_mail = order_mail;
    }

    public Integer getOrder_gender() {
        return order_gender;
    }

    public void setOrder_gender(Integer order_gender) {
        this.order_gender = order_gender;
    }

    public String getDelivery_method_name() {
        return delivery_method_name;
    }

    public void setDelivery_method_name(String delivery_method_name) {
        this.delivery_method_name = delivery_method_name;
    }

    public Timestamp getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(Timestamp order_datetime) {
        this.order_datetime = order_datetime;
    }

    public String getOrderIdent() {
        return orderIdent;
    }

    public void setOrderIdent(String orderIdent) {
        this.orderIdent = orderIdent;
    }

    public String getSeino_delivery_code() {
        return seino_delivery_code;
    }

    public void setSeino_delivery_code(String seino_delivery_code) {
        this.seino_delivery_code = seino_delivery_code;
    }

    public Integer getKubun() {
        return kubun;
    }

    public void setKubun(Integer kubun) {
        this.kubun = kubun;
    }

    public Integer getSubtotal_amount_tax() {
        return subtotal_amount_tax;
    }

    public void setSubtotal_amount_tax(Integer subtotal_amount_tax) {
        this.subtotal_amount_tax = subtotal_amount_tax;
    }

    public String getDelivery_url() {
        return delivery_url;
    }

    public void setDelivery_url(String delivery_url) {
        this.delivery_url = delivery_url;
    }

    public String getReceipt_url() {
        return receipt_url;
    }

    public void setReceipt_url(String receipt_url) {
        this.receipt_url = receipt_url;
    }

    public String getBuy_id() {
        return buy_id;
    }

    public void setBuy_id(String buy_id) {
        this.buy_id = buy_id;
    }

    public Integer getBuy_cnt() {
        return buy_cnt;
    }

    public void setBuy_cnt(Integer buy_cnt) {
        this.buy_cnt = buy_cnt;
    }

    public Date getNext_delivery_date() {
        return next_delivery_date;
    }

    public void setNext_delivery_date(Date next_delivery_date) {
        this.next_delivery_date = next_delivery_date;
    }

    public String getBikou1() {
        return bikou1;
    }

    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    public String getBikou2() {
        return bikou2;
    }

    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    public String getBikou3() {
        return bikou3;
    }

    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
    }

    public String getBikou4() {
        return bikou4;
    }

    public void setBikou4(String bikou4) {
        this.bikou4 = bikou4;
    }

    public String getBikou5() {
        return bikou5;
    }

    public void setBikou5(String bikou5) {
        this.bikou5 = bikou5;
    }

    public String getBikou6() {
        return bikou6;
    }

    public void setBikou6(String bikou6) {
        this.bikou6 = bikou6;
    }

    public String getBikou7() {
        return bikou7;
    }

    public void setBikou7(String bikou7) {
        this.bikou7 = bikou7;
    }

    public String getBikou8() {
        return bikou8;
    }

    public void setBikou8(String bikou8) {
        this.bikou8 = bikou8;
    }

    public String getBikou9() {
        return bikou9;
    }

    public void setBikou9(String bikou9) {
        this.bikou9 = bikou9;
    }

    public String getBikou10() {
        return bikou10;
    }

    public void setBikou10(String bikou10) {
        this.bikou10 = bikou10;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
