package com.example.bank.transfer.export;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class CellStyleUtils {
    private static final int HORIZONTAL_PADDING = 400;
    private static final int MAX_COLUMN_WIDTH = 255 * 256;

    static void autoSizeColumns(Sheet sheet, int numberOfColumns) {
        IntStream
                .range(0, numberOfColumns)
                .forEach(columnIndex -> {
                    sheet.autoSizeColumn(columnIndex);
                    sheet.setColumnWidth(columnIndex,
                            Math.min(sheet.getColumnWidth(columnIndex) + HORIZONTAL_PADDING, MAX_COLUMN_WIDTH));
                });
    }

    static CellStyle createHeaderStyle(Workbook workbook, IndexedColors indexedColor, FillPatternType fillPatternType,
                                       HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(indexedColor.getIndex());
        headerStyle.setFillPattern(fillPatternType);
        setAlignment(headerStyle, hAlign, vAlign);
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    static CellStyle createWrapTextStyle(Workbook workbook, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        setAlignment(cellStyle, hAlign, vAlign);
        return cellStyle;
    }

    static CellStyle createDataFormatStyle(Workbook workbook, String pattern, HorizontalAlignment hAlign,
                                           VerticalAlignment vAlign) {
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat(pattern));
        setAlignment(cellStyle, hAlign, vAlign);
        return cellStyle;
    }

    private static void setAlignment(CellStyle style, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
    }
}
