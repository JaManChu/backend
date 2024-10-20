package com.recipe.jamanchu.controller;

import com.recipe.jamanchu.service.PictureService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pictures")
public class PictureController {

  private final PictureService pictureService;

  @PostMapping(path = "/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadThumbnail(
      HttpServletRequest request,
      @RequestPart(value = "recipeThumbnail") MultipartFile recipeThumbnail,
      @RequestParam(value = "recipeName") String recipeName
  ) throws IOException {
    return ResponseEntity.ok(pictureService.uploadThumbnail(request, recipeName, recipeThumbnail));
  }

  @PostMapping(path = "/orderImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<String>> uploadOrderImages(
      HttpServletRequest request,
      @RequestPart(value = "recipeOrderImages")List<MultipartFile> recipeOrderImages,
      @RequestParam(value = "recipeName") String recipeName
  ) throws IOException {
    return ResponseEntity.ok(pictureService.uploadOrderImages(request, recipeName, recipeOrderImages));
  }

}
