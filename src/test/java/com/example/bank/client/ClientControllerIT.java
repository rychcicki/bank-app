package com.example.bank.client;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bank.client.ClientIntegrationTestUtils.clientDataMatchers;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private String adminToken;

    private static final String CLIENT_NOT_FOUND_MESSAGE = "Client not found in database";

    @BeforeAll
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
    void shouldReturnClientDtoWhenClientExists() throws Exception {
        mockMvc.perform(get("/client/1")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.firstname").value("Mike"),
                        jsonPath("$.lastname").value("Wazowski"),
                        jsonPath("$.email").value("mike.wazowski@gmail.com"),
                        jsonPath("$.address.streetName").value("MonstersEnc"),
                        jsonPath("$.address.city").value("Monsters"),
                        jsonPath("$.role").value("ADMIN"),
                        jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn404WhenClientNotExists() throws Exception {
        mockMvc.perform(get("/client/77")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value(CLIENT_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldReturn404WhenUpdatingNotExistentClient() throws Exception {
        mockMvc.perform(put("/client/77")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ClientIntegrationTestUtils.testClientRequest))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value(CLIENT_NOT_FOUND_MESSAGE));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.client.ClientIntegrationTestUtils#invalidClientRequests")
    void shouldNotCreateClientAndReturn400(String description, int statusCode, String arguments, String message)
            throws Exception {
        mockMvc.perform(post("/client")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(arguments))
                .andDo(print())
                .andExpectAll(
                        status().is(statusCode),
                        jsonPath("$.message").value(message)
                );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.client.ClientIntegrationTestUtils#invalidClientUpdateRequests")
    void shouldNotUpdateClientAndReturn400(String description, int statusCode, String arguments, String message)
            throws Exception {
        mockMvc.perform(put("/client/2")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(arguments))
                .andDo(print())
                .andExpectAll(
                        status().is(statusCode),
                        jsonPath("$.message").value(message)
                );
    }

    @Test
    void shouldCreateClientAndReturnClientDto() throws Exception {
        mockMvc.perform(post("/client")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ClientIntegrationTestUtils.testClientRequest))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").isNotEmpty())
                .andExpectAll(clientDataMatchers());
    }

    @Test
    void shouldReturnSetOfClientDtoWhenRequestedByAdmin() throws Exception {
        mockMvc.perform(get("/client")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isNotEmpty(),
                        jsonPath("$", hasSize(3))
                );
    }

    @Test
    void shouldUpdateClientAndReturnUpdatedClientDto() throws Exception {
        mockMvc.perform(put("/client/3")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ClientIntegrationTestUtils.testClientUpdateRequest))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(3))
                .andExpectAll(clientDataMatchers());
    }

    @Test
    void shouldSoftDeleteClientAndReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/client/2")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotSoftDeleteClientAndReturn404() throws Exception {
        mockMvc.perform(delete("/client/77")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value(CLIENT_NOT_FOUND_MESSAGE)
                );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.client.ClientIntegrationTestUtils#clientEndpointsRequiringAdminRole")
    void shouldReturnForbiddenForUserRole(String description, MockHttpServletRequestBuilder requestBuilder)
            throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"bad.randal@gmail.com\", \"password\": \"badpassword\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        final String responseBody = login.getResponse().getContentAsString();
        final String userToken = "Bearer " + JsonPath.parse(responseBody).read("$.accessToken");

        mockMvc.perform(requestBuilder
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.example.bank.client.ClientIntegrationTestUtils#clientEndpointsRequiringAdminRole")
    void shouldReturnUnauthorizedForInvalidToken(String description, MockHttpServletRequestBuilder requestBuilder)
            throws Exception {
        mockMvc.perform(requestBuilder
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }
}
