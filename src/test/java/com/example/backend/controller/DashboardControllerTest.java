package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyList;

/***
 * Test the DashboardController class
 */
class DashboardControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /***
     * Test the dashboard method
     * Test that the method returns the dashboard page when the user is not found
     */
    @Test
    void dashboard_UserNotFound() {
        when(principal.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        try {
            dashboardController.dashboard(principal, model);
        } catch (UsernameNotFoundException e) {
            assertEquals("User not found with email: user@example.com", e.getMessage());
        }
    }

    /***
     * Test the dashboard method
     * Test that the method returns the dashboard page when the user is a faculty member with requests
     */
    @Test
    void dashboard_FacultyWithRequests() {
        Faculty faculty = new Faculty();
        faculty.setEmail("faculty@example.com");
        Department department = Department.REGISTRARS_OFFICE;
        faculty.setDepartment(department);

        when(principal.getName()).thenReturn("faculty@example.com");
        when(userRepository.findByEmail("faculty@example.com")).thenReturn(Optional.of(faculty));
        when(requestService.getFacultyRequests("faculty@example.com", department)).thenReturn(List.of(new CourseRegistrationRequest(), new StudentHousingRequest(), new LeaveOfAbsenceRequest()));

        String result = dashboardController.dashboard(principal, model);

        assertEquals("dashboard", result);
        verify(model).addAttribute("user", faculty);
        verify(model).addAttribute(eq("courseRequests"), anyList());
        verify(model).addAttribute(eq("housingRequests"), anyList());
        verify(model).addAttribute(eq("leaveRequests"), anyList());
        verify(model).addAttribute("activePage", "dashboard");
    }

    /***
     * Test the dashboard method
     * Test that the method returns the dashboard page when the user is a student with requests
     */
    @Test
    void dashboard_StudentWithRequests() {
        Student student = new Student();
        student.setEmail("student@example.com");

        when(principal.getName()).thenReturn("student@example.com");
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(requestService.getCourseRegistrationRequests("student@example.com")).thenReturn(List.of(new CourseRegistrationRequest()));
        when(requestService.getStudentHousingRequests("student@example.com")).thenReturn(List.of(new StudentHousingRequest()));
        when(requestService.getLeaveOfAbsenceRequests("student@example.com")).thenReturn(List.of(new LeaveOfAbsenceRequest()));

        String result = dashboardController.dashboard(principal, model);

        assertEquals("dashboard", result);
        verify(model).addAttribute("user", student);
        verify(model).addAttribute(eq("courseRequests"), anyList());
        verify(model).addAttribute(eq("housingRequests"), anyList());
        verify(model).addAttribute(eq("leaveRequests"), anyList());
        verify(model).addAttribute("activePage", "dashboard");
    }
}
