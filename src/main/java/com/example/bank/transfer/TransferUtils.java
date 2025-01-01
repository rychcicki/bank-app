package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
class TransferUtils {
    static TransferHistory buildSenderAccountTransferHistory(Account account, BigDecimal amount,
                                                             Account externalAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance().add(amount))
                .balance(account.getBalance())
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .createdOn(LocalDateTime.now())
                .build();
    }

    static TransferHistory buildReceiverAccountTransferHistory(Account account, BigDecimal amount,
                                                               Account externalAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.INCOME)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance().subtract(amount))
                .balance(account.getBalance())
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .createdOn(LocalDateTime.now())
                .build();
    }

    static BigDecimal extractCurrencyExchangeRate(Currency currency, RateClient rateClient) {
        return currency == Currency.PLN ? BigDecimal.ONE :
                rateClient.getCurrencyRate(currency).rates().get(0).mid();
    }

    static void logTransferDetails(BigDecimal amount, Account sender, Account receiver) {
        log.info("Transfer of {} {} from account {} to account {} completed successfully. " +
                        "Sender's balance: {} {}, Receiver's balance: {} {}",
                amount, sender.getCurrency(), sender.getAccountNumber(), receiver.getAccountNumber(),
                sender.getBalance().setScale(2, RoundingMode.HALF_EVEN), sender.getCurrency(),
                receiver.getBalance().setScale(2, RoundingMode.HALF_EVEN), receiver.getCurrency());
    }
}
