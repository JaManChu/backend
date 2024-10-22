package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import com.recipe.jamanchu.domain.model.type.ScrapedType;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Long>, RecipeRepositoryCustom {

  // 평점순으로 레시피 가져오는 쿼리
  @Query("SELECT r FROM RecipeEntity r " +
      "LEFT JOIN r.rating rat " +
      "GROUP BY r.id " +
      "ORDER BY AVG(rat.rating) DESC")
  Page<RecipeEntity> findAllOrderByRating(Pageable pageable);

  @Query("SELECT MAX(r.originRcpId) FROM RecipeEntity r")
  Long findMaxOriginRcpId();

  @Query(value = "SELECT r FROM RecipeEntity r " +
      "JOIN ScrapedRecipeEntity s ON s.recipe.id = r.id " +
      "WHERE s.user = :user AND (s.scrapedType = :scrapedType)")
  Optional<List<RecipeEntity>> findScrapRecipeByUser(UserEntity user, ScrapedType scrapedType);

  Optional<List<RecipeEntity>> findAllByUser(UserEntity user);

  // 찜 레시피에 해당하는 ids(레시피)는 제외하고 모든 레시피 검색하는 쿼리
  Page<RecipeEntity> findByIdNotIn(List<Long> ids, Pageable pageable);

  // 찜 레시피에 해당하는 ids(레시피)는 제외하고 평점순으로 검색하는 쿼리
  @Query("SELECT r FROM RecipeEntity r " +
      "LEFT JOIN r.rating rat " +
      "WHERE r.id NOT IN :ids " +
      "GROUP BY r.id " +
      "ORDER BY AVG(rat.rating) DESC")
  Page<RecipeEntity> findByIdNotInOrderByRating(@Param("ids") List<Long> ids, Pageable pageable);

  void deleteAllByUser(UserEntity user);
}
