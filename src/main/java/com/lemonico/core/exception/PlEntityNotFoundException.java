/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;



import com.lemonico.core.attribute.ID;

public class PlEntityNotFoundException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Entity '%s' specified '%s' = '%s' does not exists.";

    static {
        ERROR_CODE = PlErrorCode.RESOURCE_NOT_FOUND;
    }

    public PlEntityNotFoundException(Class<?> entityClass, ID<?> id) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "id", id);
    }

    public PlEntityNotFoundException(Class<?> entityClass, String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "email", email);
    }
}
