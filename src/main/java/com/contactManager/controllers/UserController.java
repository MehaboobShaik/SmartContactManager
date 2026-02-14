package com.contactManager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactManager.entities.Contact;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repositories.ContactRepository;
import com.contactManager.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	/*
	 * ========================= COMMON USER DATA =========================
	 */
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		if (principal == null)
			return;

		Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
		optionalUser.ifPresent(user -> {
			model.addAttribute("user", user);
		});
	}

	@GetMapping("/index")
	public String dashboard() {
		return "normal/user_dashboard";
	}

	/*
	 * ========================= ADD CONTACT =========================
	 */
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			User user = userRepository.findByEmail(principal.getName()).get();

			handleImageUpload(contact, file);

			sanitizeDescription(contact);

			user.getContacts().add(contact);
			contact.setUser(user);

			userRepository.save(user);

			session.setAttribute("message", new Message("Contact added successfully", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
		}

		return "normal/add_contact_form";
	}

	/*
	 * ========================= SHOW CONTACTS (PAGINATION)
	 * =========================
	 */
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable Integer page, Model model, Principal principal) {

		User user = userRepository.findByEmail(principal.getName()).get();

		Pageable pageable = PageRequest.of(page, 6);
		Page<Contact> contacts = contactRepository.findContactByUser(user.getId(), pageable);

		model.addAttribute("allContacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	/*
	 * ========================= CONTACT DETAILS =========================
	 */
	@GetMapping("/{contact_id}/contact")
	public String showContactDetails(@PathVariable("contact_id") Integer contactId, Model model, Principal principal) {

		Contact contact = contactRepository.findById(contactId).get();
		User user = userRepository.findByEmail(principal.getName()).get();

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact-detail";
	}

	/*
	 * ========================= DELETE CONTACT =========================
	 */
	@GetMapping("/delete/{contact_id}")
	public String deleteContact(@PathVariable("contact_id") Integer contactId, Principal principal,
			HttpSession session) {

		Contact contact = contactRepository.findById(contactId).get();
		User user = userRepository.findByEmail(principal.getName()).get();

		if (user.getId() == contact.getUser().getId()) {
			user.getContacts().remove(contact);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));
		} else {
			session.setAttribute("message", new Message("Something went wrong", "alert-danger"));
		}

		return "redirect:/user/show_contacts/0";
	}

	/*
	 * ========================= UPDATE CONTACT =========================
	 */
	@PostMapping("/update-contact/{contact_id}")
	public String updateContact(@PathVariable("contact_id") Integer contactId, Model model) {

		Contact contact = contactRepository.findById(contactId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			User user = userRepository.findByEmail(principal.getName()).get();
			Contact oldContact = contactRepository.findById(contact.getContact_id()).get();

			updateImage(contact, file, oldContact);

			sanitizeDescription(contact);

			contact.setUser(user);
			contactRepository.save(contact);

			session.setAttribute("message", new Message("Contact updated successfully", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
		}

		return "redirect:/user/" + contact.getContact_id() + "/contact";
	}

	/*
	 * ========================= HELPER METHODS =========================
	 */

	private void handleImageUpload(Contact contact, MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			contact.setImage("contact.png");
			return;
		}

		contact.setImage(file.getOriginalFilename());

		File saveDir = new ClassPathResource("static/img").getFile();
		Path path = Paths.get(saveDir.getAbsolutePath(), file.getOriginalFilename());

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	}

	private void updateImage(Contact contact, MultipartFile file, Contact oldContact) throws Exception {

		if (file.isEmpty()) {
			contact.setImage(oldContact.getImage());
			return;
		}

		File imgDir = new ClassPathResource("static/img").getFile();
		File oldFile = new File(imgDir, oldContact.getImage());
		if (oldFile.exists())
			oldFile.delete();

		contact.setImage(file.getOriginalFilename());

		Path path = Paths.get(imgDir.getAbsolutePath(), file.getOriginalFilename());

		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	}

	private void sanitizeDescription(Contact contact) {
		if (contact.getDescription() != null) {
			String clean = contact.getDescription().replaceAll("\\<.*?>", "");
			contact.setDescription(clean);
		}
	}
}
