package com.example.bank.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccountControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void shouldCreateMyBankAccountAndReturnAccountDtoForUser() throws Exception {
        mockMvc.perform(post("/account/my-bank/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accountNumber").isNotEmpty(),
                        jsonPath("$.currency").value("PLN"),
                        jsonPath("$.type").value("CURRENT_ACCOUNT"),
                        jsonPath("$.balance").value(0),
                        jsonPath("$.clientId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldCreateMyBankAccountAndReturnAccountDtoForAdmin() throws Exception {
        mockMvc.perform(post("/account/my-bank/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldCreatePolishAccountAndReturnAccountDto() throws Exception {
        mockMvc.perform(post("/account/polish/2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accountNumber").isNotEmpty(),
                        jsonPath("$.currency").value("PLN"),
                        jsonPath("$.type").value("CURRENT_ACCOUNT"),
                        jsonPath("$.balance").value(0),
                        jsonPath("$.clientId").value(2));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldCreateForeignAccountAndReturnAccountDto() throws Exception {
        mockMvc.perform(post("/account/foreign/3"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accountNumber").isNotEmpty(),
                        jsonPath("$.currency").value(not("PLN")),
                        jsonPath("$.type").value("CURRENT_ACCOUNT"),
                        jsonPath("$.balance").value(0),
                        jsonPath("$.clientId").value(3));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldReturnAccountDtoWhenAccountNumberExists() throws Exception {
        mockMvc.perform(get("/account/GB92BARC20038472426896"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.accountNumber").value("GB92BARC20038472426896"),
                        jsonPath("$.currency").value("GBP"),
                        jsonPath("$.type").value("CURRENT_ACCOUNT"),
                        jsonPath("$.balance").value(3000.00),
                        jsonPath("$.clientId").value(2));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldReturnListOfAccountDto() throws Exception {
        mockMvc.perform(get("/account")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty(),
                        jsonPath("$", hasSize(3))
                );
    }

    @WithMockUser(username = "user", authorities = "USER")
    @ParameterizedTest(name = "forbiddenEndpoint: {0}")
    @MethodSource("com.example.bank.account.AccountServiceUtils#forbiddenEndpointsScenarios")
    void shouldReturnForbiddenForUserRole(String scenarioMethod,
                                          MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "unauthorizedEndpoint: {0}")
    @MethodSource("com.example.bank.account.AccountServiceUtils#unauthorizedEndpointsScenarios")
    void shouldReturnUnauthorizedForInvalidToken(String scenarioMethod,
                                                 MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }
}
