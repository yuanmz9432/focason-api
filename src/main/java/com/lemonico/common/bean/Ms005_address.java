package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @className: ms100_address
 * @description: ms100_address
 * @date: 2020/05/09
 **/
@ApiModel(value = "ms005_address", description = "ms005_address")
public class Ms005_address
{
    @ApiModelProperty(value = "住所CD", required = true)
    private Integer address_cd;
    @ApiModelProperty(value = "都道府県CD", required = true)
    private String todoufuken_cd;
    @ApiModelProperty(value = "郵便番号", required = true)
    private String zip;
    @ApiModelProperty(value = "都道府県")
    private String todoufuken;
    @ApiModelProperty(value = "都道府県カナ")
    private String todoufuken_kana;
    @ApiModelProperty(value = "市区町村")
    private String shikuchouson;
    @ApiModelProperty(value = "市区町村カナ")
    private String shikuchouson_kana;
    private String city_area;
    private String city_area_kn;
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

    public Integer getAddress_cd() {
        return address_cd;
    }

    public void setAddress_cd(Integer address_cd) {
        this.address_cd = address_cd;
    }

    public String getTodoufuken_cd() {
        return todoufuken_cd;
    }

    public void setTodoufuken_cd(String todoufuken_cd) {
        this.todoufuken_cd = todoufuken_cd;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTodoufuken() {
        return todoufuken;
    }

    public void setTodoufuken(String todoufuken) {
        this.todoufuken = todoufuken;
    }

    public String getTodoufuken_kana() {
        return todoufuken_kana;
    }

    public void setTodoufuken_kana(String todoufuken_kana) {
        this.todoufuken_kana = todoufuken_kana;
    }

    public String getShikuchouson() {
        return shikuchouson;
    }

    public void setShikuchouson(String shikuchouson) {
        this.shikuchouson = shikuchouson;
    }

    public String getShikuchouson_kana() {
        return shikuchouson_kana;
    }

    public void setShikuchouson_kana(String shikuchouson_kana) {
        this.shikuchouson_kana = shikuchouson_kana;
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

    public String getCity_area() {
        return city_area;
    }

    public void setCity_area(String city_area) {
        this.city_area = city_area;
    }

    public String getCity_area_kn() {
        return city_area_kn;
    }

    public void setCity_area_kn(String city_area_kn) {
        this.city_area_kn = city_area_kn;
    }
}
