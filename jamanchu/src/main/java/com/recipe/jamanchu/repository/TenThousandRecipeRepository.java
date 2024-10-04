package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenThousandRecipeRepository extends JpaRepository<TenThousandRecipeEntity, Long> {
  List<TenThousandRecipeEntity> findByCrawledRecipeIdBetween(Long startId, Long endId);
}
