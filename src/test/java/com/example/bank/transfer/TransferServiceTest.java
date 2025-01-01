package com.example.bank.transfer;

import com.example.bank.account.AccountRepository;
import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.feign.RateResponse;
import com.example.bank.transfer.feign.Rates;
import com.example.bank.transfer.model.TransferHistory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    @Mock
    private TransferHistoryRepository transferHistoryRepository;

    @Mock
    private RateClient rateClient;

    @Captor
    ArgumentCaptor<List<Account>> accountsCaptor;

    @Captor
    ArgumentCaptor<List<TransferHistory>> historiesCaptor;

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransfer")
    void shouldThrowRestExceptionWhenAuthenticatedUserIsNotOwnerOfSenderAccount(Account senderAccount,
                                                                                Account receiverAccount) {
        TransferRequest transferRequest = new TransferRequest("PL123456789", "EN123",
                BigDecimal.valueOf(10), "Money for nothing");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(receiverAccount.getClient(), null);

        when(accountRepository.findByAccountNumber(transferRequest.senderAccountNumber()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber()))
                .thenReturn(Optional.of(receiverAccount));

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> transferService.processBankTransfer(transferRequest, authenticatedUser));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransfer")
    void shouldThrowRestExceptionWhenSenderAccountNotFound(Account senderAccount) {
        TransferRequest transferRequest = new TransferRequest("PL123456789", "EN123",
                BigDecimal.valueOf(10), "Money for nothing");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        when(accountRepository.findByAccountNumber(transferRequest.senderAccountNumber()))
                .thenThrow(new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> transferService.processBankTransfer(transferRequest, authenticatedUser));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransfer")
    void shouldThrowRestExceptionWhenSenderBalanceIsInsufficient(Account senderAccount, Account receiverAccount,
                                                                 TransferHistory senderHistory,
                                                                 TransferHistory receiverHistory) {
        TransferRequest transferRequest = new TransferRequest("PL123456789", "EN123",
                BigDecimal.valueOf(500), "Money for nothing");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        Rates rateUsd = new Rates("2", "USD", BigDecimal.valueOf(4.29));
        List<Rates> rateUsdList = List.of(rateUsd);
        RateResponse rateResponseUsd = new RateResponse("A", "US Dollar", "USD", rateUsdList);

        senderAccount.setAccountNumber(transferRequest.senderAccountNumber());
        receiverAccount.setAccountNumber(transferRequest.receiverAccountNumber());
        senderHistory.setAccountNumber(transferRequest.senderAccountNumber());
        receiverHistory.setAccountNumber(transferRequest.receiverAccountNumber());

        when(accountRepository.findByAccountNumber(transferRequest.senderAccountNumber()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber()))
                .thenReturn(Optional.of(receiverAccount));
        when(rateClient.getCurrencyRate(Currency.USD)).thenReturn(rateResponseUsd);

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> transferService.processBankTransfer(transferRequest, authenticatedUser));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransfer")
    void shouldProcessBankTransferWhenAccountsAndBalanceAreValid(Account senderAccount, Account receiverAccount,
                                                                 TransferHistory senderHistory,
                                                                 TransferHistory receiverHistory) {
        TransferRequest transferRequest = new TransferRequest("PL123456789", "EN123",
                BigDecimal.valueOf(73.21), "Money for nothing");
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(senderAccount.getClient(), null);

        senderAccount.setAccountNumber(transferRequest.senderAccountNumber());
        receiverAccount.setAccountNumber(transferRequest.receiverAccountNumber());

        Rates rateUsd = new Rates("2", "USD", BigDecimal.valueOf(4.0944));
        List<Rates> rateUsdList = List.of(rateUsd);
        RateResponse rateResponseUsd = new RateResponse("A", "US Dollar", "USD", rateUsdList);

        senderAccount.setAccountNumber(transferRequest.senderAccountNumber());
        receiverAccount.setAccountNumber(transferRequest.receiverAccountNumber());

        senderHistory.setAccountNumber(transferRequest.senderAccountNumber());
        receiverHistory.setAccountNumber(transferRequest.receiverAccountNumber());
        List<TransferHistory> transferHistoryList = List.of(senderHistory, receiverHistory);

        when(accountRepository.findByAccountNumber(transferRequest.senderAccountNumber()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber()))
                .thenReturn(Optional.of(receiverAccount));
        when(accountRepository.saveAll(List.of(senderAccount, receiverAccount)))
                .thenReturn(List.of(senderAccount, receiverAccount));
        when(transferHistoryRepository.saveAll(anyList())).thenReturn(transferHistoryList);

        when(rateClient.getCurrencyRate(Currency.USD)).thenReturn(rateResponseUsd);

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        verify(accountRepository).findByAccountNumber(transferRequest.senderAccountNumber());
        verify(accountRepository).findByAccountNumber(transferRequest.receiverAccountNumber());

        verify(accountRepository).saveAll(accountsCaptor.capture());
        List<Account> savedAccounts = accountsCaptor.getValue();
        TransferServiceAssert.assertThat().hasValidAccounts(savedAccounts, transferRequest);

        verify(transferHistoryRepository).saveAll(historiesCaptor.capture());
        List<TransferHistory> savedHistories = historiesCaptor.getValue();
        TransferServiceAssert.assertThat().hasValidTransferHistories(savedHistories, transferRequest, senderAccount,
                receiverAccount);
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.SourceMethodsForTransferTest#argumentsForTransfer")
    void shouldProcessBankTransferWhenCurrenciesAreTheEqualAndUserIsAdmin(Account senderAccount, Account receiverAccount,
                                                                          TransferHistory senderHistory,
                                                                          TransferHistory receiverHistory) {
        TransferRequest transferRequest = new TransferRequest("PL123456789", "EN123",
                BigDecimal.valueOf(48.92), "Money for nothing");
        Client admin = receiverAccount.getClient();
        admin.setRole(Role.ADMIN);
        Principal authenticatedUser = new UsernamePasswordAuthenticationToken(admin, null);

        senderAccount.setCurrency(Currency.CHF);
        senderAccount.setAccountNumber(transferRequest.senderAccountNumber());
        receiverAccount.setCurrency(Currency.CHF);
        receiverAccount.setAccountNumber(transferRequest.receiverAccountNumber());
        senderHistory.setAccountNumber(transferRequest.senderAccountNumber());
        receiverHistory.setAccountNumber(transferRequest.receiverAccountNumber());

        when(accountRepository.findByAccountNumber(transferRequest.senderAccountNumber()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber()))
                .thenReturn(Optional.of(receiverAccount));
        when(accountRepository.saveAll(anyList())).thenReturn(List.of(senderAccount, receiverAccount));
        when(transferHistoryRepository.saveAll(anyList())).thenReturn(List.of(senderHistory, receiverHistory));

        transferService.processBankTransfer(transferRequest, authenticatedUser);

        verify(rateClient, never()).getCurrencyRate(any());
        verify(accountRepository).findByAccountNumber(transferRequest.senderAccountNumber());
        verify(accountRepository).findByAccountNumber(transferRequest.receiverAccountNumber());
        verify(accountRepository).saveAll(anyList());
        verify(transferHistoryRepository).saveAll(anyList());
    }
}
