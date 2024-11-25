package com.example.bank.client;

import com.example.bank.client.model.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientRequestServiceUtils {
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

    public static ClientRequest clientRequestBelow18YearsOldBuilder() {
        return new ClientRequest("Zdzislaw", "Krecina",
                LocalDate.now().minusYears(17), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"), "password");
    }
}
