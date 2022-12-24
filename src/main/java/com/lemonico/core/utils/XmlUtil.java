package com.lemonico.core.utils;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;

/**
 * XMLツール
 *
 * @since 1.0.0
 */
public class XmlUtil
{

    private static XmlMapper xmlMapper = null;

    /**
     * XmlMapperインスタンスを取得する
     *
     * @return {@link XmlMapper}
     * @since 1.0.0
     */
    public static XmlMapper getXmlMapperInstance() {
        if (xmlMapper != null) {
            return xmlMapper;
        }
        xmlMapper = new XmlMapper();
        return xmlMapper;
    }

    /**
     * XMLファイルをJAVAオブジェクトに転換する
     *
     * @param builder {@link XmlMapper}
     * @param xmlString 転換XML
     * @param clazz JAVAクラス
     * @return JAVAオブジェクト
     * @throws IOException IO異常
     */
    public static Object xml2Bean(XmlMapper builder, String xmlString, Class clazz) throws IOException {
        return builder.readValue(xmlString, clazz);
    }

    /**
     * java对象转xml文件
     *
     * @param builder xml构造器
     * @param object 要转换的java对象
     * @return String
     * @throws JsonProcessingException Json異常
     **/
    public static String bean2Xml(XmlMapper builder, Object object) throws JsonProcessingException {
        return builder.writeValueAsString(object);
    }
}
