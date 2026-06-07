package com.contactManager.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.contactManager.entities.Category;
import com.contactManager.entities.Post;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repositories.CategoryRepository;
import com.contactManager.repositories.PostRepository;
import com.contactManager.repositories.UserRepository;

@Controller
@RequestMapping("/user/blog")
public class BlogController {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	// 🔹 Open Write Blog Page
	@GetMapping("/create")
	public String openWriteBlog(Model model) {
		model.addAttribute("post", new Post());
		model.addAttribute("categories", categoryRepository.findAll());

		return "blog/add_post";
	}

	@PostMapping("/save")
	public String saveBlog(@ModelAttribute Post post, @RequestParam("categoryId") int categoryId, Principal principal,
			HttpSession session) {

		try {

			if (principal == null) {
				session.setAttribute("message", new Message("Please login first", "alert-danger"));
				return "redirect:/signin";
			}

			Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

			if (!optionalUser.isPresent()) {
				session.setAttribute("message", new Message("User not found", "alert-danger"));
				return "redirect:/signin";
			}

			User user = optionalUser.get();

			// ✅ SET USER
			post.setUser(user);

			// ✅ SET CATEGORY (THIS WAS MISSING)
			Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("Category not found"));

			post.setCategory(category);

			// ✅ SAVE
			postRepository.save(post);

			session.setAttribute("message", new Message("Blog published successfully!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();

			session.setAttribute("message", new Message("Error: " + e.getMessage(), "alert-danger"));
		}

		return "redirect:/user/blog/list";
	}

	// ================= LIST BLOGS =================
	@GetMapping("/list")
	public String listBlogs(Model model) {

		List<Post> posts = postRepository.findAll();

		model.addAttribute("posts", posts);

		return "blog/blog_list";
	}

	@GetMapping("/{postId}")
	public String viewBlog(@PathVariable("postId") int postId, Model model) {

		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

		model.addAttribute("post", post);

		return "blog/blog_details";
	}
}
