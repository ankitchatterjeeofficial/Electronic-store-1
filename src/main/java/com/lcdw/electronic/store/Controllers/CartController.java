package com.lcdw.electronic.store.Controllers;

import com.lcdw.electronic.store.Services.CartServices;
import com.lcdw.electronic.store.dtos.AddItemToRequest;
import com.lcdw.electronic.store.dtos.ApiResponseMessage;
import com.lcdw.electronic.store.dtos.CartDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartServices cartServices;

    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable String userId ,@RequestBody AddItemToRequest request)
    {
        CartDto cartDto = cartServices.additemToCart(userId, request);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId,@PathVariable int itemId)
    {
        cartServices.removeItemFromCart(userId,itemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(" Given Item is removed !!!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId)
    {
        cartServices.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(" Cart is cleared for given user !!!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId )
    {
        CartDto cartDto = cartServices.getCartByUser(userId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CartDto>> getAllCart(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "createdAt",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<CartDto> allCart = cartServices.getAllCart(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allCart,HttpStatus.FOUND);
    }

}
