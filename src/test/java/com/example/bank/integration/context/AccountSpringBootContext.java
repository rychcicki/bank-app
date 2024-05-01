package com.example.bank.integration.context;

import com.example.bank.bank.transfer.account.AccountController;
import com.example.bank.bank.transfer.account.AccountRepository;
import com.example.bank.bank.transfer.account.AccountService;
import com.example.bank.client.ClientService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = com.example.bank.bank.transfer.account.AccountRepository.class)
@EntityScan("com.example.bank.bank.transfer.account")
@EnableAutoConfiguration
public class AccountSpringBootContext {
    @Bean
    public AccountService accountService(AccountRepository accountRepository, ClientService clientService) {
        return new AccountService(accountRepository, clientService);
    }

    @Bean
    public AccountController accountController(AccountService accountService) {
        return new AccountController(accountService);
    }
}
