package com.recipe.jamanchu.api.component;

import com.recipe.jamanchu.api.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.core.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.core.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.core.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.core.exceptions.exception.WithdrewUserException;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.model.type.UserRole;
import com.recipe.jamanchu.domain.repository.CommentRepository;
import com.recipe.jamanchu.domain.repository.IngredientRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRatingRepository;
import com.recipe.jamanchu.domain.repository.RecipeRepository;
import com.recipe.jamanchu.domain.repository.ScrapedRecipeRepository;
import com.recipe.jamanchu.domain.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccessHandler {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final ScrapedRecipeRepository scrapedRecipeRepository;
  private final RecipeRepository recipeRepository;
  private final CommentRepository commentRepository;
  private final RecipeRatingRepository recipeRatingRepository;
  private final IngredientRatingRepository ingredientRatingRepository;


  // userId 값과 일치하는 회원 정보 반환
  public UserEntity findByUserId(Long userId) {

    log.info("findByUserId -> userId : {}", userId);
    return userRepository.findByUserId(userId)
        .orElseThrow(UserNotFoundException::new);
  }

  // email 값과 일치하는 회원 정보 반환
  public UserEntity findByEmail(String email) {
    log.info("findByEmail -> email : {}", email);

    return userRepository.findByEmail(email)
        .filter(user -> user.getDeletionScheduledAt() == null)
        .orElseThrow(UserNotFoundException::new);
  }

  // 카카오로부터 받은 email 값과 일치하는 회원이 있을 경우 해당 회원 정보 반환
  // 없을 경우 전달 받은 email, nickname, providerId으로 회원 정보 저장 후 반환
  public UserEntity findOrCreateUser(KakaoUserDetails kakaoUserDetails) {
    log.info("findOrCreateUser -> email : {}", kakaoUserDetails.getEmail());
    return userRepository.findKakaoUser(kakaoUserDetails.getEmail())
        .orElseGet(() -> userRepository.save(UserEntity.builder()
            .email(kakaoUserDetails.getEmail())
            .password(passwordEncoder.encode(String.valueOf(Math.random() * 8)))
            .nickname(kakaoUserDetails.getNickname())
            .provider("kakao")
            .providerId(kakaoUserDetails.getProviderId())
            .role(UserRole.USER)
            .build()));
  }

  public void existsById(Long userId) {
    log.info("existsById -> userId : {}", userId);
    if (!userRepository.existsById(userId)) {
      log.info("User Not Found!");
      throw new UserNotFoundException();
    }
  }

  // 이메일 중복 체크
  public ResultResponse existsByEmail(String email){
    if (userRepository.existsByEmail(email)) {
      Optional<UserEntity> userOpt = userRepository.findByEmail(email);

      // 탈퇴한 사용자의 이메일인 경우
      userOpt.filter(user -> user.getDeletionScheduledAt() != null)
          .ifPresent(user -> {
            throw new WithdrewUserException();
          });

      // 이미 사용중인 이메일
      return ResultResponse.of(ResultCode.EMAIL_ALREADY_IN_USE, false);
    }

    // 사용가능 한 이메일
    return ResultResponse.of(ResultCode.EMAIL_AVAILABLE, true);
  }


  // 닉네임 중복 체크
  public ResultResponse existsByNickname(String nickname){

    if (userRepository.existsByNickname(nickname)) {
      return ResultResponse.of(ResultCode.NICKNAME_ALREADY_IN_USE, false);
    }

    return ResultResponse.of(ResultCode.NICKNAME_AVAILABLE, true);
  }

  // 소셜 계정 정보 체크
  public void isSocialUser(String provider) {

    log.info("isSocialUser -> provider : {}", provider);
    if (provider != null) {

      log.info("is Social User!");
      throw new SocialAccountException();
    }
  }

  // 회원 정보 저장
  @Transactional
  public void saveUser(UserEntity user) {
    userRepository.save(user);
    log.info("saveUser -> Success");
  }

  // 회원정보 삭제
  @Transactional
  public void deleteUser(UserEntity user) {
    userRepository.delete(user);
  }

  // 비밀번호 일치 여부 체크
  // 회원 탈퇴시 사용하는 메서드
  // 아직 회원 탈퇴 부분은 협의를 하지 않아서,
  // 해당 메서드는 추후에 수정할 예정
  public void validatePassword(String enPassword, String password) {
    if (!passwordEncoder.matches(password, enPassword)) {
      throw new PasswordMismatchException();
    }
  }

  // 비밀번호 일치 여부 체크 (회원 정보 수정)
  // 회원정보 수정 하기 전 기존 비밀번호를 확인 하는 메서드
  // 비밀 번호 일치 여부 반환
  public ResultResponse validateBeforePW(String enPassword, String password) {
    if (!passwordEncoder.matches(password, enPassword)) {
      return ResultResponse.of(ResultCode.PASSWORD_MISMATCH, false);
    }
    return ResultResponse.of(ResultCode.PASSWORD_MATCH, true);
  }

  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  public void deleteAllUserData() {
    List<UserEntity> users = userRepository.findAllDeletedToday();
    log.info("today : {}", LocalDate.now());

    users.forEach(user -> {
      log.info("Delete All User Data -> user : {}", user.getEmail());
      deleteRelatedUserData(user);
      userRepository.deleteUserByUserId(user.getUserId());
    });
  }

  private void deleteRelatedUserData(UserEntity user) {
    // 스크랩한 레시피
    scrapedRecipeRepository.deleteAllByUser(user);
    // 작성한 댓글
    commentRepository.deleteAllByUser(user);
    // 레시피 평점
    recipeRatingRepository.deleteAllByUser(user);
    // 재료 평점
    ingredientRatingRepository.deleteAllByUser(user);
    // 작성한 레시피
    recipeRepository.deleteAllByUser(user);
  }
}

