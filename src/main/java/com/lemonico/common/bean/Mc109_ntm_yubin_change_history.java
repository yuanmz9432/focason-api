package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @className: Mc109_ntm_yubin_change_history
 * @description: 郵便番号変更履歴
 * @date: 2021/6/18
 **/
@ApiModel(value = "郵便番号変更履歴")
@Data
public class Mc109_ntm_yubin_change_history
{

    @ApiModelProperty(value = "変更前")
    private String before;
    @ApiModelProperty(value = "変更後")
    private String after;
    @ApiModelProperty(value = "変更日")
    private Date change_date;
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
