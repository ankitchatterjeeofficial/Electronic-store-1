package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.Repository.UserRepository;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {


    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserServices userServices;

    @Autowired
    private ModelMapper mapper;
    private User user;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init()
    {
        user=User.builder()
                .name("Durgesh")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("durgesh@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();
    }

    @Test
    public void createUserTest()
    {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto user1 = userServices.createUser(mapper.map(user, UserDto.class));

        System.out.println("The name of user is  "+user1.getName());

        Assertions.assertNotNull(user1);

        Assertions.assertEquals("MALE",user1.getGender());

    }

   @Test
    public void updateUserTest()
    {
        String userId="DHKJSNDFSJN";

        UserDto userDto = UserDto.builder()
                .name("Durgesh Kumar Tiwari")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("durgesh@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto userDto1 = userServices.updateUser(userDto, userId);
        Assertions.assertNotNull(userDto);

        System.out.println("The name of Updated User is  "+userDto1.getName());
    }

   @Test
    public void deleteUserTest()
    {
        String userId="123abc";
        Mockito.when(userRepository.findById("123abc")).thenReturn(Optional.of(user));
        userServices.deleteUser(userId);
        Mockito.verify(userRepository,Mockito.times(1)).delete(user);
    }


    @Test
    public void getAllUserTest()
    {
        User user2=User.builder()
                .name("Ankit")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("Ankit@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

       User user3=User.builder()
                .name("SHYAM")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("durgesh@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

        List<User> userList= Arrays.asList(user,user3,user2);
        Page<User> page=new PageImpl<>(userList);

        Mockito.when((userRepository.findAll((Pageable) Mockito.any()))).thenReturn(page);
        PageableResponse<UserDto> allUser = userServices.getAllUser(2, 10, "name", "desc");
        Assertions.assertEquals(3,allUser.getTotalElements());
    }

    @Test
    public void getUserByIdTest()
    {
        String userId="adshjdhads";

        Mockito.when(userRepository.findById("adshjdhads")).thenReturn(Optional.of(user));
        UserDto userDto=userServices.getUserById(userId);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getUserId(),userDto.getUserId(),"The User id is not matching !!!");
    }

    @Test
    public void getUserByEmailTest()
    {
        String email="durgesh@gmail.com";
        Mockito.when(userRepository.findByEmail("durgesh@gmail.com")).thenReturn(Optional.of(user));
        UserDto userDto=userServices.getUserByEmail(email);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getEmail(),userDto.getEmail(),"The User email is not matching !!!");
    }

    @Test
    public void searchUserTest()
    {
        User user2=User.builder()
                .name("Ankit Singh")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("Ankit@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

        User user3=User.builder()
                .name("SHYAM Singh")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("durgesh@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

        User user4=User.builder()
                .name("Uttam")
                .gender("MALE")
                .about("TESTING TEST CASE")
                .email("durgesh@gmail.com")
                .imageName("Test.png")
                .password("123abc")
                .build();

        String keyword="Singh";
        Mockito.when(userRepository.findByNameContaining(keyword)).thenReturn(Arrays.asList(user2,user3,user4));
        List<UserDto> userDtos = userServices.searchUser(keyword);
        Assertions.assertEquals("Ankit Singh",userDtos.get(2).getName());
        Assertions.assertEquals(3,userDtos.size(),"User Search is mismatched !!!");

    }



    }

