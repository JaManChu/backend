package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.StatisticsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

  Optional<StatisticsEntity> findVisitorsByStYearsAndStMonthsAndStDays(Integer years, Integer months, Integer days);

  List<StatisticsEntity> findVisitorsByStYearsAndStMonths(Integer year, Integer months);
}
