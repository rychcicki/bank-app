package com.example.bank.bankTransfer.transfer;

import java.math.BigDecimal;

public record TransferRequest(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount,
                              String title) {
}
