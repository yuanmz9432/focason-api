/**
 * GetRequestExternalModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public class GetRequestExternalModel extends RequestModel implements java.io.Serializable
{
    private String inventorySearchRange;

    private String[] itemUrl;

    public GetRequestExternalModel() {}

    public GetRequestExternalModel(
        String inventorySearchRange,
        String[] itemUrl) {
        this.inventorySearchRange = inventorySearchRange;
        this.itemUrl = itemUrl;
    }


    /**
     * Gets the inventorySearchRange value for this GetRequestExternalModel.
     * 
     * @return inventorySearchRange
     */
    public String getInventorySearchRange() {
        return inventorySearchRange;
    }


    /**
     * Sets the inventorySearchRange value for this GetRequestExternalModel.
     * 
     * @param inventorySearchRange
     */
    public void setInventorySearchRange(String inventorySearchRange) {
        this.inventorySearchRange = inventorySearchRange;
    }


    /**
     * Gets the itemUrl value for this GetRequestExternalModel.
     * 
     * @return itemUrl
     */
    public String[] getItemUrl() {
        return itemUrl;
    }


    /**
     * Sets the itemUrl value for this GetRequestExternalModel.
     * 
     * @param itemUrl
     */
    public void setItemUrl(String[] itemUrl) {
        this.itemUrl = itemUrl;
    }

    private Object __equalsCalc = null;

    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetRequestExternalModel)) {
            return false;
        }
        GetRequestExternalModel other = (GetRequestExternalModel) obj;
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
            ((this.inventorySearchRange == null && other.getInventorySearchRange() == null) ||
                (this.inventorySearchRange != null &&
                    this.inventorySearchRange.equals(other.getInventorySearchRange())))
            &&
            ((this.itemUrl == null && other.getItemUrl() == null) ||
                (this.itemUrl != null &&
                    java.util.Arrays.equals(this.itemUrl, other.getItemUrl())));
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
        if (getInventorySearchRange() != null) {
            _hashCode += getInventorySearchRange().hashCode();
        }
        if (getItemUrl() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getItemUrl()); i++) {
                Object obj = java.lang.reflect.Array.get(getItemUrl(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetRequestExternalModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetRequestExternalModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inventorySearchRange");
        elemField.setXmlName(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "inventorySearchRange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemUrl");
        elemField.setXmlName(
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "itemUrl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("java:language_builtins.lang", "string"));
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
