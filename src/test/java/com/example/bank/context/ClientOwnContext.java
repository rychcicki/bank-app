package com.example.bank.context;

import com.example.bank.client.ClientMapper;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.ClientService;
import com.example.bank.client.ClientServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test")
@EnableJpaRepositories(basePackages = {
        "com.example.bank.client",
        "com.example.bank.security"
})
@EntityScan(basePackages = {
        "com.example.bank.client.model",
        "com.example.bank.account.model",
        "com.example.bank.security.model"
})
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.example.bank.client",
        "com.example.bank.config"
})
public class ClientOwnContext {
    @Bean
    public ClientService clientService(ClientRepository clientRepository, ClientMapper clientMapper,
                                       PasswordEncoder passwordEncoder) {
        return new ClientServiceImpl(clientRepository, clientMapper, passwordEncoder);
    }
}
