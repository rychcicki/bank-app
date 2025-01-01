package com.example.bank.account;

import com.example.bank.account.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO accountToDTO(Account account);
}
