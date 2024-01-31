package com.example.bank.registration;

import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    private final ClientRepository clientRepository;
    /** Czy wielką literą ??*/
    public static final String clientLessThan18YearsOldMessage = "Client has to be adult.";
    private final String noClientInDatabaseExceptionMessage = "Error. There is no client in database.";
    private final String noClientInDatabaseLogMessage = "There is no client with id #";
    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

//    Client registerClient(ClientRequest clientRequest) {
//        log.info("Start client's registration.");
//        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
//                .getYears();
//        if (years >= getMajority()) {
//            Client client = new Client(clientRequest.id(), clientRequest.firstName(), clientRequest.lastName(),
//                    clientRequest.birthDate(), clientRequest.email(), clientRequest.address(), clientRequest.account());
//            clientRepository.save(client);
//            log.info("Client's registration passed.");
//            return client;
//        } else {
//            log.error("Client's registration failed.");
//            throw new IllegalArgumentException(clientLessThan18YearsOldMessage);
//        }
//    }

    Client getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(noClientInDatabaseLogMessage + id);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        log.info("GetClientById passed.");
        log.info(client.toString());
        return client;
    }

    Client updateClient(ClientRequest clientRequest) {
//        Long id = clientRequest.id();
        String email = clientRequest.email();
        Client clientToUpdate = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    //TODO logi stringFormat
                    log.info("App is running at {}", email);
//                    log.error(noClientInDatabaseLogMessage + email);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        clientToUpdate.setFirstName(clientRequest.firstName());
        clientToUpdate.setLastName(clientRequest.lastName());
        if (years >= getMajority()) {
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
                    log.error(noClientInDatabaseLogMessage + id);
                    return new ClientNotFoundException(noClientInDatabaseExceptionMessage);
                });
        clientRepository.delete(client);
        log.info("Client with id#" + id + " was deleted.");
    }
}
