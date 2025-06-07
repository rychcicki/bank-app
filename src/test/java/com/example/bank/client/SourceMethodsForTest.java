package com.example.bank.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.example.bank.client.ClientRequestServiceUtils.*;
import static org.junit.jupiter.api.Named.named;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SourceMethodsForTest {
    static Stream<Arguments> provideClientsForFindAndCreate() {
        return Stream.of(
                Arguments.of(
                        named("adult client", ClientServiceUtils.clientAdultBuilder()),
                        named("adult client request", clientRequestBuilder()),
                        named("updated adult client", ClientServiceUtils.updatedClientDtoWithId5Builder())),
                Arguments.of(
                        named("18yo client", ClientServiceUtils.clientExact18YearsOldBuilder()),
                        named("18yo client request", clientRequestExact18YearsOldBuilder()),
                        named("updated 18yo client", ClientServiceUtils.updatedClientExact18YearsOldBuilder()))
        );
    }

    static Stream<Arguments> provideClientsForUpdateAndDelete() {
        return Stream.of(
                Arguments.of(
                        named("adult client", ClientServiceUtils.clientAdultBuilder()),
                        named("adult client request", clientUpdateRequestBuilder()),
                        named("updated adult client", ClientServiceUtils.updatedClientDtoWithId5Builder())),
                Arguments.of(
                        named("18yo client", ClientServiceUtils.clientExact18YearsOldBuilder()),
                        named("18yo client request", clientUpdateRequestExact18YearsOldBuilder()),
                        named("updated 18yo client", ClientServiceUtils.updatedClientExact18YearsOldBuilder()))
        );
    }

    static Stream<Arguments> provideClientsForFindAll() {
        return Stream.of(
                Arguments.of(
                        named("adult clients list", ClientServiceUtils.listOfClients()),
                        named("adult clientDTOs set", ClientServiceUtils.setOfClientDTO()))
        );
    }

    static Stream<Arguments> provideClientRequestWithInvalidBirthDate() {
        return Stream.of(
                Arguments.of(named("invalid birth date client request", clientRequestInvalidBirthDateBuilder())),
                Arguments.of(named("underage client request", clientRequestBelow18YearsOldBuilder()))
        );
    }

    static Stream<Arguments> provideClientUpdateRequestWithInvalidBirthDate() {
        return Stream.of(
                Arguments.of(named("invalid birth date update request", clientUpdateRequestInvalidBirthDateBuilder())),
                Arguments.of(named("underage update request", clientUpdateRequestBelow18YearsOldBuilder()))
        );
    }
}
