package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Mw404_location
 * @description: Mw404_location
 * @date: 2020/06/25
 **/
@ApiModel(value = "Mw404_location", description = "Mw404_location")
public class Mw404_location
{
    @ApiModelProperty(value = "ロケーションID", required = true)
    private String location_id;
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "倉庫ロケーション")
    private String wh_location_cd;
    @ApiModelProperty(value = "ロケーション名称")
    private String wh_location_nm;
    @ApiModelProperty(value = "优先顺位")
    private Integer priority;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "備考")
    private String info;
    @ApiModelProperty(value = "親ロケ")
    private String parent_location;
    @ApiModelProperty(value = "作成者")
    private Date ins_usr;
    @ApiModelProperty(value = "作成日時")
    private String ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    // TODO 下面这两个字段废止 21/12/09 上载完成后 需要删除
    @ApiModelProperty(value = "賞味期限/在庫保管期限")
    private Date bestbefore_date;
    @ApiModelProperty(value = "出荷不可フラグ")
    private Integer status;

    private Mw405_product_location mw405_product_location;

    private Integer count;

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getWh_location_cd() {
        return wh_location_cd;
    }

    public void setWh_location_cd(String wh_location_cd) {
        this.wh_location_cd = wh_location_cd;
    }

    public String getWh_location_nm() {
        return wh_location_nm;
    }

    public void setWh_location_nm(String wh_location_nm) {
        this.wh_location_nm = wh_location_nm;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getParent_location() {
        return parent_location;
    }

    public void setParent_location(String parent_location) {
        this.parent_location = parent_location;
    }

    public Date getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(Date ins_usr) {
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Mw405_product_location getMw405_product_location() {
        return mw405_product_location;
    }

    public void setMw405_product_location(Mw405_product_location mw405_product_location) {
        this.mw405_product_location = mw405_product_location;
    }

    // line 172-187 废止
    public Date getBestbefore_date() {
        return bestbefore_date;
    }

    public void setBestbefore_date(Date bestbefore_date) {
        this.bestbefore_date = bestbefore_date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Mw404_location{" +
            "location_id='" + location_id + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", wh_location_cd='" + wh_location_cd + '\'' +
            ", wh_location_nm='" + wh_location_nm + '\'' +
            ", priority=" + priority +
            ", lot_no='" + lot_no + '\'' +
            ", info='" + info + '\'' +
            ", parent_location='" + parent_location + '\'' +
            ", ins_usr=" + ins_usr +
            ", ins_date='" + ins_date + '\'' +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", mw405_product_location=" + mw405_product_location +
            ", count=" + count +
            '}';
    }
}
