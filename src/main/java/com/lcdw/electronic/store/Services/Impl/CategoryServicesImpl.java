package com.lcdw.electronic.store.Services.Impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.Entities.Product;
import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.Exception.ResourceNotFoundException;
import com.lcdw.electronic.store.Helper.Helper;
import com.lcdw.electronic.store.Repository.CategoryRepository;
import com.lcdw.electronic.store.Repository.ProductRepository;
import com.lcdw.electronic.store.Services.CategoryServices;
import com.lcdw.electronic.store.Services.FileService;
import com.lcdw.electronic.store.Services.ProductServices;
import com.lcdw.electronic.store.dtos.CategoryDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.ProductDto;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@JsonIgnoreProperties("products")
public class CategoryServicesImpl implements CategoryServices {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductServices productServices;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private FileService fileService;

    @Autowired
    private  ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${category.profile.image.path}")
    private String imageUploadPath;
    private Logger logger= LoggerFactory.getLogger(CategoryServicesImpl.class);

    //CREATE CATEGORY
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        String categoryId= UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);
        Category category=mapper.map(categoryDto,Category.class);
        Category saveCategory = categoryRepository.save(category);
        return mapper.map(saveCategory,CategoryDto.class);
    }

    //UPDATE CATEGORY
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, String categoryId) {
        //get Category for id
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Not found Given Id!!"));
        //update category
       // category.setCategoryId(categoryDto.getCategoryId());
        category.setDescription(categoryDto.getDescription());
        category.setTitle(categoryDto.getTitle());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);
        return mapper.map(updatedCategory,CategoryDto.class);
    }

    //DELETE category BY ID
    @Override
    public void deleteCategory(String categoryId) {

        //find
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Not found Given Id 2!!"));
        String fullPath = imageUploadPath + category.getCoverImage();

        List<Product> products=category.getProducts();
        for(Product product:products)
        {
            product.setCategory(null);
           // ProductDto updatedProduct = mapper.map(product, ProductDto.class);
           // productServices.updateProduct(updatedProduct,updatedProduct.getProductId());
             productRepository.save(product);
            ProductDto updatedProduct = mapper.map(product, ProductDto.class);
        }
        try
        {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }
        catch(NoSuchFileException ex)
        {
            logger.info("Category_image not found in folder!!");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //delete
        categoryRepository.delete(category);

    }
    //get ALL categories
    @Override
    public PageableResponse<CategoryDto> getAllCategory(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);


        Page<Category> page=categoryRepository.findAll(pageable);
        PageableResponse<CategoryDto> response=Helper.getPageableResponse(page,CategoryDto.class);
        return response;
    }



    //Get Category By ID
    @Override
    public CategoryDto getCategoryId(String categoryId) {
        //get the user
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Not found Given Id 3!!"));
        return  mapper.map(category,CategoryDto.class);
    }

    //Search
    @Override
    public List<CategoryDto> searchCategory(String keyword) {
       // ObjectMapper mapper = new ObjectMapper();
//        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Category> categoriesLists = categoryRepository.findByTitleContaining(keyword);
   //  List<CategoryDto> categoryDtoList = categoriesLists.stream().map(categoriesList -> mapper.map(categoriesList, CategoryDto.class)).collect(Collectors.toList());
      return categoriesLists.stream().map(o-> objectMapper.convertValue(o,CategoryDto.class)).collect(Collectors.toList());
      // return categoryDtoList;
    }


}
