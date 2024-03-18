package com.example.bank.bankTransfer.transfer.history;

import com.example.bank.bankTransfer.account.Account;
import com.example.bank.bankTransfer.account.AccountRepository;
import com.example.bank.bankTransfer.transfer.TransferType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
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

    public List<TransferHistory> transferHistoryForClient(Integer clientId) {
        return transferHistoryRepository.findByClientId(clientId);
    }

    public TransferHistory buildFromAccountTransferHistory(Account fromAccount, BigDecimal amount, Account toAccount) {
        return TransferHistory.builder()
                .transferType(TransferType.EXPENSE)
                .clientId(fromAccount.getClient().getId())
                .beforeBalance(fromAccount.getBalance())
                .afterBalance(fromAccount.getBalance())
                .amount(amount)
                .bankAccountId(toAccount.getId())
                .build();
    }

    public TransferHistory buildToAccountTransferHistory(Account toAccount, BigDecimal amount, Account fromAccount) {
        return TransferHistory.builder()
                .transferType(TransferType.INCOME)
                .clientId(toAccount.getClient().getId())
                .beforeBalance(toAccount.getBalance())
                .afterBalance(toAccount.getBalance())
                .amount(amount)
                .bankAccountId(fromAccount.getId())
                .build();
    }

    //TODO:  decide whether clientId or accountId for bank statement
    public void generateXlsxBankStatement(Integer clientId) {
        XSSFWorkbook statementWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = statementWorkbook.createSheet("MyBank_Statement");
        // TODO: set column width setColumnWidth()
        XSSFRow header = sheet.createRow(0);
        XSSFCellStyle headerStyle = statementWorkbook.createCellStyle();
        headerStyle.setFillForegroundColor((short) 7);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<String> headerList = List.of("Id", "Created_on", "Client_id", "Transfer_type", "Bank_account_numer",
                "Amount", "After_balance");
        for (int i = 0; i < headerList.size(); i++) {
            XSSFCell headerCell = header.createCell(i);
            headerCell.setCellValue(headerList.get(i));
            headerCell.setCellStyle(headerStyle);
        }
        //TODO: set format
        String dbFormatPattern = "dd-MMM-YYYY HH:mm:ss";
        XSSFCellStyle style = statementWorkbook.createCellStyle();
        style.setWrapText(true);
        DataFormat dateTimeFormat = statementWorkbook.createDataFormat();
        style.setDataFormat(dateTimeFormat.getFormat(dbFormatPattern));
        int dataIndexRow = 1;
        List<TransferHistory> listOfTransferHistory = transferHistoryForClient(clientId);

        for (TransferHistory history : listOfTransferHistory) {
            Long bankAccountId = history.getBankAccountId();
            Account account = accountRepository.findById(bankAccountId).orElseThrow();
            XSSFRow row = sheet.createRow(dataIndexRow);
            //TODO: need a loop, format (DateTime, Enum,BigDecimal)
            row.createCell(0).setCellValue(history.getId());
            row.createCell(1).setCellValue(history.getCreatedOn());
            row.createCell(2).setCellValue(history.getClientId());
            row.createCell(3).setCellValue(String.valueOf(history.getTransferType()));
            row.createCell(4).setCellValue(account.getAccountNumber());
            row.createCell(5).setCellValue(String.valueOf(history.getAmount()));
            row.createCell(6).setCellValue(String.valueOf(history.getAfterBalance()));
            dataIndexRow++;
        }
        // TODO: set cells' style setCellStyle(style)
        File currentDir = new File(".");
        String path = currentDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "Bank_statement_client_" + clientId + ".xlsx";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            statementWorkbook.write(outputStream);
            statementWorkbook.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
