package com.recipe.jamanchu.service.impl;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.model.type.PictureType;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.service.PictureService;
import com.recipe.jamanchu.util.PictureManager;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

  private final JwtUtil jwtUtil;
  private final PictureManager pictureManager;

  @Override
  public String uploadThumbnail(HttpServletRequest request, String recipeName, MultipartFile recipeThumbnail)
      throws IOException {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    return pictureManager.upload(userId, recipeName, recipeThumbnail, PictureType.THUMBNAIL);
  }

  @Override
  public List<String> uploadOrderImages(HttpServletRequest request, String recipeName,
      List<MultipartFile> recipeOrderImages) throws IOException {
    Long userId = jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()));

    List<String> recipeOrderImageUrls = new ArrayList<>();
    if (recipeOrderImages != null) {
      for (MultipartFile file : recipeOrderImages) {
        String uploadedUrl = pictureManager.upload(userId, recipeName, file, PictureType.RECIPE_ORDER_IMAGE);
        recipeOrderImageUrls.add(uploadedUrl);
      }
    }

    return recipeOrderImageUrls;
  }
}
