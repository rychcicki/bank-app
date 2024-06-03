package com.example.bank.client;

import com.example.bank.client.jpa.Client;
import com.example.bank.client.jpa.ClientRepository;
import com.example.bank.exception.ClientNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    private final ClientRepository clientRepository;
    public static final String CLIENT_LESS_THAN_18_YEARS_OLD_MESSAGE = "Client has to be adult.";
    private final String noClientInDatabaseExceptionMessage = "Error. There is no client in database.";
    private final String noClientInDatabaseLogMessage = "There is no client with id={} in database.";
    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

    public Client getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseLogMessage, id);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        log.info("GetClientById passed.");
        return client;
    }

    Client updateClientById(Long id, ClientRequest clientRequest) {
        Client clientToUpdate = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseLogMessage, id);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        clientToUpdate.setFirstName(clientRequest.firstName());
        clientToUpdate.setLastName(clientRequest.lastName());
        if (years >= getMajority()) {
            clientToUpdate.setBirthDate(clientRequest.birthDate());
        } else {
            throw new IllegalArgumentException(CLIENT_LESS_THAN_18_YEARS_OLD_MESSAGE);
        }
        clientToUpdate.setEmail(clientRequest.email());
        clientToUpdate.setAddress(clientRequest.address());
        clientToUpdate.setRole(clientRequest.role());
        log.info("Client with id={} has been successfully updated.", id);
        return clientRepository.save(clientToUpdate);
    }

    void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseLogMessage, id);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        clientRepository.delete(client);
        log.info("Client with id={} has been successfully deleted.", id);
    }
}
