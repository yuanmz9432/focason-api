package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: Mw405ErrorData
 * @description: 出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致
 * @date: 2021/12/13 14:33
 **/
@Data
@ApiModel(value = "Mw405ErrorData", description = "出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致")
public class Mw405ErrorData
{
    @ApiModelProperty(value = "ロケーションID")
    private String location_id;
    @ApiModelProperty(value = "顧客CD")
    private String client_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "MW405_在庫数")
    private Integer stock_cnt;
    @ApiModelProperty(value = "MW405_出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "MW405_不可配送数")
    private Integer not_delivery;
    @ApiModelProperty(value = "TW212_引当数")
    private Integer reserve_cnt;
}
