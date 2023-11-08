package com.example.bank.userRegister;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data()
@RequiredArgsConstructor
/** Dlaczego musi być dodatkowo @RequiredArgsConstructor, skoro jest w @Data? */
@EqualsAndHashCode
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
