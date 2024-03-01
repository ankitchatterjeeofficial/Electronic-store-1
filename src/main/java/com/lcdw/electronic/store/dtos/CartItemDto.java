package com.lcdw.electronic.store.dtos;

import com.lcdw.electronic.store.Entities.Cart;
import com.lcdw.electronic.store.Entities.Product;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CartItemDto
{
    private int CartItemId;


    private ProductDto product;

    private int quantity;

    private int totalPrice;

    private int discountedPrice;

    private double discountedPercentage;



}
