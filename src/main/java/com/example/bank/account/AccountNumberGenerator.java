package com.example.bank.account;

import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.IbanUtil;

@Slf4j
public class AccountNumberGenerator {
    private static final String MY_BANK_CODE = "575";

    static Iban polishIbanGenerator() {
        return Iban.random(CountryCode.PL);
    }

    static Iban myBankIbanGenerator() {
        return new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode(MY_BANK_CODE)
                .buildRandom();
    }

    static Iban foreignIbanGenerator() {
        CountryCode countryCode = Iban.random().getCountryCode();
        Iban foreignIban = null;
        if (!countryCode.equals(CountryCode.PL)) {
            foreignIban = new Iban.Builder()
                    .countryCode(countryCode)
                    .buildRandom();
        } else {
            foreignIbanGenerator();
        }
        log.info("Foreign country account number (IBAN): " + foreignIban);
        return foreignIban;
    }

    static void accountNumberValidator(Iban iban) {
        IbanUtil.validate(String.valueOf(iban));
        log.info("Account number (IBAN) is valid.");
//        try {
//            IbanUtil.validate(String.valueOf(iban));
//        } catch (IBANException |
//                 InvalidCheckDigitException |
//                 UnsupportedCountryException e) {
//        } finally {
//            log.error("Invalid account number (IBAN): " + iban);
//        }
    }
}
