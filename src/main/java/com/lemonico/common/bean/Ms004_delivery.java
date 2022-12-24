package com.lemonico.common.bean;



import io.swagger.annotations.ApiModelProperty;

/**
 * @program: sunlogic
 * @description: 運送業者マスタ
 * @create: 2020-07-09 13:13
 **/
public class Ms004_delivery
{
    @ApiModelProperty(value = "配送業者CD", required = true)
    private String delivery_cd;
    @ApiModelProperty(value = "配送便指定 1:ポスト便 2:宅配便", required = true)
    private String delivery_method;
    @ApiModelProperty(value = "配送業者名称", required = true)
    private String delivery_nm;
    @ApiModelProperty(value = "配送方法（サンロジ用）")
    private String delivery_method_name;
    @ApiModelProperty(value = "配送方法別名(出荷CSV)")
    private String delivery_method_csv;
    @ApiModelProperty(value = "並び順番")
    private Integer sort_no;
    @ApiModelProperty(value = "荷送人コード(依頼コード)")
    private String delivery_code;
    @ApiModelProperty(value = "備考")
    private String info;
    @ApiModelProperty(value = "default size_cd")
    private String default_size_cd;
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

    public String getDefault_size_cd() {
        return default_size_cd;
    }

    public void setDefault_size_cd(String default_size_cd) {
        this.default_size_cd = default_size_cd;
    }

    public String getDelivery_cd() {
        return delivery_cd;
    }

    public void setDelivery_cd(String delivery_cd) {
        this.delivery_cd = delivery_cd;
    }

    public String getDelivery_method() {
        return delivery_method;
    }

    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

    public String getDelivery_nm() {
        return delivery_nm;
    }

    public void setDelivery_nm(String delivery_nm) {
        this.delivery_nm = delivery_nm;
    }

    public String getDelivery_method_name() {
        return delivery_method_name;
    }

    public void setDelivery_method_name(String delivery_method_name) {
        this.delivery_method_name = delivery_method_name;
    }

    public String getDelivery_method_csv() {
        return delivery_method_csv;
    }

    public void setDelivery_method_csv(String delivery_method_csv) {
        this.delivery_method_csv = delivery_method_csv;
    }

    public Integer getSort_no() {
        return sort_no;
    }

    public void setSort_no(Integer sort_no) {
        this.sort_no = sort_no;
    }

    public String getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(String delivery_code) {
        this.delivery_code = delivery_code;
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

    @Override
    public String toString() {
        return "Ms004_delivery{" + "delivery_cd='" + delivery_cd + '\'' + ", delivery_method='" + delivery_method + '\''
            + ", delivery_nm='" + delivery_nm + '\'' + ", delivery_method_name='" + delivery_method_name + '\''
            + ", delivery_method_csv='" + delivery_method_csv + '\'' + ", sort_no=" + sort_no + ", delivery_code='"
            + delivery_code + '\'' + ", info='" + info + '\'' + ", ins_usr='" + ins_usr + '\'' + ", ins_date='"
            + ins_date + '\'' + ", upd_usr='" + upd_usr + '\'' + ", upd_date='" + upd_date + '\'' + ", del_flg="
            + del_flg + '}';
    }
}
