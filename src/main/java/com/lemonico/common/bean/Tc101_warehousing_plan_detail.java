package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Tw101_warehousing_plan_detail
 * @description: 入庫予定明細テーブル
 * @date: 2020/05/09 15:16
 **/
@ApiModel(value = "入庫予定明細テーブル")
public class Tc101_warehousing_plan_detail implements Serializable
{
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "入庫依頼ID", required = true)
    private String id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "入庫依頼数")
    private Integer quantity;
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
    private Mc100_product mc100_productList;
    private Tw111_warehousing_result_detail tw111_warehousing_result_detail;
    @ApiModelProperty(value = "入庫実績数")
    private Integer product_cnt;
    @ApiModelProperty(value = "デフォルトロケーション名")
    private String location_name;
    @ApiModelProperty(value = "入庫ステータス(0:未入库  1:入库完了)")
    private Integer status;
    @ApiModelProperty(value = "賞味期限/在庫保管期限")
    private Date bestbefore_date;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "出荷フラグ")
    private Integer shipping_flag;

    public Tc101_warehousing_plan_detail() {}

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Mc100_product getMc100_productList() {
        return mc100_productList;
    }

    public void setMc100_productList(Mc100_product mc100_productList) {
        this.mc100_productList = mc100_productList;
    }

    public Tw111_warehousing_result_detail getTw111_warehousing_result_detail() {
        return tw111_warehousing_result_detail;
    }

    public void setTw111_warehousing_result_detail(Tw111_warehousing_result_detail tw111_warehousing_result_detail) {
        this.tw111_warehousing_result_detail = tw111_warehousing_result_detail;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getBestbefore_date() {
        return bestbefore_date;
    }

    public void setBestbefore_date(Date bestbefore_date) {
        this.bestbefore_date = bestbefore_date;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
    }

    public Integer getShipping_flag() {
        return shipping_flag;
    }

    public void setShipping_flag(Integer shipping_flag) {
        this.shipping_flag = shipping_flag;
    }

    @Override
    public String toString() {
        return "Tc101_warehousing_plan_detail{" +
            "warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", id='" + id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", quantity=" + quantity +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", mc100_productList=" + mc100_productList +
            ", tw111_warehousing_result_detail=" + tw111_warehousing_result_detail +
            ", product_cnt=" + product_cnt +
            ", location_name='" + location_name + '\'' +
            ", status=" + status +
            ", bestbefore_date=" + bestbefore_date +
            ", lot_no='" + lot_no + '\'' +
            ", shipping_flag=" + shipping_flag +
            '}';
    }
}
