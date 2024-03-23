package com.example.bank.bankTransfer.transfer;

import com.example.bank.bankTransfer.transfer.history.export.XlsxTransferHistoryGenerator;
import com.example.bank.client.ClientService;
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

    @PostMapping("/generate-transfer-history/{clientId}")
    ResponseEntity<Void> generateXlsxTransferHistory(@PathVariable Long clientId) {
        xlsxTransferHistoryGenerator.generateXlsxTransferHistory(clientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
