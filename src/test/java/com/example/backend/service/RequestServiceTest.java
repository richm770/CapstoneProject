package com.example.backend.service;

import com.example.backend.model.CourseRegistrationRequest;
import com.example.backend.model.LeaveOfAbsenceRequest;
import com.example.backend.model.StudentHousingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/***
 * This class is used to test the RequestService class
 */
@SpringBootTest
class RequestServiceTest {

    @Autowired
    private RequestService requestService;

    /***
     * This method is used to test the validateLeaveRequestForm method
     * when the start date is after the end date
     */
    @Test
    void testValidateLeaveRequestForm_missingDates() {
        Model model = mock(Model.class);
        LeaveOfAbsenceRequest request = new LeaveOfAbsenceRequest();

        boolean hasErrors = requestService.validateLeaveRequestForm(model, request);

        assertTrue(hasErrors);
        verify(model).addAttribute("startDateError", "Start date is required");
        verify(model).addAttribute("endDateError", "End date is required");
    }

    /***
     * This method is used to test the validateLeaveRequestForm method
     * when the start date is after the end date
     */
    @Test
    void testValidateLeaveRequestForm_noErrors() {
        Model model = mock(Model.class);
        LeaveOfAbsenceRequest request = new LeaveOfAbsenceRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));

        boolean hasErrors = requestService.validateLeaveRequestForm(model, request);

        assertFalse(hasErrors);
        verify(model, never()).addAttribute(anyString(), anyString());
    }

    /***
     * This method is used to test the validateHousingRequestForm method
     * when the fields are blank
     */
    @Test
    void testValidateHousingRequestForm_blankFields() {
        Model model = mock(Model.class);
        StudentHousingRequest request = new StudentHousingRequest();
        request.setHousingType("");
        request.setDuration("");

        boolean hasErrors = requestService.validateHousingRequestForm(model, request);

        assertTrue(hasErrors);
        verify(model).addAttribute("housingTypeError", "Housing type is required");
        verify(model).addAttribute("durationError", "Duration is required");
    }

    /***
     * This method is used to test the validateHousingRequestForm method
     * when there are no errors
     */
    @Test
    void testValidateHousingRequestForm_noErrors() {
        Model model = mock(Model.class);
        StudentHousingRequest request = new StudentHousingRequest();
        request.setHousingType("Single Room");
        request.setDuration("1 Semester");

        boolean hasErrors = requestService.validateHousingRequestForm(model, request);

        assertFalse(hasErrors);
        verify(model, never()).addAttribute(anyString(), anyString());
    }

    /***
     * This method is used to test the validateCourseRegistrationRequestForm method
     * when the fields are missing
     */
    @Test
    void testValidateCourseRegistrationRequestForm_missingFields() {
        Model model = mock(Model.class);
        CourseRegistrationRequest request = new CourseRegistrationRequest();
        request.setSemester("");

        boolean hasErrors = requestService.validateCourseRegistrationRequestForm(model, request);

        assertTrue(hasErrors);
        verify(model).addAttribute("courseIdError", "Course ID is required");
        verify(model).addAttribute("semesterError", "Semester is required");
    }

    /***
     * This method is used to test the validateCourseRegistrationRequestForm method
     * when there are no errors
     */
    @Test
    void testValidateCourseRegistrationRequestForm_noErrors() {
        Model model = mock(Model.class);
        CourseRegistrationRequest request = new CourseRegistrationRequest();
        request.setCourseId(19002L);
        request.setSemester("Fall");

        boolean hasErrors = requestService.validateCourseRegistrationRequestForm(model, request);

        assertFalse(hasErrors);
        verify(model, never()).addAttribute(anyString(), anyString());
    }
}
