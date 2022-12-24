package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @className: Mc100_product
 * @description: 商品マスタ
 * @date: 2020/05/09 14:42
 **/
@ApiModel(value = "商品マスタ")
public class Mc100_product implements Serializable
{
    @ApiModelProperty(value = "顧客CD", required = true)
    private String client_id;
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "倉庫コード", required = true)
    private String warehouse_cd;
    @ApiModelProperty(value = "商品名", required = true)
    private String name;
    @ApiModelProperty(value = "商品英語名")
    private String english_name;
    @ApiModelProperty(value = "商品コード")
    private String code;
    @ApiModelProperty(value = "セット商品フラグ")
    private Integer set_flg;
    @ApiModelProperty(value = "セット商品ID")
    private Integer set_sub_id;
    @ApiModelProperty(value = "管理バーコード")
    private String barcode;
    @ApiModelProperty(value = "同梱物フラグ")
    private Integer bundled_flg;
    @ApiModelProperty(value = "軽減税率適用商品", required = true)
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "0: 税込 1:税抜")
    private Integer tax_flag;
    @ApiModelProperty(value = "商品価格税込")
    private Integer price;
    @ApiModelProperty(value = "商品原価")
    private Integer cost_price;
    @ApiModelProperty(value = "商品識別番号")
    private String identifier;
    @ApiModelProperty(value = "品名コード")
    private String description_cd;
    @ApiModelProperty(value = "原産国")
    private String origin;
    @ApiModelProperty(value = "商品サイズコード")
    private String size_cd;
    @ApiModelProperty(value = "タグコード")
    private String tags_id;
    @ApiModelProperty(value = "表示商品フラグ")
    private Integer show_flg;
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
    @ApiModelProperty(value = "タグ")
    private Mc101_product_tag mc101_product_tag;
    @ApiModelProperty(value = "タグ")
    private String[] tags;
    @ApiModelProperty(value = "在庫")
    private Tw300_stock tw300_stock;
    @ApiModelProperty(value = "在庫履歴")
    private Tw301_stock_history tw301_stock_history;
    @ApiModelProperty(value = "商品画像")
    private List<Mc102_product_img> mc102_product_imgList;
    @ApiModelProperty(value = "セット商品")
    private List<Mc103_product_set> mc103_product_sets;
    @ApiModelProperty(value = "サイズ名")
    private String sizeName;
    @ApiModelProperty(value = "サイズタイプ")
    private String sizeType;
    @ApiModelProperty(value = "サイズ重量")
    private Integer sizeWeight;
    @ApiModelProperty(value = "ロケーション名")
    private String wh_location_nm;
    @ApiModelProperty(value = "货架最大在庫数")
    private Integer stock_cnt;
    @ApiModelProperty(value = "全部在庫数")
    private Integer stock_total;
    @ApiModelProperty(value = "商品数")
    private Integer product_cnt;
    @ApiModelProperty(value = "依頼数")
    private Integer product_plan_cnt;
    @ApiModelProperty(value = "倉庫ロケーション")
    private List<Mw405_product_location> productLocationList;
    private Boolean requestFlag;
    private Double weight;
    @ApiModelProperty(value = "画像1")
    private String img1;
    @ApiModelProperty(value = "画像2")
    private String img2;
    @ApiModelProperty(value = "画像3")
    private String img3;
    @ApiModelProperty(value = "set商品在库数")
    private Integer product_set_inventoryCnt;
    @ApiModelProperty(value = "商品url")
    private String url;
    @ApiModelProperty(value = "備考")
    private String bikou;
    @ApiModelProperty(value = "商品区分")
    private Integer kubun;
    @ApiModelProperty(value = "シリアルフラグ")
    private Integer serial_flg;
    private Integer shipmentNum;
    @ApiModelProperty(value = "商品種類")
    private String product_type;
    @ApiModelProperty(value = "商品種類CD")
    private Integer product_cd;
    @ApiModelProperty(value = "ソート")
    private Integer sort_no;
    @ApiModelProperty(value = "ntm商品原价")
    private Integer ntm_price;
    private String ntm_memo;
    @ApiModelProperty(value = "eccube的数据是否显示，0:公開，1：非公開")
    private Integer eccube_show_flg;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getWarehouse_cd() {
        return warehouse_cd;
    }

    public void setWarehouse_cd(String warehouse_cd) {
        this.warehouse_cd = warehouse_cd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSet_flg() {
        return set_flg;
    }

    public void setSet_flg(Integer set_flg) {
        this.set_flg = set_flg;
    }

    public Integer getSet_sub_id() {
        return set_sub_id;
    }

    public void setSet_sub_id(Integer set_sub_id) {
        this.set_sub_id = set_sub_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getBundled_flg() {
        return bundled_flg;
    }

    public void setBundled_flg(Integer bundled_flg) {
        this.bundled_flg = bundled_flg;
    }

    public Integer getIs_reduced_tax() {
        return is_reduced_tax;
    }

    public void setIs_reduced_tax(Integer is_reduced_tax) {
        this.is_reduced_tax = is_reduced_tax;
    }

    public Integer getTax_flag() {
        return tax_flag;
    }

    public void setTax_flag(Integer tax_flag) {
        this.tax_flag = tax_flag;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCost_price() {
        return cost_price;
    }

    public void setCost_price(Integer cost_price) {
        this.cost_price = cost_price;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription_cd() {
        return description_cd;
    }

    public void setDescription_cd(String description_cd) {
        this.description_cd = description_cd;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getSize_cd() {
        return size_cd;
    }

    public void setSize_cd(String size_cd) {
        this.size_cd = size_cd;
    }

    public String getTags_id() {
        return tags_id;
    }

    public void setTags_id(String tags_id) {
        this.tags_id = tags_id;
    }

    public Integer getShow_flg() {
        return show_flg;
    }

    public void setShow_flg(Integer show_flg) {
        this.show_flg = show_flg;
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

    public Mc101_product_tag getMc101_product_tag() {
        return mc101_product_tag;
    }

    public void setMc101_product_tag(Mc101_product_tag mc101_product_tag) {
        this.mc101_product_tag = mc101_product_tag;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Tw300_stock getTw300_stock() {
        return tw300_stock;
    }

    public void setTw300_stock(Tw300_stock tw300_stock) {
        this.tw300_stock = tw300_stock;
    }

    public Tw301_stock_history getTw301_stock_history() {
        return tw301_stock_history;
    }

    public void setTw301_stock_history(Tw301_stock_history tw301_stock_history) {
        this.tw301_stock_history = tw301_stock_history;
    }

    public List<Mc102_product_img> getMc102_product_imgList() {
        return mc102_product_imgList;
    }

    public void setMc102_product_imgList(List<Mc102_product_img> mc102_product_imgList) {
        this.mc102_product_imgList = mc102_product_imgList;
    }

    public List<Mc103_product_set> getMc103_product_sets() {
        return mc103_product_sets;
    }

    public void setMc103_product_sets(List<Mc103_product_set> mc103_product_sets) {
        this.mc103_product_sets = mc103_product_sets;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getSizeType() {
        return sizeType;
    }

    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    public Integer getSizeWeight() {
        return sizeWeight;
    }

    public void setSizeWeight(Integer sizeWeight) {
        this.sizeWeight = sizeWeight;
    }

    public String getWh_location_nm() {
        return wh_location_nm;
    }

    public void setWh_location_nm(String wh_location_nm) {
        this.wh_location_nm = wh_location_nm;
    }

    public Integer getStock_cnt() {
        return stock_cnt;
    }

    public void setStock_cnt(Integer stock_cnt) {
        this.stock_cnt = stock_cnt;
    }

    public Integer getStock_total() {
        return stock_total;
    }

    public void setStock_total(Integer stock_total) {
        this.stock_total = stock_total;
    }

    public Integer getProduct_cnt() {
        return product_cnt;
    }

    public void setProduct_cnt(Integer product_cnt) {
        this.product_cnt = product_cnt;
    }

    public Integer getProduct_plan_cnt() {
        return product_plan_cnt;
    }

    public void setProduct_plan_cnt(Integer product_plan_cnt) {
        this.product_plan_cnt = product_plan_cnt;
    }

    public List<Mw405_product_location> getProductLocationList() {
        return productLocationList;
    }

    public void setProductLocationList(List<Mw405_product_location> productLocationList) {
        this.productLocationList = productLocationList;
    }

    public Boolean getRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(Boolean requestFlag) {
        this.requestFlag = requestFlag;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public Integer getProduct_set_inventoryCnt() {
        return product_set_inventoryCnt;
    }

    public void setProduct_set_inventoryCnt(Integer product_set_inventoryCnt) {
        this.product_set_inventoryCnt = product_set_inventoryCnt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBikou() {
        return bikou;
    }

    public void setBikou(String bikou) {
        this.bikou = bikou;
    }

    public Integer getKubun() {
        return kubun;
    }

    public void setKubun(Integer kubun) {
        this.kubun = kubun;
    }

    public Integer getSerial_flg() {
        return serial_flg;
    }

    public void setSerial_flg(Integer serial_flg) {
        this.serial_flg = serial_flg;
    }

    public Integer getShipmentNum() {
        return shipmentNum;
    }

    public void setShipmentNum(Integer shipmentNum) {
        this.shipmentNum = shipmentNum;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public Integer getProduct_cd() {
        return product_cd;
    }

    public void setProduct_cd(Integer product_cd) {
        this.product_cd = product_cd;
    }

    public Integer getSort_no() {
        return sort_no;
    }

    public void setSort_no(Integer sort_no) {
        this.sort_no = sort_no;
    }

    public Integer getNtm_price() {
        return ntm_price;
    }

    public void setNtm_price(Integer ntm_price) {
        this.ntm_price = ntm_price;
    }

    public Integer getEccube_show_flg() {
        return eccube_show_flg;
    }

    public void setEccube_show_flg(Integer eccube_show_flg) {
        this.eccube_show_flg = eccube_show_flg;
    }

    public String getNtm_memo() {
        return ntm_memo;
    }

    public void setNtm_memo(String ntm_memo) {
        this.ntm_memo = ntm_memo;
    }

    @Override
    public String toString() {
        return "Mc100_product{" +
            "client_id='" + client_id + '\'' +
            ", product_id='" + product_id + '\'' +
            ", warehouse_cd='" + warehouse_cd + '\'' +
            ", name='" + name + '\'' +
            ", english_name='" + english_name + '\'' +
            ", code='" + code + '\'' +
            ", set_flg=" + set_flg +
            ", set_sub_id=" + set_sub_id +
            ", barcode='" + barcode + '\'' +
            ", bundled_flg=" + bundled_flg +
            ", is_reduced_tax=" + is_reduced_tax +
            ", tax_flag=" + tax_flag +
            ", price=" + price +
            ", cost_price=" + cost_price +
            ", identifier='" + identifier + '\'' +
            ", description_cd='" + description_cd + '\'' +
            ", origin='" + origin + '\'' +
            ", size_cd='" + size_cd + '\'' +
            ", tags_id='" + tags_id + '\'' +
            ", show_flg=" + show_flg +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", mc101_product_tag=" + mc101_product_tag +
            ", tags=" + Arrays.toString(tags) +
            ", tw300_stock=" + tw300_stock +
            ", tw301_stock_history=" + tw301_stock_history +
            ", mc102_product_imgList=" + mc102_product_imgList +
            ", mc103_product_sets=" + mc103_product_sets +
            ", sizeName='" + sizeName + '\'' +
            ", sizeType='" + sizeType + '\'' +
            ", sizeWeight=" + sizeWeight +
            ", wh_location_nm='" + wh_location_nm + '\'' +
            ", stock_cnt=" + stock_cnt +
            ", stock_total=" + stock_total +
            ", product_cnt=" + product_cnt +
            ", product_plan_cnt=" + product_plan_cnt +
            ", productLocationList=" + productLocationList +
            ", requestFlag=" + requestFlag +
            ", weight=" + weight +
            ", img1='" + img1 + '\'' +
            ", img2='" + img2 + '\'' +
            ", img3='" + img3 + '\'' +
            ", product_set_inventoryCnt=" + product_set_inventoryCnt +
            ", url='" + url + '\'' +
            ", bikou='" + bikou + '\'' +
            ", kubun=" + kubun +
            ", serial_flg=" + serial_flg +
            ", shipmentNum=" + shipmentNum +
            ", product_type='" + product_type + '\'' +
            ", product_cd=" + product_cd +
            ", sort_no=" + sort_no +
            ", ntm_price=" + ntm_price +
            ", eccube_show_flg=" + eccube_show_flg +
            '}';
    }
}
