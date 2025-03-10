package com.example.bank.account;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/my-bank/{clientId}")
    AccountDTO createMyBankAccount(@PathVariable Long clientId) {
        return accountService.createMyBankAccount(clientId);
    }

    @PostMapping("/polish/{clientId}")
    AccountDTO createPolishAccounts(@PathVariable Long clientId) {
        return accountService.createPolishAccount(clientId);
    }

    @PostMapping("/foreign/{clientId}")
    AccountDTO createForeignAccount(@PathVariable Long clientId) {
        return accountService.createForeignAccount(clientId);
    }

    @GetMapping("{number}")
    @RateLimiter(name = "getAccountByAccountNumber")
    AccountDTO getAccountByAccountNumber(@PathVariable String number) {
        return accountService.findAccountByAccountNumber(number);
    }

    @GetMapping
    List<AccountDTO> getAllAccounts() {
        return accountService.findAllAccounts();
    }
}
