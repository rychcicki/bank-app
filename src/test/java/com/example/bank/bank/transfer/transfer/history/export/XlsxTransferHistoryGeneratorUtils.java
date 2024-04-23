package com.example.bank.bank.transfer.transfer.history.export;

import com.example.bank.bank.transfer.transfer.TransferType;
import com.example.bank.bank.transfer.transfer.history.TransferHistory;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
class XlsxTransferHistoryGeneratorUtils {
    static List<TransferHistory> transferHistoryCreator() {
        TransferHistory transferHistory1 = TransferHistory
                .builder()
                .transferType(TransferType.EXPENSE)
                .externalAccountNumber("PL54613983300568639363795256")
                .title("Money for nothing")
                .amount(BigDecimal.valueOf(152.25))
                .balance(BigDecimal.valueOf(100.47))
                .build();
        transferHistory1.setCreatedOn(LocalDateTime.of(2024, 10, 5, 13, 10, 34));

        TransferHistory transferHistory2 = TransferHistory
                .builder()
                .transferType(TransferType.INCOME)
                .externalAccountNumber("PL54613983300568639363795256")
                .title("Just transfer")
                .amount(BigDecimal.valueOf(812.85))
                .balance(BigDecimal.valueOf(999.10))
                .build();
        transferHistory2.setCreatedOn(LocalDateTime.of(2024, 8, 12, 15, 20, 7));

        TransferHistory transferHistory3 = TransferHistory
                .builder()
                .transferType(TransferType.EXPENSE)
                .externalAccountNumber("PL54613983300568639363795256")
                .title("Lorem ipsum")
                .amount(BigDecimal.valueOf(10.72))
                .balance(BigDecimal.valueOf(3.0))
                .build();
        transferHistory3.setCreatedOn(LocalDateTime.of(2024, 2, 1, 10, 0, 0));

        return List.of(transferHistory1, transferHistory2, transferHistory3);
    }
}
