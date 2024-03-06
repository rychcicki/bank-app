package com.example.bank.bankTransfer.account;

import com.example.bank.client.jpa.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "account_number"))
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_sequence")
    private Long id;
    @Column(name = "account_number", nullable = false)
    @NotBlank(message = "Account number is mandatory")
    private String accountNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "client_id_fk"))
    private Client client;
}
