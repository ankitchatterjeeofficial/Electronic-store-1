package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.ProductDto;

import java.util.List;

public interface ProductServices
{

    //create

    ProductDto createProduct(ProductDto productDto);
    //update
    ProductDto updateProduct(ProductDto productDto,String productId);

    //getAll
    PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    //delete
    void deleteProduct(String productId);

    //findById
    ProductDto getProductById(String productId);

    //findByTitle
    PageableResponse<ProductDto> getProductByTitle(String title,int pageNumber, int pageSize, String sortBy, String sortDir);

    //getAll live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);


    //getAll stock
    PageableResponse<ProductDto> getAllStock(int pageNumber, int pageSize, String sortBy, String sortDir);

    //getAll price in between
    PageableResponse<ProductDto> findByPriceBetween(int start, int end,int pageNumber,int pageSize, String sortBy, String sortDir);

    //create Product with Category
    ProductDto createWithCategory(ProductDto productDto, String categoryId);

    ProductDto updateWithCategory(String productId,String categoryId);

    PageableResponse<ProductDto> getAllProductsOfCategory(String categoryId,int pageNumber,int pageSize, String sortBy, String sortDir);


}
