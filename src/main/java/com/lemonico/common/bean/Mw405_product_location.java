package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Mw405_product_location
 * @description: Mw405_product_location
 * @date: 2020/06/25
 **/
@ApiModel(value = "Mw405_product_location", description = "Mw405_product_location")
public class Mw405_product_location
{
    @ApiModelProperty(value = "ロケーションID", required = true)
    private String location_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "在庫数")
    private Integer stock_cnt;
    @ApiModelProperty(value = "出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "不可配送数")
    private Integer not_delivery;
    @ApiModelProperty(value = "優先順位")
    private Integer priority;
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
    @ApiModelProperty(value = "ロケーション名称")
    private String wh_location_nm;
    @ApiModelProperty(value = "理論在庫数")
    private Integer available_cnt;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品")
    private Mc100_product mc100_product;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "ロケーション")
    private Mw404_location mw404_location;
    @ApiModelProperty(value = "賞味期限/在庫保管期限")
    private Date bestbefore_date;
    @ApiModelProperty(value = "出荷不可フラグ")
    private Integer status;

    public Mw405_product_location() {}

    public Mw405_product_location(String client_id, String product_id) {
        this.client_id = client_id;
        this.product_id = product_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getStock_cnt() {
        return stock_cnt;
    }

    public void setStock_cnt(Integer stock_cnt) {
        this.stock_cnt = stock_cnt;
    }

    public Integer getRequesting_cnt() {
        return requesting_cnt;
    }

    public void setRequesting_cnt(Integer requesting_cnt) {
        this.requesting_cnt = requesting_cnt;
    }

    public Integer getNot_delivery() {
        return not_delivery;
    }

    public void setNot_delivery(Integer not_delivery) {
        this.not_delivery = not_delivery;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public String getWh_location_nm() {
        return wh_location_nm;
    }

    public void setWh_location_nm(String wh_location_nm) {
        this.wh_location_nm = wh_location_nm;
    }

    public Integer getAvailable_cnt() {
        return available_cnt;
    }

    public void setAvailable_cnt(Integer available_cnt) {
        this.available_cnt = available_cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mc100_product getMc100_product() {
        return mc100_product;
    }

    public void setMc100_product(Mc100_product mc100_product) {
        this.mc100_product = mc100_product;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
    }

    public Mw404_location getMw404_location() {
        return mw404_location;
    }

    public void setMw404_location(Mw404_location mw404_location) {
        this.mw404_location = mw404_location;
    }

    public Date getBestbefore_date() {
        return bestbefore_date;
    }

    public void setBestbefore_date(Date bestbefore_date) {
        this.bestbefore_date = bestbefore_date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Mw405_product_location{" +
            "location_id='" + location_id + '\'' +
            ", client_id='" + client_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", stock_cnt=" + stock_cnt +
            ", requesting_cnt=" + requesting_cnt +
            ", not_delivery=" + not_delivery +
            ", priority=" + priority +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", wh_location_nm='" + wh_location_nm + '\'' +
            ", available_cnt=" + available_cnt +
            ", name='" + name + '\'' +
            ", mc100_product=" + mc100_product +
            ", lot_no='" + lot_no + '\'' +
            ", mw404_location=" + mw404_location +
            ", bestbefore_date=" + bestbefore_date +
            ", status=" + status +
            '}';
    }
}
