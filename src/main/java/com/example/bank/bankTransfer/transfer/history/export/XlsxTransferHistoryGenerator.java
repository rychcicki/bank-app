package com.example.bank.bankTransfer.transfer.history.export;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
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

@Service
@RequiredArgsConstructor
public class XlsxTransferHistoryGenerator {
    private static final String TRANSFER_HISTORY_SHEET_NAME = "Transfer history";
    private static final String FILE_NAME_PATTERN = "Transfer history client_%d.xlsx";
    private static final int DATE_COLUMN_WIDTH = 4900;
    private static final int HORIZONTAL_PADDING = 200;
    static final List<String> headerCellTitles = List.of("No.", "Created on", "Client ID", "Transfer type",
            "Bank account numer", "Title of transfer", "Amount", "Balance");
    private final AccountRepository accountRepository;
    private final TransferHistoryService transferHistoryService;

    public void generateXlsxTransferHistory(Long clientId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheet(workbook);
            Row headerRow = createHeader(sheet);
            fillHeaderWithData(headerCellTitles, workbook, headerRow, sheet);
            Integer rowSize = transferHistoryService.transferHistoryForClient(clientId).size();
            Sheet rows = createRows(sheet, rowSize);
            fillRowWithData(clientId, transferHistoryService, accountRepository, rows, workbook);
            saveWorkbookToFile(workbook, clientId);
        } catch (IOException ex) {
            throw new RuntimeException("Error generating transfer history XLSX", ex);
        }
    }

    private Sheet createSheet(Workbook workbook) {
        return workbook.createSheet(TRANSFER_HISTORY_SHEET_NAME);
    }

    private Sheet createRows(Sheet sheet, Integer rowSize) {
        for (int i = 1; i <= rowSize; i++) {
            sheet.createRow(i);
        }
        return sheet;
    }

    private Row createHeader(Sheet sheet) {
        return sheet.createRow(0);
    }

    private void fillHeaderWithData(List<String> headerList, Workbook workbook, Row headerRow, Sheet sheet) {
        CellStyle headerStyle = createStyleForHeader(workbook);
        for (int i = 0; i < headerList.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerList.get(i));
            headerCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + HORIZONTAL_PADDING);
        }
    }

    private void fillRowWithData(Long clientId, TransferHistoryService transferHistoryService,
                                 AccountRepository accountRepository, Sheet rows, Workbook workbook) {
        CellStyle dateTimeCellStyle = createDateTimeCellStyle(workbook);
        CellStyle decimalCellStyle = createDecimalCellStyle(workbook);
        CellStyle defaultWrapTextStyle = createDefaultWrapTextStyle(workbook);

        List<TransferHistory> listOfTransferHistory = transferHistoryService.transferHistoryForClient(clientId);
        int rowDataIndex = 1;
        for (TransferHistory history : listOfTransferHistory) {
            Long bankAccountId = history.getBankAccountId();
            Account account = accountRepository.findById(bankAccountId).orElseThrow();
            Cell cell = null;

            List<Object> methodsForInsertingData = methodsForInsertingDataIntoCells(history, account);

            for (int i = 0; i < methodsForInsertingData.size(); i++) {
                cell = rows.getRow(rowDataIndex).createCell(i);
                if (methodsForInsertingData.get(i) instanceof LocalDateTime dateTime) {
                    cell.setCellValue(dateTime);
                    cell.setCellStyle(dateTimeCellStyle);
                    rows.setColumnWidth(i, DATE_COLUMN_WIDTH);
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
            rowDataIndex++;
        }
    }

    @NotNull
    private static List<Object> methodsForInsertingDataIntoCells(TransferHistory history, Account account) {
        return List.of(history.getId(), history.getCreatedOn(), history.getClientId(),
                history.getTransferType().toString(),
                account.getAccountNumber(), history.getTitle(), history.getAmount(), history.getBalance());
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

    private void saveWorkbookToFile(Workbook workbook, Long clientId) {
        String fileName = String.format(FILE_NAME_PATTERN, clientId);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
