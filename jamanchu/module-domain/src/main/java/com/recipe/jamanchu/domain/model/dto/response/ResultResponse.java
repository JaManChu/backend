package com.recipe.jamanchu.domain.model.dto.response;

import com.recipe.jamanchu.domain.model.type.ResultCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResultResponse {

  private final HttpStatus code;
  private final String message;
  private final Object data;

  public static ResultResponse of(ResultCode resultCode, Object data) {
    return new ResultResponse(resultCode, data);
  }

  public static ResultResponse of(ResultCode resultCode) {
    return new ResultResponse(resultCode, null);
  }

  public ResultResponse(ResultCode resultCode, Object data) {
    this.code = resultCode.getStatusCode();
    this.message = resultCode.getMessage();
    this.data = data;
  }
}

