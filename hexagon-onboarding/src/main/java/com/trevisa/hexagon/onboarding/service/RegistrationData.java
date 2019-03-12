package com.trevisa.hexagon.onboarding.service;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationData {
    private String fullName;
    private String documentType;
    private String documentNumber;
    private LocalDate dateOfBirth;
    private String countryOfResidence;
}