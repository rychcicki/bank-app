package com.example.bank.bankTransfer.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create-account/{clientId}")
    ResponseEntity<Account> createMyBankAccount(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.createMyBankAccount(clientId));
    }

    @PostMapping("/create-polish-account/{clientId}")
    ResponseEntity<Account> createPolishAccounts(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.createPolishAccounts(clientId));
    }

    @PostMapping("/create-foreign-account/{clientId}")
    ResponseEntity<Account> createForeignAccount(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.createForeignAccount(clientId));
    }
}
