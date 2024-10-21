package com.recipe.jamanchu.api.service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {

  String uploadThumbnail(HttpServletRequest request, String recipeName, MultipartFile recipeThumbnail) throws IOException;

  List<String> uploadOrderImages(HttpServletRequest request, String recipeName, List<MultipartFile> recipeOrderImages) throws IOException;

}
