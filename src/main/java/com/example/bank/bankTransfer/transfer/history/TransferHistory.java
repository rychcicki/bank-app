package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.auditing.AuditorEntity;
import com.example.bank.bankTransfer.transfer.TransferType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "transfer_history")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferHistory extends AuditorEntity {
    @Id
    @SequenceGenerator(name = "history_sequence", sequenceName = "history_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_generator")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferType transferType;
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    @Column(name = "external_account_number", nullable = false)
    private String externalAccountNumber;
    @Column(name = "before_balance", nullable = false)
    private BigDecimal previousBalance;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(name = "title_of_transfer", nullable = false)
    @NotBlank(message = "title of transfer is mandatory")
    private String title;
}
