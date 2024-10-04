package com.recipe.jamanchu.entity;

import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.RecipeProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * TempRecipe
 * 다른 도메인 연관관계를 설정하기 위한 임시 레시피 테이블
 * (추후 삭제 예정)
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recipe")
public class RecipeEntity extends BaseTimeEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  @NotNull
  @Column(name = "rcp_name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "rcp_level")
  private LevelType level;

  @Enumerated(EnumType.STRING)
  @Column(name = "rcp_time")
  private CookingTimeType time;

  @Column(name = "rcp_thumbnail")
  private String thumbnail;

  @NotNull
  @Column(name = "rcp_provider")
  @Enumerated(EnumType.STRING)
  private RecipeProvider provider;
}
