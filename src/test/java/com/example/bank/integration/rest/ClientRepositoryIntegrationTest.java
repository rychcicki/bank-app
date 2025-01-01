//package com.example.bank.integration.rest;
//
//import com.example.bank.client.jpa.Address;
//import com.example.bank.client.jpa.Client;
//import com.example.bank.client.jpa.ClientRepository;
//import com.example.bank.exception.ClientNotFoundException;
//import com.example.bank.integration.context.ClientSpringBootContext;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDate;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = ClientSpringBootContext.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//public class ClientRepositoryIntegrationTest {
//    @Autowired
//    private ClientRepository clientRepository;
//    private final String noClientInDatabaseExceptionMessage = "Error. There is no client in database.";
//    //    private Client testClient = ClientRequestAndClientIntegrationTestUtils.clientIntegrationTestBuilder();
//    private Client testClient = new Client("Zdzislaw", "Krecina",
//            LocalDate.of(1954, 4, 28), "zdzislaw.krecina@gmail.com", "password",
//            new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
//
//    @BeforeEach
//    void setUp() {
//        clientRepository.save(testClient);
//    }
//
//    @Test
//    void shouldFindClientByIdInRepository() {
//        Client clientFromRepo = clientRepository.findById(testClient.getId()).orElseThrow(
//                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//
//        SoftAssertions.assertSoftly(softly -> {
//            softly.assertThat(clientFromRepo).isNotNull();
//            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
//        });
//    }
//
//    @Test
//    void shouldFindClientByEmailInRepository() {
//        Client clientFromRepo = clientRepository.findByEmail(testClient.getEmail()).orElseThrow(
//                () -> new ClientNotFoundException(noClientInDatabaseExceptionMessage));
//
//        SoftAssertions.assertSoftly(softly -> {
//            softly.assertThat(clientFromRepo).isNotNull();
//            softly.assertThat(clientFromRepo).usingRecursiveComparison().isEqualTo(testClient);
//        });
//    }
//}
