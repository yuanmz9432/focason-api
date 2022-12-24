package com.lemonico.ntm.bean;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class CheckAddressResponse
 * @description 佐川急便マスタ参照 返回参数
 * @date 2021/6/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "checkAddressResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckAddressResponse
{
    // 処理結果コード
    private String resultCode;

    // リクエスト郵便番号
    private String requestYubin;

    // リクエスト住所
    private String requestAddress;

    // 配送会社コード
    private String deliveryCode;

    @JacksonXmlElementWrapper(localName = "addressList")
    @JacksonXmlProperty(localName = "addressInfo")
    private List<AddressInfo> addressList;

    @JacksonXmlElementWrapper(localName = "yubinList")
    @JacksonXmlProperty(localName = "yubin")
    private List<String> yubinList;

    // 営業所コード
    private String depotCode;

    // 営業所名
    private String depotName;

    // 営業所電話番号
    private String depotTel;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressInfo
    {
        // 都道府県
        private String todofukenName;
        // 市区郡町村名称
        private String shikuchosonName;
        // 市区郡町村名称
        private String choikiName;
    }

}
