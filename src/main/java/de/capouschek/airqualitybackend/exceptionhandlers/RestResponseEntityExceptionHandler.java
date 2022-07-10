package de.capouschek.airqualitybackend.exceptionhandlers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import de.capouschek.airqualitybackend.exceptions.DuplicateException;
import de.capouschek.airqualitybackend.exceptions.FetchException;
import de.capouschek.airqualitybackend.exceptions.LoginFailedException;
import de.capouschek.airqualitybackend.exceptions.StoreException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(AssertionError.class)
    protected ResponseEntity<String> handleAssertionError(AssertionError ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

    }

    @ExceptionHandler(InvalidFormatException.class)
    protected ResponseEntity<String> handleInvalidFormatException(InvalidFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(StoreException.class)
    protected ResponseEntity<String> handleStoreException(StoreException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(FetchException.class)
    protected ResponseEntity<String> handleFetchException(FetchException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<String> handleDuplicateException(DuplicateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(LoginFailedException.class)
    protected ResponseEntity<String> handleLoginFailedException(LoginFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
