package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.UserDto;

import java.util.List;

public interface UserServices {

    //create

    UserDto createUser (UserDto userDto);

    //update

    UserDto updateUser(UserDto userDto,String userId);

    //get

    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    //delete

    void deleteUser(String userId);

    //get single user by id

    UserDto getUserById(String userId);

    // get single user by email
    UserDto getUserByEmail(String email);

    //search user
    List<UserDto> searchUser(String keyword);

    //other user specific feature
}
