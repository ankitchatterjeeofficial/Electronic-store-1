package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.Entities.Product;

import com.lcdw.electronic.store.dtos.PageableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,String> {

    Page<Product> findByTitleContaining(String subtitle,Pageable pageable);
    Page<Product> findByLiveTrue(Pageable pageable);
    Page<Product> findByStockFalse(Pageable pageable);

   // List<Product> findByPriceBetween(int start,int end);


    Page<Product> findByPriceBetween(int start,int end,Pageable pageable);

    Page<Product> findByCategory(Category category,Pageable pageable);


}
