package com.example.bank.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Scanner;

@SpringBootTest
@ActiveProfiles("test")
public class DemoAppTests {
    @Test
    void contextLoads() {
    }
    @Sql({"classpath:schema.sql"/*, "classpath:data.sql*/})
    @Test
    void shouldSoutSomething(){
        System.out.println("hahahahahahhahahahahahahahaah");
    }
//    @Bean(name="entityManagerFactory")
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        return sessionFactory;
//    }
}
