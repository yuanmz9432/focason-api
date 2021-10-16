/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.handler;



import api.lemonico.core.attribute.LcErrorCode;
import api.lemonico.core.attribute.LcErrorResource;
import api.lemonico.core.exception.LcException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(value = LcException.class)
    @ResponseBody
    public ResponseEntity<LcErrorResource> lcExceptionHandler(Exception e) {
        LcException lcException = (LcException) e;
        String code = lcException.getCode().getValue();
        return ResponseEntity
            .status(Integer.parseInt(code.substring(1, 4)))
            .body(LcErrorResource.builder()
                .code(code)
                .message(e.getMessage()).build());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<LcErrorResource> defaultExceptionHandler(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(LcErrorResource.builder()
                .code(LcErrorCode.INTERNAL_SERVER_ERROR.getValue())
                .message(e.getMessage()).build());
    }
}
