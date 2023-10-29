package com.example.bank.userRegister;

import java.time.LocalDate;

class UserServiceUtils {
    static User userBuilder() {
        return User.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static User userWithId3Builder() {
        return User.builder()
                .id(3L)
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static User userAdultWithId5Builder() {
        return User.builder()
                .id(5L)
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }

    static User user18YearsOldBuilder() {
        return User.builder()
                .firstName("Adam")
                .lastName("Malysz")
                .email("adam.malusz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Polanska", "102", "33-450", "Ustron"))
                .build();
    }

    static User userBelow18YearsOldBuilder() {
        return User.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static User resultUpdateUserWithId5Builder() {
        return User.builder()
                .firstName("Czeslawa")
                .lastName("Cieslak")
                .email("czeslawa.cieslak@gmail.com")
                .birthDate(LocalDate.of(1938, 6, 10))
                .address(new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"))
                .build();
    }

    static User resultUpdateUser18YearsOldBuilder() {
        return User.builder()
                .firstName("Michal")
                .lastName("Listkiewicz")
                .email("michal.listkiewicz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Ku Ujsciu", "1", "67-890", "Gdynia"))
                .build();
    }

    static User resultOfRegisterUserAdultWithIdBuilder() {
        return User.builder()
                .id(5L)
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }

    static User emptyUserBuilder() {
        return User.builder().build();
    }
}
