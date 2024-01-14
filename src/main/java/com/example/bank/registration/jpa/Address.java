package com.example.bank.registration.jpa;

import jakarta.persistence.Column;
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
    @Column
    private String streetName;
    @NotBlank
    @Column
    private String streetNumber;
    @NotBlank
    @Column
    private String zipCode;
    @NotBlank
    @Column
    private String city;
}
