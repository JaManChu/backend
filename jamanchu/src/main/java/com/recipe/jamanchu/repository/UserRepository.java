package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
