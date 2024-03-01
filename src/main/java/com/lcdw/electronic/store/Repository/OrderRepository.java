package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.Order;
import com.lcdw.electronic.store.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,String> {

    Optional<Order> findByUser(User user);


}
