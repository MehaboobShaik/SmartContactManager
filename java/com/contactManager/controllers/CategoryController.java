package com.contactManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.contactManager.entities.Category;
import com.contactManager.services.CategoryServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private static final Logger logger =
            LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryServiceImpl categoryService;

    @PostMapping("/save")
    @ResponseBody
    public Category saveCategory(
            @RequestParam String title,
            @RequestParam String description) {

        logger.info("Received request to save category | title: {}, description: {}",
                title, description);

        Category category = new Category();
        category.setCategoryTitle(title);
        category.setCategoryDescription(description);

        Category savedCategory = categoryService.save(category);

        logger.info("Category saved successfully | ID: {}, Title: {}",
                savedCategory.getCategoryId(),
                savedCategory.getCategoryTitle());

        return savedCategory;
    }
}