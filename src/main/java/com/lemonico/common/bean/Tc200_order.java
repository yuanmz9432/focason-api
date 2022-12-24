package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @description 受注管理テーブル・Beanクラス
 *
 * @date 2020/06/18
 * @version 1.0
 **/
@ApiModel(value = "tc200_order", description = "受注管理テーブル")
public class Tc200_order
{
    @ApiModelProperty(value = "受注番号", required = true)
    private String purchase_order_no;
    @ApiModelProperty(value = "倉庫管理番号", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客管理番号", required = true)
    private String client_id;
    @ApiModelProperty(value = "外部受注番号", required = true)
    private String outer_order_no;
    @ApiModelProperty(value = "受注取込履歴番号")
    private String history_id;
    @ApiModelProperty(value = "外部注文ステータス")
    private Integer outer_order_status;
    @ApiModelProperty(value = "注文日時")
    private Date order_datetime;
    @ApiModelProperty(value = "支払方法")
    private String payment_method;
    @ApiModelProperty(value = "注文種別")
    private Integer order_type;
    @ApiModelProperty(value = "会員情報")
    private String member_info;
    @ApiModelProperty(value = "商品税抜金額")
    private Integer product_price_excluding_tax;
    @ApiModelProperty(value = "消費税合計")
    private Integer tax_total;
    @ApiModelProperty(value = "代引料")
    private Integer cash_on_delivery_fee;
    @ApiModelProperty(value = "その他金額")
    private Integer other_fee;
    @ApiModelProperty(value = "送料合計")
    private Integer delivery_total;
    @ApiModelProperty(value = "合計請求金額", required = true)
    private Integer billing_total;
    @ApiModelProperty(value = "注文者依頼")
    private Integer order_flag;
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
    @ApiModelProperty(value = "配送先会社名")
    private String receiver_company;
    @ApiModelProperty(value = "配送先部署")
    private String receiver_division;
    @ApiModelProperty(value = "配送先郵便番号1", required = true)
    private String receiver_zip_code1;
    @ApiModelProperty(value = "配送先郵便番号2")
    private String receiver_zip_code2;
    @ApiModelProperty(value = "配送先住所都道府県", required = true)
    private String receiver_todoufuken;
    @ApiModelProperty(value = "配送先住所郡市区", required = true)
    private String receiver_address1;
    @ApiModelProperty(value = "配送先詳細住所")
    private String receiver_address2;
    @ApiModelProperty(value = "配送先姓", required = true)
    private String receiver_family_name;
    @ApiModelProperty(value = "配送先名")
    private String receiver_first_name;
    @ApiModelProperty(value = "配送先姓カナ")
    private String receiver_family_kana;
    @ApiModelProperty(value = "配送先名カナ")
    private String receiver_first_kana;
    @ApiModelProperty(value = "配送先電話番号1")
    private String receiver_phone_number1;
    @ApiModelProperty(value = "配送先電話番号2")
    private String receiver_phone_number2;
    @ApiModelProperty(value = "配送先電話番号3")
    private String receiver_phone_number3;
    @ApiModelProperty(value = "配送先メールアドレス")
    private String receiver_mail;
    @ApiModelProperty(value = "配送先性別")
    private Integer receiver_gender;
    @ApiModelProperty(value = "配達担当者")
    private String deliveryman;
    @ApiModelProperty(value = "配達希望時間帯")
    private String delivery_time_slot;
    @ApiModelProperty(value = "配達希望日")
    private Date delivery_date;
    @ApiModelProperty(value = "出庫依頼ID")
    private String shipment_plan_id;
    @ApiModelProperty(value = "取込日時", required = true)
    private Timestamp import_datetime;
    @ApiModelProperty(value = "出庫依頼日時")
    private Timestamp shipping_request_datetime;
    @ApiModelProperty(value = "出荷希望日")
    private Date shipment_wish_date;
    @ApiModelProperty(value = "出庫予定日")
    private Date shipment_plan_date;
    @ApiModelProperty(value = "配送便指定")
    private String delivery_type;
    @ApiModelProperty(value = "配送方法")
    private String delivery_method;
    @ApiModelProperty(value = "配送会社指定")
    private String delivery_company;
    @ApiModelProperty(value = "受取方法希望")
    private String receiver_wish_method;
    @ApiModelProperty(value = "ギフト配送希望")
    private String gift_wish;
    @ApiModelProperty(value = "明細同梱設定")
    private String detail_bundled;
    @ApiModelProperty(value = "明細書金額印字")
    private String detail_price_print;
    @ApiModelProperty(value = "明細書メッセージ")
    private String detail_message;
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
    @ApiModelProperty(value = "備考内容显示flg")
    private Integer bikou_flg;
    @ApiModelProperty(value = "手数料")
    private Integer handling_charge;
    @ApiModelProperty(value = "注文者ID(依頼主ID)")
    private String sponsor_id;
    @ApiModelProperty(value = "品名")
    private String label_note;
    @ApiModelProperty(value = "不在時宅配ボックス")
    private Integer box_delivery;
    @ApiModelProperty(value = "割れ物注意")
    private Integer fragile_item;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Timestamp ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Timestamp upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "法人／個人")
    private Integer form;
    @ApiModelProperty(value = "个口数")
    private Integer boxes;
    @ApiModelProperty(value = "配送会社ID")
    private String delivery_id;
    @ApiModelProperty(value = "消費税合計10%")
    private Integer total_with_normal_tax;
    @ApiModelProperty(value = "消費税合計8%")
    private Integer total_with_reduced_tax;
    @ApiModelProperty(value = "請求コード")
    private String bill_barcode;
    @ApiModelProperty(value = "取引ID")
    private String payment_id;
    @ApiModelProperty(value = "定期購入ID")
    private String buy_id;
    @ApiModelProperty(value = "定期購入回数")
    private Integer buy_cnt;
    @ApiModelProperty(value = "次回お届け予定日")
    private Date next_delivery_date;
    @ApiModelProperty(value = "メモ")
    private String memo;
    @ApiModelProperty(value = "送り状特記事項")
    private String invoice_special_notes;
    @ApiModelProperty(value = "Qoo10関連注文番号")
    private String related_order_no;

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public Integer getBoxes() {
        return boxes;
    }

