package com.recipe.jamanchu.api.config;

import com.recipe.jamanchu.api.auth.jwt.JwtFilter;
import com.recipe.jamanchu.api.auth.jwt.JwtUtil;
import com.recipe.jamanchu.api.filter.VisitorCountFilter;
import com.recipe.jamanchu.domain.component.bean.StatisticsSet;
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
  private final StatisticsSet dailyVisitors;


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
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**").permitAll()
            .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
            .requestMatchers("/",
                "/api/v1/users/signup",
                "/api/v1/users/login/**",
                "/api/v1/auth/**",
                "/favicon.ico").permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/recipes/**",
                "/api/v1/comments/**",
                "/api/v1/notify/**",
                "/api/v1/statistics/**",
                "/actuator/**").permitAll()
            .anyRequest().authenticated());
    http
        .addFilterBefore(new JwtFilter(jwtUtil),
            UsernamePasswordAuthenticationFilter.class);
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.addFilterAfter(new VisitorCountFilter(dailyVisitors),
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

}

