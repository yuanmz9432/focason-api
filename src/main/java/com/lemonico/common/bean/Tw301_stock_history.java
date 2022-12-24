package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

/**
 * @className: Tw301_stock_history
 * @description: 在庫履歴テーブル
 * @date: 2020/05/29
 **/
@ApiModel(value = "在庫履歴テーブル")
public class Tw301_stock_history
{
    @ApiModelProperty(value = "在庫履歴ID", required = true)
    private String history_id;
    @ApiModelProperty(value = "依頼ID", required = true)
    private String plan_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_name;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "タイプ", required = true)
    private Integer type;
    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;
    @ApiModelProperty(value = "変更前のアイテム数")
    private Integer before_num;
    @ApiModelProperty(value = "変更後のアイテム数")
    private Integer after_num;
    @ApiModelProperty(value = "备考")
    private String info;
    @ApiModelProperty(value = "作成者")
    private String ins_user;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_user;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品コード")
    private String code;
    @ApiModelProperty(value = "商品画像パス")
    private List<String> product_img;
    @ApiModelProperty(value = "管理バーコード")
    private String barcode;

    public String getHistory_id() {
        return history_id;
    }

    public void setHistory_id(String history_id) {
        this.history_id = history_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getBefore_num() {
        return before_num;
    }

    public void setBefore_num(Integer before_num) {
        this.before_num = before_num;
    }

    public Integer getAfter_num() {
        return after_num;
    }

    public void setAfter_num(Integer after_num) {
        this.after_num = after_num;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIns_usr() {
        return ins_user;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_user = ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public String getUpd_usr() {
        return upd_user;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_user = upd_usr;
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

    public List<String> getProduct_img() {
        return product_img;
    }

    public void setProduct_img(List<String> product_img) {
        this.product_img = product_img;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return "Tw301_stock_history{" +
            "history_id='" + history_id + '\'' +
            ", plan_id='" + plan_id + '\'' +
            ", client_id='" + client_id + '\'' +
            ", client_name='" + client_name + '\'' +
            ", product_id='" + product_id + '\'' +
            ", type=" + type +
            ", quantity=" + quantity +
            ", before_num=" + before_num +
            ", after_num=" + after_num +
            ", info='" + info + '\'' +
            ", ins_usr='" + ins_user + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_user + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", product_img='" + product_img + '\'' +
            ", barcode='" + barcode + '\'' +
            '}';
    }
}
