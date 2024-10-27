package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.StatisticsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

  Long findVisitorsByYearsAndMonthsAndDays(Integer years, Integer months, Integer days);

  List<Long> findVisitorsByYears(Integer years);
}
