/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.exception;



import api.lemonico.core.attribute.ID;

public class LcResourceNotFoundException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Resource '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = LcErrorCode.RESOURCE_NOT_FOUND;
    }

    public LcResourceNotFoundException(Class<?> resourceClass, ID<?> id) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, resourceClass.getSimpleName(), id);
    }
}