    public void setBoxes(Integer boxes) {
        this.boxes = boxes;
    }

    private List<Tc201_order_detail> tc201_order_detail_list;

    /**
     * コンストラクタ
     */
    public Tc200_order() {
        super();
    }

    public String getLabel_note() {
        return label_note;
    }

    public Integer getBox_delivery() {
        return box_delivery;
    }

    public Integer getFragile_item() {
        return fragile_item;
    }

    public void setLabel_note(String label_note) {
        this.label_note = label_note;
    }

    public void setBox_delivery(Integer box_delivery) {
        this.box_delivery = box_delivery;
    }

    public void setFragile_item(Integer fragile_item) {
        this.fragile_item = fragile_item;
    }

    public Integer getOrder_flag() {
        return order_flag;
    }

    public String getOrder_company() {
        return order_company;
    }

    public String getOrder_division() {
        return order_division;
    }

    public String getReceiver_company() {
        return receiver_company;
    }

    public String getReceiver_division() {
        return receiver_division;
    }

    public void setOrder_flag(Integer order_flag) {
        this.order_flag = order_flag;
    }

    public void setOrder_company(String order_company) {
        this.order_company = order_company;
    }

    public void setOrder_division(String order_division) {
        this.order_division = order_division;
    }

    public void setReceiver_company(String receiver_company) {
        this.receiver_company = receiver_company;
    }

    public void setReceiver_division(String receiver_division) {
        this.receiver_division = receiver_division;
    }

    /**
     * @return purchase_order_no
     */
    public String getPurchase_order_no() {
        return purchase_order_no;
    }

    /**
     * @param purchase_order_no セットする
     */
    public void setPurchase_order_no(String purchase_order_no) {
        this.purchase_order_no = purchase_order_no;
    }

    /**
     * @return warehouse_cd
     */
    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    /**
     * @param warehouse_cd セットする
     */
    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    /**
     * @return client_id
     */
    public String getClient_id() {
        return client_id;
    }

    /**
     * @param client_id セットする
     */
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    /**
     * @return outer_order_no
     */
    public String getOuter_order_no() {
        return outer_order_no;
    }

    /**
     * @param outer_order_no セットする
     */
    public void setOuter_order_no(String outer_order_no) {
        this.outer_order_no = outer_order_no;
    }

