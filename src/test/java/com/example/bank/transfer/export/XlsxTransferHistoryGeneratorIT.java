package com.example.bank.transfer.export;

import com.example.bank.context.TransferOwnContext;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TransferOwnContext.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestPropertySource("classpath:application-test.yml")
class XlsxTransferHistoryGeneratorIT {
    @Autowired
    private XlsxTransferHistoryGenerator transferHistoryGenerator;

    @Test
    void shouldGenerateXlsxTransferHistory() {
        String accountNumber = "PL27722968758620190053098782";
        int numberOfSheets = 1;
        int numberOfRowsWithHeader = 3;
        String secondColumnName = "Transfer type";
        var amount = 200;
        var balance = 600;

        ByteArrayOutputStream outputStream = transferHistoryGenerator.generateXlsxTransferHistory(accountNumber);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Assertions.assertEquals(numberOfSheets, workbook.getNumberOfSheets());
            Sheet sheet = workbook.getSheet(WorkbookCreator.transferHistorySheetName);
            Assertions.assertNotNull(sheet);
            Assertions.assertEquals(numberOfRowsWithHeader, sheet.getPhysicalNumberOfRows());
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo(secondColumnName);
                softly.assertThat(sheet.getRow(1).getCell(4).getNumericCellValue()).isEqualTo(amount);
                softly.assertThat(sheet.getRow(2).getCell(5).getNumericCellValue()).isEqualTo(balance);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
