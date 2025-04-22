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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.example.bank.account.AccountServiceUtils.foreignAccountDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private final AccountDTO expectedAccountDTO = new AccountDTO("Number", Currency.PLN,
            AccountType.CURRENT_ACCOUNT, BigDecimal.TEN, clientId);

    private final Account account = new Account();

    @ParameterizedTest(name = "createAccountMethod: {0}")
    @MethodSource("com.example.bank.account.AccountServiceUtils#accountCreationFunctionsScenarios")
    void shouldCreateAccountAndReturnAccountDto(String scenarioMethod,
                                                BiFunction<AccountService, Long, AccountDTO> accountCreationMethod) {
        when(clientService.findClient(clientId)).thenReturn(client);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToDTO(any(Account.class))).thenReturn(expectedAccountDTO);

        AccountDTO bankAccount = accountCreationMethod.apply(accountService, clientId);

        Assertions.assertEquals(expectedAccountDTO, bankAccount);
        verify(accountRepository).save(any(Account.class));
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

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> accountService.findAccountByAccountNumber(accountNumber));

        Assertions.assertEquals(ExceptionType.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage(), exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.account.AccountServiceUtils#accountListsScenarios")
    void shouldFindListOfAccountDto(List<Account> accounts, List<AccountDTO> expectedDTOs) {
        when(accountRepository.findAll()).thenReturn(accounts);
        lenient().when(accountMapper.accountToDTO(any(Account.class))).thenReturn(foreignAccountDTO);

        List<AccountDTO> allAccounts = accountService.findAllAccounts();

        Assertions.assertEquals(expectedDTOs.size(), allAccounts.size());
        Assertions.assertEquals(expectedDTOs, allAccounts);
    }

    @ParameterizedTest(name = "notCreateAccountWhenClientNotFound: {0}")
    @MethodSource("com.example.bank.account.AccountServiceUtils#accountCreationFunctionsScenarios")
    void shouldThrowExceptionWhenClientNotFound(String scenarioMethod,
                                                BiFunction<AccountService, Long, AccountDTO> accountCreationMethod) {
        Long nonExistingClientId = 1234567890L;
        when(clientService.findClient(nonExistingClientId))
                .thenThrow(new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> accountCreationMethod.apply(accountService, nonExistingClientId));

        Assertions.assertEquals(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION.getMessage(), exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
