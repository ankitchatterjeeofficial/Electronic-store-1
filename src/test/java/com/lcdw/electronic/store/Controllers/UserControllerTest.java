package com.lcdw.electronic.store.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.Services.UserServices;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

      User user;

    @MockBean
    private UserServices userServices;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init()
    {
        user = User.builder()
                .name("Durgesh")
                .email("durgesh@gmail.com")
                .about("This is testing create method")
                .gender("Male")
                .imageName("abc.jpeg")
                .password("lcwd")
                .userId("123sahjs")
                .build();
    }

    @Test
    public void createUserTestApi() throws Exception {

//        /users +POST+ user data as json
        //data as json+status created

        UserDto dto = mapper.map(user, UserDto.class);

        Mockito.when(userServices.createUser(Mockito.any())).thenReturn(dto);

        //actual request for url

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists());
    }

    private String convertObjectToJsonString(Object user) {

        try {
            return new ObjectMapper().writeValueAsString(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Test
    public void getAllUsersTest() throws Exception {

        UserDto object1 = UserDto.builder().name("Durgesh").email("duregsh@gmail.com").password("durgesh").about("Testing").build();
        UserDto object2 = UserDto.builder().name("Amit").email("duregsh@gmail.com").password("durgesh").about("Testing").build();
        UserDto object3 = UserDto.builder().name("Sumit").email("duregsh@gmail.com").password("durgesh").about("Testing").build();
        UserDto object4 = UserDto.builder().name("Ankit").email("duregsh@gmail.com").password("durgesh").about("Testing").build();
        PageableResponse<UserDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(
                object1, object2, object3, object4
        ));
        pageableResponse.setLastPage(false);
        pageableResponse.setPageSize(10);
        pageableResponse.setPageNumber(100);
        pageableResponse.setTotalElements(1000);
        Mockito.when(userServices.getAllUser(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);


        this.mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    public void updateUserTest() throws Exception {

        // /users/{userId} + PUT request+ json

        String userId = "123sahjs";
        UserDto dto = this.mapper.map(user, UserDto.class);

        Mockito.when(userServices.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(dto);

        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(convertObjectToJsonString(user))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());

    }


}
