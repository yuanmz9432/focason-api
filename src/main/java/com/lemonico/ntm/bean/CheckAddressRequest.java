package com.lemonico.ntm.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @class CheckAddressRequest
 * @description 佐川急便マスタ参照 请求参数
 * @date 2021/6/17
 **/
@Data
@JacksonXmlRootElement(localName = "checkAddressRequest")
public class CheckAddressRequest
{

    // 用户认证（必需）
    @JacksonXmlProperty(localName = "customerAuth")
    private CustomerAuth customerAuth;

    // リクエスト郵便番号
    @JacksonXmlProperty(localName = "requestYubin")
    private String requestYubin;

    // リクエスト住所
    @JacksonXmlProperty(localName = "requestAddress")
    private String requestAddress;

    // 配送会社コード
    // 利用する配送会社を選択します 0001/null/空白 - 佐川急便
    @JacksonXmlProperty(localName = "deliveryCode")
    private String deliveryCode;

    @Data
    public class CustomerAuth
    {
        // 客户ID（必需）
        String customerId;
        // 登录密码（必需）
        String loginPassword;
    }

}
