package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.service.RecipeDivideService;
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

    recipeDivideService.processAndSaveAllData(startId, endId);

    return ResponseEntity.ok(recipeDivideService.processAndSaveAllData(startId, endId));
  }
}
