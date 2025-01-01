package com.example.bank.integration;

import com.example.bank.client.ClientRequest;
import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;

import java.time.LocalDate;

public class ClientRequestAndClientIntegrationTestUtils {
    public static ClientRequest clientRequestIntegrationTestBuilder() {
        return new ClientRequest("Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"),
                "password");
    }

    public static Client clientIntegrationTestBuilder() {
        return new Client("Zdzislaw", "Krecina",
                LocalDate.of(1954, 4, 28), "zdzislaw.krecina@gmail.com", "password",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
    }
}
