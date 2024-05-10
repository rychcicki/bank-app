//package com.example.bank.integration.context;
//
//import com.example.bank.client.ClientRequest;
//import com.example.bank.client.ClientService;
//import com.example.bank.client.jpa.Client;
//import com.example.bank.client.jpa.ClientRepository;
//import com.example.bank.exception.ClientNotFoundException;
//import com.example.bank.integration.ClientRequestAndClientIntegrationTestUtils;
//import com.example.bank.security.auth.AuthenticationRequest;
//import com.example.bank.security.auth.AuthenticationResponse;
//import com.example.bank.security.auth.AuthenticationService;
//import com.example.bank.security.config.JwtService;
//import com.example.bank.security.token.TokenRepository;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ActiveProfiles("test")
//@ContextConfiguration(classes = ClientSpringBootContext.class)
//class ClientSpringBootContextIntegrationTest {
//    @Autowired
//    private ClientService clientService;
//    @Autowired
//    private ClientRepository clientRepository;
//    @Autowired
//    private AuthenticationService authenticationService;
//    @Autowired
//    private TokenRepository tokenRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private JwtService jwtService;
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    private final String noClientInDatabaseExceptionMessage = "Error. There is no client in database.";
//
////    @Sql({"classpath:schema.sql", "classpath:data.sql"})
//    @Test
//    void shouldFindClientInDatabase() {
//        String email = "mike.wazowski@gmail.com";
//        //nie testować jpa
//        Client clientFromRepo = clientRepository.findByEmail(email)
//                .orElseThrow(() -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//        Long id = clientFromRepo.getId();
//        Client clientFromService = clientService.getClientById(id);
//
//        Assertions.assertEquals(clientFromRepo, clientFromService);
//    }
//
//    @Test
//    void shouldSaveClientInDatabase() {
//        Client client = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();
//        client.setEmail("temp@mail.com");
//        Client savedClientToRepo = clientRepository.save(client);
//        Client clientFromRepo = clientRepository.findByEmail("temp@mail.com").orElseThrow(
//                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//        Assertions.assertEquals(clientFromRepo, savedClientToRepo);
//
//        ClientRequest clientRequest = ClientRequestAndClientIntegrationTestUtils.clientRequestIntegrationTestBuilder();
//        authenticationService.registerClient(clientRequest);
//        Client clientFromRegistration = clientRepository.findByEmail("michal.listkiewicz@gmail.com").orElseThrow(
//                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//        Assertions.assertNotNull(clientFromRegistration);
//    }
//
//    @Test
//    void shouldAuthenticateClient() {
//        String email = "michal.listkiewicz@gmail.com";
//        Client client = clientRepository.findByEmail(email).orElseThrow(
//                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//        Assertions.assertNotNull(client);
//
//        AuthenticationRequest authenticationRequest =
//                new AuthenticationRequest("michal.listkiewicz@gmail.com", "password");
//        AuthenticationResponse authenticate = authenticationService.authenticate(authenticationRequest);
//        SoftAssertions.assertSoftly(softly -> {
//            softly.assertThat(authenticate).isNotNull();
//            softly.assertThat(authenticate.getAccessToken()).isNotBlank();
//            softly.assertThat(authenticate.getRefreshToken()).isNotBlank();
//        });
//    }
//}
