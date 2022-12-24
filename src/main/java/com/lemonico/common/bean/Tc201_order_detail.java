package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;


/**
 * @description 受注明細テーブル・Beanクラス
 *
 * @date 2020/06/18
 * @version 1.0
 **/
@ApiModel(value = "tc201_order_detail", description = "受注明細テーブル")
public class Tc201_order_detail
{
    @ApiModelProperty(value = "受注明細番号", required = true)
    private String order_detail_no;
    @ApiModelProperty(value = "受注番号", required = true)
    private String purchase_order_no;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "商品コード")
    private String product_code;
    @ApiModelProperty(value = "管理バーコード")
    private String product_barcode;
    @ApiModelProperty(value = "商品名")
    private String product_name;
    @ApiModelProperty(value = "単価")
    private Integer unit_price;
    @ApiModelProperty(value = "個数")
    private Integer number;
    @ApiModelProperty(value = "商品計")
    private Integer product_total_price;
    @ApiModelProperty(value = "商品オプション")
    private String product_option;
    @ApiModelProperty(value = "軽減税率適用商品")
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "セット商品ID")
    private Integer set_sub_id;
    @ApiModelProperty(value = "ラッピングタイトル1")
    private String wrapping_title1;
    @ApiModelProperty(value = "ラッピング名1")
    private String wrapping_name1;
    @ApiModelProperty(value = "ラッピング料金1")
    private Integer wrapping_price1;
    @ApiModelProperty(value = "ラッピング税込別1")
    private Integer wrapping_tax1;
    @ApiModelProperty(value = "ラッピング種類1")
    private String wrapping_type1;
    @ApiModelProperty(value = "ラッピングタイトル2")
    private String wrapping_title2;
    @ApiModelProperty(value = "ラッピング名2")
    private String wrapping_name2;
    @ApiModelProperty(value = "ラッピング料金2")
    private Integer wrapping_price2;
    @ApiModelProperty(value = "ラッピング税込別2")
    private Integer wrapping_tax2;
    @ApiModelProperty(value = "ラッピング種類2")
    private String wrapping_type2;
    @ApiModelProperty(value = "備考1")
    private String bikou1;
    @ApiModelProperty(value = "備考2")
    private String bikou2;
    @ApiModelProperty(value = "備考3")
    private String bikou3;
    @ApiModelProperty(value = "備考4")
    private String bikou4;
    @ApiModelProperty(value = "備考5")
    private String bikou5;
    @ApiModelProperty(value = "備考6")
    private String bikou6;
    @ApiModelProperty(value = "備考7")
    private String bikou7;
    @ApiModelProperty(value = "備考8")
    private String bikou8;
    @ApiModelProperty(value = "備考9")
    private String bikou9;
    @ApiModelProperty(value = "備考10")
    private String bikou10;
    @ApiModelProperty(value = "同梱物フラグ")
    private Integer bundled_flg;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Timestamp ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Timestamp upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "税区分")
    private Integer tax_flag;
    @ApiModelProperty(value = "商品区分")
    private Integer product_kubun;
    @ApiModelProperty(value = "オプション金額")
    private Integer option_price;

    // 今後仕様や生データを検討した結果によって、構造修正するかも
    // private List<XXX> xxx_list;

    /**
     * コンストラクタ
     */
    public Tc201_order_detail() {
        super();
    }

    /**
     * @return order_detail_no
     */
    public String getOrder_detail_no() {
        return order_detail_no;
    }

    /**
     * @param order_detail_no セットする
     */
    public void setOrder_detail_no(String order_detail_no) {
        this.order_detail_no = order_detail_no;
    }

    /**
     * @return purchase_order_no
     */
    public String getPurchase_order_no() {
        return purchase_order_no;
    }

    /**
     * @param purchase_order_no セットする
     */
    public void setPurchase_order_no(String purchase_order_no) {
        this.purchase_order_no = purchase_order_no;
    }

    /**
     * @return product_id
     */
    public String getProduct_id() {
        return product_id;
    }

    /**
     * @param product_id セットする
     */
    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    /**
     * @return product_code
     */
    public String getProduct_code() {
        return product_code;
    }

    /**
     * @param product_code セットする
     */
    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    /**
     * @return product_code
     */
    public String getProduct_barcode() {
        return product_barcode;
    }

    /**
     * @param product_barcode セットする
     */
    public void setProduct_barcode(String product_barcode) {
        this.product_barcode = product_barcode;
    }

    /**
     * @return product_name
     */
    public String getProduct_name() {
        return product_name;
    }

    /**
     * @param product_name セットする
     */
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    /**
     * @return unit_price
     */
    public Integer getUnit_price() {
        return unit_price;
    }

    /**
     * @param unit_price セットする
     */
    public void setUnit_price(Integer unit_price) {
        this.unit_price = unit_price;
    }

    /**
     * @return number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number セットする number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * @return product_total_price
     */
    public Integer getProduct_total_price() {
        return product_total_price;
    }

    /**
     * @param product_total_price セットする
     */
    public void setProduct_total_price(Integer product_total_price) {
        this.product_total_price = product_total_price;
    }

    /**
     * @return product_option
     */
    public String getProduct_option() {
        return product_option;
    }

    /**
     * @param product_option セットする
     */
    public void setProduct_option(String product_option) {
        this.product_option = product_option;
    }

    /**
     * @return is_reduced_tax
     */
    public Integer getIs_reduced_tax() {
        return is_reduced_tax;
    }

    /**
     * @param is_reduced_tax セットする
     */
    public void setIs_reduced_tax(Integer is_reduced_tax) {
        this.is_reduced_tax = is_reduced_tax;
    }

    /**
     * @return set_sub_id
     */
    public Integer getSet_sub_id() {
        return set_sub_id;
    }

    /**
     * @param set_sub_id セットする
     */
    public void setSet_sub_id(Integer set_sub_id) {
        this.set_sub_id = set_sub_id;
    }

    /**
     * @return wrapping_title1
     */
    public String getWrapping_title1() {
        return wrapping_title1;
    }

    /**
     * @param wrapping_title1 セットする
     */
    public void setWrapping_title1(String wrapping_title1) {
        this.wrapping_title1 = wrapping_title1;
    }

    /**
     * @return wrapping_name1
     */
    public String getWrapping_name1() {
        return wrapping_name1;
    }

    /**
     * @param wrapping_name1 セットする
     */
    public void setWrapping_name1(String wrapping_name1) {
        this.wrapping_name1 = wrapping_name1;
    }

    /**
     * @return wrapping_price1
     */
    public Integer getWrapping_price1() {
        return wrapping_price1;
    }

    /**
     * @param wrapping_price1 セットする
     */
    public void setWrapping_price1(Integer wrapping_price1) {
        this.wrapping_price1 = wrapping_price1;
    }

    /**
     * @return wrapping_tax1
     */
    public Integer getWrapping_tax1() {
        return wrapping_tax1;
    }

    /**
     * @param wrapping_tax1 セットする
     */
    public void setWrapping_tax1(Integer wrapping_tax1) {
        this.wrapping_tax1 = wrapping_tax1;
    }

    /**
     * @return wrapping_type1
     */
    public String getWrapping_type1() {
        return wrapping_type1;
    }

    /**
     * @param wrapping_type1 セットする
     */
    public void setWrapping_type1(String wrapping_type1) {
        this.wrapping_type1 = wrapping_type1;
    }

    /**
     * @return wrapping_title2
     */
    public String getWrapping_title2() {
        return wrapping_title2;
    }

    /**
     * @param wrapping_title2 セットする
     */
    public void setWrapping_title2(String wrapping_title2) {
        this.wrapping_title2 = wrapping_title2;
    }

    /**
     * @return wrapping_name2
     */
    public String getWrapping_name2() {
        return wrapping_name2;
    }

    /**
     * @param wrapping_name2 セットする
     */
    public void setWrapping_name2(String wrapping_name2) {
        this.wrapping_name2 = wrapping_name2;
    }

    /**
     * @return wrapping_price2
     */
    public Integer getWrapping_price2() {
        return wrapping_price2;
    }

    /**
     * @param wrapping_price2 セットする
     */
    public void setWrapping_price2(Integer wrapping_price2) {
        this.wrapping_price2 = wrapping_price2;
    }

    /**
     * @return wrapping_tax2
     */
    public Integer getWrapping_tax2() {
        return wrapping_tax2;
    }

    /**
     * @param wrapping_tax2 セットする
     */
    public void setWrapping_tax2(Integer wrapping_tax2) {
        this.wrapping_tax2 = wrapping_tax2;
    }

    /**
     * @return wrapping_type2
     */
    public String getWrapping_type2() {
        return wrapping_type2;
    }

    /**
     * @param wrapping_type2 セットする
     */
    public void setWrapping_type2(String wrapping_type2) {
        this.wrapping_type2 = wrapping_type2;
    }

    /**
     * @return bikou1
     */
    public String getBikou1() {
        return bikou1;
    }

    /**
     * @param bikou1 セットする
     */
    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    /**
     * @return bikou2
     */
    public String getBikou2() {
        return bikou2;
    }

    /**
     * @param bikou2 セットする
     */
    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    /**
     * @return bikou3
     */
    public String getBikou3() {
        return bikou3;
    }

    /**
     * @param bikou3 セットする
     */
    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
    }

    /**
     * @return bikou4
     */
    public String getBikou4() {
        return bikou4;
    }

    /**
     * @param bikou4 セットする
     */
    public void setBikou4(String bikou4) {
        this.bikou4 = bikou4;
    }

    /**
     * @return bikou5
     */
    public String getBikou5() {
        return bikou5;
    }

    /**
     * @param bikou5 セットする
     */
    public void setBikou5(String bikou5) {
        this.bikou5 = bikou5;
    }

    /**
     * @return bikou6
     */
    public String getBikou6() {
        return bikou6;
    }

    /**
     * @param bikou6 セットする
     */
    public void setBikou6(String bikou6) {
        this.bikou6 = bikou6;
    }

    /**
     * @return bikou7
     */
    public String getBikou7() {
        return bikou7;
    }

    /**
     * @param bikou7 セットする
     */
    public void setBikou7(String bikou7) {
        this.bikou7 = bikou7;
    }

    /**
     * @return bikou8
     */
    public String getBikou8() {
        return bikou8;
    }

    /**
     * @param bikou8 セットする
     */
    public void setBikou8(String bikou8) {
        this.bikou8 = bikou8;
    }

    /**
     * @return bikou9
     */
    public String getBikou9() {
        return bikou9;
    }

    /**
     * @param bikou9 セットする
     */
    public void setBikou9(String bikou9) {
        this.bikou9 = bikou9;
    }

    /**
     * @return bikou10
     */
    public String getBikou10() {
        return bikou10;
    }

    /**
     * @param bikou10 セットする
     */
    public void setBikou10(String bikou10) {
        this.bikou10 = bikou10;
    }

    /**
     * @return Bundled_flg
     */
    public Integer getBundled_flg() {
        return bundled_flg;
    }

    /**
     * @param bundled_flg セットする
     */
    public void setBundled_flg(Integer bundled_flg) {
        this.bundled_flg = bundled_flg;
    }

    /**
     * @return ins_usr
     */
    public String getIns_usr() {
        return ins_usr;
    }

    /**
     * @param ins_usr セットする
     */
    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    /**
     * @return ins_date
     */
    public Timestamp getIns_date() {
        return ins_date;
    }

    /**
     * @param ins_date セットする
     */
    public void setIns_date(Timestamp ins_date) {
        this.ins_date = ins_date;
    }

    /**
     * @return upd_usr
     */
    public String getUpd_usr() {
        return upd_usr;
    }

    /**
     * @param upd_usr セットする
     */
    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    /**
     * @return upd_date
     */
    public Timestamp getUpd_date() {
        return upd_date;
    }

    /**
     * @param upd_date セットする
     */
    public void setUpd_date(Timestamp upd_date) {
        this.upd_date = upd_date;
    }

    /**
     * @return del_flg
     */
    public Integer getDel_flg() {
        return del_flg;
    }

    /**
     * @param tax_flag セットする
     */
    public void setTax_flag(Integer tax_flag) {
        this.tax_flag = tax_flag;
    }

    /**
     * @return tax_flag
     */
    public Integer getTax_flag() {
        return tax_flag;
    }

    /**
     * @param del_flg セットする
     */
    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public Integer getProduct_kubun() {
        return product_kubun;
    }

    public void setProduct_kubun(Integer product_kubun) {
        this.product_kubun = product_kubun;
    }

    public Integer getOption_price() {
        return option_price;
    }

    public void setOption_price(Integer option_price) {
        this.option_price = option_price;
    }

    @Override
    public String toString() {
        return "Tc201_order_detail{" +
            "order_detail_no='" + order_detail_no + '\'' +
            ", purchase_order_no='" + purchase_order_no + '\'' +
            ", product_id='" + product_id + '\'' +
            ", product_code='" + product_code + '\'' +
            ", product_barcode='" + product_barcode + '\'' +
            ", product_name='" + product_name + '\'' +
            ", unit_price=" + unit_price +
            ", number=" + number +
            ", product_total_price=" + product_total_price +
            ", product_option='" + product_option + '\'' +
            ", is_reduced_tax=" + is_reduced_tax +
            ", set_sub_id=" + set_sub_id +
            ", wrapping_title1='" + wrapping_title1 + '\'' +
            ", wrapping_name1='" + wrapping_name1 + '\'' +
            ", wrapping_price1=" + wrapping_price1 +
            ", wrapping_tax1=" + wrapping_tax1 +
            ", wrapping_type1='" + wrapping_type1 + '\'' +
            ", wrapping_title2='" + wrapping_title2 + '\'' +
            ", wrapping_name2='" + wrapping_name2 + '\'' +
            ", wrapping_price2=" + wrapping_price2 +
            ", wrapping_tax2=" + wrapping_tax2 +
            ", wrapping_type2='" + wrapping_type2 + '\'' +
            ", bikou1='" + bikou1 + '\'' +
            ", bikou2='" + bikou2 + '\'' +
            ", bikou3='" + bikou3 + '\'' +
            ", bikou4='" + bikou4 + '\'' +
            ", bikou5='" + bikou5 + '\'' +
            ", bikou6='" + bikou6 + '\'' +
            ", bikou7='" + bikou7 + '\'' +
            ", bikou8='" + bikou8 + '\'' +
            ", bikou9='" + bikou9 + '\'' +
            ", bikou10='" + bikou10 + '\'' +
            ", bundled_flg=" + bundled_flg +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", tax_flag=" + tax_flag +
            ", product_kubun=" + product_kubun +
            ", option_price=" + option_price +
            '}';
    }
}
