package com.lcdw.electronic.store.Services.Impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcdw.electronic.store.Controllers.CategoryController;
import com.lcdw.electronic.store.Controllers.ProductController;
import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.Entities.Product;
import com.lcdw.electronic.store.Exception.ResourceNotFoundException;
import com.lcdw.electronic.store.Helper.Helper;
import com.lcdw.electronic.store.Repository.CategoryRepository;
import com.lcdw.electronic.store.Repository.ProductRepository;
import com.lcdw.electronic.store.Services.FileService;
import com.lcdw.electronic.store.Services.ProductServices;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.ProductDto;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServicesImpl implements ProductServices {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileService fileService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${product.profile.image.path}")
    private String imageUploadPath;


    private Logger logger= LoggerFactory.getLogger(ProductController.class);
    //create Product
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        String productId= UUID.randomUUID().toString();
        Product product=mapper.map(productDto,Product.class);
        product.setProductId(productId);
        product.setAddedDate(new Date());
        //product.setCartItems(null);
        Product saveProduct = productRepository.save(product);

        return mapper.map(saveProduct,ProductDto.class);
    }

    //update Product
    @Override
    public ProductDto updateProduct(ProductDto productDto, String productId) {
        //Get Product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not Found for given Id for Updating the product!!!"));
     //   Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not Found given Id for updating the Product !!!"));
        //update Product
        product.setDescription(productDto.getDescription());
        product.setLive(productDto.isLive());
        product.setTitle(productDto.getTitle());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setStock(productDto.isStock());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        //logger.info("Product image {} ",product.getProductImage());
        product.setProductImage(productDto.getProductImage());
       // product.setCategory(productDto.getCategory());
        //product.setAddedDate(productDto.getAddedDate());

        productRepository.save(product);
        return mapper.map(product,ProductDto.class);
    }
    //Get all Products
    @Override
    public PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findAll(pageable);
        PageableResponse<ProductDto> response= Helper.getPageableResponse(page,ProductDto.class);
        return response;
    }

    //delete ProductId
    @Override
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not Found for given Id Required For deleting the Product !!!"));
        String fullPath = imageUploadPath + product.getProductImage();


        try
        {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }
        catch(NoSuchFileException ex)
        {
            logger.info("Product_image not found in folder!!");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       productRepository.delete(product);
    }

    //find By productId
    @Override
    public ProductDto getProductById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not Found for given Id Required For Searching The Product !!!"));
        return mapper.map(product,ProductDto.class);
    }

    //find by title
    @Override
    public PageableResponse<ProductDto> getProductByTitle(String title,int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByTitleContaining(title, pageable);
        PageableResponse<ProductDto> response = Helper.getPageableResponse(page, ProductDto.class);
        return response;
    }


    //find all live true
    @Override
    public PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByLiveTrue(pageable);
        PageableResponse<ProductDto> response= Helper.getPageableResponse(page,ProductDto.class);
        return response;

    }

    //find all stock false
    @Override
    public PageableResponse<ProductDto> getAllStock(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByStockFalse(pageable);
        PageableResponse<ProductDto> response= Helper.getPageableResponse(page,ProductDto.class);
        return response;
    }

    //find prices in between
    @Override
    public  PageableResponse<ProductDto> findByPriceBetween(int start, int end,int pageNumber,int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByPriceBetween(start,end,pageable);
        PageableResponse<ProductDto> response= Helper.getPageableResponse(page,ProductDto.class);
        return response;

    }

    //create product with Category
    @Override
    public ProductDto createWithCategory(ProductDto productDto, String categoryId) {

        //fetch
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category Id not Found !!"));
        String productId= UUID.randomUUID().toString();
        //productId
        Product product=mapper.map(productDto,Product.class);
        //added
        product.setProductId(productId);
        product.setAddedDate(new Date());
        product.setCategory(category);
       // product.setCartItems(null);
        Product saveProduct = productRepository.save(product);

        return mapper.map(saveProduct,ProductDto.class);
    }

    //assign product with Category
    @Override
    public ProductDto updateWithCategory(String productId, String categoryId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not Found for given Id for Updating the product!!!"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not Found given Id for updating the Product !!!"));
        product.setCategory(category);
       // product.setCartItems(null);
        Product saveProduct = productRepository.save(product);
        return mapper.map(saveProduct,ProductDto.class);
    }

    //find all product for given Category
    @Override
    public PageableResponse<ProductDto> getAllProductsOfCategory(String categoryId,int pageNumber,int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not Found given Id for updating the Product !!!"));
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> page = productRepository.findByCategory(category, pageable);
        PageableResponse<ProductDto> response= Helper.getPageableResponse(page,ProductDto.class);
        return response;
    }
}
