package com.example.bank.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ClientIntegrationTestUtils {
    static final String testClientRequest = """
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
                    "password": "AnyPassword"
                }
            """;

    static final String testClientUpdateRequest = """
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
                    }
                }
            """;

    static Stream<Arguments> clientEndpointsRequiringAdminRole() {
        return Stream.of(
                arguments("get /client/1", get("/client/1")),
                arguments("get /client", get("/client")),
                arguments("put /client/2", put("/client/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest)),
                arguments("post /client", post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testClientRequest)),
                arguments("delete /client/3", delete("/client/3"))
        );
    }

    static Stream<Arguments> invalidClientRequests() {
        return Stream.of(
                arguments("client exists", 409, testClientRequest.replace("my@gmail.com",
                        "mike.wazowski@gmail.com"), "Email already exists"),
                arguments("wrong email", 400, testClientRequest.replace("my@gmail.com", "wrongMail.com"),
                        "invalid email address"),
                arguments("missing firstname", 400, testClientRequest.replace("MyFirstname", ""),
                        "firstname is mandatory"),
                arguments("missing lastname", 400, testClientRequest.replace("MyLastname", ""),
                        "lastname is mandatory"),
                arguments("birthdate in future", 400, testClientRequest.replace("1990-02-25",
                        "2990-02-25"), "Invalid birthdate"),
                arguments("client not adult", 400, testClientRequest.replace("1990-02-25", "2025-02-25"),
                        "Client has to be adult"),
                arguments("street name missing", 400, testClientRequest.replace("MainStreet", ""),
                        "street name is mandatory"),
                arguments("street number missing", 400, testClientRequest.replace("123", ""),
                        "street number is mandatory"),
                arguments("zip code missing", 400, testClientRequest.replace("00-001", ""),
                        "zip code must be between 4 and 10 characters"),
                arguments("zip code missing", 400, testClientRequest.replace("00-001", "0"),
                        "zip code must be between 4 and 10 characters"),
                arguments("city is mandatory", 400, testClientRequest.replace("MyCity", ""),
                        "city is mandatory"),
                arguments("password is mandatory", 400, testClientRequest.replace("AnyPassword", ""),
                        "password is mandatory"));
    }

    static Stream<Arguments> invalidClientUpdateRequests() {
        return Stream.of(
                arguments("client exists", 409, testClientUpdateRequest.replace("my@gmail.com",
                        "mike.wazowski@gmail.com"), "Email already exists"),
                arguments("wrong email", 400, testClientUpdateRequest.replace("my@gmail.com",
                        "wrongMail.com"), "invalid email address"),
                arguments("missing firstname", 400, testClientUpdateRequest.replace("MyFirstname", ""),
                        "firstname is mandatory"),
                arguments("missing lastname", 400, testClientUpdateRequest.replace("MyLastname", ""),
                        "lastname is mandatory"),
                arguments("birthdate in future", 400, testClientUpdateRequest.replace("1990-02-25",
                        "2990-02-25"), "Invalid birthdate"),
                arguments("client not adult", 400, testClientUpdateRequest.replace("1990-02-25",
                        "2025-02-25"), "Client has to be adult"),
                arguments("street name missing", 400, testClientUpdateRequest.replace("MainStreet", ""),
                        "street name is mandatory"),
                arguments("street number missing", 400, testClientUpdateRequest.replace("123", ""),
                        "street number is mandatory"),
                arguments("zip code missing", 400, testClientUpdateRequest.replace("00-001", ""),
                        "zip code must be between 4 and 10 characters"),
                arguments("zip code missing", 400, testClientUpdateRequest.replace("00-001", "0"),
                        "zip code must be between 4 and 10 characters"),
                arguments("city is mandatory", 400, testClientUpdateRequest.replace("MyCity", ""),
                        "city is mandatory"));
    }

    static ResultMatcher[] clientDataMatchers() {
        return new ResultMatcher[]{
                jsonPath("$.firstname").value("MyFirstname"),
                jsonPath("$.lastname").value("MyLastname"),
                jsonPath("$.birthDate").value("1990-02-25"),
                jsonPath("$.email").value("my@gmail.com"),
                jsonPath("$.address.streetName").value("MainStreet"),
                jsonPath("$.address.streetNumber").value("123"),
                jsonPath("$.address.zipCode").value("00-001")
        };
    }
}
