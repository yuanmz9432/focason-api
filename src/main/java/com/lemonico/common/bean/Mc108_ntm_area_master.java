package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @className: Mc108_ntm_area_master
 * @description: ntm支店Master
 * @date: 2021/6/18
 **/
@ApiModel(value = "ntm商品master")
@Data
public class Mc108_ntm_area_master
{

    @ApiModelProperty(value = "支店コード")
    private String area_code;
    @ApiModelProperty(value = "支店名前")
    private String area_name;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "削除フラグ:0：利用中　1:削除済")
    private Integer del_flg;

}
