package com.recipe.jamanchu.domain.repository;

import com.recipe.jamanchu.domain.entity.CommentEntity;
import com.recipe.jamanchu.domain.entity.RecipeEntity;
import com.recipe.jamanchu.domain.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  List<CommentEntity> findAllByRecipe(RecipeEntity recipe);

  void deleteAllByUser(UserEntity user);
}
