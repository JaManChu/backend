package com.recipe.jamanchu.core.exceptions.exception;

import com.recipe.jamanchu.core.exceptions.ExceptionStatus;
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
