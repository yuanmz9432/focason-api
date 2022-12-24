package com.lemonico.store.bean;



import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "出庫依頼")
public class ShipmentSearch
{

    /**
     * 顧客CD
     */
    public String client_id;

    /**
     * 出庫依頼ID
     */
    public String shipment_plan_id;

    /**
     * 出庫依頼
     */
    public Integer shipment_status;

    /**
     * 検索キーワード
     */
    public String search;

    /**
     * タグID
     */
    public String tags_id;

    /**
     * 依頼日
     */
    public String request_date_start;

    /**
     * 依頼日
     */
    public String request_date_end;

    /**
     * 区分
     */
    public String order_type;

    /**
     * 出荷予定日
     */
    public String shipping_start_date;

    /**
     * 出荷予定日
     */
    public String shipping_end_date;

    /**
     * 配送方法
     */
    public String delivery_carrier;

    /**
     * 金額印字
     */
    public String cash_on_delivery;

    /**
     * 注文日時
     */
    public String order_datetime_start;

    /**
     * 注文日時
     */
    public String order_datetime_end;

    /**
     * 依頼主
     */
    public String sponsor_id;

    /**
     * 出荷日時
     */
    public String shipment_plan_date_start;

    /**
     * 出荷日時
     */
    public String shipment_plan_date_end;

    /**
     * 注文金額
     */
    public Long min_total_price;

    /**
     * 注文金額
     */
    public Long max_total_price;

    /**
     * お届け指定日
     */
    public String delivery_date_start;

    /**
     * お届け指定日
     */
    public String delivery_date_end;

    /**
     * 商品数量
     */
    public Long min_product_plan_total;

    /**
     * 商品数量
     */
    public Long max_product_plan_total;

    /**
     * 注文者名
     */
    public String order_first_name;

    /**
     * 希望時間帯
     */
    public String delivery_time_slot;

    /**
     * 識別番号
     */
    public String identifier;

    /**
     * 注文電話番号
     */
    public String order_phone_number;

    /**
     * 金額印字
     */
    public Integer[] price_on_delivery_note;

    /**
     * 定期購入ID
     */
    public String buy_id;

    /**
     * メールアドレス
     */
    public String email;

    /**
     * 同梱物
     */
    public Integer kubun;

    /**
     * 定期購入回数
     */
    public Long buy_cnt;

    /**
     * 会社名
     */
    public String company;

    /**
     * 添付資料
     */
    public String file;

    /**
     * 出荷指示特記事項
     */
    public String instructions_special_notes;

    /**
     * 事業区分
     */
    public Integer[] form;

    /**
     * 確認メッセージ
     */
    public String status_message;

    /**
     * 支払方法
     */
    public String payment_method;
}
