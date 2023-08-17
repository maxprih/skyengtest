package com.maxpri.skyengtest.exception;

import com.maxpri.skyengtest.model.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author max_pri
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = InvalidOperationException.class)
    private ResponseEntity<MessageResponse> handleInvalidOperationException(InvalidOperationException e) {
        MessageResponse response = new MessageResponse("Invalid operation!");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSuchIndexException.class)
    private ResponseEntity<MessageResponse> handleNoSuchIndexException(NoSuchIndexException e) {
        MessageResponse response = new MessageResponse("No mail office with given index");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSuchMailItemException.class)
    private ResponseEntity<MessageResponse> handleNoSuchMailItemException(NoSuchMailItemException e) {
        MessageResponse response = new MessageResponse("No mail item with this id");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFinalDestinationException.class)
    private ResponseEntity<MessageResponse> handleNotFinalDestinationException(NotFinalDestinationException e) {
        MessageResponse response = new MessageResponse("Mail item can be received only at final destination");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
