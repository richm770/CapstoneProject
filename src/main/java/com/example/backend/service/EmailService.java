package com.example.backend.service;

import com.example.backend.model.Department;
import com.example.backend.model.Faculty;
import com.example.backend.model.Request;
import com.example.backend.model.User;
import com.example.backend.repository.FacultyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class EmailService {

    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;

    private final FacultyRepository facultyRepository;

    public EmailService(MailSender mailSender, SimpleMailMessage templateMessage, FacultyRepository facultyRepository) {
        this.mailSender = mailSender;
        this.templateMessage = templateMessage;
        this.facultyRepository = facultyRepository;
    }

    /**
     *
     * @param request
     * @param userEmail
     */
    public void sendRequestCreationConfirmationEmail(Request request, String userEmail) {
        SimpleMailMessage confirmationMsg = new SimpleMailMessage(this.templateMessage);
        confirmationMsg.setTo(userEmail);
        confirmationMsg.setText("Request number " + request.getId() + " has successfully been created");

        try {
            this.mailSender.send(confirmationMsg);
        } catch (MailException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param request
     * @param requestCreator
     */
    public void sendRequestCreationEmailToFaculty(Request request, User requestCreator) {

        Department assignedDepartment = request.getAssignedDepartment();

        Faculty faculty = facultyRepository.findFacultyByDepartment(assignedDepartment)
                .orElseThrow(() -> new NoSuchElementException("Faculty member with department " + assignedDepartment.name() + " not found."));

        SimpleMailMessage requestCreationEmail = new SimpleMailMessage(this.templateMessage);
        requestCreationEmail.setTo(faculty.getEmail());
        requestCreationEmail.setText(
                "Request number " + request.getId() + " ready for review."
                        + " Submitted by: " + requestCreator.getFirstName() + " " + requestCreator.getLastName()
        );

        try {
            this.mailSender.send(requestCreationEmail);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Send a request status change email to the request's creator
     * @param request The request that changed status
     */
    public void sendRequestStatusChangeToStudent(Request request) {
        SimpleMailMessage requestStatusChangeMessage = new SimpleMailMessage(this.templateMessage);
        requestStatusChangeMessage.setTo(request.getCreatedBy().getEmail());
        requestStatusChangeMessage.setText(
                "Request number " + request.getId() + " has been " + request.getStatus()
        );

        try {
            this.mailSender.send(requestStatusChangeMessage);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }

}
