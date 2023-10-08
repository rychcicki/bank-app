package com.example.bank.userRegister;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

//@SpringBootTest - komentarz dla mnie - nie używać tej adnotacji; ładuje cały kontekst aplikacji, czyli długo trwa!!
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private AutoCloseable closeable;
    private static final User USER = User.builder()
            .firstName("Zdzislaw")
            .lastName("Krecina")
            .email("zdzislaw.krecina@gmail.com")
            .birthDate(LocalDate.of(1954, 4, 28))
            .address(new Address("Tatrzanska", "12-456", "Zywiec"))
            .build();
    private static final UserRequest USER_REQUEST = UserRequest.builder()
            .firstName("Zdzislaw")
            .lastName("Krecina")
            .email("zdzislaw.krecina@gmail.com")
            .birthDate(LocalDate.of(1954, 4, 28))
            .address(new Address("Tatrzanska", "12-456", "Zywiec"))
            .build();
    @Mock
    private UserRepository userRepository;
    /** Czy jest sens mockować UserRequest??   */
//    private UserRequest userRequest = USER_REQUEST;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnTrueIfRegisterUserPass() {
        /** Ten test rzuca org.mockito.exceptions.misusing.NullInsteadOfMockException
         * Nie pomagają dwie ostatnie metody na samym dole tej klasy. */
        //given - arrange
        Map<Long, User> db = new HashMap<>();
        //when - act
//        when(userRepository.save(USER)).thenReturn(db.put(1L,USER));
        verify(userRepository.save(USER)).toString();
//        User newUser = userRepository.save(USER);
//        when(userRepository.save(USER)).thenReturn(USER);
//        when(userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
//        User user = userService.registerUser(USER_REQUEST);
        User newUser = userService.registerUser(USER_REQUEST);
        when(userService.registerUser(USER_REQUEST)).thenReturn(db.put(1L,newUser));
//        lenient().
//        User user = verify(userService).registerUser(userRequestForTest);
//       User myUser = userService.getUser(1L);
        //then - assert
        Assertions.assertEquals(newUser, USER);
        Assertions.assertEquals(newUser, db.get(1L));
    }

    @Test
    void shouldReturnUserFromFirstId() throws UserNotFoundException {
        //given
        Long id = 1L;
        //when
//        when(userService.getUser(1L)).thenReturn(userForTest);
//        Optional<User> optionalUser = userRepository.findById(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(USER));
        /** Jak mockować takie metody, które mają w sobie inne metody, które odwołują się do db??
         *  Przecież wywołanie userService.getUser(id) poniżej zawsze zwróci null.
         *  Jak to testować metody, żeby nie testować, czy Mockito działa?? */
        User user = userService.getUser(id);
        //then
//        assertEquals(testUser, user);
        Assertions.assertEquals(USER, user);
    }

    @Test
    void shouldReturnUserExceptionWhenNull() throws UserNotFoundException {
        //given
        //when
//        when(userService.getUser(0L)).thenThrow(UserNotFoundException.class);
        //then
//        Assertions.assertThrows();
    }

    /** Poniższe metody nic nie robią??
     * Jak obejść org.mockito.exceptions.misusing.NullInsteadOfMockException ??*/
    @BeforeEach
    void setupMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp() throws Exception {
        closeable.close();
    }
}
