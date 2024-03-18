package com.example.bank.bankTransfer.account;

import com.example.bank.client.ClientService;
import com.example.bank.client.jpa.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public Account createMyBankAccount(Integer clientId) {
        Client client = clientService.getClientById(clientId);
        Account myBankAccount = new Account();
        myBankAccount.setAccountNumber(AccountNumberGenerator.myBankIbanGenerator().toString());
        myBankAccount.setCurrency(Currency.PLN);
        myBankAccount.setType(AccountType.CURRENT_ACCOUNT);
        myBankAccount.setClient(client);
        AccountNumberGenerator.accountNumberValidator(myBankAccount.getAccountNumber());
        accountRepository.save(myBankAccount);
        return myBankAccount;
    }

    public Account createPolishAccounts(Integer clientId) {
        Client client = clientService.getClientById(clientId);
        Account polishAccount = new Account();
        polishAccount.setAccountNumber(AccountNumberGenerator.polishIbanGenerator().toString());
        polishAccount.setCurrency(Currency.PLN);
        polishAccount.setType(AccountType.CURRENT_ACCOUNT);
        polishAccount.setClient(client);
        AccountNumberGenerator.accountNumberValidator(polishAccount.getAccountNumber());
        accountRepository.save(polishAccount);
        return polishAccount;
    }

    public Account createForeignAccount(Integer clientId) {
        Client client = clientService.getClientById(clientId);
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
        foreignAccountWithOfficialCurrency.setType(AccountType.CURRENT_ACCOUNT);
        AccountNumberGenerator.accountNumberValidator(foreignAccountWithOfficialCurrency.getAccountNumber());
        foreignAccountWithOfficialCurrency.setClient(client);
        accountRepository.save(foreignAccountWithOfficialCurrency);
        return foreignAccountWithOfficialCurrency;
    }
}
