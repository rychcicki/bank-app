package com.example.bank.account;

import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.iban4j.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AccountNumberGenerator {
    private static final String MY_BANK_CODE = "575";

    static Iban polishIbanGenerator() {
        return Iban.random(CountryCode.PL);
    }

    static Iban myBankIbanGenerator() {
        return new Iban.Builder().countryCode(CountryCode.PL).bankCode(MY_BANK_CODE).buildRandom();
    }

    static Iban foreignIbanGenerator() {
        CountryCode countryCode = Iban.random().getCountryCode();
        if (!countryCode.equals(CountryCode.PL)) {
            return new Iban.Builder().countryCode(countryCode).buildRandom();
        }
        return foreignIbanGenerator();
    }

    static void accountNumberValidator(String iban) {
        try {
            IbanUtil.validate(iban);
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            throw new RestException(ExceptionType.INVALID_ACCOUNT_NUMBER);
        }
    }
}
