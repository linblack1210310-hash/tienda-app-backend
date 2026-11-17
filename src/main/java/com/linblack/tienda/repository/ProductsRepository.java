package com.linblack.tienda.repository;
import com.linblack.tienda.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findBySellerId(Long sellerId);
    List<Products> findByCategoryId(Long categoryId);
}