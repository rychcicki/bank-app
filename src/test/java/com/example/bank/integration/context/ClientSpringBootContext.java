//package com.example.bank.integration.context;
//
//import com.example.bank.client.ClientService;
//import com.example.bank.client.jpa.ClientRepository;
//import com.example.bank.security.auth.AuthenticationController;
//import com.example.bank.security.auth.AuthenticationService;
//import com.example.bank.security.config.JwtService;
//import com.example.bank.security.token.TokenRepository;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableJpaRepositories(basePackageClasses = com.example.bank.client.jpa.ClientRepository.class)
//@EntityScan({"com.example.bank.client.jpa", "com.example.bank.security.token","com.example.bank.bank.transfer.account"})
//@Profile("test")
//@EnableAutoConfiguration
//public class ClientSpringBootContext {
//    @Bean
//    public ClientService clientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
//        return new ClientService(clientRepository, passwordEncoder);
//    }
//
//    @Bean
//    public AuthenticationService authenticationService(ClientRepository clientRepository, TokenRepository tokenRepository,
//                                                       PasswordEncoder passwordEncoder, JwtService jwtService,
//                                                       AuthenticationManager authenticationManager) {
//        return new AuthenticationService(clientRepository, tokenRepository, passwordEncoder, jwtService, authenticationManager);
//    }
//
//    @Bean
//    public AuthenticationController authenticationController(AuthenticationService authenticationService) {
//        return new AuthenticationController(authenticationService);
//    }
//}
