package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: AbnormalProducts
 * @description: 还在出库的商品被删除
 * @date: 2021/12/13 14:54
 **/
@Data
@ApiModel(value = "AbnormalProducts", description = "还在出库的商品被删除")
public class AbnormalProducts
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "出庫依頼ID", required = true)
    private String shipment_plan_id;
    @ApiModelProperty(value = "出庫ステータス")
    private Integer shipment_status;
    @ApiModelProperty(value = "mc100削除フラグ")
    private Integer mc100_del;
    @ApiModelProperty(value = "tw201削除フラグ")
    private Integer tw201_del;
}
