/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;
import com.focason.api.core.attribute.ID;

public class FsEntityNotFoundException extends FsException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Entity '%s' specified '%s' = '%s' does not exists.";

    static {
        ERROR_CODE = FsErrorCode.RESOURCE_NOT_FOUND;
    }

    public FsEntityNotFoundException(Class<?> entityClass, ID<?> id) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "id", id);
    }

    public FsEntityNotFoundException(Class<?> entityClass, String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "email", email);
    }
}
