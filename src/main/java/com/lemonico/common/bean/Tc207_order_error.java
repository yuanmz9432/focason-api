package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;

/**
 * @description 受注取込失敗テーブル・Beanクラス
 *
 * @author wang
 * @date 2021/01/19
 * @version 1.0
 **/
@ApiModel(value = "tc207_order_error", description = "受注取込失敗テーブル")
public class Tc207_order_error
{
    @ApiModelProperty(value = "受注番号", required = true)
    private String order_error_no;
    @ApiModelProperty(value = "顧客管理番号", required = true)
    private String client_id;
    @ApiModelProperty(value = "外部受注番号", required = true)
    private String outer_order_no;
    @ApiModelProperty(value = "処理状況")
    private Integer status;
    @ApiModelProperty(value = "受注取込履歴番号")
    private Integer history_id;
    @ApiModelProperty(value = "エラーメッセージ")
    private String error_msg;
    @ApiModelProperty(value = "備考1")
    private String bikou1;
    @ApiModelProperty(value = "備考2")
    private String bikou2;
    @ApiModelProperty(value = "備考3")
    private String bikou3;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Timestamp ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Timestamp upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getOrder_error_no() {
        return order_error_no;
    }

    public void setOrder_error_no(String order_error_no) {
        this.order_error_no = order_error_no;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getOuter_order_no() {
        return outer_order_no;
    }

    public void setOuter_order_no(String outer_order_no) {
        this.outer_order_no = outer_order_no;
    }

    public Integer getHistory_id() {
        return history_id;
    }

    public void setHistory_id(Integer historyId) {
        this.history_id = historyId;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return biko01
     */
    public String getBiko01() {
        return bikou1;
    }

    /**
     * @param biko01 セットする biko01
     */
    public void setBiko01(String biko01) {
        this.bikou1 = biko01;
    }

    /**
     * @return biko02
     */
    public String getBiko02() {
        return bikou2;
    }

    /**
     * @param biko02 セットする biko02
     */
    public void setBiko02(String biko02) {
        this.bikou2 = biko02;
    }

    /**
     * @return biko03
     */
    public String getBiko03() {
        return bikou3;
    }

    /**
     * @param biko03 セットする biko03
     */
    public void setBiko03(String biko03) {
        this.bikou3 = biko03;
    }

    /**
     * @return ins_usr
     */
    public String getIns_usr() {
        return ins_usr;
    }

    /**
     * @param ins_usr セットする ins_usr
     */
    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
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
    public String getUpd_usr() {
        return upd_usr;
    }

    /**
     * @param upd_usr セットする upd_usr
     */
    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
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

    @Override
    public String toString() {
        return "Tc207_order_error{" +
            "order_error_no=" + order_error_no +
            ", client_id=" + client_id +
            ", outer_order_no=" + outer_order_no +
            ", history_id=" + history_id +
            ", status=" + status +
            ", error_msg='" + error_msg + '\'' +
            ", bikou1='" + bikou1 + '\'' +
            ", bikou2='" + bikou2 + '\'' +
            ", bikou3='" + bikou3 + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            '}';



    }
}
