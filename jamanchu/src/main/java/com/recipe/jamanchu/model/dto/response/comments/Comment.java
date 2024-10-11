package com.recipe.jamanchu.model.dto.response.comments;

import com.recipe.jamanchu.entity.CommentEntity;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Comment {

  private final Long commentId;

  private final String commentAuthor;

  private final String commentContent;

  private final Double rating;

  private final LocalDateTime createdAt;

  private final LocalDateTime updatedAt;


  public static Comment of(CommentEntity commentEntity) {
    return new Comment(commentEntity);
  }

  public Comment(CommentEntity commentEntity){
    this.commentId = commentEntity.getCommentId();
    this.commentAuthor = commentEntity.getUser().getNickname();
    this.commentContent = commentEntity.getCommentContent();
    this.rating = commentEntity.getCommentLike();
    this.createdAt = commentEntity.getCreatedAt();
    this.updatedAt = commentEntity.getUpdatedAt();
  }


}
