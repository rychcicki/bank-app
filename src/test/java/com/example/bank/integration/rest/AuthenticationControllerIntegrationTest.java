//package com.example.bank.integration.rest;
//
//import com.example.bank.client.ClientRequest;
//import com.example.bank.client.jpa.Client;
//import com.example.bank.client.jpa.ClientRepository;
//import com.example.bank.integration.ClientRequestAndClientIntegrationTestUtils;
//import com.example.bank.security.auth.AuthenticationRequest;
//import com.example.bank.security.auth.AuthenticationResponse;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Objects;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//class AuthenticationControllerIntegrationTest {
//    @LocalServerPort
//    private int localPort;
//    @Autowired
//    private TestRestTemplate testRestTemplate;
//    @Autowired
//    private ClientRepository clientRepository;
//    private final ClientRequest clientRequest =
//            ClientRequestAndClientIntegrationTestUtils.clientRequestIntegrationTestBuilder();
//    private final Client testClient = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();
//
//    @BeforeEach
//    public void setUp() {
//        clientRepository.save(testClient);
//    }
//
//    @Test
//    void shouldRegisterClient() {
//        ResponseEntity<AuthenticationResponse> registerClientResponse =
//                testRestTemplate.postForEntity("http://localhost:" + localPort + "/bank/api/v1/auth/register",
//                        clientRequest, AuthenticationResponse.class);
//        softAssertionsForResponse(registerClientResponse);
//    }
//
//    @Test
//    void shouldAuthenticateClient() {
//        ResponseEntity<AuthenticationResponse> authenticateResponse =
//                testRestTemplate.postForEntity("http://localhost:" + localPort + "/bank/api/v1/auth/authenticate",
//                        clientRequest, AuthenticationResponse.class);
//        softAssertionsForResponse(authenticateResponse);
//    }
//
//    @Test
//    void shouldRefreshToken() {
//        //given
//        String email = testClient.getEmail();
//        String password = testClient.getPassword();
//        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
//        HttpEntity<AuthenticationRequest> authRequest = new HttpEntity<>(authenticationRequest);
//        ResponseEntity<AuthenticationResponse> authResponse =
//                testRestTemplate.postForEntity("http://localhost:" + localPort + "/bank/api/v1/auth/authenticate",
//                        authRequest, AuthenticationResponse.class);
//        String refreshToken = Objects.requireNonNull(authResponse.getBody()).getRefreshToken();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(refreshToken);
//        HttpEntity<Void> refreshRequest = new HttpEntity<>(headers);
//        //when
//        ResponseEntity<Void> refreshResponse =
//                testRestTemplate.exchange("http://localhost:" + localPort + "/bank/api/v1/auth/refresh-token",
//                        HttpMethod.POST, refreshRequest, Void.class);
//        //then
//        Assertions.assertTrue(refreshResponse.getStatusCode().is2xxSuccessful());
//    }
//
//    private static void softAssertionsForResponse(ResponseEntity<AuthenticationResponse> response) {
//        SoftAssertions.assertSoftly(softly -> {
//            softly.assertThat(response).isNotNull();
//            softly.assertThat(Objects.requireNonNull(response.getBody()).getAccessToken()).isNotBlank();
//            softly.assertThat(Objects.requireNonNull(response.getBody()).getRefreshToken()).isNotBlank();
//            softly.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
//        });
//    }
//}
