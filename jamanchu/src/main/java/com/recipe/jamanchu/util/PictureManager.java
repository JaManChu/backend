package com.recipe.jamanchu.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.recipe.jamanchu.model.type.PictureType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class PictureManager {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public String upload(
      Long userId,
      String recipeName,
      MultipartFile multipartFile,
      PictureType pictureType
  ) throws IOException {

    String newFileName =
        pictureType.getFolderPrefix() +
            "/" + userId + "_" + recipeName + "." +
            Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.")[1];

    File uploadFile = convert(multipartFile).orElseThrow(
        () -> new IllegalArgumentException("MultipartFile -> File 전환 실패")
    );

    // 업로드한 사진의 URL 반환
    return upload(newFileName, uploadFile);
  }

  private String upload(
      String newFileName,
      File uploadFile
  ) {

    String uploadImageUrl = putS3(uploadFile, newFileName);

    // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
    removeNewFile(uploadFile);

    return uploadImageUrl;
  }

  private String putS3(
      File uploadFile,
      String fileName
  ) {

    amazonS3.putObject(
        new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead)  // PublicRead 권한으로 업로드 됨
    );
    return amazonS3.getUrl(bucket, fileName).toString();
  }

  private void removeNewFile(
      File targetFile
  ) {

    if (targetFile.delete()) {
      log.debug("파일이 삭제되었습니다.");
    } else {
      log.error("{} 파일이 삭제되지 못했습니다.", targetFile.getName());
    }
  }

  private Optional<File> convert(
      MultipartFile file
  ) throws IOException {

    String fileOriginalName = file.getOriginalFilename();
    File convertFile = new File(Objects.requireNonNull(fileOriginalName)); // 업로드한 파일의 이름

    if (convertFile.createNewFile()) {
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(file.getBytes());
      }
      return Optional.of(convertFile);
    }

    // 새로 만들어진 파일이 아닌 경우,
    return Optional.empty();
  }

}