package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final RequestService requestService;

    public DashboardController(UserRepository userRepository, RequestService requestService) {
        this.userRepository = userRepository;
        this.requestService = requestService;
    }

    @GetMapping
    public String dashboard(Principal principal, Model model) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
        model.addAttribute("user", user);

        if (user instanceof Faculty faculty) {
            Department assignedDepartment = faculty.getDepartment();

            List<CourseRegistrationRequest> courseRequests = new ArrayList<>();
            List<StudentHousingRequest> housingRequests = new ArrayList<>();
            List<LeaveOfAbsenceRequest> leaveRequests = new ArrayList<>();

            List<Request> requests = requestService.getFacultyRequests(userEmail, assignedDepartment);
            requests.forEach(request -> {
                if (request instanceof CourseRegistrationRequest) {
                    courseRequests.add((CourseRegistrationRequest) request);
                } else if (request instanceof StudentHousingRequest) {
                    housingRequests.add((StudentHousingRequest) request);
                } else if (request instanceof LeaveOfAbsenceRequest) {
                    leaveRequests.add((LeaveOfAbsenceRequest) request);
                }
            });

            model.addAttribute("courseRequests", courseRequests);
            model.addAttribute("housingRequests", housingRequests);
            model.addAttribute("leaveRequests", leaveRequests);

            log.info(leaveRequests.toString());
        } else {
            List<CourseRegistrationRequest> courseRequests = requestService.getCourseRegistrationRequests(userEmail);
            model.addAttribute("courseRequests", courseRequests);

            List<StudentHousingRequest> housingRequests = requestService.getStudentHousingRequests(userEmail);
            model.addAttribute("housingRequests", housingRequests);

            List<LeaveOfAbsenceRequest> leaveRequests = requestService.getLeaveOfAbsenceRequests(userEmail);
            model.addAttribute("leaveRequests", leaveRequests);

            log.info(leaveRequests.toString());
        }

        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }

}
