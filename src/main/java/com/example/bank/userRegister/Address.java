package com.example.bank.userRegister;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@Builder
@AllArgsConstructor
@ToString
public class Address {
    private String streetName;
    private int zipCode;
    private String city;
}
