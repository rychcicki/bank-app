package com.example.bank.account;

import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;

import java.math.BigDecimal;

public record AccountDTO(String accountNumber, Currency currency, AccountType type, BigDecimal balance,
                         Long clientId) {
}
