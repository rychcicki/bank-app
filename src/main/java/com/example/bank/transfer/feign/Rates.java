package com.example.bank.transfer.feign;

import java.math.BigDecimal;

public record Rates(String no, String effectiveDate, BigDecimal mid) {
}
