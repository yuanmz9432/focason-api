package api.lemonico.exception;

import javax.servlet.http.HttpServletRequest;
import api.lemonico.model.BaseAPIResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(LemonicoAPIException.class)
	protected BaseAPIResponse handleBusinessException(HttpServletRequest req, LemonicoAPIException ex) {
		log.info("LemonicoAPIException-ErrorCode: {}", ex.getMessage());
		return BaseAPIResponse.failure(ex.getResponseCode());
	}
}
