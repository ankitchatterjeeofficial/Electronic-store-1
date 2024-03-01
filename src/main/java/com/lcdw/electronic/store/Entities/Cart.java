package com.lcdw.electronic.store.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    private String cartId;

    private Date createdAt;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;



    @OneToMany(mappedBy ="cart",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items=new ArrayList<>();

}
