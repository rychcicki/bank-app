package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TransferServiceUtils {

    static Account createSenderAccount() {
        Client client = new Client("Adam", "Malysz", LocalDate.now().minusYears(18),
                "adam.malusz@gmail.com", "password",
                new Address("Polanska", "102", "33-450", "Ustron"));
        client.setId(5L);

        Account senderAccount = new Account();
        senderAccount.setId(2L);
        senderAccount.setAccountNumber("000");
        senderAccount.setType(AccountType.CURRENT_ACCOUNT);
        senderAccount.setCurrency(Currency.PLN);
        senderAccount.setBalance(BigDecimal.valueOf(500));
        senderAccount.setClient(client);
        return senderAccount;
    }

    static Account createReceiverAccount() {
        Client client = new Client("Michal", "Listkiewicz",
                LocalDate.now().minusYears(28), "michal.listkiewicz@gmail.com", "anypassword",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"));
        client.setId(7L);

        Account receiverAccount = new Account();
        receiverAccount.setId(3L);
        receiverAccount.setAccountNumber("888");
        receiverAccount.setType(AccountType.CURRENT_ACCOUNT);
        receiverAccount.setCurrency(Currency.USD);
        receiverAccount.setBalance(BigDecimal.valueOf(200));
        receiverAccount.setClient(client);
        return receiverAccount;
    }

    static TransferHistory senderHistory() {
        return TransferHistory
                .builder()
                .transferType(TransferType.EXPENSE)
                .accountNumber(createSenderAccount().getAccountNumber())
                .externalAccountNumber(createReceiverAccount().getAccountNumber())
                .createdOn(LocalDateTime.of(1987, 10, 5, 13, 10, 34))
                .build();
    }

    static TransferHistory receiverHistory() {
        return TransferHistory
                .builder()
                .transferType(TransferType.INCOME)
                .accountNumber(createReceiverAccount().getAccountNumber())
                .externalAccountNumber(createSenderAccount().getAccountNumber())
                .createdOn(LocalDateTime.of(1992, 8, 12, 15, 20, 7))
                .build();
    }
}
