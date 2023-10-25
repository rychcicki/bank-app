package com.example.bank.userRegister;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/users")  //komentarz dla mnie w celu zapamiętania
@RequiredArgsConstructor
/** Czy w tej klasie również ma być @Validated, skoro w parametrach metody jest @Valid??
 * Teoretycznie powinno być @Validated nad klasą, ale bez tego też działa....
 * Masz gdzieś pod ręką dobre źródło do tego? Nie mogę znaleźć dobrych przykładów ResponseEntity do PUT i DELETE. */
@Validated
class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/users")
//    @Validated  nad klasą, @Valid przy parametrze UserRequest
    ResponseEntity<User> registerUser(@RequestBody @Valid UserRequest userRequest) {
//      return   ResponseEntity.accepted().body(userService.registerUser(userRequest)); // user
        return ResponseEntity.ok(userService.registerUser(userRequest));
    }

    @GetMapping("/users/{id}")
    ResponseEntity<User> getUser(@PathVariable @Min(1L) Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/users")
    ResponseEntity<User> updateUser(@RequestBody UserRequest userRequest, Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUser(userRequest, id));
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userService.deleteUser(user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
