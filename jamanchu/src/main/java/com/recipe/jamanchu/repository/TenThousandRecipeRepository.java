package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.TenThousandRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenThousandRecipeRepository extends JpaRepository<TenThousandRecipeEntity, Long> {

}
