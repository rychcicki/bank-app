package com.example.bank.account;

import com.example.bank.registration.jpa.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
/**  W columnNames podajemy nazwę pola, czy zmapowanej kolumny w bazie danych? */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "account_number"))
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    private Long id;
    /**  Czy id może być typu String i co z inkrementacją tego pola?? Jaki to będzie GenerationType?  */
    @Column(name = "account_number", nullable = false)
    @NotBlank(message = "Account number is mandatory")
    //TODO pattern
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
    @Column(nullable = false)
    private String IBAN;
    @Column(nullable = false)
    private String SWIFT_BIC;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "client_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "client_id_fk"))
    private Client client;
    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Transfer> transfer;
}
