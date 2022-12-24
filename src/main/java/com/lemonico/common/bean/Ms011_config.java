package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @className: ms001_config
 * @description: ms001_config
 * @date: 2020/05/09
 **/
@ApiModel(value = "ms011_config", description = "ms011_config")
public class Ms011_config
{
    @ApiModelProperty(value = "設定CD", required = true)
    private Integer set_cd;
    @ApiModelProperty(value = "店舗ID", required = true)
    private String client_id;
    @ApiModelProperty(value = "設定項目", required = true)
    private String set_key;
    @ApiModelProperty(value = "設定値", required = true)
    private String set_value;
    @ApiModelProperty(value = "備考")
    private String info;
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

    public Ms011_config() {
        super();
    }

    public Ms011_config(Integer set_cd, String client_id, String set_key, String set_value, String info, String ins_usr,
        String ins_date,
        String upd_usr, String upd_date, Integer del_flg) {
        super();
        this.set_cd = set_cd;
        this.client_id = client_id;
        this.set_key = set_key;
        this.set_value = set_value;
        this.info = info;
        this.ins_usr = ins_usr;
        this.ins_date = ins_date;
        this.upd_usr = upd_usr;
        this.upd_date = upd_date;
        this.del_flg = del_flg;
    }

    public Integer getSet_cd() {
        return set_cd;
    }

    public void setSet_cd(Integer set_cd) {
        this.set_cd = set_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getSet_key() {
        return set_key;
    }

    public void setSet_key(String set_key) {
        this.set_key = set_key;
    }

    public String getSet_value() {
        return set_value;
    }

    public void setSet_value(String set_value) {
        this.set_value = set_value;
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

}
