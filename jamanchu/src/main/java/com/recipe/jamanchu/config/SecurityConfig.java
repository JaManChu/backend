package com.recipe.jamanchu.config;

import com.recipe.jamanchu.auth.jwt.JwtFilter;
import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.auth.oauth2.CustomOauth2UserService;
import com.recipe.jamanchu.auth.oauth2.handler.OAuth2FailureHandler;
import com.recipe.jamanchu.auth.oauth2.handler.OAuth2SuccessHandler;
import com.recipe.jamanchu.auth.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtUtil jwtUtil;
  private final CustomUserDetailService userDetailService;
  private final CustomOauth2UserService customOauth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {

    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable);
    http
        .formLogin(AbstractHttpConfigurer::disable);
    http
        .httpBasic(AbstractHttpConfigurer::disable);
    http
        .authorizeHttpRequests(auth -> auth
            //swagger ui 허용
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers("/v3/api-docs/**", "/api/v1/scrape-recipes", "/api/v1/divide",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**").permitAll()
            .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
            .requestMatchers("/",
                "/api/v1/users/signup",
                "/api/v1/users/login",
                "/api/v1/users/test",
                "/api/v1/notify/**",
                "/api/v1/auth/email-check").permitAll()
            .anyRequest().authenticated());

    http
        .oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                .userService(customOauth2UserService))
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler));

    http
        .addFilterBefore(new JwtFilter(jwtUtil, userDetailService), UsernamePasswordAuthenticationFilter.class);
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

}
