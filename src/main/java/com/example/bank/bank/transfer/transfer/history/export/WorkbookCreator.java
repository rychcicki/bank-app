package com.example.bank.bank.transfer.transfer.history.export;

import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

@Getter
public class WorkbookCreator {
    static final String transferHistorySheetName = "Transfer history";
    static final List<String> headerCellTitles = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");
    private final int horizontalPadding = 200;
    private final int headerIndex = 0;
    private final Workbook workbook = new XSSFWorkbook();

    @NotNull
    Sheet createSheetWithHeader(String sheetName, List<String> headerCellTitles) {
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex >= 0) {
            workbook.removeSheetAt(sheetIndex);
        }
        Sheet sheet = workbook.createSheet(sheetName);
        createHeaderRow(sheet, headerCellTitles);
        return sheet;
    }

    private void createHeaderRow(Sheet sheet, List<String> headerTitles) {
        Row headerRow = sheet.createRow(headerIndex);
        CellStyle headerStyle = createStyleForHeader(sheet.getWorkbook());
        IntStream.range(0, headerTitles.size()).forEach(columnIndex -> {
            Cell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(headerTitles.get(columnIndex));
            cell.setCellStyle(headerStyle);
        });
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

    void autoSizeColumns(Sheet sheet, List<String> headerCellTitles) {
        IntStream.range(0, headerCellTitles.size()).forEach(columnIndex -> {
            sheet.autoSizeColumn(columnIndex);
            sheet.setColumnWidth(columnIndex, sheet.getColumnWidth(columnIndex) + horizontalPadding);
        });
    }

    @NotNull
    CellStyle createDefaultWrapTextStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    @NotNull
    CellStyle createDateTimeCellStyle(Workbook workbook) {
        String dbFormatPattern = "YYYY-MMM-dd HH:mm:ss";
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        DataFormat dateTimeFormat = workbook.createDataFormat();
        style.setDataFormat(dateTimeFormat.getFormat(dbFormatPattern));
        return style;
    }

    @NotNull
    CellStyle createDecimalCellStyle(Workbook workbook) {
        String decimalFormatPattern = "#,##0.00";
        CellStyle decimalStyle = workbook.createCellStyle();
        DataFormat decimalFormat = workbook.createDataFormat();
        decimalStyle.setDataFormat(decimalFormat.getFormat(decimalFormatPattern));
        return decimalStyle;
    }
}
