package com.itiutiaiev.chipper.handler

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import javax.management.InstanceNotFoundException
import javax.naming.AuthenticationException

@ControllerAdvice
class HandlerException {

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<String> AuthenticationException(AuthenticationException exception) {
        String error = exception.getMessage() ?: "no message"
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        String error = exception.getMessage() ?: "no message"
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception) {
        String error = exception.getMessage() ?: "no message"
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseEntity<String> UsernameNotFoundException(UsernameNotFoundException exception) {
        String error = exception.getMessage() ?: "no message"
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InstanceNotFoundException.class)
    ResponseEntity<String> InstanceNotFoundException(InstanceNotFoundException exception) {
        String error = exception.getMessage() ?: "no message"
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND)
    }

}
