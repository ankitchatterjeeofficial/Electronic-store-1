package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {

}
