package com.recipe.jamanchu.api.filter;

import com.recipe.jamanchu.domain.component.bean.StatisticsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorCountFilter extends OncePerRequestFilter {

  private final StatisticsSet dailyVisitors;

  /*
  * RemoteAddr를 통해 방문자의 IP를 추출하고, 해당 IP가 dailyVisitors에 존재하지 않는 경우 새로운 방문자로 판단하여 dailyVisitors에 추가합니다.
  * */

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String visitorIp = request.getRemoteAddr();

    if (!dailyVisitors.containsKey(visitorIp)) {
      log.info("New visitor: {}", visitorIp);
      dailyVisitors.put(visitorIp, LocalDateTime.now());
    }

    filterChain.doFilter(request, response);
  }

}
