package com.example.bank.transfer.export;

import com.example.bank.transfer.model.TransferHistory;

import java.util.List;

import static java.util.Objects.requireNonNull;

final class ExportToXlsx {
    private static final String SHEET_NAME = "Transfer history";
    private final WorkbookCreator workbookCreator;
    private final List<String> header;
    private final List<TransferHistory> transferHistories;


    ExportToXlsx(WorkbookCreator workbookCreator, List<String> header, List<TransferHistory> transferHistories) {
        this.workbookCreator = requireNonNull(workbookCreator, "workbookCreator cannot be null");
        this.header = List.copyOf(requireNonNull(header, "header cannot be null"));
        this.transferHistories = List.copyOf(requireNonNull(transferHistories, "transferHistories cannot be null"));
    }

    public void export() {
        WorkbookCreator.XlsxSheet sheet = workbookCreator
                .createSheet(SHEET_NAME)
                .createStyledHeader(header);
        exportDataToSheet(sheet);
        sheet.adjustColumnWidth();
    }

    private void exportDataToSheet(WorkbookCreator.XlsxSheet sheet) {
        this.transferHistories.stream()
                .map(ExportToXlsx::mapToCellValues)
                .forEach(value -> sheet.createRow().addValues(value));
    }

    private static List<Object> mapToCellValues(TransferHistory transferHistory) {
        return List.of(
                transferHistory.getCreatedOn(),
                transferHistory.getTransferType().name(),
                transferHistory.getExternalAccountNumber(),
                transferHistory.getTitle(),
                transferHistory.getAmount(),
                transferHistory.getBalance());
    }
}
