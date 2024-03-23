package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.transfer.TransferType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class TransferHistoryService {
    private final AccountRepository accountRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    public List<TransferHistory> transferHistoryForClient(Long clientId) {
        return transferHistoryRepository.findByClientId(clientId);
    }

    public TransferHistory buildSenderAccountTransferHistory(Account senderAccount, BigDecimal amount,
                                                             Account receiverAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(senderAccount.getClient().getId())
                .previousBalance(senderAccount.getBalance())
                .balance(senderAccount.getBalance())
                .amount(amount)
                .bankAccountId(receiverAccount.getId())
                .title(title)
                .build();
    }

    public TransferHistory buildReceiverAccountTransferHistory(Account receiverAccount, BigDecimal amount,
                                                               Account senderAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.INCOME)
                .clientId(receiverAccount.getClient().getId())
                .previousBalance(receiverAccount.getBalance())
                .balance(receiverAccount.getBalance())
                .amount(amount)
                .bankAccountId(senderAccount.getId())
                .title(title)
                .build();
    }
}
