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

    static User userAdultBuilderWithId5() {
        return User.builder()
                .id(5L)
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static User user18YearsOldBuilder() {
        return User.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static User userNotAdultBuilder() {
        return User.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(2020, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static User emptyUserBuilder() {
        return User.builder().build();
    }
}
