package com.example.bank.transfer.export;

import com.example.bank.transfer.model.TransferHistory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkbookTransferHistoryAssert {
    private static final int HEADER_INDEX = 0;
    private final Workbook workbook;
    static final String SHEET_NAME = "Transfer history";
    private Sheet sheet;

    public static WorkbookTransferHistoryAssert assertThat(Workbook workbook) {
        return new WorkbookTransferHistoryAssert(workbook);
    }

    WorkbookTransferHistoryAssert hasValidSheetName(String sheetName) {
        String expected = WorkbookUtil.createSafeSheetName(
                requireNonNull(sheetName, "sheetName cannot be null"), '_');

        sheet = workbook.getSheet(expected);
        Assertions.assertThat(sheet)
                .as("sheet %s not found", expected)
                .isNotNull();
        return this;
    }

    WorkbookTransferHistoryAssert hasValidHeaderTitlesAndColumnCount(int expectedHeaderIndex,
                                                                     List<String> expectedHeaderTitles) {
        Row headerRow = requireNonNull(
                sheet.getRow(expectedHeaderIndex), "Header row " + expectedHeaderIndex + " is null");

        int physicalColumnCount = headerRow.getPhysicalNumberOfCells();
        int lastCellIndexPlusOne = headerRow.getLastCellNum();
        int expectedColumnCount = expectedHeaderTitles.size();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(physicalColumnCount)
                    .as("Header row %s is empty", expectedHeaderIndex)
                    .isGreaterThan(0);
            softly.assertThat(physicalColumnCount)
                    .as("Header column count does not match expected")
                    .isEqualTo(expectedColumnCount);
            softly.assertThat(lastCellIndexPlusOne - physicalColumnCount)
                    .as("Header has gaps (physical = %s, lastCellIndexPlusOne = %s)",
                            physicalColumnCount, lastCellIndexPlusOne)
                    .isZero();
        });

        int firstCellNum = headerRow.getFirstCellNum();

        List<String> titles = IntStream.range(firstCellNum, lastCellIndexPlusOne)
                .mapToObj(i -> {
                    Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Assertions.assertThat(cell)
                            .as("Header cell %s is null/blank", i)
                            .isNotNull();
                    return cell.getStringCellValue();
                })
                .toList();

        Assertions.assertThat(titles).isEqualTo(expectedHeaderTitles);
        return this;
    }

    WorkbookTransferHistoryAssert hasValidHeaderStyle(IndexedColors expectedForegroundColor,
                                                      FillPatternType expectedFillPatternType,
                                                      HorizontalAlignment expectedHorizontalAlignment) {
        Row header = requireNonNull(sheet.getRow(HEADER_INDEX), "Header row " + HEADER_INDEX + " is null");

        for (int i = 0; i < header.getLastCellNum(); i++) {
            CellStyle cellStyle = requireNonNull(header.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL),
                    "Header cell " + i + " is null").getCellStyle();

            Font font = workbook.getFontAt(cellStyle.getFontIndex());

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(cellStyle.getFillForegroundColor()).isEqualTo(expectedForegroundColor.getIndex());
                softly.assertThat(cellStyle.getFillPattern()).isEqualTo(expectedFillPatternType);
                softly.assertThat(cellStyle.getAlignment()).isEqualTo(expectedHorizontalAlignment);
                softly.assertThat(font.getBold()).isTrue();
            });
        }
        return this;
    }

    WorkbookTransferHistoryAssert hasValidRowDataValues(List<TransferHistory> dataList) {
        requireNonNull(dataList, "dataList cannot be null");
        Row header = requireNonNull(sheet.getRow(HEADER_INDEX), "header row cannot be null");

        int headerRowCount = 1;
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows() - headerRowCount;
        assertEquals(dataList.size(), physicalNumberOfRows, "Data row count does not match expected");

        int dataRowIndex = 1;
        int cols = header.getPhysicalNumberOfCells();
        for (TransferHistory transferHistory : dataList) {
            Row row = requireNonNull(sheet.getRow(dataRowIndex), "Row " + dataRowIndex + " is null");
            assertEquals(header.getPhysicalNumberOfCells(), row.getPhysicalNumberOfCells(),
                    "Row " + dataRowIndex + " column count does not match header");

            final int rowIndex = dataRowIndex;
            List<Cell> cells = IntStream.range(0, cols)
                    .mapToObj(i -> requireNonNull(row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL),
                            "Cell (row index= " + rowIndex + ", column= " + i + ") is null"))
                    .toList();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(cells.get(0).getLocalDateTimeCellValue()).isEqualTo(transferHistory.getCreatedOn());
                softly.assertThat(cells.get(1).getStringCellValue()).isEqualTo(transferHistory.getTransferType().name());
                softly.assertThat(cells.get(2).getStringCellValue()).isEqualTo(transferHistory.getExternalAccountNumber());
                softly.assertThat(cells.get(3).getStringCellValue()).isEqualTo(transferHistory.getTitle());
                softly.assertThat(cells.get(4).getNumericCellValue()).isEqualTo(transferHistory.getAmount().doubleValue());
                softly.assertThat(cells.get(5).getNumericCellValue()).isEqualTo(transferHistory.getBalance().doubleValue());
            });
            dataRowIndex++;
        }
        return this;
    }

    WorkbookTransferHistoryAssert hasValidDataCellsStyles() {
        Row header = requireNonNull(sheet.getRow(HEADER_INDEX), "header row cannot be null");
        int cols = header.getPhysicalNumberOfCells();

        ExportCellStyles exportCellStyles = new ExportCellStyles(workbook);
        CellStyle expDateStyle = exportCellStyles.getDateTimeCellStyle();
        CellStyle expTextStyle = exportCellStyles.getWrappedTextCellStyle();
        CellStyle expDecStyle = exportCellStyles.getDecimalCellStyle();

        int headerRowCount = 1;
        for (int i = HEADER_INDEX + headerRowCount; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = requireNonNull(sheet.getRow(i), "Row " + i + " is null");
            assertEquals(cols, row.getPhysicalNumberOfCells(),
                    "Row " + i + " column count does not match header");

            final int rowIndex = i;
            List<CellStyle> cellStyles = IntStream.range(0, cols)
                    .mapToObj(j -> requireNonNull(row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL),
                            "Cell (row index= " + rowIndex + ", column= " + j + ") is null")
                            .getCellStyle())
                    .toList();

            SoftAssertions.assertSoftly(softly -> {
                CellStyle dateStyle = cellStyles.get(0);
                softly.assertThat(dateStyle.getDataFormatString()).isEqualTo(expDateStyle.getDataFormatString());
                assertAlignment(softly, dateStyle, expDateStyle);

                for (int col : List.of(1, 2, 3)) {
                    CellStyle textStyle = cellStyles.get(col);
                    softly.assertThat(textStyle.getWrapText()).isEqualTo(expTextStyle.getWrapText());
                    assertAlignment(softly, textStyle, expTextStyle);
                }

                for (int col : List.of(4, 5)) {
                    CellStyle decStyle = cellStyles.get(col);
                    softly.assertThat(decStyle.getDataFormatString()).isEqualTo(expDecStyle.getDataFormatString());
                    assertAlignment(softly, decStyle, expDecStyle);
                }
            });
        }
        return this;
    }

    private void assertAlignment(SoftAssertions softly, CellStyle actual, CellStyle expected) {
        softly.assertThat(actual.getAlignment()).isEqualTo(expected.getAlignment());
        softly.assertThat(actual.getVerticalAlignment()).isEqualTo(expected.getVerticalAlignment());
    }
}
