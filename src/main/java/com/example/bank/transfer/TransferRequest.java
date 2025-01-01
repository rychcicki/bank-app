package com.example.bank.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

record TransferRequest(@NotBlank String senderAccountNumber, @NotBlank String receiverAccountNumber,
                       @DecimalMin("0.01") BigDecimal amount, @NotBlank String title) {
}
