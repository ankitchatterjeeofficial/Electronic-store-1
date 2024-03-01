package com.lcdw.electronic.store.Services.Impl;

import com.lcdw.electronic.store.Controllers.UserController;
import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.Exception.ResourceNotFoundException;
import com.lcdw.electronic.store.Helper.Helper;
import com.lcdw.electronic.store.Repository.UserRepository;
import com.lcdw.electronic.store.Services.UserServices;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserServices
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;


    @Value("${user.profile.image.path}")
    private String imagePath;


    private Logger logger= LoggerFactory.getLogger(UserServicesImpl.class);
    @Override
    public UserDto createUser(UserDto userDto) {

        //GENERATE UNIQUE ID FOR STRING

       String userId= UUID.randomUUID().toString();
       userDto.setUserId(userId);


        //UserDto to User entity

        User user=DtoToEntity(userDto);
        User saveUser = userRepository.save(user);

        //Entity to UserDto
        UserDto userDto1=EntityToDto(saveUser);

        //return Dto
        return userDto1;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());

        User saveuser = userRepository.save(user);
        UserDto updatedDto = EntityToDto(saveuser);


        return updatedDto;
    }

    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) :(Sort.by(sortBy).ascending());
    //pageNumber Default starts with 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<User> page=userRepository.findAll(pageable);

        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);

        return response;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        //delete user profile image
        String fullPath = imagePath + user.getImageName();

        try
        {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }
        catch(NoSuchFileException ex)
        {
            logger.info("User_image not found in folder!!");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //delete User
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with Given id"));
        UserDto userDto = EntityToDto(user);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No email found for the Above"));
        UserDto userDto = EntityToDto(user);
        return userDto;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> dtoList = users.stream().map(user -> EntityToDto(user)).collect(Collectors.toList());

        return dtoList;

    }

    private User DtoToEntity(UserDto userDto) {

        User user = User.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .gender(userDto.getGender())
                .imageName(userDto.getImageName())
                .about(userDto.getAbout())
                .build();

        return user;

    }

    private UserDto EntityToDto(User saveUser) {

//        UserDto userDto = UserDto.builder()
//                .userId(saveUser.getUserId())
//                .name(saveUser.getName())
//                .email(saveUser.getEmail())
//                .password(saveUser.getPassword())
//                .gender(saveUser.getGender())
//                .imageName(saveUser.getImageName())
//                .about(saveUser.getAbout())
//                .build();

        return  mapper.map(saveUser,UserDto.class);

    }


}
