package com.example.bank.bank.transfer.transfer.history.export;

import com.example.bank.bank.transfer.transfer.history.TransferHistory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class XlsxTransferHistoryGeneratorAssert {
    private final int headerIndex = 0;
    private final Workbook workbook;
    private final String transferHistorySheetName = WorkbookCreator.transferHistorySheetName;
    private final List<String> headerCellTitles = WorkbookCreator.headerCellTitles;

    static XlsxTransferHistoryGeneratorAssert assertThat(Workbook workbook) {
        return new XlsxTransferHistoryGeneratorAssert(workbook);
    }

    XlsxTransferHistoryGeneratorAssert hasValidSheetName(String testSheetName) {
        Sheet sheet = workbook.getSheet(testSheetName);
        assertNotNull(sheet);
        return this;
    }

    XlsxTransferHistoryGeneratorAssert hasValidHeaderName() {
        Row headerRow = workbook.getSheet(transferHistorySheetName).getRow(headerIndex);
        List<String> titles = new ArrayList<>();
        IntStream.range(headerRow.getFirstCellNum(), headerRow.getLastCellNum()).forEach(columnIndex -> {
            Cell cell = headerRow.getCell(columnIndex);
            titles.add(cell.getStringCellValue());
        });
        Assertions.assertThat(titles).isEqualTo(headerCellTitles);
        return this;
    }

    XlsxTransferHistoryGeneratorAssert hasValidHeaderNumber(int testHeaderIndex) {
        short lastCellNum = workbook.getSheet(transferHistorySheetName).getRow(testHeaderIndex).getLastCellNum();
        short expectedLastCellNum = (short) headerCellTitles.size();
        assertAll(
                () -> assertEquals(testHeaderIndex, headerIndex),
                () -> assertEquals(lastCellNum, expectedLastCellNum));
        return this;
    }

    XlsxTransferHistoryGeneratorAssert hasValidHeaderStyle(IndexedColors foregroundColor, FillPatternType fillPatternType,
                                                           HorizontalAlignment horizontalAlignment) {
        Row header = workbook.getSheet(transferHistorySheetName).getRow(headerIndex);
        List<CellStyle> headerStyles = new ArrayList<>();
        IntStream.range(header.getFirstCellNum(), header.getLastCellNum())
                .forEach(columnIndex -> {
                    CellStyle cellStyle = header.getCell(columnIndex).getCellStyle();
                    headerStyles.add(cellStyle);
                });
        for (CellStyle cellStyle : headerStyles) {
            short fillForegroundColor = cellStyle.getFillForegroundColor();
            FillPatternType fillPattern = cellStyle.getFillPattern();
            HorizontalAlignment alignment = cellStyle.getAlignment();
            Font font = workbook.getFontAt(cellStyle.getFontIndex());

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(fillForegroundColor).isEqualTo(foregroundColor.getIndex());
                softly.assertThat(fillPattern).isEqualTo(fillPatternType);
                softly.assertThat(alignment).isEqualTo(horizontalAlignment);
                softly.assertThat(font.getBold()).isTrue();
            });
        }
        return this;
    }

    XlsxTransferHistoryGeneratorAssert hasValidRowDataValues(List<TransferHistory> dataList) {
        int dataRowIndex = 1;
        int lastRowNum = workbook.getSheet(transferHistorySheetName).getLastRowNum();
        assertEquals(dataList.size(), lastRowNum);

        for (TransferHistory transferHistory : dataList) {
            Row row = workbook.getSheet(transferHistorySheetName).getRow(dataRowIndex);
            dataRowIndex++;
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(row.getCell(0).getLocalDateTimeCellValue())
                        .isEqualTo(transferHistory.getCreatedOn());
                softly.assertThat(row.getCell(1).getStringCellValue())
                        .isEqualTo(transferHistory.getTransferType().toString());
                softly.assertThat(row.getCell(2).getStringCellValue())
                        .isEqualTo(transferHistory.getExternalAccountNumber());
                softly.assertThat(row.getCell(3).getStringCellValue())
                        .isEqualTo(transferHistory.getTitle());
                softly.assertThat(row.getCell(4).getNumericCellValue())
                        .isEqualTo(transferHistory.getAmount().doubleValue());
                softly.assertThat(row.getCell(5).getNumericCellValue())
                        .isEqualTo(transferHistory.getBalance().doubleValue());
            });
        }
        return this;
    }
}
