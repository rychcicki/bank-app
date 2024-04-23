package com.example.bank.bank.transfer.transfer.history.export;

import com.example.bank.bank.transfer.transfer.history.TransferHistory;
import com.example.bank.bank.transfer.transfer.history.TransferHistoryService;
import com.example.bank.exception.XlsxGeneratingException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XlsxTransferHistoryGenerator {
    public static final String FILE_NAME_PATTERN = "Transfer history %s.xlsx";
    private final int DATE_COLUMN_WIDTH = 4900;
    public final static String XLSX_GENERATING_EXCEPTION_MESSAGE = "Error generating transfer history XLSX";
    private final TransferHistoryService transferHistoryService;
    private final WorkbookCreator workbookCreator = new WorkbookCreator();

    public ByteArrayOutputStream generateXlsxTransferHistory(String accountNumber) {
        String transferHistorySheetName = WorkbookCreator.transferHistorySheetName;
        List<String> headerCellTitles = WorkbookCreator.headerCellTitles;
        try {
            Workbook transferHistoryXlsx = generateWorkbook(accountNumber, transferHistorySheetName, headerCellTitles);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            transferHistoryXlsx.write(byteArrayOutputStream);
            return byteArrayOutputStream;
        } catch (IOException e) {
            throw new XlsxGeneratingException(XLSX_GENERATING_EXCEPTION_MESSAGE);
        }
    }

    Workbook generateWorkbook(String accountNumber, String sheetName, List<String> headerCellTitles) {
        Workbook workbook = workbookCreator.getWorkbook();
        Sheet sheet = workbookCreator.createSheetWithHeader(sheetName, headerCellTitles);
        int numberOfTransferHistories = transferHistoryService.transferHistoryForAccountNumber(accountNumber).size();
        workbookCreator.autoSizeColumns(sheet, headerCellTitles);
        createDataRows(workbook, numberOfTransferHistories, accountNumber, sheetName);
        return workbook;
    }

    private void createDataRows(Workbook workbook, int rowCount, String accountNumber, String sheetName) {
        CellStyle dateTimeCellStyle = workbookCreator.createDateTimeCellStyle(workbook);
        CellStyle decimalCellStyle = workbookCreator.createDecimalCellStyle(workbook);
        CellStyle defaultWrapTextStyle = workbookCreator.createDefaultWrapTextStyle(workbook);
        List<TransferHistory> transferHistories = transferHistoryService.transferHistoryForAccountNumber(accountNumber);

        Sheet sheet = workbook.getSheet(sheetName);
        final int dataRowIndex = 1;
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.createRow(i + dataRowIndex);
            TransferHistory history = transferHistories.get(i);
            fillRowWithData(row, history, dateTimeCellStyle, decimalCellStyle, defaultWrapTextStyle);
        }
    }

    private void fillRowWithData(Row row, TransferHistory history, CellStyle dateTimeCellStyle,
                                 CellStyle decimalCellStyle, CellStyle defaultWrapTextStyle) {
        Cell cell;
        List<Object> methodsForInsertingData = createMethodsForInsertingDataIntoCells(history);
        for (int i = 0; i < methodsForInsertingData.size(); i++) {
            cell = row.createCell(i);
            Object value = methodsForInsertingData.get(i);
            if (value instanceof LocalDateTime dateTime) {
                cell.setCellValue(dateTime);
                cell.setCellStyle(dateTimeCellStyle);
                row.getSheet().setColumnWidth(i, DATE_COLUMN_WIDTH);
            } else if (value instanceof Long number) {
                cell.setCellValue(number);
            } else if (value instanceof String name) {
                cell.setCellValue(name);
                cell.setCellStyle(defaultWrapTextStyle);
            } else if (value instanceof BigDecimal bigDecimal) {
                cell.setCellValue(bigDecimal.doubleValue());
                cell.setCellStyle(decimalCellStyle);
            }
        }
    }

    @NotNull
    private static List<Object> createMethodsForInsertingDataIntoCells(TransferHistory history) {
        return List.of(history.getCreatedOn(), history.getTransferType().toString(),
                history.getExternalAccountNumber(), history.getTitle(), history.getAmount(), history.getBalance());
    }
}
