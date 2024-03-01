package com.lcdw.electronic.store.Controllers;

import com.lcdw.electronic.store.Services.CategoryServices;
import com.lcdw.electronic.store.Services.FileService;
import com.lcdw.electronic.store.Services.Impl.CategoryServicesImpl;
import com.lcdw.electronic.store.dtos.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryServices categoryServices;

    @Autowired
    private FileService fileService;

    @Value("${category.profile.image.path}")
    private String imageUploadPath;

    private Logger logger= LoggerFactory.getLogger(CategoryController.class);


    //create
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto)
    {
        CategoryDto category1 = categoryServices.createCategory(categoryDto);
        return new ResponseEntity<>(category1, HttpStatus.OK);
    }

    //update
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,@PathVariable String categoryId)
    {
        CategoryDto categoryDto1 = categoryServices.updateCategory(categoryDto,categoryId);
        return new ResponseEntity<>(categoryDto1,HttpStatus.OK);

    }
    //get all list
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategory(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<CategoryDto> response=categoryServices.getAllCategory(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId)
    {
        categoryServices.deleteCategory(categoryId);
        ApiResponseMessage message1 = ApiResponseMessage.builder().message("Category is deleted !!")
                .status(HttpStatus.OK)
                .success(true).build();

        return new ResponseEntity<>(message1,HttpStatus.OK);
    }

    //Get CATEGORY by ID

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryByID(@PathVariable String categoryId)
    {
      return new ResponseEntity<>( categoryServices.getCategoryId(categoryId),HttpStatus.OK);

    }

    //SEARCH CATEGORY BY ID

    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<CategoryDto>> searchCategory(@PathVariable String keywords)
    {
        return new ResponseEntity<>(categoryServices.searchCategory(keywords),HttpStatus.OK);
    }

    //Upload Category Image
    @PostMapping("/image_category/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryImage(@RequestParam("categoryImage") MultipartFile image, @PathVariable String categoryId) throws IOException {
        String ApiImage = fileService.uploadFile(image, imageUploadPath);
        logger.info("get  Category API image {} ",ApiImage);
        //get the category
        CategoryDto categoryDto=categoryServices.getCategoryId(categoryId);
        categoryDto.setCoverImage(ApiImage);

        //update in the database
        categoryServices.updateCategory(categoryDto,categoryId);

        ImageResponse imageResponse=ImageResponse.builder().imageName(ApiImage).message("Image Uploaded to database !!").success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //Server Category Image
    @GetMapping("/image_category/{categoryId}")
    public void serverCategoryImage(@PathVariable String categoryId, HttpServletResponse response) throws IOException {
        if (categoryId == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide Category Id !!!");
        }
        else {
            CategoryDto category = categoryServices.getCategoryId(categoryId);
            logger.info("Category Image name {} ",category.getCoverImage());
            InputStream resource = fileService.getResource(imageUploadPath, category.getCoverImage());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(resource,response.getOutputStream());
        }
    }

}
