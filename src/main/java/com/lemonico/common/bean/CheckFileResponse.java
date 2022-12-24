package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class CheckFileResponse
 * @description
 * @date 2021/6/4
 **/
@Data
@JacksonXmlRootElement(localName = "checkFileResponse")
public class CheckFileResponse
{

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlElementWrapper(localName = "printDataList")
    @JacksonXmlProperty(localName = "printDataDetail")
    private List<PrintDataDetail> printDataList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrintDataDetail
    {

        @JacksonXmlProperty(localName = "printRequestId")
        String printRequestId;
        @JacksonXmlProperty(localName = "createDate")
        String createDate;
        @JacksonXmlProperty(localName = "resultCode")
        String resultCode;
        @JacksonXmlProperty(localName = "url")
        String url;
    }
}
