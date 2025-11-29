package com.github.ajharry69.testsoap.bpm.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.ajharry69.testsoap.bpm.common.exceptions.DocumentExtensionMismatchException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeadersValidationException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.dto.ResponsePayload;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DocumentExtensionMismatchException.class)
    ResponseEntity<ResponsePayload<?>> handleDocumentExtensionMismatchException(DocumentExtensionMismatchException exception) {
        var response = createErrorResponse(
                "4000453",
                "Document extension '%s' does not match file extension '%s'".formatted(exception.getDocumentExtension(), exception.getFileExtension())
        );

        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ResponsePayload<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.error("Unsupported content type", exception);
        var response = createErrorResponse(
                "ERR-UNSUPPORTED_CONTENT_TYPE",
                "Unsupported content type: %s".formatted(exception.getContentType())
        );

        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(HeadersValidationException.class)
    ResponseEntity<ResponsePayload<?>> handleHeadersValidationException(HeadersValidationException exception) {
        var response = createErrorResponse("4000453", "Invalid or invalid request headers");

        var errors = exception.getHeaderExceptions()
                .stream()
                .map(e -> {
                    var errorMessage = e.getMessage();
                    if (e instanceof MissingHeaderException) {
                        errorMessage = "Missing required header";
                    } else if (e instanceof InvalidHeaderValueException error) {
                        errorMessage = "Invalid header value '%s'".formatted(error.getHeaderValue());
                    }
                    var errorInfo = new ResponsePayload.ErrorInfo();
                    errorInfo.setErrorDescription(errorMessage);
                    errorInfo.setErrorCode(e.getRule().getHeaderName());
                    return errorInfo;
                })
                .toList();

        response.setErrorInfo(errors);
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(BindException.class)
    ResponseEntity<ResponsePayload<?>> handleBindException(BindException exception) {
        var response = createErrorResponse("4000453", "Validation failed");
        var errors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    var errorInfo = new ResponsePayload.ErrorInfo();
                    errorInfo.setErrorCode(fieldError.getField());
                    errorInfo.setErrorDescription(fieldError.getDefaultMessage());
                    return errorInfo;
                })
                .toList();
        response.setErrorInfo(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ResponsePayload<?>> handleConstraintViolationException(ConstraintViolationException exception) {
        var response = createErrorResponse("4000453", "Validation failed");
        var errors = exception.getConstraintViolations().stream()
                .map(v -> {
                    var info = new ResponsePayload.ErrorInfo();
                    info.setErrorDescription(v.getMessage());
                    var param = v.getPropertyPath() != null
                            ? v.getPropertyPath().toString()
                            : null;
                    if (param != null) {
                        int dot = param.lastIndexOf('.');
                        if (dot >= 0 && dot < param.length() - 1) {
                            param = param.substring(dot + 1);
                        }
                    }
                    info.setErrorCode(param);
                    return info;
                })
                .toList();
        response.setErrorInfo(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ResponsePayload<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        var description = Objects.toString(exception.getMessage(), "Malformed JSON");
        var errorCode = "json";
        if (exception.getCause() instanceof UnrecognizedPropertyException cause) {
            var path = cause.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            description = "Unknown properties: '%s'".formatted(path);
            errorCode = cause.getPropertyName();
        }

        var response = createErrorResponse("4000453", "Validation failed");
        var error = new ResponsePayload.ErrorInfo();
        error.setErrorCode(errorCode);
        error.setErrorDescription(description);
        response.setErrorInfo(List.of(error));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MultipartException.class})
    ResponseEntity<ResponsePayload<?>> handleMultipartExceptions(Exception exception) {
        var response = createErrorResponse("4000453", "Validation failed");
        var error = new ResponsePayload.ErrorInfo();
        error.setErrorDescription(Objects.toString(exception.getMessage(), "Invalid multipart request"));
        error.setErrorCode("multipart");
        if (exception instanceof MissingServletRequestPartException m) {
            error.setErrorCode(m.getRequestPartName());
        }

        response.setErrorInfo(List.of(error));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ResponsePayload<?> createErrorResponse(String messageCode, String messageDescription) {
        var response = new ResponsePayload<>();
        var requestContext = KCBRequestContextHolder.getContext();
        response.setConversationID(requestContext.conversationID());
        response.setMessageID(requestContext.messageID());
        response.setMessageCode(messageCode);
        response.setMessageDescription(messageDescription);
        response.setStatusCode(String.valueOf(TransactionStatus.FAILURE.ordinal()));
        response.setStatusDescription(TransactionStatus.FAILURE.name());
        response.setAdditionalData(Collections.emptyList());
        return response;
    }
}
