package com.example.bank.bank.transfer.transfer;

import com.example.bank.bank.transfer.account.Currency;
import lombok.NonNull;

import java.math.BigDecimal;

public class TransferValidationUtils {
    public void amountValidation(@NonNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount has to be greater than 0.");
        }
    }

    public void balanceValidation(@NonNull BigDecimal balance, @NonNull Currency senderAccountCurrency,
                                  @NonNull BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(String.format("You have not enough money to do a transfer. " +
                            "Your balance is %s %s. You need at least %s %s.", balance, senderAccountCurrency, amount,
                    senderAccountCurrency));
        }
    }
}
