package com.lcdw.electronic.store.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lcdw.electronic.store.Validate.ImageNameValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class UserDto {


    private String userId;

    @Size(min = 3,message = "Name Should be more than 3 characters!!")
    private String name;

    @NotBlank(message = "Email cant be Blank !!!")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$",message = "Invalid Email entered!!")
    private String email;

    @NotBlank(message = "Password cant be Empty!!")
    @Size(min = 3,message = "Password should be Greater than 3 characters!!")
    private String password;

    @NotBlank(message = "Gender Cant be Blank !!")
    @Pattern(regexp = "(?:male|Male|female|Female|FEMALE|MALE|Not prefer to say)$",message = "Enter Male or Female or Not prefer to say ")
    private String gender;

    @NotBlank(message = "Write something about yourself!!!")
    private String about;

    @ImageNameValid(message = "Give the proper image extension !!!")
    private String imageName;


    @JsonIgnore
    private List<OrderDto> orders=new ArrayList<>();

      @JsonIgnore
      private CartDto cart;

}
