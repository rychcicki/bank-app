package com.example.bank.registration;

import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
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

    @BeforeEach
    public void beforeEach() {
        clientService.setMajority(21);
    }

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientOver18YearsOldSource")
    void shouldReturnClientWhenClientIs18YearsOldOrMore(Client client, ClientRequest clientRequest) {
        when(clientRepository.save(client)).thenReturn(client);
        Client resultClient = clientService.registerClient(clientRequest);
        Assertions.assertEquals(resultClient, client);
    }

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientRequestBelow18YearsOldSource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18YearsOld(ClientRequest clientRequest) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            clientService.registerClient(clientRequest);
        }, "Client has to be adult.");
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientOver18YearsOldSource")
    void shouldGetClientAndReturnClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Client resultClient = clientService.getClientById(id);
        Assertions.assertEquals(resultClient, client);
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientSource")
    void shouldReturnClientExceptionWhenThereIsNoClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClientById(id);
        });
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientOver18YearsOldUpdateSource")
    void shouldReturnUpdatedClient(Client client, ClientRequest clientRequest, Client updatedClient) {
        Long id = clientRequest.id();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        Client resultClient = clientService.updateClient(clientRequest);
        Assertions.assertEquals(updatedClient, resultClient);
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientRequestOver18YearsOldSource")
    void shouldThrowClientNotFoundExceptionDuringClientUpdateWhenThereIsNoClientWithId(ClientRequest clientRequest) {
        Long id = clientRequest.id();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.updateClient(clientRequest));
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientBelow18YearsOldSource")
    void shouldThrowIllegalArgumentExceptionDuringClientUpdateWhenTheClientIsBelow18YearsOld(
            Client client,
            ClientRequest clientRequest) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                clientService.updateClient(clientRequest));
    }

    @DisplayName("A parameterized test of deleteClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientSource")
    void shouldDeleteClientWhenGivenId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);
        clientService.deleteClient(id);
        verify(clientRepository, times(1)).delete(client);
    }

    @DisplayName("A parameterized test of deleteClient() method")
    @ParameterizedTest
    @MethodSource("com.example.bank.registration.SourceMethodsForTest#clientSource")
    void shouldThrowClientNotFoundExceptionWhenThereIsNoClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.deleteClient(id));
    }
}
