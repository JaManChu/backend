package com.recipe.jamanchu.repository;

import com.recipe.jamanchu.entity.ManualEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualRepository extends JpaRepository<ManualEntity, Long> {

}
