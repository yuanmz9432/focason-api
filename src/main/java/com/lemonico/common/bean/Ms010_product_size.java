package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;

/**
 * @className: Ms010_product_size
 * @description: 商品サイズマスタ
 * @date: 2020/06/25 15:33
 **/
public class Ms010_product_size
{
    @ApiModelProperty(value = "サイズCD", required = true)
    private String size_cd;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "規格")
    private String type;
    @ApiModelProperty(value = "高さ")
    private Integer height;
    @ApiModelProperty(value = "長さ")
    private Integer length;
    @ApiModelProperty(value = "重量")
    private Integer weight;
    @ApiModelProperty(value = "備考")
    private String biko;
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

    public String getSize_cd() {
        return size_cd;
    }

    public void setSize_cd(String size_cd) {
        this.size_cd = size_cd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getBiko() {
        return biko;
    }

    public void setBiko(String biko) {
        this.biko = biko;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }
}
