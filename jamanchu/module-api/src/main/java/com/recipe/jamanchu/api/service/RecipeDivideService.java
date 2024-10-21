package com.recipe.jamanchu.api.service;

import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;

public interface RecipeDivideService {
  ResultResponse processAndSaveAllData(Long startId, Long endId);
}
