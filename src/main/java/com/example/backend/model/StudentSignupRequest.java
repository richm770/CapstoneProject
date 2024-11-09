package com.example.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSignupRequest extends SignupRequest{
    
    private String studentID;

}
