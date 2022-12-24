package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Mc101_product_tag
 * @description: 商品タグ
 * @date: 2020/05/09 15:02
 **/
@ApiModel(value = "商品タグ")
public class Mc101_product_tag implements Serializable
{
    @ApiModelProperty(value = "タグID", required = true)
    private String tags_id;
    @ApiModelProperty(value = "タグ:商品検索用", required = true)
    private String tags;
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
    private Mc104_tag mc104_tag;

    public Mc104_tag getMc104_tag() {
        return mc104_tag;
    }

    public void setMc104_tag(Mc104_tag mc104_tag) {
        this.mc104_tag = mc104_tag;
    }

    public String getTags_id() {
        return tags_id;
    }

    public void setTags_id(String tags_id) {
        this.tags_id = tags_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
