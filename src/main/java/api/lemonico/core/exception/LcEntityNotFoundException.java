/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.exception;



import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcErrorCode;

public class LcEntityNotFoundException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Entity '%s' specified '%s' = '%s' does not exists.";

    static {
        ERROR_CODE = LcErrorCode.RESOURCE_NOT_FOUND;
    }

    public LcEntityNotFoundException(Class<?> entityClass, ID<?> id) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "id", id);
    }

    public LcEntityNotFoundException(Class<?> entityClass, String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), "email", email);
    }
}
