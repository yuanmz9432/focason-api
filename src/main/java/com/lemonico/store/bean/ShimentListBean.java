package com.lemonico.store.bean;



import com.lemonico.common.bean.Ms012_sponsor_master;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * @className: tw200_shipment
 * @description: tw200_shipment
 * @date: 2021/06/29
 **/
@ApiModel(value = "tw200_shipment", description = "出庫管理テーブル")
@Data
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
    @ApiModelProperty(value = "注文者郵便番号")
    private String order_zip_code;
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
    @ApiModelProperty(value = "")
    private Integer del_flg;
    @ApiModelProperty(value = "")
    private List<ShipmentProductDetail> shipmentProductDetails;

}