    /**
     * @return history_id
     */
    public String getHistory_id() {
        return history_id;
    }

    /**
     * @param history_id セットする history_id
     */
    public void setHistory_id(String history_id) {
        this.history_id = history_id;
    }

    /**
     * @return outer_order_status
     */
    public Integer getOuter_order_status() {
        return outer_order_status;
    }

    /**
     * @param outer_order_status セットする
     */
    public void setOuter_order_status(Integer outer_order_status) {
        this.outer_order_status = outer_order_status;
    }

    /**
     * @return order_datetime
     */
    public Date getOrder_datetime() {
        return order_datetime;
    }

    /**
     * @param order_datetime セットする
     */
    public void setOrder_datetime(Date order_datetime) {
        this.order_datetime = order_datetime;
    }

    /**
     * @return payment_method
     */
    public String getPayment_method() {
        return payment_method;
    }

    /**
     * @param payment_method セットする
     */
    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    /**
     * @return order_type
     */
    public Integer getOrder_type() {
        return order_type;
    }

    /**
     * @param order_type セットする
     */
    public void setOrder_type(Integer order_type) {
        this.order_type = order_type;
    }

    /**
     * @return member_info
     */
    public String getMember_info() {
        return member_info;
    }

    /**
     * @param member_info セットする
     */
    public void setMember_info(String member_info) {
        this.member_info = member_info;
    }

    /**
     * @return product_price_excluding_tax
     */
    public Integer getProduct_price_excluding_tax() {
        return product_price_excluding_tax;
    }

    /**
     * @param product_price_excluding_tax セットする
     */
    public void setProduct_price_excluding_tax(Integer product_price_excluding_tax) {
        this.product_price_excluding_tax = product_price_excluding_tax;
    }

    /**
     * @return tax_total
     */
    public Integer getTax_total() {
        return tax_total;
    }

    /**
     * @param tax_total セットする
     */
    public void setTax_total(Integer tax_total) {
        this.tax_total = tax_total;
    }

    /**
     * @return cash_on_delivery_fee
     */
    public Integer getCash_on_delivery_fee() {
        return cash_on_delivery_fee;
    }

    /**
     * @param cash_on_delivery_fee セットする
     */
    public void setCash_on_delivery_fee(Integer cash_on_delivery_fee) {
        this.cash_on_delivery_fee = cash_on_delivery_fee;
    }

    /**
     * @return other_fee
     */
    public Integer getOther_fee() {
        return other_fee;
    }

    /**
     * @param other_fee セットする
     */
    public void setOther_fee(Integer other_fee) {
        this.other_fee = other_fee;
    }

    /**
     * @return delivery_total
     */
    public Integer getDelivery_total() {
        return delivery_total;
    }

    /**
     * @param delivery_total セットする
     */
    public void setDelivery_total(Integer delivery_total) {
        this.delivery_total = delivery_total;
    }

    /**
     * @return billing_total
     */
    public Integer getBilling_total() {
        return billing_total;
    }

    /**
     * @param billing_total セットする
     */
    public void setBilling_total(Integer billing_total) {
        this.billing_total = billing_total;
    }

    /**
     * @return order_zip_code1
     */
    public String getOrder_zip_code1() {
        return order_zip_code1;
    }

    /**
     * @param order_zip_code1 セットする
     */
    public void setOrder_zip_code1(String order_zip_code1) {
        this.order_zip_code1 = order_zip_code1;
    }

    /**
     * @return order_zip_code2
     */
    public String getOrder_zip_code2() {
        return order_zip_code2;
    }

    /**
     * @param order_zip_code2 セットする
     */
    public void setOrder_zip_code2(String order_zip_code2) {
        this.order_zip_code2 = order_zip_code2;
    }

    /**
     * @return order_todoufuken
     */
    public String getOrder_todoufuken() {
        return order_todoufuken;
    }

    /**
     * @param order_todoufuken セットする
     */
    public void setOrder_todoufuken(String order_todoufuken) {
        this.order_todoufuken = order_todoufuken;
    }

    /**
     * @return order_address1
     */
    public String getOrder_address1() {
        return order_address1;
    }

