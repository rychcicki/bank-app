package com.example.bank.account;

import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.iban4j.*;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AccountNumberGenerator {
    static final String MY_BANK_CODE = "575";

    static Iban polishIbanGenerator() {
        Iban iban = Iban.random(CountryCode.PL);
        return accountNumberValidator(iban);
    }

    static Iban myBankIbanGenerator() {
        Iban iban = new Iban.Builder().countryCode(CountryCode.PL).bankCode(MY_BANK_CODE).buildRandom();
        return accountNumberValidator(iban);
    }

    static Iban foreignIbanGenerator() {
        List<CountryCode> countryCodes = Stream.generate(() -> Iban.random().getCountryCode())
                .limit(10)
                .filter(code -> !code.getName().equals("Poland"))
                .toList();

        if (countryCodes.isEmpty()) {
            throw new RestException(ExceptionType.INVALID_ACCOUNT_NUMBER_EXCEPTION);
        }

        Iban iban = new Iban.Builder().countryCode(countryCodes.getFirst()).buildRandom();
        return accountNumberValidator(iban);
    }

    private static Iban accountNumberValidator(Iban iban) {
        try {
            IbanUtil.validate(iban.toString());
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            throw new RestException(ExceptionType.INVALID_ACCOUNT_NUMBER_EXCEPTION);
        }
        return iban;
    }
}
