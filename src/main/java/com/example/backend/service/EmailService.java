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
     * This method will send a confirmation email to the request creator when it is created
     *
     * @param request   The request that was created
     * @param userEmail The email of the user who created the request
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
     * This method sends an email to the faculty member who is part of the department of which the request is attached
     * to when the request is created
     *
     * @param request        The request that was created
     * @param requestCreator The user who created the request
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
     *
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

    /**
     * This method sends a reset password link to the user who requested it
     *
     * @param userEmail The email of the user who requested to change their password
     * @param resetLink The link that the user will click to reset their password
     */
    public void sendResetPasswordEmail(String userEmail, String resetLink) {
        SimpleMailMessage passwordResetMessage = new SimpleMailMessage();

        passwordResetMessage.setFrom("c11186907@gmail.com");
        passwordResetMessage.setSubject("Password Reset Request");
        passwordResetMessage.setTo(userEmail);
        passwordResetMessage.setText("Click the link to reset your password: " + resetLink);

        try {
            this.mailSender.send(passwordResetMessage);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * This method will email the user who requested to change their password after the password has been
     * successfully changed.
     *
     * @param userEmail The user who requested to change their password
     */
    public void sendPasswordChangeConfirmationEmail(String userEmail) {
        SimpleMailMessage passwordChangeConfirmationMessage = new SimpleMailMessage();

        passwordChangeConfirmationMessage.setFrom("c11186907@gmail.com");
        passwordChangeConfirmationMessage.setSubject("Password Changed Successfully");
        passwordChangeConfirmationMessage.setTo(userEmail);
        passwordChangeConfirmationMessage.setText("Your password was successfully changed. If you did not request this change, please contact administration.");

        try {
            this.mailSender.send(passwordChangeConfirmationMessage);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }
}
