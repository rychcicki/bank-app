package com.example.bank.bankTransfer.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create-myBank-account")
    ResponseEntity<Account> createMyBankAccount() {
        return ResponseEntity.ok(accountService.createMyBankAccount());
    }

    @PostMapping("/create-polish-account")
    ResponseEntity<Account> createPolishAccounts() {
        return ResponseEntity.ok(accountService.createPolishAccounts());
    }

    @PostMapping("/create-foreign-account")
    ResponseEntity<Account> createForeignAccount() {
        return ResponseEntity.ok(accountService.createForeignAccount());
    }
}
