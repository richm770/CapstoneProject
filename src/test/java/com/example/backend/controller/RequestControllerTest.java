package com.example.backend.controller;

import com.example.backend.model.Comment;
import com.example.backend.model.Faculty;
import com.example.backend.model.Request;
import com.example.backend.model.Student;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.RequestRepository;
import com.example.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RequestController requestController;

    private Principal principal;
    private Request request;
    private Student student;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        request = new Request();
        student = new Student();
        faculty = new Faculty();
        request.setId(1);
    }

    @Test
    void testCreateComment_success() {
        // Given
        Integer requestId = 1;
        Comment comment = new Comment();

        when(requestRepository.getRequestById(requestId)).thenReturn(request);
        when(authService.getUserByPrincipal(principal)).thenReturn(student);

        String result = requestController.createComment(requestId, principal, comment);

        assertEquals("redirect:/request/1", result);
        verify(requestRepository).getRequestById(requestId);
        verify(authService).getUserByPrincipal(principal);
        verify(commentRepository).save(comment);
        assertEquals(request, comment.getRequest());
        assertEquals(student, comment.getUser());
    }

    @Test
    void testCreateComment_requestNotFound() {
        Integer requestId = 1;
        Comment comment = new Comment();

        when(requestRepository.getRequestById(requestId)).thenReturn(null);

        String result = requestController.createComment(requestId, principal, comment);

        assertEquals("redirect:/request/" + requestId, result);
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void testWithdrawRequest_success() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(student);
        when(requestRepository.getRequestById(requestId)).thenReturn(request);

        String result = requestController.withdrawRequest(requestId, principal);

        assertEquals("redirect:/request/1", result);
        verify(requestRepository).getRequestById(requestId);
        verify(requestRepository).save(request);
        assertEquals("withdrawn", request.getStatus());
    }

    @Test
    void testWithdrawRequest_requestNotFound() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(student);
        when(requestRepository.getRequestById(requestId)).thenReturn(null);

        String result = requestController.withdrawRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId, result);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void testFacultyCannotWithdrawRequest() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(faculty);

        String result = requestController.withdrawRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId + "?error=unauthorized", result);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void testApproveRequest_success() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(faculty);
        when(requestRepository.getRequestById(requestId)).thenReturn(request);

        String result = requestController.approveRequest(requestId, principal);

        assertEquals("redirect:/request/1", result);
        verify(requestRepository).getRequestById(requestId);
        verify(requestRepository).save(request);
        assertEquals("approved", request.getStatus());
    }

    @Test
    void testApproveRequest_requestNotFound() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(faculty);
        when(requestRepository.getRequestById(requestId)).thenReturn(null);

        String result = requestController.approveRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId, result);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void testStudentCannotApproveRequest() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(student);

        String result = requestController.approveRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId + "?error=unauthorized", result);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void testRejectRequest_success() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(faculty);
        when(requestRepository.getRequestById(requestId)).thenReturn(request);

        String result = requestController.rejectRequest(requestId, principal);

        assertEquals("redirect:/request/1", result);
        verify(requestRepository).getRequestById(requestId);
        verify(requestRepository).save(request);
        assertEquals("rejected", request.getStatus());
    }

    @Test
    void testRejectRequest_requestNotFound() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(faculty);
        when(requestRepository.getRequestById(requestId)).thenReturn(null);

        String result = requestController.rejectRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId, result);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void testStudentCannotRejectRequest() {
        Integer requestId = 1;

        when(authService.getUserByPrincipal(principal)).thenReturn(student);

        String result = requestController.rejectRequest(requestId, principal);

        assertEquals("redirect:/request/" + requestId + "?error=unauthorized", result);
        verify(requestRepository, never()).save(any(Request.class));
    }

}
