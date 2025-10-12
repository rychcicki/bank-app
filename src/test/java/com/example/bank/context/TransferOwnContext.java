package com.example.bank.context;

import com.example.bank.account.AccountService;
import com.example.bank.client.ClientService;
import com.example.bank.transfer.TransferHistoryRepository;
import com.example.bank.transfer.TransferService;
import com.example.bank.transfer.export.ExportTransferHistoryService;
import com.example.bank.transfer.feign.RateClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("test")
@EnableJpaRepositories(basePackages = {
        "com.example.bank.account",
        "com.example.bank.transfer",
        "com.example.bank.client",
        "com.example.bank.security"
})
@EntityScan(basePackages = {
        "com.example.bank.transfer.model",
        "com.example.bank.account.model",
        "com.example.bank.client.model",
        "com.example.bank.security.model"
})
@EnableAutoConfiguration
@EnableFeignClients(basePackages = "com.example.bank.transfer.feign")
@ComponentScan(basePackages = {
        "com.example.bank.account",
        "com.example.bank.transfer",
        "com.example.bank.client",
        "com.example.bank.config"
})
public class TransferOwnContext {
    @Bean
    public TransferService transferService(TransferHistoryRepository transferHistoryRepository, RateClient rateClient,
                                           ClientService clientService, AccountService accountService) {
        return new TransferService(transferHistoryRepository, rateClient, accountService, clientService);
    }

    @Bean
    ExportTransferHistoryService exportTransferHistoryService(TransferHistoryRepository transferHistoryRepository) {
        return new ExportTransferHistoryService(transferHistoryRepository);
    }
}
