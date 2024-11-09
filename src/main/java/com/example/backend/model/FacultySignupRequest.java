package com.example.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultySignupRequest extends SignupRequest {

    private Department department;

}
