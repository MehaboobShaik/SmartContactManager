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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@RequestMapping(value = "/")
	public String home(Model model) {
		model.addAttribute("title", "Home - smart contact manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - smart contact manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Register - smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (!agreement) {
				throw new Exception("not agreed terms and conditions");
			}
			if (bindingResult.hasErrors() == true) {
				System.out.println("error::" + bindingResult.toString());
				model.addAttribute("user", new User());
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(agreement);
			user.setImageUrl("Default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		    userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Sucessfully Registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong here", "alert-danger"));
			return "signup";
		}

	}

	// handler for custom login

	@GetMapping("/signin")
	public String customLogin(Model model) {
		return "login";
	}
}
