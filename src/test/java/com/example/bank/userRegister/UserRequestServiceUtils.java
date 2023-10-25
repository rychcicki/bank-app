package com.example.bank.userRegister;

import java.time.LocalDate;

public class UserRequestServiceUtils {
    static UserRequest userRequestAdultBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(1954, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static UserRequest userRequest18YOBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.now().minusYears(18))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static UserRequest userRequestNotAdultBuilder() {
        return UserRequest.builder()
                .firstName("Zdzislaw")
                .lastName("Krecina")
                .email("zdzislaw.krecina@gmail.com")
                .birthDate(LocalDate.of(2015, 4, 28))
                .address(new Address("Tatrzanska", "12-456", "Zywiec"))
                .build();
    }

    static UserRequest emptyUserRequestBuilder() {
        return UserRequest.builder().build();
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
