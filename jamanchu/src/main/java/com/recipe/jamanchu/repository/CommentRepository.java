package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.CommentEntity;
import com.recipe.jamanchu.entity.RecipeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  List<CommentEntity> findAllByRecipeOrderByCreatedAtAsc(RecipeEntity recipe);
}
