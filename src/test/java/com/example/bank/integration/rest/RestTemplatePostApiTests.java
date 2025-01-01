package com.example.bank.integration.rest;

import com.example.bank.client.model.Client;
import com.example.bank.integration.ClientRequestAndClientIntegrationTestUtils;
import com.example.bank.integration.DemoAppTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(classes = DemoAppTests.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplatePostApiTests {
    @LocalServerPort
    int randomPort;
    long clientId = 1L;
    Client client = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();


    @Test
    public void testAddClientWithBodySuccess() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + randomPort + "/bank/client/" + clientId);
        Client updatedClient = restTemplate.postForObject(uri, client, Client.class);
        assert updatedClient != null;
        Assertions.assertNotNull(updatedClient.getId());
    }
}
