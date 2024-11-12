package com.example.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("leave_of_absence")
public class LeaveOfAbsenceRequest extends Request {

    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

}
