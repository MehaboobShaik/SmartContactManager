package com.contactManager.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.contactManager.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
