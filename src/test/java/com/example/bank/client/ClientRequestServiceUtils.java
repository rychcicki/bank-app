package com.example.bank.client;

import com.example.bank.client.jpa.Address;

import java.time.LocalDate;

class ClientRequestServiceUtils {
    static ClientRequest clientRequestBelow18RequestBuilder() {
        return new ClientRequest("Zdzislaw", "Krecina",
                LocalDate.of(2015, 4, 28), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"), "password",
                Role.ADMIN);
    }

    static ClientRequest updateClientRequestAdultBuilder() {
        return new ClientRequest("Czeslawa", "Cieslak",
                LocalDate.of(1938, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"),
                "password", Role.ADMIN);
    }

    static ClientRequest updateClientRequest18YOBuilder() {
        return new ClientRequest("Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"),
                "password", Role.ADMIN);
    }

    static ClientRequest clientRequest18YOForRegisterBuilder() {
        return new ClientRequest("Adam", "Mialczynski",
                LocalDate.of(1956, 11, 11), "adam.mialczynski@gmail.com",
                new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"),
                "password", Role.ADMIN);
    }
}
