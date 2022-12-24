package com.lemonico.store.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Objects;

/**
 * @className: ProductRecord
 * @description: 普通商品一览
 * @date: 2021/7/22 17:10
 **/
@ApiModel(value = "普通商品一览")
public class ProductRecord
{
    @ApiModelProperty(value = "商品ID", required = true)
    private String product_id;
    @ApiModelProperty(value = "商品画像")
    private List<String> product_img;
    @ApiModelProperty(value = "商品名")
    private String name;
    @ApiModelProperty(value = "商品コード")
    private String code;
    @ApiModelProperty(value = "バーコード")
    private String barcode;
    @ApiModelProperty(value = "商品原価")
    private Integer cost_price;
    @ApiModelProperty(value = "商品価格税込")
    private Integer price;
    @ApiModelProperty(value = "軽減税率適用商品")
    private Integer is_reduced_tax;
    @ApiModelProperty(value = "0: 税込 1:税抜")
    private Integer tax_flag;
    @ApiModelProperty(value = "理論在庫数")
    private Integer available_cnt;
    @ApiModelProperty(value = "出庫依頼中数")
    private Integer requesting_cnt;
    @ApiModelProperty(value = "実在庫数")
    private Integer inventory_cnt;
    @ApiModelProperty(value = "不可配送数")
    private Integer not_delivery;
    @ApiModelProperty(value = "依頼フラグ")
    private Boolean request_flag;
    @ApiModelProperty(value = "合計件数")
    private Long total_cont;
    @ApiModelProperty(value = "同梱物フラグ")
    private Integer bundled_flg;
    @ApiModelProperty(value = "セット商品フラグ")
    private Integer set_flg;
    @ApiModelProperty(value = "品名コード")
    private String description_cd;
    @ApiModelProperty(value = "商品サイズ")
    private String size_cd;
    @ApiModelProperty(value = "重量")
    private Double weight;
    @ApiModelProperty(value = "原産国")
    private String origin;
    @ApiModelProperty(value = "商品英語名")
    private String english_name;
    @ApiModelProperty(value = "タグ")
    private String tags;
    @ApiModelProperty(value = "QRコード")
    private String url;
    @ApiModelProperty(value = "備考")
    private String bikou;

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public Integer getAvailable_cnt() {
        return available_cnt;
    }

    public void setAvailable_cnt(Integer available_cnt) {
        this.available_cnt = available_cnt;
    }

    public Integer getRequesting_cnt() {
        return requesting_cnt;
    }

    public void setRequesting_cnt(Integer requesting_cnt) {
        this.requesting_cnt = requesting_cnt;
    }

    public Integer getInventory_cnt() {
        return inventory_cnt;
    }

    public void setInventory_cnt(Integer inventory_cnt) {
        this.inventory_cnt = inventory_cnt;
    }

    public Integer getNot_delivery() {
        return not_delivery;
    }

    public void setNot_delivery(Integer not_delivery) {
        this.not_delivery = not_delivery;
    }

    public Boolean getRequest_flag() {
        return request_flag;
    }

    public void setRequest_flag(Boolean request_flag) {
        this.request_flag = request_flag;
    }

    public Long getTotal_cont() {
        return total_cont;
    }

    public void setTotal_cont(Long total_cont) {
        this.total_cont = total_cont;
    }

    public Integer getBundled_flg() {
        return bundled_flg;
    }

    public void setBundled_flg(Integer bundled_flg) {
        this.bundled_flg = bundled_flg;
    }

    public Integer getSet_flg() {
        return set_flg;
    }

    public void setSet_flg(Integer set_flg) {
        this.set_flg = set_flg;
    }

    public Integer getCost_price() {
        return cost_price;
    }

    public void setCost_price(Integer cost_price) {
        this.cost_price = cost_price;
    }

    public String getDescription_cd() {
        return description_cd;
    }

    public void setDescription_cd(String description_cd) {
        this.description_cd = description_cd;
    }

    public String getSize_cd() {
        return size_cd;
    }

    public void setSize_cd(String size_cd) {
        this.size_cd = size_cd;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public List<String> getProduct_img() {
        return product_img;
    }

    public void setProduct_img(List<String> product_img) {
        this.product_img = product_img;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductRecord that = (ProductRecord) o;
        return Objects.equals(product_id, that.product_id) && Objects.equals(product_img, that.product_img)
            && Objects.equals(name, that.name) && Objects.equals(code, that.code)
            && Objects.equals(barcode, that.barcode) && Objects.equals(cost_price, that.cost_price)
            && Objects.equals(price, that.price) && Objects.equals(is_reduced_tax, that.is_reduced_tax)
            && Objects.equals(tax_flag, that.tax_flag) && Objects.equals(available_cnt, that.available_cnt)
            && Objects.equals(requesting_cnt, that.requesting_cnt) && Objects.equals(inventory_cnt, that.inventory_cnt)
            && Objects.equals(not_delivery, that.not_delivery) && Objects.equals(request_flag, that.request_flag)
            && Objects.equals(total_cont, that.total_cont) && Objects.equals(bundled_flg, that.bundled_flg)
            && Objects.equals(set_flg, that.set_flg) && Objects.equals(description_cd, that.description_cd)
            && Objects.equals(size_cd, that.size_cd) && Objects.equals(weight, that.weight)
            && Objects.equals(origin, that.origin) && Objects.equals(english_name, that.english_name)
            && Objects.equals(tags, that.tags) && Objects.equals(url, that.url) && Objects.equals(bikou, that.bikou);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product_id, product_img, name, code, barcode, cost_price, price, is_reduced_tax, tax_flag,
            available_cnt, requesting_cnt, inventory_cnt, not_delivery, request_flag, total_cont, bundled_flg, set_flg,
            description_cd, size_cd, weight, origin, english_name, tags, url, bikou);
    }

    @Override
    public String toString() {
        return "ProductRecord{" +
            "product_id='" + product_id + '\'' +
            ", product_img=" + product_img +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", barcode='" + barcode + '\'' +
            ", cost_price=" + cost_price +
            ", price=" + price +
            ", is_reduced_tax=" + is_reduced_tax +
            ", tax_flag=" + tax_flag +
            ", available_cnt=" + available_cnt +
            ", requesting_cnt=" + requesting_cnt +
            ", inventory_cnt=" + inventory_cnt +
            ", not_delivery=" + not_delivery +
            ", request_flag=" + request_flag +
            ", total_cont=" + total_cont +
            ", bundled_flg=" + bundled_flg +
            ", set_flg=" + set_flg +
            ", description_cd='" + description_cd + '\'' +
            ", size_cd='" + size_cd + '\'' +
            ", weight=" + weight +
            ", origin='" + origin + '\'' +
            ", english_name='" + english_name + '\'' +
            ", tags='" + tags + '\'' +
            ", url='" + url + '\'' +
            ", bikou='" + bikou + '\'' +
            '}';
    }
}
