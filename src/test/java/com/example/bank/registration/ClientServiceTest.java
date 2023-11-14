package com.example.bank.registration;

import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.example.bank.registration.ClientRequestServiceUtils.*;
import static com.example.bank.registration.ClientServiceUtils.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private ClientService clientService;

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("clientOver18YearsOldSource")
    void shouldReturnClientWhenClientIs18YearsOldOrMore(Client client, ClientRequest clientRequest) {
        when(clientRepository.save(client)).thenReturn(client);
        Client resultClient = clientService.registerClient(clientRequest);
        Assertions.assertEquals(resultClient, client);
    }

    @DisplayName("A parameterized test of registerClient() method")
    @ParameterizedTest
    @MethodSource("clientRequestBelow18YearsOldSource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18YearsOld(ClientRequest clientRequest) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            clientService.registerClient(clientRequest);
        }, "Client has to be adult.");
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("clientOver18YearsOldSource")
    void shouldGetClientAndReturnClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        Client resultClient = clientService.getClient(id);
        Assertions.assertEquals(resultClient, client);
    }

    @DisplayName("A parameterized test of getClient() method")
    @ParameterizedTest
    @MethodSource("clientSource")
    void shouldReturnClientExceptionWhenThereIsNoClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () -> {
            clientService.getClient(id);
        });
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("clientOver18YearsOldUpdateSource")
    void shouldReturnUpdatedClient(Client client, ClientRequest clientRequest, Client updatedClient) {
        Long id = clientRequest.id();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        Client resultClient = clientService.updateClient(clientRequest);
        /** 8) Michał: Jakos srednio mi sie podoba kwestia ze sa 2 parametry w tej metodzie. Bo to glupio wyglada ze masz
         * clientRequest i jeszcze dopychasz ID zamiast miec je od razu w obiekcie requesta
         *
         * Skorygowano w clientReqest, clientService i teście.*/
        Assertions.assertEquals(updatedClient, resultClient);
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("clientRequestOver18YearsOldSource")
    void shouldThrowClientNotFoundExceptionDuringClientUpdateWhenThereIsNoClientWithId(ClientRequest clientRequest) {
        Long id = clientRequest.id();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.updateClient(clientRequest));
    }

    @DisplayName("A parameterized test of updateClient() method")
    @ParameterizedTest
    @MethodSource("clientBelow18YearsOldSource")
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
    @MethodSource("clientSource")
    void shouldDeleteClientWhenGivenId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);
        clientService.deleteClient(id);
        verify(clientRepository, times(1)).delete(client);
        /** 9) Michał: co tu sie w ogole dzieje ? Wywolywanie metod w testach z kodu produkcyjnego ? Co to w ogole jest?
         *
         * Skorygowano do takiej postaci.
         *
         * 10) Dodałem do POMa <exclude>com.example.bank/exception/GlobalExceptionHandler.class</exclude>>
         * ale dalej Jacoco sprawdza klasę GlobalExceptionHandler, zamiast ją ominąć.*/
    }

    @DisplayName("A parameterized test of deleteClient() method")
    @ParameterizedTest
    @MethodSource("clientSource")
    void shouldThrowClientNotFoundExceptionWhenThereIsNoClientWithId(Client client) {
        Long id = client.getId();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(ClientNotFoundException.class, () ->
                clientService.deleteClient(id));
    }

    static Stream<Arguments> clientBelow18YearsOldSource() {
        return Stream.of(Arguments.of(clientBelow18YearsOldBuilder(), clientRequestBelow18RequestBuilder()));
    }

    static Stream<ClientRequest> clientRequestBelow18YearsOldSource() {
        return Stream.of(clientRequestBelow18RequestBuilder()
        );
    }

    static Stream<Client> clientSource() {
        return Stream.of(clientBuilder(), clientWithId3Builder(), clientAdultWithId5Builder(),
                client18YearsOldBuilder(), clientBelow18YearsOldBuilder());
    }

    static Stream<Arguments> clientOver18YearsOldUpdateSource() {
        return Stream.of(Arguments.of(clientAdultWithId5Builder(), updateClientRequestAdultBuilder(),
                        resultUpdateClientWithId5Builder()),
                Arguments.of(client18YearsOldBuilder(), updateClientRequest18YOBuilder(),
                        resultUpdateClient18YearsOldBuilder()));
    }

    static Stream<Arguments> clientOver18YearsOldSource() {
        return Stream.of(Arguments.of(clientAdultWithId5Builder(), clientRequest18YOForRegisterBuilder(),
                resultOfRegisterClientAdultWithIdBuilder()));
    }

    static Stream<Arguments> clientRequestOver18YearsOldSource() {
        return Stream.of(Arguments.of(clientRequest18YOForRegisterBuilder(),
                resultOfRegisterClientAdultWithIdBuilder()));
    }
}
