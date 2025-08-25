package com.example.e_commerce.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.ErrorData;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.Exception.Custom.InternalServerErrorException;
import com.example.e_commerce.Exception.Custom.InvalidTokenException;
import com.example.e_commerce.Exception.Custom.UserNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionHandle {
	
	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getMessage())
            .findFirst()
            .orElse("Dữ liệu không hợp lệ");

        return ResponseEntity.badRequest().body(new APIResponse(message, null));
    }
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<APIResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
	    String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
	    return new ResponseEntity<APIResponse>(new APIResponse(message,null),HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<APIResponse> userNotFound(UserNotFoundException exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),null),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<APIResponse> errorServer(UserNotFoundException exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),null),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<APIResponse> invalidToken(InvalidTokenException exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),null),exception.getHttpStatus());
	}
	
	
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<APIResponse> entityNotFoundException(EntityNotFoundException exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),null),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(GeneralException.class)
	public ResponseEntity<APIResponse> generalException(GeneralException exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),null),exception.getStatus());
	}
	
	@ExceptionHandler(ErrorData.class)
	public ResponseEntity<APIResponse> generalException(ErrorData exception){
		return new ResponseEntity<APIResponse>(new APIResponse(exception.getMessage(),exception.getDataError()),exception.getStatus());
	}
}
