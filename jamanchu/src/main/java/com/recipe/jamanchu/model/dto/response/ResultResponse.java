package com.recipe.jamanchu.model.dto.response;

import com.recipe.jamanchu.model.type.ResultCode;
import lombok.Getter;

@Getter
public class ResultResponse {

  private final String message;
  private final Object data;

  public static ResultResponse of(ResultCode resultCode, Object data) {
    return new ResultResponse(resultCode, data);
  }

  public static ResultResponse of(ResultCode resultCode) {
    return new ResultResponse(resultCode, null);
  }

  public ResultResponse(ResultCode resultCode, Object data) {
    this.message = resultCode.getMessage();
    this.data = data;
  }
}

