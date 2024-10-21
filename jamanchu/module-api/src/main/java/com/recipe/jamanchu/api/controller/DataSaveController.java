package com.recipe.jamanchu.api.controller;

import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.api.service.RecipeDivideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DataSaveController {

  final RecipeDivideService recipeDivideService;

  @GetMapping("/divide")
  public ResponseEntity<ResultResponse> processAndSaveRecipes(
      @RequestParam Long startId,
      @RequestParam Long endId) {
    return ResponseEntity.ok(recipeDivideService.processAndSaveAllData(startId, endId));
  }
}
