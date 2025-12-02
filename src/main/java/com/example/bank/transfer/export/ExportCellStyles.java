package com.example.bank.transfer.export;

import lombok.Getter;
import org.apache.poi.ss.usermodel.*;

@Getter
final class ExportCellStyles {
    private static final String EXCEL_DATETIME_FORMAT = "yyyy-mm-dd hh:mm:ss";
    private static final String EXCEL_DECIMAL_FORMAT = "#,##0.00";

    private final CellStyle headerCellStyle;
    private final CellStyle wrappedTextCellStyle;
    private final CellStyle decimalCellStyle;
    private final CellStyle dateTimeCellStyle;

    ExportCellStyles(Workbook workbook) {
        this.headerCellStyle = CellStyleUtils.createHeaderStyle(
                workbook,
                IndexedColors.GREY_25_PERCENT,
                FillPatternType.SOLID_FOREGROUND,
                HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER);

        this.wrappedTextCellStyle = CellStyleUtils.createWrapTextStyle(
                workbook,
                HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER);

        this.decimalCellStyle = CellStyleUtils.createDataFormatStyle(
                workbook,
                EXCEL_DECIMAL_FORMAT,
                HorizontalAlignment.RIGHT,
                VerticalAlignment.CENTER);

        this.dateTimeCellStyle = CellStyleUtils.createDataFormatStyle(
                workbook,
                EXCEL_DATETIME_FORMAT,
                HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER);
    }
}
