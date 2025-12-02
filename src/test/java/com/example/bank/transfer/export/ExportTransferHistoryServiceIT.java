package com.example.bank.transfer.export;

import com.example.bank.context.TransferOwnContext;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;

import static com.example.bank.transfer.export.WorkbookTransferHistoryAssert.SHEET_NAME;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TransferOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
class ExportTransferHistoryServiceIT {
    @Autowired
    private ExportTransferHistoryService exportTransferHistoryService;

    @Test
    void shouldGenerateXlsxTransferHistory() throws Exception {
        String accountNumber = "PL27722968758620190053098782";
        int expectedNumberOfSheets = 1;
        int expectedPhysicalRowCount = 3;
        String secondColumnName = "Transfer type";
        double amount = 200.0;
        double balance = 600.0;

        byte[] bytes = exportTransferHistoryService.generateXlsxTransferHistory(accountNumber);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Assertions.assertThat(workbook.getNumberOfSheets()).isEqualTo(expectedNumberOfSheets);
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            Assertions.assertThat(sheet).isNotNull();
            Assertions.assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(expectedPhysicalRowCount);
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo(secondColumnName);
                softly.assertThat(sheet.getRow(1).getCell(4).getNumericCellValue()).isEqualTo(amount);
                softly.assertThat(sheet.getRow(2).getCell(5).getNumericCellValue()).isEqualTo(balance);
            });
        }
    }
}
