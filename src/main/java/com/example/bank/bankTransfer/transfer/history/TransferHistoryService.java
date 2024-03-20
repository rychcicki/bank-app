package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.transfer.TransferType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class TransferHistoryService {
    private final AccountRepository accountRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    public List<TransferHistory> transferHistoryForClient(Long clientId) {
        return transferHistoryRepository.findByClientId(clientId);
    }

    public TransferHistory buildSenderAccountTransferHistory(Account senderAccount, BigDecimal amount,
                                                             Account receiverAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(senderAccount.getClient().getId())
                .previousBalance(senderAccount.getBalance())
                .balance(senderAccount.getBalance())
                .amount(amount)
                .bankAccountId(receiverAccount.getId())
                .title(title)
                .build();
    }

    public TransferHistory buildReceiverAccountTransferHistory(Account receiverAccount, BigDecimal amount,
                                                               Account senderAccount, String title) {
        return TransferHistory.builder()
                .transferType(TransferType.INCOME)
                .clientId(receiverAccount.getClient().getId())
                .previousBalance(receiverAccount.getBalance())
                .balance(receiverAccount.getBalance())
                .amount(amount)
                .bankAccountId(senderAccount.getId())
                .title(title)
                .build();
    }

    public void generateXlsxTransferHistory(Long clientId) {
        XSSFWorkbook transferHistoryWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = transferHistoryWorkbook.createSheet("Transfer_history");
        XSSFRow header = sheet.createRow(0);

        List<String> headerList = createStyledHeader(transferHistoryWorkbook, header, sheet);
        XSSFCellStyle dateTimeStyle = createDateTimeCellStyle(transferHistoryWorkbook);
        XSSFCellStyle decimalStyle = createDecimalCellStyle(transferHistoryWorkbook);

        int dataIndexRow = 1;
        List<TransferHistory> listOfTransferHistory = transferHistoryForClient(clientId);

        for (TransferHistory history : listOfTransferHistory) {
            Long bankAccountId = history.getBankAccountId();
            Account account = accountRepository.findById(bankAccountId).orElseThrow();
            XSSFRow row = sheet.createRow(dataIndexRow);

            for (int i = 0; i < headerList.size(); i++) {
                XSSFCell cell = row.createCell(i);

                switch (i) {
                    case 0 -> cell.setCellValue(history.getId());
                    case 1 -> {
                        cell.setCellValue(history.getCreatedOn());
                        cell.setCellStyle(dateTimeStyle);
                        sheet.setColumnWidth(1, 5700);
                    }
                    case 2 -> cell.setCellValue(history.getClientId());
                    case 3 -> cell.setCellValue(String.valueOf(history.getTransferType()));
                    case 4 -> {
                        cell.setCellValue(account.getAccountNumber());
                        sheet.setColumnWidth(4, 7400);
                    }
                    case 5 -> {
                        cell.setCellValue(history.getTitle());
                        sheet.setColumnWidth(5, 4500);
                    }
                    case 6 -> {
                        cell.setCellValue(history.getAmount().doubleValue());
                        cell.setCellStyle(decimalStyle);
                    }
                    case 7 -> {
                        cell.setCellValue(history.getBalance().doubleValue());
                        cell.setCellStyle(decimalStyle);
                    }
                    default -> cell.setCellType(CellType.ERROR);
                }
            }
            dataIndexRow++;
        }

        File currentDir = new File(".");
        String path = currentDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "Transfer_history_client_" + clientId + ".xlsx";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            transferHistoryWorkbook.write(outputStream);
            transferHistoryWorkbook.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    private static XSSFCellStyle createDecimalCellStyle(XSSFWorkbook transferHistoryWorkbook) {
        String decimalFormatPattern = "#,##0.00";
        XSSFCellStyle decimalStyle = transferHistoryWorkbook.createCellStyle();
        DataFormat decimalFormat = transferHistoryWorkbook.createDataFormat();
        decimalStyle.setDataFormat(decimalFormat.getFormat(decimalFormatPattern));
        return decimalStyle;
    }

    @NotNull
    private static XSSFCellStyle createDateTimeCellStyle(XSSFWorkbook transferHistoryWorkbook) {
        String dbFormatPattern = "YYYY-MMM-dd HH:mm:ss";
        XSSFCellStyle style = transferHistoryWorkbook.createCellStyle();
        style.setWrapText(true);
        DataFormat dateTimeFormat = transferHistoryWorkbook.createDataFormat();
        style.setDataFormat(dateTimeFormat.getFormat(dbFormatPattern));
        return style;
    }

    @NotNull
    private static List<String> createStyledHeader(XSSFWorkbook transferHistoryWorkbook, XSSFRow header, XSSFSheet sheet) {
        XSSFCellStyle headerStyle = transferHistoryWorkbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = transferHistoryWorkbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        List<String> headerList = List.of("Id", "Created_on", "Client_id", "Transfer_type", "Bank_account_numer",
                "Title of transfer", "Amount", "Balance");
        for (int i = 0; i < headerList.size(); i++) {
            XSSFCell headerCell = header.createCell(i);
            headerCell.setCellValue(headerList.get(i));
            headerCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
        return headerList;
    }
}
