package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @className: Mc103_product_set
 * @description: セット商品明細マスタ
 * @date: 2020/05/09 15:05
 **/
@ApiModel(value = "セット商品明細マスタ")
public class Mc103_product_set implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "セット商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private Integer set_sub_id;
    @ApiModelProperty(value = "商品個数", required = true)
    private Integer product_cnt;
    @ApiModelProperty(value = "商品依赖中数")
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "商品名", notes = "mc100_product")
    private String name;
    @ApiModelProperty(value = "商品コード", notes = "mc100_product")
    private String code;
    @ApiModelProperty(value = "商品コバーコード", notes = "mc100_product")
    private String barcode;
    @ApiModelProperty(value = "商品価格税込", notes = "mc100_product")
    private Integer price;
    @ApiModelProperty(value = "軽減税率適用商品", notes = "mc100_product")
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "理論在庫数", notes = "tw300_stock")
    private Integer available_cnt;
    @ApiModelProperty(value = "出庫依頼中数", notes = "tw300_stock")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "実在庫数", notes = "tw300_stock")
    private Integer inventory_cnt;
    @ApiModelProperty(value = "不可配送数", notes = "tw300_stock")
    private Integer not_delivery;
    @ApiModelProperty(value = "引当数", notes = "")
    private Integer reserve_cnt;
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
    @ApiModelProperty(value = "商品画像")
    private List<Mc102_product_img> mc102_product_imgList;
    private String url;
    @ApiModelProperty(value = "編集在庫数")
    private Integer edit_stock_cnt;
    @ApiModelProperty(value = "シリアルフラグ")
    private Integer serial_flg;
    private List<String> serialNoList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Integer getSet_sub_id() {
        return set_sub_id;
    }

    public void setSet_sub_id(Integer set_sub_id) {
        this.set_sub_id = set_sub_id;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
    }

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getIs_reduced_tax() {
        return is_reduced_tax;
    }

    public void setIs_reduced_tax(Integer is_reduced_tax) {
        this.is_reduced_tax = is_reduced_tax;
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

    public List<Mc102_product_img> getMc102_product_imgList() {
        return mc102_product_imgList;
    }

    public void setMc102_product_imgList(List<Mc102_product_img> mc102_product_imgList) {
        this.mc102_product_imgList = mc102_product_imgList;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getEdit_stock_cnt() {
        return edit_stock_cnt;
    }

    public void setEdit_stock_cnt(Integer edit_stock_cnt) {
        this.edit_stock_cnt = edit_stock_cnt;
    }

    public Integer getNot_delivery() {
        return not_delivery;
    }

    public void setNot_delivery(Integer not_delivery) {
        this.not_delivery = not_delivery;
    }

    public Integer getSerial_flg() {
        return serial_flg;
    }

    public void setSerial_flg(Integer serial_flg) {
        this.serial_flg = serial_flg;
    }

    public List<String> getSerialNoList() {
        return serialNoList;
    }

    public void setSerialNoList(List<String> serialNoList) {
        this.serialNoList = serialNoList;
    }

    @Override
    public String toString() {
        return "Mc103_product_set{" +
            "client_id='" + client_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", set_sub_id=" + set_sub_id +
            ", product_cnt=" + product_cnt +
            ", product_plan_cnt=" + product_plan_cnt +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", barcode='" + barcode + '\'' +
            ", price=" + price +
            ", is_reduced_tax=" + is_reduced_tax +
            ", available_cnt=" + available_cnt +
            ", requesting_cnt=" + requesting_cnt +
            ", inventory_cnt=" + inventory_cnt +
            ", not_delivery=" + not_delivery +
            ", reserve_cnt=" + reserve_cnt +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", mc102_product_imgList=" + mc102_product_imgList +
            ", url='" + url + '\'' +
            ", edit_stock_cnt=" + edit_stock_cnt +
            ", serial_flg=" + serial_flg +
            '}';
    }
}
