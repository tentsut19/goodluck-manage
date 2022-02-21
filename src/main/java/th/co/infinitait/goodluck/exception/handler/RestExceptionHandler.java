package th.co.infinitait.goodluck.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;
import th.co.infinitait.goodluck.component.CabsatPayload;
import th.co.infinitait.goodluck.exception.Error;
import th.co.infinitait.goodluck.exception.ErrorDetail;
import th.co.infinitait.goodluck.exception.MessageLevel;
import th.co.infinitait.goodluck.exception.NotFoundException;
import th.co.infinitait.goodluck.util.ObjectMapperUtil;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ObjectMapperUtil objectMapperUtil;
    @Autowired
    private CabsatPayload cabsatPayload;

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String errorMessage = ex.getParameterName() + " parameter is required";

        Error error = createErrorResponse("MISSING_REQUIRED_PARAMETER", errorMessage, MessageLevel.WARNING);

        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,errorMessage,ex.getCause() == null ? "" : ex.getCause().toString());

        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        log.error("Media type is not supported", ex);

        String errorMessage = builder.substring(0, builder.length() - 2);
        Error error = createErrorResponse("MEDIA_TYPE_NOT_SUPPORTED", errorMessage, MessageLevel.ERROR);

        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,errorMessage,ex.getCause() == null ? "" : ex.getCause().toString());

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        final BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        Error error = new Error();
        List<ErrorDetail> errorDetailList = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String message = createMessage(String.format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()));
            ErrorDetail errorDetail = createErrorDetail("INVALID_REQUEST_PARAMETER", message, MessageLevel.WARNING);
            errorDetailList.add(errorDetail);
        }
        error.setErrors(errorDetailList);

        log.info("Bad Request: {}", error.toString());
        log.debug("Bad Request: ", ex);

        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,error.toString(),ex.getCause() == null ? "" : ex.getCause().toString());

        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());

        String errorMessage = "Malformed JSON request";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {

            InvalidFormatException e = (InvalidFormatException) ex.getCause();

            if (e.getTargetType().getEnumConstants() != null) {
                List<String> enumList = new ArrayList<>();
                for (Object objEnum : e.getTargetType().getEnumConstants()) {
                    enumList.add(objEnum.toString());
                }
                errorMessage = String.format("JSON request is invalid value '%s' should be one of (%s)", e.getValue(), String.join(",", enumList));
            } else {
                errorMessage = String.format("JSON request is invalid value '%s' could not be converted to type %s", e.getValue(), e.getTargetType().getName());
            }

        }

        Error error = createErrorResponse("HTTP_MESSAGE_NOT_READABLE", errorMessage, MessageLevel.WARNING);

        log.warn("BAD_REQUEST: {}", objectMapperUtil.writeValueAsString(error));

        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,errorMessage,ex.getCause() == null ? "" : ex.getCause().toString());

        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Error writing JSON output";
        log.error(errorMessage, ex);
        Error error = createErrorResponse("MESSAGE_NOT_WRITABLE", errorMessage, MessageLevel.ERROR);
        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,errorMessage,ex.getCause() == null ? "" : ex.getCause().toString());
        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = ex.getMessage();
        Error error = createErrorResponse("REQUEST_BINDING_EXCEPTION", errorMessage, MessageLevel.WARNING);
        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,ex.getMessage(),ex.getCause() == null ? "" : ex.getCause().toString());
        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    //------ Custom ExceptionClass -----
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleExceptions(Exception ex, final WebRequest request) {
        log.error(ex.getMessage(), ex);
        String traceId = MDC.get("X-B3-TraceId");
        Error error = createErrorResponse("BAD_REQUEST", traceId, MessageLevel.WARNING);
        saveLogError(traceId,ex.toString(),ex.getCause() == null ? "" : ex.getCause().toString());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleSearchNotFoundException(NotFoundException ex, final WebRequest request) {
        log.warn("NOT_FOUND: {}", ex.getMessage());
        Error error = createErrorResponse("NOT_FOUND", ex.getMessage(), MessageLevel.WARNING);
        String traceId = MDC.get("X-B3-TraceId");
        saveLogError(traceId,ex.getMessage(),ex.getCause() == null ? "" : ex.getCause().toString());
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

//    @ExceptionHandler(NestedServletException.class)
//    public ResponseEntity handleNestedServletException(NestedServletException ex, final WebRequest request) {
//        log.warn("BAD_REQUEST: {}", ex.getMessage());
//        Error error = createErrorResponse("BAD_REQUEST", ex.getMessage(), MessageLevel.WARNING);
//        String traceId = MDC.get("X-B3-TraceId");
//        saveLogError(traceId,ex.getMessage(),ex.getCause() == null ? "" : ex.getCause().toString());
//        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }

    private String createMessage(String message) {
        return String.format("%s", message);
    }

    private Error createErrorResponse(String code, String message, MessageLevel messageLevel) {
        Error error = new Error();
        List<ErrorDetail> errorDetailList = new ArrayList<>();
        errorDetailList.add(createErrorDetail(code, createMessage(message), messageLevel));
        error.setErrors(errorDetailList);

        return error;
    }

    private ErrorDetail createErrorDetail(String code, String message, MessageLevel messageLevel) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(code);
        errorDetail.setMessage(message);
        errorDetail.setSeverityLevel(messageLevel.getCode());

        return errorDetail;
    }

    private void saveLogError(String traceId, String errorMessage, String cause){
        log.warn(traceId,errorMessage,cause);
    }

}
