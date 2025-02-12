package com.example.bank.client;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private final String testClientRequest = """
                {
                    "firstname": "MyFirstname",
                    "lastname": "MyLastname",
                    "birthDate": "1990-02-25",
                    "email": "my@gmail.com",
                    "address": {
                        "streetName": "MainStreet",
                        "streetNumber": "123",
                        "zipCode": "00-001",
                        "city": "MyCity"
                    },
                    "password": "itDoesNotChangePassword"
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
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldReturnClientDtoWhenClientExists() throws Exception {
        mockMvc.perform(get("/client/1"))
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
    void shouldCreateClientAndReturnClientDto() throws Exception {
        mockMvc.perform(post("/client")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.firstname").value("MyFirstname"),
                        jsonPath("$.lastname").value("MyLastname"),
                        jsonPath("$.birthDate").value("1990-02-25"),
                        jsonPath("$.email").value("my@gmail.com"),
                        jsonPath("$.address.streetName").value("MainStreet"),
                        jsonPath("$.address.streetNumber").value("123"),
                        jsonPath("$.address.zipCode").value("00-001"),
                        jsonPath("$.address.city").value("MyCity")
                );
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldReturnSetOfClientDto() throws Exception {
        mockMvc.perform(get("/client")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .content(testClientRequest))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(3),
                        jsonPath("$.firstname").value("MyFirstname"),
                        jsonPath("$.lastname").value("MyLastname"),
                        jsonPath("$.birthDate").value("1990-02-25"),
                        jsonPath("$.email").value("my@gmail.com"),
                        jsonPath("$.address.streetName").value("MainStreet"),
                        jsonPath("$.address.streetNumber").value("123"),
                        jsonPath("$.address.zipCode").value("00-001"),
                        jsonPath("$.address.city").value("MyCity")
                );
    }

    @Test
    @WithMockUser(username = "justUser", authorities = "ADMIN")
    void shouldSoftDeleteClientAndReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/client/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenForUserRole() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"bad.randal@gmail.com\", \"password\": \"badpassword\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        final String responseBody = login.getResponse().getContentAsString();
        final String userToken = "Bearer " + JsonPath.parse(responseBody).read("$.accessToken");

        mockMvc.perform(get("/client/1").header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/client").header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/client/2").header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/client").header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/client/3").header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        mockMvc.perform(get("/client/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/client").header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/client/2").header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/client").header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/client/3").header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }
}
