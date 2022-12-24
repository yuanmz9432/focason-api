package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

/**
 * @class RetryPrintRequest
 * @description
 * @date 2021/7/20
 **/
@Data
@JacksonXmlRootElement(localName = "retryPrintRequest")
public class RetryPrintRequest
{

    @JacksonXmlProperty(localName = "customerAuth")
    private CustomerAuth customerAuth;

    @JacksonXmlElementWrapper(localName = "shippingNumberList")
    @JacksonXmlProperty(localName = "shippingNumber")
    private List<String> printRequestIdList;

    @Data
    public class CustomerAuth
    {
        String customerId;
        String loginPassword;
    }
}
