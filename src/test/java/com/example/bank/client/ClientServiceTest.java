package com.example.bank.client;

import com.example.bank.client.model.Client;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

    private final Status status = Status.ACTIVE;

    private final Long id = 123L;

    @BeforeEach
    public void beforeEach() {
        clientServiceImpl.setMajority(18);
    }

    @DisplayName("createClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldCreateClientAndReturnClientDto(Client client, ClientRequest clientRequest, ClientDTO clientDTO) {
        client.setId(id);

        when(clientMapper.clientRequestToClient(clientRequest)).thenReturn(client);
        when(passwordEncoder.encode(clientRequest.password())).thenReturn("encoded_password");
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.clientToDto(client)).thenReturn(clientDTO);

        ClientDTO resultClient = clientServiceImpl.createClient(clientRequest);
        Assertions.assertEquals(resultClient, clientDTO);
    }

    @DisplayName("updateClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldUpdatedClientAndReturnClientDto(Client client, ClientRequest clientRequest, ClientDTO updatedClient) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(client, clientRequest);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.clientToDto(client)).thenReturn(updatedClient);

        ClientDTO resultClient = clientServiceImpl.updateClient(client.getId(), clientRequest);
        Assertions.assertEquals(updatedClient, resultClient);
    }

    @DisplayName("updateClientConstraintViolationExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldThrowInvalidRequestExceptionWhenInvalidClientRequest(Client client, ClientRequest clientRequest) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(client, clientRequest);
        when(clientRepository.save(client)).thenThrow(ConstraintViolationException.class);

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(client.getId(), clientRequest));
        Assertions.assertEquals(ExceptionType.INVALID_REQUEST_EXCEPTION, ex.getExceptionType());
    }

    @DisplayName("updateClientDataIntegrityViolationExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldThrowClientAlreadyExceptionWhenDataExistInDatabase(Client client, ClientRequest clientRequest) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(client, clientRequest);
        when(clientRepository.save(client)).thenThrow(DataIntegrityViolationException.class);

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(client.getId(), clientRequest));
        Assertions.assertEquals(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION, ex.getExceptionType());
    }

    @DisplayName("updateClientClientNotFoundExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldThrowClientNotFoundExceptionWhenClientNotFound(Client client, ClientRequest clientRequest) {
        client.setId(id);
        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.empty());
        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(client.getId(), clientRequest));
        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, ex.getExceptionType());
    }

    @DisplayName("updateClientInvalidMajorityExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsForClientBelow18YearsOld")
    void shouldThrowInvalidMajorityExceptionWhenClientIsBelow18YearsOld(Client client, ClientRequest clientRequest) {
        client.setId(id);
        RestException ex = Assertions.assertThrows(RestException.class, () ->
                clientServiceImpl.updateClient(client.getId(), clientRequest));
        Assertions.assertEquals(ExceptionType.INVALID_MAJORITY_EXCEPTION, ex.getExceptionType());
    }

    @DisplayName("deleteClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldSoftDeleteClientById(Client client) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        clientServiceImpl.deleteClient(client.getId());
        Assertions.assertEquals(client.getStatus(), Status.INACTIVE);
    }

    @DisplayName("findClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldFindAndReturnClient(Client client) {
        client.setId(id);
        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        Client resultClient = clientServiceImpl.findClient(client.getId());
        Assertions.assertEquals(client, resultClient);
    }

    @DisplayName("findClientAsDtoByIdTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsForClientToClientDto")
    void shouldFindClientAndReturnClientDto(Client client, ClientDTO clientDTO) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.of(client));
        when(clientMapper.clientToDto(client)).thenReturn(clientDTO);

        ClientDTO resultClientDto = clientServiceImpl.findClientAsDtoById(client.getId());
        Assertions.assertEquals(clientDTO, resultClientDto);
    }

    @DisplayName("findClient_findClientAsDtoById_deleteClient_ExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsFor18YearsOldClient")
    void shouldThrowClientNotFoundExceptionWhenNoClientWithStatusAndIdInDatabase(Client client) {
        client.setId(id);

        when(clientRepository.findByStatusAndId(status, client.getId())).thenReturn(Optional.empty());

        RestException exception1 = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.findClient(client.getId()));
        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception1.getExceptionType());

        RestException exception2 = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.findClientAsDtoById(client.getId()));
        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception2.getExceptionType());

        RestException exception3 = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.deleteClient(client.getId()));
        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception3.getExceptionType());
    }

    @DisplayName("findClientsTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#argumentsForFindAllClients")
    void shouldReturnSetOfClientsDto(List<Client> clients, Set<ClientDTO> clientDtos) {
        List<ClientDTO> clientDTOList = new ArrayList<>(clientDtos);

        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i) /*clientIterator.next()*/;
            ClientDTO clientDTO = clientDTOList.get(i)/*clientDTOIterator.next()*/;
            when(clientMapper.clientToDto(client)).thenReturn(clientDTO);
        }

        when(clientRepository.findAllByStatus(status)).thenReturn(clients);

        Set<ClientDTO> resultListOfClientDtos = clientServiceImpl.findClients();
        Assertions.assertEquals(resultListOfClientDtos, clientDtos);
    }
}
