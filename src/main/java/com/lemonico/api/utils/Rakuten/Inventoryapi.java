/**
 * Inventoryapi.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public interface Inventoryapi extends javax.xml.rpc.Service
{
    public String getinventoryapiPortAddress();

    public InventoryapiPort_PortType getinventoryapiPort() throws javax.xml.rpc.ServiceException;

    public InventoryapiPort_PortType getinventoryapiPort(java.net.URL portAddress)
        throws javax.xml.rpc.ServiceException;
}
