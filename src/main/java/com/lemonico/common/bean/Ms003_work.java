package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogi
 * @description: 作業マスタ
 * @create: 2020-07-13 10:42
 **/
@ApiModel(value = "ms003_work", description = "ms003_work")
public class Ms003_work
{

    @ApiModelProperty(value = "作業コード", required = true)
    private String operation_cd;
    @ApiModelProperty(value = "作業名", required = true)
    private String operation_nm;
    @ApiModelProperty(value = "備考")
    private String biko;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getOperation_cd() {
        return operation_cd;
    }

    public void setOperation_cd(String operation_cd) {
        this.operation_cd = operation_cd;
    }

    public String getOperation_nm() {
        return operation_nm;
    }

    public void setOperation_nm(String operation_nm) {
        this.operation_nm = operation_nm;
    }

    public String getBiko() {
        return biko;
    }

    public void setBiko(String biko) {
        this.biko = biko;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public String getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(String upd_date) {
        this.upd_date = upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }
}
