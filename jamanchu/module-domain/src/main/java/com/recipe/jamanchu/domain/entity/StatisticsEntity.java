package com.recipe.jamanchu.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "statistics")
public class StatisticsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stat_id")
  private Long analyticsId;

  @NotNull
  @Column(name = "stat_days")
  private Integer days;

  @NotNull
  @Column(name = "stat_months")
  private Integer months;

  @NotNull
  @Column(name = "stat_years")
  private Integer years;

  @NotNull
  @Column(name = "stat_visitors")
  private Long visitors;

  public void addVisitors(Long visitors) {
    this.visitors += visitors;
  }

}
