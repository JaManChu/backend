package com.recipe.jamanchu.validator;

import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateUser {

  private final UserRepository userRepository;

  public UserEntity validateUserId(Long userId) {
    return userRepository.findByUserId(userId)
        .orElseThrow(UserNotFoundException::new);
  }

  public UserEntity validateEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(UserNotFoundException::new);
  }

}
