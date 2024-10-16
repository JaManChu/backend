package com.recipe.jamanchu.component;

import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccessHandler {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;


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
        .orElseThrow(UserNotFoundException::new);
  }

  // 카카오로부터 받은 email 값과 일치하는 회원이 있을 경우 해당 회원 정보 반환
  // 없을 경우 전달 받은 email, nickname, providerId으로 회원 정보 저장 후 반환
  public UserEntity findOrCreateUser(KakaoUserDetails kakaoUserDetails) {
    log.info("findOrCreateUser -> email : {}", kakaoUserDetails.getEmail());
    return userRepository.findByEmail(kakaoUserDetails.getEmail())
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
      return ResultResponse.of(ResultCode.EMAIL_ALREADY_IN_USE, false);
    }

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
}

