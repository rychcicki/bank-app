package com.example.bank.bank.transfer.transfer;

import com.example.bank.bank.transfer.transfer.history.export.XlsxTransferHistoryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.bank.bank.transfer.transfer.history.export.XlsxTransferHistoryGenerator.FILE_NAME_PATTERN;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final XlsxTransferHistoryGenerator xlsxTransferHistoryGenerator;

    @PostMapping("/make-transfer")
    ResponseEntity<Void> bankTransfer(@RequestBody TransferRequest transferRequest) {
        transferService.bankTransfer(transferRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/generate-transfer-history/{accountNumber}")
    @ResponseBody
    ResponseEntity<byte[]> generateXlsxTransferHistory(@PathVariable String accountNumber) {
        byte[] byteArray = xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber).toByteArray();
        String fileName = String.format(FILE_NAME_PATTERN, accountNumber);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(byteArray);
    }
}
