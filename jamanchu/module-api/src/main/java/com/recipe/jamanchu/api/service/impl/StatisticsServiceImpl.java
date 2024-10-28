package com.recipe.jamanchu.api.service.impl;

import com.recipe.jamanchu.api.service.StatisticsService;
import com.recipe.jamanchu.domain.component.bean.StatisticsSet;
import com.recipe.jamanchu.domain.entity.StatisticsEntity;
import com.recipe.jamanchu.domain.model.dto.response.ResultResponse;
import com.recipe.jamanchu.domain.model.type.ResultCode;
import com.recipe.jamanchu.domain.repository.StatisticsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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

    StatisticsEntity daily = statisticsRepository.findVisitorsByYearsAndMonthsAndDays(now.getYear(), now.getMonthValue(), now.getDayOfMonth())
        .orElseGet(StatisticsEntity.builder().visitors(0L)::build);
    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_DAILY, daily.getVisitors());
  }

  @Override
  public ResultResponse getMonthlyStatistics() {

    LocalDate now = LocalDate.now();

    return ResultResponse.of(ResultCode.SUCCESS_RETRIEVE_MONTHLY,
        statisticsRepository.findVisitorsByYearsAndMonths(now.getYear(), now.getMonthValue()).stream()
            .map(StatisticsEntity::getVisitors)
            .reduce(0L, Long::sum)
    );
  }

  @Scheduled(cron = "0 0/30 * * * *")
  public void statistics() {

    int visitors = dailyVisitors.size();

    LocalDateTime now = LocalDateTime.now();

    Optional<StatisticsEntity> visitorsByYearsAndMonthsAndDays = statisticsRepository.findVisitorsByYearsAndMonthsAndDays(
        now.getYear(), now.getMonthValue(), now.getDayOfMonth());

    if (visitorsByYearsAndMonthsAndDays.isPresent()) {
      StatisticsEntity statisticsEntity = visitorsByYearsAndMonthsAndDays.get();
      statisticsEntity.addVisitors((long) visitors - statisticsEntity.getVisitors());
      statisticsRepository.save(statisticsEntity);
    }
    else{
      statisticsRepository.save(
        StatisticsEntity.builder()
          .years(now.getYear())
          .months(now.getMonthValue())
          .days(now.getDayOfMonth())
          .visitors((long) visitors)
          .build()
      );
    }

    // 정각에 방문자 수 초기화
    if(now.getHour() == 0 && now.getMinute() == 0){
      dailyVisitors.clear();
    }
  }

}
