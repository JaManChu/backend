package com.recipe.jamanchu.api.auth.service;

import com.recipe.jamanchu.domain.component.UserAccessHandler;
import com.recipe.jamanchu.domain.model.auth.UserDetailsDTO;

import com.recipe.jamanchu.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

  private final UserAccessHandler userAccessHandler;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    UserEntity user = userAccessHandler.findByUserId(Long.parseLong(userId));

    return new UserDetailsDTO(user);
  }
}
