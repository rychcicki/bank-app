package com.example.bank.bankTransfer.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public Account createMyBankAccount() {
        Account myBankAccount = new Account();
        myBankAccount.setAccountNumber(AccountNumberGenerator.myBankIbanGenerator().toString());
        myBankAccount.setCurrency(Currency.PLN);
        myBankAccount.setBalance(BigDecimal.ZERO);
        myBankAccount.setType(AccountType.CURRENT_ACCOUNT);
        AccountNumberGenerator.accountNumberValidator(myBankAccount.getAccountNumber());
        accountRepository.save(myBankAccount);
        return myBankAccount;
    }

    public Account createPolishAccounts() {
        Account polishAccount = new Account();
        polishAccount.setAccountNumber(AccountNumberGenerator.polishIbanGenerator().toString());
        polishAccount.setCurrency(Currency.PLN);
        polishAccount.setBalance(BigDecimal.ZERO);
        polishAccount.setType(AccountType.CURRENT_ACCOUNT);
        AccountNumberGenerator.accountNumberValidator(polishAccount.getAccountNumber());
        accountRepository.save(polishAccount);
        return polishAccount;
    }

    public Account createForeignAccount() {
        Account foreignAccountWithOfficialCurrency = new Account();
        Iban iban = AccountNumberGenerator.foreignIbanGenerator();
        foreignAccountWithOfficialCurrency.setAccountNumber(iban.toString());
        CountryCode countryCode = iban.getCountryCode();
        switch (countryCode) {
            case AD, AT, BE, BL, HR, CY, EE, FI, FR, DE, GF, GP, GR, IE, IT, LV, LT, LU, MC, ME, MF, MQ, MT, NL, PM, PT,
                    RE, SK, SI, SM, ES, XK, YT -> foreignAccountWithOfficialCurrency.setCurrency(Currency.EUR);
            case GB, AQ, FK, GI, GG, IM, JE, SH, GS -> foreignAccountWithOfficialCurrency.setCurrency(Currency.GBP);
            case CH, LI -> foreignAccountWithOfficialCurrency.setCurrency(Currency.CHF);
            case AU, CC, CX, HM, NF, KI, NR, TV -> foreignAccountWithOfficialCurrency.setCurrency(Currency.AUD);
            case NO, BV, SJ -> foreignAccountWithOfficialCurrency.setCurrency(Currency.NOK);
            default -> foreignAccountWithOfficialCurrency.setCurrency(Currency.USD);
        }
        foreignAccountWithOfficialCurrency.setBalance(BigDecimal.valueOf(0L));
        foreignAccountWithOfficialCurrency.setType(AccountType.CURRENT_ACCOUNT);
        AccountNumberGenerator.accountNumberValidator(foreignAccountWithOfficialCurrency.getAccountNumber());
        accountRepository.save(foreignAccountWithOfficialCurrency);
        return foreignAccountWithOfficialCurrency;
    }
}
