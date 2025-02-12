package com.example.bank.client;

import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;
import com.example.bank.context.ClientOwnContext;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static com.example.bank.client.ClientRequestServiceUtils.clientRequestBuilder;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClientOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
@Transactional
class ClientServiceIT {
    @Autowired
    private ClientServiceImpl clientServiceImpl;

    @Autowired
    private ClientRepository clientRepository;

    private final ClientRequest clientRequest = clientRequestBuilder();

    @Test
    void shouldCreateClientAndReturnClientDto() {
        ClientDTO clientDTO = clientServiceImpl.createClient(clientRequest);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientDTO.id()).isNotNull();
            softly.assertThat(clientDTO.firstname()).isEqualTo(clientRequest.firstname());
            softly.assertThat(clientDTO.lastname()).isEqualTo(clientRequest.lastname());
            softly.assertThat(clientDTO.birthDate()).isEqualTo(clientRequest.birthDate());
            softly.assertThat(clientDTO.email()).isEqualTo(clientRequest.email());
            softly.assertThat(clientDTO.address()).isEqualTo(clientRequest.address());
            softly.assertThat(clientRepository.existsById(clientDTO.id())).isTrue();
        });
    }

    @Test
    void shouldFindClientAndReturnClientDto() {
        Long id = 1L;
        ClientDTO clientDTO = clientServiceImpl.findClientAsDtoById(id);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientDTO.id()).isNotNull();
            softly.assertThat(id).isEqualTo(clientDTO.id());
            softly.assertThat(clientRepository.existsById(clientDTO.id())).isTrue();
        });
    }

    @Test
    void shouldFindAndReturnClient() {
        Long id = 2L;
        Client client = clientServiceImpl.findClient(id);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(client.getId()).isEqualTo(id);
            softly.assertThat(client.getEmail()).isEqualTo("bad.randal@gmail.com");
            softly.assertThat(client.getRole()).isEqualTo(Role.USER);
            softly.assertThat(client.getStatus()).isEqualTo(Status.ACTIVE);
            softly.assertThat(clientRepository.existsById(client.getId())).isTrue();
        });
    }

    @Test
    void shouldThrowWhenClientNotFound() {
        Long id = 4L;

        RestException exception = Assertions.assertThrows(RestException.class, () -> clientServiceImpl.findClient(id));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    void shouldThrowWhenClientIsNotAdult() {
        ClientRequest request = new ClientRequest("Czeslawa", "Cieslak",
                LocalDate.of(2018, 6, 10), "czeslawa.cieslak@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"),
                "password");
        Long id = 22L;

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.createClient(request));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.INVALID_MAJORITY_EXCEPTION.getMessage());

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(id, request));
        Assertions.assertEquals(ex.getMessage(), ExceptionType.INVALID_MAJORITY_EXCEPTION.getMessage());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldThrowWhenClientAlreadyExists() {
        ClientRequest request = new ClientRequest("Czeslawa", "Cieslak",
                LocalDate.of(1988, 6, 10), "mike.wazowski@gmail.com",
                new Address("Obroncow Warszawy", "31", "57-343", "Lewin Klodzki"),
                "password");
        Long id = 2L;

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.createClient(request));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION.getMessage());

        RestException ex = Assertions.assertThrows(RestException.class,
                () -> clientServiceImpl.updateClient(id, request));
        Assertions.assertEquals(ex.getMessage(), ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION.getMessage());
    }

    @Test
    void shouldFindClientsAndReturnList() {
        Set<ClientDTO> clients = clientServiceImpl.findClients();

        Assertions.assertEquals(3, clients.size());
    }

    @Test
    void shouldUpdateClientInDatabase() {
        final Long id = 1L;
        ClientDTO client = clientServiceImpl.updateClient(id, clientRequest);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(client.id()).isNotNull();
            softly.assertThat(client.firstname()).isEqualTo(clientRequest.firstname());
            softly.assertThat(client.lastname()).isEqualTo(clientRequest.lastname());
            softly.assertThat(client.birthDate()).isEqualTo(clientRequest.birthDate());
            softly.assertThat(client.email()).isEqualTo(clientRequest.email());
            softly.assertThat(client.address()).isEqualTo(clientRequest.address());
            softly.assertThat(clientRepository.existsById(client.id())).isTrue();
        });
    }

    @Test
    void shouldSoftDeleteClientFromDatabase() {
        Long id = 3L;

        Assertions.assertTrue(clientRepository.existsById(id));
        clientServiceImpl.deleteClient(id);

        Status statusInactive = clientRepository.findById(id).get().getStatus();
        Assertions.assertEquals(statusInactive, Status.INACTIVE);
    }
}
