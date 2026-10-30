package br.com.financepro.financePro.common.exceptions.handler;

import br.com.financepro.financePro.common.exceptions.FileException;
import br.com.financepro.financePro.common.exceptions.dto.ExceptionResponse;
import br.com.financepro.financePro.common.exceptions.InsufficientBalanceException;
import br.com.financepro.financePro.common.exceptions.InvalidJwtAuthenticationException;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestController
@RestControllerAdvice
public class CustomizedExceptionHandler {

    @ExceptionHandler(br.com.financepro.financePro.goal.exception.InvalidGoalException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidGoal(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getMessage(), request.getDescription(false), new Date()));
    }

    @ExceptionHandler(br.com.financepro.financePro.movement.exception.InvalidTransferException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidTransfer(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getMessage(), request.getDescription(false), new Date()));
    }


    @ExceptionHandler(br.com.financepro.financePro.account.service.firstSteps.InvalidFinancialAnswersException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidFinancialAnswers(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(
            ex.getMessage(), request.getDescription(false), new Date()
        ));
    }


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            ex.getMessage(),
            request.getDescription(true),
            new Date()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            ex.getMessage(),
            request.getDescription(true),
            new Date()
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            ex.getMessage(),
            request.getDescription(true),
            new Date()
        );
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public final ResponseEntity<ExceptionResponse> handleInsufficientBalanceException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            ex.getMessage(),
            request.getDescription(true),
            new Date()
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @ExceptionHandler(FileException.class)
    public final ResponseEntity<ExceptionResponse> handleFileException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            ex.getMessage(),
            request.getDescription(true),
            new Date()
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }
}
