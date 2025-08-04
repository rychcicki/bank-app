package com.example.bank.transfer;

import com.example.bank.account.AccountService;
import com.example.bank.account.model.Account;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.feign.RateClient;
import com.example.bank.transfer.model.TransferType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bank.transfer.TransferUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final TransferHistoryRepository transferHistoryRepository;
    private final RateClient rateClient;
    private final AccountService accountService;
    private final ClientService clientServiceImpl;

    @Transactional
    void processBankTransfer(TransferRequest transferRequest, Client client) {
        Account senderAccount = accountService.findAccount(transferRequest.senderAccountNumber());
        Account receiverAccount = accountService.findAccount(transferRequest.receiverAccountNumber());
        authorizeClient(client, senderAccount);

        BigDecimal receiverAmount = transferRequest.amount();
        BigDecimal senderAmountDelta = calculateSenderAmountDelta(senderAccount, receiverAccount, receiverAmount);
        BigDecimal senderPreviousBalance = senderAccount.getBalance();
        BigDecimal receiverPreviousBalance = receiverAccount.getBalance();

        senderAccount.setBalance(senderPreviousBalance.subtract(senderAmountDelta));
        receiverAccount.setBalance(receiverPreviousBalance.add(receiverAmount));
        accountService.saveAll(List.of(senderAccount, receiverAccount));

        createTransferHistory(senderAccount, receiverAccount, senderAmountDelta, receiverAmount,
                transferRequest.title(), senderPreviousBalance, receiverPreviousBalance);
        logTransferDetails(senderAmountDelta, senderAccount, receiverAccount);
    }

    private void authorizeClient(Client authClient, Account senderAccount) {
        Client clientFromDb = clientServiceImpl.findClient(authClient.getId());
        if (clientFromDb.getRole() == Role.ADMIN) {
            return;
        }

        Long senderId = senderAccount.getClient().getId();
        if (!senderId.equals(clientFromDb.getId())) {
            throw new RestException(ExceptionType.INVALID_REQUEST_EXCEPTION);
        }
    }

    private BigDecimal calculateSenderAmountDelta(Account senderAccount, Account receiverAccount,
                                                  BigDecimal receiverAmount) {
        BigDecimal senderAmountDelta =
                exchangeAmount(senderAccount.getCurrency(), receiverAccount.getCurrency(), receiverAmount, rateClient);
        if (senderAccount.getBalance().compareTo(senderAmountDelta) < 0) {
            log.error("You don't have enough money to transfer. You have {} {}. You need at least {} {}",
                    senderAccount.getBalance(), senderAccount.getCurrency(),
                    senderAmountDelta, senderAccount.getCurrency());
            throw new RestException(ExceptionType.BALANCE_INSUFFICIENT_EXCEPTION);
        }
        return senderAmountDelta;
    }

    private void createTransferHistory(Account senderAccount, Account receiverAccount,
                                       BigDecimal senderAmount, BigDecimal receiverAmount, String title,
                                       BigDecimal senderPreviousBalance, BigDecimal receiverPreviousBalance) {
        transferHistoryRepository.saveAll(List.of(
                buildTransferHistory(senderAccount, senderAmount, receiverAccount, title, TransferType.EXPENSE,
                        senderPreviousBalance),
                buildTransferHistory(receiverAccount, receiverAmount, senderAccount, title, TransferType.INCOME,
                        receiverPreviousBalance)));
    }
}
