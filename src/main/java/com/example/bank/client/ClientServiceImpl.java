package com.example.bank.client;

import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PROTECTED) // only for testing
    private Integer majority;
    private final Status status = Status.ACTIVE;

    public ClientDTO createClient(ClientRequest clientRequest) {
        validateBirthDate(clientRequest);
        Client client = clientMapper.clientRequestToClient(clientRequest);
        client.setRole(Role.USER);
        client.setStatus(Status.ACTIVE);
        client.setPassword(passwordEncoder.encode((clientRequest.password())));
        return saveClientAndMapToDto(client);
    }

    public ClientDTO findClientAsDtoById(Long id) {
        Client client = clientRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));
        return clientMapper.clientToDto(client);
    }

    public Client findClient(Long id) {
        return clientRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));
    }

    public List<ClientDTO> findClients() {
        return clientRepository.findAllByStatus(status).stream()
                .map(clientMapper::clientToDto)
                .toList();
    }

    public ClientDTO updateClient(Long id, ClientRequest clientRequest) {
        validateBirthDate(clientRequest);
        Client client = clientRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));
        clientMapper.updateClient(client, clientRequest);
        return saveClientAndMapToDto(client);
    }

    public void deleteClient(Long id) {
        Client client = clientRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));
        client.setStatus(Status.INACTIVE);
        clientRepository.save(client);
        log.info("Client has been successfully deleted.");
    }

    private void validateBirthDate(ClientRequest clientRequest) {
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        if (years < getMajority()) {
            throw new RestException(ExceptionType.INVALID_MAJORITY_EXCEPTION);
        }
    }

    private ClientDTO saveClientAndMapToDto(Client client) {
        try {
            clientRepository.save(client);
        } catch (ConstraintViolationException ex) {
            throw new RestException(ExceptionType.INVALID_REQUEST_EXCEPTION);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION);
        }
        return clientMapper.clientToDto(client);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
