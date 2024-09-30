package com.recipe.jamanchu.model.dto.request.comments;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentsUpdateDTO {

  @Min(value = 1, message = "잘못된 댓글 번호입니다.")
  private final Long commentsId;

  @NotEmpty(message = "댓글 내용을 입력해주세요.")
  @Size(min = 1, max = 300, message = "댓글 내용은 1자 이상 300자 이하로 입력해주세요.")
  private final String comment;

  @DecimalMin(value = "1.0", inclusive = false, message = "평점은 1.0이상으로 입력해주세요.")
  @DecimalMax(value = "5.0", inclusive = false, message = "평점은 5.0이하로 입력해주세요.")
  private final Double rating;

}
