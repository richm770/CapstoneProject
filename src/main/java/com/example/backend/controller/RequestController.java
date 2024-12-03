package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.RequestRepository;
import com.example.backend.service.AuthService;
import com.example.backend.service.EmailService;
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
    private final EmailService emailService;

    /***
     * Constructor for the request controller
     * @param authService The auth service
     * @param requestRepository The request repository
     * @param requestService The request service
     * @param commentRepository The comment repository
     */
    public RequestController(AuthService authService,
                             RequestRepository requestRepository,
                             RequestService requestService,
                             CommentRepository commentRepository, EmailService emailService) {
        this.authService = authService;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.commentRepository = commentRepository;
        this.emailService = emailService;
    }

    /***
     * Add the user to the model
     * @param principal The principal
     * @param model The model
     */
    private void addUserToModel(Principal principal, Model model) {
        User user = authService.getUserByPrincipal(principal);
        model.addAttribute("user", user);
    }

    /***
     * Expose the request page
     * @param requestId The request ID
     * @param principal The principal
     * @param model The model
     * @return The request page
     */
    @GetMapping("/{requestId}")
    public String requestPage(@PathVariable("requestId") Integer requestId, Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "dashboard");

        Request request = requestRepository.getRequestById(requestId);
        model.addAttribute("request", request);

        model.addAttribute("newComment", new Comment());

        return "request-page";
    }

    /***
     * Create a new comment
     * @param requestId The request ID
     * @param principal The principal
     * @param comment The comment
     * @return The redirect to the request page
     */
    @PostMapping("/comment")
    public String createComment(@RequestParam("requestId") Integer requestId,
                                Principal principal,
                                @ModelAttribute("newComment") Comment comment) {

        Request request = requestRepository.getRequestById(requestId);

        // If the request does not exist, redirect to the request page
        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        comment.setRequest(request);
        User user = authService.getUserByPrincipal(principal);
        comment.setUser(user);

        commentRepository.save(comment);

        return "redirect:/request/" + requestId;
    }

    /***
     * Withdraw a request
     * @param requestId The request ID
     * @param principal The principal
     * @return The redirect to the request page
     */
    @PostMapping("/withdraw")
    public String withdrawRequest(@RequestParam("requestId") Integer requestId,
                                  Principal principal) {

        User user = authService.getUserByPrincipal(principal);

        // If the user is a faculty member, redirect to the request page
        if (user instanceof Faculty) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        // If the request does not exist, redirect to the request page
        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("withdrawn");
        requestRepository.save(request);

        emailService.sendRequestStatusChangeToStudent(request);

        return "redirect:/request/" + requestId;
    }

    /***
     * Approve a request
     * @param requestId The request ID
     * @param principal The principal
     * @return The redirect to the request page
     */
    @PostMapping("/approve")
    public String approveRequest(@RequestParam("requestId") Integer requestId,
                                 Principal principal) {

        User user = authService.getUserByPrincipal(principal);

        // If the user is a student, redirect to the request page
        if (user instanceof Student) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        // If the request does not exist, redirect to the request page
        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("approved");
        requestRepository.save(request);

        emailService.sendRequestStatusChangeToStudent(request);

        return "redirect:/request/" + requestId;
    }

    /***
     * Reject a request
     * @param requestId The request ID
     * @param principal The principal
     * @return The redirect to the request page
     */
    @PostMapping("/reject")
    public String rejectRequest(@RequestParam("requestId") Integer requestId,
                                Principal principal) {

        User user = authService.getUserByPrincipal(principal);

        // If the user is a student, redirect to the request page
        if (user instanceof Student) {
            return "redirect:/request/" + requestId + "?error=unauthorized";
        }

        Request request = requestRepository.getRequestById(requestId);

        // If the request does not exist, redirect to the request page
        if (request == null) {
            return "redirect:/request/" + requestId;
        }

        request.setStatus("rejected");
        requestRepository.save(request);

        emailService.sendRequestStatusChangeToStudent(request);

        return "redirect:/request/" + requestId;
    }

    /***
     * Close a request
     * @param principal The principal
     * @param model The request ID
     * @return The redirect to the request page
     */
    @GetMapping("/leaveRequest")
    public String leaveRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "leaveRequest");

        model.addAttribute("createLeaveRequest", new LeaveOfAbsenceRequest());

        return "leave-request-form";
    }

    /***
     * Create a leave request
     * @param principal The principal
     * @param leaveOfAbsenceRequest The leave of absence request
     * @param model The model
     * @return The redirect to the dashboard
     */
    @PostMapping("/leaveRequest")
    public String createLeaveRequest(Principal principal,
                                     LeaveOfAbsenceRequest leaveOfAbsenceRequest,
                                     Model model) {

        boolean hasErrors = requestService.validateLeaveRequestForm(model, leaveOfAbsenceRequest);

        // If there are errors, redirect to the leave request form
        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "leaveRequest");
            model.addAttribute("createLeaveRequest", leaveOfAbsenceRequest);

            return "leave-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        // If the user is a faculty member, redirect to the dashboard
        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        leaveOfAbsenceRequest.setCreatedBy(user);
        leaveOfAbsenceRequest.setAssignedDepartment(Department.STUDENT_AFFAIRS);
        leaveOfAbsenceRequest.setStatus("open");
        leaveOfAbsenceRequest.setType("leave_of_absence");

        requestRepository.save(leaveOfAbsenceRequest);

        emailService.sendRequestCreationConfirmationEmail(leaveOfAbsenceRequest, user.getEmail());
        emailService.sendRequestCreationEmailToFaculty(leaveOfAbsenceRequest, user);

        return "redirect:/dashboard";
    }

    /***
     * Expose the housing request form
     * @param principal The principal
     * @param model The model
     * @return The housing request form
     */
    @GetMapping("/housingRequest")
    public String housingRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "housingRequest");

        model.addAttribute("createHousingRequest", new StudentHousingRequest());

        return "housing-request-form";
    }

    /***
     * Create a housing request
     * @param principal The principal
     * @param studentHousingRequest The student housing request
     * @param model The model
     * @return The redirect to the dashboard
     */
    @PostMapping("/housingRequest")
    public String createHousingRequest(Principal principal,
                                       StudentHousingRequest studentHousingRequest,
                                       Model model) {

        boolean hasErrors = requestService.validateHousingRequestForm(model, studentHousingRequest);

        // If there are errors, redirect to the housing request form
        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "housingRequest");
            model.addAttribute("createHousingRequest", studentHousingRequest);

            return "housing-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        // If the user is a faculty member, redirect to the dashboard
        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        studentHousingRequest.setCreatedBy(user);
        studentHousingRequest.setAssignedDepartment(Department.HOUSING_OFFICE);
        studentHousingRequest.setStatus("open");
        studentHousingRequest.setType("student_housing");

        requestRepository.save(studentHousingRequest);

        emailService.sendRequestCreationConfirmationEmail(studentHousingRequest, user.getEmail());
        emailService.sendRequestCreationEmailToFaculty(studentHousingRequest, user);

        return "redirect:/dashboard";
    }

    /***
     * Expose the course registration request form
     * @param principal The principal
     * @param model The model
     * @return The course registration request form
     */
    @GetMapping("/courseRegistrationRequest")
    public String courseRegistrationRequestForm(Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("activePage", "courseRegistrationRequest");

        model.addAttribute("createCourseRegistrationRequest", new CourseRegistrationRequest());

        return "course-registration-request-form";
    }

    /***
     * Create a course registration request
     * @param principal The principal
     * @param courseRegistrationRequest The course registration request
     * @param model The model
     * @return The redirect to the dashboard
     */
    @PostMapping("/courseRegistrationRequest")
    public String createCourseRegistrationRequest(Principal principal,
                                                  CourseRegistrationRequest courseRegistrationRequest,
                                                  Model model) {

        boolean hasErrors = requestService.validateCourseRegistrationRequestForm(model, courseRegistrationRequest);

        // If there are errors, redirect to the course registration request form
        if (hasErrors) {
            addUserToModel(principal, model);
            model.addAttribute("activePage", "courseRegistrationRequest");
            model.addAttribute("createCourseRegistrationRequest", courseRegistrationRequest);

            return "course-registration-request-form";
        }

        User user = authService.getUserByPrincipal(principal);

        // If the user is a faculty member, redirect to the dashboard
        if (user instanceof Faculty) {
            return "redirect:/dashboard?error";
        }

        courseRegistrationRequest.setCreatedBy(user);
        courseRegistrationRequest.setAssignedDepartment(Department.REGISTRARS_OFFICE);
        courseRegistrationRequest.setStatus("open");
        courseRegistrationRequest.setType("course_registration");

        requestRepository.save(courseRegistrationRequest);

        emailService.sendRequestCreationConfirmationEmail(courseRegistrationRequest, user.getEmail());
        emailService.sendRequestCreationEmailToFaculty(courseRegistrationRequest, user);

        return "redirect:/dashboard";
    }

}
