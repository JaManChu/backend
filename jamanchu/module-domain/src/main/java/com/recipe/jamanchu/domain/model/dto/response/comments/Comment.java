package com.recipe.jamanchu.domain.model.dto.response.comments;

import com.recipe.jamanchu.domain.entity.CommentEntity;
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
    this.commentId = commentEntity.getCmtId();
    this.commentAuthor = commentEntity.getUser().getUsrNickname();
    this.commentContent = commentEntity.getCmtContent();
    this.rating = commentEntity.getCmtLike();
    this.createdAt = commentEntity.getCreatedAt();
    this.updatedAt = commentEntity.getUpdatedAt();
  }


}
