//package com.example.bank.integration.context;
//
//import com.example.bank.bank.transfer.account.AccountRepository;
//import com.example.bank.bank.transfer.feign.RateClient;
//import com.example.bank.bank.transfer.transfer.TransferService;
//import com.example.bank.bank.transfer.transfer.TransferValidationUtils;
//import com.example.bank.bank.transfer.transfer.history.TransferHistoryRepository;
//import com.example.bank.bank.transfer.transfer.history.TransferHistoryService;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//@Configuration
//@EnableJpaRepositories(basePackageClasses = com.example.bank.bank.transfer.transfer.history
//        .TransferHistoryRepository.class)
//@EntityScan("com.example.bank.bank.transfer.history")
//@Profile("test")
//@EnableAutoConfiguration
//public class TransferSpringBootContext {
//    @Bean
//    public TransferService transferService(TransferHistoryService transferHistoryService,
//                                           AccountRepository accountRepository, RateClient rateClient,
//                                           TransferValidationUtils transferValidationUtils) {
//        return new TransferService(transferHistoryService, accountRepository, rateClient, transferValidationUtils);
//    }
//
//    @Bean
//    public TransferHistoryService transferHistoryService(TransferHistoryRepository transferHistoryRepository) {
//        return new TransferHistoryService(transferHistoryRepository);
//    }
//}
