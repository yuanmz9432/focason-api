/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.exception;



import com.blazeash.api.core.attribute.BaErrorCode;

public class BaResourceAlreadyExistsException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final BaErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Resource '%s' specified '%s' was already exists.";

    static {
        ERROR_CODE = BaErrorCode.DUPLICATE_ENTRY;
    }

    public BaResourceAlreadyExistsException(Class<?> resourceClass, String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, resourceClass.getSimpleName(), email);
    }
}
