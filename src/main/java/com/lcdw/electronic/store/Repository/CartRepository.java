package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.Cart;
import com.lcdw.electronic.store.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,String> {

   Optional<Cart> findByUser(User user);
}
