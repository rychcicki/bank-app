package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.transfer.TransferType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class TransferHistoryService {
    private final AccountRepository accountRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    @NotNull
    public List<TransferHistory> transferHistoryForAccountNumber(String accountNumber) {
        return transferHistoryRepository.findByAccountNumber(accountNumber);
    }

    public TransferHistory buildSenderAccountTransferHistory(Account account, BigDecimal amount,
                                                             Account externalAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance())
                .balance(account.getBalance())
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .build();
    }

    public TransferHistory buildReceiverAccountTransferHistory(Account account, BigDecimal amount,
                                                               Account externalAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.INCOME)
                .clientId(account.getClient().getId())
                .previousBalance(account.getBalance())
                .balance(account.getBalance())
                .amount(amount)
                .accountNumber(account.getAccountNumber())
                .externalAccountNumber(externalAccount.getAccountNumber())
                .title(title)
                .build();
    }
}
