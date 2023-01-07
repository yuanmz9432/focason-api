/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.handler;



import com.focason.api.core.attribute.FsErrorCode;
import com.focason.api.core.attribute.FsErrorResource;
import com.focason.api.core.exception.BaException;
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
    @ExceptionHandler(value = BaException.class)
    @ResponseBody
    public ResponseEntity<FsErrorResource> lcExceptionHandler(Exception e) {
        BaException lcException = (BaException) e;
        String code = lcException.getCode().getValue();
        return ResponseEntity
            .status(Integer.parseInt(code.substring(1, 4)))
            .body(FsErrorResource.builder()
                .code(code)
                .message(e.getMessage()).build());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<FsErrorResource> defaultExceptionHandler(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(FsErrorResource.builder()
                .code(FsErrorCode.INTERNAL_SERVER_ERROR.getValue())
                .message(e.getMessage()).build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<FsErrorResource> paramExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                FieldError fieldError = (FieldError) errors.get(0);
                String message = fieldError.getDefaultMessage();
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(FsErrorResource.builder()
                        .code(FsErrorCode.VALIDATION_ERROR.getValue())
                        .message(message).build());
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
