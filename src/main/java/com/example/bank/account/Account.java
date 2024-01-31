package com.example.bank.account;

import com.example.bank.registration.jpa.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "account_number"))
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    private Long id;
    @Column(name = "account_number", nullable = false)
    @NotBlank(message = "Account number is mandatory")
    //TODO pattern - pomysl nad tym zeby przy tworzeniu konta zrobic jakis generator numeru kolejnego lub losowego
    /**     W sumie ten numer konta, to powinien być IBAN */
    private String accountNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(name = "available_funds", nullable = false)
    private BigDecimal availableFunds;
    //    @Column(nullable = false)
//    private String iban;
    @Column(nullable = false)
    private String swift_Bic;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "client_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "client_id_fk"))
    private Client client;
//    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    private List<Transfer> transfer;

    List<Account> createTwoMyBankAccounts() {
        Account firstMyBankAccount = new Account();
        firstMyBankAccount.setAccountNumber(AccountNumberGenerator.myBankIbanGenerator().toString());
        firstMyBankAccount.setCurrency(Currency.PLN);
        firstMyBankAccount.setBalance(BigDecimal.valueOf(100L));
        firstMyBankAccount.setType(AccountType.CURRENT_ACCOUNT);

        Account secondAccount = new Account();
        secondAccount.setAccountNumber(AccountNumberGenerator.myBankIbanGenerator().toString());
        secondAccount.setCurrency(Currency.PLN);
        secondAccount.setBalance(BigDecimal.valueOf(0L));
        secondAccount.setType(AccountType.CURRENT_ACCOUNT);

        return List.of(firstMyBankAccount, secondAccount);
    }

    Account createMyBankDollarAccount() {
        Account myBankDollarAccount = new Account();
        myBankDollarAccount.setAccountNumber(AccountNumberGenerator.myBankIbanGenerator().toString());
        myBankDollarAccount.setCurrency(Currency.USD);
        myBankDollarAccount.setBalance(BigDecimal.valueOf(200));
        myBankDollarAccount.setType(AccountType.CURRENT_ACCOUNT);
        return myBankDollarAccount;
    }

    List<Account> createTwoPolishAccounts() {
        Account firstPolishAccount = new Account();
        firstPolishAccount.setAccountNumber(AccountNumberGenerator.polishIbanGenerator().toString());
        firstPolishAccount.setCurrency(Currency.PLN);
        firstPolishAccount.setBalance(BigDecimal.valueOf(50L));
        firstPolishAccount.setType(AccountType.CURRENT_ACCOUNT);

        Account secondPolishAccount = new Account();
        secondPolishAccount.setAccountNumber(AccountNumberGenerator.polishIbanGenerator().toString());
        secondPolishAccount.setCurrency(Currency.PLN);
        secondPolishAccount.setBalance(BigDecimal.valueOf(0L));
        secondPolishAccount.setType(AccountType.CURRENT_ACCOUNT);

        return List.of(firstPolishAccount, secondPolishAccount);
    }

    List<Account> createForeignAccounts() {
        Account firstForeignAccount = new Account();
        firstForeignAccount.setAccountNumber(AccountNumberGenerator.foreignIbanGenerator().toString());
        firstForeignAccount.setBalance(BigDecimal.valueOf(200L));
        firstForeignAccount.setType(AccountType.CURRENT_ACCOUNT);

        Account secondForeignAccount = new Account();
        secondForeignAccount.setAccountNumber(AccountNumberGenerator.foreignIbanGenerator().toString());
        secondForeignAccount.setBalance(BigDecimal.valueOf(20L));
        secondForeignAccount.setType(AccountType.CURRENT_ACCOUNT);

        return List.of(firstForeignAccount, secondForeignAccount);
    }

    Account createDollarForeignAccount() {
        Account dollarForeignAccount = new Account();
        dollarForeignAccount.setAccountNumber(AccountNumberGenerator.foreignIbanGenerator().toString());
        dollarForeignAccount.setCurrency(Currency.USD);
        dollarForeignAccount.setBalance(BigDecimal.valueOf(0L));
        dollarForeignAccount.setType(AccountType.CURRENT_ACCOUNT);
        return dollarForeignAccount;
    }

    Account createForeignAccountWithOfficialEurGbpChfCurrencyWithUsdAsDefault() {
        Account foreignAccountWithOfficialCurrency = new Account();
        Iban iban = AccountNumberGenerator.foreignIbanGenerator();
        foreignAccountWithOfficialCurrency.setAccountNumber(iban.toString());
        CountryCode countryCode = iban.getCountryCode();
        switch (countryCode) {
        /*  case US, PR, EC, SV, ZW, GU, VI, VG, TL, BQ, WS, MP, FM, PW, MH, PA, TC ->
                    foreignAccountWithOfficialCurrency.setCurrency(Currency.USD);
                    break;*/
            case AT, BE, HR, CY, EE, FI, FR, DE, GR, IE, IT, LV, LT, LU, MT, NL, PT, SK, SI, ES ->
                    foreignAccountWithOfficialCurrency.setCurrency(Currency.EUR);
            case GB, AQ, FK, GI, GG, IM, JE, SH, GS -> foreignAccountWithOfficialCurrency.setCurrency(Currency.GBP);
            case CH, LI -> foreignAccountWithOfficialCurrency.setCurrency(Currency.CHF);
            default -> foreignAccountWithOfficialCurrency.setCurrency(Currency.USD);
        }
        foreignAccountWithOfficialCurrency.setBalance(BigDecimal.valueOf(0L));
        foreignAccountWithOfficialCurrency.setType(AccountType.CURRENT_ACCOUNT);
        return foreignAccountWithOfficialCurrency;
    }
}
