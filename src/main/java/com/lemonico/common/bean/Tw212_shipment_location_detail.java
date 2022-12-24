package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 * @program: sunlogi
 * @description: 出庫作業ロケ明細
 * @create: 2020-07-13 18:42
 **/
@ApiModel(value = "tw212_shipment_location_detail", description = "tw212_shipment_location_detail")
public class Tw212_shipment_location_detail
{
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "出庫依頼ID", required = true)
    private String shipment_plan_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "ロケーションID", required = true)
    private String location_id;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "ステータス")
    private Integer status;
    @ApiModelProperty(value = "出庫依頼数", required = true)
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "実在庫数", required = true)
    private Integer inventory_cnt;
    @ApiModelProperty(value = "引当数", required = true)
    private Integer reserve_cnt;
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
    @ApiModelProperty(value = "トータルピッキング确认状态")
    private Integer total_picking_status;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "code")
    private String code;
    @ApiModelProperty(value = "管理code")
    private String barcode;
    @ApiModelProperty(value = "货架名称")
    private String wh_location_nm;
    private List<Integer> idList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
    }

    public Integer getInventory_cnt() {
        return inventory_cnt;
    }

    public void setInventory_cnt(Integer inventory_cnt) {
        this.inventory_cnt = inventory_cnt;
    }

    public Integer getReserve_cnt() {
        return reserve_cnt;
    }

    public void setReserve_cnt(Integer reserve_cnt) {
        this.reserve_cnt = reserve_cnt;
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

    public Integer getTotal_picking_status() {
        return total_picking_status;
    }

    public void setTotal_picking_status(Integer total_picking_status) {
        this.total_picking_status = total_picking_status;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getWh_location_nm() {
        return wh_location_nm;
    }

    public void setWh_location_nm(String wh_location_nm) {
        this.wh_location_nm = wh_location_nm;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
    }

    @Override
    public String toString() {
        return "Tw212_shipment_location_detail{" +
            "id=" + id +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", shipment_plan_id='" + shipment_plan_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", location_id='" + location_id + '\'' +
            ", lot_no='" + lot_no + '\'' +
            ", status=" + status +
            ", product_plan_cnt=" + product_plan_cnt +
            ", inventory_cnt=" + inventory_cnt +
            ", reserve_cnt=" + reserve_cnt +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", del_flg=" + del_flg +
            ", total_picking_status=" + total_picking_status +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", barcode='" + barcode + '\'' +
            ", wh_location_nm='" + wh_location_nm + '\'' +
            ", idList=" + idList +
            '}';
    }
}
