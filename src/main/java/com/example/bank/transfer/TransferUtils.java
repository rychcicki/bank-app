package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.feign.RateResponse;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
final class TransferUtils {
    static TransferHistory buildTransferHistory(Account account, BigDecimal amount,
                                                Account externalAccount, String title,
                                                TransferType transferType, BigDecimal previousBalance) {
        requireNonNull(account, "account cannot be null");
        requireNonNull(amount, "amount cannot be null");
        requireNonNull(externalAccount, "externalAccount cannot be null");
        requireNonNull(title, "title cannot be null");
        requireNonNull(transferType, "transferType cannot be null");

        return TransferHistory.builder()
                .transferType(transferType)
                .clientId(account.getClient().getId())
                .previousBalance(previousBalance)
                .balance(account.getBalance())
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .createdOn(LocalDateTime.now())
                .build();
    }

    static BigDecimal exchangeAmount(Currency senderCurrency, Currency receiverCurrency, BigDecimal receiverAmount,
                                     RateClient rateClient) {
        requireNonNull(senderCurrency, "sender currency cannot be null");
        requireNonNull(receiverCurrency, "receiver currency cannot be null");
        requireNonNull(receiverAmount, "receiver amount cannot be null");
        requireNonNull(rateClient, "rateClient cannot be null");

        if (senderCurrency == receiverCurrency) {
            return receiverAmount;
        }
        BigDecimal senderRate = extractCurrencyExchangeRate(senderCurrency, rateClient);
        BigDecimal receiverRate = extractCurrencyExchangeRate(receiverCurrency, rateClient);
        return receiverAmount
                .multiply(receiverRate.divide(senderRate, 4, RoundingMode.HALF_EVEN))
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal extractCurrencyExchangeRate(Currency currency, RateClient rateClient) {
        if (currency == Currency.PLN) {
            return BigDecimal.ONE;
        }

        return Optional.ofNullable(rateClient.getCurrencyRate(currency))
                .map(RateResponse::rates)
                .filter(rates -> !rates.isEmpty())
                .map(list -> list.getFirst().mid())
                .filter(rate -> rate.signum() > 0)
                .orElseThrow(() -> new RestException(ExceptionType.EXCHANGE_RATE_NOT_AVAILABLE_EXCEPTION));
    }

    static void logTransferDetails(BigDecimal amount, Account sender, Account receiver) {
        requireNonNull(amount, "amount cannot be null");
        requireNonNull(sender, "sender cannot be null");
        requireNonNull(receiver, "receiver cannot be null");

        BigDecimal senderBalance = sender.getBalance().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal receiverBalance = receiver.getBalance().setScale(2, RoundingMode.HALF_EVEN);

        log.info("Transfer of {} {} from account {} to account {} completed successfully. " +
                        "Sender's balance: {} {}, Receiver's balance: {} {}",
                amount, sender.getCurrency(),
                sender.getAccountNumber(), receiver.getAccountNumber(),
                senderBalance, sender.getCurrency(),
                receiverBalance, receiver.getCurrency());
    }
}
