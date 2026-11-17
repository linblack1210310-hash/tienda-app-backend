package com.linblack.tienda.controller;

import com.linblack.tienda.dto.HttpGlobalResponseDTO;
import com.linblack.tienda.entity.Orders;
import com.linblack.tienda.entity.Products;
import com.linblack.tienda.repository.OrdersRepository;
import com.linblack.tienda.repository.ProductsRepository;
import com.linblack.tienda.security.RequiresRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    // 1. Crear pedido (Solo CLIENTE)
    @RequiresRole({2})
    @PostMapping
    public ResponseEntity<HttpGlobalResponseDTO<Orders>> create(@RequestBody Orders order, HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        
        Products product = productsRepository.findById(order.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpGlobalResponseDTO<>("Producto no encontrado", null));
        }

        if (product.getStock() < order.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HttpGlobalResponseDTO<>("Sin stock suficiente", null));
        }

        // Descontar stock y calcular total
        product.setStock(product.getStock() - order.getQuantity());
        productsRepository.save(product);

        order.setClientId(clientId);
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setStatus("PENDIENTE");

        return ResponseEntity.status(HttpStatus.CREATED).body(new HttpGlobalResponseDTO<>("Pedido creado", ordersRepository.save(order)));
    }

    // 2. Ver mis pedidos (Solo CLIENTE)
    @RequiresRole({2})
    @GetMapping("/mine")
    public ResponseEntity<HttpGlobalResponseDTO<List<Orders>>> getMyOrders(HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(new HttpGlobalResponseDTO<>("Mis pedidos", ordersRepository.findByClientId(clientId)));
    }
}
