package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

@ApiModel(value = "Tc206_order_ftp", description = "店铺FTP表")
public class Tc206_order_ftp
{

    @ApiModelProperty(value = "id", required = true)
    private Integer id;
    @ApiModelProperty(value = "店铺Id", required = true)
    private String client_id;
    @ApiModelProperty(value = "FTPホスト", required = true)
    private String ftp_host;
    @ApiModelProperty(value = "倉庫", required = true)
    private String warehouses;
    @ApiModelProperty(value = "FTPユーザー")
    private String ftp_user;
    @ApiModelProperty(value = "FTPパスワード")
    private String ftp_passwd;
    @ApiModelProperty(value = "FTP受取先")
    private String ftp_path;
    @ApiModelProperty(value = "テンプレートID")
    private String template;
    @ApiModelProperty(value = "FTPファイル")
    private String ftp_file;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "FTPサーバー種類")
    private Integer get_send_flag;
    @ApiModelProperty(value = "模板名称")
    private String template_nm;
    private String warehouse_nm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getftp_host() {
        return ftp_host;
    }

    public void setftp_host(String ftp_host) {
        this.ftp_host = ftp_host;
    }

    public String getftp_user() {
        return ftp_user;
    }

    public void setftp_user(String ftp_user) {
        this.ftp_user = ftp_user;
    }

    public String getftp_passwd() {
        return ftp_passwd;
    }

    public void setftp_passwd(String ftp_passwd) {
        this.ftp_passwd = ftp_passwd;
    }

    public String getftp_path() {
        return ftp_path;
    }

    public void setftp_path(String ftp_path) {
        this.ftp_path = ftp_path;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getftp_file() {
        return ftp_file;
    }

    public void setftp_file(String ftp_file) {
        this.ftp_file = ftp_file;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public String getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(String warehouses) {
        this.warehouses = warehouses;
    }

    public String getTemplate_nm() {
        return template_nm;
    }

    public String getWarehouse_nm() {
        return warehouse_nm;
    }
}
