package com.example.bank.bankTransfer.transfer.history.export;

import com.example.bank.bankTransfer.transfer.history.TransferHistory;
import com.example.bank.bankTransfer.transfer.history.TransferHistoryService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class XlsxTransferHistoryGenerator {
    private static final String TRANSFER_HISTORY_SHEET_NAME = "Transfer history";
    private static final String FILE_NAME_PATTERN = "Transfer history %s.xlsx";
    private static final int DATE_COLUMN_WIDTH = 4900;
    private static final int HORIZONTAL_PADDING = 200;
    private static final List<String> HEADER_CELL_TITLES = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");
    private final TransferHistoryService transferHistoryService;

    public void generateXlsxTransferHistory(String accountNumber) {
        try (Workbook workbook = new XSSFWorkbook()) {
            initializeWorkbook(workbook, accountNumber);
        } catch (IOException ex) {
            throw new RuntimeException("Error generating transfer history XLSX", ex);
        }
    }

    private void initializeWorkbook(Workbook workbook, String accountNumber) {
        Sheet sheet = workbook.createSheet(TRANSFER_HISTORY_SHEET_NAME);
        createHeaderRow(sheet);
        int numberOfTransferHistories = transferHistoryService.transferHistoryForAccountNumber(accountNumber).size();
        autoSizeColumns(sheet);
        createDataRows(workbook, numberOfTransferHistories, accountNumber);
        saveWorkbookToFile(workbook, accountNumber);
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createStyleForHeader(sheet.getWorkbook());
        IntStream.range(0, HEADER_CELL_TITLES.size()).forEach(columnIndex -> {
            Cell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(HEADER_CELL_TITLES.get(columnIndex));
            cell.setCellStyle(headerStyle);
        });
    }

    private void autoSizeColumns(Sheet sheet) {
        IntStream.range(0, HEADER_CELL_TITLES.size()).forEach(columnIndex -> {
            sheet.autoSizeColumn(columnIndex);
            sheet.setColumnWidth(columnIndex, sheet.getColumnWidth(columnIndex) + HORIZONTAL_PADDING);
        });
    }

    private void createDataRows(Workbook workbook, int rowCount, String accountNumber) {
        CellStyle dateTimeCellStyle = createDateTimeCellStyle(workbook);
        CellStyle decimalCellStyle = createDecimalCellStyle(workbook);
        CellStyle defaultWrapTextStyle = createDefaultWrapTextStyle(workbook);
        List<TransferHistory> transferHistories = transferHistoryService.transferHistoryForAccountNumber(accountNumber);
        Sheet sheet = workbook.getSheet(TRANSFER_HISTORY_SHEET_NAME);
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.createRow(i + 1);
            TransferHistory history = transferHistories.get(i);
            fillRowWithData(row, history, dateTimeCellStyle, decimalCellStyle, defaultWrapTextStyle);
        }
    }

    private void fillRowWithData(Row row, TransferHistory history, CellStyle dateTimeCellStyle,
                                 CellStyle decimalCellStyle, CellStyle defaultWrapTextStyle) {
        Cell cell = null;
        List<Object> methodsForInsertingData = createMethodsForInsertingDataIntoCells(history);
        for (int i = 0; i < methodsForInsertingData.size(); i++) {
            cell = row.createCell(i);
            if (methodsForInsertingData.get(i) instanceof LocalDateTime dateTime) {
                cell.setCellValue(dateTime);
                cell.setCellStyle(dateTimeCellStyle);
                row.getSheet().setColumnWidth(i, DATE_COLUMN_WIDTH);
            } else if (methodsForInsertingData.get(i) instanceof Long number) {
                cell.setCellValue(number);
            } else if (methodsForInsertingData.get(i) instanceof String name) {
                cell.setCellValue(name);
                cell.setCellStyle(defaultWrapTextStyle);
            } else if (methodsForInsertingData.get(i) instanceof BigDecimal bigDecimal) {
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

    @NotNull
    private CellStyle createStyleForHeader(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    @NotNull
    private static CellStyle createDefaultWrapTextStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    @NotNull
    private static CellStyle createDateTimeCellStyle(Workbook workbook) {
        String dbFormatPattern = "YYYY-MMM-dd HH:mm:ss";
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        DataFormat dateTimeFormat = workbook.createDataFormat();
        style.setDataFormat(dateTimeFormat.getFormat(dbFormatPattern));
        return style;
    }

    @NotNull
    private static CellStyle createDecimalCellStyle(Workbook workbook) {
        String decimalFormatPattern = "#,##0.00";
        CellStyle decimalStyle = workbook.createCellStyle();
        DataFormat decimalFormat = workbook.createDataFormat();
        decimalStyle.setDataFormat(decimalFormat.getFormat(decimalFormatPattern));
        return decimalStyle;
    }

    private void saveWorkbookToFile(Workbook workbook, String accountNumber) {
        String fileName = String.format(FILE_NAME_PATTERN, accountNumber);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
