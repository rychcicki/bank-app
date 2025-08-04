package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TransferServiceITUtils {
    private static final String SENDER_ACCOUNT_NUMBER = "DE11500105171841551884";
    private static final String INVALID_ACCOUNT_NUMBER = "accountNotExist";
    private static final String RECEIVER_ACCOUNT_NUMBER = "GB92BARC20038472426896";
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(9.59);
    private static final BigDecimal INSUFFICIENT_BALANCE_AMOUNT = BigDecimal.valueOf(1000.00);
    private static final String DEFAULT_TRANSFER_TITLE = "Integration test for transferService";

    static TransferRequest createTransferRequest() {
        return new TransferRequest(SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, DEFAULT_AMOUNT,
                DEFAULT_TRANSFER_TITLE);
    }

    static TransferRequest createTransferRequestInsufficientBalance() {
        return new TransferRequest(SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, INSUFFICIENT_BALANCE_AMOUNT,
                DEFAULT_TRANSFER_TITLE);
    }

    static TransferRequest createTransferRequestAccountNotExist() {
        return new TransferRequest(INVALID_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, DEFAULT_AMOUNT,
                DEFAULT_TRANSFER_TITLE);
    }

    static Client defaultAdmin() {
        Client authenticatedAdmin = new Client("Mike", "Wazowski",
                LocalDate.of(1980, 1, 28), "mike.wazowski@gmail.com", "password",
                new Address("MonstersEnc", "10", "00-888", "Monsters"));
        authenticatedAdmin.setId(1L);
        authenticatedAdmin.setRole(Role.ADMIN);
        return authenticatedAdmin;
    }

    static Client defaultUnauthorizedClient() {
        Client unauthorizedClient = new Client("Someone", "Stranger",
                LocalDate.of(1999, 7, 11), "someone@gmail.com", "anypassword",
                new Address("Somewhere", "90", "00-911", "Somewhere"));
        unauthorizedClient.setId(4L);
        unauthorizedClient.setRole(Role.USER);
        return unauthorizedClient;
    }

    static TransferHistory buildTransferHistory(Account account, BigDecimal amount,
                                                Account externalAccount, String title,
                                                TransferType transferType, BigDecimal balance) {
        return TransferHistory.builder()
                .transferType(transferType)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance())
                .balance(balance)
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .build();
    }

    static TransferHistory buildExpectedSenderTransferHistory(TransferRequest transferRequest,
                                                              Account senderAccount, Account receiverAccount,
                                                              BigDecimal senderAmountDelta) {
        BigDecimal senderBalance = senderAccount.getBalance().subtract(senderAmountDelta);

        return buildTransferHistory(senderAccount, senderAmountDelta, receiverAccount, transferRequest.title(),
                TransferType.EXPENSE, senderBalance);

    }

    static TransferHistory buildExpectedReceiverTransferHistory(TransferRequest transferRequest,
                                                                Account receiverAccount, Account senderAccount) {
        BigDecimal receiverBalance = receiverAccount.getBalance().add(transferRequest.amount());

        return buildTransferHistory(receiverAccount, transferRequest.amount(), senderAccount, transferRequest.title(),
                TransferType.INCOME, receiverBalance);
    }
}
