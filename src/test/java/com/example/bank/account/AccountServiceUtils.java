package com.example.bank.account;

import com.example.bank.account.model.Account;
import com.example.bank.account.model.AccountType;
import com.example.bank.account.model.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AccountServiceUtils {
    static final AccountDTO foreignAccountDTO = new AccountDTO("12345", Currency.USD,
            AccountType.CURRENT_ACCOUNT, BigDecimal.TEN, 145L);

    static Stream<Arguments> accountListsScenarios() {
        return Stream.of(
                Arguments.of(List.of(new Account(), new Account()), List.of(foreignAccountDTO, foreignAccountDTO)),
                Arguments.of(Collections.<Account>emptyList(), Collections.<AccountDTO>emptyList()),
                Arguments.of(List.of(new Account()), List.of(foreignAccountDTO))
        );
    }

    static Stream<Arguments> accountCreationActionsScenarios() {
        return Stream.of(
                Arguments.of("createMyBankAccount",
                        (BiConsumer<AccountService, Long>) AccountService::createMyBankAccount),
                Arguments.of("createPolishAccount",
                        (BiConsumer<AccountService, Long>) AccountService::createPolishAccount),
                Arguments.of("createForeignAccount",
                        (BiConsumer<AccountService, Long>) AccountService::createForeignAccount));
    }

    static Stream<Arguments> accountCreationFunctionsScenarios() {
        return Stream.of(
                Arguments.of("createMyBankAccount",
                        (BiFunction<AccountService, Long, AccountDTO>) AccountService::createMyBankAccount),
                Arguments.of("createPolishAccount",
                        (BiFunction<AccountService, Long, AccountDTO>) AccountService::createPolishAccount),
                Arguments.of("createForeignAccount",
                        (BiFunction<AccountService, Long, AccountDTO>) AccountService::createForeignAccount));
    }

    static Stream<Arguments> forbiddenEndpointsScenarios() {
        return Stream.of(
                Arguments.of("POST /account/polish/2", post("/account/polish/2")),
                Arguments.of("POST /account/foreign/3", post("/account/foreign/3")),
                Arguments.of("GET /account/GB92BARC20038472426896",
                        get("/account/GB92BARC20038472426896")),
                Arguments.of("GET /account", get("/account"))
        );
    }

    static Stream<Arguments> unauthorizedEndpointsScenarios() {
        return Stream.of(
                Arguments.of("POST /account/my-bank/1", post("/account/my-bank/1")),
                Arguments.of("POST /account/polish/2", post("/account/polish/2")),
                Arguments.of("POST /account/foreign/3", post("/account/foreign/3")),
                Arguments.of("GET /account/GB92BARC20038472426896",
                        get("/account/GB92BARC20038472426896")),
                Arguments.of("GET /account", get("/account"))
        );
    }
}
