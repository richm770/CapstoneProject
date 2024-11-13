package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.RequestRepository;
import com.example.backend.service.AuthService;
import com.example.backend.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/request")
public class RequestController {

    private final AuthService authService;
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final CommentRepository commentRepository;

    public RequestController(AuthService authService, RequestRepository requestRepository, RequestService requestService, CommentRepository commentRepository) {
        this.authService = authService;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.commentRepository = commentRepository;
    }

    private void addUserToModel(Principal principal, Model model) {
        User user = authService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
    }

    @GetMapping("/{requestId}")
    public String requestPage(@PathVariable("requestId") Integer requestId, Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "dashboard");

        Request request = requestRepository.getRequestById(requestId);
        model.addAttribute("request", request);

        model.addAttribute("newComment", new Comment());

        return "request-page";
    }

    @PostMapping("/comment")
    public String createComment(@RequestParam("requestId") Integer requestId,
                                Principal principal,
                                @ModelAttribute("newComment") Comment comment) {

        Request request = requestRepository.getRequestById(requestId);

        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        comment.setRequest(request);
        User user = authService.getUserByPrincipal(principal);
        comment.setUser(user);

        commentRepository.save(comment);

        return "redirect:/request/" + requestId;
    }

    @PostMapping("/withdraw")
    public String withdrawRequest(@RequestParam("requestId") Integer requestId,
                                  Principal principal) {

        User user = authService.getUserByPrincipal(principal);
        if (user instanceof Faculty) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("withdrawn");
        requestRepository.save(request);

        return "redirect:/request/" + requestId;
    }

    @PostMapping("/approve")
    public String approveRequest(@RequestParam("requestId") Integer requestId,
                                 Principal principal) {

        User user = authService.getUserByPrincipal(principal);
        if (user instanceof Student) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("approved");
        requestRepository.save(request);

        return "redirect:/request/" + requestId;
    }

    @PostMapping("/reject")
    public String rejectRequest(@RequestParam("requestId") Integer requestId,
                                Principal principal) {

        User user = authService.getUserByPrincipal(principal);
        if (user instanceof Student) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        return "redirect:/request/" + requestId;
    }

    @GetMapping("/leaveRequest")
    public String leaveRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "leaveRequest");

        model.addAttribute("createLeaveRequest", new LeaveOfAbsenceRequest());

        return "leave-request-form";
    }

    @PostMapping("/leaveRequest")
    public String createLeaveRequest(Principal principal,
                                     LeaveOfAbsenceRequest leaveOfAbsenceRequest,
                                     Model model) {

        boolean hasErrors = requestService.validateLeaveRequestForm(model, leaveOfAbsenceRequest);

        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "leaveRequest");
            model.addAttribute("createLeaveRequest", leaveOfAbsenceRequest);

            return "leave-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        leaveOfAbsenceRequest.setCreatedBy(user);
        leaveOfAbsenceRequest.setAssignedDepartment(Department.STUDENT_AFFAIRS);
        leaveOfAbsenceRequest.setStatus("open");
        leaveOfAbsenceRequest.setType("leave_of_absence");

        requestRepository.save(leaveOfAbsenceRequest);

        return "redirect:/dashboard";
    }

    @GetMapping("/housingRequest")
    public String housingRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "housingRequest");

        model.addAttribute("createHousingRequest", new StudentHousingRequest());

        return "housing-request-form";
    }

    @PostMapping("/housingRequest")
    public String createHousingRequest(Principal principal,
                                       StudentHousingRequest studentHousingRequest,
                                       Model model) {

        boolean hasErrors = requestService.validateHousingRequestForm(model, studentHousingRequest);

        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "housingRequest");
            model.addAttribute("createHousingRequest", studentHousingRequest);

            return "housing-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        studentHousingRequest.setCreatedBy(user);
        studentHousingRequest.setAssignedDepartment(Department.HOUSING_OFFICE);
        studentHousingRequest.setStatus("open");
        studentHousingRequest.setType("student_housing");

        requestRepository.save(studentHousingRequest);

        return "redirect:/dashboard";
    }

    @GetMapping("/courseRegistrationRequest")
    public String courseRegistrationRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "courseRegistrationRequest");

        model.addAttribute("createCourseRegistrationRequest", new CourseRegistrationRequest());

        return "course-registration-request-form";
    }

    @PostMapping("/courseRegistrationRequest")
    public String createCourseRegistrationRequest(Principal principal,
                                                  CourseRegistrationRequest courseRegistrationRequest,
                                                  Model model) {

        boolean hasErrors = requestService.validateCourseRegistrationRequestForm(model, courseRegistrationRequest);

        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "courseRegistrationRequest");
            model.addAttribute("createCourseRegistrationRequest", courseRegistrationRequest);

            return "course-registration-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        courseRegistrationRequest.setCreatedBy(user);
        courseRegistrationRequest.setAssignedDepartment(Department.REGISTRARS_OFFICE);
        courseRegistrationRequest.setStatus("open");
        courseRegistrationRequest.setType("course_registration");

        requestRepository.save(courseRegistrationRequest);

        return "redirect:/dashboard";
    }

}
