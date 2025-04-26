package com.example.bank.account;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDTO createMyBankAccount(Long clientId) {
        String accountNumber = AccountNumberGenerator.myBankIbanGenerator().toString();
        return createAccount(accountNumber, clientId, Currency.PLN);
    }

    @Transactional
    public AccountDTO createPolishAccount(Long clientId) {
        String accountNumber = AccountNumberGenerator.polishIbanGenerator().toString();
        return createAccount(accountNumber, clientId, Currency.PLN);
    }

    @Transactional
    public AccountDTO createForeignAccount(Long clientId) {
        Iban iban = AccountNumberGenerator.foreignIbanGenerator();
        String foreignAccountWithOfficialCurrency = iban.toString();
        CountryCode countryCode = iban.getCountryCode();
        return createAccount(foreignAccountWithOfficialCurrency, clientId, getOfficialCurrency(countryCode));
    }

    private AccountDTO createAccount(String accountNumber, Long clientId, Currency currency) {
        Client client = clientService.findClient(clientId);
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCurrency(currency);
        account.setType(AccountType.CURRENT_ACCOUNT);
        account.setClient(client);
        accountRepository.save(account);
        return accountMapper.accountToDTO(account);
    }

    private Currency getOfficialCurrency(CountryCode countryCode) {
        return switch (countryCode) {
            case AD, AT, BE, BL, HR, CY, EE, FI, FR, DE, GF, GP, GR, IE, IT, LV, LT, LU, MC, ME, MF, MQ, MT, NL, PM, PT,
                 RE, SK, SI, SM, ES, XK, YT -> Currency.EUR;
            case GB, AQ, FK, GI, GG, IM, JE, SH, GS -> Currency.GBP;
            case CH, LI -> Currency.CHF;
            case AU, CC, CX, HM, NF, KI, NR, TV -> Currency.AUD;
            case NO, BV, SJ -> Currency.NOK;
            default -> Currency.USD;
        };
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
