package com.example.bank.config;

import com.example.bank.registration.ClientService;
import com.example.bank.registration.jpa.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class MyConfiguration {
    @Bean
    @RequestScope
    ClientService clientService(ClientRepository clientRepository) {
        return new ClientService(clientRepository);
    }
}
