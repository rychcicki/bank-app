package com.example.bank.account;

import com.example.bank.account.model.Currency;
import com.example.bank.context.AccountOwnContext;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

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

    @ParameterizedTest(name = "notCreateAccountWhenClientNotFound: {0}")
    @MethodSource("com.example.bank.account.AccountServiceUtils#accountCreationActionsScenarios")
    void shouldThrowExceptionWhenClientNotFound(String scenarioMethod,
                                                BiConsumer<AccountService, Long> accountCreationMethod) {
        Long nonExistingClientId = 1234567890L;

        RestException myBankAccountEx = Assertions.assertThrows(RestException.class,
                () -> accountCreationMethod.accept(accountService, nonExistingClientId));
        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage(), myBankAccountEx.getMessage());
    }

    private void accountAssertions(AccountDTO testAccount, Long clientId) {
        String accountNumber = testAccount.accountNumber();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(accountNumber).isNotBlank();
            softly.assertThat(accountRepository.findByAccountNumber(accountNumber).isPresent()).isEqualTo(true);
            softly.assertThat(testAccount.clientId()).isEqualTo(clientId);
            softly.assertThat(testAccount.balance()).isEqualTo(BigDecimal.ZERO);
        });
    }
}
