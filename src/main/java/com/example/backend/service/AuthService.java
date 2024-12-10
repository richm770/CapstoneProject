package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.FacultyRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final PasswordResetTokenService passwordResetTokenService;

    /**
     * Constructor
     *
     * @param studentRepository: StudentRepository object
     * @param facultyRepository: FacultyRepository object
     * @param passwordEncoder:   PasswordEncoder object
     * @param userRepository:    UserRepository object
     */
    public AuthService(StudentRepository studentRepository, FacultyRepository facultyRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, EmailService emailService, PasswordResetTokenService passwordResetTokenService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    /**
     * This method is used to register a student
     *
     * @param signupRequest: StudentSignupRequest object
     */
    public void registerStudent(StudentSignupRequest signupRequest) {
        Student student = new Student();
        student.setEmail(signupRequest.getEmail().toLowerCase());
        student.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        student.setFirstName(signupRequest.getFirstName());
        student.setLastName(signupRequest.getLastName());
        student.setPhone(signupRequest.getPhone());
        student.setStudentID(signupRequest.getStudentID());

        Authority authority = new Authority();
        authority.setAuthority("STUDENT");
        authority.setUser(student);

        Set<Authority> authorities = student.getUserAuthorities();
        authorities.add(authority);

        student.setUserAuthorities(authorities);
        studentRepository.save(student);
    }

    /**
     * This method is used to register a faculty
     *
     * @param signupRequest: FacultySignupRequest object
     */
    public void registerFaculty(FacultySignupRequest signupRequest) {
        Faculty faculty = new Faculty();
        faculty.setEmail(signupRequest.getEmail().toLowerCase());
        faculty.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        faculty.setFirstName(signupRequest.getFirstName());
        faculty.setLastName(signupRequest.getLastName());
        faculty.setPhone(signupRequest.getPhone());
        faculty.setDepartment(signupRequest.getDepartment());

        Authority authority = new Authority();
        authority.setAuthority("FACULTY");
        authority.setUser(faculty);

        Set<Authority> authorities = faculty.getUserAuthorities();
        authorities.add(authority);

        faculty.setUserAuthorities(authorities);
        facultyRepository.save(faculty);
    }

    /**
     * This method is used to get the user by principal
     *
     * @param principal: Principal object
     * @return User object
     */
    public User getUserByPrincipal(Principal principal) {
        String userEmail = principal.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }

    /**
     * This method is used to generate a reset token to store in the database for a user to reset their password.
     *
     * @param email The email of the user that is resetting their password
     */
    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        String token = UUID.randomUUID().toString();

        passwordResetTokenService.createPasswordResetTokenForUser(user, token);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;

        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
    }

    /**
     * This method is used to update the password in the database of the user who requested to reset their password
     *
     * @param email       Email of the user who is updating their password
     * @param newPassword The new password to store in the database
     */
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        emailService.sendPasswordChangeConfirmationEmail(user.getEmail());
    }
}
