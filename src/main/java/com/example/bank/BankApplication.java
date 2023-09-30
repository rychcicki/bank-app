package com.example.bank;

import com.example.bank.userRegister.Address;
import com.example.bank.userRegister.User;
import com.example.bank.userRegister.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            User user = User.builder()
                    .firstName("Andrzej")
                    .lastName("Nowak")
                    .birthDate(LocalDate.of(1955, 3, 12))
                    .email("andrzej.nowak@gmail.com")
                    .address(
                            Address.builder()
                                    .streetName("Ogrodowa")
                                    .zipCode(32432)
                                    .city("Pcim").build()
                    ).build();
            userRepository.save(user);
            userRepository.findById(1L)
                    .ifPresent(System.out::println);
        };
    }
}
