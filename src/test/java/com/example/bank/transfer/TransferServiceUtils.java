package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.transfer.feign.RateResponse;
import com.example.bank.transfer.feign.Rates;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_EVEN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TransferServiceUtils {
    static final int SCALE = 2;

    private static final String SENDER_ACCOUNT_NUMBER = "DE11500105171841551884";
    private static final String RECEIVER_ACCOUNT_NUMBER = "GB92BARC20038472426896";
    private static final String DEFAULT_TRANSFER_TITLE = "Integration test for transferService";
    private static final String INVALID_ACCOUNT_NUMBER = "accountNotExist";

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(9.59).setScale(SCALE, HALF_EVEN);
    private static final BigDecimal SENDER_DEFAULT_BALANCE = BigDecimal.valueOf(500).setScale(SCALE, HALF_EVEN);
    private static final BigDecimal RECEIVER_DEFAULT_BALANCE = BigDecimal.valueOf(200).setScale(SCALE, HALF_EVEN);
    private static final BigDecimal INSUFFICIENT_BALANCE_AMOUNT = BigDecimal.valueOf(1000).setScale(SCALE, HALF_EVEN);

    private static final BigDecimal USD_RATE = BigDecimal.valueOf(3.7796);
    private static final BigDecimal AUD_RATE = BigDecimal.valueOf(2.4526);
    private static final BigDecimal EUR_RATE = BigDecimal.valueOf(4.2933);
    private static final BigDecimal CHF_RATE = BigDecimal.valueOf(4.6221);
    private static final BigDecimal GBP_RATE = BigDecimal.valueOf(4.9893);
    private static final BigDecimal NOK_RATE = BigDecimal.valueOf(0.3925);

    static final Map<Currency, BigDecimal> RATES;

    static {
        final Map<Currency, BigDecimal> tmp = new EnumMap<>(Currency.class);
        tmp.put(Currency.USD, USD_RATE);
        tmp.put(Currency.AUD, AUD_RATE);
        tmp.put(Currency.EUR, EUR_RATE);
        tmp.put(Currency.CHF, CHF_RATE);
        tmp.put(Currency.GBP, GBP_RATE);
        tmp.put(Currency.NOK, NOK_RATE);
        RATES = Map.copyOf(tmp);
    }

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

    static Account createSenderAccount(Currency currency) {
        Client client = new Client("Adam", "Malysz", LocalDate.of(2002, 8, 9),
                "adam.malusz@gmail.com", "password",
                new Address("Polanska", "102", "33-450", "Ustron"));
        client.setId(5L);
        client.setRole(Role.USER);

        Account senderAccount = new Account();
        senderAccount.setId(2L);
        senderAccount.setAccountNumber(SENDER_ACCOUNT_NUMBER);
        senderAccount.setType(AccountType.CURRENT_ACCOUNT);
        senderAccount.setCurrency(currency);
        senderAccount.setBalance(SENDER_DEFAULT_BALANCE);
        senderAccount.setClient(client);
        return senderAccount;
    }

    static Account createReceiverAccount(Currency currency) {
        Client client = new Client("Michal", "Listkiewicz",
                LocalDate.of(1971, 2, 20), "michal.listkiewicz@gmail.com", "anypassword",
                new Address("Ku Ujsciu", "1", "67-890", "Gdynia"));
        client.setId(7L);
        client.setRole(Role.MANAGER);

        Account receiverAccount = new Account();
        receiverAccount.setId(3L);
        receiverAccount.setAccountNumber(RECEIVER_ACCOUNT_NUMBER);
        receiverAccount.setType(AccountType.CURRENT_ACCOUNT);
        receiverAccount.setCurrency(currency);
        receiverAccount.setBalance(RECEIVER_DEFAULT_BALANCE);
        receiverAccount.setClient(client);
        return receiverAccount;
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

    private static TransferHistory buildTransferHistory(Account account, BigDecimal amount,
                                                        Account externalAccount, String title,
                                                        TransferType transferType, BigDecimal balance) {
        return TransferHistory.builder()
                .transferType(transferType)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance().setScale(SCALE, HALF_EVEN))
                .balance(balance.setScale(SCALE, HALF_EVEN))
                .amount(amount.setScale(SCALE, HALF_EVEN))
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

    static RateResponse createRateResponse(Currency currency) {
        BigDecimal mid = RATES.get(currency);
        if (mid == null) {
            throw new IllegalArgumentException("No mocked rate for currency " + currency);
        }
        return new RateResponse("A", currency.name(), currency.name(),
                List.of(new Rates("0", currency.name(), mid)));
    }
}
