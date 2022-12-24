package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Tw216_delivery_fare
 * @description: 配送運賃
 * @date: 2021/5/31 10:51
 **/
@ApiModel(value = "配送運賃")
public class Tw216_delivery_fare
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer fare_id;
    @ApiModelProperty(value = "配送業者", required = true)
    private String agent_id;
    @ApiModelProperty(value = "配送方法", required = true)
    private String method;
    @ApiModelProperty(value = "都道府県", required = true)
    private String region;
    @ApiModelProperty(value = "価格", required = true)
    private Integer actual_price;
    @ApiModelProperty(value = "原価", required = true)
    private Integer original_price;
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

    public Integer getFare_id() {
        return fare_id;
    }

    public void setFare_id(Integer fare_id) {
        this.fare_id = fare_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getActual_price() {
        return actual_price;
    }

    public void setActual_price(Integer actual_price) {
        this.actual_price = actual_price;
    }

    public Integer getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(Integer original_price) {
        this.original_price = original_price;
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

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }
}
