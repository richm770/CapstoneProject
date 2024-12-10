package com.example.backend.controller;

import com.example.backend.model.StudentSignupRequest;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test the AuthController class
 */
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /***
     * Test the processStudentSignup method
     * Test that the method returns the student-signup page when the passwords do not match
     */
    @Test
    void processStudentSignup_PasswordsDoNotMatch() {
        StudentSignupRequest signupRequest = new StudentSignupRequest();
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password456");

        String result = authController.processStudentSignup(signupRequest, model);

        assertEquals("student-signup", result);
        verify(model).addAttribute("error", "Passwords do not match");
        verify(model).addAttribute("signupRequest", signupRequest);
    }

    /***
     * Test the processStudentSignup method
     * Test that the method returns the student-signup page when the password is too short
     */
    @Test
    void processStudentSignup_PasswordTooShort() {
        StudentSignupRequest signupRequest = new StudentSignupRequest();
        signupRequest.setPassword("short");
        signupRequest.setConfirmPassword("short");
        signupRequest.setPhone("1234567890");
        signupRequest.setEmail("j@mail.com");

        String result = authController.processStudentSignup(signupRequest, model);

        assertEquals("student-signup", result);
        verify(model).addAttribute("error", "Password must be at least 8 characters long");
        verify(model).addAttribute("signupRequest", signupRequest);
    }

    /***
     * Test the processStudentSignup method
     * Test that the method returns the student-signup page when the phone number is invalid
     */
    @Test
    void processStudentSignup_InvalidPhoneNumber() {
        StudentSignupRequest signupRequest = new StudentSignupRequest();
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");
        signupRequest.setPhone("123");

        String result = authController.processStudentSignup(signupRequest, model);

        assertEquals("student-signup", result);
        verify(model).addAttribute("error", "Invalid phone number");
        verify(model).addAttribute("signupRequest", signupRequest);
    }

    /***
     * Test the processStudentSignup method
     * Test that the method returns the dashboard page when the request is valid
     */
    @Test
    void processStudentSignup_ValidRequest() {
        StudentSignupRequest signupRequest = new StudentSignupRequest();
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");
        signupRequest.setPhone("1234567890");

        String result = authController.processStudentSignup(signupRequest, model);

        assertEquals("redirect:/dashboard", result);
        verify(authService).registerStudent(signupRequest);
    }
}
