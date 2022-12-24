package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Ms202_customer_wh
 * @description: 顧客別倉庫マスタ
 * @date: 2020/06/1
 **/
public class Ms202_customer_wh implements Serializable
{
    @ApiModelProperty(value = "顧客CD(店舗側)", required = true)
    private String client_id;
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "使用区分")
    private String kubun;
    @ApiModelProperty(value = "備考")
    private String yobi;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public String getKubun() {
        return kubun;
    }

    public String getYobi() {
        return yobi;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public void setKubun(String kubun) {
        this.kubun = kubun;
    }

    public void setYobi(String yobi) {
        this.yobi = yobi;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
