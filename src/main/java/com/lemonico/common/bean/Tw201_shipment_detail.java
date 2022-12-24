package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

/**
 * @className: tw201_shipment_detail
 * @description: tw201_shipment_detail
 * @date: 2020/05/09
 **/
@ApiModel(value = "tw201_shipment_detail", description = "tw201_shipment_detail")
public class Tw201_shipment_detail
{
    @ApiModelProperty(value = "管理ID", readOnly = true)
    private Integer id;
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "出庫依頼ID", required = true)
    private String shipment_plan_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "軽減税率適用商品")
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "0: 税込 1:税抜")
    private Integer tax_flag;
    @ApiModelProperty(value = "緩衝材種別")
    private String cushioning_type;
    @ApiModelProperty(value = "ギフトラッピングタイプ")
    private String gift_wrapping_type;
    @ApiModelProperty(value = "ラッピング備考")
    private String gift_wrapping_note;
    @ApiModelProperty(value = "出庫依頼数")
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "商品単価")
    private Integer unit_price;
    @ApiModelProperty(value = "商品総額")
    private Integer price;
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
    @ApiModelProperty(value = "商品List")
    private List<Mc100_product> mc100_product;
    @ApiModelProperty(value = "セット商品ID")
    private Integer set_sub_id;
    @ApiModelProperty(value = "セット数")
    private Integer set_cnt;
    @ApiModelProperty(value = "引当ステータス")
    private Integer reserve_status;
    @ApiModelProperty(value = "引当数")
    private Integer reserve_cnt;
    @ApiModelProperty(value = "商品别名 macro 相关")
    private Integer product_sub_id;
    @ApiModelProperty(value = "編集在庫数")
    private Integer edit_stock_cnt;
    @ApiModelProperty(value = "商品code")
    private String code;
    @ApiModelProperty(value = "商品name")
    private String name;
    @ApiModelProperty(value = "商品管理code")
    private String barcode;
    @ApiModelProperty(value = "商品オプション")
    private String options;
    @ApiModelProperty(value = "商品区分")
    private Integer kubun;
    @ApiModelProperty(value = "シリアル番号")
    private String serial_no;
    @ApiModelProperty(value = "税区分")
    private String TaxDivision;
    private List<String> serialNoList;
    @ApiModelProperty(value = "オプション金額")
    private Integer option_price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getIs_reduced_tax() {
        return is_reduced_tax;
    }

    public void setIs_reduced_tax(Integer is_reduced_tax) {
        this.is_reduced_tax = is_reduced_tax;
    }

    public Integer getTax_flag() {
        return tax_flag;
    }

    public void setTax_flag(Integer tax_flag) {
        this.tax_flag = tax_flag;
    }

    public String getCushioning_type() {
        return cushioning_type;
    }

    public void setCushioning_type(String cushioning_type) {
        this.cushioning_type = cushioning_type;
    }

    public String getGift_wrapping_type() {
        return gift_wrapping_type;
    }

    public void setGift_wrapping_type(String gift_wrapping_type) {
        this.gift_wrapping_type = gift_wrapping_type;
    }

    public String getGift_wrapping_note() {
        return gift_wrapping_note;
    }

    public void setGift_wrapping_note(String gift_wrapping_note) {
        this.gift_wrapping_note = gift_wrapping_note;
    }

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
    }

    public Integer getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Integer unit_price) {
        this.unit_price = unit_price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public List<Mc100_product> getMc100_product() {
        return mc100_product;
    }

    public void setMc100_product(List<Mc100_product> mc100_product) {
        this.mc100_product = mc100_product;
    }

    public Integer getSet_sub_id() {
        return set_sub_id;
    }

    public void setSet_sub_id(Integer set_sub_id) {
        this.set_sub_id = set_sub_id;
    }

    public Integer getSet_cnt() {
        return set_cnt;
    }

    public void setSet_cnt(Integer set_cnt) {
        this.set_cnt = set_cnt;
    }

    public Integer getReserve_status() {
        return reserve_status;
    }

    public void setReserve_status(Integer reserve_status) {
        this.reserve_status = reserve_status;
    }

    public Integer getReserve_cnt() {
        return reserve_cnt;
    }

    public void setReserve_cnt(Integer reserve_cnt) {
        this.reserve_cnt = reserve_cnt;
    }

    public Integer getProduct_sub_id() {
        return product_sub_id;
    }

    public void setProduct_sub_id(Integer product_sub_id) {
        this.product_sub_id = product_sub_id;
    }

    public Integer getEdit_stock_cnt() {
        return edit_stock_cnt;
    }

    public void setEdit_stock_cnt(Integer edit_stock_cnt) {
        this.edit_stock_cnt = edit_stock_cnt;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getKubun() {
        return kubun;
    }

    public void setKubun(Integer kubun) {
        this.kubun = kubun;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getTaxDivision() {
        return TaxDivision;
    }

    public void setTaxDivision(String taxDivision) {
        TaxDivision = taxDivision;
    }

    public List<String> getSerialNoList() {
        return serialNoList;
    }

    public void setSerialNoList(List<String> serialNoList) {
        this.serialNoList = serialNoList;
    }

    public Integer getOption_price() {
        return option_price;
    }

    public void setOption_price(Integer option_price) {
        this.option_price = option_price;
    }

    @Override
    public String toString() {
        return "Tw201_shipment_detail{" +
            "id=" + id +
            ", client_id='" + client_id + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", shipment_plan_id='" + shipment_plan_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", is_reduced_tax=" + is_reduced_tax +
            ", tax_flag=" + tax_flag +
            ", cushioning_type='" + cushioning_type + '\'' +
            ", gift_wrapping_type='" + gift_wrapping_type + '\'' +
            ", gift_wrapping_note='" + gift_wrapping_note + '\'' +
            ", product_plan_cnt=" + product_plan_cnt +
            ", unit_price=" + unit_price +
            ", price=" + price +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", mc100_product=" + mc100_product +
            ", set_sub_id=" + set_sub_id +
            ", set_cnt=" + set_cnt +
            ", reserve_status=" + reserve_status +
            ", reserve_cnt=" + reserve_cnt +
            ", product_sub_id=" + product_sub_id +
            ", edit_stock_cnt=" + edit_stock_cnt +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", barcode='" + barcode + '\'' +
            ", options='" + options + '\'' +
            ", kubun=" + kubun +
            ", serial_no='" + serial_no + '\'' +
            ", TaxDivision='" + TaxDivision + '\'' +
            ", serialNoList=" + serialNoList +
            ", option_price=" + option_price +
            '}';
    }
}
