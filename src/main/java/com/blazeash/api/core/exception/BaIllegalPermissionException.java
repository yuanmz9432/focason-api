/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.exception;



import com.blazeash.api.core.attribute.BaErrorCode;

public class BaIllegalPermissionException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final BaErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Permission '%s' specified does not exists.";

    static {
        ERROR_CODE = BaErrorCode.RESOURCE_NOT_FOUND;
    }

    public BaIllegalPermissionException(Class<?> entityClass, Object permission) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), permission);
    }
}
