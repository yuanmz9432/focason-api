/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class BaResourceAlreadyExistsException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Resource '%s' specified '%s' was already exists.";

    static {
        ERROR_CODE = FsErrorCode.DUPLICATE_ENTRY;
    }

    public BaResourceAlreadyExistsException(Class<?> resourceClass, String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, resourceClass.getSimpleName(), email);
    }
}
