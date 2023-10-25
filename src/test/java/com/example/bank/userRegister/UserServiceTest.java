package com.example.bank.userRegister;

import org.junit.jupiter.api.Assertions;
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
 * Zważywszy na różne artykuły, na które trafiam, mam pytanie: który kod testować?
 * 1. Tylko metody serwisowe
 * 2. Tylko odpowiedzi z controller'a
 * 3. Metody serwisowe oraz odpowiedzi z controller'a
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
//    private final User USER_WITH_ID3 = UserServiceUtils.userWithId3Builder();
//    private final User USER_TO_UPDATE = UserServiceUtils.userToUpdateBuilder();

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @ParameterizedTest
    @MethodSource("userOver18YOSource")
    void shouldReturnUserWhenUserIs18OrOlder(User user, UserRequest userRequest) {
        //given
        //when
        when(userRepository.save(user)).thenReturn(user);
        User resultUser = userService.registerUser(userRequest);
        //then
        Assertions.assertEquals(resultUser, user);
    }

    @ParameterizedTest
    @MethodSource("userLessThan18YearsOldAndNullAndEmptySource")
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18(UserRequest userRequest) {
        //given
        //when
        //then
        /** Czy może być tylko asercja w poprawnym teście??*/
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userRequest);
        }, "User has to be adult.");
    }

    @ParameterizedTest
    @MethodSource("userOver18YOSource")
    void shouldGetUserAndReturnUserFromId3(User user) throws UserNotFoundException {
        //given - arrange
        Long id = user.getId();
        //when - act
        when(userRepository.findById(id)).thenReturn(Optional.of(user));  //stub
        User resultUser = userService.getUser(id);//act
        //then - assert
        Assertions.assertEquals(resultUser, user);
    }

    @ParameterizedTest
    @MethodSource("userSource")
    void shouldReturnUserExceptionWhenThereIsNoUserWithId(User user) throws UserNotFoundException {
        //given - arrange
        Long id = user.getId();
        //when - act
        /** W sumie to co to za test skoro dowolny user zwraca null, niezależnie od tego,
         * czy znajduje się w db czy nie... */
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //then - assert
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(id);
        });
    }

    @ParameterizedTest
    @MethodSource("userOver18YOSource")
    void shouldReturnUpdatedUser(User user, UserRequest userRequest) throws UserNotFoundException {
        /** Nie działa dobrze - do poprawy!*/
        //given - arrange
        Long id = user.getId();
        //when - act
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//        when(userRepository.save(user));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setBirthDate(userRequest.getBirthDate());

        when(userRepository.save(user)).thenReturn(user);

        User resultUser = userService.updateUser(userRequest, user.getId());
        user.setLastName("AABBCCCDD");
        //then - assert
        Assertions.assertEquals(resultUser, user);
//        System.out.println(user);
//        System.out.println(userRequest);
//        verify(userRepository).save(user);
//        verify(userRepository).findById(id);

//        User resultUser = userRepository.save(USER_TO_UPDATE);
//        User resultUser = userService.updateUser(userRequest, user.getId());
//        verify(userService).updateUser(userRequest,user.getId());

    }

    @ParameterizedTest
    @MethodSource("userSource")
    void shouldDeleteUserWhenGivenId(User user) throws UserNotFoundException {
        /** Kompletnie nie wiem, jak przetestować metodę z serwisu.
         * Jak zabezpieczyć metodę, żeby nie usuwała usera, jeżeli jego ID to null.*/
        //given
        Long id = user.getId();
        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        boolean checkUser = userRepository.findById(id).isPresent();
        userService.deleteUser(id);
        User resultUser = userRepository.findById(id).get();
        //then
        Assertions.assertTrue(checkUser);
        verify(userRepository).delete(resultUser);
    }

    @ParameterizedTest
    @MethodSource("userSource")
    void shouldThrowUserNotFoundExceptionWhenThereIsNoUserWithId(User user) throws UserNotFoundException {
        //given
        Long id = user.getId();
        //when
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        //then
        Assertions.assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(id));
    }

    static Stream<Arguments> userOver18YOSource() {
        return Stream.of(Arguments.of(userAdultBuilderWithId5(), userRequestAdultBuilder()),
                Arguments.of(user18YearsOldBuilder(), userRequest18YOBuilder()));
    }

    static Stream<UserRequest> userLessThan18YearsOldAndNullAndEmptySource() {
        return Stream.of(userRequestNotAdultBuilder(),
                /** Czy testy mają sprawdzać NULL i empty skoro jest walidacja??
                 * I czy w ogóle jakoś obsługuje się tego NULL'a bo leci NullPointerException*/
                userBelow18RequestBuilder()/*, emptyUserRequestBuilder(), null*/);
    }

    static Stream<User> userSource() {
        return Stream.of(userBuilder(), userWithId3Builder(), userAdultBuilderWithId5(), user18YearsOldBuilder(),
                userNotAdultBuilder()/*, emptyUserBuilder(), null*/);
    }

}