    /**
     * @param order_address1 セットする
     */
    public void setOrder_address1(String order_address1) {
        this.order_address1 = order_address1;
    }

    /**
     * @return order_address2
     */
    public String getOrder_address2() {
        return order_address2;
    }

    /**
     * @param order_address2 セットする
     */
    public void setOrder_address2(String order_address2) {
        this.order_address2 = order_address2;
    }

    /**
     * @return order_family_name
     */
    public String getOrder_family_name() {
        return order_family_name;
    }

    /**
     * @param order_family_name セットする
     */
    public void setOrder_family_name(String order_family_name) {
        this.order_family_name = order_family_name;
    }

    /**
     * @return order_first_name
     */
    public String getOrder_first_name() {
        return order_first_name;
    }

    /**
     * @param order_first_name セットする
     */
    public void setOrder_first_name(String order_first_name) {
        this.order_first_name = order_first_name;
    }

    /**
     * @return order_family_kana
     */
    public String getOrder_family_kana() {
        return order_family_kana;
    }

    /**
     * @param order_family_kana セットする
     */
    public void setOrder_family_kana(String order_family_kana) {
        this.order_family_kana = order_family_kana;
    }

    /**
     * @return order_first_kana
     */
    public String getOrder_first_kana() {
        return order_first_kana;
    }

    /**
     * @param order_first_kana セットする
     */
    public void setOrder_first_kana(String order_first_kana) {
        this.order_first_kana = order_first_kana;
    }

    /**
     * @return order_phone_number1
     */
    public String getOrder_phone_number1() {
        return order_phone_number1;
    }

    /**
     * @param order_phone_number1 セットする
     */
    public void setOrder_phone_number1(String order_phone_number1) {
        this.order_phone_number1 = order_phone_number1;
    }

    /**
     * @return order_phone_number2
     */
    public String getOrder_phone_number2() {
        return order_phone_number2;
    }

    /**
     * @param order_phone_number2 セットする
     */
    public void setOrder_phone_number2(String order_phone_number2) {
        this.order_phone_number2 = order_phone_number2;
    }

    /**
     * @return order_phone_number3
     */
    public String getOrder_phone_number3() {
        return order_phone_number3;
    }

    /**
     * @param order_phone_number3 セットする
     */
    public void setOrder_phone_number3(String order_phone_number3) {
        this.order_phone_number3 = order_phone_number3;
    }

    /**
     * @return order_mail
     */
    public String getOrder_mail() {
        return order_mail;
    }

    /**
     * @param order_mail セットする
     */
    public void setOrder_mail(String order_mail) {
        this.order_mail = order_mail;
    }

    /**
     * @return order_gender
     */
    public Integer getOrder_gender() {
        return order_gender;
    }

    /**
     * @param order_gender セットする
     */
    public void setOrder_gender(Integer order_gender) {
        this.order_gender = order_gender;
    }

    /**
     * @return receiver_zip_code1
     */
    public String getReceiver_zip_code1() {
        return receiver_zip_code1;
    }

    /**
     * @param receiver_zip_code1 セットする
     */
    public void setReceiver_zip_code1(String receiver_zip_code1) {
        this.receiver_zip_code1 = receiver_zip_code1;
    }

    /**
     * @return receiver_zip_code2
     */
    public String getReceiver_zip_code2() {
        return receiver_zip_code2;
    }

    /**
     * @param receiver_zip_code2 セットする
     */
    public void setReceiver_zip_code2(String receiver_zip_code2) {
        this.receiver_zip_code2 = receiver_zip_code2;
    }

    /**
     * @return receiver_todoufuken
     */
    public String getReceiver_todoufuken() {
        return receiver_todoufuken;
    }

    /**
     * @param receiver_todoufuken セットする
     */
    public void setReceiver_todoufuken(String receiver_todoufuken) {
        this.receiver_todoufuken = receiver_todoufuken;
    }

    /**
     * @return receiver_address1
     */
    public String getReceiver_address1() {
        return receiver_address1;
    }

    /**
     * @param receiver_address1 セットする
     */
    public void setReceiver_address1(String receiver_address1) {
        this.receiver_address1 = receiver_address1;
    }

    /**
     * @return receiver_address2
     */
    public String getReceiver_address2() {
        return receiver_address2;
    }

