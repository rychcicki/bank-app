package com.example.bank.transfer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.example.bank.transfer.TransferServiceUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceMethodsForTransferTest {
    static Stream<Arguments> argumentsForTransfer() {
        return Stream.of(
                Arguments.of(createSenderAccount(), createReceiverAccount(), senderHistory(), receiverHistory()));
    }
}
