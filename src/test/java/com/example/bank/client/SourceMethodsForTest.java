package com.example.bank.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SourceMethodsForTest {
    static Stream<Arguments> argumentsFor18YearsOldClient() {
        return Stream.of(Arguments.of(ClientServiceUtils.clientAdultBuilder(),
                        ClientRequestServiceUtils.clientRequestBuilder(),
                        ClientServiceUtils.updatedClientDtoWithId5Builder()),
                Arguments.of(ClientServiceUtils.clientExact18YearsOldBuilder(),
                        ClientRequestServiceUtils.clientRequestExact18YearsOldBuilder(),
                        ClientServiceUtils.updatedClientExact18YearsOldBuilder()));
    }

    static Stream<Arguments> argumentsForClientBelow18YearsOld() {
        return Stream.of(Arguments.of(ClientServiceUtils.clientBelow18YearsOldBuilder(),
                ClientRequestServiceUtils.clientRequestBelow18YearsOldBuilder()));
    }

    static Stream<Arguments> argumentsForClientToClientDto() {
        return Stream.of(Arguments.of(ClientServiceUtils.clientAdultBuilder(),
                        ClientServiceUtils.updatedClientDtoWithId5Builder()),
                Arguments.of(ClientServiceUtils.clientExact18YearsOldBuilder(),
                        ClientServiceUtils.updatedClientExact18YearsOldBuilder()));
    }

    static Stream<Arguments> argumentsForFindAllClients() {
        return Stream.of(Arguments.of(ClientServiceUtils.listOfClients(), ClientServiceUtils.listOfClientDTO()));
    }
}
