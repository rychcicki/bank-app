package com.example.bank.userRegister;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.example.bank.userRegister.UserRequestServiceUtils.*;
import static com.example.bank.userRegister.UserServiceUtils.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@SpringBootTest - komentarz dla mnie  nie używać tej adnotacji; ładuje cały kontekst aplikacji, czyli długo trwa!!

/**
 * Czy testy mają sprawdzać NULL i empty skoro jest walidacja ??
 * I czy w ogóle jakoś obsługuje się tego NULL'a, bo jeżeli przekażemy go do metod CRUDowych,
 * to leci NullPointerException.
 * Jak na razie dane do testów typu EMPTY lub NULL są zakomentowane.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @DisplayName("A parameterized test of registerUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldSource")
    void shouldReturnUserWhenUserIsBetween18And119YearsOld(User user, UserRequest userRequest) {
        when(userRepository.save(user)).thenReturn(user);
        User resultUser = userService.registerUser(userRequest);
        Assertions.assertEquals(resultUser, user);
    }

    @DisplayName("A parameterized test of registerUser() method")
    @ParameterizedTest
    @MethodSource("userRequestBelow18YearsOldAndNullAndEmptySource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBetween18And119(UserRequest userRequest) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userRequest);
        }, "User has to be adult.");
    }

    @DisplayName("A parameterized test of getUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldSource")
    void shouldGetUserAndReturnUserFromId3(User user) throws UserNotFoundException {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User resultUser = userService.getUser(id);
        Assertions.assertEquals(resultUser, user);
    }

    @DisplayName("A parameterized test of getUser() method")
    @ParameterizedTest
    @MethodSource("userSource")
    void shouldReturnUserExceptionWhenThereIsNoUserWithId(User user) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(id);
        });
    }

    @DisplayName("A parameterized test of updateUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldUpdateSource")
    void shouldReturnUpdatedUser(User user, UserRequest userRequest, User updatedUser) throws UserNotFoundException {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User resultUser = userService.updateUser(userRequest, user.getId());
        Assertions.assertEquals(updatedUser, resultUser);
    }

    @DisplayName("A parameterized test of updateUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldSource")
    void shouldThrowUserNotFoundExceptionDuringUserUpdateWhenThereIsNoUserWithId(User user, UserRequest userRequest) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userRequest, id));
    }

    @DisplayName("A parameterized test of updateUser() method")
    @ParameterizedTest
    @MethodSource("userBelow18YearsOldSource")
    void shouldThrowIllegalArgumentExceptionDuringUserUpdateWhenTheUserIsNotAdult(User user, UserRequest userRequest) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userRequest, id));
    }

    @DisplayName("A parameterized test of deleteUser() method")
    @ParameterizedTest
    @MethodSource("userSource")
    void shouldDeleteUserWhenGivenId(User user) throws UserNotFoundException {
        /** Czy na tym etapie można jakoś łatwo zabezpieczyć metodę, żeby nie usuwała usera, jeżeli jego ID to null?? */
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        boolean checkUser = userRepository.findById(id).isPresent();
        userService.deleteUser(id);
        User resultUser = userRepository.findById(id).get();
        Assertions.assertTrue(checkUser);
        verify(userRepository).delete(resultUser);
    }

    @DisplayName("A parameterized test of deleteUser() method")
    @ParameterizedTest
    @MethodSource("userSource")
    void shouldThrowUserNotFoundExceptionWhenThereIsNoUserWithId(User user) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(id));
    }

    static Stream<Arguments> userBelow18YearsOldSource/*AndEmptySource*/() {
        return Stream.of(Arguments.of(userBelow18YearsOldBuilder(), userRequestBelow18RequestBuilder())/*,
                Arguments.of(emptyUserBuilder(), emptyUserRequestBuilder())*/);
    }

    static Stream<UserRequest> userRequestBelow18YearsOldAndNullAndEmptySource() {
        return Stream.of(userRequestBelow18RequestBuilder()
                /*, emptyUserRequestBuilder(), null*/);
    }

    static Stream<User> userSource() {
        return Stream.of(userBuilder(), userWithId3Builder(), userAdultWithId5Builder(), user18YearsOldBuilder(),
                userBelow18YearsOldBuilder()/*, emptyUserBuilder(), null*/);
    }

    static Stream<Arguments> userOver18YearsOldUpdateSource() {
        return Stream.of(Arguments.of(userAdultWithId5Builder(), updateUserRequestAdultBuilder(),
                        resultUpdateUserWithId5Builder()),
                Arguments.of(user18YearsOldBuilder(), updateUserRequest18YOBuilder(),
                        resultUpdateUser18YearsOldBuilder()));
    }

    static Stream<Arguments> userOver18YearsOldSource() {
        return Stream.of(Arguments.of(userAdultWithId5Builder(), userRequest18YOForRegisterBuilder(),
                resultOfRegisterUserAdultWithIdBuilder())/*,
                Arguments.of(user18YearsOldBuilder(), updateUserRequest18YOBuilder(), resultUpdateUser18YearsOldBuilder())*/);
    }
}
