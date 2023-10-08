package com.example.bank.userRegister;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data()
@RequiredArgsConstructor    //dlaczego musi być dodatkowo @Required..., skoro jest w @Data ????
@EqualsAndHashCode
public class Address {
    private final String streetName;
    private final String zipCode;
    private final String city;
}
