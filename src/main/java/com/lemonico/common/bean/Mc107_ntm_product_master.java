package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @className: Mc107_ntm_product_master
 * @description: ntm商品master
 * @date: 2021/6/17
 **/
@ApiModel(value = "ntm商品master")
@Data
public class Mc107_ntm_product_master
{

    @ApiModelProperty(value = "通しNo", required = true)
    private Integer no;
    @ApiModelProperty(value = "翻訳名称")
    private String name;
    @ApiModelProperty(value = "商品1")
    private String product1;
    @ApiModelProperty(value = "商品2")
    private String product2;
    @ApiModelProperty(value = "商品3")
    private String product3;
    @ApiModelProperty(value = "商品4")
    private String product4;
    @ApiModelProperty(value = "商品5")
    private String product5;
    @ApiModelProperty(value = "商品6")
    private String product6;
    @ApiModelProperty(value = "商品7")
    private String product7;
    @ApiModelProperty(value = "商品8")
    private String product8;
    @ApiModelProperty(value = "商品9")
    private String product9;
    @ApiModelProperty(value = "商品10")
    private String product10;
    @ApiModelProperty(value = "開始日")
    private Date start_date;
    @ApiModelProperty(value = "終了日")
    private Date end_date;
    @ApiModelProperty(value = "DM便不可フラグ")
    private Integer dm_flag;
}
