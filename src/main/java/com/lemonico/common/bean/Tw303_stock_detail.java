package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: Tw303_stock_detail
 * @description: 棚卸し在庫明細表
 * @date: 2020/07/09
 **/
@ApiModel(value = "棚卸し在庫明細表")
public class Tw303_stock_detail
{
    @ApiModelProperty(value = "明細管理ID", required = true)
    private Integer detail_id;
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer manage_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "在庫数（理論在庫数）")
    private Integer stock_count;
    @ApiModelProperty(value = "実在庫数")
    private Integer count;
    @ApiModelProperty(value = "更新日")
    private Date update_date;
    @ApiModelProperty(value = "更新者")
    private String user_id;
    private String client_id;
    private String name;
    private String client_nm;
    private Date check_date;

    public Date getCheck_date() {
        return check_date;
    }

    public void setCheck_date(Date check_date) {
        this.check_date = check_date;
    }

    public String getClient_nm() {
        return client_nm;
    }

    public void setClient_nm(String client_nm) {
        this.client_nm = client_nm;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getName() {
        return name;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDetail_id() {
        return detail_id;
    }

    public Integer getManage_id() {
        return manage_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public Integer getStock_count() {
        return stock_count;
    }

    public Integer getCount() {
        return count;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setDetail_id(Integer detail_id) {
        this.detail_id = detail_id;
    }

    public void setManage_id(Integer manage_id) {
        this.manage_id = manage_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setStock_count(Integer stock_count) {
        this.stock_count = stock_count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
