package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Tw304_stock_day
 * @description: ntm复制在库表数据
 * @date: 2021/3/9 13:19
 **/
@ApiModel(value = "ntm复制在库表数据")
public class Tw304_stock_day implements Serializable
{
    @ApiModelProperty(value = "在庫処理日", required = true)
    private String stock_date;
    @ApiModelProperty(value = "在庫ID", required = true)
    private String stock_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "理論在庫数", required = true)
    private Integer available_cnt;
    @ApiModelProperty(value = "出庫依頼中数", required = true)
    private Integer requesting_cnt;
    @ApiModelProperty(value = "実在庫数", required = true)
    private Integer inventory_cnt;
    @ApiModelProperty(value = "補充設定在庫数")
    private Integer replenish_cnt;
    @ApiModelProperty(value = "商品サイズコード")
    private String product_size_cd;
    @ApiModelProperty(value = "商品重量")
    private Integer product_weight;
    @ApiModelProperty(value = "重量単位")
    private String weigth_unit;
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
    @ApiModelProperty(value = "商品code")
    private String code;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品价格")
    private String price;

    public String getStock_date() {
        return stock_date;
    }

    public void setStock_date(String stock_date) {
        this.stock_date = stock_date;
    }

    public String getStock_id() {
        return stock_id;
    }

    public void setStock_id(String stock_id) {
        this.stock_id = stock_id;
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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getAvailable_cnt() {
        return available_cnt;
    }

    public void setAvailable_cnt(Integer available_cnt) {
        this.available_cnt = available_cnt;
    }

    public Integer getRequesting_cnt() {
        return requesting_cnt;
    }

    public void setRequesting_cnt(Integer requesting_cnt) {
        this.requesting_cnt = requesting_cnt;
    }

    public Integer getInventory_cnt() {
        return inventory_cnt;
    }

    public void setInventory_cnt(Integer inventory_cnt) {
        this.inventory_cnt = inventory_cnt;
    }

    public Integer getReplenish_cnt() {
        return replenish_cnt;
    }

    public void setReplenish_cnt(Integer replenish_cnt) {
        this.replenish_cnt = replenish_cnt;
    }

    public String getProduct_size_cd() {
        return product_size_cd;
    }

    public void setProduct_size_cd(String product_size_cd) {
        this.product_size_cd = product_size_cd;
    }

    public Integer getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(Integer product_weight) {
        this.product_weight = product_weight;
    }

    public String getWeigth_unit() {
        return weigth_unit;
    }

    public void setWeigth_unit(String weigth_unit) {
        this.weigth_unit = weigth_unit;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Tw304_stock_day{" +
            "stock_date='" + stock_date + '\'' +
            ", stock_id='" + stock_id + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", available_cnt=" + available_cnt +
            ", requesting_cnt=" + requesting_cnt +
            ", inventory_cnt=" + inventory_cnt +
            ", replenish_cnt=" + replenish_cnt +
            ", product_size_cd='" + product_size_cd + '\'' +
            ", product_weight=" + product_weight +
            ", weigth_unit='" + weigth_unit + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", price='" + price + '\'' +
            '}';
    }
}
