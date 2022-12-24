package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @className: Tw110_warehousing_result
 * @description: 入庫実績テーブル
 * @date: 2020/05/09 16:46
 **/
@ApiModel(value = "入庫実績テーブル")
public class Tw110_warehousing_result implements Serializable
{
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "入庫実績ID", required = true)
    private String warehousing_result_id;
    @ApiModelProperty(value = "入庫依頼ID")
    private String warehousing_plan_id;
    @ApiModelProperty(value = "入庫依頼日")
    private Timestamp request_date;
    @ApiModelProperty(value = "入庫予定日")
    private Date warehousing_plan_date;
    @ApiModelProperty(value = "入庫タイプ")
    private String warehousing_type;
    @ApiModelProperty(value = "検品タイプ")
    private String inspection_type;
    @ApiModelProperty(value = "検索キーワード")
    private String search_tag;
    @ApiModelProperty(value = "入庫ステータス")
    private String warehousing_status_wh;
    @ApiModelProperty(value = "着荷日")
    private Date arrived_date;
    @ApiModelProperty(value = "検品開始日")
    private Timestamp inspection_date;
    @ApiModelProperty(value = "検品処理日")
    private Timestamp warehousing_date;
    @ApiModelProperty(value = "支払方法")
    private String payment_type;
    @ApiModelProperty(value = "着払金額")
    private Integer freight_collect_amount;
    @ApiModelProperty(value = "個口数")
    private Integer unit_number;
    @ApiModelProperty(value = "問合せ番号")
    private String tracking_no;
    @ApiModelProperty(value = "入庫依頼商品種類数")
    private Integer product_kind_plan_cnt;
    @ApiModelProperty(value = "入庫依頼商品数計")
    private Integer product_plan_total;
    @ApiModelProperty(value = "入庫実績商品種類数")
    private Integer product_kind_cnt;
    @ApiModelProperty(value = "入庫実績商品数計")
    private Integer product_total;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "upd_usr")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "入庫")
    List<Tw111_warehousing_result_detail> tw111_warehousing_result_details;

    public List<Tw111_warehousing_result_detail> getTw111_warehousing_result_details() {
        return tw111_warehousing_result_details;
    }

    public void setTw111_warehousing_result_details(
        List<Tw111_warehousing_result_detail> tw111_warehousing_result_details) {
        this.tw111_warehousing_result_details = tw111_warehousing_result_details;
    }

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

    public String getWarehousing_result_id() {
        return warehousing_result_id;
    }

    public void setWarehousing_result_id(String warehousing_result_id) {
        this.warehousing_result_id = warehousing_result_id;
    }

    public String getWarehousing_plan_id() {
        return warehousing_plan_id;
    }

    public void setWarehousing_plan_id(String warehousing_plan_id) {
        this.warehousing_plan_id = warehousing_plan_id;
    }

    public Timestamp getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Timestamp request_date) {
        this.request_date = request_date;
    }

    public Date getWarehousing_plan_date() {
        return warehousing_plan_date;
    }

    public void setWarehousing_plan_date(Date warehousing_plan_date) {
        this.warehousing_plan_date = warehousing_plan_date;
    }

    public String getWarehousing_type() {
        return warehousing_type;
    }

    public void setWarehousing_type(String warehousing_type) {
        this.warehousing_type = warehousing_type;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public String getSearch_tag() {
        return search_tag;
    }

    public void setSearch_tag(String search_tag) {
        this.search_tag = search_tag;
    }

    public String getWarehousing_status_wh() {
        return warehousing_status_wh;
    }

    public void setWarehousing_status_wh(String warehousing_status_wh) {
        this.warehousing_status_wh = warehousing_status_wh;
    }

    public Date getArrived_date() {
        return arrived_date;
    }

    public void setArrived_date(Date arrived_date) {
        this.arrived_date = arrived_date;
    }

    public Timestamp getInspection_date() {
        return inspection_date;
    }

    public void setInspection_date(Timestamp inspection_date) {
        this.inspection_date = inspection_date;
    }

    public Timestamp getWarehousing_date() {
        return warehousing_date;
    }

    public void setWarehousing_date(Timestamp warehousing_date) {
        this.warehousing_date = warehousing_date;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public Integer getFreight_collect_amount() {
        return freight_collect_amount;
    }

    public void setFreight_collect_amount(Integer freight_collect_amount) {
        this.freight_collect_amount = freight_collect_amount;
    }

    public Integer getUnit_number() {
        return unit_number;
    }

    public void setUnit_number(Integer unit_number) {
        this.unit_number = unit_number;
    }

    public String getTracking_no() {
        return tracking_no;
    }

    public void setTracking_no(String tracking_no) {
        this.tracking_no = tracking_no;
    }

    public Integer getProduct_kind_plan_cnt() {
        return product_kind_plan_cnt;
    }

    public void setProduct_kind_plan_cnt(Integer product_kind_plan_cnt) {
        this.product_kind_plan_cnt = product_kind_plan_cnt;
    }

    public Integer getProduct_plan_total() {
        return product_plan_total;
    }

    public void setProduct_plan_total(Integer product_plan_total) {
        this.product_plan_total = product_plan_total;
    }

    public Integer getProduct_kind_cnt() {
        return product_kind_cnt;
    }

    public void setProduct_kind_cnt(Integer product_kind_cnt) {
        this.product_kind_cnt = product_kind_cnt;
    }

    public Integer getProduct_total() {
        return product_total;
    }

    public void setProduct_total(Integer product_total) {
        this.product_total = product_total;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public String getIns_date() {
        return ins_date;
    }

    public void setIns_date(String ins_date) {
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

    @Override
    public String toString() {
        return "Tw110_warehousing_result{" +
            "warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", warehousing_result_id='" + warehousing_result_id + '\'' +
            ", warehousing_plan_id='" + warehousing_plan_id + '\'' +
            ", request_date=" + request_date +
            ", warehousing_plan_date=" + warehousing_plan_date +
            ", warehousing_type='" + warehousing_type + '\'' +
            ", inspection_type='" + inspection_type + '\'' +
            ", search_tag='" + search_tag + '\'' +
            ", warehousing_status_wh='" + warehousing_status_wh + '\'' +
            ", arrived_date=" + arrived_date +
            ", inspection_date=" + inspection_date +
            ", warehousing_date=" + warehousing_date +
            ", payment_type='" + payment_type + '\'' +
            ", freight_collect_amount=" + freight_collect_amount +
            ", unit_number=" + unit_number +
            ", tracking_no='" + tracking_no + '\'' +
            ", product_kind_plan_cnt=" + product_kind_plan_cnt +
            ", product_plan_total=" + product_plan_total +
            ", product_kind_cnt=" + product_kind_cnt +
            ", product_total=" + product_total +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", del_flg=" + del_flg +
            '}';
    }
}
