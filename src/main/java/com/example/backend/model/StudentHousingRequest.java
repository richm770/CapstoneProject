package com.example.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("student_housing")
public class StudentHousingRequest extends Request {

    private String housingType;
    private String duration;
    private String reason;

}