package com.lcdw.electronic.store.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Entity
@Table(name = "User")
public class User
{
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name="user_name",length = 50)
    private String name;

    @Column(name="user_email",unique = true)
    private String email;

    @Column(name="user_password",length = 50)
    private String password;

    @Column(name="user_gender",length = 50)
    private String gender;

    @Column(name = "user_about")
    private String about;

    @Column(name = "user_image_name")
    private String imageName;


    @JsonIgnore
    @OneToMany( mappedBy = "user",fetch = FetchType.EAGER,cascade =CascadeType.REMOVE,orphanRemoval = true)
    private List<Order> orders=new ArrayList<>();
//

      @JsonIgnore
      @OneToOne(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE,orphanRemoval = true)
      private Cart cart;




}
