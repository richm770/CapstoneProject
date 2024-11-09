package com.example.backend.controller;

import com.example.backend.model.FacultySignupRequest;
import com.example.backend.service.AuthService;
import com.example.backend.model.StudentSignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/student-signup")
    public String signup(Model model) {
        model.addAttribute("signupRequest", new StudentSignupRequest());
        return "student-signup";
    }

    @PostMapping("/student-signup")
    public String processSignup(@ModelAttribute StudentSignupRequest signupRequest, Model model) {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "student-signup";
        }
        authService.registerStudent(signupRequest);
        return "redirect:dashboard";
    }

    @GetMapping("/faculty-signup")
    public String facultySignup(Model model) {
        model.addAttribute("signupRequest", new FacultySignupRequest());
        return "faculty-signup";
    }

    @PostMapping("/faculty-signup")
    public String processFacultySignup(@ModelAttribute FacultySignupRequest signupRequest, Model model) {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "faculty-signup";
        }
        authService.registerFaculty(signupRequest);
        return "redirect:/dashboard";
    }
}
