package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.Date;


/**
 * @description 受注履歴テーブル・Beanクラス
 *
 * @date 2020/06/26
 * @version 1.0
 **/
@ApiModel(value = "tc200_order_history", description = "受注履歴テーブル")
public class Tc202_order_history
{
    @ApiModelProperty(value = "受注取込履歴ID", required = true)
    private Integer history_id;
    @ApiModelProperty(value = "顧客管理番号", required = true)
    private String client_id;
    @ApiModelProperty(value = "取込件数")
    private Integer total_cnt;
    @ApiModelProperty(value = "成功件数")
    private Integer success_cnt;
    @ApiModelProperty(value = "失敗件数")
    private Integer failure_cnt;
    @ApiModelProperty(value = "取込時間")
    private Date import_datetime;
    @ApiModelProperty(value = "メモ")
    private String note;
    @ApiModelProperty(value = "備考1")
    private String biko01;
    @ApiModelProperty(value = "備考2")
    private String biko02;
    @ApiModelProperty(value = "備考3")
    private String biko03;
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
    @ApiModelProperty(value = "String 类型的 'yyyy-MM-dd HH:mm:SS'")
    private String importDatetime;

    /**
     * @return history_id
     */
    public Integer getHistory_id() {
        return history_id;
    }

    /**
     * @param history_id セットする history_id
     */
    public void setHistory_id(Integer history_id) {
        this.history_id = history_id;
    }

    /**
     * @return client_id
     */
    public String getClient_id() {
        return client_id;
    }

    /**
     * @param client_id セットする client_id
     */
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    /**
     * @return total_cnt
     */
    public Integer getTotal_cnt() {
        return total_cnt;
    }

    /**
     * @param total_cnt セットする total_cnt
     */
    public void setTotal_cnt(Integer total_cnt) {
        this.total_cnt = total_cnt;
    }

    /**
     * @return success_cnt
     */
    public Integer getSuccess_cnt() {
        return success_cnt;
    }

    /**
     * @param success_cnt セットする success_cnt
     */
    public void setSuccess_cnt(Integer success_cnt) {
        this.success_cnt = success_cnt;
    }

    /**
     * @return failure_cnt
     */
    public Integer getFailure_cnt() {
        return failure_cnt;
    }

    /**
     * @param failure_cnt セットする failure_cnt
     */
    public void setFailure_cnt(Integer failure_cnt) {
        this.failure_cnt = failure_cnt;
    }

    /**
     * @return import_datetime
     */
    public Date getImport_datetime() {
        return import_datetime;
    }

    /**
     * @param import_datetime セットする import_datetime
     */
    public void setImport_datetime(Date import_datetime) {
        this.import_datetime = import_datetime;
    }

    /**
     * @return note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note セットする note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return biko01
     */
    public String getBiko01() {
        return biko01;
    }

    /**
     * @param biko01 セットする biko01
     */
    public void setBiko01(String biko01) {
        this.biko01 = biko01;
    }

    /**
     * @return biko02
     */
    public String getBiko02() {
        return biko02;
    }

    /**
     * @param biko02 セットする biko02
     */
    public void setBiko02(String biko02) {
        this.biko02 = biko02;
    }

    /**
     * @return biko03
     */
    public String getBiko03() {
        return biko03;
    }

    /**
     * @param biko03 セットする biko03
     */
    public void setBiko03(String biko03) {
        this.biko03 = biko03;
    }

    /**
     * @return ins_usr
     */
    public String getIns_user() {
        return ins_user;
    }

    /**
     * @param ins_usr セットする ins_usr
     */
    public void setIns_user(String ins_user) {
        this.ins_user = ins_user;
    }

    /**
     * @return ins_date
     */
    public Timestamp getIns_date() {
        return ins_date;
    }

    /**
     * @param ins_date セットする ins_date
     */
    public void setIns_date(Timestamp ins_date) {
        this.ins_date = ins_date;
    }

    /**
     * @return upd_usr
     */
    public String getUpd_user() {
        return upd_user;
    }

    /**
     * @param upd_usr セットする upd_usr
     */
    public void setUpd_user(String upd_user) {
        this.upd_user = upd_user;
    }

    /**
     * @return upd_date
     */
    public Timestamp getUpd_date() {
        return upd_date;
    }

    /**
     * @param upd_date セットする upd_date
     */
    public void setUpd_date(Timestamp upd_date) {
        this.upd_date = upd_date;
    }

    /**
     * @return del_flg
     */
    public Integer getDel_flg() {
        return del_flg;
    }

    /**
     * @param del_flg セットする del_flg
     */
    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public String getImportDatetime() {
        return importDatetime;
    }

    public void setImportDatetime(String importDatetime) {
        this.importDatetime = importDatetime;
    }

    @Override
    public String toString() {
        return "Tc202_order_history{" +
            "history_id=" + history_id +
            ", client_id='" + client_id + '\'' +
            ", total_cnt=" + total_cnt +
            ", success_cnt=" + success_cnt +
            ", failure_cnt=" + failure_cnt +
            ", import_datetime=" + import_datetime +
            ", note='" + note + '\'' +
            ", biko01='" + biko01 + '\'' +
            ", biko02='" + biko02 + '\'' +
            ", biko03='" + biko03 + '\'' +
            ", ins_user='" + ins_user + '\'' +
            ", ins_date=" + ins_date +
            ", upd_user='" + upd_user + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", importDatetime='" + importDatetime + '\'' +
            '}';
    }
}
