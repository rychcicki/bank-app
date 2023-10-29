package com.example.bank.userRegister;

import java.time.LocalDate;

public class UserRequestServiceUtils {
    static UserRequest emptyUserRequestBuilder() {
        return UserRequest.builder().build();
    }

    static UserRequest userRequestBelow18RequestBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(2015, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static UserRequest updateUserRequestAdultBuilder() {
        return UserRequest.builder()
                .firstName("Czeslawa")
                .lastName("Cieslak")
                .email("czeslawa.cieslak@gmail.com")
                .birthDate(LocalDate.of(1938, 6, 10))
                .address(new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"))
                .build();
    }

    static UserRequest updateUserRequest18YOBuilder() {
        return UserRequest.builder()
                .firstName("Michal")
                .lastName("Listkiewicz")
                .email("michal.listkiewicz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Ku Ujsciu", "1", "67-890", "Gdynia"))
                .build();
    }

    static UserRequest userRequest18YOForRegisterBuilder() {
        return UserRequest.builder()
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }
}
