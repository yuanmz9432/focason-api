package com.lemonico.core.handler;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * グローバル異常ハンドラー
 *
 * @since 1.0.0
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler
{

    /**
     * {@link BaseException}ハンドラー
     *
     * @param exception 異常
     * @return {@link PlErrorResource}
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResponseEntity<PlErrorResource> lcExceptionHandler(Exception exception) {
        BaseException lcException = (BaseException) exception;
        final String code = lcException.getCode().getValue();
        final String message = exception.getMessage();
        // TODO PlErrorCodeの定義を修正できたら、こちらのロジックを削除する
        final int status = code.substring(1, 4).contains("_") ? 200 : Integer.parseInt(code.substring(1, 4));

        return ResponseEntity
            .status(status)
            .body(PlErrorResource.builder()
                .code(code)
                .message(message).build());
    }

    /**
     * {@link Exception}ハンドラー
     *
     * @param exception 異常
     * @return {@link PlErrorResource}
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<PlErrorResource> defaultExceptionHandler(Exception exception) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(PlErrorResource.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getValue())
                .message(exception.getMessage()).build());
    }

}
