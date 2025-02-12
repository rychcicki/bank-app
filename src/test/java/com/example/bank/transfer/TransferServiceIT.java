package com.example.bank.transfer;

import com.example.bank.account.AccountRepository;
import com.example.bank.account.model.Account;
import com.example.bank.context.TransferOwnContext;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TransferOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransferServiceIT {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferHistoryRepository transferHistoryRepository;

    @Autowired
    private TransferService transferService;

    @Test
    void shouldProcessBankTransferCorrectly() {
        Account senderAccount = accountRepository.findByAccountNumber("PL54613983300568639363795256").get();
        TransferRequest transferRequest = new TransferRequest("PL54613983300568639363795256",
                "GB92BARC20038472426896", BigDecimal.valueOf(10),
                "Integration test for transferService");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        TransferHistory expectedTransferHistory = TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(1L)
                .accountNumber("PL54613983300568639363795256")
                .externalAccountNumber("GB92BARC20038472426896")
                .previousBalance(BigDecimal.valueOf(2000.00).setScale(2, RoundingMode.HALF_EVEN))
                .amount(BigDecimal.valueOf(50.57))
                .balance(BigDecimal.valueOf(1949.43))
                .title("Integration test for transferService")
                .build();

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        TransferHistory transferHistory = transferHistoryRepository
                .findByAccountNumber("PL54613983300568639363795256").get(0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(transferHistoryRepository.findByAccountNumber("PL54613983300568639363795256"))
                    .hasSize(1);
            softly.assertThat(transferHistory).usingRecursiveComparison()
                    .ignoringFields("id", "createdOn", "createdBy", "updateOn", "updatedBy",
                            "amount", "balance")
                    .isEqualTo(expectedTransferHistory);
        });
    }

    @Test
    void shouldThrowRestExceptionWhenBalanceIsInsufficient() {
        Account senderAccount = accountRepository.findByAccountNumber("PL54613983300568639363795256").get();

        TransferRequest transferRequest = new TransferRequest("PL54613983300568639363795256",
                "DE11500105171841551884", BigDecimal.valueOf(500),
                "Integration test for transferService");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> transferService.processBankTransfer(transferRequest, authenticatedUser));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION.getMessage());
    }

    @Test
    void shouldThrowConstraintViolationExceptionWhenAmountIsInsufficient() {
        Account senderAccount = accountRepository.findByAccountNumber("PL54613983300568639363795256").get();

        TransferRequest transferRequest = new TransferRequest("PL54613983300568639363795256",
                "DE11500105171841551884", BigDecimal.valueOf(0.00999999999), "lorem ipsum");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        ConstraintViolationException exception = Assertions.assertThrows(ConstraintViolationException.class,
                () -> transferService.processBankTransfer(transferRequest, authenticatedUser));
        Assertions.assertEquals(exception.getConstraintViolations().iterator().next().getMessage(),
                "Amount must be at least 0.01");
    }
}
