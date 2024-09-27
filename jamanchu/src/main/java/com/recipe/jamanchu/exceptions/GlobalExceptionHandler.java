package com.recipe.jamanchu.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<String> globalException(GlobalException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
  }
}
