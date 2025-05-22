package com.orderme.backend.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = ?2 WHERE o.orderid = ?1")
    void updateOrderStatus(Integer orderId, String status);
}
