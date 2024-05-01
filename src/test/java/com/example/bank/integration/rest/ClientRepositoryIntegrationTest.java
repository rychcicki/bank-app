package com.example.bank.integration.rest;

import com.example.bank.client.jpa.Client;
import com.example.bank.client.jpa.ClientRepository;
import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.integration.ClientRequestAndClientIntegrationTestUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ClientRepositoryIntegrationTest {
    @Autowired
    private ClientRepository clientRepository;
    private final String noClientInDatabaseExceptionMessage = "Error. There is no client in database.";
    private final Client testClient = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();

    @BeforeEach
    void setUp() {
        clientRepository.save(testClient);
    }

    @Test
    void shouldFindClientByIdInRepository() {
        Client clientFromRepo = clientRepository.findById(testClient.getId()).orElseThrow(
                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientFromRepo).isNotNull();
            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
        });
    }

    @Test
    void shouldFindClientByEmailInRepository() {
        Client clientFromRepo = clientRepository.findByEmail(testClient.getEmail()).orElseThrow(
                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientFromRepo).isNotNull();
            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
        });
    }
}
