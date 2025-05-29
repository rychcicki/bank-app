package com.example.bank.client;

import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${age-of-majority:21}")
    @Getter
    @Setter(AccessLevel.PROTECTED) // only for testing
    private Integer ageOfMajority;

    @Transactional
    public ClientDTO createClient(ClientRequest clientRequest) {
        validateAge(clientRequest.birthDate());
        Client client = clientMapper.clientRequestToClient(clientRequest);
        client.setRole(Role.USER);
        client.setStatus(Status.ACTIVE);
        client.setPassword(passwordEncoder.encode(clientRequest.password()));
        try {
            clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            throw new RestException(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION, ex);
        }
        return clientMapper.clientToDto(client);
    }

    @Transactional(readOnly = true)
    public ClientDTO findClientAsDto(Long id) {
        Client client = findClient(id);
        return clientMapper.clientToDto(client);
    }

    @Transactional(readOnly = true)
    public Client findClient(Long id) {
        return clientRepository.findByStatusAndId(Status.ACTIVE, id)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));
    }

    @Transactional(readOnly = true)
    public Set<ClientDTO> findClients() {
        return clientRepository.findAllByStatus(Status.ACTIVE).stream()
                .map(clientMapper::clientToDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientUpdateRequest clientUpdateRequest) {
        validateAge(clientUpdateRequest.birthDate());
        Client client = findClient(id);
        clientMapper.updateClient(client, clientUpdateRequest);
        try {
            clientRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new RestException(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION, ex);
        }
        return clientMapper.clientToDto(client);
    }

    @Transactional
    public void softDeleteClient(Long id) {
        Client client = findClient(id);
        client.setStatus(Status.INACTIVE);
        log.info("Client with id {} has been deactivated (soft delete).", id);
    }

    private void validateAge(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new RestException(ExceptionType.INVALID_BIRTHDATE_EXCEPTION);
        }

        if (birthDate.plusYears(ageOfMajority).isAfter(LocalDate.now())) {
            throw new RestException(ExceptionType.INVALID_MAJORITY_EXCEPTION);
        }
    }
}
