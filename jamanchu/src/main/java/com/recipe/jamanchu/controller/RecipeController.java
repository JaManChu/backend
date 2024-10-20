package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.model.dto.request.recipe.RecipesDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesDeleteDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesSearchDTO;
import com.recipe.jamanchu.model.dto.request.recipe.RecipesUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.recipe.jamanchu.service.PictureService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

  private final RecipeService recipeService;
  private final PictureService pictureService;

  @GetMapping
  public ResponseEntity<ResultResponse> getRecipes(
      HttpServletRequest request,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "15") int size
  ) {
    return ResponseEntity.ok(recipeService.getRecipes(request, page, size));
  }

  @GetMapping("/search")
  public ResponseEntity<ResultResponse> searchRecipes(
      HttpServletRequest request,
      @RequestParam(value = "ingredientName",required = false) List<String> ingredients,
      @RequestParam(value = "recipeLevel", required = false) LevelType recipeLevel,
      @RequestParam(value = "recipeCookingTime", required = false) CookingTimeType recipeCookingTime,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "15") int size
  ) {
    RecipesSearchDTO recipesSearchDTO = new RecipesSearchDTO(ingredients, recipeLevel, recipeCookingTime);
    return ResponseEntity.ok(recipeService.searchRecipes(request, recipesSearchDTO, page, size));
  }

  @GetMapping("/{recipeId}")
  public ResponseEntity<ResultResponse> getRecipeDetails(
      @PathVariable("recipeId") Long recipeId
  ) {
    return ResponseEntity.ok(recipeService.getRecipeDetail(recipeId));
  }

  @GetMapping("/popular")
  public ResponseEntity<ResultResponse> getRecipesByRating(
      HttpServletRequest request,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "15") int size
  ) {
    return ResponseEntity.ok(recipeService.getRecipesByRating(request, page, size));
  }

  @PostMapping(path = "/upload-thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadThumbnail(
      HttpServletRequest request,
      @RequestPart(value = "recipeThumbnail") MultipartFile recipeThumbnail,
      @RequestParam(value = "recipeName") String recipeName
  ) throws IOException {
    return ResponseEntity.ok(pictureService.uploadThumbnail(request, recipeName, recipeThumbnail));
  }

  @PostMapping(path = "/upload-orderImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<String>> uploadOrderImages(
      HttpServletRequest request,
      @RequestPart(value = "recipeOrderImages")List<MultipartFile> recipeOrderImages,
      @RequestParam(value = "recipeName") String recipeName
  ) throws IOException {
    return ResponseEntity.ok(pictureService.uploadOrderImages(request, recipeName, recipeOrderImages));
  }

  @PostMapping
  public ResponseEntity<ResultResponse> registerRecipe(
      HttpServletRequest request,
      @Valid @RequestBody RecipesDTO recipesDTO,
      @RequestParam("thumbnailUrl") String thumbnailUrl,
      @RequestParam("recipeOrderImagesUrl") List<String> recipeOrderImagesUrl) {

    // 레시피 등록 서비스 호출
    return ResponseEntity.ok(recipeService.registerRecipe(request, recipesDTO, thumbnailUrl, recipeOrderImagesUrl));
  }

  @PostMapping("/{recipeId}/scrap")
  public ResponseEntity<ResultResponse> scrapedRecipe(
      HttpServletRequest request,
      @PathVariable("recipeId") Long recipeId) {
    return ResponseEntity.ok(recipeService.scrapedRecipe(request, recipeId));
  }

  @PutMapping
  public ResponseEntity<ResultResponse> updateRecipe(
      HttpServletRequest request,
      @Valid @RequestBody RecipesUpdateDTO recipesUpdateDTO,
      @RequestParam("thumbnailUrl") String thumbnailUrl,
      @RequestParam("recipeOrderImagesUrl") List<String> recipeOrderImagesUrl) {
    return ResponseEntity.ok(recipeService.updateRecipe(request, recipesUpdateDTO, thumbnailUrl, recipeOrderImagesUrl));
  }

  @DeleteMapping
  public ResponseEntity<ResultResponse> deleteRecipe(
      HttpServletRequest request,
      @Valid @RequestBody RecipesDeleteDTO recipesDeleteDTO) {
    return ResponseEntity.ok(recipeService.deleteRecipe(request, recipesDeleteDTO));
  }
}
