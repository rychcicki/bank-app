package com.example.bank.transfer.export;

import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

final class WorkbookCreator implements Closeable {
    @Getter
    private final Workbook workbook;
    private final ExportCellStyles exportCellStyles;

    WorkbookCreator(Workbook workbook) {
        this.workbook = requireNonNull(workbook, "workbook cannot be null");
        this.exportCellStyles = new ExportCellStyles(this.workbook);
    }

    XlsxSheet createSheet(String sheetName) {
        sheetName = WorkbookUtil.createSafeSheetName(sheetName, '_');
        if (workbook.getSheetIndex(sheetName) >= 0) {
            throw new IllegalArgumentException("Sheet \"" + sheetName + "\" already exists in workbook");
        }
        return new XlsxSheet(workbook.createSheet(sheetName), exportCellStyles);
    }

    void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    static final class XlsxSheet {
        private final Sheet sheet;
        private final ExportCellStyles exportCellStyles;
        private int rowIndex = 0;
        private int headerCellsCount = -1;

        XlsxSheet(Sheet sheet, ExportCellStyles exportCellStyles) {
            this.sheet = sheet;
            this.exportCellStyles = exportCellStyles;
        }

        public XlsxRow createRow() {
            return new XlsxRow(sheet.createRow(rowIndex++), exportCellStyles);
        }

        public XlsxSheet createStyledHeader(List<String> headerCellTitles) {
            if (headerCellTitles == null || headerCellTitles.isEmpty()) {
                throw new IllegalArgumentException("headerCellTitles cannot be null or empty");
            }

            Row headerRow = sheet.createRow(rowIndex++);
            IntStream.range(0, headerCellTitles.size())
                    .forEach(columnIndex -> {
                        Cell cell = headerRow.createCell(columnIndex);
                        cell.setCellValue(headerCellTitles.get(columnIndex));
                        cell.setCellStyle(exportCellStyles.getHeaderCellStyle());
                    });
            headerCellsCount = headerCellTitles.size();
            return this;
        }

        void adjustColumnWidth() {
            if (headerCellsCount < 0) {
                throw new IllegalStateException("createStyledHeader must be called before adjustColumnWidth");
            }
            CellStyleUtils.autoSizeColumns(sheet, headerCellsCount);
        }
    }

    static final class XlsxRow {
        private final Row row;
        private final ExportCellStyles exportCellStyles;
        private int cellIndex = 0;

        XlsxRow(Row row, ExportCellStyles exportCellStyles) {
            this.row = row;
            this.exportCellStyles = exportCellStyles;
        }

        XlsxRow addValues(List<Object> values) {
            if (values == null || values.isEmpty()) {
                throw new IllegalArgumentException("values cannot be null or empty");
            }

            values.forEach(this::fillCell);
            return this;
        }

        void fillCell(Object cellValue) {
            Cell cell = row.createCell(cellIndex++);
            CellStyle textStyle = exportCellStyles.getWrappedTextCellStyle();
            switch (cellValue) {
                case null -> {
                    cell.setCellValue("");
                    cell.setCellStyle(textStyle);
                }
                case String name -> {
                    cell.setCellValue(name);
                    cell.setCellStyle(textStyle);
                }
                case LocalDateTime dateTime -> {
                    cell.setCellValue(dateTime);
                    cell.setCellStyle(exportCellStyles.getDateTimeCellStyle());
                }
                case Number number -> {
                    cell.setCellValue(number.doubleValue());
                    cell.setCellStyle(exportCellStyles.getDecimalCellStyle());
                }
                default -> {
                    cell.setCellValue(cellValue.toString());
                    cell.setCellStyle(textStyle);
                }
            }
        }
    }
}
