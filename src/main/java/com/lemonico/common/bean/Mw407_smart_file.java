package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogi
 * @description: スマートCATファイル
 * @create: 2020-11-13 15:38
 **/
@ApiModel(value = "スマートCATファイル")
public class Mw407_smart_file
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "倉庫CD", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "出庫依頼ID", required = true)
    private String shipment_plan_id;
    @ApiModelProperty(value = "パス")
    private String file_path;
    @ApiModelProperty(value = "ファイル名")
    private String file_name;
    @ApiModelProperty(value = "出力フラグ")
    private Integer flag;
    @ApiModelProperty(value = "CSV出力IP")
    private String ip;
    @ApiModelProperty(value = "作成日時")
    private Date upd_date;
    @ApiModelProperty(value = "作成者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String ins_usr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }
}
