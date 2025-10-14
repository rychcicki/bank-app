package com.example.bank.transfer.export;

import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.TransferHistoryRepository;
import com.example.bank.transfer.model.TransferHistory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.example.bank.transfer.export.XlsxTransferHistoryGeneratorUtils.transferHistoryCreator;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportTransferHistoryServiceTest {
    private static final String ACCOUNT_NUMBER = "PL27722968758620190053098782";
    private static final String INVALID_EMPTY_ACCOUNT_NUMBER = " ";
    private static final String SHEET_NAME = "Transfer history";
    private static final List<String> HEADER_CELL_TITLES = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");

    @Mock
    private TransferHistoryRepository transferHistoryRepository;

    @InjectMocks
    private ExportTransferHistoryService exportService;

    @Test
    void shouldGenerateXlsxTransferHistoryHeaderAndData() throws IOException {
        final List<TransferHistory> transferHistories = transferHistoryCreator();
        final int headerRowIndex = 0;
        final int headerRowCount = 1;

        when(transferHistoryRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(transferHistories);

        byte[] byteArray = exportService.generateXlsxTransferHistory(ACCOUNT_NUMBER);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(byteArray))
        ) {
            Assertions.assertThat(workbook.getNumberOfSheets()).isOne();

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            Assertions.assertThat(sheet).isNotNull();

            Row headerRow = sheet.getRow(headerRowIndex);
            Assertions.assertThat(headerRow.getPhysicalNumberOfCells()).isEqualTo(HEADER_CELL_TITLES.size());

            int expectedRows = transferHistories.size() + headerRowCount;
            Assertions.assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(expectedRows);
        }
    }

    @Test
    void shouldThrowRestExceptionWhenTransferHistoryNotFound() {
        when(transferHistoryRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Collections.emptyList());

        Assertions.assertThatThrownBy(() -> exportService.generateXlsxTransferHistory(ACCOUNT_NUMBER))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.TRANSFER_HISTORY_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowRestExceptionWhenAccountNumberIsNull() {
        Assertions.assertThatThrownBy(() -> exportService.generateXlsxTransferHistory(null))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.INVALID_ACCOUNT_NUMBER_EXCEPTION.getMessage());

        verifyNoInteractions(transferHistoryRepository);
    }

    @Test
    void shouldThrowRestExceptionWhenAccountNumberIsBlank() {
        Assertions.assertThatThrownBy(() -> exportService.generateXlsxTransferHistory(INVALID_EMPTY_ACCOUNT_NUMBER))
                .isInstanceOf(RestException.class)
                .hasMessage(ExceptionType.INVALID_ACCOUNT_NUMBER_EXCEPTION.getMessage());

        verifyNoInteractions(transferHistoryRepository);
    }
}
