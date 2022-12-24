package com.lemonico.core.exception;



public class LcResourceNotFoundException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE_1 = "『%s』に関する情報は存在しません。";
    private static final String MESSAGE_TEMPLATE_2 = "Resource '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = LcErrorCode.RESOURCE_NOT_FOUND;
    }

    public LcResourceNotFoundException(Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE_1, object);
    }


    public LcResourceNotFoundException(Class<?> resourceClass, Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE_2, resourceClass.getSimpleName(), object);
    }
}
