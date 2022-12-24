package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Tw113_warehousing_location_detail
 * @description: 入庫作業ロケ明細
 * @date: 2020/8/12 14:00
 **/
public class Tw113_warehousing_location_detail implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "入庫依頼ID", required = true)
    private String warehousing_plan_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "ロケーションID", required = true)
    private String location_id;
    @ApiModelProperty(value = "入庫実績数")
    private Integer product_cnt;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "賞味期限/在庫保管期限")
    private Date bestbefore_date;
    @ApiModelProperty(value = "出荷フラグ")
    private Integer shipping_flag;
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

    public String getWarehousing_plan_id() {
        return warehousing_plan_id;
    }

    public void setWarehousing_plan_id(String warehousing_plan_id) {
        this.warehousing_plan_id = warehousing_plan_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
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

    public Date getBestbefore_date() {
        return bestbefore_date;
    }

    public void setBestbefore_date(Date bestbefore_date) {
        this.bestbefore_date = bestbefore_date;
    }

    public Integer getShipping_flag() {
        return shipping_flag;
    }

    public void setShipping_flag(Integer shipping_flag) {
        this.shipping_flag = shipping_flag;
    }

    @Override
    public String toString() {
        return "Tw113_warehousing_location_detail{" +
            "client_id='" + client_id + '\'' +
            ", warehousing_plan_id='" + warehousing_plan_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", location_id='" + location_id + '\'' +
            ", product_cnt=" + product_cnt +
            ", lot_no='" + lot_no + '\'' +
            ", bestbefore_date=" + bestbefore_date +
            ", shipping_flag=" + shipping_flag +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
