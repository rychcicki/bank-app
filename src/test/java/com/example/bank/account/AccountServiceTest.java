package com.example.bank.account;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountMapper accountMapper;

    @Mock
    ClientService clientService;

    @InjectMocks
    AccountService accountService;

    private final Long clientId = 5L;
    private final Client client = new Client("Zdzislaw", "Krecina",
            LocalDate.of(1954, 4, 28), "zdzislaw.krecina@gmail.com", "password",
            new Address("Tatrzanska", "7B", "12-456", "Zywiec"));

    private final Account account = new Account();
    private final AccountDTO expectedAccountDTO = new AccountDTO("Number", Currency.PLN,
            AccountType.CURRENT_ACCOUNT, BigDecimal.TEN, clientId);

    private final AccountDTO foreignAccountDTO = new AccountDTO("12345", Currency.USD,
            AccountType.CURRENT_ACCOUNT, BigDecimal.TEN, clientId);

    @DisplayName("createMyBankAccountTest")
    @Test
    void shouldCreateMyBankAccountAndReturnAccountDto() {
        when(clientService.findClient(clientId)).thenReturn(client);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToDTO(any(Account.class))).thenReturn(expectedAccountDTO);

        AccountDTO myBankAccount = accountService.createMyBankAccount(clientId);

        Assertions.assertEquals(expectedAccountDTO, myBankAccount);
    }

    @DisplayName("createPolishAccountTest")
    @Test
    void shouldCreatePolishAccountAndReturnAccountDto() {
        when(clientService.findClient(clientId)).thenReturn(client);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToDTO(any(Account.class))).thenReturn(expectedAccountDTO);

        AccountDTO myBankAccount = accountService.createPolishAccount(clientId);

        Assertions.assertEquals(expectedAccountDTO, myBankAccount);
    }

    @DisplayName("createForeignAccountTest")
    @Test
    void shouldCreateForeignAccountAndReturnAccountDto() {
        when(clientService.findClient(clientId)).thenReturn(client);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToDTO(any(Account.class))).thenReturn(foreignAccountDTO);

        AccountDTO myBankAccount = accountService.createForeignAccount(clientId);

        Assertions.assertEquals(foreignAccountDTO, myBankAccount);
    }

    @DisplayName("findAccountByAccountNumberTest")
    @Test
    void shouldFindAccountAndReturnAccountDto() {
        String accountNumber = "12345";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountMapper.accountToDTO(account)).thenReturn(foreignAccountDTO);

        AccountDTO accountByAccountNumber = accountService.findAccountByAccountNumber(accountNumber);

        Assertions.assertEquals(foreignAccountDTO, accountByAccountNumber);
    }

    @DisplayName("findAccountByAccountNumberExceptionTest")
    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountNotFound() {
        String accountNumber = "WrongAccountNumber123";

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenThrow(new RestException(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION));

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> accountService.findAccountByAccountNumber(accountNumber));

        Assertions.assertEquals(exception.getMessage(), ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("findAllAccountsTest")
    @Test
    void shouldFindListOfAccountDto() {
        List<Account> accounts = List.of(new Account(), new Account());
        List<AccountDTO> expectedDTOs = List.of(foreignAccountDTO, foreignAccountDTO);

        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.accountToDTO(any(Account.class))).thenReturn(foreignAccountDTO);

        List<AccountDTO> allAccounts = accountService.findAllAccounts();

        Assertions.assertEquals(2, allAccounts.size());
        Assertions.assertEquals(expectedDTOs, allAccounts);
    }
}
