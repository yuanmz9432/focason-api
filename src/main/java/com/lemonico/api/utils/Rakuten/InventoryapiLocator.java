/**
 * InventoryapiLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;



import java.rmi.Remote;

public class InventoryapiLocator extends org.apache.axis.client.Service implements Inventoryapi
{

    public InventoryapiLocator() {}


    public InventoryapiLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public InventoryapiLocator(String wsdlLoc, javax.xml.namespace.QName sName)
        throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for inventoryapiPort
    private String inventoryapiPort_address = "https://api.rms.rakuten.co.jp/es/1.0/inventory/ws";

    @Override
    public String getinventoryapiPortAddress() {
        return inventoryapiPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String inventoryapiPortWSDDServiceName = "inventoryapiPort";

    public String getinventoryapiPortWSDDServiceName() {
        return inventoryapiPortWSDDServiceName;
    }

    public void setinventoryapiPortWSDDServiceName(String name) {
        inventoryapiPortWSDDServiceName = name;
    }

    @Override
    public InventoryapiPort_PortType getinventoryapiPort() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(inventoryapiPort_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getinventoryapiPort(endpoint);
    }

    @Override
    public InventoryapiPort_PortType getinventoryapiPort(java.net.URL portAddress)
        throws javax.xml.rpc.ServiceException {
        try {
            InventoryapiPort_BindingStub _stub = new InventoryapiPort_BindingStub(portAddress, this);
            _stub.setPortName(getinventoryapiPortWSDDServiceName());
            return (InventoryapiPort_PortType) _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setinventoryapiPortEndpointAddress(String address) {
        inventoryapiPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (InventoryapiPort_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                InventoryapiPort_BindingStub _stub =
                    new InventoryapiPort_BindingStub(new java.net.URL(inventoryapiPort_address), this);
                _stub.setPortName(getinventoryapiPortWSDDServiceName());
                return (Remote) _stub;
            }
        } catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
            + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
        throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("inventoryapiPort".equals(inputPortName)) {
            return getinventoryapiPort();
        } else {
            Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
            "inventoryapi");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "inventoryapiPort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address)
        throws javax.xml.rpc.ServiceException {

        if ("inventoryapiPort".equals(portName)) {
            setinventoryapiPortEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address)
        throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
