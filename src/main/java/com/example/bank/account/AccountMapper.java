package com.example.bank.account;

import com.example.bank.account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "client.id", target = "clientId")
    AccountDTO accountToDTO(Account account);
}
