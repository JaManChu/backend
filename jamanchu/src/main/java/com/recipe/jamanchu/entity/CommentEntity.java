package com.recipe.jamanchu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class CommentEntity extends BaseTimeEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cmt_id")
  private Long commentId;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rcp_id")
  private RecipeEntity recipe;

  @NotNull
  @Column(name = "cmt_content", length = 300)
  private String commentContent;

  @NotNull
  @Column(name = "cmt_like", columnDefinition = "double default 1.0")
  private Double commentLike;
}
