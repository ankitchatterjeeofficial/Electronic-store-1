package com.lcdw.electronic.store.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@ToString
@Entity
@Table(name = "order")
public class Order
{
    @Id
    private String orderId;




    private String orderStatus;

    private String paymentStatus;


    private int totalBill;

   // private int discountAmount;

    private double totalDiscountPercentage;

    @NotBlank(message = "Provide a billing address")
    @Column(length = 240)
    private String billingAddress;

    @NotBlank(message = "Provide phone number !!!")
    private String billingPhone;

    @NotBlank(message = "Provide name of the billing User !!!")
    private String billingName;

    private Date orderedDate;

    private Date deliveredDate;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "order",orphanRemoval = true)
    private List<OrderItem>orderItems=new ArrayList<>();



}
