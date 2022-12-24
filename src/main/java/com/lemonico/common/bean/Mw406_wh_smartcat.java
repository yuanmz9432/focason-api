package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @program: sunlogi
 * @description: 倉庫別スマートCatマスタ
 * @create: 2020-11-10 09:34
 **/
@ApiModel(value = "mw406_wh_smartcat", description = "mw406_wh_smartcat")
public class Mw406_wh_smartcat
{
    @ApiModelProperty(value = "ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "倉庫ID", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "連携名", required = true)
    private String cooperation;
    @ApiModelProperty(value = "保存先（PC環境）", required = true)
    private String file_path;
    @ApiModelProperty(value = "ファイル接頭語")
    private String file_start;
    @ApiModelProperty(value = "ファイル接尾語")
    private String file_end;
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

    public String getCooperation() {
        return cooperation;
    }

    public void setCooperation(String cooperation) {
        this.cooperation = cooperation;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_start() {
        return file_start;
    }

    public void setFile_start(String file_start) {
        this.file_start = file_start;
    }

    public String getFile_end() {
        return file_end;
    }

    public void setFile_end(String file_end) {
        this.file_end = file_end;
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
}
