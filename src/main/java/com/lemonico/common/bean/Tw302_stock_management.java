package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Tw302_stock_management
 * @description: 棚卸し在庫管理表
 * @date: 2020/07/09
 **/
@ApiModel(value = "棚卸し在庫管理表")
public class Tw302_stock_management
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer manage_id;
    @ApiModelProperty(value = "倉庫CD", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "商品点数")
    private Integer product_cnt;
    @ApiModelProperty(value = "ステータス")
    private Integer state;
    @ApiModelProperty(value = "棚卸し年月")
    private Date check_date;
    @ApiModelProperty(value = "作業者ID", required = true)
    private String user_id;
    @ApiModelProperty(value = "作業開始日")
    private Date start_date;
    @ApiModelProperty(value = "作業終了日")
    private Date end_date;
    @ApiModelProperty(value = "更新日")
    private Date update_date;
    private String client_nm;

    public String getClient_nm() {
        return client_nm;
    }

    public void setClient_nm(String client_nm) {
        this.client_nm = client_nm;
    }

    public Integer getManage_id() {
        return manage_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public Integer getState() {
        return state;
    }

    public Date getCheck_date() {
        return check_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setManage_id(Integer manage_id) {
        this.manage_id = manage_id;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void setCheck_date(Date check_date) {
        this.check_date = check_date;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

}
