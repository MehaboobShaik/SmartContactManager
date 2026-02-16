
package com.contactManager.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.contactManager.entities.Post;
import com.contactManager.entities.User;



public interface PostRepository extends JpaRepository<Post, Integer>{

	public List<Post>findByUser(User user);
//	public List<Post>findByCategory(Category category);
//	public Page<Post>findByUser(User user,Pageable p);
//	public Page<Post>findByCategory(Category category,Pageable p);
	public List<Post>findByTitleContaining(String title);

	
}
