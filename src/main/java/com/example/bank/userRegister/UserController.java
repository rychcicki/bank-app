package com.example.bank.userRegister;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/users")  //komentarz dla mnie w celu zapamiętania
@RequiredArgsConstructor
/** Dlaczego walidacja działa niezależnie od tego, czy w metodzie POST i PUT mam @Valid, czy też go tam nie ma??
 *
 *     @Valid comes from Java Validation API
 *     @Validated comes from Spring Framework Validation, it is a variant of @Valid with support for validation groups
 *     @Valid use on method parameters and fields, don’t forget to use it on complex objects if they need to be validated
 *     @Validated use on methods and method parameters when using validation groups,
 *                use on classes to support method parameter constraint validations
 *  @Validated zapewnia bardziej szczegółową kontrolę nad procesem walidacji. Umożliwia określenie grup walidacyjnych,
 *  umożliwiając walidację różnych grup ograniczeń w różnych punktach aplikacji.*/
class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/users")
    ResponseEntity<User> registerUser(@RequestBody @Valid UserRequest userRequest) {
//      return   ResponseEntity.accepted().body(userService.registerUser(userRequest)); // user
        return ResponseEntity.ok(userService.registerUser(userRequest));
    }

    @GetMapping("/users/{id}")
    ResponseEntity<User> getUser(@PathVariable @Min(1L) Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/users")
    ResponseEntity<User> updateUser(@RequestBody @Valid UserRequest userRequest, Long id) throws UserNotFoundException {
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
