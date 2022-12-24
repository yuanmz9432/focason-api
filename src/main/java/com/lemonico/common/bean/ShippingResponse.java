package com.lemonico.common.bean;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class shippingResponse
 * @description
 * @date 2021/6/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "shippingResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingResponse
{
    private String printOutFlg;
    private String okuriCode;
    private String outputLevel;
    private String resultCode;
    private String printRequestId;
    @JacksonXmlElementWrapper(localName = "printDataList")
    @JacksonXmlProperty(localName = "printDataDetail")
    private List<PrintDataDetail> printDataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrintDataDetail
    {
        @JacksonXmlProperty(localName = "userManageNumber")
        String userManageNumber;

        @JacksonXmlElementWrapper(localName = "resultCodeList")
        @JacksonXmlProperty(localName = "resultCode")
        List<String> resultCodeList;

        @JacksonXmlElementWrapper(localName = "shippingNumberList")
        @JacksonXmlProperty(localName = "shippingNumber")
        List<String> shippingNumberList;

        @JacksonXmlProperty(localName = "depotInfo")
        DepotInfo depotInfo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class DepotInfo
    {
        @JacksonXmlProperty(localName = "depotCode")
        String depotCode;
        @JacksonXmlProperty(localName = "depotName")
        String depotName;
        @JacksonXmlProperty(localName = "depotTel")
        String depotTel;
    }
}
