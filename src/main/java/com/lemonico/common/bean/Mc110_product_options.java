package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class Mc110_product_options
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "店舗ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "商品管理番号")
    private String shop_code;
    @ApiModelProperty(value = "商品コード")
    private String product_code;
    @ApiModelProperty(value = "対応コード")
    private String sub_code;
    @ApiModelProperty(value = "サンロジ商品コード")
    private String code;
    @ApiModelProperty(value = "オプション名1")
    private String option_name1;
    @ApiModelProperty(value = "オプション値1")
    private String option_value1;
    @ApiModelProperty(value = "オプション名2")
    private String option_name2;
    @ApiModelProperty(value = "オプション名2")
    private String option_value2;
    @ApiModelProperty(value = "オプション名3")
    private String option_name3;
    @ApiModelProperty(value = "オプション名3")
    private String option_value3;
    @ApiModelProperty(value = "オプション名4")
    private String option_name4;
    @ApiModelProperty(value = "オプション名4")
    private String option_value4;
    @ApiModelProperty(value = "全てオプション文字(検索用)")
    private String options;
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

    public String getShop_code() {
        return shop_code;
    }

    public void setShop_code(String shop_code) {
        this.shop_code = shop_code;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getSub_code() {
        return sub_code;
    }

    public void setSub_code(String sub_code) {
        this.sub_code = sub_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOption_name1() {
        return option_name1;
    }

    public void setOption_name1(String option_name1) {
        this.option_name1 = option_name1;
    }

    public String getOption_value1() {
        return option_value1;
    }

    public void setOption_value1(String option_value1) {
        this.option_value1 = option_value1;
    }

    public String getOption_name2() {
        return option_name2;
    }

    public void setOption_name2(String option_name2) {
        this.option_name2 = option_name2;
    }

    public String getOption_value2() {
        return option_value2;
    }

    public void setOption_value2(String option_value2) {
        this.option_value2 = option_value2;
    }

    public String getOption_name3() {
        return option_name3;
    }

    public void setOption_name3(String option_name3) {
        this.option_name3 = option_name3;
    }

    public String getOption_value3() {
        return option_value3;
    }

    public void setOption_value3(String option_value3) {
        this.option_value3 = option_value3;
    }

    public String getOption_name4() {
        return option_name4;
    }

    public void setOption_name4(String option_name4) {
        this.option_name4 = option_name4;
    }

    public String getOption_value4() {
        return option_value4;
    }

    public void setOption_value4(String option_value4) {
        this.option_value4 = option_value4;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
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

}
