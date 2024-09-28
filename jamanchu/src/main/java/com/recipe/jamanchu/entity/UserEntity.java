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
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(name = "usr_email")
  private String email;

  @Column(name = "usr_password")
  private String password;

  @Column(name = "usr_nickname")
  private String nickname;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}
