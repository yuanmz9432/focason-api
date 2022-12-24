package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: Tw300Mw405InconsistentInventory
 * @description: 在库表和货架详细表在库数不同
 * @date: 2021/12/10 10:04
 **/
@Data
@ApiModel(value = "tw300Mw405InconsistentInventory", description = "tw300Mw405InconsistentInventory")
public class Tw300Mw405InconsistentInventory
{
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "TW300_实际在库数")
    private int inventory_cnt;
    @ApiModelProperty(value = "MW405_实际在库数")
    private int mw405_stock_cnt;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
}
