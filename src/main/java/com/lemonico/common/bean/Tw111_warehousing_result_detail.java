package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @className: Tw111_warehousing_result_detail
 * @description: 入庫実績明細テーブル
 * @date: 2020/05/09 16:54
 **/
@ApiModel(value = "入庫実績明細テーブル")
public class Tw111_warehousing_result_detail implements Serializable
{
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "入庫実績ID", required = true)
    private String warehousing_plan_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "入庫依頼数")
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "入庫実績数")
    private Integer product_cnt;
    @ApiModelProperty(value = "商品サイズコード")
    private String product_size_cd;
    @ApiModelProperty(value = "商品重量")
    private Integer product_weight;
    @ApiModelProperty(value = "重量単位")
    private String weigth_unit;
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
    @ApiModelProperty(value = "ロケーション情報")
    private Tw113_warehousing_location_detail location_detail;
    @ApiModelProperty(value = "商品")
    private Mc100_product mc100_products;
    @ApiModelProperty(value = "デフォルトロケーション名")
    private String location_name;

    public Mc100_product getMc100_products() {
        return mc100_products;
    }

    public void setMc100_products(Mc100_product mc100_products) {
        this.mc100_products = mc100_products;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
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

    public void setClient_cd(String client_id) {
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

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
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

    public Tw113_warehousing_location_detail getLocation_detail() {
        return location_detail;
    }

    public void setLocation_detail(Tw113_warehousing_location_detail location_detail) {
        this.location_detail = location_detail;
    }

    @Override
    public String toString() {
        return "Tw111_warehousing_result_detail{" +
            "warehouse_cd='" + warehouse_cd + '\'' +
            ", client_cd='" + client_id + '\'' +
            ", warehousing_result_id='" + warehousing_plan_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", product_plan_cnt=" + product_plan_cnt +
            ", product_cnt=" + product_cnt +
            ", product_size_cd='" + product_size_cd + '\'' +
            ", product_weight=" + product_weight +
            ", weigth_unit='" + weigth_unit + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", del_flg=" + del_flg +
            ", location_detail=" + location_detail +
            '}';
    }
}
