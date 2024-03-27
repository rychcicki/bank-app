package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.transfer.history.export.XlsxTransferHistoryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
class TransferController {
    private final TransferService transferService;
    private final XlsxTransferHistoryGenerator xlsxTransferHistoryGenerator;

    @PostMapping("/make-transfer")
    ResponseEntity<Void> bankTransfer(@RequestBody TransferRequest transferRequest) {
        transferService.bankTransfer(transferRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/generate-transfer-history/{accountNumber}")
    ResponseEntity<Void> generateXlsxTransferHistory(@PathVariable String accountNumber) {
        xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
