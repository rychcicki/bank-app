package com.example.bank.integration.context;

import com.example.bank.client.ClientMapper;
import com.example.bank.client.ClientService;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.ClientServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableJpaRepositories(basePackageClasses = {com.example.bank.client.ClientRepository.class,
        com.example.bank.account.AccountRepository.class})
@EntityScan({"com.example.bank.client.jpa", "com.example.bank.security.token", "com.example.bank.bank.transfer.account"})
@EnableAutoConfiguration
@Profile("test")
public class ClientSpringBootContext {
    @Bean
    public ClientService clientService(ClientRepository clientRepository,ClientMapper clientMapper,
                                       PasswordEncoder passwordEncoder) {
        return new ClientServiceImpl(clientRepository,clientMapper,passwordEncoder);
    }
}
