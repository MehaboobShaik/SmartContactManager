package com.contactManager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

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

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	/** method for adding common data */

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String name = principal.getName();
		User user = userRepository.findByEmail(name).get();
		model.addAttribute("user", user);
		System.out.println(user.toString());
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		return "normal/user_dashboard";
	}

	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.findByEmail(name).get();
			// processing na uploading file
			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");

			} else {

				// upload file to the folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				System.out.println("saveFile::" + " " + saveFile);
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println("path::" + " " + path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
				// session.setAttribute("message", new Message("Contact added
				// asucessfully","alert-success"));

			}
			String description = contact.getDescription();
			description = description.replaceAll("\\<.*?>", "");
			contact.setDescription(description);
			user.getContacts().add(contact);
			contact.setUser(user);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact added asucessfully", "alert-success"));
			System.out.println(contact);
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong:" + e.getMessage(), "alert-danger"));

		}
		return "normal/add_contact_form";

	}

	// per page 5 contacts,current page=0.
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepository.findByEmail(userName).get();
		Pageable pageable = PageRequest.of(page, 6);
		int id = user.getId();
		Page<Contact> contacts = contactRepository.findContactByUser(id, pageable);
		model.addAttribute("allContacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";

	}

	/** showing particular contact detail */
	@GetMapping("/{contact_id}/contact")
	public String showContactDetails(@PathVariable("contact_id") Integer cId, Model model, Principal principal,
			HttpSession session) {
		Contact contact = this.contactRepository.findById(cId).get();
		String name = principal.getName();
		User user = this.userRepository.findByEmail(name).get();
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		} else {
			System.out.println("you don't have a access");
		}
		return "normal/contact-detail";

	}

	@GetMapping("/delete/{contact_id}")
	public String deleteContact(@PathVariable("contact_id") Integer contact_id, Model model, Principal principal,
			HttpSession session) {

		Contact contact = this.contactRepository.findById(contact_id).get();
		String name = principal.getName();
		User user = this.userRepository.findByEmail(name).get();
		if (user.getId() == contact.getUser().getId()) {
			// this.contactRepository.delete(contact);
			user.getContacts().remove(contact);
			this.userRepository.save(user);
			System.out.println("contact deleted sucessfully");
			session.setAttribute("message", new Message("Contact deleted sucessfully..", "alert-success"));
		} else {
			session.setAttribute("message", new Message("Something went wrong..", "alert-danger"));

		}
		return "redirect:/user/show_contacts/0";

	}

	@PostMapping("/update-contact/{contact_id}")
	public String updateContact(@PathVariable("contact_id") Integer contact_id, Model model, Principal principal,
			HttpSession session) {
		Contact contact = this.contactRepository.findById(contact_id).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";

	}

	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.findByEmail(name).get();
			Contact oldContact = contactRepository.findById(contact.getContact_id()).get();
			System.out.println(oldContact);
			// processing and uploading file
			if (file.isEmpty()) {
				System.out.println("file is empty");
				String image = oldContact.getImage();
				System.out.println(image);
				contact.setImage(image);

			} else {
				// delete
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file2 = new File(deleteFile, oldContact.getImage());
				file2.delete();
				// upload file to the folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				System.out.println("saveFile::" + " " + saveFile);
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println("path::" + " " + path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
				// session.setAttribute("message", new Message("Contact added
				// asucessfully","alert-success"));

			}
			String description = contact.getDescription();
			description = description.replaceAll("\\<.*?>", "");
			contact.setDescription(description);
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact updated a sucessfully", "alert-success"));
			System.out.println(contact);
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong:" + e.getMessage(), "alert-danger"));

		}
		return "redirect:/user/" + contact.getContact_id() + "/contact";

	}
}
