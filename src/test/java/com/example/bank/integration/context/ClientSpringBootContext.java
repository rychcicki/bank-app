package com.example.bank.integration.context;

import com.example.bank.client.ClientService;
import com.example.bank.client.jpa.ClientRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableJpaRepositories(basePackageClasses = {com.example.bank.client.jpa.ClientRepository.class,
        com.example.bank.security.token.TokenRepository.class,
        com.example.bank.bank.transfer.account.AccountRepository.class,
        com.example.bank.bank.transfer.transfer.history.TransferHistoryRepository.class})
@EntityScan({"com.example.bank.client.jpa", "com.example.bank.security.token"/*, "com.example.bank.bank.transfer.account"*/})
@EnableAutoConfiguration
@Profile("test")
public class ClientSpringBootContext {
    @Bean
    public ClientService clientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        return new ClientService(clientRepository, passwordEncoder);
    }

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
//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//
//    @Bean
//    public JwtService jwtService() {
//        return new JwtService();
//    }


//    @Bean
//    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
//        return new ProviderManager(authenticationProvider);
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                return null;
//            }
//        };
//    }
}
