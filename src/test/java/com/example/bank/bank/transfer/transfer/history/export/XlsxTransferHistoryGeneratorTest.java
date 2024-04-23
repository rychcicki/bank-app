package com.example.bank.bank.transfer.transfer.history.export;

import com.example.bank.bank.transfer.transfer.history.TransferHistory;
import com.example.bank.bank.transfer.transfer.history.TransferHistoryService;
import com.example.bank.exception.XlsxGeneratingException;
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

import static com.example.bank.bank.transfer.transfer.history.export.XlsxTransferHistoryGenerator.XLSX_GENERATING_EXCEPTION_MESSAGE;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XlsxTransferHistoryGeneratorTest {
    private final String testSheetName = "Transfer history";
    private final List<String> testHeaderCellTitles = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");
    @Mock
    private TransferHistoryService transferHistoryService;
    @InjectMocks
    private XlsxTransferHistoryGenerator xlsxTransferHistoryGenerator;

    @Test
    void shouldThrowGenerateXlsxTransferHistory() {
        String accountNumber = "WrongAccountNumber123";
        when(xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber))
                .thenThrow(new XlsxGeneratingException(XLSX_GENERATING_EXCEPTION_MESSAGE));
        Assertions.assertThrows(XlsxGeneratingException.class,
                () -> xlsxTransferHistoryGenerator.generateXlsxTransferHistory(accountNumber));
    }

    @Test
    void shouldCheckSheetAndHeaderNamesAreValid() {
        String accountNumber = "PL54613983300568639363795256";
        int testHeaderIndex = 0;
        when(transferHistoryService.transferHistoryForAccountNumber(accountNumber)).thenReturn(new ArrayList<>());
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
        when(transferHistoryService.transferHistoryForAccountNumber(accountNumber)).thenReturn(new ArrayList<>());
        Workbook resultWorkbook = xlsxTransferHistoryGenerator.generateWorkbook(accountNumber,
                testSheetName, testHeaderCellTitles);
        XlsxTransferHistoryGeneratorAssert.assertThat(resultWorkbook)
                .hasValidHeaderStyle(grey25Percent, solidForeground, center);
    }

    @Test
    void shouldFillDataValues() {
        String accountNumber = "PL54613983300568639363795256";
        final List<TransferHistory> dataList = XlsxTransferHistoryGeneratorUtils.transferHistoryCreator();
        when(transferHistoryService.transferHistoryForAccountNumber(accountNumber)).thenReturn(dataList);
        Workbook resultWorkbook = xlsxTransferHistoryGenerator.generateWorkbook(accountNumber,
                testSheetName, testHeaderCellTitles);
        XlsxTransferHistoryGeneratorAssert.assertThat(resultWorkbook).hasValidRowDataValues(dataList);
    }
}
