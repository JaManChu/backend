package com.recipe.jamanchu.entity;

import com.recipe.jamanchu.model.type.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE user "
    + "SET deletion_scheduled_at = DATE_ADD(now(), INTERVAL 1 MONTH) "
    + "WHERE usr_id = ?")
@Table(name = "user")
public class UserEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usr_id")
  private Long userId;

  @NotNull
  @Column(name = "usr_email")
  private String email;

  @NotNull
  @Column(name = "usr_password")
  private String password;

  @NotNull
  @Column(name = "usr_nickname")
  private String nickname;

  @Column(name = "usr_provider")
  private String provider;

  @Column(name = "usr_provider_sub")
  private String providerId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(name = "deletion_scheduled_at")
  private LocalDate deletionScheduledAt;
}

