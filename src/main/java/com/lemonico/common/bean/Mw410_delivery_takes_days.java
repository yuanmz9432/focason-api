package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @className: Mw410_delivery_takes_days
 * @description: 配送远方表
 * @date: 2021/6/24
 **/
@ApiModel(value = "配送远方表")
@Data
public class Mw410_delivery_takes_days
{

    @ApiModelProperty(value = "送信者都道府県")
    private String sender;
    @ApiModelProperty(value = "レシーバー都道府県")
    private String receiver;
    @ApiModelProperty(value = "届け佐川急便日")
    private Integer sagawa_day;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ:0：利用中　1:削除済")
    private Integer del_flg;
}
