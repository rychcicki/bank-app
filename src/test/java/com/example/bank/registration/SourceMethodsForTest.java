package com.example.bank.registration;

import com.example.bank.registration.jpa.Client;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.example.bank.registration.ClientRequestServiceUtils.*;
import static com.example.bank.registration.ClientServiceUtils.*;

class SourceMethodsForTest {
    static Stream<Arguments> clientBelow18YearsOldSource() {
        return Stream.of(Arguments.of(clientBelow18YearsOldBuilder(), clientRequestBelow18RequestBuilder()));
    }

    static Stream<ClientRequest> clientRequestBelow18YearsOldSource() {
        return Stream.of(clientRequestBelow18RequestBuilder()
        );
    }

    static Stream<Client> clientSource() {
        return Stream.of(clientBuilder(), clientWithId3Builder(), clientAdultWithId5Builder(),
                client18YearsOldBuilder(), clientBelow18YearsOldBuilder());
    }

    static Stream<Arguments> clientOver18YearsOldUpdateSource() {
        return Stream.of(Arguments.of(clientAdultWithId5Builder(), updateClientRequestAdultBuilder(),
                        resultUpdateClientWithId5Builder()),
                Arguments.of(client18YearsOldBuilder(), updateClientRequest18YOBuilder(),
                        resultUpdateClient18YearsOldBuilder()));
    }

    static Stream<Arguments> clientOver18YearsOldSource() {
        return Stream.of(Arguments.of(clientAdultWithId5Builder(), clientRequest18YOForRegisterBuilder(),
                resultOfRegisterClientAdultWithIdBuilder()));
    }

    static Stream<Arguments> clientRequestOver18YearsOldSource() {
        return Stream.of(Arguments.of(clientRequest18YOForRegisterBuilder(),
                resultOfRegisterClientAdultWithIdBuilder()));
    }
}
