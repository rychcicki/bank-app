package com.example.bank.transfer.model;

import com.example.bank.auditing.AuditorEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class TransferHistory extends AuditorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransferType transferType;

    @NotNull
    private Long clientId;

    @NotBlank(message = "Account number is mandatory")
    private String accountNumber;

    @NotBlank(message = "External account number is mandatory")
    private String externalAccountNumber;

    @NotNull
    private BigDecimal previousBalance;

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balance;

    @NotBlank(message = "title of transfer is mandatory")
    private String title;
}
