/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class FsIllegalPermissionException extends FsException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Permission '%s' specified does not exists.";

    static {
        ERROR_CODE = FsErrorCode.RESOURCE_NOT_FOUND;
    }

    public FsIllegalPermissionException(Class<?> entityClass, Object permission) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), permission);
    }
}
