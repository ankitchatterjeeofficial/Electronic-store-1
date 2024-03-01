package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.dtos.CartDto;
import com.lcdw.electronic.store.dtos.AddItemToRequest;
import com.lcdw.electronic.store.dtos.PageableResponse;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartServices
{
    //add items to cart
    //case1: cart for user is not available : we will create cart and then add items
    //case2:cart available add the items to cart

    CartDto additemToCart(String userId, AddItemToRequest request);

    //remove item from cart
    void removeItemFromCart(String userId,int cartItemId);

    //clear item from cart or remove all items from cart
    void clearCart(String userId);

    CartDto getCartByUser(String userId);

    PageableResponse<CartDto>  getAllCart(int pageNumber, int pageSize, String sortBy, String sortDir);


}
