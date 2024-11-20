package com.example.bank.client.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NotBlank
    private String streetName;

    @NotBlank
    private String streetNumber;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String city;
}
