package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * @className: Tc203_order_client
 * @description: 店舗API連携管理表
 * @date: 2020/9/9 11:13
 **/
@ApiModel(value = "tc203_order_client", description = "店舗API連携管理表")
public class Tc203_order_client implements Serializable
{
    @ApiModelProperty(value = "id", required = true)
    private Integer id;
    @ApiModelProperty(value = "店舗Id", required = true)
    private String client_id;
    @ApiModelProperty(value = "API名称", required = true)
    private String api_name;
    @ApiModelProperty(value = "店舗URL")
    private String client_url;
    @ApiModelProperty(value = "ライセンスキー")
    private String api_key;
    @ApiModelProperty(value = "パスワード")
    private String password;
    @ApiModelProperty(value = "認証トークン")
    private String token;
    @ApiModelProperty(value = "テンプレート")
    private String template;
    @ApiModelProperty(value = "店舗識別コード")
    private String identification;
    @ApiModelProperty(value = "ホスト名")
    private String hostname;
    @ApiModelProperty(value = "出庫依頼連携")
    private Integer shipment_status;
    @ApiModelProperty(value = "受注連携設定")
    private Integer order_status;
    @ApiModelProperty(value = "送り状連携設定")
    private Integer delivery_status;
    @ApiModelProperty(value = "在庫連携設定")
    private Integer stock_status;
    @ApiModelProperty(value = "備考1")
    private String bikou1;
    @ApiModelProperty(value = "備考2")
    private String bikou2;
    @ApiModelProperty(value = "備考3")
    private String bikou3;
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
    @ApiModelProperty(value = "token")
    private String access_token;
    @ApiModelProperty(value = "刷新token")
    private String refresh_token;
    @ApiModelProperty(value = "依頼主ID")
    private String sponsor_id;
    @ApiModelProperty(value = "过期时间")
    private Date expire_date;
    @ApiModelProperty(value = "过期时间")
    private String stringExpireDate;

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

    public String getApi_name() {
        return api_name;
    }

    public void setApi_name(String api_name) {
        this.api_name = api_name;
    }

    public String getClient_url() {
        return client_url;
    }

    public void setClient_url(String client_url) {
        this.client_url = client_url;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(Integer shipment_status) {
        this.shipment_status = shipment_status;
    }

    public Integer getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    public Integer getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(Integer delivery_status) {
        this.delivery_status = delivery_status;
    }

    public Integer getStock_status() {
        return stock_status;
    }

    public void setStock_status(Integer stock_status) {
        this.stock_status = stock_status;
    }

    /**
     * @return biko01
     */
    public String getBikou1() {
        return bikou1;
    }

    /**
     * @param bikou1 セットする biko01
     */
    public void setBikou1(String bikou1) {
        this.bikou1 = bikou1;
    }

    /**
     * @return biko02
     */
    public String getBikou2() {
        return bikou2;
    }

    /**
     * @param bikou2 セットする biko02
     */
    public void setBikou2(String bikou2) {
        this.bikou2 = bikou2;
    }

    /**
     * @return biko03
     */
    public String getBikou3() {
        return bikou3;
    }

    /**
     * @param bikou3 セットする biko03
     */
    public void setBikou3(String bikou3) {
        this.bikou3 = bikou3;
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

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public String getStringExpireDate() {
        return stringExpireDate;
    }

    public void setStringExpireDate(String stringExpireDate) {
        this.stringExpireDate = stringExpireDate;
    }

    @Override
    public String toString() {
        return "Tc203_order_client{" +
            "id=" + id +
            ", client_id='" + client_id + '\'' +
            ", api_name='" + api_name + '\'' +
            ", client_url='" + client_url + '\'' +
            ", api_key='" + api_key + '\'' +
            ", password='" + password + '\'' +
            ", token='" + token + '\'' +
            ", template='" + template + '\'' +
            ", identification='" + identification + '\'' +
            ", hostname='" + hostname + '\'' +
            ", shipment_status=" + shipment_status +
            ", order_status=" + order_status +
            ", delivery_status=" + delivery_status +
            ", stock_status=" + stock_status +
            ", bikou1='" + bikou1 + '\'' +
            ", bikou2='" + bikou2 + '\'' +
            ", bikou3='" + bikou3 + '\'' +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", access_token='" + access_token + '\'' +
            ", refresh_token='" + refresh_token + '\'' +
            ", sponsor_id='" + sponsor_id + '\'' +
            ", expire_date=" + expire_date +
            '}';
    }
}
