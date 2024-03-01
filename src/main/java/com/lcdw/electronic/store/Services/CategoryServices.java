package com.lcdw.electronic.store.Services;

import com.lcdw.electronic.store.dtos.CategoryDto;
import com.lcdw.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface CategoryServices {

    //create
    CategoryDto createCategory(CategoryDto categoryDto);
    //update
    CategoryDto updateCategory(CategoryDto categoryDto,String categoryId);
    //delete
    void deleteCategory(String categoryId);
    //get
    PageableResponse<CategoryDto> getAllCategory(int pageNumber, int pageSize, String sortBy, String sortDir);
    // get single user
    CategoryDto getCategoryId(String categoryId);
    //search
    List<CategoryDto> searchCategory(String keyword);
}
