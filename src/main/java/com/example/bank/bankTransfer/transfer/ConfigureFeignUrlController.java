package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.account.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfigureFeignUrlController {
    private final RateClient rateClient;

    @GetMapping(name = "rateClient", value = "{code}")
    RateResponse getCurrencyRate(@PathVariable Currency code) {
        return rateClient.getCurrencyRate(code);
    }
}
