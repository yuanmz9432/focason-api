package api.lemonico.exception;



import api.lemonico.core.attribute.ID;

public class LcEntityNotFoundException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Entity '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = LcErrorCode.RESOURCE_NOT_FOUND;
    }

    public LcEntityNotFoundException(Class<?> entityClass, ID<?> id) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, entityClass.getSimpleName(), id);
    }
}
