package com.example.bank.transfer.export;

import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.transfer.TransferHistoryRepository;
import com.example.bank.transfer.model.TransferHistory;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.example.bank.transfer.export.WorkbookCreator.headerCellTitles;
import static com.example.bank.transfer.export.WorkbookCreator.transferHistorySheetName;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XlsxTransferHistoryGeneratorTest {
    private final String testSheetName = "Transfer history";
    private final List<String> testHeaderCellTitles = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");

    @Mock
    private TransferHistoryRepository transferHistoryRepository;
    @InjectMocks
    private XlsxTransferHistoryGenerator xlsxTransferHistoryGenerator;

    @Test
    void shouldThrowGenerateXlsxTransferHistory() {
        String accountNumber = "WrongAccountNumber123";

        when(xlsxTransferHistoryGenerator.generateWorkbook(accountNumber, transferHistorySheetName, headerCellTitles))
                .thenThrow(new RestException(ExceptionType.XLSX_GENERATING_EXCEPTION));

        RestException exception = Assertions.assertThrows(RestException.class,
                () -> xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber));
        Assertions.assertEquals(exception.getMessage(), ExceptionType.XLSX_GENERATING_EXCEPTION.getMessage());
    }

    @Test
    void shouldCheckSheetAndHeaderNamesAreValid() {
        String accountNumber = "PL54613983300568639363795256";
        int testHeaderIndex = 0;
        when(transferHistoryRepository.findByAccountNumber(accountNumber)).thenReturn(new ArrayList<>());
        Workbook resultWorkbook = xlsxTransferHistoryGenerator.generateWorkbook(accountNumber,
                testSheetName, testHeaderCellTitles);
        XlsxTransferHistoryGeneratorAssert.assertThat(resultWorkbook)
                .hasValidSheetName(testSheetName)
                .hasValidHeaderName()
                .hasValidHeaderNumber(testHeaderIndex);
    }

    @Test
    void shouldStyleHeader() {
        String accountNumber = "PL54613983300568639363795256";
        IndexedColors grey25Percent = IndexedColors.GREY_25_PERCENT;
        FillPatternType solidForeground = FillPatternType.SOLID_FOREGROUND;
        HorizontalAlignment center = HorizontalAlignment.CENTER;
        when(transferHistoryRepository.findByAccountNumber(accountNumber)).thenReturn(new ArrayList<>());
        Workbook resultWorkbook = xlsxTransferHistoryGenerator.generateWorkbook(accountNumber,
                testSheetName, testHeaderCellTitles);
        XlsxTransferHistoryGeneratorAssert.assertThat(resultWorkbook)
                .hasValidHeaderStyle(grey25Percent, solidForeground, center);
    }

    @Test
    void shouldFillDataValues() {
        String accountNumber = "PL54613983300568639363795256";
        final List<TransferHistory> dataList = XlsxTransferHistoryGeneratorUtils.transferHistoryCreator();
        when(transferHistoryRepository.findByAccountNumber(accountNumber)).thenReturn(dataList);
        Workbook resultWorkbook = xlsxTransferHistoryGenerator.generateWorkbook(accountNumber,
                testSheetName, testHeaderCellTitles);
        XlsxTransferHistoryGeneratorAssert.assertThat(resultWorkbook).hasValidRowDataValues(dataList);
    }
}
