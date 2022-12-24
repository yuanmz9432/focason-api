package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @className: Tc100_warehousing_plan
 * @description: 入庫依頼管理TBL
 * @date: 2020/05/12 15:30
 **/
@ApiModel(value = "入庫依頼管理TBL")
public class Tc100_warehousing_plan implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "入庫依頼ID", required = true)
    private String id;
    @ApiModelProperty(value = "入庫依頼日")
    private Timestamp request_date;
    @ApiModelProperty(value = "入庫予定日")
    private Date arrival_date;
    @ApiModelProperty(value = "入庫ステータス")
    private Integer status;
    @ApiModelProperty(value = "入庫ステータス件数")
    private Integer status_count;
    @ApiModelProperty(value = "検品タイプ", required = true)
    private String inspection_type;
    @ApiModelProperty(value = "入庫依頼識別子")
    private String identifier;
    @ApiModelProperty(value = "入庫依頼商品種類数")
    private Integer product_kind_plan_cnt;
    @ApiModelProperty(value = "入庫依頼商品合記")
    private Integer quantity;
    @ApiModelProperty(value = "返品フラグ")
    private Integer shipment_return;
    @ApiModelProperty(value = "お問合せ伝票番号1")
    private String tracking_codes_1;
    @ApiModelProperty(value = "お問合せ伝票番号2")
    private String tracking_codes_2;
    @ApiModelProperty(value = "お問合せ伝票番号3")
    private String tracking_codes_3;
    @ApiModelProperty(value = "お問合せ伝票番号4")
    private String tracking_codes_4;
    @ApiModelProperty(value = "お問合せ伝票番号5")
    private String tracking_codes_5;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private String upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "店舗表示名")
    private String client_nm;
    @ApiModelProperty(value = "検品処理日")
    private Timestamp warehousing_date;
    private List<Tc101_warehousing_plan_detail> tc101_warehousing_plan_details;
    private Tw110_warehousing_result tw110_warehousing_results;
    @ApiModelProperty(value = "お問合せ伝票番号リスト")
    private String tracking_codes;
    private String names;

    public Timestamp getWarehousing_date() {
        return warehousing_date;
    }

    public void setWarehousing_date(Timestamp warehousing_date) {
        this.warehousing_date = warehousing_date;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public Tc100_warehousing_plan() {}

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Timestamp request_date) {
        this.request_date = request_date;
    }

    public Date getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(Date arrival_date) {
        this.arrival_date = arrival_date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getProduct_kind_plan_cnt() {
        return product_kind_plan_cnt;
    }

    public void setProduct_kind_plan_cnt(Integer product_kind_plan_cnt) {
        this.product_kind_plan_cnt = product_kind_plan_cnt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getShipment_return() {
        return shipment_return;
    }

    public void setShipment_return(Integer shipment_return) {
        this.shipment_return = shipment_return;
    }

    public String getTracking_codes_1() {
        return tracking_codes_1;
    }

    public void setTracking_codes_1(String tracking_codes_1) {
        this.tracking_codes_1 = tracking_codes_1;
    }

    public String getTracking_codes_2() {
        return tracking_codes_2;
    }

    public void setTracking_codes_2(String tracking_codes_2) {
        this.tracking_codes_2 = tracking_codes_2;
    }

    public String getTracking_codes_3() {
        return tracking_codes_3;
    }

    public void setTracking_codes_3(String tracking_codes_3) {
        this.tracking_codes_3 = tracking_codes_3;
    }

    public String getTracking_codes_4() {
        return tracking_codes_4;
    }

    public void setTracking_codes_4(String tracking_codes_4) {
        this.tracking_codes_4 = tracking_codes_4;
    }

    public String getTracking_codes_5() {
        return tracking_codes_5;
    }

    public void setTracking_codes_5(String tracking_codes_5) {
        this.tracking_codes_5 = tracking_codes_5;
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

    public String getClient_nm() {
        return client_nm;
    }

    public void setClient_nm(String client_nm) {
        this.client_nm = client_nm;
    }

    public List<Tc101_warehousing_plan_detail> getTc101_warehousing_plan_details() {
        return tc101_warehousing_plan_details;
    }

    public void setTc101_warehousing_plan_details(List<Tc101_warehousing_plan_detail> tc101_warehousing_plan_details) {
        this.tc101_warehousing_plan_details = tc101_warehousing_plan_details;
    }

    public Tw110_warehousing_result getTw110_warehousing_results() {
        return tw110_warehousing_results;
    }

    public void setTw110_warehousing_results(Tw110_warehousing_result tw110_warehousing_results) {
        this.tw110_warehousing_results = tw110_warehousing_results;
    }

    public String getTracking_codes() {
        return tracking_codes;
    }

    public void setTracking_codes(String tracking_codes) {
        this.tracking_codes = tracking_codes;
    }

    public Integer getStatus_count() {
        return status_count;
    }

    public void setStatus_count(Integer status_count) {
        this.status_count = status_count;
    }

    @Override
    public String toString() {
        return "Tc100_warehousing_plan{" +
            "client_id='" + client_id + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", id='" + id + '\'' +
            ", request_date=" + request_date +
            ", arrival_date=" + arrival_date +
            ", status=" + status +
            ", status_count=" + status_count +
            ", inspection_type='" + inspection_type + '\'' +
            ", identifier='" + identifier + '\'' +
            ", product_kind_plan_cnt=" + product_kind_plan_cnt +
            ", quantity=" + quantity +
            ", shipment_return=" + shipment_return +
            ", tracking_codes_1='" + tracking_codes_1 + '\'' +
            ", tracking_codes_2='" + tracking_codes_2 + '\'' +
            ", tracking_codes_3='" + tracking_codes_3 + '\'' +
            ", tracking_codes_4='" + tracking_codes_4 + '\'' +
            ", tracking_codes_5='" + tracking_codes_5 + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", del_flg=" + del_flg +
            ", client_nm='" + client_nm + '\'' +
            ", warehousing_date=" + warehousing_date +
            ", tc101_warehousing_plan_details=" + tc101_warehousing_plan_details +
            ", tw110_warehousing_results=" + tw110_warehousing_results +
            ", tracking_codes='" + tracking_codes + '\'' +
            ", names='" + names + '\'' +
            '}';
    }
}
