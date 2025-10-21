package com.example.bank.transfer.export;

import com.example.bank.transfer.model.TransferHistory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.example.bank.transfer.export.ExportToXlsxSources.HEADER_CELL_TITLES;
import static com.example.bank.transfer.export.ExportToXlsxSources.SHEET_NAME;
import static com.example.bank.transfer.export.XlsxTransferHistoryGeneratorUtils.transferHistoryCreator;

class ExportToXlsxTest {
    @Test
    void shouldExportXlsxSheetWithHeaderAndData() {
        final int headerRowIndex = 0;
        final int headerRowCount = 1;
        final List<TransferHistory> transferHistories = transferHistoryCreator();
        final WorkbookCreator workbookCreator = new WorkbookCreator(new XSSFWorkbook());
        final ExportToXlsx exportToXlsx = new ExportToXlsx(workbookCreator, HEADER_CELL_TITLES, transferHistories);

        exportToXlsx.export();

        Workbook workbook = workbookCreator.getWorkbook();
        Assertions.assertThat(workbook.getNumberOfSheets()).isOne();

        Sheet sheet = workbook.getSheet(SHEET_NAME);
        Assertions.assertThat(sheet).isNotNull();

        Row headerRow = sheet.getRow(headerRowIndex);
        Assertions.assertThat(headerRow.getPhysicalNumberOfCells()).isEqualTo(HEADER_CELL_TITLES.size());

        int expectedRows = transferHistories.size() + headerRowCount;
        Assertions.assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(expectedRows);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.export.ExportToXlsxSources#exportToXlsxNullArguments")
    void shouldThrowNPEWhenConstructorArgsAreNull(String name, WorkbookCreator workbookCreator, List<String> header,
                                                  List<TransferHistory> data, String message) {
        Assertions.assertThatThrownBy(() -> new ExportToXlsx(workbookCreator, header, data))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.export.ExportToXlsxSources#exportToXlsxEmptyHeader")
    void shouldThrowIllegalArgumentExceptionWhenHeaderEmpty(String name, WorkbookCreator workbookCreator, List<String> header,
                                                            List<TransferHistory> data, String message) {
        final ExportToXlsx toXlsxEmptyHeader = new ExportToXlsx(workbookCreator, header, data);

        Assertions.assertThatThrownBy(toXlsxEmptyHeader::export)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.export.ExportToXlsxSources#exportToXlsxEmptyData")
    void shouldCreateXlsxSheetWhenDataEmpty(String name, WorkbookCreator workbookCreator, List<String> header,
                                            List<TransferHistory> data) {
        final ExportToXlsx toXlsxEmptyData = new ExportToXlsx(workbookCreator, header, data);

        toXlsxEmptyData.export();

        Sheet sheet = workbookCreator.getWorkbook().getSheet(SHEET_NAME);
        Assertions.assertThat(sheet.getPhysicalNumberOfRows()).isOne();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSheetAlreadyExists() {
        final List<TransferHistory> transferHistories = transferHistoryCreator();
        final WorkbookCreator workbookCreator = new WorkbookCreator(new XSSFWorkbook());
        final ExportToXlsx exportToXlsx = new ExportToXlsx(workbookCreator, HEADER_CELL_TITLES, transferHistories);

        exportToXlsx.export();

        Assertions.assertThatThrownBy(exportToXlsx::export)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sheet \"%s\" already exists in workbook", SHEET_NAME);
    }
}
