package com.example.orders.repository;

import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByExternalOrderId(String externalOrderId);

    boolean existsByExternalOrderId(String externalOrderId);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.externalOrderId = :externalOrderId")
    Optional<Order> findByExternalOrderIdWithItems(@Param("externalOrderId") String externalOrderId);
}
