package com.example.bank.transfer;

import com.example.bank.account.AccountService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransferControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    private String adminToken;
    private final String testTransferRequestForAdmin = """
                {
                    "senderAccountNumber": "PL54613983300568639363795256",
                    "receiverAccountNumber": "DE11500105171841551884",
                    "amount": 12.89,
                    "title": "Integration test for transfer"
                }
            """;

    private final String testTransferRequestForUserRole = """
                {
                    "senderAccountNumber": "GB92BARC20038472426896",
                    "receiverAccountNumber": "DE11500105171841551884",
                    "amount": 12.89,
                    "title": "Integration test for transfer"
                }
            """;

    @BeforeEach
    void authenticateAdminAndRetrieveJwt() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mike.wazowski@gmail.com\", \"password\": \"password\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = login.getResponse().getContentAsString();
        adminToken = "Bearer " + JsonPath.parse(responseBody).read("$.accessToken");
    }

    @Test
    void shouldProcessBankTransferForAdmin() throws Exception {
        mockMvc.perform(post("/transfer/make-transfer")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransferRequestForAdmin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldProcessBankTransferForUserRole() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"bad.randal@gmail.com\", \"password\": \"badpassword\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        final String responseBody = login.getResponse().getContentAsString();
        final String userToken = "Bearer " + JsonPath.parse(responseBody).read("$.accessToken");

        mockMvc.perform(post("/transfer/make-transfer")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransferRequestForUserRole))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void shouldGenerateXlsxTransferHistoryForUser() throws Exception {
        mockMvc.perform(post("/transfer/generate-transfer-history/PL21363593769265669736300815"))
                .andExpectAll(
                        status().isOk(),
                        header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE),
                        header().string(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=Transfer history PL21363593769265669736300815.xlsx"))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "other", authorities = "OTHER")
    void shouldReturnForbiddenForOtherRole() throws Exception {
        mockMvc.perform(post("/transfer/make-transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testTransferRequestForAdmin))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/transfer/generate-transfer-history/PL21363593769265669736300815"))
                .andExpect(status().isForbidden());
    }
}
