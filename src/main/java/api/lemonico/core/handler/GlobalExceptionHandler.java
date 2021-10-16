package api.lemonico.core.handler;



import api.lemonico.core.exception.LcErrorResource;
import api.lemonico.core.exception.LcException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler
{
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = LcException.class)
    @ResponseBody
    public ResponseEntity<LcErrorResource> lcException(Exception e) {
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
    public Object defaultErrorHandler(HttpServletRequest req, Exception e) {
        logger.error("---DefaultException Handler---Host {} invokes url {} ERROR: {}", req.getRemoteHost(),
            req.getRequestURL(), e.getMessage());
        return e.getMessage();
    }
}
