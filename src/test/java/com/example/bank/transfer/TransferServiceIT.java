package com.example.bank.transfer;

import com.example.bank.account.AccountService;
import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.client.model.Client;
import com.example.bank.context.TransferOwnContext;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.feign.RateResponse;
import com.example.bank.transfer.feign.Rates;
import com.example.bank.transfer.model.TransferHistory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.example.bank.transfer.TransferServiceITUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TransferOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
@Transactional
class TransferServiceIT {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferHistoryRepository transferHistoryRepository;

    @Autowired
    private TransferService transferService;

    @MockitoBean
    private RateClient rateClient;

    private TransferRequest transferRequest;
    private Account senderAccount;
    private Account receiverAccount;
    private Client senderUser;
    private Client admin;
    private BigDecimal senderAmountDelta;

    @BeforeEach
    void setUpInputsAndRates() {
        transferRequest = createTransferRequest();
        senderAccount = accountService.findAccount(transferRequest.senderAccountNumber());
        receiverAccount = accountService.findAccount(transferRequest.receiverAccountNumber());
        senderUser = senderAccount.getClient();
        admin = defaultAdmin();

        Map<Currency, BigDecimal> rates = Map.of(
                Currency.USD, BigDecimal.valueOf(3.7796),
                Currency.AUD, BigDecimal.valueOf(2.4526),
                Currency.EUR, BigDecimal.valueOf(4.2933),
                Currency.CHF, BigDecimal.valueOf(4.6221),
                Currency.GBP, BigDecimal.valueOf(4.9893),
                Currency.NOK, BigDecimal.valueOf(0.3925)
        );
        rates.forEach((currency, mid) -> {
            RateResponse response = new RateResponse("A", currency.name(), currency.name(),
                    List.of(new Rates("0", currency.name(), mid)));
            when(rateClient.getCurrencyRate(currency)).thenReturn(response);
        });

        BigDecimal senderRate = senderAccount.getCurrency() == Currency.PLN ? BigDecimal.ONE :
                rateClient.getCurrencyRate(senderAccount.getCurrency()).rates().getFirst().mid();

        BigDecimal receiverRate = receiverAccount.getCurrency() == Currency.PLN ? BigDecimal.ONE :
                rateClient.getCurrencyRate(receiverAccount.getCurrency()).rates().getFirst().mid();

        senderAmountDelta = transferRequest.amount()
                .multiply(receiverRate.divide(senderRate, 4, RoundingMode.HALF_EVEN))
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Test
    void shouldProcessBankTransferForSenderUser() {
        TransferHistory expectedSenderTransferHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory expectedReceiverTransferHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        transferService.processBankTransfer(transferRequest, senderUser);

        assertAccountHistoryAndBalanceMatch(transferRequest.senderAccountNumber(), expectedSenderTransferHistory);
        assertAccountHistoryAndBalanceMatch(transferRequest.receiverAccountNumber(), expectedReceiverTransferHistory);
    }

    private void assertAccountHistoryAndBalanceMatch(String accountNumber, TransferHistory expectedTransferHistory) {
        List<TransferHistory> histories = transferHistoryRepository.findByAccountNumber(accountNumber);
        Account reloadedAccount = accountService.findAccount(accountNumber);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(histories).hasSize(1);
            softly.assertThat(histories.getFirst())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "createdOn", "createdBy", "updateOn", "updatedBy")
                    .isEqualTo(expectedTransferHistory);
            softly.assertThat(reloadedAccount.getBalance()).isEqualByComparingTo(expectedTransferHistory.getBalance());
        });
    }

    @Test
    void shouldProcessBankTransferForAdmin() {
        TransferHistory expectedSenderTransferHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory expectedReceiverTransferHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        transferService.processBankTransfer(transferRequest, admin);

        assertAccountHistoryAndBalanceMatch(transferRequest.senderAccountNumber(), expectedSenderTransferHistory);
        assertAccountHistoryAndBalanceMatch(transferRequest.receiverAccountNumber(), expectedReceiverTransferHistory);
    }

    @Test
    void shouldThrowRestExceptionWhenUnauthorizedClient() {
        Client unauthorizedClient = defaultUnauthorizedClient();

        assertThatThrownBy(() -> transferService.processBankTransfer(transferRequest, unauthorizedClient))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage());
    }

    @Test
    void shouldThrowRestExceptionAndRollbackWhenBalanceInsufficient() {
        TransferRequest transferRequestTooLargeAmount = createTransferRequestInsufficientBalance();

        BigDecimal previousSenderBalance = senderAccount.getBalance();
        BigDecimal previousReceiverBalance = receiverAccount.getBalance();

        assertThatThrownBy(() -> transferService.processBankTransfer(transferRequestTooLargeAmount, senderUser))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION.getMessage());

        Account reloadedSender = accountService.findAccount(senderAccount.getAccountNumber());
        Account reloadedReceiver = accountService.findAccount(receiverAccount.getAccountNumber());
        assertThat(reloadedSender.getBalance()).isEqualByComparingTo(previousSenderBalance);
        assertThat(reloadedReceiver.getBalance()).isEqualByComparingTo(previousReceiverBalance);
    }

    @Test
    void shouldThrowRestExceptionWhenAccountNotExist() {
        TransferRequest invalidTransferRequest = createTransferRequestAccountNotExist();

        assertThatThrownBy(() -> transferService.processBankTransfer(invalidTransferRequest, admin))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
    }
}
