package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

@ApiModel(value = "Tc207_order_s3", description = "S3連携管理表")
public class Tc207_order_s3
{

    @ApiModelProperty(value = "id", required = true)
    private Integer id;
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "S3パケット")
    private String bucket;
    @ApiModelProperty(value = "パスワード1")
    private String password1;
    @ApiModelProperty(value = "パスワード2")
    private String password2;
    @ApiModelProperty(value = "取込先Path")
    private String folder;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "格納先Path")
    private String upload_folder;

    public String getUpload_folder() {
        return upload_folder;
    }

    public void setUpload_folder(String upload_folder) {
        this.upload_folder = upload_folder;
    }

    public Integer getId() {
        return id;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getBucket() {
        return bucket;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    public String getFolder() {
        return folder;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

}
