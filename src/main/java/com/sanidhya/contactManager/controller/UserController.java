package com.sanidhya.contactManager.controller;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sanidhya.contactManager.dao.ContactRepository;
import com.sanidhya.contactManager.dao.UserRepository;
import com.sanidhya.contactManager.entities.Contact;
import com.sanidhya.contactManager.entities.User;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model , Principal principal){
		String userName = principal.getName();
		System.out.println("USERNAME "+ userName);

		User user = userRepository.getUserByUserName(userName);
		//get the user using username(Email)
		System.out.println("USER " + user );

		model.addAttribute("user",user);
	}
    
    // dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	//open add form handler
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model){
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(Model model,@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal){
		model.addAttribute("title", "Add Contacts");
		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// processing and uploading file..

			if (file.isEmpty()) {
				// if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact_logo.png");

			} else {
				// file the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");

			}

			user.getContacts().add(contact);

			contact.setUser(user);

			this.userRepository.save(user);

			System.out.println("DATA " + contact);

			System.out.println("Added to data base");

			// message success.......
			model.addAttribute("successMessage", "Successfully Added Contact");

		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
			// message error
			//session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));

		}
		return "normal/add_contact_form";
	}

	//show contacts handler
	@RequestMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page,Model model, Principal principal){
		model.addAttribute("title", "View Contacts");

		//String userName = principal.getName();
		// User user = this.userRepository.getUserByUserName(userName);
		
		
		//Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		// model.addAttribute("contacts", contacts);
		
		//contact ki list ko bhejna hai
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		// currentPage-page
		// Contact Per page - 5
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);

		model.addAttribute("contacts", contacts);
		 model.addAttribute("currentPage", page);
		 model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	//show particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal){
		System.out.println("cid"+ cId);

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		//this is a security check so that one can only view his own contact 
		//rather changing url and getting another data
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}

	// delete contact handler

	@RequestMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model,
			Principal principal) {
		//Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = this.contactRepository.findById(cId).get();


		contact.setUser(null);

		this.contactRepository.delete(contact);
		
		System.out.println("DELETED");
		
			// message success.......
		


		return "redirect:/user/show_contacts/0";
	}

	//open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,  Model model){
		model.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cId).get();

		model.addAttribute("contact",contact);

		return"normal/update_form"; 
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, Principal principal) {

		try {

			// old contact details
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

			// image..
			if (!file.isEmpty()) {
				// file work..
				// rewrite

//				delete old photo

				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldcontactDetail.getImage());
				file1.delete();

//				update new photo

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepository.save(contact);

			//sucess message

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("CONTACT NAME " + contact.getName());
		System.out.println("CONTACT ID " + contact.getcId());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// open settings handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	// change password..handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal,Model model ) {
		System.out.println("OLD PASSWORD " + oldPassword);
		System.out.println("NEW PASSWORD " + newPassword);

		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// change the password

			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			//session.setAttribute("message", new Message("Your password is successfully changed..", "success"));
			// message success.......
			model.addAttribute("successMessage", "Successfully Added Contact");

		} else {
			// error...
			//session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}

}
