package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.TenThousandRecipeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TenThousandRecipeRepository extends JpaRepository<TenThousandRecipeEntity, Long> {
  List<TenThousandRecipeEntity> findByTrOriginIdBetween(Long startId, Long endId);

  @Query("SELECT MAX(t.trOriginId) FROM TenThousandRecipeEntity t")
  Long findMaxTrOriginId();
}
