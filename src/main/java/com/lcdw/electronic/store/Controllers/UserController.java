package com.lcdw.electronic.store.Controllers;

import com.lcdw.electronic.store.Exception.BadApiRequest;
import com.lcdw.electronic.store.Services.FileService;
import com.lcdw.electronic.store.Services.UserServices;
import com.lcdw.electronic.store.dtos.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;


    private Logger logger= LoggerFactory.getLogger(UserController.class);
    //create

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto)
    {
        UserDto userDto1 = userServices.createUser(userDto);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{userId}")
    public  ResponseEntity<UserDto> updateUser(@PathVariable String userId,@Valid @RequestBody UserDto userDto)
    {
        UserDto updateUser = userServices.updateUser(userDto, userId);
        return new ResponseEntity<>(updateUser,HttpStatus.OK);

    }

    //get all list
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUser(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value="sortBy",defaultValue = "name",required = false) String sortBy,
            @RequestParam(value="sortDir",defaultValue = "aesc",required = false) String sortDir
    )
    {
        return new ResponseEntity<>(userServices.getAllUser(pageNumber,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId)
    {
        userServices.deleteUser(userId);
        ApiResponseMessage message1 = ApiResponseMessage.builder()
                .message("User is Successfully Deleted!!!!")
                .success(true).status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message1,HttpStatus.OK);
    }

    //get by id
    @GetMapping("/{userId}")
    public  ResponseEntity<UserDto> getUser(@PathVariable String userId)
    {
        return new ResponseEntity<>(userServices.getUserById(userId),HttpStatus.OK);
    }

    //get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email)
    {
        return new ResponseEntity<>(userServices.getUserByEmail(email),HttpStatus.OK);
    }

    //search keyword lost
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords)
    {
    return new ResponseEntity<>(userServices.searchUser(keywords),HttpStatus.OK);
    }


    //upload User Image
    @PostMapping("/image_user/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage")MultipartFile image,@PathVariable String userId) throws IOException {
        String ApiImage = fileService.uploadFile(image, imageUploadPath);

        //get the user
        UserDto user = userServices.getUserById(userId);
        user.setImageName(ApiImage);
        //update in the database
        userServices.updateUser(user,userId);

        ImageResponse imageResponse=ImageResponse.builder().imageName(ApiImage).message("Image Uploaded to database").success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //server user Image
    @GetMapping("/image_user/{userId}")
    public void serverUserImage(@PathVariable(required = false) String userId, HttpServletResponse response) throws IOException
    {
        if (userId == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide User Id !!!");
        }
        else {
            UserDto user = userServices.getUserById(userId);
            logger.info("User Image name {} ",user.getImageName());
            InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(resource,response.getOutputStream());
        }
    }

}
