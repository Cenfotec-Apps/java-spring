package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT u FROM Category u WHERE LOWER(u.name) LIKE %?1%")
    List<Category> findCategoriesWithCharacterInName(String character);

}
