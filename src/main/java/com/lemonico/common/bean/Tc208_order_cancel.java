package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;

/**
 * @description 受注キャセンル管理テーブル・Beanクラス
 *
 * @author wang
 * @date 2021/01/19
 * @version 1.0
 **/
@ApiModel(value = "tc200_order_cancel", description = "受注キャセンル管理テーブル")
public class Tc208_order_cancel
{
    @ApiModelProperty(value = "受注番号", required = true)
    private String order_cancel_no;
    @ApiModelProperty(value = "顧客管理番号", required = true)
    private String client_id;
    @ApiModelProperty(value = "外部受注番号", required = true)
    private String outer_order_no;
    @ApiModelProperty(value = "出庫依頼番号")
    private String shipment_plan_id;
    @ApiModelProperty(value = "処理状況")
    private Integer status;
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
    @ApiModelProperty(value = "出库状态")
    private String shipment_status;

    public String getOrder_cancel_no() {
        return order_cancel_no;
    }

    public void setOrder_cancel_no(String order_cancel_no) {
        this.order_cancel_no = order_cancel_no;
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

    public String getShipment_plan_id() {
        return shipment_plan_id;
    }

    public void setShipment_plan_id(String shipment_plan_id) {
        this.shipment_plan_id = shipment_plan_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return bikou1
     */
    public String getBikou1() {
        return bikou1;
    }

    /**
     * @param bikou1 セットする bikou1
     */
    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    /**
     * @return bikou2
     */
    public String getBikou2() {
        return bikou2;
    }

    /**
     * @param bikou2 セットする bikou2
     */
    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    /**
     * @return bikou3
     */
    public String getBikou3() {
        return bikou3;
    }

    /**
     * @param bikou3 セットする bikou3
     */
    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
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

    public String getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    @Override
    public String toString() {
        return "Tc208_order_cancel{" +
            "order_cancel_no='" + order_cancel_no + '\'' +
            ", client_id='" + client_id + '\'' +
            ", outer_order_no='" + outer_order_no + '\'' +
            ", shipment_plan_id='" + shipment_plan_id + '\'' +
            ", status=" + status +
            ", bikou1='" + bikou1 + '\'' +
            ", bikou2='" + bikou2 + '\'' +
            ", bikou3='" + bikou3 + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", shipment_status='" + shipment_status + '\'' +
            '}';
    }
}
