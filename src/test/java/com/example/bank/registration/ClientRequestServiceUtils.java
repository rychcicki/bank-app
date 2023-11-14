package com.example.bank.registration;

import com.example.bank.registration.jpa.Address;

import java.time.LocalDate;

public class UserRequestServiceUtils {
    static ClientRequest emptyUserRequestBuilder() {
        return ClientRequest.builder().build();
    }

    static ClientRequest userRequestBelow18RequestBuilder() {
        return ClientRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(2015, 4, 28))
                .address(new Address("Tatrzanska", "7B", "12-456", "Zywiec"))
                .build();
    }

    static ClientRequest updateUserRequestAdultBuilder() {
        return ClientRequest.builder()
                .firstName("Czeslawa")
                .lastName("Cieslak")
                .email("czeslawa.cieslak@gmail.com")
                .birthDate(LocalDate.of(1938, 6, 10))
                .address(new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"))
                .build();
    }

    static ClientRequest updateUserRequest18YOBuilder() {
        return ClientRequest.builder()
                .firstName("Michal")
                .lastName("Listkiewicz")
                .email("michal.listkiewicz@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Ku Ujsciu", "1", "67-890", "Gdynia"))
                .build();
    }

    static ClientRequest userRequest18YOForRegisterBuilder() {
        return ClientRequest.builder()
                .firstName("Adam")
                .lastName("Mialczysnki")
                .email("adam.mialczynski@gmail.com")
                .birthDate(LocalDate.of(1956, 11, 11))
                .address(new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"))
                .build();
    }
}
