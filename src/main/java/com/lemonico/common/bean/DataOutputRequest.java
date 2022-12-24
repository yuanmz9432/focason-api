package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

/**
 * @class DataOutputRequest
 * @description
 * @date 2021/10/28
 **/
@Data
@JacksonXmlRootElement(localName = "dataOutputRequest")
public class DataOutputRequest
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
