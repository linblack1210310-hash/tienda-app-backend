package com.linblack.tienda.controller;

import com.linblack.tienda.dto.HttpGlobalResponseDTO;
import com.linblack.tienda.entity.Products;
import com.linblack.tienda.repository.ProductsRepository;
import com.linblack.tienda.security.RequiresRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    // 1. Catálogo público (sin token)
    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<Products>>> getAll(@RequestParam(required = false) Long categoryId) {
        List<Products> products;
        if (categoryId != null) {
            products = productsRepository.findByCategoryId(categoryId);
        } else {
            products = productsRepository.findAll();
        }
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Catálogo obtenido", products));
    }

    // 2. Detalle de un producto (público)
    @GetMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<Products>> getById(@PathVariable Long id) {
        Optional<Products> product = productsRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpGlobalResponseDTO<>("Producto no encontrado", null));
        }
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Producto obtenido", product.get()));
    }

    // 3. Productos del vendedor dueño del token
    @RequiresRole({3}) // Solo VENDEDOR
    @GetMapping("/mine")
    public ResponseEntity<HttpGlobalResponseDTO<List<Products>>> getMine(HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        List<Products> myProducts = productsRepository.findBySellerId(sellerId);
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Mis productos", myProducts));
    }

    // 4. Crear producto (solo VENDEDOR)
    @RequiresRole({3})
    @PostMapping
    public ResponseEntity<HttpGlobalResponseDTO<Products>> create(@RequestBody Products product, HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        product.setSellerId(sellerId); // El dueño se toma del token, no del body
        Products saved = productsRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpGlobalResponseDTO<>("Producto creado", saved));
    }

    // 5. Editar producto (VENDEDOR dueño o ADMIN)
    @RequiresRole({1, 3}) // ADMIN y VENDEDOR
    @PutMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<Products>> update(@PathVariable Long id, @RequestBody Products product, HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        Long rolId = (Long) request.getAttribute("rolId");

        Optional<Products> existing = productsRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpGlobalResponseDTO<>("Producto no encontrado", null));
        }

        Products prod = existing.get();
        // Validación de propiedad: Si es VENDEDOR (3) y no es el dueño, 403
        if (rolId == 3 && !prod.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new HttpGlobalResponseDTO<>("No puedes editar un producto que no es tuyo", null));
        }

        prod.setName(product.getName());
        prod.setDescription(product.getDescription());
        prod.setPrice(product.getPrice());
        prod.setStock(product.getStock());
        prod.setCategoryId(product.getCategoryId());

        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Producto actualizado", productsRepository.save(prod)));
    }

    // 6. Eliminar producto (VENDEDOR dueño o ADMIN)
    @RequiresRole({1, 3})
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<Void>> delete(@PathVariable Long id, HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        Long rolId = (Long) request.getAttribute("rolId");

        Optional<Products> existing = productsRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpGlobalResponseDTO<>("Producto no encontrado", null));
        }

        Products prod = existing.get();
        if (rolId == 3 && !prod.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new HttpGlobalResponseDTO<>("No puedes eliminar un producto que no es tuyo", null));
        }

        productsRepository.deleteById(id);
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Producto eliminado", null));
    }
}
