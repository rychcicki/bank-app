package com.example.bank.registration;

import com.example.bank.registration.jpa.Address;
import com.example.bank.registration.jpa.Client;

import java.time.LocalDate;

class UserServiceUtils {
    static Client userBuilder() {
        return Client.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static Client userWithId3Builder() {
        return Client.builder()
                .id(3L)
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static Client userAdultWithId5Builder() {
        return Client.builder()
                .id(5L)
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }

    static Client user18YearsOldBuilder() {
        return Client.builder()
                .firstName("Adam")
                .lastName("Malysz")
                .email("adam.malusz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Polanska", "102", "33-450", "Ustron"))
                .build();
    }

    static Client userBelow18YearsOldBuilder() {
        return Client.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static Client resultUpdateUserWithId5Builder() {
        return Client.builder()
                .firstName("Czeslawa")
                .lastName("Cieslak")
                .email("czeslawa.cieslak@gmail.com")
                .birthDate(LocalDate.of(1938, 6, 10))
                .address(new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"))
                .build();
    }

    static Client resultUpdateUser18YearsOldBuilder() {
        return Client.builder()
                .firstName("Michal")
                .lastName("Listkiewicz")
                .email("michal.listkiewicz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Ku Ujsciu", "1", "67-890", "Gdynia"))
                .build();
    }

    static Client resultOfRegisterUserAdultWithIdBuilder() {
        return Client.builder()
                .id(5L)
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }
}
