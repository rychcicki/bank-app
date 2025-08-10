package com.example.bank.transfer;

import com.example.bank.account.AccountService;
import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.feign.RateResponse;
import com.example.bank.transfer.model.TransferHistory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.example.bank.transfer.TransferServiceUtils.*;
import static java.math.RoundingMode.HALF_EVEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @InjectMocks
    private TransferService transferService;

    @Mock
    private TransferHistoryRepository transferHistoryRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private RateClient rateClient;

    @Mock
    private ClientService clientService;

    private static final int SCALE_RATE = 4;

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferDifferentCurrencies")
    void shouldThrowRestExceptionWhenSenderIsNotAdminOrAccountOwner(String scenarioMethod,
                                                                    TransferRequest transferRequest,
                                                                    Account senderAccount, Account receiverAccount,
                                                                    RateResponse unusedSenderRateResponse,
                                                                    RateResponse unusedReceiverRateResponse) {
        Client unauthorizedUser = defaultUnauthorizedClient();

        when(accountService.findAccount(transferRequest.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequest.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(unauthorizedUser.getId())).thenReturn(unauthorizedUser);

        assertThatThrownBy(() -> transferService.processBankTransfer(transferRequest, unauthorizedUser))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage());

        verify(accountService, never()).saveAll(anyList());
        verifyNoInteractions(transferHistoryRepository, rateClient);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferDifferentCurrencies")
    void shouldThrowRestExceptionWhenSenderAccountNotFound(String scenarioMethod, TransferRequest transferRequest,
                                                           Account senderAccount,
                                                           Account ususedReceiverAccount,
                                                           RateResponse unusedSenderRateResponse,
                                                           RateResponse unusedReceiverRateResponse) {
        when(accountService.findAccount(transferRequest.senderAccountNumber()))
                .thenThrow(new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));

        assertThatThrownBy(() -> transferService.processBankTransfer(transferRequest, senderAccount.getClient()))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());

        verify(accountService, never()).saveAll(anyList());
        verifyNoInteractions(transferHistoryRepository, rateClient);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferInsufficientBalance")
    void shouldThrowRestExceptionWhenSenderBalanceIsInsufficient(String scenarioMethod,
                                                                 TransferRequest transferRequestTooLargeAmount,
                                                                 Account senderAccount, Account receiverAccount,
                                                                 RateResponse rateResponse) {
        Client authenticatedUser = senderAccount.getClient();

        when(accountService.findAccount(transferRequestTooLargeAmount.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequestTooLargeAmount.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(authenticatedUser.getId())).thenReturn(authenticatedUser);
        when(rateClient.getCurrencyRate(any(Currency.class))).thenReturn(rateResponse);

        assertThatThrownBy(() -> transferService.processBankTransfer(transferRequestTooLargeAmount, authenticatedUser))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION.getMessage());

        verify(accountService, never()).saveAll(anyList());
        verifyNoInteractions(transferHistoryRepository);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferDifferentCurrencies")
    void shouldProcessTransferWhenAccountsAndBalanceAreValid(String scenarioMethod,
                                                             TransferRequest transferRequest,
                                                             Account senderAccount, Account receiverAccount,
                                                             RateResponse senderRateResponse,
                                                             RateResponse receiverRateResponse) {
        Client authenticatedUser = senderAccount.getClient();
        BigDecimal senderAmountDelta =
                calculateSenderAmountDelta(transferRequest, senderRateResponse, receiverRateResponse);
        TransferHistory senderHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory receiverHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        when(accountService.findAccount(transferRequest.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequest.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(authenticatedUser.getId())).thenReturn(authenticatedUser);
        when(rateClient.getCurrencyRate(senderAccount.getCurrency())).thenReturn(senderRateResponse);
        when(rateClient.getCurrencyRate(receiverAccount.getCurrency())).thenReturn(receiverRateResponse);

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        verify(accountService).findAccount(transferRequest.senderAccountNumber());
        verify(accountService).findAccount(transferRequest.receiverAccountNumber());
        verify(rateClient).getCurrencyRate(senderAccount.getCurrency());
        verify(rateClient).getCurrencyRate(receiverAccount.getCurrency());
        verifyAccountsAndHistoriesSavedCorrectly(senderAccount, receiverAccount, senderHistory, receiverHistory);
    }

    private static BigDecimal calculateSenderAmountDelta(TransferRequest transferRequest,
                                                         RateResponse senderRateResponse,
                                                         RateResponse receiverRateResponse) {
        BigDecimal midSender = senderRateResponse.rates().getFirst().mid();
        BigDecimal midSReceiver = receiverRateResponse.rates().getFirst().mid();
        return transferRequest.amount()
                .multiply(midSReceiver.divide(midSender, SCALE_RATE, HALF_EVEN))
                .setScale(SCALE, HALF_EVEN);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferSamePlnCurrency")
    void shouldProcessTransferWhenAccountsAndBalanceAreValidInPlnCurrency(String scenarioMethod,
                                                                          TransferRequest transferRequest,
                                                                          Account senderAccount,
                                                                          Account receiverAccount) {
        Client authenticatedUser = senderAccount.getClient();
        BigDecimal senderAmountDelta = transferRequest.amount();
        TransferHistory senderHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory receiverHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        when(accountService.findAccount(transferRequest.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequest.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(authenticatedUser.getId())).thenReturn(authenticatedUser);

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        verify(accountService).findAccount(transferRequest.senderAccountNumber());
        verify(accountService).findAccount(transferRequest.receiverAccountNumber());
        verifyNoInteractions(rateClient);
        verifyAccountsAndHistoriesSavedCorrectly(senderAccount, receiverAccount, senderHistory, receiverHistory);

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferPlnToOtherCurrency")
    void shouldProcessTransferWhenAccountsAndBalanceAreValidPlnToForeignCurrency(String scenarioMethod,
                                                                                 TransferRequest transferRequest,
                                                                                 Account senderAccount,
                                                                                 Account receiverAccount,
                                                                                 RateResponse rateResponse) {
        Client authenticatedUser = senderAccount.getClient();
        BigDecimal senderAmountDelta = transferRequest.amount().multiply(rateResponse.rates().getFirst().mid());
        TransferHistory senderHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory receiverHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        when(accountService.findAccount(transferRequest.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequest.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(authenticatedUser.getId())).thenReturn(authenticatedUser);
        when(rateClient.getCurrencyRate(receiverAccount.getCurrency())).thenReturn(rateResponse);

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        verify(accountService).findAccount(transferRequest.senderAccountNumber());
        verify(accountService).findAccount(transferRequest.receiverAccountNumber());
        verify(rateClient).getCurrencyRate(receiverAccount.getCurrency());
        verify(rateClient, never()).getCurrencyRate(Currency.PLN);
        verifyAccountsAndHistoriesSavedCorrectly(senderAccount, receiverAccount, senderHistory, receiverHistory);

    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransferSameCurrency")
    void shouldProcessTransferWhenCurrenciesAreEqualAndUserIsAdmin(String scenarioMethod,
                                                                   TransferRequest transferRequest,
                                                                   Account senderAccount,
                                                                   Account receiverAccount,
                                                                   RateResponse unusedRateResponse) {
        Client admin = TransferServiceUtils.defaultAdmin();
        BigDecimal senderAmountDelta = transferRequest.amount();
        TransferHistory senderHistory =
                buildExpectedSenderTransferHistory(transferRequest, senderAccount, receiverAccount, senderAmountDelta);
        TransferHistory receiverHistory =
                buildExpectedReceiverTransferHistory(transferRequest, receiverAccount, senderAccount);

        when(accountService.findAccount(transferRequest.senderAccountNumber())).thenReturn(senderAccount);
        when(accountService.findAccount(transferRequest.receiverAccountNumber())).thenReturn(receiverAccount);
        when(clientService.findClient(admin.getId())).thenReturn(admin);

        transferService.processBankTransfer(transferRequest, admin);

        verify(accountService).findAccount(transferRequest.senderAccountNumber());
        verify(accountService).findAccount(transferRequest.receiverAccountNumber());
        verifyNoInteractions(rateClient);
        verifyAccountsAndHistoriesSavedCorrectly(senderAccount, receiverAccount, senderHistory, receiverHistory);
    }

    private void verifyAccountsAndHistoriesSavedCorrectly(Account senderAccount, Account receiverAccount,
                                                          TransferHistory senderHistory, TransferHistory receiverHistory) {
        verify(accountService).saveAll(argThat(savedAccounts -> {
            assertThat(savedAccounts)
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrder(senderAccount, receiverAccount);
            return true;
        }));

        verify(transferHistoryRepository).saveAll(argThat(savedHistories -> {
            assertThat(savedHistories)
                    .hasSize(2)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdOn", "updateOn")
                    .containsExactlyInAnyOrder(senderHistory, receiverHistory);
            return true;
        }));
    }
}