    /**
     * @param receiver_address2 セットする
     */
    public void setReceiver_address2(String receiver_address2) {
        this.receiver_address2 = receiver_address2;
    }

    /**
     * @return receiver_family_name
     */
    public String getReceiver_family_name() {
        return receiver_family_name;
    }

    /**
     * @param receiver_family_name セットする
     */
    public void setReceiver_family_name(String receiver_family_name) {
        this.receiver_family_name = receiver_family_name;
    }

    /**
     * @return receiver_first_name
     */
    public String getReceiver_first_name() {
        return receiver_first_name;
    }

    /**
     * @param receiver_first_name セットする
     */
    public void setReceiver_first_name(String receiver_first_name) {
        this.receiver_first_name = receiver_first_name;
    }

    /**
     * @return receiver_family_kana
     */
    public String getReceiver_family_kana() {
        return receiver_family_kana;
    }

    /**
     * @param receiver_family_kana セットする
     */
    public void setReceiver_family_kana(String receiver_family_kana) {
        this.receiver_family_kana = receiver_family_kana;
    }

    /**
     * @return receiver_first_kana
     */
    public String getReceiver_first_kana() {
        return receiver_first_kana;
    }

    /**
     * @param receiver_first_kana セットする
     */
    public void setReceiver_first_kana(String receiver_first_kana) {
        this.receiver_first_kana = receiver_first_kana;
    }

    /**
     * @return receiver_phone_number1
     */
    public String getReceiver_phone_number1() {
        return receiver_phone_number1;
    }

    /**
     * @param receiver_phone_number1 セットする
     */
    public void setReceiver_phone_number1(String receiver_phone_number1) {
        this.receiver_phone_number1 = receiver_phone_number1;
    }

    /**
     * @return receiver_phone_number2
     */
    public String getReceiver_phone_number2() {
        return receiver_phone_number2;
    }

    /**
     * @param receiver_phone_number2 セットする
     */
    public void setReceiver_phone_number2(String receiver_phone_number2) {
        this.receiver_phone_number2 = receiver_phone_number2;
    }

    /**
     * @return receiver_phone_number3
     */
    public String getReceiver_phone_number3() {
        return receiver_phone_number3;
    }

    /**
     * @param receiver_phone_number3 セットする
     */
    public void setReceiver_phone_number3(String receiver_phone_number3) {
        this.receiver_phone_number3 = receiver_phone_number3;
    }

    /**
     * @return receiver_mail
     */
    public String getReceiver_mail() {
        return receiver_mail;
    }

    /**
     * @param receiver_mail セットする
     */
    public void setReceiver_mail(String receiver_mail) {
        this.receiver_mail = receiver_mail;
    }

    /**
     * @return receiver_gender
     */
    public Integer getReceiver_gender() {
        return receiver_gender;
    }

    /**
     * @param receiver_gender セットする
     */
    public void setReceiver_gender(Integer receiver_gender) {
        this.receiver_gender = receiver_gender;
    }

    /**
     * @return deliveryman
     */
    public String getDeliveryman() {
        return deliveryman;
    }

    /**
     * @param deliveryman セットする
     */
    public void setDeliveryman(String deliveryman) {
        this.deliveryman = deliveryman;
    }

    /**
     * @return delivery_time_slot
     */
    public String getDelivery_time_slot() {
        return delivery_time_slot;
    }

    /**
     * @param delivery_time_slot セットする
     */
    public void setDelivery_time_slot(String delivery_time_slot) {
        this.delivery_time_slot = delivery_time_slot;
    }

    /**
     * @return delivery_date
     */
    public Date getDelivery_date() {
        return delivery_date;
    }

