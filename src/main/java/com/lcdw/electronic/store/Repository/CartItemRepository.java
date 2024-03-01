package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

public interface CartItemRepository extends JpaRepository<CartItem,Integer>
{

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM cart_items where cart_item_id= :cartItem",nativeQuery = true)
    void remove(@Param("cartItem") int cartItem);
}
