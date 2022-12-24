package com.lemonico.store.bean;



import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShipmentProductDetail
{
    @ApiModelProperty(value = "管理ID", readOnly = true)
    private Integer id;
    @ApiModelProperty(value = "顧客CD")
    private String client_id;
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "出庫依頼ID")
    private String shipment_plan_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "軽減税率適用商品")
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "0: 税込 1:税抜")
    private Integer tax_flag;
    @ApiModelProperty(value = "緩衝材種別")
    private String cushioning_type;
    @ApiModelProperty(value = "ギフトラッピングタイプ")
    private String gift_wrapping_type;
    @ApiModelProperty(value = "ラッピング備考")
    private String gift_wrapping_note;
    @ApiModelProperty(value = "出庫依頼数")
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "商品単価")
    private Integer unit_price;
    @ApiModelProperty(value = "商品総額")
    private Integer price;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "セット商品ID")
    private Integer set_sub_id;
    @ApiModelProperty(value = "セット数")
    private Integer set_cnt;
    @ApiModelProperty(value = "引当ステータス")
    private Integer reserve_status;
    @ApiModelProperty(value = "引当数")
    private Integer reserve_cnt;
    @ApiModelProperty(value = "商品别名 macro 相关")
    private Integer product_sub_id;
    @ApiModelProperty(value = "編集在庫数")
    private Integer edit_stock_cnt;
    @ApiModelProperty(value = "商品code")
    private String code;
    @ApiModelProperty(value = "商品name")
    private String name;
    @ApiModelProperty(value = "商品管理code")
    private String barcode;
    @ApiModelProperty(value = "商品オプション")
    private String options;
    @ApiModelProperty(value = "商品区分")
    private Integer kubun;
    @ApiModelProperty(value = "オプション金額")
    private Integer option_price;
    @ApiModelProperty(value = "同梱物フラグ")
    private Integer bundled_flg;
    @ApiModelProperty(value = "商品原価")
    private Integer cost_price;
    @ApiModelProperty(value = "商品識別番号")
    private String identifier;
    @ApiModelProperty(value = "品名コード")
    private String description_cd;
    @ApiModelProperty(value = "理論在庫数")
    private Integer available_cnt;
    @ApiModelProperty(value = "出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "実在庫数")
    private Integer inventory_cnt;
    @ApiModelProperty(value = "不可配送数")
    private Integer not_delivery;
    @ApiModelProperty(value = "商品画像枝番")
    private String img_sub_id;
    @ApiModelProperty(value = "商品画像パス1")
    private String product_img1;
    @ApiModelProperty(value = "商品画像パス2")
    private String product_img2;
    @ApiModelProperty(value = "商品画像パス3")
    private String product_img3;

}
