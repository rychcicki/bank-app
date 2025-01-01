package com.example.bank.integration.rest;

import com.example.bank.client.model.Client;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
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
    private final Client testClient = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();

    @BeforeEach
    void setUp() {
        clientRepository.save(testClient);
    }

    @Test
    void shouldFindClientByIdInRepository() {
        Client clientFromRepo = clientRepository.findById(testClient.getId()).orElseThrow(
                () -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientFromRepo).isNotNull();
            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
        });
    }

    @Test
    void shouldFindClientByEmailInRepository() {
        Client clientFromRepo = clientRepository.findByStatusAndEmail(Status.ACTIVE,testClient.getEmail()).orElseThrow(
                () -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(clientFromRepo).isNotNull();
            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
        });
    }
}
