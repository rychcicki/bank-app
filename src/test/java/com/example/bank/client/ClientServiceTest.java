package com.example.bank.client;

import com.example.bank.client.jpa.Client;
import com.example.bank.client.jpa.ClientRepository;
import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.security.auth.AuthenticationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private ClientService clientService;
//    private AuthenticationService authenticationService;
//    private JwtService jwtService;

    @BeforeEach
    public void beforeEach() {
        clientService.setMajority(18);
    }

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientOver18YearsOldSource")
    void shouldReturnClientWhenClientIs18YearsOldOrMore(Client client, ClientRequest clientRequest) {
        AuthenticationResponse authenticationResponseForTest = new AuthenticationResponse();
//        when(clientRepository.save(client)).thenReturn(client);
//        AuthenticationResponse authenticationResponse = authenticationService.registerClient(clientRequest);
//        Assertions.assertEquals(authenticationResponse, authenticationResponseForTest);
    }

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientRequestBelow18YearsOldSource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18YearsOld(ClientRequest clientRequest) {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            authenticationService.registerClient(clientRequest);
//        }, "Client has to be adult.");
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientOver18YearsOldSource")
    void shouldGetClientAndReturnClientWithId(Client client) {
        Integer id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Client resultClient = clientService.getClientById(id);
        Assertions.assertEquals(resultClient, client);
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientSource")
    void shouldReturnClientExceptionWhenThereIsNoClientWithId(Client client) {
        Integer id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClientById(id);
        });
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientOver18YearsOldUpdateSource")
    void shouldReturnUpdatedClient(Client client, ClientRequest clientRequest, Client updatedClient) {
        Integer id = 19;
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        Client resultClient = clientService.updateClientById(id, clientRequest);
        Assertions.assertEquals(updatedClient, resultClient);
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientRequestOver18YearsOldSource")
    void shouldThrowClientNotFoundExceptionDuringClientUpdateWhenThereIsNoClientWithId(ClientRequest clientRequest) {
        Integer id = 123456789;
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.updateClientById(id, clientRequest));
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientBelow18YearsOldSource")
    void shouldThrowIllegalArgumentExceptionDuringClientUpdateWhenTheClientIsBelow18YearsOld(
            Client client,
            ClientRequest clientRequest) {
        Integer id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                clientService.updateClientById(id, clientRequest));
    }

    @DisplayName("A parameterized test of deleteClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientSource")
    void shouldDeleteClientWhenGivenId(Client client) {
        Integer id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);
        clientService.deleteClient(id);
        verify(clientRepository, times(1)).delete(client);
    }

    @DisplayName("A parameterized test of deleteClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.client.SourceMethodsForTest#clientSource")
    void shouldThrowClientNotFoundExceptionWhenThereIsNoClientWithId(Client client) {
        Integer id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.deleteClient(id));
    }
}
