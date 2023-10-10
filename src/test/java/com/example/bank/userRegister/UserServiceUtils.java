package com.example.bank.userRegister;

import java.time.LocalDate;

class UserServiceUtils {
    static User userBuilder() {
        return User.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static User userWithId3Builder() {
        return User.builder()
                .id(3L)
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static User userToUpdateBuilder() {
        return User.builder()
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "00-914", "Warszawa"))
                .build();
    }

    static UserRequest userRequestBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static UserRequest userBelow18RequestBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(2015, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }
}
