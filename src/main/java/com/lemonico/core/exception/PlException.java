package com.lemonico.core.exception;



import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ベース異常クラス
 *
 * @since 1.0.0
 */
@Getter
public class PlException extends RuntimeException
{

    private final static Logger logger = LoggerFactory.getLogger(PlException.class);
    private final PlErrorCode code;

    public PlException(PlErrorCode errorCode, String format, Object... args) {
        super(String.format(format, args));
        this.code = errorCode;
    }

    public PlException(PlErrorCode code, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
    }

    /**
     * 異常情報をコンソールにプリントにする
     *
     * @param throwable 異常
     * @return 異常情報
     * @since 1.0.0
     */
    public static String print(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        throwable.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

}
