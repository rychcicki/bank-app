package com.example.bank.transfer;

import com.example.bank.account.model.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.example.bank.transfer.TransferServiceUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SourceMethodsForTransferTest {
    static Stream<Arguments> argumentsForTransferDifferentCurrencies() {
        return Stream.of(
                Arguments.of("from EUR to CHF", createTransferRequest(),
                        createSenderAccount(Currency.EUR), createReceiverAccount(Currency.CHF),
                        createRateResponse(Currency.EUR), createRateResponse(Currency.CHF)),
                Arguments.of("from AUD to NOK", createTransferRequest(),
                        createSenderAccount(Currency.AUD), createReceiverAccount(Currency.NOK),
                        createRateResponse(Currency.AUD), createRateResponse(Currency.NOK)),
                Arguments.of("from GBP to EUR", createTransferRequest(),
                        createSenderAccount(Currency.GBP), createReceiverAccount(Currency.EUR),
                        createRateResponse(Currency.GBP), createRateResponse(Currency.EUR)),
                Arguments.of("from NOK to GBP", createTransferRequest(),
                        createSenderAccount(Currency.NOK), createReceiverAccount(Currency.GBP),
                        createRateResponse(Currency.NOK), createRateResponse(Currency.GBP)),
                Arguments.of("from USD to AUD", createTransferRequest(),
                        createSenderAccount(Currency.USD), createReceiverAccount(Currency.AUD),
                        createRateResponse(Currency.USD), createRateResponse(Currency.AUD)));
    }

    static Stream<Arguments> argumentsForTransferSamePlnCurrency() {
        return Stream.of(
                Arguments.of("from PLN to PLN", createTransferRequest(),
                        createSenderAccount(Currency.PLN), createReceiverAccount(Currency.PLN)));
    }

    static Stream<Arguments> argumentsForTransferPlnToOtherCurrency() {
        return Stream.of(
                Arguments.of("from PLN to USD", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.USD), createRateResponse(Currency.USD)),
                Arguments.of("from PLN to EUR", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.EUR), createRateResponse(Currency.EUR)),
                Arguments.of("from PLN to CHF", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.CHF), createRateResponse(Currency.CHF)),
                Arguments.of("from PLN to NOK", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.NOK), createRateResponse(Currency.NOK)),
                Arguments.of("from PLN to GBP", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.GBP), createRateResponse(Currency.GBP)),
                Arguments.of("from PLN to AUD", createTransferRequest(), createSenderAccount(Currency.PLN),
                        createReceiverAccount(Currency.AUD), createRateResponse(Currency.AUD))
        );
    }

    static Stream<Arguments> argumentsForTransferSameCurrency() {
        return Stream.of(
                Arguments.of("from USD to USD", createTransferRequest(), createSenderAccount(Currency.USD),
                        createReceiverAccount(Currency.USD), createRateResponse(Currency.USD)),
                Arguments.of("from CHF to CHF", createTransferRequest(), createSenderAccount(Currency.CHF),
                        createReceiverAccount(Currency.CHF), createRateResponse(Currency.CHF)),
                Arguments.of("from NOK to NOK", createTransferRequest(), createSenderAccount(Currency.NOK),
                        createReceiverAccount(Currency.NOK), createRateResponse(Currency.NOK)),
                Arguments.of("from EUR to EUR", createTransferRequest(), createSenderAccount(Currency.EUR),
                        createReceiverAccount(Currency.EUR), createRateResponse(Currency.EUR)),
                Arguments.of("from GBP to GBP", createTransferRequest(), createSenderAccount(Currency.GBP),
                        createReceiverAccount(Currency.GBP), createRateResponse(Currency.GBP)),
                Arguments.of("from AUD to AUD", createTransferRequest(), createSenderAccount(Currency.AUD),
                        createReceiverAccount(Currency.AUD), createRateResponse(Currency.AUD))
        );
    }

    static Stream<Arguments> argumentsForTransferInsufficientBalance() {
        return Stream.of(
                Arguments.of("too large amount from AUD to USD", createTransferRequestInsufficientBalance(),
                        createSenderAccount(Currency.AUD), createReceiverAccount(Currency.USD),
                        createRateResponse(Currency.USD)));
    }
}
