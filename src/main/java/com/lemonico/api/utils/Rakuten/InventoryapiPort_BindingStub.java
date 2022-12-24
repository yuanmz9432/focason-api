package com.lemonico.api.utils.Rakuten;

/**
 * InventoryapiPort_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

public class InventoryapiPort_BindingStub extends org.apache.axis.client.Stub implements InventoryapiPort_PortType
{
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc[] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1() {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getInventoryExternal");
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "externalUserAuthModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "ExternalUserAuthModel"),
            ExternalUserAuthModel.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "getRequestExternalModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "GetRequestExternalModel"),
            GetRequestExternalModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalModel"));
        oper.setReturnClass(GetResponseExternalModel.class);
        oper.setReturnQName(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateInventoryExternal");
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "externalUserAuthModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "ExternalUserAuthModel"),
            ExternalUserAuthModel.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "updateRequestExternalModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "UpdateRequestExternalModel"),
            UpdateRequestExternalModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalModel"));
        oper.setReturnClass(UpdateResponseExternalModel.class);
        oper.setReturnQName(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateSingleInventoryExternal");
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "externalUserAuthModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "ExternalUserAuthModel"),
            ExternalUserAuthModel.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi",
                "updateRequestExternalModel"),
            org.apache.axis.description.ParameterDesc.IN,
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
                "UpdateSingleRequestExternalModel"),
            UpdateSingleRequestExternalModel.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalModel"));
        oper.setReturnClass(UpdateResponseExternalModel.class);
        oper.setReturnQName(
            new javax.xml.namespace.QName("https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

    }

    public InventoryapiPort_BindingStub() throws org.apache.axis.AxisFault {
        this(null);
    }

    public InventoryapiPort_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
        throws org.apache.axis.AxisFault {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public InventoryapiPort_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
        Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ArrayOfGetResponseExternalItem");
        cachedSerQNames.add(qName);
        cls = GetResponseExternalItem[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItem");
        qName2 = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItem");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ArrayOfGetResponseExternalItemDetail");
        cachedSerQNames.add(qName);
        cls = GetResponseExternalItemDetail[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItemDetail");
        qName2 = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItemDetail");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ArrayOfUpdateRequestExternalItem");
        cachedSerQNames.add(qName);
        cls = UpdateRequestExternalItem[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateRequestExternalItem");
        qName2 = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateRequestExternalItem");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ArrayOfUpdateResponseExternalItem");
        cachedSerQNames.add(qName);
        cls = UpdateResponseExternalItem[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalItem");
        qName2 = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalItem");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "ExternalUserAuthModel");
        cachedSerQNames.add(qName);
        cls = ExternalUserAuthModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetRequestExternalModel");
        cachedSerQNames.add(qName);
        cls = GetRequestExternalModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItem");
        cachedSerQNames.add(qName);
        cls = GetResponseExternalItem.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalItemDetail");
        cachedSerQNames.add(qName);
        cls = GetResponseExternalItemDetail.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "GetResponseExternalModel");
        cachedSerQNames.add(qName);
        cls = GetResponseExternalModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName =
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "RequestModel");
        cachedSerQNames.add(qName);
        cls = RequestModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName =
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "ResponseModel");
        cachedSerQNames.add(qName);
        cls = ResponseModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateRequestExternalItem");
        cachedSerQNames.add(qName);
        cls = UpdateRequestExternalItem.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateRequestExternalModel");
        cachedSerQNames.add(qName);
        cls = UpdateRequestExternalModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalItem");
        cachedSerQNames.add(qName);
        cls = UpdateResponseExternalItem.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateResponseExternalModel");
        cachedSerQNames.add(qName);
        cls = UpdateResponseExternalModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateSingleRequestExternalItem");
        cachedSerQNames.add(qName);
        cls = UpdateSingleRequestExternalItem.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity",
            "UpdateSingleRequestExternalModel");
        cachedSerQNames.add(qName);
        cls = UpdateSingleRequestExternalModel.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName =
            new javax.xml.namespace.QName("java:jp.co.rakuten.rms.mall.inventoryapi.v1.model.entity", "UserAuthBase");
        cachedSerQNames.add(qName);
        cls = UserAuthBase.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("java:language_builtins.lang", "ArrayOfString");
        cachedSerQNames.add(qName);
        cls = String[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
        qName2 = new javax.xml.namespace.QName("java:language_builtins.lang", "string");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                            (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            Class sf = (Class) cachedSerFactories.get(i);
                            Class df = (Class) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        } else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf =
                                (org.apache.axis.encoding.SerializerFactory) cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df =
                                (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        } catch (Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    @Override
    public GetResponseExternalModel getInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        GetRequestExternalModel getRequestExternalModel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName(
            "https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "getInventoryExternal"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[] {
                externalUserAuthModel, getRequestExternalModel
            });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (GetResponseExternalModel) _resp;
                } catch (Exception _exception) {
                    return (GetResponseExternalModel) org.apache.axis.utils.JavaUtils.convert(_resp,
                        GetResponseExternalModel.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    @Override
    public UpdateResponseExternalModel updateInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        UpdateRequestExternalModel updateRequestExternalModel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName(
            "https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "updateInventoryExternal"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[] {
                externalUserAuthModel, updateRequestExternalModel
            });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (UpdateResponseExternalModel) _resp;
                } catch (Exception _exception) {
                    return (UpdateResponseExternalModel) org.apache.axis.utils.JavaUtils.convert(_resp,
                        UpdateResponseExternalModel.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    @Override
    public UpdateResponseExternalModel updateSingleInventoryExternal(ExternalUserAuthModel externalUserAuthModel,
        UpdateSingleRequestExternalModel updateRequestExternalModel) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName(
            "https://inventoryapi.rms.rakuten.co.jp/rms/mall/inventoryapi", "updateSingleInventoryExternal"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[] {
                externalUserAuthModel, updateRequestExternalModel
            });

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                try {
                    return (UpdateResponseExternalModel) _resp;
                } catch (Exception _exception) {
                    return (UpdateResponseExternalModel) org.apache.axis.utils.JavaUtils.convert(_resp,
                        UpdateResponseExternalModel.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

}
