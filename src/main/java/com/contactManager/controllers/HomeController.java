package com.contactManager.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repositories.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	// HOME
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	// SIGNUP PAGE
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// REGISTER USER
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(defaultValue = "false") boolean agreement, Model model, HttpSession session) {

		// 1️⃣ Validation errors
		if (result.hasErrors()) {
			return "signup";
		}

		// 2️⃣ Terms not accepted
		if (!agreement) {
			session.setAttribute("message", new Message("Please accept Terms & Conditions", "alert-danger"));
			return "signup";
		}

		// 3️⃣ Duplicate email check
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			session.setAttribute("message", new Message("User already exists with this email", "alert-warning"));
			return "signup";
		}

		// 4️⃣ Save user
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		userRepository.save(user);

		// 5️⃣ Reset form & success message
		model.addAttribute("user", new User());
		session.setAttribute("message", new Message("Successfully registered! Please login.", "alert-success"));

		return "signup";
	}

	// LOGIN
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
}
