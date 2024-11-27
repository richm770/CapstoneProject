package com.example.backend.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/***
 * This class tests the StudentController
 */
class StudentControllerTest {

    private final StudentController studentController = new StudentController();

    /***
     * This method tests the student method
     */
    @Test
    void student_ReturnsStudentPage() {
        String result = studentController.student();
        assertEquals("student", result);
    }
}