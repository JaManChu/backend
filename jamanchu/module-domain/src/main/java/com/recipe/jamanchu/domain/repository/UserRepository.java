package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  boolean existsByEmailAndNickname(String email, String nickname);

  @Query("SELECT u FROM UserEntity u "
      + "WHERE u.email = :email "
      + "AND u.provider IS NULL ")
  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUserId(Long userId);

  @Query("SELECT u FROM UserEntity u WHERE u.deletionScheduledAt = CURRENT_DATE")
  List<UserEntity> findAllDeletedToday();

  @Modifying
  @Query(value = "DELETE FROM user WHERE usr_id = :userId", nativeQuery = true)
  void deleteUserByUserId(@Param("userId") Long userId);

  @Query("SELECT u FROM UserEntity u "
      + "WHERE u.email = :email "
      + "AND u.provider IS NOT NULL ")
  Optional<UserEntity> findKakaoUser(@Param("email") String email);
}
