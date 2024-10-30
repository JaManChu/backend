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

  boolean existsByUsrEmail(String email);

  boolean existsByUsrNickname(String nickname);

  @Query("SELECT COUNT(u) > 0 FROM UserEntity u "
      + "WHERE u.usrEmail = :email "
      + "AND u.usrNickname = :nickname "
      + "AND u.usrProvider IS NULL "
      + "AND u.deletionScheduledAt IS NULL")
  boolean existsByEmailAndNickname(String email, String nickname);

  @Query("SELECT u FROM UserEntity u "
      + "WHERE u.usrEmail = :email "
      + "AND u.usrProvider IS NULL ")
  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUsrId(Long userId);

  @Query("SELECT u FROM UserEntity u WHERE u.deletionScheduledAt = CURRENT_DATE")
  List<UserEntity> findAllDeletedToday();

  @Modifying
  @Query(value = "DELETE FROM user WHERE usr_id = :userId", nativeQuery = true)
  void deleteByUsrId(@Param("userId") Long userId);

  @Query("SELECT u FROM UserEntity u "
      + "WHERE u.usrEmail = :email "
      + "AND u.usrProvider IS NOT NULL ")
  Optional<UserEntity> findKakaoUser(@Param("email") String email);
}
