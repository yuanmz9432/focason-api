package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: Tw200Tw201DifferentDelFlg
 * @description: 出库依赖和出库依赖明细的删除状态不一致
 * @date: 2022/2/16 10:33
 **/
@Data
@ApiModel(value = "Tw200Tw201DifferentDelFlg", description = "出库依赖和出库依赖明细的删除状态不一致")
public class Tw200Tw201DifferentDelFlg
{
    @ApiModelProperty(value = "出庫依頼ID")
    private String shipment_plan_id;
    @ApiModelProperty(value = "出庫ステータス")
    private int shipment_status;
    @ApiModelProperty(value = "削除フラグ")
    private int del_flg;
    @ApiModelProperty(value = "店铺CD")
    private String client_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "出庫依頼数")
    private int product_plan_cnt;
    @ApiModelProperty(value = "tw201削除フラグ")
    private int tw201_del_flg;
}
