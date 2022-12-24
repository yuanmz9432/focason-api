package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: ms014_payment
 * @description: ms014_payment
 * @date: 2021/01/26
 **/
@ApiModel(value = "ms014_payment", description = "ms014_payment")
public class Ms015_news
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "日付")
    private Date date;
    @ApiModelProperty(value = "知らせ内容")
    private String context;
    @ApiModelProperty(value = "HTMLファイル名")
    private Integer html_filename;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getContext() {
        return context;
    }

    public Integer getHtml_filename() {
        return html_filename;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setHtml_filename(Integer html_filename) {
        this.html_filename = html_filename;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
