package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.auditing.AuditorEntity;
import com.example.bank.bankTransfer.transfer.TransferType;
import jakarta.persistence.*;
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
    private Integer clientId;
    @Column(name = "bank_account_id", nullable = false)
    private Long bankAccountId;
    @Column(name = "before_balance", nullable = false)
    private BigDecimal beforeBalance;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "after_balance", nullable = false)
    private BigDecimal afterBalance;
}
