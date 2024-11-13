package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.FacultyRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(StudentRepository studentRepository, FacultyRepository facultyRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void registerStudent(StudentSignupRequest signupRequest) {
        Student student = new Student();
        student.setEmail(signupRequest.getEmail());
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

    public void registerFaculty(FacultySignupRequest signupRequest) {
        Faculty faculty = new Faculty();
        faculty.setEmail(signupRequest.getEmail());
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

    public User getUserByPrincipal(Principal principal) {
        String userEmail = principal.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}
