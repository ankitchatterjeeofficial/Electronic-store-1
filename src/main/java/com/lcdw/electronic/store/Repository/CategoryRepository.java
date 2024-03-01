package com.lcdw.electronic.store.Repository;

import com.lcdw.electronic.store.Entities.Category;
import com.lcdw.electronic.store.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,String> {

    List<Category> findByTitleContaining(String keyword);
}
