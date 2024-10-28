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
  @Column(name = "st_id")
  private Long stId;

  @NotNull
  @Column(name = "st_days")
  private Integer stDays;

  @NotNull
  @Column(name = "st_months")
  private Integer stMonths;

  @NotNull
  @Column(name = "st_years")
  private Integer stYears;

  @NotNull
  @Column(name = "st_visitors")
  private Long stVisitors;

  public void addVisitors(Long visitors) {
    this.visitors += visitors;
  }

}
