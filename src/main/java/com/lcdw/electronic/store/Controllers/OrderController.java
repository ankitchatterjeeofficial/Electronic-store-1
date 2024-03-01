package com.lcdw.electronic.store.Controllers;

import com.lcdw.electronic.store.Services.OrderServices;
import com.lcdw.electronic.store.dtos.ApiResponseMessage;
import com.lcdw.electronic.store.dtos.OrderDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderServices orderServices;


    //Create
    @PostMapping("/{userId}")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable String userId)
    {

        OrderDto order = orderServices.createOrder(orderDto, userId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    //remove
    @DeleteMapping("/{userId}/deleteOrder/{orderId}")
    public ResponseEntity<ApiResponseMessage> deleteOrder(@PathVariable String userId,@PathVariable String orderId)
    {
        orderServices.removeOrder(userId,orderId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Your Order is successfully deleted !!!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersOfUser(@PathVariable String userId)
    {
        List<OrderDto> ordersOfUser = orderServices.getOrdersOfUser(userId);

        return new ResponseEntity<>(ordersOfUser,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getAllOrder(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "orderedDate",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<OrderDto> response = orderServices.getOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping(("/{userId}/placeOrder/{orderId}"))
    public ResponseEntity<ApiResponseMessage> placeOrder(@PathVariable String userId,@PathVariable String orderId)
    {
        orderServices.placeOrder(userId,orderId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Your Order is Placed Successfully and will be dispatched soon !!!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> deliveredOrder(@PathVariable String orderId)
    {
        orderServices.deliveredOrder(orderId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Your order is going to be delivered in today")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
