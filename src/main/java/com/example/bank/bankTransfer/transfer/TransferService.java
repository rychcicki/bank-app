package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.account.Currency;
import com.example.bank.bankTransfer.feign.RateClient;
import com.example.bank.bankTransfer.transfer.history.TransferHistory;
import com.example.bank.bankTransfer.transfer.history.TransferHistoryRepository;
import com.example.bank.bankTransfer.transfer.history.TransferHistoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class TransferService {
    private final TransferHistoryService transferHistoryService;
    private final TransferHistoryRepository transferHistoryRepository;
    private final AccountRepository accountRepository;
    private final RateClient rateClient;
    private final TransferValidationUtils transferValidationUtils;
    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";

    public void bankTransfer(TransferRequest transferRequest) {
        Account senderAccount = accountRepository.findByAccountNumber(transferRequest.senderAccountNumber())
                .orElseThrow(() -> new IllegalStateException(ACCOUNT_NOT_FOUND_MESSAGE));
        Account receiverAccount = accountRepository.findByAccountNumber(transferRequest.receiverAccountNumber())
                .orElseThrow(() -> new IllegalStateException(ACCOUNT_NOT_FOUND_MESSAGE));
        BigDecimal amount = transferRequest.amount();
        validateAndProcessTransfer(senderAccount, receiverAccount, amount, transferRequest.title());
    }

    private void validateAndProcessTransfer(Account sender, Account receiver, BigDecimal amount, String title) {
        transferValidationUtils.amountValidation(amount);
        if (isDomesticSameCurrencyTransfer(sender, receiver)) {
            transferValidationUtils.balanceValidation(sender.getBalance(), sender.getCurrency(), amount);
            transfer(sender, receiver, amount, title);
        } else {
            BigDecimal amountInReceiverCurrency = exchangeAmount(sender.getCurrency(), receiver.getCurrency(), amount);
            transferValidationUtils.balanceValidation(sender.getBalance(), sender.getCurrency(), amountInReceiverCurrency);
            transfer(sender, receiver, amount, amountInReceiverCurrency, title);
        }
    }

    private boolean isDomesticSameCurrencyTransfer(Account sender, Account receiver) {
        return sender.getAccountNumber().substring(0, 2).equals(receiver.getAccountNumber().substring(0, 2))
                && sender.getCurrency().equals(receiver.getCurrency());
    }

    private void transfer(Account sender, Account receiver, BigDecimal amount, String title) {
        updateBalancesAndSave(sender, receiver, amount);
        createAndSaveTransferHistories(sender, receiver, amount, title, amount);
        logTransferDetails(amount, receiver.getCurrency(), sender, receiver);
    }

    private void transfer(Account sender, Account receiver, BigDecimal senderAmount, BigDecimal receiverAmount,
                          String title) {
        updateBalancesAndSave(sender, receiver, receiverAmount);
        createAndSaveTransferHistories(sender, receiver, senderAmount, title, receiverAmount);
        logTransferDetails(senderAmount, receiver.getCurrency(), sender, receiver);
    }

    private void updateBalancesAndSave(Account sender, Account receiver, BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.saveAll(List.of(sender, receiver));
    }

    private void createAndSaveTransferHistories(Account sender, Account receiver, BigDecimal amount, String title,
                                                BigDecimal receiverAmount) {
        TransferHistory senderHistory =
                transferHistoryService.buildSenderAccountTransferHistory(sender, amount, receiver, title);
        TransferHistory receiverHistory =
                transferHistoryService.buildReceiverAccountTransferHistory(receiver, receiverAmount, sender, title);
        transferHistoryRepository.saveAll(List.of(senderHistory, receiverHistory));
    }

    private BigDecimal exchangeAmount(Currency senderCurrency, Currency receiverCurrency, BigDecimal amount) {
        BigDecimal exchangeRate = calculateExchangeRate(senderCurrency, receiverCurrency);
        return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateExchangeRate(Currency senderCurrency, Currency receiverCurrency) {
        BigDecimal senderRate = extractCurrencyExchangeRate(senderCurrency);
        BigDecimal receiverRate = extractCurrencyExchangeRate(receiverCurrency);
        return senderRate.divide(receiverRate, 4, RoundingMode.HALF_EVEN); //wow!! o tym nie wiedziałem
    }

    private BigDecimal extractCurrencyExchangeRate(Currency currency) {
        return currency.equals(Currency.PLN) ? BigDecimal.ONE :
                rateClient.getCurrencyRate(currency).getRates().get(0).getMid();
    }

    private void logTransferDetails(BigDecimal amount, Currency currency, Account sender, Account receiver) {
        log.info("Transfer of {} {} from account {} to account {} completed successfully. " +
                        "Sender balance: {}, Receiver balance: {}",
                amount, currency, sender.getId(), receiver.getId(), sender.getBalance(), receiver.getBalance());
    }
}
