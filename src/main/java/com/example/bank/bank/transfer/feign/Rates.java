package com.example.bank.bank.transfer.feign;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Rates {
    private String no;
    private String effectiveDate;
    private BigDecimal mid;
}
