package com.example.bank.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
class AccountController {
    private final AccountService accountService;

    @PostMapping("/my-bank/{clientId}")
    AccountDTO createMyBankAccount(@PathVariable Long clientId) {
        return accountService.createMyBankAccount(clientId);
    }

    @PostMapping("/polish/{clientId}")
    AccountDTO createPolishAccounts(@PathVariable Long clientId) {
        return accountService.createPolishAccounts(clientId);
    }

    @PostMapping("/foreign/{clientId}")
    AccountDTO createForeignAccount(@PathVariable Long clientId) {
        return accountService.createForeignAccount(clientId);
    }

    @GetMapping("{number}")
    AccountDTO getAccountByAccountNumber(@PathVariable String number) {
        return accountService.findAccountByAccountNumber(number);
    }

    @GetMapping
    List<AccountDTO> getAllAccounts() {
        return accountService.findAllAccounts();
    }
}
