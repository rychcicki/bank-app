package com.example.bank.client.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public record Address(
        @NotBlank(message = "street name is mandatory")
        String streetName,

        @NotBlank(message = "street number is mandatory")
        String streetNumber,

        @NotBlank(message = "zip code must be between 4 and 10 characters")
        @Size(min = 3, max = 10, message = "zip code must be between 4 and 10 characters")
        String zipCode,

        @NotBlank(message = "city is mandatory")
        String city) {
}
