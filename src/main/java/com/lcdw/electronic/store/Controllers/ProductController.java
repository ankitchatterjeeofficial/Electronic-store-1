package com.lcdw.electronic.store.Controllers;


import com.lcdw.electronic.store.Exception.BadApiRequest;
import com.lcdw.electronic.store.Services.FileService;
import com.lcdw.electronic.store.Services.ProductServices;
import com.lcdw.electronic.store.dtos.ApiResponseMessage;
import com.lcdw.electronic.store.dtos.ImageResponse;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.ProductDto;
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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductServices productServices;

    @Autowired
    private FileService fileService;

    @Value("${product.profile.image.path}")
    private String imageUploadPath;

    private Logger logger= LoggerFactory.getLogger(ProductController.class);

    //create
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto)
    {
        ProductDto product = productServices.createProduct(productDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
    //update
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto,@PathVariable String productId)
    {
        ProductDto product = productServices.updateProduct(productDto, productId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    //getAll
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<ProductDto> allProducts = productServices.getAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProducts,HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId )
    {
        productServices.deleteProduct(productId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Product is deleted")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    //find by id
    @GetMapping("/searchSingle/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String productId)
    {
        ProductDto product = productServices.getProductById(productId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    // find productS by title
    @GetMapping("/search/{title}")
    public ResponseEntity<PageableResponse<ProductDto>> getProductsByTitle(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir,
            @PathVariable String title
    )
    {
        PageableResponse<ProductDto> response = productServices.getProductByTitle(title, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    //find all live true
    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLive(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<ProductDto> allProducts = productServices.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProducts,HttpStatus.OK);
    }
    //find all stock false
    @GetMapping("/stock")
    public ResponseEntity<PageableResponse<ProductDto>> getAllStock(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<ProductDto> allProducts = productServices.getAllStock(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProducts,HttpStatus.OK);
    }
    //find prices in between
    @GetMapping("/priceBetween")
    public ResponseEntity<PageableResponse<ProductDto>> getPriceInBetween(
            @RequestParam(value = "start",defaultValue = "0",required = false)int start,
            @RequestParam(required = false,defaultValue = "100000")int end,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy",required = false, defaultValue = "price")String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir

            ) {
        if (start > end) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start price should be smaller than end Price!!!");
        } else {
            PageableResponse<ProductDto> price = productServices.findByPriceBetween(start, end, pageNumber, pageSize, sortBy, sortDir);
            return new ResponseEntity<>(price, HttpStatus.OK);
        }

    }

        // upload Product Image
        @PostMapping("/image_product/{productId}")
        public ResponseEntity<ImageResponse> serverProductImage(@RequestParam("productImage") MultipartFile image, @PathVariable String productId) throws IOException {
            String ApiImage = fileService.uploadFile(image, imageUploadPath);

            //get the product
            ProductDto productDto = productServices.getProductById(productId);
            logger.info("get API image {} ",ApiImage);
            productDto.setProductImage(ApiImage);
            //update the product
            productServices.updateProduct(productDto,productId);

            ImageResponse response = ImageResponse.builder().imageName(ApiImage).message("Image uploaded To Database !! ").status(HttpStatus.CREATED).success(true).build();
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }

        //serve the Product Image
        @GetMapping("/image_product/{productId}")
        public void serverProductImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
            if (productId == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide Product Id !!!");
            }
            else {
                ProductDto productDto = productServices.getProductById(productId);
                logger.info("Product Image name {} ",productDto.getProductImage());
                InputStream resource = fileService.getResource(imageUploadPath, productDto.getProductImage());
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                StreamUtils.copy(resource,response.getOutputStream());
            }
        }

        //create fresh product with given Category
    @PostMapping("/create_with_category/{categoryId}")
    public ResponseEntity<ProductDto> createProductWithCategory(@PathVariable String categoryId, @Valid @RequestBody ProductDto productDto)
    {
        ProductDto withCategory = productServices.createWithCategory(productDto, categoryId);
        return new ResponseEntity<>(withCategory,HttpStatus.CREATED);
    }
    //assign existing product with given Category
    @PutMapping("/{productId}/assign_with_category/{categoryId}")
    public ResponseEntity<ProductDto> updateProductWithCategory(@PathVariable String categoryId,@PathVariable String productId)
    {
        ProductDto productDto = productServices.updateWithCategory(productId, categoryId);
        return new ResponseEntity<>(productDto,HttpStatus.OK);
    }
    // get all products for the given Category
    @GetMapping("/get_all_category/{categoryId}")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProductsWithCategory(
            @PathVariable String categoryId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)int pageNumber,
            @RequestParam(value ="pageSize",defaultValue = "10",required = false)int pageSize,
            @RequestParam(value="sortBy", defaultValue = "title",required = false)String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc",required = false)String sortDir
    )
    {
        PageableResponse<ProductDto> allProductsOfCategory = productServices.getAllProductsOfCategory(categoryId, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProductsOfCategory,HttpStatus.FOUND);
    }
    }




