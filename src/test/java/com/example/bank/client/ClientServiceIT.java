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
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static com.example.bank.client.ClientRequestServiceUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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

    private final Long expectedNumberOfClients = 4L;
    private final ClientRequest clientRequest = clientRequestBuilder();
    private final ClientUpdateRequest clientUpdateRequest = clientUpdateRequestBuilder();

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
        ClientDTO clientDTO = clientServiceImpl.findClientAsDto(id);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientDTO.id()).isNotNull();
            softly.assertThat(clientDTO.id()).isEqualTo(id);
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
        Long idNotExist = expectedNumberOfClients + 1L;

        assertThrowsWithType(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION,
                () -> clientServiceImpl.findClient(idNotExist));
    }

    @Test
    void shouldThrowWhenClientIsNotAdult() {
        ClientRequest request = clientRequestBelow18YearsOldBuilder();
        ClientUpdateRequest clientUpdateRequest = clientUpdateRequestBelow18YearsOldBuilder();
        Long id = 22L;

        assertThrowsWithType(ExceptionType.INVALID_MAJORITY_EXCEPTION,
                () -> clientServiceImpl.createClient(request));

        assertThrowsWithType(ExceptionType.INVALID_MAJORITY_EXCEPTION,
                () -> clientServiceImpl.updateClient(id, clientUpdateRequest));
    }

    @Test
    void shouldThrowClientAlreadyExistsOnCreate() {
        //email "bad.randal@gmail.com" already exists in db
        ClientRequest request = new ClientRequest("Bad", "Randal",
                LocalDate.now().minusYears(18), "bad.randal@gmail.com",
                new Address("Street", "1", "11-231", "Tokyo"),
                "password");

        assertThrowsWithType(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION,
                () -> clientServiceImpl.createClient(request));
    }

    @Test
    void shouldThrowClientAlreadyExistsOnUpdate() {
        //email "bad.randal@gmail.com" already exists in db
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest("Bad", "Randal",
                LocalDate.now().minusYears(18), "bad.randal@gmail.com",
                new Address("Street", "1", "11-231", "Tokyo"));
        Long id = 3L;

        assertThrowsWithType(ExceptionType.CLIENT_ALREADY_EXISTS_EXCEPTION,
                () -> clientServiceImpl.updateClient(id, clientUpdateRequest));
    }

    @Test
    void shouldThrowClientNotFoundExceptionOnUpdate() {
        final Long id = 789L;

        assertThrowsWithType(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION,
                () -> clientServiceImpl.updateClient(id, clientUpdateRequest));
    }

    @Test
    void shouldReturnAllClientsAsDtoSet() {
        Set<ClientDTO> clients = clientServiceImpl.findClients();
        assertEquals(expectedNumberOfClients, clients.size());
    }

    @Test
    void shouldReturnEmptySetWhenAllClientsSoftDeleted() {
        Set.of(1L, 2L, 3L, 4L).forEach(clientServiceImpl::softDeleteClient);

        Set<ClientDTO> clients = clientServiceImpl.findClients();
        assertTrue(clients.isEmpty());
    }

    @Test
    void shouldUpdateClientInDatabase() {
        final Long id = 1L;

        ClientDTO client = clientServiceImpl.updateClient(id, clientUpdateRequest);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(client.id()).isNotNull();
            softly.assertThat(client.firstname()).isEqualTo(clientUpdateRequest.firstname());
            softly.assertThat(client.lastname()).isEqualTo(clientUpdateRequest.lastname());
            softly.assertThat(client.birthDate()).isEqualTo(clientUpdateRequest.birthDate());
            softly.assertThat(client.email()).isEqualTo(clientUpdateRequest.email());
            softly.assertThat(client.address()).isEqualTo(clientUpdateRequest.address());
            softly.assertThat(clientRepository.existsById(client.id())).isTrue();
        });
    }

    @Test
    void shouldSoftDeleteClientFromDatabase() {
        Long id = 3L;

        Assertions.assertTrue(clientRepository.existsById(id));
        clientServiceImpl.softDeleteClient(id);

        Status statusInactive = clientRepository.findById(id).get().getStatus();

        assertEquals(Status.INACTIVE, statusInactive);
        assertTrue(clientRepository.existsById(id));
        assertFalse(clientRepository.findByStatusAndId(Status.ACTIVE, id).isPresent());

        Set<ClientDTO> clients = clientServiceImpl.findClients();
        assertFalse(clients.stream()
                .anyMatch(client -> client.id().equals(id)));
    }

    private static void assertThrowsWithType(ExceptionType exceptionType, Executable executable) {
        RestException ex = Assertions.assertThrows(RestException.class, executable);
        assertEquals(exceptionType, ex.getExceptionType());
        assertEquals(exceptionType.getMessage(), ex.getMessage());
    }
}
