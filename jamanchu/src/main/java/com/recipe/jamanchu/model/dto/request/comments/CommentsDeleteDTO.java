package com.recipe.jamanchu.model.dto.request.comments;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentsDeleteDTO {

    @Min(value = 1, message = "잘못된 입력입니다.")
    private final Long commentId;
}
