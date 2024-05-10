//package com.example.bank.integration.context;
//
//import com.example.bank.bank.transfer.account.Account;
//import com.example.bank.bank.transfer.account.AccountRepository;
//import com.example.bank.bank.transfer.account.AccountService;
//import com.example.bank.client.ClientService;
//import com.example.bank.client.jpa.ClientRepository;
//import com.example.bank.security.auth.AuthenticationService;
//import com.example.bank.security.config.JwtService;
//import com.example.bank.security.token.TokenRepository;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ActiveProfiles("test")
//@ContextConfiguration(classes = AccountSpringBootContext.class)
//class AccountSpringBootContextIntegrationTest {
//    @Autowired
//    private AccountService accountService;
//    @Autowired
//    private AccountRepository accountRepository;
//    @Autowired
//    private ClientService clientService;
//
//    @Autowired
//    private ClientRepository clientRepository;
//    @Autowired
//    private AuthenticationService authenticationService;
//    @Autowired
//    private TokenRepository tokenRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private JwtService jwtService;
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    private final Long clientId = 1L;
//
//    @Test
//    void shouldFindForeignAccount() {
//        Account foreignAccount = accountService.createForeignAccount(clientId);
//        accountAssertions(foreignAccount);
//    }
//
//    @Test
//    void shouldFindPolishAccount() {
//        Account polishAccount = accountService.createPolishAccounts(clientId);
//        accountAssertions(polishAccount);
//    }
//
//    @Test
//    void shouldFindMyBankAccount() {
//        Account myBankAccount = accountService.createMyBankAccount(clientId);
//        accountAssertions(myBankAccount);
//    }
//
//    private void accountAssertions(Account testAccount) {
//        String accountNumber = testAccount.getAccountNumber();
//        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(RuntimeException::new);
//        SoftAssertions.assertSoftly(softly -> {
//            softly.assertThat(accountNumber).isNotBlank();
//            softly.assertThat(account).isNotNull();
//            softly.assertThat(accountNumber).isEqualTo(account.getAccountNumber());
//        });
//    }
//}
