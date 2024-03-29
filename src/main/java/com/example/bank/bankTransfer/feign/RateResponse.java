package com.example.bank.bankTransfer.feign;

import lombok.Getter;

import java.util.List;

@Getter
public class RateResponse {
    private String table;
    private String currency;
    private String code;
    private List<Rates> rates;
}
