package com.example.bank.bankTransfer.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
class TransferController {
    private final TransferService transferService;

    @PostMapping("/make-transfer")
    ResponseEntity<Void> bankTransfer(@RequestBody TransferRequest transferRequest) {
        transferService.bankTransfer(transferRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
