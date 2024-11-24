package com.example.bank.account;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final AccountMapper accountMapper;

    public AccountDTO createMyBankAccount(Long clientId) {
        String accountNumber = AccountNumberGenerator.myBankIbanGenerator().toString();
        AccountNumberGenerator.accountNumberValidator(accountNumber);

        Client client = clientService.findClient(clientId);
        Account myBankAccount = new Account();
        myBankAccount.setAccountNumber(accountNumber);
        myBankAccount.setCurrency(Currency.PLN);
        myBankAccount.setType(AccountType.CURRENT_ACCOUNT);
        myBankAccount.setClient(client);
        accountRepository.save(myBankAccount);
        return accountMapper.accountToDTO(myBankAccount);
    }

    public AccountDTO createPolishAccounts(Long clientId) {
        String accountNumber = AccountNumberGenerator.polishIbanGenerator().toString();
        AccountNumberGenerator.accountNumberValidator(accountNumber);

        Client client = clientService.findClient(clientId);
        Account polishAccount = new Account();
        polishAccount.setAccountNumber(accountNumber);
        polishAccount.setCurrency(Currency.PLN);
        polishAccount.setType(AccountType.CURRENT_ACCOUNT);
        polishAccount.setClient(client);
        accountRepository.save(polishAccount);
        return accountMapper.accountToDTO(polishAccount);
    }

    public AccountDTO createForeignAccount(Long clientId) {
        Iban iban = AccountNumberGenerator.foreignIbanGenerator();
        String accountNumber = iban.toString();
        AccountNumberGenerator.accountNumberValidator(accountNumber);

        Account foreignAccountWithOfficialCurrency = new Account();
        foreignAccountWithOfficialCurrency.setAccountNumber(accountNumber);
        CountryCode countryCode = iban.getCountryCode();
        setOfficialCurrency(countryCode, foreignAccountWithOfficialCurrency);
        foreignAccountWithOfficialCurrency.setType(AccountType.CURRENT_ACCOUNT);

        Client client = clientService.findClient(clientId);
        foreignAccountWithOfficialCurrency.setClient(client);
        accountRepository.save(foreignAccountWithOfficialCurrency);
        return accountMapper.accountToDTO(foreignAccountWithOfficialCurrency);
    }

    private void setOfficialCurrency(CountryCode countryCode, Account foreignAccountWithOfficialCurrency) {
        switch (countryCode) {
            case AD, AT, BE, BL, HR, CY, EE, FI, FR, DE, GF, GP, GR, IE, IT, LV, LT, LU, MC, ME, MF, MQ, MT, NL, PM, PT,
                 RE, SK, SI, SM, ES, XK, YT -> foreignAccountWithOfficialCurrency.setCurrency(Currency.EUR);
            case GB, AQ, FK, GI, GG, IM, JE, SH, GS -> foreignAccountWithOfficialCurrency.setCurrency(Currency.GBP);
            case CH, LI -> foreignAccountWithOfficialCurrency.setCurrency(Currency.CHF);
            case AU, CC, CX, HM, NF, KI, NR, TV -> foreignAccountWithOfficialCurrency.setCurrency(Currency.AUD);
            case NO, BV, SJ -> foreignAccountWithOfficialCurrency.setCurrency(Currency.NOK);
            default -> foreignAccountWithOfficialCurrency.setCurrency(Currency.USD);
        }
    }

    public AccountDTO findAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));
        return accountMapper.accountToDTO(account);
    }

    public List<AccountDTO> findAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::accountToDTO)
                .toList();
    }
}
