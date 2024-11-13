package com.example.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("course_registration")
public class CourseRegistrationRequest extends Request {

    private Long courseId;
    private String semester;
    private String reason;

}