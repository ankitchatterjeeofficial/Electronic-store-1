package com.lcdw.electronic.store.dtos;

import com.lcdw.electronic.store.Entities.Order;
import com.lcdw.electronic.store.Entities.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderItemDto
{
    private int orderItemId;

    private int quantity;

    private int totalPrice;

    private int discountedPrice;

    private double discountPercentage;

    private String title;


    private ProductDto product;



}
