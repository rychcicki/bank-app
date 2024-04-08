package com.example.bank.bank.transfer.feign;

import com.example.bank.bank.transfer.account.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rateClient", url = "${spring.cloud.openfeign.client.config.rateClient.url}")
public interface RateClient {
    @GetMapping(value = "/{code}")
    RateResponse getCurrencyRate(@PathVariable Currency code);
}
