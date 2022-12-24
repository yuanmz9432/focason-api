package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Ms006_delivery_time
 * @description: 運輸会社時間
 * @date: 2020/11/10 12:46
 **/
@ApiModel(value = "Ms006_delivery_time", description = "Ms006_delivery_time")
public class Ms006_delivery_time
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer delivery_time_id;
    @ApiModelProperty(value = "利用区分")
    private Integer kubu;
    @ApiModelProperty(value = "配送業者名称")
    private String delivery_nm;
    @ApiModelProperty(value = "出荷時間帯（サンロジ用）")
    private String delivery_time_name;
    @ApiModelProperty(value = "出荷時間帯別名1")
    private String delivery_time_csv;
    @ApiModelProperty(value = "並び順")
    private Integer sort_no;
    @ApiModelProperty(value = "備考")
    private String info;
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

    public Integer getDelivery_time_id() {
        return delivery_time_id;
    }

    public void setDelivery_time_id(Integer delivery_time_id) {
        this.delivery_time_id = delivery_time_id;
    }

    public Integer getKubu() {
        return kubu;
    }

    public void setKubu(Integer kubu) {
        this.kubu = kubu;
    }

    public String getDelivery_nm() {
        return delivery_nm;
    }

    public void setDelivery_nm(String delivery_nm) {
        this.delivery_nm = delivery_nm;
    }

    public String getDelivery_time_name() {
        return delivery_time_name;
    }

    public void setDelivery_time_name(String delivery_time_name) {
        this.delivery_time_name = delivery_time_name;
    }

    public String getDelivery_time_csv() {
        return delivery_time_csv;
    }

    public void setDelivery_time_csv(String delivery_time_csv) {
        this.delivery_time_csv = delivery_time_csv;
    }

    public Integer getSort_no() {
        return sort_no;
    }

    public void setSort_no(Integer sort_no) {
        this.sort_no = sort_no;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    @Override
    public String toString() {
        return "Ms006_delivery_time{" +
            "delivery_time_id=" + delivery_time_id +
            ", delivery_nm='" + delivery_nm + '\'' +
            ", delivery_time_name='" + delivery_time_name + '\'' +
            ", delivery_time_csv='" + delivery_time_csv + '\'' +
            ", sort_no=" + sort_no +
            ", info='" + info + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
