package com.recipe.jamanchu.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistics")
public class StatisticsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stat_id")
  private Long analyticsId;

  @NotNull
  @Column(name = "stat_days")
  private Long days;

  @NotNull
  @Column(name = "stat_months")
  private Long months;

  @NotNull
  @Column(name = "stat_years")
  private Long years;

}
