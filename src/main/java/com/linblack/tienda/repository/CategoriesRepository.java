package com.linblack.tienda.repository;
import com.linblack.tienda.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    boolean existsByName(String name);
}
