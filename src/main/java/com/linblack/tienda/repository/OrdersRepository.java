package com.linblack.tienda.repository;

import com.linblack.tienda.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByClientId(Long clientId);
    List<Orders> findByProductIdIn(List<Long> productIds);
}
