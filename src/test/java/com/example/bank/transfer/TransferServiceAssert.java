package com.example.bank.transfer;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.Currency;
import com.example.bank.transfer.model.TransferHistory;
import com.example.bank.transfer.model.TransferType;
import org.assertj.core.api.SoftAssertions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TransferServiceAssert {
    BigDecimal expectedSenderBalance = BigDecimal.valueOf(200.25);
    BigDecimal expectedReceiverBalance = BigDecimal.valueOf(273.21);
    BigDecimal expectedSenderAmount = BigDecimal.valueOf(299.75);

    static TransferServiceAssert assertThat() {
        return new TransferServiceAssert();
    }

    TransferServiceAssert hasValidAccounts(List<Account> savedAccounts, TransferRequest transferRequest) {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(savedAccounts.size()).isEqualTo(2);
            softly.assertThat(savedAccounts.get(0).getBalance()).isEqualTo(expectedSenderBalance);
            softly.assertThat(savedAccounts.get(0).getAccountNumber()).isEqualTo(transferRequest.senderAccountNumber());
            softly.assertThat(savedAccounts.get(0).getCurrency()).isEqualTo(Currency.PLN);

            softly.assertThat(savedAccounts.get(1).getBalance()).isEqualTo(expectedReceiverBalance);
            softly.assertThat(savedAccounts.get(1).getAccountNumber()).isEqualTo(transferRequest.receiverAccountNumber());
            softly.assertThat(savedAccounts.get(1).getCurrency()).isEqualTo(Currency.USD);
        });
        return this;
    }

    TransferServiceAssert hasValidTransferHistories(List<TransferHistory> savedHistories, TransferRequest transferRequest,
                                                    Account senderAccount, Account receiverAccount) {
        BigDecimal previousSenderBalance = BigDecimal.valueOf(500);
        BigDecimal previousReceiverBalance = BigDecimal.valueOf(200);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(savedHistories.size()).isEqualTo(2);
            softly.assertThat(savedHistories.get(0).getAccountNumber()).isEqualTo(transferRequest.senderAccountNumber());
            softly.assertThat(savedHistories.get(0).getExternalAccountNumber())
                    .isEqualTo(transferRequest.receiverAccountNumber());
            softly.assertThat(savedHistories.get(0).getBalance()).isEqualTo(expectedSenderBalance);
            softly.assertThat(savedHistories.get(0).getPreviousBalance())
                    .isEqualTo(previousSenderBalance.setScale(2, RoundingMode.HALF_EVEN));
            softly.assertThat(savedHistories.get(0).getTransferType()).isEqualTo(TransferType.EXPENSE);
            softly.assertThat(savedHistories.get(0).getAmount()).isEqualTo(expectedSenderAmount);
            softly.assertThat(savedHistories.get(0).getClientId()).isEqualTo(senderAccount.getClient().getId());

            softly.assertThat(savedHistories.get(1).getAccountNumber()).
                    isEqualTo(transferRequest.receiverAccountNumber());
            softly.assertThat(savedHistories.get(1).getExternalAccountNumber())
                    .isEqualTo(transferRequest.senderAccountNumber());
            softly.assertThat(savedHistories.get(1).getBalance()).isEqualTo(expectedReceiverBalance);
            softly.assertThat(savedHistories.get(1).getPreviousBalance())
                    .isEqualTo(previousReceiverBalance.setScale(2, RoundingMode.HALF_EVEN));
            softly.assertThat(savedHistories.get(1).getTransferType()).isEqualTo(TransferType.INCOME);
            softly.assertThat(savedHistories.get(1).getAmount()).isEqualTo(transferRequest.amount());
            softly.assertThat(savedHistories.get(1).getClientId()).isEqualTo(receiverAccount.getClient().getId());
        });
        return this;
    }
}
