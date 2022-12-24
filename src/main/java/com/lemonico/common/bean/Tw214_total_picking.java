package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @className: Tw214_total_picking
 * @description: トータルピッキング
 * @date: 2021/2/8 14:54
 **/
public class Tw214_total_picking implements Serializable
{
    @ApiModelProperty(value = "トータルピッキングID", required = true)
    private Integer total_picking_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "作業者")
    private String user_id;
    @ApiModelProperty(value = "開始時間")
    private Timestamp start_date;
    @ApiModelProperty(value = "終了時間")
    private Timestamp end_date;
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
    private String client_id;
    private String shipment_plan_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品コード")
    private String code;


    public Integer getTotal_picking_id() {
        return total_picking_id;
    }

    public void setTotal_picking_id(Integer total_picking_id) {
        this.total_picking_id = total_picking_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public void setStart_date(Timestamp start_date) {
        this.start_date = start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
    }

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Tw214_total_picking{" +
            "total_picking_id=" + total_picking_id +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", user_id='" + user_id + '\'' +
            ", start_date=" + start_date +
            ", end_date=" + end_date +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", client_id='" + client_id + '\'' +
            ", shipment_plan_id='" + shipment_plan_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", product_plan_cnt=" + product_plan_cnt +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            '}';
    }
}
