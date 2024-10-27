package com.recipe.jamanchu.api.controller;

import com.recipe.jamanchu.api.service.StatisticsService;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@RestController
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping("/daily")
  public ResponseEntity<ResultResponse> getDailyStatistics() {
    return ResponseEntity.ok(statisticsService.getDailyStatistics());
  }

  @GetMapping("/monthly")
  public ResponseEntity<ResultResponse> getMonthlyStatistics() {
    return ResponseEntity.ok(statisticsService.getMonthlyStatistics());
  }
}
