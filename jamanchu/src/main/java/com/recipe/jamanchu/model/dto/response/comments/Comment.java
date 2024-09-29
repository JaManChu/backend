package com.recipe.jamanchu.model.dto.response.comments;

import com.recipe.jamanchu.entity.CommentEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Comment {

  private final Long commentId;

  private final String nickname;

  private final String content;

  private final Double rating;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;


  public static Comment of(CommentEntity commentEntity) {
    return new Comment(commentEntity);
  }

  public Comment(CommentEntity commentEntity){
    this.commentId = commentEntity.getCommentId();
    this.nickname = commentEntity.getUser().getNickname();
    this.content = commentEntity.getCommentContent();
    this.rating = commentEntity.getCommentLike();
    this.createdAt = commentEntity.getCreatedAt();
    this.updatedAt = commentEntity.getUpdatedAt();
  }


}
