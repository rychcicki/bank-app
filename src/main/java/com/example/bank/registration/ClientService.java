package com.example.bank.registration;

import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
import lombok.RequiredArgsConstructor;
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
    private final String clientLessThan18YearsOldMessage = "Client has to be adult.";
    private final String noClientInDatabaseMessage = "Error. There is no client with id #";
    @Value("${age.of.majority}")
    private int majority;

    Client registerClient(ClientRequest clientRequest) {
        log.info("Start client's registration.");
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        if (years >= majority) {
            Client client = new Client(clientRequest.id(), clientRequest.firstName(), clientRequest.lastName(),
                    clientRequest.birthDate(), clientRequest.email(), clientRequest.address());
            clientRepository.save(client);
            log.info("Client's registration passed.");
            return client;
        } else {
            log.error("Client's registration failed.");
            throw new IllegalArgumentException(clientLessThan18YearsOldMessage);
        }
    }

    Client getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseMessage + id);
                    return new ClientNotFoundException(noClientInDatabaseMessage + id);
                });
        log.info("GetClientById passed.");
        return client;
    }

    Client updateClient(ClientRequest clientRequest) {
        Long id = clientRequest.id();
        Client clientToUpdate = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseMessage + id);
                    return new ClientNotFoundException(noClientInDatabaseMessage + id);
                });
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        clientToUpdate.setFirstName(clientRequest.firstName());
        clientToUpdate.setLastName(clientRequest.lastName());
        if (years >= majority) {
            clientToUpdate.setBirthDate(clientRequest.birthDate());
        } else {
            throw new IllegalArgumentException(clientLessThan18YearsOldMessage);
        }
        clientToUpdate.setEmail(clientRequest.email());
        clientToUpdate.setAddress(clientRequest.address());
        return clientRepository.save(clientToUpdate);
    }

    void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseMessage + id);
                    return new ClientNotFoundException(noClientInDatabaseMessage + id);
                });
        clientRepository.delete(client);
        log.info("Client with id#" + id + " was deleted.");
    }
}
