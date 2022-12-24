package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;

/**
 * @description 受注模板表
 * 
 * @date 2020/09/10
 **/
@ApiModel(value = "Tc204_order_template", description = "受注模板表")
public class Tc204_order_template
{
    @ApiModelProperty(value = "模板ID", required = true)
    private Integer template_cd;
    @ApiModelProperty(value = "店铺ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "公司ID", required = true)
    private String company_id;
    @ApiModelProperty(value = "模板名称")
    private String template_nm;
    @ApiModelProperty(value = "编码方式")
    private String encoding;
    @ApiModelProperty(value = "分隔符")
    private String delimiter;
    @ApiModelProperty(value = "客户自定义数据")
    private String data;
    @ApiModelProperty(value = "CSV上传header")
    private String template;
    @ApiModelProperty(value = "受注识别子")
    private String identification;
    @ApiModelProperty(value = "固定值")
    private String constant;
    @ApiModelProperty(value = "作成者")
    private String ins_user;
    @ApiModelProperty(value = "作成日時")
    private Timestamp ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_user;
    @ApiModelProperty(value = "更新日時")
    private Timestamp upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Integer getTemplate_cd() {
        return template_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getTemplate_nm() {
        return template_nm;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getData() {
        return data;
    }

    public String getIns_user() {
        return ins_user;
    }

    public Timestamp getIns_date() {
        return ins_date;
    }

    public String getUpd_user() {
        return upd_user;
    }

    public Timestamp getUpd_date() {
        return upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setTemplate_cd(Integer template_cd) {
        this.template_cd = template_cd;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setTemplate_nm(String template_nm) {
        this.template_nm = template_nm;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIns_user(String ins_user) {
        this.ins_user = ins_user;
    }

    public void setIns_date(Timestamp ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_user(String upd_user) {
        this.upd_user = upd_user;
    }

    public void setUpd_date(Timestamp upd_date) {
        this.upd_date = upd_date;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    @Override
    public String toString() {
        return "Tc204_order_template{" +
            "template_cd=" + template_cd +
            ", client_id='" + client_id + '\'' +
            ", company_id='" + company_id + '\'' +
            ", template_nm='" + template_nm + '\'' +
            ", encoding='" + encoding + '\'' +
            ", delimiter='" + delimiter + '\'' +
            ", data='" + data + '\'' +
            ", template='" + template + '\'' +
            ", identification='" + identification + '\'' +
            ", constant='" + constant + '\'' +
            ", ins_user='" + ins_user + '\'' +
            ", ins_date=" + ins_date +
            ", upd_user='" + upd_user + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';
    }
}
