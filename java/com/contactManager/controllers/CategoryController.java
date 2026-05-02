package com.contactManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contactManager.entities.Category;
import com.contactManager.repositories.CategoryRepository;

public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	@PostMapping("/category/save")
	@ResponseBody
	public Category saveCategory(@RequestParam String title, @RequestParam String description) {

		Category category = new Category();
		category.setCategoryTitle(title);
		category.setCategoryDescription(description);

		return categoryRepository.save(category);
	}

}
