package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: Tw300ErrorRequestCnt
 * @description: 在库表依赖中数和出库详细表统计出来的依赖数不一致
 * @date: 2022/2/16 10:44
 **/
@Data
@ApiModel(value = "Tw300ErrorRequestCnt", description = "在库表依赖中数和出库详细表统计出来的依赖数不一致")
public class Tw300ErrorRequestCnt
{
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "実在庫数")
    private int inventory_cnt;
    @ApiModelProperty(value = "出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "出庫依頼数")
    private Integer product_plan_cnt;
}
