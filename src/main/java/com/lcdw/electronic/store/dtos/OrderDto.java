package com.lcdw.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lcdw.electronic.store.Entities.OrderItem;
import com.lcdw.electronic.store.Entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderDto
{

    private String orderId;



    private String orderStatus="pending";


    private String paymentStatus="NOT-PAID";


    private int totalBill;

    // private int discountAmount;

     private double totalDiscountPercentage;

    @NotBlank(message = "Provide a billing address")
    private String billingAddress;

    @NotBlank(message = "Provide phone number !!!")
    private String billingPhone;

    @NotBlank(message = "Provide name of the billing User !!!")
    private String billingName;

    private Date orderedDate=new Date();
    private Date deliveredDate;

    private UserDto user;


    private List<OrderItemDto> orderItems=new ArrayList<>();
}
