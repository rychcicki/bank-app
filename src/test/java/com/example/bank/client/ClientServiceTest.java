package com.example.bank.client;

import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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

    private final Long clientId = 123L;

    @BeforeEach
    public void beforeEach() {
        clientServiceImpl.setAgeOfMajority(18);
    }

    @DisplayName("createClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForFindAndCreate")
    void shouldCreateClientAndReturnClientDto(Client client, ClientRequest clientRequest, ClientDTO clientDTO) {
        when(clientMapper.clientRequestToClient(clientRequest)).thenReturn(client);
        when(passwordEncoder.encode(clientRequest.password())).thenReturn("encoded_password");
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.clientToDto(client)).thenReturn(clientDTO);

        ClientDTO resultClient = clientServiceImpl.createClient(clientRequest);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(resultClient).isEqualTo(clientDTO);
            softly.assertThat(resultClient.role()).isEqualTo(Role.USER);
            softly.assertThat(resultClient.status()).isEqualTo(Status.ACTIVE);
        });
        verify(passwordEncoder).encode(clientRequest.password());
        verify(clientRepository).save(any(Client.class));
    }

    @DisplayName("createClientInvalidMajorityExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientRequestWithInvalidBirthDate")
    void shouldThrowInvalidMajorityExceptionWhenBirthDateIsInvalid(ClientRequest clientRequest) {
        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.createClient(clientRequest));

        assertEquals(ExceptionType.INVALID_MAJORITY_EXCEPTION, ex.getExceptionType());
        verify(clientRepository, never()).save(any(Client.class));
        verify(clientMapper, never()).clientRequestToClient(clientRequest);
        verify(clientMapper, never()).clientToDto(any(Client.class));
    }

    @DisplayName("createClientDataIntegrityViolationExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForFindAndCreate")
    void shouldThrowInvalidRequestExceptionWhenSavingDuplicateClient(Client client, ClientRequest clientRequest,
                                                                     ClientDTO unusedDto) {
        when(clientMapper.clientRequestToClient(clientRequest)).thenReturn(client);
        doThrow(new DataIntegrityViolationException("data integrity violation ex")).when(clientRepository).save(client);

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.createClient(clientRequest));

        assertEquals(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION, ex.getExceptionType());
        verify(passwordEncoder).encode(clientRequest.password());
        verify(clientRepository).save(client);
        verify(clientMapper, never()).clientToDto(client);
    }

    @DisplayName("updateClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForUpdateAndDelete")
    void shouldUpdateClientAndReturnClientDto(Client client, ClientUpdateRequest updateRequest, ClientDTO updatedClient) {
        client.setId(clientId);

        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(client, updateRequest);
        doNothing().when(clientRepository).flush();
        when(clientMapper.clientToDto(client)).thenReturn(updatedClient);

        ClientDTO resultClient = clientServiceImpl.updateClient(clientId, updateRequest);

        assertEquals(updatedClient, resultClient);
        verify(clientMapper).updateClient(client, updateRequest);
        verify(clientRepository).flush();
    }

    @DisplayName("updateClientDataIntegrityViolationExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForUpdateAndDelete")
    void shouldThrowClientAlreadyExistsExceptionWhenFlushFails(Client client, ClientUpdateRequest updateRequest,
                                                               ClientDTO unusedDto) {
        client.setId(clientId);

        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(client, updateRequest);
        doThrow(new DataIntegrityViolationException("data integrity violation ex")).when(clientRepository).flush();

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(clientId, updateRequest));

        assertEquals(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION, ex.getExceptionType());
        verify(clientRepository).flush();
        verify(clientMapper, never()).clientToDto(client);
    }

    @DisplayName("updateClientClientNotFoundExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForUpdateAndDelete")
    void shouldThrowClientNotFoundExceptionWhenClientNotFound(Client unusedClient, ClientUpdateRequest updateRequest,
                                                              ClientDTO unusedDto) {
        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.empty());

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(clientId, updateRequest));

        assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, ex.getExceptionType());
        verify(clientMapper, never()).updateClient(any(Client.class), eq(updateRequest));
        verify(clientRepository, never()).flush();
        verify(clientMapper, never()).clientToDto(any(Client.class));
    }

    @DisplayName("updateClientInvalidMajorityExceptionTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientUpdateRequestWithInvalidBirthDate")
    void shouldThrowInvalidMajorityExceptionWhenBirthDateIsInvalid(ClientUpdateRequest updateRequest) {
        RestException ex = Assertions.assertThrows(RestException.class, () ->
                clientServiceImpl.updateClient(clientId, updateRequest));

        assertEquals(ExceptionType.INVALID_MAJORITY_EXCEPTION, ex.getExceptionType());
        verify(clientRepository, never()).findByStatusAndId(any(), any());
        verify(clientMapper, never()).updateClient(any(Client.class), eq(updateRequest));
        verify(clientRepository, never()).flush();
        verify(clientMapper, never()).clientToDto(any(Client.class));
    }

    @DisplayName("softDeleteClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForUpdateAndDelete")
    void shouldSoftDeleteClientById(Client client, ClientUpdateRequest unusedUpdateRequest, ClientDTO unusedDto) {
        client.setId(clientId);

        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.of(client));

        clientServiceImpl.softDeleteClient(clientId);

        assertEquals(client.getStatus(), Status.INACTIVE);
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
    }

    @DisplayName("findClientTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForFindAndCreate")
    void shouldFindAndReturnClient(Client client, ClientRequest unusedClientRequest, ClientDTO unusedDto) {
        client.setId(clientId);

        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.of(client));

        Client resultClient = clientServiceImpl.findClient(clientId);

        assertEquals(client, resultClient);
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
    }

    @DisplayName("findClientAsDtoTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForFindAndCreate")
    void shouldFindClientAndReturnClientDto(Client client, ClientRequest unusedClientRequest, ClientDTO clientDTO) {
        client.setId(clientId);

        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.of(client));
        when(clientMapper.clientToDto(client)).thenReturn(clientDTO);

        ClientDTO resultClientDto = clientServiceImpl.findClientAsDto(clientId);

        assertEquals(clientDTO, resultClientDto);
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
        verify(clientMapper).clientToDto(client);
    }

    @DisplayName("findClientExceptionTest")
    @Test
    void shouldThrowExceptionWhenClientNotFoundInFindClient() {
        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.empty());

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.findClient(clientId));

        assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception.getExceptionType());
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
    }

    @DisplayName("findClientAsDtoByIdExceptionTest")
    @Test
    void shouldThrowExceptionWhenClientNotFoundInFindClientAsDto() {
        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.empty());

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.findClientAsDto(clientId));

        assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception.getExceptionType());
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
        verify(clientMapper, never()).clientToDto(any(Client.class));
    }

    @DisplayName("softDeleteClientExceptionTest")
    @Test
    void shouldThrowExceptionWhenClientNotFoundInSoftDeleteClient() {
        when(clientRepository.findByStatusAndId(Status.ACTIVE, clientId)).thenReturn(Optional.empty());

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.softDeleteClient(clientId));

        assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION, exception.getExceptionType());
        verify(clientRepository).findByStatusAndId(Status.ACTIVE, clientId);
    }

    @DisplayName("findClientsTest")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#provideClientsForFindAll")
    void shouldReturnSetOfClientsDto(List<Client> clients, Set<ClientDTO> clientDtos) {
        List<ClientDTO> clientDTOList = new ArrayList<>(clientDtos);
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            client.setId(clientId + i);
            ClientDTO clientDTO = clientDTOList.get(i);
            when(clientMapper.clientToDto(client)).thenReturn(clientDTO);
        }

        when(clientRepository.findAllByStatus(Status.ACTIVE)).thenReturn(clients);

        Set<ClientDTO> resultListOfClientDtos = clientServiceImpl.findClients();

        assertEquals(resultListOfClientDtos, clientDtos);
        verify(clientRepository).findAllByStatus(Status.ACTIVE);
        verify(clientMapper, times(clients.size())).clientToDto(any(Client.class));
    }

    @DisplayName("findClientsEmptyListTest")
    @Test
    void shouldReturnEmptySetWhenNoActiveClients() {
        when(clientRepository.findAllByStatus(Status.ACTIVE)).thenReturn(Collections.emptyList());

        Set<ClientDTO> resultListOfClientDtos = clientServiceImpl.findClients();

        assertTrue(resultListOfClientDtos.isEmpty());
        verify(clientRepository).findAllByStatus(Status.ACTIVE);
        verify(clientMapper, never()).clientToDto(any(Client.class));
    }
}
