package com.example.bank.account;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Getter
public class TransferService {
    public static void main(String[] args){
        Account account = new Account();
        List<Account> twoMyBankAccounts = account.createTwoMyBankAccounts();

        Account polishAccount = new Account();
        List<Account> twoPolishAccounts = account.createTwoPolishAccounts();

        Account foreignAccount = new Account();
        List<Account> twoForeignAccounts = account.createForeignAccounts();

        TransferService myTransfer = new TransferService();
        myTransfer.internalAndExternalTransfer(twoMyBankAccounts.get(0),twoMyBankAccounts.get(1),BigDecimal.valueOf(100L));
    }
    //TODO to może być jakiś interfejs w sumie; metoda abstrakcyjna; niezależna od banku i waluty
    boolean internalAndExternalTransfer(Account firstAccount, Account secondAccount, BigDecimal amount){
        if (firstAccount.getBalance().compareTo(amount) < 0 &&
                firstAccount.getCurrency().equals(secondAccount.getCurrency()) &&
                firstAccount.getAccountNumber().substring(0,2).equals(secondAccount.getAccountNumber().substring(0,2))
        ){
            log.info("Wrong amount or currency.");
            return true;
        } else {
            firstAccount.setBalance(firstAccount.getBalance().subtract(amount));
            secondAccount.setBalance(secondAccount.getBalance().add(amount));
            log.info("Bank transfer for " + amount + " " + firstAccount.getCurrency() + " has done. " +
                    "Fist account's balance: " + firstAccount.getBalance() +  " " + firstAccount.getCurrency() +
                    ", second account's balance: " + secondAccount.getBalance() +  " " + secondAccount.getCurrency());
            return false;
        }
    }

//    boolean polishBankTransfer(Account firstAccount, Account secondAccount, BigDecimal amount){
//        if (firstAccount.getBalance().compareTo(amount) < 0 &&
//                firstAccount.getCurrency().equals(secondAccount.getCurrency())){
//            log.info("Wrong amount or currency.");
//            return true;
//        } else {
//            firstAccount.setBalance(firstAccount.getBalance().subtract(amount));
//            secondAccount.setBalance(secondAccount.getBalance().add(amount));
//            log.info("Bank transfer for " + amount + " " + firstAccount.getCurrency() + " has done. " +
//                    "Fist account's balance: " + firstAccount.getBalance() +  " " + firstAccount.getCurrency() +
//                    ", second account's balance: " + secondAccount.getBalance() +  " " + secondAccount.getCurrency());
//            return false;
//        }
//    }
}
