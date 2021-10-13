package api.lemonico.core.exception;

public class LcException extends RuntimeException
{
    private final LcErrorCode code;

    public LcException(LcErrorCode code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
    }

    public LcException(LcErrorCode code, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
    }

    public LcErrorCode getCode() {
        return this.code;
    }
}
