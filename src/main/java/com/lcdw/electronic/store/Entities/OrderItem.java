package com.lcdw.electronic.store.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lcdw.electronic.store.dtos.PageableResponse;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

     private int quantity;

     private int totalPrice;

    private int discountedPrice;

    private double discountPercentage;

    private String title;

     @JsonIgnore
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "product_id")
     private Product product;


     @JsonIgnore
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "order_id")
     private Order order;

}
