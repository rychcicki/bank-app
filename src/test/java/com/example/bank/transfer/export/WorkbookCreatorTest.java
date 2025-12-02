package com.example.bank.transfer.export;

import com.example.bank.transfer.model.TransferHistory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.example.bank.transfer.export.ExportToXlsxSources.HEADER_CELL_TITLES;
import static com.example.bank.transfer.export.ExportToXlsxUtils.transferHistoryCreator;
import static com.example.bank.transfer.export.WorkbookTransferHistoryAssert.SHEET_NAME;

class WorkbookCreatorTest {
    private final IndexedColors foregroundColor = IndexedColors.GREY_25_PERCENT;
    private final FillPatternType fillPatternType = FillPatternType.SOLID_FOREGROUND;
    private final HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private final VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private final List<TransferHistory> transferHistories = transferHistoryCreator();
    private WorkbookCreator workbookCreator;

    @BeforeEach
    void setUp() {
        workbookCreator = new WorkbookCreator(new XSSFWorkbook());
    }

    @Test
    void shouldCreateSheetInWorkbook() {
        final int headerRowNumber = 0;
        WorkbookCreator.XlsxSheet sheet = workbookCreator.createSheet(SHEET_NAME)
                .createStyledHeader(HEADER_CELL_TITLES);

        transferHistories.forEach(th -> sheet
                .createRow()
                .addValues(toValuesList(th)));

        WorkbookTransferHistoryAssert.assertThat(workbookCreator.getWorkbook())
                .hasValidSheetName(SHEET_NAME)
                .hasValidHeaderTitlesAndColumnCount(headerRowNumber, HEADER_CELL_TITLES)
                .hasValidHeaderStyle(foregroundColor, fillPatternType, horizontalAlignment)
                .hasValidRowDataValues(transferHistories)
                .hasValidDataCellsStyles();
    }

    private List<Object> toValuesList(TransferHistory transferHistory) {
        return List.of(
                transferHistory.getCreatedOn(),
                transferHistory.getTransferType().name(),
                transferHistory.getExternalAccountNumber(),
                transferHistory.getTitle(),
                transferHistory.getAmount(),
                transferHistory.getBalance()
        );
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.export.WorkbookCreatorSources#provideValuesAndExpectedStrings")
    void shouldFormatNullOrDefaultValues(Object value, String expectedValue) {
        CellStyle textStyle = CellStyleUtils.createWrapTextStyle(workbookCreator.getWorkbook(),
                horizontalAlignment, verticalAlignment);

        workbookCreator.createSheet(SHEET_NAME)
                .createRow()
                .fillCell(value);

        Cell cell = workbookCreator.getWorkbook().getSheet(SHEET_NAME)
                .getRow(0)
                .getCell(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cell.getCellStyle()).isEqualTo(textStyle);
            softly.assertThat(cell.getStringCellValue()).isEqualTo(expectedValue);
        });
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenAdjustColumnWidthBeforeHeader() {
        WorkbookCreator.XlsxSheet sheet = workbookCreator.createSheet(SHEET_NAME);

        Assertions.assertThatThrownBy(
                        sheet::adjustColumnWidth)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("createStyledHeader must be called before adjustColumnWidth");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDuplicatedSheetName() {
        String duplicatedSheetName = "TheSameNameOfSheet";

        workbookCreator.createSheet(duplicatedSheetName);

        Assertions.assertThatThrownBy(
                        () -> workbookCreator.createSheet(duplicatedSheetName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sheet \"" + duplicatedSheetName + "\" already exists in workbook");
    }

    @Test
    void shouldSanitizeSheetNameWhenIllegalChars() {
        String illegalSheetName = "My/Sheet?*[]";
        char replaceChar = '_';
        String safeSheetName = WorkbookUtil.createSafeSheetName(illegalSheetName, replaceChar);

        workbookCreator.createSheet(illegalSheetName);

        Assertions.assertThat(workbookCreator.getWorkbook().getSheet(safeSheetName)).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.export.WorkbookCreatorSources#provideNullOrEmptyLists")
    void shouldThrowIllegalArgumentExceptionWhenArgsInHeaderNullOrEmpty(List<String> headerTitles) {
        Assertions.assertThatThrownBy(
                        () -> workbookCreator.createSheet(SHEET_NAME)
                                .createStyledHeader(headerTitles)
                                .createRow()
                                .addValues(List.of("First", "Second", "Third")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("headerCellTitles cannot be null or empty");
    }

    @ParameterizedTest
    @MethodSource("com.example.bank.transfer.export.WorkbookCreatorSources#provideNullOrEmptyLists")
    void shouldThrowIllegalArgumentExceptionWhenArgsInValuesNullOrEmpty(List<Object> values) {
        Assertions.assertThatThrownBy(
                        () -> workbookCreator.createSheet(SHEET_NAME)
                                .createStyledHeader(HEADER_CELL_TITLES)
                                .createRow()
                                .addValues(values))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values cannot be null or empty");
    }
}
