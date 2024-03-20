package com.example.bank.bankTransfer.transfer;

import java.math.BigDecimal;

public record TransferRequest(Long senderAccountId, Long receiverAccountId, BigDecimal amount, String title) {
}
