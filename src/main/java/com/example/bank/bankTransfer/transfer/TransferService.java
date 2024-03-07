package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.account.Currency;
import com.example.bank.bankTransfer.feign.RateClient;
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
    private final AccountRepository accountRepository;
    private final RateClient rateClient;

    public void bankTransfer(TransferRequest transferRequest) {
        TransferValidationUtils transferValidationUtils = new TransferValidationUtils();
        @NonNull Long fromAccountId = transferRequest.fromAccountId();
        @NonNull Long toAccountId = transferRequest.toAccountId();
        @NonNull BigDecimal amount = transferRequest.amount();
        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow();
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow();
        String fromAccountCountryCode = fromAccount.getAccountNumber().substring(0, 2);
        String toAccountCountryCode = toAccount.getAccountNumber().substring(0, 2);
        Currency fromAccountCurrency = fromAccount.getCurrency();
        Currency toAccountCurrency = toAccount.getCurrency();

        transferValidationUtils.amountValidation(amount);
        BigDecimal balance;
        boolean isDomesticSameCurrencyTransfer = fromAccountCountryCode.equals(toAccountCountryCode)
                && fromAccountCurrency.equals(toAccountCurrency);
        if (isDomesticSameCurrencyTransfer) {
            balance = fromAccount.getBalance();
            transferValidationUtils.balanceValidation(balance, fromAccountCurrency, amount);
            transfer(fromAccount, toAccount, amount);
        } else {
            String transferType = fromAccountCountryCode.equals(toAccountCountryCode) ? "Different currencies."
                    : "Foreign transfer.";
            log.info(transferType);
            transferInDifferentCurrencies(fromAccountCurrency, toAccountCurrency, amount, fromAccount, toAccount,
                    transferValidationUtils);
        }
    }

    private void transferInDifferentCurrencies(Currency fromAccountCurrency, Currency toAccountCurrency,
                                               BigDecimal amount, Account fromAccount, Account toAccount,
                                               TransferValidationUtils transferValidationUtils) {
        BigDecimal amountOfTransferCurrency = exchangeAmount(fromAccountCurrency, toAccountCurrency, amount);
        BigDecimal balance = fromAccount.getBalance();
        transferValidationUtils.balanceValidation(balance, fromAccountCurrency, amountOfTransferCurrency);
        transfer(fromAccount, toAccount, amount, amountOfTransferCurrency);
    }

    BigDecimal exchangeAmount(Currency fromAccountCurrency, Currency toAccountCurrency, BigDecimal amount) {
        BigDecimal fromAccountCurrencyExchangeRate = extractCurrencyExchangeRate(fromAccountCurrency);
        BigDecimal toAccountCurrencyExchangeRate = extractCurrencyExchangeRate(toAccountCurrency);
        BigDecimal exchangeRate = fromAccountCurrencyExchangeRate.divide(toAccountCurrencyExchangeRate,
                4, RoundingMode.UP);
        return amount.divide(exchangeRate, 2, RoundingMode.UP);
    }

    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.saveAll(List.of(fromAccount, toAccount));
        log.info("Bank transfer for {} {} has done. First account's balance: {} {}, second account's balance: {} {}.",
                amount, toAccount.getCurrency(), fromAccount.getBalance(), fromAccount.getCurrency(),
                toAccount.getBalance(), toAccount.getCurrency());
    }

    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount, BigDecimal amountOfTransferCurrency) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amountOfTransferCurrency));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.saveAll(List.of(fromAccount, toAccount));
        log.info("Bank transfer for {} {} has done. First account's balance: {} {}, second account's balance: {} {}.",
                amount, toAccount.getCurrency(), fromAccount.getBalance(), fromAccount.getCurrency(),
                toAccount.getBalance(), toAccount.getCurrency());
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
