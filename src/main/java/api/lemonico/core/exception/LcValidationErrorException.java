package api.lemonico.core.exception;

public class LcValidationErrorException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;

    static {
        ERROR_CODE = LcErrorCode.VALIDATION_ERROR;
    }

    public LcValidationErrorException(String messageTemplate, Object... args) {
        super(ERROR_CODE, messageTemplate, args);
    }

    public LcValidationErrorException(Throwable cause, String messageTemplate, Object... args) {
        super(ERROR_CODE, cause, messageTemplate, args);
    }
}
