package com.sanidhya.contactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sanidhya.contactManager.dao.UserRepository;
import com.sanidhya.contactManager.entities.User;


@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home - Smart Contact Manager ");//add names to task bar
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title", "About - Smart Contact Manager ");//add names to task bar
        return "about";
    }

     @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", " Register- Smart Contact Manager ");//add names to task bar
        model.addAttribute("user", new User());
        return "signup";
    }

    //this handler for registering user
    @RequestMapping(value="/do_register", method= RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user,Model model){

        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageUrl("default.jpg");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.userRepository.save(user);

        model.addAttribute("user",new User());
        model.addAttribute("successMessage", "Successfully Registered");
        return "signup";
      
    }

    //handler for custom login
    @RequestMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }
}
