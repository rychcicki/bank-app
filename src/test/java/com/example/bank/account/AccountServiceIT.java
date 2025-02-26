package com.example.bank.account;

import com.example.bank.account.model.Currency;
import com.example.bank.context.AccountOwnContext;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccountOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
@Transactional
class AccountServiceIT {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldCreateMyBankAccountAndReturnDto() {
        Long clientId = 1L;
        AccountDTO myBankAccount = accountService.createMyBankAccount(clientId);
        accountAssertions(myBankAccount, clientId);
        Assertions.assertEquals(myBankAccount.currency(), Currency.PLN);
        Assertions.assertEquals(myBankAccount.accountNumber().substring(4, 7), AccountNumberGenerator.MY_BANK_CODE);
    }

    @Test
    void shouldCreatePolishAccountAndReturnDto() {
        Long clientId = 2L;
        AccountDTO polishAccount = accountService.createPolishAccount(clientId);
        accountAssertions(polishAccount, clientId);
        Assertions.assertEquals(polishAccount.currency(), Currency.PLN);
        Assertions.assertNotEquals(polishAccount.accountNumber().substring(4, 7), AccountNumberGenerator.MY_BANK_CODE);
    }

    @Test
    void shouldCreateForeignAccountAndReturnDto() {
        Long clientId = 3L;
        AccountDTO foreignAccount = accountService.createForeignAccount(clientId);
        accountAssertions(foreignAccount, clientId);
        Assertions.assertNotEquals(foreignAccount.currency(), Currency.PLN);
    }

    @Test
    void shouldThrowWhenClientNotFound() {
        Long clientId = 123L;

        RestException myBankAccountEx = Assertions.assertThrows(RestException.class,
                () -> accountService.createMyBankAccount(clientId));
        Assertions.assertEquals(myBankAccountEx.getMessage(), ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage());

        RestException polishAccountEx = Assertions.assertThrows(RestException.class,
                () -> accountService.createPolishAccount(clientId));
        Assertions.assertEquals(polishAccountEx.getMessage(), ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage());

        RestException foreignAccountEx = Assertions.assertThrows(RestException.class,
                () -> accountService.createForeignAccount(clientId));
        Assertions.assertEquals(foreignAccountEx.getMessage(), ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage());
    }

    private void accountAssertions(AccountDTO testAccount, Long clientId) {
        String accountNumber = testAccount.accountNumber();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(accountNumber).isNotBlank();
            softly.assertThat(accountRepository.findByAccountNumber(accountNumber).isPresent()).isEqualTo(true);
            softly.assertThat(testAccount.clientId()).isEqualTo(clientId);
        });
    }
}
