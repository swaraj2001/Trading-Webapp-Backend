package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order , Long> {
    List<Order> findByUserId(Long userId);
}
