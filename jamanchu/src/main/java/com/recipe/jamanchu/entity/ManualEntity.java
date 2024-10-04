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
@Table(name = "manuals")
public class ManualEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mn_id")
  private Long manualId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rcp_id")
  private RecipeEntity recipe;

  @NotNull
  @Column(name = "mn_content", columnDefinition = "TEXT")
  private String manualContent;

  @Column(name = "mn_picture")
  private String manualPicture;
}
