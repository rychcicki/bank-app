package com.example.bank.bankTransfer.feign;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Rates {
    private String no;
    private String effectiveDate;
    private BigDecimal mid;
}
