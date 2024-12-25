package com.example.bank.transfer;

import com.example.bank.transfer.export.XlsxTransferHistoryGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
class TransferController {
    private final TransferService transferService;
    private final XlsxTransferHistoryGenerator xlsxTransferHistoryGenerator;

    @PostMapping("/make-transfer")
    void bankTransfer(@Valid @RequestBody TransferRequest transferRequest, Principal connectedUser) {
        transferService.processBankTransfer(transferRequest, connectedUser);
    }

    @PostMapping("/generate-transfer-history/{accountNumber}")
    @ResponseBody
    ResponseEntity<byte[]> generateXlsxTransferHistory(@PathVariable String accountNumber) {
        byte[] byteArray = xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber).toByteArray();
        String fileName = String.format(XlsxTransferHistoryGenerator.FILE_NAME_PATTERN, accountNumber);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(byteArray);
    }
}
