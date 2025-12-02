package com.example.bank.transfer.export;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.example.bank.transfer.export.ExportToXlsxUtils.transferHistoryCreator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ExportToXlsxSources {
    static final String SHEET_NAME = "Transfer history";
    static final List<String> HEADER_CELL_TITLES = List.of(
            "Created on", "Transfer type", "Bank account number", "Title of transfer", "Amount", "Balance");

    static Stream<Arguments> exportToXlsxNullArguments() {
        return Stream.of(
                Arguments.of("wc null",
                        null,
                        HEADER_CELL_TITLES,
                        transferHistoryCreator(),
                        "workbookCreator cannot be null"),
                Arguments.of("header null",
                        new WorkbookCreator(new XSSFWorkbook()),
                        null,
                        transferHistoryCreator(),
                        "header cannot be null"),
                Arguments.of("data null",
                        new WorkbookCreator(new XSSFWorkbook()),
                        HEADER_CELL_TITLES,
                        null,
                        "transferHistories cannot be null")
        );
    }

    static Stream<Arguments> exportToXlsxEmptyHeader() {
        return Stream.of(
                Arguments.of("header empty",
                        new WorkbookCreator(new XSSFWorkbook()),
                        Collections.emptyList(),
                        transferHistoryCreator(),
                        "headerCellTitles cannot be null or empty")
        );
    }

    static Stream<Arguments> exportToXlsxEmptyData() {
        return Stream.of(
                Arguments.of("data empty",
                        new WorkbookCreator(new XSSFWorkbook()),
                        HEADER_CELL_TITLES,
                        Collections.emptyList())
        );
    }
}
