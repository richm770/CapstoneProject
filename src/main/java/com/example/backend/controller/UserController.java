package com.example.backend.controller;

import com.example.backend.model.Department;
import com.example.backend.model.Faculty;
import com.example.backend.model.Student;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Displays the profile page to the user
     *
     * @param principal The currently logged-in user
     * @param model     The UI model
     * @return The profile page template
     */
    @GetMapping("/profile")
    public String getProfilePage(Principal principal, Model model) {
        User user = authService.getUserByPrincipal(principal);

        if (user instanceof Student) {
            model.addAttribute("user", user);
        } else if (user instanceof Faculty) {
            model.addAttribute("user", user);
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }

        model.addAttribute("departments", Department.values());
        return "profile";
    }

    /**
     * Handles the form submission of a student profile update
     *
     * @param principal          The currently logged-in user
     * @param updatedStudent     The attached, new profile information object
     * @param redirectAttributes Error or success messages to display to the user
     * @return A redirect to the profile information page
     */
    @PostMapping("/update-student-profile")
    public String updateProfile(Principal principal, @ModelAttribute Student updatedStudent, RedirectAttributes redirectAttributes) {
        try {
            userService.updateStudentProfile(principal, updatedStudent);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    /**
     * Handles the form submission of a faculty member profile update
     *
     * @param principal          The currently logged-in user
     * @param updatedFaculty     The attached, new profile information object
     * @param redirectAttributes Error or success messages to display to the user
     * @return A redirect to the profile information page
     */
    @PostMapping("/update-faculty-profile")
    public String updateFacultyProfile(Principal principal, @ModelAttribute Faculty updatedFaculty, RedirectAttributes redirectAttributes) {
        try {
            userService.updateFacultyProfile(principal, updatedFaculty);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
}
