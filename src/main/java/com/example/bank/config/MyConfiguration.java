package com.example.bank.config;

import com.example.bank.bank.transfer.account.AccountRepository;
import com.example.bank.bank.transfer.feign.RateClient;
import com.example.bank.bank.transfer.transfer.TransferService;
import com.example.bank.bank.transfer.transfer.TransferValidationUtils;
import com.example.bank.bank.transfer.transfer.history.TransferHistoryService;
import com.example.bank.client.ClientService;
import com.example.bank.client.jpa.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class MyConfiguration {
    @Bean
    @RequestScope
    ClientService clientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        return new ClientService(clientRepository, passwordEncoder);
    }

    @Bean
    @RequestScope
    TransferService transferService(TransferHistoryService transferHistoryService, AccountRepository accountRepository,
                                    RateClient rateClient, TransferValidationUtils transferValidationUtils) {
        return new TransferService(transferHistoryService, accountRepository, rateClient, transferValidationUtils);
    }

    @Bean
    @RequestScope
    TransferValidationUtils transferValidationUtils() {
        return new TransferValidationUtils();
    }
}
