package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

}
