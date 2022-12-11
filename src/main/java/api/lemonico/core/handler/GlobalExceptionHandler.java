/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.handler;



import api.lemonico.core.attribute.LcErrorCode;
import api.lemonico.core.attribute.LcErrorResource;
import api.lemonico.core.exception.LcException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<LcErrorResource> paramExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                FieldError fieldError = (FieldError) errors.get(0);
                String message = fieldError.getDefaultMessage();
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LcErrorResource.builder()
                        .code(LcErrorCode.VALIDATION_ERROR.getValue())
                        .message(message).build());
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
