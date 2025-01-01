package com.example.bank.transfer.feign;

import java.util.List;

public record RateResponse(String table, String currency, String code, List<Rates> rates) {
}
