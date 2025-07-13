package ru.practicum.shareit.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.shareit.core.error.exception.DuplicateDataException;
import ru.practicum.shareit.core.error.exception.LackOfRightsException;
import ru.practicum.shareit.core.error.exception.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message;
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        } else {
            message = ex.getMessage();
        }

        return getResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        ParameterValidationResult firstError = ex.getAllValidationResults().getFirst();
        String errorMessage = !firstError.getResolvableErrors().isEmpty()
                ? firstError.getResolvableErrors().getFirst().getDefaultMessage()
                : "Ошибка валидации";

        return getResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateData(DuplicateDataException ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LackOfRightsException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(LackOfRightsException ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOthers(Exception ex) {
        return getResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> getResponseEntity(String message, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(message, httpStatus.value());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}
