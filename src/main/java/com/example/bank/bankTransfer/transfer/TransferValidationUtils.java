package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.account.Currency;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class TransferValidationUtils {
    public void amountValidation(@NonNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount has to be greater than 0.");
        }
    }

    public void balanceValidation(@NonNull BigDecimal balance, @NonNull Currency fromAccountCurrency,
                                  @NonNull BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(String.format("You have not enough money to do a transfer. " +
                            "Your balance is %s %s. You need at least %s %s.", balance, fromAccountCurrency, amount,
                    fromAccountCurrency));
        }
    }
}
