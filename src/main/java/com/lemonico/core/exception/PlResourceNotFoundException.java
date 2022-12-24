package com.lemonico.core.exception;



public class PlResourceNotFoundException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE_1 = "『%s』に関する情報は存在しません。";
    private static final String MESSAGE_TEMPLATE_2 = "Resource '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = PlErrorCode.RESOURCE_NOT_FOUND;
    }

    public PlResourceNotFoundException(Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE_1, object);
    }


    public PlResourceNotFoundException(Class<?> resourceClass, Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE_2, resourceClass.getSimpleName(), object);
    }
}
