package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.FacultyRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/***
 * This class contains the test cases for the AuthService class
 */
class AuthServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /***
     * This method tests the registerStudent method of the AuthService class
     * It should successfully register a student
     */
    @Test
    void registerStudent_SuccessfullyRegistersStudent() {
        StudentSignupRequest signupRequest = new StudentSignupRequest();
        signupRequest.setEmail("student@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPhone("1234567890");
        signupRequest.setStudentID("S12345");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        authService.registerStudent(signupRequest);

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    /***
     * This method tests the registerFaculty method of the AuthService class
     * It should successfully register a faculty
     */
    @Test
    void registerFaculty_SuccessfullyRegistersFaculty() {
        FacultySignupRequest signupRequest = new FacultySignupRequest();
        signupRequest.setEmail("faculty@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Doe");
        signupRequest.setPhone("0987654321");
        signupRequest.setDepartment(Department.REGISTRARS_OFFICE);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        authService.registerFaculty(signupRequest);

        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    /***
     * This method tests the getUserByPrincipal method of the AuthService class
     * It should return the user with the given principal
     */
    @Test
    void getUserByPrincipal_ReturnsUser() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = authService.getUserByPrincipal(principal);

        assertEquals("user@example.com", result.getEmail());
    }

    /***
     * This method tests the getUserByPrincipal method of the AuthService class
     * It should throw a UsernameNotFoundException if the user is not found
     */
    @Test
    void getUserByPrincipal_UserNotFound_ThrowsException() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.getUserByPrincipal(principal));
    }
}