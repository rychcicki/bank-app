package com.example.bank.client;

import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ClientServiceUtils {
    static Client clientAdultBuilder() {
        return new Client("Zdzislaw", "Krecina", LocalDate.of(1954, 4, 28),
                "zdzislaw.krecina@gmail.com", "password",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
    }

    static Client clientExact18YearsOldBuilder() {
        return new Client("Adam", "Malysz", LocalDate.now().minusYears(18),
                "adam.malusz@gmail.com", "password",
                new Address("Polanska", "102", "33-450", "Ustron"));
    }

    static ClientDTO clientDtoWithId5Builder() {
        return new ClientDTO(5L, "Adam", "Mialczynski",
                LocalDate.of(1956, 11, 11), "adam.mialczynski@gmail.com",
                new Address("Mickiewicza", "13A/3", "00-914", "Warszawa"),
                Role.ADMIN, Status.ACTIVE, LocalDateTime.now().toString(), 1L);
    }

    static ClientDTO updatedClientDtoWithId5Builder() {
        return new ClientDTO(5L, "Czeslawa", "Cieslak",
                LocalDate.of(1938, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"),
                Role.USER, Status.ACTIVE, LocalDateTime.now().toString(), 1L);
    }

    static ClientDTO updatedClientExact18YearsOldBuilder() {
        return new ClientDTO(32L, "Michal", "Listkiewicz",
                LocalDate.now().minusYears(18), "michal.listkiewicz@gmail.com",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"), Role.USER,
                Status.ACTIVE, LocalDateTime.now().toString(), 2L);
    }

    static List<Client> listOfClients() {
        return List.of(clientAdultBuilder(), clientExact18YearsOldBuilder());
    }

    static Set<ClientDTO> setOfClientDTO() {
        return Set.of(clientDtoWithId5Builder(), updatedClientExact18YearsOldBuilder());
    }
}
