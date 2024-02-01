package com.example.bank.registration;

import com.example.bank.account.Account;
import com.example.bank.registration.jpa.Address;
import com.example.bank.security.token.Token;
import com.example.bank.user.Role;

import java.time.LocalDate;
import java.util.List;

public record ClientRequest(String firstName, String lastName, LocalDate birthDate, String email,
                            Address address, String password, Role role/*, List<Token> token, List<Account> account*/){
}
