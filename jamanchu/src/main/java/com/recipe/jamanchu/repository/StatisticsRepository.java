package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

}