    /**
     * @param delivery_date セットする
     */
    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    /**
     * @return shipment_plan_id
     */
    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    /**
     * @param shipment_plan_id セットする
     */
    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
    }

    /**
     * @return import_datetime
     */
    public Timestamp getImport_datetime() {
        return import_datetime;
    }

    /**
     * @param import_datetime セットする
     */
    public void setImport_datetime(Timestamp import_datetime) {
        this.import_datetime = import_datetime;
    }

    /**
     * @return shipping_request_datetime
     */
    public Timestamp getShipping_request_datetime() {
        return shipping_request_datetime;
    }

    /**
     * @param shipping_request_datetime セットする
     */
    public void setShipping_request_datetime(Timestamp shipping_request_datetime) {
        this.shipping_request_datetime = shipping_request_datetime;
    }

    /**
     * @return shipment_wish_date
     */
    public Date getShipment_wish_date() {
        return shipment_wish_date;
    }

    /**
     * @param shipment_wish_date セットする
     */
    public void setShipment_wish_date(Date shipment_wish_date) {
        this.shipment_wish_date = shipment_wish_date;
    }

    /**
     * @return shipment_plan_date
     */
    public Date getShipment_plan_date() {
        return shipment_plan_date;
    }

    /**
     * @param shipment_plan_date セットする
     */
    public void setShipment_plan_date(Date shipment_plan_date) {
        this.shipment_plan_date = shipment_plan_date;
    }

    /**
     * @return delivery_type
     */
    public String getDelivery_type() {
        return delivery_type;
    }

    /**
     * @param delivery_type セットする
     */
    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    /**
     * @return delivery_method
     */
    public String getDelivery_method() {
        return delivery_method;
    }

    /**
     * @param delivery_method セットする
     */
    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

    /**
     * @return delivery_company
     */
    public String getDelivery_company() {
        return delivery_company;
    }

    /**
     * @param delivery_company セットする
     */
    public void setDelivery_company(String delivery_company) {
        this.delivery_company = delivery_company;
    }

    /**
     * @return delivery_id
     */
    public String getDelivery_id() {
        return delivery_id;
    }

    /**
     * @param delivery_id セットする
     */
    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    /**
     * @return receiver_wish_method
     */
    public String getReceiver_wish_method() {
        return receiver_wish_method;
    }

    /**
     * @param receiver_wish_method セットする
     */
    public void setReceiver_wish_method(String receiver_wish_method) {
        this.receiver_wish_method = receiver_wish_method;
    }

    /**
     * @return gift_wish
     */
    public String getGift_wish() {
        return gift_wish;
    }

    /**
     * @param gift_wish セットする
     */
    public void setGift_wish(String gift_wish) {
        this.gift_wish = gift_wish;
    }

    /**
     * @return detail_bundled
     */
    public String getDetail_bundled() {
        return detail_bundled;
    }

    /**
     * @param detail_bundled セットする
     */
    public void setDetail_bundled(String detail_bundled) {
        this.detail_bundled = detail_bundled;
    }

    /**
     * @return detail_price_print
     */
    public String getDetail_price_print() {
        return detail_price_print;
    }

    /**
     * @param detail_price_print セットする
     */
    public void setDetail_price_print(String detail_price_print) {
        this.detail_price_print = detail_price_print;
    }

    /**
     * @return detail_message
     */
    public String getDetail_message() {
        return detail_message;
    }

    /**
     * @param detail_message セットする
     */
    public void setDetail_message(String detail_message) {
        this.detail_message = detail_message;
    }

    /**
     * @return bikou1
     */
    public String getBikou1() {
        return bikou1;
    }

    /**
     * @param bikou1 セットする
     */
    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    /**
     * @return bikou2
     */
    public String getBikou2() {
        return bikou2;
    }

    /**
     * @param bikou2 セットする
     */
    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    /**
     * @return bikou3
     */
    public String getBikou3() {
        return bikou3;
    }

    /**
     * @param bikou3 セットする
     */
    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
    }

    /**
     * @return bikou4
     */
    public String getBikou4() {
        return bikou4;
    }

    /**
     * @param bikou4 セットする
     */
    public void setBikou4(String bikou4) {
        this.bikou4 = bikou4;
    }

    /**
     * @return bikou5
     */
    public String getBikou5() {
        return bikou5;
    }

    /**
     * @param bikou5 セットする
     */
    public void setBikou5(String bikou5) {
        this.bikou5 = bikou5;
    }

    /**
     * @return bikou6
     */
    public String getBikou6() {
        return bikou6;
    }

    /**
     * @param bikou6 セットする
     */
    public void setBikou6(String bikou6) {
        this.bikou6 = bikou6;
    }

    /**
     * @return bikou7
     */
    public String getBikou7() {
        return bikou7;
    }

    /**
     * @param bikou7 セットする
     */
    public void setBikou7(String bikou7) {
        this.bikou7 = bikou7;
    }

    /**
     * @return bikou8
     */
    public String getBikou8() {
        return bikou8;
    }

    /**
     * @param bikou8 セットする
     */
    public void setBikou8(String bikou8) {
        this.bikou8 = bikou8;
    }

    /**
     * @return bikou9
     */
    public String getBikou9() {
        return bikou9;
    }

    /**
     * @param bikou9 セットする
     */
    public void setBikou9(String bikou9) {
        this.bikou9 = bikou9;
    }

    /**
     * @return bikou10
     */
    public String getBikou10() {
        return bikou10;
    }

    /**
     * @param bikou10 セットする
     */
    public void setBikou10(String bikou10) {
        this.bikou10 = bikou10;
    }

    /**
     * @return ins_usr
     */
    public String getIns_usr() {
        return ins_usr;
    }

    /**
     * @param ins_usr セットする
     */
    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    /**
     * @return ins_date
     */
    public Timestamp getIns_date() {
        return ins_date;
    }

    /**
     * @param ins_date セットする
     */
    public void setIns_date(Timestamp ins_date) {
        this.ins_date = ins_date;
    }

    /**
     * @return upd_usr
     */
    public String getUpd_usr() {
        return upd_usr;
    }

    /**
     * @param upd_usr セットする
     */
    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    /**
     * @return upd_date
     */
    public Timestamp getUpd_date() {
        return upd_date;
    }

    /**
     * @param upd_date セットする
     */
    public void setUpd_date(Timestamp upd_date) {
        this.upd_date = upd_date;
    }

    /**
     * @return del_flg
     */
    public Integer getDel_flg() {
        return del_flg;
    }

    /**
     * @param del_flg セットする
     */
    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    /**
     * @return tc201_order_detail_list
     */
    public List<Tc201_order_detail> getTc201_order_detail_list() {
        return tc201_order_detail_list;
    }

    /**
     * @param tc201_order_detail_list セットする
     */
    public void setTc201_order_detail_list(List<Tc201_order_detail> tc201_order_detail_list) {
        this.tc201_order_detail_list = tc201_order_detail_list;
    }

    public Integer getBikou_flg() {
        return bikou_flg;
    }

    public void setBikou_flg(Integer bikou_flg) {
        this.bikou_flg = bikou_flg;
    }

    public Integer getHandling_charge() {
        return handling_charge;
    }

    public void setHandling_charge(Integer handling_charge) {
        this.handling_charge = handling_charge;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
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

    public String getBill_barcode() {
        return bill_barcode;
    }

    public void setBill_barcode(String bill_barcode) {
        this.bill_barcode = bill_barcode;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRelated_order_no() {
        return related_order_no;
    }

    public void setRelated_order_no(String related_order_no) {
        this.related_order_no = related_order_no;
    }

    public String getInvoice_special_notes() {
        return invoice_special_notes;
    }

    public void setInvoice_special_notes(String invoice_special_notes) {
        this.invoice_special_notes = invoice_special_notes;
    }

    @Override
    public String toString() {
        return "Tc200_order{" +
            "purchase_order_no='" + purchase_order_no + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", outer_order_no='" + outer_order_no + '\'' +
            ", history_id='" + history_id + '\'' +
            ", outer_order_status=" + outer_order_status +
            ", order_datetime=" + order_datetime +
            ", payment_method='" + payment_method + '\'' +
            ", order_type=" + order_type +
            ", member_info='" + member_info + '\'' +
            ", product_price_excluding_tax=" + product_price_excluding_tax +
            ", tax_total=" + tax_total +
            ", cash_on_delivery_fee=" + cash_on_delivery_fee +
            ", other_fee=" + other_fee +
            ", delivery_total=" + delivery_total +
            ", billing_total=" + billing_total +
            ", order_flag=" + order_flag +
            ", order_company='" + order_company + '\'' +
            ", order_division='" + order_division + '\'' +
            ", order_zip_code1='" + order_zip_code1 + '\'' +
            ", order_zip_code2='" + order_zip_code2 + '\'' +
            ", order_todoufuken='" + order_todoufuken + '\'' +
            ", order_address1='" + order_address1 + '\'' +
            ", order_address2='" + order_address2 + '\'' +
            ", order_family_name='" + order_family_name + '\'' +
            ", order_first_name='" + order_first_name + '\'' +
            ", order_family_kana='" + order_family_kana + '\'' +
            ", order_first_kana='" + order_first_kana + '\'' +
            ", order_phone_number1='" + order_phone_number1 + '\'' +
            ", order_phone_number2='" + order_phone_number2 + '\'' +
            ", order_phone_number3='" + order_phone_number3 + '\'' +
            ", order_mail='" + order_mail + '\'' +
            ", order_gender=" + order_gender +
            ", receiver_company='" + receiver_company + '\'' +
            ", receiver_division='" + receiver_division + '\'' +
            ", receiver_zip_code1='" + receiver_zip_code1 + '\'' +
            ", receiver_zip_code2='" + receiver_zip_code2 + '\'' +
            ", receiver_todoufuken='" + receiver_todoufuken + '\'' +
            ", receiver_address1='" + receiver_address1 + '\'' +
            ", receiver_address2='" + receiver_address2 + '\'' +
            ", receiver_family_name='" + receiver_family_name + '\'' +
            ", receiver_first_name='" + receiver_first_name + '\'' +
            ", receiver_family_kana='" + receiver_family_kana + '\'' +
            ", receiver_first_kana='" + receiver_first_kana + '\'' +
            ", receiver_phone_number1='" + receiver_phone_number1 + '\'' +
            ", receiver_phone_number2='" + receiver_phone_number2 + '\'' +
            ", receiver_phone_number3='" + receiver_phone_number3 + '\'' +
            ", receiver_mail='" + receiver_mail + '\'' +
            ", receiver_gender=" + receiver_gender +
            ", deliveryman='" + deliveryman + '\'' +
            ", delivery_time_slot='" + delivery_time_slot + '\'' +
            ", delivery_date=" + delivery_date +
            ", shipment_plan_id='" + shipment_plan_id + '\'' +
            ", import_datetime=" + import_datetime +
            ", shipping_request_datetime=" + shipping_request_datetime +
            ", shipment_wish_date=" + shipment_wish_date +
            ", shipment_plan_date=" + shipment_plan_date +
            ", delivery_type='" + delivery_type + '\'' +
            ", delivery_method='" + delivery_method + '\'' +
            ", delivery_company='" + delivery_company + '\'' +
            ", receiver_wish_method='" + receiver_wish_method + '\'' +
            ", gift_wish='" + gift_wish + '\'' +
            ", detail_bundled='" + detail_bundled + '\'' +
            ", detail_price_print='" + detail_price_print + '\'' +
            ", detail_message='" + detail_message + '\'' +
            ", bikou1='" + bikou1 + '\'' +
            ", bikou2='" + bikou2 + '\'' +
            ", bikou3='" + bikou3 + '\'' +
            ", bikou4='" + bikou4 + '\'' +
            ", bikou5='" + bikou5 + '\'' +
            ", bikou6='" + bikou6 + '\'' +
            ", bikou7='" + bikou7 + '\'' +
            ", bikou8='" + bikou8 + '\'' +
            ", bikou9='" + bikou9 + '\'' +
            ", bikou10='" + bikou10 + '\'' +
            ", bikou_flg=" + bikou_flg +
            ", handling_charge=" + handling_charge +
            ", sponsor_id='" + sponsor_id + '\'' +
            ", label_note='" + label_note + '\'' +
            ", box_delivery=" + box_delivery +
            ", fragile_item=" + fragile_item +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", form=" + form +
            ", boxes=" + boxes +
            ", delivery_id='" + delivery_id + '\'' +
            ", total_with_normal_tax=" + total_with_normal_tax +
            ", total_with_reduced_tax=" + total_with_reduced_tax +
            ", bill_barcode='" + bill_barcode + '\'' +
            ", payment_id='" + payment_id + '\'' +
            ", form=" + form +
            ", buy_id='" + buy_id + '\'' +
            ", buy_cnt=" + buy_cnt +
            ", next_delivery_date=" + next_delivery_date +
            ", memo='" + memo + '\'' +
            ", invoice_special_notes='" + invoice_special_notes + '\'' +
            ", tc201_order_detail_list=" + tc201_order_detail_list +
            '}';
    }
}
