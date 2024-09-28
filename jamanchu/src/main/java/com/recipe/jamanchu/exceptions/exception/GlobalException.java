package com.recipe.jamanchu.exceptions.exception;

import com.recipe.jamanchu.exceptions.ExceptionStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException{
  HttpStatus status;

  public GlobalException(ExceptionStatus ex) {
    super(ex.getMessage());
    this.status = ex.getStatusCode();
  }
}
