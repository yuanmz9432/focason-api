/**
 * InventoryapiPort_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.lemonico.api.utils.Rakuten;

public interface InventoryapiPort_PortType extends java.rmi.Remote
{
    public GetResponseExternalModel getInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        GetRequestExternalModel getRequestExternalModel) throws java.rmi.RemoteException;

    public UpdateResponseExternalModel updateInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        UpdateRequestExternalModel updateRequestExternalModel) throws java.rmi.RemoteException;

    public UpdateResponseExternalModel updateSingleInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        UpdateSingleRequestExternalModel updateRequestExternalModel) throws java.rmi.RemoteException;
}
