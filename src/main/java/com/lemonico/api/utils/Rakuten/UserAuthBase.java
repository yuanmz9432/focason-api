/**
 * UserAuthBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public class UserAuthBase implements java.io.Serializable
{
    private String authKey;

    private String userName;

    public UserAuthBase() {}

    public UserAuthBase(
        String authKey,
        String userName) {
        this.authKey = authKey;
        this.userName = userName;
    }


    /**
     * Gets the authKey value for this UserAuthBase.
     * 
     * @return authKey
     */
    public String getAuthKey() {
        return authKey;
    }


    /**
     * Sets the authKey value for this UserAuthBase.
     * 
     * @param authKey
     */
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }


    /**
     * Gets the userName value for this UserAuthBase.
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this UserAuthBase.
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UserAuthBase))
            return false;
        UserAuthBase other = (UserAuthBase) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.authKey == null && other.getAuthKey() == null) ||
                (this.authKey != null &&
                    this.authKey.equals(other.getAuthKey())))
            &&
            ((this.userName == null && other.getUserName() == null) ||
                (this.userName != null &&
                    this.userName.equals(other.getUserName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAuthKey() != null) {
            _hashCode += getAuthKey().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserAuthBase.class, true);

    static {
        typeDesc.setXmlType(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "UserAuthBase"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authKey");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "authKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "userName"));
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
