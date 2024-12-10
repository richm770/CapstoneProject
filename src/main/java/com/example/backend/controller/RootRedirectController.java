package com.example.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    /**
     * Redirects authenticated users who navigate to the root ('/') directory to the dashboard page
     *
     * @param authentication The currently logged-in user's authentication
     * @return A redirect to the dashboard or null
     */
    @GetMapping("/")
    public String handleRootRedirect(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return null;
    }

}
