package com.recipe.jamanchu.component;

import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.DuplicatedEmailException;
import com.recipe.jamanchu.exceptions.exception.DuplicatedNicknameException;
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
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
  public ResultCode existsByEmail(String email){

    if (userRepository.existsByEmail(email)) {
      return ResultCode.EMAIL_ALREADY_IN_USE;
    }

    return ResultCode.EMAIL_AVAILABLE;
  }

  // 닉네임 중복 체크
  public void existsByNickname(String nickname){

    log.info("existsByNickname -> nickname : {}", nickname);
    if (userRepository.existsByNickname(nickname)) {

      log.info("Nickname is Duplicated!");
      throw new DuplicatedNicknameException();
    }
  }

  // 소셜 계정 정보 체크
  public void isSocialUser(String provider) {

    log.info("isSocialUser -> provider : {}", provider);
    if (provider != null) {

      log.info("is Social User!");
      throw new SocialAccountException();
    }
  }

  // 비밀번호 일치 여부 체크
  public void validatePassword(String enPassword, String password) {
    if (!passwordEncoder.matches(password, enPassword)) {
      throw new PasswordMismatchException();
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
}

