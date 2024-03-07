package com.example.bank.bankTransfer.transfer;

import com.example.bank.client.jpa.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Column(name = "account_number_id", nullable = false)
    private Long accountNumberId;
    @Column(nullable = false)
    @NotBlank(message = "beneficiary name is mandatory")
    private String beneficiaryName;
    @Embedded
    private Address beneficiaryAddress;
    @Column(name = "beneficiary_account_number_id", nullable = false)
    private Long beneficiaryAccountNumberId;
    @Column(name = "title_of_transfer", nullable = false)
    @NotBlank(message = "title of transfer is mandatory")
    private String title;
    @Column(nullable = false)
    @Min(1L)
    private BigDecimal amount;
    @Column(name = "delivery_time", nullable = false)
    @DateTimeFormat
    private LocalDate deliveryTime;
    @Column(name = "transfer_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferType transferType;
}
