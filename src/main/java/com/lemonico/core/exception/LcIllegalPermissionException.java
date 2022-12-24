/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class LcIllegalPermissionException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Permission '%s' specified does not exists.";

    static {
        ERROR_CODE = LcErrorCode.RESOURCE_NOT_FOUND;
    }

    public LcIllegalPermissionException(Class<?> entityClass, Object permission) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), permission);
    }
}
