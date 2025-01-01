package com.example.bank.transfer;

import com.example.bank.account.AccountRepository;
import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.model.TransferHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

import static com.example.bank.transfer.TransferUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransferHistoryRepository transferHistoryRepository;
    private final RateClient rateClient;

    void processBankTransfer(TransferRequest transferRequest, Principal authenticatedUser) {
        Client client = (Client) ((UsernamePasswordAuthenticationToken) authenticatedUser).getPrincipal();

        Account senderAccount = accountRepository.findByAccountNumber(transferRequest.senderAccountNumber())
                .orElseThrow(() -> new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));
        Account receiverAccount = accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber())
                .orElseThrow(() -> new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));

        Long senderId = senderAccount.getClient().getId();
        if (!senderId.equals(client.getId()) && client.getRole() != Role.ADMIN) {
            throw new RestException(ExceptionType.INVALID_REQUEST_EXCEPTION);
        }
        BigDecimal receiverAmount = transferRequest.amount();
        validateAndExecuteTransfer(senderAccount, receiverAccount, receiverAmount, transferRequest.title());
    }

    private void validateAndExecuteTransfer(Account sender, Account receiver, BigDecimal receiverAmount, String title) {
        BigDecimal senderAmount = exchangeAmount(sender.getCurrency(), receiver.getCurrency(), receiverAmount);
        if (sender.getBalance().compareTo(senderAmount) < 0) {
            log.error("You don't have enough money to transfer. You have {} {}. You need at least {} {}",
                    sender.getBalance(), sender.getCurrency(), senderAmount, sender.getCurrency());
            throw new RestException(ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION);
        }
        updateAndSaveBalances(sender, receiver, senderAmount, receiverAmount);
        createAndSaveTransferHistories(sender, receiver, receiverAmount, title, senderAmount);
        logTransferDetails(senderAmount, sender, receiver);
    }

    private void updateAndSaveBalances(Account sender, Account receiver, BigDecimal senderAmount,
                                       BigDecimal receiverAmount) {
        sender.setBalance(sender.getBalance().subtract(senderAmount));
        receiver.setBalance(receiver.getBalance().add(receiverAmount));
        List<Account> senderAndReceiverAccounts = List.of(sender, receiver);
        accountRepository.saveAll(senderAndReceiverAccounts);
    }

    private void createAndSaveTransferHistories(Account sender, Account receiver, BigDecimal receiverAmount,
                                                String title, BigDecimal senderAmount) {
        TransferHistory senderHistory = buildSenderAccountTransferHistory(sender, senderAmount, receiver, title);
        TransferHistory receiverHistory = buildReceiverAccountTransferHistory(receiver, receiverAmount, sender, title);
        List<TransferHistory> transferHistoryList = List.of(senderHistory, receiverHistory);
        transferHistoryRepository.saveAll(transferHistoryList);
    }

    private BigDecimal exchangeAmount(Currency senderCurrency, Currency receiverCurrency, BigDecimal senderAmount) {
        if (senderCurrency == receiverCurrency) {
            return senderAmount;
        }
        BigDecimal senderRate = extractCurrencyExchangeRate(senderCurrency, rateClient);
        BigDecimal receiverRate = extractCurrencyExchangeRate(receiverCurrency, rateClient);
        return senderAmount.multiply(receiverRate.divide(senderRate, 4, RoundingMode.HALF_EVEN))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
