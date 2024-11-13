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

    /***
     * Constructor for auth controller
     * @param authService The auth service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /***
     * Expose the login page
     * @return The login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /***
     * Expose the student signup page
     * @param model The model
     * @return The student signup page
     */
    @GetMapping("/student-signup")
    public String signup(Model model) {
        model.addAttribute("signupRequest", new StudentSignupRequest());
        return "student-signup";
    }

    /***
     * Process the student signup form
     * @param signupRequest The signup request
     * @param model The model
     * @return The redirect to the dashboard
     */
    @PostMapping("/student-signup")
    public String processStudentSignup(@ModelAttribute StudentSignupRequest signupRequest, Model model) {

        // Validate that passwords match
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("signupRequest", signupRequest);
            return "student-signup";
        }
        // Validate that phone number is 10 digits long
        if (!isValidPhoneNumber(signupRequest.getPhone())) {
            model.addAttribute("error", "Invalid phone number");
            model.addAttribute("signupRequest", signupRequest);
            return "student-signup";
        }
        // Validate that password is at least 8 characters long
        if (signupRequest.getPassword().length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long");
            model.addAttribute("signupRequest", signupRequest);
            return "student-signup";
        }
        authService.registerStudent(signupRequest);
        return "redirect:/dashboard";
    }

    /***
     * Expose the faculty signup page
     * @param model The model
     * @return The faculty signup page
     */
    @GetMapping("/faculty-signup")
    public String facultySignup(Model model) {
        model.addAttribute("signupRequest", new FacultySignupRequest());
        return "faculty-signup";
    }

    /***
     * Process the faculty signup form
     * @param signupRequest The signup request
     * @param model The model
     * @return The redirect to the dashboard
     */
    @PostMapping("/faculty-signup")
    public String processFacultySignup(@ModelAttribute FacultySignupRequest signupRequest, Model model) {

        // Validate that passwords match
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("signupRequest", signupRequest);
            return "faculty-signup";
        }
        // Validate that phone number is 10 digits long
        if (!isValidPhoneNumber(signupRequest.getPhone())) {
            model.addAttribute("error", "Invalid phone number");
            model.addAttribute("signupRequest", signupRequest);
            return "student-signup";
        }
        // Validate that password is at least 8 characters long
        if (signupRequest.getPassword().length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long");
            model.addAttribute("signupRequest", signupRequest);
            return "faculty-signup";
        }
        authService.registerFaculty(signupRequest);
        return "redirect:/dashboard";
    }

    /***
     * Validates that the phone number is 10 digits long
     * @param phone The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        return phone.matches(phoneRegex);
    }

}