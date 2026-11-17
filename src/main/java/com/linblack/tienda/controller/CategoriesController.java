package com.linblack.tienda.controller;

import com.linblack.tienda.dto.HttpGlobalResponseDTO;
import com.linblack.tienda.entity.Categories;
import com.linblack.tienda.repository.CategoriesRepository;
import com.linblack.tienda.security.RequiresRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<Categories>>> getAll() {
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Categorías obtenidas", categoriesRepository.findAll()));
    }

    @RequiresRole({1}) // Solo ADMIN
    @PostMapping
    public ResponseEntity<HttpGlobalResponseDTO<Categories>> create(@RequestBody Categories category) {
        if (categoriesRepository.existsByName(category.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new HttpGlobalResponseDTO<>("La categoría ya existe", null));
        }
        Categories saved = categoriesRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpGlobalResponseDTO<>("Categoría creada", saved));
    }

    @RequiresRole({1}) // Solo ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<Void>> delete(@PathVariable Long id) {
        // (La validación de si tiene productos la haremos después, por ahora solo borra)
        categoriesRepository.deleteById(id);
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Categoría eliminada", null));
    }
}