package com.example.bank.transfer.model;

import com.example.bank.auditing.AuditorEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class TransferHistory extends AuditorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransferType transferType;

    @NotNull
    private Long clientId;

    private String accountNumber;

    private String externalAccountNumber;

    @NotNull
    private BigDecimal previousBalance;

    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balance;

    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferHistory that = (TransferHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
