package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.account.Currency;
import com.example.bank.bankTransfer.feign.RateClient;
import com.example.bank.bankTransfer.transfer.history.TransferHistory;
import com.example.bank.bankTransfer.transfer.history.TransferHistoryRepository;
import com.example.bank.bankTransfer.transfer.history.TransferHistoryService;
import lombok.Getter;
import lombok.NonNull;
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

    public void bankTransfer(TransferRequest transferRequest) {
        TransferValidationUtils transferValidationUtils = new TransferValidationUtils();
        @NonNull Long senderAccountId = transferRequest.senderAccountId();
        @NonNull Long receiverAccountId = transferRequest.receiverAccountId();
        @NonNull BigDecimal amount = transferRequest.amount();
        Account senderAccount = accountRepository.findById(senderAccountId).orElseThrow();
        Account receiverAccount = accountRepository.findById(receiverAccountId).orElseThrow();
        String senderAccountCountryCode = senderAccount.getAccountNumber().substring(0, 2);
        String receiverAccountCountryCode = receiverAccount.getAccountNumber().substring(0, 2);
        Currency senderAccountCurrency = senderAccount.getCurrency();
        Currency receiverAccountCurrency = receiverAccount.getCurrency();
        String title = transferRequest.title();

        transferValidationUtils.amountValidation(amount);
        BigDecimal balance;
        boolean isDomesticSameCurrencyTransfer = senderAccountCountryCode.equals(receiverAccountCountryCode)
                && senderAccountCurrency.equals(receiverAccountCurrency);
        if (isDomesticSameCurrencyTransfer) {
            balance = senderAccount.getBalance();
            transferValidationUtils.balanceValidation(balance, senderAccountCurrency, amount);
            transfer(senderAccount, receiverAccount, amount, title);
        } else {
            String transferType = senderAccountCountryCode.equals(receiverAccountCountryCode) ? "Different currencies."
                    : "Foreign transfer.";
            log.info(transferType);
            transferInDifferentCurrencies(senderAccountCurrency, receiverAccountCurrency, amount, senderAccount,
                    receiverAccount, transferValidationUtils, title);
        }
    }

    private void transferInDifferentCurrencies(Currency senderAccountCurrency, Currency receiverAccountCurrency,
                                               BigDecimal amount, Account senderAccount, Account receiverAccount,
                                               TransferValidationUtils transferValidationUtils, String title) {
        BigDecimal amountOfTransferCurrency = exchangeAmount(senderAccountCurrency, receiverAccountCurrency, amount);
        BigDecimal balance = senderAccount.getBalance();
        transferValidationUtils.balanceValidation(balance, senderAccountCurrency, amountOfTransferCurrency);
        transfer(senderAccount, receiverAccount, amount, amountOfTransferCurrency, title);
    }

    BigDecimal exchangeAmount(Currency senderAccountCurrency, Currency receiverAccountCurrency, BigDecimal amount) {
        BigDecimal senderAccountCurrencyExchangeRate = extractCurrencyExchangeRate(senderAccountCurrency);
        BigDecimal receiverAccountCurrencyExchangeRate = extractCurrencyExchangeRate(receiverAccountCurrency);
        BigDecimal exchangeRate = senderAccountCurrencyExchangeRate.divide(receiverAccountCurrencyExchangeRate,
                4, RoundingMode.UP);
        return amount.divide(exchangeRate, 2, RoundingMode.UP);
    }

    public void transfer(Account senderAccount, Account receiverAccount, BigDecimal amount, String title) {
        TransferHistory senderAccountTransferHistory =
                transferHistoryService.buildSenderAccountTransferHistory(senderAccount, amount, receiverAccount, title);
        TransferHistory receiverAccountTransferHistory =
                transferHistoryService.buildReceiverAccountTransferHistory(receiverAccount, amount, senderAccount, title);

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));

        senderAccountTransferHistory.setBalance(senderAccount.getBalance());
        receiverAccountTransferHistory.setBalance(receiverAccount.getBalance());
        transferHistoryRepository.saveAll(List.of(senderAccountTransferHistory, receiverAccountTransferHistory));

        log.info("Bank transfer for {} {} has done. Sender account's balance: {} {}, receiver account's balance: {} {}.",
                amount, receiverAccount.getCurrency(), senderAccount.getBalance(), senderAccount.getCurrency(),
                receiverAccount.getBalance(), receiverAccount.getCurrency());
    }

    public void transfer(Account senderAccount, Account receiverAccount, BigDecimal amount,
                         BigDecimal amountOfTransferCurrency, String title) {
        TransferHistory senderAccountTransferHistory =
                transferHistoryService.buildSenderAccountTransferHistory(senderAccount, amountOfTransferCurrency,
                        receiverAccount, title);
        TransferHistory receiverAccountTransferHistory =
                transferHistoryService.buildReceiverAccountTransferHistory(receiverAccount, amount, senderAccount, title);

        senderAccount.setBalance(senderAccount.getBalance().subtract(amountOfTransferCurrency));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));

        senderAccountTransferHistory.setBalance(senderAccount.getBalance());
        receiverAccountTransferHistory.setBalance(receiverAccount.getBalance());
        transferHistoryRepository.saveAll(List.of(senderAccountTransferHistory, receiverAccountTransferHistory));

        log.info("Bank transfer for {} {} has done. Sender account's balance: {} {}, receiver account's balance: {} {}.",
                amount, receiverAccount.getCurrency(), senderAccount.getBalance(), senderAccount.getCurrency(),
                receiverAccount.getBalance(), receiverAccount.getCurrency());
    }

    public BigDecimal extractCurrencyExchangeRate(Currency currency) {
        if (currency.equals(Currency.PLN)) {
            return BigDecimal.ONE;
        }
        BigDecimal mid = rateClient
                .getCurrencyRate(currency)
                .getRates()
                .get(0)
                .getMid();
        log.info("{} rate: {}", currency, mid);
        return mid;
    }
}
