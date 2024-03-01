package com.lcdw.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lcdw.electronic.store.Entities.Product;
import com.lcdw.electronic.store.Validate.ImageNameValid;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDto
{
    private String categoryId;

    @NotBlank
    @Size(min = 4,message = "Title Should be more than 3 characters!!")
    private String title;

    @Size(min = 5,message = "Description Should be more than 3 characters!!")
    private String description;

    @ImageNameValid(message = "Give the proper image extension !!!")
    private String coverImage;


    @JsonIgnore
    private List<ProductDto> products=new ArrayList<>();

}
