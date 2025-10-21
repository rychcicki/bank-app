package com.example.bank.transfer.export;

import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ExportToXlsxUtils {
    static List<TransferHistory> transferHistoryCreator() {
        final String accountNumber = "PL54613983300568639363795256";
        TransferHistory transferHistory1 = TransferHistory
                .builder()
                .transferType(TransferType.EXPENSE)
                .externalAccountNumber(accountNumber)
                .title("Money for nothing")
                .amount(BigDecimal.valueOf(152.25))
                .balance(BigDecimal.valueOf(100.47))
                .createdOn(LocalDateTime.of(2024, 10, 5, 13, 10, 34))
                .build();

        TransferHistory transferHistory2 = TransferHistory
                .builder()
                .transferType(TransferType.INCOME)
                .externalAccountNumber(accountNumber)
                .title("Just transfer")
                .amount(BigDecimal.valueOf(812.85))
                .balance(BigDecimal.valueOf(999.10))
                .createdOn(LocalDateTime.of(2024, 8, 12, 15, 20, 7))
                .build();

        TransferHistory transferHistory3 = TransferHistory
                .builder()
                .transferType(TransferType.EXPENSE)
                .externalAccountNumber(accountNumber)
                .title("Lorem ipsum")
                .amount(BigDecimal.valueOf(10.72))
                .balance(BigDecimal.valueOf(3.0))
                .createdOn(LocalDateTime.of(2024, 2, 1, 10, 0, 0))
                .build();

        return List.of(transferHistory1, transferHistory2, transferHistory3);
    }
}
