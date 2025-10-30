package domain.backend.common.exception;

import io.jsonwebtoken.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleIoException(IOException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
