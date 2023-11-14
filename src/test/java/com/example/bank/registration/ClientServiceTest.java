package com.example.bank.registration;

import com.example.bank.exception.UserNotFoundException;
import com.example.bank.registration.jpa.User;
import com.example.bank.registration.jpa.UserRepository;
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

import static com.example.bank.registration.UserRequestServiceUtils.*;
import static com.example.bank.registration.UserServiceUtils.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @DisplayName("A parameterized test of registerUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldSource")
    void shouldReturnUserWhenUserIs18YearsOldOrMore(User user, UserRequest userRequest) {
        when(userRepository.save(user)).thenReturn(user);
        User resultUser = userService.registerUser(userRequest);
        Assertions.assertEquals(resultUser, user);
    }

    @DisplayName("A parameterized test of registerUser() method")
    @ParameterizedTest
    @MethodSource("userRequestBelow18YearsOldSource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18YearsOld(UserRequest userRequest) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userRequest);
        }, "User has to be adult.");
    }

    @DisplayName("A parameterized test of getUser() method")
    @ParameterizedTest
    @MethodSource("userOver18YearsOldSource")
    void shouldGetUserAndReturnUserWithId(User user) {
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
    void shouldReturnUpdatedUser(User user, UserRequest userRequest, User updatedUser) {
        Long id = userRequest.id();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User resultUser = userService.updateUser(userRequest);
        /** Michał: Jakos srednio mi sie podoba kwestia ze sa 2 parametry w tej metodzie. Bo to glupio wyglada ze masz
         * userRequest i jeszcze dopychasz ID zamiast miec je od razu w obiekcie requesta
         *
         * Skorygowano w userReqest, userService i teście.*/
        Assertions.assertEquals(updatedUser, resultUser);
    }

    @DisplayName("A parameterized test of updateUser() method")
    @ParameterizedTest
    @MethodSource("userRequestOver18YearsOldSource")
    void shouldThrowUserNotFoundExceptionDuringUserUpdateWhenThereIsNoUserWithId(UserRequest userRequest) {
        Long id = userRequest.id();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userRequest));
    }

    @DisplayName("A parameterized test of updateUser() method")
    @ParameterizedTest
    @MethodSource("userBelow18YearsOldSource")
    void shouldThrowIllegalArgumentExceptionDuringUserUpdateWhenTheUserIsBelow18YearsOld(User user,
                                                                                         UserRequest userRequest) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userRequest));
    }

    @DisplayName("A parameterized test of deleteUser() method")
    @ParameterizedTest
    @MethodSource("userSource")
    void shouldDeleteUserWhenGivenId(User user) {
        Long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userService.deleteUser(id);
        verify(userRepository).delete(user);
        /** Michał: co tu sie w ogole dzieje ? Wywolywanie metod w testach z kodu produkcyjnego ? Co to w ogole jest?
         *
         * Skorygowano do takiej postaci. */
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

    static Stream<Arguments> userBelow18YearsOldSource() {
        return Stream.of(Arguments.of(userBelow18YearsOldBuilder(), userRequestBelow18RequestBuilder()));
    }

    static Stream<UserRequest> userRequestBelow18YearsOldSource() {
        return Stream.of(userRequestBelow18RequestBuilder()
        );
    }

    static Stream<User> userSource() {
        return Stream.of(userBuilder(), userWithId3Builder(), userAdultWithId5Builder(), user18YearsOldBuilder(),
                userBelow18YearsOldBuilder());
    }

    static Stream<Arguments> userOver18YearsOldUpdateSource() {
        return Stream.of(Arguments.of(userAdultWithId5Builder(), updateUserRequestAdultBuilder(),
                        resultUpdateUserWithId5Builder()),
                Arguments.of(user18YearsOldBuilder(), updateUserRequest18YOBuilder(),
                        resultUpdateUser18YearsOldBuilder()));
    }

    static Stream<Arguments> userOver18YearsOldSource() {
        return Stream.of(Arguments.of(userAdultWithId5Builder(), userRequest18YOForRegisterBuilder(),
                resultOfRegisterUserAdultWithIdBuilder()));
    }
    static Stream<Arguments> userRequestOver18YearsOldSource() {
        return Stream.of(Arguments.of(userRequest18YOForRegisterBuilder(),
                resultOfRegisterUserAdultWithIdBuilder()));
    }
}
