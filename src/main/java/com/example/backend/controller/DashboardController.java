package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "dashboard";
    }

}
