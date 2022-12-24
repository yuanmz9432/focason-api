package com.lemonico.wms.bean;



import com.lemonico.common.bean.Mc102_product_img;
import com.lemonico.common.bean.Mw405_product_location;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @className: Tw111_warehousing_result_detail
 * @description: 入庫実績明細テーブル
 * @date: 2020/05/09 16:54
 **/
@ApiModel(value = "入庫実績明細テーブル")
public class WarehousingDetailBean
{
    @ApiModelProperty(value = "倉庫コード")
    private String warehouse_cd;
    @ApiModelProperty(value = "顧客CD")
    private String client_id;
    @ApiModelProperty(value = "入庫実績ID")
    private String warehousing_result_id;
    @ApiModelProperty(value = "入庫依頼日")
    private Timestamp request_date;
    @ApiModelProperty(value = "検品タイプ")
    private String inspection_type;
    @ApiModelProperty(value = "入庫予定日")
    private Date arrival_date;
    @ApiModelProperty(value = "検品処理日")
    private Timestamp warehousing_date;
    @ApiModelProperty(value = "商品ID")
    private String product_id;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品コード")
    private String code;
    @ApiModelProperty(value = "商品バーコード")
    private String barcode;
    @ApiModelProperty(value = "入庫依頼商品合記")
    private Integer quantity;
    @ApiModelProperty(value = "商品重量")
    private Double weight;
    @ApiModelProperty(value = "商品サイズ")
    private String sizeName;
    @ApiModelProperty(value = "入庫実績数")
    private Integer product_total;
    @ApiModelProperty(value = "ロケーションリスト")
    private List<Mw405_product_location> location_list;
    @ApiModelProperty(value = "ロケーション名")
    private String wh_location_nm;
    @ApiModelProperty(value = "賞味期限/在庫保管期限")
    private Date bestbefore_date;
    @ApiModelProperty(value = "ロット番号")
    private String lot_no;
    @ApiModelProperty(value = "商品画像")
    private List<Mc102_product_img> mc102_product_imgList;
    @ApiModelProperty(value = "デフォルトロケーション名")
    private String location_name;
    @ApiModelProperty(value = "入庫ステータス(0:未入库  1:入库完了)")
    private Integer detail_status;
    @ApiModelProperty(value = "出荷フラグ")
    private Integer shipping_flag;


    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getWarehousing_result_id() {
        return warehousing_result_id;
    }

    public void setWarehousing_result_id(String warehousing_result_id) {
        this.warehousing_result_id = warehousing_result_id;
    }

    public Timestamp getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Timestamp request_date) {
        this.request_date = request_date;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public Date getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(Date arrival_date) {
        this.arrival_date = arrival_date;
    }

    public Timestamp getWarehousing_date() {
        return warehousing_date;
    }

    public void setWarehousing_date(Timestamp warehousing_date) {
        this.warehousing_date = warehousing_date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getProduct_total() {
        return product_total;
    }

    public void setProduct_total(Integer product_total) {
        this.product_total = product_total;
    }

    public List<Mw405_product_location> getLocation_list() {
        return location_list;
    }

    public void setLocation_list(List<Mw405_product_location> location_list) {
        this.location_list = location_list;
    }

    public String getWh_location_nm() {
        return wh_location_nm;
    }

    public void setWh_location_nm(String wh_location_nm) {
        this.wh_location_nm = wh_location_nm;
    }

    public Date getBestbefore_date() {
        return bestbefore_date;
    }

    public void setBestbefore_date(Date bestbefore_date) {
        this.bestbefore_date = bestbefore_date;
    }

    public String getLot_no() {
        return lot_no;
    }

    public void setLot_no(String lot_no) {
        this.lot_no = lot_no;
    }

    public List<Mc102_product_img> getMc102_product_imgList() {
        return mc102_product_imgList;
    }

    public void setMc102_product_imgList(List<Mc102_product_img> mc102_product_imgList) {
        this.mc102_product_imgList = mc102_product_imgList;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Integer getDetail_status() {
        return detail_status;
    }

    public void setDetail_status(Integer detail_status) {
        this.detail_status = detail_status;
    }

    public Integer getShipping_flag() {
        return shipping_flag;
    }

    public void setShipping_flag(Integer shipping_flag) {
        this.shipping_flag = shipping_flag;
    }

    @Override
    public String toString() {
        return "WarehousingDetailBean{" +
            "warehouse_cd='" + warehouse_cd + '\'' +
            ", client_id='" + client_id + '\'' +
            ", warehousing_result_id='" + warehousing_result_id + '\'' +
            ", request_date=" + request_date +
            ", inspection_type='" + inspection_type + '\'' +
            ", arrival_date=" + arrival_date +
            ", warehousing_date=" + warehousing_date +
            ", product_id='" + product_id + '\'' +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", barcode='" + barcode + '\'' +
            ", quantity=" + quantity +
            ", weight=" + weight +
            ", sizeName='" + sizeName + '\'' +
            ", product_total=" + product_total +
            ", location_list=" + location_list +
            ", wh_location_nm='" + wh_location_nm + '\'' +
            ", bestbefore_date=" + bestbefore_date +
            ", lot_no='" + lot_no + '\'' +
            ", mc102_product_imgList=" + mc102_product_imgList +
            ", location_name='" + location_name + '\'' +
            ", detail_status=" + detail_status +
            ", shipping_flag=" + shipping_flag +
            '}';
    }
}
