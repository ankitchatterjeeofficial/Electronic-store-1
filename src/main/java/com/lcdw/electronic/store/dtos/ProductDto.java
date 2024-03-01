package com.lcdw.electronic.store.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.Validate.ImageNameValid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private String productId;

    @Size(min = 3,message = "title Should be more than 3 characters!!")
    private String title;

    @Size(min=5,message = "Give proper description !!")
    private String description;

    @Positive(message = "Provide a Price positive Value!!")
    private int price;

    @Positive(message ="Provide a Discounted Price positive Value!!" )
    private int discountedPrice;

    @Positive(message = "Provide a proper quantity !!!")
    private int quantity;

    private Date addedDate;

    @NotNull
    private boolean live;
    @NotNull
    private boolean stock;

   // @ImageNameValid(message = "Provide proper extension .jpg/.jpeg/.png !!")
    private String productImage;


    private CategoryDto category;

    @JsonIgnore
    private List<CartItemDto> cartItems=new ArrayList<>();
//
}
