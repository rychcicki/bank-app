package com.example.bank.bank.transfer.transfer;

import java.math.BigDecimal;

public record TransferRequest(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount,
                              String title) {
}
