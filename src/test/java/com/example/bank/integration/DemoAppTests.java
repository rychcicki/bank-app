package com.example.bank.integration;

import com.example.bank.BankApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BankApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class DemoAppTests {
    @Test
    void contextLoads() {
    }

    //    @Sql({"classpath:schema.sql"/*, "classpath:data.sql"*/})
    @Test
    void something() {
        System.out.println("test test test test");
    }
}
