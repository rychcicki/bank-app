package com.example.bank.transfer.export;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WorkbookCreatorSources {
    static Stream<Arguments> provideValuesAndExpectedStrings() {
        return Stream.of(
                Arguments.of(LocalDate.of(1998, 12, 26), "1998-12-26"),
                Arguments.of(false, "false"),
                Arguments.of(true, "true"),
                Arguments.of(null, "")
        );
    }

    static Stream<Arguments> provideNullOrEmptyLists() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(List.of())
        );
    }
}
