package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<LeaveOfAbsenceRequest> getLeaveOfAbsenceRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "leave_of_absence")
                .stream()
                .map(request -> (LeaveOfAbsenceRequest) request)
                .collect(Collectors.toList());
    }

    public List<CourseRegistrationRequest> getCourseRegistrationRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "course_registration")
                .stream()
                .map(request -> (CourseRegistrationRequest) request)
                .collect(Collectors.toList());
    }

    public List<StudentHousingRequest> getStudentHousingRequests(String userEmail) {
        return requestRepository.findAllByCreatedByEmailAndType(userEmail, "student_housing")
                .stream()
                .map(request -> (StudentHousingRequest) request)
                .collect(Collectors.toList());
    }

    public List<Request> getFacultyRequests(String facultyEmail, Department assignedDepartment) {
        return requestRepository.findAllByApprovedByEmailOrAssignedDepartmentAndStatusIsNot(facultyEmail, assignedDepartment, "withdrawn");
    }

    public boolean validateLeaveRequestForm(Model model, LeaveOfAbsenceRequest leaveOfAbsenceRequest) {
        boolean hasErrors = false;

        if (leaveOfAbsenceRequest.getStartDate() == null) {
            model.addAttribute("startDateError", "Start date is required");
            hasErrors = true;
        }

        if (leaveOfAbsenceRequest.getEndDate() == null) {
            model.addAttribute("endDateError", "End date is required");
            hasErrors = true;
        }

        return hasErrors;
    }

    public boolean validateHousingRequestForm(Model model, StudentHousingRequest studentHousingRequest) {
        boolean hasErrors = false;

        if (studentHousingRequest.getHousingType().isBlank()) {
            model.addAttribute("housingTypeError", "Housing type is required");
            hasErrors = true;
        }

        if (studentHousingRequest.getDuration().isBlank()) {
            model.addAttribute("durationError", "Duration is required");
            hasErrors = true;
        }

        return hasErrors;
    }

    public boolean validateCourseRegistrationRequestForm(Model model, CourseRegistrationRequest courseRegistrationRequest) {
        boolean hasErrors = false;

        if (courseRegistrationRequest.getCourseId() == null) {
            model.addAttribute("courseIdError", "Course ID is required");
            hasErrors = true;
        }

        if (courseRegistrationRequest.getSemester().isBlank()) {
            model.addAttribute("semesterError", "Semester is required");
            hasErrors = true;
        }

        return hasErrors;
    }
}
