package com.example.bank.integration.rest;

import com.example.bank.client.ChangePasswordRequest;
import com.example.bank.client.ClientRequest;
import com.example.bank.client.jpa.Client;
import com.example.bank.integration.ClientRequestAndClientIntegrationTestUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientControllerIntegrationTest {
    @LocalServerPort
    private int localPort;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final long clientId = 1L;
    private final ClientRequest clientRequest =
            ClientRequestAndClientIntegrationTestUtils.clientRequestIntegrationTestBuilder();

    @Sql({"classpath:schema.sql", "classpath:data.sql"})
    @Test
    void shouldGetClientById() {
        ResponseEntity<Client> getClientResponse = testRestTemplate
                .getForEntity("http://localhost:" + localPort + "/bank/client/find/" + clientId, Client.class);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getClientResponse).isNotNull();
            softly.assertThat(1).isEqualTo(Objects.requireNonNull(getClientResponse.getBody()).getId());
            softly.assertThat(getClientResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        });
    }

    @Test
    void shouldUpdateClientById() {
        ResponseEntity<Client> updateClientResponse = testRestTemplate
                .postForEntity("http://localhost:" + localPort + "/bank/client/update/" + clientId,
                        clientRequest, Client.class);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updateClientResponse).isNotNull();
            softly.assertThat(Objects.requireNonNull(updateClientResponse.getBody()).getId()).isEqualTo(clientId);
            softly.assertThat(updateClientResponse.getStatusCode()).isEqualTo(201);
        });
    }

    @Test
    void shouldDeleteClientById() {
        ResponseEntity<Void> deleteClientResponse = testRestTemplate
                .getForEntity("http://localhost:" + localPort + "/bank/client/delete/" + clientId, Void.class);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(deleteClientResponse).isNotNull();
            softly.assertThat(deleteClientResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        });
    }

    @Test
    void shouldChangePassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("weakpassword",
                "strOngPa$$w0rd", "strOngPa$$w0rd");

        ResponseEntity<?> changePasswordResponse = testRestTemplate
                .postForEntity("http://localhost:" + localPort + "/bank/client/change-password", changePasswordRequest,
                        Client.class);
        Assertions.assertEquals(changePasswordResponse.getStatusCode(), HttpStatus.OK);
    }
}
