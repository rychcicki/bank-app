package com.example.bank.integration.context;

import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = ClientSpringBootContext.class)
@ActiveProfiles("test")
class ClientSpringBootContextIntegrationTest {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;

    @Sql({"classpath:schema.sql", "classpath:data.sql"})
    @Test
    void shouldFindClientInDatabase() {
        String email = "mike.wazowski@gmail.com";

        Client clientFromRepo = clientRepository.findByStatusAndEmail(Status.ACTIVE,email)
                .orElseThrow();
        Long id = clientFromRepo.getId();
        Client clientFromService = clientService.findClient(id);

        Assertions.assertEquals(clientFromRepo, clientFromService);
    }
}
