package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;

public interface StatisticsService {

  ResultResponse getDailyStatistics();

  ResultResponse getMonthlyStatistics();
}
