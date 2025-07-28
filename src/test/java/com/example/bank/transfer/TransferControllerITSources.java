package com.example.bank.transfer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TransferControllerITSources {
    private static final String VALID_SENDER_ACCOUNT_NUMBER = "GB92BARC20038472426896";
    private static final String VALID_RECEIVER_ACCOUNT_NUMBER = "DE11500105171841551884";
    private static final String BAD_REQUEST_ERROR_CODE = "006";

    private static String jsonRequest(String sender, String receiver, String amount, String title) {
        return """
                {
                  "senderAccountNumber":"%s",
                  "receiverAccountNumber":"%s",
                  "amount": %s,
                  "title":"%s"
                }
                """.formatted(sender, receiver, amount, title);
    }

    static Stream<Arguments> validTransferRequest() {
        return Stream.of(Arguments.of(
                "valid request",
                jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                        VALID_RECEIVER_ACCOUNT_NUMBER,
                        "12",
                        "Integration test for transfer")
        ));
    }

    static Stream<Arguments> invalidAccountNumberInTransferRequest() {
        return Stream.of(Arguments.of(
                "wrong account number",
                jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                        "wrong account number",
                        "12",
                        "Account not found in database")
        ));
    }

    static Stream<Arguments> invalidTransferRequests() {
        return Stream.of(
                Arguments.of("same accounts",
                        jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                                VALID_SENDER_ACCOUNT_NUMBER,
                                "100.00",
                                "same accounts"),
                        BAD_REQUEST_ERROR_CODE,
                        "sender and receiver account numbers must differ"
                ),
                Arguments.of(
                        "negative amount",
                        jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                                VALID_RECEIVER_ACCOUNT_NUMBER,
                                "-5.50",
                                "negative amount"),
                        BAD_REQUEST_ERROR_CODE,
                        "amount must be at least 0.01"
                ),
                Arguments.of(
                        "too low amount",
                        jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                                VALID_RECEIVER_ACCOUNT_NUMBER,
                                "0.0099999",
                                "too low amount"),
                        BAD_REQUEST_ERROR_CODE,
                        "amount must be at least 0.01"
                ),
                Arguments.of(
                        "no sender account",
                        jsonRequest("",
                                VALID_RECEIVER_ACCOUNT_NUMBER,
                                "10",
                                "no sender"),
                        BAD_REQUEST_ERROR_CODE,
                        "sender account number is mandatory"
                ),
                Arguments.of(
                        "no receiver account",
                        jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                                "",
                                "10.00",
                                "no receiver"),
                        BAD_REQUEST_ERROR_CODE,
                        "receiver account number is mandatory"
                ),
                Arguments.of(
                        "no title",
                        jsonRequest(VALID_SENDER_ACCOUNT_NUMBER,
                                VALID_RECEIVER_ACCOUNT_NUMBER,
                                "10.00",
                                ""),
                        BAD_REQUEST_ERROR_CODE,
                        "title of transfer is mandatory"
                )
        );
    }
}
