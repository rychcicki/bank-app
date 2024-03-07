package com.example.bank.bankTransfer.transfer;

import com.example.bank.auditing.AuditorEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

public class TransferHistory extends AuditorEntity {
    @Id
    @SequenceGenerator(name = "history_sequence", sequenceName = "history_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_generator")
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransferType transferType;
    private Transfer transferHistory;
    private Transfer bankStatement;
    private Transfer standingOrder;
    private Integer clientId;
    private Integer bankAccountId;
    private BigDecimal amount;
}
