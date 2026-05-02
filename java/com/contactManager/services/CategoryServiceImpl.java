package com.contactManager.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contactManager.entities.Category;
import com.contactManager.repositories.CategoryRepository;

@Service
public class CategoryServiceImpl {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}