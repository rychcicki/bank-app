package com.example.bank.transfer;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

record TransferRequest(
        @NotBlank(message = "sender account number is mandatory")
        String senderAccountNumber,

        @NotBlank(message = "receiver account number is mandatory")
        String receiverAccountNumber,

        @NotNull
        @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "title of transfer is mandatory")
        String title) {

    @AssertTrue(message = "sender and receiver account numbers must differ")
    private boolean isSenderDifferentFromReceiver() {
        return !senderAccountNumber.equals(receiverAccountNumber);
    }
}
