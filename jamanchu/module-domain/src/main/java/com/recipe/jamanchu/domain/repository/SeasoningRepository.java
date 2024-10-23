package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.SeasoningEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasoningRepository extends JpaRepository<SeasoningEntity, Long> {
  boolean existsByName(String name);
  @Query("SELECT s.name FROM SeasoningEntity s")
  List<String> findAllName();
}
