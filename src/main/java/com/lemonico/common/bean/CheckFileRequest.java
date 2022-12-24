package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

/**
 * @class CheckFileRequest
 * @description
 * @date 2021/6/4
 **/
@Data
@JacksonXmlRootElement(localName = "checkFileRequest")
public class CheckFileRequest
{

    @JacksonXmlProperty(localName = "customerAuth")
    private CustomerAuth customerAuth;

    @JacksonXmlElementWrapper(localName = "printRequestIdList")
    @JacksonXmlProperty(localName = "printRequestId")
    private List<String> printRequestIdList;


    @Data
    public class CustomerAuth
    {
        String customerId;
        String loginPassword;
    }
}
