package com.lcdw.electronic.store.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Category")
public class Category {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_title",unique = true,length = 25)
    private String title;
    @Column(name = "category_description",nullable = false,length = 60)
    private String description;
    @Column(name = "category_image")
    private String coverImage;

    @JsonIgnore
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Product> products=new ArrayList<>();
}
