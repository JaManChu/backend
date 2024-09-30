package com.recipe.jamanchu.model.dto.response.comments;

import java.util.List;
import lombok.Getter;

@Getter
public class Comments {

  private final List<Comment> comments;

  public Comments(List<Comment> comments){
    this.comments = comments;
  }

  public static Comments of(List<Comment> comments) {
    return new Comments(comments);
  }
}
