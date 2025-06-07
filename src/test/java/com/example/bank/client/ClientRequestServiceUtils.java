package com.example.bank.client;

import com.example.bank.client.model.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ClientRequestServiceUtils {
    static ClientRequest clientRequestBuilder() {
        return new ClientRequest("Czeslawa", "Cieslak",
                LocalDate.of(1938, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"),
                "password");
    }

    static ClientRequest clientRequestExact18YearsOldBuilder() {
        return new ClientRequest("Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"),
                "password");
    }

    static ClientRequest clientRequestBelow18YearsOldBuilder() {
        return new ClientRequest("Zdzislaw", "Krecina",
                LocalDate.now().minusYears(17), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"),
                "password");
    }

    static ClientRequest clientRequestInvalidBirthDateBuilder() {
        return new ClientRequest("Zdzislaw", "Krecina",
                LocalDate.now().plusYears(1), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"),
                "password");
    }

    static ClientUpdateRequest clientUpdateRequestBuilder() {
        return new ClientUpdateRequest("Czeslawa", "Cieslak",
                LocalDate.of(1938, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"));
    }

    static ClientUpdateRequest clientUpdateRequestExact18YearsOldBuilder() {
        return new ClientUpdateRequest("Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"));
    }

    static ClientUpdateRequest clientUpdateRequestBelow18YearsOldBuilder() {
        return new ClientUpdateRequest("Zdzislaw", "Krecina",
                LocalDate.now().minusYears(17), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
    }

    static ClientUpdateRequest clientUpdateRequestInvalidBirthDateBuilder() {
        return new ClientUpdateRequest("Zdzislaw", "Krecina",
                LocalDate.now().plusYears(1), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
    }
}
