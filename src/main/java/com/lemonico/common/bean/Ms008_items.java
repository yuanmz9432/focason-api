package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Ms008_items
 * @description: Ms008_items
 * @date: 2020/06/08
 **/
@ApiModel(value = "Ms008_items", description = "Ms008_items")
public class Ms008_items
{
    @ApiModelProperty(value = "分类CD", required = true)
    private String category_cd;
    @ApiModelProperty(value = "品名CD", required = true)
    private Integer item_id;
    @ApiModelProperty(value = "品名", required = true)
    private String item_nm;
    @ApiModelProperty(value = "品名カナ")
    private String item_nm_kana;
    @ApiModelProperty(value = "品名英語表記")
    private String item_nm_en;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除flag")
    private Integer del_flg;

    public String getCategory_cd() {
        return category_cd;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public String getItem_nm() {
        return item_nm;
    }

    public String getItem_nm_kana() {
        return item_nm_kana;
    }

    public String getItem_nm_en() {
        return item_nm_en;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setCategory_cd(String category_cd) {
        this.category_cd = category_cd;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public void setItem_nm(String item_nm) {
        this.item_nm = item_nm;
    }

    public void setItem_nm_kana(String item_nm_kana) {
        this.item_nm_kana = item_nm_kana;
    }

    public void setItem_nm_en(String item_nm_en) {
        this.item_nm_en = item_nm_en;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
