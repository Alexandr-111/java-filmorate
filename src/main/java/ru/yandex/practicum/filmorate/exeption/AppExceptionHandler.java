package ru.yandex.practicum.filmorate.exeption;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.dto.Response;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Response> handleDataNotFoundException(DataNotFoundException ex) {
        log.warn("Выброшено исключение DataNotFoundException: {}", ex.getMessage());
        Response response = new Response("Не найден ресурс", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.error("Ошибка валидации поля {}: {}", error.getField(), error.getDefaultMessage());
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            log.error("Ошибка валидации параметра {}: {}", fieldName, message);
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Response> handleNumberFormatException(NumberFormatException ex) {
        log.error("Выброшено исключение NumberFormatException: ", ex);
        Response response = new Response("Ошибочный формат", "Вы ввели некорректное значение");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleAllExceptions(Exception ex) {
        log.error("Внутренняя ошибка сервера: ", ex);
        Response response = new Response("Ошибка сервера", "Операция не выполнена из-за ошибки на сервере");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}