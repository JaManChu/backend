package com.recipe.jamanchu.api.service.impl;

import com.recipe.jamanchu.api.service.StatisticsService;
import com.recipe.jamanchu.domain.component.bean.StatisticsSet;
import com.recipe.jamanchu.domain.entity.StatisticsEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.repository.StatisticsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

  private final StatisticsRepository statisticsRepository;
  private final StatisticsSet dailyVisitors;

  @Override
  public ResultResponse getDailyStatistics() {

    LocalDate now = LocalDate.now();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_DAILY,
        statisticsRepository.findStVisitorsByStYearsAndStMonthsAndStDays(now.getYear(), now.getMonthValue(),
            now.getDayOfMonth()));
  }

  @Override
  public ResultResponse getMonthlyStatistics() {

    LocalDate now = LocalDate.now();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_MONTHLY,
        statisticsRepository.findStVisitorsByStYears(now.getYear()).stream().mapToLong(Long::longValue)
            .sum());
  }

  @Scheduled(cron = "0 0/30 * * * *")
  public void statistics() {

    int visitors = dailyVisitors.size();

    LocalDateTime now = LocalDateTime.now();

    statisticsRepository.save(StatisticsEntity.builder()
        .stYears(now.getYear())
        .stMonths(now.getMonthValue())
        .stDays(now.getDayOfMonth())
        .stVisitors((long) visitors)
        .build()
    );
    // 자정마다 dailyVisitors 저장 후 초기화
    dailyVisitors.clear();
  }

}
