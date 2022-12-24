package com.lemonico.batch.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: Tw300ErrorData
 * @description: 出库依赖中数和在库表的依赖中数不同
 * @date: 2021/12/13 17:49
 **/
@Data
@ApiModel(value = "Tw300ErrorData", description = "出库依赖中数和在库表的依赖中数不同")
public class Tw300ErrorData
{
    @ApiModelProperty(value = "顧客CD")
    private String client_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "TW300_実在庫数")
    private Integer inventory_cnt;
    @ApiModelProperty(value = "TW300_出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "TW201_出庫依頼数")
    private Integer product_plan_cnt;

    public Tw300ErrorData() {}

    public Tw300ErrorData(String client_id, String product_id, Integer inventory_cnt, Integer requesting_cnt,
        Integer product_plan_cnt) {
        this.client_id = client_id;
        this.product_id = product_id;
        this.inventory_cnt = inventory_cnt;
        this.requesting_cnt = requesting_cnt;
        this.product_plan_cnt = product_plan_cnt;
    }
}
