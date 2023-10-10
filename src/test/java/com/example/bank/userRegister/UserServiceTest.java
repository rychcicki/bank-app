package com.example.bank.userRegister;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@SpringBootTest - komentarz dla mnie - nie używać tej adnotacji; ładuje cały kontekst aplikacji, czyli długo trwa!!
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final User USER = UserServiceUtils.userBuilder();
    private static final User USER_WITH_ID3 = UserServiceUtils.userWithId3Builder();
    private static final User USER_TO_UPDATE = UserServiceUtils.userToUpdateBuilder();
    private static final UserRequest USER_REQUEST = UserServiceUtils.userRequestBuilder();
    private static final UserRequest USER_BELOW_18_REQUEST = UserServiceUtils.userBelow18RequestBuilder();

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnTrueIfRegisterUserPass() {
        /** Czy to jest właściwie przetestowana metoda?
         * Jeżeli nie chcesz tracić czasu na to, to wróćmy do tego w środę. */
        //given - arrange
        //when - act   //lenient() od biedy przy Unneccessary Stubbing exception
        when(userRepository.save(USER)).thenReturn(USER); //stub
//        userRepository.save(USER); // Jeżeli verify() jest w then-assert to jest za dużo wywołań.
        when(userService.registerUser(USER_REQUEST)).thenReturn(USER); //stub
        User userForTest = userService.registerUser(USER_REQUEST);
//        //then - assert
        verify(userRepository).save(USER);
        /** Dlaczego poniższa linia rzuca org.mockito.exceptions.misusing.NotAMockException:
         Argument passed to verify() is of type UserService and is not a mock! ???
         Przecież w userService jest @InjectMocks...*/
//        verify(userService).registerUser(USER_REQUEST);
        Assertions.assertEquals(USER, userForTest);
    }

    @Test
    void shouldReturnIllegalArgumentExceptionWhenAgeIsBelow18() {
        //given
        //when
//        when(userService.registerUser(USER_REQUEST)).thenThrow(new IllegalArgumentException());
        //then
        /** Czy może być tylko asersja w poprawnym teście??*/
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(USER_BELOW_18_REQUEST);
        }, "User has to be adult.");
    }

    @Test
    void shouldGetUserAndReturnUserFromId3() {
        //given - arrange
        //when - act
        when(userRepository.findById(USER_WITH_ID3.getId())).thenReturn(Optional.of(USER_WITH_ID3));  //stub
        userRepository.findById(USER_WITH_ID3.getId());  //act
        //then - assert
        verify(userRepository).findById(USER_WITH_ID3.getId());  //verify
    }

    @Test
    void shouldReturnUserExceptionWhenThereIsNoUserWithId() {
        //given
        Long id = 2540L;
        //when
        /** Ten test nie działa.
         * Jeżeli ta metoda zwraca Optionala, czyli NoSuchElementException, to czy w ogóle możemy wymusić
         * rzucenie własnego wyjątku UserNotFoundException i weryfikację własnego wyjątku w asercji??
         * Z drugiej strony przez Postmana rzuca UserNotFoundException, więc gdzie jest błąd? */
        when(userRepository.findById(id)).thenThrow(new UserNotFoundException(id));
//        //then
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userRepository.findById(id);
        });
    }

    /**
     * Ten test działa z NoSuchElementException.
     */
    @Test
    void shouldReturnNoSuchElementExceptionWhenThereIsNoUserWithId() {
        //given
        Long id = 2540L;
        //when
        when(userRepository.findById(id)).thenThrow(new NoSuchElementException());
        //then
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userRepository.findById(id);
        });
    }

    @Test
    void shouldReturnUpdatedUser() throws UserNotFoundException {
        //given
        Long id = USER_WITH_ID3.getId();
        //when - then
        when(userRepository.findById(id)).thenReturn(Optional.of(USER_TO_UPDATE));
        userRepository.findById(id);
        verify(userRepository).findById(id);

        when(userRepository.save(USER_TO_UPDATE)).thenReturn(USER_TO_UPDATE);
        userRepository.save(USER_TO_UPDATE);
        verify(userRepository).save(USER_TO_UPDATE);

//        when(userService.updateUser(USER_REQUEST,id)).thenReturn(USER_TO_UPDATE);
//        userService.updateUser(USER_REQUEST,id);
        /** To samo co powyżej. Dlaczego poniższa linia rzuca org.mockito.exceptions.misusing.NotAMockException:
         Argument passed to verify() is of type UserService and is not a mock! ???
         Przecież w userService jest @InjectMocks...*/
//        verify(userService).updateUser(USER_REQUEST,id);
    }

    @Test
    void shouldReturnSthWhenUserWasDeleted() {
        //given
        Long id = USER_WITH_ID3.getId();
        //when
        /** Nic tu nie pasuje. Czy nie jest tak, że metoda deleteUser(id) z serwisu powinna coś zwracać?
         * (obecnie jest null) */
//        when(userRepository.findById(id)).thenReturn(Optional.of(USER_WITH_ID3));
        userRepository.deleteById(id);
        //then
        verify(userRepository).deleteById(id);
        /** To samo co powyżej. Zastanawiam się, czy testować metody CRUDowe, czy metody wewnątrz tych metod...
         * Czy nie jest tak, że metoda deleteUser(id) z serwisu powinna coś zwraca? (obecnie jest null)*/
//        verify(userService).deleteUser(id);
    }
}
