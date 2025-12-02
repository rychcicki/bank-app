package com.example.bank.transfer;

import com.example.bank.client.model.Client;
import com.example.bank.transfer.export.ExportTransferHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
class TransferController {
    private final TransferService transferService;
    private final ExportTransferHistoryService exportTransferHistoryService;

    @PostMapping("/make-transfer")
    void bankTransfer(@RequestBody @Valid TransferRequest transferRequest, @AuthenticationPrincipal Client client) {
        transferService.processBankTransfer(transferRequest, client);
    }

    @PostMapping("/generate-transfer-history/{accountNumber}")
    ResponseEntity<byte[]> generateXlsxTransferHistory(@PathVariable String accountNumber) {
        byte[] byteArray = exportTransferHistoryService.generateXlsxTransferHistory(accountNumber);
        String fileName = String.format(ExportTransferHistoryService.FILE_NAME_PATTERN, accountNumber)
                .replaceAll("[\\r\\n\"]", "_");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(byteArray);
    }
}
