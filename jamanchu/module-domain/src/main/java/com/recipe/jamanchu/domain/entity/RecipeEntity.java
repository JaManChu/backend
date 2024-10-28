package com.recipe.jamanchu.domain.entity;

import com.recipe.jamanchu.domain.model.type.CookingTimeType;
import com.recipe.jamanchu.domain.model.type.LevelType;
import com.recipe.jamanchu.domain.model.type.RecipeProvider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recipe")
public class RecipeEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rcp_id")
  private Long rcpId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "usr_id")
  private UserEntity user;

  @NotNull
  @Column(name = "rcp_name")
  private String rcpName;

  @Enumerated(EnumType.STRING)
  @Column(name = "rcp_level")
  private LevelType rcpLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "rcp_time")
  private CookingTimeType rcpTime;

  @Column(name = "rcp_thumbnail")
  private String rcpThumbnail;

  @NotNull
  @Column(name = "rcp_provider")
  @Enumerated(EnumType.STRING)
  private RecipeProvider rcpProvider;

  @Column(name = "rcp_origin_id")
  private Long rcpOriginId;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<ManualEntity> manuals;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<RecipeIngredientEntity> ingredients;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<RecipeRatingEntity> rating;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<RecipeIngredientMappingEntity> mapping;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<CommentEntity> comments;

  public void updateRecipe(String rcpName, LevelType rcpLevel, CookingTimeType rcpTime, String rcpThumbnail) {
    this.rcpName = rcpName;
    this.rcpLevel = rcpLevel;
    this.rcpTime = rcpTime;
    this.rcpThumbnail = rcpThumbnail;
  }
}
