package com.example.bank.bankTransfer.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.*;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
public class AccountNumberGenerator {
    @Value("${my-bank-code}")
    @Getter
    private static String MY_BANK_CODE;

    static Iban polishIbanGenerator() {
        return Iban.random(CountryCode.PL);
    }

    static Iban myBankIbanGenerator() {
        return new Iban.Builder().countryCode(CountryCode.PL).bankCode(MY_BANK_CODE).buildRandom();
    }

    static Iban foreignIbanGenerator() {
        CountryCode countryCode = Iban.random().getCountryCode();
        if (!countryCode.equals(CountryCode.PL)) {
            Iban foreignIban = new Iban.Builder().countryCode(countryCode).buildRandom();
            log.info("Foreign country account number (IBAN): {}", foreignIban);
            return foreignIban;
        }
        return foreignIbanGenerator();
    }

    static void accountNumberValidator(String iban) {
        log.info("Account number (IBAN): {} is valid.", iban);
        try {
            IbanUtil.validate(iban);
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            log.error("Invalid account number (IBAN): {}", iban);
        }
    }
}
