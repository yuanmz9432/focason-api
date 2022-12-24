package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @className: Tw112_warehousing_result_unknown
 * @description: 入庫実績不明品テーブル
 * @date: 2020/05/09 16:59
 **/
@ApiModel(value = "入庫実績不明品テーブル")
public class Tw112_warehousing_result_unknown implements Serializable
{
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_cd;
    @ApiModelProperty(value = "入庫実績ID", required = true)
    private String warehousing_result_id;
    @ApiModelProperty(value = "不明品枝番", required = true)
    private Integer sub_id;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "入庫実績数")
    private Integer product_cnt;
    @ApiModelProperty(value = "商品サイズコード")
    private String product_size_cd;
    @ApiModelProperty(value = "位置")
    private String location;
    @ApiModelProperty(value = "保留理由")
    private String hold_reason;
    @ApiModelProperty(value = "倉庫メモ")
    private String memo;
    @ApiModelProperty(value = "画像パス")
    private String item_img;
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

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getClient_cd() {
        return client_cd;
    }

    public void setClient_cd(String client_cd) {
        this.client_cd = client_cd;
    }

    public String getWarehousing_result_id() {
        return warehousing_result_id;
    }

    public void setWarehousing_result_id(String warehousing_result_id) {
        this.warehousing_result_id = warehousing_result_id;
    }

    public Integer getSub_id() {
        return sub_id;
    }

    public void setSub_id(Integer sub_id) {
        this.sub_id = sub_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHold_reason() {
        return hold_reason;
    }

    public void setHold_reason(String hold_reason) {
        this.hold_reason = hold_reason;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getItem_img() {
        return item_img;
    }

    public void setItem_img(String item_img) {
        this.item_img = item_img;
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
        return "Tw112_warehousing_result_unknown{" +
            "warehouse_cd='" + warehouse_cd + '\'' +
            ", client_cd='" + client_cd + '\'' +
            ", warehousing_result_id='" + warehousing_result_id + '\'' +
            ", sub_id=" + sub_id +
            ", product_id=" + product_id +
            ", product_cnt=" + product_cnt +
            ", product_size='" + product_size_cd + '\'' +
            ", location='" + location + '\'' +
            ", hold_reason='" + hold_reason + '\'' +
            ", memo='" + memo + '\'' +
            ", item_img='" + item_img + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date='" + upd_date + '\'' +
            ", del_flg=" + del_flg +
            '}';
    }
}
