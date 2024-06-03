package com.example.bank.integration.context;

import com.example.bank.client.ClientService;
import com.example.bank.client.jpa.ClientRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {com.example.bank.client.jpa.ClientRepository.class,
        com.example.bank.security.token.TokenRepository.class,
        com.example.bank.bank.transfer.account.AccountRepository.class})
@EntityScan({"com.example.bank.client.jpa", "com.example.bank.security.token", "com.example.bank.bank.transfer.account"})
@EnableAutoConfiguration
@Profile("test")
public class ClientSpringBootContext {
    @Bean
    public ClientService clientService(ClientRepository clientRepository) {
        return new ClientService(clientRepository);
    }
}
