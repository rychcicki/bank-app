package com.example.bank.registration.jpa;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data
@RequiredArgsConstructor
public class Address {
    @NotBlank
    private final String streetName;
    @NotBlank
    private final String streetNumber;
    @NotBlank
    private final String zipCode;
    @NotBlank
    private final String city;
}
