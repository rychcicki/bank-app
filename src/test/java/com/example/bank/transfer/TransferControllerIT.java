package com.example.bank.transfer;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransferControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private static final String MAKE_TRANSFER_URL = "/transfer/make-transfer";
    private static final String GENERATE_TRANSFER_HISTORY_URL = "/transfer/generate-transfer-history/{account}";
    private String adminToken;
    private String userToken;
    private String otherRoleToken;

    @BeforeAll
    void setUpTokens() throws Exception {
        adminToken = obtainToken("mike.wazowski@gmail.com", "password");
        userToken = obtainToken("bad.randal@gmail.com", "badpassword");
        otherRoleToken = obtainToken("someone@gmail.com", "anypassword");
    }

    private String obtainToken(String email, String password) throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password)))
                .andExpect(status().isOk())
                .andReturn();

        final String responseBody = login.getResponse().getContentAsString();
        return "Bearer " + JsonPath.parse(responseBody).read("$.accessToken");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#validTransferRequest")
    void shouldProcessTransferWhenAdmin(String description,
                                        String transferRequest) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#validTransferRequest")
    void shouldProcessTransferWhenUser(String description,
                                       String transferRequest) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#validTransferRequest")
    void shouldReturnForbiddenWhenOtherRoleForMakeTransfer(String description,
                                                           String transferRequest) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, otherRoleToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#validTransferRequest")
    void shouldReturnUnauthorizedWhenBearerTokenInvalidForMakeTransfer(String description,
                                                                       String transferRequest) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#invalidAccountNumberInTransferRequest")
    void shouldReturnNotFoundWhenAccountNumberInvalidForMakeTransfer(String description,
                                                                     String transferRequest) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequest))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorCode").value("003"),
                        jsonPath("$.message")
                                .value("Account not found in database"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.transfer.TransferControllerITSources#invalidTransferRequests")
    void shouldReturnBadRequestWhenTransferRequestInvalid(String description,
                                                          String jsonPayload,
                                                          String expectedErrorCode,
                                                          String expectedMessage) throws Exception {
        mockMvc.perform(post(MAKE_TRANSFER_URL)
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.errorCode").value(expectedErrorCode),
                        jsonPath("$.message")
                                .value(expectedMessage));
    }

    @Test
    void shouldGenerateXlsxTransferHistoryWhenAdmin() throws Exception {
        mockMvc.perform(post(GENERATE_TRANSFER_HISTORY_URL, "PL21363593769265669736300815")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpectAll(
                        status().isOk(),
                        header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE),
                        header().string(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=Transfer history PL21363593769265669736300815.xlsx"))
                .andReturn();
    }

    @Test
    void shouldReturnForbiddenWhenOtherRoleForGenerateHistory() throws Exception {
        mockMvc.perform(post(GENERATE_TRANSFER_HISTORY_URL, "PL21363593769265669736300815")
                        .header(HttpHeaders.AUTHORIZATION, otherRoleToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenInvalidBearerTokenForGenerateHistory() throws Exception {
        mockMvc.perform(post(GENERATE_TRANSFER_HISTORY_URL, "PL21363593769265669736300815")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }
}
