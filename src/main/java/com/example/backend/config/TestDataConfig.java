package com.example.backend.config;

import com.example.backend.model.Authority;
import com.example.backend.model.Department;
import com.example.backend.model.Faculty;
import com.example.backend.model.Student;
import com.example.backend.repository.FacultyRepository;
import com.example.backend.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class TestDataConfig {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataConfig(StudentRepository studentRepository, FacultyRepository facultyRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner insertTestStudent() {
        return args -> {
            Student student = new Student();
            student.setFirstName("John");
            student.setLastName("Doe");
            student.setEmail("student@example.com");
            student.setPassword(passwordEncoder.encode("password"));
            student.setStudentID("1333567");
            student.setPhone("888-888-8888");

            Authority authority = new Authority();
            authority.setAuthority("STUDENT");
            authority.setUser(student);

            Set<Authority> authorities = student.getUserAuthorities();
            authorities.add(authority);

            student.setUserAuthorities(authorities);
            studentRepository.save(student);
        };
    }

    @Bean
    public CommandLineRunner insertTestFaculty() {
        return args -> {
            Faculty faculty = new Faculty();
            faculty.setFirstName("John");
            faculty.setLastName("Doe");
            faculty.setEmail("faculty@example.com");
            faculty.setPassword(passwordEncoder.encode("password"));
            faculty.setPhone("888-888-8888");
            faculty.setDepartment(Department.ADMISSIONS);

            Authority authority = new Authority();
            authority.setAuthority("FACULTY");
            authority.setUser(faculty);

            Set<Authority> authorities = faculty.getUserAuthorities();
            authorities.add(authority);

            faculty.setUserAuthorities(authorities);
            facultyRepository.save(faculty);
        };
    }
}
