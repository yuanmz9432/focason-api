/**
 * GetResponseExternalItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public class GetResponseExternalItem implements java.io.Serializable
{
    private GetResponseExternalItemDetail[] getResponseExternalItemDetail;

    private int inventoryType;

    private String itemNumber;

    private String itemUrl;

    private int nokoriThreshold;

    private int restTypeFlag;

    public GetResponseExternalItem() {}

    public GetResponseExternalItem(
        GetResponseExternalItemDetail[] getResponseExternalItemDetail,
        int inventoryType,
        String itemNumber,
        String itemUrl,
        int nokoriThreshold,
        int restTypeFlag) {
        this.getResponseExternalItemDetail = getResponseExternalItemDetail;
        this.inventoryType = inventoryType;
        this.itemNumber = itemNumber;
        this.itemUrl = itemUrl;
        this.nokoriThreshold = nokoriThreshold;
        this.restTypeFlag = restTypeFlag;
    }


    /**
     * Gets the getResponseExternalItemDetail value for this GetResponseExternalItem.
     * 
     * @return getResponseExternalItemDetail
     */
    public GetResponseExternalItemDetail[] getGetResponseExternalItemDetail() {
        return getResponseExternalItemDetail;
    }


    /**
     * Sets the getResponseExternalItemDetail value for this GetResponseExternalItem.
     * 
     * @param getResponseExternalItemDetail
     */
    public void setGetResponseExternalItemDetail(GetResponseExternalItemDetail[] getResponseExternalItemDetail) {
        this.getResponseExternalItemDetail = getResponseExternalItemDetail;
    }


    /**
     * Gets the inventoryType value for this GetResponseExternalItem.
     * 
     * @return inventoryType
     */
    public int getInventoryType() {
        return inventoryType;
    }


    /**
     * Sets the inventoryType value for this GetResponseExternalItem.
     * 
     * @param inventoryType
     */
    public void setInventoryType(int inventoryType) {
        this.inventoryType = inventoryType;
    }


    /**
     * Gets the itemNumber value for this GetResponseExternalItem.
     * 
     * @return itemNumber
     */
    public String getItemNumber() {
        return itemNumber;
    }


    /**
     * Sets the itemNumber value for this GetResponseExternalItem.
     * 
     * @param itemNumber
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }


    /**
     * Gets the itemUrl value for this GetResponseExternalItem.
     * 
     * @return itemUrl
     */
    public String getItemUrl() {
        return itemUrl;
    }


    /**
     * Sets the itemUrl value for this GetResponseExternalItem.
     * 
     * @param itemUrl
     */
    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }


    /**
     * Gets the nokoriThreshold value for this GetResponseExternalItem.
     * 
     * @return nokoriThreshold
     */
    public int getNokoriThreshold() {
        return nokoriThreshold;
    }


    /**
     * Sets the nokoriThreshold value for this GetResponseExternalItem.
     * 
     * @param nokoriThreshold
     */
    public void setNokoriThreshold(int nokoriThreshold) {
        this.nokoriThreshold = nokoriThreshold;
    }


    /**
     * Gets the restTypeFlag value for this GetResponseExternalItem.
     * 
     * @return restTypeFlag
     */
    public int getRestTypeFlag() {
        return restTypeFlag;
    }


    /**
     * Sets the restTypeFlag value for this GetResponseExternalItem.
     * 
     * @param restTypeFlag
     */
    public void setRestTypeFlag(int restTypeFlag) {
        this.restTypeFlag = restTypeFlag;
    }

    private Object __equalsCalc = null;

    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetResponseExternalItem)) {
            return false;
        }
        GetResponseExternalItem other = (GetResponseExternalItem) obj;
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
        _equals = true &&
            ((this.getResponseExternalItemDetail == null && other.getGetResponseExternalItemDetail() == null) ||
                (this.getResponseExternalItemDetail != null &&
                    java.util.Arrays.equals(this.getResponseExternalItemDetail,
                        other.getGetResponseExternalItemDetail())))
            &&
            this.inventoryType == other.getInventoryType() &&
            ((this.itemNumber == null && other.getItemNumber() == null) ||
                (this.itemNumber != null &&
                    this.itemNumber.equals(other.getItemNumber())))
            &&
            ((this.itemUrl == null && other.getItemUrl() == null) ||
                (this.itemUrl != null &&
                    this.itemUrl.equals(other.getItemUrl())))
            &&
            this.nokoriThreshold == other.getNokoriThreshold() &&
            this.restTypeFlag == other.getRestTypeFlag();
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
        int _hashCode = 1;
        if (getGetResponseExternalItemDetail() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getGetResponseExternalItemDetail()); i++) {
                Object obj = java.lang.reflect.Array.get(getGetResponseExternalItemDetail(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getInventoryType();
        if (getItemNumber() != null) {
            _hashCode += getItemNumber().hashCode();
        }
        if (getItemUrl() != null) {
            _hashCode += getItemUrl().hashCode();
        }
        _hashCode += getNokoriThreshold();
        _hashCode += getRestTypeFlag();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetResponseExternalItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getResponseExternalItemDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "getResponseExternalItemDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItemDetail"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItemDetail"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inventoryType");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "inventoryType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemNumber");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "itemNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemUrl");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "itemUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nokoriThreshold");
        elemField.setXmlName(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "nokoriThreshold"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("restTypeFlag");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "restTypeFlag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
