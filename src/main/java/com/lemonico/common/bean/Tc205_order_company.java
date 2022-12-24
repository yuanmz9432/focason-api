package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @description 受注公司表
 * 
 * @date 2020/09/10
 **/
@ApiModel(value = "Tc203_order_company", description = "受注公司表")
public class Tc205_order_company
{
    @ApiModelProperty(value = "公司ID", required = true)
    private String company_id;
    @ApiModelProperty(value = "公司名称")
    private String company_name;
    @ApiModelProperty(value = "公司模板")
    private String template;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getCompany_id() {
        return company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getTemplate() {
        return template;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

}
