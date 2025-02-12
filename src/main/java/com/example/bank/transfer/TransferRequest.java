package com.example.bank.transfer;

import java.math.BigDecimal;

record TransferRequest(String senderAccountNumber, String receiverAccountNumber,
                       BigDecimal amount, String title) {
}
