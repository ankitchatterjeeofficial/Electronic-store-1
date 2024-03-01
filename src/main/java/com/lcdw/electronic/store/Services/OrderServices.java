package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.Entities.Cart;
import com.lcdw.electronic.store.dtos.OrderDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderServices {

    //create order
    OrderDto createOrder(OrderDto orderDto, String userId);

    //remove order
    void removeOrder(String userId,String orderId);

    //get order of user
    List<OrderDto> getOrdersOfUser(String userId);

    //get all order
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

    void placeOrder(String userId, String orderId);


    void deliveredOrder(String orderId);




}
