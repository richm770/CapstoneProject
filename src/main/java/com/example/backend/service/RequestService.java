package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestService {

    private final RequestRepository requestRepository;

    /**
     * Constructor for RequestService
     *
     * @param requestRepository RequestRepository
     */
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Method to get all requests
     *
     * @return List of Request
     */
    public List<LeaveOfAbsenceRequest> getLeaveOfAbsenceRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "leave_of_absence")
                .stream()
                .map(request -> (LeaveOfAbsenceRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Method to get all course registration requests
     *
     * @return List of CourseRegistrationRequest
     */
    public List<CourseRegistrationRequest> getCourseRegistrationRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "course_registration")
                .stream()
                .map(request -> (CourseRegistrationRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Method to get all student housing requests
     *
     * @return List of StudentHousingRequest
     */
    public List<StudentHousingRequest> getStudentHousingRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "student_housing")
                .stream()
                .map(request -> (StudentHousingRequest) request)
                .collect(Collectors.toList());
    }

    /**
     * Method to get all faculty requests
     *
     * @return List of Request
     */
    public List<Request> getFacultyRequests(String facultyEmail, Department assignedDepartment) {
        return requestRepository.findAllByApprovedByEmailOrAssignedDepartmentAndStatusIsNot(facultyEmail, assignedDepartment, "withdrawn");
    }

    /**
     * Method to get all faculty requests
     *
     * @return List of Request
     */
    public boolean validateLeaveRequestForm(Model model, LeaveOfAbsenceRequest leaveOfAbsenceRequest) {
        boolean hasErrors = false;

        // Check if reason is provided
        if (leaveOfAbsenceRequest.getStartDate() == null) {
            model.addAttribute("startDateError", "Start date is required");
            hasErrors = true;
        }

        // Check if end date is provided
        if (leaveOfAbsenceRequest.getEndDate() == null) {
            model.addAttribute("endDateError", "End date is required");
            hasErrors = true;
        }

        return hasErrors;
    }

    /**
     * Method to validate housing request form
     *
     * @param model                 Model
     * @param studentHousingRequest StudentHousingRequest
     * @return boolean
     */
    public boolean validateHousingRequestForm(Model model, StudentHousingRequest studentHousingRequest) {
        boolean hasErrors = false;

        // Check if housing type is provided
        if (studentHousingRequest.getHousingType().isBlank()) {
            model.addAttribute("housingTypeError", "Housing type is required");
            hasErrors = true;
        }

        // Check if duration is provided
        if (studentHousingRequest.getDuration().isBlank()) {
            model.addAttribute("durationError", "Duration is required");
            hasErrors = true;
        }

        return hasErrors;
    }

    /**
     * Method to validate course registration request form
     *
     * @param model                     Model
     * @param courseRegistrationRequest CourseRegistrationRequest
     * @return boolean
     */
    public boolean validateCourseRegistrationRequestForm(Model model, CourseRegistrationRequest courseRegistrationRequest) {
        boolean hasErrors = false;

        // Check if course ID is provided
        if (courseRegistrationRequest.getCourseId() == null) {
            model.addAttribute("courseIdError", "Course ID is required");
            hasErrors = true;
        }

        // Check if semester is provided
        if (courseRegistrationRequest.getSemester().isBlank()) {
            model.addAttribute("semesterError", "Semester is required");
            hasErrors = true;
        }

        return hasErrors;
    }
}
