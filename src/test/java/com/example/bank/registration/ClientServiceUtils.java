package com.example.bank.registration;

import com.example.bank.account.Account;
import com.example.bank.registration.jpa.Address;
import com.example.bank.registration.jpa.Client;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

class ClientServiceUtils {
    static final List<Account> tempListOfAccounts = Collections.emptyList();

/*    static Client clientBuilder() {
        return new Client(null, "Zdzislaw", "Krecina",
                LocalDate.of(1954, 4, 28), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"),
                tempListOfAccounts);
    }

    static Client clientWithId3Builder() {
        return new Client(3L, "Zdzislaw", "Krecina",
                LocalDate.of(1954, 4, 28), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"),
                tempListOfAccounts);
    }

    static Client clientAdultWithId5Builder() {
        return new Client(5L, "Adam", "Mialczynski",
                LocalDate.of(1956, 11, 11), "adam.mialczynski@gmail.com",
                new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"),
                tempListOfAccounts);
    }

    static Client client18YearsOldBuilder() {
        return new Client(null, "Adam", "Malysz", LocalDate.now().minusYears(18),
                "adam.malusz@gmail.com",
                new Address("Polanska", "102", "33-450", "Ustron"),
                tempListOfAccounts);
    }

    static Client clientBelow18YearsOldBuilder() {
        return new Client(null, "Zdzislaw", "Krecina",
                LocalDate.of(2015, 4, 28), "zdzislaw.krecina@gmail.com",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"),
                tempListOfAccounts);
    }

    static Client resultUpdateClientWithId5Builder() {
        return new Client(5L, "Czeslawa", "Cieslak",
                LocalDate.of(1938, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343",
                        "Lewin Klodzki"), tempListOfAccounts);
    }

    static Client resultUpdateClient18YearsOldBuilder() {
        return new Client(null, "Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"), tempListOfAccounts);
    }

    static Client resultOfRegisterClientAdultWithIdBuilder() {
        return new Client(5L, "Adam", "Mialczynski",
                LocalDate.of(1956, 11, 11), "adam.mialczynski@gmail.com",
                new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"),
                tempListOfAccounts);
    }*/
}
