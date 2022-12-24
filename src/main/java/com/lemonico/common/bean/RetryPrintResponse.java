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
 * @class RetryPrintResponse
 * @description
 * @date 2021/7/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "retryPrintResponse")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetryPrintResponse
{

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlProperty(localName = "printRequestId")
    private String printRequestId;

    @JacksonXmlElementWrapper(localName = "printDataList")
    @JacksonXmlProperty(localName = "printDataDetail")
    private List<ErrorShippingNumberDetail> errorShippingNumberList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorShippingNumberDetail
    {

        @JacksonXmlProperty(localName = "shippingNumber")
        String shippingNumber;

        @JacksonXmlElementWrapper(localName = "resultCodeList")
        @JacksonXmlProperty(localName = "resultCode")
        List<String> resultCodeList;
    }
}
