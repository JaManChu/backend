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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usr_id")
  private Long userId;

  @Column(name = "usr_email")
  private String email;

  @Column(name = "usr_password")
  private String password;

  @Column(name = "usr_nickname")
  private String nickname;

  @Column(name = "usr_provider")
  private String provider;

  @Column(name = "usr_provider_sub")
  private String providerId;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}
