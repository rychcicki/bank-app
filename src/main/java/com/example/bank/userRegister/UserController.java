package com.example.bank.userRegister;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/users")  //komentarz dla mnie w celu zapamiętania
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping("/users")
//    @Validated  //komentarz dla mnie: walidacja -> doczytaj wszystko
    void registerUser(@RequestBody UserRequest userRequest) {
        userService.registerUser(userRequest);
    }

    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUser(id);
    }

    @PutMapping("/users")
    User updateUser(@RequestBody UserRequest userRequest, Long id) throws UserNotFoundException {
        return userService.updateUser(userRequest, id);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
