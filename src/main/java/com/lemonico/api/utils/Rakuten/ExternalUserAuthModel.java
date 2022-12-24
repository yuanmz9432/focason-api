/**
 * ExternalUserAuthModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public class ExternalUserAuthModel extends UserAuthBase implements java.io.Serializable
{
    private String shopUrl;

    public ExternalUserAuthModel() {}

    public ExternalUserAuthModel(
        String authKey,
        String userName,
        String shopUrl) {
        super(
            authKey,
            userName);
        this.shopUrl = shopUrl;
    }


    /**
     * Gets the shopUrl value for this ExternalUserAuthModel.
     * 
     * @return shopUrl
     */
    public String getShopUrl() {
        return shopUrl;
    }


    /**
     * Sets the shopUrl value for this ExternalUserAuthModel.
     * 
     * @param shopUrl
     */
    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    private Object __equalsCalc = null;

    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ExternalUserAuthModel)) {
            return false;
        }
        ExternalUserAuthModel other = (ExternalUserAuthModel) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
            ((this.shopUrl == null && other.getShopUrl() == null) ||
                (this.shopUrl != null &&
                    this.shopUrl.equals(other.getShopUrl())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    @Override
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getShopUrl() != null) {
            _hashCode += getShopUrl().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExternalUserAuthModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ExternalUserAuthModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shopUrl");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "shopUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
        String mechType,
        Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
        String mechType,
        Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}