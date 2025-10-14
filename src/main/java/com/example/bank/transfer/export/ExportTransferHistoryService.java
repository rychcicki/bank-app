package com.example.bank.transfer.export;

import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.TransferHistoryRepository;
import com.example.bank.transfer.model.TransferHistory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public final class ExportTransferHistoryService {
    public static final String FILE_NAME_PATTERN = "Transfer history %s.xlsx";
    private static final List<String> HEADER_CELL_TITLES = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");
    private final TransferHistoryRepository transferHistoryRepository;

    public byte[] generateXlsxTransferHistory(String accountNumber) {
        List<TransferHistory> transferHistory = findTransferHistoryIfNotEmpty(accountNumber);
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                WorkbookCreator workbookCreator = new WorkbookCreator(createXlsxWorkbook())
        ) {
            new ExportToXlsx(workbookCreator, HEADER_CELL_TITLES, transferHistory).export();
            workbookCreator.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new RestException(ExceptionType.XLSX_GENERATING_EXCEPTION, ex);
        }
    }

    private List<TransferHistory> findTransferHistoryIfNotEmpty(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new RestException(ExceptionType.INVALID_ACCOUNT_NUMBER_EXCEPTION);
        }
        List<TransferHistory> transferHistory = transferHistoryRepository.findByAccountNumber(accountNumber);
        if (transferHistory.isEmpty()) {
            throw new RestException(ExceptionType.TRANSFER_HISTORY_NOT_FOUND);
        }
        return transferHistory;
    }

    private Workbook createXlsxWorkbook() {
        return new XSSFWorkbook();
    }
}
