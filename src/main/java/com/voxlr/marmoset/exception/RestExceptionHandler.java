package com.voxlr.marmoset.exception;

import com.mongodb.DuplicateKeyException;
import com.voxlr.marmoset.util.error.ApiError;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j2
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    return buildResponseEntity(
        new ApiError(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info(
        "{} to {}",
        servletWebRequest.getHttpMethod(),
        servletWebRequest.getRequest().getServletPath());
    String error = "Malformed JSON request";
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String error = "Error writing JSON output";
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(javax.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      javax.validation.ConstraintViolationException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getConstraintViolations());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
    ApiError apiError = new ApiError(NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(
        String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  protected ResponseEntity<Object> handleDuplicateKeyException(
      DuplicateKeyException ex, WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("A key was found with the same data.");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(UnauthorizedUserException.class)
  protected ResponseEntity<Object> handleUnauthorizedUserException(
      UnauthorizedUserException ex, WebRequest request) {
    ApiError apiError = new ApiError(UNAUTHORIZED);
    apiError.setMessage("Unauthorized to execute request.");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(HandlerNotFoundException.class)
  protected ResponseEntity<Object> handlerHandlerNotFoundException(HandlerNotFoundException ex) {
    ApiError apiError = new ApiError(NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(CallbackException.class)
  protected ResponseEntity<Object> handlerCallbackException(CallbackException ex) {
    ApiError apiError = new ApiError(ex.getStatus());
    apiError.setMessage("An error occurred while processing a callback.");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(InvalidArgumentsException.class)
  protected ResponseEntity<Object> handlerInvalidArgumentsException(InvalidArgumentsException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Could not complete request.");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ConvertException.class)
  protected ResponseEntity<Object> handlerInvalidArgumentsException(ConvertException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(
        "Invalid arguments [" + String.join(",", ex.getNonConvertableStrings()) + "]");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
