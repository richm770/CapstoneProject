package com.example.backend.service;

import com.example.backend.model.Faculty;
import com.example.backend.model.Student;
import com.example.backend.model.User;
import com.example.backend.repository.FacultyRepository;
import com.example.backend.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
public class UserService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AuthService authService;

    public UserService(StudentRepository studentRepository, FacultyRepository facultyRepository, AuthService authService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.authService = authService;
    }

    /**
     * Updates the profile of a student
     *
     * @param principal      The currently logged-in user
     * @param updatedStudent The new student profile information
     */
    public void updateStudentProfile(Principal principal, Student updatedStudent) {
        User user = authService.getUserByPrincipal(principal);

        if (!(user instanceof Student student)) {
            throw new IllegalArgumentException("User must be a student");
        }

        // Validate that phone number is 10 digits long
        if (!authService.isValidPhoneNumber(updatedStudent.getPhone())) {
            throw new IllegalArgumentException("Phone number must be 10 digits long.");
        }

        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setPhone(updatedStudent.getPhone());
        student.setStudentID(updatedStudent.getStudentID());

        studentRepository.save(student);
    }

    /**
     * Updates the profile of a faculty member
     *
     * @param principal      The currently logged-in user
     * @param updatedFaculty The new faculty profile information
     */
    public void updateFacultyProfile(Principal principal, Faculty updatedFaculty) {
        User user = authService.getUserByPrincipal(principal);

        if (!(user instanceof Faculty faculty)) {
            throw new IllegalArgumentException("User must be a faculty member");
        }

        // Validate that phone number is 10 digits long
        if (!authService.isValidPhoneNumber(updatedFaculty.getPhone())) {
            throw new IllegalArgumentException("Phone number must be 10 digits long.");
        }

        faculty.setFirstName(updatedFaculty.getFirstName());
        faculty.setLastName(updatedFaculty.getLastName());
        faculty.setPhone(updatedFaculty.getPhone());
        faculty.setDepartment(updatedFaculty.getDepartment());

        facultyRepository.save(faculty);
    }

}
